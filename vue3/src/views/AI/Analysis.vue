<template>
  <div class="analysis-container">
    <div class="header-section">
      <h2 class="main-title">智能分析预警系统</h2>
      <p class="main-subtitle">基于AI技术的项目和产品风险分析平台</p>
    </div>
    
    <div class="content-section">
      <div class="left-panel">
        <el-card class="panel-card">
          <div class="title">
            <h3>分析操作</h3>
            <p class="subtitle">点击下方按钮开始分析</p>
          </div>
          <div class="action-buttons">
            <el-button type="primary" @click="analyzeAllProjects" :loading="loading.projects" class="action-btn">
              <el-icon class="btn-icon"><i-ep-data-analysis /></el-icon>
              分析项目风险
            </el-button>
            <el-button type="success" @click="analyzeAllProducts" :loading="loading.products" class="action-btn">
              <el-icon class="btn-icon"><i-ep-goods /></el-icon>
              分析产品风险
            </el-button>
            <el-button type="info" @click="analyzeAll" :loading="loading.all" class="action-btn">
              <el-icon class="btn-icon"><i-ep-aim /></el-icon>
              全量分析
            </el-button>
          </div>
          
          <div class="summary" v-if="summaryData">
            <h4>分析摘要</h4>
            <div class="statistic-group">
              <div class="statistic-item">
                <el-statistic 
                  v-if="summaryData.projects" 
                  title="项目总数" 
                  :value="summaryData.projects.totalProjects"
                  class="statistic-card"
                />
              </div>
              <div class="statistic-item">
                <el-statistic 
                  v-if="summaryData.projects" 
                  title="高风险项目" 
                  :value="summaryData.projects.highRiskProjects"
                  value-style="color: #f56c6c"
                  class="statistic-card danger"
                />
              </div>
              <div class="statistic-item">
                <el-statistic 
                  v-if="summaryData.projects" 
                  title="中风险项目" 
                  :value="summaryData.projects.mediumRiskProjects"
                  value-style="color: #e6a23c"
                  class="statistic-card warning"
                />
              </div>
              <div class="statistic-item">
                <el-statistic 
                  v-if="summaryData.projects" 
                  title="低风险项目" 
                  :value="summaryData.projects.lowRiskProjects"
                  value-style="color: #67c23a"
                  class="statistic-card success"
                />
              </div>
            </div>
          </div>
          
          <div class="analysis-info" v-if="lastAnalysisTime">
            <div class="info-item">
              <el-icon><i-ep-timer /></el-icon>
              <span>上次分析时间：{{ lastAnalysisTime }}</span>
            </div>
            <div class="info-item">
              <el-icon><i-ep-info-filled /></el-icon>
              <span>分析耗时：{{ analysisDuration }}秒</span>
            </div>
          </div>
        </el-card>
      </div>

      <div class="right-panel">
        <el-card class="panel-card">
          <div class="card-header">
            <h3>分析结果</h3>
            <el-tabs v-model="activeTab" class="result-tabs">
              <el-tab-pane label="项目风险" name="projects">
                <div class="project-risk-list" v-if="projectsData">
                  <el-collapse v-model="activeProjectNames" class="risk-collapse">
                    <el-collapse-item 
                      v-for="project in projectsData.projects" 
                      :key="project.projectId"
                      :title="`${project.projectName} (风险等级: ${project.riskLevel})`"
                      :class="`risk-level-${project.riskLevel === '高' ? 'high' : project.riskLevel === '中' ? 'medium' : 'low'}`"
                    >
                      <div class="project-risk-details">
                        <div class="risk-metrics">
                          <div class="progress-section">
                            <h5>项目进度</h5>
                            <el-progress 
                              :percentage="project.progress" 
                              :color="getProgressColor(project.progress)"
                              status="success"
                              :format="formatProgress"
                              class="progress-bar"
                            />
                          </div>
                          <div class="risk-scores">
                            <h5>风险指标</h5>
                            <div class="risk-score-item">
                              <span class="score-label">进度风险:</span>
                              <el-progress 
                                :percentage="project.progressRisk" 
                                :color="getRiskColor(project.progressRisk)"
                                :format="formatRisk"
                                class="risk-bar"
                              />
                            </div>
                            <div class="risk-score-item">
                              <span class="score-label">任务风险:</span>
                              <el-progress 
                                :percentage="project.taskRisk" 
                                :color="getRiskColor(project.taskRisk)"
                                :format="formatRisk"
                                class="risk-bar"
                              />
                            </div>
                            <div class="risk-score-item">
                              <span class="score-label">Bug风险:</span>
                              <el-progress 
                                :percentage="project.bugRisk" 
                                :color="getRiskColor(project.bugRisk)"
                                :format="formatRisk"
                                class="risk-bar"
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
                            class="warning-alert"
                          />
                        </div>
                        <div class="risk-suggestions" v-if="project.suggestions && project.suggestions.length > 0">
                          <h5>建议措施</h5>
                          <ul class="suggestion-list">
                            <li v-for="(suggestion, index) in project.suggestions" :key="index" class="suggestion-item">
                              <el-icon class="suggestion-icon"><i-ep-lightbulb /></el-icon>
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
                  <el-collapse v-model="activeProductNames" class="risk-collapse">
                    <el-collapse-item 
                      v-for="product in productsData.products" 
                      :key="product.productId"
                      :title="`${product.productName} (风险等级: ${product.riskLevel})`"
                      :class="`risk-level-${product.riskLevel === '高' ? 'high' : product.riskLevel === '中' ? 'medium' : 'low'}`"
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
                            class="warning-alert"
                          />
                        </div>
                        <div class="risk-suggestions" v-if="product.suggestions && product.suggestions.length > 0">
                          <h5>建议措施</h5>
                          <ul class="suggestion-list">
                            <li v-for="(suggestion, index) in product.suggestions" :key="index" class="suggestion-item">
                              <el-icon class="suggestion-icon"><i-ep-lightbulb /></el-icon>
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
                      <el-avatar icon="el-icon-chat-line-round" size="large" class="ai-avatar" />
                      <h4>智能分析助手</h4>
                    </div>
                    <div class="ai-content">
                      <p class="ai-intro">基于当前分析结果，我为您提供以下智能建议：</p>
                      <ul class="ai-suggestion-list" v-if="aiSuggestions.length > 0">
                        <li v-for="(suggestion, index) in aiSuggestions" :key="index" class="ai-suggestion-item">
                          <el-icon class="ai-icon"><i-ep-chat-line-round /></el-icon>
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
  </div>
