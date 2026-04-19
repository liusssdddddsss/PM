<template>
  <div class="workComment">
<!--    左边整体-->
    <div class="left">
      <el-card style="max-width: 98%;">
        <div class="message">
          <p>
            {{currentTime}}
          </p>
<!--          <p>-->
<!--            昨日您处理任务和Bug-->
<!--            <span>{{bug}}</span>-->
<!--            次-->
<!--          </p>-->
          <div class="state">
            <div class="label">              <div class="user-avatar" @click="triggerFileInput">                <img :src="userAvatar" alt="头像" class="avatar">                <input type="file" ref="fileInput" accept="image/*" @change="handleFileChange" style="display: none;">              </div>              <p>                {{name}}，上午好              </p>            </div>
            <div class="tasks">              <div class="approve">                <p class="tasks-shu">                  {{projectCount}}                </p>                <span>产品总数</span>              </div>              <div class="task">                <p class="tasks-shu">                  {{xiangMuCount}}                </p>                <span>项目总数</span>              </div>              <div class="bugs">                <p class="tasks-shu">                  {{taskAllCount}}                </p>                <span>任务总数</span>              </div>              <div class="needs">                <p class="tasks-shu">                  {{bugState}}                </p>                <span>Bug数</span>              </div>              <div class="users">                <p class="tasks-shu">                  {{approveState}}                </p>                <span>待审批数</span>              </div>            </div>
          </div>
        </div>
      </el-card>

<!--      参与项目-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <h3>我参与的项目</h3>
        <div class="project-self">
          <div class="project-container">
            <div
                v-for="item in projectList"
                :key="item.id"
                class="project-item"
            >
              <div class="project-card" @click="handleProjectCardClick(item.projectName)">
                <div class="project-header">
                  <h3 class="project-title">{{item.projectName}}</h3>
                  <div class="project-arrow">
                    <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round" style="color: #409EFF;">
                      <polyline points="9 18 15 12 9 6"></polyline>
                    </svg>
                  </div>
                </div>
                <p class="project-member">项目成员: 共{{item.projectMember}}人</p>
                <p class="project-time">计划完成时间: {{formatDate(item.finishTime)}}</p>
                <div class="project-progress">
                  <el-progress :percentage="item.degree" color="#409EFF"/>
                </div>
              </div>
            </div>
          </div>
        </div>
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
              v-if="index !== 2 && index !== 3"
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
          <BugList v-if="activeTab === 2"/>
        </main>
      </el-card>

<!--      未完成项目列表-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <h3 style="margin-bottom: 15px">未完成的项目列表</h3>
        <div class="unfinish-project">
          <NoFinishList @project-click="handleUnfinishedProjectClick"/>
        </div>
      </el-card>

