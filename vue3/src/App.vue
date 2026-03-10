<template>
  <div class="app-container">
    <router-view />
    <Watermark :user-info="userInfo" />
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import Watermark from './components/Watermark.vue';

const userInfo = ref({});

onMounted(() => {
  // 从本地存储中获取用户信息
  const userStr = localStorage.getItem('user');
  console.log('获取到的用户信息字符串:', userStr);
  if (userStr) {
    try {
      const user = JSON.parse(userStr);
      console.log('解析后的用户信息:', user);
      userInfo.value = user;
    } catch (error) {
      console.error('解析用户信息失败:', error);
    }
  } else {
    console.log('本地存储中没有用户信息');
  }
});
</script>

<style scoped>
.app-container {
  position: relative;
  min-height: 100vh;
}
</style>
