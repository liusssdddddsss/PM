<template>
  <div class="test-statistics">
    <div class="main-content">
      <!-- 左侧面板 -->
      <div class="left-panel" style="width: 55%;display: flex;flex-direction: column;gap: 10px;">
        <!-- 第一个卡片：项目列表 + Bug + Bug统计（横向等宽排列） -->
        <el-card class="stats-card">
          <!-- 项目列表 -->
          <div style="display: flex">
            <div class="pro-column" style="flex:1">
              <h3>项目列表</h3>
              <ul class="project-list-horizontal">
                <li
                    v-for="project in userProjects"
                    :key="project.id"
                    :class="{ 'active': selectedProject === project.name }"
                    @click="selectProject(project.name)"
                >
                  {{ project.name }}
                </li>
              </ul>
            </div>

            <!-- Bug模块 -->
            <div class="bug-column" style="flex:1">
              <h3>Bug</h3>
              <div class="bug-circle-container">
                <div class="progress-circle">
                  <div class="circle">
                    <span class="progress-text">{{ bugStats.percentage }}%</span>
                  </div>
                </div>
                <div class="bug-details-horizontal">
                  <p>有效Bug: {{ bugStats.youXiaoBug }}</p>
                  <p>已修复: {{ bugStats.bugRepair }}</p>
                  <p>未关闭: {{ bugStats.noClose }}</p>
                </div>
              </div>
            </div>

            <!-- Bug统计模块 -->
            <div class="bug-s-column" style="flex:1">
              <h3>Bug统计</h3>
              <div class="bug-stat-grid">
                <div class="bug-stat-item-horizontal" v-for="(item, index) in bugStats.detail" :key="index">
                  <span class="label-text">{{ item.name }}</span>
                  <span class="label-count">{{ item.count }}</span>
                </div>
              </div>
            </div>
          </div>

        </el-card>
        
        <!-- 第二个卡片：待测试的测试单 -->
        <el-card style="flex: 1;display: flex;flex-direction: column;">
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
                <el-table-column prop="name" label="测试单名称" min-width="150" />
                <el-table-column prop="priority" label="优先级" width="80">
                  <template #default="scope">
                    <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="product" label="所属产品" width="120" />
                <el-table-column label="开始日期" width="100">
                  <template #default="scope">
                    {{ formatDate(scope.row.startDate) }}
                  </template>
                </el-table-column>
                <el-table-column label="结束日期" width="100">
                  <template #default="scope">
                    {{ formatDate(scope.row.endDate) }}
                  </template>
                </el-table-column>
              </el-table>
            </div>
          </div>
        </el-card>
      </div>

      <!-- 右侧面板：指派给我的Bug列表 -->
      <div class="right-panel" style="width: 45%;">
        <el-card style="height: calc(100vh - 80px);display: flex;flex-direction: column;">
          <div class="assigned-bugs" style="flex: 1;display: flex;flex-direction: column;">
            <div class="section-header">
              <h3>待处理的Bug列表</h3>
              <div style="display: flex; gap: 10px;">
                <el-button type="primary" size="small" @click="goToCreateBug">创建Bug</el-button>
                <el-button type="text" icon="el-icon-setting" @click="goToBugList">更多</el-button>
              </div>
            </div>
            <div class="card-content" style="flex: 1;overflow-y: auto;">
              <el-table :data="assignedBugs" stripe style="width: 100%;height: 100%;">
                <el-table-column prop="name" label="Bug名称" min-width="150" />
                <el-table-column prop="priority" label="优先级" width="80">
                  <template #default="scope">
                    <el-tag :type="getPriorityType(scope.row.priority)">{{ scope.row.priority }}</el-tag>
                  </template>
                </el-table-column>
                <el-table-column prop="status" label="状态" width="80">
                  <template #default="scope">
                    <el-tag :type="getStatusType(scope.row.status)">{{ scope.row.status }}</el-tag>
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
import { ref, onMounted, computed } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request.js';

const router = useRouter();

// 用户参与的项目列表
const userProjects = ref([]);

// 选中的项目
const selectedProject = ref('');

// bug统计
const bugStats = ref({
  youXiaoBug: 0,
  bugRepair: 0,
  noClose: 0,
  percentage: 0,
  detail: [
    { name: '昨天新增', count: 0 },
    { name: '昨天解决', count: 0 },
    { name: '今天新增', count: 0 },
    { name: '昨天关闭', count: 0 },
    { name: '今天关闭', count: 0 }
  ]
});

// 待测试的测试单
const pendingTestCases = ref([]);

// 指派给我的Bug列表
const assignedBugs = ref([]);

// 当前用户角色
const userRole = ref('');

// 判断用户是否为开发者
const isDeveloper = computed(() => {
  return userRole.value === 3;
});

// 获取当前登录用户的username
const getCurrentUsername = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    const user = JSON.parse(userStr);
    return user.username || user.name || '';
  }
  return '';
};

// 获取用户参与的项目列表
const fetchUserProjects = async () => {
  try {
    const username = getCurrentUsername();
    const response = await request.get('/dashboard/user-projects', {
      params: { username }
    });
    console.log('获取用户参与的项目响应:', response);
    if (response.data.code === 200) {
      userProjects.value = response.data.data;
      console.log('用户参与的项目列表:', userProjects.value);
      if (userProjects.value.length > 0) {
        selectedProject.value = userProjects.value[0].name;
      }
    }
  } catch (error) {
    console.error('获取用户参与的项目失败:', error);
  }
};

