<template>
  <div class="product-comment">
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
      <div class="addProduct">
        <el-button class="button"
                   @click="goToTestSubmit"
        >
          提交测试
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
  {name:'待测试',type:'stayingTest',count:10},
  {name:'测试中',type:'testing',count:10},
  {name:'已完成',type:'finish',count:10}
]);
const activeTab=ref('all');

const router =useRouter();
const goToTestSubmit = () =>{
  router.push('/test/testSubmit');
}
</script>

<style scoped>
.product-comment{
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
.addProduct{
  display: inline-block;
  float: right;
}
.button{
  background-color: #238EFF;
  color: #fff;
}
</style>