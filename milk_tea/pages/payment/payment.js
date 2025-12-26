// pages/payment/payment.js
const app = getApp();

Page({
  data: {
    orderId: '',
    order: null,
    paymentMethod: 'wechat', // wechat, balance
    paying: false,
    countdown: 15 * 60, // 15分钟倒计时
    timer: null
  },

  onLoad(options) {
    const orderNo = options.orderNo || options.orderId;
    if (orderNo) {
      this.setData({ orderId: orderNo });
      this.loadOrderDetail();
      this.startCountdown();
    }
  },

  onUnload() {
    if (this.data.timer) {
      clearInterval(this.data.timer);
    }
  },

  // 加载订单详情
  async loadOrderDetail() {
    try {
      const { orderApi } = require('../../utils/api.js');
      const res = await orderApi.getOrderDetail(this.data.orderId);
      if (res.code === 200) {
        this.setData({ order: res.data });
      } else {
        throw new Error(res.message);
      }
    } catch (error) {
      console.error('加载订单详情失败:', error);
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  // 开始倒计时
  startCountdown() {
    this.data.timer = setInterval(() => {
      let countdown = this.data.countdown - 1;
      
      if (countdown <= 0) {
        clearInterval(this.data.timer);
        this.handleTimeout();
      }
      
      this.setData({ countdown });
    }, 1000);
  },

  // 处理超时
  handleTimeout() {
    wx.showModal({
      title: '订单已超时',
      content: '订单支付已超时，请重新下单',
      showCancel: false,
      success: () => {
        wx.redirectTo({
          url: '/pages/order-list/order-list'
        });
      }
    });
  },

  // 格式化倒计时
  formatCountdown(seconds) {
    const minutes = Math.floor(seconds / 60);
    const secs = seconds % 60;
    return `${minutes}:${secs.toString().padStart(2, '0')}`;
  },

  // 切换支付方式
  switchPaymentMethod(e) {
    const method = e.currentTarget.dataset.method;
    this.setData({ paymentMethod: method });
  },

  // 提交支付
  async submitPayment() {
    const { orderId, paymentMethod, paying } = this.data;
    
    if (paying) return;
    
    this.setData({ paying: true });
    
    try {
      if (paymentMethod === 'wechat') {
        await this.payWithWechat();
      } else if (paymentMethod === 'balance') {
        await this.payWithBalance();
      }
    } catch (error) {
      console.error('支付失败:', error);
      wx.showToast({
        title: error.message || '支付失败',
        icon: 'none'
      });
      this.setData({ paying: false });
    }
  },

  // 微信支付
  async payWithWechat() {
    try {
      // 模拟获取支付参数
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 实际应该调用后端获取支付参数
      const paymentParams = {
        timeStamp: Date.now().toString(),
        nonceStr: 'randomstring',
        package: 'prepay_id=wx123456789',
        signType: 'RSA',
        paySign: 'sign123456'
      };
      
      // 调起微信支付
      await wx.requestPayment(paymentParams);
      
      // 支付成功
      this.handlePaymentSuccess();
      
    } catch (error) {
      if (error.errMsg === 'requestPayment:fail cancel') {
        wx.showToast({
          title: '支付已取消',
          icon: 'none'
        });
      } else {
        throw error;
      }
      this.setData({ paying: false });
    }
  },

  // 余额支付
  async payWithBalance() {
    try {
      // 模拟支付
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 支付成功
      this.handlePaymentSuccess();
      
    } catch (error) {
      throw error;
    }
  },

  // 处理支付成功
  handlePaymentSuccess() {
    // 清除倒计时
    if (this.data.timer) {
      clearInterval(this.data.timer);
    }
    
    // 跳转到支付结果页
    wx.redirectTo({
      url: `/pages/payment/result?orderId=${this.data.orderId}&status=success`
    });
  },

  // 取消订单
  cancelOrder() {
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          this.handleCancelOrder();
        }
      }
    });
  },

  // 处理取消订单
  async handleCancelOrder() {
    try {
      // 模拟取消订单
      await new Promise(resolve => setTimeout(resolve, 500));
      
      wx.showToast({
        title: '订单已取消',
        icon: 'success',
        duration: 1500
      });
      
      setTimeout(() => {
        wx.redirectTo({
          url: '/pages/order-list/order-list'
        });
      }, 1500);
      
    } catch (error) {
      console.error('取消订单失败:', error);
      wx.showToast({
        title: '取消失败',
        icon: 'none'
      });
    }
  }
});
