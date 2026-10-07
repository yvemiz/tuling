import axios from 'axios'
import { message } from 'ant-design-vue'

const service = axios.create({
  baseURL: '/api',
  timeout: 30000
})

// Request interceptor
service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('CLIENT_TOKEN')
    if (token) {
      config.headers['token'] = token
      config.headers['token-c'] = token
      config.headers['clientToken'] = token
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor
service.interceptors.response.use(
  (response) => {
    const res = response.data
    // Snowy API CommonResult contract: { code: 200, msg: "...", data: ... }
    if (res.code !== 200) {
      if (res.code === 401 || res.code === 1011007 || res.code === 1011008) {
        localStorage.removeItem('CLIENT_TOKEN')
        localStorage.removeItem('CLIENT_USER_INFO')
        localStorage.removeItem('CLIENT_ROLE_INFO')
        localStorage.removeItem('CLIENT_ACTIVE_ROLE')
        message.warning('登录已过期，请重新登录')
        if (window.location.pathname !== '/login') {
          window.location.href = '/login'
        }
      } else {
        message.error(res.msg || '操作请求失败')
      }
      return Promise.reject(new Error(res.msg || 'Error'))
    } else {
      return res.data
    }
  },
  (error) => {
    console.error('Request Error: ', error)
    const errorMsg = error.response?.data?.msg || error.message || '网络连接异常，请检查后端服务'
    message.error(errorMsg)
    return Promise.reject(error)
  }
)

export default service
