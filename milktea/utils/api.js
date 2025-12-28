const { get, post, put, del, commonGet, commonPost, uploadFile } = require('./request.js')

// 用户认证相关
const authApi = {
  wechatLogin: (code, userInfo) => post('/auth/wx-login', { code, userInfo }),
  login: (username, password) => post('/auth/login', { username, password }),
  getUserProfile: () => get('/user/profile'),
  updateUserProfile: (data) => put('/user/profile', data),
  getCardBalance: () => get('/user/card-balance'),
  bindCard: (cardNumber) => post('/user/bind-card', { cardNumber }),
  register: (data) => post('/auth/register', data),
  changePassword: (oldPassword, newPassword) => post('/auth/change-password', { oldPassword, newPassword }),
  deactivate: () => post('/user/deactivate')
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
  getProductReviews: (id) => get(`/products/${id}/reviews`),
  getProductCustomizations: (id) => get(`/products/${id}/customizations`),
  calculateProductPrice: (id, data) => post(`/products/${id}/calculate-price`, data),
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
  autoMatchCoupons: (data) => post('/cart/auto-match-coupons', data),
  clearCart: () => del('/cart/clear')
}

// 订单相关
const orderApi = {
  createOrder: (data) => post('/orders/create', data),
  getOrderList: (params) => get('/orders', params),
  getOrderDetail: (orderNo) => get(`/orders/${orderNo}`),
  cancelOrder: (orderNo) => post(`/orders/${orderNo}/cancel`),
  confirmOrder: (orderNo) => post(`/orders/${orderNo}/confirm`),
  remindOrder: (orderNo) => post(`/orders/${orderNo}/remind`),
  applyRefund: (orderNo, reason) => post(`/orders/${orderNo}/refund`, { reason }),
  rateOrder: (orderNo, data) => post(`/orders/${orderNo}/review`, data)
}

// 地址相关
const addressApi = {
  getAddressList: () => get('/address/list'),
  addAddress: (data) => post('/address/add', data),
  updateAddress: (id, data) => put(`/address/update/${id}`, data),
  deleteAddress: (id) => del(`/address/delete/${id}`),
  getAddressByLocation: (lat, lng) => get('/address/geolocation', { lat, lng })
}

// 优惠券相关
const couponApi = {
  getCouponList: () => get('/coupons/available'),
  receiveCoupon: (id) => post('/coupons/receive', { id }),
  getMyCoupons: () => get('/coupons/my')
}

// 积分相关
const pointsApi = {
  getPointsTransactions: (page = 1, size = 10) => get('/member/points', { page, size }),
  getPointsRules: () => get('/member/points-rules'),
  getPointsProducts: (page = 1, size = 10, category) => get('/member/mall/items', { page, size, category }),
  getPointsCategories: () => get('/member/mall/categories'),
  exchangeProduct: (productId) => post('/member/mall/exchange', { productId }),
  signIn: () => post('/member/mall/sign-in')
}

// 会员相关
const memberApi = {
  getMemberInfo: () => get('/member/level'),
  applyCard: (data) => post('/user/apply-card', data),
  getExclusiveProducts: () => get('/member/exclusive-products')
}

// 门店相关
const storeApi = {
  getNearbyStores: (params) => get('/stores/nearby', params),
  getStoreDetail: (id) => get(`/stores/${id}`)
}

// 支付相关
const paymentApi = {
  initiatePayment: (orderNo, method = 'WECHAT') => post('/payment/initiate', { orderNo, method }),
  confirmPayment: (orderNo, method = 'BALANCE') => post('/payment/confirm', { orderNo, method }),
  getPaymentStatus: (orderNo) => get(`/payment/status/${orderNo}`)
}

// 收藏相关
const favoriteApi = {
  getFavorites: (params) => get('/favorites', params),
  addFavorite: (productId) => post('/favorites/add', { productId }),
  removeFavorite: (productId) => del(`/favorites/remove/${productId}`),
  clearFavorites: () => del('/favorites/clear'),
  checkFavorite: (productId) => get(`/favorites/check/${productId}`)
}

// 搜索相关
const searchApi = {
  searchProducts: (keyword, params) => get('/search', { keyword, ...params }),
  getHotKeywords: () => get('/search/hot'),
  getSearchHistory: () => get('/search/history'),
  clearSearchHistory: () => del('/search/history')
}

// 系统相关
const systemApi = {
  getNotificationSettings: () => get('/settings/notification'),
  updateNotificationSettings: (settings) => put('/settings/notification', settings),
  confirmAgreement: () => post('/settings/agreement')
}

// 社交/分享相关
const socialApi = {
  generateShareLink: () => post('/share/generate'),
  getInvitations: () => get('/share/invitations'),
  receiveShareCoupon: (shareId) => post('/share/receive-coupon', { shareId }),
  getInviteRules: () => get('/share/invite-rules'),
  rewardInvite: (inviteeId) => post('/share/reward-invite', { inviteeId })
}

const shareApi = {
  getShareStats: () => get('/share/stats'),
  getRewardRecords: () => get('/share/rewards')
}

// 售后相关
const afterSalesApi = {
  submitComplaint: (data) => post('/complaints', data),
  getAfterSalesRecords: () => get('/after-sales')
}

// 礼品卡相关
const giftCardApi = {
  buyGiftCard: (faceValue) => post('/gift-card/buy', { faceValue }),
  activateGiftCard: (cardNo, cardCode) => post('/gift-card/activate', { cardNo, cardCode }),
  getMyGiftCards: () => get('/gift-card/list')
}

// 关于我们相关
const aboutUsApi = {
  getAboutUs: () => get('/about'),
  updateAboutUs: (data) => put('/admin/about-us', data)
}

// 公共接口
const commonApi = {
  getStores: () => commonGet('/stores'),
  getRegions: () => commonGet('/regions'),
  uploadImage: (filePath) => uploadFile('/upload/image', filePath),
  getTimestamp: () => commonGet('/timestamp'),
  checkVersion: (platform, currentVersion) => commonGet('/version', { platform, currentVersion })
}

module.exports = {
  authApi,
  homeApi,
  productApi,
  cartApi,
  orderApi,
  addressApi,
  couponApi,
  pointsApi,
  memberApi,
  storeApi,
  paymentApi,
  favoriteApi,
  searchApi,
  systemApi,
  socialApi,
  shareApi,
  afterSalesApi,
  giftCardApi,
  aboutUsApi,
  commonApi
}