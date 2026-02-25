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
        <h3>项目统计</h3>
        <div class="project-statistics">
          <ProjectList/>
          <div class="project-statistics-task">
            预计完成时间:{{projectFinishDate}}
            <div class="exit-count">
              存在风险{{exitDangerCount}}
              存在问题{{exitProblemCount}}
            </div>
            <div class="project-statistics-task-kuai">
              <el-row>
                <el-col :span="5" class="tou-ru">
                  投入
                  <div class="tou-ru-comment">
                    已投入工时{{touRuTime}}h
                    <br>
                    消耗工时{{touRuXiaoHaoTime}}h
                    <br>
                    预计剩余{{predictResidue}}h
                  </div>
                </el-col>
                <el-col :span="5" class="project-needs">
                  需求
                  <div class="project-needs-comment">
                    总需求数{{projectNeedsCount}}个
                    <br>
                    已完成{{projectNeedsFinish}}个
                    <br>
                    未关闭{{projectNeedsNoClose}}个
                  </div>
                </el-col>
                <el-col :span="5" class="ren-wu">
                  任务
                  <div class="ren-wu-comment">
                    总任务数{{renWuCount}}
                    <br>
                    未开始{{renWuNoStart}}
                    <br>
                    进行中{{renWuIng}}
                  </div>
                </el-col>
                <el-col :span="5" class="project-bug">
                  Bug
                  <div class="project-bug-comment">
                    总Bug数{{ProjectBugAllCount}}
                    <br>
                    已关闭{{ProjectBugClose}}
                    <br>
                    未关闭{{ProjectBugNoClose}}
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </el-card>

<!--      产品统计-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <h3>产品统计</h3>
        <div class="product-statistics">
          <ProductList style="width: 100px;"/>
          <div class="product-statistics-echart">
            <el-row>
              <el-col :span="8" class="jiao-fu">
                <h3>需求交付率</h3>
                <el-progress type="circle" :percentage="25" />
              </el-col>
              <el-col :span="8" class="needs-statistic">
                <h3>需求统计</h3>
                <p>本月完成统计{{thisMonthFinish}}</p>
                <p>本月新增{{thisMonthAdd}}</p>
                <div ref="needsDom" style="width: 200px; height: 250px;"></div>
              </el-col>
              <el-col :span="8" class="project-latest">
                <h3>产品最新推进</h3>
                最新任务
                最新发布
              </el-col>
            </el-row>
          </div>
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
              近三年完成的项目数量分布
              <div ref="chartDom" style="width: 150px; height: 100px;"></div>
            </span>
          </div>
        </div>
      </el-card>

<!--      任务完成总览-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <div class="project-overview">
          <h3>任务完成总览</h3>
          <el-divider/>
          <div>
            <span>
              <p>{{taskAllCount}}</p>
              项目总数
            </span>
            <el-divider direction="vertical" />
            <span>
              <p>{{taskFinishCount}}</p>
              完成数量
            </span>
            <el-divider direction="vertical" />
            <span>
              未关闭的任务分布
              <div ref="taskDom" style="width: 150px; height: 100px;"></div>
            </span>
          </div>
        </div>
      </el-card>
<!--      测试统计-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <div class="measure">
          <h3>测量统计</h3>
          <el-divider/>
          <div>
            <div class="shang">
              <div class="system-list">
                <ProjectList/>
              </div>
              <div class="bug-statistics">
                <h3>Bug统计</h3>
                <div class="Add">
                  昨天新增{{yAdd}} <el-progress :percentage="50" status="exception" />
                  今日新增{{tAdd}} <el-progress :percentage="50" status="exception" />
                </div>
                <div class="Deal">
                  昨天新增{{yDeal}} <el-progress :percentage="50" status="exception" />
                  今日新增{{tDeal}} <el-progress :percentage="50" status="exception" />
                </div>
              </div>
            </div>
            <div class="xia">
              <div class="bug-repair">
                <h3>Bug修复率</h3>
                <el-progress type="circle" :percentage="25" />
                <span>
                  {{youXiaoBug}}
                  <p>有效Bug</p>
                </span>
                <span>
                  {{yiRepair}}
                  已修复
                </span>
                <span>
                  {{noClose}}
                  未关闭
                </span>
              </div>
              <div class="staying-test-list">
                <h3>待测试的测试单</h3>
                <StayTestList/>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import {ref} from "vue";
import ApproveList from '@/views/workbenchView/listView/ApproveList.vue';
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import ResearchList from "@/views/workbenchView/listView/ResearchList.vue";
import UserNeedList from "@/views/workbenchView/listView/UserNeedList.vue";
import BugList from "@/views/workbenchView/listView/BugList.vue";
import NoFinishList from "@/views/workbenchView/listView/NoFinishList.vue";
import {Document} from "@element-plus/icons-vue";
import DynamicList from "@/views/workbenchView/listView/DynamicList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";
import ProductList from "@/views/workbenchView/listView/ProductList.vue";
import { useEcharts } from '@/utils/useEcharts.js';
import StayTestList from "@/views/workbenchView/listView/StayTestList.vue";

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
const {chartRef: chartDom} = useEcharts({
    xAxis:{
      type: 'category',
      data:['2022','2023','2024']
    },
    yAxis:{
      type: 'value',
    },
    series:[{
      type:'bar',
      data:[20,100,30]
    }]
});

// 任务数量总览
const taskAllCount=1222;
const taskFinishCount=1000;
const {chartRef: taskDom} = useEcharts({
    xAxis:{
      type: 'category',
      data:['未开始','进行中','已排期']
    },
    yAxis:{
      type: 'value',
    },
    series:[{
      type:'bar',
      data:[20,100,30]
    }]
});
// 需求统计图
const {chartRef: needsDom} = useEcharts({
  xAxis:{
    type: 'category',
    data:['7月','8月','9月','10月','11月','12月']
  },
  yAxis:{
    type: 'value',
  },
  series:[
      {
        name:'finish',
        type:'line',
        stack:'Total',
        data:[15,24,54,23,14,56]
      },
    {
      name:'add',
      type:'line',
      stack:'Total',
      data:[35,67,34,28,89,99]
    }
  ]
});

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

/*项目统计样式*/
.project-statistics{
  display: flex;
}
ProjectList{
  flex: 1;
}
.exit-count{
  display: inline-block;
  float: right;
  margin-right: 10px;
}
.project-statistics-task{
  flex: 5;
  margin-left: 10px;
}
.project-statistics-task-kuai .el-col{
  height: 100px;
  border: 1px #909399 solid;
  text-align: center;
  margin-left: 10px;
}

/*产品统计样式图*/
.product-statistics{
  display: flex;
  align-items: center;
  gap:16px;
}
.product-statistics :first-child{
  flex-shrink: 0;
}
.product-statistics-echart{
  flex: 1;
  min-width: 0;
}

/*右边*/
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

/*产品、项目通用总览样式*/
.project-overview div{
  display: flex;
  justify-content: center;
  align-items: center;
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

/*测量统计样式*/
.shang{
  height: 150px;
  background-color: pink;
  display: flex;
}
.system-list{
  flex:1;
}
.bug-statistics{
  flex: 2;
}
.xia{
  height: 150px;
  background-color: green;
  display: flex;
}
.bug-repair{
  flex: 1;
}
.staying-test-list{
  flex:1;
}
</style>