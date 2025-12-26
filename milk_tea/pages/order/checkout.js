// pages/order/checkout.js
const app = getApp();
const storage = require('../../utils/storage.js');
const util = require('../../utils/util.js');
const { orderApi, couponApi } = require('../../utils/api.js');

Page({
  data: {
    // 订单类型：cart-购物车结算，buyNow-立即购买
    orderType: 'cart',
    
    // 订单商品
    orderItems: [],
    
    // 收货地址
    selectedAddress: null,
    
    // 配送方式：delivery-外卖配送，pickup-到店自取
    deliveryType: 'delivery',
    
    // 门店信息
    selectedStore: null,
    
    // 支付方式：wechat-微信支付，balance-余额支付
    paymentMethod: 'wechat',
    
    // 优惠券
    selectedCoupon: null,
    availableCoupons: [],
    
    // 积分
    availablePoints: 0,
    usePoints: false,
    pointsToUse: 0,
    maxPoints: 0,
    
    // 备注
    remark: '',
    
    // 餐具数量
    tablewareCount: 0,
    
    // 预计送达时间
    estimatedDeliveryTime: '',
    
    // 费用明细
    subtotal: 0,           // 商品小计
    deliveryFee: 0,        // 配送费
    packagingFee: 0,       // 包装费
    couponDiscount: 0,     // 优惠券优惠
    pointsDiscount: 0,     // 积分抵扣
    totalAmount: 0,        // 总金额
    
    // 加载状态
    loading: true,
    submitting: false
  },

  onLoad(options) {
    // 获取订单类型
    const orderType = options.type || 'cart';
    this.setData({ orderType });
    
    // 加载数据
    this.loadOrderData();
    this.loadUserData();
    this.loadCoupons();
  },

  // 加载订单数据
  loadOrderData() {
    try {
      let orderItems = [];
      
      if (this.data.orderType === 'cart') {
        // 从购物车获取数据
        const checkoutData = wx.getStorageSync('checkoutData');
        if (checkoutData && checkoutData.items) {
          orderItems = checkoutData.items;
          
          // 恢复其他信息
          if (checkoutData.coupon) {
            this.setData({ selectedCoupon: checkoutData.coupon });
          }
          if (checkoutData.usePoints) {
            this.setData({ 
              usePoints: checkoutData.usePoints,
              pointsToUse: checkoutData.pointsToUse || 0
            });
          }
          if (checkoutData.remark) {
            this.setData({ remark: checkoutData.remark });
          }
        }
      } else {
        // 立即购买
        const buyNowItem = wx.getStorageSync('buyNowItem');
        if (buyNowItem) {
          orderItems = [buyNowItem];
        }
      }
      
      this.setData({ 
        orderItems,
        loading: false 
      }, () => {
        this.calculateAmount();
      });
      
    } catch (error) {
      console.error('加载订单数据失败:', error);
      this.setData({ loading: false });
    }
  },

  // 加载用户数据
  async loadUserData() {
    try {
      // 加载收货地址
      const addresses = storage.getAddresses();
      const defaultAddress = addresses.find(addr => addr.isDefault);
      
      if (defaultAddress) {
        this.setData({ selectedAddress: defaultAddress });
        this.calculateDeliveryFee();
      }
      
      // 加载用户积分
      const userInfo = storage.getUserInfo();
      if (userInfo && userInfo.points) {
        this.setData({ availablePoints: userInfo.points });
      }
      
    } catch (error) {
      console.error('加载用户数据失败:', error);
    }
  },

  // 加载优惠券
  async loadCoupons() {
    try {
      const res = await couponApi.getCouponList();
      if (res.code === 200) {
        this.setData({ availableCoupons: res.data });
      }
    } catch (error) {
      console.error('加载优惠券失败:', error);
    }
  },

  // 计算费用
  calculateAmount() {
    const { orderItems, selectedCoupon, usePoints, pointsToUse, deliveryType } = this.data;
    
    // 计算商品小计
    let subtotal = 0;
    orderItems.forEach(item => {
      subtotal += item.subtotal || (item.price * item.quantity);
    });
    
    // 计算配送费
    let deliveryFee = 0;
    if (deliveryType === 'delivery') {
      deliveryFee = subtotal >= 30 ? 0 : 5; // 满30免配送费
    }
    
    // 计算包装费
    const packagingFee = orderItems.length * 0.5;
    
    // 计算优惠券优惠
    let couponDiscount = 0;
    if (selectedCoupon) {
      if (subtotal >= selectedCoupon.minAmount) {
        if (selectedCoupon.type === 'full_reduce') {
          couponDiscount = selectedCoupon.value;
        } else if (selectedCoupon.type === 'discount') {
          couponDiscount = subtotal * (1 - selectedCoupon.value);
          if (selectedCoupon.maxDiscount) {
            couponDiscount = Math.min(couponDiscount, selectedCoupon.maxDiscount);
          }
        }
      }
    }
    
    // 计算积分抵扣
    let pointsDiscount = 0;
    if (usePoints && pointsToUse > 0) {
      pointsDiscount = pointsToUse / 100; // 100积分=1元
    }
    
    // 计算总金额
    const totalAmount = Math.max(0, subtotal + deliveryFee + packagingFee - couponDiscount - pointsDiscount);
    
    // 计算最大可用积分
    const maxPoints = Math.min(this.data.availablePoints, subtotal * 100);
    
    // 计算预计送达时间
    const estimatedDeliveryTime = this.calculateDeliveryTime();
    
    this.setData({
      subtotal: subtotal.toFixed(2),
      deliveryFee: deliveryFee.toFixed(2),
      packagingFee: packagingFee.toFixed(2),
      couponDiscount: couponDiscount.toFixed(2),
      pointsDiscount: pointsDiscount.toFixed(2),
      totalAmount: totalAmount.toFixed(2),
      maxPoints,
      estimatedDeliveryTime
    });
  },

  // 计算预计送达时间
  calculateDeliveryTime() {
    const now = new Date();
    now.setMinutes(now.getMinutes() + 30); // 预计30分钟送达
    return util.formatDate(now, 'HH:mm');
  },

  // 计算配送费
  calculateDeliveryFee() {
    const { selectedAddress, deliveryType } = this.data;
    
    if (deliveryType === 'pickup') {
      this.setData({ deliveryFee: 0 });
      return;
    }
    
    if (!selectedAddress) {
      this.setData({ deliveryFee: 0 });
      return;
    }
    
    // 根据距离计算配送费（模拟）
    // 实际应该根据地址和门店距离计算
    const distance = 2.5; // km
    let deliveryFee = 0;
    
    if (this.data.subtotal >= 30) {
      deliveryFee = 0; // 满30免配送费
    } else if (distance <= 3) {
      deliveryFee = 5;
    } else if (distance <= 5) {
      deliveryFee = 8;
    } else {
      deliveryFee = 12;
    }
    
    this.setData({ deliveryFee });
    this.calculateAmount();
  },

  // 选择收货地址
  selectAddress() {
    wx.navigateTo({
      url: '/pages/address/address?mode=select',
      events: {
        selectAddress: (address) => {
          this.setData({ selectedAddress: address });
          this.calculateDeliveryFee();
        }
      }
    });
  },

  // 切换配送方式
  switchDeliveryType(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ deliveryType: type }, () => {
      this.calculateAmount();
    });
  },

  // 选择门店
  selectStore() {
    wx.navigateTo({
      url: '/pages/store/list?mode=select',
      events: {
        selectStore: (store) => {
          this.setData({ selectedStore: store });
        }
      }
    });
  },

  // 选择优惠券
  selectCoupon() {
    const { availableCoupons, subtotal } = this.data;
    
    wx.navigateTo({
      url: '/pages/coupon/select?amount=' + subtotal,
      events: {
        selectCoupon: (coupon) => {
          this.setData({ selectedCoupon: coupon }, () => {
            this.calculateAmount();
          });
        }
      }
    });
  },

  // 切换使用积分
  toggleUsePoints(e) {
    const usePoints = e.detail.value;
    
    if (usePoints) {
      // 默认使用最大可用积分
      const pointsToUse = Math.min(this.data.maxPoints, this.data.availablePoints);
      this.setData({ 
        usePoints,
        pointsToUse
      }, () => {
        this.calculateAmount();
      });
    } else {
      this.setData({ 
        usePoints: false,
        pointsToUse: 0
      }, () => {
        this.calculateAmount();
      });
    }
  },

  // 积分输入
  onPointsInput(e) {
    let pointsToUse = parseInt(e.detail.value) || 0;
    const { maxPoints, availablePoints } = this.data;
    
    // 限制在可用范围内
    pointsToUse = Math.min(pointsToUse, maxPoints, availablePoints);
    
    // 必须是100的倍数
    pointsToUse = Math.floor(pointsToUse / 100) * 100;
    
    this.setData({ pointsToUse }, () => {
      this.calculateAmount();
    });
  },

  // 备注输入
  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  // 餐具数量变化
  onTablewareChange(e) {
    this.setData({ tablewareCount: parseInt(e.detail.value) || 0 });
  },

  // 切换支付方式
  switchPaymentMethod(e) {
    const method = e.currentTarget.dataset.method;
    this.setData({ paymentMethod: method });
  },

  // 提交订单
  async submitOrder() {
    const {
      orderItems,
      selectedAddress,
      deliveryType,
      selectedStore,
      paymentMethod,
      totalAmount,
      remark,
      tablewareCount,
      submitting
    } = this.data;
    
    if (submitting) return;
    
    // 验证
    if (orderItems.length === 0) {
      wx.showToast({
        title: '订单商品为空',
        icon: 'none'
      });
      return;
    }
    
    if (deliveryType === 'delivery' && !selectedAddress) {
      wx.showToast({
        title: '请选择收货地址',
        icon: 'none'
      });
      return;
    }
    
    if (deliveryType === 'pickup' && !selectedStore) {
      wx.showToast({
        title: '请选择自提门店',
        icon: 'none'
      });
      return;
    }
    
    this.setData({ submitting: true });
    
    try {
      // 构建订单数据
      const orderData = {
        orderItems: orderItems.map(item => ({
          productId: item.id,
          quantity: item.quantity,
          specId: item.specId,
          sweetness: item.customizations?.sweetness,
          temperature: item.customizations?.temperature,
          toppings: item.customizations?.toppings?.map(t => t.id)
        })),
        deliveryType,
        addressId: selectedAddress?.id,
        storeId: selectedStore?.id,
        paymentMethod,
        couponId: this.data.selectedCoupon?.id,
        usePoints: this.data.usePoints,
        pointsToUse: this.data.pointsToUse,
        remark,
        tablewareCount,
        totalAmount
      };
      
      // 调用真实API创建订单
      const res = await orderApi.createOrder(orderData);
      
      if (res.code === 200) {
        const order = res.data;
        
        // 清空购物车（如果是购物车结算）
        if (this.data.orderType === 'cart') {
          const cart = storage.getCart();
          const remainingCart = cart.filter(item => {
            return !orderItems.find(orderItem => orderItem.id === item.id);
          });
          storage.saveCart(remainingCart);
          app.updateCartCount();
        }
        
        // 清除临时数据
        wx.removeStorageSync('checkoutData');
        wx.removeStorageSync('buyNowItem');
        
        // 跳转到支付页面
        wx.redirectTo({
          url: `/pages/payment/payment?orderNo=${order.orderNo}`
        });
      } else {
        throw new Error(res.message || '创建订单失败');
      }
      
    } catch (error) {
      console.error('提交订单失败:', error);
      wx.showToast({
        title: error.message || '提交订单失败',
        icon: 'none'
      });
    } finally {
      this.setData({ submitting: false });
    }
  }
});
