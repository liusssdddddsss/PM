<template>
  <div class="market-analysis-container">
    <div class="left-panel">
      <el-card>
        <div class="title">
          <h3>市场分析报告</h3>
        </div>
        <div class="form-container">
          <el-form :model="formData" label-width="80px">
            <el-form-item label="目标市场">
              <el-input
                  v-model="formData.targetMarket"
                  placeholder="请输入内容"
                  type="textarea"
                  :rows="2"
              />
            </el-form-item>

            <el-form-item label="市场概况">
              <el-input
                  v-model="formData.marketOverview"
                  placeholder="请输入内容"
                  type="textarea"
                  :rows="2"
              />
            </el-form-item>

            <el-form-item label="细分市场">
              <el-input
                  v-model="formData.segmentedMarket"
                  placeholder="请输入内容"
                  type="textarea"
                  :rows="2"
              />
            </el-form-item>

            <el-form-item label="竞品名称">
              <el-input
                  v-model="formData.competitors"
                  placeholder="请输入内容"
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
            <el-button type="primary" @click="generateReport" style="width: 100%" :loading="loading">生成</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div class="right-panel">
      <el-card>
        <div class="chat-header">
          <h3>市场分析助手</h3>
        </div>
        <div class="chat-messages">
          <el-timeline>
            <el-timeline-item
                v-for="(message, index) in chatMessages"
                :key="index"
                :timestamp="message.timestamp"
                :type="message.role === 'user' ? 'primary' : 'info'"
                :icon="message.role === 'user' ? 'el-icon-user' : 'el-icon-chat-line-round'"
            >
              <el-card :class="{ 'user-message': message.role === 'user', 'ai-message': message.role === 'assistant' }">
                <div class="message-header">
                  <span class="message-role">{{ message.role === 'user' ? '您' : '市场分析助手' }}</span>
                  <span class="message-time">{{ message.timestamp }}</span>
                </div>
                <div class="message-content" v-html="message.content"></div>
              </el-card>
            </el-timeline-item>
            <el-timeline-item v-if="isGenerating" type="info" icon="el-icon-loading">
              <el-card class="ai-message">
                <div class="message-header">
                  <span class="message-role">市场分析助手</span>
                </div>
                <div class="message-content">
                  <el-skeleton :rows="3" animated />
                </div>
              </el-card>
            </el-timeline-item>
          </el-timeline>
        </div>
        <div class="chat-input">
          <el-input
              v-model="chatInput"
              placeholder="输入您的问题..."
              @keyup.enter="sendMessage"
              type="textarea"
              :rows="2"
              :disabled="isLoading"
          />
          <el-button type="primary" @click="sendMessage" style="margin-top: 10px; width: 100%" :disabled="isLoading" :loading="isLoading">发送</el-button>
        </div>
      </el-card>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import axios from 'axios';

// 表单数据
const formData = ref({
  targetMarket: '',
  marketOverview: '',
  segmentedMarket: '',
  competitors: '',
  competitorDimensions: ''
});

// 状态变量
const loading = ref(false);
const chatMessages = ref([]);
const chatInput = ref('');
const isGenerating = ref(false);
const isLoading = ref(false); // 用于控制输入框禁用状态

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

