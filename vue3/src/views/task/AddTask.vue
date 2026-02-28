<template>
  <div class="test-submit">
    <h3>提交测试</h3>
    <div class="form-container">
      <el-form :model="testForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属项目">
              <el-select v-model="testForm.project" placeholder="请选择">
                <el-option label="智慧教室 智慧云盘" value="project1" />
                <el-option label="实践教学管理平台" value="project2" />
                <el-option label="电子班牌管理系统" value="project3" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属任务">
              <el-select v-model="testForm.task" placeholder="请选择">
                <el-option label="家长端，界面优化调整" value="task1" />
                <el-option label="班牌PC端管理界面调整" value="task2" />
                <el-option label="班牌模板调整" value="task3" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="测试类型">
              <el-select v-model="testForm.testType" placeholder="请选择">
                <el-option label="功能测试" value="functional" />
                <el-option label="性能测试" value="performance" />
                <el-option label="安全测试" value="security" />
                <el-option label="兼容性测试" value="compatibility" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select v-model="testForm.leader" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
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
                  placeholder="请选！"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="预计完成日期">
              <el-date-picker
                  v-model="testForm.endDate"
                  type="date"
                  placeholder="请选！"
                  style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="当前状态">
              <el-select v-model="testForm.status" placeholder="请选择">
                <el-option label="待测试" value="pending" />
                <el-option label="测试中" value="testing" />
                <el-option label="已完成" value="completed" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="测试负责人">
              <el-select v-model="testForm.testLeader" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="团队成员">
              <el-input v-model="testForm.teamMembers" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="任务描述">
              <el-input
                  v-model="testForm.description"
                  type="textarea"
                  placeholder="请输入任务描述"
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
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 测试表单数据
const testForm = ref({
  project: '',
  task: '',
  testType: '',
  leader: '',
  startDate: '',
  endDate: '',
  status: '',
  testLeader: '',
  teamMembers: '',
  description: ''
});

// 文件列表
const fileList = ref([]);

// 处理文件选择
const handleFileChange = (file, fileList) => {
  console.log('文件变化:', file, fileList);
};

// 保存测试
const saveTest = () => {
  // 这里可以添加保存逻辑
  console.log('保存测试:', testForm.value);
  // 保存成功后返回
  goBack();
};

// 返回上一页
const goBack = () => {
  router.push('/workbench/testStatistics');
};
</script>

<style scoped>
.test-submit {
  padding: 10px;
  background-color: #fff;
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