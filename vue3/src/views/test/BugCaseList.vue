<template>
  <div class="bug-table-container">
    <div class="table-container">
      <el-table
          :data="pagedBugList"
          style="width: 100%"
          class="BugTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
          @row-click="handleRowClick"
      >
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="projectName" label="项目名称" min-width="120">
          <template #default="scope">
            <span class="bug-name">{{ scope.row.projectName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="Bug名称" min-width="180">
          <template #default="scope">
            <span class="bug-name">{{ scope.row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="负责人" min-width="100">
          <template #default="scope">
            <span>{{ scope.row.assignee }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="优先级" width="80">
          <template #default="scope">
            <span :class="getPriorityClass(scope.row.priority)">{{ scope.row.priority }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="90">
          <template #default="scope">
            <span :class="getStatusClass(scope.row.status)">{{ getStatusText(scope.row.status) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="deadline" label="截止时间" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.deadline) }}
          </template>
        </el-table-column>
        <el-table-column prop="creator" label="创建人" width="100"></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
            <template #default="scope">
              <span class="action-text edit-action" @click.stop="handleEdit(scope.row)">编辑</span>
              <span 
                v-if="canResolve(scope.row)" 
                class="action-text resolve-action" 
                @click.stop="handleResolve(scope.row)">解决</span>
              <span 
                v-if="canVerify(scope.row)" 
                class="action-text verify-action" 
                @click.stop="handleVerify(scope.row)">验证</span>
              <span class="action-text delete-action" @click.stop="handleDelete(scope.row.id)">删除</span>
            </template>
          </el-table-column>
      </el-table>
    </div>
    
    <!-- 分页 -->
    <div class="pagination-container">
      <span class="total-count">共 {{ bugList.length }} 项</span>
      <el-pagination
        layout="total, sizes, prev, pager, next, jumper"
        :total="bugList.length"
        :page-size="pageSize"
        :current-page="currentPage"
        :page-sizes="[10, 20, 30]"
        @size-change="handleSizeChange"
        @current-change="handlePageChange"
      />
    </div>

    <!-- Bug详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="Bug详情"
      width="600px"
      :center="true"
    >
      <div class="dialog-content">
        <div class="detail-item">
          <label>Bug名称：</label>
          <span>{{ detailBug.name }}</span>
        </div>
        <div class="detail-item">
          <label>项目名称：</label>
          <span>{{ detailBug.projectName }}</span>
        </div>
        <div class="detail-item">
          <label>优先级：</label>
          <span :class="getPriorityClass(detailBug.priority)">{{ detailBug.priority }}</span>
        </div>
        <div class="detail-item">
          <label>状态：</label>
          <span :class="getStatusClass(detailBug.status)">{{ detailBug.status }}</span>
        </div>
        <div class="detail-item" v-if="detailBug.solution">
          <label>修复说明：</label>
          <span>{{ detailBug.solution }}</span>
        </div>
        <div class="detail-item">
          <label>截止时间：</label>
          <span>{{ formatDate(detailBug.deadline) || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>Bug描述：</label>
          <p class="description">{{ detailBug.description || '无' }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑Bug对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑Bug"
      width="500px"
      :center="true"
    >
      <div class="dialog-content">
        <h4>编辑Bug</h4>
        <div class="form-item">
          <label>Bug名称：</label>
          <el-input
            v-model="editForm.name"
            placeholder="请输入Bug名称"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>项目名称：</label>
          <el-input
            v-model="editForm.projectName"
            placeholder="请输入项目名称"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>优先级：</label>
          <el-select
            v-model="editForm.priority"
            placeholder="选择优先级"
            style="width: 100%"
          >
            <el-option label="紧急" value="紧急"></el-option>
            <el-option label="一般" value="一般"></el-option>
            <el-option label="正常" value="正常"></el-option>
          </el-select>
        </div>
        <div class="form-item">
          <label>状态：</label>
          <el-select
            v-model="editForm.status"
            placeholder="选择状态"
            style="width: 100%"
          >
            <el-option label="待处理" value="待处理"></el-option>
            <el-option label="处理中" value="处理中"></el-option>
            <el-option label="待验证" value="待验证"></el-option>
            <el-option label="已解决" value="已解决"></el-option>
            <el-option label="已关闭" value="已关闭"></el-option>
          </el-select>
        </div>
        <div class="form-item">
          <label>截止时间：</label>
          <el-date-picker
            v-model="editForm.deadline"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>Bug描述：</label>
          <el-input
            v-model="editForm.description"
            type="textarea"
            placeholder="请输入Bug描述"
            :rows="3"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="editDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmEdit">保存修改</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 开发者解决Bug对话框 -->
    <el-dialog
      v-model="resolveDialogVisible"
      title="解决Bug"
      width="500px"
      :center="true"
    >
      <div class="dialog-content">
        <h4>{{ currentBug.name }}</h4>
        <div class="form-item">
          <label>修复说明：</label>
          <el-input
            v-model="resolveForm.solution"
            type="textarea"
            placeholder="请输入修复说明"
            :rows="4"
          />
        </div>
        <div class="form-item">
          <label>解决时间：</label>
          <el-date-picker
            v-model="resolveForm.resolveTime"
            type="datetime"
            placeholder="选择日期时间"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>附件：</label>
          <el-upload
            class="upload-demo"
            action="#"
            :auto-upload="false"
            :on-change="handleResolveFileChange"
            :file-list="resolveFileList"
            drag
          >
            <i class="el-icon-upload"></i>
            <div class="el-upload__text">选择文件</div>
            <template #tip>
              <div class="el-upload__tip">
                可点击添加或拖拽上传，不超过100.0MB
              </div>
            </template>
          </el-upload>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="resolveDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmResolve">提交修复</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 测试者验证对话框 -->
    <el-dialog
      v-model="verifyDialogVisible"
      title="验证Bug修复"
      width="500px"
      :center="true"
    >
      <div class="dialog-content">
        <h4>{{ currentBug.name }}</h4>
        <div class="detail-item">
          <label>当前状态：</label>
          <span :class="getStatusClass(currentBug.status)">{{ getStatusText(currentBug.status) }}</span>
        </div>
        <div class="detail-item" v-if="currentBug.solution">
          <label>修复说明：</label>
          <p class="description">{{ currentBug.solution }}</p>
        </div>
        <div class="form-item">
          <label>验证结果：</label>
          <el-radio-group v-model="verifyForm.result">
            <el-radio label="pass">验证通过</el-radio>
            <el-radio label="fail">验证未通过</el-radio>
          </el-radio-group>
        </div>
        <div class="form-item">
          <label>验证说明：</label>
          <el-input
            v-model="verifyForm.remark"
            type="textarea"
            placeholder="请输入验证说明"
            :rows="3"
          />
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="verifyDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmVerify">提交验证</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 删除确认对话框 -->
    <el-dialog
      v-model="deleteDialogVisible"
      title="确认删除"
      width="400px"
      :center="true"
    >
      <div class="dialog-content">
        <p>您确定要删除Bug <strong>{{ currentBug.name }}</strong> 吗？</p>
        <p class="warning-text">删除后将无法恢复，请谨慎操作。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确认删除</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useRouter } from "vue-router";
import { ElMessage } from "element-plus";
import request from "@/utils/request.js";
import { recordOperationLog } from "@/utils/operationLog.js";

const props = defineProps({
  activeTab: {
    type: String,
    default: 'all'
  },
  searchQuery: {
    type: String,
    default: ''
  }
});

const router = useRouter();

// 当前用户
const currentUser = ref(null);

// 原始Bug数据
const allBugList = ref([]);

// 分页
const currentPage = ref(1);
const pageSize = ref(20);

// 根据activeTab和searchQuery筛选显示的数据
const bugList = computed(() => {
  let filteredList = [];
  
  // 根据activeTab筛选
  if (props.activeTab === 'all') {
    filteredList = allBugList.value;
  } else if (props.activeTab === 'pending') {
    filteredList = allBugList.value.filter(bug => getStatusText(bug.status) === '待处理');
  } else if (props.activeTab === 'processing') {
    filteredList = allBugList.value.filter(bug => getStatusText(bug.status) === '处理中');
  } else if (props.activeTab === 'to-verify') {
    filteredList = allBugList.value.filter(bug => getStatusText(bug.status) === '待验证');
  } else if (props.activeTab === 'resolved') {
    filteredList = allBugList.value.filter(bug => getStatusText(bug.status) === '已解决');
  } else if (props.activeTab === 'closed') {
    filteredList = allBugList.value.filter(bug => getStatusText(bug.status) === '已关闭');
  }
  
  // 根据searchQuery筛选
  if (props.searchQuery) {
    const query = props.searchQuery.toLowerCase();
    filteredList = filteredList.filter(bug => 
      bug.name.toLowerCase().includes(query) ||
      bug.projectName.toLowerCase().includes(query)
    );
  }
  
  return filteredList;
});

// 分页后的Bug列表
const pagedBugList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  return bugList.value.slice(start, start + pageSize.value);
});

// 分页事件处理
const handlePageChange = (page) => {
  currentPage.value = page;
};

const handleSizeChange = (size) => {
  pageSize.value = size;
  currentPage.value = 1;
};

// 获取状态的文本
const getStatusText = (status) => {
  if (typeof status === 'string') {
    return status;
  }
  switch (status) {
    case 0:
    case '待处理':
      return '待处理';
    case 1:
    case '处理中':
      return '处理中';
    case 2:
    case '待验证':
      return '待验证';
    case 3:
    case '已解决':
      return '已解决';
    case 4:
    case '已关闭':
      return '已关闭';
    default:
      return '待处理';
  }
};

// 获取状态的数值
const getStatusValue = (status) => {
  if (typeof status === 'number') {
    return status;
  }
  switch (status) {
    case '待处理':
      return 0;
    case '处理中':
      return 1;
    case '待验证':
      return 2;
    case '已解决':
      return 3;
    case '已关闭':
      return 4;
    default:
      return 0;
  }
};

// 从后端获取Bug列表数据
onMounted(() => {
  loadCurrentUser();
  fetchBugs();
});

const loadCurrentUser = () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      currentUser.value = JSON.parse(userStr);
    }
  } catch (error) {
    console.error('加载用户信息失败:', error);
  }
};

const fetchBugs = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get(`/workbench/bugs?username=${user.username}`);
      
      if (response.data.code === 200 && Array.isArray(response.data.data)) {
        allBugList.value = response.data.data.map(item => ({
          id: item.id,
          projectName: item.project_name || '未知项目',
          name: item.title,
          assignee: item.assignee_name || '未指派',
          assigneeId: item.assigneeId,
          priority: getPriorityText(item.severity),
          status: getStatusText(item.status),
          deadline: item.createdAt || '',
          creator: item.creator_name || item.reporter_name || '未知',
          createdAt: item.createdAt || '',
          description: item.description || '',
          solution: item.solution || ''
        }));
      }
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
  }
};

