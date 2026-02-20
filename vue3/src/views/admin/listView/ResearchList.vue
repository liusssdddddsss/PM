<template>
  <el-table
      :data="tableData"
      style="width: 100%"
      class="ResearchTable"
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
import {ref} from "vue";

//ddl列表
const tableData = ref([
  {
    id:1,
    title:'ash丢啊复',
    priority:'紧急',
    state:'进行中',
    category:'功能',
    finishTime:'2029-09-09 12:00'
  }
])
</script>

<style scoped>

</style>