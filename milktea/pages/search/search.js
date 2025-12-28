const { productApi, searchApi, favoriteApi } = require('../../utils/api.js');

Page({
  data: {
    searchKeyword: '',
    searchType: 'product',
    showResults: false,
    searchHistory: [],
    hotKeywords: [],
    searchResults: [],
    storeResults: [],
    favoriteIds: {},
    sortBy: 'default',
    filteredProducts: [],
    filteredStores: []
  },

  onLoad(options) {
    this.loadInitData();
    if (options.keyword) {
      this.setData({ searchKeyword: options.keyword });
      this.onSearch();
    }
  },

  async loadInitData() {
    try {
      const [historyRes, hotRes, favoritesRes] = await Promise.all([
        productApi.getSearchHistory(),
        productApi.getHotKeywords(),
        favoriteApi.getFavorites({ page: 1, size: 100 })
      ]);

      const favIds = {};
      if (favoritesRes?.data?.content) {
        favoritesRes.data.content.forEach(item => {
          favIds[item.product.id] = true;
        });
      }

      this.setData({
        searchHistory: historyRes.data || [],
        hotKeywords: hotRes.data || [],
        favoriteIds: favIds
      });
    } catch (error) {
      console.error('加载初始化数据失败:', error);
    }
  },

  onInput(e) {
    this.setData({ searchKeyword: e.detail.value });
    if (!e.detail.value) {
      this.setData({ showResults: false });
    }
  },

  async onSearch() {
    const keyword = this.data.searchKeyword.trim();
    if (!keyword) return;

    wx.showLoading({ title: '搜索中...' });
    try {
      const [productRes, storeRes] = await Promise.all([
        productApi.searchProducts(keyword),
        searchApi.searchStores(keyword)
      ]);

      this.setData({
        searchResults: productRes.data.list || productRes.data || [],
        storeResults: storeRes.data.list || storeRes.data || [],
        showResults: true
      }, () => {
        this.filterResults();
      });

      // 刷新历史记录
      const historyRes = await productApi.getSearchHistory();
      this.setData({ searchHistory: historyRes.data || [] });
    } catch (error) {
      console.error('搜索失败:', error);
    } finally {
      wx.hideLoading();
    }
  },

  searchByKeyword(e) {
    this.setData({ searchKeyword: e.currentTarget.dataset.keyword }, () => {
      this.onSearch();
    });
  },

  clearKeyword() {
    this.setData({ searchKeyword: '', showResults: false });
  },

  async clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定要清除搜索历史吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await productApi.clearSearchHistory();
            this.setData({ searchHistory: [] });
          } catch (error) {
            console.error('清空历史失败:', error);
          }
        }
      }
    });
  },

  switchType(e) {
    this.setData({ searchType: e.currentTarget.dataset.type }, () => {
      this.filterResults();
    });
  },

  changeSort(e) {
    this.setData({ sortBy: e.currentTarget.dataset.sort }, () => {
      this.filterResults();
    });
  },

  filterResults() {
    let products = [...this.data.searchResults];
    if (this.data.searchType === 'product') {
      switch (this.data.sortBy) {
        case 'sales':
          products.sort((a, b) => (b.sales || 0) - (a.sales || 0));
          break;
        case 'price_asc':
          products.sort((a, b) => a.price - b.price);
          break;
        case 'price_desc':
          products.sort((a, b) => b.price - a.price);
          break;
      }
    }

    this.setData({
      filteredProducts: products,
      filteredStores: this.data.storeResults
    });
  },

  onProductTap(e) {
    wx.navigateTo({ url: `/pages/product/detail?id=${e.currentTarget.dataset.id}` });
  },

  onStoreTap(e) {
    wx.navigateTo({ url: `/pages/index/index?storeId=${e.currentTarget.dataset.id}` });
  },

  async onFavoriteChange(e) {
    const { id, isFavorite } = e.detail;
    try {
      if (isFavorite) {
        await favoriteApi.addFavorite(id);
      } else {
        await favoriteApi.removeFavorite(id);
      }
      const favIds = { ...this.data.favoriteIds };
      favIds[id] = isFavorite;
      this.setData({ favoriteIds: favIds });
    } catch (error) {
      wx.showToast({ title: '操作失败', icon: 'none' });
    }
  }
});