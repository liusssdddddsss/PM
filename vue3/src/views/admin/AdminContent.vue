<template>
  <div>
    <!-- 其他模块内容 -->
    <div>
      <el-card style="max-width: 100%; margin-bottom: 10px">
        <h3>管理员面板</h3>
        <p>欢迎使用管理员控制台，您可以在此管理用户、团队、查看日志和处理反馈。</p>
      </el-card>

      <!-- 统计信息卡片 -->
      <el-row :gutter="20">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <p class="stat-number">{{userCount}}</p>
              <span>用户总数</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <p class="stat-number">{{teamCount}}</p>
              <span>团队数量</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <p class="stat-number">{{logCount}}</p>
              <span>操作日志</span>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-item">
              <p class="stat-number">{{feedbackCount}}</p>
              <span>反馈数量</span>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <div class="caoZuo-users">
        <!-- 最近操作 -->
        <div style="span:1">
          <el-card style="max-width: 98%; margin-top: 10px">
            <h3>最近操作</h3>
            <el-table :data="recentActivities" style="width: 100%">
              <el-table-column prop="time" label="时间" width="180" />
              <el-table-column prop="action" label="操作" />
              <el-table-column prop="user" label="用户" width="120" />
              <el-table-column prop="status" label="状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '成功' ? 'success' : 'danger'">
                    {{scope.row.status}}
                  </el-tag>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
        <div style="sapn:2">
          <!-- 用户信息列表 -->
          <el-card style="width: 750px; margin: 10px 0">
            <div class="card-header">
                <h3>用户管理</h3>
                <div class="header-actions">
                  <el-input
                    v-model="searchQuery"
                    placeholder="搜索用户"
                    style="width: 200px; margin-right: 10px"
                    prefix-icon="el-icon-search"
                  />
                  <el-button type="primary" @click="showAddUserDialog">添加用户</el-button>
                </div>
              </div>
            <el-table :data="userList" style="width: 100%">
              <el-table-column prop="userId" label="工号" width="80">
                <template #default="scope">
                  <el-button type="text" @click="showUserDetail(scope.row)">{{scope.row.userId}}</el-button>
                </template>
              </el-table-column>
              <el-table-column prop="name" label="姓名" width="90" />
              <el-table-column prop="status" label="账号状态" width="100">
                <template #default="scope">
                  <el-tag :type="scope.row.status === '启用' ? 'success' : 'danger'">
                    {{scope.row.status}}
                  </el-tag>
                </template>
              </el-table-column>
              <el-table-column prop="position" label="用户职称" width="100" />
              <el-table-column label="操作" min-width="320">
                <template #default="scope">
                  <el-button size="small" type="primary" @click="showUserDetail(scope.row)">查看</el-button>
                  <el-button size="small" type="success" @click="editPermission(scope.row)">权限</el-button>
                  <el-button size="small" type="warning" @click="editUser(scope.row)">编辑</el-button>
                  <el-button size="small" type="danger" @click="deleteUser(scope.row)">
                    {{scope.row.status === '启用' ? '禁用' : '启用'}}
                  </el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </div>
      </div>

    </div>

    <!-- 添加/编辑用户对话框 -->
    <el-dialog
      v-model="userDialogVisible"
      :title="isEditUser ? '编辑用户' : '添加用户'"
      width="500px"
    >
      <el-form :model="formUser" label-width="80px">
        <el-form-item label="工号">
          <el-input v-model="formUser.userId" :disabled="isEditUser" />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="formUser.name" />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="formUser.email" />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="formUser.department" />
        </el-form-item>
        <el-form-item label="性别">
          <el-select v-model="formUser.sex">
            <el-option label="男" value="男" />
            <el-option label="女" value="女" />
          </el-select>
        </el-form-item>
        <el-form-item label="密码">
          <el-input v-model="formUser.password" type="password" placeholder="留空表示不修改密码" />
        </el-form-item>
        <el-form-item label="职称">
          <el-select v-model="formUser.position" placeholder="请选择职称">
            <el-option label="产品经理" value="产品经理" />
            <el-option label="开发者" value="开发者" />
            <el-option label="测试者" value="测试者" />
            <el-option label="管理员" value="管理员" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="formUser.status" placeholder="请选择状态">
            <el-option label="启用" value="启用" />
            <el-option label="禁用" value="禁用" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveUser">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 用户详情对话框 -->
    <el-dialog
      v-model="userDetailVisible"
      title="用户详情"
      width="600px"
    >
      <el-form :model="selectedUser" label-width="100px">
        <el-form-item label="姓名">
          <el-input v-model="selectedUser.name" disabled />
        </el-form-item>
        <el-form-item label="性别">
          <el-input v-model="selectedUser.sex" disabled />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="selectedUser.userId" disabled />
        </el-form-item>
        <el-form-item label="邮箱">
          <el-input v-model="selectedUser.email" disabled />
        </el-form-item>
        <el-form-item label="所属部门">
          <el-input v-model="selectedUser.department" disabled />
        </el-form-item>
        <el-form-item label="职称">
          <el-input v-model="selectedUser.position" disabled />
        </el-form-item>
        <el-form-item label="账号状态">
          <el-input v-model="selectedUser.status" disabled />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="userDetailVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 权限编辑对话框 -->
    <el-dialog
      v-model="permissionDialogVisible"
      title="编辑用户权限"
      width="600px"
    >
      <el-form :model="formPermission" label-width="100px">
        <el-form-item label="用户">
          <el-input v-model="formPermission.userName" disabled />
        </el-form-item>
        <el-form-item label="角色">
          <el-select v-model="formPermission.role" placeholder="请选择角色">
            <el-option label="超级管理员" value="超级管理员" />
            <el-option label="产品经理" value="产品经理" />
            <el-option label="开发者" value="开发者" />
            <el-option label="测试者" value="测试者" />
          </el-select>
        </el-form-item>
        <el-form-item label="权限列表">
          <el-table :data="permissionList" style="width: 100%">
            <el-table-column prop="code" label="权限代码" width="150" />
            <el-table-column prop="name" label="权限名称" />
            <el-table-column label="权限" width="100">
              <template #default="scope">
                <el-checkbox v-model="scope.row.checked" />
              </template>
            </el-table-column>
          </el-table>
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="permissionDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePermission">保存</el-button>
        </span>
      </template>
    </el-dialog>
    
    <!-- 修改密码对话框 -->
    <el-dialog
      v-model="passwordDialogVisible"
      title="修改密码"
      width="400px"
    >
      <el-form :model="formPassword" label-width="80px">
        <el-form-item label="用户">
          <el-input v-model="formPassword.userName" disabled />
        </el-form-item>
        <el-form-item label="新密码">
          <el-input v-model="formPassword.newPassword" type="password" />
        </el-form-item>
        <el-form-item label="确认密码">
          <el-input v-model="formPassword.confirmPassword" type="password" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="passwordDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="savePassword">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from "vue";
