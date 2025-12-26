// utils/storage.js
const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo',
  CART: 'cart',
  ADDRESSES: 'addresses',
  SEARCH_HISTORY: 'searchHistory',
  FAVORITE_PRODUCTS: 'favoriteProducts',
  FAVORITE_STORES: 'favoriteStores',
  NOTIFICATION_SETTINGS: 'notificationSettings',
  LAST_LOCATION: 'lastLocation',
  SELECTED_STORE: 'selectedStore'
};

/**
 * 设置存储数据
 * @param {string} key - 键名
 * @param {any} data - 数据
 * @param {boolean} encrypt - 是否加密
 */
function setStorage(key, data, encrypt = false) {
  try {
    let value = data;
    if (encrypt) {
      value = encryptData(data);
    }
    wx.setStorageSync(key, value);
    return true;
  } catch (error) {
    console.error(`存储数据失败: ${key}`, error);
    return false;
  }
}

/**
 * 获取存储数据
 * @param {string} key - 键名
 * @param {boolean} decrypt - 是否解密
 * @returns {any} 数据
 */
function getStorage(key, decrypt = false) {
  try {
    let value = wx.getStorageSync(key);
    if (decrypt && value) {
      value = decryptData(value);
    }
    return value;
  } catch (error) {
    console.error(`获取数据失败: ${key}`, error);
    return null;
  }
}

/**
 * 删除存储数据
 * @param {string} key - 键名
 */
function removeStorage(key) {
  try {
    wx.removeStorageSync(key);
    return true;
  } catch (error) {
    console.error(`删除数据失败: ${key}`, error);
    return false;
  }
}

/**
 * 清空所有存储
 */
function clearStorage() {
  try {
    wx.clearStorageSync();
    return true;
  } catch (error) {
    console.error('清空存储失败', error);
    return false;
  }
}

/**
 * 获取购物车数据
 * @returns {Array} 购物车数据
 */
function getCart() {
  const cart = getStorage(STORAGE_KEYS.CART) || [];
  return cart;
}

/**
 * 保存购物车数据
 * @param {Array} cart - 购物车数据
 */
function saveCart(cart) {
  return setStorage(STORAGE_KEYS.CART, cart);
}

/**
 * 获取搜索历史
 * @param {number} limit - 限制数量
 * @returns {Array} 搜索历史
 */
function getSearchHistory(limit = 10) {
  const history = getStorage(STORAGE_KEYS.SEARCH_HISTORY) || [];
  return history.slice(0, limit);
}

/**
 * 添加搜索历史
 * @param {string} keyword - 搜索关键词
 */
function addSearchHistory(keyword) {
  if (!keyword.trim()) return;
  
  let history = getStorage(STORAGE_KEYS.SEARCH_HISTORY) || [];
  
  // 移除重复的关键词
  history = history.filter(item => item !== keyword);
  
  // 添加到开头
  history.unshift(keyword);
  
  // 限制数量
  if (history.length > 20) {
    history = history.slice(0, 20);
  }
  
  setStorage(STORAGE_KEYS.SEARCH_HISTORY, history);
}

/**
 * 清空搜索历史
 */
function clearSearchHistory() {
  removeStorage(STORAGE_KEYS.SEARCH_HISTORY);
}

/**
 * 获取用户信息
 * @returns {Object} 用户信息
 */
function getUserInfo() {
  return getStorage(STORAGE_KEYS.USER_INFO);
}

/**
 * 保存用户信息
 * @param {Object} userInfo - 用户信息
 */
function saveUserInfo(userInfo) {
  return setStorage(STORAGE_KEYS.USER_INFO, userInfo);
}

/**
 * 获取地址列表
 * @returns {Array} 地址列表
 */
function getAddresses() {
  return getStorage(STORAGE_KEYS.ADDRESSES) || [];
}

/**
 * 保存地址列表
 * @param {Array} addresses - 地址列表
 */
function saveAddresses(addresses) {
  return setStorage(STORAGE_KEYS.ADDRESSES, addresses);
}

/**
 * 获取收藏的商品
 * @returns {Array} 收藏的商品ID列表
 */
function getFavoriteProducts() {
  return getStorage(STORAGE_KEYS.FAVORITE_PRODUCTS) || [];
}

/**
 * 添加收藏的商品
 * @param {string} productId - 商品ID
 */
