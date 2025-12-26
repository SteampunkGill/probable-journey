const { authApi } = require('../../utils/api.js');

Page({
  data: {
    username: '',
    phone: '',
    password: '',
    confirmPassword: '',
    loading: false
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  onConfirmPasswordInput(e) {
    this.setData({ confirmPassword: e.detail.value });
  },

  async handleRegister() {
    const { username, phone, password, confirmPassword } = this.data;

    if (!username || !phone || !password || !confirmPassword) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' });
      return;
    }

    if (password !== confirmPassword) {
      wx.showToast({ title: '两次输入的密码不一致', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    try {
      await authApi.register({
        username,
        phone,
        password,
        nickname: username // 默认昵称为用户名
      });

      wx.showToast({
        title: '注册成功',
        icon: 'success',
        duration: 2000
      });

      setTimeout(() => {
        wx.navigateBack();
      }, 2000);
    } catch (error) {
      wx.showToast({
        title: error.message || '注册失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  },

  goToLogin() {
    wx.navigateBack();
  }
});