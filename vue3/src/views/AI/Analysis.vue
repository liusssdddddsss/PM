<template>
  <div class="analysis-container">
    <div class="header-section">
      <h2 class="main-title">智能分析预警系统</h2>
      <p class="main-subtitle">基于AI技术的项目和产品风险分析平台</p>
    </div>
    
    <div class="content-section">
      <div class="left-panel">
        <!-- AI分析引擎控制 -->
        <el-card class="panel-card">
          <div class="title">
            <h3>AI分析引擎</h3>
            <p class="subtitle">定时扫描与手动触发</p>
          </div>
          <div class="engine-status">
            <div class="status-item">
              <el-icon :class="engineStatus.isScanning ? 'status-icon running' : 'status-icon idle'">
                <Loading v-if="engineStatus.isScanning" />
                <Check v-else />
              </el-icon>
              <span>状态：{{ engineStatus.isScanning ? '运行中' : '空闲' }}</span>
            </div>
            <div class="status-item" v-if="engineStatus.lastScanTime">
              <el-icon><Timer /></el-icon>
              <span>上次扫描：{{ formatTime(engineStatus.lastScanTime) }}</span>
            </div>
            <div class="status-item">
              <el-icon><DataAnalysis /></el-icon>
              <span>扫描项目：{{ engineStatus.totalProjectsScanned || 0 }}个</span>
            </div>
            <div class="status-item">
              <el-icon><Goods /></el-icon>
              <span>扫描产品：{{ engineStatus.totalProductsScanned || 0 }}个</span>
            </div>
          </div>
          <div class="engine-actions">
            <el-button type="warning" @click="executeEngineScan" :loading="loading.engine" class="action-btn">
              <el-icon class="btn-icon"><Refresh /></el-icon>
              立即扫描
            </el-button>
            <el-button type="info" @click="getEngineStatus" class="action-btn">
              <el-icon class="btn-icon"><View /></el-icon>
              刷新状态
            </el-button>
          </div>
        </el-card>
      </div>

      <div class="right-panel">
        <el-card class="panel-card">
          <div class="card-header">
            <h3>分析结果</h3>
            <el-tabs v-model="activeTab" class="result-tabs">
              <el-tab-pane label="AI警告" name="warnings">
                <div class="warnings-container">
                  <div class="warnings-header">
                    <h4>AI警告列表</h4>
                    <el-button type="primary" @click="refreshWarnings" :loading="loading.warnings" class="refresh-btn">
                      <el-icon><Refresh /></el-icon>
                      刷新
                    </el-button>
                  </div>
                  <div class="warnings-list" v-if="warningsData.length > 0">
                    <el-collapse v-model="activeWarningIds" class="warnings-collapse">
                      <el-collapse-item 
                        v-for="warning in warningsData" 
                        :key="warning.id"
                        :title="`项目风险预警: ${getProjectName(warning.projectId)} (${getStatusText(warning.isRead)})`"
                        :class="`warning-level-${warning.riskLevel}`"
                      >
                        <div class="warning-details">
                          <div class="warning-meta">
                            <div class="meta-item">
                              <el-icon><Timer /></el-icon>
                              <span>创建时间: {{ formatTime(warning.createdAt) }}</span>
                            </div>
                            <div class="meta-item">
                              <el-icon><InfoFilled /></el-icon>
                              <span>类型: {{ getTypeText(warning.riskType) }}</span>
                            </div>
                            <div class="meta-item">
                              <el-icon><InfoFilled /></el-icon>
                              <span>风险等级: {{ getRiskLevelText(warning.riskLevel) }}</span>
                            </div>
                          </div>
                          <div class="warning-content">
                            <pre>{{ warning.riskDescription }}</pre>
                          </div>
                          <div class="warning-content">
                            <h5>建议措施:</h5>
                            <pre>{{ warning.suggestion }}</pre>
                          </div>
                          <div class="warning-actions">
                            <el-button size="small" @click="markAsRead(warning.id)" v-if="warning.isRead === 0">
                              标记为已读
                            </el-button>
                            <el-button size="small" type="danger" @click="deleteWarning(warning.id)">
                              删除
                            </el-button>
                          </div>
                        </div>
                      </el-collapse-item>
                    </el-collapse>
                  </div>
                  <div v-else class="empty-state">
                    <el-empty description="暂无AI警告数据" />
                  </div>
                </div>
              </el-tab-pane>
            </el-tabs>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue';
