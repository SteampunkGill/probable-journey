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
  deactivate: () => post('/user/deactivate'),
  
  // 获取会员卡余额
  getCardBalance: () => get('/user/card-balance'),
  
  // 绑定会员卡
  bindCard: (cardNumber) => post('/user/bind-card', { cardNumber })
}

// 兼容性导出，防止旧代码报错
export const userApi = {
  ...authApi,
  getCardBalance: authApi.getCardBalance,
  bindCard: authApi.bindCard
}

// 首页相关
export const homeApi = {
  // 获取首页数据（后端路径为 /home/page）
  getHomeData: () => get('/home/page'),
  
  // 获取推荐商品
  getRecommendations: () => get('/home/recommendations'),
  
  // 提交推荐反馈
  submitRecommendationFeedback: (data) => post('/home/recommendation-feedback', data),
  
  // 获取通知列表
  getNotifications: () => get('/home/notifications')
}

// 商品相关
export const productApi = {
  // 获取分类列表
  getCategories: () => get('/categories'),
  
  // 获取商品列表（可通过categoryId筛选）
  getProducts: (categoryId, params) => get('/products', { categoryId, ...params }),
  
  // 获取商品详情
  getProductDetail: (id) => get(`/products/${id}`),

  // 获取商品评价
  getProductReviews: (id) => get(`/products/${id}/reviews`),
  
  // 获取商品定制选项（规格和选项）
  getProductCustomizations: (id) => get(`/products/${id}/customizations`),
  
  // 计算商品价格（根据定制选项）
  calculateProductPrice: (id, data) => post(`/products/${id}/calculate-price`, data),
  
  // 高级筛选商品
  advancedFilter: (filter) => get('/products/advanced-filter', filter),
  
  // 获取分类商品（兼容旧接口，推荐使用getProducts）
  getCategoryProducts: (categoryId, params) => get('/products', { categoryId, ...params }),
  
  // 商品搜索（后端路径为 /search）
  searchProducts: (keyword, params) => {
    // DEMO ONLY: 纯前端搜索模拟
    const products = JSON.parse(localStorage.getItem('demo_all_products') || '[]');
    const filtered = products.filter(p => p.name.includes(keyword) || (p.description && p.description.includes(keyword)));
    
    // 保存搜索历史
    const history = JSON.parse(localStorage.getItem('demo_search_history') || '[]');
    if (!history.includes(keyword)) {
      history.unshift(keyword);
      localStorage.setItem('demo_search_history', JSON.stringify(history.slice(0, 10)));
    }
    
    return Promise.resolve({ code: 200, data: { list: filtered }, message: 'success' });
  },
  
  // 获取搜索热词（后端路径为 /search/hot）
  getHotKeywords: () => {
    // DEMO ONLY: 随机抽取两个关键词
    const products = JSON.parse(localStorage.getItem('demo_all_products') || '[]');
    const keywords = products.map(p => p.name).sort(() => 0.5 - Math.random()).slice(0, 2);
    const hotKeywords = [
      { word: keywords[0] || '杨枝甘露', count: 999 },
      { word: keywords[1] || '多肉葡萄', count: 888 },
      { word: '生椰拿铁', count: 777 },
      { word: '烤奶', count: 666 }
    ];
    return Promise.resolve({ code: 200, data: hotKeywords, message: 'success' });
  },
  
  // 获取搜索历史
  getSearchHistory: () => {
    // DEMO ONLY
    const history = JSON.parse(localStorage.getItem('demo_search_history') || '[]');
    return Promise.resolve({ code: 200, data: history, message: 'success' });
  },
  
  // 清空搜索历史（后端路径为 /search/history）
  clearSearchHistory: () => {
    // DEMO ONLY
    localStorage.removeItem('demo_search_history');
    return Promise.resolve({ code: 200, message: '已清空历史记录' });
  },
  
  // 门店搜索模拟（DEMO ONLY）
  searchStores: (keyword) => {
    const stores = JSON.parse(localStorage.getItem('demo_all_stores') || '[]');
    const filtered = stores.filter(s => s.name.includes(keyword) || (s.address && s.address.includes(keyword)));
    return Promise.resolve({ code: 200, data: { list: filtered }, message: 'success' });
  }
}

// 搜索相关（独立导出以修复 SyntaxError）
export const searchApi = {
  searchProducts: productApi.searchProducts,
  getHotKeywords: productApi.getHotKeywords,
  getSearchHistory: productApi.getSearchHistory,
  clearSearchHistory: productApi.clearSearchHistory,
  searchStores: productApi.searchStores
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
  updateOrderRemark: (orderNo, remark) => put(`/orders/${orderNo}/remark`, { remark }),
  
  // 提交申诉
  submitAppeal: (orderNo, data) => post(`/orders/${orderNo}/appeal`, data)
}