<!-- 统计弹窗 -->
      <el-dialog
        v-model="dialogVisible"
        title="项目统计"
        width="70%"
        :close-on-click-modal="false"
      >
        <div class="dialog-content">
          <!-- 项目统计 -->
          <div class="project-statistics">
            <h3 style="margin-bottom: 15px">项目统计</h3>
            <div class="project-statistics-task">
              <div class="project-header-info">
                <div class="project-name">{{ projectDetail.projectName || '暂无项目' }}</div>
                <div class="project-meta">
                  <span class="finish-time">预计完成时间: {{ formatDate(projectDetail.finishTime) || '—' }}</span>
                  <span class="risk-count">存在风险 <span class="risk-number">0</span></span>
                  <span class="problem-count">存在问题 <span class="problem-number">0</span></span>
                </div>
              </div>
              <div class="project-statistics-task-kuai">
                <el-row :gutter="15">
                  <el-col :span="5" class="stat-card">
                    <div class="stat-card-header">投入</div>
                    <div class="stat-card-content">
                      <div class="stat-item">已投入工时 <span class="stat-value">{{ projectDetail.workTimeTotal || 0 }}h</span></div>
                      <div class="stat-item">消耗工时 <span class="stat-value">{{ projectDetail.workTimeConsumed || 0 }}h</span></div>
                      <div class="stat-item">预计剩余 <span class="stat-value">{{ projectDetail.workTimeRemaining || 0 }}h</span></div>
                    </div>
                  </el-col>

                  <el-col :span="5" class="stat-card">
                    <div class="stat-card-header">任务</div>
                    <div class="stat-card-content">
                      <div class="stat-item">总任务数 <span class="stat-value">{{ projectDetail.taskTotal || 0 }}个</span></div>
                      <div class="stat-item">未开始 <span class="stat-value">{{ projectDetail.taskNotStarted || 0 }}个</span></div>
                      <div class="stat-item">进行中 <span class="stat-value">{{ projectDetail.taskInProgress || 0 }}个</span></div>
                    </div>
                  </el-col>
                  <el-col :span="5" class="stat-card">
                    <div class="stat-card-header">Bug</div>
                    <div class="stat-card-content">
                      <div class="stat-item">总Bug数 <span class="stat-value">{{ projectDetail.bugTotal || 0 }}个</span></div>
                      <div class="stat-item">已关闭 <span class="stat-value">{{ projectDetail.bugClosed || 0 }}个</span></div>
                      <div class="stat-item">未关闭 <span class="stat-value">{{ projectDetail.bugUnclosed || 0 }}个</span></div>
                    </div>
                  </el-col>
                </el-row>
              </div>
          </div>
          </div>
        </div>
      </el-dialog>

      <!-- 项目详情弹窗 -->
      <el-dialog
        v-model="projectDetailDialogVisible"
        title="项目详情"
        width="70%"
        :close-on-click-modal="false"
      >
        <div class="project-detail-content">
          <div class="project-detail-header">
            <h3 class="project-detail-name" @click="goToProjectModule">{{ currentProjectDetail.projectName || '暂无项目' }}</h3>
            <div class="project-detail-meta">
              <span class="start-time">开始时间: {{ formatDate(currentProjectDetail.startTime) || '—' }}</span>
              <span class="finish-time">预计完成时间: {{ formatDate(currentProjectDetail.finishTime) || '—' }}</span>
              <span class="remaining-time">剩余时间: {{ currentProjectDetail.remainingTime || '—' }}</span>
            </div>
          </div>
          
          <div class="project-detail-body">
            <!-- 项目基本信息 -->
            <div class="project-basic-info">
              <h4>项目基本信息</h4>
              <div class="basic-info-grid">
                <div class="info-item">
                  <span class="info-label">项目经理:</span>
                  <span class="info-value">{{ currentProjectDetail.projectManager || '—' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">项目状态:</span>
                  <span class="info-value">{{ currentProjectDetail.projectStatus || '—' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">项目进度:</span>
                  <span class="info-value">{{ currentProjectDetail.projectProgress || '—' }}</span>
                </div>
                <div class="info-item">
                  <span class="info-label">所属产品:</span>
                  <span class="info-value">{{ currentProjectDetail.productName || '—' }}</span>
                </div>
              </div>
            </div>
            
            <!-- 团队成员 -->
            <div class="project-team">
              <h4 @click="goToTeamModule" style="cursor: pointer; color: #238EFF;">
                所属团队：{{ currentProjectDetail.teamName || '未知团队' }}
              </h4>
              <div class="team-members">
                <div 
                  v-for="(member, index) in currentProjectDetail.teamMembers" 
                  :key="index"
                  class="team-member"
                >
                  <div class="member-name">{{ member.name || '未知' }}</div>
                  <div class="member-position">{{ member.position || '未知职位' }}</div>
                </div>
                <span v-if="!currentProjectDetail.teamMembers || currentProjectDetail.teamMembers.length === 0" class="no-members">
                  暂无团队成员
                </span>
              </div>
            </div>
            
            <!-- 项目目标 -->
            <div class="project-goals">
              <h4>项目目标</h4>
              <ul class="goal-list">
                <li v-for="(goal, index) in currentProjectDetail.projectGoals" :key="index">
                  {{ goal }}
                </li>
                <li v-if="!currentProjectDetail.projectGoals || currentProjectDetail.projectGoals.length === 0" class="no-goals">
                  暂无项目目标
                </li>
              </ul>
            </div>
            
            <!-- 项目详情 -->
            <div class="project-info">
              <h4>项目详情</h4>
              <p class="project-description">{{ currentProjectDetail.description || '暂无项目详情' }}</p>
            </div>
          </div>
        </div>
      </el-dialog>

      <!-- 团队完成情况弹窗 -->
      <el-dialog
        v-model="teamDialogVisible"
        title="团队完成情况"
        width="70%"
        :close-on-click-modal="false"
      >
        <div class="team-dialog-content">
          <div class="stats-grid">
            <div class="icon">
              <div class="kuai">
                <div class="icon-container blue">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15v4a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2v-4"></path><polyline points="7 10 12 15 17 10"></polyline><line x1="12" y1="15" x2="12" y2="3"></line></svg>
                </div>
                <div>
                  <div>完成任务数量</div>
                  昨日 <span class="number">{{yesterday.task}}</span> | 今日 <span class="number blue">{{today.task}}</span>
                </div>
              </div>
              <div class="kuai">
                <div class="icon-container green">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"></line><line x1="5" y1="12" x2="19" y2="12"></line></svg>
                </div>
                <div>
                  <div>创建需求数量</div>
                  昨日 <span class="number">{{yesterday.create}}</span> | 今日 <span class="number blue">{{today.create}}</span>
                </div>
              </div>
              <div class="kuai">
                <div class="icon-container orange">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.985 10C21.523 14.728 18.391 19 14 19H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v3.985"></path><path d="M19 17v1a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-1"></path><path d="M12 3v4"></path><path d="M9 5h6"></path></svg>
                </div>
                <div>
                  <div>提出Bug数量</div>
                  昨日 <span class="number">{{yesterday.tiChu}}</span> | 今日 <span class="number blue">{{today.tiChu}}</span>
                </div>
              </div>
              <div class="kuai">
                <div class="icon-container red">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20.985 10C21.523 14.728 18.391 19 14 19H6a2 2 0 0 1-2-2V7a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v3.985"></path><path d="M19 17v1a2 2 0 0 1-2 2H6a2 2 0 0 1-2-2v-1"></path><path d="M12 3v4"></path><path d="M9 5h6"></path></svg>
                </div>
                <div>
                  <div>修改Bug数量</div>
                  昨日 <span class="number">{{yesterday.bug}}</span> | 今日 <span class="number blue">{{today.bug}}</span>
                </div>
              </div>
              <div class="kuai">
                <div class="icon-container light-blue">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                </div>
                <div>
                  <div>总消耗工时/h</div>
                  昨日 <span class="number">{{yesterday.clock}}</span> | 今日 <span class="number blue">{{today.clock}}</span>
                </div>
              </div>
              <div class="kuai">
                <div class="icon-container purple">
                  <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><circle cx="12" cy="12" r="10"></circle><polyline points="12 6 12 12 16 14"></polyline></svg>
                </div>
                <div>
                  <div>平均消耗工时/h</div>
                  昨日 <span class="number">{{yesterday.averageClock}}</span> | 今日 <span class="number blue">{{today.averageClock}}</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </el-dialog>
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




<!--      测试统计-->
      <el-card style="max-width: 98%;margin-top: 10px">
        <div class="measure">
          <h3>测试统计</h3>
          <el-divider/>
          <div style="display: flex; gap: 20px;">
            <!-- 左侧：项目列表 -->
            <div style="width: 150px; flex-shrink: 0;">
              <div class="system-list">
                <ProjectList @project-click="handleTestProjectClick"/>
              </div>
            </div>
            <!-- 右侧：Bug统计-->
            <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff; min-height: 200px;">
              <div class="bug-statistics">
                <h3>Bug统计</h3>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">昨天新增</div>
                  <div class="bug-stat-value">{{testStatistics.yesterdayNew}}</div>
                  <el-progress :percentage="testStatistics.yesterdayNew * 100 / 200" />
                </div>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">今日新增</div>
                  <div class="bug-stat-value">{{testStatistics.todayNew}}</div>
                  <el-progress :percentage="testStatistics.todayNew * 100 / 200" />
                </div>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">昨天解决</div>
                  <div class="bug-stat-value">{{testStatistics.yesterdaySolved}}</div>
                  <el-progress :percentage="testStatistics.yesterdaySolved * 100 / 200" />
                </div>
                <div class="bug-stat-item">
                  <div class="bug-stat-label">今日解决</div>
                  <div class="bug-stat-value">{{testStatistics.todaySolved}}</div>
                  <el-progress :percentage="testStatistics.todaySolved * 100 / 200" />
                </div>
              </div>
            </div>
          </div>
          <!-- 下排：Bug修复率和待测试的测试单 -->
          <div style="display: flex; gap: 20px; margin-top: 20px;">
            <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff;">
              <div class="bug-repair">
                <h3>Bug修复率</h3>
                <el-progress type="circle" :percentage="testStatistics.bugRepairRate" />
                <div class="bug-repair-stats">
                  <span>
                    <div class="stat-value">{{testStatistics.validBugs}}</div>
                    <div class="stat-label">有效Bug</div>
                  </span>
                  <span>
                    <div class="stat-value">{{testStatistics.fixedBugs}}</div>
                    <div class="stat-label">已修复</div>
                  </span>
                  <span>
                    <div class="stat-value">{{testStatistics.unclosedBugs}}</div>
                    <div class="stat-label">未关闭</div>
                  </span>
                </div>
              </div>
            </div>
            <div style="flex: 1; border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff;">
              <div class="staying-test-list">
                <h3>待测试的测试单</h3>
                <ul class="test-list">
                  <li v-for="(item, index) in testStatistics.testLists" :key="index">
                    <a href="#" style="cursor: pointer;" @click.prevent="handleTestClick(item)">{{item}}</a>
                  </li>
                  <li v-if="testStatistics.testLists.length === 0">
                    <span style="color: #909399;">暂无待测试的测试单</span>
                  </li>
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
import {ref, onMounted, watch} from "vue";
import { useRouter } from "vue-router";
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

// 初始化路由
const router = useRouter();

// 处理测试单点击事件
const handleTestClick = (testName) => {
  // 跳转到测试列表页面，并传递测试名称作为搜索参数
  router.push(`/test/testList?search=${encodeURIComponent(testName)}`);
};

// 待处理标签
const activeTab = ref(0);
const tabs = ref([
  {name: '审批', hasArrow: true},
  {name: '任务', hasArrow: true},
  {name: 'BUG', hasArrow: true}
]);

//日期
const currentTime = ref(new Date()).value.toLocaleDateString('zh-CN', {
  year: 'numeric', month: 'long', day: 'numeric', weekday: 'long'
})

// 用户信息
const name = ref('');
const bug = ref(0);
const approveState = ref(0);
const taskState = ref(0);
const bugState = ref(0);
const needsState = ref(0);
const userState = ref(0);
const passageState = ref(0);
const userAvatar = ref('https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png');
const fileInput = ref(null);

//我参与的项目数
const projectList = ref([]);

// 从后端获取项目列表数据
const fetchProjects = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get(`/workbench/projects?username=${user.username}`);
      console.log('获取项目列表响应:', response);
      if (response.data.code === 200) {
        projectList.value = response.data.data;
        console.log('转换后的项目列表数据:', projectList.value);
      }
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

// 从后端获取用户信息
const fetchUserInfo = async (username) => {
  try {
    // 从后端获取管理员信息
    const response = await request.get('/admin/findAll');
    console.log('后端返回的用户列表:', response.data.data);
    console.log('要查找的用户名:', username);
    if (response.data.code === 200) {
      // 遍历用户列表，找到匹配的用户
      for (const user of response.data.data) {
        console.log('遍历用户:', user);
        // 直接比较数字类型的username
        if (user.username == username) {
          // 使用数据库中的name字段
          name.value = user.name || user.username || '用户';
          console.log('获取到的用户姓名:', name.value);
          // 如果用户有头像，使用用户的头像
          if (user.avatar) {
            userAvatar.value = getAvatarUrl(user.avatar);
          }
          return;
        }
      }
      console.error('未找到用户信息:', username);
    } else {
      console.error('获取用户信息失败:', response.data.msg);
    }
  } catch (error) {
    console.error('获取用户信息失败:', error);
  }
};

// 从后端获取统计数据
const fetchStatistics = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 从后端获取工作台统计数据
      const response = await request.get(`/workbench/statistics?username=${user.username}`);
      if (response.data.code === 200) {
        // 更新统计数据
        const data = response.data.data;
        bug.value = data.bug || 0;
        approveState.value = data.approveState || 0;
        taskState.value = data.taskState || 0;
        bugState.value = data.bugState || 0;
        needsState.value = data.needsState || 0;
        userState.value = data.userState || 0;
        passageState.value = data.passageState || 0;
      }
    }
  } catch (error) {
    console.error('获取统计数据失败:', error);
  }
};

// 页面加载时获取用户信息、统计数据和项目列表
onMounted(async () => {
  // 从本地存储中获取用户信息
  const userStr = localStorage.getItem('user');
  console.log('从本地存储获取用户信息:', userStr);
  if (userStr) {
    const user = JSON.parse(userStr);
    console.log('解析后的用户信息:', user);
    // 先使用username作为默认值
    name.value = user.username || '用户';
    console.log('默认用户名:', name.value);
    
    // 如果本地存储中有头像，先显示本地存储的头像
    if (user.avatar) {
      userAvatar.value = getAvatarUrl(user.avatar);
    }
    
    // 从后端获取用户的真实姓名和头像
    fetchUserInfo(user.username);
  } else {
    console.error('本地存储中没有用户信息');
  }
  
  // 从后端获取统计数据
  await fetchStatistics();
  
  // 从后端获取项目列表
  await fetchProjects();
  
  // 从后端获取团队完成情况数据
  await fetchTeamStatistics();
  
  // 从后端获取产品总览数据
  await fetchProductOverview();
  // 从后端获取单个项目统计详情
  await fetchProjectDetail();
  
  // 从后端获取项目总览数据
  await fetchProjectOverview();
  
  // 从后端获取任务完成总览数据
  await fetchTaskOverview();
  
  // 从后端获取测试统计数据
  await fetchTestStatistics();
  // 从后端获取未关闭产品统计数据
  await fetchProductStatistics();
  
  // 初始化图表
  setTimeout(() => {
    initProjectChart(projectYearsData.value);
    initTaskChart(taskDistributionData.value);
    initNeedsChart(needsChartData.value);
  }, 100);
});

// 格式化日期
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  });
};

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value.click();
};

