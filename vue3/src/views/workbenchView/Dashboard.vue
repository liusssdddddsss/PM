<template>
  <div class="workComment">
<!--    左边整体-->
    <div class="left">
      <el-card style="max-width: 98%;">
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
              <div class="user-avatar">
                <img src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" alt="头像" class="avatar">
              </div>
              <p>
                {{name}}，上午好
              </p>
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
          <div class="ddl-list">
            <span 
              v-for="(item, index) in tabs" 
              :key="index" 
              :class="['tab-item', { active: activeTab === index }]"
              @click="activeTab = index"
            >
              {{ item.name }}
              <el-icon v-if="item.hasArrow" class="arrow-icon"><ArrowUp /></el-icon>
            </span>
          </div>
        </div>

        <!-- 待处理列表内容区域 -->
        <main class="content-area">
          <ApproveList v-if="activeTab === 0"/>
          <TaskList v-if="activeTab === 1"/>
          <ResearchList v-if="activeTab === 2"/>
          <UserNeedList v-if="activeTab === 3"/>
          <BugList v-if="activeTab === 4"/>
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
            <div class="project-header-info">
              <div class="project-name">智慧教室_智慧云盘</div>
              <div class="project-meta">
                <span class="finish-time">预计完成时间: 2023-08-08</span>
                <span class="risk-count">存在风险 <span class="risk-number">9</span></span>
                <span class="problem-count">存在问题 <span class="problem-number">26</span></span>
              </div>
            </div>
            <div class="project-statistics-task-kuai">
              <el-row :gutter="15">
                <el-col :span="5" class="stat-card">
                  <div class="stat-card-header">投入</div>
                  <div class="stat-card-content">
                    <div class="stat-item">已投入工时 <span class="stat-value">1234h</span></div>
                    <div class="stat-item">消耗工时 <span class="stat-value">350h</span></div>
                    <div class="stat-item">预计剩余 <span class="stat-value">160h</span></div>
                  </div>
                </el-col>
                <el-col :span="5" class="stat-card">
                  <div class="stat-card-header">需求</div>
                  <div class="stat-card-content">
                    <div class="stat-item">总需求数 <span class="stat-value">1596个</span></div>
                    <div class="stat-item">已完成 <span class="stat-value">152个</span></div>
                    <div class="stat-item">未关闭 <span class="stat-value">168个</span></div>
                  </div>
                </el-col>
                <el-col :span="5" class="stat-card">
                  <div class="stat-card-header">任务</div>
                  <div class="stat-card-content">
                    <div class="stat-item">总任务数 <span class="stat-value">59个</span></div>
                    <div class="stat-item">未开始 <span class="stat-value">26个</span></div>
                    <div class="stat-item">进行中 <span class="stat-value">168个</span></div>
                  </div>
                </el-col>
                <el-col :span="5" class="stat-card">
                  <div class="stat-card-header">Bug</div>
                  <div class="stat-card-content">
                    <div class="stat-item">总Bug数 <span class="stat-value">153个</span></div>
                    <div class="stat-item">已关闭 <span class="stat-value">52个</span></div>
                    <div class="stat-item">未关闭 <span class="stat-value">69个</span></div>
                  </div>
                </el-col>
              </el-row>
            </div>
          </div>
        </div>
      </el-card>

