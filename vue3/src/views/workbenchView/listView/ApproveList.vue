<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="tableData"
          style="width: 100%"
          class="TaskTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
      >
    <el-table-column label="序号" width="80">
      <template #default="scope">
        {{ scope.$index + 1 }}
      </template>
    </el-table-column>
    <el-table-column prop="title" label="标题" width="300">
      <template #default="scope">
        <span class="task-name">{{ scope.row.title }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="comment" label="审批内容" width="300">
      <template #default="scope">
        <span class="task-name">{{ scope.row.comment }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="type" label="审批类型" width="150"></el-table-column>
    <el-table-column prop="approver" label="审批人" width="150"></el-table-column>
    <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
    <el-table-column label="状态" width="120">
      <template #default="scope">
        <span :class="{ 'status-rejected': scope.row.status === '已退回' }">
          {{ scope.row.status }}
        </span>
      </template>
    </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";
import request from "@/utils/request.js";

//ddl列表
const tableData = ref([]);

// 从后端获取审批列表数据
onMounted(() => {
  fetchApprovals();
});

const fetchApprovals = async () => {
  try {
    const response = await request.get('/workbench/approvals');
    console.log('获取审批列表响应:', response);
    if (response.data.code === 200) {
      // 转换数据格式以匹配前端组件
      tableData.value = response.data.data.map(item => ({
        id: item.id,
        title: item.project_name,
        comment: item.comment || '',
        type: item.type || '',
        approver: item.approver_name,
        submitTime: item.created_at,
        status: item.action === '通过' ? '已通过' : item.action === '退回' ? '已退回' : item.action || ''
      }));
      console.log('转换后的审批列表数据:', tableData.value);
    }
  } catch (error) {
    console.error('获取审批列表失败:', error);
  }
};
</script>

<style scoped>
.status-rejected {
  color: #F56C6C;
  font-weight: 500;
  font-size: 13px;
}

.task-name {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.priority-urgent {
  color: #F56C6C;
  font-weight: 500;
  font-size: 13px;
}

.priority-normal {
  color: #E6A23C;
  font-weight: 500;
  font-size: 13px;
}

.priority-regular {
  color: #67C23A;
  font-weight: 500;
  font-size: 13px;
}

.status-in-progress {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.status-completed {
  color: #67C23A;
  font-weight: 500;
  font-size: 13px;
}

.task-table-container {
  padding: 0;
  background-color: #fff;
  border-radius: 0;
  box-shadow: none;
  overflow-x: auto;
}

.table-container {
  width: 100%;
  min-width: 800px;
}

.TaskTable {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
}

.el-table th {
  background-color: #f9f9f9;
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
</style>