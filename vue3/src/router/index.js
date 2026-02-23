import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {path:'/',component:()=>import('@/views/login/Login.vue')},
    {path:'/workbench',component:()=>import('@/views/home/Home.vue'),
        meta:{
            tabs:[
                {name:'仪表盘',route:'/workbench/dashboard'},
                {name:'日程',route:'/workbench/schedule'},
                {name:'待处理',route:'/workbench/pending'},
                {name:'待审批',route:'/workbench/approval'},
                {name:'项目',route:'/workbench/projects'},
                {name:'任务',route:'/workbench/tasks'}
            ]
        },
        children:[
        {path:'style',component:()=>import('@/views/Style.vue')},
        {path:'dashboard',component:()=>import('@/views/workbenchView/Dashboard.vue')},
        {path:'approveList',component:()=>import('@/views/workbenchView/listView/ApproveList.vue')},
        {path:'taskList',component:()=>import('@/views/workbenchView/listView/TaskList.vue')},
        {path:'noFinishList',component:()=>import('@/views/workbenchView/listView/NoFinishList.vue')},
        {path:'researchList',component:()=>import('@/views/workbenchView/listView/ResearchList.vue')},
        {path:'userNeedList',component:()=>import('@/views/workbenchView/listView/UserNeedList.vue')},
        {path:'bugList',component:()=>import('@/views/workbenchView/listView/BugList.vue')},
        {path:'dynamicList',component:()=>import('@/views/workbenchView/listView/DynamicList.vue')},
        {path:'projectList',component:()=>import('@/views/workbenchView/listView/ProjectList.vue')},
        {path:'productList',component:()=>import('@/views/workbenchView/listView/ProductList.vue')},
        {path:'stayTestList',component:()=>import('@/views/workbenchView/listView/StayTestList.vue')},

        {path:'schedule',component:()=>import('@/views/workbenchView/Schedule.vue')},
        {path:'pending',component:()=>import('@/views/workbenchView/Pending.vue')},
        {path:'approval',component:()=>import('@/views/workbenchView/Approval.vue')},
        {path:'projects',component:()=>import('@/views/workbenchView/Projects.vue')},
        {path:'tasks',component:()=>import('@/views/workbenchView/Tasks.vue')},

        ]
    },

    {path:'/itemSet',component:()=>import('@/views/home/Home.vue'),
        meta:{
            tabs:[
                {name:'项目列表',route:'/itemSet/itemList'},
                {name:'项目看板',route:'/itemSet/itemBorder'},
            ]
        },
        children:[
            {path:'itemList',component:()=>import('@/views/itemSetView/ItemList.vue')},
            {path:'itemBorder',component:()=>import('@/views/itemSetView/ItemBorder.vue')},
            {path:'itemEdit',component:()=>import('@/views/itemSetView/ItemEdit.vue')},

        ]}
  ],
})

export default router
