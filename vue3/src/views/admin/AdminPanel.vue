<template>
  <div class="admin-panel">
    <!-- 顶部状态栏 -->
    <header class="admin-header">
      <div class="title">
        <h1>管理员控制台</h1>
      </div>
      <div class="tags">
        <!-- 顶部状态栏暂时为空 -->
      </div>
      <div class="leave">
        <span>欢迎，{{adminName}}</span>
        <a href="javascript:void(0)" @click="logout">退出</a>
      </div>
    </header>

    <div class="admin-content">
      <!-- 左侧导航栏 -->
      <aside class="aside">
        <el-menu router>
          <el-menu-item
              class="menu-item"
              v-for="(item,index) in menuList"
              :key="item.path"
              :index="item.path"
              :class="{'liang':currentPath.includes(item.path)}"
          >
            {{item.name}}
          </el-menu-item>
        </el-menu>
      </aside>
      <!-- 主内容区域 -->
      <main class="main-content">
        <router-view/>
      </main>
    </div>
    

  </div>
</template>

<script setup>
import {ref, computed} from "vue";
import {useRoute} from "vue-router";
import axios from "axios";

const route = useRoute();

// 管理员信息
const adminName = ref('管理员');

// 当前路径
const currentPath = computed(() => route.path);

// 左侧导航菜单
const menuList = ref([
  {path: '/admin/userManagement', name: '用户管理'},
  {path: '/admin/teamManagement', name: '团队管理'},
  {path: '/admin/logManagement', name: '日志管理'},
  {path: '/admin/feedback', name: '反馈'}
]);

// 退出登录
const logout = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let username = '1'; // 默认值
    if (userStr) {
      const user = JSON.parse(userStr);
      username = user.username;
    }
    await axios.post('http://localhost:9091/admin/logout', { username });
    // 清除本地存储中的用户信息
    localStorage.removeItem('user');
    window.location.href = '/';
  } catch (error) {
    console.error('退出登录失败:', error);
    // 清除本地存储中的用户信息
    localStorage.removeItem('user');
    window.location.href = '/';
  }
};
</script>

<style scoped>
.admin-panel {
  display: flex;
  flex-direction: column;
  height: 100vh;
  background-color: #F0F2F5;
}

/* 顶部样式 */
.admin-header {
  background-color: #238EFF;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
  height: 60px;
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
}

.title {
  flex: 1;
}

.tags {
  flex: 1;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.leave {
  flex: 1;
  text-align: right;
  padding-right: 30px;
}

.leave a {
  color: white;
  text-decoration: none;
  margin-left: 20px;
}

.leave a:hover {
  text-decoration: underline;
}

/* 内容区域 */
.admin-content {
  display: flex;
  flex: 1;
  margin-top: 60px;
  background-color: #F0F2F5;
}

/* 左侧导航栏 */
.aside {
  width: 120px;
  background-color: #30394A;
  position: fixed;
  left: 0;
  top: 70px;
  bottom: 0;
  z-index: 90;
}

.menu-item {
  background-color: #30394A;
  justify-content: center;
  color: white;
}

.menu-item:hover {
  background-color: #238EFF;
}

.liang {
  background-color: #238EFF;
  color: white;
}

/* 主内容 */
.main-content {
  flex: 1;
  margin-left: 120px;
  padding: 20px;
  overflow-y: auto;
  height: calc(100vh - 60px);
}
</style>