import request from "@/utils/request.js";

// 统计数据
const userCount = ref(0);
const teamCount = ref(0);
const logCount = ref(0);
const feedbackCount = ref(0);

// 最近操作
const recentActivities = ref([]);

// 从后端获取管理员面板数据
const fetchDashboardData = async () => {
  try {
    const response = await request.get('/admin/dashboard/summary');
    if (response.data.code === 200) {
      const data = response.data.data;
      userCount.value = data.userCount || 0;
      teamCount.value = data.teamCount || 0;
      logCount.value = data.logCount || 0;
      feedbackCount.value = data.feedbackCount || 0;
      recentActivities.value = data.recentActivities || [];
    }
  } catch (error) {
    console.error('获取管理员面板数据失败:', error);
  }
};

// 页面加载时获取数据
onMounted(() => {
  fetchDashboardData();
  fetchUserList();
});

// 搜索关键词
const searchQuery = ref('');

// 原始用户列表
const originalUserList = ref([]);

// 从后端获取用户列表
const fetchUserList = async () => {
  try {
    const response = await request.get('/admin/users');
    if (response.data.code === 200) {
      const users = response.data.data || [];
      console.log('获取到的用户列表:', users);
      originalUserList.value = users;
    }
  } catch (error) {
    console.error('获取用户列表失败:', error);
  }
};

// 过滤后的用户列表
const userList = computed(() => {
  if (!searchQuery.value) {
    return originalUserList.value;
  }
  const query = searchQuery.value.toLowerCase();
  return originalUserList.value.filter(user => 
    user.userId.toLowerCase().includes(query) ||
    user.name.toLowerCase().includes(query) ||
    user.position.toLowerCase().includes(query)
  );
});

// 对话框状态
const userDialogVisible = ref(false);
const userDetailVisible = ref(false);
const permissionDialogVisible = ref(false);
const passwordDialogVisible = ref(false);
const isEditUser = ref(false);

// 表单数据
const formUser = ref({
  userId: '',
  name: '',
  email: '',
  department: '',
  sex: '',
  password: '',
  position: '',
  status: '启用'
});

const selectedUser = ref({
  name: '',
  sex: '',
  userId: '',
  email: '',
  department: '',
  position: '',
  status: ''
});

