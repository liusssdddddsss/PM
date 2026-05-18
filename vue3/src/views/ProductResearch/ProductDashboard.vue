<template>
  <div class="product-research">
    <el-card style="max-width: 99%">
    <div class="tou">
      <div class="left">
        <h3>产品总览</h3>
        <div class="kuai">
          <span
              v-for="(item,index) in ovCount"
              :key="index"
          >
            <div>{{item.name}}</div>
            <div>{{item.count}}</div>
          </span>
        </div>
      </div>

      <el-divider direction="vertical" style="height: auto;align-self: stretch"/>

      <div class="right">
        <h3 style="display: inline-block">产品年度推进统计</h3>
<!--        下拉菜单待改-->
        <el-select v-model="selectedYear" @change="handleYearChange" placeholder="请选择年份" size="small" style="display: inline-block;width: 80px;">
          <el-option label="2023" value="2023"/>
          <el-option label="2024" value="2024" />
          <el-option label="2025" value="2025" />
        </el-select>
        <div class="kuai">
          <span
              v-for="(item,index) in tuiJinCount"
              :key="index"
          >
            <div>{{item.name}}</div>
            <div>{{item.count}}</div>
          </span>
        </div>
      </div>
    </div>
    </el-card>

    <div class="comment">
      <div class="com-left" style="width: 65%">


<!--        产品列表整合-->
        <el-card style="max-width: 98%;margin-top: 10px">
          <div class="product-list-tabs">
            <div class="tab-header" style="display: flex; justify-content: space-between; align-items: center;">
              <div>
                <h4 style="margin: 0; font-size: 16px; color: #303133;">产品列表</h4>
              </div>
              <el-button v-if="isProductManager" type="primary" @click="showCreateProductDialog = true">创建产品</el-button>
            </div>
            <div class="tab-content">
              <!-- 未关闭的产品列表 -->
              <div class="unclosed-product-list">
                <el-table :data="unclosedProducts" stripe style="width: 100%">
                  <el-table-column prop="projectName" label="产品名称" min-width="200">
                    <template #default="scope">
                      <span class="project-name" style="cursor: pointer; color: #409EFF;" @click="openProductDetail(scope.row)">
                        {{ scope.row.projectName }}
                      </span>
                    </template>
                  </el-table-column>
                  <el-table-column prop="manager" label="负责人" min-width="100" />
                  <el-table-column prop="activeNeeds" label="项目" min-width="80" />
                  <el-table-column prop="iterationCount" label="迭代项目数" min-width="100">
                    <template #default="scope">
                      <div class="iteration-count">
                        {{ scope.row.iterationCount || 0 }}
                      </div>
                    </template>
                  </el-table-column>
                  <el-table-column prop="plan" label="计划" min-width="80" />
                  <el-table-column prop="activeBugs" label="Bug数" min-width="80" />
                  <el-table-column prop="release" label="发布" min-width="80" />
                  <el-table-column label="操作" min-width="80">
                    <template #default="scope">
                      <el-button type="text" size="small" @click="handleDeleteProduct(scope.row)" style="color: #F56C6C;">删除</el-button>
                    </template>
                  </el-table-column>
                </el-table>
              </div>
            </div>
          </div>
        </el-card>
      </div>

      <div class="com-right" style="width: 35%;">
