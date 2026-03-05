<template>
  <div class="projects-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="activeTab=tab.type"
      >
        {{tab.name}}
        <span class="count">{{tab.count}}</span>
      </span>
      <div class="addProject">
        <el-button @click="goToItemEdit">
          编辑项目
        </el-button>
        <el-button class="button">
          添加项目
        </el-button>
      </div>
    </div>
    <div class="list">
      <el-table
          :data="filteredData"
          style="width: 100%"
          class="ProjectTable"
          :header-cell-style="{backgroundColor: '#f5f7fa', color: '#303133', fontSize: '14px', fontWeight: '500', padding: '8px 0'}"
          :cell-style="{padding: '8px 0'}"
          :row-style="{height: '40px'}"
      >
        <el-table-column prop="id" label="序号" width="60"></el-table-column>
        <el-table-column prop="title" label="项目名称" min-width="180">
          <template #default="scope">
            <a class="project-name">{{ scope.row.title }}</a>
          </template>
        </el-table-column>
        <el-table-column prop="person" label="负责人" width="90"></el-table-column>
        <el-table-column prop="startTime" label="开始时间" width="130"></el-table-column>
        <el-table-column prop="finishTime" label="预计完成时间" width="130"></el-table-column>
        <el-table-column prop="jinDu" label="进度" width="70">
          <template #default="scope">
            <div class="progress-circle">
              <span class="progress-text">{{ scope.row.jinDu }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="states" label="状态" width="90">
          <template #default="scope">
            <span class="status-tag" :class="getStateClass(scope.row.states)">{{ scope.row.states }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="action" label="操作" min-width="150">
          <template #default="scope">
            <el-button size="mini" type="primary" plain style="margin-right: 5px;">关闭</el-button>
            <el-button size="mini" type="success" plain style="margin-right: 5px;">编辑</el-button>
            <el-button size="mini" type="danger" plain>删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>

import {ref, computed} from "vue";
import {useRouter} from "vue-router";

const tabs = ref([
  {name:'全部',type:'all',count:16},
  {name:'进行中',type:'ing',count:9},
  {name:'未开始',type:'noBegin',count:5},
  {name:'已关闭',type:'close',count:2},
]);
const activeTab=ref('all');

const router =useRouter();
const goToItemEdit = () =>{
  router.push('/itemSet/itemEdit');
}

// 项目数据
const projectData = ref([
  {id: 1, title: '智慧教室_智慧云盘', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 2, title: '实践教学管理平台', person: '李四', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 3, title: '电子班牌管理系统', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 4, title: '智慧校园(中学版)', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '已关闭'},
  {id: 5, title: '宿舍管理系统', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '已关闭'},
  {id: 6, title: '教务考试系统', person: '胡一刀', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 0, states: '进行中'},
  {id: 7, title: '在线试卷批改', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 8, title: '家校互通平台', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 9, title: '智慧教室_智慧云盘', person: '胡一刀', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '未开始'},
  {id: 10, title: '实践教学管理平台', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '未开始'},
  {id: 11, title: '电子班牌管理系统', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 12, title: '智慧校园(中学版)', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 13, title: '宿舍管理系统', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 0, states: '已关闭'},
  {id: 14, title: '教务考试系统', person: '胡一刀', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 25, states: '进行中'},
  {id: 15, title: '在线试卷批改', person: '张三', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 0, states: '未开始'},
  {id: 16, title: '家校互通平台', person: '王五', startTime: '2023-08-08', finishTime: '2023-08-08', jinDu: 0, states: '未开始'},
]);

// 根据当前标签过滤数据
const filteredData = computed(() => {
  if (activeTab.value === 'all') {
    return projectData.value;
  } else if (activeTab.value === 'ing') {
    return projectData.value.filter(item => item.states === '进行中');
  } else if (activeTab.value === 'noBegin') {
    return projectData.value.filter(item => item.states === '未开始');
  } else if (activeTab.value === 'close') {
    return projectData.value.filter(item => item.states === '已关闭');
  }
  return projectData.value;
});

// 获取状态标签的类名
const getStateClass = (state) => {
  if (state === '进行中') {
    return 'in-progress';
  } else if (state === '未开始') {
    return 'scheduled';
  } else if (state === '已关闭') {
    return 'closed';
  }
  return '';
};
</script>

<style scoped>
.projects-comment{
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
.option{
  height: 40px;
  padding: 10px 0;
  margin-bottom: 20px;
  border-bottom: 1px solid #ebeef5;
}
.option span{
  display: inline-block;
  padding: 0 15px;
  text-align: center;
  margin-right: 10px;
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
.addProject{
  display: inline-block;
  float: right;
}
.button{
  background-color: #238EFF;
  color: #fff;
  margin-left: 10px;
}

.ProjectTable {
  border-radius: 8px;
  overflow: hidden;
}

.project-name {
  color: #409EFF;
  text-decoration: none;
  font-weight: 500;
}

.project-name:hover {
  text-decoration: underline;
}

.status-tag {
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.status-tag.in-progress {
  background-color: #fff7e6;
  color: #e6a23c;
}

.status-tag.scheduled {
  background-color: #f0f0f0;
  color: #909399;
}

.status-tag.closed {
  background-color: #fef0f0;
  color: #f56c6c;
}

.progress-circle {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
  position: relative;
}

.progress-text {
  font-size: 14px;
  font-weight: bold;
  color: #409EFF;
}
</style>