const formPermission = ref({
  userName: '',
  role: ''
});

const formPassword = ref({
  userName: '',
  newPassword: '',
  confirmPassword: ''
});

// 权限列表
const permissionList = ref([
  {code: 'USER_MANAGE', name: '用户管理', checked: false},
  {code: 'ROLE_MANAGE', name: '角色权限配置', checked: false},
  {code: 'ADMIN_SETTINGS', name: '系统配置', checked: false},
  {code: 'LOG_VIEW', name: '查看全系统日志', checked: false}
]);

// 方法
const showAddUserDialog = () => {
  isEditUser.value = false;
  formUser.value = {
    userId: '',
    name: '',
    email: '',
    department: '',
    sex: '',
    password: '',
    position: '',
    status: '启用'
  };
  userDialogVisible.value = true;
};

const editUser = (user) => {
  console.log('编辑用户时的用户对象:', user);
  isEditUser.value = true;
  // 处理性别字段，将后端存储的字符转换为前端显示的文本
  let sexValue = '';
  if (user.sex === '男' || user.sex === '女') {
    sexValue = user.sex;
  } else if (user.sex === 'M') {
    sexValue = '男';
  } else if (user.sex === 'F') {
    sexValue = '女';
  }
  
  formUser.value = {
    userId: user.userId,
    name: user.name,
    email: user.email || '',
    department: user.department || '',
    sex: sexValue,
    password: '',
    position: user.position,
    status: user.status
  };
  console.log('设置后的formUser:', formUser.value);
  userDialogVisible.value = true;
};

const saveUser = async () => {
  try {
    if (isEditUser.value) {
      // 编辑用户
      const response = await request.put(`/admin/users/${formUser.value.userId}`, formUser.value);
      if (response.data.code === 200) {
        // 重新获取用户列表
        await fetchUserList();
        // 刷新统计数据
        await fetchDashboardData();
        userDialogVisible.value = false;
      }
    } else {
      // 添加用户
      const response = await request.post('/admin/users', formUser.value);
      if (response.data.code === 200) {
        // 重新获取用户列表
        await fetchUserList();
        // 刷新统计数据
        await fetchDashboardData();
        userDialogVisible.value = false;
      }
    }
  } catch (error) {
    console.error('保存用户失败:', error);
  }
};

const showUserDetail = (user) => {
  // 显示从后端获取的用户信息
  selectedUser.value = {
    name: user.name,
    sex: user.sex || '',
    userId: user.userId,
    email: user.email || '',
    department: user.department || '',
    position: user.position,
    status: user.status
  };
  userDetailVisible.value = true;
};

const editPermission = (user) => {
  formPermission.value = {
    userName: user.name,
    role: user.position
  };
  // 根据角色设置权限
  permissionList.value.forEach(permission => {
    permission.checked = user.position === '管理员';
  });
  permissionDialogVisible.value = true;
};

const savePermission = () => {
  permissionDialogVisible.value = false;
};

const changePassword = (user) => {
  formPassword.value = {
    userName: user.name,
    newPassword: '',
    confirmPassword: ''
  };
  passwordDialogVisible.value = true;
};

const savePassword = async () => {
  try {
    // 找到对应的用户
    const user = userList.value.find(u => u.name === formPassword.value.userName);
    if (user) {
      const response = await request.put(`/admin/users/${user.userId}/password`, { password: formPassword.value.newPassword });
      if (response.data.code === 200) {
        // 刷新统计数据
        await fetchDashboardData();
        passwordDialogVisible.value = false;
      }
    }
  } catch (error) {
    console.error('修改密码失败:', error);
  }
};

const deleteUser = async (user) => {
  try {
    const newStatus = user.status === '启用' ? '禁用' : '启用';
    const response = await request.put(`/admin/users/${user.userId}/status`, { status: newStatus });
    if (response.data.code === 200) {
      // 重新获取用户列表
      await fetchUserList();
      // 刷新统计数据
      await fetchDashboardData();
    }
  } catch (error) {
    console.error('更新用户状态失败:', error);
  }
};
</script>

<style scoped>
/* 最近操作和用户管理 */
.caoZuo-users {
  display: flex;
  gap: 20px;
  margin-top: 5px;
}

.caoZuo-users > div {
  flex: 1;
}

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

/* 表格样式 */
.el-table {
  margin-top: 10px;
}

/* 卡片头部样式 */
.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-header h3 {
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
}

/* 对话框样式 */
.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

/* 权限表格样式 */
.el-table .el-checkbox {
  margin-right: 0;
}

/* 进度条样式 */
.el-progress {
  margin-top: 5px;
}
</style>