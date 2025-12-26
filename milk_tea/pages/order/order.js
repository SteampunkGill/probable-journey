// pages/order/order.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { productApi } = require('../../utils/api.js');

Page({
  data: {
    // 分类列表
    categories: [],
    
    // 当前选中的分类
    activeCategoryId: null,
    
    // 商品列表
    productList: [],
    
    // 搜索关键词
    searchKeyword: '',
    
    // 购物车数量
    cartCount: 0,
    cartAnimating: false,
    
    // 加载状态
    loading: false,
    
    // 自定义弹窗
    showCustomModal: false,
    selectedProduct: null,
    customizations: {
      size: '中杯',
      temperature: '常温',
      sweetness: '正常糖',
      ice: '正常冰',
      toppings: []
    },
    quantity: 1,
    totalPrice: 0,
    
    // 可选项
    sizeOptions: ['小杯', '中杯', '大杯'],
    temperatureOptions: ['热饮', '常温', '冷饮'],
    sweetnessOptions: ['无糖', '三分糖', '五分糖', '七分糖', '正常糖'],
    iceOptions: ['去冰', '少冰', '正常冰', '多冰'],
    toppingOptions: [],
    
    // 筛选选项
    filterOptions: [
      { id: 'hot', name: '热度', options: [
        { value: 'all', label: '全部' },
        { value: 'hot', label: '热销' },
        { value: 'new', label: '新品' }
      ]},
      { id: 'price', name: '价格', options: [
        { value: 'all', label: '全部' },
        { value: 'low', label: '低价' },
        { value: 'medium', label: '中等' },
        { value: 'high', label: '高价' }
      ]}
    ],
    
    // 当前筛选状态
    activeFilters: {
      hot: 'all',
      price: 'all'
    },
    
    // 是否显示筛选面板
    showFilterPanel: false
  },

  onLoad(options) {
    this.loadCategories();
    this.updateCartCount();
  },

  onShow() {
    this.updateCartCount();
  },

  // 加载分类列表
  async loadCategories() {
    try {
      const res = await productApi.getCategoryList();
      if (res.code === 200) {
        this.setData({ 
          categories: res.data,
          activeCategoryId: res.data.length > 0 ? res.data[0].id : null
        });
        if (this.data.activeCategoryId) {
          this.loadProducts();
        }
      }
    } catch (error) {
      console.error('加载分类失败:', error);
    }
  },

  // 切换分类
  selectCategory(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ 
      activeCategoryId: id,
      searchKeyword: ''
    });
    this.loadProducts();
  },

  // 加载商品列表
  async loadProducts() {
    this.setData({ loading: true });
    try {
      const { activeCategoryId, activeFilters, searchKeyword } = this.data;
      const params = {
        categoryId: activeCategoryId,
        keyword: searchKeyword,
        isHot: activeFilters.hot === 'hot' ? true : null,
        isNew: activeFilters.hot === 'new' ? true : null,
        priceRange: activeFilters.price !== 'all' ? activeFilters.price : null
      };
      const res = await productApi.getProductList(params);
      if (res.code === 200) {
        this.setData({ productList: res.data });
      }
    } catch (error) {
      console.error('加载商品失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 更新购物车数量
  updateCartCount() {
    const cart = storage.getCart() || [];
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    this.setData({ cartCount: count });
  },

  // 点击商品
  async onProductTap(e) {
    const { id } = e.currentTarget.dataset;
    try {
      const res = await productApi.getProductDetail(id);
      if (res.code === 200) {
        const product = res.data;
        this.setData({
          showCustomModal: true,
          selectedProduct: product,
          quantity: 1,
          customizations: {
            size: '中杯',
            temperature: '常温',
            sweetness: '正常糖',
            ice: '正常冰',
            toppings: []
          },
          toppingOptions: product.toppings || []
        });
        this.calculateTotalPrice();
      }
    } catch (error) {
      console.error('加载商品详情失败:', error);
    }
  },

  // 选择杯型等属性
  selectAttr(e) {
    const { type, value } = e.currentTarget.dataset;
    this.setData({
      [`customizations.${type}`]: value
    });
    this.calculateTotalPrice();
  },

  // 切换加料
  toggleTopping(e) {
    const { id } = e.currentTarget.dataset;
    const toppings = [...this.data.customizations.toppings];
    const index = toppings.indexOf(id);
    
    if (index > -1) {
      toppings.splice(index, 1);
    } else {
      toppings.push(id);
    }
    
    this.setData({
      'customizations.toppings': toppings
    });
    this.calculateTotalPrice();
  },

  // 计算总价
  calculateTotalPrice() {
    const { selectedProduct, quantity, customizations, toppingOptions } = this.data;
    if (!selectedProduct) return;
    
    let toppingPrice = 0;
    customizations.toppings.forEach(id => {
      const topping = toppingOptions.find(t => t.id === id);
      if (topping) toppingPrice += topping.price;
    });
    
    const totalPrice = ((selectedProduct.price + toppingPrice) * quantity).toFixed(2);
    this.setData({ totalPrice });
  },

  // 确认加入购物车
  confirmAddToCart() {
    const { selectedProduct, quantity, customizations, toppingOptions, totalPrice } = this.data;
    
    const cartItem = {
      id: `${selectedProduct.id}_${Date.now()}`,
      productId: selectedProduct.id,
      name: selectedProduct.name,
      image: selectedProduct.image,
      price: selectedProduct.price,
      quantity: quantity,
      customizations: {
        ...customizations,
        toppingNames: customizations.toppings.map(id => {
          const t = toppingOptions.find(item => item.id === id);
          return t ? t.name : '';
        }).filter(Boolean)
      },
      subtotal: parseFloat(totalPrice)
    };
    
    const cart = storage.getCart() || [];
    cart.push(cartItem);
    storage.saveCart(cart);
    
    wx.showToast({ title: '已加入购物车', icon: 'success' });
    this.setData({ showCustomModal: false });
    this.updateCartCount();
  },

  // 切换筛选面板
  toggleFilterPanel() {
    this.setData({ showFilterPanel: !this.data.showFilterPanel });
  },

  // 选择筛选选项
  selectFilterOption(e) {
    const { type, value } = e.currentTarget.dataset;
    this.setData({ [`activeFilters.${type}`]: value });
  },

  // 确认筛选
  confirmFilters() {
    this.setData({ showFilterPanel: false });
    this.loadProducts();
  },

  // 重置筛选
  resetFilters() {
    this.setData({
      activeFilters: { hot: 'all', price: 'all' }
    });
    this.loadProducts();
  },

  // 跳转到购物车
  goToCart() {
    wx.navigateTo({ url: '/pages/cart/cart' });
  },

  hideModal() {
    this.setData({ showCustomModal: false });
  }
});