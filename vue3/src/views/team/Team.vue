<template>
  <div class="team-container">
    <div class="left">
      <!-- 团队概览 -->
      <el-card style="max-width: 98%;">
        <div class="team-header">
          <h3>团队管理</h3>
          <div class="header-actions">
            <button class="btn" @click="createTeam">创建团队</button>
            <button class="btn" @click="inviteMember">邀请成员</button>
<!--            <button class="btn" @click="exportReport">导出报告</button>-->
          </div>
        </div>
        <div class="overview-cards">
          <div class="overview-card">
            <div class="overview-icon team-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"></path><circle cx="9" cy="7" r="4"></circle><path d="M23 21v-2a4 4 0 0 0-3-3.87"></path><path d="M16 3.13a4 4 0 0 1 0 7.75"></path></svg>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ totalTeams }}</div>
              <div class="overview-label">团队数量</div>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon member-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2"></path><circle cx="12" cy="7" r="4"></circle></svg>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ totalMembers }}</div>
              <div class="overview-label">总成员数</div>
            </div>
          </div>
          <div class="overview-card" style="cursor: pointer;" @click="goToProjectModule">
            <div class="overview-icon project-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M22 19a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V5a2 2 0 0 1 2-2h5l2 3h9a2 2 0 0 1 2 2z"></path></svg>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ totalProjects }}</div>
              <div class="overview-label">项目数量</div>
            </div>
          </div>
          <div class="overview-card">
            <div class="overview-icon message-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            </div>
            <div class="overview-content">
              <div class="overview-value">{{ totalMessages }}</div>
              <div class="overview-label">消息数</div>
            </div>
          </div>
        </div>
      </el-card>



      <!-- 所属团队和团队分工 -->
      <el-card style="max-width: 98%; margin-top: 10px">
        <h3>团队管理</h3>
        <el-divider />
        <!-- 所属团队 -->
        <div class="team-section" style="margin-bottom: 10px">
          <h4 style="margin-bottom: 10px">所属团队</h4>
          <div class="team-cards">
            <div 
              class="team-card" 
              v-for="(team, index) in teams" 
              :key="index" 
              @click="switchTeam(team.name)"
              :class="{ active: currentTeam === team.name }"
            >
              <div class="team-info">
                <h4 class="team-name">{{ team.name }}</h4>
                <div class="team-stats">
                    <div class="stat-item">
                      <span class="stat-label">成员</span>
                      <span class="stat-value">{{ team.memberDetails ? team.memberDetails.length : 0 }}人</span>
                    </div>
                  </div>
              </div>
              <div class="team-actions">
                <button class="btn-small" @click.stop="switchTeam(team.name)">切换</button>
                <br>
                <button class="btn-small warn" @click.stop="disbandTeam(team)">解散</button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 团队分工 -->
        <div class="division-section">
          <h4>团队分工</h4>
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
                <td><button class="btn-small warn" @click="deleteDivision(item)">删除</button></td>
              </tr>
            </table>
          </div>
        </div>
      </el-card>
    </div>

    <div class="right">


      <!-- 团队公告和消息提示 -->
      <el-card style="max-width: 98%;">
        
        <!-- 消息提示 -->
        <div class="content-scroll">
          <div v-if="messages.length > 0" class="message-list">
            <div class="message-item" v-for="(message, index) in messages" :key="index">
              <div class="message-content">
                <p><strong>{{ message.sender }}</strong> <span class="message-time">{{ message.time }}</span></p>
                <p class="message-text">{{ message.content }}</p>
              </div>
            </div>
          </div>
          <div v-else class="empty-state">
            <div class="empty-icon">
              <svg xmlns="http://www.w3.org/2000/svg" width="48" height="48" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"></path></svg>
            </div>
            <div class="empty-text">暂无消息</div>
            <div class="empty-desc">您还没有收到任何消息</div>
          </div>
        </div>
      </el-card>

      <!-- 创建团队弹窗 -->
      <el-dialog
        v-model="showCreateTeamDialog"
        title="创建团队"
        width="500px"
      >
        <el-form :model="createTeamForm" label-width="100px">
          <el-form-item label="团队名称">
            <el-input v-model="createTeamForm.name" placeholder="请输入团队名称"></el-input>
          </el-form-item>
          <el-form-item label="团队描述">
            <el-input type="textarea" v-model="createTeamForm.description" placeholder="请输入团队描述"></el-input>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showCreateTeamDialog = false">取消</el-button>
            <el-button type="primary" @click="submitCreateTeam">确定</el-button>
          </span>
        </template>
      </el-dialog>

      <!-- 邀请成员弹窗 -->
      <el-dialog
        v-model="showInviteMemberDialog"
        title="邀请成员"
        width="500px"
      >
        <el-form :model="inviteMemberForm" label-width="100px">
          <el-form-item label="团队">
            <el-select v-model="inviteMemberForm.teamId" placeholder="请选择团队">
              <el-option
                v-for="team in teams"
                :key="team.name"
                :label="team.name"
                :value="team.name"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="用户名">
            <el-select
              v-model="inviteMemberForm.username"
              placeholder="请输入用户名搜索"
              filterable
              remote
              :remote-method="searchUser"
              :loading="loading"
              style="width: 100%"
            >
              <el-option
                v-for="user in searchUsers"
                :key="user.value"
                :label="user.label"
                :value="user.value"
              ></el-option>
            </el-select>
          </el-form-item>
          <el-form-item label="角色">
            <el-select v-model="inviteMemberForm.role" placeholder="请选择角色">
              <el-option label="项目经理" value="项目经理"></el-option>
              <el-option label="开发" value="开发"></el-option>
              <el-option label="测试工程师" value="测试工程师"></el-option>
            </el-select>
          </el-form-item>
        </el-form>
        <template #footer>
          <span class="dialog-footer">
            <el-button @click="showInviteMemberDialog = false">取消</el-button>
            <el-button type="primary" @click="submitInviteMember">确定</el-button>
          </span>
        </template>
      </el-dialog>




    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import request from '@/utils/request';