// 处理文件选择
const handleFileChange = (event) => {
  const file = event.target.files[0];
  if (file) {
    // 预览头像
    const reader = new FileReader();
    reader.onload = (e) => {
      userAvatar.value = e.target.result;
      // 上传头像到服务器
      uploadAvatar(file);
    };
    reader.readAsDataURL(file);
  }
};

// 上传头像到服务器
const uploadAvatar = async (file) => {
  try {
    const formData = new FormData();
    formData.append('file', file);
    
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 调用后端API上传头像
      const response = await request.post(`/admin/upload-avatar?username=${user.username}`, formData);
      if (response.data.code === 200 && response.data.data) {
        console.log('头像上传成功');
        // 处理返回的头像URL
        const avatarUrl = getAvatarUrl(response.data.data);
        // 更新本地存储中的头像信息
        user.avatar = response.data.data;
        localStorage.setItem('user', JSON.stringify(user));
        // 更新当前显示的头像
        userAvatar.value = avatarUrl;
      }
    }
  } catch (error) {
    console.error('头像上传失败:', error);
  }
};

// 获取用户头像
const fetchUserAvatar = async (username) => {
  try {
    // 调用后端API获取用户头像
    const response = await request.get(`/admin/avatar?username=${username}`);
    if (response.data.code === 200 && response.data.data) {
      userAvatar.value = getAvatarUrl(response.data.data);
    }
  } catch (error) {
    console.error('获取用户头像失败:', error);
  }
};

