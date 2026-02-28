<template>
  <div class="workorder-container">

    <div class="workorder-content">
      <div class="left-sidebar">
        <div class="sidebar-header">
          <span>项目</span>
        </div>
        <div class="project-list">
          <div class="project-item" :class="{ active: activeProject === 'all' }" @click="activeProject = 'all'">
            全部
          </div>
          <div class="project-item" :class="{ active: activeProject === 'test' }" @click="activeProject = 'test'">
            测试
          </div>
          <div class="project-item" :class="{ active: activeProject === 'testProject' }" @click="activeProject = 'testProject'">
            测试项目
          </div>
          <div class="project-item" :class="{ active: activeProject === 'permissionManagement' }" @click="activeProject = 'permissionManagement'">
            提供增强化的权限管理
          </div>
        </div>
      </div>

      <div class="right-content">
        <div class="content-header">
          <el-tabs v-model="activeTab" class="workorder-tabs">
            <el-tab-pane label="全部" name="all"></el-tab-pane>
            <el-tab-pane label="未关闭" name="open"></el-tab-pane>
            <el-tab-pane label="待处理 5" name="pending"></el-tab-pane>
            <el-tab-pane label="处理中" name="processing"></el-tab-pane>
            <el-tab-pane label="待关闭" name="toClose"></el-tab-pane>
          </el-tabs>

          <div class="search-bar">
<!--            <el-input-->
<!--                v-model="searchQuery"-->
<!--                placeholder="搜索"-->
<!--                prefix-icon="el-icon-search"-->
<!--                style="width: 200px"-->
<!--            />-->
<!--            <el-button type="primary" size="small">导出</el-button>-->
<!--            <el-button type="primary" size="small">导入</el-button>-->
          </div>
        </div>

<!--        <div class="workorder-list">-->
<!--          <el-table-->
<!--              :data="workorderList"-->
<!--              style="width: 100%"-->
<!--              border-->
<!--          >-->
<!--            <el-table-column type="selection" width="40"></el-table-column>-->
<!--            <el-table-column prop="id" label="ID" width="80"></el-table-column>-->
<!--            <el-table-column prop="title" label="标题" min-width="300"></el-table-column>-->
<!--            <el-table-column prop="priority" label="P" width="40" align="center">-->
<!--              <template #default="scope">-->
<!--                <el-tag size="small" :type="getPriorityType(scope.row.priority)">-->
<!--                  {{ scope.row.priority }}-->
<!--                </el-tag>-->
<!--              </template>-->
<!--            </el-table-column>-->
<!--            <el-table-column prop="source" label="来源" width="100"></el-table-column>-->
<!--            <el-table-column prop="status" label="状态" width="100"></el-table-column>-->
<!--            <el-table-column prop="type" label="类型" width="100"></el-table-column>-->
<!--            <el-table-column label="操作" width="150" fixed="right">-->
<!--              <template #default="scope">-->
<!--                <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>-->
<!--                <el-button size="small" @click="editWorkorder(scope.row)">编辑</el-button>-->
<!--                <el-button size="small" @click="assignWorkorder(scope.row)">指派</el-button>-->
<!--                <el-button size="small" @click="moreActions(scope.row)">更多</el-button>-->
<!--              </template>-->
<!--            </el-table-column>-->
<!--          </el-table>-->
        <TaskList/>


          <div class="pagination">
            <span>共 5 项</span>
            <el-pagination
                layout="total, sizes, prev, pager, next, jumper"
                :total="5"
                :page-size="20"
                :current-page="1"
                @size-change="handleSizeChange"
                @current-change="handleCurrentChange"
            />
<!--          </div>-->
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import TaskList from "@/views/workbenchView/listView/TaskList.vue";

const router = useRouter();

// 激活的项目
const activeProject = ref('all');
// 激活的标签
const activeTab = ref('all');
// 搜索查询
const searchQuery = ref('');

// 工单列表数据
const workorderList = ref([
  {
    id: 5,
    title: '添加自定义字段客户反馈到需求管理模块',
    priority: 3,
    source: '',
    status: '等待',
    type: '程序缺陷'
  },
  {
    id: 4,
    title: '解决甘特图导出为PDF时格式错乱问题',
    priority: 3,
    source: '',
    status: '等待',
    type: '程序缺陷'
  },
  {
    id: 3,
    title: '更新用户权限设置',
    priority: 3,
    source: '',
    status: '等待',
    type: '程序缺陷'
  },
  {
    id: 2,
    title: '新增项目模板"敏捷开发流程"',
    priority: 3,
    source: '',
    status: '等待',
    type: '程序缺陷'
  },
  {
    id: 1,
    title: '修复登录页面偶现的加载缓慢问题',
    priority: 3,
    source: '',
    status: '等待',
    type: '程序缺陷'
  }
]);

// 根据优先级获取标签类型
const getPriorityType = (priority) => {
  switch (priority) {
    case 1:
      return 'success';
    case 2:
      return 'warning';
    case 3:
      return 'danger';
    default:
      return 'info';
  }
};

// 查看详情
const viewDetail = (row) => {
  console.log('查看详情:', row);
  // 这里可以添加查看详情的逻辑
};

// 编辑工单
const editWorkorder = (row) => {
  console.log('编辑工单:', row);
  // 这里可以添加编辑工单的逻辑
};

// 指派工单
const assignWorkorder = (row) => {
  console.log('指派工单:', row);
  // 这里可以添加指派工单的逻辑
};

// 更多操作
const moreActions = (row) => {
  console.log('更多操作:', row);
  // 这里可以添加更多操作的逻辑
};

// 处理分页大小变化
const handleSizeChange = (size) => {
  console.log('每页条数:', size);
};

// 处理页码变化
const handleCurrentChange = (current) => {
  console.log('当前页码:', current);
};
</script>

<style scoped>
.workorder-container {
  background-color: #fff;
  min-height: 100vh;
}

.workorder-header {
  background-color: #1890ff;
  color: white;
  padding: 10px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-left {
  display: flex;
  align-items: center;
}

.product-selector {
  margin-right: 20px;
  font-weight: bold;
}

.header-right {
  display: flex;
  gap: 10px;
}

.workorder-content {
  display: flex;
  padding: 20px;
  gap: 20px;
}

.left-sidebar {
  width: 200px;
  background-color: white;
  padding: 15px;
}

.sidebar-header {
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #e4e7ed;
}

.project-list {
  max-height: 500px;
  overflow-y: auto;
}

.project-item {
  padding: 8px 10px;
  cursor: pointer;
  border-radius: 4px;
  margin-bottom: 5px;
  transition: all 0.3s ease;
}

.project-item:hover {
  background-color: #f0f9ff;
}

.project-item.active {
  background-color: #1890ff;
  color: white;
}

.right-content {
  flex: 1;
  background-color: white;
  padding: 15px;
}

.content-header {
  padding-bottom: 15px;
  border-bottom: 1px solid #e4e7ed;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.workorder-tabs {
  flex: 1;
}

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
}

.workorder-list {
  margin-top: 20px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.more-options {
  padding: 10px;
  display: flex;
  gap: 10px;
}
</style>