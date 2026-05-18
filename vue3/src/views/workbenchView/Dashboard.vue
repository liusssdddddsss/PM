<template>
  <div class="workComment">
<!--    左边整体-->
    <div class="left">
      <el-card style="max-width: 98%;">
        <div class="message">
          <p>
            {{currentTime}}
          </p>
          <div class="state">
            <div class="label">
              <div class="user-avatar" >
              </div>
              <p @click="showUserInfoDialog" style="cursor: pointer;">{{name}}，上午好</p>
             
            </div>
            <div class="tasks">
              <div class="approve" @click="navigateToModule('products')">
                <p class="tasks-shu">
                  {{projectCount}}
                </p>
                <span>产品总数</span>
              </div>
              <div class="task" @click="navigateToModule('projects')">
                <p class="tasks-shu">
                  {{xiangMuCount}}
                </p>
                <span>项目总数</span>
              </div>
              <div class="bugs" @click="navigateToModule('tasks')">
                <p class="tasks-shu">
                  {{taskAllCount}}
                </p>
                <span>任务数</span>
              </div>
              <div class="needs" @click="navigateToModule('bugs')">
                <p class="tasks-shu">
                  {{bugState}}
                </p>
                <span>Bug数</span>
              </div>
              <div class="users">
                <p class="tasks-shu">
                  {{approveState}}
                </p>
                <span>待审批数</span>
              </div>
            </div>
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
              <div class="project-card" @click="handleProjectCardClick(item.projectName, item.id)">
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

      <!-- 成员信息弹窗 -->
      <el-dialog
        v-model="memberInfoDialogVisible"
        title="成员信息"
        width="500px"
        :close-on-click-modal="false"
      >
        <div class="member-info-content">
          <div class="form-item">
            <label class="form-label">工号</label>
            <el-input 
              v-model="memberInfoForm.username" 
              :disabled="true"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label class="form-label">姓名</label>
            <el-input 
              v-model="memberInfoForm.name" 
              :disabled="true"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label class="form-label">性别</label>
            <el-input 
              v-model="memberInfoForm.sex" 
              :disabled="true"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label class="form-label">邮箱</label>
            <el-input 
              v-model="memberInfoForm.email" 
              :disabled="true"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label class="form-label">部门</label>
            <el-input 
              v-model="memberInfoForm.department" 
              :disabled="true"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label class="form-label">职称</label>
            <el-input 
              v-model="memberInfoForm.position" 
              :disabled="true"
              class="form-input"
            />
          </div>
        </div>
        <template #footer>
          <el-button @click="memberInfoDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- 用户信息弹窗 -->
      <el-dialog
        v-model="userInfoDialogVisible"
        title="个人信息"
        width="500px"
        :close-on-click-modal="false"
      >
        <div class="user-info-content">
          <div class="form-item">
            <label class="form-label">用户名</label>
            <el-input 
              v-model="userInfoForm.username" 
              :disabled="true"
              class="form-input"
            />
          </div>
          <div class="form-item">
            <label class="form-label">姓名</label>
            <el-input 
              v-model="userInfoForm.name" 
              class="form-input"
              placeholder="请输入姓名"
            />
          </div>
          <div class="form-item">
            <label class="form-label">密码</label>
            <el-input 
              v-model="userInfoForm.password" 
              type="password" 
              class="form-input"
              placeholder="请输入密码（不修改请留空）"
              :show-password="showPassword"
            />
            <span class="password-toggle" @click="togglePassword">
              <svg v-if="!showPassword" xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M2 12s3-7 10-7 10 7 10 7-3 7-10 7-10-7-10-7Z"></path>
                <circle cx="12" cy="12" r="3"></circle>
              </svg>
              <svg v-else xmlns="http://www.w3.org/2000/svg" width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"></path>
                <path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"></path>
                <line x1="1" x2="23" y1="1" y2="23"></line>
              </svg>
            </span>
          </div>
          <div class="form-item">
            <label class="form-label">性别</label>
            <el-select v-model="userInfoForm.sex" class="form-input" placeholder="请选择性别">
              <el-option label="男" value="男" />
              <el-option label="女" value="女" />
            </el-select>
          </div>
          <div class="form-item">
            <label class="form-label">邮箱</label>
            <el-input 
              v-model="userInfoForm.email" 
              class="form-input"
              placeholder="请输入邮箱"
            />
          </div>
          <div class="form-item">
            <label class="form-label">部门</label>
            <el-input 
              v-model="userInfoForm.department" 
              class="form-input"
              placeholder="请输入部门"
              :disabled="true"
            />
          </div>
          <div class="form-item">
            <label class="form-label">角色</label>
            <el-select v-model="userInfoForm.role_id" class="form-input" placeholder="请选择角色" :disabled="true">
              <el-option label="产品经理" :value="1" />
              <el-option label="开发者" :value="2" />
              <el-option label="测试者" :value="3" />
              <el-option label="管理员" :value="99" />
            </el-select>
          </div>
          <div class="form-item">
            <label class="form-label">状态</label>
            <el-select v-model="userInfoForm.status" class="form-input" :disabled="true">
              <el-option label="启用" :value="1" />
              <el-option label="禁用" :value="0" />
            </el-select>
          </div>
        </div>
        <template #footer>
          <div class="dialog-footer">
            <el-button @click="userInfoDialogVisible = false">取消</el-button>
            <el-button type="primary" @click="saveUserInfo">保存修改</el-button>
          </div>
        </template>
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
<!--              <span class="remaining-time">剩余时间: {{ currentProjectDetail.remainingTime || '—' }}</span>-->
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
                  @click="showMemberInfo(member)"
                >
                  <div class="member-name">{{ member.name || '未知' }}</div>
                  <div class="member-position">{{ member.position || '未知职位' }}</div>
                </div>
                <span v-if="!currentProjectDetail.teamMembers || currentProjectDetail.teamMembers.length === 0" class="no-members">
                  暂无团队成员
                </span>
              </div>
            </div>
            
            <!-- 项目详情 -->
            <div class="project-info">
              <h4>项目详情</h4>
              <p class="project-description">{{ currentProjectDetail.description || '暂无项目详情' }}</p>
            </div>
            
            <!-- 项目任务列表 -->
            <div class="project-tasks">
              <h4 @click="goToProjectTasks" style="cursor: pointer; color: #238EFF;">任务列表</h4>
              <div class="task-list-container">
                <div 
                  v-for="(task, index) in currentProjectDetail.tasks" 
                  :key="index"
                  class="task-item-row"
                  @click="goToTaskDetail(task.id)"
                >
                  <div class="task-info">
                    <span class="task-title">{{ task.title || '未命名任务' }}</span>
                  </div>
                  <div class="task-status-badge" :class="{ completed: task.status === 3 }">
                    {{ task.status === 3 ? '已完成' : '未完成' }}
                  </div>
                </div>
                <div v-if="!currentProjectDetail.tasks || currentProjectDetail.tasks.length === 0" class="no-tasks">
                  暂无任务
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
            <!-- 右侧：Bug统计和Bug修复率竖排显示 -->
            <div style="flex: 1; display: flex; flex-direction: column; gap: 20px;">
              <!-- Bug统计 -->
              <div style="border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff; min-height: 200px;">
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
              <!-- Bug修复率 -->
              <div style="border: 1px solid #ebeef5; border-radius: 8px; padding: 15px; background-color: #ffffff;">
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
            </div>
          </div>
        </div>
      </el-card>


      
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, watch, computed} from "vue";
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

