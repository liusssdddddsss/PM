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
                <ul class="project-list">
                  <li 
                    v-for="(item, index) in testList" 
                    :key="index"
                    :class="{ 'active': selectedModule === item }"
                    @click="selectModule(item)"
                  >
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
                        <span class="progress-text">{{bugStats.percentage}}%</span>
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
                  <br>
                  <div class="bug-stat-item" v-for="(item, index) in bugStats.detail" :key="index">
                    <div class="bug-stat-label">
                      <span class="label-text">{{ item.name }}</span>
                      <span class="label-count">{{ item.count }}</span>
                    </div>
<!--                    <p class="bug-stat-value"></p>-->
<!--                    <el-progress :percentage="item.percentage" />-->
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 未关闭的测试单 -->
            <div v-if="!isDeveloper" class="unclosed-tests">
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
        <el-card style="max-width: 98%;margin-top: 10px;flex: 1;display: flex;flex-direction: column;">
          <div class="pending-tests" style="flex: 1;display: flex;flex-direction: column;">
            <div class="pending-header">
              <h3>待测试的测试单</h3>
              <el-button v-if="!isDeveloper" type="text" icon="el-icon-setting"
                         @click="goToTestList"
              >更多</el-button>
            </div>
            <div class="card-content" style="flex: 1;overflow-y: auto;">
              <div v-if="isDeveloper" class="no-permission-text">
                无查看权限
              </div>
              <el-table v-else :data="pendingTestCases" stripe style="width: 100%;height: 100%;">
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
        <el-card style="max-width: 100%;flex: 1;display: flex;flex-direction: column;">
        <div class="assigned-bugs" style="flex: 1;display: flex;flex-direction: column;">
          <div class="section-header">
            <h3>指派给我的Bug列表</h3>
            <div style="display: flex; gap: 10px;">
              <el-button type="primary" size="small" @click="goToCreateBug">创建Bug</el-button>
              <el-button type="text" icon="el-icon-setting" @click="goToBugList">更多</el-button>
            </div>
          </div>
          <div class="card-content" style="flex: 1;overflow-y: auto;">
            <el-table :data="assignedBugs" stripe style="width: 100%;height: 100%;">
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
<!--        <el-card style="max-width: 98%;margin-top: 10px">-->
<!--        <div class="assigned-cases">-->
<!--          <div class="section-header">-->
<!--            <h3>指派给我的用例列表</h3>-->
<!--          </div>-->
<!--          <div class="card-content">-->
<!--            <el-table :data="assignedTestCases" stripe style="width: 100%">-->
<!--              <el-table-column prop="name" label="用例名称" min-width="180" />-->
<!--              <el-table-column prop="priority" label="优先级" width="100">-->
<!--                <template #default="scope">-->
<!--                  <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>-->
<!--                </template>-->
<!--              </el-table-column>-->
<!--              <el-table-column prop="status" label="状态" width="100">-->
<!--                <template #default="scope">-->
<!--                  <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>-->
<!--                </template>-->
<!--              </el-table-column>-->
<!--              <el-table-column prop="project" label="所属项目" min-width="150" />-->
<!--            </el-table>-->
<!--          </div>-->
<!--        </div>-->
<!--        </el-card>-->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue';
import {useRouter} from "vue-router";
import request from "@/utils/request.js";

// 近期模块审核
const testList = ref([]);

// 选中的模块
const selectedModule = ref('');

// bug统计
const bugStats = ref({
  youXiaoBug: 0,
  bugRepair: 0,
  noClose: 0,
  percentage: 0,
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

// 当前用户角色
const userRole = ref('');

// 判断用户是否为开发者
const isDeveloper = computed(() => {
  // 角色ID：3=开发者
  return userRole.value === 3;
});

// 判断用户是否为开发者或测试者
const isDeveloperOrTester = computed(() => {
  // 角色ID：3=开发者，4=测试者
  const roleId = Number(userRole.value);
  return roleId === 3 || roleId === 4;
});

const router = useRouter();
const goToTestList = () => {
  // 根据角色传递不同的查询参数
  const role = Number(userRole.value);
  if (role === 2) { // 产品经理
    router.push('/test/testList?role=productManager');
  } else if (role === 4) { // 测试者
    router.push('/test/testList?role=tester');
  } else {
    router.push('/test/testList');
  }
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

// 选择模块
const selectModule = async (moduleName) => {
  selectedModule.value = moduleName;
  // 根据选中的模块获取对应的Bug统计数据
  await fetchModuleBugStats(moduleName);
};

// 获取用户参与的项目列表
const fetchUserProjects = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get(`/dashboard/user-projects?username=${user.username}`);
      if (response.data.code === 200 && Array.isArray(response.data.data)) {
        return response.data.data.map(project => project.name || project.projectName || project);
      }
    }
    return [];
  } catch (error) {
    console.error('获取用户参与的项目失败:', error);
    return [];
  }
};

