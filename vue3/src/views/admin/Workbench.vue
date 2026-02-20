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
        <h3>我参与的项目</h3>
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
        <div class="ddl">
          <h3>我的待处理</h3>
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

        <!-- 待处理列表内容区域 -->
        <main class="content-area">
          <ApproveList/>
          <TaskList/>
          <ResearchList/>
          <UserNeedList/>
          <BugList/>
        </main>
      </el-card>

<!--      未完成项目列表-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <h3>未完成的项目列表</h3>
        <div class="unfinish-project">
          <NoFinishList/>
        </div>
      </el-card>

<!--      项目统计-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <h3>未完成的项目列表</h3>
        <div class="unfinish-project">
          <NoFinishList/>
        </div>
      </el-card>
    </div>


<!--    右边整体-->
    <div class="right">
<!--      最新动态-->
      <el-card style="max-width: 98%">
        <div class="dynamic">
          <h3>最新动态</h3>
          <el-divider />
          <DynamicList/>
        </div>
      </el-card>

<!--      团队完成情况-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <h3>团队完成情况</h3>
        <el-divider />
        <div class="stats-grid">
          <div class="icon">
            <div class="kuai">
              <el-icon><Document/></el-icon>
              <div>
                <div>完成任务数量</div>
                昨日 {{yesterday.task}}
                <el-divider direction="vertical" />
                今日{{today.task}}
              </div>
            </div>
            <div class="kuai">
              <el-icon><CircleClose /></el-icon>
              <div>
                <div>修改Bug数量</div>
                昨日 {{yesterday.bug}}
                <el-divider direction="vertical" />
                今日{{today.bug}}
              </div>
            </div>
            <div class="kuai">
              <el-icon><Plus /></el-icon>
              <div>
                <div>创建需求数量</div>
                昨日 {{yesterday.create}}
                <el-divider direction="vertical" />
                今日{{today.create}}
              </div>
            </div>
            <div class="kuai">
              <el-icon><Warning /></el-icon>
              <div>
                <div>提出Bug数量</div>
                昨日 {{yesterday.tiChu}}
                <el-divider direction="vertical" />
                今日{{today.tiChu}}
              </div>
            </div>
            <div class="kuai">
              <el-icon><Clock /></el-icon>
              <div>
                <div>总消耗工时/h</div>
                昨日 {{yesterday.clock}}
                <el-divider direction="vertical" />
                今日{{today.clock}}
              </div>
            </div>
            <div class="kuai">
              <el-icon><Clock /></el-icon>
              <div>
                <div>平均消耗工时/h</div>
                昨日 {{yesterday.averageClock}}
                <el-divider direction="vertical" />
                今日{{today.averageClock}}
              </div>
            </div>
          </div>
        </div>
      </el-card>

<!--      产品总览-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <div class="project-overview">
          <h3>产品总览</h3>
          <el-divider/>
          <div>
          <span>
            <p>{{projectCount}}</p>
            产品总数
          </span>
            <el-divider direction="vertical" />
            <span>
            <p>{{thisYearIssue}}</p>
            今年发布
          </span>
            <el-divider direction="vertical" />
            <span>
            <p>{{closeCount}}</p>
            关闭数量
          </span>
          </div>
        </div>
      </el-card>

<!--      项目总览-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <div class="project-overview">
          <h3>项目总览</h3>
          <el-divider/>
          <div>
            <span>
              <p>{{xiangMuCount}}</p>
              项目总数
            </span>
            <el-divider direction="vertical" />
            <span>
              <p>{{thisYearFinish}}</p>
              今年完成
            </span>
            <el-divider direction="vertical" />
            <span>
              最三年完成的项目数量分布
            </span>
          </div>
        </div>
      </el-card>

<!--      测量统计-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <div class="measure">
          <h3>测量统计</h3>
          <el-divider/>
          <div>
            <div class="shang">

            </div>
            <div class="xia">

            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import {ref} from "vue";
import ApproveList from '@/views/admin/listView/ApproveList.vue';
import TaskList from "@/views/admin/listView/TaskList.vue";
import ResearchList from "@/views/admin/listView/ResearchList.vue";
import UserNeedList from "@/views/admin/listView/UserNeedList.vue";
import BugList from "@/views/admin/listView/BugList.vue";
import NoFinishList from "@/views/admin/listView/NoFinishList.vue";
import {Document} from "@element-plus/icons-vue";
import DynamicList from "@/views/admin/listView/DynamicList.vue";
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

// 团队完成情况
const yesterday=ref(
  {
    task:10,
    create:12,
    tiChu:34,
    bug:80,
    clock:78,
    averageClock:24
  }
);
const today=ref(
    {
      task:10,
      create:12,
      tiChu:34,
      bug:80,
      clock:78,
      averageClock:24
    }
);

// 产品总览
const projectCount=24;
const thisYearIssue=23;
const closeCount=24;
// 项目总览
const xiangMuCount=12;
const thisYearFinish=12;
</script>

<style scoped>
/*整体布局*/
.workComment{
  display: flex;
  justify-content: space-between;
}
.left{
  max-width: 65%;
  background-color: blue;
}
.right{
  flex: 1;
  background-color: orange;
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

/*最新动态样式*/
.dynamic{
  height: 373px;
}

/*团队完成情况样式*/
.icon{
  display: grid;
  grid-template-columns: repeat(2, 1fr);
}
.kuai{
  width: 200px;
  height: 70px;
  display: flex;
  justify-content: center;
  align-items: center;
  background-color: blue;
}

/*产品总览样式*/
.project-overview div{
  display: flex;
}
.project-overview span{
  width: 130px;
  flex: 1;
  text-align: center;
}
.project-overview p{
  display: block;
  font-size: 25px;
  margin-bottom: 2px;
}
</style>