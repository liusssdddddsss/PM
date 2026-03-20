<template>
  <div class="pending-container">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active: activeTab === tab.type}"
          @click="activeTab = tab.type"
      >
        {{tab.name}}
        <span class="tab-count">{{tab.count}}</span>
      </span>
    </div>
    <div class="list">
      <TaskList v-if="activeTab === 'task'"/>
      <ResearchList v-if="activeTab === 'rd'"/>
      <UserNeedList v-if="activeTab === 'user'"/>
      <BugList v-if="activeTab === 'bug'"/>
    </div>
  </div>
</template>

<script setup>
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import UserNeedList from "@/views/workbenchView/listView/UserNeedList.vue";
import BugList from "@/views/workbenchView/listView/BugList.vue";
import ResearchList from "@/views/workbenchView/listView/ResearchList.vue";
import {ref} from "vue";

const tabs = ref([
  {name:'任务',type:'task'},
  {name:'研发需求',type:'rd'},
  {name:'用户需求',type:'user'},
  {name:'Bug',type:'bug'}
]);
const activeTab = ref('task');
</script>

<style scoped>
.pending-container{
  background-color: white;
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.option{
  height: 40px;
  //padding: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.option span{
  display: inline-block;
  padding: 0 10px;
  text-align: center;
  //margin-right: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.option span.active{
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
  font-weight: 500;
}

.tab-count{
  margin-left: 6px;
  color: #606266;
  font-size: 12px;
  font-weight: normal;
}

.list {
  padding-left: 5px;
}
</style>