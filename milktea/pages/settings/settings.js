const api = require('../../utils/api');

Page({
  data: {
    showNotificationSettings: false,
    notifSettings: {
      enabled: true,
      orderUpdate: true,
      promotion: false
    }
  },

  onLoad() {
    this.loadSettings();
  },

  async loadSettings() {
    try {
      // 模拟从 API 加载设置
      // const res = await api.getNotificationSettings();
      // if (res.code === 200) {
      //   this.setData({ notifSettings: res.data });
      // }
    } catch (error) {
      console.error('加载设置失败:', error);
    }
  },

  navigateTo(e) {
    const url = e.currentTarget.dataset.url;
    wx.navigateTo({ url });
  },

  toggleNotificationModal() {
    this.setData({
      showNotificationSettings: !this.data.showNotificationSettings
    });
  },

  stopBubbling() {
    // 阻止冒泡
  },

  onSettingChange(e) {
    const key = e.currentTarget.dataset.key;
    const value = e.detail.value;
    this.setData({
      [`notifSettings.${key}`]: value
    });
    this.updateSettings();
  },

  async updateSettings() {
    try {
      // await api.updateNotificationSettings(this.data.notifSettings);
    } catch (error) {
      console.error('更新设置失败:', error);
    }
  },

  handleLogout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          // 清除登录态
          wx.removeStorageSync('token');
          wx.removeStorageSync('userInfo');
          wx.reLaunch({
            url: '/pages/login/login'
          });
        }
      }
    });
  }
});