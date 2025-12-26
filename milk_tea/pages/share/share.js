// pages/share/share.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { socialApi } = require('../../utils/api.js');

Page({
  data: {
    // 用户信息
    userInfo: null,
    
    // 分享奖励规则
    rewards: [],
    
    // 分享统计数据
    stats: {
      invitedCount: 0,
      totalRewards: 0,
      pendingRewards: 0
    },
    
    // 分享链接
    shareLink: '',
    
    // 分享内容
    shareContent: {
      title: '快来和我一起喝奶茶吧！',
      desc: '甜屿茶仓，新鲜现做，好喝不贵！注册即送20元优惠券。',
      imageUrl: '/images/banners/default.jpg'
    }
  },

  onLoad() {
    this.loadUserData();
    this.loadShareData();
  },

  onShow() {
    this.loadUserData();
  },

  // 加载用户数据
  loadUserData() {
    const userInfo = storage.getUserInfo();
    if (!userInfo) {
      wx.showToast({ title: '请先登录', icon: 'none' });
      setTimeout(() => { wx.navigateBack(); }, 1500);
      return;
    }
    this.setData({ userInfo });
  },

  // 加载分享相关数据
  async loadShareData() {
    try {
      const { authApi } = require('../../utils/api.js');
      // 获取邀请规则
      const rulesRes = await authApi.getInviteRules();
      if (rulesRes.code === 200) {
        this.setData({ rewards: rulesRes.data });
      }

      // 获取邀请记录和统计
      const inviteRes = await authApi.getInvitations();
      if (inviteRes.code === 200) {
        const invitations = inviteRes.data || [];
        this.setData({
          stats: {
            invitedCount: invitations.length,
            totalRewards: invitations.reduce((sum, item) => sum + (item.rewardAmount || 0), 0),
            pendingRewards: invitations.filter(item => item.status === 'PENDING').length
          }
        });
      }

      // 生成分享链接
      const linkRes = await authApi.generateShareLink();
      if (linkRes.code === 200) {
        this.setData({ shareLink: linkRes.data });
      }
    } catch (error) {
      console.error('加载分享数据失败:', error);
    }
  },

  // 复制分享链接
  copyShareLink() {
    wx.setClipboardData({
      data: this.data.shareLink,
      success: () => {
        wx.showToast({ title: '链接已复制', icon: 'success' });
      }
    });
  },

  // 领取奖励
  async claimReward(e) {
    const { id } = e.currentTarget.dataset;
    wx.showLoading({ title: '领取中...' });
    
    try {
      const res = await authApi.rewardInvite(id);
      if (res.code === 200) {
        wx.showToast({ title: '领取成功', icon: 'success' });
        this.loadShareData();
      } else {
        wx.showToast({ title: res.message || '领取失败', icon: 'none' });
      }
    } catch (error) {
      console.error('领取奖励失败:', error);
      wx.showToast({ title: '网络错误', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  },

  // 页面分享
  onShareAppMessage() {
    return {
      title: this.data.shareContent.title,
      path: `/pages/index/index?inviter=${this.data.userInfo?.id || ''}`,
      imageUrl: this.data.shareContent.imageUrl
    };
  }
});