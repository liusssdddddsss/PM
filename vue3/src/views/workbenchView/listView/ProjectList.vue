<template>
  <div class="project-list-container">
    <ul class="project-list">
      <li 
        v-for="(project, index) in projectList" 
        :key="project.id"
        :class="{ active: index === activeIndex }"
        @click="handleProjectClick(index, project)"
      >
        {{ project.name }}
      </li>
    </ul>
  </div>
</template>

<script setup>
import {ref, defineEmits, onMounted} from "vue";
import request from "@/utils/request.js";

const emit = defineEmits(['project-click']);
const activeIndex = ref(0);

const projectList = ref([]);

// 从后端获取项目列表数据
const fetchProjects = async () => {
  try {
    // 从后端获取所有项目列表
    const response = await request.get('/workbench/all-projects');
    console.log('获取项目列表响应:', response);
    if (response.data.code === 200) {
      projectList.value = response.data.data.map(project => ({
        id: project.id,
        name: project.title
      }));
      console.log('转换后的项目列表数据:', projectList.value);
    }
  } catch (error) {
    console.error('获取项目列表失败:', error);
  }
};

const handleProjectClick = (index, project) => {
  activeIndex.value = index;
  // 向父组件发送事件，传递项目对象（包含id和name）
  emit('project-click', project);
};

// 页面加载时获取项目列表
onMounted(async () => {
  await fetchProjects();
});
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