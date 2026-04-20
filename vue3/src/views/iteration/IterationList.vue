<template>
  <!--  迭代列表-->
  <div class="iterations-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="activeTab=tab.type"
      >
        {{tab.name}}
        <span class="count">{{tab.count}}</span>
      </span>
      <div class="search-add-container">
        <div class="search-box">
          <el-input
              v-model="searchQuery"
              placeholder="搜索迭代名称"
              size="small"
              class="search-input"
          />
        </div>
        <div class="addIteration">
          <el-button class="button" @click="goToAddIteration">
            添加迭代
          </el-button>
        </div>
      </div>
    </div>
    <div class="list">
      <el-table
          :data="paginatedData"
          style="width: 100%"
          class="IterationTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
      >
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="name" label="迭代名称" min-width="180">
          <template #default="scope">
            <span class="iteration-name">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200">
          <template #default="scope">
            <span>{{ scope.row.description }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="所属项目" width="180">
          <template #default="scope">
            <span>{{ scope.row.projectName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始时间" width="130"></el-table-column>
        <el-table-column prop="endDate" label="预计完成时间" width="130"></el-table-column>
        <el-table-column prop="progress" label="进度" width="100">
          <template #default="scope">
            <el-progress type="circle" :percentage="scope.row.progress" :width="20" :stroke-width="3" />
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope">
            <span :class="getStateClass(scope.row.status)">{{ getStatusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <span v-if="scope.row.status !== 2" class="action-text close-action" @click="handleClose(scope.row)">关闭</span>
            <span v-else class="action-text open-action" @click="handleOpen(scope.row)">打开</span>
            <span class="action-text edit-action" @click="handleEdit(scope.row.id)">编辑</span>
            <span class="action-text delete-action" @click="handleDelete(scope.row.id)">删除</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
    
    <div class="pagination">
      <span>共 {{ total }} 项</span>
      <el-pagination
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          style="margin-right: 10px"
      />
    </div>
  </div>
</template>

<script setup>

import {ref, computed, onMounted} from "vue";
import {useRouter} from "vue-router";
import request from '@/utils/request.js';

const tabs = ref([
  {name:'全部',type:'all'},
  {name:'进行中',type:'ing'},
  {name:'未开始',type:'noBegin'},
  {name:'已关闭',type:'close'},
]);
const activeTab=ref('all');
const searchQuery=ref('');
const iterations = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);

const router =useRouter();
const goToIterationEdit = () =>{
  router.push('/iteration/iterationEdit');
}

// 跳转到添加迭代页面
const goToAddIteration = () => {
  router.push('/iteration/iterationEdit');
};

// 项目列表
const projects = ref([]);

// 获取项目列表
const fetchProjects = async () => {
  try {
    const response = await request.get('/workbench/projects', {
      params: { username: '202201' }
    });
    if (response.data.code === 200) {
      projects.value = response.data.data;
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

// 获取迭代数据
const fetchIterations = async () => {
  try {
    // 先获取项目列表
    await fetchProjects();
    
    const response = await request.get('/iteration/list');
    if (response.data.code === 200) {
      // 更新迭代列表
      iterations.value = response.data.data;
      
      // 为每个迭代设置项目名称和进度（如果不存在）
      iterations.value.forEach(iteration => {
        // 根据 projectId 获取项目名称
        if (iteration.projectId) {
          const project = projects.value.find(p => p.id === iteration.projectId);
          if (project) {
            iteration.projectName = project.projectName;
          } else {
            iteration.projectName = '未知项目';
          }
        } else {
          iteration.projectName = '未指定项目';
        }
        // 如果没有进度，设置为0
        if (iteration.progress === undefined || iteration.progress === null) {
          iteration.progress = 0;
        }
      });
      total.value = iterations.value.length;
    }
  } catch (error) {
    console.error('获取迭代列表失败:', error);
  }
};

// 初始加载数据
onMounted(() => {
  fetchIterations();
});

// 根据当前标签和搜索词过滤数据
const filteredData = computed(() => {
  let result = iterations.value;
  
  // 首先根据标签过滤
  if (activeTab.value === 'ing') {
    result = result.filter(item => item.status === 1);
  } else if (activeTab.value === 'noBegin') {
    result = result.filter(item => item.status === 0);
  } else if (activeTab.value === 'close') {
    result = result.filter(item => item.status === 2);
  }
  
  // 然后根据搜索词过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(item => 
      item.name.toLowerCase().includes(query) ||
      item.projectName.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// 分页后的迭代列表
const paginatedData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredData.value.slice(start, end);
});

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

// 处理页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
};

// 获取状态文本
const getStatusText = (status) => {
  if (status === 0) {
    return '未开始';
  } else if (status === 1) {
    return '进行中';
  } else if (status === 2) {
    return '已关闭';
  }
  return '';
};

// 获取状态标签的类名
const getStateClass = (status) => {
  if (status === 1) {
    return 'status-in-progress';
  } else if (status === 0) {
    return 'status-scheduled';
  } else if (status === 2) {
    return 'status-closed';
  }
  return '';
};

// 处理操作
const handleClose = async (iteration) => {
  try {
    console.log('关闭迭代:', iteration);
    // 确保id是数字类型
    const iterationId = parseInt(iteration.id);
    console.log('迭代ID:', iterationId);
    
    // 发送请求关闭迭代，包含所有必要字段
    const response = await request.put(`/iteration/update`, {
      id: iterationId,
      name: iteration.name,
      description: iteration.description,
      projectId: iteration.projectId,
      startDate: iteration.startDate,
      endDate: iteration.endDate,
      status: 2 // 2表示已关闭
    });
    console.log('关闭迭代响应:', response);
    if (response.data.code === 200) {
      console.log('关闭迭代成功:', iterationId);
      // 重新获取迭代列表
      await fetchIterations();
    }
  } catch (error) {
    console.error('关闭迭代失败:', error);
    console.error('错误详情:', error.response);
  }
};

// 处理打开迭代
const handleOpen = async (iteration) => {
  try {
    console.log('打开迭代:', iteration);
    // 确保id是数字类型
    const iterationId = parseInt(iteration.id);
    console.log('迭代ID:', iterationId);
    
    // 发送请求打开迭代，包含所有必要字段
    const response = await request.put(`/iteration/update`, {
      id: iterationId,
      name: iteration.name,
      description: iteration.description,
      projectId: iteration.projectId,
      startDate: iteration.startDate,
      endDate: iteration.endDate,
      status: 1 // 1表示进行中
    });
    console.log('打开迭代响应:', response);
    if (response.data.code === 200) {
      console.log('打开迭代成功:', iterationId);
      // 重新获取迭代列表
      await fetchIterations();
    }
  } catch (error) {
    console.error('打开迭代失败:', error);
    console.error('错误详情:', error.response);
  }
};

const handleEdit = (id) => {
  router.push(`/iteration/iterationEdit?id=${id}`);
};

const handleDelete = async (id) => {
  try {
    const response = await request.delete(`/iteration/delete/${id}`);
    if (response.data.code === 200) {
      console.log('删除迭代成功:', id);
      // 重新获取迭代列表
      await fetchIterations();
    }
  } catch (error) {
    console.error('删除迭代失败:', error);
  }
};
</script>

<style scoped>
.iterations-comment{
  background-color: white;
  height: 100vh;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
.option{
  height: 40px;
  //padding: 10px 0;
  //margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}
.option span{
  display: inline-block;
  padding: 0 10px;
  text-align: center;
  //margin-right: 10px;
  cursor: pointer;
  transition: all 0.3s;
}
.option span.active{
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
  font-weight: 500;
}
.count {
  margin-left: 5px;
  font-size: 12px;
  color: #909399;
}
.search-add-container {
  display: inline-block;
  float: right;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box {
  margin-right: 10px;
}

.search-input {
  width: 200px;
}

.addIteration{
  display: inline-block;
}
.button{
  background-color: #238EFF;
  color: #fff;
  margin-left: 0;
}

.IterationTable {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
}

.iteration-name {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.iteration-name:hover {
  text-decoration: underline;
}

.status-in-progress {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.status-scheduled {
  color: #909399;
  font-weight: 500;
  font-size: 13px;
}

.status-closed {
  color: #F56C6C;
  font-weight: 500;
  font-size: 13px;
}

.action-text {
  display: inline-block;
  margin: 0 8px;
  cursor: pointer;
  font-size: 13px;
  transition: color 0.3s;
}

.close-action {
  color: #409EFF;
}

.open-action {
  color: #67C23A;
}

.edit-action {
  color: #E6A23C;
}

.delete-action {
  color: #F56C6C;
}

.action-text:hover {
  text-decoration: underline;
}

.el-table .cell {
  font-size: 12px;
  text-align: center;
  vertical-align: middle;
  line-height: 1.2;
}

.el-table th {
  font-size: 12px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 4px 12px;
  text-align: center;
  vertical-align: middle;
  height: 28px !important;
}

.el-table__row {
  height: 28px !important;
  line-height: 28px !important;
}

.el-table--border th {
  border: none !important;
}

.el-table--border td {
  border: none !important;
  vertical-align: middle;
}

.el-progress {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.el-progress__text {
  font-size: 10px !important;
  margin: 0;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
