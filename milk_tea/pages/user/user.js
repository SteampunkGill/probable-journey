
// pages/user/user.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { authApi, memberApi } = require('../../utils/api.js');

Page({
  data: {
    // 用户信息
    userInfo: null,
    cardNumber: '',
    
    // 资产列表
    assets: [
      { id: 1, type: 'balance', name: '余额', icon: 'icon-wallet', value: '0.00', bgColor: '#36CFC9' },
      { id: 2, type: 'coupon', name: '优惠券', icon: 'icon-coupon', value: '0', bgColor: '#FF6B6B' },
      { id: 3, type: 'points', name: '积分', icon: 'icon-points', value: '0', bgColor: '#FFA940' },
      { id: 4, type: 'gift', name: '礼品卡', icon: 'icon-gift', value: '0', bgColor: '#73D13D' }
    ],
    
    // 常用功能
    functions: [
      { id: 1, name: '会员等级', icon: 'icon-star', color: '#FFD700', url: '/pages/member/level' },
      { id: 2, name: '积分商城', icon: 'icon-gift', color: '#FF6B6B', url: '/pages/points/points' },
      { id: 3, name: '地址管理', icon: 'icon-address', color: '#36CFC9', url: '/pages/address/address' },
      { id: 4, name: '我的收藏', icon: 'icon-heart', color: '#FF6B6B', url: '/pages/favorite/favorite' },
      { id: 5, name: '购买记录', icon: 'icon-history', color: '#FFA940', url: '/pages/order-list/order-list' },
      { id: 7, name: '客服中心', icon: 'icon-service', color: '#73D13D', url: '' },
      { id: 8, name: '关于我们', icon: 'icon-info', color: '#597EF7', url: '/pages/about/about' },
      { id: 9, name: '设置', icon: 'icon-settings', color: '#9254DE', url: '/pages/settings/settings' }
    ]
  },

  onLoad() {
    this.loadUserData();
  },

  onShow() {
    this.checkLoginStatus();
    this.loadUserData();
  },

  onPullDownRefresh() {
    this.loadUserData();
    wx.stopPullDownRefresh();
  },

  // 检查登录状态
  checkLoginStatus() {
    const token = storage.getStorage('token');
    if (!token) {
      // 未登录，跳转到登录页
      wx.showModal({
        title: '温馨提示',
        content: '请先登录以查看个人中心',
        confirmText: '去登录',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }
        }
      });
    }
  },

  // 加载用户数据
  async loadUserData() {
    try {
      const token = storage.getStorage('token');
      if (!token) {
        this.setData({ userInfo: null });
        return;
      }
      
      // 调用真实API获取用户信息
      // 注意：request.js 已经处理了 res.data.code === 200 并返回了 res.data.data
      const fullUserInfo = await authApi.getUserProfile();
      if (fullUserInfo) {
        // 适配后端字段名 (avatarUrl -> avatar)
        if (fullUserInfo.avatarUrl && !fullUserInfo.avatar) {
          fullUserInfo.avatar = fullUserInfo.avatarUrl;
        }

        // 获取余额
        try {
          const balanceData = await memberApi.getCardBalance();
          if (balanceData) {
            fullUserInfo.balance = balanceData.balance;
          }
        } catch (e) {
          console.error('获取余额失败:', e);
        }

        // 获取会员等级信息
        try {
          const memberData = await memberApi.getMemberInfo();
          if (memberData) {
            fullUserInfo.levelName = memberData.currentLevel;
            fullUserInfo.discount = memberData.discount;
          }
        } catch (e) {
          console.error('获取会员信息失败:', e);
        }
        
        // 更新资产数据
        const assets = this.updateAssetsData(fullUserInfo);
        
        // 计算等级进度
        const growthValue = fullUserInfo.growthValue || 0;
        const nextLevelPoints = fullUserInfo.nextLevelPoints || 1000;
        const levelProgress = Math.min((growthValue / nextLevelPoints) * 100, 100);

        this.setData({
          userInfo: fullUserInfo,
          assets,
          levelProgress
        });
        
        // 更新全局用户信息
        app.globalData.userInfo = fullUserInfo;
        storage.saveUserInfo(fullUserInfo);
      }
    } catch (error) {
      console.error('加载用户数据失败:', error);
    }
  },

  // 更新资产数据
  updateAssetsData(userInfo) {
    return [
      { id: 1, type: 'balance', name: '余额', icon: 'icon-wallet', value: (userInfo.balance || 0).toFixed(2), bgColor: '#36CFC9' },
      { id: 2, type: 'coupon', name: '优惠券', icon: 'icon-coupon', value: userInfo.couponCount || 0, bgColor: '#FF6B6B' },
      { id: 3, type: 'points', name: '积分', icon: 'icon-points', value: userInfo.points || 0, bgColor: '#FFA940' },
      { id: 4, type: 'gift', name: '礼品卡', icon: 'icon-gift', value: '0', bgColor: '#73D13D' }
    ];
  },

  // 跳转到用户资料页
  goToUserProfile() {
    const { userInfo } = this.data;
    if (!userInfo) {
      wx.navigateTo({
        url: '/pages/login/login'
      });
      return;
    }
    
    wx.navigateTo({
      url: '/pages/profile/profile'
    });
  },

  // 跳转到钱包页
  goToWallet() {
    this.checkLoginBeforeAction(() => {
      wx.navigateTo({
        url: '/pages/wallet/wallet'
      });
    });
  },

  // 跳转到优惠券页
  goToCoupons() {
    this.checkLoginBeforeAction(() => {
      wx.navigateTo({
        url: '/pages/coupon/coupon'
      });
    });
  },

  // 跳转到积分商城
  goToPointsMall() {
    this.checkLoginBeforeAction(() => {
      wx.navigateTo({
        url: '/pages/points/points'
      });
    });
  },
  
  // 跳转到积分页
  goToPoints() {
    this.checkLoginBeforeAction(() => {
      wx.navigateTo({
        url: '/pages/points/points'
      });
    });
  },

  // 跳转到资产页
  goToAsset(e) {
    const type = e.currentTarget.dataset.type;
    this.checkLoginBeforeAction(() => {
      switch (type) {
        case 'balance':
          this.goToWallet();
          break;
        case 'coupon':
          this.goToCoupons();
          break;
        case 'points':
          this.goToPoints();
          break;
        case 'gift':
          wx.navigateTo({
            url: '/pages/gift/gift'
          });
          break;
      }
    });
  },

  // 跳转到功能页
  goToFunction(e) {
    const url = e.currentTarget.dataset.url;
    if (!url) return;
    
    this.checkLoginBeforeAction(() => {
      wx.navigateTo({
        url
      });
    });
  },


  // 跳转到设置页
  goToSettings() {
    this.checkLoginBeforeAction(() => {
      wx.navigateTo({
        url: '/pages/settings/settings'
      });
    });
  },

  // 拨打电话
  makePhoneCall() {
    wx.makePhoneCall({
      phoneNumber: '400-123-4567'
    });
  },

  onCardNumberInput(e) {
    this.setData({ cardNumber: e.detail.value });
  },

  async bindMemberCard() {
    const { cardNumber, userInfo } = this.data;
    if (!userInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }
    if (!cardNumber) {
      wx.showToast({ title: '请输入会员卡号', icon: 'none' });
      return;
    }

    try {
      await memberApi.bindCard(cardNumber);
      wx.showToast({ title: '绑定成功', icon: 'success' });
      this.setData({ cardNumber: '' });
      this.loadUserData(); // 重新加载数据
    } catch (error) {
      wx.showToast({ title: error.message || '绑定失败', icon: 'none' });
    }
  },

  // 检查登录状态后再执行操作
  checkLoginBeforeAction(callback) {
    const { userInfo } = this.data;
    if (!userInfo) {
      wx.showModal({
        title: '温馨提示',
        content: '请先登录',
        confirmText: '去登录',
        cancelText: '取消',
        success: (res) => {
          if (res.confirm) {
            wx.navigateTo({
              url: '/pages/login/login'
            });
          }
        }
      });
      return;
    }
    
    callback();
  }
});