// 当前用户角色
const userRole = ref(null);

// 判断用户是否为开发者
const isDeveloper = computed(() => {
  // 角色ID：3=开发者
  return userRole.value === 3;
});

// 判断用户是否为开发者或测试者
const isDeveloperOrTester = computed(() => {
  // 角色ID：3=开发者，4=测试者
  const roleId = Number(userRole.value);
  return roleId === 3 || roleId === 4;
});

// 获取用户角色
const fetchUserRole = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('用户信息:', user);
      // 直接从用户对象中获取角色ID
      if (user.role_id !== undefined) {
        userRole.value = user.role_id;
        console.log('从本地存储获取角色ID:', userRole.value);
      } else if (user.roleId !== undefined) {
        userRole.value = user.roleId;
        console.log('从本地存储获取角色ID (roleId):', userRole.value);
      } else {
        // 如果本地存储中没有角色信息，从后端获取
        console.log('本地存储中没有角色信息，从后端获取');
        const response = await request.get('/admin/findAll');
        if (response.data.code === 200) {
          const users = response.data.data;
          const currentUser = users.find(u => u.username == user.username);
          if (currentUser) {
            userRole.value = currentUser.role_id || currentUser.roleId;
            console.log('从后端获取角色ID:', userRole.value);
            // 更新本地存储中的用户信息
            user.role_id = userRole.value;
            localStorage.setItem('user', JSON.stringify(user));
          }
        }
      }
    }
  } catch (error) {
    console.error('获取用户角色失败:', error);
  }
};

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

