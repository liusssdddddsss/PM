<template>
  <div class="task-edit">
    <h3>数据大屏-实训教学资源大数据</h3>
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

            <el-form-item label="历史记录">
              <div class="history-list">
                <div class="history-item">
                  <span class="history-number">1</span>
                  <span class="history-date">2023-08-30 09:12:12</span>
                  <span class="history-action">由张三创建</span>
                </div>
                <div class="history-item">
                  <span class="history-number">2</span>
                  <span class="history-date">2023-08-30 09:12:12</span>
                  <span class="history-action">由李四修改</span>
                </div>
                <div class="history-item">
                  <span class="history-number">3</span>
                  <span class="history-date">2023-09-30 09:12:11</span>
                  <span class="history-action">由王五完成</span>
                </div>
              </div>
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

            <el-form-item label="父级任务">
              <el-select v-model="taskForm.parentTask" placeholder="请选择">
                <el-option label="任务名称1" value="task1" />
                <el-option label="任务名称2" value="task2" />
                <el-option label="任务名称3" value="task3" />
              </el-select>
            </el-form-item>

            <el-form-item label="指派给">
              <el-select v-model="taskForm.assignedTo" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>

            <el-form-item label="任务类型">
              <el-select v-model="taskForm.taskType" placeholder="请选择">
                <el-option label="开发" value="development" />
                <el-option label="测试" value="testing" />
                <el-option label="设计" value="design" />
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

            <el-form-item label="优先级">
              <el-input-number v-model="taskForm.priorityValue" :min="0" :max="100" />
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

            <el-form-item label="完成时长">
              <el-input v-model="taskForm.duration" placeholder="请输入" />
            </el-form-item>

            <el-form-item label="负责人">
              <el-select v-model="taskForm.leader" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-form>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 任务表单数据
const taskForm = ref({
  name: '数据大屏-实训教学资源大数据',
  description: '',
  project: 'project1',
  parentTask: 'task2',
  assignedTo: 'zhangsan',
  taskType: 'development',
  status: 'inProgress',
  priority: 'high',
  priorityValue: 25,
  startDate: '',
  endDate: '',
  duration: '',
  leader: ''
});

// 文件列表
const fileList = ref([]);

// 处理文件选择
const handleFileChange = (file, fileList) => {
  console.log('文件变化:', file, fileList);
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