// 地址相关
export const addressApi = {
  // 获取地址列表（后端路径为 /address/list）
  getAddressList: () => {
    // DEMO ONLY: 模拟后端脱离
    const saved = localStorage.getItem('demo_addresses');
    const list = saved ? JSON.parse(saved) : [
      { id: 1, name: '张三', phone: '13812345678', province: '广东省', city: '广州市', district: '天河区', detail: '珠江新城花城大道88号', tag: '公司', isDefault: true },
      { id: 2, name: '张三', phone: '13812345678', province: '广东省', city: '广州市', district: '海珠区', detail: '新港中路397号', tag: '家', isDefault: false }
    ];
    if (!saved) localStorage.setItem('demo_addresses', JSON.stringify(list));
    return Promise.resolve({ code: 200, data: list, message: 'success' });
  },
  
  // 添加地址（后端路径为 /address/add）
  addAddress: (data) => {
    // DEMO ONLY
    const list = JSON.parse(localStorage.getItem('demo_addresses') || '[]');
    const newAddress = { ...data, id: Date.now() };
    list.push(newAddress);
    localStorage.setItem('demo_addresses', JSON.stringify(list));
    return Promise.resolve({ code: 200, data: newAddress, message: '添加成功' });
  },
  
  // 更新地址（后端路径为 /address/update/{id}）
  updateAddress: (id, data) => {
    // DEMO ONLY
    let list = JSON.parse(localStorage.getItem('demo_addresses') || '[]');
    list = list.map(item => item.id == id ? { ...item, ...data } : item);
    localStorage.setItem('demo_addresses', JSON.stringify(list));
    return Promise.resolve({ code: 200, data: true, message: '更新成功' });
  },
  
  // 删除地址（后端路径为 /address/delete/{id}）
  deleteAddress: (id) => {
    // DEMO ONLY
    let list = JSON.parse(localStorage.getItem('demo_addresses') || '[]');
    list = list.filter(item => item.id != id);
    localStorage.setItem('demo_addresses', JSON.stringify(list));
    return Promise.resolve({ code: 200, data: true, message: '删除成功' });
  },
  
  // 根据定位获取地址（后端路径为 /address/geolocation）
  getAddressByLocation: (lat, lng) => get('/address/geolocation', { lat, lng }),
  
  // 获取地址历史（后端路径为 /address/history）
  getAddressHistory: (limit = 10) => {
    // DEMO ONLY: 模拟历史地址记录
    const saved = localStorage.getItem('demo_address_history');
    const list = saved ? JSON.parse(saved) : [
      { id: 999, name: '王大妈', phone: '13800138000', province: '广东省', city: '广州市', district: '天河区', detail: '兴盛路10号饮饮茶', usedCount: 5 },
      { id: 998, name: '李大爷', phone: '13900139000', province: '广东省', city: '广州市', district: '越秀区', detail: '北京路步行街123号', usedCount: 2 }
    ];
    if (!saved) localStorage.setItem('demo_address_history', JSON.stringify(list));
    return Promise.resolve({ code: 200, data: list.slice(0, limit), message: 'success' });
  }
}

// 优惠券相关
export const couponApi = {
  // 获取优惠券列表（后端路径为 /coupons/available）
  getCouponList: () => {
    // DEMO ONLY
    const list = [
      { id: 1, name: '新人大礼包', value: 10, minAmount: 30, type: '代金券', expireTime: '2026-01-31', description: '全场通用' },
      { id: 2, name: '分享专属券', value: 5, minAmount: 0, type: '无门槛', expireTime: '2026-02-15', description: '仅限自取' },
      { id: 3, name: '午后奶茶券', value: 3, minAmount: 20, type: '折扣券', expireTime: '2026-01-20', description: '14:00-17:00可用' }
    ];
    return Promise.resolve({ code: 200, data: list, message: 'success' });
  },
  
  // 领取优惠券（后端路径为 /coupons/receive）
  receiveCoupon: (id) => {
    // DEMO ONLY
    const available = [
      { id: 1, name: '新人大礼包', value: 10, minAmount: 30, type: '代金券', expireTime: '2026-01-31', description: '全场通用' },
      { id: 2, name: '分享专属券', value: 5, minAmount: 0, type: '无门槛', expireTime: '2026-02-15', description: '仅限自取' },
      { id: 3, name: '午后奶茶券', value: 3, minAmount: 20, type: '折扣券', expireTime: '2026-01-20', description: '14:00-17:00可用' }
    ];
    const coupon = available.find(c => c.id == id);
    if (coupon) {
      const myCoupons = JSON.parse(localStorage.getItem('demo_my_coupons') || '[]');
      if (!myCoupons.find(c => c.id == id)) {
        myCoupons.push({ ...coupon, receiveTime: '刚刚', status: 'UNUSED' });
        localStorage.setItem('demo_my_coupons', JSON.stringify(myCoupons));
      }
      return Promise.resolve({ code: 200, message: '领取成功' });
    }
    return Promise.resolve({ code: 404, message: '优惠券不存在' });
  },
  
  // 获取我的优惠券（后端路径为 /coupons/my）
  getMyCoupons: async () => {
    // DEMO ONLY: 优先从 IndexedDB 获取
    try {
      const list = await pointsDB.getAllCoupons();
      if (list && list.length > 0) {
        return { code: 200, data: list, message: 'success' };
      }
    } catch (e) {
      console.error('IndexedDB coupons error:', e);
    }
    
    // 兜底使用 localStorage
    const list = JSON.parse(localStorage.getItem('demo_my_coupons') || '[]');
    if (list.length === 0) {
      const defaultCoupon = { id: 99, name: '待领取优惠券', value: 5, minAmount: 0, type: '无门槛', expireTime: '2026-12-31', description: '演示专用', status: 'UNUSED' };
      list.push(defaultCoupon);
      localStorage.setItem('demo_my_coupons', JSON.stringify(list));
    }
    return { code: 200, data: list, message: 'success' };
  },
  
  // 获取优惠券详情（后端路径为 /coupons/{id}）
  getCouponDetail: (id) => {
    // DEMO ONLY
    const myCoupons = JSON.parse(localStorage.getItem('demo_my_coupons') || '[]');
    const coupon = myCoupons.find(c => c.id == id);
    return Promise.resolve({ code: 200, data: coupon, message: 'success' });
  }
}

