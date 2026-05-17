<template>
  <div class="border-comment">
    <div class="comment">
      <div class="no-begin">
        <p>未开始({{noBeginCount}})</p>
        <ul class="item-list">
          <li v-for="(item,index) in noBeginList " :key="index">
            {{item}}
          </li>
        </ul>
      </div>
      <div class="ing">
        <div class="ing-title">
          <p>进行中({{ingCount}})</p>
        </div>
        <div class="ing-com">
          <div class="left">
            <p>进行中的迭代</p>
            <ul class="item-list">
              <li v-for="(item,index) in ingList " :key="index">
                {{item}}
              </li>
            </ul>
          </div>
        </div>
      </div>

      <div class="close">
        <p>已关闭({{closeCount}})</p>
        <ul class="item-list">
          <li v-for="(item,index) in closeList " :key="index">
            {{item}}
          </li>
        </ul>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, computed, onMounted} from "vue";
import request from "@/utils/request";

// 迭代数据
const ingList = ref([]);
const noBeginList = ref([]);
const closeList = ref([]);

// 计算属性
const ingCount = computed(() => ingList.value.length);
const noBeginCount = computed(() => noBeginList.value.length);
const closeCount = computed(() => closeList.value.length);

// 从后端获取迭代数据
onMounted(() => {
  fetchIterationData();
});

const fetchIterationData = async () => {
  try {
    // 获取所有迭代列表
    const iterationResponse = await request.get('/iteration/list');
    if (iterationResponse.data.code === 200) {
      const iterations = iterationResponse.data.data;
      console.log('获取到的迭代数据:', iterations);
      
      // 根据状态分类迭代
      // status: 0=未开始, 1=进行中, 2=已关闭
      noBeginList.value = iterations
        .filter(iteration => iteration.status === 0)
        .map(iteration => iteration.name);
      
      ingList.value = iterations
        .filter(iteration => iteration.status === 1)
        .map(iteration => iteration.name);
      
      closeList.value = iterations
        .filter(iteration => iteration.status === 2)
        .map(iteration => iteration.name);
    }
  } catch (error) {
    console.error('获取迭代数据失败:', error);
  }
};
</script>

<style scoped>
.border-comment{
  width: 100%;
  min-height: 100vh;
  padding: 20px;
  background-color: #fff;
  text-align: center;
}
.addProduct{
  float: right;
  margin-right: 20px;
  margin-bottom: 20px;
}
.button{
  background-color: #238EFF;
  color: #fff;
}

.comment{
  display: flex;
  gap:20px;
  width: 100%;
}
.comment p{
  height: 30px;
  line-height: 30px;
}
.item-list {
  list-style: none;
  padding: 0;
  margin: 0;
  background-color: #FAFAFA;
  height: 500px;
  padding-top: 10px;
}
.item-list li {
  width: 80%;
  margin: auto;
  padding: 8px;
  border-left: 5px solid #409eff;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.ing{
  flex: 2;
}
.ing p{
  background-color: #E8FFFF;
}
.ing-com{
  display: flex;
}
.left{
  flex: 1;
}
.right{
  flex: 1;
  margin-left: 2px;
}
.no-begin{
  flex: 1;
}
.no-begin p{
  background-color: #EEF6FF;
  height: 60px;
  line-height: 60px;
}
.close{
  flex: 1;
}
.close p{
  background-color: #FFF1F1;
  height: 60px;
  line-height: 60px;
}
</style>