<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="paginatedTableData"
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
        <span class="task-name" style="cursor: pointer;" @click="handleTitleClick(scope.row.title)">{{ scope.row.title }}</span>
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
    <el-table-column prop="assignee" label="负责人" width="100"></el-table-column>
    <el-table-column prop="finishTime" label="完成时间" width="180"></el-table-column>
      </el-table>
    </div>
    
    <div class="pagination">
      <span>共 {{ total }} 项</span>
      <el-pagination
          layout="total, sizes, prev, pager, next, jumper"
          :total="total"
          :page-size="pageSize"
          :current-page="currentPage"
          @size-change="handleSizeChange"
          @current-change="handleCurrentChange"
          style="margin-right: 10px"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request.js";

// 初始化路由
const router = useRouter();

// 处理标题点击事件
const handleTitleClick = (title) => {
  console.log('点击了Bug标题:', title);
  // 跳转到Bug模块，并传递标题作为筛选条件
  router.push(`/bug?title=${encodeURIComponent(title)}`);
};

//ddl列表
const tableData = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);

// 从后端获取Bug列表数据
onMounted(() => {
  fetchBugs();
});

const fetchBugs = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    console.log('BugList - localStorage user:', userStr);
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('BugList - 当前用户:', user);
      
      // 尝试多个可能的接口地址
      const possibleUrls = [
        `/workbench/bugs?username=${user.username}`,
        `/api/bug/list?assignee=${user.username}`,
        `/dashboard/test-statistics?username=${user.username}`
      ];
      
      let success = false;
      for (const url of possibleUrls) {
        try {
          console.log('BugList - 尝试请求:', url);
          const response = await request.get(url);
          console.log('BugList - 获取Bug列表响应:', response);
          
          if (response.data.code === 200) {
            const data = response.data.data;
            console.log('BugList - 后端返回的数据:', data);
            
            if (data && Array.isArray(data)) {
              // 如果是bug列表数据
              tableData.value = data.map(item => ({
                id: item.id,
                title: item.title || '未命名Bug',
                priority: getPriorityText(item.severity || item.priority),
                state: getStatusText(item.status),
                sure: '已确认',
                assignee: item.assignee_name || item.assignee || '未指派',
                assigneeId: item.assigneeId,
                assignee_name: item.assignee_name,
                finishTime: item.resolvedAt || item.completedAt || ''
              }));
              success = true;
              break;
            } else if (data && typeof data === 'object' && !Array.isArray(data)) {
              // 如果是测试统计数据，不显示列表
              console.log('BugList - 收到的是统计数据，不是列表');
              continue;
            }
          }
        } catch (error) {
          console.log('BugList - 请求失败:', url, error.message);
        }
      }
      
      // 使用 assigneeId 字段匹配用户（后端返回的是驼峰命名）
      const userId = String(user.username);
      console.log('BugList - 当前用户ID:', userId);
      
      // 显示所有Bug的assigneeId字段供排查
      console.log('BugList - 所有Bug的assigneeId字段:');
      tableData.value.forEach((bug, index) => {
        console.log(`BugList - Bug ${index}: title=${bug.title}, assigneeId=${bug.assigneeId}, assignee_name=${bug.assignee_name}`);
      });
      
      // 过滤出指派给当前用户的Bug（使用 assigneeId 字段）
      tableData.value = tableData.value.filter(bug => {
        const assigneeId = String(bug.assigneeId || '');
        const matches = assigneeId === userId;
        if (matches) {
          console.log('BugList - 匹配到Bug:', bug.title, 'assigneeId:', assigneeId);
        }
        return matches;
      });
      
      console.log('BugList - 过滤后的Bug列表:', tableData.value);
      total.value = tableData.value.length;
      
      if (!success) {
        console.error('BugList - 所有Bug接口都无法获取数据');
      }
    }
  } catch (error) {
    console.error('BugList - 获取Bug列表失败:', error);
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

// 分页后的Bug列表
const paginatedTableData = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return tableData.value.slice(start, end);
});

// 处理分页大小变化
const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

// 处理页码变化
const handleCurrentChange = (current) => {
  currentPage.value = current;
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
  max-height: 400px;
  overflow-y: auto;
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>