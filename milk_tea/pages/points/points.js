// pages/points/points.js
const app = getApp();

Page({
  data: {
    // 用户积分
    userPoints: 0,
    
    // 积分商品分类
    categories: [
      { id: 'all', name: '全部' },
      { id: 'drink', name: '饮品' },
      { id: 'coupon', name: '优惠券' },
      { id: 'gift', name: '周边' }
    ],
    activeCategoryId: 'all',
    
    // 积分商品列表
    productList: [],
    
    // 兑换记录
    exchangeRecords: [],
    
    // 显示兑换记录
    showRecords: false,
    
    loading: false
  },

  onLoad() {
    this.loadUserPoints();
    this.loadProducts();
  },

  onShow() {
    this.loadUserPoints();
  },

  onPullDownRefresh() {
    this.loadProducts();
    wx.stopPullDownRefresh();
  },

  // 加载用户积分
  async loadUserPoints() {
    try {
      const { authApi } = require('../../utils/api.js');
      const res = await authApi.getUserProfile();
      if (res.code === 200) {
        this.setData({ userPoints: res.data.points || 0 });
      }
    } catch (error) {
      console.error('加载积分失败:', error);
    }
  },

  // 加载积分商品
  async loadProducts() {
    this.setData({ loading: true });
    
    try {
      const { pointsMallApi } = require('../../utils/api.js');
      const res = await pointsMallApi.getProducts();
      
      if (res.code === 200) {
        const products = res.data.map(p => ({
          id: p.id,
          name: p.name,
          image: p.imageUrl,
          points: p.pointsRequired,
          stock: p.stock,
          category: 'all', // 后端暂未细分
          description: p.name
        }));
        
        this.setData({
          productList: products,
          loading: false
        });
      } else {
        throw new Error(res.message);
      }
    } catch (error) {
      console.error('加载积分商品失败:', error);
      this.setData({ loading: false });
    }
  },

  // 切换分类
  selectCategory(e) {
    const { id } = e.currentTarget.dataset;
    this.setData({ activeCategoryId: id });
  },

  // 兑换商品
  exchangeProduct(e) {
    const { id } = e.currentTarget.dataset;
    const product = this.data.productList.find(p => p.id === id);
    
    if (!product) return;
    
    if (this.data.userPoints < product.points) {
      wx.showToast({
        title: '积分不足',
        icon: 'none'
      });
      return;
    }
    
    if (product.stock <= 0) {
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
      return;
    }
    
    wx.showModal({
      title: '确认兑换',
      content: `确定花费${product.points}积分兑换${product.name}吗？`,
      success: (res) => {
        if (res.confirm) {
          this.doExchange(product);
        }
      }
    });
  },

  // 执行兑换
  async doExchange(product) {
    try {
      wx.showLoading({ title: '兑换中...' });
      const { pointsMallApi } = require('../../utils/api.js');
      const res = await pointsMallApi.exchangeProduct(product.id);
      
      wx.hideLoading();
      if (res.code === 200) {
        wx.showToast({
          title: '兑换成功',
          icon: 'success'
        });
        // 震动反馈
        wx.vibrateShort();
        // 刷新数据
        this.loadUserPoints();
        this.loadProducts();
      } else {
        wx.showToast({
          title: res.message || '兑换失败',
          icon: 'none'
        });
      }
    } catch (error) {
      wx.hideLoading();
      wx.showToast({
        title: '兑换失败',
        icon: 'none'
      });
    }
  },

  // 显示兑换记录
  toggleRecords() {
    this.setData({ showRecords: !this.data.showRecords });
  },

  // 关闭记录
  hideRecords() {
    this.setData({ showRecords: false });
  },

  // 跳转到积分规则
  goToRules() {
    wx.showModal({
      title: '积分规则',
      content: '1. 每消费1元获得1积分\n2. 每日签到可获得10积分\n3. 完成订单评价可获得20积分\n4. 积分可用于兑换商品',
      showCancel: false
    });
  }
});
