<template>
  <div class="team-management">
    <!-- 团队管理卡片 -->
    <el-card style="max-width: 100%; margin-bottom: 10px">
      <div class="card-header">
        <h3>团队管理</h3>
        <div class="header-actions">
          <el-input
            v-model="searchQuery"
            placeholder="搜索团队或项目经理"
            style="width: 200px; margin-right: 10px"
            prefix-icon="el-icon-search"
          />
          <el-button type="primary" @click="showAddTeamDialog">创建团队</el-button>
        </div>
      </div>
      <el-table :data="teamList" style="width: 100%">
        <el-table-column prop="teamId" label="团队编号" width="100">
          <template #default="scope">
            <el-button type="text" @click="showTeamDetail(scope.row)">{{scope.row.teamId}}</el-button>
          </template>
        </el-table-column>
        <el-table-column prop="leader" label="领导人员" width="120" />
        <el-table-column label="团队成员" width="200">
          <template #default="scope">
            <div v-if="scope.row.members && scope.row.members.length > 0" class="member-grid">
              <div v-for="(member, index) in scope.row.members" :key="index" class="member-item">
                {{ member.name }}
              </div>
            </div>
            <div v-else>
              暂无成员
            </div>
          </template>
        </el-table-column>
        <el-table-column label="角色" width="200">
          <template #default="scope">
            <div v-if="scope.row.members && scope.row.members.length > 0" class="role-grid">
              <div v-for="(member, index) in scope.row.members" :key="index" class="role-item">
                {{ member.role }}
              </div>
            </div>
            <div v-else>
              -</div>
          </template>
        </el-table-column>
        <el-table-column label="项目名称" width="250">
          <template #default="scope">
            <div v-if="scope.row.projectName">
              <el-dropdown v-if="scope.row.projectName.includes('、')" trigger="click">
                <span class="el-dropdown-link" style="cursor: pointer; color: #409EFF;">
                  {{ scope.row.projectName.split('、')[0] }} <el-icon class="el-icon--right"><arrow-down /></el-icon>
                </span>
                <template #dropdown>
                  <el-dropdown-menu>
                    <el-dropdown-item v-for="(project, index) in scope.row.projectName.split('、')" :key="index" @click="showProjectDesc(project, scope.row.projectName)">
                      {{ project }}
                    </el-dropdown-item>
                  </el-dropdown-menu>
                </template>
              </el-dropdown>
              <span v-else style="cursor: pointer; color: #409EFF;" @click="showProjectDesc(scope.row.projectName, scope.row.projectName)">{{ scope.row.projectName }}</span>
            </div>
            <div v-else>
              -</div>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column label="已存在天数" width="150">
          <template #default="scope">
            <div v-if="scope.row.createTime">
              {{ calculateDaysExisted(scope.row.createTime) }} 天
            </div>
            <div v-else>
              -
            </div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" type="danger" @click="confirmDisband(scope.row)">解散</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </div>
  
  <!-- 创建团队对话框 -->
  <el-dialog
    v-model="teamDialogVisible"
    :title="isEditTeam ? '编辑团队' : '创建团队'"
    width="500px"
  >
    <el-form :model="formTeam" label-width="100px">
      <el-form-item label="团队编号">
        <el-input v-model="formTeam.teamId" :disabled="isEditTeam" placeholder="自动生成" />
      </el-form-item>
      <el-form-item label="团队名称">
        <el-input v-model="formTeam.teamName" />
      </el-form-item>
      <el-form-item label="领导人员">
        <el-input v-model="formTeam.leader" placeholder="输入领导姓名" />
      </el-form-item>
      <el-form-item label="项目名称">
        <el-input v-model="formTeam.projectName" />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="formTeam.createTime" type="date" placeholder="选择日期" style="width: 100%" />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="teamDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveTeam">保存</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 团队详情对话框 -->
  <el-dialog
    v-model="teamDetailVisible"
    title="团队详情"
    width="600px"
  >
    <el-form :model="selectedTeam" label-width="100px">
      <el-form-item label="团队编号">
        <el-input v-model="selectedTeam.teamId" disabled />
      </el-form-item>
      <el-form-item label="团队名称">
        <el-input v-model="selectedTeam.teamName" disabled />
      </el-form-item>
      <el-form-item label="领导人员">
        <el-input v-model="selectedTeam.leader" disabled />
      </el-form-item>
      <el-form-item label="项目名称">
        <el-input v-model="selectedTeam.projectName" disabled />
      </el-form-item>
      <el-form-item label="项目介绍">
        <el-input v-model="selectedTeam.projectDesc" type="textarea" rows="4" disabled />
      </el-form-item>
      <el-form-item label="团队成员">
        <el-table :data="selectedTeam.members" style="width: 100%">
          <el-table-column prop="name" label="姓名" width="100" />
          <el-table-column prop="role" label="角色" width="100" />
          <el-table-column prop="position" label="职位" />
        </el-table>
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="teamDetailVisible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 确认解散对话框 -->
  <el-dialog
    v-model="confirmDialogVisible"
    title="确认解散团队"
    width="400px"
  >
    <p>确定要解散团队 <strong>{{ teamToDisband?.teamName }}</strong> 吗？</p>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="confirmDialogVisible = false">取消</el-button>
        <el-button type="danger" @click="disbandTeam">确认解散</el-button>
      </span>
    </template>
  </el-dialog>
  
  <!-- 项目描述弹窗 -->
  <el-dialog
    v-model="projectDescVisible"
    :title="`项目描述 - ${selectedProject.name}`"
    width="500px"
  >
    <el-form :model="selectedProject" label-width="100px">
      <el-form-item label="项目名称">
        <el-input v-model="selectedProject.name" disabled />
      </el-form-item>
      <el-form-item label="项目描述">
        <el-input v-model="selectedProject.description" type="textarea" rows="4" disabled />
      </el-form-item>
    </el-form>
    <template #footer>
      <span class="dialog-footer">
        <el-button @click="projectDescVisible = false">关闭</el-button>
      </span>
    </template>
  </el-dialog>
