<template>
  <div class="dynamic-list">
    <ul>
      <li class="dynamic-item"
           v-for="(item,index) in dynamicData"
           :key="index"
      >
        <div class="dynamic-content">
          <div class="dynamic-message">
            {{item.operator}}{{getActionText(item.action, item.link)}}
            <span class="dynamic-link" v-if="item.link" @click="navigateToModule(item.link)">
                    {{item.link}}
          </span>
          </div>
          <div class="dynamic-time">
            {{item.time}}
          </div>
        </div>
      </li>
      <li v-if="dynamicData.length === 0" class="no-data">
        <span style="color: #909399;">暂无最新动态</span>
      </li>
    </ul>
  </div>
</template>

<script setup>

// 最新动态数据
import {ref, onMounted, onUnmounted} from "vue";
import { useRouter } from "vue-router";
import request from "@/utils/request.js";

const router = useRouter();

const dynamicData = ref([]);

// 从后端获取最新动态数据
const fetchDynamicData = async () => {
  try {
    // 最新动态改为全员可见，不传递用户名参数
    const response = await request.get('/dashboard/dynamic');
    if (response.data.code === 200) {
      dynamicData.value = response.data.data || [];
    }
  } catch (error) {
    console.error('获取最新动态失败:', error);
  }
};

// 页面加载时获取最新动态数据
onMounted(() => {
  fetchDynamicData();
  
  // 监听全局事件，当有操作日志记录时刷新最新动态
  console.log('Adding event listener for refreshDynamic');
  window.addEventListener('refreshDynamic', fetchDynamicData);
  
  // 测试事件触发
  setTimeout(() => {
    console.log('Testing refreshDynamic event');
    window.dispatchEvent(new CustomEvent('refreshDynamic'));
  }, 2000);
});

// 组件卸载时移除事件监听
onUnmounted(() => {
  console.log('Removing event listener for refreshDynamic');
  window.removeEventListener('refreshDynamic', fetchDynamicData);
});

// 导航到相应模块
const navigateToModule = (link) => {
  console.log('Navigating with link:', link);
  if (!link) return;
  
  // 根据link内容跳转到不同模块
  if (link.includes('project')) {
    console.log('Navigating to project module');
    // 跳转到项目集模块
    router.push('/itemSet/itemList');
  } else if (link.includes('approval')) {
    console.log('Navigating to approval module');
    // 跳转到审批模块
    router.push('/workbench/approval');
  } else if (link.includes('bug')) {
    console.log('Navigating to bug module');
    // 跳转到Bug模块
    router.push('/test/bugList');
  } else if (link.includes('task')) {
    console.log('Navigating to task module');
    // 跳转到任务模块
    router.push('/task/taskList');
  } else if (link.includes('user')) {
    console.log('Navigating to user module');
    // 跳转到用户模块
    router.push('/admin/userManagement');
  } else if (link.includes('product')) {
    console.log('Navigating to product module');
    // 跳转到产品模块
    router.push('/productResearch/productList');
  } else {
    console.log('Navigating to default workbench');
    // 默认跳转到工作台
    router.push('/workbench');
  }
};

// 根据action和link生成操作文本
const getActionText = (action, link) => {
  if (action && action.trim() !== '') {
    return action;
  }
  
  // 根据link字段生成默认操作文本
  if (link) {
    if (link.includes('product') || link.includes('auth')) {
      return '创建了产品';
    } else if (link.includes('project')) {
      return '创建了项目';
    } else if (link.includes('task')) {
      return '完成了任务';
    } else if (link.includes('bug')) {
      return '提交了Bug';
    } else if (link.includes('user')) {
      return '管理了用户';
    } else if (link.includes('approval')) {
      return '处理了审批';
    } else {
      return '进行了操作';
    }
  }
  
  return '';
};

</script>

<style scoped>
.dynamic-list{
  display: flex;
  flex-direction: column;
  gap: 16px;
  max-height: 600px;
  overflow-y: auto;
  padding-right: 8px;
  width: 100%;
  box-sizing: border-box;
}

/* 自定义滚动条样式 */
.dynamic-list::-webkit-scrollbar {
  width: 6px;
}

.dynamic-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.dynamic-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.dynamic-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
.dynamic-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  border-bottom: 1px solid #f0f0f0;
}
.dynamic-item:last-child {
  border-bottom: none;
}
.dynamic-content {
  display: flex;
  flex-direction: column;
}
.dynamic-time {
  color: #909399;
  white-space: nowrap;
  margin-top: 4px;
}
.dynamic-link {
  color: #409eff;
  cursor: pointer;
}
.dynamic-link:hover {
  text-decoration: underline;
}
</style>