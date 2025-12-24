import { get, post, put, del, uploadFile, commonGet, commonPost, commonPut, commonDel } from './request.js'

// 用户认证相关
export const authApi = {
  // 微信登录（后端路径为 /auth/wx-login）
  wechatLogin: (code, userInfo) => post('/auth/wx-login', { code, userInfo }),
  
  // 账号密码登录
  login: (username, password) => post('/auth/login', { username, password }),
  
  // 登出（后端未实现，前端本地处理）
  logout: () => Promise.resolve(), // 本地处理，无需调用后端
  
  // 获取用户信息
  getUserProfile: () => get('/user/profile'),
  
  // 更新用户信息
  updateUserProfile: (data) => put('/user/profile', data),
  
  
  // 用户注册
  register: (data) => post('/auth/register', data),
  
  // 修改密码
  changePassword: (oldPassword, newPassword) => post('/auth/change-password', { oldPassword, newPassword }),
  
  // 注销账号验证
  verifyDeactivation: () => post('/user/verify-deactivation'),
  
  // 注销账号
  deactivate: () => post('/user/deactivate')
}

// 首页相关
export const homeApi = {
  // 获取首页数据（后端路径为 /home/page）
  getHomeData: () => get('/home/page'),
  
  // 获取推荐商品
  getRecommendations: () => get('/home/recommendations'),
  
  // 提交推荐反馈
  submitRecommendationFeedback: (data) => post('/home/recommendation-feedback', data)
}

// 商品相关
export const productApi = {
  // 获取分类列表
  getCategories: () => get('/categories'),
  
  // 获取商品列表（可通过categoryId筛选）
  getProducts: (categoryId, params) => get('/products', { categoryId, ...params }),
  
  // 获取商品详情
  getProductDetail: (id) => get(`/products/${id}`),
  
  // 获取商品定制选项（规格和选项）
  getProductCustomizations: (id) => get(`/products/${id}/customizations`),
  
  // 计算商品价格（根据定制选项）
  calculateProductPrice: (id, data) => post(`/products/${id}/calculate-price`, data),
  
  // 高级筛选商品
  advancedFilter: (filter) => get('/products/advanced-filter', filter),
  
  // 获取分类商品（兼容旧接口，推荐使用getProducts）
  getCategoryProducts: (categoryId, params) => get('/products', { categoryId, ...params }),
  
  // 商品搜索（后端路径为 /search）
  searchProducts: (keyword, params) => get('/search', { keyword, ...params }),
  
  // 获取搜索热词（后端路径为 /search/hot）
  getHotKeywords: () => get('/search/hot'),
  
  // 获取搜索历史
  getSearchHistory: () => get('/search/history'),
  
  // 清空搜索历史（后端路径为 /search/history）
  clearSearchHistory: () => del('/search/history')
}

// 购物车相关
export const cartApi = {
  // 获取购物车
  getCart: () => get('/cart'),
  
  // 添加商品到购物车（后端路径为 /cart/add）
  addToCart: (data) => post('/cart/add', data),
  
  // 更新购物车项（后端路径为 /cart/update-quantity）
  updateCartItem: (id, data) => put('/cart/update-quantity', { id, ...data }),
  
  // 删除购物车项（后端路径为 /cart/remove/{id}）
  deleteCartItem: (id) => del(`/cart/remove/${id}`),
  
  // 获取结算信息（后端路径为 /cart/checkout）
  getCheckoutInfo: () => post('/cart/checkout'),
  
  // 自动匹配优惠券（后端路径为 /cart/auto-match-coupons）
  autoMatchCoupons: (data) => post('/cart/auto-match-coupons', data),
  
  // 清空购物车（后端路径为 /cart/clear）
  clearCart: () => del('/cart/clear')
}

// 订单相关
export const orderApi = {
  // 创建订单（后端路径为 /orders/create）
  createOrder: (data) => post('/orders/create', data),
  
  // 获取订单列表
  getOrderList: (params) => get('/orders', params),
  
  // 获取订单详情（注意参数为 orderNo，不是 id）
  getOrderDetail: (orderNo) => get(`/orders/${orderNo}`),
  
  // 获取订单进度可视化
  getOrderProgressVisual: (orderNo) => get(`/orders/${orderNo}/progress-visual`),
  
  // 催单（后端路径为 /orders/{orderNo}/remind）
  remindOrder: (orderNo) => post(`/orders/${orderNo}/remind`),
  
  // 申请退款（后端路径为 /orders/{orderNo}/refund）
  applyRefund: (orderNo, reason) => post(`/orders/${orderNo}/refund`, { reason }),
  
  // 获取退款状态
  getRefundStatus: (orderNo) => get(`/orders/${orderNo}/refund/status`),
  
  // 评价订单（后端路径为 /orders/{orderNo}/review）
  rateOrder: (orderNo, data) => post(`/orders/${orderNo}/review`, data),
  
  // 获取订单状态
  getOrderStatus: (orderNo) => get(`/orders/${orderNo}/status`),
  
  // 获取预计时间
  getEstimatedTime: (orderNo) => get(`/orders/${orderNo}/estimated-time`),
  
  // 获取订单统计
  getOrderStatistics: () => get('/orders/statistics'),
  
  // 获取订单状态数量
  getStatusCount: () => get('/orders/status-count'),
  
  // 取消订单（后端路径为 /orders/{orderNo}/cancel）
  cancelOrder: (orderNo) => post(`/orders/${orderNo}/cancel`),
  
  // 确认收货（后端路径为 /orders/{orderNo}/confirm）
  confirmOrder: (orderNo) => post(`/orders/${orderNo}/confirm`),
  
  // 立即购买（调用创建订单接口，但传递 buyNow 标志）
  buyNow: (data) => post('/orders/create', { ...data, buyNow: true }),
  
  // 更新订单备注
  updateOrderRemark: (orderNo, remark) => put(`/orders/${orderNo}/remark`, { remark })
}

