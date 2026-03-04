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
          style="width: 300px; margin-right: 10px"
          prefix-icon="el-icon-search"
        />
        <el-select
          v-model="teamFilter"
          placeholder="选择团队"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="全部团队" value="" />
          <el-option v-for="team in teams" :key="team.id" :label="team.name" :value="team.id" />
        </el-select>
        <el-select
          v-model="userFilter"
          placeholder="选择用户"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="全部用户" value="" />
          <el-option v-for="user in users" :key="user.userId" :label="user.name" :value="user.userId" />
        </el-select>
        <el-select
          v-model="typeFilter"
          placeholder="选择操作类型"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="全部类型" value="" />
          <el-option label="登录" value="login" />
          <el-option label="退出" value="logout" />
          <el-option label="提交" value="submit" />
          <el-option label="修改" value="update" />
          <el-option label="删除" value="delete" />
          <el-option label="其他" value="other" />
        </el-select>
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
      <h3>操作日志</h3>
      <el-table :data="filteredLogs" style="width: 100%">
        <el-table-column prop="id" label="日志ID" width="80" />
        <el-table-column prop="teamName" label="团队" width="120" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="userId" label="工号" width="80" />
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
        <el-table-column prop="submitCount" label="提交次数" width="100" />
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

    <!-- 用户操作统计 -->
    <el-card style="max-width: 100%; margin: 10px 0">
      <h3>用户操作统计</h3>
      <el-table :data="userStats" style="width: 100%">
        <el-table-column prop="userName" label="用户" width="120" />
        <el-table-column prop="userId" label="工号" width="100" />
        <el-table-column prop="teamName" label="团队" width="120" />
        <el-table-column prop="loginCount" label="登录次数" width="100" />
        <el-table-column prop="submitCount" label="提交次数" width="100" />
        <el-table-column prop="updateCount" label="修改次数" width="100" />
        <el-table-column prop="deleteCount" label="删除次数" width="100" />
        <el-table-column prop="lastLogin" label="最后登录" width="180" />
      </el-table>
    </el-card>
  </div>
</template>

<script setup>
import {ref, computed} from "vue";

// 统计数据
const totalLogs = 1256;
const todayLogs = 42;
const activeUsers = 18;
const errorLogs = 5;

// 搜索和筛选
const searchQuery = ref('');
const teamFilter = ref('');
const userFilter = ref('');
const typeFilter = ref('');
const dateRange = ref([]);
const currentPage = ref(1);

// 团队列表
const teams = ref([
  {id: '1', name: '产品团队'},
  {id: '2', name: '开发团队'},
  {id: '3', name: '测试团队'},
  {id: '4', name: '设计团队'}
]);

// 用户列表
const users = ref([
  {userId: '001', name: '张三'},
  {userId: '002', name: '李四'},
  {userId: '003', name: '王五'},
  {userId: '004', name: '赵六'},
  {userId: '005', name: '孙七'}
]);

