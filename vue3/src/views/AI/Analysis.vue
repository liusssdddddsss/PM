<template>
  <div class="analysis-container">
    <div class="left-panel">
      <el-card>
        <div class="title">
          <h3>智能分析预警</h3>
        </div>
        <div class="form-container">
          <el-form :model="formData" label-width="80px">
            <el-form-item label="项目名称">
              <el-input
                  v-model="formData.projectName"
                  placeholder="请输入项目名称"
              />
            </el-form-item>

            <el-form-item label="分析维度">
              <el-select v-model="formData.analysisDimensions" placeholder="请选择" multiple>
                <el-option label="进度风险" value="progress" />
                <el-option label="资源风险" value="resource" />
                <el-option label="技术风险" value="technology" />
                <el-option label="成本风险" value="cost" />
                <el-option label="质量风险" value="quality" />
              </el-select>
            </el-form-item>

            <el-form-item label="项目周期">
              <el-date-picker
                  v-model="formData.projectPeriod"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
              />
            </el-form-item>

            <el-form-item label="项目状态">
              <el-select v-model="formData.projectStatus" placeholder="请选择">
                <el-option label="进行中" value="inProgress" />
                <el-option label="已完成" value="completed" />
                <el-option label="已暂停" value="paused" />
              </el-select>
            </el-form-item>
          </el-form>

          <div class="generate-button">
            <el-button type="primary" @click="generateAnalysis" style="width: 100%" :loading="loading">分析</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div class="right-panel">
      <el-card>
        <div class="chat-header">
          <h3>智能分析助手</h3>
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
                  <span class="message-role">{{ message.role === 'user' ? '您' : '智能分析助手' }}</span>
                  <span class="message-time">{{ message.timestamp }}</span>
                </div>
                <div class="message-content" v-html="message.content"></div>
              </el-card>
            </el-timeline-item>
            <el-timeline-item v-if="isGenerating" type="info" icon="el-icon-loading">
              <el-card class="ai-message">
                <div class="message-header">
                  <span class="message-role">智能分析助手</span>
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
  projectName: '',
  analysisDimensions: [],
  projectPeriod: [],
  projectStatus: ''
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

// 生成分析报告
const generateAnalysis = async () => {
  if (!formData.value.projectName) {
    alert('请填写项目名称');
    return;
  }

  loading.value = true;
  isLoading.value = true; // 禁用输入框
  isGenerating.value = true; // 显示加载动画
  try {
    // 构建更友好的用户输入消息
    const userMessage = `我需要分析以下项目的风险：\n项目名称：${formData.value.projectName}\n分析维度：${formData.value.analysisDimensions.join(', ')}\n项目周期：${formData.value.projectPeriod ? formData.value.projectPeriod[0] + ' 至 ' + formData.value.projectPeriod[1] : '未指定'}\n项目状态：${formData.value.projectStatus}`;

    // 添加用户消息到聊天历史（显示给用户的友好版本）
    chatMessages.value.push({
      role: 'user',
      content: userMessage,
      timestamp: getCurrentTimestamp()
    });

    // 构建详细的prompt给AI
    const prompt = `请根据以下项目信息进行风险分析和预警：\n\n项目名称：${formData.value.projectName}\n分析维度：${formData.value.analysisDimensions.join(', ')}\n项目周期：${formData.value.projectPeriod ? formData.value.projectPeriod[0] + ' 至 ' + formData.value.projectPeriod[1] : '未指定'}\n项目状态：${formData.value.projectStatus}\n\n请生成一份详细的风险分析报告，包括：\n1. 各维度的风险评估\n2. 潜在风险点的预警\n3. 风险缓解建议\n4. 优先级排序\n\n报告应该专业、全面，并且易于理解。`;

    const response = await axios.post('https://dashscope.aliyuncs.com/compatible-mode/v1/chat/completions', {
      model: 'qwen3.5-flash',
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
    console.error('生成分析报告失败:', error);
    // 添加错误消息到聊天历史
    chatMessages.value.push({
      role: 'assistant',
      content: `生成分析报告失败: ${error.response ? JSON.stringify(error.response.data) : error.message}`,
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
      model: 'qwen3.5-flash',
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
.analysis-container {
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
  .analysis-container {
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