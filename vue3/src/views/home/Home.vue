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
          <a href="/vue3/public">退出</a>
        </div>
      </el-header>

      <el-container class="admin-content">
        <!--      导航栏-->
        <el-aside class="aside">
          <el-menu router>
            <el-menu-item
                class="menu-item"
                v-for="(item,index) in menuList"
                :key="item.path"
                :index="item.path"
                :class="{'liang':index === 0}"
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
import {ref, watch} from "vue";
import {useRoute, useRouter} from "vue-router";

const route = useRoute();
const router = useRouter();

const tagList = ref([]);
const tagRoutes=ref({});
// 根据当前路由的 meta.tags 更新标签
watch(
    () => route.meta.tags,
    (newTags) => {
      if (newTags && Array.isArray(newTags)) {
        tagList.value = newTags.map(tag => tag.name);
        // 构建路由映射对象
        const routesMap = {};
        newTags.forEach(tag => {
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
  {path: '/AI/marketAI',name:'AI'},
  {path: '/feedbacks/feedback',name:'反馈'}
])
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
.liang{
  background-color: #238EFF;
  color:white;
}
/*主要内容*/
.main-comment{
  padding-top: 0;
}
</style>