<template>
  <div class="product-research">
    <el-card style="max-width: 98%">
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
<!--        产品年度统计工作-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <div class="yearWorkStatistic">
            <h3>产品年度工作量统计</h3>
            <el-divider/>
            <div class="container">
              <div class="demand-size">
                <h3>完成需求规模</h3>
                <ul>
                  <li
                      v-for="(item,index) in demandSizeList"
                      :key="index"
                  >
                    {{item.name}}:<el-progress :percentage="item.count" :format="formatProgress"/>
                  </li>
                </ul>
              </div>
              <div class="demand-count">
                <h3>完成需求数</h3>
                <ul>
                  <li
                      v-for="(item,index) in demandCountList"
                      :key="index"
                  >
                    {{item.name}}:<el-progress :percentage="item.count" :format="formatProgress"/>
                  </li>
                </ul>
              </div>
              <div class="repair-bug">
                <h3>修复Bug数</h3>
                <ul>
                  <li
                      v-for="(item,index) in repairBugList"
                      :key="index"
                  >
                    {{item.name}}:<el-progress :percentage="item.count" :format="formatProgress"/>
                  </li>
                </ul>
              </div>
            </div>
          </div>
          <!-- 需求统计图 -->
          <div ref="needsDom" style="width: 100%; height: 250px; margin-top: 20px;"></div>
        </el-card>

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
                  <el-table-column prop="projectName" label="项目名称" min-width="250">
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
                  <el-table-column prop="projectName" label="项目名称" min-width="200">
                    <template #default="scope">
                      <a href="#" class="project-link">{{ scope.row.projectName }}</a>
                    </template>
                  </el-table-column>
                  <el-table-column prop="manager" label="负责人" min-width="100" />
                  <el-table-column prop="activeNeeds" label="激活需求" min-width="80" />
                  <el-table-column prop="completionRate" label="需求完成率" min-width="100">
                    <template #default="scope">
                      <div class="completion-rate">
                        <el-progress
                            type="circle"
                            :percentage="scope.row.completionRate"
                            :width="40"
                            :stroke-width="4"
                        />
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column prop="plan" label="计划" min-width="80" />
                  <el-table-column prop="activeBugs" label="激活Bug" min-width="80" />
                  <el-table-column prop="release" label="发布" min-width="80" />
                </el-table>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="com-right" style="width: 35%;">
<!--        产品发布统计-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>产品发布统计</h3>
          <el-divider/>
          <div ref="faBuDom" style="width: 100%; height: 250px;"></div>
          <div class="bangDan">
            <h4>年度发布榜(2024年)</h4>
            <div class="ranking-list">
              <div class="ranking-item">
                <span class="rank">1</span>
                <span class="product-name">实践教学管理平台</span>
                <div class="progress-container">
                  <div class="progress-bar" style="width: 90%"></div>
                </div>
                <span class="count">123</span>
              </div>
              <div class="ranking-item">
                <span class="rank">2</span>
                <span class="product-name">电子班牌管理系统</span>
                <div class="progress-container">
                  <div class="progress-bar" style="width: 80%"></div>
                </div>
                <span class="count">101</span>
              </div>
              <div class="ranking-item">
                <span class="rank">3</span>
                <span class="product-name">智慧校园(中学版)</span>
                <div class="progress-container">
                  <div class="progress-bar" style="width: 70%"></div>
                </div>
                <span class="count">86</span>
              </div>
              <div class="ranking-item">
                <span class="rank">4</span>
                <span class="product-name">宿舍管理系统</span>
                <div class="progress-container">
                  <div class="progress-bar" style="width: 60%"></div>
                </div>
                <span class="count">71</span>
              </div>
              <div class="ranking-item">
                <span class="rank">5</span>
                <span class="product-name">教务考试系统</span>
                <div class="progress-container">
                  <div class="progress-bar" style="width: 50%"></div>
                </div>
                <span class="count">66</span>
              </div>
            </div>
          </div>
        </el-card>

<!--        指派研发需求-->
        <el-card style="max-width: 98%;margin-top: 10px">
        <div class="assigned-rd-requirements">
          <h3>指派给我的研发需求</h3>
          <div class="table-container">
            <el-table :data="requirements" stripe style="width: 100%">
              <el-table-column prop="id" label="序号" width="80" />
              <el-table-column prop="name" label="研发需求名称" min-width="250" />
              <el-table-column prop="priority" label="优先级">
                <template #default="scope">
            <span :class="getPriorityClass(scope.row.priority)">
              {{ scope.row.priority }}
            </span>
                </template>
              </el-table-column>
            </el-table>
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
import ProductList from "@/views/workbenchView/listView/ProductList.vue";
import {useEcharts} from "@/utils/useEcharts.js";
import StayTestList from "@/views/workbenchView/listView/StayTestList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";
import request from "@/utils/request.js";
// 选择的年份
const selectedYear = ref('2023');

// 指派给我的研发需求数据
const requirements = ref([]);

// 未关闭的产品列表数据
const unclosedProducts = ref([]);

// 产品发布列表数据
const productReleases = ref([]);

