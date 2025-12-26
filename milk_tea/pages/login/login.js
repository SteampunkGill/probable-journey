// pages/login/login.js
const app = getApp();
const storage = require('../../utils/storage.js');
const util = require('../../utils/util.js');

Page({
  data: {
    // 登录方式：password-密码登录
    loginType: 'password',
    
    // 密码登录相关
    username: '',
    password: '',
    showPassword: false,
    rememberMe: true,
    
    // 协议相关
    agreed: false,
    
    // 加载状态
    loading: false,
    loadingText: '',
    
    // 输入框聚焦状态
    isFocusUsername: false,
    isFocusPassword: false,
  },

  onLoad(options) {
    // 从本地存储加载记住的用户名
    const rememberedUsername = storage.getStorage('remembered_username');
    if (rememberedUsername) {
      this.setData({
        username: rememberedUsername,
        rememberMe: true
      });
    }
    
    // 检查是否有跳转参数
    if (options.type) {
      this.setData({
        loginType: options.type
      });
    }
  },

  onShow() {
    // 检查是否已登录
    if (app.globalData.token) {
      wx.showModal({
        title: '提示',
        content: '您已登录，是否返回上一页？',
        success: (res) => {
          if (res.confirm) {
            wx.navigateBack();
          }
        }
      });
    }
  },

  // =========== 输入框事件 ===========
  
  // 用户名输入
  onUsernameInput(e) {
    this.setData({
      username: e.detail.value.trim()
    });
  },

  // 密码输入
  onPasswordInput(e) {
    this.setData({
      password: e.detail.value
    });
  },

  // 输入框聚焦事件
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
  
  // 清空用户名
  clearUsername() {
    this.setData({ username: '' });
  },

  // 切换密码可见性
  togglePasswordVisibility() {
    this.setData({
      showPassword: !this.data.showPassword
    });
  },

  // =========== 表单操作 ===========

  // 记住我选项变化
  onRememberChange(e) {
    this.setData({
      rememberMe: e.detail.value.includes('true')
    });
  },

  // 协议选项变化
  onAgreementChange(e) {
    this.setData({
      agreed: e.detail.value.includes('true')
    });
  },

  // =========== 登录功能 ===========

  // 密码登录
  async loginWithPassword() {
    try {
      // 检查网络
      await this.checkNetworkBeforeLogin();
      
      const { username, password, agreed, rememberMe } = this.data;
      
      // 验证输入
      if (!username || !password) {
        wx.showToast({
          title: '请输入用户名和密码',
          icon: 'none'
        });
        return;
      }
      
      if (!agreed) {
        wx.showToast({
          title: '请同意用户协议和隐私政策',
          icon: 'none'
        });
        return;
      }
      
      this.setLoading(true, '登录中...');
      
      // 调用真实API
      const { authApi } = require('../../utils/api.js');
      const userData = await authApi.login(username, password);
      
      // request.js 已经处理了 code !== 200 的情况并抛出错误
      // 如果能执行到这里，说明登录成功，userData 就是用户信息
      if (userData) {
        // 记住用户名
        if (rememberMe) {
          storage.setStorage('remembered_username', username);
        } else {
          storage.removeStorage('remembered_username');
        }
        
        // 登录成功
        await this.handleLoginSuccess(userData);
      }
      
    } catch (error) {
      wx.showToast({
        title: error.message || '登录失败',
        icon: 'none'
      });
    } finally {
      this.setLoading(false);
    }
  },

  // 微信授权登录
  async loginWithWechat() {
    if (!this.data.agreed) {
      wx.showToast({
        title: '请先同意用户协议和隐私政策',
        icon: 'none'
      });
      return;
    }
    
    this.setLoading(true, '授权中...');
    
    try {
      // 获取微信登录凭证
      const loginRes = await new Promise((resolve, reject) => {
        wx.login({
          success: resolve,
          fail: reject
        });
      });
      
      if (!loginRes.code) {
        throw new Error('获取微信code失败');
      }
      
      // 调用真实API
      const { authApi } = require('../../utils/api.js');
      const userData = await authApi.wechatLogin(loginRes.code);
      
      if (userData) {
        // 登录成功
        await this.handleLoginSuccess({
          token: userData.token || 'mock_token_' + userData.id,
          userInfo: userData
        });
      }
      
    } catch (error) {
      wx.showToast({
        title: error.message || '微信登录失败',
        icon: 'none'
      });
    } finally {
      this.setLoading(false);
    }
  },

  // 获取用户信息
  onGetUserInfo(e) {
    if (!this.data.agreed) {
      wx.showToast({
        title: '请先同意用户协议和隐私政策',
        icon: 'none'
      });
      return;
    }
    
    const userInfo = e.detail.userInfo;
    
    if (userInfo) {
      // 用户允许授权
      this.loginWithWechat();
    } else {
      // 用户拒绝授权
      wx.showToast({
        title: '授权失败',
        icon: 'none'
      });
    }
  },

  // =========== 模拟API调用 ===========

  // 模拟密码登录
  async mockLoginWithPassword(username, password) {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // 模拟验证（实际应该调用API）
    if (!username || !password) {
      throw new Error('用户名和密码不能为空');
    }
    
    if (password !== '123456') {
      throw new Error('密码错误，请重试');
    }
    
    // 返回模拟的用户数据
    return {
      token: 'mock_jwt_token_' + Date.now(),
      userInfo: {
        id: 'user_' + Date.now(),
        username: username,
        nickname: username === 'admin' ? '管理员' : '奶茶爱好者',
        avatar: 'https://images.unsplash.com/photo-1494790108755-2616c113a1c4',
        phone: '13800138000',
        level: 'gold',
        points: 1250,
        balance: 86.50
      }
    };
  },

  // 模拟微信登录
  async mockWechatLogin(code) {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 1500));
    
    // 返回模拟的用户数据
    return {
      token: 'mock_jwt_token_' + Date.now(),
      userInfo: {
        id: 'user_wx_' + Date.now(),
        username: 'wx_' + Date.now(),
        nickname: '微信用户',
        avatar: 'https://images.unsplash.com/photo-1494790108755-2616c113a1c4',
        phone: '',
        level: 'normal',
        points: 100,
        balance: 0
      }
    };
  },

  // =========== 辅助功能 ===========

  // 处理登录成功
  async handleLoginSuccess(loginResult) {
    try {
      // 保存token和用户信息
      const token = loginResult.token || 'mock_token_' + loginResult.id;
      const userInfo = loginResult;
      
      wx.setStorageSync('token', token);
      wx.setStorageSync('userInfo', userInfo);
      
      app.globalData.token = token;
      app.globalData.userInfo = userInfo;
      app.globalData.isGuest = false; // 明确不是游客
      
      // 显示登录成功提示
      wx.showToast({
        title: '登录成功',
        icon: 'success',
        duration: 1500
      });
      
      // 延迟跳转
      setTimeout(() => {
        // 获取页面栈
        const pages = getCurrentPages();
        
        if (pages.length > 1) {
          // 有上一页，返回上一页
          wx.navigateBack();
        } else {
          // 没有上一页，跳转到首页
          wx.switchTab({
            url: '/pages/index/index'
          });
        }
      }, 1500);
      
    } catch (error) {
      console.error('Login success handler error:', error);
      wx.showToast({
        title: '登录处理失败',
        icon: 'none'
      });
    }
  },

  // 设置加载状态
  setLoading(loading, text = '') {
    this.setData({
      loading,
      loadingText: text
    });
  },

  // 检查网络
  checkNetworkBeforeLogin() {
    return new Promise((resolve, reject) => {
      wx.getNetworkType({
        success: (res) => {
          const networkType = res.networkType;
          if (networkType === 'none') {
            wx.showModal({
              title: '网络异常',
              content: '当前网络不可用，请检查网络设置',
              showCancel: false
            });
            reject(new Error('网络不可用'));
          } else {
            resolve();
          }
        },
        fail: () => {
          reject(new Error('网络检测失败'));
        }
      });
    });
  },

  // 监听键盘完成按钮
  onInputConfirm(e) {
    // 点击完成直接登录
    if (this.data.username && this.data.password && this.data.agreed) {
      this.loginWithPassword();
    }
  },

  // =========== 开发辅助功能 ===========

  // 快速测试账号登录
  quickLogin() {
    this.setData({
      username: 'testuser',
      password: '123456',
      agreed: true
    });
    
    wx.showToast({
      title: '已填入测试账号',
      icon: 'none',
      duration: 1500
    });
  },

  // 清除所有输入
  clearAllInputs() {
    this.setData({
      username: '',
      password: '',
      showPassword: false,
      isFocusUsername: false,
      isFocusPassword: false,
    });
    
    wx.showToast({
      title: '已清空所有输入',
      icon: 'success',
      duration: 1500
    });
  },

  // =========== 跳转功能 ===========

  // 跳转到忘记密码页
  goToForgotPassword() {
    wx.navigateTo({
      url: '/pages/forgot/forgot'
    });
  },

  // 跳转到注册页
  goToRegister() {
    wx.navigateTo({
      url: '/pages/register/register'
    });
  },

  // 跳转到用户协议
  goToAgreement(e) {
    e && e.stopPropagation();
    wx.navigateTo({
      url: '/pages/agreement/agreement?type=user'
    });
  },

  // 跳转到隐私政策
  goToPrivacy(e) {
    e && e.stopPropagation();
    wx.navigateTo({
      url: '/pages/agreement/agreement?type=privacy'
    });
  },

  // 游客模式进入
  goToHomeAsGuest() {
    wx.showModal({
      title: '温馨提示',
      content: '以游客身份进入将无法使用收藏、订单等功能，确认继续吗？',
      success: (res) => {
        if (res.confirm) {
          app.globalData.isGuest = true;
          wx.switchTab({
            url: '/pages/index/index'
          });
        }
      }
    });
  }
});