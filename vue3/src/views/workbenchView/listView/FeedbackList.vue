<template>
  <div class="feedback-table-container">
    <div class="table-container">
      <el-table
          :data="filteredFeedbackList"
          style="width: 100%"
          class="FeedbackTable"
          :row-style="{height: '45px'}"
          :cell-style="{padding: '4px'}"
      >
        <el-table-column label="序号" width="60">
          <template #default="scope">
            {{ scope.$index + 1 }}
          </template>
        </el-table-column>
        <el-table-column prop="title" label="标题" min-width="200">
          <template #default="scope">
            <span class="feedback-title">{{ scope.row.title }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="priority" label="P" width="60">
          <template #default="scope">
            <span :class="getPriorityClass(scope.row.priority)">{{ scope.row.priority }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态" width="80">
          <template #default="scope">
            <span :class="getStatusClass(scope.row.status)">{{ scope.row.status }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="90"></el-table-column>
        <el-table-column prop="creator" label="创建者" width="100"></el-table-column>
        <el-table-column prop="createdAt" label="创建时间" width="150"></el-table-column>
        <el-table-column prop="updatedAt" label="最后操作时间" width="150"></el-table-column>
        <el-table-column prop="assignee" label="指派对象" width="100"></el-table-column>
        <el-table-column label="操作" width="180">
            <template #default="scope">
              <!-- 非开发者和测试者显示完整操作按钮 -->
              <template v-if="!isDeveloperOrTester">
                <span class="action-text close-action" @click="handleClose(scope.row)">关闭</span>
                <span class="action-text edit-action" @click="handleEdit(scope.row.id)">编辑</span>
                <span class="action-text delete-action" @click="handleDelete(scope.row.id)">删除</span>
              </template>
              <!-- 开发者和测试者只显示查看按钮 -->
              <template v-else>
                <span class="action-text edit-action" @click="handleEdit(scope.row.id)">查看</span>
              </template>
            </template>
          </el-table-column>
      </el-table>
    </div>

    <!-- 关闭反馈对话框 -->
    <el-dialog
      v-model="dialogVisible"
      title="关闭反馈"
      width="400px"
    >
      <div class="dialog-content">
        <h4>{{ currentFeedback.title }}</h4>
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
          <el-button type="primary" @click="confirmClose">关闭反馈</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from "vue";
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

const router = useRouter();

// 接收props
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

// 定义emit事件
const emit = defineEmits(['update:total']);

// 反馈数据
const feedbackList = ref([]);

// 从后端获取反馈列表数据
onMounted(async () => {
  await fetchUserRole();
  await fetchFeedbacks();
});

// 监听状态变化，重新过滤数据
watch(() => props.activeTab, () => {
  // 状态变化时，过滤数据会自动更新
});

// 监听搜索变化，重新过滤数据
watch(() => props.searchQuery, () => {
  // 搜索变化时，过滤数据会自动更新
});

// 格式化日期，只显示日期部分
const formatDate = (dateString) => {
  if (!dateString) return '';
  const date = new Date(dateString);
  return date.toISOString().split('T')[0];
};

const fetchFeedbacks = async () => {
  try {
    const response = await request.get('/api/feedback/list');
    console.log('获取反馈列表响应:', response);
    if (response.data.code === 200) {
      // 转换数据格式以匹配前端组件
      feedbackList.value = response.data.data.map(item => ({
        id: item.id,
        title: item.title,
        priority: item.priority || 3,
        status: item.status || '待处理',
        type: item.type || '',
        assignee: item.assigneeName || '未指派',
        solution: item.solution || '',
        creator: item.creatorName || '',
        createdAt: formatDate(item.createdAt),
        updatedAt: formatDate(item.updatedAt),
        relatedObjects: item.relatedObjects || 0
      }));
      console.log('转换后的反馈列表数据:', feedbackList.value);
      // 发送实际数据数量给父组件
      emit('update:total', feedbackList.value.length);
    }
  } catch (error) {
    console.error('获取反馈列表失败:', error);
  }
};

// 过滤后的反馈列表
const filteredFeedbackList = computed(() => {
  let filtered = [...feedbackList.value];
  
  // 根据状态过滤
  if (props.activeTab !== 'all') {
    switch (props.activeTab) {
      case 'pending':
        filtered = filtered.filter(item => item.status === '待处理');
        break;
      case 'processing':
        filtered = filtered.filter(item => item.status === '处理中');
        break;
      case 'toClose':
        filtered = filtered.filter(item => item.status === '待关闭');
        break;
    }
  }
  
  // 根据搜索关键词过滤
  if (props.searchQuery) {
    const keyword = props.searchQuery.toLowerCase();
    filtered = filtered.filter(item => {
      return item.title.toLowerCase().includes(keyword) ||
             item.description?.toLowerCase().includes(keyword) ||
             item.creator.toLowerCase().includes(keyword) ||
             item.assignee.toLowerCase().includes(keyword);
    });
  }
  
  return filtered;
});

// 获取优先级的类名
const getPriorityClass = (priority) => {
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

// 获取状态的类名
const getStatusClass = (status) => {
  switch (status) {
    case '处理中':
      return 'status-in-progress';
    case '已完成':
      return 'status-completed';
    default:
      return '';
  }
};

// 关闭反馈对话框
const dialogVisible = ref(false);
const currentFeedback = ref({ title: '' });
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

// 处理操作
const handleClose = (feedback) => {
  currentFeedback.value = feedback;
  dialogVisible.value = true;
};

const handleEdit = (id) => {
  console.log('编辑反馈:', id);
  // 跳转到编辑页面
  router.push(`/feedbacks/edit/${id}`);
};

const handleDelete = async (id) => {
  try {
    const response = await request.delete(`/api/feedback/delete/${id}`);
    if (response.data.code === 200) {
      console.log('删除反馈成功:', id);
      // 重新获取反馈列表
      await fetchFeedbacks();
    } else {
      console.error('删除反馈失败:', response.data.message);
    }
  } catch (error) {
    console.error('删除反馈失败:', error);
  }
};

// 确认关闭反馈
const confirmClose = async () => {
  try {
    const response = await request.post(`/api/feedback/close/${currentFeedback.value.id}`, {
      expectedCompleteTime: closeForm.value.expectedCompleteTime,
      actualCompleteTime: closeForm.value.actualCompleteTime,
      remark: closeForm.value.remark
    });
    if (response.data.code === 200) {
      console.log('关闭反馈成功:', currentFeedback.value.id);
      dialogVisible.value = false;
      // 重新获取反馈列表
      await fetchFeedbacks();
    } else {
      console.error('关闭反馈失败:', response.data.message);
    }
  } catch (error) {
    console.error('关闭反馈失败:', error);
  }
};
</script>

<style scoped>
.feedback-table-container {
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

.FeedbackTable {
  border-radius: 0;
  overflow: hidden;
  border: none !important;
}

.feedback-title {
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
</style>
