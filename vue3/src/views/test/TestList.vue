<template>
  <div class="product-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="activeTab=tab.type"
      >
        {{tab.name}}
      </span>
      <div class="addProduct">
        <el-button class="button"
                   @click="goToTestSubmit"
        >
          创建测试
        </el-button>
      </div>
    </div>
    <div class="list">
      <TestCaseList :activeTab="activeTab" />
    </div>
  </div>
</template>

<script setup>

import {ref, onMounted} from "vue";
import TestCaseList from "@/views/workbenchView/listView/TestCaseList.vue";
import {useRouter} from "vue-router";
import request from "@/utils/request.js";

const tabs = ref([
  {name:'全部',type:'all'},
  {name:'待测试',type:'stayingTest'},
  {name:'测试中',type:'testing'},
  {name:'已完成',type:'finish'}
]);
const activeTab=ref('all');

const router =useRouter();
const goToTestSubmit = () =>{
  router.push('/test/testSubmit');
}

// 获取测试用例统计数据
const fetchTestCaseStats = async () => {
  try {
    const response = await request.get('/dashboard/test-cases');
    if (response.data.code === 200) {
      const testCases = response.data.data;
      tabs.value[0].count = testCases.length;
      
      let stayingTestCount = 0;
      let testingCount = 0;
      let finishCount = 0;
      
      for (let tc of testCases) {
        if (tc.status === 0) {
          stayingTestCount++;
        } else if (tc.status === 1) {
          testingCount++;
        } else if (tc.status === 2) {
          finishCount++;
        }
      }
      
      tabs.value[1].count = stayingTestCount;
      tabs.value[2].count = testingCount;
      tabs.value[3].count = finishCount;
    }
  } catch (error) {
    console.error('获取测试用例统计失败:', error);
  }
}

onMounted(() => {
  fetchTestCaseStats();
});
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
  //padding: 10px 0;
  //margin-bottom: 20px;
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