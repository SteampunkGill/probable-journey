// pages/cart/cart.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { couponApi, pointsApi } = require('../../utils/api.js');

Page({
  data: {
    // 购物车商品
    cartItems: [],
    
    // 是否编辑模式
    isEditing: false,
    
    // 全选状态
    isAllSelected: false,
    
    // 选中商品数量
    selectedCount: 0,
    
    // 选中商品总价
    selectedAmount: 0,
    
    // 总金额
    totalAmount: 0,
    
    // 总优惠金额
    totalDiscount: 0,
    
    // 优惠券
    availableCoupons: [],
    selectedCoupon: null,
    
    // 积分
    availablePoints: 0,
    usePoints: false,
    pointsToUse: 0,
    pointsDiscount: 0,
    maxPoints: 0,
    
    // 备注
    remark: ''
  },

  onLoad() {
    this.loadCartData();
    this.loadCoupons();
    this.loadPoints();
  },

  onShow() {
    this.loadCartData();
    this.updateCartCount();
  },

  // 加载购物车数据
  loadCartData() {
    const cart = storage.getCart() || [];
    const cartItems = cart.map(item => ({
      ...item,
      selected: false
    }));
    this.setData({ cartItems }, () => {
      this.calculateSummary();
    });
  },

  // 加载优惠券
  async loadCoupons() {
    try {
      const res = await couponApi.getAvailableCoupons();
      if (res.code === 200) {
        this.setData({ availableCoupons: res.data });
      }
    } catch (error) {
      console.error('加载优惠券失败:', error);
    }
  },

  // 加载积分
  async loadPoints() {
    try {
      const res = await pointsApi.getPoints();
      if (res.code === 200) {
        this.setData({ availablePoints: res.data.points });
      }
    } catch (error) {
      console.error('加载积分失败:', error);
    }
  },

  // 更新购物车数量
  updateCartCount() {
    const cart = storage.getCart() || [];
    const count = cart.reduce((sum, item) => sum + item.quantity, 0);
    app.globalData.cartCount = count;
    return count;
  },

  // 计算汇总信息
  calculateSummary() {
    const { cartItems, selectedCoupon, usePoints, pointsToUse } = this.data;
    
    let selectedCount = 0;
    let selectedAmount = 0;
    
    cartItems.forEach(item => {
      if (item.selected) {
        selectedCount += item.quantity;
        selectedAmount += item.subtotal;
      }
    });
    
    // 自动匹配最优优惠券
    if (!selectedCoupon && this.data.availableCoupons.length > 0) {
      const bestCoupon = this.findBestCoupon(selectedAmount);
      if (bestCoupon) {
        this.setData({ selectedCoupon: bestCoupon });
      }
    }

    const discount = this.calculateDiscount(selectedAmount);
    const pointsDiscount = usePoints ? pointsToUse / 100 : 0;
    const totalAmount = Math.max(0, selectedAmount - discount - pointsDiscount);
    const isAllSelected = cartItems.length > 0 && cartItems.every(item => item.selected);
    
    this.setData({
      selectedCount,
      selectedAmount,
      totalDiscount: discount + pointsDiscount,
      totalAmount: totalAmount.toFixed(2),
      isAllSelected
    });
  },

  // 寻找最优优惠券
  findBestCoupon(amount) {
    return this.data.availableCoupons
      .filter(c => amount >= c.minAmount)
      .sort((a, b) => b.value - a.value)[0];
  },

  // 计算优惠金额
  calculateDiscount(amount) {
    const { selectedCoupon } = this.data;
    if (!selectedCoupon || amount < selectedCoupon.minAmount) return 0;
    return selectedCoupon.value;
  },

  // 切换全选
  toggleSelectAll() {
    const { cartItems, isAllSelected } = this.data;
    const newSelectedState = !isAllSelected;
    const newCartItems = cartItems.map(item => ({
      ...item,
      selected: newSelectedState
    }));
    this.setData({
      cartItems: newCartItems,
      isAllSelected: newSelectedState
    }, () => {
      this.calculateSummary();
    });
  },

  // 切换单个商品选中状态
  toggleSelectItem(e) {
    const index = e.currentTarget.dataset.index;
    const cartItems = [...this.data.cartItems];
    cartItems[index].selected = !cartItems[index].selected;
    this.setData({ cartItems }, () => {
      this.calculateSummary();
    });
  },

  // 数量变化
  onQuantityChange(e) {
    const index = e.currentTarget.dataset.index;
    const quantity = e.detail.value;
    const cartItems = [...this.data.cartItems];
    cartItems[index].quantity = parseInt(quantity) || 1;
    cartItems[index].subtotal = (cartItems[index].price + (cartItems[index].toppingsCost || 0)) * cartItems[index].quantity;
    
    const cart = cartItems.map(item => {
      const { selected, ...rest } = item;
      return rest;
    });
    storage.saveCart(cart);
    
    this.setData({ cartItems }, () => {
      this.calculateSummary();
      this.updateCartCount();
    });
  },

  // 删除商品
  deleteItem(e) {
    const index = e.currentTarget.dataset.index;
    const cartItems = [...this.data.cartItems];
    cartItems.splice(index, 1);
    
    const cart = cartItems.map(item => {
      const { selected, ...rest } = item;
      return rest;
    });
    storage.saveCart(cart);
    
    this.setData({ cartItems }, () => {
      this.calculateSummary();
      this.updateCartCount();
    });
  },

  // 备注输入
  onRemarkInput(e) {
    this.setData({ remark: e.detail.value });
  },

  // 跳转到结算页
  goToCheckout() {
    const { cartItems, selectedCoupon, usePoints, pointsToUse, remark } = this.data;
    const selectedItems = cartItems.filter(item => item.selected);
    
    if (selectedItems.length === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' });
      return;
    }
    
    const checkoutData = {
      items: selectedItems.map(item => {
        const { selected, ...rest } = item;
        return rest;
      }),
      coupon: selectedCoupon,
      usePoints,
      pointsToUse,
      remark
    };
    
    wx.setStorageSync('checkoutData', checkoutData);
    wx.navigateTo({ url: '/pages/order/checkout' });
  }
});