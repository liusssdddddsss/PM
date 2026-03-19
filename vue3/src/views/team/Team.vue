<template>
  <div class="team-container">
    <div class="left">
      <!-- 团队概览 -->
      <el-card style="max-width: 98%;">
        <div class="team-header">
          <h3>团队管理</h3>
          <div class="header-actions">
            <button class="btn">创建团队</button>
            <button class="btn">邀请成员</button>
            <button class="btn">导出报告</button>
          </div>
        </div>
        <div class="overview-cards">
          <div class="overview-card">
            <div class="overview-icon team-icon">👥</div>
            <div class="overview-content">
              <div class="overview-value">{{ totalTeams }}</div>
              <div class="overview-label">团队数量</div>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon member-icon">👤</div>
            <div class="overview-content">
              <div class="overview-value">{{ totalMembers }}</div>
              <div class="overview-label">总成员数</div>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon project-icon">📁</div>
            <div class="overview-content">
              <div class="overview-value">{{ totalProjects }}</div>
              <div class="overview-label">项目数量</div>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon task-icon">✅</div>
            <div class="overview-content">
              <div class="overview-value">{{ totalTasks }}</div>
              <div class="overview-label">任务总数</div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 任务进展 -->
      <el-card style="max-width: 98%; margin-top: 10px">
        <h3>任务进展</h3>
        <el-divider />
        <div class="task-progress">
          <table width="100%">
            <tr>
              <th width="200">任务</th>
              <th width="120">负责人</th>
              <th width="150">截止日期</th>
              <th width="100">进度</th>
              <th width="100">状态</th>
              <th width="150">操作</th>
            </tr>
            <tr v-for="(task, index) in taskProgressData" :key="index">
              <td>{{ task.task }}</td>
              <td>{{ task.assignee }}</td>
              <td>{{ task.deadline }}</td>
              <td>{{ task.progress }}%</td>
              <td>{{ task.status }}</td>
              <td>
                <button class="btn-small" @click="viewTask(task)">查看</button>
                <button class="btn-small warn" @click="remindTask(task)">催促</button>
              </td>
            </tr>
          </table>
        </div>
      </el-card>

      <!-- 所属团队 -->
      <el-card style="max-width: 98%; margin-top: 10px">
        <h3>所属团队</h3>
        <el-divider />
        <div class="team-cards">
          <div class="team-card" v-for="(team, index) in teams" :key="index" @click="switchTeam(team.name)">
            <div class="team-info">
              <h4 class="team-name">{{ team.name }}</h4>
              <div class="team-stats">
                <div class="stat-item">
                  <span class="stat-label">成员</span>
                  <span class="stat-value">{{ team.members }}人</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">项目</span>
                  <span class="stat-value">{{ team.projects }}个</span>
                </div>
                <div class="stat-item">
                  <span class="stat-label">任务</span>
                  <span class="stat-value">{{ team.tasks }}个</span>
                </div>
              </div>
            </div>
            <button class="btn-small" @click.stop="switchTeam(team.name)">切换</button>
          </div>
        </div>
      </el-card>

      <!-- 团队分工 -->
      <el-card style="max-width: 98%; margin-top: 10px">
        <h3>团队分工</h3>
        <el-divider />
        <div class="division-table">
          <table width="100%">
            <tr>
              <th width="120">成员</th>
              <th width="100">角色</th>
              <th width="200">职责</th>
              <th width="150">负责项目</th>
              <th width="120">操作</th>
            </tr>
            <tr v-for="(item, index) in divisionData" :key="index">
              <td>{{ item.member }}</td>
              <td>{{ item.role }}</td>
              <td>{{ item.responsibility }}</td>
              <td>{{ item.projects }}</td>
              <td><button class="btn-small" @click="editDivision(item)">编辑</button></td>
            </tr>
          </table>
        </div>
      </el-card>
    </div>

    <div class="right">
      <!-- 个人进度 -->
      <el-card style="max-width: 98%;">
        <h3>个人进度</h3>
        <el-divider />
        <div class="progress-container">
          <div class="progress-scroll">
            <div class="progress-item" v-for="(member, index) in progressData" :key="index">
              <div class="member-info">
                <span class="member-name">{{ member.name }}</span>
                <span class="member-role">{{ member.role }}</span>
                <span v-if="member.unreadMessages > 0" class="message-badge">消息: {{ member.unreadMessages }}</span>
              </div>
              <div class="progress-details">
                <p>任务完成率: {{ member.completionRate }}%</p>
                <p>当前任务: {{ member.currentTasks }}个</p>
                <p>逾期任务: {{ member.overdueTasks }}个</p>
              </div>
              <div class="member-actions">
                <button class="btn-small" @click="viewDetails(member)">查看详情</button>
                <button class="btn-small warn" @click="remindMember(member)">催促</button>
              </div>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 团队公告和消息提示 -->
      <el-card style="max-width: 98%; margin-top: 10px">
        
        <!-- 标签页 -->
        <div class="tab-container">
          <div class="tabs">
            <div 
              class="tab" 
              :class="{ active: activeTab === 'announcements' }"
              @click="activeTab = 'announcements'"
            >
              团队公告
            </div>
            <div 
              class="tab" 
              :class="{ active: activeTab === 'messages' }"
              @click="activeTab = 'messages'"
            >
              消息提示
            </div>
          </div>
          
          <!-- 标签内容 -->
          <div class="tab-content">
            <!-- 团队公告内容 -->
            <div v-if="activeTab === 'announcements'" class="tab-pane">
              <div class="content-scroll">
                <div class="announcement-list">
                  <div class="announcement-item" v-for="(announcement, index) in announcements" :key="index">
                    <div class="announcement-header">
                      <span class="announcement-title">{{ announcement.title }}</span>
                      <span class="announcement-date">{{ announcement.date }}</span>
                    </div>
                    <p class="announcement-content">{{ announcement.content }}</p>
                    <div class="announcement-author">发布人: {{ announcement.author }}</div>
                  </div>
                </div>
              </div>
            </div>
            
            <!-- 消息提示内容 -->
            <div v-if="activeTab === 'messages'" class="tab-pane">
              <div class="content-scroll">
                <div class="message-list">
                  <div class="message-item" v-for="(message, index) in messages" :key="index">
                    <div class="message-content">
                      <p><strong>{{ message.sender }}</strong> <span class="message-time">{{ message.time }}</span></p>
                      <p>{{ message.content }}</p>
                      <p v-if="!message.read"><strong>未读</strong></p>
                      <div class="message-actions">
                        <button class="btn-small" @click="replyMessage(message)">回复</button>
                        <button class="btn-small" @click="markAsRead(message)">标记已读</button>
                      </div>
                    </div>
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
import { ref, computed, onMounted } from 'vue';
import request from '@/utils/request.js';

