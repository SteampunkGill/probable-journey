import { get, post } from '../../utils/request'

Page({
  data: {
    loading: true,
    submitting: false,
    deliveryType: 'delivery',
    orderItems: [],
    selectedAddress: null,
    selectedStore: null,
    selectedCoupon: null,
    availableCoupons: [],
    usePoints: false,
    pointsToUse: 0,
    availablePoints: 0,
    remark: '',
    estimatedDeliveryTime: '预计30分钟送达',
    subtotal: '0.00',
    deliveryFee: '0.00',
    packagingFee: '0.00',
    couponDiscount: '0.00',
    pointsDiscount: '0.00',
    totalAmount: '0.00',
    isBestCoupon: false
  },

  onLoad() {
    this.loadOrderData()
  },

  onShow() {
    // 检查是否有从地址选择或优惠券选择页面返回的数据
    const selectedAddress = wx.getStorageSync('selectedAddress')
    if (selectedAddress) {
      this.setData({ selectedAddress })
      wx.removeStorageSync('selectedAddress')
    }

    const selectedCoupon = wx.getStorageSync('selectedCoupon')
    if (selectedCoupon) {
      this.setData({ selectedCoupon }, () => {
        this.calculateFees()
      })
      wx.removeStorageSync('selectedCoupon')
    }

    const selectedStore = wx.getStorageSync('selectedStore')
    if (selectedStore) {
      this.setData({ selectedStore })
      wx.removeStorageSync('selectedStore')
    }
  },

  async loadOrderData() {
    this.setData({ loading: true })
    try {
      const items = wx.getStorageSync('checkoutItems')
      const remark = wx.getStorageSync('orderRemark') || ''
      
      if (items) {
        this.setData({ 
          orderItems: items,
          remark: remark
        })
      }

      // 并行获取地址、优惠券和用户信息
      const [addressRes, couponRes, profileRes] = await Promise.all([
        get('/api/address'),
        get('/api/coupons/my'),
        get('/api/user/profile')
      ])

      if (addressRes.data && addressRes.data.length > 0) {
        const defaultAddr = addressRes.data.find(a => a.isDefault) || addressRes.data[0]
        this.setData({ selectedAddress: defaultAddr })
      }

      if (profileRes.data) {
        this.setData({ availablePoints: profileRes.data.points || 0 })
      }

      if (couponRes.data) {
        const amount = parseFloat(this.calculateSubtotal())
        const available = couponRes.data.filter(c => 
          c.status === 'UNUSED' && amount >= c.minAmount
        )
        this.setData({ availableCoupons: available })
        
        // 自动选择最佳优惠券
        if (available.length > 0) {
          const best = this.findBestCoupon(available, amount)
          this.setData({ selectedCoupon: best })
        }
      }

      this.calculateFees()
    } catch (error) {
      console.error('加载结算数据失败', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  calculateSubtotal() {
    const subtotal = this.data.orderItems.reduce((total, item) => total + parseFloat(item.price) * item.quantity, 0)
    return subtotal.toFixed(2)
  },

  findBestCoupon(coupons, amount) {
    return coupons.map(coupon => {
      let discount = 0
      const type = coupon.type?.toUpperCase()
      if (type === 'REDUCTION' || type === 'DISCOUNT_FIXED' || coupon.type === 'discount') {
        discount = coupon.value
      } else if (type === 'DISCOUNT' || type === 'PERCENTAGE' || coupon.type === 'percentage') {
        const rate = coupon.value < 1 ? coupon.value : coupon.value / 100
        discount = amount * (1 - rate)
      }
      return { ...coupon, _discount: discount }
    }).sort((a, b) => b._discount - a._discount)[0]
  },

  calculateFees() {
    const subtotalVal = parseFloat(this.calculateSubtotal())
    const deliveryFeeVal = this.data.deliveryType === 'pickup' ? 0 : (subtotalVal >= 30 ? 0 : 5)
    const packagingFeeVal = this.data.orderItems.length * 0.5
    
    let couponDiscountVal = 0
    if (this.data.selectedCoupon) {
      const coupon = this.data.selectedCoupon
      const type = coupon.type?.toUpperCase()
      if (type === 'REDUCTION' || type === 'DISCOUNT_FIXED' || coupon.type === 'discount') {
        couponDiscountVal = coupon.value
      } else if (type === 'DISCOUNT' || type === 'PERCENTAGE' || coupon.type === 'percentage') {
        const rate = coupon.value < 1 ? coupon.value : coupon.value / 100
        couponDiscountVal = subtotalVal * (1 - rate)
      }
    }

    const pointsDiscountVal = this.data.usePoints ? (this.data.pointsToUse / 100) : 0
    
    const totalAmountVal = Math.max(0, subtotalVal + deliveryFeeVal + packagingFeeVal - couponDiscountVal - pointsDiscountVal)

    this.setData({
      subtotal: subtotalVal.toFixed(2),
      deliveryFee: deliveryFeeVal.toFixed(2),
      packagingFee: packagingFeeVal.toFixed(2),
      couponDiscount: couponDiscountVal.toFixed(2),
      pointsDiscount: pointsDiscountVal.toFixed(2),
      totalAmount: totalAmountVal.toFixed(2)
    })

    // 检查是否为最优优惠券
    if (this.data.selectedCoupon && this.data.availableCoupons.length > 0) {
      const best = this.findBestCoupon(this.data.availableCoupons, subtotalVal)
      this.setData({ isBestCoupon: this.data.selectedCoupon.id === best.id })
    }
  },

  switchDeliveryType(e) {
    const type = e.currentTarget.dataset.type
    this.setData({ deliveryType: type }, () => {
      this.calculateFees()
    })
  },

  onPointsChange(e) {
    const usePoints = e.detail.value
    if (usePoints) {
      const maxPointsForOrder = Math.floor(parseFloat(this.data.subtotal) * 100)
      const pointsToUse = Math.min(this.data.availablePoints, maxPointsForOrder)
      this.setData({ usePoints, pointsToUse }, () => {
        this.calculateFees()
      })
    } else {
      this.setData({ usePoints, pointsToUse: 0 }, () => {
        this.calculateFees()
      })
    }
  },

  onPointsInput(e) {
    let val = parseInt(e.detail.value) || 0
    const maxPointsForOrder = Math.floor(parseFloat(this.data.subtotal) * 100)
    val = Math.min(val, this.data.availablePoints, maxPointsForOrder)
    this.setData({ pointsToUse: val }, () => {
      this.calculateFees()
    })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  goToAddress() {
    wx.navigateTo({ url: '/pages/address/address?mode=select' })
  },

  goToStore() {
    // 假设点单页可以选门店
    wx.navigateTo({ url: '/pages/order/order?mode=select' })
  },

  goToCoupon() {
    wx.navigateTo({ url: `/pages/coupon/coupon?mode=select&amount=${this.data.subtotal}` })
  },

  async submitOrder() {
    if (this.data.submitting) return
    
    if (this.data.deliveryType === 'delivery' && !this.data.selectedAddress) {
      wx.showToast({ title: '请选择收货Address', icon: 'none' })
      return
    }
    
    this.setData({ submitting: true })
    try {
      const orderData = {
        items: this.data.orderItems.map(item => ({
          productId: item.productId,
          quantity: item.quantity,
          price: parseFloat(item.price),
          specId: item.specId,
          customizations: item.customizations
        })),
        deliveryType: this.data.deliveryType.toUpperCase(),
        addressId: this.data.selectedAddress?.id,
        storeId: this.data.selectedStore?.id || 1, // 默认门店
        couponId: this.data.selectedCoupon?.id,
        usePoints: this.data.usePoints ? this.data.pointsToUse : 0,
        remark: this.data.remark,
        deliveryFee: parseFloat(this.data.deliveryFee),
        packagingFee: parseFloat(this.data.packagingFee),
        totalAmount: parseFloat(this.data.totalAmount)
      }
      
      const res = await post('/api/orders', orderData)
      if (res.data) {
        wx.showToast({ title: '订单提交成功' })
        // 清除缓存
        wx.removeStorageSync('checkoutItems')
        wx.removeStorageSync('orderRemark')
        
        // 跳转支付
        wx.navigateTo({ 
          url: `/pages/payment/payment?orderNo=${res.data.orderNo}&amount=${this.data.totalAmount}` 
        })
      }
    } catch (error) {
      console.error('提交订单失败', error)
      wx.showToast({ title: '提交失败，请重试', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  }
})