// 处理头像URL
const getAvatarUrl = (avatarPath) => {
  if (!avatarPath) return 'https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png';
  // 如果存储的是完整http路径，直接返回；否则拼接后端地址
  if (avatarPath.startsWith('http')) {
    return avatarPath;
  }
  // 拼接后端地址，确保前端能正确访问静态资源
  return `http://localhost:8080${avatarPath}`;
};

// 团队完成情况
const yesterday=ref({
  task:0,
  create:0,
  tiChu:0,
  bug:0,
  clock:0,
  averageClock:0
});
const today=ref({
  task:0,
  create:0,
  tiChu:0,
  bug:0,
  clock:0,
  averageClock:0
});

// 产品总览
const projectCount=ref(0);
const thisYearIssue=ref(0);
const closeCount=ref(0);
const productCount=ref(0);

// 单个项目统计详情（项目统计卡片）
const projectDetail = ref({
  projectName: '',
  finishTime: null,
  workTimeTotal: 0,
  workTimeConsumed: 0,
  workTimeRemaining: 0,
  needTotal: 0,
  needFinished: 0,
  needUnclosed: 0,
  taskTotal: 0,
  taskNotStarted: 0,
  taskInProgress: 0,
  bugTotal: 0,
  bugClosed: 0,
  bugUnclosed: 0
});

