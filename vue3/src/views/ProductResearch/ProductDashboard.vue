<template>
  <div class="product-research">
    <el-card style="max-width: 99%">
    <div class="tou">
      <div class="left">
        <h3>产品总览</h3>
        <div class="kuai">
          <span
              v-for="(item,index) in ovCount"
              :key="index"
          >
            <div>{{item.name}}</div>
            <div>{{item.count}}</div>
          </span>
        </div>
      </div>

      <el-divider direction="vertical" style="height: auto;align-self: stretch"/>

      <div class="right">
        <h3 style="display: inline-block">产品年度推进统计</h3>
<!--        下拉菜单待改-->
        <el-select v-model="selectedYear" @change="handleYearChange" placeholder="请选择年份" size="small" style="display: inline-block;width: 80px;">
          <el-option label="2023" value="2023"/>
          <el-option label="2024" value="2024" />
          <el-option label="2025" value="2025" />
        </el-select>
        <div class="kuai">
          <span
              v-for="(item,index) in tuiJinCount"
              :key="index"
          >
            <div>{{item.name}}</div>
            <div>{{item.count}}</div>
          </span>
        </div>
      </div>
    </div>
    </el-card>

    <div class="comment">
      <div class="com-left" style="width: 65%">


<!--        产品列表整合-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <div class="product-list-tabs">
            <div class="tab-header">
              <span 
                v-for="(tab, index) in tabs" 
                :key="index"
                :class="['tab-item', { active: activeTab === index }]"
                @click="activeTab = index"
              >
                {{ tab.name }}
              </span>
            </div>
            <div class="tab-content">
              <!-- 产品发布列表 -->
              <div v-if="activeTab === 1" class="product-release-list">
                <el-table :data="productReleases" stripe style="width: 100%">
                  <el-table-column prop="projectName" label="产品名称" min-width="250">
                    <template #default="scope">
                      <a href="#" class="project-link">{{ scope.row.projectName }}</a>
                    </template>
                  </el-table-column>
                  <el-table-column prop="product" label="所属产品" min-width="150" />
                  <el-table-column prop="releaseDate" label="计划发布日期" min-width="120" />
                  <el-table-column prop="status" label="状态" min-width="100">
                    <template #default="scope">
                      <span class="status-tag">{{ scope.row.status }}</span>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
              <!-- 未关闭的产品列表 -->
              <div v-if="activeTab === 0" class="unclosed-product-list">
                <el-table :data="unclosedProducts" stripe style="width: 100%">
                  <el-table-column prop="projectName" label="产品名称" min-width="200">
                    <template #default="scope">
                      <a href="#" class="project-link">{{ scope.row.projectName }}</a>
                    </template>
                  </el-table-column>
                  <el-table-column prop="manager" label="负责人" min-width="100" />
                  <el-table-column prop="activeNeeds" label="项目" min-width="80" />
                  <el-table-column prop="iterationCount" label="迭代项目数" min-width="100">
                    <template #default="scope">
                      <div class="iteration-count">
                        {{ scope.row.iterationCount || 0 }}
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column prop="plan" label="计划" min-width="80" />
                  <el-table-column prop="activeBugs" label="Bug数" min-width="80" />
                  <el-table-column prop="release" label="发布" min-width="80" />
                </el-table>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="com-right" style="width: 35%;">
<!--        产品开发周期分布-->        
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>产品开发周期分布</h3>
          <el-divider/>
          <div ref="faBuDom" style="width: 100%; height: 250px;"></div>
          <div class="bangDan">
            <h4>对应周期产品列表</h4>
            <div class="product-list" style="max-height: 200px; overflow-y: auto;">
              <div v-if="!selectedPeriodProducts || selectedPeriodProducts.length === 0" class="no-data">
                请点击上方饼图查看对应周期的产品列表
              </div>
              <div v-else v-for="(product, index) in selectedPeriodProducts" :key="product.id" class="product-item">
                <span class="product-index">{{ index + 1 }}</span>
                <span class="product-name" style="cursor: pointer; color: #409EFF;" @click="goToProductList(product.name)">{{ product.name }}</span>
              </div>
            </div>
          </div>
        </el-card>



      </div>
    </div>
  </div>