// 计算并更新所有项目的进度
const calculateAllProjectsProgress = async () => {
  try {
    const response = await request.post('/api/project/calculate-all-progress');
    console.log('计算项目进度响应:', response);
    if (response.data.code === 200) {
      console.log('项目进度计算完成');
      // 重新获取项目列表以获取更新后的进度
      await fetchProjects();
    }
  } catch (error) {
    console.error('计算项目进度失败:', error);
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

// 获取指派给当前用户的Bug数量
const fetchBugCount = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('Dashboard - 获取Bug数量，用户:', user.username, user.name);
      
      // 尝试多个可能的接口获取Bug列表
      const possibleUrls = [
        `/workbench/bugs?username=${user.username}`,
        `/api/bug/list?assignee=${user.username}`
      ];
      
      let bugCount = 0;
      for (const url of possibleUrls) {
        try {
          console.log('Dashboard - 尝试获取Bug列表:', url);
          const response = await request.get(url);
          console.log('Dashboard - Bug列表响应:', response);
          
          if (response.data.code === 200 && response.data.data && Array.isArray(response.data.data)) {
            const bugs = response.data.data;
            console.log('Dashboard - 获取到的Bug列表:', bugs);
            
            // 过滤出指派给当前用户的Bug（未关闭的）
            const userId = String(user.username);
            console.log('Dashboard - 用户ID:', userId);
            
            // 显示所有Bug的assigneeId字段供排查
            console.log('Dashboard - 所有Bug的assigneeId字段:');
            bugs.forEach((bug, index) => {
              console.log(`Dashboard - Bug ${index}: title=${bug.title}, assigneeId=${bug.assigneeId}, status=${bug.status}`);
            });
            
            // 使用 assigneeId 字段匹配用户（后端返回的是驼峰命名）
            const assignedBugs = bugs.filter(bug => {
              const assigneeId = String(bug.assigneeId || '');
              const isAssigned = assigneeId === userId;
              const isNotClosed = !bug.status || bug.status !== 4;
              
              console.log('Dashboard - Bug:', bug.title, 'assigneeId:', assigneeId, 'isAssigned:', isAssigned, 'status:', bug.status, 'isNotClosed:', isNotClosed);
              return isAssigned && isNotClosed;
            });
            
            bugCount = assignedBugs.length;
            console.log('Dashboard - 过滤后的Bug数量:', bugCount);
            break;
          }
        } catch (error) {
          console.log('Dashboard - 请求Bug列表失败:', url, error.message);
        }
      }
      
      bugState.value = bugCount;
      console.log('Dashboard - 最终Bug数量:', bugState.value);
    }
  } catch (error) {
    console.error('Dashboard - 获取Bug数量失败:', error);
    bugState.value = 0;
  }
};

