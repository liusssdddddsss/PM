<template>
  <div class="iteration-detail">
    <h3>项目计划：{{ projectName }}</h3>

    <div class="main-content">
      <!-- 左侧内容 -->
      <div class="left-content">
        <!-- 甘特图 -->
        <div class="gantt-chart">
          <div class="gantt-row">
            <div class="gantt-label">设计</div>
            <div class="gantt-bar-container">
              <div class="gantt-bar design" style="width: 25%;"></div>
              <div class="gantt-progress">进度100%</div>
            </div>
          </div>
          <div class="gantt-row">
            <div class="gantt-label">需求</div>
            <div class="gantt-bar-container">
              <div class="gantt-bar requirement" style="width: 20%;"></div>
              <div class="gantt-progress">进度100%</div>
            </div>
          </div>
          <div class="gantt-row">
            <div class="gantt-label">开发</div>
            <div class="gantt-bar-container">
              <div class="gantt-bar development" style="width: 45%;"></div>
              <div class="gantt-progress">进度72%</div>
            </div>
          </div>
          <div class="gantt-row">
            <div class="gantt-label">测试</div>
            <div class="gantt-bar-container">
              <div class="gantt-bar test" style="width: 10%;"></div>
              <div class="gantt-progress">进度0%</div>
            </div>
          </div>
          <div class="gantt-row">
            <div class="gantt-label">发布</div>
            <div class="gantt-bar-container">
              <div class="gantt-bar release" style="width: 10%;"></div>
              <div class="gantt-progress">进度0%</div>
            </div>
          </div>
          <div class="gantt-row">
            <div class="gantt-label">总结评审</div>
            <div class="gantt-bar-container">
              <div class="gantt-bar review" style="width: 10%;"></div>
              <div class="gantt-progress">进度0%</div>
            </div>
          </div>
          <div class="gantt-timeline">
            <div class="timeline-item">06/01</div>
            <div class="timeline-item">06/15</div>
            <div class="timeline-item">07/01</div>
            <div class="timeline-item">07/15</div>
            <div class="timeline-item">07/30</div>
            <div class="timeline-item">08/01</div>
            <div class="timeline-item">08/15</div>
            <div class="timeline-item">09/01</div>
            <div class="timeline-item">09/15</div>
          </div>
        </div>

        <!-- 项目周报 -->
        <div class="project-report">
          <h3>项目周报</h3>
          <div class="report-header">
            <span>预计完成时间: 2023-08-08</span>
            <span>存在风险: <span class="risk">9</span></span>
            <span>存在问题: <span class="problem">26</span></span>
          </div>
          <div class="report-content">
            <div class="progress-circle">
              <div class="circle">
                <span class="progress-text">50%</span>
                <span class="progress-label">总进度</span>
              </div>
            </div>
            <div class="report-stats">
              <div class="stat-item">
                <h4>需求</h4>
                <p>总需求数: 1596个</p>
                <p>已完成: 152个</p>
                <p>未关闭: 168个</p>
              </div>
              <div class="stat-item">
                <h4>任务</h4>
                <p>总任务数: 59个</p>
                <p>未开始: 26个</p>
                <p>进行中: 168个</p>
              </div>
              <div class="stat-item">
                <h4>Bug</h4>
                <p>总Bug数: 153个</p>
                <p>已关闭: 52个</p>
                <p>未关闭: 69个</p>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 右侧内容 -->
      <div class="right-content">
        <!-- 最新动态 -->
        <div class="latest-activities">
          <h3>最新动态</h3>
          <ul class="activity-list">
            <li v-for="(activity, index) in activities" :key="index">
              <span class="activity-time">{{ activity.time }}</span>
              <span class="activity-content">{{ activity.content }}</span>
              <span class="activity-project">{{ activity.project }}</span>
            </li>
          </ul>
        </div>

        <!-- 项目进展趋势图 -->
        <div class="trend-chart">
          <h3>项目进展趋势图</h3>
          <div ref="trendChartRef" class="chart"></div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import * as echarts from 'echarts';


// 最新动态数据
const activities = ref([
  { time: '08月08日 12:12', content: '张三审批了研发需求', project: '班牌模板调整，参考海康，增加竖版' },
  { time: '08月08日 12:12', content: '王麻子确认通过研发需求', project: '家校互通留言' },
  { time: '08月08日 12:12', content: '王麻子创建了产品', project: '家校互通平台管理' },
  { time: '08月08日 12:12', content: '王麻子登录系统', project: '实践教学管理平台' },
  { time: '08月08日 12:12', content: '李四开始了项目', project: '实践教学管理平台' },
  { time: '08月08日 12:12', content: '张三审批了研发需求', project: '班牌模板调整，参考海康，增加竖版' },
  { time: '08月08日 12:12', content: '王麻子确认通过研发需求', project: '家校互通留言' },
  { time: '08月08日 12:12', content: '王麻子创建了产品', project: '家校互通平台管理' },
  { time: '08月08日 12:12', content: '王麻子登录系统', project: '实践教学管理平台' },
  { time: '08月08日 12:12', content: '李四开始了项目', project: '实践教学管理平台' },
  { time: '08月08日 12:12', content: '张三审批了研发需求', project: '班牌模板调整，参考海康，增加竖版' }
]);

