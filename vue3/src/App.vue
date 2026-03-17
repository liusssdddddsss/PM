<template>
  <div class="app-container">
    <router-view />
    <Watermark :user-info="userInfo" />
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import { useRoute } from 'vue-router';
import Watermark from './components/Watermark.vue';

const userInfo = ref({});
const route = useRoute();

// 从本地存储中获取用户信息的函数
const fetchUserInfo = () => {
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
    userInfo.value = {};
  }
};

onMounted(() => {
  fetchUserInfo();
});

// 监听路由变化，每次路由变化时检查用户信息
watch(
  () => route.path,
  () => {
    fetchUserInfo();
  }
);
</script>

<style scoped>
.app-container {
  position: relative;
  min-height: 100vh;
}
</style>