// 未关闭产品统计
const productStatistics = ref({
  deliveryRate: 0,
  validNeeds: 0,
  deliveredNeeds: 0,
  unclosedNeeds: 0,
  monthFinish: 0,
  monthAdd: 0,
  productList: []
});

// 项目总览
const xiangMuCount=ref(0);
const thisYearFinish=ref(0);
const projectYearsData=ref({
  xAxis:{
    type: 'category',
    data:['2022','2023','2024']
  },
  yAxis:{
    type: 'value',
  },
  series:[{
    type:'bar',
    data:[0,0,0]
  }]
});
const {chartRef: chartDom, initChart: initProjectChart, updateChart: updateProjectChart} = useEcharts();

// 任务数量总览
const taskAllCount=ref(0);
const taskFinishCount=ref(0);
const taskDistributionData=ref({
  xAxis:{
    type: 'category',
    data:['未开始','进行中','已排期']
  },
  yAxis:{
    type: 'value',
  },
  series:[{
    type:'bar',
    data:[0,0,0]
  }]
});
const {chartRef: taskDom, initChart: initTaskChart, updateChart: updateTaskChart} = useEcharts();

// 需求统计图
const needsChartData=ref({
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
        data:[0,0,0,0,0,0]
      },
    {
      name:'add',
      type:'line',
      stack:'Total',
      data:[0,0,0,0,0,0]
    }
  ]
});
const {chartRef: needsDom, initChart: initNeedsChart, updateChart: updateNeedsChart} = useEcharts();

// 测试统计数据
const testStatistics = ref({
  yesterdayNew: 0,
  todayNew: 0,
  yesterdaySolved: 0,
  todaySolved: 0,
  bugRepairRate: 0,
  validBugs: 0,
  fixedBugs: 0,
  unclosedBugs: 0,
  testLists: []
});

