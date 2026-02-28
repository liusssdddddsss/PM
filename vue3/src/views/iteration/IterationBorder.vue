<template>
  <div class="border-comment">
    <div class="addProduct">
      <el-button class="button">
        添加产品
      </el-button>
    </div>
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
            <p>进行中的产品</p>
            <ul class="item-list">
              <li v-for="(item,index) in ingProjectList " :key="index">
                {{item}}
              </li>
            </ul>
          </div>
          <div class="right">
            <p>进行中的任务</p>
            <ul class="item-list">
              <li v-for="(item,index) in ingTaskList " :key="index">
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
import {ref} from "vue";

const ingProjectList = ref([
  '智慧教室 智慧云盘',
  '实践教学管理平台',
  '电子班牌管理系统',
  '宿舍管理系统',
  '在线试卷批改',
  '智慧园区OA办公系统'
]);

const ingTaskList = ref([
  '家长端，界面优化调整，新增功能：授权监...',
  '班牌PC端管理界面调整，样式统一，菜单归类',
  '班牌模板调整，参考海康，增加竖版',
  '家校互通留言台'
]);

const noBeginList = ref([
  '电子班牌管理系统',
  '宿舍管理系统',
  '在线试卷批改',
  '智慧园区OA办公系统'
]);

const closeList = ref([
  '实践教学管理平台',
  '电子班牌管理系统',
  '宿舍管理系统'
]);
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