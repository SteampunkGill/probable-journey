import { get, post, put, del } from '../../utils/request'

Page({
  data: {
    isEditing: false,
    remark: '',
    availableCouponCount: 0,
    cartItems: [],
    isAllSelected: true,
    selectedCount: 0,
    totalAmount: '0.00'
  },

  onShow() {
    this.fetchCart()
    this.loadAvailableCouponCount()
  },

  async fetchCart() {
    try {
      const res = await get('/api/cart')
      const data = res.data || res
      const items = (Array.isArray(data) ? data : []).map(item => {
        const imageUrl = item.image || item.productImage || item.product?.mainImageUrl || item.product?.imageUrl
        const name = item.name || item.productName || item.product?.name
        const price = item.price || 0
        const quantity = item.quantity || 1
        return {
          ...item,
          cartItemId: item.id,
          productId: item.productId || item.product?.id,
          name: name,
          image: imageUrl, // 假设 request.js 或后端已处理好路径
          price: price.toFixed(2),
          quantity: quantity,
          subtotal: (price * quantity).toFixed(2),
          selected: true
        }
      })
      this.setData({ cartItems: items }, () => {
        this.calculateTotal()
      })
    } catch (error) {
      console.error('获取购物车失败', error)
      this.setData({ cartItems: [] })
    }
  },

  async loadAvailableCouponCount() {
    try {
      const res = await get('/api/coupons/my')
      if (res.data) {
        const availableCoupons = res.data.filter(coupon => 
          coupon.status === 'AVAILABLE' || coupon.status === 'UNUSED'
        )
        this.setData({ availableCouponCount: availableCoupons.length })
      }
    } catch (error) {
      console.error('获取优惠券数量失败', error)
    }
  },

  toggleEdit() {
    this.setData({ isEditing: !this.data.isEditing })
  },

  toggleSelect(e) {
    const id = e.currentTarget.dataset.id
    const items = this.data.cartItems.map(item => {
      if (item.cartItemId === id) {
        return { ...item, selected: !item.selected }
      }
      return item
    })
    this.setData({ cartItems: items }, () => {
      this.calculateTotal()
    })
  },

  toggleSelectAll() {
    const target = !this.data.isAllSelected
    const items = this.data.cartItems.map(item => ({ ...item, selected: target }))
    this.setData({
      cartItems: items,
      isAllSelected: target
    }, () => {
      this.calculateTotal()
    })
  },

  calculateTotal() {
    const { cartItems } = this.data
    const selectedItems = cartItems.filter(item => item.selected)
    const count = selectedItems.reduce((total, item) => total + item.quantity, 0)
    const amount = selectedItems.reduce((total, item) => total + parseFloat(item.price) * item.quantity, 0)
    const isAllSelected = cartItems.length > 0 && cartItems.every(item => item.selected)
    
    this.setData({
      selectedCount: count,
      totalAmount: amount.toFixed(2),
      isAllSelected
    })
  },

  async decreaseQuantity(e) {
    const item = e.currentTarget.dataset.item
    if (item.quantity > 1) {
      await this.updateQuantity(item.cartItemId, item.quantity - 1)
    }
  },

  async increaseQuantity(e) {
    const item = e.currentTarget.dataset.item
    await this.updateQuantity(item.cartItemId, item.quantity + 1)
  },

  async updateQuantity(cartItemId, quantity) {
    try {
      await put(`/api/cart/${cartItemId}`, { quantity })
      this.fetchCart()
    } catch (error) {
      console.error('更新数量失败', error)
    }
  },

  async deleteItem(e) {
    const id = e.currentTarget.dataset.id
    wx.showModal({
      title: '提示',
      content: '确定删除该商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await del(`/api/cart/${id}`)
            this.fetchCart()
          } catch (error) {
            console.error('删除失败', error)
          }
        }
      }
    })
  },

  async deleteSelectedItems() {
    const selectedIds = this.data.cartItems.filter(item => item.selected).map(item => item.cartItemId)
    if (selectedIds.length === 0) return

    wx.showModal({
      title: '提示',
      content: '确定删除选中的商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            // 循环删除或调用批量接口（如果后端支持）
            await Promise.all(selectedIds.map(id => del(`/api/cart/${id}`)))
            this.fetchCart()
          } catch (error) {
            console.error('批量删除失败', error)
          }
        }
      }
    })
  },

  async clearCart() {
    wx.showModal({
      title: '提示',
      content: '确定要清空购物车吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await del('/api/cart/clear')
            this.setData({ cartItems: [] }, () => {
              this.calculateTotal()
            })
          } catch (error) {
            console.error('清空失败', error)
          }
        }
      }
    })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  goToHome() {
    wx.switchTab({ url: '/pages/index/index' })
  },

  goToDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/product/detail?id=${id}` })
  },

  goToCoupon() {
    wx.navigateTo({ url: '/pages/coupon/coupon' })
  },

  goToCheckout() {
    const selectedItems = this.data.cartItems.filter(item => item.selected)
    // 使用小程序 storage 传递数据
    wx.setStorageSync('checkoutItems', selectedItems)
    wx.setStorageSync('orderRemark', this.data.remark)
    wx.navigateTo({ url: '/pages/order/checkout' })
  }
})