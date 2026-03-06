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
            
            <el-form-item label="附件">
              <el-upload
                class="upload-demo"
                action="#"
                :on-preview="handlePreview"
                :on-remove="handleRemove"
                :file-list="fileList"
                :auto-upload="false"
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
              <el-button type="primary" @click="submitForm">保存</el-button>
              <el-button @click="goBack">返回</el-button>
            </div>
          </el-form>
        </div>
        
        <div class="right-section">
          <h3>基本信息</h3>
          <el-form :model="taskForm" label-width="120px">
            <el-form-item label="所属项目">
              <el-select v-model="taskForm.project" placeholder="请选择">
                <el-option label="智慧教室" value="智慧教室" />
                <el-option label="电子班牌" value="电子班牌" />
                <el-option label="数据大屏" value="数据大屏" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="任务类型">
              <el-select v-model="taskForm.type" placeholder="请选择">
                <el-option label="需求" value="需求" />
                <el-option label="开发" value="开发" />
                <el-option label="测试" value="测试" />
                <el-option label="文档" value="文档" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="父任务">
              <el-select v-model="taskForm.parentTask" placeholder="请选择">
                <el-option label="无" value="" />
                <el-option label="任务1" value="task1" />
                <el-option label="任务2" value="task2" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="指派给">
              <el-select v-model="taskForm.assignee" placeholder="请选择">
                <el-option label="张三" value="张三" />
                <el-option label="李四" value="李四" />
                <el-option label="王五" value="王五" />
                <el-option label="赵六" value="赵六" />
                <el-option label="孙七" value="孙七" />
              </el-select>
            </el-form-item>
            
            <el-form-item label="优先级">
              <el-select v-model="taskForm.priority" placeholder="请选择">
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
              <el-select v-model="taskForm.leader" placeholder="请选择">
                <el-option label="张三" value="张三" />
                <el-option label="李四" value="李四" />
                <el-option label="王五" value="王五" />
                <el-option label="赵六" value="赵六" />
                <el-option label="孙七" value="孙七" />
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
  name: '',
  description: '',
  project: '',
  type: '',
  parentTask: '',
  assignee: '',
  priority: '',
  startDate: '',
  endDate: '',
  estimatedHours: '',
  leader: ''
});

// 文件列表
const fileList = ref([]);

// 处理文件预览
const handlePreview = (file) => {
  console.log(file);
};

// 处理文件移除
const handleRemove = (file, fileList) => {
  console.log(file, fileList);
};

// 提交表单
const submitForm = () => {
  console.log('提交任务:', taskForm.value);
  // 这里可以添加表单验证和提交逻辑
  // 提交成功后跳转回任务列表
  router.push('/task/meJoinTasks');
};

// 重置表单
const resetForm = () => {
  taskForm.value = {
    name: '',
    description: '',
    project: '',
    type: '',
    parentTask: '',
    assignee: '',
    priority: '',
    startDate: '',
    endDate: '',
    estimatedHours: '',
    leader: ''
  };
  fileList.value = [];
};

// 返回上一页
const goBack = () => {
  router.push('/task/meJoinTasks');
};
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