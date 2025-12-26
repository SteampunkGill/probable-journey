// pages/favorite/favorite.js
const storage = require('../../utils/storage.js');

Page({
  data: {
    favoriteList: [],
    loading: false,
    isEditMode: false
  },

  onLoad() {
    this.loadFavorites();
  },

  onShow() {
    this.loadFavorites();
  },

  onPullDownRefresh() {
    this.loadFavorites();
    wx.stopPullDownRefresh();
  },

  // 加载收藏列表
  loadFavorites() {
    this.setData({ loading: true });
    
    try {
      const favorites = wx.getStorageSync('favorites') || [];
      console.log('收藏列表:', favorites);
      
      this.setData({
        favoriteList: favorites,
        loading: false
      });
    } catch (error) {
      console.error('加载收藏失败:', error);
      this.setData({ loading: false });
    }
  },

  // 切换编辑模式
  toggleEditMode() {
    this.setData({
      isEditMode: !this.data.isEditMode
    });
  },

  // 取消收藏
  removeFavorite(e) {
    const { id } = e.currentTarget.dataset;
    
    wx.showModal({
      title: '提示',
      content: '确定要取消收藏吗？',
      success: (res) => {
        if (res.confirm) {
          const favorites = this.data.favoriteList.filter(item => item.id !== id);
          wx.setStorageSync('favorites', favorites);
          
          this.setData({
            favoriteList: favorites
          });
          
          wx.showToast({
            title: '已取消收藏',
            icon: 'success'
          });
        }
      }
    });
  },

  // 点击商品
  onProductTap(e) {
    if (this.data.isEditMode) return;
    
    const { id } = e.currentTarget.dataset;
    wx.navigateTo({
      url: `/pages/product/product?id=${id}`
    });
  },

  // 加入购物车
  addToCart(e) {
    e.stopPropagation();
    const { id } = e.currentTarget.dataset;
    const product = this.data.favoriteList.find(p => p.id === id);
    
    if (!product) return;
    
    // 跳转到商品详情页进行定制
    wx.navigateTo({
      url: `/pages/product/product?id=${id}`
    });
  },

  // 清空收藏
  clearAll() {
    wx.showModal({
      title: '提示',
      content: '确定要清空所有收藏吗？',
      success: (res) => {
        if (res.confirm) {
          wx.setStorageSync('favorites', []);
          this.setData({
            favoriteList: [],
            isEditMode: false
          });
          
          wx.showToast({
            title: '已清空',
            icon: 'success'
          });
        }
      }
    });
  },
  
  // 去逛逛
  goToOrder() {
    wx.switchTab({
      url: '/pages/order/order'
    });
  }
});
