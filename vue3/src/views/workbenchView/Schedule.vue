<template>
<!--  <div class="title">
    <h3>工作日程</h3>
  </div>-->
  <div class="ScheduleComment">
    <div class="scheduleLeft">
      <el-calendar v-model="value" :controller-type="controllerType">
        <template #dateCell="{ date, data }">
          <div class="calendar-cell">
            {{ data.day || '' }}
            <span v-if="getDueItemsCount(date) > 0" class="due-badge">{{ getDueItemsCount(date) }}</span>
          </div>
        </template>
      </el-calendar>
    </div>
    <div class="scheduleRight">
      <div class="option">
        <span
            :class="{active:activeTab === 'task'}"
            @click="activeTab = 'task'"
        >任务</span>
        <span
            :class="{active:activeTab === 'bug'}"
            @click="activeTab = 'bug'"
        >Bug</span>
      </div>
      <div class="OptionList">
        <SimpleTaskList
            v-if="activeTab ==='task'"
            :selected-date="value"
        />
        <SimpleBugList
            v-if="activeTab === 'bug'"
            :selected-date="value"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";
import SimpleTaskList from "@/views/workbenchView/listView/SimpleTaskList.vue";
import SimpleBugList from "@/views/workbenchView/listView/SimpleBugList.vue";
import request from "@/utils/request.js";

const value =ref(new Date());
const activeTab = ref("task");
const tasks = ref([]);
const bugs = ref([]);

// 从后端获取任务数据
const fetchTasks = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      
      // 调用后端API获取任务列表
      const response = await request.get(`/workbench/tasks?username=${user.username}`);
      if (response.data.code === 200) {
        tasks.value = response.data.data;
      }
    }
  } catch (error) {
    console.error('获取任务列表失败:', error);
  }
};

// 从后端获取Bug数据
const fetchBugs = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      
      // 调用后端API获取Bug列表
      const response = await request.get(`/workbench/bugs?username=${user.username}`);
      if (response.data.code === 200) {
        bugs.value = response.data.data;
      }
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
  }
};

// 检查日期是否有截止任务或Bug
const hasDueItems = (date) => {
  const checkDate = new Date(date);
  checkDate.setHours(0, 0, 0, 0);
  
  // 检查该日期是否有任务的截止日期在7天内
  for (const task of tasks.value) {
    if (task.due_date) {
      const dueDate = new Date(task.due_date.split(' ')[0]);
      dueDate.setHours(0, 0, 0, 0);
      
      const diffTime = dueDate - checkDate;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays >= 0 && diffDays <= 7) {
        return true;
      }
    }
  }
  
  // 检查该日期是否有Bug的截止日期在7天内
  for (const bug of bugs.value) {
    if (bug.due_date) {
      const dueDate = new Date(bug.due_date.split(' ')[0]);
      dueDate.setHours(0, 0, 0, 0);
      
      const diffTime = dueDate - checkDate;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays >= 0 && diffDays <= 7) {
        return true;
      }
    }
  }
  
  return false;
};

// 计算指定日期有多少个距离截止日期只剩七天不到的任务
const getDueItemsCount = (date) => {
  const checkDate = new Date(date);
  checkDate.setHours(0, 0, 0, 0);
  let count = 0;
  
  // 检查该日期是否有任务的截止日期在7天内
  for (const task of tasks.value) {
    if (task.due_date) {
      const dueDate = new Date(task.due_date.split(' ')[0]);
      dueDate.setHours(0, 0, 0, 0);
      
      const diffTime = dueDate - checkDate;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays >= 0 && diffDays <= 7) {
        count++;
      }
    }
  }
  
  // 检查该日期是否有Bug的截止日期在7天内
  for (const bug of bugs.value) {
    if (bug.due_date) {
      const dueDate = new Date(bug.due_date.split(' ')[0]);
      dueDate.setHours(0, 0, 0, 0);
      
      const diffTime = dueDate - checkDate;
      const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
      
      if (diffDays >= 0 && diffDays <= 7) {
        count++;
      }
    }
  }
  
  return count;
};

// 页面加载时获取数据
onMounted(() => {
  fetchTasks();
  fetchBugs();
});
</script>

<style scoped>
.ScheduleComment {
  margin-top:10px;
  display: flex;
  gap: 10px;
}
.scheduleLeft {
  width: 70%;
}
.scheduleRight {
  width: 30%;
  background-color: white;
}
.option{
  width: 100%;
  height: 30px;
  margin-top: 20px;
  margin-left: 10px;
}
.option span{
  display: inline-block;
  width: 50px;
  text-align: center;
}
.option span.active{
  color: #238EFF;
  border-bottom: 2px solid #238EFF;
}

/* 日历单元格样式 */
.calendar-cell {
  width: 100%;
  height: 80px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 4px;
  transition: background-color 0.3s;
  position: relative;
}

/* 截止日期提示徽章 */
.due-badge {
  position: absolute;
  top: 5px;
  right: 5px;
  background-color: #f56c6c;
  color: white;
  border-radius: 50%;
  width: 18px;
  height: 18px;
  font-size: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-weight: 500;
}

/* 鼠标悬停样式 */
.calendar-cell:hover {
  background-color: #ecf5ff;
}

/* 选中日期样式 */
.el-calendar__date--current {
  .calendar-cell {
    background-color: #409EFF;
    color: white;
  }
}
</style>