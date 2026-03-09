<template>
  <el-table
      :data="tableData"
      style="width: 100%"
  >
    <el-table-column prop="id" label="序号" width="80"></el-table-column>
    <el-table-column prop="title" label="标题" width="300"></el-table-column>
    <el-table-column prop="comment" label="审批内容" width="300"></el-table-column>
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
    if (response.code === 200) {
      // 转换数据格式以匹配前端组件
      tableData.value = response.data.map(item => ({
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
  color: red;
  font-weight: 500;
}
</style>