// 产品统计相关
const activeProductIndex = ref(-1); // -1表示全部产品

// 控制统计弹窗显示
const dialogVisible = ref(false);

// 控制团队完成情况弹窗显示
const teamDialogVisible = ref(false);

// 控制项目详情弹窗显示
const projectDetailDialogVisible = ref(false);

// 当前项目详情
const currentProjectDetail = ref({
  projectName: '',
  startTime: '',
  finishTime: '',
  remainingTime: '',
  risk: 0,
  teamMembers: [],
  description: ''
});



// 处理产品点击事件
const handleProductClick = async (index, productName) => {
  activeProductIndex.value = index;
  // 这里可以添加从数据库获取对应产品数据的逻辑
  console.log('点击了产品:', productName);
  // 调用后端API获取产品统计数据
  await fetchProductStatistics(productName);
};

// 处理项目点击事件
const handleProjectClick = async (projectName) => {
  console.log('点击了项目:', projectName);
  // 调用后端API获取项目统计数据
  await fetchProjectDetail(projectName);
};

// 处理测试统计项目点击事件
const handleTestProjectClick = async (project) => {
  console.log('点击了测试统计项目:', project);
  // 调用后端API获取项目的测试统计数据
  await fetchTestStatistics(project.name);
};

// 处理未完成项目列表的项目点击事件
const handleUnfinishedProjectClick = async (projectName) => {
  console.log('点击了未完成项目:', projectName);
  // 显示团队完成情况弹窗
  teamDialogVisible.value = true;
};

// 处理项目卡片点击事件，显示项目详情弹窗
const handleProjectCardClick = async (projectName) => {
  console.log('点击了项目卡片:', projectName);
  try {
    // 调用后端API获取项目详细信息
    const response = await request.get('/dashboard/project-info', {
      params: { projectName }
    });
    if (response.data.code === 200) {
      // 更新项目详情数据
      currentProjectDetail.value = {
        projectName: response.data.data.projectName || '暂无项目',
        startTime: response.data.data.startTime || '',
        finishTime: response.data.data.finishTime || '',
        remainingTime: response.data.data.remainingTime || '',
        risk: response.data.data.risk || 0,
        productName: response.data.data.productName || '未知',
        projectManager: response.data.data.projectManager || '未知',
        projectStatus: response.data.data.projectStatus || '未知',
        projectProgress: response.data.data.projectProgress || '0%',
        teamMembers: response.data.data.teamMembers || [],
        description: response.data.data.description || '暂无项目详情',
        projectGoals: response.data.data.projectGoals || [],
        teamName: response.data.data.teamName || '未知团队'
      };
      // 显示项目详情弹窗
      projectDetailDialogVisible.value = true;
    } else {
      console.error('获取项目详情失败:', response.data.msg || '未知错误');
    }
  } catch (error) {
    console.error('获取项目详情失败:', error.message || error || '未知错误');
  }
};

// 跳转到团队模块
const goToTeamModule = () => {
  console.log('跳转到团队模块');
  // 跳转到团队模块的页面
  router.push('/teams/team');
};

// 跳转到项目集模块
const goToProjectModule = () => {
  console.log('跳转到项目集模块');
  // 跳转到项目集模块的页面，并传递项目名称作为筛选条件
  router.push(`/itemSet/itemList?projectName=${encodeURIComponent(currentProjectDetail.value.projectName)}`);
};

// 从后端获取团队完成情况数据
const fetchTeamStatistics = async () => {
  try {
    const response = await request.get('/dashboard/team-statistics');
    if (response.data.code === 200) {
      const data = response.data.data;
      yesterday.value = data.yesterday || yesterday.value;
      today.value = data.today || today.value;
    }
  } catch (error) {
    console.error('获取团队完成情况失败:', error);
  }
};

// 从后端获取产品总览数据
const fetchProductOverview = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let params = {};
    if (userStr) {
      const user = JSON.parse(userStr);
      params = { username: user.username };
    }
    const response = await request.get('/dashboard/product-overview', { params });
    if (response.data.code === 200) {
      const data = response.data.data;
      projectCount.value = data.productCount || 0;
      thisYearIssue.value = data.unfinishedPlanCount || 0;
      closeCount.value = data.unclosedNeedCount || 0;
      productCount.value = data.productCount || 0;
    }
  } catch (error) {
    console.error('获取产品总览失败:', error);
  }
};

// 从后端获取单个项目统计详情
const fetchProjectDetail = async (projectName = '') => {
  try {
    let url = '/dashboard/project-detail';
    if (projectName) {
      url += `?projectName=${encodeURIComponent(projectName)}`;
    }
    const response = await request.get(url);
    if (response.data.code === 200 && response.data.data) {
      projectDetail.value = response.data.data;
    }
  } catch (error) {
    console.error('获取项目统计详情失败:', error);
  }
};

