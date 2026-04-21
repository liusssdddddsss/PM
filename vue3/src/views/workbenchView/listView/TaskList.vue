<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="paginatedTaskList"
          style="width: 100%"
          class="TaskTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
      >
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="120">
          <template #default="scope">
            <span class="task-name" style="cursor: pointer;" @click="handleProjectNameClick(scope.row.projectName)">{{ scope.row.projectName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="任务描述" min-width="180">
          <template #default="scope">
            <span class="task-name">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="scope">
            <span :class="getPriorityClass(scope.row.priority)">{{ scope.row.priority }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <span :class="getStatusClass(scope.row.status)">{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="120"></el-table-column>
        <el-table-column prop="progress" label="进度" width="80">
          <template #default="scope">
            <el-progress type="circle" :percentage="scope.row.progress" :width="20" :stroke-width="3" />
          </template>
        </el-table-column>
        <el-table-column prop="workTime" label="工时" width="80"></el-table-column>
        <el-table-column prop="remainingTime" label="剩余工时" width="80"></el-table-column>
        <el-table-column label="操作" width="240">
            <template #default="scope">
              <!-- 非开发者和测试者显示完整操作按钮 -->
              <template v-if="!isDeveloperOrTester">
                <span v-if="scope.row.status !== '已关闭'" class="action-text close-action" @click="handleClose(scope.row)">关闭</span>
                <span v-else class="action-text open-action" @click="handleOpen(scope.row)">打开</span>
                <span v-if="scope.row.status !== '已关闭'" class="action-text edit-action" @click="goToProductEdit(scope.row.id)">编辑</span>
                <span class="action-text submit-action" @click="handleSubmitCode(scope.row)">提交代码</span>
                <span class="action-text delete-action" @click="handleDelete(scope.row)">删除</span>
              </template>
              <!-- 开发者和测试者只显示查看和提交代码按钮 -->
              <template v-else>
                <span class="action-text edit-action" @click="goToProductEdit(scope.row.id)">查看</span>
                <span class="action-text submit-action" @click="handleSubmitCode(scope.row)">提交代码</span>
              </template>
            </template>
          </el-table-column>
      </el-table>
    </div>

    <!-- 关闭任务对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="关闭任务"
      width="400px"
    >
      <div class="dialog-content">
        <h4>{{ currentTask.name }}</h4>
        <div class="form-item">
          <label>预计完成时间：</label>
          <el-date-picker
            v-model="closeForm.expectedCompleteTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>实际完成时间：</label>
          <el-date-picker
            v-model="closeForm.actualCompleteTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>备注：</label>
          <el-input
            v-model="closeForm.remark"
            type="textarea"
            placeholder="请输入备注"
            :rows="3"
          />
        </div>
        <div class="history-section">
          <h5>历史记录</h5>
          <div class="history-item" v-for="(item, index) in historyList" :key="index">
            <span class="history-number">{{ index + 1 }}</span>
            <span class="history-date">{{ item.date }}</span>
            <span class="history-action">{{ item.action }}</span>
          </div>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmClose">关闭项目</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 代码提交对话框 -->
    <el-dialog
      v-model="codeDialogVisible"
      title="提交代码"
      width="500px"
    >
      <div class="dialog-content">
        <h4>{{ currentTask.name }}</h4>
        <div class="form-item">
          <label>代码仓库链接：</label>
          <el-input
            v-model="codeForm.repositoryUrl"
            placeholder="请输入代码仓库链接"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>分支名称：</label>
          <el-input
            v-model="codeForm.branch"
            placeholder="请输入分支名称"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>提交信息：</label>
          <el-input
            v-model="codeForm.commitMessage"
            type="textarea"
            placeholder="请输入提交信息"
            :rows="3"
          />
        </div>
        <div class="form-item">
          <label>代码文件：</label>
          <el-upload
            class="upload-demo"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="5"
            :file-list="codeForm.files"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                最多上传5个文件，支持zip、rar、tar.gz格式
              </div>
            </template>
          </el-upload>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="codeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmitCode">提交代码</el-button>
        </span>
      </template>
    </el-dialog>
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
</template>

<script setup>
import { ref, onMounted, computed, defineProps, watch } from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request.js";
import { recordOperationLog } from "@/utils/operationLog.js";
import { ElMessageBox, ElMessage } from "element-plus";

// 获取用户角色
const userRole = ref(null);

// 检查是否为开发者或测试者
const isDeveloperOrTester = computed(() => {
  const roleId = Number(userRole.value);
  return roleId === 3 || roleId === 4;
});

// 获取用户角色
const fetchUserRole = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const username = user.username;
      
      console.log('获取用户角色，用户名:', username);
      const userRes = await request.get(`/admin/findAll`);
      if (userRes.data.code === 200 && Array.isArray(userRes.data.data)) {
        // 从User列表中查找当前用户，将username转换为字符串类型进行匹配
        const currentUser = userRes.data.data.find(u => u.username == String(username));
        console.log('找到的当前用户:', currentUser);
        if (currentUser) {
          console.log('当前用户的role_id:', currentUser.role_id);
          console.log('当前用户的roleId:', currentUser.roleId); // 检查驼峰命名的字段
          if (currentUser.role_id) {
            userRole.value = currentUser.role_id;
            console.log('当前用户角色ID:', userRole.value);
            console.log('是否为开发者或测试者:', isDeveloperOrTester.value);
          } else if (currentUser.roleId) {
            // 处理驼峰命名的情况
            userRole.value = currentUser.roleId;
            console.log('当前用户角色ID (roleId):', userRole.value);
            console.log('是否为开发者或测试者:', isDeveloperOrTester.value);
          } else {
            console.error('当前用户没有role_id字段或role_id为null/undefined:', currentUser);
          }
        } else {
          console.error('未找到用户角色信息:', currentUser);
        }
      } else {
        console.error('获取用户列表失败:', userRes.data);
      }
    } else {
      console.error('本地存储中没有用户信息');
    }
  } catch (error) {
    console.error('获取用户角色失败:', error);
  }
};

// 接收父组件传递的搜索词、活动标签和状态
const props = defineProps({
  searchQuery: {
    type: String,
    default: ''
  },
  activeTab: {
    type: String,
    default: 'all'
  },
  status: {
    type: Number,
    default: null
  },
  productName: {
    type: String,
    default: ''
  }
});

const router = useRouter();
const goToProductEdit = (id) =>{
  router.push(`/task/taskEdit?id=${id}`);
}

// 任务数据
const taskList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);