import axios from 'axios';
import { ElMessage, ElEmpty, ElCollapse, ElCollapseItem, ElTabs, ElTabPane, ElButton, ElIcon } from 'element-plus';
import { DataAnalysis, Goods, InfoFilled, Timer, Refresh, View, Loading, Check } from '@element-plus/icons-vue';

// 状态变量
const loading = ref({
  engine: false,
  warnings: false,
  projects: false
});
const activeWarningIds = ref([]);
const warningsData = ref([]);
const projectsData = ref([]);
// 组件挂载状态
const isMounted = ref(false);
const activeTab = ref('warnings');
const engineStatus = ref({
  isScanning: false,
  lastScanTime: null,
  totalProjectsScanned: 0,
  highRiskProjects: 0,
  mediumRiskProjects: 0,
  lowRiskProjects: 0,
  totalProductsScanned: 0,
  highRiskProducts: 0,
  mediumRiskProducts: 0,
  lowRiskProducts: 0
});

// 页面加载时获取引擎状态和AI警告
onMounted(() => {
  isMounted.value = true;
  getEngineStatus();
  refreshWarnings();
  getProjectList();
});

// 组件卸载时设置挂载状态为 false
onUnmounted(() => {
  isMounted.value = false;
});

// 获取当前时间戳
const getCurrentTimestamp = () => {
  const now = new Date();
  return now.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};

// 格式化时间
const formatTime = (timestamp) => {
  if (!timestamp) return '';
  const date = new Date(timestamp);
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
    second: '2-digit'
  });
};



// 获取AI分析引擎状态
const getEngineStatus = async () => {
  if (!isMounted.value) return;
  try {
    const response = await axios.get('/ai/engine/status');
    if (!isMounted.value) return;
    if (response.data.code === 200) {
      engineStatus.value = response.data.data;
    } else {
      ElMessage.error('获取引擎状态失败: ' + response.data.msg);
    }
  } catch (error) {
    if (!isMounted.value) return;
    console.error('获取引擎状态失败:', error);
    ElMessage.error('获取引擎状态失败: ' + (error.response ? error.response.data.msg : error.message));
  }
};

// 执行AI分析引擎扫描
const executeEngineScan = async () => {
  if (!isMounted.value) return;
  loading.value.engine = true;
  try {
    const response = await axios.post('/ai/engine/scan');
    if (!isMounted.value) return;
    if (response.data.code === 200) {
      ElMessage.success('扫描任务已启动');
      // 延迟获取状态，让扫描任务有时间开始
      setTimeout(() => {
        if (isMounted.value) {
          getEngineStatus();
        }
      }, 1000);
    } else {
      ElMessage.error('启动扫描任务失败: ' + response.data.msg);
    }
  } catch (error) {
    if (!isMounted.value) return;
    console.error('启动扫描任务失败:', error);
    ElMessage.error('启动扫描任务失败: ' + (error.response ? error.response.data.msg : error.message));
  } finally {
    if (isMounted.value) {
      loading.value.engine = false;
    }
  }
};

// 获取进度条颜色
const getProgressColor = (progress) => {
  if (progress >= 90) {
    return '#67c23a'; // 绿色
  } else if (progress >= 60) {
    return '#e6a23c'; // 橙色
  } else {
    return '#f56c6c'; // 红色
  }
};

// 获取风险条颜色
const getRiskColor = (risk) => {
  if (risk >= 70) {
    return '#f56c6c'; // 红色
  } else if (risk >= 40) {
    return '#e6a23c'; // 橙色
  } else {
    return '#67c23a'; // 绿色
  }
};