</template>

<script setup>
// 产品总览数据
import {ref, onMounted} from "vue";
import { useRouter } from "vue-router";
import ProductList from "@/views/workbenchView/listView/ProductList.vue";
import {useEcharts} from "@/utils/useEcharts.js";
import StayTestList from "@/views/workbenchView/listView/StayTestList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";
import request from "@/utils/request.js";

// 初始化路由
const router = useRouter();
// 调试：查看request对象的baseURL
console.log('Request baseURL:', request.defaults.baseURL);
// 选择的年份
const selectedYear = ref('2023');

// 未关闭的产品列表数据
const unclosedProducts = ref([]);

// 产品发布列表数据
const productReleases = ref([]);

// 产品总览数据
const ovCount = ref([]);

// 产品年度推进统计数据
const tuiJinCount = ref([]);

// 选中周期的产品列表
const selectedPeriodProducts = ref([]);

// 标签页数据
const activeTab = ref(0);
const tabs = ref([
  { name: '产品列表' },
  { name: '未关闭列表' }
]);



// 从后端获取产品总览数据
const fetchProductOverview = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get('/dashboard/product-overview', {
        params: { username: user.username }
      });
      if (response.data.code === 200) {
        const data = response.data.data;
        ovCount.value = [
          { name: '产品总数', count: data.productCount || 0 },
          { name: '未完成计划数', count: data.unfinishedPlanCount || 0 },
          { name: '项目总数', count: data.unclosedNeedCount || 0 },
          { name: '激活Bug数', count: data.activeBugCount || 0 },
          { name: '迭代项目总数', count: data.iterationProjectCount || 0 }
        ];
      }
    }
  } catch (error) {
    console.error('获取产品总览数据失败:', error);
  }
};

// 从后端获取产品年度推进统计数据
const fetchProductProgress = async () => {
  try {
    const response = await request.get('/dashboard/product-progress', {
      params: { year: selectedYear.value }
    });
    if (response.data.code === 200) {
      const data = response.data.data;
      tuiJinCount.value = [
        { name: '已完成发布数', count: data.completedReleaseCount || 0 },
        { name: '已完成项目数', count: data.completedNeedCount || 0 },
        { name: '已完成Bug数', count: data.completedBugCount || 0 }
      ];
    }
  } catch (error) {
    console.error('获取产品年度推进统计数据失败:', error);
  }
};

// 从后端获取未关闭的产品列表数据
const fetchUnclosedProducts = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get('/dashboard/unclosed-products', {
        params: { username: user.username }
      });
      if (response.data.code === 200) {
        unclosedProducts.value = response.data.data || [];
      }
    }
  } catch (error) {
    console.error('获取未关闭的产品列表数据失败:', error);
  }
};

// 从后端获取产品发布列表数据
const fetchProductReleases = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get('/dashboard/product-releases', {
        params: { username: user.username }
      });
      if (response.data.code === 200) {
        productReleases.value = response.data.data || [];
      }
    }
  } catch (error) {
    console.error('获取产品发布列表数据失败:', error);
  }
};

// 产品开发周期分布图表
const {
  chartRef: faBuDom,
  initChart: initFaBuEchart,
  updateChart: updateFaBuEchart
} = useEcharts();
let faBuInited = false;

// 存储从后端获取的产品数据
const productDataByPeriod = ref({
  '3个月以内': [],
  '3-6个月': [],
  '6-12个月': [],
  '12个月以上': []
});

const handlePeriodClick = (params) => {
  const period = params.name;
  selectedPeriodProducts.value = productDataByPeriod.value[period] || [];
};

// 跳转到产品列表并筛选显示
const goToProductList = (productName) => {
  router.push(`/ProductResearch/ProductList?productName=${encodeURIComponent(productName)}`);
};

