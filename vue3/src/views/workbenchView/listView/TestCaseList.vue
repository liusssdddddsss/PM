<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="testCaseList"
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
        <el-table-column prop="workTime" label="工时" width="80"></el-table-column>
        <el-table-column prop="remainingTime" label="剩余工时" width="80"></el-table-column>
        <el-table-column label="操作" width="240">
            <template #default="scope">
              <span class="action-text close-action" @click="handleClose(scope.row)">关闭</span>
              <span class="action-text edit-action" @click="goToTestEdit">编辑</span>
              <span class="action-text submit-action" @click="handleSubmitTest(scope.row)">提交测试</span>
              <span class="action-text delete-action" @click="handleDelete(scope.row.id)">删除</span>
            </template>
          </el-table-column>
      </el-table>
    </div>

    <!-- 测试成果提交对话框 -->
    <el-dialog
      v-model="testDialogVisible"
      title="提交测试成果"
      width="500px"
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
  }
});

const router = useRouter();
const goToTestEdit = () =>{
  router.push('/test/testSubmit');
}

// 原始测试用例数据
const allTestCaseList = ref([]);

// 根据activeTab筛选显示的数据
const testCaseList = computed(() => {
  if (props.activeTab === 'all') {
    return allTestCaseList.value;
  } else if (props.activeTab === 'stayingTest') {
    return allTestCaseList.value.filter(tc => tc.status === '待测试');
  } else if (props.activeTab === 'testing') {
    return allTestCaseList.value.filter(tc => tc.status === '测试中');
  } else if (props.activeTab === 'finish') {
    return allTestCaseList.value.filter(tc => tc.status === '已完成');
  }
  return [];
});

// 从后端获取测试用例列表数据
onMounted(() => {
  fetchTestCases();
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
          workTime: '-',
          remainingTime: '-'
        }));
      console.log('转换后的测试用例列表数据:', allTestCaseList.value);
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
    case 0:
      return '待测试';
    case 1:
      return '测试中';
    case 2:
      return '已完成';
    case 3:
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
    default:
      return '';
  }
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

// 处理操作
const handleClose = (testCase) => {
  // 这里可以添加关闭测试用例的逻辑
  console.log('关闭测试用例:', testCase);
};

const handleDelete = async (id) => {
  try {
    const response = await request.delete(`/workbench/test-cases/${id}`);
    if (response.data.code === 200) {
      console.log('删除测试用例成功:', id);
      // 重新获取测试用例列表
      await fetchTestCases();
    }
  } catch (error) {
    console.error('删除测试用例失败:', error);
  }
};

// 处理测试成果提交
const handleSubmitTest = (testCase) => {
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
      // 可以添加成功提示
    }
  } catch (error) {
    console.error('测试成果提交失败:', error);
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

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
}
</style>
