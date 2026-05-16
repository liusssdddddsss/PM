<template>
  <div class="feedback-edit">
    <h3>{{ isEdit ? '编辑反馈' : '创建反馈' }}</h3>
    <div class="form-container">
      <el-form :model="feedbackForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="24">
            <el-form-item label="所属项目" required>
              <el-autocomplete
                v-model="feedbackForm.project"
                :fetch-suggestions="querySearch"
                placeholder="请输入项目名称"
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
              placeholder="请详细描述反馈内容"
              :rows="6"
              style="width: 80%"
          />
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="反馈者">
              <el-input v-model="feedbackForm.reporter" placeholder="请输入" disabled />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="类型">
              <el-select v-model="feedbackForm.type" placeholder="请选择">
                <el-option label="功能建议" value="suggestion" />
                <el-option label="用户需求" value="user" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="指派对象">
              <el-select v-model="feedbackForm.assigneeId" placeholder="请选择" filterable>
                <el-option
                  v-for="pm in productManagers"
                  :key="pm.id"
                  :label="pm.name"
                  :value="pm.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

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
import { recordOperationLog } from "@/utils/operationLog.js";

const router = useRouter();
const route = useRoute();

// 判断是编辑还是创建
const isEdit = ref(!!route.params.id);

// 当前登录用户
const currentUser = ref(null);

// 反馈表单数据
const feedbackForm = ref({
  project: '',
  productId: '',
  title: '',
  description: '',
  reporter: '',
  type: '',
  creatorId: '',
  assigneeId: null
});

// 文件列表
const fileList = ref([]);

// 项目列表
const projects = ref([]);

// 产品经理列表
const productManagers = ref([]);

// 获取当前登录用户信息
const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      currentUser.value = user;
      // 自动设置反馈者为当前登录用户
      feedbackForm.value.reporter = user.name || user.username;
      // 使用 username 作为 creatorId（数据库中用户表的主键是 username）
      feedbackForm.value.creatorId = user.username;
      console.log('设置创建者ID:', feedbackForm.value.creatorId);
    } catch (e) {
      console.error('解析用户信息失败:', e);
    }
  }
};

// 加载项目列表
const loadProjects = async () => {
  try {
    const response = await request.get('/api/projects');
    if (response.data.code === 200) {
      projects.value = response.data.data;
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

// 加载产品经理列表
const loadProductManagers = async () => {
  try {
    if (!currentUser.value) return;
    
    // 获取当前用户的团队信息
    const userId = currentUser.value.id || currentUser.value.username;
    const response = await request.get(`/api/users/${userId}/product-managers`);
    if (response.data.code === 200) {
      productManagers.value = response.data.data || [];
      // 如果有产品经理，默认选择第一个
      if (productManagers.value.length > 0 && !isEdit.value) {
        feedbackForm.value.assigneeId = productManagers.value[0].id;
      }
    }
  } catch (error) {
    console.error('获取产品经理列表失败:', error);
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

// 项目搜索
const querySearch = (queryString, callback) => {
  const results = queryString ? projects.value.filter(item => 
    item.name.toLowerCase().includes(queryString.toLowerCase())
  ) : projects.value;
  callback(results.map(item => ({ value: item.name, id: item.id })));
};

// 处理项目选择
const handleSelect = (item) => {
  feedbackForm.value.productId = item.id;
};

// 加载反馈详情
const loadFeedbackDetail = async () => {
  const id = route.params.id;
  if (id) {
    try {
      // 先加载项目列表和产品经理列表
      await loadProjects();
      await loadProductManagers();
      
      const response = await request.get(`/api/feedback/detail/${id}`);
      if (response.data.code === 200) {
        const data = response.data.data;
        // 根据productId找到项目名称
        const project = projects.value.find(p => p.id === data.productId);
        feedbackForm.value = {
          project: project ? project.name : '',
          productId: data.productId || '',
          title: data.title || '',
          description: data.description || '',
          reporter: data.creatorName || '', // 反馈者就是创建人
          type: data.type || '', // 反馈类型
          creatorId: data.creatorId || '',
          assigneeId: data.assigneeId || null
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
      response = await request.put('/api/feedback/update', feedbackForm.value);
    } else {
      // 创建反馈
      response = await request.post('/api/feedback/create', feedbackForm.value);
    }
    if (response.data.code === 200) {
      console.log('保存反馈成功');
      
      // 记录操作日志
      const action = id ? '编辑反馈' : '创建反馈';
      await recordOperationLog(action, '反馈管理', id || response.data.data?.id, feedbackForm.value.title);
      
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

// 页面加载时获取反馈详情和项目列表
onMounted(() => {
  getCurrentUser();
  loadProjects();
  loadProductManagers();
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