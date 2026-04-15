<template>
  <div class="simple-task-list">
    <el-table
      :data="tasks"
      style="width: 100%"
      class="simple-table"
      v-if="tasks.length > 0"
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="任务名称">
        <template #default="{ row }">
          <span 
            :class="{ 'due-task': isDueTask(row) }"
            style="cursor: pointer;"
            @click="handleTaskClick(row)"
          >{{ row.title }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div v-else class="no-data">
      <span style="color: #909399;">暂无任务</span>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted, watch} from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request.js";

// 初始化路由
const router = useRouter();

const props = defineProps({
  selectedDate: {
    type: Date,
    required: true
  }
});

const tasks = ref([]);

// 检查任务是否是当天截止或在7天内截止
const isDueTask = (task) => {
  if (task.due_date) {
    const dueDate = new Date(task.due_date.split(' ')[0]);
    dueDate.setHours(0, 0, 0, 0);
    
    const selectedDate = props.selectedDate;
    selectedDate.setHours(0, 0, 0, 0);
    
    const diffTime = dueDate - selectedDate;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return diffDays >= 0 && diffDays <= 7;
  }
  return false;
};

// 从后端获取任务数据
const fetchTasks = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 使用本地日期格式，确保与任务日期格式一致
      const selectedDate = props.selectedDate;
      const year = selectedDate.getFullYear();
      const month = String(selectedDate.getMonth() + 1).padStart(2, '0');
      const day = String(selectedDate.getDate()).padStart(2, '0');
      const selectedDateStr = `${year}-${month}-${day}`;
      
      // 调用后端API获取任务列表
      const response = await request.get(`/workbench/tasks?username=${user.username}`);
      if (response.data.code === 200) {
        // 过滤出在选中日期范围内的任务
        tasks.value = response.data.data.filter(task => {
          // 检查任务是否有开始日期或截止日期
          if (task.start_date || task.due_date) {
            // 情况1：任务有开始日期和截止日期，检查选中日期是否在范围内
            if (task.start_date && task.due_date) {
              const startDateStr = task.start_date.split(' ')[0]; // 只取日期部分
              const dueDateStr = task.due_date.split(' ')[0]; // 只取日期部分
              return selectedDateStr >= startDateStr && selectedDateStr <= dueDateStr;
            }
            // 情况2：任务只有开始日期，检查选中日期是否等于开始日期
            else if (task.start_date) {
              const startDateStr = task.start_date.split(' ')[0]; // 只取日期部分
              return startDateStr === selectedDateStr;
            }
            // 情况3：任务只有截止日期，检查选中日期是否等于截止日期
            else if (task.due_date) {
              const dueDateStr = task.due_date.split(' ')[0]; // 只取日期部分
              return dueDateStr === selectedDateStr;
            }
          }
          return false;
        });
      }
    }
  } catch (error) {
    console.error('获取任务列表失败:', error);
  }
};

// 当选中日期变化时，重新获取任务数据
watch(() => props.selectedDate, () => {
  fetchTasks();
}, { deep: true });

// 处理任务点击事件
const handleTaskClick = (task) => {
  // 跳转到任务模块，并传递任务名称作为筛选条件
  router.push(`/task/taskList?search=${encodeURIComponent(task.title)}`);
};

// 页面加载时获取任务数据
onMounted(() => {
  fetchTasks();
});
</script>

<style scoped>
.simple-task-list {
  padding: 10px;
}

.simple-table {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
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

.no-data {
  text-align: center;
  padding: 20px;
  color: #909399;
}

/* 当天截止任务的样式 */
.due-task {
  color: #f56c6c;
  font-weight: 500;
}
</style>