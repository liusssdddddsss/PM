<template>
  <div class="task-table-container">
    <el-table
        :data="taskList"
        style="width: 100%"
        class="TaskTable"
        :row-style="{height: '45px'}"
        :cell-style="{padding: '4px'}"
    >
      <el-table-column prop="id" label="序号" width="80"></el-table-column>
      <el-table-column prop="name" label="任务名称" width="350">
        <template #default="scope">
          <span class="task-name">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="priority" label="优先级" width="100">
        <template #default="scope">
          <span :class="getPriorityClass(scope.row.priority)">{{ scope.row.priority }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="scope">
          <span :class="getStatusClass(scope.row.status)">{{ scope.row.status }}</span>
        </template>
      </el-table-column>
      <el-table-column prop="deadline" label="截止时间" width="110"></el-table-column>
      <el-table-column prop="progress" label="进度" width="100">
        <template #default="scope">
          <el-progress type="circle" :percentage="scope.row.progress" :width="20" :stroke-width="3" />
        </template>
      </el-table-column>
      <el-table-column prop="workTime" label="工时" width="100"></el-table-column>
      <el-table-column prop="remainingTime" label="剩余工时" width="100"></el-table-column>
      <el-table-column label="操作" width="200">
        <template #default="scope">
          <span class="action-text close-action" @click="handleClose(scope.row.id)">关闭</span>
          <span class="action-text edit-action" @click="handleEdit(scope.row.id)">编辑</span>
          <span class="action-text delete-action" @click="handleDelete(scope.row.id)">删除</span>
        </template>
      </el-table-column>
    </el-table>
  </div>
</template>

<script setup>
import { ref } from "vue";

// 任务数据
const taskList = ref([
  { id: 1, name: '家校互通留言', priority: '紧急', status: '进度中', deadline: '2023-08-08', progress: 25, workTime: '8h', remainingTime: '1h' },
  { id: 2, name: '数据大屏 实训教学资源大数据', priority: '紧急', status: '进度中', deadline: '2023-08-08', progress: 25, workTime: '8h', remainingTime: '1h' },
  { id: 3, name: '终端-教师端查询评分标准列表', priority: '紧急', status: '进度中', deadline: '2023-08-08', progress: 25, workTime: '8h', remainingTime: '1h' },
  { id: 4, name: '家校互通留言', priority: '紧急', status: '进度中', deadline: '2023-08-08', progress: 0, workTime: '8h', remainingTime: '1h' },
  { id: 5, name: '数据大屏 实训教学资源大数据', priority: '一般', status: '进度中', deadline: '2023-08-08', progress: 0, workTime: '8h', remainingTime: '1h' },
  { id: 6, name: '终端-教师端查询评分标准列表', priority: '一般', status: '进度中', deadline: '2023-08-08', progress: 25, workTime: '6h', remainingTime: '1h' },
  { id: 7, name: '实训任务、示范列表详情优化', priority: '一般', status: '进度中', deadline: '2023-08-08', progress: 25, workTime: '6h', remainingTime: '6h' },
  { id: 8, name: '学期结束后，自动给学生推送实训档案', priority: '一般', status: '进度中', deadline: '2023-08-08', progress: 25, workTime: '6h', remainingTime: '6h' },
  { id: 9, name: '家长端，界面优化调整，新增功能：授权监控设备查看', priority: '正常', status: '已完成', deadline: '2023-08-08', progress: 0, workTime: '6h', remainingTime: '6h' },
  { id: 10, name: '班牌PC端管理界面调整，样式统一，菜单归类', priority: '正常', status: '已完成', deadline: '2023-08-08', progress: 0, workTime: '6h', remainingTime: '6h' },
  { id: 11, name: '班牌模板调整，参考海康，增加竖版', priority: '正常', status: '已完成', deadline: '2023-08-08', progress: 25, workTime: '6h', remainingTime: '6h' },
  { id: 12, name: '终端-教师端查询评分标准列表', priority: '正常', status: '已完成', deadline: '2023-08-08', progress: 25, workTime: '6h', remainingTime: '6h' }
]);

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
const getStatusClass = (status) => {
  switch (status) {
    case '进度中':
      return 'status-in-progress';
    case '已完成':
      return 'status-completed';
    default:
      return '';
  }
};

// 处理操作
const handleClose = (id) => {
  console.log('关闭任务:', id);
};

const handleEdit = (id) => {
  console.log('编辑任务:', id);
};

const handleDelete = (id) => {
  console.log('删除任务:', id);
};
</script>

<style scoped>
.task-table-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 1px 4px rgba(0, 0, 0, 0.05);
}

.TaskTable {
  border-radius: 8px;
  overflow: hidden;
}

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

.action-text:hover {
  text-decoration: underline;
}

.el-table .cell {
  font-size: 12px;
  text-align: center;
  vertical-align: middle;
  line-height: 1.2;
}

.el-table th {
  font-size: 12px;
  font-weight: 500;
  background-color: #f9f9f9;
  padding: 4px 12px;
  text-align: center;
  vertical-align: middle;
  height: 28px !important;
}

.el-table__row {
  height: 28px !important;
  line-height: 28px !important;
}

.el-table--border th {
  border-bottom: 1px solid #ebeef5;
}

.el-table--border td {
  border-bottom: 1px solid #ebeef5;
  vertical-align: middle;
}

.el-progress {
  display: flex;
  justify-content: center;
  align-items: center;
  margin: 0 auto;
}

.el-progress__text {
  font-size: 10px !important;
  margin: 0;
}
</style>