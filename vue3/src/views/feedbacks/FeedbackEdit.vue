<template>
  <div class="feedback-edit">
    <h3>{{ isEdit ? '编辑反馈' : '创建反馈' }}</h3>
    <div class="form-container">
      <el-form :model="feedbackForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="所属产品" required>
              <el-autocomplete
                v-model="feedbackForm.product"
                :fetch-suggestions="querySearch"
                placeholder="请输入产品名称"
                style="width: 100%"
                :trigger-on-focus="false"
                @select="handleSelect"
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="标题" required>
          <el-input v-model="feedbackForm.title" placeholder="请输入" style="width: 80%" />
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
            <el-form-item label="抄送给">
              <el-select v-model="feedbackForm.cc" placeholder="请选择">
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

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

// 判断是编辑还是创建
const isEdit = ref(!!route.params.id);

// 反馈表单数据
const feedbackForm = ref({
  product: '',
  productId: '',
  title: '',
  description: '',
  reporter: '',
  cc: ''
});

// 文件列表
const fileList = ref([]);

// 产品列表
const products = ref([]);

// 加载产品列表
const loadProducts = async () => {
  try {
    const response = await request.get('/api/products');
    if (response.data.code === 200) {
      products.value = response.data.data;
    }
  } catch (error) {
    console.error('获取产品列表失败:', error);
  }
};

// 处理文件预览
const handlePreview = (file) => {
  console.log(file);
};

// 处理文件移除
const handleRemove = (file, fileList) => {
  console.log(file, fileList);
};

// 产品搜索
const querySearch = (queryString, callback) => {
  const results = queryString ? products.value.filter(item => 
    item.name.toLowerCase().includes(queryString.toLowerCase())
  ) : products.value;
  callback(results.map(item => ({ value: item.name, id: item.id })));
};

// 处理产品选择
const handleSelect = (item) => {
  feedbackForm.value.productId = item.id;
};

// 加载反馈详情
const loadFeedbackDetail = async () => {
  const id = route.params.id;
  if (id) {
    try {
      // 先加载产品列表
      await loadProducts();
      
      const response = await request.get(`/api/feedback/detail/${id}`);
      if (response.data.code === 200) {
        const data = response.data.data;
        // 根据productId找到产品名称
        const product = products.value.find(p => p.id === data.productId);
        feedbackForm.value = {
          product: product ? product.name : '',
          productId: data.productId || '',
          title: data.title || '',
          description: data.description || '',
          reporter: data.creatorName || '', // 反馈者就是创建人
          cc: data.assigneeName || '' // 抄送者就是被指派的人
        };
      } else {
        console.error('获取反馈详情失败:', response.data.message);
      }
    } catch (error) {
      console.error('获取反馈详情失败:', error);
    }
  }
};

// 保存反馈
const saveFeedback = async () => {
  try {
    const id = route.params.id;
    let response;
    if (id) {
      // 更新反馈
      response = await request.put(`/api/feedback/update/${id}`, feedbackForm.value);
    } else {
      // 创建反馈
      response = await request.post('/api/feedback/create', feedbackForm.value);
    }
    if (response.data.code === 200) {
      console.log('保存反馈成功');
      // 保存成功后返回
      goBack();
    } else {
      console.error('保存反馈失败:', response.data.message);
    }
  } catch (error) {
    console.error('保存反馈失败:', error);
  }
};

// 返回上一页
const goBack = () => {
  router.push('/feedbacks/feedback');
};

// 页面加载时获取反馈详情和产品列表
onMounted(() => {
  loadProducts();
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