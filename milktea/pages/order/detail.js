const { orderApi } = require('../../utils/api')
const { formatImageUrl } = require('../../utils/util')

Page({
  data: {
    loading: true,
    order: null,
    statusSteps: [],
    currentStep: 0
  },

  onLoad(options) {
    const { id } = options
    if (id) {
      this.loadOrderDetail(id)
    } else {
      wx.showToast({
        title: '订单号缺失',
        icon: 'none'
      })
      setTimeout(() => wx.navigateBack(), 1500)
    }
  },

  async loadOrderDetail(id) {
    this.setData({ loading: true })
    try {
      const res = await orderApi.getOrderDetail(id)
      // 兼容后端返回结构
      const orderData = res.data || res
      if (orderData) {
        this.setData({
          order: this.formatOrderData(orderData)
        })
        this.generateStatusSteps(orderData)
      }
    } catch (error) {
      console.error('加载订单详情失败:', error)
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      })
    } finally {
      this.setData({ loading: false })
    }
  },

  formatOrderData(order) {
    // 格式化图片和状态文本
    if (order.items) {
      order.items = order.items.map(item => ({
        ...item,
        image: formatImageUrl(item.image || item.product?.mainImageUrl || item.product?.imageUrl)
      }))
    }
    
    // 状态映射
    const statusTextMap = {
      'pending_payment': '待支付',
      'paid': '已支付',
      'processing': '制作中',
      'delivering': '配送中',
      'ready': '待取餐',
      'completed': '已完成',
      'cancelled': '已取消'
    }
    order.statusText = statusTextMap[order.status] || order.status

    // 支付方式映射
    const paymentMethodMap = {
      'WECHAT': '微信支付',
      'ALIPAY': '支付宝支付',
      'BALANCE': '余额支付'
    }
    order.paymentMethodText = paymentMethodMap[order.paymentMethod] || '在线支付'

    return order
  },

  generateStatusSteps(order) {
    const createTime = order.createTime || ''
    const payTime = order.payTime || ''
    let steps = []
    
    if (order.deliveryType === 'delivery') {
      steps = [
        { key: 'pending_payment', title: '订单已提交', time: createTime },
        { key: 'paid', title: '支付成功', time: payTime },
        { key: 'processing', title: '商家制作中', time: '' },
        { key: 'delivering', title: '配送中', time: '' },
        { key: 'completed', title: '订单完成', time: '' }
      ]
    } else {
      steps = [
        { key: 'pending_payment', title: '订单已提交', time: createTime },
        { key: 'paid', title: '支付成功', time: payTime },
        { key: 'processing', title: '商家制作中', time: '' },
        { key: 'ready', title: '已备餐', time: '' },
        { key: 'completed', title: '已取餐', time: '' }
      ]
    }

    // 根据状态设置当前步骤
    const statusMap = {
      'pending_payment': 0,
      'paid': 1,
      'processing': 2,
      'delivering': 3,
      'ready': 3,
      'completed': 4,
      'cancelled': -1
    }
    
    this.setData({
      statusSteps: steps,
      currentStep: statusMap[order.status] ?? 0
    })
  },

  copyText(e) {
    const text = e.currentTarget.dataset.text
    wx.setClipboardData({
      data: text,
      success: () => {
        wx.showToast({ title: '已复制', icon: 'success' })
      }
    })
  },

  callPhone(e) {
    const phone = e.currentTarget.dataset.phone
    if (phone) {
      wx.makePhoneCall({ phoneNumber: phone })
    }
  },

  async cancelOrder() {
    wx.showModal({
      title: '提示',
      content: '确定要取消订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await orderApi.cancelOrder(this.data.order.orderNo)
            wx.showToast({ title: '订单已取消' })
            this.loadOrderDetail(this.data.order.orderNo)
          } catch (e) {
            wx.showToast({ title: '取消失败', icon: 'none' })
          }
        }
      }
    })
  },

  payOrder() {
    wx.navigateTo({
      url: `/pages/payment/payment?orderNo=${this.data.order.orderNo}&amount=${this.data.order.totalAmount}`
    })
  },

  async remindOrder() {
    try {
      await orderApi.remindOrder(this.data.order.orderNo)
      wx.showToast({ title: '已提醒商家' })
    } catch (e) {
      wx.showToast({ title: '提醒失败', icon: 'none' })
    }
  },

  reviewOrder() {
    wx.navigateTo({
      url: `/pages/review/review?orderNo=${this.data.order.orderNo}`
    })
  },

  reorder() {
    // 简化逻辑：跳转到点单页
    wx.switchTab({ url: '/pages/order/order' })
  },

  contactService() {
    wx.makePhoneCall({ phoneNumber: '400-123-4567' })
  }
})