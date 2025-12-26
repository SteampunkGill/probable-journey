// pages/search/search.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { productApi, systemApi } = require('../../utils/api.js');

Page({
  data: {
    // 搜索关键词
    searchKeyword: '',
    
    // 是否显示搜索结果
    showResults: false,
    
    // 是否显示搜索建议
    showSuggest: false,
    
    // 搜索历史
    searchHistory: [],
    
    // 热门搜索
    hotKeywords: [],
    
    // 搜索建议
    searchSuggest: [],
    
    // 搜索结果
    searchResults: [],
    
    // 筛选后的结果
    filteredResults: [],
    
    // 排序方式
    sortBy: 'default',
    
    // 分类筛选
    categories: [],
    activeCategory: '',
    
    // 加载状态
    loading: false
  },

  onLoad(options) {
    if (options.keyword) {
      this.setData({ searchKeyword: options.keyword });
      this.search();
    }
    this.loadSearchData();
  },

  onShow() {
    this.loadSearchHistory();
  },

  // 加载搜索相关数据
  loadSearchData() {
    this.loadSearchHistory();
    this.loadHotKeywords();
    this.loadCategories();
  },

  // 加载搜索历史
  loadSearchHistory() {
    const history = storage.getSearchHistory() || [];
    this.setData({ searchHistory: history });
  },

  // 加载热门搜索
  async loadHotKeywords() {
    try {
      const res = await systemApi.getHotKeywords();
      if (res.code === 200) {
        this.setData({ hotKeywords: res.data });
      }
    } catch (error) {
      console.error('加载热门搜索失败:', error);
    }
  },

  // 加载分类
  async loadCategories() {
    try {
      const res = await productApi.getCategoryList();
      if (res.code === 200) {
        this.setData({ categories: res.data });
      }
    } catch (error) {
      console.error('加载分类失败:', error);
    }
  },

  // 关键词输入
  onKeywordInput(e) {
    const keyword = e.detail.value.trim();
    this.setData({
      searchKeyword: keyword,
      showSuggest: keyword.length > 0
    });
    if (keyword.length > 0) {
      this.loadSearchSuggest(keyword);
    }
  },

  // 清空关键词
  clearKeyword() {
    this.setData({
      searchKeyword: '',
      showSuggest: false,
      showResults: false
    });
  },

  // 搜索
  onSearch() {
    this.search();
  },

  // 执行搜索
  async search() {
    const keyword = this.data.searchKeyword.trim();
    if (!keyword) return;
    
    storage.addSearchHistory(keyword);
    this.loadSearchHistory();
    
    this.setData({ loading: true });
    wx.showLoading({ title: '搜索中...' });
    
    try {
      const res = await productApi.getProductList({ keyword });
      if (res.code === 200) {
        this.setData({
          searchResults: res.data,
          filteredResults: res.data,
          showResults: true,
          showSuggest: false
        });
      }
    } catch (error) {
      console.error('搜索失败:', error);
    } finally {
      this.setData({ loading: false });
      wx.hideLoading();
    }
  },

  // 加载搜索建议
  async loadSearchSuggest(keyword) {
    try {
      const res = await productApi.getSearchSuggest(keyword);
      if (res.code === 200) {
        this.setData({ searchSuggest: res.data });
      }
    } catch (error) {
      console.error('加载搜索建议失败:', error);
    }
  },

  // 通过历史搜索
  searchByHistory(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({ searchKeyword: keyword }, () => this.search());
  },

  // 通过热门搜索
  searchByHot(e) {
    const keyword = e.currentTarget.dataset.keyword;
    this.setData({ searchKeyword: keyword }, () => this.search());
  },

  // 清除历史记录
  clearHistory() {
    storage.clearSearchHistory();
    this.setData({ searchHistory: [] });
  },

  // 商品点击
  onProductTap(e) {
    const productId = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/product/product?id=${productId}` });
  }
});