<template>
<!--  项目集-->
  <div class="projects-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="activeTab=tab.type"
      >
        {{tab.name}}
      </span>
      <div class="addProject">
<!--        <el-button @click="goToItemEdit">-->
<!--          编辑项目-->
<!--        </el-button>-->
        <el-button class="button">
          添加项目
        </el-button>
      </div>
    </div>
    <div class="list">
      <el-table
          :data="filteredData"
          style="width: 100%"
          class="ProjectTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
      >
        <el-table-column label="序号" width="80">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="项目名称" min-width="180">
          <template #default="scope">
            <span class="project-name">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="person" label="负责人" width="90"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="130"></el-table-column>
        <el-table-column prop="finishTime" label="预计完成时间" width="130"></el-table-column>
        <el-table-column prop="jinDu" label="进度" width="100">
          <template #default="scope">
            <el-progress type="circle" :percentage="scope.row.jinDu" :width="20" :stroke-width="3" />
          </template>
        </el-table-column>
        <el-table-column prop="states" label="状态" width="90">
          <template #default="scope">
            <span :class="getStateClass(scope.row.states)">{{ scope.row.states }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <span class="action-text close-action" @click="handleClose(scope.row)">关闭</span>
            <span class="action-text edit-action" @click="handleEdit(scope.row.id)">编辑</span>
            <span class="action-text delete-action" @click="handleDelete(scope.row.id)">删除</span>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>

import {ref, computed} from "vue";
import {useRouter} from "vue-router";
import request from "@/utils/request.js";

const tabs = ref([
  {name:'全部',type:'all'},
  {name:'进行中',type:'ing'},
  {name:'未开始',type:'noBegin'},
  {name:'已关闭',type:'close'},
]);
const activeTab=ref('all');

const router =useRouter();
const goToItemEdit = () =>{
  router.push('/itemSet/itemEdit');
}

// 项目数据
const projectData = ref([
  {id: 1, title: '智慧教室_智慧云盘', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 2, title: '实践教学管理平台', person: '李四', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 3, title: '电子班牌管理系统', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 4, title: '智慧校园(中学版)', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '已关闭'},
  {id: 5, title: '宿舍管理系统', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '已关闭'},
]);

// 根据当前标签过滤数据
const filteredData = computed(() => {
  if (activeTab.value === 'all') {
    return projectData.value;
  } else if (activeTab.value === 'ing') {
    return projectData.value.filter(item => item.states === '进行中');
  } else if (activeTab.value === 'noBegin') {
    return projectData.value.filter(item => item.states === '未开始');
  } else if (activeTab.value === 'close') {
    return projectData.value.filter(item => item.states === '已关闭');
  }
  return projectData.value;
});

// 获取状态标签的类名
const getStateClass = (state) => {
  if (state === '进行中') {
    return 'status-in-progress';
  } else if (state === '未开始') {
    return 'status-scheduled';
  } else if (state === '已关闭') {
    return 'status-closed';
  }
  return '';
};

// 处理操作
const handleClose = (project) => {
  console.log('关闭项目:', project);
};

const handleEdit = (id) => {
  router.push('/itemSet/itemEdit');
};

const handleDelete = async (id) => {
  try {
    // 尝试从数据库删除项目
    const response = await request.delete(`/itemSet/delete/${id}`);
    if (response.code === 200) {
      console.log('删除项目成功:', id);
      // 从本地数据中移除删除的项目
      projectData.value = projectData.value.filter(project => project.id !== id);
    }
  } catch (error) {
    console.error('删除项目失败:', error);
  }
};
</script>

<style scoped>
.projects-comment{
  background-color: white;
  height: 100vh;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
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

.addProject{
  display: inline-block;
  float: right;
}
.button{
  background-color: #238EFF;
  color: #fff;
  margin-left: 10px;
}

.ProjectTable {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
}

.project-name {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.project-name:hover {
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