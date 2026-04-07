<template>
  <div class="border-comment">
    <div class="addProject">
      <el-button>
        添加项目
      </el-button>
    </div>
    <div class="comment">
      <div class="ing">
        <div class="ing-title">
          <p>进行中({{ingProjectList.length}})</p>
        </div>
        <div class="ing-com">
          <div class="left">
            <p>进行中的项目</p>
            <ul class="item-list">
              <li v-for="(item,index) in ingProjectList " :key="index">
                {{item.projectName}}
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
      <div class="no-begin">
        <p>未开始({{noBeginList.length}})</p>
        <ul class="item-list">
          <li v-for="(item,index) in noBeginList " :key="index">
            {{item.projectName}}
          </li>
        </ul>
      </div>
      <div class="close">
        <p>已关闭({{closeList.length}})</p>
        <ul class="item-list">
          <li v-for="(item,index) in closeList " :key="index">
            {{item.projectName}}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";
import request from "@/utils/request.js";

const ingProjectList = ref([]);
const ingTaskList = ref([]);
const noBeginList = ref([]);
const closeList = ref([]);

// 获取当前登录用户的信息
const getCurrentUser = () => {
  const userStr = localStorage.getItem('user');
  if (userStr) {
    try {
      return JSON.parse(userStr);
    } catch (error) {
      console.error('解析用户信息失败:', error);
      return null;
    }
  }
  return null;
};

// 获取用户参与的项目
const fetchUserProjects = async () => {
  const user = getCurrentUser();
  console.log('当前登录用户:', user);
  if (!user || !user.username) {
    console.error('未找到用户信息');
    return;
  }
  
  try {
    console.log('发送请求到 /workbench/projects，参数:', { username: user.username });
    const response = await request.get('/workbench/projects', {
      params: { username: user.username }
    });
    
    console.log('获取项目列表响应:', response);
    console.log('获取项目列表响应.data:', response.data);
    
    if (response.data && response.data.code === 200 && response.data.data) {
      const projects = response.data.data;
      console.log('获取到的项目列表:', projects);
      // 清空现有列表
      ingProjectList.value = [];
      noBeginList.value = [];
      closeList.value = [];
      
      // 根据项目状态分配到不同列表
      projects.forEach(project => {
        console.log('项目信息:', project);
        console.log('project.degree:', project.degree);
        console.log('typeof project.degree:', typeof project.degree);
        // 首先根据status字段判断项目状态
        if (project.status === 0) {
          noBeginList.value.push(project);
          console.log('添加到未开始列表:', project.projectName);
        } else if (project.status === 1) {
          ingProjectList.value.push(project);
          console.log('添加到进行中列表:', project.projectName);
        } else if (project.status === 2) {
          closeList.value.push(project);
          console.log('添加到已关闭列表:', project.projectName);
        } else {
          // 如果没有status字段，根据progress字段判断
          if (project.degree > 0 && project.degree < 100) {
            ingProjectList.value.push(project);
            console.log('添加到进行中列表:', project.projectName);
          } else if (project.degree === 0) {
            noBeginList.value.push(project);
            console.log('添加到未开始列表:', project.projectName);
          } else if (project.degree === 100) {
            closeList.value.push(project);
            console.log('添加到已关闭列表:', project.projectName);
          } else {
            console.log('未匹配到任何列表:', project.projectName, 'degree:', project.degree, 'status:', project.status);
          }
        }
      });
      
      console.log('分配后的列表:');
      console.log('进行中列表长度:', ingProjectList.value.length);
      console.log('进行中:', ingProjectList.value);
      console.log('未开始列表长度:', noBeginList.value.length);
      console.log('未开始:', noBeginList.value);
      console.log('已关闭列表长度:', closeList.value.length);
      console.log('已关闭:', closeList.value);
    } else {
      console.error('获取项目列表失败:', response.data ? response.data.msg : '响应格式错误');
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
    console.error('错误详细信息:', error.response ? error.response.data : error.message);
  }
};

// 模拟获取进行中的任务
const fetchIngTasks = () => {
  ingTaskList.value = [
    '家长端，界面优化调整，新增功能：授权监...',
    '班牌PC端管理界面调整，样式统一，菜单归类',
    '班牌模板调整，参考海康，增加竖版',
    '家校互通留言台'
  ];
};

onMounted(() => {
  fetchUserProjects();
  fetchIngTasks();
});
</script>

<style scoped>
.border-comment{
  width: 100%;
  min-height: 100vh;
  padding: 20px;
  background-color: #fff;
  text-align: center;
}
.addProject{
  float: right;
  margin-right: 20px;
  margin-bottom: 20px;
}
.addProject .el-button{
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