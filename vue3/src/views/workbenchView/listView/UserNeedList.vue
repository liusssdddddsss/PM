<template>
  <el-table
      :data="tableData"
      style="width: 100%"
      class="UserNeedTable"
  >
    <el-table-column prop="id" label="序号" width="80"></el-table-column>
    <el-table-column prop="title" label="标题" width="400"></el-table-column>
    <el-table-column prop="priority" label="优先级" width="80"></el-table-column>
    <el-table-column prop="state" label="状态" width="90"></el-table-column>
    <el-table-column prop="category" label="类别" width="80"></el-table-column>
    <el-table-column prop="finishTime" label="完成时间" width="180"></el-table-column>
  </el-table>
</template>

<script setup>
import { ref, onMounted } from "vue";
import request from "@/utils/request.js";

//ddl列表
const tableData = ref([]);

// 从后端获取用户需求列表数据
onMounted(() => {
  fetchUserNeeds();
});

const fetchUserNeeds = async () => {
  try {
    const response = await request.get('/workbench/user-needs');
    console.log('获取用户需求列表响应:', response);
    if (response.code === 200) {
      // 转换数据格式以匹配前端组件
      tableData.value = response.data.map(item => ({
        id: item.id,
        title: item.title,
        priority: getPriorityText(item.priority),
        state: getStatusText(item.status),
        category: getCategoryText(item.type),
        finishTime: item.due_date
      }));
      console.log('转换后的用户需求列表数据:', tableData.value);
    }
  } catch (error) {
    console.error('获取用户需求列表失败:', error);
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
      return '已完成';
    default:
      return '待处理';
  }
};

// 获取类别文本
const getCategoryText = (type) => {
  switch (type) {
    case 1:
      return '研发需求';
    case 2:
      return '用户需求';
    case 3:
      return '业务需求';
    default:
      return '用户需求';
  }
};
</script>

<style scoped>

</style>