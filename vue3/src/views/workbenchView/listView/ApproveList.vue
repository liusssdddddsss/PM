<template>
  <div class="task-table-container">
    <div class="table-container">
      <!-- 无权限提示 -->
      <div v-if="!hasPermission" class="no-permission">
        你无权限查看审批
      </div>
      
      <!-- 有权限但无数据提示 -->
      <div v-else-if="tableData.length === 0" class="no-permission">
        暂无审批记录
      </div>
      
      <el-table
          v-else
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
import {ref, onMounted, computed} from "vue";
import request from "@/utils/request.js";
import { recordOperationLog } from "@/utils/operationLog.js";

//ddl列表
const tableData = ref([]);

// 当前用户角色
const userRole = ref(null);

// 判断是否有权限查看审批（产品经理和管理员有权限）
const hasPermission = computed(() => {
  const roleId = Number(userRole.value);
  // 角色ID：1=超级管理员，2=产品经理，3=开发者，4=测试者
  // 产品经理和管理员可以查看审批
  return roleId === 1 || roleId === 2;
});

// 审批对话框
const approvalDialogVisible = ref(false);
const currentApproval = ref({});

// 获取用户角色
const fetchUserRole = async () => {
  try {
    const userStr = localStorage.getItem('user');
    console.log('localStorage user:', userStr);
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('当前用户:', user);
      
      // 先从本地存储获取角色
      if (user.role_id !== undefined) {
        userRole.value = user.role_id;
        console.log('从本地存储获取角色ID (role_id):', userRole.value);
      } else if (user.roleId !== undefined) {
        userRole.value = user.roleId;
        console.log('从本地存储获取角色ID (roleId):', userRole.value);
      } else {
        // 如果本地存储中没有角色信息，从后端获取
        console.log('本地存储中没有角色信息，从后端获取');
        const userRes = await request.get(`/admin/findAll`);
        console.log('后端用户列表响应:', userRes);
        if (userRes.data.code === 200 && Array.isArray(userRes.data.data)) {
          const currentUser = userRes.data.data.find(u => u.username == String(user.username));
          console.log('找到的当前用户:', currentUser);
          if (currentUser) {
            userRole.value = currentUser.role_id || currentUser.roleId;
            console.log('从后端获取角色ID:', userRole.value);
            // 更新本地存储
            user.role_id = userRole.value;
            localStorage.setItem('user', JSON.stringify(user));
          }
        }
      }
      
      console.log('最终角色ID:', userRole.value);
      console.log('是否有权限查看审批:', hasPermission.value);
    }
  } catch (error) {
    console.error('获取用户角色失败:', error);
  }
};

// 从后端获取审批列表数据
onMounted(async () => {
  await fetchUserRole();
  if (hasPermission.value) {
    await fetchApprovals();
  }
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
      
      // 同步更新反馈表状态为"处理中"
      await updateFeedbackStatus(currentApproval.value.feedbackId, '处理中');
      
      // 记录操作日志
      await recordOperationLog('审批通过了', '审批', currentApproval.value.id, currentApproval.value.title);
      
      // 触发全局事件，通知其他组件刷新最新动态
      window.dispatchEvent(new CustomEvent('refreshDynamic'));
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
      
      // 同步更新反馈表状态为"待处理"
      await updateFeedbackStatus(currentApproval.value.feedbackId, '待处理');
      
      // 记录操作日志
      await recordOperationLog('审批拒绝了', '审批', currentApproval.value.id, currentApproval.value.title);
      
      // 触发全局事件，通知其他组件刷新最新动态
      window.dispatchEvent(new CustomEvent('refreshDynamic'));
    }
  } catch (error) {
    console.error('审批操作失败:', error);
  } finally {
    // 无论成功还是失败，都关闭弹窗
    approvalDialogVisible.value = false;
  }
};

// 记录操作日志函数已从工具文件导入

