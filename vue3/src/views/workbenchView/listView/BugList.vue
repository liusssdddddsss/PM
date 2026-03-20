<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="tableData"
          style="width: 100%"
          class="TaskTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
      >
    <el-table-column label="序号" width="80">
      <template #default="scope">
        {{ scope.$index + 1 }}
      </template>
    </el-table-column>
    <el-table-column prop="title" label="标题" width="320">
      <template #default="scope">
        <span class="task-name">{{ scope.row.title }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="priority" label="优先级" width="80">
      <template #default="scope">
        <span :class="getPriorityClass(scope.row.priority)">{{ scope.row.priority }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="state" label="状态" width="90">
      <template #default="scope">
        <span :class="getStatusClass(scope.row.state)">{{ scope.row.state }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="sure" label="确认" width="90"></el-table-column>
    <el-table-column prop="finishTime" label="完成时间" width="180"></el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from "vue";
import request from "@/utils/request.js";

//ddl列表
const tableData = ref([]);

// 从后端获取Bug列表数据
onMounted(() => {
  fetchBugs();
});

const fetchBugs = async () => {
  try {
    const response = await request.get('/workbench/bugs');
    console.log('获取Bug列表响应:', response);
    if (response.data.code === 200) {
      // 转换数据格式以匹配前端组件
      tableData.value = response.data.data.map(item => ({
        id: item.id,
        title: item.title,
        priority: getPriorityText(item.priority),
        state: getStatusText(item.status),
        sure: item.confirmed ? '已确认' : '未确认',
        finishTime: item.due_date
      }));
      console.log('转换后的Bug列表数据:', tableData.value);
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
  }
};

// 获取优先级文本
const getPriorityText = (priority) => {
  switch (priority) {
    case 1:
      return '紧急';
    case 2:
      return '一般';
    case 3:
      return '正常';
    default:
      return '正常';
  }
};

// 获取状态文本
const getStatusText = (status) => {
  switch (status) {
    case 1:
      return '待处理';
    case 2:
      return '进行中';
    case 3:
      return '已修复';
    case 4:
      return '已关闭';
    default:
      return '待处理';
  }
};

// 获取优先级的类名
const getPriorityClass = (priority) => {
  switch (priority) {
    case '紧急':
      return 'priority-urgent';
    case '一般':
      return 'priority-normal';
    case '正常':
      return 'priority-regular';
    default:
      return '';
  }
};

// 获取状态的类名
const getStatusClass = (state) => {
  switch (state) {
    case '进行中':
      return 'status-in-progress';
    case '已修复':
    case '已关闭':
      return 'status-completed';
    default:
      return '';
  }
};
</script>

<style scoped>
.task-name {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.priority-urgent {
  color: #F56C6C;
  font-weight: 500;
  font-size: 13px;
}

.priority-normal {
  color: #E6A23C;
  font-weight: 500;
  font-size: 13px;
}

.priority-regular {
  color: #67C23A;
  font-weight: 500;
  font-size: 13px;
}

.status-in-progress {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.status-completed {
  color: #67C23A;
  font-weight: 500;
  font-size: 13px;
}

.task-table-container {
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

.TaskTable {
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
</style>