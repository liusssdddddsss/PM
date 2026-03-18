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
      <div class="addIteration">
        <el-button class="button">
          添加迭代
        </el-button>
      </div>
    </div>
    <div class="list">
      <el-table
          :data="filteredData"
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
        <el-table-column prop="projectName" label="所属项目" width="180">
          <template #default="scope">
            <span>{{ scope.row.projectName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="start_date" label="开始时间" width="130"></el-table-column>
        <el-table-column prop="end_date" label="预计完成时间" width="130"></el-table-column>
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
            <span class="action-text close-action" @click="handleClose(scope.row)">关闭</span>
            <span class="action-text edit-action" @click="goToIterationEdit">编辑</span>
            <span class="action-text delete-action" @click="handleDelete(scope.row.id)">删除</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>

import {ref, computed, onMounted} from "vue";
import {useRouter} from "vue-router";
import axios from 'axios';

const tabs = ref([
  {name:'全部',type:'all',count:16},
  {name:'进行中',type:'ing',count:9},
  {name:'未开始',type:'noBegin',count:5},
  {name:'已关闭',type:'close',count:2},
]);
const activeTab=ref('all');
const iterations = ref([]);

const router =useRouter();
const goToIterationEdit = () =>{
  router.push('/iteration/iterationEdit');
}

// 获取迭代数据
const fetchIterations = async () => {
  try {
    const response = await axios.get('http://localhost:9090/iteration/list');
    if (response.data.code === 200) {
      iterations.value = response.data.data;
      // 模拟项目名称数据
      iterations.value.forEach(iteration => {
        // 这里应该根据project_id从项目表获取项目名称
        // 暂时使用模拟数据
        const projectNames = ['智慧教室_智慧云盘', '实践教学管理平台', '电子班牌管理系统', '智慧校园(中学版)', '宿舍管理系统'];
        iteration.projectName = projectNames[Math.floor(Math.random() * projectNames.length)];
        // 模拟进度数据
        iteration.progress = Math.floor(Math.random() * 101);
      });
    }
  } catch (error) {
    console.error('获取迭代列表失败:', error);
  }
};

// 初始加载数据
onMounted(() => {
  fetchIterations();
});

// 根据当前标签过滤数据
const filteredData = computed(() => {
  if (activeTab.value === 'all') {
    return iterations.value;
  } else if (activeTab.value === 'ing') {
    return iterations.value.filter(item => item.status === 1);
  } else if (activeTab.value === 'noBegin') {
    return iterations.value.filter(item => item.status === 0);
  } else if (activeTab.value === 'close') {
    return iterations.value.filter(item => item.status === 2);
  }
  return iterations.value;
});

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
const handleClose = (iteration) => {
  console.log('关闭迭代:', iteration);
};

const handleEdit = (id) => {
  router.push(`/iteration/iterationEdit?id=${id}`);
};

const handleDelete = (id) => {
  console.log('删除迭代:', id);
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
.addIteration{
  display: inline-block;
  float: right;
}
.button{
  background-color: #238EFF;
  color: #fff;
  margin-left: 10px;
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
</style>
