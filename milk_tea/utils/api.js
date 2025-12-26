// utils/api.js
const { get, post, put, delete: del, uploadFile } = require('./request.js');

// 用户认证相关
const authApi = {
  // 微信登录（后端路径为 /auth/wx-login）
  wechatLogin: (code, userInfo) => post('/auth/wx-login', { code, userInfo }),
  
  // 账号密码登录
  login: (username, password) => post('/auth/login', { username, password }),
  
  // 注册
  register: (data) => post('/auth/register', data),

  // 登出（后端未实现，前端本地处理）
  logout: () => post('/auth/logout'),
  
  // 获取用户信息
  getUserProfile: () => get('/user/profile'),
  
  // 更新用户信息
  updateUserProfile: (data) => put('/user/profile', data),
  
  
  
  // 修改密码
  changePassword: (oldPassword, newPassword) => post('/auth/change-password', { oldPassword, newPassword }),
  
  // 注销账号
  deactivate: () => post('/user/deactivate')
};

// 商品相关
const productApi = {
  // 获取首页数据（后端路径为 /home/page）
  getHomeData: () => get('/home/page'),
  
  // 获取推荐商品
  getRecommendProducts: () => get('/home/recommendations'),
  
  // 获取热门商品
  getHotProducts: () => get('/products', { sort: 'sales,desc' }),
  
  // 获取商品详情
  getProductDetail: (id) => get(`/products/${id}`),
  
  // 获取分类商品（后端通过查询参数 categoryId）
  getCategoryProducts: (categoryId, params) => get('/products', { categoryId, ...params }),
  
  // 商品搜索（后端路径为 /search）
  searchProducts: (keyword, params) => get('/search', { keyword, ...params }),
  
  // 获取搜索热词（后端路径为 /search/hot）
  getHotKeywords: () => get('/search/hot'),
  
  // 获取搜索建议（后端未实现，暂时保留）
  getSearchSuggest: (keyword) => get('/search/suggest', { keyword }),
  
  // 收藏商品（后端未实现，暂时保留）
  favoriteProduct: (id) => post(`/products/${id}/favorite`),
  
  // 取消收藏（后端未实现，暂时保留）
  unfavoriteProduct: (id) => del(`/products/${id}/favorite`),
  
  // 获取收藏状态（后端未实现，暂时保留）
  getFavoriteStatus: (id) => get(`/products/${id}/favorite/status`),
  
  // 保存搜索历史（后端未实现，暂时保留）
  saveSearchHistory: (keyword, type = 'product') => post('/search/history', { keyword, type }),
  
  // 清空搜索历史（后端路径为 /search/history）
  clearSearchHistory: () => del('/search/history'),

  // 获取分类列表
  getCategoryList: () => get('/categories'),

  // 获取定制化选项
  getCustomizations: (id) => get(`/products/${id}/customizations`),

  // 获取商品评价
  getProductReviews: (id) => get(`/products/${id}/reviews`),

  // 计算定制价格
  calculatePrice: (id, data) => post(`/products/${id}/calculate-price`, data),

  // 高级筛选
  advancedFilter: (params) => get('/products/advanced-filter', params)
};

// 购物车相关
const cartApi = {
  // 获取购物车
  getCart: () => get('/cart'),
  
  // 添加商品到购物车（后端路径为 /cart/add）
  addToCart: (data) => post('/cart/add', data),
  
  // 更新购物车项（后端路径为 /cart/update-quantity）
  updateCartItem: (id, data) => put('/cart/update-quantity', { id, ...data }),
  
  // 删除购物车项（后端路径为 /cart/remove/{id}）
  deleteCartItem: (id) => del(`/cart/remove/${id}`),
  
  // 批量操作（后端未实现，暂时保留）
  batchCart: (action, itemIds) => post('/cart/batch', { action, itemIds }),
  
  // 清空购物车（后端未实现，暂时保留）
  clearCart: () => del('/cart'),
  
  // 获取结算信息（后端路径为 /cart/checkout）
  getCheckoutInfo: () => post('/cart/checkout'),
  
  // 验证订单（后端未实现，暂时保留）
  validateOrder: (data) => post('/cart/validate', data),
  
  // 自动匹配优惠券（后端路径为 /cart/auto-match-coupons）
  autoMatchCoupons: (data) => post('/cart/auto-match-coupons', data)
};

