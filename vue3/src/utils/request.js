import axios from 'axios';

const request = axios.create({
    baseURL: 'https://4b324173af1d83e3-47-99-144-233.serveousercontent.com',
    timeout: 10000,
})
//request拦截器
request.interceptors.request.use(config => {
    // 如果是FormData，让浏览器自动设置Content-Type（包含boundary）
    if (config.data instanceof FormData) {
        // 删除任何手动设置的Content-Type，让浏览器自动处理
        delete config.headers['Content-Type'];
    } else if (!config.headers['Content-Type']) {
        // 只有当Content-Type未设置且不是FormData时，才设置为application/json
        config.headers['Content-Type'] = 'application/json;charset=UTF-8';
    }
    
    // 从本地存储获取JWT令牌
    const token = localStorage.getItem('token');
    if (token) {
        // 在请求头中携带JWT令牌
        config.headers['Authorization'] = 'Bearer ' + token;
    }
    
    return config;
}, error => {
    return Promise.reject(error);
})

// response拦截器
request.interceptors.response.use(
    response=>{
        return response;
    },
    error => {
        return Promise.reject(error);
    }
)
// 默认导出
export default request;