import { ElMessage, ElMessageBox } from 'element-plus';

// 初始化路由
const router = useRouter();

// 团队数据（从后端获取，只包含当前登录用户所在团队）
const teams = ref([]);

// 团队分工数据
const divisionData = ref([]);





// 当前选中的团队
const currentTeam = ref('');



// 创建团队弹窗
const showCreateTeamDialog = ref(false);
const createTeamForm = ref({
  name: '',
  description: ''
});

// 邀请成员弹窗
const showInviteMemberDialog = ref(false);
const inviteMemberForm = ref({
  teamId: '',
  username: '',
  role: ''
});

// 搜索用户相关
const searchUsers = ref([]);
const loading = ref(false);
const queryString = ref('');

// 搜索用户
const searchUser = async (query) => {
  if (query.length < 1) {
    searchUsers.value = [];
    return;
  }
  loading.value = true;
  try {
    const response = await request.get(`/admin/users/search?keyword=${query}`);
    if (response.data.code === 200) {
      searchUsers.value = response.data.data.map(user => ({
        label: `${user.name} (${user.userId})`,
        value: user.userId
      }));
    }
  } catch (error) {
    console.error('搜索用户失败:', error);
    searchUsers.value = [];
  } finally {
    loading.value = false;
  }
};





// 消息数据
const messages = ref([]);

