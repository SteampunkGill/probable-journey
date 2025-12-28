const request = require('../../utils/request');

Page({
  data: {
    loginType: 'user', // 'user' or 'admin'
    username: '',
    password: '',
    showPassword: false,
    rememberMe: true,
    agreed: false,
    loading: false,
    isFocusUsername: false,
    isFocusPassword: false
  },

  onLoad() {
    const remembered = wx.getStorageSync('remembered_username');
    const rememberedRole = wx.getStorageSync('remembered_role');
    if (remembered) {
      this.setData({ username: remembered });
    }
    if (rememberedRole) {
      this.setData({ loginType: rememberedRole });
    }
  },

  switchRole(e) {
    const role = e.currentTarget.dataset.role;
    if (this.data.loading) return;
    this.setData({ loginType: role });
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value });
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value });
  },

  onUsernameFocus() {
    this.setData({ isFocusUsername: true });
  },

  onUsernameBlur() {
    this.setData({ isFocusUsername: false });
  },

  onPasswordFocus() {
    this.setData({ isFocusPassword: true });
  },

  onPasswordBlur() {
    this.setData({ isFocusPassword: false });
  },

  clearUsername() {
    this.setData({ username: '' });
  },

  togglePassword() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  toggleRemember() {
    this.setData({ rememberMe: !this.data.rememberMe });
  },

  toggleAgreed() {
    this.setData({ agreed: !this.data.agreed });
  },

  async handleLogin() {
    const { username, password, loginType, agreed, rememberMe } = this.data;

    if (!username.trim()) {
      wx.showToast({ title: '请输入用户名', icon: 'none' });
      return;
    }
    if (!password.trim()) {
      wx.showToast({ title: '请输入密码', icon: 'none' });
      return;
    }
    if (loginType === 'user' && !agreed) {
      wx.showToast({ title: '请先同意协议', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    try {
      const res = await request({
        url: '/api/auth/login',
        method: 'POST',
        data: { username, password }
      });

      if (res.code === 200) {
        const data = res.data;
        const userInfo = data.user || data;
        const token = data.token;
        const isAdmin = data.isAdmin || userInfo.status === 'ADMIN';

        if (loginType === 'admin' && !isAdmin) {
          throw new Error('该账号没有管理员权限');
        }

        if (isAdmin) {
          userInfo.role = 'admin';
        }

        wx.setStorageSync('token', token);
        wx.setStorageSync('userInfo', userInfo);

        if (rememberMe) {
          wx.setStorageSync('remembered_username', username);
          wx.setStorageSync('remembered_role', loginType);
        } else {
          wx.removeStorageSync('remembered_username');
          wx.removeStorageSync('remembered_role');
        }

        wx.showToast({ title: '登录成功' });
        
        setTimeout(() => {
          if (loginType === 'admin') {
            wx.reLaunch({ url: '/pages/admin/index' });
          } else {
            wx.navigateBack({
              fail: () => {
                wx.switchTab({ url: '/pages/index/index' });
              }
            });
          }
        }, 1500);
      } else {
        wx.showToast({ title: res.message || '登录失败', icon: 'none' });
      }
    } catch (error) {
      wx.showToast({ title: error.message || '登录失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  handleWechatLogin() {
    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意协议', icon: 'none' });
      return;
    }
    wx.showToast({ title: '正在调用微信登录...', icon: 'none' });
    // 实际开发中调用 wx.login 并换取 token
  },

  goToRegister() {
    wx.navigateTo({ url: '/pages/login/register' });
  },

  goToAgreement(e) {
    const type = e.currentTarget.dataset.type;
    wx.navigateTo({ url: `/pages/settings/about?type=${type}` });
  },

  goToHomeAsGuest() {
    wx.switchTab({ url: '/pages/index/index' });
  }
});