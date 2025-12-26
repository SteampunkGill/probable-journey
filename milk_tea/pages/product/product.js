// pages/product/product.js
const app = getApp();
const storage = require('../../utils/storage.js');
const util = require('../../utils/util.js');
const { productApi } = require('../../utils/api.js');

Page({
  data: {
    // 商品ID
    productId: '',
    
    // 商品信息
    product: {
      id: '',
      name: '',
      images: [],
      price: 0,
      originalPrice: 0,
      description: '',
      rating: 4.5,
      sales: 0,
      stock: 100,
      isHot: false,
      isNew: false,
      isRecommend: false,
      detailHtml: '',
      ingredients: [],
      nutrition: [],
      allergens: [],
      commentCount: 0
    },
    
    // 当前图片索引
    currentImageIndex: 0,
    
    // 是否收藏
    isFavorite: false,
    
    // 定制化选项
    customizations: {
      sweetness: 'normal',      // 默认正常糖
      temperature: 'normal',    // 默认正常冰
      toppings: [],            // 加料
      quantity: 1              // 数量
    },
    
    // 甜度选项
    sweetnessOptions: [
      { value: 'no_sugar', label: '无糖', hint: '0%' },
      { value: 'low_sugar', label: '三分糖', hint: '30%' },
      { value: 'half_sugar', label: '五分糖', hint: '50%' },
      { value: 'less_sugar', label: '七分糖', hint: '70%' },
      { value: 'normal', label: '正常糖', hint: '100%' }
    ],
    
    // 温度选项
    temperatureOptions: [
      { value: 'hot', label: '热' },
      { value: 'warm', label: '温' },
      { value: 'no_ice', label: '去冰' },
      { value: 'less_ice', label: '少冰' },
      { value: 'normal', label: '正常冰' }
    ],
    
    // 加料选项
    toppingOptions: [],
    
    // 最多加料数量
    maxToppings: 5,
    
    // 标签页
    activeTab: 0,
    
    // 用户评价
    comments: [],
    ratingDistribution: [],
    hasMoreComments: false,
    commentPage: 1,
    commentPageSize: 5,
    
    // 加料总价
    toppingsCost: 0,
    
    // 总价
    totalPrice: 0,
    
    // 购物车数量
    cartCount: 0,
    
    // 加载状态
    loading: true
  },

  onLoad(options) {
    const { id } = options;
    if (id) {
      this.setData({ productId: id });
      this.loadProductDetail();
    } else {
      wx.showToast({
        title: '商品不存在',
        icon: 'none',
        complete: () => {
          setTimeout(() => {
            wx.navigateBack();
          }, 1500);
        }
      });
    }
  },

  onShow() {
    // 更新购物车数量
    this.updateCartCount();
    
    // 检查收藏状态
    this.checkFavoriteStatus();
  },

  // 加载商品详情
  async loadProductDetail() {
    try {
      this.setData({ loading: true });
      
      // 调用真实API获取商品详情
      const res = await productApi.getProductDetail(this.data.productId);
      if (res.code === 200) {
        this.setData({ product: res.data });
      }
      
      // 加载用户评价
      await this.loadComments();
      
      // 计算初始总价
      this.calculateTotalPrice();
      
    } catch (error) {
      console.error('加载商品详情失败:', error);
      wx.showToast({
        title: '加载失败，请重试',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  // 加载用户评价
  async loadComments() {
    try {
      const res = await productApi.getProductReviews(this.data.productId);
      if (res.code === 200) {
        this.setData({
          comments: res.data.list || [],
          hasMoreComments: (res.data.list || []).length >= this.data.commentPageSize
        });
      }
    } catch (error) {
      console.error('加载评价失败:', error);
    }
  },

  // 加载更多评价
  async loadMoreComments() {
    try {
      const res = await productApi.getProductReviews(this.data.productId, {
        page: this.data.commentPage + 1,
        size: this.data.commentPageSize
      });
      
      if (res.code === 200) {
        const moreComments = res.data.list || [];
        const currentComments = this.data.comments;
        const newComments = [...currentComments, ...moreComments];
        const newPage = this.data.commentPage + 1;
        
        this.setData({
          comments: newComments,
          commentPage: newPage,
          hasMoreComments: moreComments.length >= this.data.commentPageSize
        });
        
        wx.showToast({
          title: '加载完成',
          icon: 'success'
        });
      }
    } catch (error) {
      console.error('加载更多评价失败:', error);
    }
  },

  // 检查收藏状态
  checkFavoriteStatus() {
    const { productId } = this.data;
    if (productId) {
      const isFavorite = storage.isProductFavorite(productId);
      this.setData({ isFavorite });
    }
  },

  // 更新购物车数量
  updateCartCount() {
    const count = app.updateCartCount();
    this.setData({ cartCount: count });
    return count;
  },

  // 图片加载错误处理
  onImageError(e) {
    console.error('图片加载失败:', e);
    const index = e.currentTarget.dataset.index;
    // 可以设置默认图片
  },

  // 图片加载完成
  onImageLoad(e) {
    console.log('图片加载完成:', e);
  },

  // 切换收藏状态
  toggleFavorite() {
    const { productId, isFavorite } = this.data;
    const newFavoriteStatus = !isFavorite;
    
    if (newFavoriteStatus) {
      storage.addFavoriteProduct(productId);
      wx.showToast({
        title: '已收藏',
        icon: 'success',
        duration: 1000
      });
    } else {
      storage.removeFavoriteProduct(productId);
      wx.showToast({
        title: '已取消收藏',
        icon: 'success',
        duration: 1000
      });
    }
    
    this.setData({ isFavorite: newFavoriteStatus });
  },

  // 选择甜度
  selectSweetness(e) {
    const value = e.currentTarget.dataset.value;
    this.setData({
      'customizations.sweetness': value
    });
  },

  // 选择温度
  selectTemperature(e) {
    const value = e.currentTarget.dataset.value;
    this.setData({
      'customizations.temperature': value
    });
  },

  // 切换加料
  toggleTopping(e) {
    const { id, price } = e.currentTarget.dataset;
    const { toppings, maxToppings } = this.data.customizations;
    
    let newToppings = [...toppings];
    
    if (newToppings.includes(id)) {
      // 取消选择
      newToppings = newToppings.filter(toppingId => toppingId !== id);
    } else {
      // 检查是否超过最大数量
      if (newToppings.length >= maxToppings) {
        wx.showToast({
          title: `最多只能选择${maxToppings}种加料`,
          icon: 'none'
        });
        return;
      }
      newToppings.push(id);
    }
    
    this.setData({
      'customizations.toppings': newToppings
    }, () => {
      this.calculateToppingsCost();
      this.calculateTotalPrice();
    });
  },

  // 计算加料费用
  calculateToppingsCost() {
    const { toppings } = this.data.customizations;
    const { toppingOptions } = this.data;
    
    let cost = 0;
    toppings.forEach(toppingId => {
      const topping = toppingOptions.find(t => t.id === toppingId);
      if (topping) {
        cost += topping.price;
      }
    });
    
    this.setData({ toppingsCost: cost });
  },

  // 计算总价
  calculateTotalPrice() {
    const { product, customizations, toppingsCost } = this.data;
    const basePrice = product.price;
    const quantity = customizations.quantity || 1;
    
    const total = (basePrice + toppingsCost) * quantity;
    this.setData({ totalPrice: total.toFixed(2) });
  },

  // 切换标签页
  switchTab(e) {
    const index = parseInt(e.currentTarget.dataset.index);
    this.setData({ activeTab: index });
  },

  // 预览图片
  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    const urls = this.data.product.images;
    
    wx.previewImage({
      urls: urls,
      current: url
    });
  },

  // 跳转到购物车
  goToCart() {
    wx.switchTab({
      url: '/pages/cart/cart'
    });
  },

  // 添加到购物车
  addToCart() {
    const { product, customizations, toppingsCost } = this.data;
    
    // 检查必选项是否已选择
    if (!customizations.sweetness || !customizations.temperature) {
      wx.showToast({
        title: '请选择甜度和温度',
        icon: 'none'
      });
      return;
    }
    
    // 获取加料详情
    const selectedToppings = customizations.toppings.map(toppingId => {
      const topping = this.data.toppingOptions.find(t => t.id === toppingId);
      return {
        id: topping.id,
        name: topping.name,
        price: topping.price
      };
    });
    
    // 构建购物车项
    const cartItem = {
      id: product.id,
      name: product.name,
      image: product.images[0] || '',
      price: product.price,
      originalPrice: product.originalPrice,
      quantity: customizations.quantity || 1,
      customizations: {
        sweetness: customizations.sweetness,
        temperature: customizations.temperature,
        toppings: selectedToppings
      },
      toppingsCost: toppingsCost,
      subtotal: (product.price + toppingsCost) * (customizations.quantity || 1)
    };
    
    // 获取当前购物车
    let cart = storage.getCart();
    
    // 检查是否已存在相同定制化的商品
    const existingIndex = cart.findIndex(item => {
      if (item.id !== cartItem.id) return false;
      if (item.customizations.sweetness !== cartItem.customizations.sweetness) return false;
      if (item.customizations.temperature !== cartItem.customizations.temperature) return false;
      
      const itemToppings = item.customizations.toppings.map(t => t.id).sort();
      const newToppings = cartItem.customizations.toppings.map(t => t.id).sort();
      if (JSON.stringify(itemToppings) !== JSON.stringify(newToppings)) return false;
      
      return true;
    });
    
    if (existingIndex >= 0) {
      // 合并数量
      cart[existingIndex].quantity += cartItem.quantity;
      cart[existingIndex].subtotal = (cart[existingIndex].price + cart[existingIndex].toppingsCost) * cart[existingIndex].quantity;
    } else {
      // 添加到购物车
      cart.push(cartItem);
    }
    
    // 保存购物车
    storage.saveCart(cart);
    
    // 更新购物车数量
    this.updateCartCount();
    
    // 显示成功提示
    wx.showToast({
      title: '已加入购物车',
      icon: 'success',
      duration: 1500
    });
    
    // 震动反馈
    wx.vibrateShort();
    
    // 触发购物车更新事件
    this.triggerCartUpdate();
  },

  // 立即购买
  async buyNow() {
    const { product, customizations, toppingsCost } = this.data;
    
    // 检查必选项是否已选择
    if (!customizations.sweetness || !customizations.temperature) {
      wx.showToast({
        title: '请选择甜度和温度',
        icon: 'none'
      });
      return;
    }
    
    // 获取加料详情
    const selectedToppings = customizations.toppings.map(toppingId => {
      const topping = this.data.toppingOptions.find(t => t.id === toppingId);
      return {
        id: topping.id,
        name: topping.name,
        price: topping.price
      };
    });
    
    // 构建订单项
    const orderItem = {
      productId: product.id,
      productName: product.name,
      productImage: product.images[0] || '',
      price: product.price,
      quantity: customizations.quantity || 1,
      customizations: {
        sweetness: customizations.sweetness,
        temperature: customizations.temperature,
        toppings: selectedToppings
      },
      toppingsCost: toppingsCost,
      subtotal: (product.price + toppingsCost) * (customizations.quantity || 1)
    };
    
    // 保存到本地存储，用于订单确认页
    wx.setStorageSync('buyNowItem', orderItem);
    
    // 跳转到订单确认页
    wx.navigateTo({
      url: '/pages/order/confirm'
    });
  },

  // 触发购物车更新事件
  triggerCartUpdate() {
    // 获取当前页面栈
    const pages = getCurrentPages();
    const cartPage = pages.find(page => page.route === 'pages/cart/cart');
    
    if (cartPage) {
      // 如果购物车页面已存在，刷新数据
      cartPage.onShow && cartPage.onShow();
    }
    
    // 触发全局购物车更新事件
    const eventChannel = this.getOpenerEventChannel();
    if (eventChannel) {
      eventChannel.emit('cartUpdated');
    }
  },
  
  // 检查收藏状态
  checkFavoriteStatus() {
    const favorites = wx.getStorageSync('favorites') || [];
    const isFavorite = favorites.some(f => f.id === this.data.productId);
    this.setData({ isFavorite });
  },
  
  // 切换收藏
  toggleFavorite() {
    const { productId, product, isFavorite } = this.data;
    let favorites = wx.getStorageSync('favorites') || [];
    
    if (isFavorite) {
      // 取消收藏
      favorites = favorites.filter(f => f.id !== productId);
      wx.showToast({
        title: '已取消收藏',
        icon: 'success'
      });
    } else {
      // 添加收藏
      favorites.push({
        id: productId,
        name: product.name,
        image: product.images[0],
        price: product.price,
        description: product.description,
        sales: product.sales
      });
      wx.showToast({
        title: '收藏成功',
        icon: 'success'
      });
      wx.vibrateShort();
    }
    
    wx.setStorageSync('favorites', favorites);
    this.setData({ isFavorite: !isFavorite });
  }
});