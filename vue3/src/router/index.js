import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path:'/',component:()=>import('@/views/login/Login.vue')},
    {path:'/admin',component:()=>import('@/views/home/AdminHome.vue'),children:[
        {path:'style',component:()=>import('@/views/admin/Style.vue')},
        {path:'workbench',component:()=>import('@/views/admin/Workbench.vue')},
        {path:'approveList',component:()=>import('@/views/admin/listView/ApproveList.vue')},
        {path:'taskList',component:()=>import('@/views/admin/listView/TaskList.vue')},
        {path:'noFinishList',component:()=>import('@/views/admin/listView/NoFinishList.vue')},
      ]
    }
  ],
})

export default router
