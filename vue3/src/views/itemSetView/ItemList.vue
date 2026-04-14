<template>
<!--  项目集-->
  <div class="projects-comment">
    <div class="option">
      <span
          v-for="tab in tabs"
          :key="tab.type"
          :class="{active:activeTab===tab.type}"
          @click="activeTab=tab.type"
      >
        {{tab.name}}
      </span>
      <div class="search-add-container">
        <div class="search-box">
          <el-input
              v-model="searchQuery"
              placeholder="搜索项目名称"
              size="small"
              class="search-input"
          />
        </div>
        <div class="addProject">
<!--        <el-button @click="goToItemEdit">
          编辑项目
        </el-button>-->
          <el-button class="button" @click="goToAddProject">
            添加项目
          </el-button>
        </div>
      </div>
    </div>
    <div class="list">
      <div class="project-table-container">
        <div class="table-container">
          <el-table
              :data="filteredData"
              style="width: 100%"
              class="ProjectTable"
              :row-style="{height: '45px'}"
              :cell-style="{padding: '4px'}"
          >
            <el-table-column label="序号" width="60">
              <template #default="scope">
                {{ scope.$index + 1 }}
              </template>
            </el-table-column>
            <el-table-column prop="title" label="项目名称" min-width="120">
              <template #default="scope">
                <span class="project-name">{{ scope.row.title }}</span>
              </template>
            </el-table-column>
            <el-table-column prop="person" label="负责人" width="90"></el-table-column>
            <el-table-column prop="startTime" label="开始时间" width="130"></el-table-column>
            <el-table-column prop="finishTime" label="预计完成时间" width="130"></el-table-column>
            <el-table-column prop="jinDu" label="进度" width="80">
              <template #default="scope">
                <el-progress type="circle" :percentage="scope.row.jinDu" :width="20" :stroke-width="3" />
              </template>
            </el-table-column>
            <el-table-column prop="states" label="状态" width="80">
              <template #default="scope">
                <span :class="getStatusClass(scope.row.states)">{{ scope.row.states }}</span>
              </template>
            </el-table-column>
            <el-table-column label="工时" width="80">
              <template #default="scope">
                <span>{{ scope.row.totalHours }}h</span>
              </template>
            </el-table-column>
            <el-table-column label="剩余工时" width="80">
              <template #default="scope">
                <span>{{ scope.row.remainingHours }}h</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="200">
              <template #default="scope">
                <span v-if="scope.row.states !== '已关闭'" class="action-text close-action" @click="handleClose(scope.row)">关闭</span>
                <span v-else class="action-text open-action" @click="handleOpen(scope.row)">打开</span>
                <span v-if="scope.row.states !== '已关闭'" class="action-text edit-action" @click="handleEdit(scope.row.id)">编辑</span>
                <span class="action-text delete-action" @click="handleDelete(scope.row)">删除</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>

import {ref, computed} from "vue";
import {useRouter} from "vue-router";
import request from "@/utils/request.js";
import { ElMessageBox, ElMessage } from "element-plus";

const tabs = ref([
  {name:'全部',type:'all'},
  {name:'进行中',type:'ing'},
  {name:'未开始',type:'noBegin'},
  {name:'已关闭',type:'close'},
]);
const activeTab=ref('all');
const searchQuery=ref('');

const router =useRouter();
const goToItemEdit = () =>{
  router.push('/itemSet/itemEdit');
}

// 跳转到添加项目页面
const goToAddProject = () => {
  router.push('/itemSet/addProject');
};

// 项目数据
const projectData = ref([]);

