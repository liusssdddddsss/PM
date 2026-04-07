<template>
  <div class="border-comment">
    <div class="comment">
      <div class="no-begin">
        <p>未开始({{noBeginCount}})</p>
        <ul class="item-list">
          <li v-for="(item,index) in noBeginList " :key="index">
            {{item}}
          </li>
        </ul>
      </div>
      <div class="ing">
        <div class="ing-title">
          <p>进行中({{ingCount}})</p>
        </div>
        <div class="ing-com">
          <div class="left">
            <p>进行中的产品</p>
            <ul class="item-list">
              <li v-for="(item,index) in ingProjectList " :key="index">
                {{item}}
              </li>
            </ul>
          </div>
          <div class="right">
            <p>进行中的任务</p>
            <ul class="item-list">
              <li v-for="(item,index) in ingTaskList " :key="index">
                {{item}}
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="close">
        <p>已关闭({{closeCount}})</p>
        <ul class="item-list">
          <li v-for="(item,index) in closeList " :key="index">
            {{item}}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from "vue";
import request from "@/utils/request";

// 迭代数据
const ingProjectList = ref([]);
const ingTaskList = ref([]);
const noBeginList = ref([]);
const closeList = ref([]);

// 计算属性
const ingCount = computed(() => ingProjectList.value.length);
const noBeginCount = computed(() => noBeginList.value.length);
const closeCount = computed(() => closeList.value.length);

// 从后端获取迭代数据
onMounted(() => {
  fetchIterationData();
});

const fetchIterationData = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 获取项目列表
      const projectResponse = await request.get(`/workbench/projects?username=${user.username}`);
      if (projectResponse.data.code === 200) {
        const projects = projectResponse.data.data;
        // 分类项目
        ingProjectList.value = projects
          .filter(project => project.status === 1)
          .map(project => project.projectName);
        noBeginList.value = projects
          .filter(project => project.status === 0)
          .map(project => project.projectName);
        closeList.value = projects
          .filter(project => project.status === 2)
          .map(project => project.projectName);
      }
      
      // 获取任务列表
      const taskResponse = await request.get(`/workbench/tasks?username=${user.username}`);
      if (taskResponse.data.code === 200) {
        const tasks = taskResponse.data.data;
        ingTaskList.value = tasks
          .filter(task => task.status === 2)
          .map(task => task.name);
      }
    }
  } catch (error) {
    console.error('获取迭代数据失败:', error);
  }
};
</script>

<style scoped>
.border-comment{
  width: 100%;
  min-height: 100vh;
  padding: 20px;
  background-color: #fff;
  text-align: center;
}
.addProduct{
  float: right;
  margin-right: 20px;
  margin-bottom: 20px;
}
.button{
  background-color: #238EFF;
  color: #fff;
}

.comment{
  display: flex;
  gap:20px;
  width: 100%;
}
.comment p{
  height: 30px;
  line-height: 30px;
}
.item-list {
  list-style: none;
  padding: 0;
  margin: 0;
  background-color: #FAFAFA;
  height: 500px;
  padding-top: 10px;
}
.item-list li {
  width: 80%;
  margin: auto;
  padding: 8px;
  border-left: 5px solid #409eff;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ing{
  flex: 2;
}
.ing p{
  background-color: #E8FFFF;
}
.ing-com{
  display: flex;
}
.left{
  flex: 1;
}
.right{
  flex: 1;
  margin-left: 2px;
}
.no-begin{
  flex: 1;
}
.no-begin p{
  background-color: #EEF6FF;
  height: 60px;
  line-height: 60px;
}
.close{
  flex: 1;
}
.close p{
  background-color: #FFF1F1;
  height: 60px;
  line-height: 60px;
}
</style>