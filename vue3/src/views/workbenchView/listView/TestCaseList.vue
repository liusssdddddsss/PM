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
        <el-table-column label="优先级" width="80">
          <template #default="scope">
            <span :class="getPriorityClass(scope.row.priorityValue)">{{ getPriorityText(scope.row.priorityValue) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="80">
          <template #default="scope">
            <span :class="getStatusClass(scope.row.statusValue)">{{ getStatusText(scope.row.statusValue) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="截止时间" width="120">
          <template #default="scope">
            {{ formatDate(scope.row.deadline) || '无' }}
          </template>
        </el-table-column>
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
                <!-- 已关闭状态（status === 3） -->
                <template v-if="scope.row.status === 3">
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
        <div v-if="testForm.result === '部分通过'" class="form-item">
          <label>测试进度：</label>
          <el-slider
            v-model="testForm.progress"
            :min="0"
            :max="100"
            :step="10"
            show-input
            style="width: 100%"
          />
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
            <el-option label="紧急" :value="1"></el-option>
            <el-option label="一般" :value="2"></el-option>
            <el-option label="正常" :value="3"></el-option>
          </el-select>
        </div>
        <div class="form-item">
          <label>状态：</label>
          <el-select
            v-model="editForm.status"
            placeholder="选择状态"
            style="width: 100%"
          >
            <el-option label="已完成" :value="0"></el-option>
            <el-option label="待测试" :value="1"></el-option>
            <el-option label="测试中" :value="2"></el-option>
            <el-option label="已关闭" :value="3"></el-option>
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
          <label>所属项目：</label>
          <span>{{ detailTestCase.projectName }}</span>
        </div>
        <div class="detail-item">
          <label>所属产品：</label>
          <span>{{ detailTestCase.productName || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>优先级：</label>
          <span :class="getPriorityClass(detailTestCase.priorityValue)">{{ getPriorityText(detailTestCase.priorityValue) }}</span>
        </div>
        <div class="detail-item">
          <label>状态：</label>
          <span :class="getStatusClass(detailTestCase.statusValue)">{{ getStatusText(detailTestCase.statusValue) }}</span>
        </div>
        <div class="detail-item">
          <label>截止时间：</label>
          <span>{{ detailTestCase.deadline || '无' }}</span>
        </div>
        <div class="detail-item">
          <label>进度：</label>
          <div class="progress-container">
            <el-progress :percentage="detailTestCase.progress" :stroke-width="15" :show-text="false" />
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
import { ElMessage, ElMessageBox } from "element-plus";
import request from "@/utils/request.js";
import { recordOperationLog } from "@/utils/operationLog.js";

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
  },
  role: {
    type: String,
    default: ''
  }
});

const router = useRouter();

// 当前登录用户
const currentUser = ref(null);
// 当前用户所在团队的成员ID
const teamMemberIds = ref(new Set());

// 原始测试用例数据
const allTestCaseList = ref([]);
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
  
  // 最后根据role筛选
  if (props.role === 'productManager') {
    // 产品经理：显示团队成员负责的所有测试用例
    if (teamMemberIds.value.size > 0) {
      filteredList = filteredList.filter(tc => {
        return teamMemberIds.value.has(Number(tc.assigneeId));
      });
    }
  } else if (props.role === 'tester') {
    // 测试者：显示与自己有关的（自己负责的）
    if (currentUser.value && currentUser.value.id) {
      filteredList = filteredList.filter(tc => {
        return Number(tc.assigneeId) === currentUser.value.id;
      });
    }
  }
  
  return filteredList;
});

// 总条数（根据筛选后的结果计算）
const total = computed(() => {
  return testCaseList.value.length;
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
  await fetchCurrentUser();
  await fetchUserRole();
  if (props.role === 'productManager') {
    await fetchTeamMembers();
  }
  await fetchTestCases();
});

// 获取当前登录用户信息
const fetchCurrentUser = async () => {
  try {
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const username = user.username;
      const userRes = await request.get(`/admin/findAll`);
      if (userRes.data.code === 200 && Array.isArray(userRes.data.data)) {
        const foundUser = userRes.data.data.find(u => u.username == String(username));
        if (foundUser) {
          currentUser.value = foundUser;
        }
      }
    }
  } catch (error) {
    console.error('获取当前用户信息失败:', error);
  }
};