// 日志数据
const logs = ref([
  {
    id: '1',
    teamId: '1',
    teamName: '产品团队',
    userId: '001',
    userName: '张三',
    action: '登录系统',
    actionType: 'login',
    loginTime: '2024-01-15 09:00:00',
    logoutTime: '2024-01-15 18:00:00',
    actionTime: '2024-01-15 09:00:00',
    submitCount: 1,
    ipAddress: '192.168.1.100',
    status: '成功'
  },
  {
    id: '2',
    teamId: '2',
    teamName: '开发团队',
    userId: '002',
    userName: '李四',
    action: '提交代码',
    actionType: 'submit',
    loginTime: '2024-01-15 09:30:00',
    logoutTime: '2024-01-15 19:00:00',
    actionTime: '2024-01-15 10:00:00',
    submitCount: 5,
    ipAddress: '192.168.1.101',
    status: '成功'
  },
  {
    id: '3',
    teamId: '3',
    teamName: '测试团队',
    userId: '003',
    userName: '王五',
    action: '提交测试报告',
    actionType: 'submit',
    loginTime: '2024-01-15 10:00:00',
    logoutTime: '2024-01-15 18:30:00',
    actionTime: '2024-01-15 11:00:00',
    submitCount: 3,
    ipAddress: '192.168.1.102',
    status: '成功'
  },
  {
    id: '4',
    teamId: '1',
    teamName: '产品团队',
    userId: '001',
    userName: '张三',
    action: '修改产品需求',
    actionType: 'update',
    loginTime: '2024-01-15 09:00:00',
    logoutTime: '2024-01-15 18:00:00',
    actionTime: '2024-01-15 14:00:00',
    submitCount: 1,
    ipAddress: '192.168.1.100',
    status: '成功'
  },
  {
    id: '5',
    teamId: '2',
    teamName: '开发团队',
    userId: '002',
    userName: '李四',
    action: '退出系统',
    actionType: 'logout',
    loginTime: '2024-01-15 09:30:00',
    logoutTime: '2024-01-15 19:00:00',
    actionTime: '2024-01-15 19:00:00',
    submitCount: 5,
    ipAddress: '192.168.1.101',
    status: '成功'
  },
  {
    id: '6',
    teamId: '4',
    teamName: '设计团队',
    userId: '004',
    userName: '赵六',
    action: '上传设计稿',
    actionType: 'submit',
    loginTime: '2024-01-15 09:15:00',
    logoutTime: '2024-01-15 17:30:00',
    actionTime: '2024-01-15 10:30:00',
    submitCount: 2,
    ipAddress: '192.168.1.103',
    status: '成功'
  },
  {
    id: '7',
    teamId: '3',
    teamName: '测试团队',
    userId: '003',
    userName: '王五',
    action: '删除测试用例',
    actionType: 'delete',
    loginTime: '2024-01-15 10:00:00',
    logoutTime: '2024-01-15 18:30:00',
    actionTime: '2024-01-15 15:00:00',
    submitCount: 3,
    ipAddress: '192.168.1.102',
    status: '成功'
  },
  {
    id: '8',
    teamId: '1',
    teamName: '产品团队',
    userId: '005',
    userName: '孙七',
    action: '登录系统',
    actionType: 'login',
    loginTime: '2024-01-15 10:30:00',
    logoutTime: '2024-01-15 19:30:00',
    actionTime: '2024-01-15 10:30:00',
    submitCount: 0,
    ipAddress: '192.168.1.104',
    status: '成功'
  }
]);

// 过滤后的日志
const filteredLogs = computed(() => {
  let result = logs.value;
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(log => 
      log.userName.toLowerCase().includes(query) ||
      log.action.toLowerCase().includes(query) ||
      log.teamName.toLowerCase().includes(query)
    );
  }
  
  // 团队过滤
  if (teamFilter.value) {
    result = result.filter(log => log.teamId === teamFilter.value);
  }
  
  // 用户过滤
  if (userFilter.value) {
    result = result.filter(log => log.userId === userFilter.value);
  }
  
  // 类型过滤
  if (typeFilter.value) {
    result = result.filter(log => log.actionType === typeFilter.value);
  }
  
  // 日期过滤
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0]);
    const endDate = new Date(dateRange.value[1]);
    endDate.setHours(23, 59, 59, 999);
    
    result = result.filter(log => {
      const actionDate = new Date(log.actionTime);
      return actionDate >= startDate && actionDate <= endDate;
    });
  }
  
  return result;
});

// 用户操作统计
const userStats = computed(() => {
  const statsMap = new Map();
  
  logs.value.forEach(log => {
    const key = log.userId;
    if (!statsMap.has(key)) {
      statsMap.set(key, {
        userId: log.userId,
        userName: log.userName,
        teamId: log.teamId,
        teamName: log.teamName,
        loginCount: 0,
        submitCount: 0,
        updateCount: 0,
        deleteCount: 0,
        lastLogin: log.loginTime
      });
    }
    
    const stat = statsMap.get(key);
    switch (log.actionType) {
      case 'login':
        stat.loginCount++;
        if (new Date(log.loginTime) > new Date(stat.lastLogin)) {
          stat.lastLogin = log.loginTime;
        }
        break;
      case 'submit':
        stat.submitCount += log.submitCount;
        break;
      case 'update':
        stat.updateCount++;
        break;
      case 'delete':
        stat.deleteCount++;
        break;
    }
  });
  
  return Array.from(statsMap.values());
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