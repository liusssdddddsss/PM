<template>
  <div class="task-create">
    <h3>新建任务</h3>
    <div class="form-container">
      <div class="form-content">
        <div class="left-section">
          <el-form :model="taskForm" label-width="120px">
            <el-form-item label="任务名称">
              <el-input v-model="taskForm.name" placeholder="请输入任务名称" />
            </el-form-item>
            
            <el-form-item label="任务描述">
              <el-input
                v-model="taskForm.description"
                type="textarea"
                :rows="6"
                placeholder="请输入任务描述"
              />
            </el-form-item>
            
            <div class="form-buttons">
              <el-button type="primary" @click="submitForm">保存</el-button>
              <el-button @click="goBack">返回</el-button>
            </div>
          </el-form>
        </div>
        
        <div class="right-section">
          <h3>基本信息</h3>
          <el-form :model="taskForm" label-width="120px">
            <el-form-item label="所属项目">
              <el-select
                v-model="taskForm.project"
                placeholder="请选择项目（与迭代二选一）"
                filterable
                remote
                reserve-keyword
                :remote-method="searchProjects"
                :loading="projectLoading"
                style="width: 100%"
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
                placeholder="请选择迭代（与项目二选一）"
                filterable
                remote
                reserve-keyword
                :remote-method="searchIterations"
                :loading="iterationLoading"
                style="width: 100%"
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
            
            <el-form-item label="任务类型">
              <el-select v-model="taskForm.type" placeholder="请选择" style="width: 100%">
                <el-option label="需求" value="需求" />
                <el-option label="开发" value="开发" />
                <el-option label="测试" value="测试" />
                <el-option label="文档" value="文档" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="父任务">
              <el-select v-model="taskForm.parentTask" placeholder="请选择" style="width: 100%">
                <el-option label="无" value="" />
                <el-option label="任务1" value="task1" />
                <el-option label="任务2" value="task2" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="指派给">
              <el-select
                v-model="taskForm.assignee"
                placeholder="请选择负责人"
                filterable
                remote
                reserve-keyword
                :remote-method="searchUsers"
                :loading="userLoading"
                style="width: 100%"
              >
                <el-option
                  v-for="item in assigneeOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
            
            <el-form-item label="优先级">
              <el-select v-model="taskForm.priority" placeholder="请选择" style="width: 100%">
                <el-option label="紧急" value="紧急" />
                <el-option label="一般" value="一般" />
                <el-option label="正常" value="正常" />
              </el-select>
            </el-form-item>
            
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
            
            <el-form-item label="预计完成时长">
              <el-input v-model="taskForm.estimatedHours" placeholder="请输入" />
            </el-form-item>
            
            <el-form-item label="负责人">
              <el-select
                v-model="taskForm.leader"
                placeholder="请选择负责人"
                filterable
                remote
                reserve-keyword
                :remote-method="searchUsers"
                :loading="userLoading"
                style="width: 100%"
              >
                <el-option
                  v-for="item in assigneeOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request.js';

const router = useRouter();

// 任务表单数据
const taskForm = ref({
  name: '',
  description: '',
  project: '',
  iteration: '',
  type: '',
  parentTask: '',
  assignee: '',
  priority: '',
  startDate: '',
  endDate: '',
  estimatedHours: '',
  leader: ''
});

// 选项列表
const projectOptions = ref([]);
const iterationOptions = ref([]);
const assigneeOptions = ref([]);

// 加载状态
const projectLoading = ref(false);
const iterationLoading = ref(false);
const userLoading = ref(false);

// 团队成员ID集合
const teamMemberIds = ref(new Set());
// 用户参与的项目ID集合
const userProjectIds = ref(new Set());

// 获取当前用户所在团队的成员和参与的项目
const loadTeamMembers = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (!userStr) return;
    
    const user = JSON.parse(userStr);
    
    // 获取用户参与的项目
    const projectRes = await request.get(`/dashboard/user-projects?username=${user.username}`);
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
    const userIdForTeam = user.username;
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
      console.log('团队成员ID:', teamMemberIds.value);
    }
  } catch (error) {
    console.error('加载团队成员失败:', error);
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
    const selectedIteration = iterationOptions.value.find(i => i.id === taskForm.value.iteration);
    if (selectedIteration && selectedIteration.projectId) {
      taskForm.value.project = selectedIteration.projectId;
    }
  }
};

// 搜索项目（只搜索用户参与的项目）
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

// 搜索迭代（只显示用户参与的项目的迭代）
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

// 搜索用户（优先显示同团队成员）
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
        
        assigneeOptions.value = filteredUsers
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

// 加载项目
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

// 加载迭代
const loadIterations = async () => {
  try {
    const res = await request.get('/api/iterations');
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

// 加载用户
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
      
      assigneeOptions.value = filteredUsers.map(item => ({
        id: item.username,
        name: item.name || item.username,
        username: item.username
      }));
    }
  } catch (error) {
    console.error('加载用户失败:', error);
  }
};

// 提交表单
const submitForm = async () => {
  try {
    console.log('提交任务:', taskForm.value);
    
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      console.error('用户未登录');
      return;
    }
    const user = JSON.parse(userStr);
    
    const taskData = {
      title: taskForm.value.name,
      description: taskForm.value.description,
      projectId: taskForm.value.project,
      iterationId: taskForm.value.iteration,
      type: taskForm.value.type,
      parentTaskId: taskForm.value.parentTask,
      assigneeId: taskForm.value.assignee,
      creatorId: parseInt(user.username),
      priority: taskForm.value.priority === '紧急' ? 1 : taskForm.value.priority === '一般' ? 2 : 3,
      startDate: taskForm.value.startDate,
      dueDate: taskForm.value.endDate,
      estimatedHours: taskForm.value.estimatedHours,
      leaderId: taskForm.value.leader,
      status: 1,
      progress: 0
    };
    
    console.log('发送任务数据:', taskData);
    
    const response = await request.post('/workbench/tasks', taskData);
    console.log('创建任务响应:', response);
    
    if (response.data.code === 200) {
      console.log('创建任务成功');
      alert('创建任务成功');
      router.push('/task/meJoinTasks');
    } else {
      console.error('创建任务失败:', response.data.msg);
      alert('创建任务失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('提交任务失败:', error);
    alert('创建任务失败');
  }
};

// 返回上一页
const goBack = () => {
  router.push('/task/meJoinTasks');
};

onMounted(async () => {
  await loadTeamMembers();
  await loadProjects();
  await loadIterations();
  await loadUsers();
});
</script>

<style scoped>
.task-create {
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

.form-content {
  display: flex;
  gap: 40px;
}

.left-section {
  flex: 1;
  border-right: 1px solid #e4e7ed;
  padding-right: 40px;
}

.right-section {
  flex: 1;
  padding-left: 20px;
}

.form-buttons {
  display: flex;
  margin-top: 20px;
  align-content: center;
  justify-content: center;
}

.upload-demo {
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  padding: 20px;
  text-align: center;
}

.el-upload__text {
  color: #409eff;
  margin-top: 10px;
}

.el-upload__tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}
</style>