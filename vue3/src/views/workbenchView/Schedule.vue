<template>
  <div class="title">
    <h3>工作日程</h3>
  </div>
  <div class="ScheduleComment">
    <div class="scheduleLeft">
      <el-calendar v-model="value" :controller-type="controllerType" />
    </div>
    <div class="scheduleRight">
      <div class="option">
        <span
            :class="{active:activeTab === 'task'}"
            @click="activeTab = 'task'"
        >任务</span>
        <span
            :class="{active:activeTab === 'bug'}"
            @click="activeTab = 'bug'"
        >Bug</span>
      </div>
      <div class="OptionList">
        <TaskList
            v-if="activeTab ==='task'"
        />
        <BugList
            v-if="activeTab === 'bug'"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref} from "vue";
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import BugList from "@/views/workbenchView/listView/BugList.vue";

const value =ref(new Date());
const activeTab = ref("task");
</script>

<style scoped>
.ScheduleComment {
  margin-top:10px;
  display: flex;
  gap: 10px;
}
.scheduleLeft {
  width: 70%;
}
.scheduleRight {
  width: 30%;
  background-color: white;
}
.option{
  width: 100%;
  height: 30px;
  margin-top: 20px;
  margin-left: 10px;
}
.option span{
  display: inline-block;
  width: 50px;
  text-align: center;
}
.option span.active{
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
}
</style>