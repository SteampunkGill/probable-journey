import { get, post, del } from '../../utils/request'

Page({
  data: {
    productId: '',
    product: {},
    currentImageIndex: 0,
    isFavorite: false,
    activeTab: 0,
    loading: true,
    customizations: {
      sweetness: '',
      temperature: '',
      toppings: [],
      quantity: 1
    },
    sweetnessOptions: [],
    temperatureOptions: [],
    toppingOptions: [],
    maxToppings: 5,
    comments: [],
    cartCount: 0,
    totalPrice: '0.00'
  },

  onLoad(options) {
    if (options.id) {
      this.setData({ productId: options.id })
      this.loadProductDetail()
    }
  },

  onShow() {
    this.updateCartCount()
  },

  async loadProductDetail() {
    this.setData({ loading: true })
    try {
      const detailRes = await get(`/api/products/${this.data.productId}`)
      if (detailRes.data) {
        this.setData({ product: detailRes.data })
      }

      // 检查收藏
      try {
        const favRes = await get(`/api/favorites/check/${this.data.productId}`)
        this.setData({ isFavorite: favRes.data })
      } catch (e) {}

      // 获取规格
      const customRes = await get(`/api/products/${this.data.productId}/customizations`)
      if (customRes.data) {
        const { specs, options } = customRes.data
        
        const sweetnessSpec = specs.find(s => s.type === 'SWEETNESS')
        const sweetnessOptions = sweetnessSpec?.items.map(item => ({
          value: item.name,
          label: item.name,
          hint: item.description
        })) || []

        const tempSpec = specs.find(s => s.type === 'TEMPERATURE')
        const temperatureOptions = tempSpec?.items.map(item => ({
          value: item.name,
          label: item.name
        })) || []

        const toppingOptions = options.filter(o => o.type === 'TOPPING')

        this.setData({
          sweetnessOptions,
          temperatureOptions,
          toppingOptions,
          'customizations.sweetness': sweetnessOptions[0]?.value || '',
          'customizations.temperature': temperatureOptions[0]?.value || ''
        })
        this.calculateTotalPrice()
      }

      // 获取评价
      const reviewsRes = await get(`/api/products/${this.data.productId}/reviews`)
      this.setData({ comments: reviewsRes.data || [] })

    } catch (error) {
      console.error('加载详情失败', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  calculateTotalPrice() {
    const basePrice = this.data.product.price || 0
    const toppingsPrice = this.data.customizations.toppings.reduce((sum, id) => {
      const topping = this.data.toppingOptions.find(t => t.id === id)
      return sum + (topping?.price || 0)
    }, 0)
    const total = ((basePrice + toppingsPrice) * this.data.customizations.quantity).toFixed(2)
    this.setData({ totalPrice: total })
  },

  onImageChange(e) {
    this.setData({ currentImageIndex: e.detail.current })
  },

  async toggleFavorite() {
    try {
      if (this.data.isFavorite) {
        await del(`/api/favorites/${this.data.productId}`)
        this.setData({ isFavorite: false })
        wx.showToast({ title: '已取消收藏', icon: 'none' })
      } else {
        await post(`/api/favorites/${this.data.productId}`)
        this.setData({ isFavorite: true })
        wx.showToast({ title: '已收藏', icon: 'none' })
      }
    } catch (e) {
      wx.showToast({ title: '操作失败', icon: 'none' })
    }
  },

  selectSweetness(e) {
    this.setData({ 'customizations.sweetness': e.currentTarget.dataset.value })
  },

  selectTemperature(e) {
    this.setData({ 'customizations.temperature': e.currentTarget.dataset.value })
  },

  toggleTopping(e) {
    const topping = e.currentTarget.dataset.item
    let toppings = [...this.data.customizations.toppings]
    const index = toppings.indexOf(topping.id)
    
    if (index > -1) {
      toppings.splice(index, 1)
    } else {
      if (toppings.length >= this.data.maxToppings) {
        wx.showToast({ title: `最多选${this.data.maxToppings}种`, icon: 'none' })
        return
      }
      toppings.push(topping.id)
    }
    this.setData({ 'customizations.toppings': toppings })
    this.calculateTotalPrice()
  },

  switchTab(e) {
    this.setData({ activeTab: parseInt(e.currentTarget.dataset.index) })
  },

  updateCartCount() {
    const cart = wx.getStorageSync('cart') || []
    const count = cart.reduce((total, item) => total + item.quantity, 0)
    this.setData({ cartCount: count })
  },

  addToCart() {
    let cart = wx.getStorageSync('cart') || []
    const cartItem = {
      id: this.data.product.id,
      name: this.data.product.name,
      image: this.data.product.images[0],
      price: this.data.product.price,
      quantity: this.data.customizations.quantity,
      customizations: { ...this.data.customizations },
      totalPrice: this.data.totalPrice
    }
    cart.push(cartItem)
    wx.setStorageSync('cart', cart)
    this.updateCartCount()
    wx.showToast({ title: '已加入购物车' })
  },

  buyNow() {
    const buyNowItem = {
      id: this.data.product.id,
      name: this.data.product.name,
      image: this.data.product.images[0],
      price: this.data.product.price,
      quantity: this.data.customizations.quantity,
      customizations: { ...this.data.customizations },
      totalPrice: this.data.totalPrice
    }
    wx.setStorageSync('buyNowItem', buyNowItem)
    wx.navigateTo({ url: '/pages/order/checkout?type=buyNow' })
  },

  goToCart() {
    wx.navigateTo({ url: '/pages/cart/cart' })
  }
})