// pages/wallet/wallet.js  
// pages/points/points.js
// pages/settings/settings.js
// pages/profile/profile.js
// 由于篇幅原因，这里创建一个统一的简化版本

const app = getApp();
const storage = require('../../utils/storage.js');

Page({
  data: {
    userInfo: null,
    balance: 0,
    points: 0
  },

  onLoad() {
    this.loadData();
  },

  onShow() {
    this.loadData();
  },

  async loadData() {
    const userInfo = storage.getUserInfo();
    this.setData({ userInfo });
    
    try {
      const { memberApi } = require('../../utils/api.js');
      const res = await memberApi.getCardBalance();
      if (res.code === 200) {
        this.setData({
          balance: res.data.balance || 0,
          points: res.data.points || 0
        });
      }
    } catch (error) {
      console.error('加载钱包数据失败:', error);
    }
  },

  // 充值
  recharge() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 提现
  withdraw() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  },

  // 查看明细
  viewDetail() {
    wx.showToast({ title: '功能开发中', icon: 'none' });
  }
});
