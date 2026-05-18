<template>
  <div class="bug-submit">
    <h3>创建Bug</h3>
    <div class="form-container">
      <el-form :model="bugForm" label-width="120px" :rules="formRules" ref="bugFormRef">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属项目">
              <el-select
                v-model="bugForm.projectId"
                placeholder="请选择项目（与迭代二选一）"
                filterable
                remote
                reserve-keyword
                :remote-method="searchProjects"
                :loading="projectLoading"
                style="width: 100%"
                @change="onProjectSelect"
                @focus="onProjectFocus"
              >
                <el-option
                  v-for="item in projectOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属迭代">
              <el-select
                v-model="bugForm.iterationId"
                placeholder="请选择迭代（与项目二选一）"
                filterable
                remote
                reserve-keyword
                :remote-method="searchIterations"
                :loading="iterationLoading"
                style="width: 100%"
                @focus="onIterationFocus"
                @change="onIterationSelect"
              >
                <el-option
                  v-for="item in iterationOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前状态">
              <el-select v-model="bugForm.status" placeholder="请选择" style="width: 100%">
                <el-option label="待处理" value="0" />
                <el-option label="处理中" value="1" />
                <el-option label="已解决" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="Bug类型">
              <el-select v-model="bugForm.bugType" placeholder="请选择" style="width: 100%">
                <el-option label="代码错误" value="代码错误" />
                <el-option label="界面问题" value="界面问题" />
                <el-option label="逻辑错误" value="逻辑错误" />
                <el-option label="性能问题" value="性能问题" />
                <el-option label="安全问题" value="安全问题" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="严重程度" prop="severity">
              <el-select v-model="bugForm.severity" placeholder="请选择" style="width: 100%">
                <el-option label="紧急" value="1" />
                <el-option label="一般" value="2" />
                <el-option label="正常" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报告人">
              <el-input
                :value="currentUserName"
                disabled
                placeholder="当前登录用户"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select
                v-model="bugForm.assigneeId"
                placeholder="请选择负责人"
                filterable
                remote
                reserve-keyword
                :remote-method="searchUsers"
                :loading="userLoading"
                style="width: 100%"
                @focus="onUserFocus"
              >
                <el-option
                  v-for="item in userOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="Bug标题" prop="title">
              <el-input v-model="bugForm.title" placeholder="请输入Bug标题" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="Bug描述">
              <el-input
                  v-model="bugForm.description"
                  type="textarea"
                  placeholder="请输入Bug描述"
                  :rows="4"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="创建时间">
              <el-date-picker
                v-model="bugForm.createdAt"
                type="datetime"
                placeholder="选择创建时间"
                style="width: 100%"
                format="YYYY-MM-DD HH:mm:ss"
                value-format="YYYY-MM-DD HH:mm:ss"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="截止时间">
              <el-date-picker
                v-model="bugForm.deadline"
                type="date"
                placeholder="选择截止时间"
                style="width: 100%"
                format="YYYY-MM-DD"
                value-format="YYYY-MM-DD"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <div class="form-buttons">
              <el-button type="primary" @click="saveBug">保存</el-button>
              <el-button @click="goBack">返回</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '@/utils/request.js';
import { recordOperationLog } from '@/utils/operationLog.js';

const router = useRouter();

const bugFormRef = ref(null);

const formRules = {
  title: [
    { required: true, message: 'Bug标题不能为空', trigger: 'blur' }
  ],
  severity: [
    { required: true, message: '严重程度必须选择', trigger: 'change' }
  ]
};

const bugForm = ref({
  title: '',
  description: '',
  projectId: '',
  iterationId: '',
  reporterId: '',
  assigneeId: '',
  severity: '',
  status: '0',
  bugType: '',
  createdAt: '',
  deadline: ''
});

const currentUser = ref(null);
const currentUserName = ref('');

const projectOptions = ref([]);
const iterationOptions = ref([]);
const userOptions = ref([]);

const projectLoading = ref(false);
const iterationLoading = ref(false);
const userLoading = ref(false);

const teamMemberIds = ref(new Set());
const userProjectIds = ref(new Set());

const loadCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      currentUser.value = JSON.parse(userStr);
      currentUserName.value = currentUser.value.name || currentUser.value.username;
      bugForm.value.reporterId = currentUser.value.username;
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
  }
};