<!--        产品开发周期分布-->        
        <el-card style="max-width: 98%;margin-top: 10px">
          <h3>产品开发周期分布</h3>
          <el-divider/>
          <div ref="faBuDom" style="width: 100%; height: 250px;"></div>
          <div class="bangDan">
            <h4>对应周期产品列表</h4>
            <div class="product-list" style="max-height: 200px; overflow-y: auto;">
              <div v-if="!selectedPeriodProducts || selectedPeriodProducts.length === 0" class="no-data">
                请点击上方饼图查看对应周期的产品列表
              </div>
              <div v-else v-for="(product, index) in selectedPeriodProducts" :key="product.id" class="product-item">
                <span class="product-index">{{ index + 1 }}</span>
                <span class="product-name" style="cursor: pointer; color: #409EFF;" @click="goToProductList(product.name)">{{ product.name }}</span>
              </div>
            </div>
          </div>
        </el-card>



      </div>
    </div>
  </div>

  <!-- 创建产品对话框 -->
  <el-dialog
    v-model="showCreateProductDialog"
    title="创建产品"
    width="500px"
  >
    <el-form :model="newProduct" :rules="createProductRules" ref="createProductForm">
      <el-form-item label="产品名称" prop="name">
        <el-input v-model="newProduct.name" placeholder="请输入产品名称" />
      </el-form-item>
      <el-form-item label="产品代码" prop="code">
        <el-input v-model="newProduct.code" placeholder="请输入产品代码" />
      </el-form-item>
      <el-form-item label="负责人" prop="owner_id">
        <el-select 
          v-model="newProduct.owner_id" 
          placeholder="请输入负责人姓名搜索" 
          filterable 
          remote 
          :remote-method="searchUsers"
          :loading="userLoading"
          style="width: 100%;"
        >
          <el-option 
            v-for="user in userOptions" 
            :key="user.userId" 
            :label="user.name" 
            :value="user.userId" 
          />
        </el-select>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="showCreateProductDialog = false">取消</el-button>
        <el-button type="primary" @click="createProduct">确定</el-button>
      </span>
    </template>
  </el-dialog>

  <!-- 产品详情对话框 -->
  <el-dialog
    v-model="showProductDetailDialog"
    :title="isEditing ? '编辑产品' : ''"
    width="700px"
  >
    <div class="product-detail-content">
      <!-- 编辑模式 -->
      <div v-if="isEditing">
        <el-form :model="editingProduct" :rules="editProductRules" ref="editProductForm" label-width="100px">
          <el-form-item label="产品名称" prop="name">
            <el-input v-model="editingProduct.name" placeholder="请输入产品名称" />
          </el-form-item>
          <el-form-item label="产品代码" prop="code">
            <el-input v-model="editingProduct.code" placeholder="请输入产品代码" />
          </el-form-item>
          <el-form-item label="负责人" prop="owner_id">
            <el-select 
              v-model="editingProduct.owner_id" 
              placeholder="请输入负责人姓名搜索" 
              filterable 
              remote 
              :remote-method="searchUsers"
              :loading="userLoading"
              style="width: 100%;"
            >
              <el-option 
                v-for="user in userOptions" 
                :key="user.userId" 
                :label="user.name" 
                :value="user.userId" 
              />
            </el-select>
          </el-form-item>
          <el-form-item label="状态" prop="status">
            <el-select v-model="editingProduct.status" style="width: 100%;">
              <el-option label="进行中" :value="1" />
              <el-option label="已完成" :value="0" />
            </el-select>
          </el-form-item>
          <el-form-item label="描述" prop="description">
            <el-input v-model="editingProduct.description" type="textarea" :rows="3" placeholder="请输入产品描述" />
          </el-form-item>
        </el-form>
      </div>
      
      <!-- 查看模式 -->
      <div v-else>
        <div class="product-detail-header">
          <h3 class="product-detail-name">{{ productDetail.name }}</h3>
        </div>
        <div class="product-detail-body">
          <div class="product-basic-info">
            <h4>产品基本信息</h4>
            <div class="basic-info-grid">
              <div class="info-item">
                <span class="info-label">产品代码</span>
                <span class="info-value">{{ productDetail.code || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">负责人</span>
                <span class="info-value">{{ productDetail.manager || '-' }}</span>
              </div>
              <div class="info-item">
                <span class="info-label">状态</span>
                <span class="info-value">{{ getProductStatusText(productDetail.status) }}</span>
              </div>
            </div>
          </div>
          
          <div class="product-projects">
            <h4>关联项目</h4>
            <div class="project-list-container">
              <div v-if="!productDetail.projects || productDetail.projects.length === 0" class="no-projects">
                暂无关联项目
              </div>
              <div v-else>
                <div 
                  v-for="project in productDetail.projects" 
                  :key="project.id" 
                  class="project-item-row"
                  @click="goToProject(project)"
                >
                  <div class="project-info">
                    <div class="project-title">{{ project.name }}</div>
                  </div>
                  <div class="project-meta">
                    <div class="project-manager">{{ project.manager || '-' }}</div>
                    <div class="project-team">{{ project.team || '-' }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <div class="product-iterations">
            <h4>关联迭代</h4>
            <div class="iteration-list-container">
              <div v-if="!productDetail.iterations || productDetail.iterations.length === 0" class="no-data">
                暂无关联迭代
              </div>
              <div v-else>
                <div 
                  v-for="iteration in productDetail.iterations" 
                  :key="iteration.id" 
                  class="iteration-item-row"
                >
                  <div class="iteration-info">
                    <div class="iteration-title">{{ iteration.name }}</div>
                    <div class="iteration-project" style="font-size: 12px;color: #909399;">
                      所属项目: {{ iteration.projectName }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="handleProductDetailCancel">取消</el-button>
        <el-button v-if="!isEditing" @click="startEditing">编辑</el-button>
        <el-button v-if="isEditing" type="primary" @click="saveProduct">保存</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
// 产品总览数据
import {ref, onMounted} from "vue";
import { useRouter } from "vue-router";
import ProductList from "@/views/workbenchView/listView/ProductList.vue";
import {useEcharts} from "@/utils/useEcharts.js";
import StayTestList from "@/views/workbenchView/listView/StayTestList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";
import request from "@/utils/request.js";
import { ElMessage, ElMessageBox } from 'element-plus';
import { recordOperationLog } from '@/utils/operationLog.js';

// 初始化路由
const router = useRouter();
// 调试：查看request对象的baseURL
console.log('Request baseURL:', request.defaults.baseURL);
// 选择的年份
const selectedYear = ref('2023');

// 判断用户是否为产品经理
const isProductManager = ref(false);

// 获取用户角色
const getUserRole = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    const user = JSON.parse(userStr);
    // 角色ID为2表示产品经理
    if (user.role_id && user.role_id === 2) {
      isProductManager.value = true;
    }
  }
};

// 未关闭的产品列表数据
const unclosedProducts = ref([]);

// 产品发布列表数据
const productReleases = ref([]);

// 产品总览数据
const ovCount = ref([]);

// 产品年度推进统计数据
const tuiJinCount = ref([]);

// 选中周期的产品列表
const selectedPeriodProducts = ref([]);

// 标签页数据
const activeTab = ref(0);
const tabs = ref([
  { name: '产品列表' }
]);

// 创建产品相关
const showCreateProductDialog = ref(false);
const newProduct = ref({
  name: '',
  code: '',
  owner_id: '',
  status: 1
});
const createProductForm = ref(null);
const createProductRules = ref({
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入产品代码', trigger: 'blur' }
  ],
  owner_id: [
    { required: true, message: '请选择负责人', trigger: 'blur' }
  ]
});

// 产品详情相关
const showProductDetailDialog = ref(false);
const isEditing = ref(false);
const currentProductId = ref(null);
const productDetail = ref({
  id: null,
  name: '',
  code: '',
  manager: '',
  status: 1,
  projects: [],
  iterations: []
});
const editingProduct = ref({
  id: null,
  name: '',
  code: '',
  owner_id: '',
  status: 1,
  description: ''
});
const editProductForm = ref(null);
const editProductRules = ref({
  name: [
    { required: true, message: '请输入产品名称', trigger: 'blur' }
  ],
  code: [
    { required: true, message: '请输入产品代码', trigger: 'blur' }
  ],
  owner_id: [
    { required: true, message: '请选择负责人', trigger: 'blur' }
  ],
  status: [
    { required: true, message: '请选择状态', trigger: 'change' }
  ]
});

// 用户搜索相关
const userOptions = ref([]);
const userLoading = ref(false);

// 搜索用户
const searchUsers = async (keyword) => {
  if (!keyword) {
    userOptions.value = [];
    return;
  }
  
  userLoading.value = true;
  try {
    const response = await request.get('/admin/users/search', {
      params: { keyword: keyword }
    });
    if (response.data.code === 200) {
      userOptions.value = response.data.data || [];
    }
  } catch (error) {
    console.error('搜索用户失败:', error);
    userOptions.value = [];
  } finally {
    userLoading.value = false;
  }
};

// 创建产品
const createProduct = async () => {
  if (!createProductForm.value) return;
  
  try {
    await createProductForm.value.validate();
    
    const response = await request.post('/api/products', newProduct.value);
    if (response.data.code === 200) {
      ElMessage.success('产品创建成功');
      showCreateProductDialog.value = false;
      
      // 重置表单
      newProduct.value = {
        name: '',
        code: '',
        owner_id: '',
        status: 1
      };
      
      // 重新获取产品数据
      await fetchUnclosedProducts();
      await fetchProductOverview();
      await initCharts();
    } else {
      ElMessage.error('产品创建失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('创建产品失败:', error);
    ElMessage.error('产品创建失败，请稍后重试');
  }
};

// 删除产品
const handleDeleteProduct = async (productRow) => {
  try {
    // 获取产品名称用于确认和日志记录
    const productName = productRow.projectName || productRow.name;
    
    // 确认删除
    await ElMessageBox.confirm(
      `确定要删除产品 "${productName}" 吗？`,
      '提示',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }
    );
    
    // 获取产品ID
    let productId = productRow.id;
    if (!productId && productRow.projectName) {
      const foundProduct = unclosedProducts.value.find(p => p.projectName === productRow.projectName);
      if (foundProduct) {
        productId = foundProduct.id;
      }
    }
    
    if (!productId) {
      ElMessage.error('无法获取产品ID');
      return;
    }
    
    // 调用删除接口
    const response = await request.delete(`/api/products/${productId}`);
    
    if (response.data.code === 200) {
      ElMessage.success('产品删除成功');
      
      // 记录操作日志
      try {
        await recordOperationLog('删除了', '产品', null, productName);
      } catch (logError) {
        console.error('记录操作日志失败:', logError);
      }
      
      // 重新获取产品数据
      await fetchUnclosedProducts();
      await fetchProductOverview();
      await initCharts();
    } else {
      ElMessage.error('产品删除失败: ' + response.data.msg);
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除产品失败:', error);
      ElMessage.error('产品删除失败，请稍后重试');
    }
  }
};

// 打开产品详情
const openProductDetail = async (productRow) => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    const username = userStr ? JSON.parse(userStr).username : null;
    
    // 从产品列表数据中查找产品ID
    let productId = productRow.id;
    if (!productId && productRow.projectName) {
      // 如果没有ID，尝试通过名称查找
      const foundProduct = unclosedProducts.value.find(p => p.projectName === productRow.projectName);
      if (foundProduct) {
        productId = foundProduct.id;
      }
    }
    
    if (!productId) {
      ElMessage.error('无法获取产品信息');
      return;
    }
    
    currentProductId.value = productId;
    
    // 获取产品详情
    const response = await request.get(`/api/products/${productId}/detail`, {
      params: { username }
    });
    
    if (response.data.code === 200) {
      productDetail.value = response.data.data;
      isEditing.value = false;
      showProductDetailDialog.value = true;
    } else {
      ElMessage.error('获取产品详情失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('打开产品详情失败:', error);
    ElMessage.error('获取产品详情失败');
  }
};

// 获取产品状态文本
const getProductStatusText = (status) => {
  // 确保status是数字类型
  const statusNum = Number(status);
  const statusMap = {
    0: '已完成',
    1: '进行中'
  };
  console.log('产品状态原始值:', status, '转换后:', statusNum);
  return statusMap[statusNum] || '未知';
};

// 开始编辑
const startEditing = () => {
  editingProduct.value = {
    id: productDetail.value.id,
    name: productDetail.value.name,
    code: productDetail.value.code,
    owner_id: productDetail.value.owner_id,
    status: productDetail.value.status || 1,
    description: productDetail.value.description || ''
  };
  isEditing.value = true;
};

// 取消产品详情编辑
const handleProductDetailCancel = () => {
  if (isEditing.value) {
    isEditing.value = false;
  } else {
    showProductDetailDialog.value = false;
  }
};

// 保存产品
const saveProduct = async () => {
  if (!editProductForm.value) return;
  
  try {
    await editProductForm.value.validate();
    
    const response = await request.put(`/api/products/${currentProductId.value}`, editingProduct.value);
    
    if (response.data.code === 200) {
      ElMessage.success('产品保存成功');
      isEditing.value = false;
      
      // 重新获取产品详情
      await openProductDetail({ id: currentProductId.value });
      
      // 重新获取产品列表
      await fetchUnclosedProducts();
      await fetchProductOverview();
      await initCharts();
    } else {
      ElMessage.error('产品保存失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('保存产品失败:', error);
    ElMessage.error('产品保存失败，请稍后重试');
  }
};

// 跳转到项目
const goToProject = (project) => {
  router.push(`/Workbench/ProjectList?projectId=${project.id}`);
};


// 从后端获取产品总览数据
const fetchProductOverview = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      
      // 获取产品总览基础数据
      const overviewResponse = await request.get('/dashboard/product-overview', {
        params: { username: user.username }
      });
      
      if (overviewResponse.data.code === 200) {
        const data = overviewResponse.data.data;
        ovCount.value = [
          { name: '产品总数', count: data.productCount || 0 },
          { name: '未完成计划数', count: data.unfinishedPlanCount || 0 },
          { name: '项目总数', count: data.unclosedNeedCount || 0 },
          { name: '总Bug数', count: data.activeBugCount || 0 },
          { name: '迭代项目总数', count: data.iterationProjectCount || 0 }
        ];
      }
    }
  } catch (error) {
    console.error('获取产品总览数据失败:', error);
  }
};

// 从后端获取产品年度推进统计数据
const fetchProductProgress = async () => {
  try {
    const response = await request.get('/dashboard/product-progress', {
      params: { year: selectedYear.value }
    });
    if (response.data.code === 200) {
      const data = response.data.data;
      tuiJinCount.value = [
        { name: '已完成发布数', count: data.completedReleaseCount || 0 },
        { name: '已完成项目数', count: data.completedNeedCount || 0 },
        { name: '已完成Bug数', count: data.completedBugCount || 0 }
      ];
    }
  } catch (error) {
    console.error('获取产品年度推进统计数据失败:', error);
  }
};

// 从后端获取未关闭的产品列表数据
const fetchUnclosedProducts = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('调用产品列表接口，用户名:', user.username);
      const response = await request.get('/dashboard/unclosed-products', {
        params: { username: user.username }
      });
      console.log('产品列表接口返回:', response);
      if (response.data.code === 200) {
        unclosedProducts.value = response.data.data || [];
        console.log('产品列表数据:', unclosedProducts.value);
      }
    }
  } catch (error) {
    console.error('获取未关闭的产品列表数据失败:', error);
  }
};

