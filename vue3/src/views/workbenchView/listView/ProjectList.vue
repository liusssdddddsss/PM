<template>
  <div class="project-statistics-list">
    <ul>
      <li
          v-for="item in projectList",
          :key="item"
      >
        {{item.name}}
      </li>
    </ul>
  </div>
</template>

<script setup>
import {ref} from "vue";

const projectList = ref([
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
])
</script>

<style scoped>

</style>