// 团队公告数据（按团队分类）
const teamAnnouncements = ref({
  // 'xx产品团队': [
  //   {
  //     id: 1,
  //     title: '团队周会通知',
  //     content: '本周团队周会将于周五下午2点在会议室A举行，请所有团队成员准时参加。会议将讨论下周工作计划和项目进度。',
  //     date: '2023-05-28',
  //     author: '张三'
  //   },
  //   {
  //     id: 2,
  //     title: '系统升级通知',
  //     content: '系统将于本周末进行升级维护，预计维护时间为周六上午9点至下午3点。期间系统将暂时不可用，请提前做好工作安排。',
  //     date: '2023-05-25',
  //     author: '李四'
  //   }
  // ],
  // '研发团队': [
  //   {
  //     id: 1,
  //     title: '技术分享会',
  //     content: '下周三下午将举行前端技术分享会，主题为Vue3新特性，请团队成员积极参加。',
  //     date: '2023-06-01',
  //     author: '王五'
  //   },
  //   {
  //     id: 2,
  //     title: '代码规范更新',
  //     content: '团队代码规范已更新，请所有成员按照新规范进行代码编写。',
  //     date: '2023-05-20',
  //     author: '赵六'
  //   }
  // ],
  // '测试团队': [
  //   {
  //     id: 1,
  //     title: '测试计划变更',
  //     content: '下周测试计划有所调整，请查看最新的测试用例文档。',
  //     date: '2023-05-30',
  //     author: '孙七'
  //   }
  // ]
});



// 概览统计（从后端获取）
const overview = ref({
  totalTeams: 0,
  totalMembers: 0,
  totalProjects: 0,
  totalMessages: 0
});

// 计算属性
const totalTeams = computed(() => overview.value.totalTeams || 0);
const totalMembers = computed(() => overview.value.totalMembers || 0);
const totalProjects = computed(() => overview.value.totalProjects || 0);
const totalMessages = computed(() => overview.value.totalMessages || 0);

// 从后端获取团队概览和所属团队（仅限当前登录用户）
const fetchTeamData = async () => {
  try {
    console.log('开始获取团队数据');
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      console.error('用户未登录');
      return;
    }
    const user = JSON.parse(userStr);
    const username = user.username;
    console.log('获取团队数据，用户名:', username);

    // 概览数据
    console.log('开始获取团队概览数据');
    const overviewRes = await request.get(`/teams/overview?username=${username}`);
    console.log('获取团队概览数据响应:', overviewRes);
    if (overviewRes.data.code === 200 && overviewRes.data.data) {
      overview.value = overviewRes.data.data;
      console.log('更新团队概览数据:', overview.value);
    } else {
      console.error('获取团队概览数据失败:', overviewRes.data.msg || '未知错误');
    }

    // 所属团队列表
    console.log('开始获取所属团队列表');
    const teamsRes = await request.get(`/teams/my-teams?username=${username}`);
    console.log('获取所属团队列表响应:', teamsRes);
    if (teamsRes.data.code === 200 && Array.isArray(teamsRes.data.data)) {
      teams.value = teamsRes.data.data;
      console.log('更新所属团队列表:', teams.value);
      
      // 默认选中第一个团队
      if (teams.value.length > 0) {
        currentTeam.value = teams.value[0].name;
        console.log('默认选中团队:', currentTeam.value);
      }
      
      // 处理团队成员数据，构建团队分工
      await processTeamMembers();
    } else {
      console.error('获取所属团队列表失败:', teamsRes.data.msg || '未知错误');
    }
  } catch (e) {
    console.error('获取团队数据失败:', e);
  }
};

// 根据角色编号获取角色名称
const getRoleName = (roleId) => {
  const roleMap = {
    '1': '超级管理员',
    '2': '产品经理',
    '3': '开发者',
    '4': '测试者'
  };
  return roleMap[roleId] || '成员';
};

