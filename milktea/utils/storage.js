/**
 * 存储键名常量
 */
const STORAGE_KEYS = {
  TOKEN: 'token',
  USER_INFO: 'userInfo',
  CART: 'cart',
  ADDRESSES: 'addresses',
  SEARCH_HISTORY: 'searchHistory',
  FAVORITE_PRODUCTS: 'favoriteProducts',
  FAVORITE_STORES: 'favoriteStores',
  NOTIFICATION_SETTINGS: 'notificationSettings',
  LAST_LOCATION: 'lastLocation'
};

/**
 * 设置存储数据
 */
const setStorage = (key, data) => {
  try {
    wx.setStorageSync(key, data);
    return true;
  } catch (e) {
    console.error(`存储数据失败: ${key}`, e);
    return false;
  }
};

/**
 * 获取存储数据
 */
const getStorage = (key) => {
  try {
    return wx.getStorageSync(key);
  } catch (e) {
    console.error(`获取数据失败: ${key}`, e);
    return null;
  }
};

/**
 * 删除存储数据
 */
const removeStorage = (key) => {
  try {
    wx.removeStorageSync(key);
    return true;
  } catch (e) {
    console.error(`删除数据失败: ${key}`, e);
    return false;
  }
};

/**
 * 清空所有存储
 */
const clearStorage = () => {
  try {
    wx.clearStorageSync();
    return true;
  } catch (e) {
    console.error('清空存储失败', e);
    return false;
  }
};

/**
 * 购物车相关
 */
const getCart = () => getStorage(STORAGE_KEYS.CART) || [];
const saveCart = (cart) => setStorage(STORAGE_KEYS.CART, cart);

/**
 * 搜索历史相关
 */
const getSearchHistory = (limit = 10) => {
  const history = getStorage(STORAGE_KEYS.SEARCH_HISTORY) || [];
  return history.slice(0, limit);
};

const addSearchHistory = (keyword) => {
  if (!keyword || !keyword.trim()) return;
  let history = getStorage(STORAGE_KEYS.SEARCH_HISTORY) || [];
  history = history.filter(item => item !== keyword);
  history.unshift(keyword);
  if (history.length > 20) history = history.slice(0, 20);
  setStorage(STORAGE_KEYS.SEARCH_HISTORY, history);
};

const clearSearchHistory = () => removeStorage(STORAGE_KEYS.SEARCH_HISTORY);

/**
 * 用户信息相关
 */
const getUserInfo = () => getStorage(STORAGE_KEYS.USER_INFO);
const saveUserInfo = (userInfo) => setStorage(STORAGE_KEYS.USER_INFO, userInfo);

/**
 * 收藏相关
 */
const getFavoriteProducts = () => getStorage(STORAGE_KEYS.FAVORITE_PRODUCTS) || [];
const isProductFavorite = (productId) => getFavoriteProducts().includes(productId);
const toggleFavoriteProduct = (productId) => {
  let favorites = getFavoriteProducts();
  const index = favorites.indexOf(productId);
  if (index > -1) {
    favorites.splice(index, 1);
  } else {
    favorites.push(productId);
  }
  setStorage(STORAGE_KEYS.FAVORITE_PRODUCTS, favorites);
  return index === -1; // 返回是否添加成功
};

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
  getFavoriteProducts,
  isProductFavorite,
  toggleFavoriteProduct
};