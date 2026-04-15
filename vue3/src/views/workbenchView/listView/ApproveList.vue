<template>
  <div class="task-table-container">
    <div class="table-container">
      <el-table
          :data="tableData"
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
    <el-table-column prop="title" label="标题" width="160">
      <template #default="scope">
        <span class="task-name">{{ scope.row.title }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="comment" label="审批内容" width="200">
      <template #default="scope">
        <span class="task-name">{{ scope.row.comment }}</span>
      </template>
    </el-table-column>
    <el-table-column prop="type" label="审批类型" width="100"></el-table-column>
    <el-table-column prop="approver" label="审批人" width="80"></el-table-column>
    <el-table-column prop="submitTime" label="提交时间" width="120"></el-table-column>
    <el-table-column label="操作" width="120">
      <template #default="scope">
        <el-button 
          :type="getStatusType(scope.row.status)" 
          :size="'small'"
          @click="handleApproval(scope.row)"
        >
          {{ scope.row.status }}
        </el-button>
      </template>
    </el-table-column>
      </el-table>

    <!-- 审批对话框 -->
    <el-dialog
      v-model="approvalDialogVisible"
      title="审批操作"
      width="400px"
    >
      <div class="dialog-content">
        <h4>{{ currentApproval.title }}</h4>
        <p class="approval-content">{{ currentApproval.comment }}</p>
        <p class="approval-info">审批类型：{{ currentApproval.type }}</p>
        <p class="approval-info">审批人：{{ currentApproval.approver }}</p>
        <p class="approval-info">提交时间：{{ currentApproval.submitTime }}</p>
      </div>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="approvalDialogVisible = false">取消</el-button>
          <el-button type="danger" @click="handleReject">拒绝</el-button>
          <el-button type="warning" @click="handlePending">待定</el-button>
          <el-button type="success" @click="handleApprove">通过</el-button>
        </span>
      </template>
    </el-dialog>
    </div>
  </div>
</template>

<script setup>
import {ref, onMounted} from "vue";
import request from "@/utils/request.js";

//ddl列表
const tableData = ref([]);

// 审批对话框
const approvalDialogVisible = ref(false);
const currentApproval = ref({});

// 从后端获取审批列表数据
onMounted(() => {
  fetchApprovals();
});

// 获取状态对应的按钮类型
const getStatusType = (status) => {
  switch (status) {
    case '已通过':
      return 'success';
    case '已退回':
      return 'danger';
    default:
      return '';
  }
};

// 处理审批操作
const handleApproval = (approval) => {
  currentApproval.value = approval;
  approvalDialogVisible.value = true;
};

// 处理通过操作
const handleApprove = async () => {
  try {
    // 检查审批ID是否存在
    if (!currentApproval.value.id) {
      console.error('审批ID不存在');
      approvalDialogVisible.value = false;
      return;
    }
    
    console.log('当前审批ID:', currentApproval.value.id);
    console.log('当前审批状态:', currentApproval.value.status);
    
    // 获取当前时间作为审批完成时间
    const currentTime = new Date().toISOString();
    
    // 调用后端API更新审批状态
    const response = await request.put(`/workbench/approvals/${currentApproval.value.id}`, {
      action: '通过',
      approvalTime: currentTime
    });
    console.log('API响应:', response);
    
    if (response.data.code === 200) {
      // 更新本地数据
      console.log('更新前的tableData:', tableData.value);
      const index = tableData.value.findIndex(item => item.id === currentApproval.value.id);
      console.log('找到的索引:', index);
      if (index !== -1) {
        console.log('更新前的状态:', tableData.value[index].status);
        tableData.value[index].status = '已通过';
        console.log('更新后的状态:', tableData.value[index].status);
        console.log('更新后的tableData:', tableData.value);
      }
      
      // 记录操作日志
      await recordOperationLog('通过', currentApproval.value.id, currentApproval.value.title);
    }
  } catch (error) {
    console.error('审批操作失败:', error);
  } finally {
    // 无论成功还是失败，都关闭弹窗
    approvalDialogVisible.value = false;
  }
};

// 处理拒绝操作
const handleReject = async () => {
  try {
    // 检查审批ID是否存在
    if (!currentApproval.value.id) {
      console.error('审批ID不存在');
      approvalDialogVisible.value = false;
      return;
    }
    
    console.log('当前审批ID:', currentApproval.value.id);
    console.log('当前审批状态:', currentApproval.value.status);
    
    // 获取当前时间作为审批完成时间
    const currentTime = new Date().toISOString();
    
    // 调用后端API更新审批状态
    const response = await request.put(`/workbench/approvals/${currentApproval.value.id}`, {
      action: '退回',
      approvalTime: currentTime
    });
    console.log('API响应:', response);
    
    if (response.data.code === 200) {
      // 更新本地数据
      console.log('更新前的tableData:', tableData.value);
      const index = tableData.value.findIndex(item => item.id === currentApproval.value.id);
      console.log('找到的索引:', index);
      if (index !== -1) {
        console.log('更新前的状态:', tableData.value[index].status);
        tableData.value[index].status = '已退回';
        console.log('更新后的状态:', tableData.value[index].status);
        console.log('更新后的tableData:', tableData.value);
      }
      
      // 记录操作日志
      await recordOperationLog('拒绝', currentApproval.value.id, currentApproval.value.title);
    }
  } catch (error) {
    console.error('审批操作失败:', error);
  } finally {
    // 无论成功还是失败，都关闭弹窗
    approvalDialogVisible.value = false;
  }
};

// 记录操作日志
const recordOperationLog = async (action, approvalId, approvalTitle) => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      // 调用后端API记录操作日志
      await request.post('/workbench/operation-logs', {
        username: user.username,
        action: `审批${action}：${approvalTitle}`,
        targetId: approvalId,
        targetType: 'approval',
        createTime: new Date().toISOString()
      });
    }
  } catch (error) {
    console.error('记录操作日志失败:', error);
  }
};

