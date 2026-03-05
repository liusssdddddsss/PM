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
        {{tab.count}}
      </span>
      <div class="addProduct">
        <el-button class="button">
          添加任务
        </el-button>
        <el-button class="button">
          关闭
        </el-button>
        <el-button class="button" @click="goToEdit">
          编辑
        </el-button>
      </div>
    </div>
    <div class="no-tasks">
      <p>暂时没有任务。<a href="#" class="new-task-link">新建任务</a></p>
    </div>
  </div>
</template>

<script setup>
import { ref } from "vue";
import { useRouter } from "vue-router";

const tabs = ref([
  {name:'全部',type:'all',count:16},
  {name:'指派我的',type:'zhiPaiMe',count:9},
  {name:'我参与的',type:'meJoin',count:0},
  {name:'我指派的',type:'meZhiPai',count:0},
]);
const activeTab=ref('meZhiPai');

const router = useRouter();
const goToEdit = () => {
  router.push('/task/taskEdit');
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

.no-tasks {
  padding: 60px 20px;
  text-align: center;
  background-color: #f9f9f9;
  margin: 20px;
  border-radius: 8px;
}

.no-tasks p {
  font-size: 14px;
  color: #606266;
  margin: 0;
}

.new-task-link {
  color: #409EFF;
  text-decoration: none;
  margin-left: 4px;
  cursor: pointer;
}

.new-task-link:hover {
  text-decoration: underline;
}
</style>