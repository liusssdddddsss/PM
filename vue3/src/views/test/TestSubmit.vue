<template>
  <div class="test-submit">
    <h3>创建测试</h3>
    <div class="form-container">
      <el-form :model="testForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属项目">
              <el-select
                v-model="testForm.project"
                placeholder="请选择项目（与迭代二选一）"
                filterable
                remote
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
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属迭代">
              <el-select
                v-model="testForm.iteration"
                placeholder="请选择迭代（与项目二选一）"
                filterable
                remote
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
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="测试负责人">
              <el-select
                v-model="testForm.testLeader"
                placeholder="请选择测试负责人"
                filterable
                remote
                :remote-method="searchUsers"
                :loading="userLoading"
                style="width: 100%"
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
          <el-col :span="12">
            <el-form-item label="测试类型">
              <el-select v-model="testForm.testType" placeholder="请选择" style="width: 100%">
                <el-option label="功能测试" value="functional" />
                <el-option label="性能测试" value="performance" />
                <el-option label="安全测试" value="security" />
                <el-option label="兼容性测试" value="compatibility" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="优先级">
              <el-select v-model="testForm.priority" placeholder="请选择" style="width: 100%">
                <el-option label="紧急" value="urgent" />
                <el-option label="正常" value="normal" />
                <el-option label="一般" value="low" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="预计开始日期">
              <el-date-picker
                  v-model="testForm.startDate"
                  type="date"
                  placeholder="请选择"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计完成日期">
              <el-date-picker
                  v-model="testForm.endDate"
                  type="date"
                  placeholder="请选择"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="测试名称">
              <el-input
                  v-model="testForm.name"
                  placeholder="请输入测试名称"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <div class="form-buttons">
              <el-button type="primary" @click="saveTest">保存</el-button>
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
import request from '@/utils/request.js';
import { recordOperationLog } from '@/utils/operationLog.js';

const router = useRouter();

// 测试表单数据
const testForm = ref({
  project: '',
  iteration: '',
  testType: '',
  priority: '',
  startDate: '',
  endDate: '',
  testLeader: '',
  name: ''
});

