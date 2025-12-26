// utils/util.js
const app = getApp();

/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
function formatPrice(price) {
  if (price === undefined || price === null) return '¥0.00';
  return '¥' + parseFloat(price).toFixed(2);
}

/**
 * 格式化距离
 * @param {number} distance - 距离(米)
 * @returns {string} 格式化后的距离
 */
function formatDistance(distance) {
  if (!distance) return '';
  if (distance < 1000) {
    return distance + 'm';
  } else {
    return (distance / 1000).toFixed(1) + 'km';
  }
}

/**
 * 格式化时间
 * @param {string|Date} date - 日期
 * @param {string} format - 格式
 * @returns {string} 格式化后的时间
 */
function formatDate(date, format = 'YYYY-MM-DD HH:mm') {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  const year = d.getFullYear();
  const month = String(d.getMonth() + 1).padStart(2, '0');
  const day = String(d.getDate()).padStart(2, '0');
  const hour = String(d.getHours()).padStart(2, '0');
  const minute = String(d.getMinutes()).padStart(2, '0');
  const second = String(d.getSeconds()).padStart(2, '0');
  
  return format
    .replace('YYYY', year)
    .replace('MM', month)
    .replace('DD', day)
    .replace('HH', hour)
    .replace('mm', minute)
    .replace('ss', second);
}

/**
 * 获取相对时间
 * @param {string|Date} date - 日期
 * @returns {string} 相对时间描述
 */
function getRelativeTime(date) {
  if (!date) return '';
  
  const d = new Date(date);
  if (isNaN(d.getTime())) return '';
  
  const now = new Date();
  const diff = Math.floor((now - d) / 1000);
  
  if (diff < 60) {
    return '刚刚';
  } else if (diff < 3600) {
    return Math.floor(diff / 60) + '分钟前';
  } else if (diff < 86400) {
    return Math.floor(diff / 3600) + '小时前';
  } else if (diff < 604800) {
    return Math.floor(diff / 86400) + '天前';
  } else {
    return formatDate(date, 'MM-DD HH:mm');
  }
}

/**
 * 防抖函数
 * @param {Function} func - 需要防抖的函数
 * @param {number} wait - 等待时间
 * @returns {Function} 防抖后的函数
 */
function debounce(func, wait) {
  let timeout;
  return function executedFunction(...args) {
    const later = () => {
      clearTimeout(timeout);
      func(...args);
    };
    clearTimeout(timeout);
    timeout = setTimeout(later, wait);
  };
}

/**
 * 节流函数
 * @param {Function} func - 需要节流的函数
 * @param {number} limit - 限制时间
 * @returns {Function} 节流后的函数
 */
function throttle(func, limit) {
  let inThrottle;
  return function executedFunction(...args) {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  };
}

/**
 * 深度克隆对象
 * @param {Object} obj - 需要克隆的对象
 * @returns {Object} 克隆后的对象
 */
function deepClone(obj) {
  if (obj === null || typeof obj !== 'object') return obj;
  if (obj instanceof Date) return new Date(obj.getTime());
  if (obj instanceof Array) return obj.map(item => deepClone(item));
  if (typeof obj === 'object') {
    const clonedObj = {};
    for (const key in obj) {
      if (obj.hasOwnProperty(key)) {
        clonedObj[key] = deepClone(obj[key]);
      }
    }
    return clonedObj;
  }
}

/**
 * 验证手机号
 * @param {string} phone - 手机号
 * @returns {boolean} 是否有效
 */
function validatePhone(phone) {
  const reg = /^1[3-9]\d{9}$/;
  return reg.test(phone);
}

/**
 * 验证邮箱
 * @param {string} email - 邮箱
 * @returns {boolean} 是否有效
 */
function validateEmail(email) {
  const reg = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return reg.test(email);
}

/**
 * 生成随机字符串
 * @param {number} length - 长度
 * @returns {string} 随机字符串
 */
function generateRandomString(length = 8) {
  const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';
  let result = '';
  for (let i = 0; i < length; i++) {
    result += chars.charAt(Math.floor(Math.random() * chars.length));
  }
  return result;
}

/**
 * 计算两个坐标之间的距离
 * @param {number} lat1 - 纬度1
 * @param {number} lng1 - 经度1
 * @param {number} lat2 - 纬度2
 * @param {number} lng2 - 经度2
 * @returns {number} 距离(米)
 */
function getDistance(lat1, lng1, lat2, lng2) {
  const radLat1 = (lat1 * Math.PI) / 180.0;
  const radLat2 = (lat2 * Math.PI) / 180.0;
  const a = radLat1 - radLat2;
  const b = (lng1 * Math.PI) / 180.0 - (lng2 * Math.PI) / 180.0;
  let s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2) + Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)));
  s = s * 6378.137;
  s = Math.round(s * 10000) / 10;
  return s;
}

/**
 * 检查是否需要登录
 * @param {boolean} needAuth - 是否需要认证
 * @returns {Promise<boolean>} 是否已登录
 */
async function checkLogin(needAuth = true) {
  const app = getApp();
  
  if (!app.globalData.token || !app.globalData.userInfo) {
    if (needAuth) {
      const result = await app.showConfirm('温馨提示', '此操作需要登录，是否立即登录？');
      if (result) {
        wx.navigateTo({
          url: '/pages/login/login'
        });
      }
      return false;
    }
    return false;
  }
  return true;
}

/**
 * 生成取餐码
 * @returns {string} 取餐码
 */
function generatePickupCode() {
  const letters = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ';
  const numbers = '0123456789';
  let code = '';
  
  // 随机一个字母
  code += letters.charAt(Math.floor(Math.random() * letters.length));
  
  // 随机三个数字
  for (let i = 0; i < 3; i++) {
    code += numbers.charAt(Math.floor(Math.random() * numbers.length));
  }
  
  return code;
}

module.exports = {
  formatPrice,
  formatDistance,
  formatDate,
  getRelativeTime,
  debounce,
  throttle,
  deepClone,
  validatePhone,
  validateEmail,
  generateRandomString,
  getDistance,
  checkLogin,
  generatePickupCode
};