<template>
  <div class="report-container">
    <div class="left-panel">
      <el-card>
        <div class="title">
          <h3>工作汇报</h3>
        </div>
        <div class="form-container">
          <el-form :model="formData" label-width="80px">
            <el-form-item label="汇报周期">
              <el-select v-model="formData.reportPeriod" placeholder="请选择">
                <el-option label="日汇报" value="daily" />
                <el-option label="周汇报" value="weekly" />
                <el-option label="月汇报" value="monthly" />
                <el-option label="季度汇报" value="quarterly" />
                <el-option label="年度汇报" value="yearly" />
              </el-select>
            </el-form-item>

            <el-form-item label="汇报时间">
              <el-date-picker
                  v-model="formData.reportDate"
                  type="daterange"
                  range-separator="至"
                  start-placeholder="开始日期"
                  end-placeholder="结束日期"
              />
            </el-form-item>

            <el-form-item label="汇报内容">
              <el-input
                  v-model="formData.reportContent"
                  placeholder="请输入汇报内容要点"
                  type="textarea"
                  :rows="3"
              />
            </el-form-item>

            <el-form-item label="项目名称">
              <el-input
                  v-model="formData.projectName"
                  placeholder="请输入项目名称"
              />
            </el-form-item>

            <el-form-item label="汇报类型">
              <el-select v-model="formData.reportType" placeholder="请选择">
                <el-option label="工作总结" value="summary" />
                <el-option label="进度汇报" value="progress" />
                <el-option label="问题反馈" value="issue" />
                <el-option label="计划安排" value="plan" />
              </el-select>
            </el-form-item>
          </el-form>

          <div class="generate-button">
            <el-button type="primary" @click="generateReport" style="width: 100%" :loading="loading">生成汇报</el-button>
          </div>
        </div>
      </el-card>
    </div>

    <div class="right-panel">
      <el-card>
        <div class="chat-header">
          <h3>工作汇报助手</h3>
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
                  <span class="message-role">{{ message.role === 'user' ? '您' : '工作汇报助手' }}</span>
                  <span class="message-time">{{ message.timestamp }}</span>
                </div>
                <div class="message-content" v-html="message.content"></div>
              </el-card>
            </el-timeline-item>
            <el-timeline-item v-if="isGenerating" type="info" icon="el-icon-loading">
              <el-card class="ai-message">
                <div class="message-header">
                  <span class="message-role">工作汇报助手</span>
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
  reportPeriod: '',
  reportDate: [],
  reportContent: '',
  projectName: '',
  reportType: ''
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

// 生成工作汇报
const generateReport = async () => {
  if (!formData.value.reportPeriod || !formData.value.reportDate) {
    alert('请填写汇报周期和汇报时间');
    return;
  }

  loading.value = true;
  isLoading.value = true; // 禁用输入框
  isGenerating.value = true; // 显示加载动画
  try {
    // 构建更友好的用户输入消息
    const userMessage = `我需要生成以下工作汇报：\n汇报周期：${formData.value.reportPeriod}\n汇报时间：${formData.value.reportDate[0]} 至 ${formData.value.reportDate[1]}\n汇报内容：${formData.value.reportContent}\n项目名称：${formData.value.projectName}\n汇报类型：${formData.value.reportType}`;

    // 添加用户消息到聊天历史（显示给用户的友好版本）
    chatMessages.value.push({
      role: 'user',
      content: userMessage,
      timestamp: getCurrentTimestamp()
    });

    // 构建详细的prompt给AI
    const prompt = `请根据以下信息生成一份详细的工作汇报：\n\n汇报周期：${formData.value.reportPeriod}\n汇报时间：${formData.value.reportDate[0]} 至 ${formData.value.reportDate[1]}\n汇报内容：${formData.value.reportContent}\n项目名称：${formData.value.projectName}\n汇报类型：${formData.value.reportType}\n\n请生成一份结构清晰、内容正式的工作汇报，包括：\n1. 工作内容概述\n2. 完成的任务和成果\n3. 遇到的问题和解决方案\n4. 下一步计划\n5. 总结和建议\n\n汇报应该专业、全面，并且易于理解。`;

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
    console.error('生成工作汇报失败:', error);
    // 添加错误消息到聊天历史
    chatMessages.value.push({
      role: 'assistant',
      content: `生成工作汇报失败: ${error.response ? JSON.stringify(error.response.data) : error.message}`,
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
.report-container {
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
  .report-container {
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