// 获取指派给当前用户的任务数量
const fetchTaskCount = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('Dashboard - 获取任务数量，用户:', user.username, user.name);
      
      // 尝试多个可能的接口获取任务列表
      const possibleUrls = [
        `/workbench/tasks?username=${user.username}`,
        `/api/task/list?assignee=${user.username}`
      ];
      
      let taskCount = 0;
      for (const url of possibleUrls) {
        try {
          console.log('Dashboard - 尝试获取任务列表:', url);
          const response = await request.get(url);
          console.log('Dashboard - 任务列表响应:', response);
          
          if (response.data.code === 200 && response.data.data && Array.isArray(response.data.data)) {
            const tasks = response.data.data;
            console.log('Dashboard - 获取到的任务列表:', tasks);
            
            // 过滤出指派给当前用户的任务（未完成的）
            const userId = String(user.username);
            console.log('Dashboard - 用户ID:', userId);
            
            // 显示所有任务的assignee_id字段供排查
            console.log('Dashboard - 所有任务的assignee_id字段:');
            tasks.forEach((task, index) => {
              console.log(`Dashboard - 任务 ${index}: title=${task.title}, assignee_id=${task.assignee_id}, status=${task.status}`);
            });
            
            // 使用 assignee_id 字段匹配用户（后端返回的是下划线命名）
            const assignedTasks = tasks.filter(task => {
              const assigneeId = String(task.assignee_id || task.assigneeId || '');
              const isAssigned = assigneeId === userId;
              const isNotCompleted = !task.status || task.status < 2;
              
              console.log('Dashboard - 任务:', task.title, 'assignee_id:', assigneeId, 'isAssigned:', isAssigned, 'status:', task.status, 'isNotCompleted:', isNotCompleted);
              return isAssigned && isNotCompleted;
            });
            
            taskCount = assignedTasks.length;
            console.log('Dashboard - 过滤后的任务数量:', taskCount);
            break;
          }
        } catch (error) {
          console.log('Dashboard - 请求任务列表失败:', url, error.message);
        }
      }
      
      taskAllCount.value = taskCount;
      console.log('Dashboard - 最终任务数量:', taskAllCount.value);
    }
  } catch (error) {
    console.error('Dashboard - 获取任务数量失败:', error);
    taskAllCount.value = 0;
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
    
    // 获取用户角色
    await fetchUserRole();
  } else {
    console.error('本地存储中没有用户信息');
  }
  
  // 从后端获取统计数据
  await fetchStatistics();
  
  // 获取指派给当前用户的Bug数量（覆盖之前的bugState）
  await fetchBugCount();
  
  // 获取指派给当前用户的任务数量
  await fetchTaskCount();
  
  // 从后端获取项目列表
  await fetchProjects();
  
  // 计算并更新项目进度
  await calculateAllProjectsProgress();
  
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



// 控制用户信息弹窗显示
const userInfoDialogVisible = ref(false);

// 控制成员信息弹窗显示
const memberInfoDialogVisible = ref(false);

// 成员信息表单
const memberInfoForm = ref({
  username: '',
  name: '',
  sex: '',
  email: '',
  department: '',
  position: ''
});

// 密码显示切换
const showPassword = ref(false);

// 用户信息表单
const userInfoForm = ref({
  username: '',
  name: '',
  password: '',
  sex: '',
  email: '',
  department: '',
  role_id: null,
  status: 1
});

// 控制项目详情弹窗显示
const projectDetailDialogVisible = ref(false);

