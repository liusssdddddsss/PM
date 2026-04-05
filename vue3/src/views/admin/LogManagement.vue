<template>
  <div>
    <el-card style="max-width: 100%; margin-bottom: 10px">
      <h3>日志管理</h3>
      <p>查看系统操作日志，包括用户登录、退出、操作记录等信息。</p>
    </el-card>

    <!-- 统计信息卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{totalLogs}}</p>
            <span>总日志数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{todayLogs}}</p>
            <span>今日日志</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{activeUsers}}</p>
            <span>活跃用户</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{errorLogs}}</p>
            <span>错误日志</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card style="max-width: 100%; margin: 10px 0">
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索日志"
          style="width: 250px; margin-right: 10px"
        >
          <template #prefix>
            <el-icon><Search /></el-icon>
          </template>
        </el-input>
        <el-select
          v-model="typeFilter"
          placeholder="选择日志类型"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="全部类型" value="" />
          <el-option label="登录日志" value="登录日志" />
          <el-option label="操作日志" value="操作日志" />
        </el-select>
        <el-input
          v-model="userSearchQuery"
          placeholder="搜索用户"
          style="width: 150px; margin-right: 10px"
        >
          <template #prefix>
            <el-icon><User /></el-icon>
          </template>
        </el-input>
        <el-input
          v-model="teamSearchQuery"
          placeholder="搜索团队"
          style="width: 150px; margin-right: 10px"
        >
          <template #prefix>
            <el-icon><OfficeBuilding /></el-icon>
          </template>
        </el-input>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 300px"
        />
      </div>
    </el-card>

    <!-- 日志列表 -->
    <el-card style="max-width: 100%; margin: 10px 0">
      <h3>日志记录</h3>
      <el-table :data="filteredLogs" style="width: 100%">
        <el-table-column prop="logType" label="日志类型" width="120">
          <template #default="scope">
            <el-tag :type="scope.row.logType === '登录日志' ? 'info' : 'primary'">
              {{scope.row.logType}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="userId" label="工号" width="100" />
        <el-table-column prop="action" label="操作内容" />
        <el-table-column prop="actionType" label="操作类型" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.actionType)">
              {{scope.row.actionType}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="loginTime" label="登录时间" width="180" />
        <el-table-column prop="logoutTime" label="退出时间" width="180" />
        <el-table-column prop="actionTime" label="操作时间" width="180" />
        <el-table-column prop="ipAddress" label="IP地址" width="150" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <el-tag :type="scope.row.status === '成功' ? 'success' : 'danger'">
              {{scope.row.status}}
            </el-tag>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="filteredLogs.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from "vue";
import axios from 'axios';
import { Search, User, OfficeBuilding } from '@element-plus/icons-vue';

// 统计数据
const totalLogs = ref(0);
const todayLogs = ref(0);
const activeUsers = ref(0);
const errorLogs = ref(0);

// 搜索和筛选
const searchQuery = ref('');
const typeFilter = ref('');
const userSearchQuery = ref('');
const teamSearchQuery = ref('');
const dateRange = ref([]);
const currentPage = ref(1);

// 用户列表
const users = ref([]);

// 日志数据
const logs = ref([]);

// 获取统计数据
const fetchLogSummary = async () => {
  try {
    const response = await axios.get('http://localhost:9091/admin/logs/summary');
    if (response.data.code === 200) {
      const summary = response.data.data;
      totalLogs.value = summary.totalLogs || 0;
      todayLogs.value = summary.todayLogs || 0;
      activeUsers.value = summary.activeUsers || 0;
      errorLogs.value = summary.errorLogs || 0;
    }
  } catch (error) {
    console.error('获取日志统计失败:', error);
  }
};

// 获取日志列表
const fetchLogs = async () => {
  try {
    const response = await axios.get('http://localhost:9091/admin/logs');
    if (response.data.code === 200) {
      logs.value = response.data.data || [];
    }
  } catch (error) {
    console.error('获取日志列表失败:', error);
  }
};

// 获取用户列表
const fetchUsers = async () => {
  try {
    const response = await axios.get('http://localhost:9091/admin/users');
    if (response.data.code === 200) {
      users.value = (response.data.data || []).map(user => ({
        userId: user.username,
        name: user.name || user.username
      }));
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
  }
};

// 过滤后的日志
const filteredLogs = computed(() => {
  let result = logs.value;
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(log => 
      (log.userName && log.userName.toLowerCase().includes(query)) ||
      (log.action && log.action.toLowerCase().includes(query))
    );
  }
  
  // 日志类型过滤
  if (typeFilter.value) {
    result = result.filter(log => log.logType === typeFilter.value);
  }
  
  // 用户搜索过滤
  if (userSearchQuery.value) {
    const query = userSearchQuery.value.toLowerCase();
    result = result.filter(log => 
      (log.userName && log.userName.toLowerCase().includes(query)) ||
      (log.userId && log.userId.toLowerCase().includes(query))
    );
  }
  
  // 团队搜索过滤
  if (teamSearchQuery.value) {
    const query = teamSearchQuery.value.toLowerCase();
    result = result.filter(log => 
      (log.teamName && log.teamName.toLowerCase().includes(query))
    );
  }
  
  // 日期过滤
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0]);
    const endDate = new Date(dateRange.value[1]);
    endDate.setHours(23, 59, 59, 999);
    
    result = result.filter(log => {
      const actionDate = new Date(log.actionTime || log.loginTime);
      return actionDate >= startDate && actionDate <= endDate;
    });
  }
  
  return result;
});

// 获取标签类型
const getTagType = (actionType) => {
  switch (actionType) {
    case 'login':
      return 'info';
    case 'logout':
      return 'warning';
    case 'submit':
      return 'success';
    case 'update':
      return 'primary';
    case 'delete':
      return 'danger';
    default:
      return 'default';
  }
};

// 分页处理
const handleCurrentChange = (page) => {
  currentPage.value = page;
};

// 组件挂载时获取数据
onMounted(() => {
  fetchLogSummary();
  fetchLogs();
  fetchUsers();
});
</script>

<style scoped>
/* 统计卡片 */
.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #238EFF;
  margin-bottom: 5px;
}

/* 搜索和筛选 */
.search-filter {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

/* 表格样式 */
.el-table {
  margin-top: 10px;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}
</style>
