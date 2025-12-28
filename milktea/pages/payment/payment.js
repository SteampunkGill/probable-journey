const { orderApi, authApi, paymentApi } = require('../../utils/api')

Page({
  data: {
    orderNo: '',
    order: null,
    paymentMethod: 'ALIPAY',
    paying: false,
    countdown: 15 * 60,
    countdownText: '15:00',
    userBalance: '0.00'
  },

  onLoad(options) {
    const { orderNo } = options
    if (orderNo) {
      this.setData({ orderNo })
      this.loadOrderDetail()
      this.loadUserBalance()
      this.startCountdown()
    } else {
      wx.showToast({ title: '订单号缺失', icon: 'none' })
      setTimeout(() => wx.navigateBack(), 1500)
    }
  },

  onUnload() {
    if (this.timer) clearInterval(this.timer)
  },

  async loadOrderDetail() {
    try {
      const res = await orderApi.getOrderDetail(this.data.orderNo)
      const orderData = res.data || res
      if (orderData) {
        this.setData({ order: orderData })
      }
    } catch (error) {
      console.error('加载订单详情失败:', error)
    }
  },

  async loadUserBalance() {
    try {
      const res = await authApi.getUserProfile()
      const userData = res.data || res
      if (userData) {
        this.setData({ userBalance: (userData.balance || 0).toFixed(2) })
      }
    } catch (error) {
      console.error('加载余额失败:', error)
    }
  },

  startCountdown() {
    this.timer = setInterval(() => {
      if (this.data.countdown > 0) {
        const countdown = this.data.countdown - 1
        this.setData({
          countdown,
          countdownText: this.formatCountdown(countdown)
        })
      } else {
        clearInterval(this.timer)
        this.handleTimeout()
      }
    }, 1000)
  },

  handleTimeout() {
    wx.showModal({
      title: '支付超时',
      content: '订单支付已超时，请重新下单',
      showCancel: false,
      success: () => {
        wx.redirectTo({ url: '/pages/order-list/order-list' })
      }
    })
  },

  formatCountdown(seconds) {
    const minutes = Math.floor(seconds / 60)
    const secs = seconds % 60
    return `${minutes}:${secs.toString().padStart(2, '0')}`
  },

  selectMethod(e) {
    this.setData({ paymentMethod: e.currentTarget.dataset.method })
  },

  async submitPayment() {
    if (this.data.paying) return
    this.setData({ paying: true })
    
    try {
      const res = await paymentApi.confirmPayment(this.data.orderNo, this.data.paymentMethod)
      if (res.code === 200 || res.status === 'success') {
        wx.showToast({ title: '支付成功！', icon: 'success' })
        setTimeout(() => {
          wx.redirectTo({
            url: `/pages/order/detail?id=${this.data.orderNo}&status=success`
          })
        }, 1500)
      } else {
        wx.showToast({ title: res.message || '支付失败', icon: 'none' })
      }
    } catch (error) {
      console.error('支付请求失败:', error)
      wx.showToast({ title: '支付系统繁忙', icon: 'none' })
    } finally {
      this.setData({ paying: false })
    }
  },

  cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定要取消该订单吗？',
      success: (res) => {
        if (res.confirm) {
          wx.redirectTo({ url: '/pages/order-list/order-list' })
        }
      }
    })
  }
})