// 当前项目详情
const currentProjectDetail = ref({
  projectName: '',
  teamName:'',
  startTime: '',
  finishTime: '',
  remainingTime: '',
  risk: 0,
  teamMembers: [],
  description: '',
  productName: '',
  projectManager: '',
  projectStatus: '',
  projectProgress: '',
  projectGoals: [],
  tasks: []
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
  // 确保获取到项目名称
  const projectName = project && project.name ? project.name : '';
  console.log('点击了测试统计项目:', projectName);
  // 调用后端API获取项目的测试统计数据
  await fetchTestStatistics(projectName);
};

// 处理未完成项目列表的项目点击事件
const handleUnfinishedProjectClick = async (projectName) => {
  console.log('点击了未完成项目:', projectName);
  // 跳转到项目列表页面，并传递项目名称作为筛选条件
  router.push(`/itemSet/itemList?projectName=${encodeURIComponent(projectName)}`);
};

// 处理项目卡片点击事件，显示项目详情弹窗
const handleProjectCardClick = async (projectName, projectId) => {
  console.log('点击了项目卡片:', projectName, '项目ID:', projectId);
  try {
    // 调用后端API获取项目详细信息
    const response = await request.get('/dashboard/project-info', {
      params: { projectName }
    });
    
    // 获取项目下的任务列表（使用项目ID）
    let tasks = [];
    try {
      const tasksResponse = await request.get('/workbench/project-tasks', {
        params: { projectId }
      });
      if (tasksResponse.data.code === 200) {
        tasks = tasksResponse.data.data || [];
        console.log('获取到的任务列表:', tasks);
        // 打印每个任务的ID
        tasks.forEach((task, index) => {
          console.log(`任务 ${index}: id=${task.id}, title=${task.title}`);
        });
      }
    } catch (error) {
      console.error('获取任务列表失败:', error);
    }
    
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
        projectGoals: [],
        teamName: response.data.data.teamName || '未知团队',
        tasks: tasks
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

// 显示成员信息弹窗
const showMemberInfo = async (member) => {
  console.log('点击了成员:', member);
  
  // 重置表单
  memberInfoForm.value = {
    username: '',
    name: '',
    sex: '',
    email: '',
    department: '',
    position: ''
  };
  
  // 如果成员有username，从后端获取详细信息
  if (member.username) {
    try {
      const response = await request.get('/admin/user-info', {
        params: { username: member.username }
      });
      if (response.data.code === 200 && response.data.data) {
        const userData = response.data.data;
        memberInfoForm.value = {
          username: userData.username || '',
          name: userData.name || '',
          sex: userData.sex || '',
          email: userData.email || '',
          department: userData.department || '',
          position: member.position || ''
        };
      }
    } catch (error) {
      console.error('获取成员信息失败:', error);
    }
  } else {
    // 如果没有username，使用已有信息
    memberInfoForm.value = {
      username: '',
      name: member.name || '',
      sex: '',
      email: '',
      department: '',
      position: member.position || ''
    };
  }
  
  // 显示弹窗
  memberInfoDialogVisible.value = true;
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

// 跳转到任务模块，筛选显示当前项目的所有任务
const goToProjectTasks = () => {
  console.log('跳转到任务模块，显示项目:', currentProjectDetail.value.projectName);
  // 关闭弹窗
  projectDetailDialogVisible.value = false;
  // 跳转到任务列表页面，传递项目名称作为筛选条件
  router.push(`/task/taskList?projectName=${encodeURIComponent(currentProjectDetail.value.projectName)}`);
};

// 跳转到任务模块，显示具体的任务详情
const goToTaskDetail = (taskId) => {
  console.log('跳转到任务详情，任务ID:', taskId);
  // 关闭弹窗
  projectDetailDialogVisible.value = false;
  // 跳转到任务列表页面，传递任务ID
  router.push(`/task/taskList?taskId=${taskId}`);
};

// 导航到对应模块页面
const navigateToModule = (module) => {
  console.log('跳转到模块:', module);
  // 从本地存储中获取用户信息
  const userStr = localStorage.getItem('user');
  const username = userStr ? JSON.parse(userStr).username : '';
  
  switch (module) {
    case 'products':
      router.push('/productResearch/productDashboard');
      break;
    case 'projects':
      router.push('/itemSet/itemList');
      break;
    case 'tasks':
      // 跳转到任务列表页面，并传递用户名为参数，以便只显示该用户负责的任务
      router.push(`/task/taskList?assignee=${encodeURIComponent(username)}`);
      break;
    case 'bugs':
      // 跳转到Bug列表页面，并传递用户名为参数，以便只显示该用户负责的Bug
      router.push(`/test/bugList?assignee=${encodeURIComponent(username)}`);
      break;
    default:
      break;
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
      // taskAllCount 已由 fetchTaskCount() 设置，不再覆盖
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
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let url = '/dashboard/test-statistics';
    if (projectName) {
      url += `?projectName=${encodeURIComponent(projectName)}`;
    }
    if (userStr) {
      const user = JSON.parse(userStr);
      if (url.includes('?')) {
        url += `&username=${user.username}`;
      } else {
        url += `?username=${user.username}`;
      }
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

// 显示用户信息弹窗
const showUserInfoDialog = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 从后端获取用户详细信息
      const response = await request.get(`/admin/user-info?username=${user.username}`);
      if (response.data.code === 200) {
        const userData = response.data.data;
        userInfoForm.value = {
          username: userData.username || '',
          name: userData.name || '',
          password: '',
          sex: userData.sex ? userData.sex.toString() : '',
          email: userData.email || '',
          department: userData.department || '',
          role_id: userData.role_id || null,
          status: userData.status || 1
        };
      } else {
        // 如果后端获取失败，使用本地存储的信息
        userInfoForm.value = {
          username: user.username || '',
          name: user.name || user.username || '',
          password: '',
          sex: '',
          email: '',
          department: '',
          role_id: user.role_id || null,
          status: 1
        };
      }
    }
    userInfoDialogVisible.value = true;
  } catch (error) {
    console.error('获取用户信息失败:', error);
    alert('获取用户信息失败');
  }
};

// 切换密码显示
const togglePassword = () => {
  showPassword.value = !showPassword.value;
};

// 保存用户信息
const saveUserInfo = async () => {
  try {
    const response = await request.put('/admin/update-user', userInfoForm.value);
    if (response.data.code === 200) {
      // 更新成功后，更新本地存储的用户信息
      const userStr = localStorage.getItem('user');
      if (userStr) {
        const user = JSON.parse(userStr);
        user.name = userInfoForm.value.name;
        user.role_id = userInfoForm.value.role_id;
        localStorage.setItem('user', JSON.stringify(user));
        // 更新页面上显示的用户名
        name.value = userInfoForm.value.name;
      }
      userInfoDialogVisible.value = false;
      alert('用户信息更新成功');
    } else {
      alert('用户信息更新失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('更新用户信息失败:', error);
    alert('更新用户信息失败');
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

/* 用户信息弹窗样式 */
.user-info-content {
  padding: 20px 0;
}

.form-item {
  margin-bottom: 20px;
}

.form-label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 500;
  color: #303133;
}

.form-input {
  width: 100%;
}

.password-toggle {
  display: inline-block;
  margin-left: -30px;
  cursor: pointer;
  color: #909399;
  vertical-align: middle;
}

.password-toggle:hover {
  color: #409EFF;
}

.dialog-footer {
  text-align: right;
}

.dialog-footer .el-button {
  margin-left: 10px;
}

/* 编辑图标样式 */
.label {
  cursor: pointer;
  position: relative;
}

.label:hover .edit-icon {
  opacity: 1;
}

.edit-icon {
  opacity: 0;
  transition: opacity 0.3s;
  margin-left: 8px;
  vertical-align: middle;
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
.project-info,
.project-tasks {
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
}

.project-basic-info h4,
.project-team h4,
.project-goals h4,
.project-risks h4,
.project-info h4,
.project-tasks h4 {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 10px;
}

.task-list-container {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
}

.task-item-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 14px;
  background-color: #ffffff;
  border-radius: 6px;
  border: 1px solid #e4e7ed;
  cursor: pointer;
  transition: all 0.2s ease;
}

.task-item-row:hover {
  background-color: #f5f7fa;
  border-color: #c0c4cc;
}

.task-item-row:active {
  background-color: #e8eaed;
}

.task-title {
  font-size: 14px;
  color: #303133;
  font-weight: 400;
}

.task-status-badge {
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  background-color: #f5f7fa;
  color: #909399;
}

.task-status-badge.completed {
  background-color: #e8f5e9;
  color: #67c23a;
}

.no-tasks {
  font-size: 14px;
  color: #909399;
  text-align: center;
  padding: 20px;
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
  cursor: pointer;
  transition: all 0.2s ease;
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