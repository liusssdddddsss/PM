<template>
  <div class="team-management">
    <!-- 团队管理卡片 -->
    <el-card style="max-width: 98%; margin-bottom: 10px">
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
        <el-table-column prop="projectName" label="项目名称" />
        <el-table-column prop="progress" label="项目进度" width="100">
          <template #default="scope">
            <el-progress :percentage="scope.row.progress" :format="() => scope.row.progress + '%'" />
          </template>
        </el-table-column>
        <el-table-column prop="progressNode" label="进度节点" width="120" />
        <el-table-column prop="createTime" label="创建时间" width="150" />
        <el-table-column prop="deadline" label="截止时间" width="150" />
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button size="small" type="danger" @click="disbandTeam(scope.row)">解散</el-button>
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
        <el-input v-model="formTeam.teamId" :disabled="isEditTeam" />
      </el-form-item>
      <el-form-item label="团队名称">
        <el-input v-model="formTeam.teamName" />
      </el-form-item>
      <el-form-item label="领导人员">
        <el-input v-model="formTeam.leader" />
      </el-form-item>
      <el-form-item label="项目名称">
        <el-input v-model="formTeam.projectName" />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="formTeam.createTime" type="date" placeholder="选择日期" style="width: 100%" />
      </el-form-item>
      <el-form-item label="截止时间">
        <el-date-picker v-model="formTeam.deadline" type="date" placeholder="选择日期" style="width: 100%" />
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
</template>

<script setup>
import {ref, computed} from "vue";

// 搜索关键词
const searchQuery = ref('');

// 原始团队列表
const originalTeamList = ref([
  {
    teamId: 'T001',
    teamName: '智慧教室团队',
    leader: '张三',
    projectName: '智慧教室系统',
    progress: 80,
    progressNode: '测试阶段',
    createTime: '2024-01-01',
    deadline: '2024-06-30'
  },
  {
    teamId: 'T002',
    teamName: '实践教学平台团队',
    leader: '李四',
    projectName: '实践教学平台',
    progress: 50,
    progressNode: '开发阶段',
    createTime: '2024-02-01',
    deadline: '2024-07-31'
  },
  {
    teamId: 'T003',
    teamName: '在线考试系统团队',
    leader: '王五',
    projectName: '在线考试系统',
    progress: 20,
    progressNode: '需求分析',
    createTime: '2024-03-01',
    deadline: '2024-08-31'
  }
]);

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
const isEditTeam = ref(false);

// 表单数据
const formTeam = ref({
  teamId: '',
  teamName: '',
  leader: '',
  projectName: '',
  createTime: '',
  deadline: ''
});

const selectedTeam = ref({
  teamId: '',
  teamName: '',
  leader: '',
  projectName: '',
  projectDesc: '',
  members: []
});

// 方法
const showAddTeamDialog = () => {
  isEditTeam.value = false;
  formTeam.value = {
    teamId: '',
    teamName: '',
    leader: '',
    projectName: '',
    createTime: '',
    deadline: ''
  };
  teamDialogVisible.value = true;
};

const saveTeam = () => {
  if (isEditTeam.value) {
    // 编辑团队
    const index = originalTeamList.value.findIndex(team => team.teamId === formTeam.value.teamId);
    if (index !== -1) {
      originalTeamList.value[index] = {...formTeam.value};
    }
  } else {
    // 创建团队
    originalTeamList.value.push({...formTeam.value, progress: 0, progressNode: '初始化'});
  }
  teamDialogVisible.value = false;
};

const showTeamDetail = (team) => {
  // 模拟团队详情数据
  selectedTeam.value = {
    ...team,
    projectDesc: `${team.projectName}是一个现代化的系统，旨在提高教学效率和学生学习体验。`,
    members: [
      {name: team.leader, role: '项目经理', position: '产品经理'},
      {name: '开发者1', role: '开发', position: '前端开发工程师'},
      {name: '开发者2', role: '开发', position: '后端开发工程师'},
      {name: '测试1', role: '测试', position: '测试工程师'}
    ]
  };
  teamDetailVisible.value = true;
};

const disbandTeam = (team) => {
  const index = originalTeamList.value.findIndex(t => t.teamId === team.teamId);
  if (index !== -1) {
    originalTeamList.value.splice(index, 1);
  }
};
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
</style>