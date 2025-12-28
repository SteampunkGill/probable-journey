import { get } from '../../utils/request'

Page({
  data: {
    mode: 'list',
    tabs: [
      { key: 'available', name: '可使用' },
      { key: 'used', name: '已使用' },
      { key: 'expired', name: '已过期' }
    ],
    activeTab: 'available',
    coupons: [],
    orderAmount: 0
  },

  onLoad(options) {
    this.setData({
      mode: options.mode || 'list',
      orderAmount: parseFloat(options.amount || 0)
    })
    this.loadCoupons()
  },

  switchTab(e) {
    const key = e.currentTarget.dataset.key
    this.setData({ activeTab: key }, () => {
      this.loadCoupons()
    })
  },

  calculateDiscount(coupon) {
    const { orderAmount } = this.data
    if (!orderAmount) return 0
    let discount = 0
    const type = coupon.type?.toUpperCase()
    if (type === 'REDUCTION' || type === 'DISCOUNT_FIXED' || coupon.type === 'discount') {
      discount = coupon.value
    } else if (type === 'DISCOUNT' || type === 'PERCENTAGE' || coupon.type === 'percentage') {
      const rate = coupon.value < 1 ? coupon.value : coupon.value / 100
      discount = orderAmount * (1 - rate)
    }
    return discount
  },

  async loadCoupons() {
    wx.showLoading({ title: '加载中' })
    try {
      const res = await get('/api/coupons/my')
      const allCoupons = res.data || []
      const statusMap = {
        'available': 'UNUSED',
        'used': 'USED',
        'expired': 'EXPIRED'
      }
      
      let filtered = allCoupons.filter(c => c.status === statusMap[this.data.activeTab])
      
      if (this.data.mode === 'select' && this.data.activeTab === 'available' && this.data.orderAmount > 0) {
        filtered = filtered.map(c => ({
          ...c,
          _discount: this.calculateDiscount(c),
          _isUsable: this.data.orderAmount >= c.minAmount
        }))

        filtered.sort((a, b) => {
          if (a._isUsable !== b._isUsable) return b._isUsable ? 1 : -1
          return b._discount - a._discount
        })

        const usableCoupons = filtered.filter(c => c._isUsable)
        usableCoupons.slice(0, 3).forEach((c, index) => {
          c._recommend = true
          c._recommendText = index === 0 ? '省最多' : '推荐'
        })
      }
      
      this.setData({ coupons: filtered })
    } catch (error) {
      console.error('加载优惠券失败:', error)
    } finally {
      wx.hideLoading()
    }
  },

  useCoupon(e) {
    const coupon = e.currentTarget.dataset.item
    if (this.data.mode === 'select') {
      if (coupon._isUsable === false) {
        wx.showToast({
          title: `订单金额未满${coupon.minAmount}元`,
          icon: 'none'
        })
        return
      }
      wx.setStorageSync('selectedCoupon', coupon)
      wx.navigateBack()
    } else {
      wx.switchTab({ url: '/pages/index/index' })
    }
  }
})