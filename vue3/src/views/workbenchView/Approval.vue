<template>
  <div class="approval-comment">
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
      <UserNeedList v-if="activeTab === 'user'"/>
    </div>
  </div>
</template>

<script setup>
import UserNeedList from "@/views/workbenchView/listView/UserNeedList.vue";
import {ref} from "vue";

const tabs = ref([
  {name:'全部',type:'all',count:0},
  {name:'研发需求',type:'rd',count:10},
  {name:'用户需求',type:'user',count:10},
  {name:'项目',type:'project',count:10}
]);
const activeTab = ref('task');
</script>

<style scoped>
.approval-comment{
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