// 从后端获取未关闭产品统计数据
const fetchProductStatistics = async (productName = '') => {
  try {
    let url = '/dashboard/product-statistics';
    if (productName && productName !== '全部产品') {
      url += `?productName=${encodeURIComponent(productName)}`;
    }
    const response = await request.get(url);
    if (response.data.code === 200 && response.data.data) {
      // 保留原有的产品列表，只更新统计数据
      const newData = response.data.data;
      if (newData.productList) {
        productStatistics.value = newData;
      } else {
        // 只更新统计数据，保留产品列表
        productStatistics.value = {
          ...productStatistics.value,
          ...newData
        };
      }
    }
  } catch (error) {
    console.error('获取未关闭产品统计数据失败:', error);
  }
};

// 从后端获取项目总览数据
const fetchProjectOverview = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let params = {};
    if (userStr) {
      const user = JSON.parse(userStr);
      params = { username: user.username };
    }
    const response = await request.get('/dashboard/project-overview', { params });
    if (response.data.code === 200) {
      const data = response.data.data;
      xiangMuCount.value = data.xiangMuCount || 0;
      thisYearFinish.value = data.thisYearFinish || 0;
      if (data.projectYearsData) {
        projectYearsData.value = data.projectYearsData;
        // 更新图表
        updateProjectChart(projectYearsData.value);
      }
    }
  } catch (error) {
    console.error('获取项目总览失败:', error);
  }
};

// 从后端获取任务完成总览数据
const fetchTaskOverview = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let params = {};
    if (userStr) {
      const user = JSON.parse(userStr);
      params = { username: user.username };
    }
    const response = await request.get('/dashboard/task-overview', { params });
    if (response.data.code === 200) {
      const data = response.data.data;
      taskAllCount.value = data.taskAllCount || 0;
      taskFinishCount.value = data.taskFinishCount || 0;
      if (data.taskDistributionData) {
        taskDistributionData.value = data.taskDistributionData;
        // 更新图表
        updateTaskChart(taskDistributionData.value);
      }
    }
  } catch (error) {
    console.error('获取任务完成总览失败:', error);
  }
};



// 从后端获取测试统计数据
const fetchTestStatistics = async (projectName = '') => {
  try {
    let url = '/dashboard/test-statistics';
    if (projectName) {
      url += `?projectName=${encodeURIComponent(projectName)}`;
    }
    const response = await request.get(url);
    if (response.data.code === 200) {
      const data = response.data.data;
      testStatistics.value = {
        yesterdayNew: data.yesterdayNew || 0,
        todayNew: data.todayNew || 0,
        yesterdaySolved: data.yesterdaySolved || 0,
        todaySolved: data.todaySolved || 0,
        bugRepairRate: data.bugRepairRate || 0,
        validBugs: data.validBugs || 0,
        fixedBugs: data.fixedBugs || 0,
        unclosedBugs: data.unclosedBugs || 0,
        testLists: data.testLists || []
      };
    }
  } catch (error) {
    console.error('获取测试统计数据失败:', error);
  }
};

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

/*最新动态样式*/
.dynamic {
  width: 100%;
  box-sizing: border-box;
}

.dynamic h3 {
  margin: 0 0 10px 0;
  padding: 0;
}

.dynamic .el-divider {
  margin: 10px 0;
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
.project-self {
  overflow: hidden;
}

.project-container {
  display: flex;
  width: 700px;
  overflow-x: hidden;
  padding: 10px 0;
}

.project-container:hover {
  overflow-x: auto;
  scrollbar-width: thin;
  scrollbar-color: #409EFF #f0f0f0;
}

.project-container:hover::-webkit-scrollbar {
  height: 6px;
}

.project-container:hover::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 3px;
}

.project-container:hover::-webkit-scrollbar-thumb {
  background: #409EFF;
  border-radius: 3px;
}

.project-item {
  flex: 0 0 calc(33.333% - 10px);
  max-width: calc(33.333% - 10px);
  margin-right: 15px;
  box-sizing: border-box;
}

.project-item:last-child {
  margin-right: 0;
}

.project-card {
  width: 100%;
  padding: 15px;
  border: 1px solid #ebeef5;
  border-radius: 8px;
  background-color: #ffffff;
  transition: all 0.3s ease;
}

.project-card:hover {
  border-color: #409EFF;
  box-shadow: 0 2px 12px 0 rgba(64, 158, 255, 0.1);
}

.project-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.project-title {
  font-size: 16px;
  font-weight: 500;
  color: #409EFF;
  margin: 0;
  flex: 1;
}

.project-arrow {
  cursor: pointer;
  transition: transform 0.3s ease;
}

