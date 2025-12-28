const BASE_URL = 'http://localhost:8080/api/v1/app' // 微信小程序需使用合法域名，开发阶段可勾选不校验域名
const COMMON_BASE_URL = 'http://localhost:8080/api/common'

const request = (options) => {
  return new Promise((resolve, reject) => {
    let url = options.url
    let header = {
      'Content-Type': 'application/json',
      ...options.header
    }

    // 模拟原项目的拦截器逻辑
    if (url.startsWith('/api/admin') || url.startsWith('/api/common')) {
      url = `http://localhost:8080${url}`
    } else if (url.startsWith('/upload/')) {
      url = `http://localhost:8080/api/common${url}`
    } else {
      url = `${BASE_URL}${url}`
    }

    const token = wx.getStorageSync('token')
    if (token && token !== 'undefined' && token !== 'null') {
      header['Authorization'] = `Bearer ${token}`
      header['token'] = token
    }

    wx.request({
      url,
      method: options.method || 'GET',
      data: options.data,
      header,
      success: (res) => {
        const { statusCode, data } = res
        if (statusCode === 200) {
          // 业务逻辑处理
          if (data.code === 200 || data.status === 'success' || options.isCommon) {
            resolve(options.isCommon ? (data.data || data) : data)
          } else {
            handleBusinessError(data)
            reject(new Error(data.message || '业务请求失败'))
          }
        } else if (statusCode === 401) {
          handleUnauthorized()
          reject(new Error('未授权'))
        } else {
          handleHttpError(statusCode)
          reject(new Error(`HTTP错误: ${statusCode}`))
        }
      },
      fail: (err) => {
        wx.showToast({
          title: '网络连接失败',
          icon: 'none'
        })
        reject(err)
      }
    })
  })
}

function handleBusinessError(data) {
  const errorMap = {
    1001: '参数错误',
    1002: '认证失败，请重新登录',
    1003: '权限不足',
    1004: '资源不存在'
  }
  const message = errorMap[data.code] || data.message || '请求失败'
  
  if (data.code === 1002) {
    handleUnauthorized()
  } else {
    wx.showToast({
      title: message,
      icon: 'none'
    })
  }
}

function handleUnauthorized() {
  wx.removeStorageSync('token')
  wx.removeStorageSync('userInfo')
  wx.showModal({
    title: '提示',
    content: '登录已过期，请重新登录',
    showCancel: false,
    success: () => {
      wx.reLaunch({
        url: '/pages/login/login'
      })
    }
  })
}

function handleHttpError(status) {
  const errorMap = {
    400: '请求参数错误',
    401: '未授权，请登录',
    403: '权限不足，拒绝访问',
    404: '请求地址不存在',
    500: '服务器内部错误'
  }
  const message = errorMap[status] || `请求失败: ${status}`
  wx.showToast({
    title: message,
    icon: 'none'
  })
}

export const get = (url, data) => request({ url, data, method: 'GET' })
export const post = (url, data) => request({ url, data, method: 'POST' })
export const put = (url, data) => request({ url, data, method: 'PUT' })
export const del = (url) => request({ url, method: 'DELETE' })

export const commonGet = (url, data) => request({ url, data, method: 'GET', isCommon: true })
export const commonPost = (url, data) => request({ url, data, method: 'POST', isCommon: true })

export default request