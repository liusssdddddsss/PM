<template>
  <div class="edit">
    <h3>查看项目</h3>
    <el-divider/>
    <div class="form-container">
      <el-form :model="projectForm" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="项目名称">
              <el-input v-model="projectForm.name" placeholder="请输入项目名称" readonly />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="所属产品">
              <el-select
                v-model="projectForm.product"
                filterable
                remote
                :remote-method="remoteSearch"
                :loading="loading"
                placeholder="请选择所属产品"
                disabled
              >
                <el-option
                  v-for="product in productList"
                  :key="product.id"
                  :label="product.name"
                  :value="product.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="计划开始日期">
              <el-date-picker
                  v-model="projectForm.startDate"
                  type="date"
                  placeholder="请选"
                  style="width: 100%"
                  disabled
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="计划完成日期">
              <el-date-picker
                  v-model="projectForm.endDate"
                  type="date"
                  placeholder="请选"
                  style="width: 100%"
                  disabled
              />
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="负责人">
              <el-select v-model="projectForm.leader" placeholder="请选择负责人" disabled>
                <el-option label="张三" value="zhangsan" />
                <el-option label="李四" value="lisi" />
                <el-option label="王五" value="wangwu" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="开发者">
              <el-select
                v-model="projectForm.developers"
                multiple
                filterable
                remote
                :remote-method="remoteSearchDevelopers"
                :loading="loadingDevelopers"
                placeholder="请选择开发者"
                disabled
              >
                <el-option
                  v-for="developer in developerList"
                  :key="developer.id"
                  :label="developer.name"
                  :value="developer.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="测试者">
              <el-select
                v-model="projectForm.testers"
                multiple
                filterable
                remote
                :remote-method="remoteSearchTesters"
                :loading="loadingTesters"
                placeholder="请选择测试者"
                disabled
              >
                <el-option
                  v-for="tester in testerList"
                  :key="tester.id"
                  :label="tester.name"
                  :value="tester.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="备注">
          <el-input
              v-model="projectForm.remark"
              type="textarea"
              placeholder="请输入备注"
              :rows="4"
              readonly
          />
        </el-form-item>

        <el-form-item label="访问控制">
          <el-radio-group v-model="projectForm.accessControl" disabled>
            <el-radio label="public">公开(所有人可见)</el-radio>
            <el-radio label="private">私有(只项目负责人、团队成员和干系人可访问)</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-row :gutter="20">
          <el-col :span="24">
            <div class="form-buttons">
              <el-button @click="goBack">返回</el-button>
            </div>
          </el-col>
        </el-row>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from '@/utils/request.js';
import { ElMessage } from 'element-plus';

const router = useRouter();
const route = useRoute();

// 项目表单数据
const projectForm = ref({
  name: '',
  product: '',
  startDate: '',
  endDate: '',
  leader: '',
  developers: [],
  testers: [],
  remark: '',
  accessControl: 'public'
});

// 产品列表
const productList = ref([]);
// 加载状态
const loading = ref(false);

// 开发者列表
const developerList = ref([]);
// 开发者加载状态
const loadingDevelopers = ref(false);

// 测试者列表
const testerList = ref([]);
// 测试者加载状态
const loadingTesters = ref(false);

// 远程搜索产品
const remoteSearch = (query) => {
  if (query !== '') {
    loading.value = true;
    // 模拟远程搜索，实际应该调用后端API
    setTimeout(() => {
      productList.value = productList.value.filter(item => {
        return item.name.toLowerCase().includes(query.toLowerCase());
      });
      loading.value = false;
    }, 200);
  } else {
    // 如果查询为空，加载所有产品
    loadProducts();
  }
};

// 加载产品列表
const loadProducts = async () => {
  try {
    console.log('开始加载产品列表...');
    // 修改API路径，确保与后端服务器的API路径匹配
    const response = await request.get('/workbench/products');
    console.log('产品列表响应:', response);
    if (response.data.code === 200) {
      // 验证数据格式，确保每个产品都有id和name属性
      const products = response.data.data || [];
      console.log('原始产品数据:', products);
      productList.value = products.map(product => ({
        id: product.id || product.product_id || Math.random().toString(36).substr(2, 9),
        name: product.name || product.product_name || '未知产品'
      }));
      console.log('加载的产品列表:', productList.value);
    } else {
      console.error('加载产品列表失败，响应码:', response.data.code);
      productList.value = [];
    }
  } catch (error) {
    console.error('加载产品列表失败:', error);
    // 加载失败时，设置默认数据
    productList.value = [];
  }
};