// 从后端获取任务列表数据
onMounted(async () => {
  await fetchUserRole();
  await fetchTasks();
});

// 监听 activeTab 变化，重新获取任务列表
watch(() => props.activeTab, () => {
  fetchTasks();
});

// 监听 status 变化，重新计算过滤后的任务列表
watch(() => props.status, () => {
  // 状态变化时，过滤列表会自动重新计算，因为 filteredTaskList 是计算属性
});

// 根据搜索词、标签、状态和产品名称过滤任务列表
const filteredTaskList = computed(() => {
  let filtered = taskList.value;
  
  // 根据状态过滤
  if (props.status !== null) {
    if (props.status === 0) {
      // 未关闭的任务（状态不是已关闭）
      filtered = filtered.filter(task => task.status !== '已关闭');
    } else if (props.status === 2) {
      // 已关闭的任务
      filtered = filtered.filter(task => task.status === '已关闭');
    }
  }
  
  // 根据标签过滤
  if (props.activeTab !== 'all') {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const userId = parseInt(user.username);
      
      if (props.activeTab === 'zhiPaiMe') {
        // 指派我的任务
        filtered = filtered.filter(task => task.assignee_id === userId);
      } else if (props.activeTab === 'meJoin') {
        // 我参与的任务
        // 这里简化处理，假设参与的任务是指被指派的任务
        filtered = filtered.filter(task => task.assignee_id === userId);
      } else if (props.activeTab === 'meZhiPai') {
        // 我指派的任务
        filtered = filtered.filter(task => task.creator_id === userId);
      }
    }
  }
  
  // 根据搜索词过滤
  if (props.searchQuery) {
    const query = props.searchQuery.toLowerCase();
    filtered = filtered.filter(task => 
      task.name.toLowerCase().includes(query) ||
      task.projectName.toLowerCase().includes(query)
    );
  }
  
  // 根据产品名称过滤
  if (props.productName) {
    const productName = props.productName.toLowerCase();
    filtered = filtered.filter(task => 
      task.projectName.toLowerCase().includes(productName)
    );
  }
  
  return filtered;
});