.project-arrow:hover {
  transform: translateX(3px);
}

.project-member {
  font-size: 14px;
  color: #606266;
  margin: 5px 0;
  display: block;
}

.project-time {
  font-size: 14px;
  color: #606266;
  margin: 5px 0 10px 0;
  display: block;
}

.project-progress {
  margin-top: 10px;
}

.project-progress .el-progress {
  width: 100%;
}

.project-progress .el-progress__text {
  font-size: 12px;
  color: #409EFF;
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
  height: 400px;
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: #409EFF #f0f0f0;
}

.content-area::-webkit-scrollbar {
  width: 6px;
}

.content-area::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 3px;
}

.content-area::-webkit-scrollbar-thumb {
  background: #409EFF;
  border-radius: 3px;
}

/*项目统计样式*/
.project-statistics{
  display: flex;
  gap: 20px;
}

.project-list-container {
  flex: 1;
  min-width: 150px;
}

.exit-count{
  display: inline-block;
  float: right;
  margin-right: 10px;
}

.project-statistics-task{
  flex: 4;
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

/* 弹窗内容布局 */
.dialog-content {
  width: 100%;
}

/* 团队完成情况弹窗布局 */
.team-dialog-content {
  width: 100%;
}

.stats-grid {
  width: 100%;
}

.icon {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
  box-sizing: border-box;
  padding: 0 10px;
}

.kuai {
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

/* 项目详情弹窗样式 */
.project-detail-content {
  width: 100%;
}

.project-detail-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.project-detail-name {
  font-size: 18px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  cursor: pointer;
  color: #409EFF;
}

.project-detail-name:hover {
  text-decoration: underline;
}

.project-detail-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  font-size: 14px;
  color: #606266;
}

.project-detail-body {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.project-basic-info,
.project-team,
.project-goals,
.project-risks,
.project-info {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
}

.project-basic-info h4,
.project-team h4,
.project-goals h4,
.project-risks h4,
.project-info h4 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.basic-info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}

.info-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.info-label {
  font-size: 13px;
  color: #909399;
}

.info-value {
  font-size: 14px;
  color: #303133;
  font-weight: 500;
}

.team-members {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.team-member {
  padding: 10px 15px;
  background-color: #ffffff;
  border: 1px solid #dcdfe6;
  border-radius: 8px;
  font-size: 14px;
  color: #303133;
  cursor: pointer;
  transition: all 0.3s;
  margin: 5px;
  min-width: 120px;
  text-align: center;
}

.team-member:hover {
  background-color: #ecf5ff;
  border-color: #c6e2ff;
}

.member-name {
  font-weight: 500;
  margin-bottom: 4px;
  color: #409EFF;
}

.member-position {
  font-size: 12px;
  color: #909399;
}

.no-members {
  font-size: 14px;
  color: #909399;
  font-style: italic;
}

.goal-list,
.risk-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.goal-list li,
.risk-list li {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin-bottom: 8px;
  padding-left: 20px;
  position: relative;
}

.goal-list li::before,
.risk-list li::before {
  content: '•';
  position: absolute;
  left: 0;
  color: #409EFF;
}

.no-goals,
.no-risks {
  font-size: 14px;
  color: #909399;
  font-style: italic;
  padding-left: 0 !important;
}

.no-goals::before,
.no-risks::before {
  display: none;
}

.project-description {
  font-size: 14px;
  color: #606266;
  line-height: 1.5;
  margin: 0;
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
  gap: 20px;
}

.product-list-container {
  flex: 0 0 150px;
  max-width: 150px;
  //background-color: red;
}

.product-statistics-content {
  flex: 4;
}

.project-list {
  width: 150px;
  list-style: none;
  padding: 0;
  margin: 0;
}

.project-list li {
  padding: 10px 0;
  cursor: pointer;
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  transition: all 0.3s ease;
  padding-left: 10px;
  border-radius: 4px;
}

.project-list li:hover {
  background-color: #ecf5ff;
}

.project-list li.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409EFF;
}

.product-statistics{
  min-height: 250px;
}
.product-statistics-content {
  min-height: 250px;
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
  height: 610px;
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
  text-align: center;
  height: 100%;
}

.bug-repair h3 {
  margin-bottom: 15px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
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
  //padding: 15px;
  height: 250px;
  display: flex;
  flex-direction: column;
}

.staying-test-list .test-list {
  flex: 1;
  overflow-y: auto;
}

.staying-test-list .test-list::-webkit-scrollbar {
  width: 6px;
}

.staying-test-list .test-list::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 3px;
}

.staying-test-list .test-list::-webkit-scrollbar-thumb {
  background: #409EFF;
  border-radius: 3px;
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