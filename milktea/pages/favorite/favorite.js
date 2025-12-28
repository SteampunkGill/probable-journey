const { favoriteApi } = require('../../utils/api.js');

Page({
  data: {
    favoriteList: [],
    loading: false,
    isEditMode: false,
    page: 1,
    size: 10,
    total: 0,
    hasMore: false,
    defaultImage: 'https://images.unsplash.com/photo-1567095761054-7a02e69e5c43?w=400'
  },

  onShow() {
    this.setData({ page: 1 }, () => {
      this.loadFavorites();
    });
  },

  async loadFavorites(isLoadMore = false) {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    try {
      const params = {
        page: this.data.page,
        size: this.data.size
      };
      const res = await favoriteApi.getFavorites(params);
      const pageData = res.data || res;
      if (pageData && pageData.content) {
        const list = isLoadMore ? [...this.data.favoriteList, ...pageData.content] : pageData.content;
        this.setData({
          favoriteList: list,
          total: pageData.totalElements || pageData.total || 0,
          hasMore: !pageData.last
        });
      }
    } catch (error) {
      console.error('加载收藏失败:', error);
      wx.showToast({ title: '加载失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  loadMore() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ page: this.data.page + 1 }, () => {
        this.loadFavorites(true);
      });
    }
  },

  toggleEditMode() {
    this.setData({ isEditMode: !this.data.isEditMode });
  },

  async removeFavorite(e) {
    const productId = e.currentTarget.dataset.id;
    wx.showModal({
      title: '提示',
      content: '确定要取消收藏吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await favoriteApi.removeFavorite(productId);
            const list = this.data.favoriteList.filter(item => item.product.id !== productId);
            this.setData({
              favoriteList: list,
              total: this.data.total - 1
            });
            wx.showToast({ title: '已取消收藏' });
          } catch (error) {
            console.error('取消收藏失败:', error);
            wx.showToast({ title: '操作失败', icon: 'none' });
          }
        }
      }
    });
  },

  onProductTap(e) {
    if (this.data.isEditMode) return;
    wx.navigateTo({ url: `/pages/product/detail?id=${e.currentTarget.dataset.id}` });
  },

  addToCart(e) {
    wx.navigateTo({ url: `/pages/product/detail?id=${e.currentTarget.dataset.id}` });
  },

  async clearAll() {
    wx.showModal({
      title: '提示',
      content: '确定要清空所有收藏吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await favoriteApi.clearFavorites();
            this.setData({
              favoriteList: [],
              total: 0,
              isEditMode: false
            });
            wx.showToast({ title: '已清空' });
          } catch (error) {
            console.error('清空收藏失败:', error);
            wx.showToast({ title: '操作失败', icon: 'none' });
          }
        }
      }
    });
  },

  goToOrder() {
    wx.switchTab({ url: '/pages/index/index' });
  }
});