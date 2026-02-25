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
        <el-button @Click="goToProductEdit">
          编辑产品
        </el-button>
        <el-button class="button">
          添加产品
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
  {name:'未关闭',type:'noClose',count:10},
  {name:'已关闭',type:'close',count:10},
]);
const activeTab=ref('all');

const router =useRouter();
const goToProductEdit = () =>{
  router.push('/productResearch/productEdit');
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