const api = require('../../utils/api');

Page({
  data: {
    registerType: 'user',
    username: '',
    phone: '',
    password: '',
    confirmPassword: '',
    secret: '',
    showPassword: false,
    showConfirmPassword: false,
    agreed: false,
    loading: false,
    isFocusUsername: false,
    isFocusPhone: false,
    isFocusPassword: false,
    isFocusConfirmPassword: false,
    isFocusSecret: false,
    errors: {
      username: '',
      phone: '',
      password: '',
      confirmPassword: '',
      secret: ''
    }
  },

  onInput(e) {
    const { key } = e.currentTarget.dataset;
    this.setData({ [key]: e.detail.value });
  },

  onFocus(e) {
    const { key } = e.currentTarget.dataset;
    this.setData({ [key]: true });
  },

  switchType(e) {
    this.setData({ registerType: e.currentTarget.dataset.type });
  },

  togglePassword() {
    this.setData({ showPassword: !this.data.showPassword });
  },

  toggleConfirmPassword() {
    this.setData({ showConfirmPassword: !this.data.showConfirmPassword });
  },

  onAgreementChange(e) {
    this.setData({ agreed: e.detail.value.length > 0 });
  },

  validateUsername() {
    this.setData({ isFocusUsername: false });
    const { username } = this.data;
    let error = '';
    if (!username.trim()) {
      error = '用户名不能为空';
    } else if (username.length < 2) {
      error = '用户名至少2个字符';
    }
    this.setData({ 'errors.username': error });
    return !error;
  },

  validatePhone() {
    this.setData({ isFocusPhone: false });
    const { phone } = this.data;
    const phoneReg = /^1[3-9]\d{9}$/;
    let error = '';
    if (!phone.trim()) {
      error = '手机号不能为空';
    } else if (!phoneReg.test(phone)) {
      error = '请输入正确的11位手机号';
    }
    this.setData({ 'errors.phone': error });
    return !error;
  },

  validatePassword() {
    this.setData({ isFocusPassword: false });
    const { password } = this.data;
    let error = '';
    if (!password) {
      error = '密码不能为空';
    } else if (password.length < 6) {
      error = '密码长度不能少于6位';
    }
    this.setData({ 'errors.password': error });
    return !error;
  },

  validateConfirmPassword() {
    this.setData({ isFocusConfirmPassword: false });
    const { password, confirmPassword } = this.data;
    let error = '';
    if (!confirmPassword) {
      error = '请再次输入密码';
    } else if (confirmPassword !== password) {
      error = '两次输入的密码不一致';
    }
    this.setData({ 'errors.confirmPassword': error });
    return !error;
  },

  onBlurSecret() {
    this.setData({ isFocusSecret: false });
  },

  async handleRegister() {
    const v1 = this.validateUsername();
    const v2 = this.validatePhone();
    const v3 = this.validatePassword();
    const v4 = this.validateConfirmPassword();

    let v5 = true;
    if (this.data.registerType === 'admin') {
      if (!this.data.secret) {
        this.setData({ 'errors.secret': '请输入管理员注册密令' });
        v5 = false;
      } else {
        this.setData({ 'errors.secret': '' });
      }
    }

    if (!v1 || !v2 || !v3 || !v4 || !v5) return;

    if (!this.data.agreed) {
      wx.showToast({ title: '请先同意协议', icon: 'none' });
      return;
    }

    this.setData({ loading: true });
    try {
      const registerData = {
        username: this.data.username.trim(),
        phone: this.data.phone.trim(),
        password: this.data.password,
        role: this.data.registerType === 'admin' ? 'admin' : 'user',
        adminCode: this.data.registerType === 'admin' ? this.data.secret.trim() : ''
      };

      const res = await api.register(registerData);
      wx.showModal({
        title: '提示',
        content: this.data.registerType === 'admin' ? '管理员账号注册成功，请登录' : '注册成功，请登录',
        showCancel: false,
        success: () => {
          wx.navigateTo({ url: '/pages/login/login' });
        }
      });
    } catch (error) {
      wx.showToast({ title: error.message || '注册失败', icon: 'none' });
    } finally {
      this.setData({ loading: false });
    }
  },

  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  goToAgreement(e) {
    const { type } = e.currentTarget.dataset;
    wx.navigateTo({ url: `/pages/agreement/agreement?type=${type}` });
  }
});