// 从后端获取项目数据
const fetchProjects = async () => {
  try {
    const response = await request.get('/workbench/all-projects');
    if (response.data.code === 200 && Array.isArray(response.data.data)) {
      // 处理时间格式，只保留日期部分
      projectData.value = response.data.data.map(project => ({
        ...project,
        startTime: project.startTime ? project.startTime.split('T')[0] : '',
        finishTime: project.finishTime ? project.finishTime.split('T')[0] : '',
        // 初始化工时数据
        totalHours: 0,
        remainingHours: 0
      }));
      
      // 获取任务列表以计算工时
      await fetchTasksForHours();
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

// 获取任务列表以计算工时
const fetchTasksForHours = async () => {
  try {
    const response = await request.get('/workbench/tasks');
    if (response.data.code === 200 && Array.isArray(response.data.data)) {
      const tasks = response.data.data;
      
      // 为每个项目计算工时
      projectData.value.forEach(project => {
        let totalHours = 0;
        let remainingHours = 0;
        
        tasks.forEach(task => {
          if (task.project_id === project.id) {
            // 累计总工时
            if (task.estimated_hours) {
              totalHours += task.estimated_hours;
            }
            
            // 计算剩余工时
            if (task.estimated_hours && task.actual_hours) {
              remainingHours += Math.max(0, task.estimated_hours - task.actual_hours);
            } else if (task.estimated_hours) {
              remainingHours += task.estimated_hours;
            }
          }
        });
        
        project.totalHours = totalHours;
        project.remainingHours = remainingHours;
      });
    }
  } catch (error) {
    console.error('获取任务列表失败:', error);
  }
};

// 组件挂载时获取数据
import { onMounted, onBeforeMount } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

onBeforeMount(() => {
  // 从URL参数中获取projectName
  const projectName = route.query.projectName;
  if (projectName) {
    searchQuery.value = projectName;
  }
});

onMounted(() => {
  fetchProjects();
});

// 根据当前标签和搜索词过滤数据
const filteredData = computed(() => {
  let result = projectData.value;
  
  // 首先根据标签过滤
  if (activeTab.value === 'ing') {
    result = result.filter(item => item.states === '进行中');
  } else if (activeTab.value === 'noBegin') {
    result = result.filter(item => item.states === '未开始');
  } else if (activeTab.value === 'close') {
    result = result.filter(item => item.states === '已关闭');
  }
  
  // 然后根据搜索词过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(item => 
      item.title.toLowerCase().includes(query)
    );
  }
  
  return result;
});

// 获取状态标签的类名
const getStatusClass = (status) => {
  switch (status) {
    case '进行中':
      return 'status-in-progress';
    case '已完成':
      return 'status-completed';
    case '已关闭':
      return 'status-closed';
    default:
      return '';
  }
};

// 处理操作
const handleClose = (project) => {
  ElMessageBox.confirm(
    '确定要关闭这个项目吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(async () => {
    try {
      // 尝试从数据库关闭项目
      const response = await request.put(`/workbench/projects/${project.id}/status?status=2`);
      if (response.data.code === 200) {
        console.log('关闭项目成功:', project.id);
        // 更新本地数据
        project.states = '已关闭';
        ElMessage.success('项目已关闭');
      }
    } catch (error) {
      console.error('关闭项目失败:', error);
      ElMessage.error('关闭项目失败');
    }
  })
  .catch(() => {
    // 取消关闭
  });
};

// 打开项目
const handleOpen = (project) => {
  ElMessageBox.confirm(
    '确定要打开这个项目吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  )
  .then(async () => {
    try {
      // 尝试从数据库打开项目
      const response = await request.put(`/workbench/projects/${project.id}/status?status=1`);
      if (response.data.code === 200) {
        console.log('打开项目成功:', project.id);
        // 更新本地数据
        project.states = '进行中';
        ElMessage.success('项目已打开');
      }
    } catch (error) {
      console.error('打开项目失败:', error);
      ElMessage.error('打开项目失败');
    }
  })
  .catch(() => {
    // 取消打开
  });
};

const handleEdit = (id) => {
  router.push('/itemSet/itemEdit');
};

const handleSubmitCode = (project) => {
  console.log('提交代码:', project);
};

const handleDelete = (project) => {
  ElMessageBox.confirm(
    '确定要删除这个项目吗？此操作不可恢复。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    }
  )
  .then(async () => {
    try {
      // 尝试从数据库删除项目
      console.log('删除项目ID:', project.id);
      // 调用后端API删除项目
      const response = await request.delete(`/workbench/projects/${project.id}`);
      if (response.data.code === 200) {
        console.log('删除项目成功:', project.id);
        // 从本地数据中移除删除的项目
        projectData.value = projectData.value.filter(p => p.id !== project.id);
        ElMessage.success('项目已删除');
      }
    } catch (error) {
      console.error('删除项目失败:', error);
      ElMessage.error('删除项目失败');
    }
  })
  .catch(() => {
    // 取消删除
  });
};
</script>

<style scoped>
.projects-comment{
  background-color: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}
.option{
  height: 40px;
  border-bottom: 1px solid #ebeef5;
}
.option span{
  display: inline-block;
  padding: 0 10px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
}
.option span.active{
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
  font-weight: 500;
}

.search-add-container {
  display: inline-block;
  float: right;
  display: flex;
  align-items: center;
  gap: 10px;
}

.search-box {
  margin-right: 10px;
}

.search-input {
  width: 200px;
}

.addProject{
  display: inline-block;
}
.button{
  background-color: #238EFF;
  color: #fff;
  margin-left: 0;
}

.project-table-container {
  padding: 0;
  background-color: #fff;
  border-radius: 0;
  box-shadow: none;
  overflow-x: auto;
}

.table-container {
  width: 100%;
  min-width: 800px;
}

.ProjectTable {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
}

.project-name {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.action-text {
  display: inline-block;
  margin: 0 8px;
  cursor: pointer;
  font-size: 13px;
  transition: color 0.3s;
}

.close-action {
  color: #409EFF;
}

.edit-action {
  color: #E6A23C;
}

.delete-action {
  color: #F56C6C;
}

.submit-action {
  color: #67C23A;
}

.open-action {
  color: #409EFF;
}

.action-text:hover {
  text-decoration: underline;
}

.el-table th {
  background-color: #f9f9f9;
  text-align: center;
  vertical-align: middle;
}

.el-table--border th {
  border: none !important;
}

.el-table--border td {
  border: none !important;
  vertical-align: middle;
}
</style>