// 完成需求规模数据
const demandSizeList = ref([]);

// 完成需求数数据
const demandCountList = ref([]);

// 修复Bug数数据
const repairBugList = ref([]);

// 产品总览数据
const ovCount = ref([]);

// 产品年度推进统计数据
const tuiJinCount = ref([]);

// 标签页数据
const activeTab = ref(0);
const tabs = ref([
  { name: '发布列表' },
  { name: '未关闭列表' }
]);

// 获取优先级样式
const getPriorityClass = (priority) => {
  switch (priority) {
    case '紧急':
      return 'priority-urgent';
    case '严重':
      return 'priority-serious';
    case '一般':
      return 'priority-normal';
    case '正常':
      return 'priority-regular';
    default:
      return '';
  }
};

// 格式化进度条显示
const formatProgress = (percentage) => {
  return `${percentage}`;
};

// 从后端获取产品总览数据
const fetchProductOverview = async () => {
  try {
    const response = await request.get('/dashboard/product-overview');
    if (response.data.code === 200) {
      const data = response.data.data;
      ovCount.value = [
        { name: '产品线总量', count: data.productLineCount || 0 },
        { name: '产品总数', count: data.productCount || 0 },
        { name: '未完成计划数', count: data.unfinishedPlanCount || 0 },
        { name: '未关闭需求数', count: data.unclosedNeedCount || 0 },
        { name: '激活Bug数', count: data.activeBugCount || 0 }
      ];
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
        { name: '已完成需求数', count: data.completedNeedCount || 0 },
        { name: '已完成Bug数', count: data.completedBugCount || 0 }
      ];
    }
  } catch (error) {
    console.error('获取产品年度推进统计数据失败:', error);
  }
};

// 从后端获取研发需求数据
const fetchRequirements = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get('/dashboard/requirements', {
        params: { username: user.username }
      });
      if (response.data.code === 200) {
        requirements.value = response.data.data || [];
      }
    }
  } catch (error) {
    console.error('获取研发需求数据失败:', error);
  }
};

// 从后端获取未关闭的产品列表数据
const fetchUnclosedProducts = async () => {
  try {
    const response = await request.get('/dashboard/unclosed-products');
    if (response.data.code === 200) {
      unclosedProducts.value = response.data.data || [];
    }
  } catch (error) {
    console.error('获取未关闭的产品列表数据失败:', error);
  }
};

// 从后端获取产品发布列表数据
const fetchProductReleases = async () => {
  try {
    const response = await request.get('/dashboard/product-releases');
    if (response.data.code === 200) {
      productReleases.value = response.data.data || [];
    }
  } catch (error) {
    console.error('获取产品发布列表数据失败:', error);
  }
};

// 从后端获取产品年度工作量统计数据
const fetchYearWorkStatistics = async () => {
  try {
    const response = await request.get('/dashboard/year-work-statistics', {
      params: { year: selectedYear.value }
    });
    if (response.data.code === 200) {
      const data = response.data.data;
      demandSizeList.value = data.demandSizeList || [];
      demandCountList.value = data.demandCountList || [];
      repairBugList.value = data.repairBugList || [];
    }
  } catch (error) {
    console.error('获取产品年度工作量统计数据失败:', error);
  }
};

// 产品发布统计图表
const faBuDom = ref(null);
const initFaBuChart = async () => {
  if (!faBuDom.value) return;
  try {
    const response = await request.get('/dashboard/monthly-release', {
      params: { year: selectedYear.value }
    });
    if (response.data.code === 200) {
      const data = response.data.data;
      useEcharts(faBuDom.value, {
        title: {
          text: '月度发布次数趋势图(个)',
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'normal'
          }
        },
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis:{
          type: 'category',
          boundaryGap: false,
          data: data.months || ['1月','2月','3月','4月','5月','6月','7月','8月','9月','10月','11月','12月'],
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            fontSize: 11
          }
        },
        yAxis:{
          type: 'value',
          min: 0,
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          },
          axisLabel: {
            fontSize: 11
          }
        },
        series:[{
          type:'line',
          data: data.releaseCounts || [0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
          lineStyle: {
            color: '#409EFF',
            width: 2
          },
          itemStyle: {
            color: '#409EFF',
            borderWidth: 2,
            borderColor: '#fff'
          },
          symbol: 'circle',
          symbolSize: 8
        }]
      });
    }
  } catch (error) {
    console.error('获取月度发布数据失败:', error);
  }
};

