// pages/index/index.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { productApi, systemApi, storeApi, promotionApi } = require('../../utils/api.js');

Page({
  data: {
    // 轮播图
    banners: [],
    
    // 推荐商品
    recommendProducts: [],
    
    // 今日热门
    hotProducts: [],
    
    // 促销活动
    promotions: [],
    
    // 附近门店
    nearbyStore: null,
    
    // 选中的门店
    selectedStore: null,
    isLocating: false,
    
    // 加载状态
    loading: true,
    refreshing: false,
    
    // 用户位置
    userLocation: null,
    
    // 购物车数量
    cartCount: 0,
    cartAnimating: false,
    
    // 可用优惠券数量
    availableCouponCount: 0,
    
    // 滚动位置
    scrollTop: 0
  },

  onLoad() {
    this.loadData();
  },

  onShow() {
    // 检查登录状态
    if (!app.globalData.userInfo) {
      this.checkLoginStatus();
    }
    
    // 更新购物车数量
    this.updateCartCount();
  },

  onPullDownRefresh() {
    this.onRefresh();
  },
  
  // 下拉刷新
  onRefresh() {
    this.setData({ refreshing: true });
    this.loadData();
  },
  
  // 滚动事件
  onScroll(e) {
    this.setData({
      scrollTop: e.detail.scrollTop
    });
  },

  // 加载数据
  async loadData() {
    try {
      this.setData({ loading: true });
      
      // 并行加载核心数据，不等待位置信息
      await Promise.all([
        this.loadBanners(),
        this.loadRecommendProducts(),
        this.loadHotProducts(),
        this.loadPromotions()
      ]);

      // 异步获取位置并加载附近门店，不阻塞页面显示
      this.getLocation().then(() => {
        this.loadNearbyStore();
      });
      
    } catch (error) {
      console.error('加载数据失败:', error);
    } finally {
      this.setData({ 
        loading: false,
        refreshing: false 
      });
      wx.stopPullDownRefresh();
    }
  },

  // 检查登录状态
  checkLoginStatus() {
    const userInfo = storage.getUserInfo();
    if (!userInfo) {
      // 提示登录
    }
  },

  // 获取位置信息
  async getLocation() {
    return new Promise((resolve) => {
      wx.getLocation({
        type: 'gcj02',
        success: (res) => {
          const location = {
            latitude: res.latitude,
            longitude: res.longitude
          };
          this.setData({ userLocation: location });
          resolve(location);
        },
        fail: () => {
          resolve(null);
        }
      });
    });
  },

  // 加载轮播图数据
  async loadBanners() {
    try {
      const res = await systemApi.getBanners();
      if (res.code === 200) {
        this.setData({ banners: res.data });
      }
    } catch (error) {
      console.error('加载轮播图失败:', error);
    }
  },

  // 加载推荐商品
  async loadRecommendProducts() {
    try {
      const res = await productApi.getRecommendProducts();
      if (res.code === 200) {
        this.setData({ recommendProducts: res.data });
      }
    } catch (error) {
      console.error('加载推荐商品失败:', error);
    }
  },

  // 加载热门商品
  async loadHotProducts() {
    try {
      const res = await productApi.getHotProducts();
      if (res.code === 200) {
        this.setData({ hotProducts: res.data });
      }
    } catch (error) {
      console.error('加载热门商品失败:', error);
    }
  },

  // 加载促销活动
  async loadPromotions() {
    try {
      const res = await promotionApi.getPromotionList();
      if (res.code === 200) {
        this.setData({ promotions: res.data });
      }
    } catch (error) {
      console.error('加载促销活动失败:', error);
    }
  },

  // 加载附近门店
  async loadNearbyStore() {
    try {
      const { userLocation } = this.data;
      const res = await storeApi.getNearbyStores({
        latitude: userLocation?.latitude,
        longitude: userLocation?.longitude
      });
      if (res.code === 200 && res.data.length > 0) {
        this.setData({ nearbyStore: res.data[0] });
      }
    } catch (error) {
      console.error('加载附近门店失败:', error);
    }
  },

  // 更新购物车数量
  updateCartCount() {
    const cart = storage.getCart() || [];
    const count = cart.reduce((total, item) => total + item.quantity, 0);
    app.globalData.cartCount = count;
    this.setData({ cartCount: count });
    return count;
  },
  
  // 跳转到购物车
  goToCart() {
    wx.navigateTo({ url: '/pages/cart/cart' });
  },

  // 跳转到外卖点单
  goToTakeout() {
    this.goToMenu('takeout');
  },

  // 跳转到堂食点单
  goToDinein() {
    this.goToMenu('dinein');
  },

  // 通用的跳转到菜单页面函数
  goToMenu(orderType) {
    app.globalData.orderType = orderType;
    wx.switchTab({
      url: '/pages/order/order'
    });
  },

  // 跳转到搜索页
  goToSearch() {
    wx.navigateTo({ url: '/pages/search/search' });
  },

  // 点击轮播图
  onBannerTap(e) {
    const url = e.currentTarget.dataset.url;
    if (url) {
      wx.navigateTo({ url });
    }
  },

  // 点击商品
  onProductTap(e) {
    const productId = e.currentTarget.dataset.id;
    if (productId) {
      wx.navigateTo({ url: `/pages/product/product?id=${productId}` });
    }
  },

  // 点击热门商品添加到购物车
  onHotProductAddToCart(e) {
    const productId = e.currentTarget.dataset.id;
    const product = this.data.hotProducts.find(p => p.id === productId) || 
                   this.data.recommendProducts.find(p => p.id === productId);
    if (!product) return;
    
    const cart = storage.getCart() || [];
    const existingItemIndex = cart.findIndex(item => item.id === productId);
    
    if (existingItemIndex >= 0) {
      cart[existingItemIndex].quantity += 1;
    } else {
      cart.push({
        id: product.id,
        name: product.name,
        price: product.price,
        image: product.image,
        quantity: 1
      });
    }
    
    storage.saveCart(cart);
    this.updateCartCount();
    wx.showToast({ title: '已加入购物车', icon: 'success' });
  }
});