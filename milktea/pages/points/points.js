const { pointsApi, authApi } = require('../../utils/api.js');

Page({
  data: {
    userPoints: 0,
    records: [],
    page: 1,
    size: 20,
    hasMore: true,
    isLoading: false
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    try {
      const profileRes = await authApi.getUserProfile();
      this.setData({
        userPoints: profileRes.data.points || 0,
        records: [],
        page: 1,
        hasMore: true
      }, () => {
        this.loadRecords();
      });
    } catch (error) {
      console.error('加载积分数据失败:', error);
    }
  },

  async loadRecords() {
    if (this.data.isLoading || !this.data.hasMore) return;

    this.setData({ isLoading: true });
    try {
      const res = await pointsApi.getPointsTransactions(this.data.page, this.data.size);
      const list = res.data.content || res.data || [];
      
      this.setData({
        records: [...this.data.records, ...list],
        hasMore: list.length === this.data.size,
        page: this.data.page + 1
      });
    } catch (error) {
      console.error('加载积分记录失败:', error);
    } finally {
      this.setData({ isLoading: false });
    }
  },

  onReachBottom() {
    this.loadRecords();
  },

  goToMall() {
    wx.navigateTo({
      url: '/pages/points/mall'
    });
  },

  async goToRules() {
    try {
      const res = await pointsApi.getPointsRules();
      wx.showModal({
        title: '积分规则',
        content: res.data.rules || '暂无规则说明',
        showCancel: false
      });
    } catch (error) {
      console.error('获取积分规则失败:', error);
    }
  }
});