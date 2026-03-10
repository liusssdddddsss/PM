<template>
  <el-table
      :data="tableData"
      style="width: 100%;height: 400px"
      class="NoFinishTable"
      :header-cell-style="{backgroundColor: '#f5f7fa', color: '#303133', fontSize: '14px', fontWeight: '500'}"
      :row-style="{height: '20px'}"
      :cell-style="{ padding: '0px',lineHeight: '20px'}"
  >
    <el-table-column prop="title" label="项目名称" width="180">
      <template #default="scope">
        <a class="project-name">{{ scope.row.title }}</a>
      </template>
    </el-table-column>
    <el-table-column prop="person" label="负责人" width="80"></el-table-column>
    <el-table-column prop="states" label="状态" width="80">
      <template #default="scope">
        <span class="status-tag" :class="scope.row.states === '进行中' ? 'in-progress' : 'scheduled'">{{ scope.row.states }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="workTime" label="总计工时" width="120"></el-table-column>
    <el-table-column prop="shengYuNeeds" label="剩余需求" width="120"></el-table-column>
    <el-table-column prop="shengYuTask" label="剩余任务" width="120"></el-table-column>
    <el-table-column prop="shengYuBug" label="剩余Bug" width="120"></el-table-column>
    <el-table-column prop="finishTime" label="计划完成" width="110"></el-table-column>
    <el-table-column prop="jinDu" label="进度" width="80">
      <template #default="scope">
        <div class="progress-circle">
          <span class="progress-text">{{ scope.row.jinDu }}</span>
        </div>
      </template>
    </el-table-column>
  </el-table>
</template>

<script setup>
import {ref, onMounted} from "vue";
import request from "@/utils/request.js";

//ddl列表
const tableData = ref([]);

// 从后端获取未完成项目列表
const fetchUnfinishedProjects = async () => {
  try {
    const response = await request.get('/workbench/unfinished-projects');
    if (response.code === 200) {
      tableData.value = response.data;
    }
  } catch (error) {
    console.error('获取未完成项目列表失败:', error);
  }
};

// 页面加载时获取未完成项目列表
onMounted(() => {
  fetchUnfinishedProjects();
});
</script>

<style scoped>
.NoFinishTable {
  border-radius: 8px;
  overflow: hidden;
}

.project-name {
  color: #409EFF;
  text-decoration: none;
  font-weight: 500;
}

.project-name:hover {
  text-decoration: underline;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.in-progress {
  background-color: #fff7e6;
  color: #e6a23c;
}

.status-tag.scheduled {
  background-color: #f0f0f0;
  color: #909399;
}

.progress-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  position: relative;
}

.progress-text {
  font-size: 14px;
  font-weight: bold;
  color: #409EFF;
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
  height: 35px !important;
  line-height: 35px !important;
}

.el-table--border th {
  border: none !important;
}

.el-table--border td {
  border: none !important;
  vertical-align: middle;
}
</style>