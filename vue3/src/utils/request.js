import axios from 'axios';

const request = axios.create({
    baseURL: 'http://localhost:8080',//直接指向后端服务地址
    timeout: 30000,//后台接口超时时间
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