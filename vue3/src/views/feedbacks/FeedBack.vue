<template>
  <div class="feedback-container">
    <div class="feedback-header">
      <div class="header-left">
        <span class="product-selector">全部产品</span>
      </div>
      <div class="header-right">
        <el-button type="primary" plain>反馈</el-button>
        <el-button type="primary" plain>工单</el-button>
        <el-button type="primary" round>创建反馈</el-button>
      </div>
    </div>

    <div class="feedback-content">
      <div class="content-header">
        <el-tabs v-model="activeTab" class="feedback-tabs">
          <el-tab-pane label="全部" name="all"></el-tab-pane>
          <el-tab-pane label="待处理 19" name="pending"></el-tab-pane>
          <el-tab-pane label="处理中" name="processing"></el-tab-pane>
          <el-tab-pane label="待关闭" name="toClose"></el-tab-pane>
<!--          <el-tab-pane label="待归档" name="toArchive"></el-tab-pane>-->
<!--          <el-tab-pane label="由我反馈" name="myFeedback"></el-tab-pane>-->
<!--          <el-tab-pane label="公开" name="public"></el-tab-pane>-->
<!--          <el-tab-pane label="更多" name="more">-->
<!--            <template #default>-->
<!--              <div class="more-options">-->
<!--                <el-button size="small">选项1</el-button>-->
<!--                <el-button size="small">选项2</el-button>-->
<!--              </div>-->
<!--            </template>-->
<!--          </el-tab-pane>-->
        </el-tabs>

        <div class="search-bar">
          <el-input
              v-model="searchQuery"
              placeholder="搜索"
              prefix-icon="el-icon-search"
              style="width: 200px;margin-bottom: 5px"
          />
          <el-button type="primary" size="small">导出</el-button>
          <el-button type="primary" size="small">导入</el-button>
        </div>
      </div>

<!--      <div class="feedback-list">-->
<!--        <el-table-->
<!--            :data="feedbackList"-->
<!--            style="width: 100%"-->
<!--            border-->
<!--        >-->
<!--          <el-table-column type="selection" width="40"></el-table-column>-->
<!--          <el-table-column prop="id" label="ID" width="80"></el-table-column>-->
<!--          <el-table-column prop="title" label="标题" min-width="300"></el-table-column>-->
<!--          <el-table-column prop="priority" label="P" width="40" align="center">-->
<!--            <template #default="scope">-->
<!--              <el-tag size="small" :type="getPriorityType(scope.row.priority)">-->
<!--                {{ scope.row.priority }}-->
<!--              </el-tag>-->
<!--            </template>-->
<!--          </el-table-column>-->
<!--          <el-table-column prop="status" label="状态" width="100"></el-table-column>-->
<!--          <el-table-column prop="type" label="类型" width="100"></el-table-column>-->
<!--          <el-table-column prop="assignee" label="指派给" width="120"></el-table-column>-->
<!--          <el-table-column label="操作" width="150" fixed="right">-->
<!--            <template #default="scope">-->
<!--              <el-button size="small" @click="viewDetail(scope.row)">查看</el-button>-->
<!--              <el-button size="small" @click="assignFeedback(scope.row)">指派</el-button>-->
<!--            </template>-->
<!--          </el-table-column>-->

<!--        </el-table>-->

      <TaskList/>

        <div class="pagination">
          <span>共 19 项</span>
          <el-pagination
              layout="total, sizes, prev, pager, next, jumper"
              :total="19"
              :page-size="20"
              :current-page="1"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
          />
<!--        </div>-->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import TaskList from "@/views/workbenchView/listView/TaskList.vue";

const router = useRouter();

// 激活的标签
const activeTab = ref('all');
// 搜索查询
const searchQuery = ref('');

// 反馈列表数据
const feedbackList = ref([

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
};

// 编辑反馈
const editFeedback = (row) => {
  console.log('编辑反馈:', row);
  // 这里可以添加编辑反馈的逻辑
};

// 指派反馈
const assignFeedback = (row) => {
  console.log('指派反馈:', row);
  // 这里可以添加指派反馈的逻辑
};

// 更多操作
const moreActions = (row) => {
  console.log('更多操作:', row);
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
.feedback-container {
  background-color: #fff;
  min-height: 100vh;
}

.feedback-header {
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
  font-weight: bold;
}

.header-right {
  display: flex;
  gap: 10px;
}

.feedback-content {
  padding: 20px;
}

.content-header {
  background-color: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.feedback-tabs {
  flex: 1;
}

.search-bar {
  display: flex;
  gap: 10px;
  align-items: center;
}

.feedback-list {
  background-color: white;
  padding: 15px;
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