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
          <el-tab-pane label="待处理" name="pending"></el-tab-pane>
          <el-tab-pane label="处理中" name="processing"></el-tab-pane>
          <el-tab-pane label="待关闭" name="toClose"></el-tab-pane>

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
      <FeedbackList :activeTab="activeTab" :searchQuery="searchQuery" @update:total="updateTotal"/>

        <div class="pagination">
          <span>共 {{ total }} 项</span>
          <el-pagination
              layout="total, sizes, prev, pager, next, jumper"
              :total="total"
              :page-size="20"
              :current-page="1"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
              style="margin-right: 10px"
          />
<!--        </div>-->
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import FeedbackList from "@/views/workbenchView/listView/FeedbackList.vue";

const router = useRouter();

// 激活的标签
const activeTab = ref('all');
// 搜索查询
const searchQuery = ref('');
// 数据总数
const total = ref(0);

// 更新数据总数
const updateTotal = (count) => {
  total.value = count;
  console.log('更新数据总数:', count);
};

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
  margin-left: 20px;
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
  margin-right:10px;
}

.pagination {
  margin-top: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

</style>