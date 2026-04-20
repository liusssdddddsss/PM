<template>
<!--  产品-->
  <div class="product-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="activeTab=tab.type"
      >
        {{tab.name}}
        {{tab.count}}
      </span>
      <div class="addProduct">
<!--        <el-button @click="goToProductEdit">-->
<!--          编辑产品-->
<!--        </el-button>-->
        <el-button class="button" @click="goToProductEdit">
          添加产品
        </el-button>
      </div>
    </div>
    <div class="list">
      <TaskList v-if="activeTab==='all'" :productName="productName"/>
      <TaskList v-else-if="activeTab==='noClose'" :status="0" :productName="productName"/>
      <TaskList v-else-if="activeTab==='close'" :status="2" :productName="productName"/>
    </div>
  </div>
</template>

<script setup>

import {ref, onMounted} from "vue";
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import {useRouter, useRoute} from "vue-router";

const tabs = ref([
  {name:'全部',type:'all'},
  {name:'未关闭',type:'noClose'},
  {name:'已关闭',type:'close'},
]);
const activeTab=ref('all');
const productName = ref('');

const router =useRouter();
const route = useRoute();

onMounted(() => {
  // 从URL参数中获取productName
  if (route.query.productName) {
    productName.value = route.query.productName;
  }
});

const goToProductEdit = () =>{
  router.push('/productResearch/productEdit');
}
</script>

<style scoped>
  .product-comment{
    background-color: white;
    padding: 20px;
    border-radius: 8px;
    box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  }
  .option{
    height: 40px;
    border-bottom: 1px solid #ebeef5;
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
.addProduct{
  display: inline-block;
  float: right;
}
.button{
  background-color: #238EFF;
  color: #fff;
}
</style>