import axios from 'axios';

const request = axios.create({
    baseURL: 'http://localhost:9090',
    timeout: 30000,//后台接口超时时间
})
//request拦截器
// request.interceptors.response.use(config => {
//     config.headers['Content-Type'] = 'application/json;charset=UTF-8';
//     return config;
// }),error => {
//     return Promise.reject(error);
// }

// response拦截器
request.interceptors.response.use(
    response=>{
        return response.data;
    },
    error => {
        return Promise.reject(error);
    }
)
// 默认导出
export default request;