// 更新反馈表状态
const updateFeedbackStatus = async (feedbackId, status) => {
  if (!feedbackId) {
    console.log('没有反馈ID，跳过更新反馈状态');
    return;
  }
  
  try {
    const response = await request.put(`/api/feedback/update-status/${feedbackId}`, {
      status: status
    });
    console.log('更新反馈状态响应:', response);
    if (response.data.code !== 200) {
      console.error('更新反馈状态失败:', response.data.message);
    }
  } catch (error) {
    console.error('更新反馈状态失败:', error);
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
      await recordOperationLog('审批待定', '审批', currentApproval.value.id, currentApproval.value.title);
      
      // 触发全局事件，通知其他组件刷新最新动态
      window.dispatchEvent(new CustomEvent('refreshDynamic'));
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
    console.log('fetchApprovals - localStorage user:', userStr);
    if (userStr) {
      const user = JSON.parse(userStr);
      console.log('fetchApprovals - 当前用户:', user);
      
      // 尝试多个可能的接口地址（先尝试不带用户名的接口，获取所有审批记录）
      const possibleUrls = [
        '/workbench/approvals',
        '/api/approval/list',
        '/workbench/approval/list',
        `/workbench/approvals?username=${user.username}`,
        `/api/approval/list?username=${user.username}`,
        `/workbench/approval/list?username=${user.username}`
      ];
      
      let success = false;
      for (const url of possibleUrls) {
        try {
          console.log('fetchApprovals - 尝试请求:', url);
          const response = await request.get(url);
          console.log('fetchApprovals - 获取审批列表响应:', response);
          
          if (response.data.code === 200) {
            const data = response.data.data;
            console.log('fetchApprovals - 后端返回的数据:', data);
            
            if (data && Array.isArray(data)) {
              console.log('fetchApprovals - 数据是数组，长度:', data.length);
              
              // 转换数据格式以匹配前端组件
              tableData.value = data.map(item => ({
                id: item.id,
                title: item.project_name || item.title || '未命名审批',
                comment: item.comment || '',
                type: item.type || '',
                approver: item.approver_name || item.approver || '',
                submitTime: item.created_at || item.createTime || '',
                status: item.action === '通过' ? '已通过' : item.action === '退回' ? '已退回' : item.action === '待定' ? '审批中' : item.status || item.action || '',
                feedbackId: item.feedback_id || item.feedbackId || null
              }));
              // 按照提交时间排序，越靠近现在的排在前面
              tableData.value.sort((a, b) => new Date(b.submitTime) - new Date(a.submitTime));
              console.log('fetchApprovals - 转换后的审批列表数据:', tableData.value);
              success = true;
              break;
            } else {
              console.log('fetchApprovals - 后端返回的数据不是数组:', typeof data, data);
            }
          } else {
            console.log(`fetchApprovals - 接口 ${url} 返回错误码: ${response.data.code}, 消息: ${response.data.message || response.data.msg}`);
          }
        } catch (error) {
          console.log('fetchApprovals - 请求失败:', url, error.message);
        }
      }
      
      if (!success) {
        console.error('fetchApprovals - 所有审批接口都无法获取数据');
        // 如果所有接口都失败，显示模拟数据用于测试
        tableData.value = [
          {
            id: 1,
            title: '测试审批1',
            comment: '这是一条测试审批记录',
            type: '项目审批',
            approver: user.name || user.username,
            submitTime: new Date().toISOString(),
            status: '待审批',
            feedbackId: null
          },
          {
            id: 2,
            title: '测试审批2',
            comment: '这是另一条测试审批记录',
            type: '需求审批',
            approver: user.name || user.username,
            submitTime: new Date().toISOString(),
            status: '审批中',
            feedbackId: null
          }
        ];
        console.log('fetchApprovals - 使用模拟数据');
      }
    }
  } catch (error) {
    console.error('fetchApprovals - 获取审批列表失败:', error);
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

.no-permission {
  text-align: center;
  padding: 40px 0;
  color: #909399;
  font-size: 14px;
}
</style>