// 趋势图引用
const trendChartRef = ref(null);

// 初始化趋势图
const initTrendChart = () => {
  if (trendChartRef.value) {
    const chart = echarts.init(trendChartRef.value);

    const option = {
      tooltip: {
        trigger: 'axis'
      },
      legend: {
        data: ['计划用时', '实际用时'],
        top: 0
      },
      grid: {
        left: '3%',
        right: '4%',
        bottom: '3%',
        containLabel: true
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: ['06/01', '06/15', '07/01', '07/15', '07/30', '08/01', '08/15', '09/01', '09/15']
      },
      yAxis: {
        type: 'value',
        min: 0,
        max: 30,
        interval: 5
      },
      series: [
        {
          name: '计划用时',
          type: 'line',
          data: [15, 16, 17, 18, 17, 16, 15, 14, 13],
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: {
            color: '#73d13d'
          },
          itemStyle: {
            color: '#73d13d'
          }
        },
        {
          name: '实际用时',
          type: 'line',
          data: [10, 11, 13, 12, 14, 12, 13, 11, 12],
          symbol: 'circle',
          symbolSize: 8,
          lineStyle: {
            color: '#1890ff'
          },
          itemStyle: {
            color: '#1890ff'
          }
        }
      ]
    };

    chart.setOption(option);

    // 响应式调整
    window.addEventListener('resize', () => {
      chart.resize();
    });
  }
};

// 组件挂载后初始化图表
onMounted(() => {
  initTrendChart();
});
</script>

<style scoped>
.iteration-detail {
  padding: 20px;
  background-color: #fff;
  min-height: 100vh;
}

.main-content {
  display: flex;
  gap: 20px;
}

.left-content {
  flex: 2;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

/* 甘特图样式 */
.gantt-chart {
  background-color: #fff;
  padding: 20px;
}

.gantt-row {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.gantt-label {
  width: 80px;
  font-size: 14px;
  color: #606266;
}

.gantt-bar-container {
  flex: 1;
  position: relative;
  height: 20px;
  background-color: #f0f0f0;
  overflow: hidden;
}

.gantt-bar {
  height: 100%;
  border-radius: 10px;
}

.gantt-bar.design {
  background-color: #1890ff;
}

.gantt-bar.requirement {
  background-color: #1890ff;
}

.gantt-bar.development {
  background-color: #1890ff;
}

.gantt-bar.test {
  background-color: #1890ff;
}

.gantt-bar.release {
  background-color: #1890ff;
}

.gantt-bar.review {
  background-color: #1890ff;
}

.gantt-progress {
  position: absolute;
  right: 10px;
  top: 0;
  line-height: 20px;
  font-size: 12px;
  color: #606266;
}

.gantt-timeline {
  display: flex;
  justify-content: space-between;
  margin-top: 20px;
  padding-top: 10px;
}

.timeline-item {
  font-size: 12px;
  color: #909399;
  text-align: center;
  flex: 1;
}

/* 项目周报样式 */
.project-report {
  background-color: #fff;
  padding: 20px;
}

.project-report h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.report-header {
  display: flex;
  gap: 30px;
  margin-bottom: 20px;
  font-size: 14px;
  color: #606266;
}

.risk {
  color: #ff4d4f;
  font-weight: bold;
}

.problem {
  color: #fa8c16;
  font-weight: bold;
}

.report-content {
  display: flex;
  align-items: center;
  gap: 30px;
}

.progress-circle {
  flex: 0 0 120px;
}

.circle {
  width: 120px;
  height: 120px;
  border-radius: 50%;
  border: 8px solid #e6f7ff;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  position: relative;
}

.circle::before {
  content: '';
  position: absolute;
  top: -8px;
  left: -8px;
  right: -8px;
  bottom: -8px;
  border-radius: 50%;
  border: 8px solid transparent;
  border-top-color: #1890ff;
  border-right-color: #1890ff;
  transform: rotate(45deg);
}

.progress-text {
  font-size: 24px;
  font-weight: bold;
  color: #1890ff;
}

.progress-label {
  font-size: 14px;
  color: #606266;
  margin-top: 5px;
}

.report-stats {
  flex: 1;
  display: flex;
  gap: 20px;
}

.stat-item {
  flex: 1;
  background-color: #f5f7fa;
  padding: 15px;
  border-radius: 4px;
}

.stat-item h4 {
  margin-bottom: 10px;
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}

.stat-item p {
  margin: 5px 0;
  font-size: 13px;
  color: #606266;
}

/* 最新动态样式 */
.latest-activities {
  background-color: #fff;
  border-radius: 4px;
  padding: 20px;
}

.latest-activities h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.activity-list {
  list-style: none;
  padding: 0;
  margin: 0;
  max-height: 300px;
  overflow-y: auto;
}

.activity-list li {
  padding: 10px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
}

.activity-list li:last-child {
  border-bottom: none;
}

.activity-time {
  color: #909399;
  margin-right: 10px;
}

.activity-content {
  color: #303133;
  margin-right: 10px;
}

.activity-project {
  color: #1890ff;
  font-weight: bold;
}

/* 趋势图样式 */
.trend-chart {
  background-color: #fff;
  padding: 20px;
}

.trend-chart h3 {
  margin-bottom: 15px;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.chart {
  width: 100%;
  height: 300px;
}
</style>