<template>
  <div class="test-statistics">
    <div class="main-content">
      <!-- 左侧内容 -->
      <div class="left-content">
        <!-- 顶部：Bug统计 -->
        <el-card style="max-width: 98%">
          <div class="test-view">
            <!-- 近期模块审核 -->
            <div class="list-name">
              <h3>近期模块审核</h3>
              <div class="list-content">
                <ul>
                  <li v-for="item in testList">
                    {{item}}
                  </li>
                </ul>
              </div>
            </div>
            
            <!-- Bug统计 -->
            <div class="bug-statistics">
              <div class="bug-content">
                <div class="bug-header">
                  <h3>Bug</h3>
                  <div class="bug-circle-container">
                    <div class="progress-circle">
                      <div class="circle">
                        <span class="progress-text">50%</span>
                      </div>
                    </div>
                    <div class="bug-details">
                      <p>有效Bug: {{bugStats.youXiaoBug}}</p>
                      <p>已修复: {{bugStats.bugRepair}}</p>
                      <p>未关闭: {{bugStats.noClose}}</p>
                    </div>
                  </div>
                </div>

                <div class="bug-stats">
                  <h3>Bug统计</h3>
                  <div class="stat-row" v-for="(item, index) in bugStats.detail" :key="index">
                    <span class="stat-label">{{item.name}}</span>
                    <div class="stat-bar">
                      <div class="bar-fill" :style="{width: item.percentage + '%'}"></div>
                    </div>
                    <span class="stat-value">{{item.count}}</span>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 未关闭的测试单 -->
            <div class="unclosed-tests">
              <h3>未关闭的测试单</h3>
              <div class="list-content">
                <ul class="test-list">
                  <li v-for="(item, index) in unclosedTestCases" :key="index">{{ item }}</li>
                </ul>
              </div>
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
            <div class="card-content">
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
            <div style="display: flex; gap: 10px;">
              <el-button type="primary" size="small" @click="goToCreateBug">创建Bug</el-button>
              <el-button type="text" icon="el-icon-setting" @click="goToBugList">更多</el-button>
            </div>
          </div>
          <div class="card-content">
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
        </div>
        </el-card>

        <!-- 指派给我的用例列表 -->
        <el-card style="max-width: 98%;margin-top: 10px">
        <div class="assigned-cases">
          <div class="section-header">
            <h3>指派给我的用例列表</h3>
          </div>
          <div class="card-content">
            <el-table :data="assignedTestCases" stripe style="width: 100%">
              <el-table-column prop="name" label="用例名称" min-width="180" />
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
              <el-table-column prop="project" label="所属项目" min-width="150" />
            </el-table>
          </div>
        </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {useRouter} from "vue-router";
import request from "@/utils/request.js";

// 近期模块审核
const testList = ref([]);

// bug统计
const bugStats = ref({
  youXiaoBug: 0,
  bugRepair: 0,
  noClose: 0,
  detail: [
    {name: '昨天新增', count: 0, percentage: 0},
    {name: '昨天解决', count: 0, percentage: 0},
    {name: '今日新增', count: 0, percentage: 0},
    {name: '昨天关闭', count: 0, percentage: 0},
    {name: '今日关闭', count: 0, percentage: 0}
  ]
});

// 未关闭的测试单
const unclosedTestCases = ref([]);

// 待测试的测试单
const pendingTestCases = ref([]);

const router = useRouter();
const goToTestList =()=>{
  router.push('/test/testList');
};

const goToBugList =()=>{
  router.push('/test/bugList');
};

const goToCreateBug =()=>{
  router.push('/test/createBug');
};

// 指派给我的Bug列表
const assignedBugs = ref([]);

// 指派给我的用例列表
const assignedTestCases = ref([]);

