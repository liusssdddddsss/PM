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
    ]},

      {path:'/productResearch',component:()=>import('@/views/home/Home.vue'),
          meta:{
              tabs:[
                  {name:'仪表盘',route:'/productResearch/productDashboard'},
                  {name:'产品列表',route:'/productResearch/productList'},
                  {name:'产品看板',route:'/productResearch/productBorder'},
              ]
          },
          children:[
              {path:'productDashboard',component:()=>import('@/views/ProductResearch/ProductDashboard.vue')},
              {path:'productList',component:()=>import('@/views/ProductResearch/ProductList.vue')},
              {path:'productBorder',component:()=>import('@/views/ProductResearch/ProductBorder.vue')},
              {path:'productEdit',component:()=>import('@/views/ProductResearch/ProductEdit.vue')},

          ]},
      {path:'/iteration',component:()=>import('@/views/home/Home.vue'),
          meta:{
              tabs:[
                  {name:'项目列表',route:'/iteration/iterationList'},
                  {name:'项目看板',route:'/iteration/iterationBorder'},
              ]
          },
          children:[
              {path:'iterationList',component:()=>import('@/views/iteration/IterationList.vue')},
              {path:'iterationBorder',component:()=>import('@/views/iteration/IterationBorder.vue')},
              {path:'iterationDetail',component:()=>import('@/views/iteration/IterationDetail.vue')}

          ]},
      {path:'/test',component:()=>import('@/views/home/Home.vue'),

          children:[
              {path:'tests',component:()=>import('@/views/test/Tests.vue')},
              {path:'testSubmit',component:()=>import('@/views/test/TestSubmit.vue')},
              {path:'testList',component:()=>import('@/views/test/TestList.vue')},
          ]},
      {path:'/task',component:()=>import('@/views/home/Home.vue'),
          meta:{
              tabs:[
                  {name:'任务列表',route:'/task/taskList'},
                  {name:'任务看板',route:'/task/taskBorder'},
              ]
          },
          children:[
              {path:'taskList',component:()=>import('@/views/task/TList.vue')},
      {path:'taskBorder',component:()=>import('@/views/task/TaskBorder.vue')},
      {path:'addTask',component:()=>import('@/views/task/AddTask.vue')},
      {path:'addTaskForm',component:()=>import('@/views/task/AddTaskForm.vue')},
      {path:'taskEdit',component:()=>import('@/views/task/TaskEdit.vue')},
      {path:'taskCreate',component:()=>import('@/views/task/TaskCreate.vue')},
      {path:'assignedTasks',component:()=>import('@/views/task/AssignedTasks.vue')},
      {path:'meJoinTasks',component:()=>import('@/views/task/MeJoinTasks.vue')},
      {path:'meAssignedTasks',component:()=>import('@/views/task/MeAssignedTasks.vue')},

          ]},
      {path:'/feedbacks',component:()=>import('@/views/home/Home.vue'),
          meta:{
              tabs:[
                  {name:'反馈',route:'/feedbacks/feedback'},
                  {name:'工单',route:'/feedbacks/workOrder'},
              ]
          },
          children:[
              {path:'feedback',component:()=>import('@/views/feedbacks/FeedBack.vue')},
              {path:'workOrder',component:()=>import('@/views/feedbacks/WorkOrder.vue')},

          ]},
      {path:'/AI',component:()=>import('@/views/home/Home.vue'),
          children:[
              {path:'marketAI',component:()=>import('@/views/AI/Market.vue')},

          ]},
      {path:'/teams',component:()=>import('@/views/home/Home.vue'),
          children:[
              {path:'team',component:()=>import('@/views/team/Team.vue')},

          ]},
      {path:'/admin',component:()=>import('@/views/admin/AdminPanel.vue'),
          meta:{
              tabs:[
                  {name:'管理员面板',route:'/admin'}
              ]
          },
          children:[
              {path:'',component:()=>import('@/views/admin/AdminContent.vue')},
              {path:'userManagement',component:()=>import('@/views/admin/AdminContent.vue')},
              {path:'teamManagement',component:()=>import('@/views/admin/TeamManagement.vue')},
              {path:'logManagement',component:()=>import('@/views/admin/LogManagement.vue')},
              {path:'feedback',component:()=>import('@/views/admin/Feedback.vue')}
          ]}
  ],
})

export default router