// 团队数据（从后端获取，只包含当前登录用户所在团队）
const teams = ref([]);

// 团队分工数据
const divisionData = ref([]);

// 个人进度数据
const progressData = ref([]);

// 标签页状态
const activeTab = ref('announcements');

// 任务进展数据
const taskProgressData = ref([
  {
    task: '智慧教室前端页面开发',
    assignee: '李四',
    deadline: '2023-06-15',
    progress: 60,
    status: '进行中'
  },
  {
    task: '产品需求文档编写',
    assignee: '张三',
    deadline: '2023-05-30',
    progress: 85,
    status: '待审核'
  }
]);

// 消息数据
const messages = ref([
  {
    id: 1,
    sender: '张三',
    content: '智慧教室项目需求文档已更新，请查看',
    time: '2023-05-28 14:30',
    read: false
  },
  {
    id: 2,
    sender: '赵六',
    content: '电子班牌测试报告已生成，请查看',
    time: '2023-05-27 16:20',
    read: true
  },
  {
    id: 3,
    sender: '李四',
    content: '前端页面开发遇到问题，需要后端API支持',
    time: '2023-05-26 10:15',
    read: true
  },
  {
    id: 4,
    sender: '王五',
    content: '数据库设计方案已完成，请审阅',
    time: '2023-05-25 15:45',
    read: false
  }
]);