// 从后端获取产品发布列表数据
const fetchProductReleases = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get('/dashboard/product-releases', {
        params: { username: user.username }
      });
      if (response.data.code === 200) {
        productReleases.value = response.data.data || [];
      }
    }
  } catch (error) {
    console.error('获取产品发布列表数据失败:', error);
  }
};

// 产品开发周期分布图表
const {
  chartRef: faBuDom,
  initChart: initFaBuEchart,
  updateChart: updateFaBuEchart
} = useEcharts();
let faBuInited = false;

// 存储从后端获取的产品数据
const productDataByPeriod = ref({
  '3个月以内': [],
  '3-6个月': [],
  '6-12个月': [],
  '12个月以上': []
});

const handlePeriodClick = (params) => {
  const period = params.name;
  selectedPeriodProducts.value = productDataByPeriod.value[period] || [];
};

// 跳转到产品列表并筛选显示
const goToProductList = (productName) => {
  router.push(`/ProductResearch/ProductList?productName=${encodeURIComponent(productName)}`);
};

const initFaBuChart = async () => {
  if (!faBuDom.value) return;
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let params = {};
    if (userStr) {
      const user = JSON.parse(userStr);
      params = { username: user.username };
    }
    
    const response = await request.get('/dashboard/product-development-cycle', { params });
    if (response.data.code === 200) {
      const data = response.data.data;
      
      // 更新产品数据
      if (data.productsByCycle) {
        productDataByPeriod.value = data.productsByCycle;
      }
      
      const option = {
        title: {
          text: '产品开发周期分布',
          left: 'center',
          textStyle: {
            fontSize: 14,
            fontWeight: 'normal'
          }
        },
        tooltip: {
          trigger: 'item',
          formatter: '{a} <br/>{b}: {c} ({d}%)'
        },
        legend: {
          orient: 'vertical',
          left: 'left',
          data: ['3个月以内', '3-6个月', '6-12个月', '12个月以上']
        },
        series: [
          {
            name: '开发周期',
            type: 'pie',
            radius: '50%',
            data: data.pieData || [
              { value: 0, name: '3个月以内' },
              { value: 0, name: '3-6个月' },
              { value: 0, name: '6-12个月' },
              { value: 0, name: '12个月以上' }
            ],
            emphasis: {
              itemStyle: {
                shadowBlur: 10,
                shadowOffsetX: 0,
                shadowColor: 'rgba(0, 0, 0, 0.5)'
              }
            },
            itemStyle: {
              borderRadius: 10,
              borderColor: '#fff',
              borderWidth: 2
            }
          }
        ],
        toolbox: {
          feature: {
            saveAsImage: {}
          }
        }
      };

      if (faBuInited) updateFaBuEchart(option);
      else {
        initFaBuEchart(option, (chart) => {
          // 绑定点击事件
          chart.on('click', handlePeriodClick);
        });
        faBuInited = true;
      }
    }
  } catch (error) {
    console.error('获取产品开发周期分布数据失败:', error);
  }
};

