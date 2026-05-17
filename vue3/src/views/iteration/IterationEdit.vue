<template>
  <div class="iteration-edit">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑迭代' : '添加迭代' }}</span>
        </div>
      </template>
      <div style="width: 90%;margin: auto">
        <el-form :model="iterationForm" label-width="100px" class="iteration-form">
          <el-form-item label="迭代名称">
            <el-input v-model="iterationForm.name" placeholder="请输入迭代名称" :readonly="isEdit && false" />
          </el-form-item>
          <el-form-item label="所属项目">
            <el-select v-model="iterationForm.projectId" placeholder="请选择项目" :disabled="isEdit" @change="onProjectChange">
              <el-option
                  v-for="project in projects"
                  :key="project.id"
                  :label="project.projectName"
                  :value="project.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="所属产品">
            <el-input
                :value="currentProductName"
                disabled
                placeholder="选择项目后自动获取"
            />
            <el-input type="hidden" v-model="iterationForm.productId" />
          </el-form-item>
          <el-form-item label="所属团队">
            <el-select v-model="iterationForm.teamId" placeholder="请选择团队" :disabled="isEdit">
              <el-option
                  v-for="team in teams"
                  :key="team.id"
                  :label="team.name"
                  :value="team.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker
                v-model="iterationForm.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
                :disabled="isEdit"
            />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
                v-model="iterationForm.endDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%"
                :disabled="isEdit"
            />
          </el-form-item>
          <el-form-item label="状态">
          <el-select v-model="iterationForm.status" placeholder="请选择状态" :disabled="isEdit">
            <el-option label="未开始" value="0" />
            <el-option label="进行中" value="1" />
            <el-option label="已关闭" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="进度">
          <el-input-number v-model="iterationForm.progress" :min="0" :max="100" :step="1" placeholder="请输入进度百分比" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="迭代描述">
          <el-input
              v-model="iterationForm.description"
              type="textarea"
              placeholder="请输入迭代描述"
              :rows="4"
              :readonly="isEdit && false"
          />
        </el-form-item>
          <el-form-item class="form-buttons">
            <el-button type="primary" @click="saveIteration" v-if="!isEdit || isEdit">保存</el-button>
            <el-button @click="cancel">返回</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import { ElMessage } from 'element-plus';
import request from '@/utils/request.js';
import { recordOperationLog } from '@/utils/operationLog.js';

const router = useRouter();
const route = useRoute();
const isEdit = ref(!!route.query.id);
const iterationId = ref(route.query.id);

const iterationForm = ref({
  name: '',
  description: '',
  projectId: '',
  productId: '',
  teamId: '',
  startDate: '',
  endDate: '',
  status: 0,
  progress: 0
});

const projects = ref([]);
const teams = ref([]);
const currentProductName = ref('');

// 获取项目列表
const fetchProjects = async () => {
  try {
    const userStr = localStorage.getItem('user');
    const username = userStr ? JSON.parse(userStr).username : '202201';
    
    const response = await request.get('/workbench/projects', {
      params: { username: username }
    });
    if (response.data.code === 200) {
      projects.value = response.data.data;
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

// 获取用户所在团队列表
const fetchUserTeams = async () => {
  try {
    const userStr = localStorage.getItem('user');
    const username = userStr ? JSON.parse(userStr).username : '202201';
    
    const response = await request.get(`/teams/user-teams/${username}`);
    if (response.data.code === 200 && response.data.data) {
      teams.value = response.data.data.map(team => ({
        id: team.id,
        name: team.name
      }));
    }
  } catch (error) {
    console.error('获取团队列表失败:', error);
  }
};

// 根据项目ID获取产品信息
const fetchProductByProject = async (projectId) => {
  if (!projectId) {
    iterationForm.value.productId = '';
    currentProductName.value = '';
    return;
  }
  
  try {
    const response = await request.get(`/api/projects/${projectId}`);
    if (response.data.code === 200 && response.data.data) {
      const project = response.data.data;
      if (project.product_id) {
        iterationForm.value.productId = project.product_id;
        // 获取产品名称
        const productRes = await request.get(`/api/productResearch/products/${project.product_id}`);
        if (productRes.data.code === 200 && productRes.data.data) {
          currentProductName.value = productRes.data.data.name || '';
        } else {
          currentProductName.value = '未知产品';
        }
      } else {
        iterationForm.value.productId = '';
        currentProductName.value = '';
      }
    }
  } catch (error) {
    console.error('获取产品信息失败:', error);
    iterationForm.value.productId = '';
    currentProductName.value = '';
  }
};

// 项目选择变化时触发
const onProjectChange = (projectId) => {
  fetchProductByProject(projectId);
};

// 获取迭代详情
const fetchIterationDetail = async () => {
  if (isEdit.value && iterationId.value) {
    try {
      const response = await request.get(`/iteration/detail/${iterationId.value}`);
      if (response.data.code === 200) {
        iterationForm.value = response.data.data;
      }
    } catch (error) {
      console.error('获取迭代详情失败:', error);
    }
  }
};

// 保存迭代
const saveIteration = async () => {
  try {
    let response;
    if (isEdit.value) {
      response = await request.put('/iteration/update', iterationForm.value);
      if (response.data.code === 200) {
        ElMessage.success('迭代更新成功');
        // 记录操作日志
        await recordOperationLog('编辑了', '迭代', null, iterationForm.value.name);
        router.push('/iteration/iterationList');
      }
    } else {
      response = await request.post('/iteration/create', iterationForm.value);
      if (response.data.code === 200) {
        ElMessage.success('迭代创建成功');
        // 记录操作日志
        await recordOperationLog('创建了', '迭代', null, iterationForm.value.name);
        router.push('/iteration/iterationList');
      }
    }
  } catch (error) {
    console.error('保存迭代失败:', error);
    ElMessage.error('保存失败，请稍后重试');
  }
};

// 取消
const cancel = () => {
  router.push('/iteration/iterationList');
};

// 初始加载
onMounted(() => {
  fetchProjects();
  fetchUserTeams();
  fetchIterationDetail();
});
</script>

<style scoped>
.iteration-edit {
  min-height: 100vh;
}

.box-card {
  max-width: 100%;
  margin: 0 auto;
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.iteration-form {
  margin-top: 20px;
}

.form-buttons :deep(.el-form-item__content) {
  display: flex;
  justify-content: center;
}
</style>