// 将数字优先级转换为文本
const getPriorityText = (priority) => {
  if (typeof priority === 'string') {
    return priority;
  }
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

const getStatusClass = (status) => {
  const statusText = getStatusText(status);
  switch (statusText) {
    case '待处理':
      return 'status-pending';
    case '处理中':
      return 'status-in-progress';
    case '待验证':
      return 'status-to-verify';
    case '已解决':
      return 'status-resolved';
    case '已关闭':
      return 'status-closed';
    default:
      return '';
  }
};

const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  if (isNaN(date.getTime())) return '';
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  return `${year}-${month}-${day}`;
};

// 判断是否可以解决Bug（开发者角色，且状态为待处理或处理中）
const canResolve = (bug) => {
  const statusText = getStatusText(bug.status);
  // 只要状态是待处理或处理中就显示解决按钮
  return (statusText === '待处理' || statusText === '处理中');
};

// 判断是否可以验证Bug（测试者角色，且状态为待验证）
const canVerify = (bug) => {
  const statusText = getStatusText(bug.status);
  // 只要状态是待验证就显示验证按钮
  return statusText === '待验证';
};

// Bug详情对话框
const detailDialogVisible = ref(false);
const detailBug = ref({ 
  name: '',
  projectName: '',
  priority: '',
  status: '',
  deadline: '',
  solution: '',
  description: ''
});

