<template>
  <el-table
      :data="tableData"
      style="width: 100%"
      class="BudTable"
  >
    <el-table-column prop="id" label="序号" width="80"></el-table-column>
    <el-table-column prop="title" label="标题" width="320"></el-table-column>
    <el-table-column prop="priority" label="优先级" width="80"></el-table-column>
    <el-table-column prop="state" label="状态" width="90"></el-table-column>
    <el-table-column prop="sure" label="确认" width="90"></el-table-column>
    <el-table-column prop="finishTime" label="完成时间" width="180"></el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from "vue";
import request from "@/utils/request.js";

//ddl列表
const tableData = ref([]);

// 从后端获取Bug列表数据
onMounted(() => {
  fetchBugs();
});

const fetchBugs = async () => {
  try {
    const response = await request.get('/workbench/bugs');
    console.log('获取Bug列表响应:', response);
    if (response.code === 200) {
      // 转换数据格式以匹配前端组件
      tableData.value = response.data.map(item => ({
        id: item.id,
        title: item.title,
        priority: getPriorityText(item.priority),
        state: getStatusText(item.status),
        sure: item.confirmed ? '已确认' : '未确认',
        finishTime: item.due_date
      }));
      console.log('转换后的Bug列表数据:', tableData.value);
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
  }
};

// 获取优先级文本
const getPriorityText = (priority) => {
  switch (priority) {
    case 1:
      return '紧急';
    case 2:
      return '一般';
    case 3:
      return '正常';
    default:
      return '正常';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1:
      return '待处理';
    case 2:
      return '进行中';
    case 3:
      return '已修复';
    case 4:
      return '已关闭';
    default:
      return '待处理';
  }
};
</script>

<style scoped>

</style>