// 处理团队成员数据
const processTeamMembers = async () => {
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
            username: member.username,
            teams: [team.name]
          });
        } else {
          // 如果成员已经存在，添加项目和团队
          const existingMember = allMembers.get(member.userId);
          if (!existingMember.projects.includes(team.name)) {
            existingMember.projects.push(team.name);
          }
          if (!existingMember.teams.includes(team.name)) {
            existingMember.teams.push(team.name);
          }
        }
      });
    }
  });
  
  // 构建团队分工数据
  if (currentTeam.value) {
    // 只显示当前选中团队的分工
    divisionData.value = Array.from(allMembers.values())
      .filter(member => member.teams.includes(currentTeam.value))
      .map(member => ({
        userId: member.id,
        username: member.username,
        member: member.name,
        role: getRoleName(member.role),
        responsibility: getResponsibilityByRole(getRoleName(member.role)),
        projects: member.projects.join('、')
      }));
  } else {
    // 显示所有团队的分工
    divisionData.value = Array.from(allMembers.values()).map(member => ({
      userId: member.id,
      username: member.username,
      member: member.name,
      role: getRoleName(member.role),
      responsibility: getResponsibilityByRole(getRoleName(member.role)),
      projects: member.projects.join('、')
    }));
  }
  

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

// 从后端获取消息数据
const fetchMessages = async () => {
  try {
    // 获取当前选中的团队
    if (currentTeam.value) {
      // 找到当前团队的ID
      const teamObj = teams.value.find(t => t.name === currentTeam.value);
      if (teamObj && teamObj.id) {
        console.log('开始获取消息数据，团队ID:', teamObj.id);
        const response = await request.get(`/teams/messages/by-team?teamId=${teamObj.id}`);
        console.log('获取消息数据响应:', response);
        if (response.data && response.data.code === 200) {
          console.log('消息数据:', response.data.data);
          // 确保数据是数组
          if (Array.isArray(response.data.data)) {
            // 格式化消息数据，确保时间格式正确
            messages.value = response.data.data.map(msg => ({
              id: msg.id || Math.random(),
              sender: msg.sender || '系统',
              content: msg.content || '',
              time: msg.createdAt ? new Date(msg.createdAt).toLocaleString('zh-CN') : '',
              read: msg.isRead === 1
            }));
            console.log('格式化后的消息数据:', messages.value);
            
            // 如果没有消息，添加默认的团队消息
            if (messages.value.length === 0) {
              messages.value = [
                {
                  id: Math.random(),
                  sender: '系统',
                  content: `欢迎加入${currentTeam.value}团队！这是您的团队消息中心，您将在这里收到团队相关的通知和消息。`,
                  time: new Date().toLocaleString('zh-CN'),
                  read: 1
                }
              ];
            }
          } else {
            console.error('消息数据格式错误:', response.data.data);
            // 添加默认的团队消息
            messages.value = [
              {
                id: Math.random(),
                sender: '系统',
                content: `欢迎加入${currentTeam.value}团队！这是您的团队消息中心，您将在这里收到团队相关的通知和消息。`,
                time: new Date().toLocaleString('zh-CN'),
                read: 1
              }
            ];
          }
        } else {
          console.error('获取消息失败:', response.data?.msg || '未知错误');
          // 添加默认的团队消息
          messages.value = [
            {
              id: Math.random(),
              sender: '系统',
              content: `欢迎加入${currentTeam.value}团队！这是您的团队消息中心，您将在这里收到团队相关的通知和消息。`,
              time: new Date().toLocaleString('zh-CN'),
              read: 1
            }
          ];
        }
      } else {
        console.error('团队ID不存在');
        messages.value = [];
      }
    } else {
      console.error('未选择团队');
      messages.value = [];
    }
  } catch (error) {
    console.error('获取消息失败:', error);
    // 添加默认的团队消息
    if (currentTeam.value) {
      messages.value = [
        {
          id: Math.random(),
          sender: '系统',
          content: `欢迎加入${currentTeam.value}团队！这是您的团队消息中心，您将在这里收到团队相关的通知和消息。`,
          time: new Date().toLocaleString('zh-CN'),
          read: 1
        }
      ];
    } else {
      messages.value = [];
    }
  }
};

onMounted(async () => {
  await fetchTeamData();
  // 确保默认显示第一个所属团队的消息列表
  if (teams.value.length > 0 && currentTeam.value) {
    await fetchMessages();
  }
});

