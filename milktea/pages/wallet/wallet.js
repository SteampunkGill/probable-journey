const { authApi } = require('../../utils/api.js');

Page({
  data: {
    balance: '0.00'
  },

  onShow() {
    this.loadBalance();
  },

  async loadBalance() {
    try {
      const res = await authApi.getUserProfile();
      this.setData({
        balance: (res.data.balance || 0).toFixed(2)
      });
    } catch (error) {
      console.error('加载余额失败:', error);
    }
  },

  recharge() {
    wx.showModal({
      title: '充值提示',
      content: '充值功能请前往柜台或使用支付宝/微信充值',
      showCancel: false
    });
  },

  withdraw() {
    wx.showModal({
      title: '提现提示',
      content: '提现申请已提交，请等待审核',
      showCancel: false
    });
  },

  goToGiftCard() {
    wx.navigateTo({
      url: '/pages/wallet/gift-card'
    });
  },

  viewDetail() {
    wx.showToast({
      title: '暂无更多交易明细',
      icon: 'none'
    });
  }
});