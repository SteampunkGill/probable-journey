/**
 * 格式化价格
 * @param {number} price - 价格
 * @returns {string} 格式化后的价格字符串
 */
const formatPrice = (price) => {
  if (price === undefined || price === null) return '¥0.00';
  return '¥' + parseFloat(price).toFixed(2);
}

/**
 * 格式化距离
 * @param {number} distance - 距离(米)
 * @returns {string} 格式化后的距离
 */
const formatDistance = (distance) => {
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
const formatDate = (date, format = 'YYYY-MM-DD HH:mm') => {
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
const getRelativeTime = (date) => {
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
 * 验证手机号
 * @param {string} phone - 手机号
 * @returns {boolean} 是否有效
 */
const validatePhone = (phone) => {
  const reg = /^1[3-9]\d{9}$/;
  return reg.test(phone);
}

/**
 * 计算两个坐标之间的距离
 * @param {number} lat1 - 纬度1
 * @param {number} lng1 - 经度1
 * @param {number} lat2 - 纬度2
 * @param {number} lng2 - 经度2
 * @returns {number} 距离(米)
 */
const getDistance = (lat1, lng1, lat2, lng2) => {
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
 * 格式化图片URL
 * @param {string} url - 原始URL
 * @returns {string} 格式化后的完整URL
 */
const formatImageUrl = (url) => {
  if (!url) return '';
  if (url.startsWith('http') || url.startsWith('data:')) return url;
  const baseUrl = 'http://localhost:8080'; // 与小程序 request.js 保持一致
  const path = url.startsWith('/') ? url : `/${url}`;
  return `${baseUrl}${path}`;
}

module.exports = {
  formatPrice,
  formatDistance,
  formatDate,
  getRelativeTime,
  validatePhone,
  getDistance,
  formatImageUrl
}