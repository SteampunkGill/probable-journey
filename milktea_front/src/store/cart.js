import { defineStore } from 'pinia'
import { cartApi } from '../utils/api.js'

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: []
  }),
  getters: {
    totalCount: (state) => state.items.reduce((total, item) => total + item.quantity, 0),
    totalPrice: (state) => state.items.reduce((total, item) => total + (item.price + (item.toppingsCost || 0)) * item.quantity, 0)
  },
  actions: {
    async fetchCart() {
      try {
        const res = await cartApi.getCart()
        const data = res.data || res
        this.items = (Array.isArray(data) ? data : []).map(item => {
          let imageUrl = item.image || item.product?.mainImageUrl || item.product?.imageUrl
          if (imageUrl && !imageUrl.startsWith('http') && !imageUrl.startsWith('data:')) {
            const path = imageUrl.startsWith('/') ? imageUrl : `/${imageUrl}`
            imageUrl = `http://localhost:8081${path}`
          }
          return {
            ...item,
            image: imageUrl,
            selected: true,
            cartItemId: item.id
          }
        })
      } catch (error) {
        console.error('获取购物车失败', error)
        this.items = []
      }
    },
    async addToCart(product) {
      try {
        // 构建后端需要的参数
        const requestData = {
          storeId: product.storeId,
          productId: product.id,
          quantity: product.quantity || 1,
          sweetness: product.sweetness,
          temperature: product.temperature,
          specId: product.specId,
          toppings: product.toppings?.map(t => t.id) || []
        }
        await cartApi.addToCart(requestData)
        // 添加成功后重新获取购物车
        await this.fetchCart()
      } catch (error) {
        console.error('添加购物车失败', error)
        throw error
      }
    },
    async removeFromCart(cartItemId) {
      try {
        await cartApi.deleteCartItem(cartItemId)
        // 删除成功后重新获取购物车
        await this.fetchCart()
      } catch (error) {
        console.error('删除购物车项失败', error)
        throw error
      }
    },
    async updateQuantity(cartItemId, quantity) {
      try {
        await cartApi.updateCartItem(cartItemId, { quantity })
        // 更新成功后重新获取购物车
        await this.fetchCart()
      } catch (error) {
        console.error('更新数量失败', error)
        throw error
      }
    },
    async clearCart() {
      try {
        await cartApi.clearCart()
        this.items = []
      } catch (error) {
        console.error('清空购物车失败', error)
        throw error
      }
    },
    // 本地辅助方法，用于选中/取消选中
    toggleSelect(cartItemId) {
      const item = this.items.find(item => item.cartItemId === cartItemId)
      if (item) {
        item.selected = !item.selected
      }
    },
    toggleSelectAll(selected) {
      this.items.forEach(item => item.selected = selected)
    },
    removeSelected() {
      const selectedIds = this.items.filter(item => item.selected).map(item => item.cartItemId)
      selectedIds.forEach(id => this.removeFromCart(id))
    }
  }
})