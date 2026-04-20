<template>
  <div class="common-layout">
    <el-container>
<!--      顶部 -->
      <el-header class="admin-header">
        <div class="title">
          <h1>项目管理系统</h1>
        </div>
        <div class="tags">
            <el-tag
              v-for="(tag,index) in tagList"
              :key="index"
              class="tag-item"
              @click="handleTagClick(tag)"
            >
              {{tag}}
            </el-tag>
        </div>
        <div class="leave">
          <span>欢迎，
            {{name}}
          </span>
          <a href="javascript:void(0)" style="margin-left: 10px;text-decoration: none;color: white" @click="logout">退出</a>
        </div>
      </el-header>

      <el-container class="admin-content">
        <!--      导航栏-->
        <el-aside class="aside">
          <el-menu router :default-active="activeMenu">
            <el-menu-item
                class="menu-item"
                v-for="(item,index) in menuList"
                :key="item.path"
                :index="item.path"
            >
<!--              图标-->
<!--              <el-icon>-->  
<!--                <component :is="item.icon"/>-->  
<!--              </el-icon>-->  
              {{item.name}}
            </el-menu-item>
          </el-menu>
        </el-aside>
<!--        主页内容-->
        <el-main class="main-comment">
          <router-view/>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import {ref, watch, onMounted, computed} from "vue";
import {useRoute, useRouter} from "vue-router";
import axios from "axios";

const route = useRoute();
const router = useRouter();

const tagList = ref([]);
const tagRoutes=ref({});
const name = ref('用户');

// 页面加载时获取用户信息
onMounted(() => {
  // 从本地存储中获取用户信息
  const userStr = localStorage.getItem('user');
  if (userStr) {
    const user = JSON.parse(userStr);
    name.value = user.username || '用户';
  }
});

// 根据当前路由的 meta.tabs 更新标签
watch(
    () => route.meta.tabs,
    (newTabs) => {
      if (newTabs && Array.isArray(newTabs)) {
        tagList.value = newTabs.map(tag => tag.name);
        // 构建路由映射对象
        const routesMap = {};
        newTabs.forEach(tag => {
          routesMap[tag.name] = tag.route;
        });
        tagRoutes.value = routesMap;
      } else {
        // 默认标签（可留空或设为通用）
        tagList.value = [];
        tagRoutes.value = {};
      }
    },
    { immediate: true } // 立即执行一次，以初始化
);
// 处理标签点击
const handleTagClick = (tag) => {
  const path= tagRoutes.value[tag];
  if (path) {
    router.push(path);
  }
  else{
    console.warn('未找到对应路由',tag);
  }
}

const menuList = ref([
  {path:'/workbench/dashboard',name:'工作台'},
  {path: '/itemSet/itemList',name:'项目集'},
  {path: '/productResearch/productDashboard',name:'产品'},
  {path: '/task/taskList',name:'任务'},
  {path: '/iteration/iterationList',name:'迭代'},
  {path: '/teams/team',name:'团队'},
  {path: '/test/tests',name:'测试'},
  {path: '/AI/assistant',name:'AI'},
  {path: '/feedbacks/feedback',name:'反馈'}
])

// 计算当前激活的菜单
const activeMenu = computed(() => {
  // 获取当前路由路径
  const currentPath = route.path;
  
  // 检查当前路径是否匹配菜单中的某个项
  for (const item of menuList.value) {
    // 获取菜单项路径的父路径
    const itemParentPath = item.path.split('/').slice(0, 3).join('/');
    // 获取当前路径的父路径
    const currentParentPath = currentPath.split('/').slice(0, 3).join('/');
    
    // 如果当前路径的父路径与菜单项的父路径匹配，则返回该菜单项的路径
    if (currentParentPath === itemParentPath) {
      return item.path;
    }
  }
  
  // 默认返回工作台
  return '/workbench/dashboard';
});

// 退出登录
const logout = async () => {
  try {
    // 从本地存储中获取用户信息
    const userStr = localStorage.getItem('user');
    let username = '';
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
.common-layout {
  height: 100vh;  /* 占满视口 */
  background-color: #F0F2F5;
}
/* 顶部样式 */
.admin-header{
  background-color: #238EFF;
  color: white;
  display: flex;
  justify-content: center;
  align-items: center;
}
.title{
  flex:1;
}
.tags{
  flex:1;
  height: 100%;
  display: flex;
  justify-content: center;
  align-items: center;
}
.leave{
  flex:1;
  text-align: right;
  padding-right: 30px;
}

.tag-item{
  background-color:#238EFF;
  color: white;
  border: none;
  border-radius: 0;
  height:100%;

}
.tag-item:hover{
  background-color: #C6E2FF;
  color: #1e293b;
}
/*下面整块*/
.admin-content{
  padding-top: 10px;
  background-color: #F0F2F5;
}
/*导航栏样式*/
.aside{
  width: 120px;
  background-color: #30394A;
}
.menu-item{
  background-color: #30394A;
  justify-content: center;
  color: white;
}
.menu-item:hover{
  background-color: #238EFF;
}

/* Element Plus 激活菜单样式 */
.el-menu-item.is-active {
  background-color: #238EFF !important;
  color: white !important;
}
/*主要内容*/
.main-comment{
  padding-top: 0;
}
</style>