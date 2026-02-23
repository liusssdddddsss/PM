<template>
  <el-table
      :data="tableData"
      style="width: 100%;height: 400px"
      class="NoFinishTable"
  >
    <el-table-column prop="title" label="项目名称" width="180"></el-table-column>
    <el-table-column prop="person" label="负责人" width="80"></el-table-column>
    <el-table-column prop="states" label="状态" width="80"></el-table-column>
    <el-table-column prop="workTime" label="总计工时" width="120"></el-table-column>
    <el-table-column prop="shengYuNeeds" label="剩余需求" width="120"></el-table-column>
    <el-table-column prop="shengYuTask" label="剩余任务" width="120"></el-table-column>
    <el-table-column prop="shengYuBug" label="剩余Bug" width="120"></el-table-column>
    <el-table-column prop="finish" label="计划完成" width="110"></el-table-column>
    <el-table-column prop="jinDu" label="进度" width="80"></el-table-column>
  </el-table>
</template>

<script setup>
import {ref} from "vue";

//ddl列表
const tableData = ref([
  {
    title:'智慧教室',
    person:'张大',
    states:'进行中',
    workTime:'180h',
    shengYuNeeds:18,
    shengYuTask:10,
    shengYuBug:23,
    finishTime:'2026-09-09',
    jinDu:25
  },
  {
    title:'智慧教室',
    person:'张大',
    states:'进行中',
    workTime:'180h',
    shengYuNeeds:18,
    shengYuTask:10,
    shengYuBug:23,
    finishTime:'2026-09-09',
    jinDu:25
  },
  {
    title:'智慧教室',
    person:'张大',
    states:'进行中',
    workTime:'180h',
    shengYuNeeds:18,
    shengYuTask:10,
    shengYuBug:23,
    finishTime:'2026-09-09',
    jinDu:25
  }
])
</script>

<style scoped>

</style>