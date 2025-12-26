// pages/settings/settings.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { systemApi, authApi } = require('../../utils/api.js');

Page({
  data: {
    // 用户信息
    userInfo: null,
    
    // 设置项列表
    settings: [
      {
        id: 1,
        title: '消息推送设置',
        icon: 'icon-notification',
        type: 'switch',
        value: true,
        key: 'notification'
      },
      {
        id: 2,
        title: '隐私协议',
        icon: 'icon-privacy',
        type: 'navigate',
        url: '/pages/settings/privacy'
      },
      {
        id: 3,
        title: '关于我们',
        icon: 'icon-info',
        type: 'navigate',
        url: '/pages/settings/about'
      },
      {
        id: 4,
        title: '帮助中心',
        icon: 'icon-help',
        type: 'navigate',
        url: '/pages/settings/help'
      },
      {
        id: 5,
        title: '清除缓存',
        icon: 'icon-clear',
        type: 'action',
        value: '0.0MB'
      },
      {
        id: 6,
        title: '积分商城',
        icon: 'icon-gift',
        type: 'navigate',
        url: '/pages/points/mall'
      },
      {
        id: 7,
        title: '检查更新',
        icon: 'icon-update',
        type: 'action',
        value: '当前版本 1.0.0'
      }
    ],
    
    // 账户相关
    accountSettings: [
      {
        id: 8,
        title: '修改密码',
        icon: 'icon-password',
        type: 'navigate',
        url: '/pages/settings/change-password'
      },
      {
        id: 10,
        title: '注销账号',
        icon: 'icon-delete',
        type: 'danger',
        color: '#ff4d4f'
      }
    ],
    
    // 缓存大小
    cacheSize: '0.0MB'
  },

  onLoad() {
    this.loadUserData();
    this.loadSettings();
    this.calculateCacheSize();
  },

  onShow() {
    this.loadUserData();
  },

  // 加载用户数据
  async loadUserData() {
    const token = storage.getStorage('token');
    if (!token) {
      this.setData({ userInfo: null });
      return;
    }
    
    try {
      const userInfo = storage.getUserInfo();
      if (userInfo) {
        // 适配后端字段名 (avatarUrl -> avatar)
        if (userInfo.avatarUrl && !userInfo.avatar) {
          userInfo.avatar = userInfo.avatarUrl;
        }
      }
      this.setData({ userInfo });
    } catch (error) {
      console.error('加载用户数据失败:', error);
    }
  },

  // 加载设置
  async loadSettings() {
    try {
      // 获取通知设置
      const notificationSettings = await systemApi.getNotificationSettings();
      
      // 更新设置项
      const settings = this.data.settings.map(item => {
        if (item.key === 'notification') {
          return { ...item, value: notificationSettings.enabled };
        }
        return item;
      });
      
      this.setData({ settings });
    } catch (error) {
      console.error('加载设置失败:', error);
    }
  },

  // 计算缓存大小
  calculateCacheSize() {
    const cacheInfo = wx.getStorageInfoSync();
    const size = (cacheInfo.currentSize / 1024).toFixed(1);
    this.setData({ 
      cacheSize: `${size}MB`,
      'settings[4].value': `${size}MB`
    });
  },

  // 切换开关
  async onSwitchChange(e) {
    const { index, value } = e.detail;
    const setting = this.data.settings[index];
    
    if (setting.key === 'notification') {
      try {
        await systemApi.updateNotificationSettings({ enabled: value });
        wx.showToast({ title: '设置已保存', icon: 'success' });
      } catch (error) {
        console.error('更新通知设置失败:', error);
        wx.showToast({ title: '保存失败', icon: 'none' });
        // 恢复原状态
        const settings = this.data.settings;
        settings[index].value = !value;
        this.setData({ settings });
      }
    }
  },

  // 点击设置项
  onSettingTap(e) {
    const { index, type } = e.currentTarget.dataset;
    
    if (type === 'account') {
      const setting = this.data.accountSettings[index];
      this.handleAccountSetting(setting);
    } else {
      const setting = this.data.settings[index];
      this.handleSetting(setting);
    }
  },

  // 处理设置项点击
  handleSetting(setting) {
    switch (setting.type) {
      case 'navigate':
        if (setting.url) {
          wx.navigateTo({ url: setting.url });
        }
        break;
        
      case 'action':
        if (setting.title === '清除缓存') {
          this.clearCache();
        } else if (setting.title === '检查更新') {
          this.checkUpdate();
        }
        break;
        
      case 'danger':
        this.showLogoutConfirm();
        break;
    }
  },

  // 处理账户设置
  handleAccountSetting(setting) {
    switch (setting.title) {
      case '修改密码':
        wx.navigateTo({ url: '/pages/settings/change-password' });
        break;
        
        
      case '注销账号':
        this.showDeleteAccountConfirm();
        break;
    }
  },

  // 清除缓存
  clearCache() {
    wx.showModal({
      title: '清除缓存',
      content: '确定要清除所有缓存数据吗？',
      success: (res) => {
        if (res.confirm) {
          wx.clearStorageSync();
          this.calculateCacheSize();
          wx.showToast({ title: '缓存已清除', icon: 'success' });
        }
      }
    });
  },

  // 检查更新
  checkUpdate() {
    const updateManager = wx.getUpdateManager();
    
    updateManager.onCheckForUpdate((res) => {
      if (res.hasUpdate) {
        wx.showModal({
          title: '发现新版本',
          content: '检测到新版本，是否立即更新？',
          success: (res) => {
            if (res.confirm) {
              updateManager.applyUpdate();
            }
          }
        });
      } else {
        wx.showToast({ title: '已是最新版本', icon: 'success' });
      }
    });
  },

  // 显示注销确认
  showLogoutConfirm() {
    wx.showModal({
      title: '退出登录',
      content: '确定要退出登录吗？',
      success: async (res) => {
        if (res.confirm) {
          await this.logout();
        }
      }
    });
  },

  // 显示注销账号确认
  showDeleteAccountConfirm() {
    wx.showModal({
      title: '注销账号',
      content: '此操作将永久删除您的账号和所有数据，且无法恢复。确定要继续吗？',
      confirmText: '确认注销',
      confirmColor: '#ff4d4f',
      success: async (res) => {
        if (res.confirm) {
          await this.deleteAccount();
        }
      }
    });
  },

  // 退出登录
  async logout() {
    try {
      // 调用登出接口
      await authApi.logout();
      
      // 清除本地存储
      storage.clearStorage();
      
      // 重置全局数据
      app.globalData.userInfo = null;
      app.globalData.token = null;
      
      // 跳转到首页
      wx.reLaunch({ url: '/pages/index/index' });
      
      wx.showToast({ title: '已退出登录', icon: 'success' });
    } catch (error) {
      console.error('退出登录失败:', error);
      // 即使接口失败也清除本地数据
      storage.clearStorage();
      app.globalData.userInfo = null;
      app.globalData.token = null;
      wx.reLaunch({ url: '/pages/index/index' });
    }
  },

  // 注销账号
  async deleteAccount() {
    wx.showLoading({ title: '处理中...' });
    
    try {
      // 调用注销账号的接口
      const res = await authApi.deactivate();
      
      if (res.code === 200) {
        // 清除本地存储
        storage.clearStorage();
        
        // 重置全局数据
        app.globalData.userInfo = null;
        app.globalData.token = null;
        
        wx.hideLoading();
        wx.showToast({ title: '账号已注销', icon: 'success' });
        
        // 跳转到首页
        setTimeout(() => {
          wx.reLaunch({ url: '/pages/index/index' });
        }, 1500);
      } else {
        throw new Error(res.message || '注销失败');
      }
      
    } catch (error) {
      wx.hideLoading();
      console.error('注销账号失败:', error);
      wx.showToast({ title: error.message || '注销失败', icon: 'none' });
    }
  },

  // 跳转到登录页
  goToLogin() {
    wx.navigateTo({ url: '/pages/login/login' });
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
});