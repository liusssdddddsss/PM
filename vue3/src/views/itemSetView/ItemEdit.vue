<template>
  <div class="edit">
    <h3>编辑项目</h3>
    <el-divider/>
    <div class="form-container">
      <el-form :model="projectForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称">
              <el-input v-model="projectForm.name" placeholder="请输入项目名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属产品">
              <el-select
                v-model="projectForm.product"
                filterable
                remote
                :remote-method="remoteSearch"
                :loading="loading"
                placeholder="请选择所属产品"
              >
                <el-option
                  v-for="product in productList"
                  :key="product.id"
                  :label="product.name"
                  :value="product.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划开始日期">
              <el-date-picker
                  v-model="projectForm.startDate"
                  type="date"
                  placeholder="请选择"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划完成日期">
              <el-date-picker
                  v-model="projectForm.endDate"
                  type="date"
                  placeholder="请选择"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select 
                v-model="projectForm.leader" 
                filterable
                remote
                :remote-method="searchUsers"
                :loading="loadingLeader"
                placeholder="请选择负责人"
              >
                <el-option 
                  v-for="user in leaderOptions" 
                  :key="user.id" 
                  :label="user.name || user.username" 
                  :value="user.id" 
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属团队">
              <el-select
                v-model="projectForm.team"
                filterable
                remote
                :remote-method="searchTeams"
                :loading="loadingTeam"
                placeholder="请选择所属团队"
              >
                <el-option
                  v-for="team in teamOptions"
                  :key="team.teamId"
                  :label="team.teamName"
                  :value="team.teamId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="项目描述">
          <el-input
              v-model="projectForm.description"
              type="textarea"
              placeholder="请输入项目描述"
              :rows="4"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="24">
            <div class="form-buttons">
              <el-button @click="goBack">返回</el-button>
              <el-button type="primary" @click="saveProject">保存</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from '@/utils/request.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();

// 项目表单数据
const projectForm = ref({
  name: '',
  product: '',
  startDate: '',
  endDate: '',
  leader: '',
  team: '',
  description: '',
  progress: 0,
  status: 0
});

// 产品列表
const productList = ref([]);
const loading = ref(false);

// 负责人选项
const leaderOptions = ref([]);
const loadingLeader = ref(false);

// 团队选项
const teamOptions = ref([]);
const loadingTeam = ref(false);

// 远程搜索产品
const remoteSearch = async (query) => {
  if (query !== '') {
    loading.value = true;
    try {
      const response = await request.get('/api/products');
      if (response.data.code === 200) {
        const newProducts = response.data.data
          .filter(item => item.name && item.name.toLowerCase().includes(query.toLowerCase()))
          .map(item => ({
            id: Number(item.id),
            name: item.name
          }));
        // 合并数据，避免覆盖已存在的选项
        newProducts.forEach(product => {
          const exists = productList.value.some(p => p.id === product.id);
          if (!exists) {
            productList.value.push(product);
          }
        });
      }
    } catch (error) {
      console.error('搜索产品失败:', error);
    }
    loading.value = false;
  } else {
    loadProducts();
  }
};

// 加载产品列表
const loadProducts = async () => {
  try {
    const response = await request.get('/api/products');
    if (response.data.code === 200) {
      const newProducts = response.data.data.map(item => ({
        id: Number(item.id),
        name: item.name
      }));
      // 合并数据，避免覆盖已存在的选项
      newProducts.forEach(product => {
        const exists = productList.value.some(p => p.id === product.id);
        if (!exists) {
          productList.value.push(product);
        }
      });
    }
  } catch (error) {
    console.error('加载产品列表失败:', error);
  }
};

// 搜索用户
const searchUsers = async (query) => {
  loadingLeader.value = true;
  try {
    const response = await request.get(`/admin/search-users?search=${query}`);
    if (response.data.code === 200) {
      leaderOptions.value = response.data.data.map(item => ({
        username: item.username,
        name: item.name,
        id: item.id
      }));
    }
  } catch (error) {
    console.error('搜索用户失败:', error);
  }
  loadingLeader.value = false;
};

