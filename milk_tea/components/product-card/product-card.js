// components/product-card/product-card.js
const storage = require('../../utils/storage.js');

Component({
  properties: {
    product: {
      type: Object,
      value: {}
    },
    showFavorite: {
      type: Boolean,
      value: true
    },
    showRating: {
      type: Boolean,
      value: true
    }
  },

  data: {
    isFavorite: false
  },

  observers: {
    'product.id': function(productId) {
      if (productId) {
        this.checkFavoriteStatus();
      }
    }
  },

  lifetimes: {
    attached() {
      this.checkFavoriteStatus();
    }
  },

  methods: {
    // 检查收藏状态
    checkFavoriteStatus() {
      const { product } = this.data;
      if (product && product.id) {
        const isFavorite = storage.isProductFavorite(product.id);
        this.setData({ isFavorite });
      }
    },

    // 点击商品卡片
    onTap(e) {
      const { product } = this.data;
      if (product && product.id) {
        this.triggerEvent('tap', { productId: product.id });
        
        // 跳转到商品详情页
        wx.navigateTo({
          url: `/pages/product/product?id=${product.id}`
        });
      }
    },

    // 点击收藏按钮
    onFavoriteTap() {
      const { product, isFavorite } = this.data;
      if (!product || !product.id) return;

      const newFavoriteStatus = !isFavorite;
      
      if (newFavoriteStatus) {
        storage.addFavoriteProduct(product.id);
        wx.showToast({
          title: '已收藏',
          icon: 'success',
          duration: 1000
        });
      } else {
        storage.removeFavoriteProduct(product.id);
        wx.showToast({
          title: '已取消收藏',
          icon: 'success',
          duration: 1000
        });
      }
      
      this.setData({ isFavorite: newFavoriteStatus });
      this.triggerEvent('favoritechange', { 
        productId: product.id, 
        isFavorite: newFavoriteStatus 
      });
    },

    // 添加到购物车
    onAddToCart() {
      const { product } = this.data;
      if (!product || !product.id) return;

      // 获取当前购物车
      const cart = storage.getCart();
      
      // 查找是否已在购物车中
      const existingItemIndex = cart.findIndex(item => item.id === product.id);
      
      if (existingItemIndex >= 0) {
        // 数量加1
        cart[existingItemIndex].quantity += 1;
      } else {
        // 添加到购物车
        cart.push({
          id: product.id,
          name: product.name,
          price: product.price,
          image: product.image || product.mainImage,
          quantity: 1,
          customizations: {
            sweetness: 'normal',
            temperature: 'normal',
            toppings: []
          }
        });
      }
      
      // 保存购物车
      storage.saveCart(cart);
      
      // 更新全局购物车数量
      const app = getApp();
      app.updateCartCount();
      
      // 显示成功提示
      wx.showToast({
        title: '已加入购物车',
        icon: 'success',
        duration: 1500
      });
      
      // 震动反馈
      wx.vibrateShort();
      
      this.triggerEvent('addtocart', { productId: product.id });
    },

    // 停止事件冒泡
    stopPropagation() {
      // 空函数，用于阻止事件冒泡
    }
  }
});