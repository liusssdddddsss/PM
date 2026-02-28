<template>
  <div class="test-statistics">
    <div class="main-content">
      <!-- 左侧内容 -->
      <div class="left-content">
        <!-- 顶部：Bug统计 -->
        <el-card style="max-width: 98%">
          <div class="test-view">
            <div class="list-name">
              <h3>测试统计</h3>
              <ul>
                <li v-for="item in testList">
                  {{item}}
                </li>
              </ul>
            </div>
            <div class="bug-statistics">
              <div class="bug-header">
                <h3>Bug</h3>
                <div class="bug-progress">
                  <div class="progress-circle">
                    <div class="circle">
                      <span class="progress-text">50%</span>
                    </div>
                  </div>
                  <div class="bug-details">
                    <p>有效Bug: {{youXiaoBug}}</p>
                    <p>已修复: {{bugRepair}}</p>
                    <p>未关闭: {{ noClose }}</p>
                  </div>
                </div>
              </div>

              <div class="bug-stats">
                <div class="stat-item">
                  <h3>Bug统计</h3>
                  <div class="stat-row">
                    <ul>
                      <li v-for="(a,b) in bugYouGuan"
                          :key="b">
                        {{a.name}} {{a.count}} <el-progress :percentage="a.count" />
                      </li>
                    </ul>
                  </div>
                </div>
              </div>
            </div>
            <div class="stat-item">
              <h3>未关闭的测试单</h3>
              <ul class="test-list">
                <li v-for="(c, d) in unclosedTestCases" :key="d">{{ c }}</li>
              </ul>
            </div>
          </div>
        </el-card>

        <!-- 底部：待测试的测试单 -->
        <el-card style="max-width: 98%;margin-top: 10px">
          <div class="pending-tests">
            <div class="pending-header">
              <h3>待测试的测试单</h3>
              <el-button type="text" icon="el-icon-setting"
                         @click="goToTestList"
              >更多</el-button>
            </div>
            <el-table :data="pendingTestCases" stripe style="width: 100%">
              <el-table-column prop="name" label="测试单名称" min-width="200" />
              <el-table-column prop="priority" label="优先级" width="100">
                <template #default="scope">
                  <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="product" label="所属产品" width="150" />
              <el-table-column prop="startDate" label="开始日期" width="120" />
              <el-table-column prop="endDate" label="结束日期" width="120" />
            </el-table>
          </div>
        </el-card>
      </div>

      <!-- 右侧内容 -->
      <div class="right-content">
        <!-- 指派给我的Bug列表 -->
        <el-card style="max-width: 98%">
        <div class="assigned-bugs">
          <div class="section-header">
            <h3>指派给我的Bug列表</h3>
            <el-button type="text" icon="el-icon-setting">更多</el-button>
          </div>
          <el-table :data="assignedBugs" stripe style="width: 100%">
            <el-table-column prop="name" label="Bug名称" min-width="200" />
            <el-table-column prop="priority" label="优先级" width="100">
              <template #default="scope">
                <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="status" label="状态" width="100">
              <template #default="scope">
                <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
        </el-card>

        <!-- 指派给我的用例列表 -->
        <el-card style="max-width: 98%;margin-top: 10px">
        <div class="assigned-cases">
          <div class="section-header">
            <h3>指派给我的用例列表</h3>
          </div>
          <div class="no-data">
            <p>暂无</p>
          </div>
        </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import {useRouter} from "vue-router";

const testList = ref([
  '班牌模版调整',
  '班牌PC端管理界面调整',
  '界面优化调整',
  '家校互通留言台',
  '终端教师端查询评分标准列表'
])

// bug统计
const bugYouGuan = ref([
  {name:'昨天新增',count:30},
  {name:'今日新增',count:30},
  {name:'昨天解决',count:30},
  {name:'今日解决',count:30},
  {name:'昨天关闭',count:30},
  {name:'今日关闭',count:30}
]);

// 未关闭的测试单
const unclosedTestCases = ref([
  '班牌PC端管理界面调整',
  '学期结束后，自动给学生推送实训档案',
  '班牌模板调整，参考海康，增加竖版',
  '家校互通留言台',
  '终端教师端查询评分标准列表'
]);

