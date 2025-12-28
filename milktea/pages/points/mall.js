const { pointsApi, authApi } = require('../../utils/api.js');

Page({
  data: {
    points: 0,
    signedIn: false,
    categories: [],
    activeCategoryId: 'all',
    products: [],
    loading: false
  },

  onLoad() {
    this.loadInitialData();
  },

  async loadInitialData() {
    try {
      const [profileRes, categoriesRes] = await Promise.all([
        authApi.getUserProfile(),
        pointsApi.getPointsCategories()
      ]);

      const backendCategories = categoriesRes.data || [];
      this.setData({
        points: profileRes.data.points || 0,
        categories: [
          { id: 'all', name: '全部' },
          ...backendCategories.map(c => ({
            id: c.type.toLowerCase(),
            name: c.name
          }))
        ]
      });

      this.loadProducts();
    } catch (error) {
      console.error('加载初始数据失败:', error);
    }
  },

  async loadProducts() {
    this.setData({ loading: true });
    try {
      const category = this.data.activeCategoryId === 'all' ? null : this.data.activeCategoryId.toUpperCase();
      const res = await pointsApi.getPointsProducts(1, 50, category);
      this.setData({
        products: res.data.list || res.data || []
      });
    } catch (error) {
      console.error('加载商品失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  selectCategory(e) {
    const id = e.currentTarget.dataset.id;
    this.setData({ activeCategoryId: id }, () => {
      this.loadProducts();
    });
  },

  async signIn() {
    if (this.data.signedIn) return;
    
    try {
      const res = await pointsApi.signIn();
      this.setData({
        points: res.data || this.data.points + 10,
        signedIn: true
      });
      wx.showToast({ title: '签到成功' });
    } catch (error) {
      console.error('签到失败:', error);
    }
  },

  async exchangeProduct(e) {
    const product = e.currentTarget.dataset.product;
    if (this.data.points < product.points) {
      wx.showToast({ title: '积分不足', icon: 'none' });
      return;
    }

    wx.showModal({
      title: '兑换确认',
      content: `确定花费 ${product.points} 积分兑换 ${product.name} 吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await pointsApi.exchangeProduct(product.id);
            wx.showToast({ title: '兑换成功' });
            this.loadInitialData(); // 刷新积分和列表
          } catch (error) {
            console.error('兑换失败:', error);
          }
        }
      }
    });
  },

  viewProductDetail(e) {
    const id = e.currentTarget.dataset.id;
    // 积分商品详情逻辑，目前可复用普通商品详情或弹出说明
    wx.showToast({ title: '查看详情: ' + id, icon: 'none' });
  }
});