// 公告数据
const announcements = ref([
  {
    id: 1,
    title: '团队周会通知',
    content: '本周团队周会将于周五下午2点在会议室A举行，请所有团队成员准时参加。会议将讨论下周工作计划和项目进度。',
    date: '2023-05-28',
    author: '张三'
  },
  {
    id: 2,
    title: '系统升级通知',
    content: '系统将于本周末进行升级维护，预计维护时间为周六上午9点至下午3点。期间系统将暂时不可用，请提前做好工作安排。',
    date: '2023-05-25',
    author: '李四'
  },
  {
    id: 3,
    title: '新成员加入',
    content: '欢迎王五加入我们的团队，担任后端开发工程师一职。请大家相互熟悉，共同协作。',
    date: '2023-05-20',
    author: '张三'
  }
]);

// 概览统计（从后端获取）
const overview = ref({
  totalTeams: 0,
  totalMembers: 0,
  totalProjects: 0,
  totalTasks: 0
});

// 计算属性
const totalTeams = computed(() => overview.value.totalTeams || 0);
const totalMembers = computed(() => overview.value.totalMembers || 0);
const totalProjects = computed(() => overview.value.totalProjects || 0);
const totalTasks = computed(() => overview.value.totalTasks || 0);

// 从后端获取团队概览和所属团队（仅限当前登录用户）
const fetchTeamData = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (!userStr) return;
    const user = JSON.parse(userStr);
    const username = user.username;

    // 概览数据
    const overviewRes = await request.get(`/team/overview?username=${username}`);
    if (overviewRes.data.code === 200 && overviewRes.data.data) {
      overview.value = overviewRes.data.data;
    }

    // 所属团队列表
    const teamsRes = await request.get(`/team/my-teams?username=${username}`);
    if (teamsRes.data.code === 200 && Array.isArray(teamsRes.data.data)) {
      teams.value = teamsRes.data.data;
      
      // 处理团队成员数据，构建团队分工和个人进度
      processTeamMembers();
    }
  } catch (e) {
    console.error('获取团队数据失败:', e);
  }
};

// 处理团队成员数据
const processTeamMembers = () => {
  const allMembers = new Map();
  
  // 收集所有成员信息
  teams.value.forEach(team => {
    if (team.memberDetails && Array.isArray(team.memberDetails)) {
      team.memberDetails.forEach(member => {
        if (!allMembers.has(member.userId)) {
          allMembers.set(member.userId, {
            id: member.userId,
            name: member.name,
            role: member.role,
            projects: [team.name],
            username: member.username
          });
        } else {
          // 如果成员已经存在，添加项目
          const existingMember = allMembers.get(member.userId);
          if (!existingMember.projects.includes(team.name)) {
            existingMember.projects.push(team.name);
          }
        }
      });
    }
  });
  
  // 构建团队分工数据
  divisionData.value = Array.from(allMembers.values()).map(member => ({
    member: member.name,
    role: member.role || '成员',
    responsibility: getResponsibilityByRole(member.role),
    projects: member.projects.join('、')
  }));
  
  // 构建个人进度数据
  progressData.value = Array.from(allMembers.values()).map(member => ({
    name: member.name,
    role: member.role || '成员',
    completionRate: Math.floor(Math.random() * 100), // 模拟数据
    currentTasks: Math.floor(Math.random() * 10), // 模拟数据
    overdueTasks: Math.floor(Math.random() * 3), // 模拟数据
    unreadMessages: Math.floor(Math.random() * 5) // 模拟数据
  }));
};

// 根据角色获取职责描述
const getResponsibilityByRole = (role) => {
  const roleResponsibilities = {
    '项目经理': '负责项目规划、协调和管理',
    '产品经理': '负责产品规划、需求分析和用户研究',
    '前端开发': '负责前端页面开发和交互实现',
    '后端开发': '负责后端系统开发和数据库设计',
    'UI设计师': '负责用户界面设计和用户体验优化',
    '测试工程师': '负责系统测试和质量保证',
    '运维工程师': '负责系统部署和维护'
  };
  return roleResponsibilities[role] || '参与项目开发和维护';
};

onMounted(() => {
  fetchTeamData();
});

// 切换团队
function switchTeam(team) {
  console.log('切换到团队:', team);
}

