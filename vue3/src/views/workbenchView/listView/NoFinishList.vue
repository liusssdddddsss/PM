<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="tableData"
          style="width: 100%"
          class="NoFinishTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
      >
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="项目名称" min-width="80">
          <template #default="scope">
            <span class="task-name" @click="handleProjectClick(scope.row.title)">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="person" label="负责人" width="70"></el-table-column>
        <el-table-column prop="states" label="状态" width="70">
          <template #default="scope">
            <span class="status-tag" :class="scope.row.states === '进行中' ? 'status-in-progress' : 'status-scheduled'">
              {{ scope.row.states }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="workTime" label="总计工时" width="80"></el-table-column>
        <el-table-column prop="shengYuTask" label="剩余任务" width="80"></el-table-column>
        <el-table-column prop="shengYuBug" label="剩余Bug" width="80"></el-table-column>
        <el-table-column prop="finishTime" label="计划完成" width="100">
          <template #default="scope">
            {{ formatDate(scope.row.finishTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="jinDu" label="进度" width="70">
          <template #default="scope">
            <el-progress type="circle" :percentage="parseInt(scope.row.jinDu)" :width="20" :stroke-width="3" />
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";
import request from "@/utils/request.js";

// 定义自定义事件，用于向父组件传递项目名称
const emit = defineEmits(['project-click']);

//ddl列表
const tableData = ref([]);

// 从后端获取未完成项目列表
const fetchUnfinishedProjects = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    const user = userStr ? JSON.parse(userStr) : null;
    const username = user ? user.username : '';
    
    const response = await request.get(`/workbench/unfinished-projects?username=${username}`);
    if (response.data.code === 200) {
      tableData.value = response.data.data;
    }
  } catch (error) {
    console.error('获取未完成项目列表失败:', error);
  }
};

// 处理项目点击事件
const handleProjectClick = (projectName) => {
  console.log('点击了项目:', projectName);
  // 触发自定义事件，将项目名称传递给父组件
  emit('project-click', projectName);
};

// 格式化日期，只显示到日期部分
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return '';
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

// 页面加载时获取未完成项目列表
onMounted(() => {
  fetchUnfinishedProjects();
});
</script>

<style scoped>
.task-table-container {
  padding: 0;
  background-color: #fff;
  border-radius: 0;
  box-shadow: none;
  overflow-x: auto;
  max-height: 400px;
  overflow-y: auto;
}

.table-container {
  width: 100%;
  min-width: 800px;
}

.NoFinishTable {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
}

.task-name {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.status-in-progress {
  background-color: #fff7e6;
  color: #e6a23c;
}

.status-tag.status-scheduled {
  background-color: #f0f0f0;
  color: #909399;
}

.el-table th {
  font-size: 12px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 4px 12px;
  text-align: center;
  vertical-align: middle;
}

.el-table--border th {
  border: none !important;
}

.el-table--border td {
  border: none !important;
  vertical-align: middle;
}

.el-table .cell {
  text-align: center;
  vertical-align: middle;
}
</style>