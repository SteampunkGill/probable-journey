const { productApi, storeApi } = require('../../utils/api.js');
const storage = require('../../utils/storage.js');

Page({
  data: {
    searchKeyword: '',
    searchType: 'product', // product | store
    showResults: false,
    showSuggest: false,
    searchHistory: [],
    hotKeywords: [
      { word: '杨枝甘露' },
      { word: '多肉葡萄' },
      { word: '芝芝莓莓' },
      { word: '烤奶' },
      { word: '珍珠奶茶' }
    ],
    searchSuggest: [],
    searchResults: [],
    storeResults: [],
    sortBy: 'default',
    filteredProducts: [],
    filteredStores: []
  },

  onLoad(options) {
    this.loadSearchHistory();
    if (options.keyword) {
      this.setData({ searchKeyword: options.keyword });
      this.onSearch();
    }
  },

  loadSearchHistory() {
    const history = storage.getSearchHistory();
    this.setData({ searchHistory: history });
  },

  onInput(e) {
    const keyword = e.detail.value;
    this.setData({ 
      searchKeyword: keyword,
      showSuggest: !!keyword,
      showResults: !keyword ? false : this.data.showResults,
      searchSuggest: keyword ? [
        `${keyword}奶茶`,
        `${keyword}拿铁`,
        `${keyword}冰淇淋`
      ] : []
    });
  },

  clearKeyword() {
    this.setData({
      searchKeyword: '',
      showResults: false,
      showSuggest: false
    });
  },

  clearHistory() {
    wx.showModal({
      title: '提示',
      content: '确定要清除搜索历史吗？',
      success: (res) => {
        if (res.confirm) {
          storage.clearSearchHistory();
          this.setData({ searchHistory: [] });
        }
      }
    });
  },

  searchByKeyword(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({ 
      searchKeyword: keyword,
      showSuggest: false
    });
    this.onSearch();
  },

  async onSearch() {
    const keyword = this.data.searchKeyword.trim();
    if (!keyword) return;

    wx.showLoading({ title: '搜索中...' });
    try {
      // 保存搜索历史
      storage.addSearchHistory(keyword);
      this.loadSearchHistory();

      const [productRes, storeRes] = await Promise.all([
        productApi.searchProducts(keyword),
        storeApi.getNearbyStores({ keyword })
      ]);

      this.setData({
        searchResults: productRes.data || [],
        storeResults: storeRes.data || [],
        showResults: true,
        showSuggest: false
      });
      
      this.applyFilter();
    } catch (error) {
      console.error('搜索失败:', error);
      wx.showToast({ title: '搜索失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  switchTab(e) {
    const type = e.currentTarget.dataset.type;
    this.setData({ searchType: type });
    this.applyFilter();
  },

  changeSort(e) {
    const sort = e.currentTarget.dataset.sort;
    this.setData({ sortBy: sort });
    this.applyFilter();
  },

  applyFilter() {
    let products = [...this.data.searchResults];
    
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

    this.setData({
      filteredProducts: products,
      filteredStores: this.data.storeResults
    });
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/product/detail?id=${id}`
    });
  },

  onStoreTap(e) {
    const id = e.currentTarget.dataset.id;
    // 切换门店并跳转点单页
    const app = getApp();
    if (app.globalData) {
      app.globalData.selectedStoreId = id;
    }
    wx.switchTab({
      url: '/pages/order/order'
    });
  }
});