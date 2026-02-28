<template>
  <div class="market-analysis-container">
    <div class="left-panel">
      <div class="panel-header">
        <div class="header-top">
            <div class="title">
              <h2>市场分析报告</h2>
            </div>
        </div>

      </div>

      <div class="form-container">
        <el-form :model="formData" label-width="80px">
          <el-form-item label="目标市场">
            <el-input
                v-model="formData.targetMarket"
                placeholder="请概述目标市场的定义和范围"
                type="textarea"
                :rows="2"
            />
          </el-form-item>

          <el-form-item label="市场概况">
            <el-input
                v-model="formData.marketOverview"
                placeholder="可描述市场规模、增长率、发展趋势等信息"
                type="textarea"
                :rows="2"
            />
          </el-form-item>

          <el-form-item label="细分市场">
            <el-input
                v-model="formData.segmentedMarket"
                placeholder="请描述市场的主要细分领域"
                type="textarea"
                :rows="2"
            />
          </el-form-item>

          <el-form-item label="竞品名称">
            <el-input
                v-model="formData.competitors"
                placeholder="请描述列举竞品名称"
                type="textarea"
                :rows="2"
            />
          </el-form-item>

          <el-form-item label="竞品分析维度">
            <el-select v-model="formData.competitorDimensions" placeholder="请选择">
              <el-option label="功能特性" value="features" />
              <el-option label="价格策略" value="pricing" />
              <el-option label="市场份额" value="marketShare" />
              <el-option label="用户体验" value="ux" />
              <el-option label="技术优势" value="technology" />
              <el-option label="营销策略" value="marketing" />
            </el-select>
          </el-form-item>
        </el-form>

        <div class="generate-button">
          <el-button type="primary" @click="generateReport" style="width: 100%">生成</el-button>
        </div>
      </div>
    </div>

    <div class="right-panel">
      <div class="right-content">
        <el-alert
            :title="'尚未配置语言模型'"
            type="warning"
            show-icon
        >
<!--          <template #default>-->
<!--            <p>若已完成相关配置，请尝试重新加载页面。</p>-->
<!--          </template>-->
        </el-alert>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();

// 语言模型
const languageModel = ref('default');
// 收藏状态
const isFavorite = ref(false);
// 表单数据
const formData = ref({
  targetMarket: '',
  marketOverview: '',
  segmentedMarket: '',
  competitors: '',
  competitorDimensions: ''
});

// 切换收藏状态
const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value;
  console.log('收藏状态:', isFavorite.value);
};

// 清空表单
const clearForm = () => {
  formData.value = {
    targetMarket: '',
    marketOverview: '',
    segmentedMarket: '',
    competitors: '',
    competitorDimensions: ''
  };
  console.log('表单已清空');
};

// 生成报告
const generateReport = () => {
  console.log('生成报告:', formData.value);
  // 这里可以添加生成报告的逻辑
};
</script>

<style scoped>
.market-analysis-container {
  display: flex;
  min-height: 100vh;
  background-color: #fff;
}

.left-panel {
  width: 400px;
  padding: 20px;
  overflow-y: auto;
}

.panel-header {
  margin-bottom: 30px;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
}

.logo-title {
  display: flex;
  gap: 15px;
  align-items: flex-start;
}

.title h2 {
  margin: 0 0 5px 0;
  font-size: 18px;
  font-weight: bold;
  color: #303133;
}

.subtitle {
  font-size: 12px;
  color: #909399;
  line-height: 1.4;
}

.actions {
  margin-top: 5px;
}

.language-model {
  display: flex;
  align-items: center;
  padding: 10px;
  background-color: #f5f7fa;
  border-radius: 4px;
  font-size: 14px;
}

.form-container {
  margin-top: 20px;
}

.el-form-item {
  margin-bottom: 20px;
}

.generate-button {
  margin-top: 30px;
}

.right-panel {
  flex: 1;
  padding: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background-color: lightgray;
}

.right-content {
  width: 100%;
  max-width: 600px;
}

@media (max-width: 768px) {
  .market-analysis-container {
    flex-direction: column;
  }

  .left-panel {
    width: 100%;
    box-shadow: none;
    border-bottom: 1px solid #e4e7ed;
  }

  .right-panel {
    padding: 20px;
  }
}
</style>