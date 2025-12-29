const api = require('../../utils/api');

Page({
  navigateTo(e) {
    const url = e.currentTarget.dataset.url;
    wx.navigateTo({ url });
  },

  handleDeactivate() {
    wx.showModal({
      title: '警告',
      content: '确定要注销账号吗？注销后所有数据将被清空且无法恢复！',
      confirmColor: '#ff6b6b',
      success: async (res) => {
        if (res.confirm) {
          try {
            // await api.deactivate();
            wx.showToast({ title: '账号已注销', icon: 'success' });
            wx.removeStorageSync('token');
            wx.removeStorageSync('userInfo');
            setTimeout(() => {
              wx.reLaunch({ url: '/pages/login/login' });
            }, 1500);
          } catch (error) {
            wx.showToast({ title: error.message || '注销失败', icon: 'none' });
          }
        }
      }
    });
  }
});