const initFaBuChart = async () => {
  if (!faBuDom.value) return;
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let params = {};
    if (userStr) {
      const user = JSON.parse(userStr);
      params = { username: user.username };
    }
    
    const response = await request.get('/dashboard/product-development-cycle', { params });
    if (response.data.code === 200) {
      const data = response.data.data;
      
      // 更新产品数据
      if (data.productsByCycle) {
        productDataByPeriod.value = data.productsByCycle;
      }
      
      const option = {
        title: {
          text: '产品开发周期分布',
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'normal'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['3个月以内', '3-6个月', '6-12个月', '12个月以上']
        },
        series: [
          {
            name: '开发周期',
            type: 'pie',
            radius: '50%',
            data: data.pieData || [
              { value: 0, name: '3个月以内' },
              { value: 0, name: '3-6个月' },
              { value: 0, name: '6-12个月' },
              { value: 0, name: '12个月以上' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            }
          }
        ],
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        }
      };

      if (faBuInited) updateFaBuEchart(option);
      else {
        initFaBuEchart(option, (chart) => {
          // 绑定点击事件
          chart.on('click', handlePeriodClick);
        });
        faBuInited = true;
      }
    }
  } catch (error) {
    console.error('获取产品开发周期分布数据失败:', error);
  }
};

// 项目与迭代关联分布图表
const rankingChart = ref(null);
const initRankingChart = async () => {
  if (!rankingChart.value) return;
  try {
    const response = await request.get('/dashboard/product-overview');
    if (response.data.code === 200) {
      const data = response.data.data;
      useEcharts(rankingChart.value, {
        title: { text: '项目与迭代关联分布' },
        xAxis: {
          type: 'category',
          data: ['有多个迭代的项目', '有单个迭代的项目', '无迭代的项目'],
          axisLabel: {
            fontSize: 11,
            rotate: 45
          }
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: [3, 2, 1],
          label: { show: true, position: 'top', fontSize: 11 }
        }]
      });
    }
  } catch (error) {
    console.error('获取项目与迭代关联分布数据失败:', error);
  }
};

// 初始化图表
const initCharts = async () => {
  await initFaBuChart();
};

// 在数据加载后初始化图表
onMounted(async () => {
  await fetchProductOverview();
  await fetchProductProgress();
  await fetchUnclosedProducts();
  await fetchProductReleases();
  await initCharts();
});

// 当选择年份变化时重新获取数据和初始化图表
const handleYearChange = async () => {
  await fetchProductProgress();
  await initCharts();
};
</script>

<style scoped>
.tou{
  display: flex;
  background-color: #fff;
}
.left{
  width: 60%;
}
.right{
  width: 40%;
}
.kuai{
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100px;
  text-align: center;
}
.kuai span{
  flex: 1;
}
.kuai span div:first-child {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}
.kuai span div {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}
h3{
  padding: 10px;
}

.comment{
  display: flex;
}

.bangDan {
  margin-top: 20px;
}

.bangDan h4 {
  margin: 0 0 15px 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.rank {
  width: 20px;
  text-align: center;
  font-weight: bold;
  color: #303133;
}

.product-name {
  flex: 1;
  font-size: 12px;
  color: #606266;
}

.progress-container {
  flex: 2;
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background-color: #409EFF;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.count {
  width: 30px;
  text-align: right;
  color: #303133;
  font-weight: 500;
}

/* 为不同排名设置不同颜色 */
.ranking-item:nth-child(1) .rank {
  color: #F56C6C;
}

.ranking-item:nth-child(2) .rank {
  color: #E6A23C;
}

.ranking-item:nth-child(3) .rank {
  color: #409EFF;
}

.ranking-item:nth-child(1) .progress-bar {
  background-color: #F56C6C;
}

.ranking-item:nth-child(2) .progress-bar {
  background-color: #E6A23C;
}

.ranking-item:nth-child(3) .progress-bar {
  background-color: #409EFF;
}

.ranking-item:nth-child(4) .progress-bar,
.ranking-item:nth-child(5) .progress-bar {
  background-color: #67C23A;
}

/* 产品列表样式 */
.product-list {
  margin-top: 10px;
  padding: 0 10px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
}

.product-index {
  width: 20px;
  font-weight: bold;
  margin-right: 10px;
  color: #409EFF;
}

.product-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
}

.no-data {
  text-align: center;
  padding: 20px 0;
  color: #909399;
  font-size: 13px;
}

.product-statistics{
  display: flex;
  align-items: flex-start;
  gap:20px;
  padding: 10px 0;
}