// 获取警告类型
const getWarningType = (riskLevel) => {
  if (riskLevel === '高') {
    return 'error';
  } else if (riskLevel === '中') {
    return 'warning';
  } else {
    return 'success';
  }
};

// 格式化进度显示
const formatProgress = (percentage) => {
  return `${percentage}%`;
};

// 格式化风险显示
const formatRisk = (percentage) => {
  if (percentage >= 70) {
    return '高';
  } else if (percentage >= 40) {
    return '中';
  } else {
    return '低';
  }
};

// 刷新AI警告列表
const refreshWarnings = async () => {
  if (!isMounted.value) return;
  loading.value.warnings = true;
  try {
    const response = await axios.get('/ai/warnings/list');
    if (!isMounted.value) return;
    if (response.data.code === 200) {
      warningsData.value = response.data.data;
    } else {
      ElMessage.error('获取AI警告失败: ' + response.data.msg);
    }
  } catch (error) {
    if (!isMounted.value) return;
    console.error('获取AI警告失败:', error);
    const errorMessage = error.response 
      ? `状态码: ${error.response.status}, 消息: ${error.response.data?.msg || '未找到资源'}`
      : error.message || '网络错误';
    ElMessage.error(`获取AI警告失败: ${errorMessage}`);
    warningsData.value = [];
  } finally {
    if (!isMounted.value) return;
    loading.value.warnings = false;
  }
};

// 标记警告为已读
const markAsRead = async (id) => {
  if (!isMounted.value) return;
  try {
    const response = await axios.post(`/ai/warnings/read?id=${id}`);
    if (!isMounted.value) return;
    if (response.data.code === 200) {
      ElMessage.success('已标记为已读');
      refreshWarnings();
    } else {
      ElMessage.error('操作失败: ' + response.data.msg);
    }
  } catch (error) {
    if (!isMounted.value) return;
    console.error('标记为已读失败:', error);
    ElMessage.error('操作失败: ' + (error.response ? error.response.data.msg : error.message));
  }
};

// 删除警告
const deleteWarning = async (id) => {
  if (!isMounted.value) return;
  try {
    const response = await axios.post(`/ai/warnings/delete?id=${id}`);
    if (!isMounted.value) return;
    if (response.data.code === 200) {
      ElMessage.success('已删除警告');
      refreshWarnings();
    } else {
      ElMessage.error('删除失败: ' + response.data.msg);
    }
  } catch (error) {
    if (!isMounted.value) return;
    console.error('删除警告失败:', error);
    ElMessage.error('删除失败: ' + (error.response ? error.response.data.msg : error.message));
  }
};

// 获取状态文本
const getStatusText = (isRead) => {
  switch (isRead) {
    case 0:
      return '未读';
    case 1:
      return '已读';
    default:
      return '未知';
  }
};

// 获取类型文本
const getTypeText = (type) => {
  switch (type) {
    case 'project_risk':
      return '项目风险';
    case 'product_risk':
      return '产品风险';
    default:
      return type;
  }
};

// 获取风险等级文本
const getRiskLevelText = (level) => {
  switch (level) {
    case 'high':
      return '高';
    case 'medium':
      return '中';
    case 'low':
      return '低';
    default:
      return level;
  }
};

// 获取项目列表
const getProjectList = async () => {
  if (!isMounted.value) return;
  loading.value.projects = true;
  try {
    const response = await axios.get('/api/project/list');
    if (!isMounted.value) return;
    if (response.data.code === 200) {
      projectsData.value = response.data.data;
    } else {
      ElMessage.error('获取项目列表失败: ' + response.data.msg);
    }
  } catch (error) {
    if (!isMounted.value) return;
    console.error('获取项目列表失败:', error);
    ElMessage.error('获取项目列表失败: ' + (error.response ? error.response.data.msg : error.message));
  } finally {
    if (!isMounted.value) return;
    loading.value.projects = false;
  }
};

