<template>
  <div class="task-edit">
    <h3>{{ taskForm.name || '编辑任务' }}</h3>
    <div class="form-container">
      <div class="form-content">
        <div class="left-section">
          <el-form :model="taskForm" label-width="120px">
            <el-form-item label="任务名称">
              <el-input v-model="taskForm.name" placeholder="请输入" />
            </el-form-item>

            <el-form-item label="任务描述">
              <el-input
                  v-model="taskForm.description"
                  type="textarea"
                  placeholder="请输入任务描述"
                  :rows="6"
              />
            </el-form-item>

            <el-form-item label="附件">
              <el-upload
                  class="upload-demo"
                  action="#"
                  :auto-upload="false"
                  :on-change="handleFileChange"
                  :file-list="fileList"
                  drag
              >
                <i class="el-icon-upload"></i>
                <div class="el-upload__text">选择文件</div>
                <template #tip>
                  <div class="el-upload__tip">
                    可点击添加或拖拽上传，不超过100.0MB
                  </div>
                </template>
              </el-upload>
            </el-form-item>

            <div class="form-buttons">
              <el-button type="primary" @click="saveTask">保存</el-button>
              <el-button @click="goBack">返回</el-button>
            </div>
          </el-form>
        </div>

        <div class="right-section">
          <h3>基本信息</h3>
          <el-form :model="taskForm" label-width="120px">
            <el-form-item label="所属项目">
              <el-select v-model="taskForm.project" placeholder="请选择">
                <el-option label="项目名称1" value="project1" />
                <el-option label="项目名称2" value="project2" />
                <el-option label="项目名称3" value="project3" />
              </el-select>
            </el-form-item>

            <el-form-item label="指派给">
              <el-select v-model="taskForm.assignedTo" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>

            <el-form-item label="任务状态">
              <el-select v-model="taskForm.status" placeholder="请选择">
                <el-option label="进行中" value="inProgress" />
                <el-option label="待开始" value="pending" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>

            <el-form-item label="优先级">
              <el-select v-model="taskForm.priority" placeholder="请选择">
                <el-option label="重要" value="high" />
                <el-option label="一般" value="medium" />
                <el-option label="次要" value="low" />
              </el-select>
            </el-form-item>

            <el-form-item label="开始日期">
              <el-date-picker
                  v-model="taskForm.startDate"
                  type="date"
                  placeholder="请选"
                  style="width: 100%"
              />
            </el-form-item>

            <el-form-item label="完成日期">
              <el-date-picker
                  v-model="taskForm.endDate"
                  type="date"
                  placeholder="请选"
                  style="width: 100%"
              />
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from '@/utils/request.js';

const router = useRouter();
const route = useRoute();

// 任务表单数据
const taskForm = ref({
  name: '数据大屏-实训教学资源大数据',
  description: '',
  project: 'project1',
  assignedTo: 'zhangsan',
  status: 'inProgress',
  priority: 'high',
  startDate: '',
  endDate: ''
});

// 文件列表
const fileList = ref([]);

// 处理文件选择
const handleFileChange = (file, fileList) => {
  console.log('文件变化:', file, fileList);
};

// 获取任务详细数据
const getTaskDetails = async () => {
  try {
    // 从URL参数中获取任务ID
    const taskId = route.query.id;
    console.log('获取任务ID:', taskId);
    if (taskId) {
      // 调用后端API获取任务详细数据
      console.log('调用API获取任务详情:', `/workbench/tasks/${taskId}`);
      const response = await request.get(`/workbench/tasks/${taskId}`);
      console.log('API响应:', response);
      // 检查响应结构
      if (response && response.data) {
        if (response.data.code === 200) {
          const taskData = response.data.data;
          console.log('任务详情数据:', taskData);
          if (taskData) {
            // 填充表单数据
            taskForm.value = {
              name: taskData.name || taskData.title || '',
              description: taskData.description || '',
              project: taskData.project_id || '',
              assignedTo: taskData.assignee_id || '',
              status: getStatusValue(taskData.status),
              priority: getPriorityValue(taskData.priority),
              startDate: taskData.start_date || '',
              endDate: taskData.due_date || ''
            };
            console.log('表单数据:', taskForm.value);
          } else {
            console.error('任务数据为空');
          }
        } else {
          console.error('获取任务详情失败:', response.data.msg);
        }
      } else {
        console.error('API响应格式错误');
      }
    }
  } catch (error) {
    console.error('获取任务详细数据失败:', error);
  }
};

// 将状态数字转换为对应的值
const getStatusValue = (status) => {
  switch (status) {
    case 1:
      return 'pending';
    case 2:
      return 'inProgress';
    case 3:
      return 'completed';
    case 4:
      return 'completed';
    default:
      return 'pending';
  }
};

// 将优先级数字转换为对应的值
const getPriorityValue = (priority) => {
  switch (priority) {
    case 1:
      return 'high';
    case 2:
      return 'medium';
    case 3:
      return 'low';
    default:
      return 'medium';
  }
};

// 保存任务
const saveTask = () => {
  // 这里可以添加保存逻辑
  console.log('保存任务:', taskForm.value);
  // 保存成功后返回
  goBack();
};

// 返回上一页
const goBack = () => {
  router.push('/workbench/tasks');
};

// 组件挂载时获取任务详细数据
onMounted(() => {
  getTaskDetails();
});
</script>

<style scoped>
.task-edit {
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

.history-list {
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  padding: 10px;
  max-height: 200px;
  overflow-y: auto;
}

.history-item {
  display: flex;
  align-items: center;
  margin-bottom: 10px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.history-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.history-number {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  margin-right: 10px;
}

.history-date {
  margin-right: 15px;
  font-size: 12px;
  color: #909399;
}

.history-action {
  flex: 1;
  font-size: 12px;
  color: #303133;
}
</style>