// 获取测试统计数据
const fetchTestStatistics = async (projectName = '') => {
  try {
    const username = getCurrentUsername();
    console.log('获取测试统计数据，用户名:', username, '项目名:', projectName);
    
    // 获取用户角色
    try {
      const userRes = await request.get('/admin/findAll');
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
    
    let params = { username };
    if (projectName) {
      params.projectName = projectName;
    }
    
    const response = await request.get('/dashboard/test-statistics', { params });
    console.log('获取测试统计响应:', response);
    
    if (response.data.code === 200) {
      const data = response.data.data;
      console.log('测试统计数据:', data);
      
      const totalBugs = data.validBugs || 0;
      const unclosedBugs = data.unclosedBugs || 0;
      const fixedBugs = totalBugs - unclosedBugs;
      
      const percentage = totalBugs > 0 ? Math.round((fixedBugs / totalBugs) * 100) : 0;
      
      bugStats.value = {
        youXiaoBug: totalBugs,
        bugRepair: fixedBugs,
        noClose: unclosedBugs,
        percentage: percentage,
        detail: [
          { name: '昨天新增', count: data.yesterdayNew || 0 },
          { name: '昨天解决', count: data.yesterdaySolved || 0 },
          { name: '今天新增', count: data.todayNew || 0 },
          { name: '昨天关闭', count: data.yesterdayClosed || 0 },
          { name: '今天关闭', count: data.todayClosed || 0 }
        ]
      };
      
      pendingTestCases.value = data.pendingTests || [];
      console.log('待测试的测试单:', pendingTestCases.value);
    }
  } catch (error) {
    console.error('获取测试统计数据失败:', error);
  }
};

// 获取指派给我的Bug列表
const fetchBugs = async (projectName = '') => {
  try {
    const username = getCurrentUsername();
    console.log('获取Bug列表，用户名:', username, '项目名:', projectName);
    
    let params = { username };
    if (projectName) {
      params.projectName = projectName;
    }
    
    const response = await request.get('/dashboard/user-bugs', { params });
    console.log('获取Bug列表响应:', response);
    
    if (response.data.code === 200 && Array.isArray(response.data.data)) {
      assignedBugs.value = response.data.data;
      console.log('指派给我的Bug列表:', assignedBugs.value);
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
  }
};

// 选择项目
const selectProject = async (projectName) => {
  selectedProject.value = projectName;
  await fetchTestStatistics(projectName);
  await fetchBugs(projectName);
};

// 页面跳转
const goToTestList = () => {
  router.push('/test/testList');
};

const goToBugList = () => {
  router.push('/test/bugList');
};

const goToCreateBug = () => {
  router.push('/test/createBug');
};

// 获取优先级对应的标签类型
const getPriorityType = (priority) => {
  switch (priority) {
    case '严重':
    case '紧急':
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
    case '处理中':
      return 'warning';
    case '已解决':
      return 'success';
    default:
      return 'info';
  }
};

// 格式化日期，只显示到天
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  try {
    if (dateStr.includes('T')) {
      return dateStr.split('T')[0];
    } else if (dateStr.includes(' ')) {
      return dateStr.split(' ')[0];
    }
    if (dateStr.length >= 10) {
      return dateStr.substring(0, 10);
    }
    return dateStr;
  } catch (e) {
    console.error('日期格式化错误:', e, '原始值:', dateStr);
    return dateStr;
  }
};

// 组件挂载时获取数据
onMounted(async () => {
  console.log('Tests.vue 页面加载');
  await fetchUserProjects();
  if (selectedProject.value) {
    await fetchTestStatistics(selectedProject.value);
    await fetchBugs(selectedProject.value);
  } else {
    await fetchTestStatistics();
    await fetchBugs();
  }
});
</script>

<style scoped>
.test-statistics {
  min-height: 100vh;
}

.main-content {
  display: flex;
  gap: 15px;
  padding: 10px;
}

.left-panel {
  display: flex;
  flex-direction: column;
}

.right-panel {
  display: flex;
  flex-direction: column;
}

.stats-card {
  display: flex;
  gap: 0;
}

.pro-column, .bug-column, .bug-s-column {
  padding: 15px;
}

.pro-column {
  border-right: 1px solid #f0f0f0;
}

.bug-column {
  border-right: 1px solid #f0f0f0;
}

.project-list-horizontal {
  list-style: none;
  padding: 0;
  margin: 0;
  flex: 1;
  overflow-y: auto;
  max-height: 200px;
}

.project-list-horizontal li {
  padding: 8px 10px;
  cursor: pointer;
  font-size: 12px;
  color: #606266;
  transition: all 0.3s ease;
  border-radius: 4px;
  margin-bottom: 3px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.project-list-horizontal li:hover {
  background-color: #ecf5ff;
  color: #409eff;
}

.project-list-horizontal li.active {
  background-color: #409eff;
  color: #fff;
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

.bug-details-horizontal {
  display: flex;
  justify-content: space-around;
  width: 100%;
  margin-top: 15px;
}

.bug-details-horizontal p {
  margin: 5px 0;
  font-size: 12px;
  color: #606266;
}

.bug-stat-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 8px;
  flex: 1;
}

.bug-stat-item-horizontal {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 12px;
  color: #606266;
}

.pending-tests, .assigned-bugs {
  background-color: #fff;
  display: flex;
  flex-direction: column;
}

.pending-header, .section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  flex-shrink: 0;
  padding: 10px;
  padding-bottom: 0;
}

.card-content {
  flex: 1;
  overflow-y: auto;
  padding: 0 10px 10px;
}

.no-permission-text {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100px;
  color: #909399;
  font-size: 14px;
}
</style>