.product-list {
  flex-shrink: 0;
  width: 150px;
  border-right: 1px solid #ebeef5;
  padding-right: 15px;
}

.product-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.product-list li {
  padding: 8px 0;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.3s;
  border-radius: 4px;
  padding-left: 10px;
}

.product-list li:hover {
  color: #409EFF;
  background-color: #ecf5ff;
}

.product-list li.active {
  color: #409EFF;
  font-weight: 500;
  background-color: #ecf5ff;
}

.product-statistics-content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1.2fr 1fr;
  gap: 20px;
}

.delivery-rate, .needs-statistics, .latest-progress {
  padding: 15px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.delivery-rate h4, .needs-statistics h4, .latest-progress h4 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.delivery-rate {
  text-align: center;
}

.delivery-rate .el-progress {
  width: 120px !important;
  height: 120px !important;
  margin: 0 auto 15px;
}

.delivery-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
  flex-wrap: wrap;
  gap: 10px;
}

.delivery-stats span {
  font-size: 12px;
  color: #606266;
  display: inline-block;
  margin: 0 5px;
}

.stat-number {
  font-weight: bold;
  color: #303133;
  margin-left: 4px;
}

.needs-statistics {
  text-align: center;
}

.monthly-stats {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
  gap: 20px;
}

.monthly-stats span {
  font-size: 14px;
  color: #606266;
}

.latest-progress {
  padding: 15px;
}

.latest-task, .latest-release {
  margin-bottom: 20px;
}

.latest-task p, .latest-release p {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.task-item, .release-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.task-name, .release-name {
  font-size: 12px;
  color: #409EFF;
  text-decoration: none;
  flex: 1;
  margin-right: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-name:hover, .release-name:hover {
  text-decoration: underline;
}

.task-status, .release-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
  flex-shrink: 0;
}

.task-status {
  background-color: #f0f0f0;
  color: #909399;
}

.release-status {
  background-color: #f0f9eb;
  color: #67C23A;
}
.unclosed-product-list {
  margin-top: 10px;
}

.project-link {
  color: #409EFF;
  text-decoration: none;
  font-size: 13px;
  display: block;
  text-align: center;
}

.project-link:hover {
  text-decoration: underline;
}

.completion-rate {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.el-table .cell {
  font-size: 13px;
  padding: 10px;
  text-align: center !important;
  vertical-align: middle !important;
}

.el-table th {
  font-size: 13px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 10px;
  text-align: center !important;
  vertical-align: middle !important;
}

.el-table__row {
  height: 36px !important;
}

.el-table--border th {
  border-bottom: 1px solid #ebeef5;
}

.el-table--border td {
  border-bottom: 1px solid #ebeef5;
  vertical-align: middle !important;
}

.completion-rate .el-progress {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  width: 40px !important;
  height: 40px !important;
}

.el-progress {
  position: relative;
  display: inline-block;
}

.el-progress__text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 11px !important;
  margin: 0;
  z-index: 1;
}

.el-progress__circle {
  display: flex;
  justify-content: center;
  align-items: center;
}

.product-release-list {
  margin-top: 10px;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  background-color: #f0f9eb;
  color: #67C23A;
}

.product-release-list .project-link {
  color: #409EFF;
  text-decoration: none;
  font-size: 13px;
  display: block;
  text-align: left;
}

.product-release-list .project-link:hover {
  text-decoration: underline;
}

.product-release-list .el-table .cell {
  font-size: 13px;
  padding: 10px;
  text-align: left;
  vertical-align: middle;
}

.product-release-list .el-table th {
  font-size: 13px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 10px;
  text-align: left;
  vertical-align: middle;
}

.product-release-list .el-table__row {
  height: 36px !important;
}

.el-table--border th {
  border-bottom: 1px solid #ebeef5;
}

.el-table--border td {
  border-bottom: 1px solid #ebeef5;
}

/* 标签页样式 */
.tab-header {
  display: flex;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 15px;
}

.tab-item {
  padding: 10px 20px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-item:hover {
  color: #409EFF;
}

.tab-item.active {
  color: #409EFF;
  border-bottom-color: #409EFF;
  font-weight: 500;
}

.tab-content {
  min-height: 300px;
}
</style>