// 编辑分工
function editDivision(row) {
  console.log('编辑分工:', row);
}

// 查看详情
function viewDetails(member) {
  console.log('查看详情:', member);
}

// 催促成员
function remindMember(member) {
  console.log('催促成员:', member);
}

// 查看任务
function viewTask(task) {
  console.log('查看任务:', task);
}

// 催促任务
function remindTask(task) {
  console.log('催促任务:', task);
}

// 回复消息
function replyMessage(message) {
  console.log('回复消息:', message);
}

// 标记已读
function markAsRead(message) {
  message.read = true;
  console.log('标记已读:', message);
}

// 导出报告
function exportReport() {
  console.log('导出团队报告');
}
</script>

<style scoped>
/* 全局样式 */
.team-container {
  display: flex;
  justify-content: space-between;
  min-height: 100vh;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
}

.left {
  max-width: 65%;
}

.right {
  flex: 1;
}

/* 概览区域 */
.overview-cards {
  display: flex;
  gap: 8px;
  margin-top: 20px;
}

.overview-card {
  background-color: transparent;
  border-radius: 8px;
  padding: 10px;
  width: 180px;
  display: flex;
  align-items: center;
  gap: 12px;
  transition: none;
  border: 1px solid #e9ecef;
  box-shadow: none;
}

.overview-icon {
  font-size: 32px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background-color: #f8f9fa;
}

.team-icon {
  background-color: #e3f2fd;
  color: #1976d2;
}

.member-icon {
  background-color: #f3e5f5;
  color: #7b1fa2;
}

.project-icon {
  background-color: #e8f5e8;
  color: #388e3c;
}

.task-icon {
  background-color: #fff3e0;
  color: #ef6c00;
}

.overview-content {
  flex: 1;
}

.overview-value {
  font-size: 24px;
  font-weight: 600;
  color: #343a40;
  margin: 0;
}

.overview-label {
  font-size: 14px;
  color: #6c757d;
  margin: 4px 0 0 0;
}

/* 公告样式 */
.announcement-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.announcement-item {
  background-color: transparent;
  border-radius: 8px;
  padding: 14px;
  transition: none;
  border-left: 4px solid #007bff;
  border: 1px solid #e9ecef;
  box-shadow: none;
}

.announcement-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.announcement-title {
  font-size: 14px;
  font-weight: 600;
  color: #343a40;
}

.announcement-date {
  font-size: 11px;
  color: #6c757d;
}

.announcement-content {
  font-size: 12px;
  color: #495057;
  margin: 8px 0;
  line-height: 1.5;
}

.announcement-author {
  font-size: 11px;
  color: #6c757d;
  margin-top: 8px;
  text-align: right;
}

/* 团队卡片样式 */
.team-name {
  font-size: 14px;
  font-weight: 600;
  color: #343a40;
  margin: 0 0 8px 0;
}

.team-stats {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stat-label {
  font-size: 12px;
  color: #6c757d;
}

.stat-value {
  font-size: 12px;
  font-weight: 500;
  color: #343a40;
}

/* 头部样式 */
.team-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 2px solid #e9ecef;
}

.team-header h3 {
  margin: 0;
  font-size: 20px;
  font-weight: 600;
  color: #343a40;
}

.header-actions {
  display: flex;
  gap: 12px;
  margin-top: 0;
}

/* 按钮样式 */
.btn {
  padding: 8px 16px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 500;
  transition: none;
  margin-right: 0;
}

.btn-small {
  padding: 6px 12px;
  background-color: #007bff;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: none;
}

.btn-small.warn {
  background-color: #ffc107;
  color: #212529;
}

/* 所属团队 */
.team-cards {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 16px;
  flex-wrap: wrap;
}

.team-card {
  width: 100%;
  background-color: transparent;
  border-radius: 8px;
  padding: 5px;
  transition: none;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border: 1px solid #e9ecef;
  box-shadow: none;
}

.team-info h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #343a40;
}

/* 表格样式 */
.division-table table,
.task-progress table {
  border-collapse: collapse;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e9ecef;
  box-shadow: none;
  background-color: transparent;
}