// 分页后的任务列表
const paginatedTaskList = computed(() => {
  // 更新总数量为过滤后的任务数量
  total.value = filteredTaskList.value.length;
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredTaskList.value.slice(start, end);
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

const fetchTasks = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    console.log('获取用户信息:', userStr);
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('解析用户信息:', user);
      console.log('请求URL:', `/workbench/tasks?username=${user.username}`);
      const response = await request.get(`/workbench/tasks?username=${user.username}`);
      console.log('获取任务列表响应:', response);
      console.log('响应状态码:', response.data.code);
      console.log('响应数据:', response.data.data);
      if (response.data.code === 200) {
        // 转换数据格式以匹配前端组件
        if (response.data.data && Array.isArray(response.data.data)) {
          console.log('任务列表数据长度:', response.data.data.length);
          taskList.value = response.data.data.map(item => ({
            id: item.id,
            projectName: item.project_name,
            name: item.description || item.title,
            priority: getPriorityText(item.priority),
            status: getStatusText(item.status),
            deadline: item.due_date,
            progress: item.progress || 0,
            workTime: item.actual_hours ? `${item.actual_hours}h` : '0h',
            remainingTime: item.estimated_hours && item.actual_hours ? `${item.estimated_hours - item.actual_hours}h` : '0h',
            assignee_id: item.assignee_id,
            creator_id: item.creator_id
          }));
          console.log('转换后的任务列表数据:', taskList.value);
          console.log('转换后的任务列表数据长度:', taskList.value.length);
          total.value = taskList.value.length;
        } else {
          taskList.value = [];
          console.log('任务列表数据为空或不是数组');
        }
      } else {
        taskList.value = [];
        console.log('获取任务列表失败:', response.data.msg);
      }
    } else {
      taskList.value = [];
      console.log('用户未登录');
    }
  } catch (error) {
    console.error('获取任务列表失败:', error);
    console.error('错误详情:', error.message);
    console.error('错误堆栈:', error.stack);
    taskList.value = [];
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
      return '待开始';
    case 2:
      return '进度中';
    case 3:
      return '已完成';
    case 4:
      return '已关闭';
    default:
      return '待开始';
  }
};

// 关闭任务对话框
const dialogVisible = ref(false);
const currentTask = ref({ name: '' });
const closeForm = ref({
  expectedCompleteTime: '',
  actualCompleteTime: '',
  remark: ''
});

// 历史记录
const historyList = ref([
  { date: '2023-08-08 12:12:12', action: '由张三创建' },
  { date: '2023-08-08 12:12:12', action: '由李四修改' },
  { date: '2023-08-08 12:12:12', action: '由王五完成' }
]);

