<template>
  <div class="add-project">
    <h3>添加项目</h3>
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
              <el-select v-model="projectForm.managerId" placeholder="请选择">
                <el-option label="张三" value="202201" />
                <el-option label="李四" value="202202" />
                <el-option label="王五" value="202203" />
                <el-option label="胡一刀" value="202204" />
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
  startDate: '',
  endDate: '',
  status: '0', // 默认未开始
  progress: 0,
  description: ''
});

// 保存项目
const saveProject = async () => {
  try {
    // 构建请求数据
    const projectData = {
      name: projectForm.value.name,
      manager_id: projectForm.value.managerId ? parseInt(projectForm.value.managerId) : null,
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