// 项目与迭代关联分布图表
const rankingChart = ref(null);
const initRankingChart = async () => {
  if (!rankingChart.value) return;
  try {
    const response = await request.get('/dashboard/product-overview');
    if (response.data.code === 200) {
      const data = response.data.data;
      useEcharts(rankingChart.value, {
        title: { text: '项目与迭代关联分布' },
        xAxis: {
          type: 'category',
          data: ['有多个迭代的项目', '有单个迭代的项目', '无迭代的项目'],
          axisLabel: {
            fontSize: 11,
            rotate: 45
          }
        },
        yAxis: { type: 'value' },
        series: [{
          type: 'bar',
          data: [3, 2, 1],
          label: { show: true, position: 'top', fontSize: 11 }
        }]
      });
    }
  } catch (error) {
    console.error('获取项目与迭代关联分布数据失败:', error);
  }
};

// 初始化图表
const initCharts = async () => {
  await initFaBuChart();
};

// 在数据加载后初始化图表
onMounted(async () => {
  // 获取用户角色
  getUserRole();
  await fetchProductOverview();
  await fetchProductProgress();
  await fetchUnclosedProducts();
  await fetchProductReleases();
  await initCharts();
});

// 当选择年份变化时重新获取数据和初始化图表
const handleYearChange = async () => {
  await fetchProductProgress();
  await initCharts();
};
</script>