// 选项列表
const projectOptions = ref([]);
const iterationOptions = ref([]);
const userOptions = ref([]);

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
    console.log('当前用户信息:', user);
    
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
    
    // 获取用户所在的团队 - 使用username作为userId（与后端保持一致）
    // 直接使用username字符串，后端存储的是字符串格式
    const userIdForTeam = user.username;
    
    console.log('用于查询团队的用户ID:', userIdForTeam);
    
    const teamRes = await request.get(`/teams/user-teams/${userIdForTeam}`);
    if (teamRes.data.code === 200 && teamRes.data.data) {
      const memberIds = new Set();
      
      for (const team of teamRes.data.data) {
        try {
          // 获取团队成员
          const memberRes = await request.get(`/teams/${team.id}/members`);
          if (memberRes.data.code === 200 && memberRes.data.data) {
            memberRes.data.data.forEach(member => {
              if (member.userId) {
                // 团队成员的userId可能是字符串格式，统一转成数字
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
  if (testForm.value.project) {
    testForm.value.iteration = '';
  }
};

// 选择迭代时清空项目（二选一）
const onIterationSelect = () => {
  if (testForm.value.iteration) {
    testForm.value.project = '';
    // 选中迭代后，获取迭代对应的项目信息用于保存
    const selectedIteration = iterationOptions.value.find(i => i.id === testForm.value.iteration);
    if (selectedIteration && selectedIteration.projectId) {
      testForm.value.project = selectedIteration.projectId;
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
          .filter(item => userProjectIds.value.has(Number(item.id))) // 只显示用户参与的项目
          .filter(item => 
            item.name.toLowerCase().includes(query.toLowerCase())
          )
          .map(item => ({
            id: item.id,
            name: item.name,
            product_id: item.product_id // 保存产品ID
          }));
      }
    } catch (error) {
      console.error('搜索项目失败:', error);
    } finally {
      projectLoading.value = false;
    }
  } else {
    // 如果没有搜索词，加载用户参与的项目
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
        
        // 只显示用户参与的项目的迭代
        iterations = iterations.filter(item => userProjectIds.value.has(Number(item.projectId)));
        
        iterationOptions.value = iterations
          .filter(item => 
            item.name && item.name.toLowerCase().includes(query.toLowerCase())
          )
          .map(item => ({
            id: item.id,
            name: item.name,
            projectId: item.projectId // 保存项目ID用于二选一时关联
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

// 加载迭代（只显示用户参与的项目的迭代）
const loadIterations = async () => {
  try {
    const res = await request.get('/api/iterations');
    if (res.data.code === 200 && Array.isArray(res.data.data)) {
      let iterations = res.data.data;
      
      // 只显示用户参与的项目的迭代
      iterations = iterations.filter(item => userProjectIds.value.has(Number(item.projectId)));
      
      iterationOptions.value = iterations.map(item => ({
        id: item.id,
        name: item.name,
        projectId: item.projectId // 保存项目ID用于二选一时关联
      }));
    }
  } catch (error) {
    console.error('加载迭代失败:', error);
  }
};

// 搜索用户（优先显示同团队成员，如果没有团队成员则显示所有用户）
const searchUsers = async (query) => {
  if (query) {
    userLoading.value = true;
    try {
      const res = await request.get('/admin/findAll');
      if (res.data.code === 200) {
        let filteredUsers = res.data.data;
        
        // 如果团队成员集合不为空，则只显示同团队成员；否则显示所有用户
        // 注意：团队成员表中存储的是username（字符串转数字），所以要用username来匹配
        if (teamMemberIds.value.size > 0) {
          filteredUsers = filteredUsers.filter(item => 
            teamMemberIds.value.has(Number(item.username))
          );
        }
        
        userOptions.value = filteredUsers
          .map(item => ({
            id: item.username, // 使用username作为id，因为团队成员表中存储的是username
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
    // 如果没有搜索词，加载用户
    loadUsers();
  }
};

// 加载用户参与的项目
const loadProjects = async () => {
  try {
    const res = await request.get('/api/projects');
    if (res.data.code === 200) {
      projectOptions.value = res.data.data
        .filter(item => userProjectIds.value.has(Number(item.id))) // 只显示用户参与的项目
        .map(item => ({
          id: item.id,
          name: item.name,
          product_id: item.product_id // 保存产品ID
        }));
    }
  } catch (error) {
    console.error('加载项目失败:', error);
  }
};

// 加载用户（优先显示同团队成员，如果没有团队成员则显示所有用户）
const loadUsers = async () => {
  try {
    const res = await request.get('/admin/findAll');
    if (res.data.code === 200) {
      let filteredUsers = res.data.data;
      
      // 如果团队成员集合不为空，则只显示同团队成员；否则显示所有用户
      // 注意：团队成员表中存储的是username（字符串转数字），所以要用username来匹配
      if (teamMemberIds.value.size > 0) {
        filteredUsers = filteredUsers.filter(item => 
          teamMemberIds.value.has(Number(item.username))
        );
      }
      
      userOptions.value = filteredUsers.map(item => ({
        id: item.username, // 使用username作为id，因为团队成员表中存储的是username
        name: item.name || item.username,
        username: item.username
      }));
      
      console.log('加载用户数量:', userOptions.value.length);
      console.log('团队成员数量:', teamMemberIds.value.size);
    }
  } catch (error) {
    console.error('加载用户失败:', error);
  }
};

// 保存测试
const saveTest = async () => {
  try {
    // 验证必填字段
    if (!testForm.value.name) {
      alert('请输入测试名称');
      return;
    }
    if (!testForm.value.project) {
      alert('请选择所属项目');
      return;
    }
    if (!testForm.value.testLeader) {
      alert('请选择测试负责人');
      return;
    }
    
    // 根据选中的项目获取产品ID
    const selectedProject = projectOptions.value.find(p => p.id === testForm.value.project);
    const productId = selectedProject ? selectedProject.product_id : null;
    
    const response = await request.post('/dashboard/test-cases', {
      name: testForm.value.name,
      project: testForm.value.project,
      iteration: testForm.value.iteration,
      productId: productId, // 传入选中的产品ID
      testLeader: testForm.value.testLeader,
      testType: testForm.value.testType,
      priority: testForm.value.priority,
      startDate: testForm.value.startDate,
      endDate: testForm.value.endDate
    });
    
    console.log('保存测试响应:', response);
    
    if (response.data.code === 200) {
      // 记录操作日志
      await recordOperationLog('创建了', '测试', null, testForm.value.name);
      alert('创建测试成功');
      goBack();
    } else {
      alert('创建测试失败: ' + response.data.message);
    }
  } catch (error) {
    console.error('保存测试失败:', error);
    alert('创建测试失败');
  }
};

// 返回上一页
const goBack = () => {
  router.push('/test/testList');
};

onMounted(async () => {
  await loadTeamMembers(); // 先加载团队成员
  await loadProjects();
  await loadIterations();
  await loadUsers();
});
</script>

<style scoped>
.test-submit {
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
