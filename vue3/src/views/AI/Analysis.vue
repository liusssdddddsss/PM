<template>
  <div class="analysis-container">
    <div class="left-panel">
      <el-card>
        <div class="title">
          <h3>智能分析预警</h3>
          <p class="subtitle">自动分析项目和产品的潜在风险</p>
        </div>
        <div class="action-buttons">
          <el-button type="primary" @click="analyzeAllProjects" style="width: 100%; margin-bottom: 10px" :loading="loading.projects">
            分析所有项目风险
          </el-button>
          <el-button type="success" @click="analyzeAllProducts" style="width: 100%; margin-bottom: 10px" :loading="loading.products">
            分析所有产品风险
          </el-button>
          <el-button type="info" @click="analyzeAll" style="width: 100%" :loading="loading.all">
            全量分析
          </el-button>
        </div>
        <div class="summary" v-if="summaryData">
          <h4>分析摘要</h4>
          <div class="statistic-group">
            <el-statistic 
              v-if="summaryData.projects" 
              title="项目总数" 
              :value="summaryData.projects.totalProjects"
            />
            <el-statistic 
              v-if="summaryData.projects" 
              title="高风险项目" 
              :value="summaryData.projects.highRiskProjects"
              value-style="color: #f56c6c"
            />
            <el-statistic 
              v-if="summaryData.projects" 
              title="中风险项目" 
              :value="summaryData.projects.mediumRiskProjects"
              value-style="color: #e6a23c"
            />
            <el-statistic 
              v-if="summaryData.projects" 
              title="低风险项目" 
              :value="summaryData.projects.lowRiskProjects"
              value-style="color: #67c23a"
            />
          </div>
        </div>
      </el-card>
    </div>

    <div class="right-panel">
      <el-card>
        <div class="card-header">
          <h3>分析结果</h3>
          <el-tabs v-model="activeTab">
            <el-tab-pane label="项目风险" name="projects">
              <div class="project-risk-list" v-if="projectsData">
                <el-collapse v-model="activeProjectNames">
                  <el-collapse-item 
                    v-for="project in projectsData.projects" 
                    :key="project.projectId"
                    :title="`${project.projectName} (风险等级: ${project.riskLevel})`"
                  >
                    <div class="project-risk-details">
                      <div class="risk-metrics">
                        <el-progress 
                          :percentage="project.progress" 
                          :color="getProgressColor(project.progress)"
                          status="success"
                          :format="formatProgress"
                        />
                        <div class="risk-scores">
                          <div class="risk-score-item">
                            <span class="score-label">进度风险:</span>
                            <el-progress 
                              :percentage="project.progressRisk" 
                              :color="getRiskColor(project.progressRisk)"
                              :format="formatRisk"
                            />
                          </div>
                          <div class="risk-score-item">
                            <span class="score-label">任务风险:</span>
                            <el-progress 
                              :percentage="project.taskRisk" 
                              :color="getRiskColor(project.taskRisk)"
                              :format="formatRisk"
                            />
                          </div>
                          <div class="risk-score-item">
                            <span class="score-label">Bug风险:</span>
                            <el-progress 
                              :percentage="project.bugRisk" 
                              :color="getRiskColor(project.bugRisk)"
                              :format="formatRisk"
                            />
                          </div>
                        </div>
                      </div>
                      <div class="risk-warnings" v-if="project.warnings && project.warnings.length > 0">
                        <h5>风险预警</h5>
                        <el-alert 
                          v-for="(warning, index) in project.warnings" 
                          :key="index"
                          :title="warning"
                          :type="getWarningType(project.riskLevel)"
                          show-icon
                        />
                      </div>
                      <div class="risk-suggestions" v-if="project.suggestions && project.suggestions.length > 0">
                        <h5>建议措施</h5>
                        <ul class="suggestion-list">
                          <li v-for="(suggestion, index) in project.suggestions" :key="index">
                            <span class="suggestion-icon">💡</span>
                            <span>{{ suggestion }}</span>
                          </li>
                        </ul>
                      </div>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
              <div v-else class="empty-state">
                <el-empty description="暂无项目风险分析数据" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="产品风险" name="products">
              <div class="product-risk-list" v-if="productsData">
                <el-collapse v-model="activeProductNames">
                  <el-collapse-item 
                    v-for="product in productsData.products" 
                    :key="product.productId"
                    :title="`${product.productName} (风险等级: ${product.riskLevel})`"
                  >
                    <div class="product-risk-details">
                      <div class="risk-warnings" v-if="product.warnings && product.warnings.length > 0">
                        <h5>风险预警</h5>
                        <el-alert 
                          v-for="(warning, index) in product.warnings" 
                          :key="index"
                          :title="warning"
                          :type="getWarningType(product.riskLevel)"
                          show-icon
                        />
                      </div>
                      <div class="risk-suggestions" v-if="product.suggestions && product.suggestions.length > 0">
                        <h5>建议措施</h5>
                        <ul class="suggestion-list">
                          <li v-for="(suggestion, index) in product.suggestions" :key="index">
                            <span class="suggestion-icon">💡</span>
                            <span>{{ suggestion }}</span>
                          </li>
                        </ul>
                      </div>
                    </div>
                  </el-collapse-item>
                </el-collapse>
              </div>
              <div v-else class="empty-state">
                <el-empty description="暂无产品风险分析数据" />
              </div>
            </el-tab-pane>
            <el-tab-pane label="智能建议" name="ai">
              <div class="ai-suggestions">
                <el-card class="ai-card">
                  <div class="ai-header">
                    <el-avatar icon="el-icon-chat-line-round" size="large" />
                    <h4>智能分析助手</h4>
                  </div>
                  <div class="ai-content">
                    <p>基于当前分析结果，我为您提供以下建议：</p>
                    <ul class="ai-suggestion-list" v-if="aiSuggestions.length > 0">
                      <li v-for="(suggestion, index) in aiSuggestions" :key="index">
                        <span class="ai-icon">🤖</span>
                        <span>{{ suggestion }}</span>
                      </li>
                    </ul>
                    <div v-else class="empty-suggestions">
                      <el-empty description="分析完成后将显示智能建议" />
                    </div>
                  </div>
                </el-card>
              </div>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElEmpty, ElCollapse, ElCollapseItem, ElProgress, ElAlert, ElStatistic, ElTabs, ElTabPane, ElButton, ElAvatar } from 'element-plus';

