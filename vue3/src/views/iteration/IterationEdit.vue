<template>
  <div class="iteration-edit">
    <el-card class="box-card">
      <template #header>
        <div class="card-header">
          <span>{{ isEdit ? '编辑迭代' : '创建迭代' }}</span>
        </div>
      </template>
      <div style="width: 90%;margin: auto">
        <el-form :model="iterationForm" label-width="100px" class="iteration-form">
          <el-form-item label="迭代名称">
            <el-input v-model="iterationForm.name" placeholder="请输入迭代名称" />
          </el-form-item>
          <el-form-item label="所属项目">
            <el-select v-model="iterationForm.projectId" placeholder="请选择项目">
              <el-option
                  v-for="project in projects"
                  :key="project.id"
                  :label="project.projectName"
                  :value="project.id"
              />
            </el-select>
          </el-form-item>
          <el-form-item label="开始时间">
            <el-date-picker
                v-model="iterationForm.startDate"
                type="date"
                placeholder="选择开始日期"
                style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="结束时间">
            <el-date-picker
                v-model="iterationForm.endDate"
                type="date"
                placeholder="选择结束日期"
                style="width: 100%"
            />
          </el-form-item>
          <el-form-item label="状态">
          <el-select v-model="iterationForm.status" placeholder="请选择状态">
            <el-option label="未开始" value="0" />
            <el-option label="进行中" value="1" />
            <el-option label="已关闭" value="2" />
          </el-select>
        </el-form-item>
        <el-form-item label="进度">
          <el-input-number v-model="iterationForm.progress" :min="0" :max="100" :step="1" placeholder="请输入进度百分比" />
        </el-form-item>
        <el-form-item label="迭代描述">
          <el-input
              v-model="iterationForm.description"
              type="textarea"
              placeholder="请输入迭代描述"
              :rows="4"
          />
        </el-form-item>
          <el-form-item class="form-buttons">
            <el-button type="primary" @click="saveIteration">{{ isEdit ? '更新' : '创建' }}</el-button>
            <el-button @click="cancel">取消</el-button>
          </el-form-item>
        </el-form>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import {useRouter, useRoute} from 'vue-router';
import axios from 'axios';

const router = useRouter();
const route = useRoute();
const isEdit = ref(!!route.query.id);
const iterationId = ref(route.query.id);

const iterationForm = ref({
  name: '',
  description: '',
  projectId: '',
  startDate: '',
  endDate: '',
  status: 0,
  progress: 0
});

const projects = ref([]);

// 获取项目列表
const fetchProjects = async () => {
  try {
    // 传递username参数，使用固定值202201
    const response = await axios.get('http://localhost:9090/workbench/projects', {
      params: { username: '202201' }
    });
    if (response.data.code === 200) {
      projects.value = response.data.data;
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

// 获取迭代详情
const fetchIterationDetail = async () => {
  if (isEdit.value && iterationId.value) {
    try {
      const response = await axios.get(`http://localhost:9090/iteration/detail/${iterationId.value}`);
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
      response = await axios.put('http://localhost:9090/iteration/update', iterationForm.value);
    } else {
      response = await axios.post('http://localhost:9090/iteration/create', iterationForm.value);
    }
    if (response.data.code === 200) {
      ElMessage.success(isEdit.value ? '迭代更新成功' : '迭代创建成功');
      router.push('/iteration/iterationTableList');
    }
  } catch (error) {
    console.error('保存迭代失败:', error);
    ElMessage.error('保存失败，请稍后重试');
  }
};

// 取消
const cancel = () => {
  router.push('/iteration/iterationTableList');
};

// 初始加载
onMounted(() => {
  fetchProjects();
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