// 待测试的测试单
const pendingTestCases = ref([
  { name: '家长端，界面优化调整，新增功能：授权监控', priority: '一般', product: '智慧教室 智慧云盘', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '班牌PC端管理界面调整，样式统一，菜单归类', priority: '严重', product: '实践教学管理平台', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '班牌模板调整，参考海康，增加竖版', priority: '严重', product: '电子班牌管理系统', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '数据大屏-实训教学资源大数据', priority: '一般', product: '智慧校园(中学版)', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '终端教师端查询评分标准列表', priority: '一般', product: '教务考试系统', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '实训任务、示范列表表情优化', priority: '严重', product: '在线试卷批改', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '学期结束后，自动给学生推送实训档案', priority: '严重', product: '家校互通平台', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '终端教师端查询评分标准列表', priority: '严重', product: '智慧园区OA办公系统', startDate: '2023-08-08', endDate: '2023-08-08' },
  { name: '实训任务、示范列表表情优化', priority: '严重', product: '物业管理平台', startDate: '2023-08-08', endDate: '2023-08-08' }
]);

const router = useRouter();
const goToTestList =()=>{
  router.push('/test/testList');
};

// 指派给我的Bug列表
const assignedBugs = ref([
  { name: '切换季度弹窗错误', priority: '一般', status: '解决中' },
  { name: '移动应用崩溃现象', priority: '严重', status: '已解决' },
  { name: '导出数据含有错误信息的数据', priority: '严重', status: '已解决' },
  { name: '项目归档功能失效', priority: '一般', status: '已解决' },
  { name: '自定义工作流后缺少步骤', priority: '一般', status: '已解决' },
  { name: '项目成员列表重复显示', priority: '一般', status: '已解决' },
  { name: '预算调整表单验证失败', priority: '严重', status: '已解决' },
  { name: '实训任务、示范列表表情优化', priority: '严重', status: '已解决' }
]);

// 获取优先级对应的标签类型
const getPriorityType = (priority) => {
  switch (priority) {
    case '严重':
      return 'danger';
    case '一般':
      return 'info';
    default:
      return 'warning';
  }
};

// 获取状态对应的标签类型
const getStatusType = (status) => {
  switch (status) {
    case '解决中':
      return 'warning';
    case '已解决':
      return 'success';
    default:
      return 'info';
  }
};
</script>

<style scoped>
.test-statistics {
  min-height: 100vh;
}

.main-content {
  display: flex;
}

.left-content {
  flex: 3;
  display: flex;
  flex-direction: column;
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.test-view{
  display: flex;
  width: 100%;
  background-color: red;
}
/* Bug统计样式 */
.bug-statistics {
  display: flex;
  flex: 3;
  background-color: #fff;
  justify-content: center;
  align-items: center;
  text-align: center;
}
.list-name{
  flex: 1;
}
.bug-header{
  flex: 1;
}
.bug-stats{
  flex: 1;
  width: 100%;
  background-color: green;
}

.bug-progress {
  align-items: center;
  gap: 20px;
}

.progress-circle {
  flex: 0 0 80px;
}

.circle {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  border: 8px solid #e6f7ff;
  display: flex;
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
  font-size: 18px;
  font-weight: bold;
  color: #1890ff;
}

.bug-details p {
  margin: 5px 0;
  font-size: 13px;
  color: #606266;
}

.bug-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  margin-left: 20px;
}

.stat-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
}

.stat-row span:first-child {
  width: 80px;
}

.bar-fill {
  height: 100%;
  background-color: #1890ff;
  border-radius: 4px;
}

.test-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.test-list li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: #606266;
}

.test-list li:last-child {
  border-bottom: none;
}

/* 待测试的测试单样式 */
.pending-tests {
  background-color: #fff;
  padding: 20px;
}

.pending-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.pending-header h3 {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

/* 右侧内容样式 */
.assigned-bugs,
.assigned-cases {
  background-color: #fff;
  padding: 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.no-data {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}
</style>