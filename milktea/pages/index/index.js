import { get } from '../../utils/request'

Page({
  data: {
    loading: true,
    isLocating: false,
    scrollTop: 0,
    banners: [],
    recommendProducts: [],
    hotProducts: [],
    availableCouponCount: 0,
    showNotification: false,
    currentNotification: null,
    selectedStore: null,
    cartCount: 0,
    cartAnimating: false
  },

  onLoad() {
    this.loadData()
  },

  onShow() {
    this.updateCartCount()
    // 检查是否有手动选择的门店
    const selectedStore = wx.getStorageSync('selectedStore')
    if (selectedStore) {
      this.setData({ selectedStore })
    }
  },

  async loadData() {
    this.setData({ loading: true })
    try {
      // 并行获取首页数据、轮播图、推荐商品
      const [homeRes, bannersRes, recommendRes] = await Promise.all([
        get('/api/home/data'),
        get('/api/banners'),
        get('/api/home/recommendations')
      ])

      this.setData({
        banners: bannersRes.data || [],
        recommendProducts: recommendRes.data || [],
        hotProducts: homeRes.data?.hotProducts || []
      })

      // 获取定位和附近门店
      this.getLocationAndNearbyStore()

      // 获取优惠券数量
      this.loadCouponCount()

    } catch (error) {
      console.error('加载首页数据失败', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  async getLocationAndNearbyStore() {
    this.setData({ isLocating: true })
    wx.getLocation({
      type: 'gcj02',
      success: async (res) => {
        try {
          const nearbyRes = await get('/api/stores/nearby', {
            latitude: res.latitude,
            longitude: res.longitude
          })
          if (nearbyRes.data && nearbyRes.data.length > 0) {
            const store = nearbyRes.data[0]
            this.setData({ selectedStore: store })
            wx.setStorageSync('selectedStore', store)
          }
        } catch (e) {
          console.error('获取附近门店失败', e)
        }
      },
      fail: () => {
        wx.showToast({ title: '定位失败，请手动选择门店', icon: 'none' })
      },
      complete: () => {
        this.setData({ isLocating: false })
      }
    })
  },

  async loadCouponCount() {
    try {
      const res = await get('/api/coupons/my')
      if (res.data) {
        const count = res.data.filter(c => c.status === 'UNUSED').length
        this.setData({ availableCouponCount: count })
      }
    } catch (e) {
      console.warn('获取优惠券数量失败')
    }
  },

  updateCartCount() {
    const cart = wx.getStorageSync('cart') || []
    const count = cart.reduce((total, item) => total + item.quantity, 0)
    this.setData({ cartCount: count })
  },

  onScroll(e) {
    this.setData({ scrollTop: e.detail.scrollTop })
  },

  onScan() {
    wx.scanCode({
      success: (res) => {
        console.log('扫码结果', res)
      }
    })
  },

  reLocation() {
    this.getLocationAndNearbyStore()
  },

  selectOrderMode(e) {
    const mode = e.currentTarget.dataset.mode
    wx.setStorageSync('orderMode', mode)
    wx.switchTab({ url: '/pages/order/order' })
  },

  quickAdd(e) {
    const product = e.currentTarget.dataset.item
    let cart = wx.getStorageSync('cart') || []
    const index = cart.findIndex(item => item.id === product.id)
    
    if (index > -1) {
      cart[index].quantity += 1
    } else {
      cart.push({ ...product, quantity: 1 })
    }
    
    wx.setStorageSync('cart', cart)
    this.updateCartCount()
    this.setData({ cartAnimating: true })
    setTimeout(() => this.setData({ cartAnimating: false }), 600)
    wx.showToast({ title: '已加入购物车', icon: 'none' })
  },

  goToSearch() { wx.navigateTo({ url: '/pages/search/search' }) },
  goToSelectStore() { wx.navigateTo({ url: '/pages/address/address?type=select_store' }) },
  goToOrder() { wx.switchTab({ url: '/pages/order/order' }) },
  goToCoupon() { wx.navigateTo({ url: '/pages/coupon/coupon' }) },
  goToWallet() { wx.navigateTo({ url: '/pages/wallet/wallet' }) },
  goToShare() { wx.navigateTo({ url: '/pages/share/share' }) },
  goToCart() { wx.navigateTo({ url: '/pages/cart/cart' }) },
  goToProductDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product/detail?id=${id}` })
  },

  makePhoneCall(e) {
    const phone = e.currentTarget.dataset.phone
    if (phone) wx.makePhoneCall({ phoneNumber: phone })
  },

  onBannerClick(e) {
    const item = e.currentTarget.dataset.item
    if (item.link) wx.navigateTo({ url: item.link })
  },

  closeNotification() {
    this.setData({ showNotification: false })
  }
})