// 加载项目成员列表
const loadProjectMembers = async (projectId) => {
  try {
    console.log('开始加载项目成员列表，项目ID:', projectId);
    // 修改API路径，确保与后端服务器的API路径匹配
    const response = await request.get(`/workbench/projects/${projectId}/members`);
    console.log('项目成员列表响应:', response);
    if (response.data.code === 200) {
      const members = response.data.data || [];
      console.log('原始项目成员数据:', members);
      
      // 验证数据格式，确保每个成员都有id和name属性
      const validMembers = members.map(member => ({
        id: member.id || member.user_id || Math.random().toString(36).substr(2, 9),
        name: member.name || member.username || '未知用户',
        role: member.role || 'member'
      }));
      console.log('验证后的项目成员数据:', validMembers);
      
      // 按角色分类 - 使用数字角色ID
      developerList.value = validMembers.filter(member => member.role === '3' || member.role === 3);
      testerList.value = validMembers.filter(member => member.role === '4' || member.role === 4);
      
      console.log('加载的开发者列表:', developerList.value);
      console.log('加载的测试者列表:', testerList.value);
      
      // 强制更新表单值，确保组件能够正确显示已选项
      setTimeout(() => {
        projectForm.value.developers = [...developerList.value.map(member => member.id)];
        projectForm.value.testers = [...testerList.value.map(member => member.id)];
        console.log('设置后的开发者表单值:', projectForm.value.developers);
        console.log('设置后的测试者表单值:', projectForm.value.testers);
      }, 100);
    } else {
      console.error('加载项目成员列表失败，响应码:', response.data.code);
      developerList.value = [];
      testerList.value = [];
    }
  } catch (error) {
    console.error('加载项目成员列表失败:', error);
    // 加载失败时，设置默认数据
    developerList.value = [];
    testerList.value = [];
  }
};

// 远程搜索开发者
const remoteSearchDevelopers = (query) => {
  if (query !== '') {
    loadingDevelopers.value = true;
    // 过滤本地列表
    setTimeout(() => {
      developerList.value = developerList.value.filter(item => {
        return item.name.toLowerCase().includes(query.toLowerCase());
      });
      loadingDevelopers.value = false;
    }, 200);
  } else {
    // 如果查询为空，重新加载项目成员
    const projectId = route.query.id;
    if (projectId) {
      loadProjectMembers(projectId);
    }
  }
};

// 加载开发者列表
const loadDevelopers = async () => {
  // 开发者列表将通过loadProjectMembers加载
};

// 远程搜索测试者
const remoteSearchTesters = (query) => {
  if (query !== '') {
    loadingTesters.value = true;
    // 过滤本地列表
    setTimeout(() => {
      testerList.value = testerList.value.filter(item => {
        return item.name.toLowerCase().includes(query.toLowerCase());
      });
      loadingTesters.value = false;
    }, 200);
  } else {
    // 如果查询为空，重新加载项目成员
    const projectId = route.query.id;
    if (projectId) {
      loadProjectMembers(projectId);
    }
  }
};

// 加载测试者列表
const loadTesters = async () => {
  // 测试者列表将通过loadProjectMembers加载
};

// 获取项目详情
const fetchProjectDetail = async () => {
  const projectId = route.query.id;
  if (!projectId) {
    ElMessage.error('缺少项目ID');
    return;
  }
  
  try {
    console.log('Fetching project detail for ID:', projectId);
    // 修改API路径，确保与后端服务器的API路径匹配
    const response = await request.get(`/workbench/projects/${projectId}`);
    console.log('Project detail response:', response);
    if (response.data.code === 200) {
      const project = response.data.data;
      console.log('Project data:', project);
      // 填充表单数据
      projectForm.value = {
        name: project.title || '',
        product: project.product_id || '',
        startDate: project.startTime ? new Date(project.startTime) : '',
        endDate: project.finishTime ? new Date(project.finishTime) : '',
        leader: project.person || '',
        developers: project.developers || [],
        testers: project.testers || [],
        remark: project.remark || project.description || '',
        accessControl: project.accessControl || project.access_control || 'public'
      };
      
      // 确保产品列表已加载
      if (productList.value.length === 0) {
        await loadProducts();
      }
      
      // 加载项目成员列表
      await loadProjectMembers(projectId);
    }
  } catch (error) {
    console.error('获取项目详情失败:', error);
    ElMessage.error('获取项目详情失败');
  }
};

// 保存项目
const saveProject = async () => {
  const projectId = route.query.id;
  if (!projectId) {
    ElMessage.error('缺少项目ID');
    return;
  }
  
  try {
    // 构建保存数据
    const saveData = {
      title: projectForm.value.name,
      product_id: projectForm.value.product,
      startTime: projectForm.value.startDate,
      finishTime: projectForm.value.endDate,
      person: projectForm.value.leader,
      developers: projectForm.value.developers,
      testers: projectForm.value.testers,
      remark: projectForm.value.remark,
      accessControl: projectForm.value.accessControl
    };
    
    // 调用后端API保存项目
    const response = await request.put(`/workbench/projects/${projectId}`, saveData);
    if (response.data.code === 200) {
      ElMessage.success('项目保存成功');
      // 保存成功后返回
      goBack();
    }
  } catch (error) {
    console.error('保存项目失败:', error);
    ElMessage.error('保存项目失败');
  }
};

// 返回上一页
const goBack = () => {
  router.push('/itemSet/itemList');
};

// 页面加载时获取项目详情
onMounted(async () => {
  console.log('Page mounted, loading data...');
  console.log('Route query:', route.query);
  await loadProducts();
  await fetchProjectDetail();
  console.log('Data loading completed');
  console.log('Product list:', productList.value);
  console.log('Developer list:', developerList.value);
  console.log('Tester list:', testerList.value);
});
</script>

<style scoped>
.edit {
  background-color: #fff;
  padding: 20px;
  min-height: 100vh;
}
.el-divider{
  margin-top: 10px;
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
.el-form-item {
  margin-bottom: 20px;
}

.el-radio,
.el-checkbox {
  display: block;
  margin-bottom: 10px;
}
</style>