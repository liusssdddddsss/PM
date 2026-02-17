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
              v-for="tag in tagList"
              :key="tag"
              class="tag-item"
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
import {ref} from "vue";

const tagList = ref([
    '仪表盘',
    '日程',
    '待处理',
    '待审批',
    '项目',
    '任务'
]);

const menuList = ref([
  {path:'/home/style',name:'工作台'},
  {path: '/',name:'项目集'},
  {path: '/',name:'产品'},
  {path: '/',name:'迭代'},
  {path: '/',name:'执行'},
  {path: '/',name:'测试'},
  {path: '/',name:'迭代'},
  {path: '/',name:'AI'},
  {path: '/',name:'反馈'},
  {path: '/',name:'更多'},
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