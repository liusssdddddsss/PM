<template>
  <div class="task-add">
    <h3>新增任务</h3>
    <div class="form-container">
      <el-form :model="taskForm" label-width="120px" class="add-task-form">
        <div class="form-row">
          <el-form-item label="所属项目">
            <el-select
              v-model="taskForm.project"
              filterable
              remote
              reserve-keyword
              placeholder="请选择项目（与迭代二选一）"
              :remote-method="searchProjects"
              :loading="projectLoading"
              @change="onProjectSelect"
            >
              <el-option
                v-for="item in projectOptions"
                :key="item.id"
                :label="item.name"
                :value="item.id"
              />
            </el-select>
          </el-form-item>
          
          <el-form-item label="所属迭代">
            <el-select
              v-model="taskForm.iteration"
              filterable
              remote
              reserve-keyword
              placeholder="请选择迭代（与项目二选一）"
              :remote-method="searchIterations"
              :loading="iterationLoading"
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
        </div>
        
        <div class="form-row">
          <el-form-item label="任务名称">
            <el-input v-model="taskForm.name" placeholder="请输入" />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="负责人">
            <el-select
              v-model="taskForm.assignee"
              filterable
              remote
              reserve-keyword
              placeholder="请输入负责人姓名"
              :remote-method="searchUsers"
              :loading="loading"
            >
              <el-option
                v-for="item in assigneeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务状态">
            <el-select v-model="taskForm.status" placeholder="请选择">
              <el-option label="待开始" value="1" />
              <el-option label="进行中" value="2" />
              <el-option label="已完成" value="3" />
              <el-option label="已关闭" value="4" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="优先级">
            <el-select v-model="taskForm.priority" placeholder="请选择">
              <el-option label="紧急" value="1" />
              <el-option label="一般" value="2" />
              <el-option label="正常" value="3" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="预计开始日期">
            <el-date-picker
              v-model="taskForm.startDate"
              type="date"
              placeholder="请选"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="预计完成日期">
            <el-date-picker
              v-model="taskForm.endDate"
              type="date"
              placeholder="请选"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务描述">
            <el-input
              v-model="taskForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入任务描述"
            />
          </el-form-item>
        </div>
        
        <div class="form-buttons">
          <el-button type="primary" @click="saveTask">保存</el-button>
          <el-button @click="goBack">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import { recordOperationLog } from '@/utils/operationLog.js';
import request from "@/utils/request.js";

const router = useRouter();

// 任务表单数据
const taskForm = ref({
  project: '',
  iteration: '',
  name: '',
  assignee: '',
  status: '1', // 默认待开始
  priority: '2', // 默认一般
  startDate: '',
  endDate: '',
  description: ''
});

// 当前用户信息
const currentUser = ref(null);

// 负责人选项
const assigneeOptions = ref([]);
// 项目选项
const projectOptions = ref([]);
// 迭代选项
const iterationOptions = ref([]);

// 加载状态
const loading = ref(false);
const projectLoading = ref(false);
const iterationLoading = ref(false);

// 团队成员ID集合
const teamMemberIds = ref(new Set());
// 用户参与的项目ID集合
const userProjectIds = ref(new Set());

// 获取当前用户信息
const loadCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      currentUser.value = JSON.parse(userStr);
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
  }
};

