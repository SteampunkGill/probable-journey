const api = require('../../utils/api');

Page({
  data: {
    loading: false,
    oldPassword: '',
    newPassword: '',
    confirmPassword: ''
  },

  onInput(e) {
    const { key } = e.currentTarget.dataset;
    this.setData({ [key]: e.detail.value });
  },

  async handleSubmit() {
    const { oldPassword, newPassword, confirmPassword } = this.data;

    if (!oldPassword) return wx.showToast({ title: '请输入旧密码', icon: 'none' });
    if (!newPassword) return wx.showToast({ title: '请输入新密码', icon: 'none' });
    if (newPassword.length < 6) return wx.showToast({ title: '新密码长度不能少于6位', icon: 'none' });
    if (newPassword !== confirmPassword) return wx.showToast({ title: '两次输入的新密码不一致', icon: 'none' });

    this.setData({ loading: true });
    try {
      const res = await api.changePassword(oldPassword, newPassword);
      if (res && (res.code === 200 || res.status === 'success')) {
        wx.showModal({
          title: '提示',
          content: '密码修改成功，请重新登录',
          showCancel: false,
          success: () => {
            wx.removeStorageSync('token');
            wx.removeStorageSync('userInfo');
            wx.reLaunch({ url: '/pages/login/login' });
          }
        });
      } else {
        wx.showToast({ title: res.message || '修改失败', icon: 'none' });
      }
    } catch (error) {
      wx.showToast({ title: error.message || '修改失败，请检查旧密码', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  }
});