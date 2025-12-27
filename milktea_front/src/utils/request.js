import axios from 'axios'

const BASE_URL = '/api/v1/app'
const COMMON_BASE_URL = '/api/common'

const service = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

const commonService = axios.create({
  baseURL: COMMON_BASE_URL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 如果 url 是以 /api/admin 开头的，说明是管理端接口，不应该拼接 /v1/app
    if (config.url.startsWith('/api/admin')) {
      config.baseURL = ''
    }
    console.log(`请求地址: ${config.baseURL || ''}${config.url}`)
    const token = localStorage.getItem('token')
    if (token && token !== 'undefined' && token !== 'null') {
      // 尝试多种常见的 Token 传递方式
      config.headers['Authorization'] = `Bearer ${token}`
      config.headers['token'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 公共服务的请求拦截器
commonService.interceptors.request.use(
  config => {
    console.log(`公共请求地址: ${config.baseURL || ''}${config.url}`)
    const token = localStorage.getItem('token')
    if (token && token !== 'undefined' && token !== 'null') {
      config.headers['Authorization'] = `Bearer ${token}`
      config.headers['token'] = token
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
service.interceptors.response.use(
  response => {
    const res = response.data
    // 统一处理：后端 ApiResponse 结构包含 code, message, data
    if (res.code === 200 || res.status === 'success') {
      // 返回完整 res 以便页面判断 code，或者根据约定只返回 data
      // 为了兼容 index.vue 中的 if (res.code === 200)，这里返回 res
      return res
    } else {
      handleBusinessError(res)
      const error = new Error(res.message || '业务请求失败')
      error.response = response // 挂载原始响应
      return Promise.reject(error)
    }
  },
  error => {
    if (axios.isCancel(error)) {
      console.log('Request canceled', error.message)
      return Promise.reject(error)
    }
    handleHttpError(error.response)
    return Promise.reject(error)
  }
)

// 公共服务的响应拦截器
commonService.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200 || res.status === 'success' || !res.code) {
      return res.data || res
    } else {
      handleBusinessError(res)
      return Promise.reject(new Error(res.message || 'Error'))
    }
  },
  error => {
    if (axios.isCancel(error)) {
      console.log('Request canceled', error.message)
      return new Promise(() => {})
    }
    handleHttpError(error.response)
    return Promise.reject(error)
  }
)

let isRedirecting = false

function handleBusinessError(data) {
  const errorMap = {
    1001: '参数错误',
    1002: '认证失败，请重新登录',
    1003: '权限不足',
    1004: '资源不存在'
  }
  const message = errorMap[data.code] || data.message || '请求失败'
  console.error('业务错误:', message)
  
  if (data.code === 1002) {
    handleUnauthorized()
  } else {
    // 避免在登录页频繁弹窗
    if (!window.location.pathname.includes('/login')) {
      alert(message)
    }
  }
}

function handleUnauthorized() {
  if (isRedirecting) return
  isRedirecting = true
  
  localStorage.removeItem('token')
  localStorage.removeItem('userInfo')
  
  // 只有不在登录页时才跳转
  if (!window.location.pathname.includes('/login')) {
    alert('登录已过期，请重新登录')
    window.location.href = '/login?redirect=' + encodeURIComponent(window.location.pathname + window.location.search)
  }
  
  setTimeout(() => {
    isRedirecting = false
  }, 2000)
}

function handleHttpError(response) {
  if (!response) {
    console.error('网络连接失败')
    return
  }
  const errorMap = {
    400: '请求参数错误',
    401: '未授权，请登录',
    403: '权限不足，拒绝访问',
    404: '请求地址不存在',
    500: '服务器内部错误'
  }
  const message = errorMap[response.status] || `请求失败: ${response.status}`
  console.error('HTTP错误:', message, response.config.url)
  
  if (response.status === 401) {
    handleUnauthorized()
  } else if (response.status === 403) {
    // 403 权限不足，仅提示，不强制登出
    // 除非是登录接口报错（通常不会报403，除非被封禁）
    if (!response.config.url.includes('/auth/login')) {
      console.warn('检测到权限不足请求，已拦截登出逻辑')
    }
  }
}

export const get = (url, params) => service.get(url, { params })
export const post = (url, data) => service.post(url, data)
export const put = (url, data) => service.put(url, data)
export const del = (url) => service.delete(url)

export const commonGet = (url, params) => commonService.get(url, { params })
export const commonPost = (url, data) => commonService.post(url, data)
export const commonPut = (url, data) => commonService.put(url, data)
export const commonDel = (url) => commonService.delete(url)

export const uploadFile = (url, file, params = {}) => {
  const formData = new FormData()
  formData.append('file', file)
  Object.keys(params).forEach(key => {
    if (params[key] !== undefined && params[key] !== null) {
      formData.append(key, params[key])
    }
  })
  return commonService.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    },
    transformRequest: [(data) => data]
  })
}

export default service