// 积分相关
export const pointsApi = {
  // 获取积分明细（后端路径为 /member/points）
  getPointsTransactions: (page = 1, size = 10) => get('/member/points', { page, size }),
  
  // 获取积分规则（后端路径为 /member/points-rules）
  getPointsRules: () => get('/member/points-rules'),
  
  // 积分商城相关
  // 获取积分商品列表
  getPointsProducts: (page = 1, size = 10, category) => get('/member/mall/items', { page, size, category }),
  
  // 获取积分商品分类
  getPointsCategories: () => get('/member/mall/categories'),
  
  // 兑换积分商品
  exchangeProduct: (productId) => post('/member/mall/exchange', { productId }),
  
  // 获取兑换记录
  getExchangeRecords: (page = 1, size = 10) => get('/member/mall/exchange-records', { page, size }),
  
  // 积分兑换优惠券
  exchangeCoupon: (couponId) => post('/member/mall/exchange-coupon', { couponId }),
  
  // 每日签到
  signIn: () => post('/member/mall/sign-in')
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
  
  // 解绑会员卡
  unbindCard: () => post('/user/unbind-card'),
  
  // 注册/申请电子会员卡
  applyCard: (data) => post('/user/apply-card', data),
  
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
  generateShareLink: () => {
    // DEMO ONLY
    return Promise.resolve({
      code: 200,
      data: { shareId: 'S' + Date.now(), link: window.location.origin + '/register?inviteCode=TEA888' },
      message: '生成成功'
    });
  },
  
  // 获取邀请列表（后端路径为 /share/invitations）
  getInvitations: () => {
    // DEMO ONLY
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, nickname: '王大妈', avatar: '', time: '2025-12-30 10:00', status: '已下单' },
        { id: 2, nickname: '李大爷', avatar: '', time: '2025-12-30 11:30', status: '已注册' }
      ],
      message: 'success'
    });
  },
  
  // 领取分享优惠券（后端路径为 /share/receive-coupon）
  receiveShareCoupon: (shareId) => {
    // DEMO ONLY
    return new Promise(resolve => {
      setTimeout(() => {
        const myCoupons = JSON.parse(localStorage.getItem('demo_my_coupons') || '[]');
        const newCoupon = {
          id: Date.now(), // 使用数字 ID 修复后端 Long 类型反序列化错误
          name: '分享专属优惠券',
          value: 5,
          minAmount: 0,
          type: '无门槛',
          expireTime: '2026-02-15',
          description: '分享活动奖励',
          status: 'UNUSED',
          receiveTime: '刚刚'
        };
        myCoupons.push(newCoupon);
        localStorage.setItem('demo_my_coupons', JSON.stringify(myCoupons));
        
        // 同时更新分享统计
        const stats = JSON.parse(localStorage.getItem('demo_share_stats') || '{"inviteCount":0,"totalReward":0,"inviteCode":"TEA888"}');
        stats.inviteCount += 1;
        stats.totalReward += 5;
        localStorage.setItem('demo_share_stats', JSON.stringify(stats));

        resolve({ code: 200, message: '恭喜获得“分享专属5元优惠券”，已发放至您的账户' });
      }, 500);
    });
  },
  
  // 获取邀请规则（后端路径为 /share/invite-rules）
  getInviteRules: () => get('/share/invite-rules'),
  
  // 奖励邀请（后端路径为 /share/reward-invite）
  rewardInvite: (inviteeId) => {
    // DEMO ONLY
    return Promise.resolve({ code: 200, message: '奖励已发放' });
  }
}

