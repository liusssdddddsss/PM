<template>
  <div class="project-statistics-list">
    <ul>
      <li
          v-for="(item, index) in projectList",
          :key="index"
          :class="{ active: index === activeIndex }"
      >
        {{item.name}}
        <el-icon v-if="index === activeIndex" class="arrow-icon"><ArrowRight /></el-icon>
      </li>
    </ul>
  </div>
</template>

<script setup>
import {ref} from "vue";
import { ArrowRight } from "@element-plus/icons-vue";

const activeIndex = ref(0);

const projectList = ref([
  {name:'智慧教室_智慧云盘'},
  {name:'实践教学管理平台'},
  {name:'电子班牌管理系统'},
  {name:'智慧校园(中学版)'},
  {name:'宿舍管理系统'},
  {name:'教务考试系统'},
])
</script>

<style scoped>
.project-statistics-list {
  width: 150px;
  border-right: 1px solid #ebeef5;
  padding-right: 15px;
  margin-right: 20px;
}

.project-statistics-list ul {
  list-style: none;
  padding: 0;
  margin: 0;
}

.project-statistics-list li {
  padding: 10px 0;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  display: flex;
  justify-content: space-between;
  align-items: center;
  transition: all 0.3s;
}

.project-statistics-list li:hover {
  color: #409EFF;
}

.project-statistics-list li.active {
  color: #409EFF;
  font-weight: 500;
}

.arrow-icon {
  font-size: 12px;
}
</style>