// 订单相关
const orderApi = {
  // 创建订单（后端路径为 /orders/create）
  createOrder: (data) => post('/orders/create', data),
  
  // 立即购买（后端未实现，暂时保留）
  buyNow: (data) => post('/orders/buy-now', data),
  
  // 获取订单列表
  getOrderList: (params) => get('/orders', params),
  
  // 获取订单详情（注意参数为 orderNo，不是 id）
  getOrderDetail: (orderNo) => get(`/orders/${orderNo}`),
  
  // 取消订单（后端未实现，暂时保留）
  cancelOrder: (id, reason) => post(`/orders/${id}/cancel`, { reason }),
  
  // 确认收货（后端未实现，暂时保留）
  confirmOrder: (id) => post(`/orders/${id}/confirm`),
  
  // 催单（后端路径为 /orders/{orderNo}/remind）
  remindOrder: (orderNo) => post(`/orders/${orderNo}/remind`),
  
  // 申请退款（后端路径为 /orders/{orderNo}/refund）
  applyRefund: (orderNo, reason) => post(`/orders/${orderNo}/refund`, { reason }),
  
  // 评价订单（后端路径为 /orders/{orderNo}/review）
  rateOrder: (orderNo, data) => post(`/orders/${orderNo}/review`, data),
  
  // 获取支付参数（后端未实现，暂时保留）
  getPayParams: (id, data) => post(`/orders/${id}/pay`, data),
  
  // 查询支付状态（后端未实现，暂时保留）
  getPayStatus: (id) => get(`/orders/${id}/pay/status`),
  
  // 获取订单状态
  getOrderStatus: (orderNo) => get(`/orders/${orderNo}/status`),
  
  // 获取预计时间
  getEstimatedTime: (orderNo) => get(`/orders/${orderNo}/estimated-time`),
  
  // 获取订单统计
  getOrderStatistics: () => get('/orders/statistics'),
  
  // 获取订单状态数量
  getStatusCount: () => get('/orders/status-count'),

  // 获取订单进度可视化
  getProgressVisual: (orderNo) => get(`/orders/${orderNo}/progress-visual`),

  // 更新订单备注
  updateRemark: (orderNo, remark) => put(`/orders/${orderNo}/remark`, { remark })
};

// 地址相关
const addressApi = {
  // 获取地址列表（后端路径为 /address/list）
  getAddressList: () => get('/address/list'),
  
  // 添加地址（后端路径为 /address/add）
  addAddress: (data) => post('/address/add', data),
  
  // 更新地址（后端路径为 /address/update/{id}）
  updateAddress: (id, data) => put(`/address/update/${id}`, data),
  
  // 删除地址（后端路径为 /address/delete/{id}）
  deleteAddress: (id) => del(`/address/delete/${id}`),
  
  // 设置默认地址（后端未实现，暂时保留）
  setDefaultAddress: (id) => put(`/user/addresses/${id}/default`),
  
  // 根据定位获取地址（后端路径为 /address/geolocation）
  getAddressByLocation: (lat, lng) => get('/address/geolocation', { lat, lng }),
  
  // 获取地址历史（后端路径为 /address/history）
  getAddressHistory: (limit = 10) => get('/address/history', { limit })
};

// 优惠券相关
const couponApi = {
  // 获取优惠券列表（后端路径为 /coupons/available）
  getCouponList: () => get('/coupons/available'),
  
  // 领取优惠券（后端路径为 /coupons/receive）
  receiveCoupon: (id) => post('/coupons/receive', { id }),
  
  // 批量领取（后端未实现，暂时保留）
  batchReceiveCoupon: (couponIds) => post('/coupons/batchreceive', { couponIds }),
  
  // 获取我的优惠券（后端路径为 /coupons/my）
  getMyCoupons: () => get('/coupons/my'),
  
  // 获取优惠券详情（后端路径为 /coupons/{id}）
  getCouponDetail: (id) => get(`/coupons/${id}`)
};

// 积分相关
const pointsApi = {
  // 获取积分明细（后端路径为 /member/points）
  getPointsTransactions: (page = 1, size = 10) => get('/member/points', { page, size }),
  
  // 积分兑换（后端未实现，暂时保留）
  exchangePoints: (data) => post('/points/exchange', data),
  
  // 获取积分规则（后端路径为 /member/points-rules）
  getPointsRules: () => get('/member/points-rules'),
  
  // 积分商城相关
  // 获取积分商品列表
  getPointsProducts: (category) => get('/points-mall/products', { category }),
  
  // 获取积分商品分类
  getPointsCategories: () => get('/points-mall/categories'),
  
  // 兑换积分商品
  exchangeProduct: (productId) => post('/points/exchange', { productId }),
  
  // 获取兑换记录
  getExchangeRecords: () => get('/points/exchange-records')
};

// 门店相关（后端未实现，暂时保留但注释掉）
const storeApi = {
  // 获取附近门店
  getNearbyStores: (params) => get('/stores/nearby', params),
  
  // 获取门店详情
  getStoreDetail: (id) => get(`/stores/${id}`),
  
  // 收藏门店
  favoriteStore: (id) => post(`/stores/${id}/favorite`),
  
  // 取消收藏门店
  unfavoriteStore: (id) => del(`/stores/${id}/favorite`)
};

