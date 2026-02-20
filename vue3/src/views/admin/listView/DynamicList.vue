<template>
  <div class="dynamic-list">
    <ul>
      <li class="dynamic-item"
           v-for="(item,index) in dynamicData"
           :key="index"
      >
        <div class="dynamic-time">
          {{item.time}}
        </div>
        <div class="dynamic-message">
          {{item.operator}}{{item.action}}
          <span class="dynamic-link" v-if="item.link">
                  {{item.link}}
        </span>
        </div>
      </li>
    </ul>
  </div>
</template>

<script setup>

// 最新动态数据
import {ref} from "vue";

const dynamicData = ref([
  {
    time: '08月08日 12:12',
    operator: '张三三',
    action: '审批了研发需求',
    link: '班牌模板调整，参考海康，增加竖版'
  },
  {
    time: '08月08日 12:12',
    operator: '王麻子',
    action: '确认通过研发需求',
    link: '家校互通留言'
  }
])

</script>

<style scoped>
.dynamic-list{
  display: flex;
  flex-direction: column;
  gap: 16px;
}
.dynamic-item {
  display: flex;
  align-items: flex-start;
  gap: 10px;
  border-bottom: 1px solid #f0f0f0;
}
.dynamic-item:last-child {
  border-bottom: none;
}
.dynamic-time {
  color: #909399;
  white-space: nowrap;
}
.dynamic-link {
  color: #409eff;
  cursor: pointer;
}
.dynamic-link:hover {
  text-decoration: underline;
}
</style>