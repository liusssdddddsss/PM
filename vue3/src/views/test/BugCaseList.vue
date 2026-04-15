<template>
  <div class="bug-table-container">
    <div class="table-container">
      <el-table
          :data="bugList"
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
        <el-table-column label="操作" width="200">
            <template #default="scope">
              <span class="action-text edit-action" @click.stop="handleEdit(scope.row)">编辑</span>
              <span class="action-text resolve-action" @click.stop="handleResolve(scope.row)">解决</span>
              <span class="action-text delete-action" @click.stop="handleDelete(scope.row.id)">删除</span>
            </template>
          </el-table-column>
      </el-table>
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
        <div class="detail-item">
          <label>截止时间：</label>
          <span>{{ detailBug.deadline || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>进度：</label>
          <div class="progress-container">
            <el-progress :percentage="detailBug.progress" :stroke-width="15" />
            <span class="progress-text">{{ detailBug.progress }}%</span>
          </div>
        </div>
        <div class="detail-item">
          <label>工时：</label>
          <span>{{ detailBug.workTime }}</span>
        </div>
        <div class="detail-item">
          <label>剩余工时：</label>
          <span>{{ detailBug.remainingTime }}</span>
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
            <el-option label="已解决" value="已解决"></el-option>
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

    <!-- 解决Bug对话框 -->
    <el-dialog
      v-model="resolveDialogVisible"
      title="解决Bug"
      width="450px"
      :center="true"
    >
      <div class="dialog-content">
        <h4>{{ currentBug.name }}</h4>
        <div class="form-item">
          <label>解决方法：</label>
          <el-input
            v-model="resolveForm.solution"
            type="textarea"
            placeholder="请输入解决方法"
            :rows="3"
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
          <el-button type="primary" @click="confirmResolve">确认解决</el-button>
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
import request from "@/utils/request.js";

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

// 原始Bug数据
const allBugList = ref([]);

// 根据activeTab和searchQuery筛选显示的数据
const bugList = computed(() => {
  // 首先根据activeTab筛选
  let filteredList = [];
  if (props.activeTab === 'all') {
    filteredList = allBugList.value;
  } else if (props.activeTab === 'pending') {
    filteredList = allBugList.value.filter(bug => bug.status === '待处理');
  } else if (props.activeTab === 'processing') {
    filteredList = allBugList.value.filter(bug => bug.status === '处理中');
  } else if (props.activeTab === 'resolved') {
    filteredList = allBugList.value.filter(bug => bug.status === '已解决');
  }
  
  // 然后根据searchQuery筛选
  if (props.searchQuery) {
    const query = props.searchQuery.toLowerCase();
    filteredList = filteredList.filter(bug => 
      bug.name.toLowerCase().includes(query) ||
      bug.projectName.toLowerCase().includes(query)
    );
  }
  
  return filteredList;
});

// 从后端获取Bug列表数据
onMounted(() => {
  fetchBugs();
});

const fetchBugs = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 注意：后端使用username查询，因为bugs表的assignee_id存储的是username
      const response = await request.get(`/dashboard/user-bugs?username=${user.username}`);
      if (response.data.code === 200 && Array.isArray(response.data.data)) {
        // 转换数据格式以匹配前端组件
        allBugList.value = response.data.data.map(item => ({
          id: item.id,
          projectName: item.project || '未知项目',
          name: item.name || item.title,
          priority: item.priority,
          status: item.status,
          deadline: item.deadline || '',
          progress: item.progress || 0,
          workTime: '-',
          remainingTime: '-',
          description: ''
        }));
      }
    }
  } catch (error) {
    console.error('获取Bug列表失败:', error);
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
const getStatusClass = (status) => {
  switch (status) {
    case '处理中':
      return 'status-in-progress';
    case '已解决':
      return 'status-completed';
    case '待处理':
      return 'status-pending';
    default:
      return '';
  }
};

// Bug详情对话框
const detailDialogVisible = ref(false);
const detailBug = ref({ 
  name: '',
  projectName: '',
  priority: '',
  status: '',
  deadline: '',
  progress: 0,
  workTime: '-',
  remainingTime: '-',
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

// 删除确认对话框
const deleteDialogVisible = ref(false);
const currentDeleteId = ref(null);

// 处理操作
const handleEdit = (bug) => {
  console.log('点击编辑按钮:', bug);
  currentBug.value = bug;
  // 填充编辑表单
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
  console.log('点击解决按钮:', bug);
  currentBug.value = bug;
  // 重置解决表单和文件列表
  resolveForm.value = {
    solution: '',
    resolveTime: ''
  };
  resolveFileList.value = [];
  resolveDialogVisible.value = true;
};

// 处理解决Bug附件选择
const handleResolveFileChange = (file, fileList) => {
  console.log('解决Bug附件变化:', file, fileList);
  resolveFileList.value = fileList;
};

const handleDelete = (id) => {
  console.log('点击删除按钮:', id);
  // 查找对应的Bug
  const bug = allBugList.value.find(b => b.id === id);
  if (bug) {
    currentBug.value = bug;
    currentDeleteId.value = id;
    deleteDialogVisible.value = true;
  }
};

// 处理行点击，显示详情
const handleRowClick = (row) => {
  console.log('点击Bug行:', row);
  detailBug.value = { ...row };
  detailDialogVisible.value = true;
};

// 确认编辑Bug
const confirmEdit = async () => {
  try {
    console.log('确认编辑Bug:', currentBug.value.id);
    console.log('编辑表单:', editForm.value);
    
    // 这里可以添加编辑Bug的逻辑，例如调用后端API
    // 模拟API调用
    const response = await request.put('/bug/update', {
      bugId: currentBug.value.id,
      name: editForm.value.name,
      projectName: editForm.value.projectName,
      priority: editForm.value.priority,
      status: editForm.value.status,
      deadline: editForm.value.deadline,
      description: editForm.value.description
    });
    
    if (response.data.code === 200) {
      console.log('Bug编辑成功');
      editDialogVisible.value = false;
      // 重新获取Bug列表
      await fetchBugs();
    }
  } catch (error) {
    console.error('编辑Bug失败:', error);
  }
};

// 确认解决Bug
const confirmResolve = async () => {
  try {
    console.log('确认解决Bug:', currentBug.value.id);
    console.log('解决表单:', resolveForm.value);
    console.log('附件列表:', resolveFileList.value);
    
    // 这里可以添加解决Bug的逻辑，例如调用后端API
    // 模拟API调用，包含附件数据
    const response = await request.post('/bug/resolve', {
      bugId: currentBug.value.id,
      solution: resolveForm.value.solution,
      resolveTime: resolveForm.value.resolveTime,
      attachments: resolveFileList.value
    });
    
    if (response.data.code === 200) {
      console.log('Bug解决成功');
      resolveDialogVisible.value = false;
      // 重新获取Bug列表
      await fetchBugs();
    }
  } catch (error) {
    console.error('解决Bug失败:', error);
  }
};

// 确认删除Bug
const confirmDelete = async () => {
  try {
    if (currentDeleteId.value) {
      console.log('确认删除Bug:', currentDeleteId.value);
      // 这里可以添加删除Bug的逻辑，例如调用后端API
      // 模拟API调用
      const response = await request.delete(`/bug/${currentDeleteId.value}`);
      if (response.data.code === 200) {
        console.log('删除Bug成功:', currentDeleteId.value);
        deleteDialogVisible.value = false;
        // 重新获取Bug列表
        await fetchBugs();
      }
    }
  } catch (error) {
    console.error('删除Bug失败:', error);
  }
};
</script>

<style scoped>
.bug-table-container {
  padding: 0;
  background-color: #fff;
  overflow-x: auto;
}

.table-container {
  width: 100%;
  min-width: 800px;
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

/* 详情项样式 */
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

/* 进度条容器 */
.progress-container {
  flex: 1;
  position: relative;
}

.progress-text {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 12px;
  color: #909399;
}

/* 描述文本 */
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