// 处理待定操作
const handlePending = async () => {
  try {
    // 检查审批ID是否存在
    if (!currentApproval.value.id) {
      console.error('审批ID不存在');
      approvalDialogVisible.value = false;
      return;
    }
    
    console.log('当前审批ID:', currentApproval.value.id);
    console.log('当前审批状态:', currentApproval.value.status);
    
    // 获取当前时间作为审批时间
    const currentTime = new Date().toISOString();
    
    // 调用后端API更新审批状态
    const response = await request.put(`/workbench/approvals/${currentApproval.value.id}`, {
      action: '待定',
      approvalTime: currentTime
    });
    console.log('API响应:', response);
    
    if (response.data.code === 200) {
      // 更新本地数据
      console.log('更新前的tableData:', tableData.value);
      const index = tableData.value.findIndex(item => item.id === currentApproval.value.id);
      console.log('找到的索引:', index);
      if (index !== -1) {
        console.log('更新前的状态:', tableData.value[index].status);
        tableData.value[index].status = '审批中';
        console.log('更新后的状态:', tableData.value[index].status);
        console.log('更新后的tableData:', tableData.value);
      }
      
      // 记录操作日志
      await recordOperationLog('待定', currentApproval.value.id, currentApproval.value.title);
    }
  } catch (error) {
    console.error('审批操作失败:', error);
  } finally {
    // 无论成功还是失败，都关闭弹窗
    approvalDialogVisible.value = false;
  }
};

const fetchApprovals = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    if (userStr) {
      const user = JSON.parse(userStr);
      const response = await request.get(`/workbench/approvals?username=${user.username}`);
      console.log('获取审批列表响应:', response);
      if (response.data.code === 200) {
        // 转换数据格式以匹配前端组件
        tableData.value = response.data.data.map(item => ({
          id: item.id,
          title: item.project_name,
          comment: item.comment || '',
          type: item.type || '',
          approver: item.approver_name,
          submitTime: item.created_at,
          status: item.action === '通过' ? '已通过' : item.action === '退回' ? '已退回' : item.action === '待定' ? '审批中' : item.action || ''
        }));
        // 按照提交时间排序，越靠近现在的排在前面
        tableData.value.sort((a, b) => new Date(b.submitTime) - new Date(a.submitTime));
        console.log('转换后的审批列表数据:', tableData.value);
      }
    }
  } catch (error) {
    console.error('获取审批列表失败:', error);
  }
};
</script>

<style scoped>
.status-rejected {
  color: #F56C6C;
  font-weight: 500;
  font-size: 13px;
}

/* 审批对话框样式 */
.dialog-content {
  padding: 10px 0;
}

.dialog-content h4 {
  margin: 0 0 16px 0;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.approval-content {
  margin: 0 0 12px 0;
  color: #606266;
  line-height: 1.5;
}

.approval-info {
  margin: 0 0 8px 0;
  color: #909399;
  font-size: 13px;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 8px;
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

.task-table-container {
  padding: 0;
  background-color: #fff;
  border-radius: 0;
  box-shadow: none;
  overflow-x: auto;
  max-height: 350px;
  overflow-y: auto;
}

.table-container {
  width: 100%;
  min-width: 600px;
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
</style>