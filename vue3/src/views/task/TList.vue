<template>
  <div class="product-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="handleTabClick(tab.type)"
      >
        {{tab.name}}
        <span class="count">{{tab.count}}</span>
      </span>
      <div class="search-add-container">
        <div class="search-box">
          <el-input
              v-model="searchQuery"
              placeholder="搜索任务名称"
              size="small"
              class="search-input"
          />
        </div>
        <div class="addProduct">
          <el-button class="button" @click="goToAddForm">
            添加任务
          </el-button>
        </div>
      </div>
    </div>
    <div class="list">
      <TaskList v-if="activeTab==='all'" :search-query="searchQuery"/>
    </div>
  </div>
</template>

<script setup>

import {ref} from "vue";
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import {useRouter} from "vue-router";

const tabs = ref([
  {name:'全部',type:'all'},
  {name:'指派我的',type:'zhiPaiMe'},
  {name:'我参与的',type:'meJoin'},
  {name:'我指派的',type:'meZhiPai'},
]);
const activeTab=ref('all');
const searchQuery=ref('');

const router =useRouter();
const goToEdit = () => {
  router.push('/task/taskEdit');
};
const goToDetail = () => {
  router.push('/iteration/iterationDetail');
};
const goToAddForm = () => {
  router.push('/task/addTaskForm');
};

// 处理标签点击
const handleTabClick = (type) => {
  activeTab.value = type;
  if (type === 'all') {
    router.push('/task/taskList');
  } else if (type === 'zhiPaiMe') {
    router.push('/task/assignedTasks');
  } else if (type === 'meJoin') {
    router.push('/task/meJoinTasks');
  } else if (type === 'meZhiPai') {
    router.push('/task/meAssignedTasks');
  }
};
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
.count {
  margin-left: 5px;
  font-size: 12px;
  color: #909399;
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

.addProduct{
  display: inline-block;
}
.button{
  background-color: #238EFF;
  color: #fff;
  margin-left: 0;
}
</style>