<template>
  <el-table
      :data="tableData"
      style="width: 100%"
      class="TaskTable"
  >
    <el-table-column prop="id" label="序号" width="80"></el-table-column>
    <el-table-column prop="title" label="标题" width="320"></el-table-column>
    <el-table-column prop="priority" label="优先级" width="80"></el-table-column>
    <el-table-column prop="state" label="状态" width="90"></el-table-column>
    <el-table-column prop="deadline" label="截止时间" width="180"></el-table-column>
    <el-table-column prop="workTime" label="工时" width="80"></el-table-column>
    <el-table-column prop="shengYu" label="剩余" width="80"></el-table-column>
    <el-table-column prop="jinDu" label="进度" width="80"></el-table-column>
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
    deadline:'2026-09-09',
    workTime:'24h',
    shengYu:'12h',
    jinDu:90,
  }
])
</script>

<style scoped>

</style>