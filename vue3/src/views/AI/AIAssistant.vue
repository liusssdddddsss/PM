<template>
  <div class="ai-assistant">
    <div class="ai-header">
      <h2>AI智能助手</h2>
    </div>
    <div class="ai-tabs">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="功能" name="discover">
          <div class="ai-grid">
            <div class="ai-card" @click="navigateTo('/AI/marketAI')">
              <div class="ai-card-icon">
                <i class="el-icon-data-analysis"></i>
              </div>
              <h3>市场分析</h3>
              <p>基于市场数据和行业趋势，为您提供产品市场分析报告，帮助您做出更明智的决策。</p>
              <div class="ai-card-footer">
                <span class="ai-card-tag">工作</span>
                <el-button type="text" size="small">使用</el-button>
              </div>
            </div>
            <div class="ai-card" @click="navigateTo('/AI/analysis')">
              <div class="ai-card-icon">
                <i class="el-icon-warning"></i>
              </div>
              <h3>智能分析预警</h3>
              <p>通过机器学习算法，分析项目风险和潜在问题，提前预警可能的风险点。</p>
              <div class="ai-card-footer">
                <span class="ai-card-tag">工作</span>
                <el-button type="text" size="small">使用</el-button>
              </div>
            </div>
            <div class="ai-card" @click="navigateTo('/AI/report')">
              <div class="ai-card-icon">
                <i class="el-icon-document"></i>
              </div>
              <h3>工作汇报</h3>
              <p>自动汇总您的工作内容和项目进展，生成结构化的工作汇报文档。</p>
              <div class="ai-card-footer">
                <span class="ai-card-tag">工作</span>
                <el-button type="text" size="small">使用</el-button>
              </div>
            </div>
          </div>
        </el-tab-pane>

      </el-tabs>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const activeTab = ref('discover');

const navigateTo = (path) => {
  router.push(path);
};
</script>

<style scoped>
.ai-assistant {
  padding: 10px;
  min-height: 100vh;
  background-color: #fff;
}

.ai-header {
  margin-bottom: 10px;
}

.ai-header h2 {
  margin: 0;
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.ai-tabs {
  padding: 15px;
}

.ai-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  margin-top: 20px;
}

.ai-card {
  background-color: #ffffff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 20px;
  transition: all 0.3s ease;
  cursor: pointer;
}

.ai-card:hover {
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
  border-color: #409EFF;
}

.ai-card-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #ecf5ff;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 15px;
  font-size: 24px;
  color: #409EFF;
}

.ai-card h3 {
  margin: 0 0 10px 0;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

.ai-card p {
  margin: 0 0 15px 0;
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  height: 48px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.ai-card-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.ai-card-tag {
  font-size: 12px;
  color: #909399;
  background-color: #f5f7fa;
  padding: 2px 8px;
  border-radius: 4px;
}

.ai-empty {
  padding: 40px 0;
  text-align: center;
}
</style>