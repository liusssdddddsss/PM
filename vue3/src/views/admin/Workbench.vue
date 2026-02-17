<template>
  <div class="workComment">
<!--    左边整体-->
    <div class="left">
<!--      待办-->
      <el-card style="max-width: 98%">
        <div class="message">
          <p>
            {{currentTime}}
          </p>
          <p>
            昨日您处理任务和Bug
            <span>{{bug}}</span>
            次
          </p>
          <div class="state">
            <div class="label">
              <p>
                {{name}}，上午好
              </p>
<!--              <img src="@/assets/img">-->
            </div>
            <div class="tasks">
              <div class="approve">
                <p class="tasks-shu">
                  {{approveState}}
                </p>
                <span>待我审批数</span>
              </div>
              <div class="task">
                <p class="tasks-shu">
                  {{taskState}}
                </p>
                <span>任务数</span>
              </div>
              <div class="bugs">
                <p class="tasks-shu">
                  {{bugState}}
                </p>
                <span>BUG数</span>
              </div>
              <div class="needs">
                <p class="tasks-shu">
                  {{needsState}}
                </p>
                <span>研发需求数</span>
              </div>
              <div class="users">
                <p class="tasks-shu">
                  {{userState}}
                </p>
                <span>用户需求数</span>
              </div>
              <div class="passages">
                <p class="tasks-shu">
                  {{passageState}}
                </p>
                <span>文档数</span>
              </div>
            </div>
          </div>
        </div>
      </el-card>
<!--      参与项目-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <p>我参与的项目</p>
        <el-row class="project-self">
          <el-col
              v-for="item in projectList"
              :key="item"
              :title="item"
              :span="8"
          >
            <el-card class="project-list">
              <h1>{{item.projectName}}</h1>
              <p>项目成员: 共{{item.projectMember}}人</p><br>
              <p>计划完成时间: {{item.finishTime}}</p>
              <div class="progress-bar">
                <el-progress :percentage="item.degree"/>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-card>

<!--      待处理-->
      <el-card style="max-width: 98%;margin-top: 10px">
<!--        待处理标题-->
        <div class="ddl">
          <span>我的待处理</span>
          <span>
            <div class="ddl-list">
              <span class="ddl-approve">审批</span>
              <span class="ddl-task">任务</span>
              <span class="research-list">研发需求</span>
              <span class="user-needs-list">用户需求</span>
              <span class="bug-list">BUG</span>
            </div>
          </span>
        </div>

        <!-- 列表内容区域 -->
        <main class="content-area">
          <ApproveList/>
          <TaskList/>
        </main>
      </el-card>
    </div>
<!--    右边整体-->
    <div class="right">
      <el-card style="max-width: 99.5%">

      </el-card>
    </div>
  </div>
</template>

<script setup>
import {ref} from "vue";
import ApproveList from '@/views/admin/listView/ApproveList.vue';
import TaskList from "@/views/admin/listView/TaskList.vue";
//日期
const currentTime = ref(new Date()).value.toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})
const name='张三';
const bug = 10;
const approveState=17;
const taskState=13;
const bugState=10;
const needsState=10;
const userState=10;
const passageState=10;

//我参与的项目数
const projectList = ref([
  {
    projectName:'智慧教室',
    projectMember:6,
    finishTime:'2026-09-09',
    degree:80
  },
  {
    projectName:'实践教学平台',
    projectMember:6,
    finishTime:'2026-09-09',
    degree:26
  },
  {
    projectName:'实践教学平台',
    projectMember:6,
    finishTime:'2026-09-09',
    degree:26
  }
])


</script>

<style scoped>
/*整体布局*/
.workComment{
  display: flex;
  justify-content: space-between;
}
.left{
  flex: 2;
}
.right{
  flex: 1;
}
/*左上个人信息和任务状态*/
.message{
  height: 140px;
}
p{
  display: inline;
}
.state{
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.label{
  width: 25%;
}
.tasks{
  width: 80%;
  display: flex;
  text-align: center;
  justify-content: center;
  align-items: center;
}
.tasks div{
  flex: 1;
}
.tasks span{
  display: block;
}
.tasks-shu{
  font-size: 20px;
  color: #238EFF;
}

/*我参与的项目样式*/
.project-list{
  width: 250px;
  height: 150px;
  margin-top: 10px;
  box-shadow: none;
}
.progress-bar{
  margin-top: 30px;
}

/*我的待处理样式*/
.ddl{
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0;
}
.ddl span{
  flex: 1;
  height: 35px;
  align-content: center;
}
.ddl-list{
  display: flex;
  text-align: center;
}
.ddl-list span{
  flex: 1;
}
</style>