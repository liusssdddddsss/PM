<template>
  <el-table
      :data="tableData"
      style="width: 100%;height: 400px"
      class="NoFinishTable"
      :header-cell-style="{backgroundColor: '#f5f7fa', color: '#303133', fontSize: '14px', fontWeight: '500'}"
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
import {ref} from "vue";

//ddl列表
const tableData = ref([
  {
    title:'智慧教室_智慧云盘',
    person:'张三三',
    states:'进行中',
    workTime:'160h',
    shengYuNeeds:102,
    shengYuTask:25,
    shengYuBug:15,
    finishTime:'2023-08-08',
    jinDu:25
  },
  {
    title:'实践教学管理平台',
    person:'李四四',
    states:'进行中',
    workTime:'1150h',
    shengYuNeeds:12,
    shengYuTask:36,
    shengYuBug:63,
    finishTime:'2023-08-08',
    jinDu:25
  },
  {
    title:'电子班牌管理系统',
    person:'张三三',
    states:'进行中',
    workTime:'320h',
    shengYuNeeds:56,
    shengYuTask:59,
    shengYuBug:72,
    finishTime:'2023-08-08',
    jinDu:25
  },
  {
    title:'智慧校园(中学版)',
    person:'王麻子',
    states:'已排期',
    workTime:'160h',
    shengYuNeeds:85,
    shengYuTask:86,
    shengYuBug:0,
    finishTime:'2023-08-08',
    jinDu:0
  },
  {
    title:'宿舍管理系统',
    person:'王麻子',
    states:'已排期',
    workTime:'78h',
    shengYuNeeds:20,
    shengYuTask:26,
    shengYuBug:0,
    finishTime:'2023-08-08',
    jinDu:0
  },
  {
    title:'教务考试系统',
    person:'胡一刀',
    states:'进行中',
    workTime:'300h',
    shengYuNeeds:28,
    shengYuTask:75,
    shengYuBug:152,
    finishTime:'2023-08-08',
    jinDu:25
  },
  {
    title:'在线试卷批改',
    person:'张三三',
    states:'进行中',
    workTime:'320h',
    shengYuNeeds:45,
    shengYuTask:102,
    shengYuBug:3,
    finishTime:'2023-08-08',
    jinDu:25
  },
  {
    title:'家校互通平台',
    person:'王麻子',
    states:'进行中',
    workTime:'160h',
    shengYuNeeds:13,
    shengYuTask:160,
    shengYuBug:9,
    finishTime:'2023-08-08',
    jinDu:25
  }
])
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
</style>