// 获取团队成员ID列表
const fetchTeamMembers = async () => {
  try {
    if (!currentUser.value || !currentUser.value.id) return;
    
    const response = await request.get(`/teams/user-teams/${currentUser.value.id}`);
    if (response.data.code === 200) {
      const teams = response.data.data || [];
      const memberIds = new Set();
      
      for (const team of teams) {
        try {
          const memberRes = await request.get(`/teams/${team.id}/members`);
          if (memberRes.data.code === 200) {
            const members = memberRes.data.data || [];
            members.forEach(member => {
              if (member.userId) {
                memberIds.add(Number(member.userId));
              }
            });
          }
        } catch (e) {
          console.error('获取团队成员失败:', e);
        }
      }
      
      teamMemberIds.value = memberIds;
    }
  } catch (error) {
    console.error('获取团队成员失败:', error);
  }
};

const fetchTestCases = async () => {
  try {
    const userStr = localStorage.getItem('user');
    let username = null;
    if (userStr) {
      const user = JSON.parse(userStr);
      username = user.username;
    }
    
    const response = await request.get('/dashboard/test-cases', { params: { username } });
    console.log('获取测试用例列表响应:', response);
    console.log('response.data:', response.data);
    console.log('response.data.code:', response.data.code);
    console.log('response.data.data:', response.data.data);
    console.log('response.data.data类型:', Array.isArray(response.data.data) ? '数组' : typeof response.data.data);
    if (response.data.code === 200) {
      if (Array.isArray(response.data.data)) {
        // 转换数据格式以匹配前端组件
        allTestCaseList.value = response.data.data.map(item => {
          const progress = item.progress || 0;
          // 根据进度判断状态：进度为0是待测试，进度不为0是测试中
          let statusValue;
          if (progress === 0) {
            statusValue = 1; // 待测试
          } else if (progress > 0 && progress < 100) {
            statusValue = 2; // 测试中
          } else {
            statusValue = 0; // 已完成
          }
          
          return {
            id: item.id,
            projectName: item.project_name,
            productName: item.product_name || '',
            name: item.name || item.title,
            priority: getPriorityText(item.priority),
            priorityValue: item.priority, // 保留原始数字用于编辑
            status: getStatusText(statusValue),
            statusValue: statusValue,
            deadline: item.due_date,
            progress: progress,
            createdAt: item.created_at || '',
            assignee: item.assignee || '',
            assigneeId: item.assignee_id || item.assigneeId,
            description: ''
          };
        });
        console.log('转换后的测试用例列表数据:', allTestCaseList.value);
        console.log('转换后的长度:', allTestCaseList.value.length);
        // total.value 将通过 computed 属性自动计算
      } else {
        console.error('response.data.data不是数组:', response.data.data);
        allTestCaseList.value = [];
        total.value = 0;
      }
    } else {
      console.error('响应code不是200:', response.data.code);
    }
  } catch (error) {
    console.error('获取测试用例列表失败:', error);
  }
};

// 获取优先级的文本
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

// 获取优先级的类名
const getPriorityClass = (priority) => {
  // priority现在是数字：1=紧急，2=一般，3=正常
  switch (priority) {
    case 1:
      return 'priority-urgent';
    case 2:
      return 'priority-normal';
    case 3:
      return 'priority-regular';
    default:
      return '';
  }
};

// 获取状态的文本
const getStatusText = (status) => {
  switch (status) {
    case 0:
      return '已完成';
    case 1:
      return '待测试';
    case 2:
      return '测试中';
    case 3:
      return '已关闭';
    default:
      return '待测试';
  }
};

// 获取状态的类名
const getStatusClass = (status) => {
  switch (status) {
    case 2:
      return 'status-in-progress';
    case 0:
      return 'status-completed';
    case 3:
      return 'status-closed';
    default:
      return '';
  }
};

// 格式化日期，只显示到天
const formatDate = (dateStr) => {
  if (!dateStr) return '';
  let result = dateStr;
  
  // 先处理 ISO 格式的日期
  if (result.includes('T')) {
    result = result.split('T')[0];
  } else if (result.includes(' ')) {
    result = result.split(' ')[0];
  }
  
  // 处理毫秒部分
  if (result.includes('.')) {
    result = result.split('.')[0];
  }
  
  // 移除 Z 字符
  if (result.includes('Z')) {
    result = result.replace('Z', '');
  }
  
  return result;
};

// 测试成果提交对话框
const testDialogVisible = ref(false);
const currentTestCase = ref({ name: '' });
const testForm = ref({
  result: '',
  progress: 0,
  reportUrl: '',
  description: '',
  files: []
});