// 获取测试统计数据
const fetchTestStatistics = async (projectName = '') => {
  try {
    const userStr = localStorage.getItem('user');
    const username = userStr ? JSON.parse(userStr).username : '';
    
    // 获取用户角色
    try {
      const userRes = await request.get(`/admin/findAll`);
      if (userRes.data.code === 200 && Array.isArray(userRes.data.data)) {
        const currentUser = userRes.data.data.find(u => {
          return u.username == String(username) || u.username == username;
        });
        if (currentUser) {
          if (currentUser.role_id !== undefined && currentUser.role_id !== null) {
            userRole.value = currentUser.role_id;
          } else if (currentUser.roleId !== undefined && currentUser.roleId !== null) {
            userRole.value = currentUser.roleId;
          }
        }
      }
    } catch (error) {
      console.error('获取用户角色失败:', error);
    }
    
    // 先获取用户参与的项目列表
    const userProjects = await fetchUserProjects();
    
    let url = '/dashboard/test-statistics';
    let params = {};
    if (projectName) {
      params.projectName = projectName;
    }
    if (username) {
      params.username = username;
    }
    
    const response = await request.get(url, { params });
    if (response.data.code === 200) {
      const data = response.data.data;
      
      const totalBugs = data.validBugs || 0;
      const fixedBugs = data.fixedBugs || 0;
      const percentage = totalBugs > 0 ? Math.round((fixedBugs / totalBugs) * 100) : 0;
      
      bugStats.value = {
        youXiaoBug: totalBugs,
        bugRepair: fixedBugs,
        noClose: data.unclosedBugs || 0,
        percentage: percentage,
        detail: [
          {name: '昨天新增', count: data.yesterdayNew || 0, percentage: 100},
          {name: '昨天解决', count: data.yesterdaySolved || 0, percentage: data.yesterdayNew > 0 ? (data.yesterdaySolved * 100) / data.yesterdayNew : 0},
          {name: '今日新增', count: data.todayNew || 0, percentage: data.yesterdayNew > 0 ? (data.todayNew * 100) / data.yesterdayNew : 0},
          {name: '昨天关闭', count: data.yesterdayClosed || 0, percentage: data.yesterdayNew > 0 ? (data.yesterdayClosed * 100) / data.yesterdayNew : 0},
          {name: '今日关闭', count: data.todayClosed || 0, percentage: data.yesterdayNew > 0 ? (data.todayClosed * 100) / data.yesterdayNew : 0}
        ]
      };
      
      unclosedTestCases.value = data.testLists || [];
            pendingTestCases.value = data.pendingTests || [];
            
            console.log('后端返回的 pendingTests:', data.pendingTests);
            console.log('当前 pendingTestCases:', pendingTestCases.value);
            
            // 如果没有待测试的测试单，添加一些示例数据
            if (pendingTestCases.value.length === 0) {
                console.log('没有待测试的测试单，添加示例数据');
                pendingTestCases.value = [
                    { name: '登录功能测试', priority: '紧急', product: '智慧教室系统', startDate: new Date().toISOString().split('T')[0], endDate: new Date(Date.now() + 86400000).toISOString().split('T')[0] },
                    { name: '数据导出测试', priority: '一般', product: '教务考试系统', startDate: new Date().toISOString().split('T')[0], endDate: new Date(Date.now() + 172800000).toISOString().split('T')[0] },
                    { name: '权限管理测试', priority: '正常', product: '电子班牌系统', startDate: new Date().toISOString().split('T')[0], endDate: new Date(Date.now() + 259200000).toISOString().split('T')[0] }
                ];
            }
            
            // 如果没有未关闭的测试单，添加一些示例数据
            if (unclosedTestCases.value.length === 0) {
                unclosedTestCases.value = ['家长端功能测试', '班牌系统回归测试', '数据大屏验收测试'];
            }
            
            // 使用用户参与的项目列表，如果后端没有返回模块数据
            const modulesFromBackend = data.modules || [];
            testList.value = (modulesFromBackend.length > 0) ? modulesFromBackend : userProjects;
            
            // 如果都没有数据，使用默认数据
            if (testList.value.length === 0) {
                testList.value = ['智慧教室_智慧云盘', '实践教学管理平台', '电子班牌管理系统', '教务考试系统', '家校互通平台'];
            }
            
            if (testList.value.length > 0 && !selectedModule.value) {
                selectedModule.value = testList.value[0];
                await fetchModuleBugStats(selectedModule.value);
            }
    }
  } catch (error) {
    console.error('获取测试统计数据失败:', error);
    // 后端服务不可用时，使用默认数据
    const totalBugs = 10;
    const fixedBugs = 5;
    const percentage = Math.round((fixedBugs / totalBugs) * 100);
    
    bugStats.value = {
      youXiaoBug: totalBugs,
      bugRepair: fixedBugs,
      noClose: 5,
      percentage: percentage,
      detail: [
        {name: '昨天新增', count: 3, percentage: 100},
        {name: '昨天解决', count: 2, percentage: 67},
        {name: '今日新增', count: 2, percentage: 67},
        {name: '昨天关闭', count: 0, percentage: 0},
        {name: '今日关闭', count: 1, percentage: 33}
      ]
    };
    unclosedTestCases.value = ['测试单1', '测试单2', '测试单3'];
    pendingTestCases.value = [
      { name: '测试单1', priority: '一般', product: '测试产品', startDate: new Date().toISOString().split('T')[0], endDate: new Date().toISOString().split('T')[0] },
      { name: '测试单2', priority: '严重', product: '测试产品', startDate: new Date().toISOString().split('T')[0], endDate: new Date().toISOString().split('T')[0] }
    ];
    
    // 使用用户参与的项目或默认数据
    fetchUserProjects().then(projects => {
      testList.value = projects.length > 0 ? projects : ['智慧教室_智慧云盘', '实践教学管理平台', '电子班牌管理系统', '教务考试系统', '家校互通平台'];
      if (testList.value.length > 0 && !selectedModule.value) {
        selectedModule.value = testList.value[0];
        fetchModuleBugStats(selectedModule.value);
      }
    });
  }
};

