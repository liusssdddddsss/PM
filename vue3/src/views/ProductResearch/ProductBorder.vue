<template>
  <div class="border-comment">
    <div class="addProduct">
      <el-button class="button" @click="goToProductEdit">
        添加产品
      </el-button>
    </div>
    <div class="comment">
      <div class="ing">
        <div class="ing-title">
          <p>进行中({{ingCount}})</p>
        </div>
        <div class="ing-com">
          <div class="left">
            <p>进行中的产品</p>
            <ul class="item-list">
              <li v-for="(item,index) in ingList " :key="index">
                {{item}}
              </li>
            </ul>
          </div>
        </div>
      </div>
      <div class="no-begin">
        <p>未开始({{noBeginCount}})</p>
        <ul class="item-list">
          <li v-for="(item,index) in noBeginList " :key="index">
            {{item}}
          </li>
        </ul>
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
import {useRouter} from "vue-router";
import request from "@/utils/request";

const router = useRouter();
const goToProductEdit = () => {
  router.push('/productResearch/productEdit');
};

// 产品数据
const ingList = ref([]);
const noBeginList = ref([]);
const closeList = ref([]);

// 计算属性
const ingCount = computed(() => ingList.value.length);
const noBeginCount = computed(() => noBeginList.value.length);
const closeCount = computed(() => closeList.value.length);

// 从后端获取产品数据
onMounted(() => {
  fetchProductData();
});

const fetchProductData = async () => {
  try {
    // 获取所有产品列表
    const productResponse = await request.get('/api/products');
    if (productResponse.data.code === 200) {
      const products = productResponse.data.data;
      console.log('获取到的产品数据:', products);
      
      // 根据状态分类产品
      // status: 0=未开始, 1=进行中, 2=已关闭
      noBeginList.value = products
        .filter(product => product.status === 0)
        .map(product => product.name);
      
      ingList.value = products
        .filter(product => product.status === 1)
        .map(product => product.name);
      
      closeList.value = products
        .filter(product => product.status === 2)
        .map(product => product.name);
    }
  } catch (error) {
    console.error('获取产品数据失败:', error);
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
