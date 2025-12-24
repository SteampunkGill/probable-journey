import axios from 'axios'

const BASE_URL = '/api/v1/app'
const COMMON_BASE_URL = '/api/common'

const service = axios.create({
  baseURL: BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

const commonService = axios.create({
  baseURL: COMMON_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
})

// 请求拦截器
service.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    config.headers['X-Timestamp'] = Date.now().toString()
    config.headers['X-Client-Version'] = '1.0.0'
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 公共服务的请求拦截器
commonService.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}`
    }
    config.headers['X-Timestamp'] = Date.now().toString()
    config.headers['X-Client-Version'] = '1.0.0'
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
    if (res.code === 200) {
      return res.data
    } else {
      handleBusinessError(res)
      return Promise.reject(new Error(res.message || 'Error'))
    }
  },
  error => {
    handleHttpError(error.response)
    return Promise.reject(error)
  }
)

// 公共服务的响应拦截器
commonService.interceptors.response.use(
  response => {
    const res = response.data
    if (res.code === 200) {
      return res.data
    } else {
      handleBusinessError(res)
      return Promise.reject(new Error(res.message || 'Error'))
    }
  },
  error => {
    handleHttpError(error.response)
    return Promise.reject(error)
  }
)

function handleBusinessError(data) {
  const errorMap = {
    1001: '参数错误',
    1002: '认证失败，请重新登录',
    1003: '权限不足',
    1004: '资源不存在',
    2001: '商品库存不足',
    2002: '优惠券已过期',
    2003: '订单状态不允许此操作',
    2004: '支付失败，请重试',
    2005: '地址超出配送范围',
    2006: '门店已打烊',
    2007: '积分不足',
    2008: '余额不足',
    2009: '手机号已注册',
    2010: '验证码错误'
  }
  const message = errorMap[data.code] || data.message || '请求失败'
  alert(message)
  if (data.code === 1002) {
    localStorage.removeItem('token')
    window.location.href = '/login'
  }
}

function handleHttpError(response) {
  if (!response) {
    alert('网络连接失败，请检查网络')
    return
  }
  const errorMap = {
    400: '请求参数错误',
    401: '未授权，请登录',
    403: '拒绝访问',
    404: '请求地址不存在',
    405: '请求方法不允许',
    500: '服务器内部错误',
    502: '网关错误',
    503: '服务不可用',
    504: '网关超时'
  }
  const message = errorMap[response.status] || `请求失败: ${response.status}`
  alert(message)
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
    formData.append(key, params[key])
  })
  return commonService.post(url, formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export default service