const getCurrentDateTime = () => {
  const now = new Date();
  const year = now.getFullYear();
  const month = String(now.getMonth() + 1).padStart(2, '0');
  const day = String(now.getDate()).padStart(2, '0');
  const hours = String(now.getHours()).padStart(2, '0');
  const minutes = String(now.getMinutes()).padStart(2, '0');
  const seconds = String(now.getSeconds()).padStart(2, '0');
  return `${year}-${month}-${day} ${hours}:${minutes}:${seconds}`;
};

const loadTeamMembers = async () => {
  if (!currentUser.value) return;
  
  try {
    const projectRes = await request.get(`/dashboard/user-projects?username=${currentUser.value.username}`);
    if (projectRes.data.code === 200 && Array.isArray(projectRes.data.data)) {
      const projectIds = new Set();
      projectRes.data.data.forEach(project => {
        if (project.id) {
          projectIds.add(Number(project.id));
        }
      });
      userProjectIds.value = projectIds;
    }
    
    const userIdForTeam = currentUser.value.username;
    const teamRes = await request.get(`/teams/user-teams/${userIdForTeam}`);
    if (teamRes.data.code === 200 && teamRes.data.data) {
      const memberIds = new Set();
      
      for (const team of teamRes.data.data) {
        try {
          const memberRes = await request.get(`/teams/${team.id}/members`);
          if (memberRes.data.code === 200 && memberRes.data.data) {
            memberRes.data.data.forEach(member => {
              if (member.userId) {
                memberIds.add(Number(member.userId));
              }
            });
          }
        } catch (e) {
          console.error('获取团队成员失败:', e);
        }
      }
      
      teamMemberIds.value = memberIds;
    }
  } catch (error) {
    console.error('加载团队成员失败:', error);
  }
};

const searchProjects = async (query) => {
  if (query) {
    projectLoading.value = true;
    try {
      const res = await request.get('/api/projects');
      if (res.data.code === 200) {
        projectOptions.value = res.data.data
          .filter(item => userProjectIds.value.has(Number(item.id)))
          .filter(item => 
            item.name.toLowerCase().includes(query.toLowerCase())
          )
          .map(item => ({
            id: item.id,
            name: item.name
          }));
      }
    } catch (error) {
      console.error('搜索项目失败:', error);
    } finally {
      projectLoading.value = false;
    }
  } else {
    loadProjects();
  }
};

const onProjectSelect = () => {
  if (bugForm.value.projectId) {
    bugForm.value.iterationId = '';
  }
};

const onProjectFocus = () => {
  if (projectOptions.value.length === 0) {
    loadProjects();
  }
};

const onIterationSelect = () => {
  if (bugForm.value.iterationId) {
    const selectedIteration = iterationOptions.value.find(i => i.id === bugForm.value.iterationId);
    if (selectedIteration && selectedIteration.projectId) {
      bugForm.value.projectId = selectedIteration.projectId;
    }
  }
};

const onIterationFocus = () => {
  if (iterationOptions.value.length === 0) {
    loadIterations();
  }
};

const onUserFocus = () => {
  if (userOptions.value.length === 0) {
    loadUsers();
  }
};

const searchIterations = async (query) => {
  if (query) {
    iterationLoading.value = true;
    try {
      const res = await request.get('/iteration/list');
      if (res.data.code === 200 && Array.isArray(res.data.data)) {
        let iterations = res.data.data;
        
        iterations = iterations.filter(item => userProjectIds.value.has(Number(item.projectId)));
        
        iterationOptions.value = iterations
          .filter(item => 
            item.name && item.name.toLowerCase().includes(query.toLowerCase())
          )
          .map(item => ({
            id: item.id,
            name: item.name,
            projectId: item.projectId
          }));
      }
    } catch (error) {
      console.error('搜索迭代失败:', error);
    } finally {
      iterationLoading.value = false;
    }
  } else {
    loadIterations();
  }
};

