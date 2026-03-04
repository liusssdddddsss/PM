<template>
  <div>
    <el-card style="max-width: 100%; margin-bottom: 10px">
      <h3>反馈管理</h3>
      <p>查看和处理普通用户提交的反馈和询问。</p>
    </el-card>

    <!-- 统计信息卡片 -->
    <el-row :gutter="20">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{totalFeedbacks}}</p>
            <span>总反馈数</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{pendingFeedbacks}}</p>
            <span>未处理</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{processedFeedbacks}}</p>
            <span>已处理</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-item">
            <p class="stat-number">{{todayFeedbacks}}</p>
            <span>今日反馈</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索和筛选 -->
    <el-card style="max-width: 100%; margin: 10px 0">
      <div class="search-filter">
        <el-input
          v-model="searchQuery"
          placeholder="搜索反馈内容或用户"
          style="width: 300px; margin-right: 10px"
          prefix-icon="el-icon-search"
        />
        <el-select
          v-model="typeFilter"
          placeholder="选择反馈类型"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="全部类型" value="" />
          <el-option label="功能建议" value="suggestion" />
          <el-option label="bug报告" value="bug" />
          <el-option label="使用问题" value="question" />
          <el-option label="其他" value="other" />
        </el-select>
        <el-select
          v-model="statusFilter"
          placeholder="选择状态"
          style="width: 150px; margin-right: 10px"
        >
          <el-option label="全部状态" value="" />
          <el-option label="未处理" value="pending" />
          <el-option label="处理中" value="processing" />
          <el-option label="已处理" value="processed" />
        </el-select>
        <el-date-picker
          v-model="dateRange"
          type="daterange"
          range-separator="至"
          start-placeholder="开始日期"
          end-placeholder="结束日期"
          style="width: 300px"
        />
      </div>
    </el-card>

    <!-- 反馈列表 -->
    <el-card style="max-width: 100%; margin: 10px 0">
      <h3>反馈列表</h3>
      <el-table :data="filteredFeedbacks" style="width: 100%">
        <el-table-column prop="id" label="反馈ID" width="80" />
        <el-table-column prop="userName" label="用户" width="100" />
        <el-table-column prop="userId" label="工号" width="80" />
        <el-table-column prop="teamName" label="团队" width="120" />
        <el-table-column prop="title" label="标题" min-width="200" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="scope">
            <el-tag :type="getTagType(scope.row.type)">
              {{scope.row.type}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="180" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="scope">
            <el-tag :type="getStatusType(scope.row.status)">
              {{scope.row.status}}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="150">
          <template #default="scope">
            <el-button size="small" type="primary" @click="viewFeedback(scope.row)">查看</el-button>
            <el-button size="small" type="success" @click="processFeedback(scope.row)">处理</el-button>
          </template>
        </el-table-column>
      </el-table>
      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          layout="prev, pager, next"
          :total="filteredFeedbacks.length"
          :page-size="10"
          :current-page="currentPage"
          @current-change="handleCurrentChange"
        />
      </div>
    </el-card>

    <!-- 反馈详情对话框 -->
    <el-dialog
      v-model="feedbackDialogVisible"
      :title="currentFeedback.title || '反馈详情'"
      width="800px"
    >
      <el-form :model="currentFeedback" label-width="100px">
        <el-form-item label="反馈ID">
          <el-input v-model="currentFeedback.id" disabled />
        </el-form-item>
        <el-form-item label="用户">
          <el-input v-model="currentFeedback.userName" disabled />
        </el-form-item>
        <el-form-item label="工号">
          <el-input v-model="currentFeedback.userId" disabled />
        </el-form-item>
        <el-form-item label="团队">
          <el-input v-model="currentFeedback.teamName" disabled />
        </el-form-item>
        <el-form-item label="类型">
          <el-tag :type="getTagType(currentFeedback.type)">{{currentFeedback.type}}</el-tag>
        </el-form-item>
        <el-form-item label="提交时间">
          <el-input v-model="currentFeedback.submitTime" disabled />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="currentFeedback.status" placeholder="请选择状态">
            <el-option label="未处理" value="未处理" />
            <el-option label="处理中" value="处理中" />
            <el-option label="已处理" value="已处理" />
          </el-select>
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input v-model="currentFeedback.content" type="textarea" :rows="5" disabled />
        </el-form-item>
        <el-form-item label="附件">
          <el-upload
            class="upload-demo"
            :action="''"
            :auto-upload="false"
            :file-list="currentFeedback.attachments || []"
            list-type="picture"
          >
            <el-button type="primary">点击上传</el-button>
          </el-upload>
        </el-form-item>
        <el-form-item label="处理回复">
          <el-input v-model="currentFeedback.reply" type="textarea" :rows="3" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="feedbackDialogVisible = false">取消</el-button>
          <el-button type="primary" @click="saveFeedback">保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import {ref, computed} from "vue";

// 统计数据
const totalFeedbacks = 156;
const pendingFeedbacks = 23;
const processedFeedbacks = 133;
const todayFeedbacks = 5;

// 搜索和筛选
const searchQuery = ref('');
const typeFilter = ref('');
const statusFilter = ref('');
const dateRange = ref([]);
const currentPage = ref(1);

// 反馈数据
const feedbacks = ref([
  {
    id: '1',
    userId: '001',
    userName: '张三',
    teamId: '1',
    teamName: '产品团队',
    title: '建议添加批量操作功能',
    type: '功能建议',
    content: '在用户管理页面，希望能够添加批量操作功能，比如批量禁用/启用用户，这样可以提高工作效率。',
    submitTime: '2024-01-15 09:30:00',
    status: '未处理',
    reply: '',
    attachments: []
  },
  {
    id: '2',
    userId: '002',
    userName: '李四',
    teamId: '2',
    teamName: '开发团队',
    title: '登录页面偶尔会出现500错误',
    type: 'bug报告',
    content: '在使用Chrome浏览器登录时，偶尔会出现500内部服务器错误，刷新后又能正常登录。',
    submitTime: '2024-01-15 10:15:00',
    status: '处理中',
    reply: '已收到反馈，正在排查问题。',
    attachments: []
  },
  {
    id: '3',
    userId: '003',
    userName: '王五',
    teamId: '3',
    teamName: '测试团队',
    title: '如何导出测试报告？',
    type: '使用问题',
    content: '在测试管理页面，找不到导出测试报告的功能，请问如何导出测试报告？',
    submitTime: '2024-01-15 11:00:00',
    status: '已处理',
    reply: '在测试报告页面，点击右上角的"导出"按钮即可导出测试报告。',
    attachments: []
  },
  {
    id: '4',
    userId: '004',
    userName: '赵六',
    teamId: '4',
    teamName: '设计团队',
    title: '建议优化界面布局',
    type: '功能建议',
    content: '管理后台的界面布局可以更加紧凑一些，目前有些页面的空白区域太多，影响使用体验。',
    submitTime: '2024-01-15 14:30:00',
    status: '未处理',
    reply: '',
    attachments: []
  },
  {
    id: '5',
    userId: '005',
    userName: '孙七',
    teamId: '2',
    teamName: '开发团队',
    title: '代码提交后无法查看历史记录',
    type: 'bug报告',
    content: '在代码管理页面，提交代码后无法查看历史记录，点击历史记录按钮没有反应。',
    submitTime: '2024-01-15 15:45:00',
    status: '处理中',
    reply: '已确认问题，正在修复中。',
    attachments: []
  },
  {
    id: '6',
    userId: '001',
    userName: '张三',
    teamId: '1',
    teamName: '产品团队',
    title: '希望增加项目甘特图功能',
    type: '功能建议',
    content: '在项目管理页面，希望能够增加甘特图功能，以便更直观地查看项目进度和任务安排。',
    submitTime: '2024-01-15 16:20:00',
    status: '未处理',
    reply: '',
    attachments: []
  }
]);

// 过滤后的反馈
const filteredFeedbacks = computed(() => {
  let result = feedbacks.value;
  
  // 搜索过滤
  if (searchQuery.value) {
    const query = searchQuery.value.toLowerCase();
    result = result.filter(feedback => 
      feedback.userName.toLowerCase().includes(query) ||
      feedback.title.toLowerCase().includes(query) ||
      feedback.content.toLowerCase().includes(query)
    );
  }
  
  // 类型过滤
  if (typeFilter.value) {
    result = result.filter(feedback => feedback.type === typeFilter.value);
  }
  
  // 状态过滤
  if (statusFilter.value) {
    result = result.filter(feedback => feedback.status === statusFilter.value);
  }
  
  // 日期过滤
  if (dateRange.value && dateRange.value.length === 2) {
    const startDate = new Date(dateRange.value[0]);
    const endDate = new Date(dateRange.value[1]);
    endDate.setHours(23, 59, 59, 999);
    
    result = result.filter(feedback => {
      const submitDate = new Date(feedback.submitTime);
      return submitDate >= startDate && submitDate <= endDate;
    });
  }
  
  return result;
});

// 当前反馈
const currentFeedback = ref({});
const feedbackDialogVisible = ref(false);

// 查看反馈
const viewFeedback = (feedback) => {
  currentFeedback.value = {...feedback};
  feedbackDialogVisible.value = true;
};

// 处理反馈
const processFeedback = (feedback) => {
  currentFeedback.value = {...feedback};
  currentFeedback.value.status = '处理中';
  feedbackDialogVisible.value = true;
};

// 保存反馈
const saveFeedback = () => {
  const index = feedbacks.value.findIndex(f => f.id === currentFeedback.value.id);
  if (index !== -1) {
    feedbacks.value[index] = {...currentFeedback.value};
  }
  feedbackDialogVisible.value = false;
};

// 获取标签类型
const getTagType = (type) => {
  switch (type) {
    case '功能建议':
      return 'primary';
    case 'bug报告':
      return 'danger';
    case '使用问题':
      return 'warning';
    default:
      return 'default';
  }
};

// 获取状态类型
const getStatusType = (status) => {
  switch (status) {
    case '未处理':
      return 'danger';
    case '处理中':
      return 'warning';
    case '已处理':
      return 'success';
    default:
      return 'default';
  }
};

// 分页处理
const handleCurrentChange = (page) => {
  currentPage.value = page;
};
</script>

<style scoped>
/* 统计卡片 */
.stat-card {
  height: 120px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #238EFF;
  margin-bottom: 5px;
}

/* 搜索和筛选 */
.search-filter {
  display: flex;
  align-items: center;
  flex-wrap: wrap;
  gap: 10px;
}

/* 表格样式 */
.el-table {
  margin-top: 10px;
}

/* 分页 */
.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

/* 对话框样式 */
.dialog-footer {
  width: 100%;
  display: flex;
  justify-content: flex-end;
}
</style>