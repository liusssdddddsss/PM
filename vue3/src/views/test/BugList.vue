<template>
  <div class="bug-list-container">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="activeTab=tab.type"
      >
        {{tab.name}}
      </span>
      <div class="search-add-container">
        <div class="search-box">
          <el-input
              v-model="searchQuery"
              placeholder="搜索Bug名称"
              size="small"
              class="search-input"
          />
        </div>
        <div class="addProduct">
          <el-button class="button"
                     @click="goToBugSubmit"
          >
            创建Bug
          </el-button>
        </div>
      </div>
    </div>
    <div class="list">
      <BugCaseList :activeTab="activeTab" :search-query="searchQuery" />
    </div>
  </div>
</template>

<script setup>

import {ref, onMounted} from "vue";
import BugCaseList from "@/views/test/BugCaseList.vue";
import {useRouter, useRoute} from "vue-router";
import request from "@/utils/request.js";

const tabs = ref([
  {name:'全部',type:'all'},
  {name:'待处理',type:'pending'},
  {name:'处理中',type:'processing'},
  {name:'已解决',type:'resolved'}
]);

const activeTab = ref('all');
const searchQuery = ref('');

const router = useRouter();
const route = useRoute();

const goToBugSubmit =()=>{
  // 跳转到Bug提交页面
  router.push('/test/createBug');
};

// 组件加载时检查URL参数中的search
onMounted(() => {
  const search = route.query.search;
  if (search) {
    searchQuery.value = search;
  }
});
</script>

<style scoped>
.bug-list-container {
  padding: 20px;
  background-color: #fff;
  min-height: 100vh;
}

.option {
  height: 40px;
}

.option span {
  display: inline-block;
  padding: 0 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}

.option span.active {
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
  font-weight: 500;
}

.search-add-container {
  display: inline-block;
  float: right;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box {
  margin-right: 10px;
}

.search-input {
  width: 200px;
}

.addProduct {
  display: inline-block;
}

.button {
  background-color: #238EFF;
  color: #fff;
}

.list {
  background-color: white;
}
</style>