// 状态变量
const loading = ref({
  projects: false,
  products: false,
  all: false
});
const projectsData = ref(null);
const productsData = ref(null);
const summaryData = ref(null);
const activeTab = ref('projects');
const activeProjectNames = ref([]);
const activeProductNames = ref([]);
const aiSuggestions = ref([]);

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

// 分析所有项目风险
const analyzeAllProjects = async () => {
  loading.value.projects = true;
  try {
    const response = await axios.get('http://localhost:8080/ai/analysis/projects');
    if (response.data.code === 200) {
      projectsData.value = response.data.data;
      summaryData.value = {
        projects: response.data.data
      };
      generateAISuggestions();
    } else {
      ElMessage.error('分析项目风险失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('分析项目风险失败:', error);
    ElMessage.error('分析项目风险失败: ' + (error.response ? error.response.data.msg : error.message));
  } finally {
    loading.value.projects = false;
  }
};

// 分析所有产品风险
const analyzeAllProducts = async () => {
  loading.value.products = true;
  try {
    const response = await axios.get('http://localhost:8080/ai/analysis/products');
    if (response.data.code === 200) {
      productsData.value = response.data.data;
      summaryData.value = {
        ...summaryData.value,
        products: response.data.data
      };
      generateAISuggestions();
    } else {
      ElMessage.error('分析产品风险失败: ' + response.data.msg);
    }
  } catch (error) {
    console.error('分析产品风险失败:', error);
    ElMessage.error('分析产品风险失败: ' + (error.response ? error.response.data.msg : error.message));
  } finally {
    loading.value.products = false;
  }
};

// 全量分析
const analyzeAll = async () => {
  loading.value.all = true;
  try {
    // 并行请求
    const [projectsResponse, productsResponse] = await Promise.all([
      axios.get('http://localhost:8080/ai/analysis/projects'),
      axios.get('http://localhost:8080/ai/analysis/products')
    ]);

    if (projectsResponse.data.code === 200) {
      projectsData.value = projectsResponse.data.data;
    } else {
      ElMessage.error('分析项目风险失败: ' + projectsResponse.data.msg);
    }

    if (productsResponse.data.code === 200) {
      productsData.value = productsResponse.data.data;
    } else {
      ElMessage.error('分析产品风险失败: ' + productsResponse.data.msg);
    }

    summaryData.value = {
      projects: projectsResponse.data.data,
      products: productsResponse.data.data
    };

    generateAISuggestions();
  } catch (error) {
    console.error('全量分析失败:', error);
    ElMessage.error('全量分析失败: ' + (error.response ? error.response.data.msg : error.message));
  } finally {
    loading.value.all = false;
  }
};

// 生成AI建议
const generateAISuggestions = () => {
  aiSuggestions.value = [];
  
  // 基于项目风险生成建议
  if (projectsData.value) {
    const highRiskProjects = projectsData.value.projects.filter(p => p.riskLevel === '高');
    if (highRiskProjects.length > 0) {
      aiSuggestions.value.push(`发现 ${highRiskProjects.length} 个高风险项目，建议优先关注这些项目的进度和资源分配`);
    }

    const mediumRiskProjects = projectsData.value.projects.filter(p => p.riskLevel === '中');
    if (mediumRiskProjects.length > 0) {
      aiSuggestions.value.push(`发现 ${mediumRiskProjects.length} 个中风险项目，建议定期检查这些项目的进展情况`);
    }
  }

  // 通用建议
  aiSuggestions.value.push('建议每周进行一次风险分析，及时发现和解决潜在问题');
  aiSuggestions.value.push('对于高风险项目，建议增加资源投入或调整项目计划');
  aiSuggestions.value.push('定期检查任务完成情况，避免任务延期导致项目风险增加');
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
</script>

<style scoped>
.analysis-container {
  display: flex;
  min-height: 100vh;
  gap: 20px;
  padding: 20px;
}

.left-panel {
  width: 350px;
  flex-shrink: 0;
}

.left-panel .el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.title {
  margin-bottom: 30px;
}

.title h3 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
}

.title .subtitle {
  margin: 0;
  color: #909399;
  font-size: 14px;
}

.action-buttons {
  margin-bottom: 30px;
}

.summary {
  margin-top: auto;
  padding-top: 20px;
  border-top: 1px solid #e4e7ed;
}

.summary h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.statistic-group {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  justify-content: space-between;
}

.statistic-group .el-statistic {
  flex: 1;
  min-width: 80px;
}

.right-panel {
  flex: 1;
}

.right-panel .el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.card-header {
  margin-bottom: 20px;
}

.card-header h3 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.project-risk-list,
.product-risk-list {
  max-height: 700px;
  overflow-y: auto;
}

.project-risk-details,
.product-risk-details {
  padding: 10px;
}

.risk-metrics {
  margin-bottom: 20px;
}

.risk-scores {
  margin-top: 20px;
}

.risk-score-item {
  margin-bottom: 10px;
}

.score-label {
  display: inline-block;
  width: 80px;
  font-size: 14px;
  color: #606266;
}

.risk-warnings {
  margin-bottom: 20px;
}

.risk-warnings h5,
.risk-suggestions h5 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.suggestion-icon,
.ai-icon {
  margin-right: 10px;
  font-size: 16px;
}

.suggestion-list,
.ai-suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestion-list li,
.ai-suggestion-list li {
  padding: 8px 0;
  display: flex;
  align-items: center;
  border-bottom: 1px solid #f0f0f0;
}

.suggestion-list li:last-child,
.ai-suggestion-list li:last-child {
  border-bottom: none;
}

.ai-suggestions {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-card {
  width: 100%;
  max-width: 600px;
}

.ai-header {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.ai-header .el-avatar {
  margin-right: 10px;
}

.ai-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.ai-content {
  line-height: 1.6;
  color: #303133;
}

.ai-content p {
  margin: 0 0 15px 0;
}

.empty-state,
.empty-suggestions {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 300px;
}

@media (max-width: 768px) {
  .analysis-container {
    flex-direction: column;
  }

  .left-panel {
    width: 100%;
  }

  .project-risk-list,
  .product-risk-list {
    max-height: 500px;
  }
}
</style>