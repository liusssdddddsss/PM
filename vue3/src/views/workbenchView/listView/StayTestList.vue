<template>
  <div class="staying-test-list">
    <ul>
      <li
          v-for="item in stayingList",
          :key="item"
      >
        {{item.name}}
      </li>
    </ul>
  </div>
</template>

<script setup>
import {ref} from "vue";

const stayingList = ref([
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
])
</script>

<style scoped>

</style>