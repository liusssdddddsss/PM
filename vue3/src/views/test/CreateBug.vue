<template>
  <div class="bug-submit">
    <h3>创建Bug</h3>
    <div class="form-container">
      <el-form :model="bugForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属项目">
              <el-select v-model="bugForm.projectId" placeholder="请选择">
                <el-option label="智慧教室 智慧云盘" value="1" />
                <el-option label="实践教学管理平台" value="2" />
                <el-option label="电子班牌管理系统" value="3" />
                <el-option label="家长端应用" value="8" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属任务">
              <el-select v-model="bugForm.taskId" placeholder="请选择">
                <el-option label="家长端，界面优化调整" value="1" />
                <el-option label="班牌PC端管理界面调整" value="2" />
                <el-option label="班牌模板调整" value="3" />
                <el-option label="家长端授权功能" value="5" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="Bug类型">
              <el-select v-model="bugForm.bugType" placeholder="请选择">
                <el-option label="代码错误" value="代码错误" />
                <el-option label="界面问题" value="界面问题" />
                <el-option label="逻辑错误" value="逻辑错误" />
                <el-option label="性能问题" value="性能问题" />
                <el-option label="安全问题" value="安全问题" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="严重程度">
              <el-select v-model="bugForm.severity" placeholder="请选择">
                <el-option label="紧急" value="1" />
                <el-option label="一般" value="2" />
                <el-option label="正常" value="3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="报告人">
              <el-select v-model="bugForm.reporterId" placeholder="请选择">
                <el-option label="202204" value="202204" />
                <el-option label="202205" value="202205" />
                <el-option label="202202" value="202202" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select v-model="bugForm.assigneeId" placeholder="请选择">
                <el-option label="202205" value="202205" />
                <el-option label="202203" value="202203" />
                <el-option label="202202" value="202202" />
                <el-option label="202201" value="202201" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前状态">
              <el-select v-model="bugForm.status" placeholder="请选择">
                <el-option label="待处理" value="0" />
                <el-option label="处理中" value="1" />
                <el-option label="已解决" value="2" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="Bug标题">
              <el-input v-model="bugForm.title" placeholder="请输入Bug标题" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="Bug描述">
              <el-input
                  v-model="bugForm.description"
                  type="textarea"
                  placeholder="请输入Bug描述"
                  :rows="4"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
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
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <div class="form-buttons">
              <el-button type="primary" @click="saveBug">保存</el-button>
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

const router = useRouter();

// Bug表单数据
const bugForm = ref({
  title: '',
  description: '',
  projectId: '',
  taskId: '',
  reporterId: '',
  assigneeId: '',
  severity: '',
  status: '0', // 默认待处理
  bugType: ''
});

// 文件列表
const fileList = ref([]);

// 处理文件选择
const handleFileChange = (file, fileList) => {
  console.log('文件变化:', file, fileList);
};

// 保存Bug
const saveBug = async () => {
  try {
    // 构建请求数据
    const bugData = {
      title: bugForm.value.title,
      description: bugForm.value.description,
      project_id: bugForm.value.projectId,
      task_id: bugForm.value.taskId,
      reporter_id: bugForm.value.reporterId,
      assignee_id: bugForm.value.assigneeId,
      severity: bugForm.value.severity,
      status: bugForm.value.status,
      bug_type: bugForm.value.bugType
    };
    
    console.log('保存Bug:', bugData);
    
    // 这里可以添加保存逻辑，调用后端API
    // 暂时模拟保存成功
    // const response = await request.post('/bug/create', bugData);
    // if (response.data.code === 200) {
      console.log('Bug创建成功');
      // 保存成功后返回
      goBack();
    // }
  } catch (error) {
    console.error('保存Bug失败:', error);
  }
};

// 返回上一页
const goBack = () => {
  router.push('/test/bugList');
};
</script>

<style scoped>
.bug-submit {
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