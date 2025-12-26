// app.js
App({
  onLaunch: function () {
    // 小程序启动时执行
    console.log('小程序启动');
    
    // 检查并初始化本地存储
    this.checkStorage();
  },
  
  checkStorage: function() {
    // 检查购物车数据是否存在
    if (!wx.getStorageSync('cartData')) {
      wx.setStorageSync('cartData', {
        items: [],
        count: 0,
        total: 0
      });
    }
    
    // 检查收藏数据是否存在
    if (!wx.getStorageSync('favorites')) {
      wx.setStorageSync('favorites', []);
    }
  },
  
  globalData: {
    userInfo: null,
    token: null,
    isGuest: false,
    cartCount: 0,
    cartTotal: 0,
    favorites: [],
    orderType: 'delivery', // delivery:外卖 pickup:堂食
    selectedAddress: null,
    selectedStore: null,
    userPoints: 1250 // 用户积分
  },
  
  // 更新购物车数量
  updateCartCount: function() {
    try {
      const cartData = wx.getStorageSync('cart') || [];
      const count = cartData.reduce((total, item) => total + (item.quantity || 0), 0);
      this.globalData.cartCount = count;
      
      // 更新TabBar徽章（购物车不在TabBar中，使用浮动按钮）
      // 注：购物车通过浮动按钮访问，不占用TabBar位置
      return count;
    } catch (error) {
      console.error('更新购物车数量失败:', error);
      return 0;
    }
  },
  
  // 登出
  logout: function() {
    try {
      // 清除存储
      wx.removeStorageSync('token');
      wx.removeStorageSync('userInfo');
      
      // 清除全局数据
      this.globalData.token = null;
      this.globalData.userInfo = null;
      this.globalData.isGuest = true;
      
      // 跳转到登录页
      wx.redirectTo({
        url: '/pages/login/login'
      });
    } catch (error) {
      console.error('登出失败:', error);
    }
  },
  
  // 显示加载提示
  showLoading: function(title = '加载中...') {
    wx.showLoading({
      title: title,
      mask: true
    });
  },
  
  // 隐藏加载提示
  hideLoading: function() {
    wx.hideLoading();
  },
  
  // 显示确认对话框
  showConfirm: function(title, content) {
    return new Promise((resolve) => {
      wx.showModal({
        title: title,
        content: content,
        success: (res) => {
          resolve(res.confirm);
        }
      });
    });
  }
});