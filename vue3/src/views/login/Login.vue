<template>
  <div class="login">
<!--    左侧图片-->
    <div class="left">
      <div class="brand">
        <h1>项目管理系统</h1>

      </div>
    </div>
<!--    右侧登录-->
    <div class="right">
      <div class="login-card">
        <h2>登录您的账户</h2>
        <form @submit.prevent="handleLogin">
<!--          账户-->
          <div class="login-form">
            <label for="username">
              <psan>账户</psan>
            </label>
<!--            autocomplete用户再次访问同一表单时，浏览器可以直接填充姓名、地址、邮箱、密码等，无需重复输入。-->
            <input
                type="text"
                id="username"
                v-model="form.username"
                placeholder="请输入账户"
                autocomplete="username">

          </div>
          <div class="form-group">
            <label for="password">
              <span>密码</span>
            </label>
            <input
                type="password"
                id="password"
                v-model="form.password"
                placeholder="请输入密码"
                autocomplete="current-password"
            >
          </div>
<!--          自动登录-->
          <div class="login-options">
            <labe class="checkbox">
              <input
                  type="checkbox"
                  v-model="form.autoLogin"
              >
              <span>自动登录</span>
            </labe>
            <a href="#" class="forgot-password">忘记密码</a>
          </div>

<!--          登录-->
          <el-button type="submit" class="login-button" @click="goToWorkbench">登录</el-button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import {reactive} from "vue";
import request from "@/utils/request.js";
import {useRouter} from "vue-router";

request.get('/').then(res => {
  console.log(res);
})
// 表单数据
const form = reactive({
  username:'',
  password:'',
  autoLogin:false,
})
// 登录处理
const handleLogin = async () => {
  console.log('登录信息',{
    账户:form.username,
    密码:form.password,
    自动登录:form.autoLogin
  })
}
const router = useRouter();
const goToWorkbench = () =>{
  router.push('/workbench/dashboard');
}
</script>

<style scoped>
.login{
  display: flex;
  width: 100%;
  min-height: 100vh;
  background-color: #F6FBFF;
}
.left{
  flex: 1;
  background-color: blue;
  color: white;
}
.right{
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}
.login-card{
  width: 100%;
  max-width: 440px;
  background-color: white;
  border-radius: 32px;
  box-shadow: 0 2px 8px -8px;
  padding: 3rem;
  text-align: center;
}
h2{
  color: #1e293b;
  font-size: 2rem;
}
.login-options{
  display: flex;
  justify-content: space-between;
}
</style>