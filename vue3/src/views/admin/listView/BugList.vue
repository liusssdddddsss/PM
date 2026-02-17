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
import {ref} from "vue";

//ddl列表
const tableData = ref([
  {
    id:1,
    title:'ash丢啊复古风',
    priority:'紧急',
    state:'进行中',
    sure:'已确认',
    finishTime:'2029-09-09 12:00'
  }
])
</script>

<style scoped>

</style>