// 代码提交对话框
const codeDialogVisible = ref(false);
const codeForm = ref({
  repositoryUrl: '',
  branch: '',
  commitMessage: '',
  files: []
});

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
const handleClose = (task) => {
  ElMessageBox.confirm(
    '确定要关闭这个任务吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  )
  .then(async () => {
    try {
      // 尝试从数据库关闭任务
      const response = await request.put(`/workbench/tasks/${task.id}/status?status=4`);
      if (response.data.code === 200) {
        console.log('关闭任务成功:', task.id);
        // 记录操作日志
        await recordOperationLog('关闭了', '任务', task.id, task.name);
        // 更新本地数据
        task.status = '已关闭';
        ElMessage.success('任务已关闭');
      }
    } catch (error) {
      console.error('关闭任务失败:', error);
      ElMessage.error('关闭任务失败');
    }
  })
  .catch(() => {
    // 取消关闭
  });
};

// 处理项目名称点击事件
const handleProjectNameClick = (projectName) => {
  console.log('点击了项目名称:', projectName);
  // 跳转到任务模块，并传递项目名称作为筛选条件
  router.push(`/task/taskList?projectName=${encodeURIComponent(projectName)}`);
};

const handleEdit = (id) => {
  router.push('/task/taskEdit');
};

// 打开任务
const handleOpen = (task) => {
  ElMessageBox.confirm(
    '确定要打开这个任务吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  )
  .then(async () => {
    try {
      // 尝试从数据库打开任务
      const response = await request.put(`/workbench/tasks/${task.id}/status?status=1`);
      if (response.data.code === 200) {
        console.log('打开任务成功:', task.id);
        // 记录操作日志
        await recordOperationLog('打开了', '任务', task.id, task.name);
        // 更新本地数据
        task.status = '待开始';
        ElMessage.success('任务已打开');
      }
    } catch (error) {
      console.error('打开任务失败:', error);
      ElMessage.error('打开任务失败');
    }
  })
  .catch(() => {
    // 取消打开
  });
};

const handleDelete = (task) => {
  ElMessageBox.confirm(
    '确定要删除这个任务吗？此操作不可恢复。',
    '警告',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'danger'
    }
  )
  .then(async () => {
    try {
      // 尝试从数据库删除任务
      console.log('删除任务ID:', task.id);
      // 调用后端API删除任务
      const response = await request.delete(`/workbench/tasks/${task.id}`);
      if (response.data.code === 200) {
        console.log('删除任务成功:', task.id);
        // 记录操作日志
        await recordOperationLog('删除了', '任务', task.id, task.name);
        // 从本地数据中移除删除的任务
        taskList.value = taskList.value.filter(t => t.id !== task.id);
        ElMessage.success('任务已删除');
      }
    } catch (error) {
      console.error('删除任务失败:', error);
      ElMessage.error('删除任务失败');
    }
  })
  .catch(() => {
    // 取消删除
  });
};

// 确认关闭任务
const confirmClose = async () => {
  console.log('确认关闭任务:', currentTask.value.id);
  console.log('关闭表单:', closeForm.value);
  dialogVisible.value = false;
  // 记录操作日志
  await recordOperationLog('关闭任务', 'task', currentTask.value.id, currentTask.value.name);
  // 这里可以添加关闭任务的逻辑
};

// 处理代码提交
const handleSubmitCode = (task) => {
  ElMessageBox.confirm(
    '确定要提交代码吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'info'
    }
  )
  .then(async () => {
    try {
      // 这里可以添加代码提交的逻辑
      console.log('提交代码:', task);
      // 记录操作日志
      await recordOperationLog('提交了代码', '任务', task.id, task.name);
      ElMessage.success('代码提交成功');
    } catch (error) {
      console.error('提交代码失败:', error);
      ElMessage.error('提交代码失败');
    }
  })
  .catch(() => {
    // 取消提交
  });
};

// 处理文件选择
const handleFileChange = (file, fileList) => {
  codeForm.value.files = fileList;
};

// 确认提交代码
const confirmSubmitCode = async () => {
  try {
    console.log('确认提交代码:', currentTask.value.id);
    console.log('代码提交表单:', codeForm.value);
    
    // 这里可以添加代码提交的逻辑，例如调用后端API
    // 模拟API调用
    const response = await request.post('/task/submit-code', {
      taskId: currentTask.value.id,
      repositoryUrl: codeForm.value.repositoryUrl,
      branch: codeForm.value.branch,
      commitMessage: codeForm.value.commitMessage,
      // 注意：文件上传需要特殊处理，这里只是示例
    });
    
    if (response.data.code === 200) {
      console.log('代码提交成功');
      // 记录操作日志
      await recordOperationLog('提交代码', 'task', currentTask.value.id, currentTask.value.name);
      codeDialogVisible.value = false;
      // 可以添加成功提示
    }
  } catch (error) {
    console.error('代码提交失败:', error);
  }
};
</script>

<style scoped>
.task-table-container {
  padding: 0;
  background-color: #fff;
  border-radius: 0;
  box-shadow: none;
  overflow-x: auto;
  max-height: 800px;
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

.task-name {
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

.action-text:hover {
  text-decoration: underline;
}

.el-table th {
  //font-size: 12px;
  //font-weight: 500;
  background-color: #f9f9f9;
  //padding: 4px 12px;
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

/* 对话框样式 */
.dialog-content {
  padding: 10px 0;
}

.dialog-content h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  color: #606266;
}

.history-section {
  margin-top: 20px;
  padding-top: 16px;
  border-top: 1px solid #e4e7ed;
}

.history-section h5 {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #303133;
  font-weight: bold;
}

.history-item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
}

.history-number {
  width: 16px;
  height: 16px;
  border-radius: 50%;
  background-color: #409eff;
  color: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 10px;
  margin-right: 8px;
  flex-shrink: 0;
}

.history-date {
  margin-right: 12px;
  color: #909399;
  flex-shrink: 0;
}

.history-action {
  flex: 1;
  color: #303133;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}


.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>