// 关闭测试用例对话框
const closeDialogVisible = ref(false);
const closeForm = ref({
  reason: ''
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
  productName: '',
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
  
  ElMessageBox.confirm(
    '确定要关闭这个测试用例吗？',
    '提示',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const response = await request.post('/dashboard/test-cases/close', {
        testCaseId: testCase.id,
        reason: '用户手动关闭'
      });
      
      if (response.data.code === 200) {
        ElMessage.success('测试用例已关闭');
        await recordOperationLog('关闭测试用例', 'testcase', testCase.id, testCase.name);
        await fetchTestCases();
      } else {
        ElMessage.error('关闭失败');
      }
    } catch (error) {
      console.error('关闭失败:', error);
      ElMessage.error('关闭失败: ' + (error.response?.data?.msg || error.message));
    }
  }).catch(() => {
    ElMessage.info('已取消关闭');
  });
};

const handleOpen = async (testCase) => {
  console.log('点击打开按钮:', testCase);
  await openTestCase(testCase.id);
};

const handleEdit = (testCase) => {
  console.log('点击编辑按钮:', testCase);
  currentTestCase.value = testCase;
  // 填充编辑表单，使用原始数字值
  editForm.value = {
    name: testCase.name,
    projectName: testCase.projectName,
    priority: testCase.priorityValue !== undefined ? testCase.priorityValue : testCase.priority,
    status: testCase.statusValue !== undefined ? testCase.statusValue : testCase.status,
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
    progress: 0,
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
    
    let status;
    if (testForm.value.progress === 100) {
      status = 0;
    } else if (testForm.value.progress > 0) {
      status = 2;
    } else {
      status = 1;
    }
    
    const response = await request.post('/dashboard/test/submit-result', {
      testCaseId: currentTestCase.value.id,
      result: testForm.value.result,
      progress: testForm.value.progress,
      status: status,
      reportUrl: testForm.value.reportUrl,
      description: testForm.value.description,
    });
    
    if (response.data.code === 200) {
      console.log('测试成果提交成功');
      await recordOperationLog('提交测试成果', 'testcase', currentTestCase.value.id, currentTestCase.value.name);
      ElMessage.success('测试成果提交成功');
      testDialogVisible.value = false;
      await fetchTestCases();
    } else {
      ElMessage.error('提交失败: ' + (response.data.msg || '未知错误'));
    }
  } catch (error) {
    console.error('测试成果提交失败:', error);
    ElMessage.error('提交失败: ' + (error.response?.data?.msg || error.message));
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
      reason: closeForm.value.reason
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
    
    const response = await request.post('/dashboard/test-cases/open', {
      testCaseId: testCaseId
    });
    
    if (response.data.code === 200) {
      console.log('测试用例打开成功');
      const testCase = allTestCaseList.value.find(tc => tc.id === testCaseId);
      await recordOperationLog('打开测试用例', 'testcase', testCaseId, testCase?.name || '测试用例');
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
    
    const priorityValue = typeof editForm.value.priority === 'number' 
      ? editForm.value.priority 
      : parseInt(editForm.value.priority) || 3;
    const statusValue = typeof editForm.value.status === 'number' 
      ? editForm.value.status 
      : parseInt(editForm.value.status) || 1;
    
    const response = await request.put('/dashboard/test/update', {
      testCaseId: currentTestCase.value.id,
      name: editForm.value.name,
      projectName: editForm.value.projectName,
      priority: priorityValue,
      status: statusValue,
      deadline: editForm.value.deadline,
      description: editForm.value.description
    });
    
    if (response.data.code === 200) {
      console.log('测试用例编辑成功');
      await recordOperationLog('编辑测试用例', 'testcase', currentTestCase.value.id, currentTestCase.value.name);
      ElMessage.success('测试用例编辑成功');
      editDialogVisible.value = false;
      await fetchTestCases();
    } else {
      ElMessage.error('编辑失败: ' + (response.data.msg || '未知错误'));
    }
  } catch (error) {
    console.error('编辑测试用例失败:', error);
    let errorMsg = '编辑失败';
    if (error.response?.status === 404) {
      errorMsg = '接口不存在，请检查后端服务';
    } else if (error.response?.status === 500) {
      errorMsg = '服务器内部错误';
    } else if (error.response?.data?.msg) {
      errorMsg = error.response.data.msg;
    } else if (error.message) {
      errorMsg = error.message;
    }
    ElMessage.error(errorMsg);
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
        await recordOperationLog('删除测试用例', 'testcase', currentDeleteId.value, currentTestCase.value.name);
        deleteDialogVisible.value = false;
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