<style scoped>
.tou{
  display: flex;
  background-color: #fff;
}
.left{
  width: 60%;
}
.right{
  width: 40%;
}
.kuai{
  display: flex;
  align-items: center;
  justify-content: center;
  width: 100%;
  height: 100px;
  text-align: center;
}
.kuai span{
  flex: 1;
}
.kuai span div:first-child {
  font-size: 14px;
  color: #606266;
  margin-bottom: 5px;
}
.kuai span div {
  font-size: 18px;
  font-weight: bold;
  color: #409EFF;
}
h3{
  padding: 10px;
}

.comment{
  display: flex;
}

.bangDan {
  margin-top: 20px;
}

.bangDan h4 {
  margin: 0 0 15px 0;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.ranking-item {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 13px;
}

.rank {
  width: 20px;
  text-align: center;
  font-weight: bold;
  color: #303133;
}

.product-name {
  flex: 1;
  font-size: 12px;
  color: #606266;
}

.progress-container {
  flex: 2;
  height: 8px;
  background-color: #f0f0f0;
  border-radius: 4px;
  overflow: hidden;
}

.progress-bar {
  height: 100%;
  background-color: #409EFF;
  border-radius: 4px;
  transition: width 0.3s ease;
}

.count {
  width: 30px;
  text-align: right;
  color: #303133;
  font-weight: 500;
}

/* 为不同排名设置不同颜色 */
.ranking-item:nth-child(1) .rank {
  color: #F56C6C;
}

.ranking-item:nth-child(2) .rank {
  color: #E6A23C;
}

.ranking-item:nth-child(3) .rank {
  color: #409EFF;
}

.ranking-item:nth-child(1) .progress-bar {
  background-color: #F56C6C;
}

.ranking-item:nth-child(2) .progress-bar {
  background-color: #E6A23C;
}

.ranking-item:nth-child(3) .progress-bar {
  background-color: #409EFF;
}

.ranking-item:nth-child(4) .progress-bar,
.ranking-item:nth-child(5) .progress-bar {
  background-color: #67C23A;
}

/* 产品列表样式 */
.product-list {
  margin-top: 10px;
  padding: 0 10px;
}

.product-item {
  display: flex;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f0f0f0;
}

.product-item:last-child {
  border-bottom: none;
}

.product-index {
  width: 20px;
  font-weight: bold;
  margin-right: 10px;
  color: #409EFF;
}

.product-name {
  flex: 1;
  font-size: 13px;
  color: #303133;
}

.no-data {
  text-align: center;
  padding: 20px 0;
  color: #909399;
  font-size: 13px;
}

.product-statistics{
  display: flex;
  align-items: flex-start;
  gap:20px;
  padding: 10px 0;
}

.product-list {
  flex-shrink: 0;
  width: 150px;
  border-right: 1px solid #ebeef5;
  padding-right: 15px;
}

.product-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.product-list li {
  padding: 8px 0;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.3s;
  border-radius: 4px;
  padding-left: 10px;
}

.product-list li:hover {
  color: #409EFF;
  background-color: #ecf5ff;
}

.product-list li.active {
  color: #409EFF;
  font-weight: 500;
  background-color: #ecf5ff;
}

.product-statistics-content {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1.2fr 1fr;
  gap: 20px;
}

.delivery-rate, .needs-statistics, .latest-progress {
  padding: 15px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.delivery-rate h4, .needs-statistics h4, .latest-progress h4 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.delivery-rate {
  text-align: center;
}

.delivery-rate .el-progress {
  width: 120px !important;
  height: 120px !important;
  margin: 0 auto 15px;
}

.delivery-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
  flex-wrap: wrap;
  gap: 10px;
}

.delivery-stats span {
  font-size: 12px;
  color: #606266;
  display: inline-block;
  margin: 0 5px;
}

.stat-number {
  font-weight: bold;
  color: #303133;
  margin-left: 4px;
}

.needs-statistics {
  text-align: center;
}

.monthly-stats {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
  gap: 20px;
}

.monthly-stats span {
  font-size: 14px;
  color: #606266;
}

.latest-progress {
  padding: 15px;
}

.latest-task, .latest-release {
  margin-bottom: 20px;
}

.latest-task p, .latest-release p {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.task-item, .release-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.task-name, .release-name {
  font-size: 12px;
  color: #409EFF;
  text-decoration: none;
  flex: 1;
  margin-right: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-name:hover, .release-name:hover {
  text-decoration: underline;
}

.task-status, .release-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
  flex-shrink: 0;
}

.task-status {
  background-color: #f0f0f0;
  color: #909399;
}

.release-status {
  background-color: #f0f9eb;
  color: #67C23A;
}
.unclosed-product-list {
  margin-top: 10px;
}

.project-link {
  color: #409EFF;
  text-decoration: none;
  font-size: 13px;
  display: block;
  text-align: center;
}

.project-link:hover {
  text-decoration: underline;
}

.completion-rate {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
}

.el-table .cell {
  font-size: 13px;
  padding: 10px;
  text-align: center !important;
  vertical-align: middle !important;
}

.el-table th {
  font-size: 13px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 10px;
  text-align: center !important;
  vertical-align: middle !important;
}

.el-table__row {
  height: 36px !important;
}

.el-table--border th {
  border-bottom: 1px solid #ebeef5;
}

.el-table--border td {
  border-bottom: 1px solid #ebeef5;
  vertical-align: middle !important;
}

.completion-rate .el-progress {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  width: 40px !important;
  height: 40px !important;
}

.el-progress {
  position: relative;
  display: inline-block;
}

.el-progress__text {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 11px !important;
  margin: 0;
  z-index: 1;
}

.el-progress__circle {
  display: flex;
  justify-content: center;
  align-items: center;
}

.product-release-list {
  margin-top: 10px;
}

.status-tag {
  display: inline-block;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
  background-color: #f0f9eb;
  color: #67C23A;
}

.product-release-list .project-link {
  color: #409EFF;
  text-decoration: none;
  font-size: 13px;
  display: block;
  text-align: left;
}

.product-release-list .project-link:hover {
  text-decoration: underline;
}

.product-release-list .el-table .cell {
  font-size: 13px;
  padding: 10px;
  text-align: left;
  vertical-align: middle;
}

.product-release-list .el-table th {
  font-size: 13px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 10px;
  text-align: left;
  vertical-align: middle;
}

.product-release-list .el-table__row {
  height: 36px !important;
}

.el-table--border th {
  border-bottom: 1px solid #ebeef5;
}

.el-table--border td {
  border-bottom: 1px solid #ebeef5;
}

/* 标签页样式 */
.tab-header {
  display: flex;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 15px;
}

.tab-item {
  padding: 10px 20px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-bottom: 2px solid transparent;
  transition: all 0.3s;
}

.tab-item:hover {
  color: #409EFF;
}

.tab-item.active {
  color: #409EFF;
  border-bottom-color: #409EFF;
  font-weight: 500;
}

.tab-content {
  min-height: 300px;
}

/* 产品详情弹窗样式 */
.product-detail-header {
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 20px;
}

.product-detail-name {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin: 0;
}

.product-detail-body {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.product-basic-info h4,
.product-projects h4 {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.basic-info-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 13px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #303133;
}

.project-list-container {
  background-color: #fafafa;
  border-radius: 4px;
  padding: 8px;
}

.project-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background-color: #fff;
  border-radius: 4px;
  cursor: pointer;
  transition: background-color 0.2s;
}

.project-item-row:hover {
  background-color: #f5f7fa;
}

.project-item-row:last-child {
  margin-bottom: 0;
}

.project-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.project-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.project-iteration {
  font-size: 12px;
  color: #909399;
}

.project-meta {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 4px;
}

.project-manager {
  font-size: 13px;
  color: #606266;
}

.project-team {
  font-size: 12px;
  color: #909399;
}

.no-projects {
  padding: 40px;
  text-align: center;
  color: #909399;
  background-color: #fff;
  border-radius: 4px;
}

.product-iterations h4 {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 16px;
  padding-bottom: 8px;
  border-bottom: 1px solid #ebeef5;
}

.iteration-list-container {
  background-color: #fafafa;
  border-radius: 4px;
  padding: 8px;
}

.iteration-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 16px;
  margin-bottom: 8px;
  background-color: #fff;
  border-radius: 4px;
  transition: background-color 0.2s;
}

.iteration-item-row:last-child {
  margin-bottom: 0;
}

.iteration-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.iteration-title {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.no-data {
  padding: 40px;
  text-align: center;
  color: #909399;
  background-color: #fff;
  border-radius: 4px;
}
</style>