<!--      未关闭的产品统计-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <h3>未关闭的产品统计</h3>
        <div class="product-statistics">
          <div class="product-list">
            <ul>
              <li class="active">班牌模板调整</li>
              <li>班牌PC端管理界面调整</li>
              <li>界面优化调整</li>
              <li>家校互通留言</li>
              <li>实训教学资源大数据</li>
              <li>教师端查询评分标准列表</li>
            </ul>
          </div>
          <div class="product-statistics-content">
            <div class="delivery-rate">
              <h4>需求交付率</h4>
              <el-progress type="circle" :percentage="50" />
              <div class="delivery-stats">
                <span>有效需求 <span class="stat-number">56</span></span>
                <span>已交付 <span class="stat-number">16</span></span>
                <span>未关闭 <span class="stat-number">42</span></span>
              </div>
            </div>
            <div class="needs-statistics">
              <h4>需求统计</h4>
              <div class="monthly-stats">
                <span>本月完成 <span class="stat-number">26</span></span>
                <span>本月新增 <span class="stat-number">12</span></span>
              </div>
              <div ref="needsDom" style="width: 100%; height: 200px;"></div>
            </div>
            <div class="latest-progress">
              <h4>产品最新推进</h4>
              <div class="latest-task">
                <p>最新任务</p>
                <div class="task-item">
                  <a class="task-name">班牌PC端管理界面调整</a>
                  <span class="task-status">未开始</span>
                </div>
              </div>
              <div class="latest-release">
                <p>最新发布</p>
                <div class="release-item">
                  <a class="release-name">自动给学生推送实训档案</a>
                  <span class="release-status">进行中</span>
                </div>
              </div>
            </div>
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
              <div class="icon-container blue">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
              </div>
              <div>
                <div>完成任务数量</div>
                昨日 <span class="number">120</span> | 今日 <span class="number blue">68</span>
              </div>
            </div>
            <div class="kuai">
              <div class="icon-container green">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
              </div>
              <div>
                <div>创建需求数量</div>
                昨日 <span class="number">56</span> | 今日 <span class="number blue">45</span>
              </div>
            </div>
            <div class="kuai">
              <div class="icon-container orange">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.985 10C21.523 14.728 18.391 19 14 19H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v3.985"></path><path d="M19 17v1a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-1"></path><path d="M12 3v4"></path><path d="M9 5h6"></path></svg>
              </div>
              <div>
                <div>提出Bug数量</div>
                昨日 <span class="number">159</span> | 今日 <span class="number blue">123</span>
              </div>
            </div>
            <div class="kuai">
              <div class="icon-container red">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.985 10C21.523 14.728 18.391 19 14 19H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v3.985"></path><path d="M19 17v1a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-1"></path><path d="M12 3v4"></path><path d="M9 5h6"></path></svg>
              </div>
              <div>
                <div>修改Bug数量</div>
                昨日 <span class="number">165</span> | 今日 <span class="number blue">158</span>
              </div>
            </div>
            <div class="kuai">
              <div class="icon-container light-blue">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
              </div>
              <div>
                <div>总消耗工时/h</div>
                昨日 <span class="number">65</span> | 今日 <span class="number blue">50</span>
              </div>
            </div>
            <div class="kuai">
              <div class="icon-container purple">
                <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
              </div>
              <div>
                <div>平均消耗工时/h</div>
                昨日 <span class="number">7</span> | 今日 <span class="number blue">4.5</span>
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
          <h3>测试统计</h3>
          <el-divider/>
          <div style="display: flex; gap: 20px;">
            <!-- 左侧：项目列表 -->
            <div style="width: 150px; flex-shrink: 0;">
              <div class="system-list">
                <ProjectList/>
              </div>
            </div>
            <!-- 右侧：Bug统计（红色框内） -->
            <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff; min-height: 200px;">
              <div class="bug-statistics">
                <h3>Bug统计</h3>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">昨天新增</div>
                  <div class="bug-stat-value">125</div>
                  <el-progress :percentage="70" />
                </div>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">今日新增</div>
                  <div class="bug-stat-value">65</div>
                  <el-progress :percentage="40" />
                </div>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">昨天解决</div>
                  <div class="bug-stat-value">98</div>
                  <el-progress :percentage="60" />
                </div>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">今日解决</div>
                  <div class="bug-stat-value">26</div>
                  <el-progress :percentage="20" />
                </div>
              </div>
            </div>
          </div>
          <!-- 下排：Bug修复率和待测试的测试单 -->
          <div style="display: flex; gap: 20px; margin-top: 20px;">
            <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff;">
              <div class="bug-repair">
                <h3>Bug修复率</h3>
                <el-progress type="circle" :percentage="50" />
                <div class="bug-repair-stats">
                  <span>
                    <div class="stat-value">56</div>
                    <div class="stat-label">有效Bug</div>
                  </span>
                  <span>
                    <div class="stat-value">16</div>
                    <div class="stat-label">已修复</div>
                  </span>
                  <span>
                    <div class="stat-value">42</div>
                    <div class="stat-label">未关闭</div>
                  </span>
                </div>
              </div>
            </div>
            <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff;">
              <div class="staying-test-list">
                <h3>待测试的测试单</h3>
                <ul class="test-list">
                  <li><a href="#">班牌PC端管理界面调整</a></li>
                  <li><a href="#">学期结束后，自动给学生推送实训档案</a></li>
                  <li><a href="#">班牌模板调整，参考海康，增加竖版</a></li>
                  <li><a href="#">家校互通留言</a></li>
                  <li><a href="#">终端-教师端查询评分标准列表</a></li>
                </ul>
              </div>
            </div>
          </div>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";