// 编辑Bug对话框
const editDialogVisible = ref(false);
const currentBug = ref({ name: '' });
const editForm = ref({
  name: '',
  projectName: '',
  priority: '',
  status: '',
  deadline: '',
  description: ''
});

// 解决Bug对话框
const resolveDialogVisible = ref(false);
const resolveForm = ref({
  solution: '',
  resolveTime: ''
});
const resolveFileList = ref([]);

// 验证Bug对话框
const verifyDialogVisible = ref(false);
const verifyForm = ref({
  result: 'pass',
  remark: ''
});

// 删除确认对话框
const deleteDialogVisible = ref(false);
const currentDeleteId = ref(null);

const handleEdit = (bug) => {
  currentBug.value = bug;
  editForm.value = {
    name: bug.name,
    projectName: bug.projectName,
    priority: bug.priority,
    status: bug.status,
    deadline: bug.deadline,
    description: bug.description
  };
  editDialogVisible.value = true;
};

const handleResolve = (bug) => {
  currentBug.value = bug;
  resolveForm.value = {
    solution: '',
    resolveTime: ''
  };
  resolveFileList.value = [];
  resolveDialogVisible.value = true;
};

const handleVerify = (bug) => {
  currentBug.value = bug;
  verifyForm.value = {
    result: 'pass',
    remark: ''
  };
  verifyDialogVisible.value = true;
};

const handleResolveFileChange = (file, fileList) => {
  resolveFileList.value = fileList;
};

const handleDelete = (id) => {
  const bug = allBugList.value.find(b => b.id === id);
  if (bug) {
    currentBug.value = bug;
    currentDeleteId.value = id;
    deleteDialogVisible.value = true;
  }
};

const handleRowClick = (row) => {
  detailBug.value = { ...row };
  detailDialogVisible.value = true;
};