// 切换团队
async function switchTeam(team) {
  console.log('切换到团队:', team);
  currentTeam.value = team;
  // 重新处理团队成员数据，只显示当前选中团队的分工
  await processTeamMembers();
  // 更新当前团队的消息
  await fetchMessages();
}

// 解散团队
async function disbandTeam(team) {
  try {
    // 弹出确认对话框
    await ElMessageBox.confirm(
      `确定要解散团队"${team.name}"吗？此操作将删除团队的所有数据，不可恢复。`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    // 调用后端API
    const response = await request.delete(`/teams/disband/${team.id}`);
    if (response.data.code === 200) {
      ElMessage.success('团队解散成功');
      // 重新获取团队数据
      await fetchTeamData();
    } else {
      ElMessage.error(response.data.msg || '团队解散失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('解散团队失败:', error);
      ElMessage.error('团队解散失败');
    }
  }
}

// 删除团队成员
async function deleteDivision(row) {
  try {
    // 弹出确认对话框
    await ElMessageBox.confirm(
      `确定要从团队中删除成员"${row.member}"吗？`,
      '警告',
      {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning',
      }
    );
    
    // 获取团队ID
    const teamId = teams.value.find(t => t.name === currentTeam.value)?.id;
    if (!teamId) {
      ElMessage.error('团队ID不存在');
      return;
    }
    
    // 调用后端API
    const response = await request.delete('/teams/remove-member', {
      data: {
        teamId: teamId,
        userId: row.userId
      }
    });
    if (response.data.code === 200) {
      ElMessage.success('成员删除成功');
      // 重新获取团队数据
      await fetchTeamData();
    } else {
      ElMessage.error(response.data.msg || '成员删除失败');
    }
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除成员失败:', error);
      ElMessage.error('成员删除失败');
    }
  }
}



// 回复消息
async function replyMessage(message) {
  try {
    // 获取当前用户信息
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      ElMessage.error('用户未登录');
      return;
    }
    const user = JSON.parse(userStr);
    
    // 弹出回复对话框（这里简化处理，实际应用中应该使用el-dialog）
    const replyContent = prompt('请输入回复内容:', '');
    if (replyContent) {
      // 创建回复消息
      const response = await request.post('/teams/messages', {
        sender: user.username,
        receiver: message.sender,
        content: replyContent,
        teamId: null // 可以根据实际情况设置团队ID
      });
      if (response.data.code === 200) {
        ElMessage.success('回复消息已发送');
        // 重新获取消息数据
        fetchMessages();
      } else {
        ElMessage.error(response.data.msg || '发送回复消息失败');
      }
    }
  } catch (error) {
    console.error('回复消息失败:', error);
    ElMessage.error('发送回复消息失败');
  }
}

// 标记已读
async function markAsRead(message) {
  try {
    // 调用后端API
    const response = await request.put('/teams/messages/read', {
      messageId: message.id
    });
    if (response.data.code === 200) {
      message.read = true;
      ElMessage.success('标记消息为已读成功');
    } else {
      ElMessage.error(response.data.msg || '标记消息为已读失败');
    }
  } catch (error) {
    console.error('标记消息为已读失败:', error);
    ElMessage.error('标记消息为已读失败');
  }
}

// 创建团队
function createTeam() {
  // 显示创建团队弹窗
  showCreateTeamDialog.value = true;
}

// 提交创建团队表单
async function submitCreateTeam() {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 调用创建团队的API
      const response = await request.post('/teams/create', {
        name: createTeamForm.value.name,
        description: createTeamForm.value.description,
        username: String(user.username)
      });
      if (response.data.code === 200) {
        // 提交成功后关闭弹窗
        showCreateTeamDialog.value = false;
        // 重置表单
        createTeamForm.value = {
          name: '',
          description: ''
        };
        // 重新获取团队数据
        fetchTeamData();
        // 显示成功消息
        ElMessage.success('团队创建成功');
      } else {
        // 显示错误消息
        ElMessage.error(response.data.msg || '团队创建失败');
      }
    }
  } catch (error) {
    console.error('创建团队失败:', error);
    ElMessage.error('团队创建失败');
  }
}