// 获取用户所在团队的成员和参与的项目
const loadTeamMembers = async () => {
  if (!currentUser.value) return;
  
  try {
    // 获取用户参与的项目
    const projectRes = await request.get(`/dashboard/user-projects?username=${currentUser.value.username}`);
    console.log('用户项目返回:', projectRes);
    if (projectRes.data.code === 200 && Array.isArray(projectRes.data.data)) {
      const projectIds = new Set();
      projectRes.data.data.forEach(project => {
        if (project.id) {
          projectIds.add(Number(project.id));
        }
      });
      userProjectIds.value = projectIds;
      console.log('用户参与的项目ID:', userProjectIds.value);
    }
    
    // 获取用户所在的团队
    const userIdForTeam = currentUser.value.username;
    const teamRes = await request.get(`/teams/user-teams/${userIdForTeam}`);
    console.log('用户团队返回:', teamRes);
    if (teamRes.data.code === 200 && teamRes.data.data) {
      const memberIds = new Set();
      
      for (const team of teamRes.data.data) {
        try {
          const memberRes = await request.get(`/teams/${team.id}/members`);
          console.log('团队成员返回:', memberRes);
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
      console.log('团队成员ID:', teamMemberIds.value);
    }
  } catch (error) {
    console.error('加载团队成员失败:', error);
  }
};

// 加载用户列表
const loadUsers = async () => {
  try {
    const res = await request.get('/admin/findAll');
    console.log('加载用户返回:', res);
    if (res.data.code === 200) {
      let filteredUsers = res.data.data;
      
      if (teamMemberIds.value.size > 0) {
        filteredUsers = filteredUsers.filter(item => 
          teamMemberIds.value.has(Number(item.username))
        );
      }
      
      assigneeOptions.value = filteredUsers.map(item => ({
        value: item.username,
        label: item.name || item.username,
        username: item.username
      }));
      console.log('加载用户选项:', assigneeOptions.value);
    }
  } catch (error) {
    console.error('加载用户失败:', error);
  }
};

// 搜索负责人（优先显示同团队成员）
const searchUsers = async (query) => {
  if (query) {
    loading.value = true;
    try {
      const res = await request.get('/admin/findAll');
      console.log('搜索用户返回:', res);
      if (res.data.code === 200) {
        let filteredUsers = res.data.data;
        
        // 如果团队成员集合不为空，则只显示同团队成员
        if (teamMemberIds.value.size > 0) {
          filteredUsers = filteredUsers.filter(item => 
            teamMemberIds.value.has(Number(item.username))
          );
        }
        
        assigneeOptions.value = filteredUsers
          .map(item => ({
            value: item.username,
            label: item.name || item.username,
            username: item.username
          }))
          .filter(item => 
            (item.label && item.label.toLowerCase().includes(query.toLowerCase())) ||
            (item.username && item.username.toLowerCase().includes(query.toLowerCase()))
          );
        console.log('负责人选项:', assigneeOptions.value);
      }
    } catch (error) {
      console.error('搜索用户失败:', error);
    } finally {
      loading.value = false;
    }
  } else {
    assigneeOptions.value = [];
  }
};

// 搜索项目（只搜索用户参与的项目）
const searchProjects = async (query) => {
  if (query) {
    projectLoading.value = true;
    try {
      const res = await request.get('/api/projects');
      console.log('搜索项目返回:', res);
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
        console.log('项目选项:', projectOptions.value);
      }
    } catch (error) {
      console.error('搜索项目失败:', error);
    } finally {
      projectLoading.value = false;
    }
  } else {
    projectOptions.value = [];
  }
};

// 搜索迭代（只显示用户参与的项目的迭代）
const searchIterations = async (query) => {
  if (query) {
    iterationLoading.value = true;
    try {
      const res = await request.get('/iteration/list');
      console.log('搜索迭代返回:', res);
      if (res.data.code === 200 && Array.isArray(res.data.data)) {
        let iterations = res.data.data;
        
        // 只显示用户参与的项目的迭代
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
        console.log('迭代选项:', iterationOptions.value);
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

// 加载迭代列表
const loadIterations = async () => {
  try {
    const res = await request.get('/iteration/list');
    console.log('加载迭代返回:', res);
    if (res.data.code === 200 && Array.isArray(res.data.data)) {
      let iterations = res.data.data;
      
      iterations = iterations.filter(item => userProjectIds.value.has(Number(item.projectId)));
      
      iterationOptions.value = iterations.map(item => ({
        id: item.id,
        name: item.name,
        projectId: item.projectId
      }));
      console.log('迭代选项:', iterationOptions.value);
    }
  } catch (error) {
    console.error('加载迭代失败:', error);
  }
};

// 选择项目时清空迭代（二选一）
const onProjectSelect = () => {
  if (taskForm.value.project) {
    taskForm.value.iteration = '';
  }
};

// 选择迭代时关联项目（二选一）
const onIterationSelect = () => {
  if (taskForm.value.iteration) {
    // 选中迭代后，获取迭代对应的项目信息用于保存
    const selectedIteration = iterationOptions.value.find(i => i.id === taskForm.value.iteration);
    if (selectedIteration && selectedIteration.projectId) {
      taskForm.value.project = selectedIteration.projectId;
    }
  }
};

// 保存任务
const saveTask = async () => {
  try {
    console.log('保存任务:', taskForm.value);
    
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      console.error('用户未登录');
      return;
    }
    const user = JSON.parse(userStr);
    
    // 构建任务数据
    const taskData = {
      title: taskForm.value.name,
      description: taskForm.value.description,
      projectId: taskForm.value.project ? parseInt(taskForm.value.project) : null,
      assigneeId: taskForm.value.assignee ? parseInt(taskForm.value.assignee) : null,
      creatorId: parseInt(user.username),
      startDate: taskForm.value.startDate,
      dueDate: taskForm.value.endDate,
      status: parseInt(taskForm.value.status), // 使用表单中的状态值
      priority: parseInt(taskForm.value.priority), // 使用表单中的优先级值
      progress: 0 // 默认为0%
    };
    
    console.log('发送任务数据:', taskData);
    
    // 调用后端 API 创建任务
    const response = await request.post('/workbench/tasks', taskData);
    console.log('创建任务响应:', response);
    
    if (response.data.code === 200) {
      console.log('创建任务成功');
      // 记录操作日志
      await recordOperationLog('创建了', '任务', null, taskForm.value.name);
      // 保存成功后跳转回任务列表
      router.push('/task/taskList');
    } else {
      console.error('创建任务失败:', response.data.msg);
    }
  } catch (error) {
    console.error('保存任务失败:', error);
  }
};

// 返回上一页
const goBack = () => {
  router.push('/task/taskList');
};

onMounted(async () => {
  loadCurrentUser();
  await loadTeamMembers();
});
</script>

<style scoped>
.task-add {
  padding: 10px;
  background-color: #fff;
  min-height: 100vh;
}

.form-container {
  background-color: #fff;
  padding: 20px;
}

h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.add-task-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.form-row .el-form-item {
  flex: 1;
  margin-bottom: 0;
}

.task-type-buttons {
  display: flex;
  gap: 10px;
}

.access-control {
  margin-top: 4px;
}

.mt-2 {
  margin-top: 8px;
}

.form-buttons {
  display: flex;
  margin-top: 20px;
  gap: 12px;
  justify-content: center;
}
</style>