const loadIterations = async () => {
  try {
    const res = await request.get('/iteration/list');
    if (res.data.code === 200 && Array.isArray(res.data.data)) {
      let iterations = res.data.data;
      
      iterations = iterations.filter(item => userProjectIds.value.has(Number(item.projectId)));
      
      iterationOptions.value = iterations.map(item => ({
        id: item.id,
        name: item.name,
        projectId: item.projectId
      }));
    }
  } catch (error) {
    console.error('加载迭代失败:', error);
  }
};

const searchUsers = async (query) => {
  if (query) {
    userLoading.value = true;
    try {
      const res = await request.get('/admin/findAll');
      if (res.data.code === 200) {
        let filteredUsers = res.data.data;
        
        if (teamMemberIds.value.size > 0) {
          filteredUsers = filteredUsers.filter(item => 
            teamMemberIds.value.has(Number(item.username))
          );
        }
        
        userOptions.value = filteredUsers
          .map(item => ({
            id: item.username,
            name: item.name || item.username,
            username: item.username
          }))
          .filter(item => 
            (item.name && item.name.toLowerCase().includes(query.toLowerCase())) ||
            (item.username && item.username.toLowerCase().includes(query.toLowerCase()))
          );
      }
    } catch (error) {
      console.error('搜索用户失败:', error);
    } finally {
      userLoading.value = false;
    }
  } else {
    loadUsers();
  }
};

const loadProjects = async () => {
  try {
    const res = await request.get('/api/projects');
    if (res.data.code === 200) {
      projectOptions.value = res.data.data
        .filter(item => userProjectIds.value.has(Number(item.id)))
        .map(item => ({
          id: item.id,
          name: item.name
        }));
    }
  } catch (error) {
    console.error('加载项目失败:', error);
  }
};

const loadUsers = async () => {
  try {
    const res = await request.get('/admin/findAll');
    if (res.data.code === 200) {
      let filteredUsers = res.data.data;
      
      if (teamMemberIds.value.size > 0) {
        filteredUsers = filteredUsers.filter(item => 
          teamMemberIds.value.has(Number(item.username))
        );
      }
      
      userOptions.value = filteredUsers.map(item => ({
        id: item.username,
        name: item.name || item.username,
        username: item.username
      }));
    }
  } catch (error) {
    console.error('加载用户失败:', error);
  }
};

const saveBug = async () => {
  if (!bugFormRef.value) return;
  
  try {
    await bugFormRef.value.validate();
  } catch (error) {
    console.error('表单验证失败:', error);
    return;
  }
  
  try {
    if (!bugForm.value.projectId) {
      ElMessage.error('请选择所属项目');
      return;
    }
    if (!bugForm.value.assigneeId) {
      ElMessage.error('请选择负责人');
      return;
    }
    
    const bugData = {
      title: bugForm.value.title,
      description: bugForm.value.description,
      projectId: bugForm.value.projectId,
      iterationId: bugForm.value.iterationId,
      reporterId: bugForm.value.reporterId,
      assigneeId: bugForm.value.assigneeId,
      severity: bugForm.value.severity,
      status: bugForm.value.status,
      bugType: bugForm.value.bugType,
      createdAt: bugForm.value.createdAt,
      deadline: bugForm.value.deadline
    };
    
    console.log('保存Bug:', bugData);
    
    const response = await request.post('/api/bugs/create', bugData);
    if (response.data.code === 200) {
      console.log('Bug创建成功');
      // 记录操作日志
      await recordOperationLog('创建了', 'Bug', null, bugForm.value.title);
      ElMessage.success('Bug创建成功');
      goBack();
    } else {
      ElMessage.error('创建Bug失败: ' + response.data.message);
    }
  } catch (error) {
    console.error('保存Bug失败:', error);
    ElMessage.error('创建Bug失败');
  }
};

const goBack = () => {
  router.push('/test/bugList');
};

onMounted(async () => {
  loadCurrentUser();
  bugForm.value.createdAt = getCurrentDateTime();
  await loadTeamMembers();
  await loadProjects();
  await loadIterations();
  await loadUsers();
});
</script>

<style scoped>
.bug-submit {
  padding: 10px;
  min-height: 100vh;
  background-color: #fff;
}

.form-container {
  background-color: #fff;
  padding: 20px;
}

h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
}

.form-buttons {
  display: flex;
  margin-top: 20px;
  justify-content: center;
  align-items: center;
}
</style>
