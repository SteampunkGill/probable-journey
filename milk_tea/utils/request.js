// utils/request.js
const BASE_URL = 'http://localhost:8081/api/v1/app';

const request = (url, method = 'GET', data = {}, header = {}) => {
  return new Promise((resolve, reject) => {
    const app = getApp();
    const token = app.globalData.token;
    
    const defaultHeader = {
      'Content-Type': 'application/json',
      'Authorization': token ? `Bearer ${token}` : '',
      'X-Client-Version': '1.0.0',
      'X-Timestamp': Date.now().toString()
    };
    
    wx.request({
      url: `${BASE_URL}${url}`,
      method,
      data,
      header: { ...defaultHeader, ...header },
      success: (res) => {
        if (res.statusCode >= 200 && res.statusCode < 300) {
          if (res.data.code === 200) {
            resolve(res.data.data);
          } else {
            // 业务错误
            handleBusinessError(res.data);
            reject(res.data);
          }
        } else {
          // HTTP错误
          handleHttpError(res);
          reject(res);
        }
      },
      fail: (err) => {
        // 网络错误
        handleNetworkError(err);
        reject(err);
      },
      complete: () => {
        app.hideLoading();
      }
    });
  });
};

// 错误处理
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
  };
  
  const message = errorMap[data.code] || data.message || '请求失败';
  
  wx.showToast({
    title: message,
    icon: 'none',
    duration: 2000
  });
  
  // 如果是认证失败，清除登录状态
  if (data.code === 1002) {
    const app = getApp();
    app.logout();
  }
}

function handleHttpError(res) {
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
  };
  
  const message = errorMap[res.statusCode] || `请求失败: ${res.statusCode}`;
  
  wx.showToast({
    title: message,
    icon: 'none',
    duration: 2000
  });
}

function handleNetworkError(err) {
  wx.showToast({
    title: '网络连接失败，请检查网络',
    icon: 'none',
    duration: 2000
  });
}

// GET请求
const get = (url, data = {}) => request(url, 'GET', data);

// POST请求
const post = (url, data = {}) => request(url, 'POST', data);

// PUT请求
const put = (url, data = {}) => request(url, 'PUT', data);

// DELETE请求
const del = (url, data = {}) => request(url, 'DELETE', data);

// 上传文件
const uploadFile = (url, filePath, formData = {}) => {
  return new Promise((resolve, reject) => {
    const app = getApp();
    const token = app.globalData.token;
    
    wx.uploadFile({
      url: `${BASE_URL}${url}`,
      filePath,
      name: 'file',
      formData: {
        ...formData,
        token
      },
      success: (res) => {
        if (res.statusCode === 200) {
          const data = JSON.parse(res.data);
          if (data.code === 200) {
            resolve(data.data);
          } else {
            handleBusinessError(data);
            reject(data);
          }
        } else {
          handleHttpError(res);
          reject(res);
        }
      },
      fail: (err) => {
        handleNetworkError(err);
        reject(err);
      }
    });
  });
};

module.exports = {
  request,
  get,
  post,
  put,
  delete: del,
  uploadFile
};