// 搜索团队
const searchTeams = async (query) => {
  loadingTeam.value = true;
  try {
    const response = await request.get('/admin/teams');
    if (response.data.code === 200) {
      teamOptions.value = response.data.data
        .filter(item => item.teamName && item.teamName.toLowerCase().includes((query || '').toLowerCase()))
        .map(item => ({
          teamId: item.teamId,
          teamName: item.teamName
        }));
    }
  } catch (error) {
    console.error('搜索团队失败:', error);
  }
  loadingTeam.value = false;
};

// 获取项目详情
const fetchProjectDetail = async () => {
  const projectId = route.query.id;
  if (!projectId) {
    ElMessage.error('缺少项目ID');
    return;
  }
  
  try {
    const response = await request.get(`/workbench/projects/${projectId}`);
    if (response.data.code === 200) {
      const project = response.data.data;
      console.log('项目详情:', project);
      
      const productId = project.product_id;
      const managerId = project.manager_id;
      const teamId = project.team_id;
      
      // 1. 先获取当前项目关联的数据，添加到选项列表
      if (productId) {
        try {
          const productRes = await request.get(`/api/products/${productId}/detail`);
          if (productRes.data.code === 200) {
            const product = productRes.data.data;
            const pid = Number(product.id);
            const isProductExist = productList.value.some(p => Number(p.id) === pid);
            if (!isProductExist) {
              productList.value.push({
                id: pid,
                name: product.name || `产品${productId}`
              });
            }
          }
        } catch (e) {
          console.error('获取产品信息失败:', e);
        }
      }
      
      if (managerId) {
        const userId = Number(managerId);
        const isUserExist = leaderOptions.value.some(u => Number(u.id) === userId);
        if (!isUserExist) {
          try {
            const userRes = await request.get(`/admin/search-users?search=${userId}`);
            if (userRes.data.code === 200 && userRes.data.data.length > 0) {
              const user = userRes.data.data[0];
              leaderOptions.value.push({
                id: Number(user.id),
                username: user.username,
                name: user.name
              });
            }
          } catch (e) {
            console.error('获取用户信息失败:', e);
          }
        }
      }
      
      if (teamId) {
        const tid = Number(teamId);
        const isTeamExist = teamOptions.value.some(t => Number(t.teamId) === tid);
        if (!isTeamExist) {
          try {
            const teamRes = await request.get('/admin/teams');
            if (teamRes.data.code === 200) {
              const teams = teamRes.data.data;
              const targetTeam = teams.find(t => Number(t.teamId) === tid);
              if (targetTeam) {
                teamOptions.value.push({
                  teamId: Number(targetTeam.teamId),
                  teamName: targetTeam.teamName
                });
              }
            }
          } catch (e) {
            console.error('获取团队信息失败:', e);
          }
        }
      }
      
      // 2. 然后加载完整的选项列表
      await loadProducts();
      await loadAllUsers();
      await loadAllTeams();
      
      // 3. 最后设置表单值（使用nextTick确保DOM更新）
      await new Promise(resolve => setTimeout(resolve, 100));
      
      projectForm.value = {
        name: project.name || project.title || '',
        product: productId ? Number(productId) : '',
        startDate: project.startTime ? new Date(project.startTime) : '',
        endDate: project.finishTime ? new Date(project.finishTime) : '',
        leader: managerId !== null && managerId !== undefined ? Number(managerId) : '',
        team: teamId !== null && teamId !== undefined ? Number(teamId) : '',
        description: project.description !== null && project.description !== undefined ? project.description : '',
        progress: project.progress !== null && project.progress !== undefined ? project.progress : 0,
        status: project.status !== null && project.status !== undefined ? project.status : 0
      };
      
      console.log('表单数据:', projectForm.value);
      console.log('产品列表:', productList.value);
      console.log('用户列表:', leaderOptions.value);
      console.log('团队列表:', teamOptions.value);
    }
  } catch (error) {
    console.error('获取项目详情失败:', error);
    ElMessage.error('获取项目详情失败');
  }
};

