<template>
  <div class="add-project">
    <h3>添加项目</h3>
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
            <el-form-item label="负责人">
              <el-select
                v-model="projectForm.managerId"
                placeholder="请选择"
                filterable
                remote
                :remote-method="searchUsers"
                :loading="loadingUsers"
              >
                <el-option
                  v-for="user in userOptions"
                  :key="user.id"
                  :label="user.name"
                  :value="user.username"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属团队">
              <el-select
                v-model="projectForm.teamId"
                placeholder="请选择"
                filterable
                remote
                :remote-method="searchTeams"
                :loading="loadingTeams"
              >
                <el-option
                  v-for="team in teamOptions"
                  :key="team.id"
                  :label="team.name"
                  :value="team.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属产品">
              <el-select
                v-model="projectForm.productId"
                placeholder="请选择"
                filterable
                remote
                :remote-method="searchProducts"
                :loading="loadingProducts"
              >
                <el-option
                  v-for="product in productOptions"
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
            <el-form-item label="开始日期">
              <el-date-picker
                  v-model="projectForm.startDate"
                  type="date"
                  placeholder="请选择"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计完成日期">
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
            <el-form-item label="状态">
              <el-select v-model="projectForm.status" placeholder="请选择">
                <el-option label="未开始" value="0" />
                <el-option label="进行中" value="1" />
                <el-option label="已关闭" value="2" />
                <el-option label="已归档" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="进度">
              <el-input-number v-model="projectForm.progress" :min="0" :max="100" :step="5" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="项目描述">
              <el-input
                  v-model="projectForm.description"
                  type="textarea"
                  placeholder="请输入项目描述"
                  :rows="4"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <div class="form-buttons">
              <el-button type="primary" @click="saveProject">保存</el-button>
              <el-button @click="goBack">返回</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request.js';
import { recordOperationLog } from '@/utils/operationLog.js';

const router = useRouter();

// 项目表单数据
const projectForm = ref({
  name: '',
  managerId: '',
  teamId: '',
  productId: '',
  startDate: '',
  endDate: '',
  status: '0', // 默认未开始
  progress: 0,
  description: ''
});

// 搜索相关变量
const userOptions = ref([]);
const teamOptions = ref([]);
const productOptions = ref([]);
const loadingUsers = ref(false);
const loadingTeams = ref(false);
const loadingProducts = ref(false);

// 搜索用户
const searchUsers = async (query) => {
  if (query) {
    loadingUsers.value = true;
    try {
      const response = await request.get(`/admin/search-users?search=${query}`);
      if (response.data.code === 200) {
        userOptions.value = response.data.data || [];
      }
    } catch (error) {
      console.error('搜索用户失败:', error);
    } finally {
      loadingUsers.value = false;
    }
  } else {
    userOptions.value = [];
  }
};

// 搜索团队
const searchTeams = async (query) => {
  if (query) {
    loadingTeams.value = true;
    try {
      const response = await request.get(`/teams?search=${query}`);
      if (response.data.code === 200) {
        teamOptions.value = response.data.data || [];
      }
    } catch (error) {
      console.error('搜索团队失败:', error);
    } finally {
      loadingTeams.value = false;
    }
  } else {
    teamOptions.value = [];
  }
};

// 搜索产品
const searchProducts = async (query) => {
  if (query) {
    loadingProducts.value = true;
    try {
      const response = await request.get(`/api/productResearch/products?search=${query}`);
      if (response.data.code === 200) {
        productOptions.value = response.data.data || [];
      }
    } catch (error) {
      console.error('搜索产品失败:', error);
    } finally {
      loadingProducts.value = false;
    }
  } else {
    productOptions.value = [];
  }
};

// 保存项目
const saveProject = async () => {
  try {
    // 构建请求数据
    const projectData = {
      name: projectForm.value.name,
      manager_id: projectForm.value.managerId ? parseInt(projectForm.value.managerId) : null,
      team_id: projectForm.value.teamId ? parseInt(projectForm.value.teamId) : null,
      product_id: projectForm.value.productId ? parseInt(projectForm.value.productId) : null,
      start_date: projectForm.value.startDate,
      end_date: projectForm.value.endDate,
      status: parseInt(projectForm.value.status),
      progress: projectForm.value.progress,
      description: projectForm.value.description
    };
    
    console.log('保存项目:', projectData);
    
    // 调用后端API保存项目
    const response = await request.post('/workbench/projects', projectData);
    if (response.data.code === 200) {
      console.log('项目创建成功');
      // 记录操作日志
      await recordOperationLog('创建了', '项目', null, projectForm.value.name);
      // 保存成功后返回
      goBack();
    }
  } catch (error) {
    console.error('保存项目失败:', error);
  }
};

// 返回上一页
const goBack = () => {
  router.push('/itemSet/itemList');
};
</script>

<style scoped>
.add-project {
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
}
</style>