// 获取测试统计数据
const fetchTestStatistics = async () => {
  try {
    const response = await request.get('/dashboard/test-statistics');
    if (response.data.code === 200 && response.data.data) {
      const data = response.data.data;
      
      // 更新Bug统计数据
      bugStats.value = {
        youXiaoBug: data.validBugs || 0,
        bugRepair: data.fixedBugs || 0,
        noClose: data.unclosedBugs || 0,
        detail: [
          {name: '昨天新增', count: data.yesterdayNew || 0, percentage: 100},
          {name: '昨天解决', count: data.yesterdaySolved || 0, percentage: data.yesterdayNew > 0 ? (data.yesterdaySolved * 100) / data.yesterdayNew : 0},
          {name: '今日新增', count: data.todayNew || 0, percentage: data.yesterdayNew > 0 ? (data.todayNew * 100) / data.yesterdayNew : 0},
          {name: '昨天关闭', count: 0, percentage: 0},
          {name: '今日关闭', count: data.todaySolved || 0, percentage: data.yesterdayNew > 0 ? (data.todaySolved * 100) / data.yesterdayNew : 0}
        ]
      };
      
      // 更新未关闭的测试单
      unclosedTestCases.value = data.testLists || [];
      
      // 更新待测试的测试单（这里使用测试列表数据，实际应该从测试单表中获取）
      pendingTestCases.value = data.testLists ? data.testLists.map((item, index) => ({
        name: item,
        priority: index % 2 === 0 ? '一般' : '严重',
        product: '测试产品',
        startDate: new Date().toISOString().split('T')[0],
        endDate: new Date().toISOString().split('T')[0]
      })) : [];
      
      // 更新近期模块审核列表
      testList.value = data.testLists || [];
    }
  } catch (error) {
    console.error('获取测试统计数据失败:', error);
  }
};

// 获取用户Bug列表
const fetchBugs = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 注意：后端使用username查询，因为bugs表的assignee_id存储的是username
      const response = await request.get(`/dashboard/user-bugs?username=${user.username}`);
      if (response.data.code === 200 && Array.isArray(response.data.data)) {
        assignedBugs.value = response.data.data;
      }
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
  }
};

// 获取用户测试任务
const fetchUserTestTasks = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 注意：后端使用username查询，因为test_suites表的assignee_id存储的是username
      const response = await request.get(`/dashboard/user-test-tasks?username=${user.username}`);
      if (response.data.code === 200 && Array.isArray(response.data.data)) {
        assignedTestCases.value = response.data.data;
      }
    }
  } catch (error) {
    console.error('获取用户测试任务失败:', error);
  }
};

// 组件挂载时获取数据
onMounted(() => {
  fetchTestStatistics();
  fetchBugs();
  fetchUserTestTasks();
});

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
  gap: 20px;
  height: 200px;
}

/* 近期模块审核 */
.list-name{
  flex: 1;
  padding: 0 10px;
  height: 200px;
  display: flex;
  flex-direction: column;
}

.list-name h3 {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.list-content {
  flex: 1;
  overflow-y: auto;
}

.list-name ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.list-name li {
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
  font-size: 13px;
  color: #606266;
}

.list-name li:last-child {
  border-bottom: none;
}

/* Bug统计样式 */
.bug-statistics {
  flex: 2;
  padding: 0 10px;
  border-left: 1px solid #f0f0f0;
  border-right: 1px solid #f0f0f0;
  height: 200px;
  display: flex;
  flex-direction: column;
}

.bug-content {
  display: flex;
  gap: 20px;
  flex: 1;
  align-items: flex-start;
}

.bug-header{
  flex: 1;
  display: flex;
  flex-direction: column;
}

.bug-stats {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.bug-header h3 {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

.bug-circle-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
}

.progress-circle {
  flex: 0 0 80px;
}

.bug-details {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin-top: 10px;
}

.bug-details p {
  margin: 0;
  font-size: 12px;
  color: #606266;
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

.bug-stats h3 {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

.stat-row {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  gap: 10px;
}

.stat-label {
  width: 80px;
  font-size: 13px;
  color: #606266;
}

.stat-bar {
  flex: 1;
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background-color: #1890ff;
  border-radius: 4px;
}

.stat-value {
  width: 30px;
  font-size: 13px;
  color: #606266;
  text-align: right;
}

/* 未关闭的测试单 */
.unclosed-tests {
  flex: 1;
  padding: 0 10px;
  height: 200px;
  display: flex;
  flex-direction: column;
}

.unclosed-tests h3 {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.test-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.unclosed-tests .list-content {
  flex: 1;
  overflow-y: auto;
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
  max-height: 400px;
  display: flex;
  flex-direction: column;
}

.pending-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.pending-tests .card-content {
  flex: 1;
  overflow-y: auto;
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
  max-height: 300px;
  display: flex;
  flex-direction: column;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-shrink: 0;
}

.card-content {
  flex: 1;
  overflow-y: auto;
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

.no-permission {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
}
</style>