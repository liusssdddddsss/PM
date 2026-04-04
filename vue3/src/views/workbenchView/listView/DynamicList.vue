<template>
  <div class="dynamic-list">
    <ul>
      <li class="dynamic-item"
           v-for="(item,index) in dynamicData"
           :key="index"
      >
        <div class="dynamic-content">
          <div class="dynamic-message">
            {{item.operator}}{{item.action}}
            <span class="dynamic-link" v-if="item.link">
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
import {ref, onMounted} from "vue";
import request from "@/utils/request.js";

const dynamicData = ref([]);

// 从后端获取最新动态数据
const fetchDynamicData = async () => {
  try {
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
});

</script>

<style scoped>
.dynamic-list{
  display: flex;
  flex-direction: column;
  gap: 16px;
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