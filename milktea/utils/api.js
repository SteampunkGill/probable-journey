const { get, post, put, del, commonGet, commonPost } = require('./request.js')

// 用户认证相关
const authApi = {
  wechatLogin: (code, userInfo) => post('/auth/wx-login', { code, userInfo }),
  login: (username, password) => post('/auth/login', { username, password }),
  getUserProfile: () => get('/user/profile'),
  updateUserProfile: (data) => put('/user/profile', data),
  getCardBalance: () => get('/user/card-balance'),
  bindCard: (cardNumber) => post('/user/bind-card', { cardNumber })
}

// 首页相关
const homeApi = {
  getHomeData: () => get('/home/page'),
  getRecommendations: () => get('/home/recommendations'),
  getNotifications: () => get('/home/notifications')
}

// 商品相关
const productApi = {
  getCategories: () => get('/categories'),
  getProducts: (categoryId, params) => get('/products', { categoryId, ...params }),
  getProductDetail: (id) => get(`/products/${id}`),
  searchProducts: (keyword, params) => get('/search', { keyword, ...params }),
  getHotKeywords: () => get('/search/hot')
}

// 购物车相关
const cartApi = {
  getCart: () => get('/cart'),
  addToCart: (data) => post('/cart/add', data),
  updateCartItem: (id, data) => put('/cart/update-quantity', { id, ...data }),
  deleteCartItem: (id) => del(`/cart/remove/${id}`),
  getCheckoutInfo: () => post('/cart/checkout'),
  clearCart: () => del('/cart/clear')
}

// 订单相关
const orderApi = {
  createOrder: (data) => post('/orders/create', data),
  getOrderList: (params) => get('/orders', params),
  getOrderDetail: (orderNo) => get(`/orders/${orderNo}`),
  cancelOrder: (orderNo) => post(`/orders/${orderNo}/cancel`),
  confirmOrder: (orderNo) => post(`/orders/${orderNo}/confirm`),
  remindOrder: (orderNo) => post(`/orders/${orderNo}/remind`)
}

// 地址相关
const addressApi = {
  getAddressList: () => get('/address/list'),
  addAddress: (data) => post('/address/add', data),
  updateAddress: (id, data) => put(`/address/update/${id}`, data),
  deleteAddress: (id) => del(`/address/delete/${id}`)
}

// 优惠券相关
const couponApi = {
  getCouponList: () => get('/coupons/available'),
  receiveCoupon: (id) => post('/coupons/receive', { id }),
  getMyCoupons: () => get('/coupons/my')
}

// 门店相关
const storeApi = {
  getNearbyStores: (params) => get('/stores/nearby', params),
  getStoreDetail: (id) => get(`/stores/${id}`)
}

// 公共接口
const commonApi = {
  getStores: () => commonGet('/stores'),
  getRegions: () => commonGet('/regions')
}

module.exports = {
  authApi,
  homeApi,
  productApi,
  cartApi,
  orderApi,
  addressApi,
  couponApi,
  storeApi,
  commonApi
}