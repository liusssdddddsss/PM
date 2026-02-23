<template>
  <el-table
      :data="tableData"
      style="width: 100%"
  >
    <el-table-column prop="id" label="序号" width="80"></el-table-column>
    <el-table-column prop="title" label="标题" width="450"></el-table-column>
    <el-table-column prop="type" label="审批类型" width="150"></el-table-column>
    <el-table-column prop="submitTime" label="提交时间" width="180"></el-table-column>
    <el-table-column prop="status" label="状态" width="120"></el-table-column>
  </el-table>
</template>

<script setup>
import {ref} from "vue";

//ddl列表
const tableData = ref([
  {
    id: 1,
    title: '家长端，界面优化调整，新增功能：授权监控设备查看',
    type: '研发需求',
    submitTime: '2023-08-08 12:12',
    status: '审批中'
  },
  {
    id: 2,
    title: '班牌PC端管理界面调整，样式统一，菜单归类',
    type: '用户需求',
    submitTime: '2023-08-08 12:12',
    status: '审批中'
  },
  {
    id: 3,
    title: '班牌模板调整，参考海康，增加竖版',
    type: '研发需求',
    submitTime: '2023-08-08 12:12',
    status: '审批中'
  }
])
</script>

<style scoped>

</style>