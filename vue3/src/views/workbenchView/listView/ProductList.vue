<template>
  <div class="product-statistics-list">
    <ul>
      <li
          v-for="item in productList",
          :key="item"
      >
        {{item.name}}
      </li>
    </ul>
  </div>
</template>

<script setup>
import {ref} from "vue";

const productList = ref([
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
  {name:'智慧教室'},
])
</script>

<style scoped>

</style>