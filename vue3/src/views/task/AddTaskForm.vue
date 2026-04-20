<template>
  <div class="task-add">
    <h3>新增任务</h3>
    <div class="form-container">
      <el-form :model="taskForm" label-width="120px" class="add-task-form">
        <div class="form-row">
          <el-form-item label="所属项目">
            <el-select
              v-model="taskForm.project"
              filterable
              remote
              reserve-keyword
              placeholder="请输入项目名称"
              :remote-method="remoteProjectMethod"
              :loading="projectLoading"
            >
              <el-option
                v-for="item in projectOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务名称">
            <el-input v-model="taskForm.name" placeholder="请输入" />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="负责人">
            <el-select
              v-model="taskForm.assignee"
              filterable
              remote
              reserve-keyword
              placeholder="请输入负责人姓名"
              :remote-method="remoteMethod"
              :loading="loading"
            >
              <el-option
                v-for="item in assigneeOptions"
                :key="item.value"
                :label="item.label"
                :value="item.value"
              />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务状态">
            <el-select v-model="taskForm.status" placeholder="请选择">
              <el-option label="待开始" value="1" />
              <el-option label="进行中" value="2" />
              <el-option label="已完成" value="3" />
              <el-option label="已关闭" value="4" />
            </el-select>
          </el-form-item>
          
          <el-form-item label="优先级">
            <el-select v-model="taskForm.priority" placeholder="请选择">
              <el-option label="紧急" value="1" />
              <el-option label="一般" value="2" />
              <el-option label="正常" value="3" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="预计开始日期">
            <el-date-picker
              v-model="taskForm.startDate"
              type="date"
              placeholder="请选"
              style="width: 100%"
            />
          </el-form-item>
          
          <el-form-item label="预计完成日期">
            <el-date-picker
              v-model="taskForm.endDate"
              type="date"
              placeholder="请选"
              style="width: 100%"
            />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务描述">
            <el-input
              v-model="taskForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入任务描述"
            />
          </el-form-item>
        </div>
        
        <div class="form-buttons">
          <el-button type="primary" @click="saveTask">保存</el-button>
          <el-button @click="goBack">返回</el-button>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { recordOperationLog } from '@/utils/operationLog.js';
import request from "@/utils/request.js";

const router = useRouter();

// 任务表单数据
const taskForm = ref({
  project: '',
  name: '',
  assignee: '',
  status: '1', // 默认待开始
  priority: '2', // 默认一般
  startDate: '',
  endDate: '',
  description: ''
});

// 负责人选项
const assigneeOptions = ref([]);
// 加载状态
const loading = ref(false);

// 模拟远程搜索方法
const remoteMethod = (query) => {
  if (query !== '') {
    loading.value = true;
    // 模拟API请求延迟
    setTimeout(() => {
      loading.value = false;
      // 模拟搜索结果
      const allOptions = [
        { label: '张三', value: '张三' },
        { label: '李四', value: '李四' },
        { label: '王五', value: '王五' },
        { label: '赵六', value: '赵六' },
        { label: '孙七', value: '孙七' },
        { label: '周八', value: '周八' }
      ];
      // 根据查询过滤结果
      assigneeOptions.value = allOptions.filter(item => 
        item.label.toLowerCase().includes(query.toLowerCase())
      );
    }, 200);
  } else {
    assigneeOptions.value = [];
  }
};

// 项目选项
const projectOptions = ref([]);
// 项目加载状态
const projectLoading = ref(false);

// 模拟项目远程搜索方法
const remoteProjectMethod = (query) => {
  if (query !== '') {
    projectLoading.value = true;
    // 模拟API请求延迟
    setTimeout(() => {
      projectLoading.value = false;
      // 模拟搜索结果
      const allProjects = [
        { label: '智慧教室', value: '智慧教室' },
        { label: '电子班牌', value: '电子班牌' },
        { label: '数据大屏', value: '数据大屏' },
        { label: '实训教学管理平台', value: '实训教学管理平台' },
        { label: '电子班牌管理系统', value: '电子班牌管理系统' },
        { label: '智慧园区OA办公系统', value: '智慧园区OA办公系统' },
        { label: '在线试卷批改系统', value: '在线试卷批改系统' },
        { label: '家校互通平台', value: '家校互通平台' }
      ];
      // 根据查询过滤结果
      projectOptions.value = allProjects.filter(item => 
        item.label.toLowerCase().includes(query.toLowerCase())
      );
    }, 200);
  } else {
    projectOptions.value = [];
  }
};

// 保存任务
const saveTask = async () => {
  try {
    console.log('保存任务:', taskForm.value);
    
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (!userStr) {
      console.error('用户未登录');
      return;
    }
    const user = JSON.parse(userStr);
    
    // 构建任务数据
    const taskData = {
      title: taskForm.value.name,
      description: taskForm.value.description,
      projectId: taskForm.value.project === '智慧教室' ? 1 : 
                 taskForm.value.project === '电子班牌' ? 2 : 
                 taskForm.value.project === '数据大屏' ? 3 : null,
      assigneeId: taskForm.value.assignee === '张三' ? 202201 : 
                 taskForm.value.assignee === '李四' ? 202202 : 
                 taskForm.value.assignee === '王五' ? 202203 : 
                 taskForm.value.assignee === '赵六' ? 202204 : 
                 taskForm.value.assignee === '孙七' ? 202205 : 
                 taskForm.value.assignee === '周八' ? 202206 : null,
      creatorId: parseInt(user.username),
      startDate: taskForm.value.startDate,
      dueDate: taskForm.value.endDate,
      status: parseInt(taskForm.value.status), // 使用表单中的状态值
      priority: parseInt(taskForm.value.priority), // 使用表单中的优先级值
      progress: 0 // 默认为0%
    };
    
    console.log('发送任务数据:', taskData);
    
    // 调用后端 API 创建任务
    const response = await request.post('/workbench/tasks', taskData);
    console.log('创建任务响应:', response);
    
    if (response.data.code === 200) {
      console.log('创建任务成功');
      // 记录操作日志
      await recordOperationLog('创建了', '任务', null, taskForm.value.name);
      // 保存成功后跳转回任务列表
      router.push('/task/taskList');
    } else {
      console.error('创建任务失败:', response.data.msg);
    }
  } catch (error) {
    console.error('保存任务失败:', error);
  }
};

// 返回上一页
const goBack = () => {
  router.push('/task/taskList');
};
</script>

<style scoped>
.task-add {
  padding: 10px;
  background-color: #fff;
  min-height: 100vh;
}

.form-container {
  background-color: #fff;
  padding: 20px;
}

h3 {
  margin-bottom: 20px;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.add-task-form {
  max-width: 800px;
  margin: 0 auto;
}

.form-row {
  display: flex;
  gap: 20px;
  margin-bottom: 16px;
}

.form-row .el-form-item {
  flex: 1;
  margin-bottom: 0;
}

.task-type-buttons {
  display: flex;
  gap: 10px;
}

.access-control {
  margin-top: 4px;
}

.mt-2 {
  margin-top: 8px;
}

.form-buttons {
  display: flex;
  margin-top: 20px;
  gap: 12px;
  justify-content: center;
}
</style>