// 分享相关
export const shareApi = {
  // 获取分享统计
  getShareStats: () => {
    // DEMO ONLY
    const saved = localStorage.getItem('demo_share_stats');
    const stats = saved ? JSON.parse(saved) : {
      inviteCount: 3,
      totalReward: 15,
      inviteCode: 'TEA888'
    };
    if (!saved) localStorage.setItem('demo_share_stats', JSON.stringify(stats));
    return Promise.resolve({ code: 200, data: stats, message: 'success' });
  },
  // 获取分享奖励记录
  getRewardRecords: () => {
    // DEMO ONLY
    return Promise.resolve({
      code: 200,
      data: [
        { id: 1, type: '邀请奖励', amount: 5, time: '刚刚', desc: '好友“王大妈”完成首单' },
        { id: 2, type: '邀请奖励', amount: 5, time: '1小时前', desc: '好友“张三”完成首单' }
      ],
      message: 'success'
    });
  }
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
  // 发起支付（支持 ALIPAY, BALANCE, WECHAT 等）
  initiateAlipay: (orderNo, method = 'ALIPAY') => post('/payment/alipay', { orderNo, method }),
  // 确认支付（用于余额支付或模拟支付成功后的状态同步）
  confirmPayment: (orderNo, method = 'BALANCE') => post('/payment/confirm', { orderNo, method }),
  // 获取支付状态
  getPaymentStatus: (orderNo) => get(`/payment/status/${orderNo}`)
}


// 营销活动相关
import { promotionDB, pointsDB, userDB } from './db.js'

export const promotionApi = {
  // 活动列表
  getPromotionList: async () => {
    // DEMO ONLY: 优先从 IndexedDB 获取促销活动
    try {
      const dbPromotions = await promotionDB.getAll();
      if (dbPromotions && dbPromotions.length > 0) {
        return { code: 200, data: dbPromotions, message: 'success' };
      }
    } catch (e) {
      console.error('IndexedDB error:', e);
    }
    return get('/promotions');
  },
  
  // 满减活动
  getFullReduction: () => get('/promotions/full-reduction'),
  
  // 第二杯半价活动详情
  getSecondHalf: (id) => get(`/promotions/second-half/${id}`),
  
  // 限时折扣商品
  getFlashSale: () => get('/promotions/flash-sale'),

  // DEMO ONLY: 保存促销活动到 IndexedDB
  savePromotion: async (data) => {
    if (data.id) {
      await promotionDB.update(data);
    } else {
      await promotionDB.add({ ...data, id: Date.now() });
    }
    return { code: 200, message: '保存成功' };
  },

  // DEMO ONLY: 删除促销活动
  deletePromotion: async (id) => {
    await promotionDB.delete(id);
    return { code: 200, message: '删除成功' };
  }
}

// 售后相关
export const afterSalesApi = {
  // 提交投诉建议
  submitComplaint: (data) => post('/complaints', data),
  
  // 售后记录查询
  getAfterSalesRecords: () => get('/after-sales')
}

// 收藏相关
export const favoriteApi = {
  // 获取收藏列表
  getFavorites: (params) => get('/favorites', params),
  
  // 添加收藏
  addFavorite: (productId) => post('/favorites/add', { productId }),
  
  // 取消收藏
  removeFavorite: (productId) => del(`/favorites/remove/${productId}`),
  
  // 清空收藏
  clearFavorites: () => del('/favorites/clear'),
  
  // 检查是否已收藏
  checkFavorite: (productId) => get(`/favorites/check/${productId}`)
}

// 广告相关
export const bannerApi = {
  // 获取轮播图
  getBanners: () => get('/banners')
}

// 礼品卡相关
export const giftCardApi = {
  // 购买礼品卡
  buyGiftCard: (faceValue) => post('/gift-card/buy', { faceValue }),
  // 激活礼品卡
  activateGiftCard: (cardNo, cardCode) => post('/gift-card/activate', { cardNo, cardCode }),
  // 获取我的礼品卡列表
  getMyGiftCards: () => get('/gift-card/list')
}

// 关于我们相关
export const aboutUsApi = {
  // 获取关于我们内容
  getAboutUs: () => get('/about'),
  // 更新关于我们内容（管理端）
  updateAboutUs: (data) => put('/admin/about-us', data)
}

// 门店相关
export const storeApi = {
  // 获取附近门店
  getNearbyStores: (params) => get('/stores/nearby', params),
  
  // 获取门店详情
  getStoreDetail: (id) => get(`/stores/${id}`)
}