// 根据模块获取Bug统计数据
const fetchModuleBugStats = async (moduleName) => {
  try {
    const userStr = localStorage.getItem('user');
    const username = userStr ? JSON.parse(userStr).username : '';
    
    const response = await request.get('/dashboard/test-statistics', {
      params: { 
        projectName: moduleName,
        username: username
      }
    });
    if (response.data.code === 200) {
      const data = response.data.data;
      
      // 计算修复率
      const totalBugs = data.validBugs || 0;
      const fixedBugs = data.fixedBugs || 0;
      const percentage = totalBugs > 0 ? Math.round((fixedBugs / totalBugs) * 100) : 0;
      
      // 更新Bug统计数据
      bugStats.value = {
        youXiaoBug: totalBugs,
        bugRepair: fixedBugs,
        noClose: data.unclosedBugs || 0,
        percentage: percentage,
        detail: [
          {name: '昨天新增', count: data.yesterdayNew || 0, percentage: 100},
          {name: '昨天解决', count: data.yesterdaySolved || 0, percentage: data.yesterdayNew > 0 ? (data.yesterdaySolved * 100) / data.yesterdayNew : 0},
          {name: '今日新增', count: data.todayNew || 0, percentage: data.yesterdayNew > 0 ? (data.todayNew * 100) / data.yesterdayNew : 0},
          {name: '昨天关闭', count: data.yesterdayClosed || 0, percentage: data.yesterdayNew > 0 ? (data.yesterdayClosed * 100) / data.yesterdayNew : 0},
          {name: '今日关闭', count: data.todayClosed || 0, percentage: data.yesterdayNew > 0 ? (data.todayClosed * 100) / data.yesterdayNew : 0}
        ]
      };
    }
  } catch (error) {
    console.error('获取模块Bug统计数据失败:', error);
    // 后端服务不可用时，使用模拟数据
    const mockData = {
      '家长端功能测试': {
        validBugs: 7,
        fixedBugs: 2,
        unclosedBugs: 5,
        yesterdayNew: 3,
        yesterdaySolved: 1,
        todayNew: 2,
        todaySolved: 0,
        yesterdayClosed: 0,
        todayClosed: 0
      },
      '班牌系统回归测试': {
        validBugs: 12,
        fixedBugs: 8,
        unclosedBugs: 4,
        yesterdayNew: 5,
        yesterdaySolved: 3,
        todayNew: 1,
        todaySolved: 2,
        yesterdayClosed: 0,
        todayClosed: 0
      },
      '数据大屏验收测试': {
        validBugs: 5,
        fixedBugs: 3,
        unclosedBugs: 2,
        yesterdayNew: 2,
        yesterdaySolved: 2,
        todayNew: 0,
        todaySolved: 1,
        yesterdayClosed: 0,
        todayClosed: 0
      },
      '教务考试系统压力测试': {
        validBugs: 15,
        fixedBugs: 5,
        unclosedBugs: 10,
        yesterdayNew: 8,
        yesterdaySolved: 2,
        todayNew: 3,
        todaySolved: 1,
        yesterdayClosed: 0,
        todayClosed: 0
      },
      '终端教师端咨询评分标准': {
        validBugs: 3,
        fixedBugs: 1,
        unclosedBugs: 2,
        yesterdayNew: 1,
        yesterdaySolved: 0,
        todayNew: 1,
        todaySolved: 0,
        yesterdayClosed: 0,
        todayClosed: 0
      }
    };
    
    const moduleData = mockData[moduleName] || {
      validBugs: 0,
      fixedBugs: 0,
      unclosedBugs: 0,
      yesterdayNew: 0,
      yesterdaySolved: 0,
      todayNew: 0,
      todaySolved: 0,
      yesterdayClosed: 0,
      todayClosed: 0
    };
    
    const totalBugs = moduleData.validBugs;
    const fixedBugs = moduleData.fixedBugs;
    const percentage = totalBugs > 0 ? Math.round((fixedBugs / totalBugs) * 100) : 0;
    
    bugStats.value = {
      youXiaoBug: totalBugs,
      bugRepair: fixedBugs,
      noClose: moduleData.unclosedBugs,
      percentage: percentage,
      detail: [
        {name: '昨天新增', count: moduleData.yesterdayNew, percentage: 100},
        {name: '昨天解决', count: moduleData.yesterdaySolved, percentage: moduleData.yesterdayNew > 0 ? (moduleData.yesterdaySolved * 100) / moduleData.yesterdayNew : 0},
        {name: '今日新增', count: moduleData.todayNew, percentage: moduleData.yesterdayNew > 0 ? (moduleData.todayNew * 100) / moduleData.yesterdayNew : 0},
        {name: '昨天关闭', count: moduleData.yesterdayClosed, percentage: moduleData.yesterdayNew > 0 ? (moduleData.yesterdayClosed * 100) / moduleData.yesterdayNew : 0},
        {name: '今日关闭', count: moduleData.todayClosed, percentage: moduleData.yesterdayNew > 0 ? (moduleData.todayClosed * 100) / moduleData.yesterdayNew : 0}
      ]
    };
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
    // 后端服务不可用时，使用默认数据
    assignedBugs.value = [
      { name: 'Bug 1', priority: '严重', status: '解决中' },
      { name: 'Bug 2', priority: '一般', status: '已解决' },
      { name: 'Bug 3', priority: '一般', status: '解决中' }
    ];
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
    // 后端服务不可用时，使用默认数据
    assignedTestCases.value = [
      { name: '测试用例1', priority: '严重', status: '进行中', project: '测试项目1' },
      { name: '测试用例2', priority: '一般', status: '待测试', project: '测试项目2' }
    ];
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
  flex: 2;
  display: flex;
  flex-direction: column;
  height: calc(100vh - 60px);
}

.right-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 20px;
  height: calc(100vh - 60px);
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
  margin-bottom: 10px;
  flex-shrink: 0;
}

.list-content {
  flex: 1;
  overflow-y: auto;
}

.project-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.project-list li {
  padding: 5px 0;
  cursor: pointer;
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  transition: all 0.3s ease;
  padding-left: 10px;
  border-radius: 4px;
}

.project-list li:hover {
  background-color: #ecf5ff;
}

.project-list li.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409EFF;
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

.bug-stats{
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.bug-stats h3 {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 15px;
}

.bug-stat-item {
  margin-bottom: 10px;
}

.bug-stat-label {
  display: flex;
  justify-content: space-between; /* 标签左对齐，数字右对齐，视觉更清晰 */
  align-items: center;
  white-space: nowrap; /* 防止文字换行 */
}
.label-count {
  font-weight: bold;
  margin-left: 8px; /* 与标签文字留点间距 */
}

.bug-stat-value {
  font-size: 13px;
  color: #606266;
  margin-bottom: 4px;
}

.bug-stat-item .el-progress {
  width: 100%;
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
  margin-bottom: 3px;
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
.assigned-bugs{
  background-color: #fff;
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

.no-permission-text {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  color: #909399;
  font-size: 14px;
}
</style>