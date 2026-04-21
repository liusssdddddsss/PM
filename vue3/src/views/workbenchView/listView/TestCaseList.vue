<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="paginatedTestCaseList"
          style="width: 100%"
          class="TaskTable"
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
            <span class="task-name">{{ scope.row.projectName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="name" label="测试用例名称" min-width="180">
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
        <el-table-column label="创建时间" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.createdAt) }}
          </template>
        </el-table-column>
        <el-table-column prop="assignee" label="负责人" width="100"></el-table-column>
        <el-table-column label="操作" width="240">
            <template #default="scope">
              <!-- 非开发者和测试者显示完整操作按钮 -->
              <template v-if="!isDeveloperOrTester">
                <!-- 已关闭状态 -->
                <template v-if="scope.row.status === '已关闭'">
                  <span class="action-text close-action" @click.stop="handleOpen(scope.row)">打开</span>
                  <span class="action-text edit-action disabled">编辑</span>
                  <span class="action-text submit-action disabled">提交测试</span>
                  <span class="action-text delete-action" @click.stop="handleDelete(scope.row.id)">删除</span>
                </template>
                <!-- 未关闭状态 -->
                <template v-else>
                  <span class="action-text close-action" @click.stop="handleClose(scope.row)">关闭</span>
                  <span class="action-text edit-action" @click.stop="handleEdit(scope.row)">编辑</span>
                  <span class="action-text submit-action" @click.stop="handleSubmitTest(scope.row)">提交测试</span>
                  <span class="action-text delete-action" @click.stop="handleDelete(scope.row.id)">删除</span>
                </template>
              </template>
              <!-- 开发者和测试者只显示查看和提交测试按钮 -->
              <template v-else>
                <span class="action-text edit-action" @click.stop="handleRowClick(scope.row)">查看</span>
                <span class="action-text submit-action" @click.stop="handleSubmitTest(scope.row)">提交测试</span>
              </template>
            </template>
          </el-table-column>
      </el-table>
    </div>

    <!-- 测试成果提交对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="提交测试成果"
      width="500px"
      :center="true"
    >
      <div class="dialog-content">
        <h4>{{ currentTestCase.name }}</h4>
        <div class="form-item">
          <label>测试结果：</label>
          <el-radio-group v-model="testForm.result">
            <el-radio label="通过">通过</el-radio>
            <el-radio label="失败">失败</el-radio>
            <el-radio label="部分通过">部分通过</el-radio>
          </el-radio-group>
        </div>
        <div class="form-item">
          <label>测试报告链接：</label>
          <el-input
            v-model="testForm.reportUrl"
            placeholder="请输入测试报告链接"
            style="width: 100%"
          />
        </div>
        <div class="form-item">
          <label>测试描述：</label>
          <el-input
            v-model="testForm.description"
            type="textarea"
            placeholder="请输入测试描述"
            :rows="3"
          />
        </div>
        <div class="form-item">
          <label>测试文件：</label>
          <el-upload
            class="upload-demo"
            :auto-upload="false"
            :on-change="handleFileChange"
            :limit="5"
            :file-list="testForm.files"
          >
            <el-button type="primary">选择文件</el-button>
            <template #tip>
              <div class="el-upload__tip">
                最多上传5个文件，支持zip、rar、pdf格式
              </div>
            </template>
          </el-upload>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="testDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmSubmitTest">提交测试</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 关闭测试用例对话框 -->
    <el-dialog
      v-model="closeDialogVisible"
      title="关闭测试用例"
      width="400px"
      :center="true"
    >
      <div class="dialog-content">
        <h4>{{ currentTestCase.name }}</h4>
        <div class="form-item">
          <label>关闭原因：</label>
          <el-input
            v-model="closeForm.reason"
            type="textarea"
            placeholder="请输入关闭原因"
            :rows="3"
          />
        </div>
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
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="closeDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="confirmClose">确认关闭</el-button>
        </span>
      </template>
    </el-dialog>

    <!-- 编辑测试用例对话框 -->
    <el-dialog
      v-model="editDialogVisible"
      title="编辑测试用例"
      width="500px"
      :center="true"
    >
      <div class="dialog-content">
        <h4>编辑测试用例</h4>
        <div class="form-item">
          <label>测试用例名称：</label>
          <el-input
            v-model="editForm.name"
            placeholder="请输入测试用例名称"
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
            <el-option label="待测试" value="待测试"></el-option>
            <el-option label="测试中" value="测试中"></el-option>
            <el-option label="已完成" value="已完成"></el-option>
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
          <label>测试描述：</label>
          <el-input
            v-model="editForm.description"
            type="textarea"
            placeholder="请输入测试描述"
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

    <!-- 测试用例详情对话框 -->
    <el-dialog
      v-model="detailDialogVisible"
      title="测试用例详情"
      width="600px"
      :center="true"
    >
      <div class="dialog-content">
        <div class="detail-item">
          <label>测试用例名称：</label>
          <span>{{ detailTestCase.name }}</span>
        </div>
        <div class="detail-item">
          <label>项目名称：</label>
          <span>{{ detailTestCase.projectName }}</span>
        </div>
        <div class="detail-item">
          <label>优先级：</label>
          <span :class="getPriorityClass(detailTestCase.priority)">{{ detailTestCase.priority }}</span>
        </div>
        <div class="detail-item">
          <label>状态：</label>
          <span :class="getStatusClass(detailTestCase.status)">{{ detailTestCase.status }}</span>
        </div>
        <div class="detail-item">
          <label>截止时间：</label>
          <span>{{ detailTestCase.deadline || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>进度：</label>
          <div class="progress-container">
            <el-progress :percentage="detailTestCase.progress" :stroke-width="15" />
            <span class="progress-text">{{ detailTestCase.progress }}%</span>
          </div>
        </div>
        <div class="detail-item">
          <label>创建时间：</label>
          <span>{{ formatDate(detailTestCase.createdAt) || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>负责人：</label>
          <span>{{ detailTestCase.assignee || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>测试描述：</label>
          <p class="description">{{ detailTestCase.description || '无' }}</p>
        </div>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="detailDialogVisible = false">关闭</el-button>
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
        <p>您确定要删除测试用例 <strong>{{ currentTestCase.name }}</strong> 吗？</p>
        <p class="warning-text">删除后将无法恢复，请谨慎操作。</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="deleteDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="confirmDelete">确认删除</el-button>
        </span>
      </template>
    </el-dialog>
    
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

// 原始测试用例数据
const allTestCaseList = ref([]);
const total = ref(0);
const currentPage = ref(1);
const pageSize = ref(20);

// 根据activeTab和searchQuery筛选显示的数据
const testCaseList = computed(() => {
  // 首先根据activeTab筛选
  let filteredList = [];
  if (props.activeTab === 'all') {
    filteredList = allTestCaseList.value;
  } else if (props.activeTab === 'stayingTest') {
    filteredList = allTestCaseList.value.filter(tc => tc.status === '待测试');
  } else if (props.activeTab === 'testing') {
    filteredList = allTestCaseList.value.filter(tc => tc.status === '测试中');
  } else if (props.activeTab === 'finish') {
    filteredList = allTestCaseList.value.filter(tc => tc.status === '已完成');
  }
  
  // 然后根据searchQuery筛选
  if (props.searchQuery) {
    const query = props.searchQuery.toLowerCase();
    filteredList = filteredList.filter(tc => 
      tc.name.toLowerCase().includes(query) ||
      tc.projectName.toLowerCase().includes(query)
    );
  }
  
  return filteredList;
});

// 分页后的测试用例列表
const paginatedTestCaseList = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return testCaseList.value.slice(start, end);
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

// 从后端获取测试用例列表数据
onMounted(async () => {
  await fetchUserRole();
  await fetchTestCases();
});

const fetchTestCases = async () => {
  try {
    const response = await request.get('/dashboard/test-cases');
    console.log('获取测试用例列表响应:', response);
    if (response.data.code === 200) {
      // 转换数据格式以匹配前端组件
        allTestCaseList.value = response.data.data.map(item => ({
          id: item.id,
          projectName: item.project_name,
          name: item.name || item.title,
          priority: getPriorityText(item.priority),
          status: getStatusText(item.status),
          deadline: item.due_date,
          progress: item.progress || 0,
          createdAt: item.created_at || '',
          assignee: item.assignee || '',
          description: ''
        }));
      console.log('转换后的测试用例列表数据:', allTestCaseList.value);
      total.value = allTestCaseList.value.length;
    }
  } catch (error) {
    console.error('获取测试用例列表失败:', error);
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
      return '待测试';
    case 2:
      return '测试中';
    case 3:
      return '已完成';
    case 4:
      return '已关闭';
    default:
      return '待测试';
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
    case '测试中':
      return 'status-in-progress';
    case '已完成':
      return 'status-completed';
    case '已关闭':
      return 'status-closed';
    default:
      return '';
  }
};

// 格式化日期
const formatDate = (date) => {
  if (!date) return '';
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  return d.toLocaleDateString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit'
  });
};

// 测试成果提交对话框
const testDialogVisible = ref(false);
const currentTestCase = ref({ name: '' });
const testForm = ref({
  result: '',
  reportUrl: '',
  description: '',
  files: []
});

// 关闭测试用例对话框
const closeDialogVisible = ref(false);
const closeForm = ref({
  reason: '',
  expectedCompleteTime: '',
  actualCompleteTime: ''
});

// 编辑测试用例对话框
const editDialogVisible = ref(false);
const editForm = ref({
  name: '',
  projectName: '',
  priority: '',
  status: '',
  deadline: '',
  description: ''
});

// 测试用例详情对话框
const detailDialogVisible = ref(false);
const detailTestCase = ref({ 
  name: '',
  projectName: '',
  priority: '',
  status: '',
  deadline: '',
  progress: 0,
  createdAt: '',
  assignee: '',
  description: ''
});

// 删除确认对话框
const deleteDialogVisible = ref(false);
const currentDeleteId = ref(null);

// 处理操作
const handleClose = (testCase) => {
  console.log('点击关闭按钮:', testCase);
  currentTestCase.value = testCase;
  // 重置关闭表单
  closeForm.value = {
    reason: '',
    expectedCompleteTime: '',
    actualCompleteTime: ''
  };
  closeDialogVisible.value = true;
};

const handleOpen = (testCase) => {
  console.log('点击打开按钮:', testCase);
  openTestCase(testCase.id);
};

const handleEdit = (testCase) => {
  console.log('点击编辑按钮:', testCase);
  currentTestCase.value = testCase;
  // 填充编辑表单
  editForm.value = {
    name: testCase.name,
    projectName: testCase.projectName,
    priority: testCase.priority,
    status: testCase.status,
    deadline: testCase.deadline,
    description: testCase.description
  };
  editDialogVisible.value = true;
};

const handleDelete = (id) => {
  console.log('点击删除按钮:', id);
  // 查找对应的测试用例
  const testCase = allTestCaseList.value.find(tc => tc.id === id);
  if (testCase) {
    currentTestCase.value = testCase;
    currentDeleteId.value = id;
    deleteDialogVisible.value = true;
  }
};

// 处理测试成果提交
const handleSubmitTest = (testCase) => {
  console.log('点击提交测试按钮:', testCase);
  currentTestCase.value = testCase;
  // 重置测试提交表单
  testForm.value = {
    result: '',
    reportUrl: '',
    description: '',
    files: []
  };
  testDialogVisible.value = true;
};

// 处理行点击，显示详情
const handleRowClick = (row) => {
  console.log('点击测试用例行:', row);
  detailTestCase.value = { ...row };
  detailDialogVisible.value = true;
};

// 处理文件选择
const handleFileChange = (file, fileList) => {
  testForm.value.files = fileList;
};

// 确认提交测试成果
const confirmSubmitTest = async () => {
  try {
    console.log('确认提交测试成果:', currentTestCase.value.id);
    console.log('测试提交表单:', testForm.value);
    
    // 这里可以添加测试成果提交的逻辑，例如调用后端API
    // 模拟API调用
    const response = await request.post('/test/submit-result', {
      testCaseId: currentTestCase.value.id,
      result: testForm.value.result,
      reportUrl: testForm.value.reportUrl,
      description: testForm.value.description,
      // 注意：文件上传需要特殊处理，这里只是示例
    });
    
    if (response.data.code === 200) {
      console.log('测试成果提交成功');
      testDialogVisible.value = false;
      // 重新获取测试用例列表
      await fetchTestCases();
    }
  } catch (error) {
    console.error('测试成果提交失败:', error);
  }
};

// 确认关闭测试用例
const confirmClose = async () => {
  try {
    console.log('确认关闭测试用例:', currentTestCase.value.id);
    console.log('关闭表单:', closeForm.value);
    
    // 调用关闭测试用例的API
    const response = await request.post('/dashboard/test-cases/close', {
      testCaseId: currentTestCase.value.id,
      reason: closeForm.value.reason,
      expectedCompleteTime: closeForm.value.expectedCompleteTime,
      actualCompleteTime: closeForm.value.actualCompleteTime
    });
    
    if (response.data.code === 200) {
      console.log('测试用例关闭成功');
      closeDialogVisible.value = false;
      // 重新获取测试用例列表
      await fetchTestCases();
    }
  } catch (error) {
    console.error('关闭测试用例失败:', error);
  }
};

// 打开测试用例
const openTestCase = async (testCaseId) => {
  try {
    console.log('打开测试用例:', testCaseId);
    
    // 调用打开测试用例的API
    const response = await request.post('/dashboard/test-cases/open', {
      testCaseId: testCaseId
    });
    
    if (response.data.code === 200) {
      console.log('测试用例打开成功');
      // 重新获取测试用例列表
      await fetchTestCases();
    }
  } catch (error) {
    console.error('打开测试用例失败:', error);
  }
};

// 确认编辑测试用例
const confirmEdit = async () => {
  try {
    console.log('确认编辑测试用例:', currentTestCase.value.id);
    console.log('编辑表单:', editForm.value);
    
    // 这里可以添加编辑测试用例的逻辑，例如调用后端API
    // 模拟API调用
    const response = await request.put('/test/update', {
      testCaseId: currentTestCase.value.id,
      name: editForm.value.name,
      projectName: editForm.value.projectName,
      priority: editForm.value.priority,
      status: editForm.value.status,
      deadline: editForm.value.deadline,
      description: editForm.value.description
    });
    
    if (response.data.code === 200) {
      console.log('测试用例编辑成功');
      editDialogVisible.value = false;
      // 重新获取测试用例列表
      await fetchTestCases();
    }
  } catch (error) {
    console.error('编辑测试用例失败:', error);
  }
};

// 确认删除测试用例
const confirmDelete = async () => {
  try {
    if (currentDeleteId.value) {
      console.log('确认删除测试用例:', currentDeleteId.value);
      const response = await request.delete(`/dashboard/test-cases/${currentDeleteId.value}`);
      if (response.data.code === 200) {
        console.log('删除测试用例成功:', currentDeleteId.value);
        deleteDialogVisible.value = false;
        // 重新获取测试用例列表
        await fetchTestCases();
      }
    }
  } catch (error) {
    console.error('删除测试用例失败:', error);
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

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
</style>
