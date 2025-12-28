const { favoriteApi } = require('../../utils/api.js');

Page({
  data: {
    favorites: [],
    page: 1,
    size: 10,
    hasMore: true,
    isLoading: false
  },

  onShow() {
    this.setData({ 
      favorites: [],
      page: 1,
      hasMore: true 
    }, () => {
      this.loadFavorites();
    });
  },

  async loadFavorites() {
    if (this.data.isLoading || !this.data.hasMore) return;

    this.setData({ isLoading: true });
    try {
      const res = await favoriteApi.getFavorites({
        page: this.data.page,
        size: this.data.size
      });

      const list = res.data.content || res.data || [];
      this.setData({
        favorites: [...this.data.favorites, ...list],
        hasMore: list.length === this.data.size,
        page: this.data.page + 1
      });
    } catch (error) {
      console.error('加载收藏失败:', error);
    } finally {
      this.setData({ isLoading: false });
    }
  },

  onReachBottom() {
    this.loadFavorites();
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product/detail?id=${id}`
    });
  },

  goShopping() {
    wx.switchTab({
      url: '/pages/order/order'
    });
  }
});