function addFavoriteProduct(productId) {
  const favorites = getFavoriteProducts();
  if (!favorites.includes(productId)) {
    favorites.push(productId);
    setStorage(STORAGE_KEYS.FAVORITE_PRODUCTS, favorites);
  }
}

/**
 * 移除收藏的商品
 * @param {string} productId - 商品ID
 */
function removeFavoriteProduct(productId) {
  let favorites = getFavoriteProducts();
  favorites = favorites.filter(id => id !== productId);
  setStorage(STORAGE_KEYS.FAVORITE_PRODUCTS, favorites);
}

/**
 * 检查是否收藏了商品
 * @param {string} productId - 商品ID
 * @returns {boolean} 是否收藏
 */
function isProductFavorite(productId) {
  const favorites = getFavoriteProducts();
  return favorites.includes(productId);
}

/**
 * 获取收藏的门店
 * @returns {Array} 收藏的门店ID列表
 */
function getFavoriteStores() {
  return getStorage(STORAGE_KEYS.FAVORITE_STORES) || [];
}

/**
 * 添加收藏的门店
 * @param {string} storeId - 门店ID
 */
function addFavoriteStore(storeId) {
  const favorites = getFavoriteStores();
  if (!favorites.includes(storeId)) {
    favorites.push(storeId);
    setStorage(STORAGE_KEYS.FAVORITE_STORES, favorites);
  }
}

/**
 * 移除收藏的门店
 * @param {string} storeId - 门店ID
 */
function removeFavoriteStore(storeId) {
  let favorites = getFavoriteStores();
  favorites = favorites.filter(id => id !== storeId);
  setStorage(STORAGE_KEYS.FAVORITE_STORES, favorites);
}

/**
 * 检查是否收藏了门店
 * @param {string} storeId - 门店ID
 * @returns {boolean} 是否收藏
 */
function isStoreFavorite(storeId) {
  const favorites = getFavoriteStores();
  return favorites.includes(storeId);
}

/**
 * 获取通知设置
 * @returns {Object} 通知设置
 */
function getNotificationSettings() {
  const defaultSettings = {
    orderStatus: true,
    promotion: true,
    system: true
  };
  
  const settings = getStorage(STORAGE_KEYS.NOTIFICATION_SETTINGS);
  return { ...defaultSettings, ...settings };
}

/**
 * 更新通知设置
 * @param {Object} settings - 通知设置
 */
function updateNotificationSettings(settings) {
  const currentSettings = getNotificationSettings();
  const newSettings = { ...currentSettings, ...settings };
  setStorage(STORAGE_KEYS.NOTIFICATION_SETTINGS, newSettings);
  return newSettings;
}

/**
 * 获取最后的位置
 * @returns {Object} 位置信息
 */
function getLastLocation() {
  return getStorage(STORAGE_KEYS.LAST_LOCATION);
}

/**
 * 保存最后的位置
 * @param {Object} location - 位置信息
 */
function saveLastLocation(location) {
  return setStorage(STORAGE_KEYS.LAST_LOCATION, location);
}

/**
 * 简单的数据加密（生产环境应使用更安全的加密方式）
 */
function encryptData(data) {
  try {
    const jsonStr = JSON.stringify(data);
    return btoa(encodeURIComponent(jsonStr));
  } catch (error) {
    console.error('加密数据失败', error);
    return data;
  }
}

/**
 * 简单的数据解密
 */
function decryptData(data) {
  try {
    const jsonStr = decodeURIComponent(atob(data));
    return JSON.parse(jsonStr);
  } catch (error) {
    console.error('解密数据失败', error);
    return data;
  }
}

module.exports = {
  STORAGE_KEYS,
  setStorage,
  getStorage,
  removeStorage,
  clearStorage,
  getCart,
  saveCart,
  getSearchHistory,
  addSearchHistory,
  clearSearchHistory,
  getUserInfo,
  saveUserInfo,
  getAddresses,
  saveAddresses,
  getFavoriteProducts,
  addFavoriteProduct,
  removeFavoriteProduct,
  isProductFavorite,
  getFavoriteStores,
  addFavoriteStore,
  removeFavoriteStore,
  isStoreFavorite,
  getNotificationSettings,
  updateNotificationSettings,
  getLastLocation,
  saveLastLocation
};