const confirmEdit = async () => {
  try {
    // 模拟编辑API
    const response = { data: { code: 200 } };
    
    if (response.data.code === 200) {
      await recordOperationLog('编辑Bug', 'bug', currentBug.value.id, currentBug.value.name);
      editDialogVisible.value = false;
      ElMessage.success('编辑成功');
      await fetchBugs();
    }
  } catch (error) {
    console.error('编辑Bug失败:', error);
    ElMessage.error('编辑失败');
  }
};

const confirmResolve = async () => {
  try {
    // 直接调用后端API更新Bug状态为待验证 (状态值=2)
    const response = await request.put(`/workbench/bugs/${currentBug.value.id}/status`, {
      status: 2,
      solution: resolveForm.value.solution || '已完成修复'
    });
    
    if (response.data.code === 200) {
      await recordOperationLog('提交Bug修复', 'bug', currentBug.value.id, currentBug.value.name);
      resolveDialogVisible.value = false;
      ElMessage.success('提交成功，状态已更新为待验证');
      await fetchBugs();
    } else {
      ElMessage.error(response.data.message || '提交失败');
    }
  } catch (error) {
    console.error('解决Bug失败:', error);
    ElMessage.error('提交失败');
  }
};

const confirmVerify = async () => {
  try {
    const isPass = verifyForm.value.result === 'pass';
    const newStatus = isPass ? 4 : 0; // 4=已关闭, 0=待处理
    const successMessage = isPass ? '验证通过，Bug已关闭' : '验证未通过，已退回待处理';
    
    const response = await request.put(`/workbench/bugs/${currentBug.value.id}/status`, {
      status: newStatus
    });
    
    if (response.data.code === 200) {
      const logAction = isPass ? '验证通过并关闭Bug' : '验证未通过，退回待处理';
      await recordOperationLog(logAction, 'bug', currentBug.value.id, currentBug.value.name);
      verifyDialogVisible.value = false;
      ElMessage.success(successMessage);
      await fetchBugs();
    } else {
      ElMessage.error(response.data.message || '验证失败');
    }
  } catch (error) {
    console.error('验证Bug失败:', error);
    ElMessage.error('验证失败');
  }
};

const confirmDelete = async () => {
  try {
    if (currentDeleteId.value) {
      const response = { data: { code: 200 } };
      
      if (response.data.code === 200) {
        await recordOperationLog('删除Bug', 'bug', currentDeleteId.value, currentBug.value.name);
        deleteDialogVisible.value = false;
        ElMessage.success('删除成功');
        await fetchBugs();
      }
    }
  } catch (error) {
    console.error('删除Bug失败:', error);
    ElMessage.error('删除失败');
  }
};
</script>

<style scoped>
.bug-table-container {
  padding: 0;
  background-color: #fff;
  overflow-x: auto;
  display: flex;
  flex-direction: column;
}

.table-container {
  width: 100%;
  min-width: 800px;
  flex: 1;
}

.pagination-container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 20px;
  border-top: 1px solid #ebeef5;
}

.total-count {
  font-size: 14px;
  color: #606266;
}

.BugTable {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
}

.bug-name {
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

.status-pending {
  color: #E6A23C;
  font-weight: 500;
  font-size: 13px;
}

.status-in-progress {
  color: #409EFF;
  font-weight: 500;
  font-size: 13px;
}

.status-to-verify {
  color: #F56C6C;
  font-weight: 500;
  font-size: 13px;
}

.status-resolved {
  color: #67C23A;
  font-weight: 500;
  font-size: 13px;
}

.status-closed {
  color: #909399;
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

.action-text.disabled {
  color: #909399;
  cursor: not-allowed;
  text-decoration: none !important;
}

.edit-action {
  color: #E6A23C;
}

.resolve-action {
  color: #67C23A;
}

.verify-action {
  color: #409EFF;
}

.delete-action {
  color: #F56C6C;
}

.action-text:hover:not(.disabled) {
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

.detail-item {
  display: flex;
  margin-bottom: 16px;
  align-items: flex-start;
}

.detail-item label {
  width: 100px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
  margin-right: 20px;
}

.detail-item span {
  flex: 1;
  font-size: 14px;
  color: #303133;
  word-break: break-word;
}

.description {
  flex: 1;
  font-size: 14px;
  color: #303133;
  line-height: 1.5;
  margin: 0;
  word-break: break-word;
}

.warning-text {
  color: #F56C6C;
  font-size: 13px;
  margin-top: 10px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}

.upload-demo {
  border: 1px dashed #d9d9d9;
  border-radius: 4px;
  padding: 20px;
  text-align: center;
}

.el-upload__text {
  color: #409eff;
  margin-top: 10px;
}

.el-upload__tip {
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}
</style>