// 生成报告
const generateReport = async () => {
  if (!formData.value.targetMarket) {
    alert('请填写目标市场');
    return;
  }

  loading.value = true;
  isLoading.value = true; // 禁用输入框
  isGenerating.value = true; // 显示加载动画
  try {
    // 构建更友好的用户输入消息
    const userMessage = `我需要分析以下市场信息：\n目标市场：${formData.value.targetMarket}\n市场概况：${formData.value.marketOverview}\n细分市场：${formData.value.segmentedMarket}\n竞品名称：${formData.value.competitors}\n竞品分析维度：${formData.value.competitorDimensions}`;

    // 添加用户消息到聊天历史（显示给用户的友好版本）
    chatMessages.value.push({
      role: 'user',
      content: userMessage,
      timestamp: getCurrentTimestamp()
    });

    // 构建详细的prompt给AI
    const prompt = `请根据以下市场信息生成一份详细的市场分析报告：\n\n目标市场：${formData.value.targetMarket}\n市场概况：${formData.value.marketOverview}\n细分市场：${formData.value.segmentedMarket}\n竞品名称：${formData.value.competitors}\n竞品分析维度：${formData.value.competitorDimensions}\n\n请生成一份结构清晰、内容正式的市场分析报告，包括：市场规模分析、发展趋势、竞争格局、机会与挑战等内容。报告应该专业、全面，并且易于理解。`;

    const response = await axios.post('https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions', {
      model: 'qwen3.5-plus',
      messages: [
        { role: 'user', content: prompt }
      ]
    }, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer sk-24fe119404b1404da366d99aacbe3bf9'
      }
    });

    // 添加AI回复到聊天历史
    chatMessages.value.push({
      role: 'assistant',
      content: response.data.choices[0].message.content,
      timestamp: getCurrentTimestamp()
    });
  } catch (error) {
    console.error('生成报告失败:', error);
    // 添加错误消息到聊天历史
    chatMessages.value.push({
      role: 'assistant',
      content: `生成报告失败: ${error.response ? JSON.stringify(error.response.data) : error.message}`,
      timestamp: getCurrentTimestamp()
    });
  } finally {
    loading.value = false;
    isLoading.value = false; // 启用输入框
    isGenerating.value = false; // 隐藏加载动画
  }
};

// 发送消息
const sendMessage = async () => {
  if (!chatInput.value.trim()) return;

  // 添加用户消息
  const userMessage = chatInput.value;
  chatMessages.value.push({
    role: 'user',
    content: userMessage,
    timestamp: getCurrentTimestamp()
  });
  chatInput.value = '';

  isGenerating.value = true;
  isLoading.value = true; // 禁用输入框
  try {
    // 构建聊天历史消息
    const messages = chatMessages.value.map(msg => ({
      role: msg.role,
      content: msg.content
    }));

    const response = await axios.post('https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions', {
      model: 'qwen3.5-plus',
      messages: messages
    }, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer sk-24fe119404b1404da366d99aacbe3bf9'
      }
    });

    // 添加AI回复
    chatMessages.value.push({
      role: 'assistant',
      content: response.data.choices[0].message.content,
      timestamp: getCurrentTimestamp()
    });
  } catch (error) {
    console.error('发送消息失败:', error);
    // 添加错误消息到聊天历史
    chatMessages.value.push({
      role: 'assistant',
      content: `抱歉，我暂时无法回答您的问题，请稍后重试。错误: ${error.response ? JSON.stringify(error.response.data) : error.message}`,
      timestamp: getCurrentTimestamp()
    });
  } finally {
    isGenerating.value = false;
    isLoading.value = false; // 启用输入框
  }
};
</script>

<style scoped>
.market-analysis-container {
  display: flex;
  min-height: 100vh;
  gap: 20px;
}

.left-panel {
  width: 400px;
  flex-shrink: 0;
}

.left-panel .el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.title h3 {
  margin: 0 0 20px 0;
  color: #303133;
  font-size: 18px;
  font-weight: bold;
}

.form-container {
  flex: 1;
}

.generate-button {
  margin-top: 30px;
}

.right-panel {
  flex: 1;
}

.right-panel .el-card {
  height: 100%;
  display: flex;
  flex-direction: column;
}

.chat-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.chat-header h3 {
  margin: 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.chat-messages {
  flex: 1;
  overflow-y: auto;
  margin-bottom: 20px;
  max-height: 600px;
}

.chat-input {
  margin-top: auto;
}

.message-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
  font-size: 12px;
}

.message-role {
  font-weight: bold;
  color: #303133;
}

.message-time {
  color: #909399;
}

.message-content {
  line-height: 1.6;
  color: #303133;
  white-space: pre-wrap;
}

.user-message {
  border-left: 4px solid #409eff;
}

.ai-message {
  border-left: 4px solid #67c23a;
}

.el-timeline-item {
  padding-bottom: 20px;
}

@media (max-width: 768px) {
  .market-analysis-container {
    flex-direction: column;
  }

  .left-panel {
    width: 100%;
  }

  .chat-messages {
    max-height: 400px;
  }
}
</style>