// 地址相关
export const addressApi = {
  // 获取地址列表（后端路径为 /address/list）
  getAddressList: () => get('/address/list'),
  
  // 添加地址（后端路径为 /address/add）
  addAddress: (data) => post('/address/add', data),
  
  // 更新地址（后端路径为 /address/update/{id}）
  updateAddress: (id, data) => put(`/address/update/${id}`, data),
  
  // 删除地址（后端路径为 /address/delete/{id}）
  deleteAddress: (id) => del(`/address/delete/${id}`),
  
  // 根据定位获取地址（后端路径为 /address/geolocation）
  getAddressByLocation: (lat, lng) => get('/address/geolocation', { lat, lng }),
  
  // 获取地址历史（后端路径为 /address/history）
  getAddressHistory: (limit = 10) => get('/address/history', { limit })
}

// 优惠券相关
export const couponApi = {
  // 获取优惠券列表（后端路径为 /coupons/available）
  getCouponList: () => get('/coupons/available'),
  
  // 领取优惠券（后端路径为 /coupons/receive）
  receiveCoupon: (id) => post('/coupons/receive', { id }),
  
  // 获取我的优惠券（后端路径为 /coupons/my）
  getMyCoupons: () => get('/coupons/my'),
  
  // 获取优惠券详情（后端路径为 /coupons/{id}）
  getCouponDetail: (id) => get(`/coupons/${id}`)
}

// 积分相关
export const pointsApi = {
  // 获取积分明细（后端路径为 /member/points）
  getPointsTransactions: (page = 1, size = 10) => get('/member/points', { page, size }),
  
  // 获取积分规则（后端路径为 /member/points-rules）
  getPointsRules: () => get('/member/points-rules'),
  
  // 积分商城相关
  // 获取积分商品列表
  getPointsProducts: (page = 1, size = 10, category) => get('/points-mall/products', { page, size, category }),
  
  // 获取积分商品分类
  getPointsCategories: () => get('/points-mall/categories'),
  
  // 兑换积分商品
  exchangeProduct: (productId) => post('/points-mall/exchange', { productId }),
  
  // 获取兑换记录
  getExchangeRecords: (page = 1, size = 10) => get('/points-mall/exchange-records', { page, size }),
  
  // 积分兑换优惠券
  exchangeCoupon: (couponId) => post('/points/exchange-coupon', { couponId })
}

// 系统相关（部分后端未实现）
export const systemApi = {
  // 获取通知设置（后端路径为 /settings/notification）
  getNotificationSettings: () => get('/settings/notification'),
  
  // 更新通知设置（后端路径为 /settings/notification）
  updateNotificationSettings: (settings) => put('/settings/notification', settings),
  
  // 确认协议（后端路径为 /settings/agreement）
  confirmAgreement: () => post('/settings/agreement')
}

// 会员相关
export const memberApi = {
  // 获取会员信息（后端路径为 /member/level）
  getMemberInfo: () => get('/member/level'),
  
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
}

// 社交功能
export const socialApi = {
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
}

// 公共接口
export const commonApi = {
  // 获取门店列表
  getStores: () => commonGet('/stores'),
  // 获取区域数据
  getRegions: () => commonGet('/regions'),
  // 上传图片
  uploadImage: (file, type) => uploadFile('/upload/image', file, { type }),
  // 上传文件
  uploadFile: (file) => uploadFile('/upload/file', file),
  // 获取服务器时间戳
  getTimestamp: () => commonGet('/timestamp'),
  // 检查版本
  checkVersion: (platform, currentVersion) => commonGet('/version', { platform, currentVersion })
}

// 支付相关
export const paymentApi = {
  // 发起支付宝支付
  initiateAlipay: (orderNo) => post('/payment/alipay', { orderNo }),
  // 获取支付状态
  getPaymentStatus: (orderNo) => get(`/payment/status/${orderNo}`)
}

// 积分商城相关
export const pointsMallApi = {
  // 获取积分商品列表
  getProducts: (category) => get('/points-mall/products', { category }),
  
  // 获取积分商品分类
  getCategories: () => get('/points-mall/categories'),
  
  // 兑换积分商品
  exchangeProduct: (productId) => post('/points-mall/exchange', { productId }),
  
  // 获取兑换记录
  getExchangeRecords: () => get('/points-mall/exchange-records')
}

// 营销活动相关
export const promotionApi = {
  // 活动列表
  getPromotionList: () => get('/promotions'),
  
  // 满减活动
  getFullReduction: () => get('/promotions/full-reduction'),
  
  // 第二杯半价活动详情
  getSecondHalf: (id) => get(`/promotions/second-half/${id}`),
  
  // 限时折扣商品
  getFlashSale: () => get('/promotions/flash-sale')
}

// 售后相关
export const afterSalesApi = {
  // 提交投诉建议
  submitComplaint: (data) => post('/complaints', data),
  
  // 售后记录查询
  getAfterSalesRecords: () => get('/after-sales')
}

// 广告相关
export const bannerApi = {
  // 获取轮播图
  getBanners: () => get('/banners')
}

// 门店相关
export const storeApi = {
  // 获取附近门店
  getNearbyStores: (params) => get('/stores/nearby', params),
  
  // 获取门店详情
  getStoreDetail: (id) => get(`/stores/${id}`)
}