// 根据项目ID获取项目名称
const getProjectName = (projectId) => {
  if (!projectId) return '未知项目';
  const project = projectsData.value.find(p => p.id === projectId);
  return project ? project.name : `项目ID ${projectId}`;
};
</script>

<style scoped>
.analysis-container {
  min-height: 100vh;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 30px;
}

.header-section {
  text-align: center;
  margin-bottom: 40px;
}

.main-title {
  font-size: 28px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 10px;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.1);
}

.main-subtitle {
  font-size: 16px;
  color: #606266;
  margin: 0;
}

.content-section {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.left-panel {
  width: 380px;
  flex-shrink: 0;
}

.right-panel {
  flex: 1;
}

.panel-card {
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  transition: all 0.3s ease;
}

.panel-card:hover {
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.left-panel .panel-card {
  height: 100%;
  display: flex;
  flex-direction: column;
  background: white;
}

.right-panel .panel-card {
  min-height: 700px;
  background: white;
}

.title {
  margin-bottom: 30px;
  padding: 0 20px;
}

.title h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 20px;
  font-weight: bold;
}

.title .subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.card-header {
  padding: 20px 20px 0;
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 20px;
  font-weight: bold;
}

.result-tabs {
  width: 100%;
}

.empty-state {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

@media (max-width: 1024px) {
  .content-section {
    flex-direction: column;
  }

  .left-panel {
    width: 100%;
  }

  .right-panel {
    width: 100%;
  }
}

@media (max-width: 768px) {
  .analysis-container {
    padding: 20px;
  }

  .main-title {
    font-size: 24px;
  }
}

/* AI分析引擎样式 */
.engine-status {
  margin: 20px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 12px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
  font-size: 14px;
  color: #606266;
}

.status-item:last-child {
  margin-bottom: 0;
}

.status-icon {
  font-size: 18px;
}

.status-icon.running {
  color: #409EFF;
  animation: spin 2s linear infinite;
}

.status-icon.idle {
  color: #67c23a;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.engine-actions {
  margin-top: 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

@media (max-width: 480px) {
  .analysis-container {
    padding: 15px;
  }

  .main-title {
    font-size: 20px;
  }

  .action-btn {
    height: 45px;
    font-size: 14px;
  }

  .statistic-group {
    grid-template-columns: 1fr;
  }

  .engine-status {
    padding: 10px;
  }

  .status-item {
    font-size: 13px;
  }
}

/* AI警告样式 */
.warnings-container {
  padding: 20px;
  max-height: 600px;
  overflow-y: auto;
}

.warnings-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.warnings-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.refresh-btn {
  border-radius: 8px;
  transition: all 0.3s ease;
}

.refresh-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.warnings-list {
  width: 100%;
}

.warnings-collapse {
  border-radius: 12px;
  overflow: hidden;
}

.warnings-collapse .el-collapse-item {
  border: none;
  margin-bottom: 10px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.warnings-collapse .el-collapse-item__header {
  padding: 15px 20px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.warnings-collapse .el-collapse-item__header:hover {
  background: #e9ecef;
}

.warning-level-high .el-collapse-item__header {
  border-left: 4px solid #f56c6c;
}

.warning-level-medium .el-collapse-item__header {
  border-left: 4px solid #e6a23c;
}

.warning-level-low .el-collapse-item__header {
  border-left: 4px solid #67c23a;
}

.warning-details {
  padding: 20px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.warning-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
}

.warning-content {
  margin-bottom: 20px;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409EFF;
}

.warning-content pre {
  margin: 0;
  font-family: 'Courier New', Courier, monospace;
  white-space: pre-wrap;
  word-wrap: break-word;
  font-size: 14px;
  line-height: 1.5;
  color: #303133;
}

.warning-actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
}

/* 滚动条样式 */
.warnings-container::-webkit-scrollbar {
  width: 8px;
}

.warnings-container::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.warnings-container::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.warnings-container::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

@media (max-width: 768px) {
  .warnings-container {
    max-height: 500px;
  }

  .warning-meta {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }

  .warning-actions {
    flex-direction: column;
    align-items: stretch;
  }
}
</style>