// 邀请成员
function inviteMember() {
  // 显示邀请成员弹窗
  showInviteMemberDialog.value = true;
}

// 提交邀请成员表单
async function submitInviteMember() {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      ElMessage.error('用户未登录');
      return;
    }
    const user = JSON.parse(userStr);
    
    // 找到当前选择的团队对象，获取团队ID
    const selectedTeam = teams.value.find(team => team.name === inviteMemberForm.value.teamId);
    if (!selectedTeam) {
      ElMessage.error('团队不存在');
      return;
    }
    
    // 调用邀请成员的API
    const response = await request.post('/teams/invite', {
      teamId: selectedTeam.id.toString(),
      username: String(inviteMemberForm.value.username),
      role: inviteMemberForm.value.role,
      operator: user.username
    });
    if (response.data.code === 200) {
      // 提交成功后关闭弹窗
      showInviteMemberDialog.value = false;
      // 重置表单
      inviteMemberForm.value = {
        teamId: '',
        username: '',
        role: ''
      };
      // 重新获取团队数据
      fetchTeamData();
      // 显示成功消息
      ElMessage.success('成员邀请成功');
    } else {
      // 显示错误消息
      ElMessage.error(response.data.msg || '成员邀请失败');
    }
  } catch (error) {
    console.error('邀请成员失败:', error);
    ElMessage.error('成员邀请失败');
  }
}

// 导出报告
function exportReport() {
  // 实现导出报告的功能
  console.log('导出团队报告');
  // 这里可以添加导出报告的逻辑
}

// 跳转到项目集模块
function goToProjectModule() {
  console.log('跳转到项目集模块');
  router.push('/itemSet/itemList');
}

// 跳转到任务模块
function goToTaskModule() {
  console.log('跳转到任务模块');
  router.push('/task/taskList');
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

.message-icon {
  background-color: #e3f2fd;
  color: #1976d2;
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
  background-color: #238EFF;
  color: white;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  font-weight: 500;
  transition: none;
  margin-top: 5px;
}

.btn-small.warn {
  background-color: #E3F2FD;
  color: #1976D2;
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
  background-color: #ffffff;
  border-radius: 8px;
  padding: 15px;
  transition: all 0.3s ease;
  cursor: pointer;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  border: 1px solid #e9ecef;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.05);
  min-height: 60px;
}

.team-card.active {
  background-color: #e6f7ff;
  border: 1px solid #1890ff;
  box-shadow: 0 4px 8px rgba(24, 144, 255, 0.2);
}

.team-info h4 {
  margin: 0 0 12px 0;
  font-size: 16px;
  font-weight: 600;
  color: #343a40;
}

/* 表格样式 */
.division-table table {
  border-collapse: collapse;
  width: 100%;
  border-radius: 8px;
  overflow: hidden;
  border: 1px solid #e9ecef;
  box-shadow: none;
  background-color: transparent;
}

.division-table th,
.division-table td {
  padding: 12px 16px;
  text-align: left;
  border: 1px solid #e9ecef;
}

.division-table th {
  background-color: transparent;
  font-weight: 600;
  color: #495057;
  border-bottom: 2px solid #f1f3f5;
}

.division-table tr {
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

/* 空状态提示 */
.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  text-align: center;
  color: #6c757d;
}

.empty-icon {
  margin-bottom: 16px;
  color: #adb5bd;
}

.empty-text {
  font-size: 16px;
  font-weight: 500;
  margin-bottom: 8px;
  color: #495057;
}

.empty-desc {
  font-size: 14px;
  color: #6c757d;
}

/* 标签页样式 */
.tab-container {
  width: 100%;
  height: 640px;
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

.message-text {
  font-size: 14px;
  line-height: 1.5;
  color: #343a40;
  margin: 12px 0;
  white-space: pre-line;
}

.message-time {
  color: #6c757d;
  font-size: 11px;
  margin-left: 8px;
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