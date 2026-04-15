<template>
  <div class="simple-bug-list">
    <el-table
      :data="bugs"
      style="width: 100%"
      class="simple-table"
      v-if="bugs.length > 0"
    >
      <el-table-column type="index" label="序号" width="60" />
      <el-table-column label="Bug名称">
        <template #default="{ row }">
          <span 
            :class="{ 'due-bug': isDueBug(row) }"
            style="cursor: pointer;"
            @click="handleBugClick(row)"
          >{{ row.title }}</span>
        </template>
      </el-table-column>
    </el-table>
    <div v-else class="no-data">
      <span style="color: #909399;">暂无Bug</span>
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

const bugs = ref([]);

// 检查Bug是否是当天截止或在7天内截止
const isDueBug = (bug) => {
  if (bug.due_date) {
    const dueDate = new Date(bug.due_date.split(' ')[0]);
    dueDate.setHours(0, 0, 0, 0);
    
    const selectedDate = props.selectedDate;
    selectedDate.setHours(0, 0, 0, 0);
    
    const diffTime = dueDate - selectedDate;
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));
    
    return diffDays >= 0 && diffDays <= 7;
  }
  return false;
};

// 从后端获取Bug数据
const fetchBugs = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 使用本地日期格式，确保与Bug日期格式一致
      const selectedDate = props.selectedDate;
      const year = selectedDate.getFullYear();
      const month = String(selectedDate.getMonth() + 1).padStart(2, '0');
      const day = String(selectedDate.getDate()).padStart(2, '0');
      const selectedDateStr = `${year}-${month}-${day}`;
      
      // 调用后端API获取Bug列表
      const response = await request.get(`/workbench/bugs?username=${user.username}`);
      if (response.data.code === 200) {
        // 过滤出在选中日期范围内的Bug
        bugs.value = response.data.data.filter(bug => {
          // 检查Bug是否有开始日期或截止日期
          if (bug.due_date) {
            const dueDateStr = bug.due_date.split(' ')[0]; // 只取日期部分
            return dueDateStr === selectedDateStr;
          }
          return false;
        });
      }
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
  }
};

// 当选中日期变化时，重新获取Bug数据
watch(() => props.selectedDate, () => {
  fetchBugs();
}, { deep: true });

// 处理Bug点击事件
const handleBugClick = (bug) => {
  // 跳转到测试模块，并传递Bug名称作为筛选条件
  router.push(`/test/bugList?search=${encodeURIComponent(bug.title)}`);
};

// 页面加载时获取Bug数据
onMounted(() => {
  fetchBugs();
});
</script>

<style scoped>
.simple-bug-list {
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

/* 当天截止或7天内截止Bug的样式 */
.due-bug {
  color: #f56c6c;
  font-weight: 500;
}
</style>