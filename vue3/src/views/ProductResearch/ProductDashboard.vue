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
            <dvi>{{item.count}}</dvi>
          </span>
        </div>
      </div>

      <el-divider direction="vertical" style="height: auto;align-self: stretch"/>

      <div class="right">
        <h3 style="display: inline-block">产品年度推进统计</h3>
<!--        下拉菜单待改-->
        <el-select v-model="selectedYear" placeholder="请选择年份" size="small" style="display: inline-block;width: 80px;">
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
            <dvi>{{item.count}}</dvi>
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
        </el-card>

<!--        未关闭的产品统计-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>未关闭的产品统计</h3>
          <div class="product-statistics">
            <div class="product-list">
              <ul>
                <li class="active">班牌模板调整</li>
                <li>班牌PC端管理界面调整</li>
                <li>界面优化调整</li>
                <li>家校互通留言</li>
                <li>实训教学资源大数据</li>
                <li>教师端查询评分标准列表</li>
              </ul>
            </div>
            <div class="product-statistics-content">
              <div class="delivery-rate">
                <h4>需求交付率</h4>
                <el-progress type="circle" :percentage="50" />
                <div class="delivery-stats">
                  <span>有效需求 <span class="stat-number">56</span></span>
                  <span>已交付 <span class="stat-number">16</span></span>
                  <span>未关闭 <span class="stat-number">42</span></span>
                </div>
              </div>
              <div class="needs-statistics">
                <h4>需求统计</h4>
                <div class="monthly-stats">
                  <span>本月完成 <span class="stat-number">26</span></span>
                  <span>本月新增 <span class="stat-number">12</span></span>
                </div>
                <div ref="needsDom" style="width: 100%; height: 250px;"></div>
              </div>
              <div class="latest-progress">
                <h4>产品最新推进</h4>
                <div class="latest-task">
                  <p>最新任务</p>
                  <div class="task-item">
                    <a class="task-name">班牌PC端管理界面调整</a>
                    <span class="task-status">未开始</span>
                  </div>
                </div>
                <div class="latest-release">
                  <p>最新发布</p>
                  <div class="release-item">
                    <a class="release-name">自动给学生推送实训档案</a>
                    <span class="release-status">进行中</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </el-card>

<!--        未关闭的产品列表-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>未关闭的产品列表</h3>
          <div class="unclosed-product-list">
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
        </el-card>

<!--        产品发布列表-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>产品发布列表</h3>
          <div class="product-release-list">
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
        <!--      测试统计-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <div class="measure">
            <h3>测试统计</h3>
            <el-divider/>
            <div style="display: flex; gap: 20px;">
              <!-- 左侧：项目列表 -->
              <div style="width: 150px; flex-shrink: 0;">
                <div class="system-list">
                  <ProjectList/>
                </div>
              </div>
              <!-- 右侧：Bug统计 -->
              <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 10px; background-color: #ffffff;">
                <div class="bug-statistics">
                  <h3>Bug统计</h3>
                  <div class="bug-stat-item">
                    <div class="bug-stat-label">昨天新增</div>
                    <div class="bug-stat-value">125</div>
                    <el-progress :percentage="70" :height="6" />
                  </div>
                  <div class="bug-stat-item">
                    <div class="bug-stat-label">今日新增</div>
                    <div class="bug-stat-value">65</div>
                    <el-progress :percentage="40" :height="6" />
                  </div>
                  <div class="bug-stat-item">
                    <div class="bug-stat-label">昨天解决</div>
                    <div class="bug-stat-value">98</div>
                    <el-progress :percentage="60" :height="6" />
                  </div>
                  <div class="bug-stat-item">
                    <div class="bug-stat-label">今日解决</div>
                    <div class="bug-stat-value">26</div>
                    <el-progress :percentage="20" :height="6" />
                  </div>
                </div>
              </div>
            </div>
            <!-- 下排：Bug修复率和待测试的测试单 -->
            <div style="display: flex; gap: 15px; margin-top: 15px;">
              <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 10px; background-color: #ffffff;">
                <div class="bug-repair">
                  <h3>Bug修复率</h3>
                  <el-progress type="circle" :percentage="50" :width="80" :stroke-width="6" />
                  <div class="bug-repair-stats">
                    <span>
                      <div class="stat-value">56</div>
                      <div class="stat-label">有效Bug</div>
                    </span>
                    <span>
                      <div class="stat-value">16</div>
                      <div class="stat-label">已修复</div>
                    </span>
                    <span>
                      <div class="stat-value">42</div>
                      <div class="stat-label">未关闭</div>
                    </span>
                  </div>
                </div>
              </div>
              <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 10px; background-color: #ffffff;">
                <div class="staying-test-list">
                  <h3>待测试的测试单</h3>
                  <ul class="test-list">
                    <li><a href="#">班牌PC端管理界面调整</a></li>
                    <li><a href="#">学期结束后，自动给学生推送实训档案</a></li>
                    <li><a href="#">班牌模板调整，参考海康，增加竖版</a></li>
                    <li><a href="#">家校互通留言</a></li>
                    <li><a href="#">终端-教师端查询评分标准列表</a></li>
                  </ul>
                </div>
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
import {ref} from "vue";
import ProductList from "@/views/workbenchView/listView/ProductList.vue";
import {useEcharts} from "@/utils/useEcharts.js";
import StayTestList from "@/views/workbenchView/listView/StayTestList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";