</template>

<script setup>
import {ref, computed, onMounted} from "vue";
import axios from "axios";

// 搜索关键词
const searchQuery = ref('');

// 原始团队列表
const originalTeamList = ref([]);

// 过滤后的团队列表
const teamList = computed(() => {
  if (!searchQuery.value) {
    return originalTeamList.value;
  }
  const query = searchQuery.value.toLowerCase();
  return originalTeamList.value.filter(team => 
    team.teamId.toLowerCase().includes(query) ||
    team.teamName.toLowerCase().includes(query) ||
    team.leader.toLowerCase().includes(query) ||
    team.projectName.toLowerCase().includes(query)
  );
});

// 对话框状态
const teamDialogVisible = ref(false);
const teamDetailVisible = ref(false);
const projectDescVisible = ref(false);
const confirmDialogVisible = ref(false);
const isEditTeam = ref(false);

// 选中的团队和项目
const selectedTeam = ref({
  teamId: '',
  teamName: '',
  leader: '',
  projectName: '',
  projectDesc: '',
  members: []
});

const selectedProject = ref({
  name: '',
  description: ''
});

const teamToDisband = ref(null);

// 表单数据
const formTeam = ref({
  teamId: '',
  teamName: '',
  leader: '',
  projectName: '',
  createTime: ''
});

// 获取团队列表
const fetchTeams = async () => {
  try {
    const response = await axios.get('http://localhost:9091/admin/teams');
    if (response.data.code === 200) {
      originalTeamList.value = response.data.data;
    }
  } catch (error) {
    console.error('获取团队列表失败:', error);
  }
};

// 方法
const showAddTeamDialog = () => {
  isEditTeam.value = false;
  formTeam.value = {
    teamId: '',
    teamName: '',
    leader: '',
    projectName: '',
    createTime: ''
  };
  teamDialogVisible.value = true;
};

const saveTeam = async () => {
  try {
    if (isEditTeam.value) {
      // 编辑团队
      // 这里可以添加编辑团队的 API 调用
    } else {
      // 创建团队
      const response = await axios.post('http://localhost:9091/admin/teams', {
        teamName: formTeam.value.teamName,
        leaderName: formTeam.value.leader,
        projectName: formTeam.value.projectName,
        createTime: formTeam.value.createTime,
        creatorId: 202201 // 假设当前用户的 ID 是 202201
      });
      if (response.data.code === 200) {
        // 重新获取团队列表
        await fetchTeams();
      }
    }
    teamDialogVisible.value = false;
  } catch (error) {
    console.error('保存团队失败:', error);
  }
};

const showTeamDetail = async (team) => {
  try {
    const response = await axios.get(`http://localhost:9091/admin/teams/${team.teamId}`);
    if (response.data.code === 200) {
      selectedTeam.value = {
        ...response.data.data,
        projectDesc: response.data.data.projectName + '是一个现代化的系统，旨在提高教学效率和学生学习体验。'
      };
      teamDetailVisible.value = true;
    }
  } catch (error) {
    console.error('获取团队详情失败:', error);
  }
};

const confirmDisband = (team) => {
  teamToDisband.value = team;
  confirmDialogVisible.value = true;
};

const disbandTeam = async () => {
  if (!teamToDisband.value) return;
  
  try {
    const response = await axios.delete(`http://localhost:9091/admin/teams/${teamToDisband.value.teamId}`);
    if (response.data.code === 200) {
      // 重新获取团队列表
      await fetchTeams();
      // 关闭确认对话框
      confirmDialogVisible.value = false;
      // 重置选中的团队
      teamToDisband.value = null;
    }
  } catch (error) {
    console.error('解散团队失败:', error);
  }
};

const showProjectDesc = (projectName, projectDesc) => {
  selectedProject.value = {
    name: projectName,
    description: projectDesc
  };
  projectDescVisible.value = true;
};

const calculateDaysExisted = (createTime) => {
  if (!createTime) return 0;
  const createdDate = new Date(createTime);
  const now = new Date();
  const timeDiff = Math.abs(now - createdDate);
  return Math.ceil(timeDiff / (1000 * 60 * 60 * 24));
};

// 组件挂载时获取团队列表
onMounted(() => {
  fetchTeams();
});
</script>

<style scoped>

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.card-header h3 {
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
}

.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}

.el-progress {
  margin-top: 5px;
}

.member-grid, .role-grid {
  display: flex;
  flex-direction: column;
  gap: 0;
}

.member-item, .role-item {
  padding: 8px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 0;
  background-color: #ffffff;
  text-align: center;
  margin-bottom: -1px;
}

.member-item:first-child, .role-item:first-child {
  border-top-left-radius: 4px;
  border-top-right-radius: 4px;
}

.member-item:last-child, .role-item:last-child {
  border-bottom-left-radius: 4px;
  border-bottom-right-radius: 4px;
  margin-bottom: 0;
}

</style>