// 需求统计图
const needsDom = ref(null);
const initNeedsChart = async () => {
  if (!needsDom.value) return;
  try {
    const response = await request.get('/dashboard/needs-statistics', {
      params: { year: selectedYear.value }
    });
    if (response.data.code === 200) {
      const data = response.data.data;
      // 处理后端返回的数据结构
      const chartData = data.needsChartData || {};
      const xAxisData = chartData.xAxis ? chartData.xAxis.data : ['7月','8月','9月','10月','11月','12月'];
      const seriesData = chartData.series || [];
      const completedData = seriesData[0] ? seriesData[0].data : [0, 0, 0, 0, 0, 0];
      const newData = seriesData[1] ? seriesData[1].data : [0, 0, 0, 0, 0, 0];
      
      useEcharts(needsDom.value, {
        grid: {
          left: '3%',
          right: '4%',
          bottom: '3%',
          containLabel: true
        },
        xAxis:{
          type: 'category',
          boundaryGap: false,
          data: xAxisData,
          axisLine: {
            lineStyle: {
              color: '#d9d9d9'
            }
          },
          axisLabel: {
            fontSize: 11
          }
        },
        yAxis:{
          type: 'value',
          min: 0,
          axisLine: {
            show: false
          },
          axisTick: {
            show: false
          },
          splitLine: {
            lineStyle: {
              color: '#f0f0f0'
            }
          },
          axisLabel: {
            fontSize: 11
          }
        },
        series:[
            {
              name:'完成',
              type:'line',
              stack:'Total',
              data: completedData,
              lineStyle: {
                color: '#409EFF',
                width: 2
              },
              itemStyle: {
                color: '#409EFF'
              }
            },
          {
            name:'新增',
            type:'line',
            stack:'Total',
            data: newData,
            lineStyle: {
              color: '#67C23A',
              width: 2
            },
            itemStyle: {
              color: '#67C23A'
            }
          }
        ]
      });
    }
  } catch (error) {
    console.error('获取需求统计数据失败:', error);
  }
};

// 年度发布榜图表
const rankingChart = ref(null);
const initRankingChart = async () => {
  if (!rankingChart.value) return;
  try {
    const response = await request.get('/dashboard/yearly-ranking', {
      params: { year: selectedYear.value }
    });
    if (response.data.code === 200) {
      const data = response.data.data;
      useEcharts(rankingChart.value, {
        title: { text: '年度发布榜' },
        xAxis: {
          type: 'category',
          data: data.products || [],
          axisLabel: {
            fontSize: 11,
            rotate: 45
          }
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: data.releaseCounts || [],
          label: { show: true, position: 'top', fontSize: 11 }
        }]
      });
    }
  } catch (error) {
    console.error('获取年度发布榜数据失败:', error);
  }
};

// 初始化图表
const initCharts = async () => {
  await initFaBuChart();
  await initNeedsChart();
};

// 在数据加载后初始化图表
onMounted(async () => {
  await fetchProductOverview();
  await fetchProductProgress();
  await fetchRequirements();
  await fetchUnclosedProducts();
  await fetchProductReleases();
  await fetchYearWorkStatistics();
  await initCharts();
});

// 当选择年份变化时重新获取数据和初始化图表
const handleYearChange = async () => {
  await fetchProductProgress();
  await fetchYearWorkStatistics();
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
/*产品年度工作量统计样式*/
.container{
  display: flex;
}
.container div{
  flex: 1;
}

.assigned-rd-requirements{
  background-color: #fff;
}


/*测量统计样式*/
.measure {
  padding: 10px 0;
}

.measure h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.system-list {
  padding: 10px 0;
}

.bug-statistics {
  padding: 10px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.bug-statistics h3 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.bug-stat-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.bug-stat-label {
  font-size: 13px;
  color: #606266;
  width: 60px;
}

.bug-stat-value {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  width: 20px;
}

.bug-stat-item .el-progress {
  flex: 1;
  height: 8px !important;
}

.bug-repair {
  padding: 10px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  text-align: center;
  height: 100%;
}

.bug-repair h3 {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.bug-repair .el-progress {
  width: 100px !important;
  height: 100px !important;
  margin: 0 auto 10px;
}

.bug-repair-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 10px;
}

.bug-repair-stats span {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
}

.staying-test-list {
  padding: 10px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  height: 100%;
}

.staying-test-list h3 {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.test-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.test-list li {
  margin-bottom: 8px;
}

.test-list a {
  font-size: 13px;
  color: #409EFF;
  text-decoration: none;
  transition: color 0.3s;
}

.test-list a:hover {
  text-decoration: underline;
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
  padding: 8px 12px;
  text-align: center !important;
  vertical-align: middle !important;
}

.el-table th {
  font-size: 13px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 10px 12px;
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
  padding: 8px 12px;
  text-align: left;
  vertical-align: middle;
}

.product-release-list .el-table th {
  font-size: 13px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 10px 12px;
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

.assigned-rd-requirements {
  margin-top: 10px;
}

.assigned-rd-requirements .el-table .cell {
  font-size: 13px;
  padding: 8px 12px;
  vertical-align: middle;
}

.assigned-rd-requirements .el-table th {
  font-size: 13px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 10px 12px;
  vertical-align: middle;
}

.assigned-rd-requirements .el-table__row {
  height: 36px !important;
}

/* 标签页样式 */
.product-list-tabs {
  margin-top: 10px;
}

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

/* 优先级样式 */
.priority-urgent {
  color: #F56C6C;
  font-weight: 500;
}

.priority-serious {
  color: #E6A23C;
  font-weight: 500;
}

.priority-normal {
  color: #909399;
  font-weight: 500;
}

.priority-regular {
  color: #67C23A;
  font-weight: 500;
}
</style>