// 系统相关（部分后端未实现）
const systemApi = {
  // 获取系统配置（后端未实现，暂时保留）
  getSystemConfig: () => get('/system/config'),
  
  // 获取页面配置（后端未实现，暂时保留）
  getPageConfig: (page) => get(`/system/pageconfig/${page}`),
  
  // 获取消息列表（后端未实现，暂时保留）
  getNotifications: (params) => get('/notifications', params),
  
  // 标记消息已读（后端未实现，暂时保留）
  markNotificationRead: (id) => put(`/notifications/${id}/read`),
  
  // 批量标记已读（后端未实现，暂时保留）
  batchMarkNotificationRead: (ids) => post('/notifications/batchread', { ids }),
  
  // 上传文件
  uploadFile: (filePath, data) => uploadFile('/upload', filePath, data),

  // 上传图片
  uploadImage: (filePath, type) => uploadFile('/../../common/upload/image', filePath, { type }),
  
  // 获取通知设置（后端路径为 /settings/notification）
  getNotificationSettings: () => get('/settings/notification'),
  
  // 更新通知设置（后端路径为 /settings/notification）
  updateNotificationSettings: (settings) => put('/settings/notification', settings),
  
  // 确认协议（后端路径为 /settings/agreement）
  confirmAgreement: () => post('/settings/agreement'),

  // 获取轮播图（根据文档，轮播图属于广告管理，但这里先添加到系统API）
  getBanners: () => get('/banners'),

  // 获取促销活动
  getPromotions: () => get('/promotions'),

  // 获取搜索历史
  getSearchHistory: () => get('/search/history')
};

// 会员相关
const memberApi = {
  // 获取会员信息（后端路径为 /member/level）
  getMemberInfo: () => get('/member/level'),
  
  // 领取生日特权（后端未实现，暂时保留）
  receiveBirthdayGift: () => post('/member/birthday-gift/receive'),
  
  // 获取会员卡余额（后端路径为 /user/card-balance）
  getCardBalance: () => get('/user/card-balance'),
  
  // 绑定会员卡（后端路径为 /user/bind-card）
  bindCard: (cardNumber) => post('/user/bind-card', { cardNumber }),

  // 获取会员专享商品
  getExclusiveProducts: () => get('/member/exclusive-products'),

  // 获取会员专享价
  getExclusivePrices: () => get('/member/exclusive-prices'),

  // 获取生日特权信息
  getBirthdayPrivilege: () => get('/member/birthday-privilege'),

  // 领取生日特权
  receiveBirthdayPrivilege: () => post('/member/birthday-privilege/receive')
};

// 社交功能
const socialApi = {
  // 获取分享信息（后端未实现，暂时保留）
  getShareInfo: () => get('/user/share/info'),
  
  // 生成邀请海报（后端未实现，暂时保留）
  generatePoster: (data) => post('/user/share/poster', data),
  
  // 记录分享行为（后端未实现，暂时保留）
  recordShare: (data) => post('/user/share/record', data),
  
  // 生成分享链接（后端路径为 /share/generate）
  generateShareLink: () => post('/share/generate'),
  
  // 获取邀请列表（后端路径为 /share/invitations）
  getInvitations: () => get('/share/invitations'),
  
  // 领取分享优惠券（后端路径为 /share/receive-coupon）
  receiveShareCoupon: (shareId) => post('/share/receive-coupon', { shareId }),
  
  // 获取邀请规则（后端路径为 /share/invite-rules）
  getInviteRules: () => get('/share/invite-rules'),
  
  // 奖励邀请（后端路径为 /share/reward-invite）
  rewardInvite: (inviteeId) => post('/share/reward-invite', { inviteeId })
};

// 积分商城相关
const pointsMallApi = {
  // 获取积分商品列表
  getProducts: (category) => get('/points-mall/products', { category }),
  
  // 获取积分商品分类
  getCategories: () => get('/points-mall/categories'),
  
  // 兑换积分商品
  exchangeProduct: (productId) => post('/points/exchange', { productId }),
  
  // 获取兑换记录
  getExchangeRecords: () => get('/points/exchange-records')
};

// 支付相关
const paymentApi = {
  // 支付宝支付
  alipay: (orderNo) => post('/payment/alipay', { orderNo }),
  
  // 查询支付状态
  getPaymentStatus: (orderNo) => get(`/payment/status/${orderNo}`)
};

// 营销活动相关
const promotionApi = {
  // 活动列表
  getPromotionList: () => get('/promotions'),
  
  // 满减活动
  getFullReduction: () => get('/promotions/full-reduction'),
  
  // 第二杯半价活动详情
  getSecondHalf: (id) => get(`/promotions/second-half/${id}`),
  
  // 限时折扣商品
  getFlashSale: () => get('/promotions/flash-sale')
};

// 售后相关
const afterSalesApi = {
  // 提交投诉建议
  submitComplaint: (data) => post('/complaints', data),
  
  // 售后记录查询
  getAfterSalesRecords: () => get('/after-sales')
};

module.exports = {
  authApi,
  productApi,
  cartApi,
  orderApi,
  addressApi,
  couponApi,
  pointsApi,
  storeApi,
  systemApi,
  memberApi,
  socialApi,
  pointsMallApi,
  paymentApi,
  promotionApi,
  afterSalesApi
};