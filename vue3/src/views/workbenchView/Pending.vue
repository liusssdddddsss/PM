<template>
  <div class="pending-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active: activeTab === tab.type}"
          @click="activeTab = tab.type"
      >
        {{tab.name}}
        {{tab.count}}
      </span>
    </div>
    <div class="option-list">
      <TaskList v-if="activeTab === 'task'"/>
      <UserNeedList v-if="activeTab === 'user'"/>
      <BugList v-if="activeTab === 'bug'"/>
    </div>
  </div>
</template>

<script setup>
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import UserNeedList from "@/views/workbenchView/listView/UserNeedList.vue";
import BugList from "@/views/workbenchView/listView/BugList.vue";
import {ref} from "vue";

const tabs = ref([
  {name:'任务',type:'task',count:0},
  {name:'研发需求',type:'rd',count:10},
  {name:'用户需求',type:'user',count:10},
  {name:'业务需求',type:'business',count:10},
  {name:'Bug',type:'bug',count:0}
]);
const activeTab = ref('task');
</script>

<style scoped>
.pending-comment{
  background-color: white;
}
.option{
  height: 40px;
  padding: 10px;
}
.option span{
  display: inline-block;
  width: 80px;
  text-align: center;
  margin-left: 10px;
}
.option span.active{
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
}
</style>