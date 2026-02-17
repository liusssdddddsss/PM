<template>
  <div class="workbench">
    <!-- 头部：日期 + 用户名/问候 -->
    <div class="header">
      <div class="date-info">
        <el-icon class="calendar-icon"><Calendar /></el-icon>
        <span>{{ currentDate }} {{ currentWeekday }}</span>
      </div>
      <div class="user-greeting">
        <el-avatar :size="32" src="https://cube.elemecdn.com/3/7c/3ea6beec64369c2642b92c6726f1epng.png" />
        <span>张三三，上午好</span>
      </div>
    </div>

    <!-- 昨日处理摘要 -->
    <el-card class="summary-card" shadow="hover">
      <div class="summary-content">
        <el-icon><TrendCharts /></el-icon>
        <span>昨日您处理任务和 Bug 20 次</span>
      </div>
    </el-card>

    <!-- 两大核心统计卡片：待我审批 / 指派给我的 -->
    <el-row :gutter="20" class="stats-row">
      <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12">
        <el-card class="stat-card large" shadow="hover">
          <div class="stat-title">
            <el-icon><Clock /></el-icon>
            <span>待我审批</span>
          </div>
          <div class="stat-value">{{ pendingApproval }}</div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="12" :lg="12" :xl="12">
        <el-card class="stat-card large" shadow="hover">
          <div class="stat-title">
            <el-icon><User /></el-icon>
            <span>指派给我的</span>
          </div>
          <div class="stat-value">{{ assignedToMe }}</div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 六项细分统计卡片：使用 Element Plus 的 Statistic 组件 -->
    <el-row :gutter="20" class="stats-row">
      <el-col
          v-for="(item, index) in statsList"
          :key="index"
          :xs="12"
          :sm="8"
          :md="8"
          :lg="8"
          :xl="8"
      >
        <el-card class="stat-card" shadow="hover">
          <el-statistic :value="item.value">
            <template #title>
              <div class="stat-title-with-icon">
                <el-icon><component :is="item.icon" /></el-icon>
                <span>{{ item.title }}</span>
              </div>
            </template>
          </el-statistic>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import {
  Calendar,
  TrendCharts,
  Clock,
  User,
  Document,
  Warning,
  DataBoard,
  Tickets,
  Files
} from '@element-plus/icons-vue'

// 顶部日期
const currentDate = ref('')
const currentWeekday = ref('')

// 两大卡片数据
const pendingApproval = ref(16)
const assignedToMe = ref(12)

// 下方六项统计数据（图标与标题对应）
const statsList = ref([
  { title: '待我审批数', value: 8, icon: Clock },
  { title: '任务数', value: 12, icon: Document },
  { title: 'BUG数', value: 3, icon: Warning },
  { title: '研发需求数', value: 4, icon: DataBoard },
  { title: '用户需求数', value: 6, icon: Tickets },
  { title: '文档数', value: 7, icon: Files }
])

// 初始化当前日期（格式：2023年08月08日 星期一）
onMounted(() => {
  const date = new Date()
  const year = date.getFullYear()
  const month = String(date.getMonth() + 1).padStart(2, '0')
  const day = String(date.getDate()).padStart(2, '0')
  const weekdays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']

  currentDate.value = `${year}年${month}月${day}日`
  currentWeekday.value = weekdays[date.getDay()]
})
</script>

<style scoped>
.workbench {
  max-width: 1400px;
  margin: 0 auto;
  padding: 24px;
  background-color: #f5f7fa;
  min-height: 100vh;
  box-sizing: border-box;
}

/* 头部布局：左右分布，垂直居中 */
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
}

.date-info {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  color: #303133;
}

.calendar-icon {
  font-size: 20px;
  color: #409eff;
}

.user-greeting {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  font-weight: 500;
  color: #303133;
}

/* 摘要卡片样式 */
.summary-card {
  margin-bottom: 24px;
  border-radius: 12px;
  border: none;
  background: linear-gradient(145deg, #ecf5ff, #d9ecff);
}

.summary-content {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 16px;
  color: #2c3e50;
  padding: 4px 0;
}

.summary-content .el-icon {
  font-size: 24px;
  color: #409eff;
}

/* 统计卡片通用样式 */
.stat-card {
  border-radius: 16px;
  border: none;
  transition: all 0.3s ease;
  margin-bottom: 20px;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.06) !important;
}

/* 大卡片 (待我审批 / 指派给我的) */
.stat-card.large {
  background: linear-gradient(145deg, #ffffff, #fafcff);
  text-align: center;
  padding: 16px 0;
}

.stat-card.large .stat-title {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 18px;
  color: #606266;
  margin-bottom: 16px;
}

.stat-card.large .stat-title .el-icon {
  font-size: 26px;
  color: #409eff;
}

.stat-card.large .stat-value {
  font-size: 48px;
  font-weight: 600;
  color: #303133;
  line-height: 1.2;
}

/* 下方小卡片内的标题与图标 */
.stat-title-with-icon {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 15px;
  color: #606266;
  white-space: nowrap;
}

.stat-title-with-icon .el-icon {
  font-size: 18px;
  color: #409eff;
}

/* 覆盖 el-statistic 内部样式，让数字更突出 */
:deep(.el-statistic__number) {
  font-size: 28px !important;
  font-weight: 600;
  color: #303133;
  margin-top: 8px;
}

/* 响应式调整 */
@media (max-width: 768px) {
  .workbench {
    padding: 16px;
  }
  .stat-card.large .stat-value {
    font-size: 36px;
  }
  :deep(.el-statistic__number) {
    font-size: 24px !important;
  }
  .header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  .user-greeting {
    align-self: flex-end;
  }
}
</style>