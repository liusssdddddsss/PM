<template>
  <div class="project-list-container">
    <ul class="project-list">
      <li 
        v-for="(project, index) in projectList" 
        :key="index"
        :class="{ active: index === activeIndex }"
        @click="handleProjectClick(index, project.name)"
      >
        {{ project.name }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import {ref, defineEmits} from "vue";

const emit = defineEmits(['project-click']);
const activeIndex = ref(0);

const projectList = ref([
  {name:'智慧教室_智慧云盘'},
  {name:'实践教学管理平台'},
  {name:'电子班牌管理系统'},
  {name:'智慧校园(中学版)'},
  {name:'宿舍管理系统'},
  {name:'教务考试系统'},
]);

const handleProjectClick = (index, projectName) => {
  activeIndex.value = index;
  // 向父组件发送事件
  emit('project-click', projectName);
};
</script>

<style scoped>
.project-list-container {
  width: 100%;
}

.project-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.project-list li {
  padding: 10px 0;
  cursor: pointer;
  font-size: 14px;
  color: #409EFF;
  font-weight: 500;
  transition: all 0.3s ease;
  padding-left: 10px;
  border-radius: 4px;
}

.project-list li:hover {
  background-color: #ecf5ff;
}

.project-list li.active {
  background-color: #ecf5ff;
  border-left: 3px solid #409EFF;
}
</style>