const ovCount = ref([
  { name: '产品线总量', count: 10 },
  { name: '产品总数', count: 10 },
  { name: '未完成计划数', count: 6 },
  { name: '未关闭需求数', count: 4 },
  { name: '激活Bug数', count: 10 },
]);
// 产品年度推进统计数据
const tuiJinCount = ref([
  { name: '已完成发布数', count: 120 },
  { name: '已完成需求数', count: 80 },
  { name: '已完成Bug数', count: 543}
]);
// 选择的年份
const selectedYear = ref('2023');

// 指派给我的研发需求数据
const requirements = ref([
  { id: 1, name: '家长端，界面优化调整，新增功能：授权监控', priority: '紧急' },
  { id: 2, name: '班牌PC端管理界面调整，样式统一，菜单归类', priority: '紧急' },
  { id: 3, name: '班牌模板调整，参考海康，增加竖版', priority: '紧急' },
  { id: 4, name: '家校互通留言', priority: '严重' },
  { id: 5, name: '数据大屏 实训教学资源大数据', priority: '一般' },
  { id: 6, name: '终端-教师端查询评分标准列表', priority: '一般' },
  { id: 7, name: '实训任务、示范列表详情优化', priority: '正常' },
  { id: 8, name: '学期结束后，自动给学生推送实训档案', priority: '正常' },
  { id: 9, name: '智慧教室_智慧云盘', priority: '一般' },
  { id: 10, name: '终端-教师端查询评分标准列表', priority: '一般' }
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

// 未关闭的产品列表数据
const unclosedProducts = ref([
  { projectName: '智慧教室.智慧云盘', manager: '张三三', activeNeeds: 102, completionRate: 25, plan: 102, activeBugs: 25, release: 15 },
  { projectName: '实践教学管理平台', manager: '李四四', activeNeeds: 12, completionRate: 25, plan: 12, activeBugs: 36, release: 63 },
  { projectName: '电子班牌管理系统', manager: '张三三', activeNeeds: 56, completionRate: 25, plan: 56, activeBugs: 59, release: 72 },
  { projectName: '智慧校园(中学版)', manager: '王麻子', activeNeeds: 85, completionRate: 0, plan: 85, activeBugs: 86, release: 0 },
  { projectName: '宿舍管理系统', manager: '王麻子', activeNeeds: 20, completionRate: 0, plan: 20, activeBugs: 26, release: 0 },
  { projectName: '教务考试系统', manager: '胡一刀', activeNeeds: 28, completionRate: 25, plan: 28, activeBugs: 75, release: 152 },
  { projectName: '在线试卷批改', manager: '张三三', activeNeeds: 45, completionRate: 25, plan: 45, activeBugs: 102, release: 3 },
  { projectName: '家校互通平台', manager: '王麻子', activeNeeds: 13, completionRate: 25, plan: 13, activeBugs: 160, release: 9 }
]);

// 产品发布列表数据
const productReleases = ref([
  { projectName: '家长端，界面优化调整，新增功能：授权监控', product: '智慧教室_智慧云盘', releaseDate: '2023-08-08', status: '已发布' },
  { projectName: '班牌PC端管理界面调整，样式统一，菜单归类', product: '实践教学管理平台', releaseDate: '2023-08-08', status: '已发布' },
  { projectName: '班牌模板调整，参考海康，增加竖版', product: '电子班牌管理系统', releaseDate: '2023-08-08', status: '已发布' },
  { projectName: '家校互通留言', product: '智慧校园(中学版)', releaseDate: '2023-08-08', status: '已发布' },
  { projectName: '数据大屏 实训教学资源大数据', product: '宿舍管理系统', releaseDate: '2023-08-08', status: '已发布' },
  { projectName: '终端-教师端查询评分标准列表', product: '教务考试系统', releaseDate: '2023-08-08', status: '已发布' },
  { projectName: '实训任务、示范列表详情优化', product: '在线试卷批改', releaseDate: '2023-08-08', status: '已发布' },
  { projectName: '学期结束后，自动给学生推送实训档案', product: '家校互通平台', releaseDate: '2023-08-08', status: '已发布' }
]);

// 格式化进度条显示
const formatProgress = (percentage) => {
  return `${percentage}`;
};

// 完成需求规模数据
const demandSizeList = ref([
  { name: '智慧教室 智慧云盘', count: 123 },
  { name: '实践教学管理平台', count: 110 },
  { name: '电子班牌管理系统', count: 142 },
  { name: '智慧校园(中学版)', count: 80 },
  { name: '宿舍管理系统', count: 63 },
  { name: '家校互通平台', count: 86 }
]);

// 完成需求数数据
const demandCountList = ref([
  { name: '智慧教室 智慧云盘', count: 123 },
  { name: '实践教学管理平台', count: 110 },
  { name: '电子班牌管理系统', count: 142 },
  { name: '智慧校园(中学版)', count: 80 },
  { name: '宿舍管理系统', count: 63 },
  { name: '家校互通平台', count: 86 }
]);

// 修复Bug数数据
const repairBugList = ref([
  { name: '智慧教室 智慧云盘', count: 123 },
  { name: '实践教学管理平台', count: 110 },
  { name: '电子班牌管理系统', count: 142 },
  { name: '智慧校园(中学版)', count: 80 },
  { name: '宿舍管理系统', count: 63 },
  { name: '家校互通平台', count: 86 }
]);

const {chartRef: faBuDom} = useEcharts({
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
    data:['6月','7月','8月','9月','10月','11月'],
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
    max: 30,
    interval: 5,
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
    data:[10, 15, 20, 25, 10, 20, 15],
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
const { chartRef: rankingChart } = useEcharts({
  title: { text: '年度发布榜' },
  xAxis: {
    type: 'category',
    data: ['实践教学管理平台', '电子班牌管理系统', '智慧校园(中学版)', '宿舍管理系统', '教务考试系统']
  },
  yAxis: { type: 'value' },
  series: [{
    type: 'bar',
    data: [123, 101, 86, 71, 66],
    label: { show: true, position: 'top' }
  }]
});

// 需求统计图
const {chartRef: needsDom} = useEcharts({
  grid: {
    left: '3%',
    right: '4%',
    bottom: '3%',
    containLabel: true
  },
  xAxis:{
    type: 'category',
    boundaryGap: false,
    data:['7月','8月','9月','10月','11月','12月'],
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
    max: 30,
    interval: 5,
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
        data:[15,24,54,23,14,56],
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
      data:[35,67,34,28,89,99],
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
  height: 150px;
  text-align: center;
}
.kuai span{
  flex: 1;
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