import ApproveList from '@/views/workbenchView/listView/ApproveList.vue';
import TaskList from "@/views/workbenchView/listView/TaskList.vue";
import ResearchList from "@/views/workbenchView/listView/ResearchList.vue";
import UserNeedList from "@/views/workbenchView/listView/UserNeedList.vue";
import BugList from "@/views/workbenchView/listView/BugList.vue";
import NoFinishList from "@/views/workbenchView/listView/NoFinishList.vue";
import {Document, ArrowUp} from "@element-plus/icons-vue";
import DynamicList from "@/views/workbenchView/listView/DynamicList.vue";
import ProjectList from "@/views/workbenchView/listView/ProjectList.vue";
import ProductList from "@/views/workbenchView/listView/ProductList.vue";
import { useEcharts } from '@/utils/useEcharts.js';
import StayTestList from "@/views/workbenchView/listView/StayTestList.vue";
import request from "@/utils/request.js";

// 待处理标签
const activeTab = ref(0);
const tabs = ref([
  {name: '审批', hasArrow: true},
  {name: '任务', hasArrow: true},
  {name: '研发需求', hasArrow: true},
  {name: '用户需求', hasArrow: true},
  {name: 'BUG', hasArrow: true}
]);

//日期
const currentTime = ref(new Date()).value.toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})

// 用户信息
const name = ref('');
const bug = ref(10);
const approveState = ref(17);
const taskState = ref(13);
const bugState = ref(10);
const needsState = ref(10);
const userState = ref(10);
const passageState = ref(10);

// 页面加载时获取用户信息和统计数据
onMounted(() => {
  // 从本地存储中获取用户信息
  const userStr = localStorage.getItem('user');
  if (userStr) {
    const user = JSON.parse(userStr);
    // 先使用username作为默认值
    name.value = user.username || '用户';
    
    // 从后端获取用户的真实姓名
    fetchUserInfo(user.username);
  }
  
  // 从后端获取统计数据
  fetchStatistics();
});

