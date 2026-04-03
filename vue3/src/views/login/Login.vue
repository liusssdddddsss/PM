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
              <span>账户</span>
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
            <label class="checkbox">
              <input
                  type="checkbox"
                  v-model="form.autoLogin"
              >
              <span>自动登录</span>
            </label>
            <a href="#" class="forgot-password">忘记密码</a>
          </div>

<!--          登录-->
          <el-button type="primary" native-type="submit" class="login-button">登录</el-button>
        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import {reactive, onMounted} from "vue";
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

// 页面加载时，检查本地存储中是否有保存的用户名和密码
onMounted(() => {
  const savedUser = localStorage.getItem('autoLoginUser');
  if (savedUser) {
    const user = JSON.parse(savedUser);
    form.username = user.username;
    form.password = user.password;
    form.autoLogin = true;
  }
});
// 登录处理
const handleLogin = async () => {
  try {
    console.log('登录请求数据:', {
      username: form.username,
      password: form.password
    });
    
    // 构建FormData对象
    const formData = new FormData();
    formData.append('username', form.username);
    formData.append('password', form.password);
    
    // 发送登录请求
    console.log('发送登录请求到:', '/admin/login');
    const response = await request.post('/admin/login', formData);
    console.log('登录响应:', response);
    
    if (response.data.code === 200) {
      const user = response.data.data;
      console.log('登录成功，用户信息:', user);
      // 存储用户信息到本地存储
      localStorage.setItem('user', JSON.stringify(user));
      
      // 如果勾选了自动登录，保存用户名和密码到本地存储
      if (form.autoLogin) {
        localStorage.setItem('autoLoginUser', JSON.stringify({
          username: form.username,
          password: form.password
        }));
      } else {
        // 如果未勾选自动登录，清除本地存储中的自动登录信息
        localStorage.removeItem('autoLoginUser');
      }
      
      // 根据 is_admin 字段判断是否为管理员并跳转
      if (user.is_admin === 1) {
        await router.push('/admin');
      } else {
        await router.push('/workbench/dashboard');
      }
    } else {
      console.log('登录失败，错误信息:', response.data.msg);
      alert(response.data.msg);
    }
  } catch (error) {
    console.error('登录失败:', error);
    alert('登录失败，请检查网络连接');
  }
}
const router = useRouter();
</script>

<style scoped>
.login {
  display: flex;
  width: 100%;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
}

.left {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
  padding: 2rem;
  background: rgba(0, 0, 0, 0.1);
}

.brand {
  text-align: center;
}

.brand h1 {
  font-size: 2.5rem;
  font-weight: 700;
  margin-bottom: 1rem;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.brand p {
  font-size: 1.2rem;
  opacity: 0.9;
  max-width: 400px;
  line-height: 1.5;
}

.right {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
}

.login-card {
  width: 100%;
  max-width: 440px;
  background-color: white;
  border-radius: 16px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
  padding: 3rem;
  text-align: center;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}

.login-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.15);
}

h2 {
  color: #1e293b;
  font-size: 1.8rem;
  font-weight: 600;
  margin-bottom: 2rem;
}

.login-form,
.form-group {
  margin-bottom: 1.5rem;
  text-align: left;
}

.login-form label,
.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #475569;
  font-size: 0.9rem;
}

.login-form input,
.form-group input {
  width: 100%;
  padding: 0.875rem 1rem;
  border: 1px solid #e2e8f0;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease, box-shadow 0.3s ease;
  background-color: #f8fafc;
}

.login-form input:focus,
.form-group input:focus {
  outline: none;
  border-color: #667eea;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  background-color: white;
}

.login-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
  font-size: 0.9rem;
}

.checkbox {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  color: #475569;
}

.checkbox input[type="checkbox"] {
  width: auto;
  accent-color: #667eea;
}

.forgot-password {
  color: #667eea;
  text-decoration: none;
  transition: color 0.3s ease;
}

.forgot-password:hover {
  color: #764ba2;
  text-decoration: underline;
}

.login-button {
  width: 100%;
  padding: 0.875rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: background 0.3s ease, transform 0.2s ease;
  margin-top: 1rem;
}

.login-button:hover {
  background: linear-gradient(135deg, #764ba2 0%, #667eea 100%);
  transform: translateY(-2px);
}

.login-button:active {
  transform: translateY(0);
}

@media (max-width: 768px) {
  .login {
    flex-direction: column;
  }

  .left {
    flex: none;
    padding: 2rem;
  }

  .right {
    flex: none;
    padding: 2rem;
  }

  .login-card {
    padding: 2rem;
  }
}
</style>