// 加载所有用户
const loadAllUsers = async () => {
  try {
    const response = await request.get('/admin/findAll');
    if (response.data.code === 200) {
      const newUsers = response.data.data.map(item => ({
        username: item.username,
        name: item.name,
        id: Number(item.id)
      }));
      // 合并数据，避免覆盖已存在的选项
      newUsers.forEach(user => {
        const exists = leaderOptions.value.some(u => u.id === user.id);
        if (!exists) {
          leaderOptions.value.push(user);
        }
      });
    }
  } catch (error) {
    console.error('加载用户列表失败:', error);
  }
};

// 加载所有团队
const loadAllTeams = async () => {
  try {
    const response = await request.get('/admin/teams');
    if (response.data.code === 200) {
      const newTeams = response.data.data.map(item => ({
        teamId: Number(item.teamId),
        teamName: item.teamName
      }));
      // 合并数据，避免覆盖已存在的选项
      newTeams.forEach(team => {
        const exists = teamOptions.value.some(t => t.teamId === team.teamId);
        if (!exists) {
          teamOptions.value.push(team);
        }
      });
    }
  } catch (error) {
    console.error('加载团队列表失败:', error);
  }
};

// 保存项目
const saveProject = async () => {
  const projectId = route.query.id;
  if (!projectId) {
    ElMessage.error('缺少项目ID');
    return;
  }
  
  if (!projectForm.value.name) {
    ElMessage.error('请输入项目名称');
    return;
  }
  
  try {
    // 格式化日期为字符串
    const formatDate = (date) => {
      if (!date) return null;
      if (date instanceof Date) {
        const year = date.getFullYear();
        const month = String(date.getMonth() + 1).padStart(2, '0');
        const day = String(date.getDate()).padStart(2, '0');
        return `${year}-${month}-${day}`;
      }
      return date;
    };
    
    const saveData = {
      name: projectForm.value.name,
      product_id: projectForm.value.product ? Number(projectForm.value.product) : null,
      start_date: formatDate(projectForm.value.startDate),
      end_date: formatDate(projectForm.value.endDate),
      manager_id: projectForm.value.leader ? Number(projectForm.value.leader) : null,
      team_id: projectForm.value.team ? Number(projectForm.value.team) : null,
      description: projectForm.value.description,
      progress: projectForm.value.progress,
      status: projectForm.value.status
    };
    
    console.log('保存数据:', saveData);
    
    const response = await request.put(`/workbench/projects/${projectId}`, saveData);
    if (response.data.code === 200) {
      ElMessage.success('项目保存成功');
      goBack();
    } else {
      ElMessage.error('保存失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('保存项目失败:', error);
    ElMessage.error('保存失败: ' + (error.response?.data?.msg || error.message || '未知错误'));
  }
};

// 返回上一页
const goBack = () => {
  router.push('/itemSet/itemList');
};

onMounted(async () => {
  await loadProducts();
  await searchUsers('');
  await searchTeams('');
  await fetchProjectDetail();
});
</script>

<style scoped>
.edit {
  background-color: #fff;
  padding: 20px;
  min-height: 100vh;
}
.el-divider{
  margin-top: 10px;
}

.form-container {
  background-color: #fff;
  padding: 20px;
}

.form-buttons {
  display: flex;
  margin-top: 20px;
  justify-content: center;
  align-items: center;
  gap: 20px;
}
.el-form-item {
  margin-bottom: 20px;
}

.el-radio,
.el-checkbox {
  display: block;
  margin-bottom: 10px;
}
</style>