<template>
  <div class="feedback-edit">
    <h3>编辑反馈</h3>
    <div class="form-container">
      <el-form :model="feedbackForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="所属产品" required>
              <el-select v-model="feedbackForm.product" placeholder="请选择">
                <el-option label="禅道开源版" value="zentao" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属模块">
              <el-select v-model="feedbackForm.module" placeholder="请选择">
                <el-option label="/" value="/" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="类型">
              <el-select v-model="feedbackForm.type" placeholder="请选择">
                <el-option label="功能建议" value="功能建议" />
                <el-option label="缺陷" value="缺陷" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标题" required>
          <div style="display: flex; align-items: center; width: 100%">
            <el-input v-model="feedbackForm.title" placeholder="请输入" style="flex: 1; margin-right: 10px" />
            <el-checkbox v-model="feedbackForm.isPublic" style="margin-right: 10px">公开</el-checkbox>
            <span style="margin-right: 5px">优先级</span>
            <el-select v-model="feedbackForm.priority" style="width: 80px">
              <el-option label="1" value="1" />
              <el-option label="2" value="2" />
              <el-option label="3" value="3" />
              <el-option label="4" value="4" />
              <el-option label="5" value="5" />
            </el-select>
          </div>
        </el-form-item>

        <el-form-item label="描述">
          <el-input
              v-model="feedbackForm.description"
              type="textarea"
              placeholder="可以在编辑器直接贴图。"
              :rows="6"
              style="width: 80%"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="反馈者">
              <el-input v-model="feedbackForm.reporter" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="来源公司">
              <el-input v-model="feedbackForm.company" placeholder="请输入" />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="通知邮箱">
              <el-input v-model="feedbackForm.email" placeholder="请输入" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="抄送给">
              <el-select v-model="feedbackForm.cc" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="关键词">
          <el-input v-model="feedbackForm.keywords" placeholder="请输入" />
        </el-form-item>

        <el-form-item label="附件">
          <el-upload
              class="upload-demo"
              action="#"
              :auto-upload="false"
              :on-preview="handlePreview"
              :on-remove="handleRemove"
              :file-list="fileList"
              list-type="picture"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">可点击添加或拖拽上传,不超过 100.0MB</div>
            </template>
          </el-upload>
        </el-form-item>

        <el-form-item label="通知">
          <el-checkbox v-model="feedbackForm.notification">接收邮件通知</el-checkbox>
        </el-form-item>

        <el-form-item>
          <div class="form-buttons">
            <el-button type="primary" @click="saveFeedback">保存</el-button>
            <el-button @click="goBack">返回</el-button>
          </div>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from "@/utils/request.js";

const router = useRouter();
const route = useRoute();

// 反馈表单数据
const feedbackForm = ref({
  product: '',
  module: '',
  type: '',
  title: '',
  isPublic: false,
  priority: 3,
  description: '',
  reporter: '',
  company: '',
  email: '',
  cc: '',
  keywords: '',
  notification: true
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

// 加载反馈详情
const loadFeedbackDetail = async () => {
  const id = route.params.id;
  if (id) {
    try {
      const response = await request.get(`/api/feedback/detail/${id}`);
      if (response.code === 200) {
        const data = response.data;
        feedbackForm.value = {
          product: 'zentao', // 假设默认产品
          module: '/', // 假设默认模块
          type: data.type || '',
          title: data.title || '',
          isPublic: false, // 默认不公开
          priority: data.priority || 3,
          description: data.description || '',
          reporter: '', // 需要从用户信息中获取
          company: '',
          email: '',
          cc: '',
          keywords: '',
          notification: true
        };
      }
    } catch (error) {
      console.error('获取反馈详情失败:', error);
    }
  }
};

// 保存反馈
const saveFeedback = () => {
  // 这里可以添加保存逻辑
  console.log('保存反馈:', feedbackForm.value);
  // 保存成功后返回
  goBack();
};

// 返回上一页
const goBack = () => {
  router.push('/feedbacks');
};

// 页面加载时获取反馈详情
onMounted(() => {
  loadFeedbackDetail();
});
</script>

<style scoped>
.feedback-edit {
  background-color: white;
  padding-top: 10px;
  padding-left: 10px;
}

.form-container {
  padding: 20px;
}

.form-buttons {
  margin-top: 10px;
  width: 100%;
  text-align: center;
}
</style>