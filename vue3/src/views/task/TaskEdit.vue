<template>
  <div class="task-edit">
    <h3>编辑任务</h3>
    <div class="form-container">
      <el-form :model="taskForm" label-width="120px" class="edit-task-form">
        <div class="form-row">
          <el-form-item label="所属项目">
            <el-select v-model="taskForm.project" placeholder="请选择项目">
              <el-option v-for="item in projectOptions" :key="item.id" :label="item.name" :value="item.id" />
            </el-select>
          </el-form-item>
          <el-form-item label="任务名称">
            <el-input v-model="taskForm.name" placeholder="请输入任务名称" />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="负责人">
            <el-select v-model="taskForm.assignee" placeholder="请选择负责人">
              <el-option v-for="item in assigneeOptions" :key="item.value" :label="item.label" :value="item.value" />
            </el-select>
          </el-form-item>
          <el-form-item label="任务状态">
            <el-select v-model="taskForm.status">
              <el-option label="待开始" value="1" />
              <el-option label="进行中" value="2" />
              <el-option label="已完成" value="3" />
              <el-option label="已关闭" value="4" />
            </el-select>
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="优先级">
            <el-select v-model="taskForm.priority">
              <el-option label="紧急" value="1" />
              <el-option label="一般" value="2" />
              <el-option label="正常" value="3" />
            </el-select>
          </el-form-item>
          <el-form-item label="预计开始日期">
            <el-date-picker v-model="taskForm.startDate" type="date" style="width: 100%" />
          </el-form-item>
          <el-form-item label="预计完成日期">
            <el-date-picker v-model="taskForm.endDate" type="date" style="width: 100%" />
          </el-form-item>
        </div>
        
        <div class="form-row">
          <el-form-item label="任务描述">
            <el-input v-model="taskForm.description" type="textarea" :rows="4" />
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
import { ref, onMounted } from 'vue';
import { useRouter, useRoute } from 'vue-router';
import request from "@/utils/request.js";

const router = useRouter();
const route = useRoute();
const taskId = ref(null);

const taskForm = ref({
  project: '',
  name: '',
  assignee: '',
  status: '1',
  priority: '2',
  startDate: '',
  endDate: '',
  description: ''
});

const assigneeOptions = ref([]);
const projectOptions = ref([]);
const attachments = ref([]);

const loadTask = async () => {
  taskId.value = route.query.id;
  if (!taskId.value) return;
  
  try {
    const res = await request.get(`/workbench/tasks/${taskId.value}`);
    if (res.data.code === 200) {
      const task = res.data.data;
      taskForm.value = {
        project: task.project_id ? task.project_id.toString() : '',
        name: task.title || task.name || '',
        assignee: task.assignee_id ? task.assignee_id.toString() : '',
        status: task.status ? task.status.toString() : '1',
        priority: task.priority ? task.priority.toString() : '2',
        startDate: task.start_date || '',
        endDate: task.due_date || '',
        description: task.description || ''
      };
    }
    await loadOptions();
  } catch (e) { console.error('加载任务失败:', e); }
};

const loadOptions = async () => {
  try {
    const userRes = await request.get('/admin/findAll');
    if (userRes.data.code === 200) {
      assigneeOptions.value = userRes.data.data.map(u => ({
        value: u.username,
        label: u.name || u.username
      }));
    }
    const projectRes = await request.get('/api/projects');
    if (projectRes.data.code === 200) {
      projectOptions.value = projectRes.data.data.map(p => ({
        id: p.id,
        name: p.name || p.title
      }));
    }
  } catch (e) { console.error('加载选项失败:', e); }
};

const loadAttachments = async () => {
  try {
    const res = await request.get(`/workbench/tasks/${taskId.value}/attachments`);
    if (res.data.code === 200) {
      attachments.value = res.data.data;
    }
  } catch (e) { console.error('加载附件失败:', e); }
};

const deleteAttachment = async (id) => {
  try {
    const res = await request.delete(`/workbench/tasks/${taskId.value}/attachments/${id}`);
    if (res.data.code === 200) {
      attachments.value = attachments.value.filter(a => a.id !== id);
    }
  } catch (e) { console.error('删除附件失败:', e); }
};

const saveTask = async () => {
  const data = {
    title: taskForm.value.name,
    description: taskForm.value.description,
    projectId: taskForm.value.project ? parseInt(taskForm.value.project) : null,
    assigneeId: taskForm.value.assignee ? parseInt(taskForm.value.assignee) : null,
    status: parseInt(taskForm.value.status),
    priority: parseInt(taskForm.value.priority),
    startDate: taskForm.value.startDate,
    dueDate: taskForm.value.endDate
  };
  
  try {
    const res = await request.put(`/workbench/tasks/${taskId.value}`, data);
    if (res.data.code === 200) {
      router.push('/workbench/taskList');
    }
  } catch (e) { console.error('保存任务失败:', e); }
};

const goBack = () => router.push('/workbench/taskList');

onMounted(() => loadTask());
</script>

<style scoped>
.task-edit { padding: 20px;
  background-color: #fff; }
.form-container { max-width: 800px; margin: 0 auto; }
.form-row { display: flex; gap: 20px; margin-bottom: 15px; }
.form-row .el-form-item { flex: 1; }

.form-buttons {
  display: flex;
  margin-top: 20px;
  gap: 12px;
  justify-content: center;
}
</style>