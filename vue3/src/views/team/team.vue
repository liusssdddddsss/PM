<template>
  <div class="team-container">
    <div class="team-header">
      <h3>团队管理</h3>
      <div class="header-actions">
        <button class="btn">创建团队</button>
        <button class="btn">邀请成员</button>
      </div>
    </div>

    <div class="team-content">
      <!-- 所属团队 -->
      <div class="team-section">
        <h3>所属团队</h3>
        <div class="team-cards">
          <div class="team-card">
            <div class="team-info">
              <h4>产品研发团队</h4>
              <p>成员: 12人</p>
              <p>项目: 5个</p>
            </div>
            <button class="btn-small" onclick="switchTeam('product')">切换</button>
          </div>
          <div class="team-card">
            <div class="team-info">
              <h4>测试团队</h4>
              <p>成员: 8人</p>
              <p>项目: 3个</p>
            </div>
            <button class="btn-small" onclick="switchTeam('test')">切换</button>
          </div>
          <div class="team-card">
            <div class="team-info">
              <h4>设计团队</h4>
              <p>成员: 5人</p>
              <p>项目: 4个</p>
            </div>
            <button class="btn-small" onclick="switchTeam('design')">切换</button>
          </div>
        </div>
      </div>

      <!-- 团队分工 -->
      <div class="team-section">
        <h3>团队分工</h3>
        <div class="division-table">
          <table border="1" width="100%">
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
              <td><button class="btn-small" onclick="editDivision(item)">编辑</button></td>
            </tr>
          </table>
        </div>
      </div>

      <!-- 个人进度 -->
      <div class="team-section">
        <h3>个人进度</h3>
        <div class="progress-list">
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
              <button class="btn-small" onclick="viewDetails(member)">查看详情</button>
              <button class="btn-small warn" onclick="remindMember(member)">催促</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 任务进展 -->
      <div class="team-section">
        <h3>任务进展</h3>
        <div class="task-progress">
          <table border="1" width="100%">
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
                <button class="btn-small" onclick="viewTask(task)">查看</button>
                <button class="btn-small warn" onclick="remindTask(task)">催促</button>
              </td>
            </tr>
          </table>
        </div>
      </div>

      <!-- 消息提示 -->
      <div class="team-section">
        <h3>消息提示</h3>
        <div class="message-list">
          <div class="message-item" v-for="(message, index) in messages" :key="index">
            <div class="message-content">
              <p><strong>{{ message.sender }}</strong> <span class="message-time">{{ message.time }}</span></p>
              <p>{{ message.content }}</p>
              <p v-if="!message.read"><strong>未读</strong></p>
              <div class="message-actions">
                <button class="btn-small" onclick="replyMessage(message)">回复</button>
                <button class="btn-small" onclick="markAsRead(message)">标记已读</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';

// 团队分工数据
const divisionData = ref([
  {
    member: '张三',
    role: '产品经理',
    responsibility: '负责产品规划、需求分析和用户研究',
    projects: '智慧教室、电子班牌'
  },
  {
    member: '孙七',
    role: 'UI设计师',
    responsibility: '负责用户界面设计和用户体验优化',
    projects: '智慧教室、电子班牌'
  }
]);

// 个人进度数据
const progressData = ref([
  {
    name: '张三',
    role: '产品经理',
    completionRate: 85,
    currentTasks: 3,
    overdueTasks: 0,
    unreadMessages: 2
  },
  {
    name: '孙七',
    role: 'UI设计师',
    completionRate: 50,
    currentTasks: 6,
    overdueTasks: 2,
    unreadMessages: 0
  }
]);

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
  }
]);

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
</script>

<style scoped>
.team-container {
  padding: 10px;
  background-color: #fff;
  min-height: 100vh;
}

.team-header {
  margin-bottom: 20px;
}

.team-header h3 {
  margin-top: 0;
  color: #333;
}

.header-actions {
  margin-top: 10px;
}

.btn {
  padding: 5px 10px;
  background-color: #1890ff;
  color: white;
  border: none;
  cursor: pointer;
  margin-right: 10px;
}

.btn-small {
  padding: 3px 8px;
  background-color: #1890ff;
  color: white;
  border: none;
  cursor: pointer;
}

.btn-small.warn {
  background-color: #ff9800;
}

.team-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.team-section {
  border: 1px solid #ccc;
  padding: 15px;
  margin-bottom: 15px;
}

.team-section h3 {
  margin-top: 0;
  color: #333;
  border-bottom: 1px solid #ccc;
  padding-bottom: 5px;
}

/* 所属团队 */
.team-cards {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.team-card {
  width: 250px;
  border: 1px solid #ccc;
  padding: 10px;
}

.team-info h4 {
  margin: 0 0 5px 0;
  color: #333;
}

.team-info p {
  margin: 3px 0;
}

/* 团队分工 */
.division-table table {
  border-collapse: collapse;
  width: 100%;
}

.division-table th, .division-table td {
  border: 1px solid #ccc;
  padding: 8px;
}

.division-table th {
  background-color: #f0f0f0;
}

/* 个人进度 */
.progress-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.progress-item {
  border: 1px solid #ccc;
  padding: 10px;
}

.member-info {
  margin-bottom: 10px;
}

.member-name {
  font-weight: bold;
  margin-right: 10px;
}

.member-role {
  color: #666;
  margin-right: 10px;
}

.message-badge {
  color: red;
}

.progress-details p {
  margin: 3px 0;
}

.member-actions {
  margin-top: 10px;
}

/* 任务进展 */
.task-progress table {
  border-collapse: collapse;
  width: 100%;
}

.task-progress th, .task-progress td {
  border: 1px solid #ccc;
  padding: 8px;
}

.task-progress th {
  background-color: #f0f0f0;
}

/* 消息提示 */
.message-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.message-item {
  border: 1px solid #ccc;
  padding: 10px;
}

.message-content p {
  margin: 3px 0;
}

.message-time {
  color: #999;
}

.message-actions {
  margin-top: 10px;
}
</style>