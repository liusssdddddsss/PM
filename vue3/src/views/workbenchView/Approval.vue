<template>
  <div class="approval-container">
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
      <ApproveList v-if="activeTab === 'all'"/>
      <ResearchList v-if="activeTab === 'rd'"/>
      <UserNeedList v-if="activeTab === 'user'"/>
      <ProjectList v-if="activeTab === 'project'"/>
    </div>
  </div>
</template>

<script setup>
import UserNeedList from "@/views/workbenchView/listView/UserNeedList.vue";
import ResearchList from "@/views/workbenchView/listView/ResearchList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";
import ApproveList from "@/views/workbenchView/listView/ApproveList.vue";
import {ref} from "vue";

const tabs = ref([
  {name:'全部',type:'all',count:0},
  {name:'研发需求',type:'rd',count:0},
  {name:'用户需求',type:'user',count:0},
  {name:'项目',type:'project',count:0}
]);
const activeTab = ref('all');
</script>

<style scoped>
.approval-container{
  background-color: white;
  padding: 20px;
  min-height: calc(100vh - 60px);
}

.option{
  height: 40px;
  border-bottom: 1px solid #e4e7ed;
}

.option span{
  display: inline-block;
  padding: 0 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.option span.active{
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
  font-weight: 500;
}

.tab-count{
  margin-left: 3px;
  color: #606266;
  font-size: 10px;
  font-weight: normal;
}

.list {
  padding-left:5px;
}
</style>