</template>

<script setup>
import { ref, computed } from 'vue';
import axios from 'axios';
import { ElMessage, ElEmpty, ElCollapse, ElCollapseItem, ElProgress, ElAlert, ElStatistic, ElTabs, ElTabPane, ElButton, ElAvatar, ElIcon } from 'element-plus';
import { DataAnalysis, Goods, Aim, Clock, InfoFilled, Timer } from '@element-plus/icons-vue';

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
const lastAnalysisTime = ref(null);
const analysisDuration = ref(0);

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
  const startTime = Date.now();
  loading.value.projects = true;
  try {
    const response = await axios.get('http://localhost:8080/ai/analysis/projects');
    if (response.data.code === 200) {
      projectsData.value = response.data.data;
      summaryData.value = {
        projects: response.data.data
      };
      generateAISuggestions();
      lastAnalysisTime.value = getCurrentTimestamp();
      analysisDuration.value = ((Date.now() - startTime) / 1000).toFixed(2);
      ElMessage.success('项目风险分析完成');
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
  const startTime = Date.now();
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
      lastAnalysisTime.value = getCurrentTimestamp();
      analysisDuration.value = ((Date.now() - startTime) / 1000).toFixed(2);
      ElMessage.success('产品风险分析完成');
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
  const startTime = Date.now();
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
    lastAnalysisTime.value = getCurrentTimestamp();
    analysisDuration.value = ((Date.now() - startTime) / 1000).toFixed(2);
    ElMessage.success('全量分析完成');
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
  
  // 基于项目风险生成智能建议
  if (projectsData.value) {
    const highRiskProjects = projectsData.value.projects.filter(p => p.riskLevel === '高');
    const mediumRiskProjects = projectsData.value.projects.filter(p => p.riskLevel === '中');
    const lowRiskProjects = projectsData.value.projects.filter(p => p.riskLevel === '低');
    
    if (highRiskProjects.length > 0) {
      aiSuggestions.value.push(`🔴 发现 ${highRiskProjects.length} 个高风险项目，建议立即组织专项会议评估风险，优先调配资源解决关键问题`);
      
      // 分析高风险项目的具体问题
      const progressIssues = highRiskProjects.filter(p => p.progressRisk >= 70);
      if (progressIssues.length > 0) {
        aiSuggestions.value.push(`⏰ ${progressIssues.length} 个项目存在进度风险，建议重新评估时间计划，考虑调整里程碑或增加人力投入`);
      }
      
      const taskIssues = highRiskProjects.filter(p => p.taskRisk >= 70);
      if (taskIssues.length > 0) {
        aiSuggestions.value.push(`📋 ${taskIssues.length} 个项目存在任务风险，建议检查任务分配合理性，优先处理延期任务`);
      }
      
      const bugIssues = highRiskProjects.filter(p => p.bugRisk >= 70);
      if (bugIssues.length > 0) {
        aiSuggestions.value.push(`🐛 ${bugIssues.length} 个项目存在Bug风险，建议增加测试资源，制定Bug修复计划`);
      }
    }

    if (mediumRiskProjects.length > 0) {
      aiSuggestions.value.push(`🟠 发现 ${mediumRiskProjects.length} 个中风险项目，建议每周定期检查这些项目的进展情况，提前识别潜在问题`);
    }

    if (lowRiskProjects.length > 0) {
      aiSuggestions.value.push(`🟢 ${lowRiskProjects.length} 个项目风险较低，建议总结这些项目的成功经验，推广到其他项目中`);
    }
  }

  // 基于产品风险生成建议
  if (productsData.value) {
    const highRiskProducts = productsData.value.products.filter(p => p.riskLevel === '高');
    if (highRiskProducts.length > 0) {
      aiSuggestions.value.push(`🛡️ 发现 ${highRiskProducts.length} 个高风险产品，建议加强产品质量监控，及时收集用户反馈`);
    }
  }

  // 高级智能建议
  aiSuggestions.value.push('📊 建议建立项目风险预警机制，设置关键指标阈值，当风险超过阈值时自动提醒');
  aiSuggestions.value.push('🤖 推荐使用AI辅助项目管理，通过数据分析预测项目风险，提前制定应对策略');
  aiSuggestions.value.push('📈 建议定期生成风险分析报告，跟踪风险变化趋势，持续优化项目管理流程');
  aiSuggestions.value.push('👥 建议加强团队协作，建立风险沟通机制，确保团队成员及时了解项目风险状况');
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

.action-buttons {
  padding: 0 20px 20px;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.action-btn {
  width: 100%;
  height: 50px;
  font-size: 16px;
  border-radius: 12px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
}

.action-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.btn-icon {
  font-size: 18px;
}

.summary {
  margin-top: auto;
  padding: 20px;
  border-top: 1px solid #e4e7ed;
}

.summary h4 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.statistic-group {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.statistic-item {
  width: 100%;
}

.statistic-card {
  padding: 15px;
  border-radius: 12px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.statistic-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.statistic-card.danger {
  background: #fff1f0;
  border: 1px solid #ffccc7;
}

.statistic-card.warning {
  background: #fffbe6;
  border: 1px solid #ffe58f;
}

.statistic-card.success {
  background: #f6ffed;
  border: 1px solid #b7eb8f;
}

.analysis-info {
  padding: 20px;
  border-top: 1px solid #e4e7ed;
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: #606266;
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

.project-risk-list,
.product-risk-list {
  max-height: 600px;
  overflow-y: auto;
  padding: 0 20px 20px;
}

.risk-collapse {
  border-radius: 12px;
  overflow: hidden;
}

.risk-collapse .el-collapse-item {
  border: none;
  margin-bottom: 10px;
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.risk-collapse .el-collapse-item__header {
  padding: 15px 20px;
  font-size: 16px;
  font-weight: 500;
  border: none;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.risk-collapse .el-collapse-item__header:hover {
  background: #e9ecef;
}

.risk-level-high .el-collapse-item__header {
  border-left: 4px solid #f56c6c;
}

.risk-level-medium .el-collapse-item__header {
  border-left: 4px solid #e6a23c;
}

.risk-level-low .el-collapse-item__header {
  border-left: 4px solid #67c23a;
}

.project-risk-details,
.product-risk-details {
  padding: 20px;
  background: white;
  border-top: 1px solid #e4e7ed;
}

.risk-metrics {
  margin-bottom: 30px;
}

.progress-section {
  margin-bottom: 25px;
}

.progress-section h5,
.risk-scores h5 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.progress-bar {
  height: 12px;
  border-radius: 6px;
}

.risk-scores {
  margin-top: 20px;
}

.risk-score-item {
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 15px;
}

.score-label {
  display: inline-block;
  width: 90px;
  font-size: 14px;
  color: #606266;
  font-weight: 500;
}

.risk-bar {
  flex: 1;
  height: 10px;
  border-radius: 5px;
}

.risk-warnings {
  margin-bottom: 25px;
}

.risk-warnings h5,
.risk-suggestions h5 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 14px;
  font-weight: bold;
}

.warning-alert {
  margin-bottom: 10px;
  border-radius: 8px;
}

.suggestion-list,
.ai-suggestion-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.suggestion-item,
.ai-suggestion-item {
  padding: 12px 15px;
  display: flex;
  align-items: flex-start;
  gap: 12px;
  border-bottom: 1px solid #f0f0f0;
  border-radius: 8px;
  margin-bottom: 10px;
  background: #f8f9fa;
  transition: all 0.3s ease;
}

.suggestion-item:hover,
.ai-suggestion-item:hover {
  background: #e9ecef;
  transform: translateX(5px);
}

.suggestion-item:last-child,
.ai-suggestion-item:last-child {
  border-bottom: none;
  margin-bottom: 0;
}

.suggestion-icon,
.ai-icon {
  font-size: 18px;
  margin-top: 2px;
  flex-shrink: 0;
}

.ai-suggestions {
  padding: 20px;
  height: 600px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.ai-card {
  width: 100%;
  max-width: 800px;
  border-radius: 16px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  overflow: hidden;
  background: linear-gradient(135deg, #f5f7fa 0%, #e9ecef 100%);
}

.ai-header {
  display: flex;
  align-items: center;
  padding: 20px;
  background: white;
  border-bottom: 1px solid #e4e7ed;
}

.ai-avatar {
  margin-right: 15px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.ai-header h4 {
  margin: 0;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
}

.ai-content {
  padding: 30px;
  line-height: 1.8;
  color: #303133;
}

.ai-intro {
  margin: 0 0 25px 0;
  font-size: 16px;
  font-weight: 500;
}

.empty-state,
.empty-suggestions {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 400px;
}

/* 滚动条样式 */
.project-risk-list::-webkit-scrollbar,
.product-risk-list::-webkit-scrollbar {
  width: 8px;
}

.project-risk-list::-webkit-scrollbar-track,
.product-risk-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.project-risk-list::-webkit-scrollbar-thumb,
.product-risk-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.project-risk-list::-webkit-scrollbar-thumb:hover,
.product-risk-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
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

  .statistic-group {
    grid-template-columns: repeat(4, 1fr);
  }
}

@media (max-width: 768px) {
  .analysis-container {
    padding: 20px;
  }

  .main-title {
    font-size: 24px;
  }

  .statistic-group {
    grid-template-columns: repeat(2, 1fr);
  }

  .project-risk-list,
  .product-risk-list {
    max-height: 500px;
  }

  .ai-suggestions {
    height: 500px;
  }
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
}
</style>