.division-table th,
.division-table td,
.task-progress th,
.task-progress td {
  padding: 12px 16px;
  text-align: left;
  border: 1px solid #e9ecef;
}

.division-table th,
.task-progress th {
  background-color: transparent;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #f1f3f5;
}

.division-table tr,
.task-progress tr {
  transition: none;
}

/* 个人进度 */
.progress-container {
  overflow: hidden;
  width: 100%;
}

.progress-scroll {
  display: flex;
  gap: 16px;
  overflow-x: auto;
  padding-bottom: 10px;
  scrollbar-width: thin;
  scrollbar-color: #409EFF #f0f0f0;
  width: 100%;
  box-sizing: border-box;
}

.progress-scroll::-webkit-scrollbar {
  height: 6px;
}

.progress-scroll::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 3px;
}

.progress-scroll::-webkit-scrollbar-thumb {
  background: #409EFF;
  border-radius: 3px;
}

/* 确保右侧栏内容不超出 */
.right {
  flex: 1;
  min-width: 0;
  overflow: hidden;
}

.progress-item {
  background-color: transparent;
  border-radius: 8px;
  padding: 14px;
  transition: none;
  border: 1px solid #e9ecef;
  box-shadow: none;
  min-width: 180px;
  flex: 0 0 auto;
}

.member-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.member-name {
  font-weight: 600;
  font-size: 14px;
  color: #343a40;
  margin-right: 0;
}

.member-role {
  color: #6c757d;
  font-size: 12px;
  margin-right: 0;
  background-color: #e3f2fd;
  padding: 4px 12px;
  border-radius: 16px;
}

.message-badge {
  margin-left: auto;
  background-color: #dc3545;
  color: white;
  padding: 4px 12px;
  border-radius: 16px;
  font-size: 11px;
  font-weight: 500;
}

.progress-details {
  margin-bottom: 16px;
}

.progress-details p {
  margin: 8px 0;
  font-size: 12px;
  color: #495057;
}

.member-actions {
  display: flex;
  gap: 12px;
  margin-top: 16px;
}

/* 消息提示 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

/* 标签页样式 */
.tab-container {
  width: 100%;
  height: 650px;
}

.tabs {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
  border-bottom: 1px solid #e9ecef;
}

.tab {
  padding: 8px 12px;
  cursor: pointer;
  font-size: 14px;
  color: #6c757d;
  position: relative;
  transition: all 0.3s ease;
}

.tab:hover {
  color: #007bff;
}

.tab.active {
  color: #007bff;
  font-weight: 500;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 12px;
  right: 12px;
  height: 2px;
  background-color: #007bff;
}

.tab-content {
  width: 100%;
}

.tab-pane {
  padding: 8px 0;
}

/* 内容滚动区域 */
.content-scroll {
  max-height: 600px; /* 设置固定高度 */
  overflow-y: auto; /* 启用垂直滚动 */
  scrollbar-width: thin;
  scrollbar-color: #409EFF #f0f0f0;
}

.content-scroll::-webkit-scrollbar {
  width: 6px;
}

.content-scroll::-webkit-scrollbar-track {
  background: #f0f0f0;
  border-radius: 3px;
}

.content-scroll::-webkit-scrollbar-thumb {
  background: #409EFF;
  border-radius: 3px;
}

.message-item {
  background-color: transparent;
  border-radius: 8px;
  padding: 14px;
  transition: none;
  border-left: 4px solid #e9ecef;
  border: 1px solid #e9ecef;
  box-shadow: none;
}

.message-content p {
  margin: 8px 0;
  font-size: 12px;
  color: #495057;
}

.message-time {
  color: #6c757d;
  font-size: 11px;
}

.message-actions {
  display: flex;
  gap: 8px;
  margin-top: 12px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .team-container {
    flex-direction: column;
    padding: 12px;
  }
  
  .left,
  .right {
    max-width: 100%;
    width: 100%;
  }
  
  .team-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .header-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .team-cards {
    grid-template-columns: 1fr;
  }
  
  .member-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }
  
  .message-badge {
    margin-left: 0;
  }
  
  .member-actions {
    width: 100%;
    justify-content: space-between;
  }
}
</style>