// 从后端获取用户信息
const fetchUserInfo = async (username) => {
  try {
    // 从后端获取管理员信息
    const response = await request.get('/admin/findAll');
    if (response.code === 200) {
      // 找到当前登录用户的信息
      const currentUser = response.data.find(user => user.username === username);
      if (currentUser) {
        // 使用数据库中的name字段
        name.value = currentUser.name || currentUser.username || '用户';
      }
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

// 从后端获取统计数据
const fetchStatistics = async () => {
  try {
    // 从后端获取工作台统计数据
    const response = await request.get('/dashboard/statistics');
    if (response.code === 200) {
      // 更新统计数据
      const data = response.data;
      bug.value = data.bug || 0;
      approveState.value = data.approveState || 0;
      taskState.value = data.taskState || 0;
      bugState.value = data.bugState || 0;
      needsState.value = data.needsState || 0;
      userState.value = data.userState || 0;
      passageState.value = data.passageState || 0;
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

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
  //background-color: blue;
}
.right{
  flex: 1;
  //background-color: orange;
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
  justify-content: flex-start;
  align-items: center;
  gap: 40px;
}
.label{
  width: 25%;
  display: flex;
  align-items: center;
  gap: 15px;
}
.user-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  overflow: hidden;
  flex-shrink: 0;
}
.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.label p {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0;
}
.tasks{
  width: 75%;
  display: flex;
  text-align: center;
  justify-content: space-around;
  align-items: center;
}
.tasks div{
  flex: 1;
}
.tasks span{
  display: block;
  font-size: 14px;
  color: #606266;
  margin-top: 4px;
}
.tasks-shu{
  font-size: 24px;
  font-weight: bold;
  color: #238EFF;
  margin: 0;
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
  padding: 0 0 15px 0;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 15px;
}

.ddl h3 {
  margin: 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.ddl-list{
  display: flex;
  align-items: center;
  gap: 20px;
}

.tab-item {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 6px 12px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  border-radius: 4px;
  transition: all 0.3s;
}

.tab-item:hover {
  color: #409EFF;
  background-color: #ecf5ff;
}

.tab-item.active {
  color: #409EFF;
  background-color: #ecf5ff;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
}

.content-area {
  min-height: 400px;
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
.project-statistics-task-kuai .stat-card{
  height: 140px;
  border: 1px #ebeef5 solid;
  border-radius: 8px;
  padding: 12px;
  text-align: left;
  margin-left: 10px;
  background-color: #ffffff;
}

.stat-card-header {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.stat-card-content {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-item {
  font-size: 10px;
  color: #606266;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-value {
  font-size: 12px;
  font-weight: bold;
  color: #303133;
}

.project-header-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}

.project-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.project-meta {
  display: flex;
  align-items: center;
  gap: 20px;
}

.finish-time {
  font-size: 13px;
  color: #606266;
}

.risk-count, .problem-count {
  font-size: 13px;
  color: #606266;
}

.risk-number, .problem-number {
  font-weight: bold;
  color: #F56C6C;
  margin-left: 4px;
}

/*未关闭的产品统计样式*/
.product-statistics{
  display: flex;
  align-items: flex-start;
  gap:20px;
  padding: 10px 0;
}

.product-list {
  flex-shrink: 0;
  width: 150px;
  border-right: 1px solid #ebeef5;
  padding-right: 15px;
}

.product-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.product-list li {
  padding: 8px 0;
  cursor: pointer;
  font-size: 13px;
  color: #606266;
  transition: all 0.3s;
  border-radius: 4px;
  padding-left: 10px;
}

.product-list li:hover {
  color: #409EFF;
  background-color: #ecf5ff;
}

.product-list li.active {
  color: #409EFF;
  font-weight: 500;
  background-color: #ecf5ff;
}

.product-statistics-content {
  flex: 1;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
}

.delivery-rate, .needs-statistics, .latest-progress {
  padding: 15px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.delivery-rate h4, .needs-statistics h4, .latest-progress h4 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.delivery-rate {
  text-align: center;
}

.delivery-rate .el-progress {
  width: 120px !important;
  height: 120px !important;
  margin: 0 auto 15px;
}

.delivery-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
  flex-wrap: wrap;
  gap: 10px;
}

.delivery-stats span {
  font-size: 12px;
  color: #606266;
  display: inline-block;
  margin: 0 5px;
}

.stat-number {
  font-weight: bold;
  color: #303133;
  margin-left: 4px;
}

.needs-statistics {
  text-align: center;
}

.monthly-stats {
  display: flex;
  justify-content: center;
  margin-bottom: 15px;
  gap: 20px;
}

.monthly-stats span {
  font-size: 14px;
  color: #606266;
}

.latest-progress {
  padding: 15px;
}

.latest-task, .latest-release {
  margin-bottom: 20px;
}

.latest-task p, .latest-release p {
  font-size: 13px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.task-item, .release-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.task-name, .release-name {
  font-size: 12px;
  color: #409EFF;
  text-decoration: none;
  flex: 1;
  margin-right: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.task-name:hover, .release-name:hover {
  text-decoration: underline;
}

.task-status, .release-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 500;
  flex-shrink: 0;
}

.task-status {
  background-color: #f0f0f0;
  color: #909399;
}

.release-status {
  background-color: #f0f9eb;
  color: #67C23A;
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
  gap: 15px;
}
.kuai{
  width: 100%;
  min-height: 80px;
  display: flex;
  align-items: center;
  gap: 12px;
  padding: 10px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
.icon-container {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
  color: white;
  flex-shrink: 0;
}
.icon-container.blue {
  background-color: #409EFF;
}
.icon-container.green {
  background-color: #67C23A;
}
.icon-container.orange {
  background-color: #E6A23C;
}
.icon-container.red {
  background-color: #F56C6C;
}
.icon-container.light-blue {
  background-color: #90C9FF;
}
.icon-container.purple {
  background-color: #C084FC;
}
.kuai div:last-child {
  flex: 1;
}
.kuai div:last-child div:first-child {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
}
.number {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
}
.number.blue {
  color: #409EFF;
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

/*测试统计样式*/
.measure {
  padding: 10px 0;
}

.measure h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.system-list {
  padding: 10px 0;
}

.bug-statistics {
  padding: 10px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.bug-statistics h3 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.bug-stat-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 10px;
}

.bug-stat-label {
  font-size: 13px;
  color: #606266;
  width: 60px;
}

.bug-stat-value {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  width: 20px;
}

.bug-stat-item .el-progress {
  flex: 1;
  height: 8px !important;
}

.bug-repair {
  padding: 15px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  text-align: center;
  height: 100%;
}

.bug-repair h3 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.bug-repair .el-progress {
  width: 120px !important;
  height: 120px !important;
  margin: 0 auto 15px;
}

.bug-repair-stats {
  display: flex;
  justify-content: space-around;
  margin-top: 15px;
}

.bug-repair-stats span {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-value {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #606266;
}

.staying-test-list {
  padding: 15px;
  border-radius: 8px;
  background-color: #ffffff;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
  height: 100%;
}

.staying-test-list h3 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.test-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.test-list li {
  margin-bottom: 10px;
}

.test-list a {
  font-size: 13px;
  color: #409EFF;
  text-decoration: none;
  transition: color 0.3s;
}

.test-list a:hover {
  text-decoration: underline;
}
</style>