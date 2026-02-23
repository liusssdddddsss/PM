<template>
  <div class="projects-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @Click="activeTab=tab.type"
      >
        {{tab.name}}
        {{tab.count}}
      </span>
      <div class="addProject">
        <el-button @Click="goToItemEdit">
          编辑项目
        </el-button>
        <el-button>
          添加项目
        </el-button>
      </div>
    </div>
    <div class="list">
      <TaskList v-if="activeTab==='all'"/>
    </div>
  </div>
</template>

<script setup>

import {ref} from "vue";
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import {useRouter} from "vue-router";

const tabs = ref([
  {name:'全部',type:'all',count:10},
  {name:'进行中',type:'ing',count:10},
  {name:'未开始',type:'noBegin',count:10},
  {name:'已关闭',type:'close',count:10},
]);
const activeTab=ref('all');

const router =useRouter();
const goToItemEdit = () =>{
  router.push('/itemSet/itemEdit');
}
</script>

<style scoped>
.projects-comment{
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
.addProject{
  display: inline-block;
  float: right;
}
.addProject .el-button{
  background-color: #238EFF;
  color: #fff;
}
</style>