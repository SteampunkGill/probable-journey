const request = require('../../utils/request');

Page({
  data: {
    userInfo: null,
    assets: [
      { id: 1, type: 'balance', name: '余额', icon: 'icon-wallet', value: '0.00', bgColor: '#36CFC9' },
      { id: 2, type: 'coupon', name: '优惠券', icon: 'icon-coupon', value: '0', bgColor: '#FF6B6B' },
      { id: 3, type: 'points', name: '积分', icon: 'icon-points', value: '0', bgColor: '#FFA940' },
      { id: 4, type: 'gift', name: '礼品卡', icon: 'icon-gift', value: '0', bgColor: '#73D13D' }
    ],
    functions: [
      { id: 1, name: '地址管理', icon: 'icon-address', color: '#36CFC9', url: '/pages/address/address' },
      { id: 2, name: '我的收藏', icon: 'icon-heart', color: '#FF6B6B', url: '/pages/favorite/favorite' },
      { id: 3, name: '购买记录', icon: 'icon-history', color: '#FFA940', url: '/pages/order-list/order-list' },
      { id: 10, name: '分享有礼', icon: 'icon-share', color: '#FF9800', url: '/pages/share/share' },
      { id: 9, name: '绑定会员卡', icon: 'icon-wallet', color: '#D4A574', url: '/pages/user/bind-card' },
      { id: 6, name: '关于我们', icon: 'icon-info', color: '#597EF7', url: '/pages/settings/about' },
      { id: 7, name: '设置', icon: 'icon-settings', color: '#9254DE', url: '/pages/settings/settings' },
      { id: 8, name: '管理后台', icon: 'icon-lock', color: '#FF4D4F', url: '/pages/admin/index', requiresAdmin: true }
    ]
  },

  onShow() {
    this.loadUserData();
  },

  async loadUserData() {
    const token = wx.getStorageSync('token');
    if (!token) {
      this.setData({ userInfo: null });
      return;
    }

    try {
      const res = await request({
        url: '/api/auth/profile',
        method: 'GET'
      });

      if (res.code === 200 && res.data) {
        const userData = res.data;
        this.setData({
          userInfo: userData,
          'assets[0].value': (userData.balance || 0).toFixed(2),
          'assets[1].value': (userData.couponCount || 0).toString(),
          'assets[2].value': (userData.points || 0).toString()
        });
        wx.setStorageSync('userInfo', userData);
      }
    } catch (error) {
      console.error('加载用户信息失败:', error);
    }
  },

  goToUserProfile() {
    if (!this.data.userInfo) {
      wx.navigateTo({ url: '/pages/login/login' });
    } else {
      wx.navigateTo({ url: '/pages/user/profile' });
    }
  },

  goToAsset(e) {
    const type = e.currentTarget.dataset.type;
    const routes = {
      balance: '/pages/wallet/wallet',
      coupon: '/pages/coupon/coupon',
      points: '/pages/points/points',
      gift: '/pages/wallet/gift-card'
    };
    if (routes[type]) {
      wx.navigateTo({ url: routes[type] });
    }
  },

  goToFunction(e) {
    const url = e.currentTarget.dataset.url;
    if (url) {
      wx.navigateTo({ url });
    }
  },

  goToOrder() {
    wx.switchTab({ url: '/pages/order/order' });
  },

  goToSettings() {
    wx.navigateTo({ url: '/pages/settings/settings' });
  },

  makePhoneCall() {
    wx.makePhoneCall({
      phoneNumber: '400-123-4567'
    });
  },

  async checkBirthdayPrivilege() {
    if (!this.data.userInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      return;
    }

    try {
      const res = await request({
        url: '/api/member/birthday-privilege',
        method: 'GET'
      });

      if (res.code === 200) {
        const data = res.data;
        if (data.isBirthdayMonth) {
          if (data.received) {
            wx.showModal({
              title: '生日快乐',
              content: '您已领取本月生日礼包，祝您生日快乐！',
              showCancel: false
            });
          } else {
            wx.showModal({
              title: '生日特权',
              content: `检测到本月是您的生日月！\n专属礼包：${data.privilegeName}\n立即领取？`,
              success: async (modalRes) => {
                if (modalRes.confirm) {
                  const receiveRes = await request({
                    url: '/api/member/receive-birthday-privilege',
                    method: 'POST'
                  });
                  if (receiveRes.code === 200) {
                    wx.showToast({ title: '领取成功' });
                    this.loadUserData();
                  }
                }
              }
            });
          }
        } else {
          wx.showToast({
            title: `生日特权仅在生日月开放`,
            icon: 'none'
          });
        }
      }
    } catch (error) {
      console.error('获取生日特权失败:', error);
    }
  }
});