// pages/member/level.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { memberApi } = require('../../utils/api.js');

Page({
  data: {
    // 当前用户信息
    userInfo: null,
    
    // 会员等级数据
    levels: [],
    
    // 用户当前等级进度
    currentLevel: null,
    nextLevel: null,
    progress: 0,
    
    // 会员特权列表
    privileges: []
  },

  onLoad() {
    this.loadUserData();
  },

  onShow() {
    this.loadUserData();
  },

  // 加载用户数据
  async loadUserData() {
    const userInfo = storage.getUserInfo();
    const token = storage.getStorage('token');
    
    if (!token || !userInfo) {
      wx.showToast({
        title: '请先登录',
        icon: 'none'
      });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
      return;
    }
    
    try {
      const res = await memberApi.getMemberInfo();
      if (res.code === 200) {
        const memberInfo = res.data;
        const fullUserInfo = {
          ...userInfo,
          points: memberInfo.currentPoints,
          levelName: memberInfo.currentLevel,
          growthValue: memberInfo.currentPoints,
          nextLevelPoints: memberInfo.nextLevelPoints
        };
        
        this.setData({
          userInfo: fullUserInfo
        });
        
        // 计算当前等级和进度
        this.calculateLevelProgress(fullUserInfo.growthValue);
      }
    } catch (error) {
      console.error('加载会员信息失败:', error);
    }
  },

  // 计算等级进度
  calculateLevelProgress(points) {
    const { levels } = this.data;
    let currentLevel = null;
    let nextLevel = null;
    let progress = 0;
    
    // 查找当前等级
    for (let i = 0; i < levels.length; i++) {
      if (points >= levels[i].minPoints && points <= levels[i].maxPoints) {
        currentLevel = levels[i];
        nextLevel = i < levels.length - 1 ? levels[i + 1] : null;
        
        // 计算进度
        if (nextLevel) {
          const range = nextLevel.minPoints - currentLevel.minPoints;
          const currentProgress = points - currentLevel.minPoints;
          progress = Math.min(Math.round((currentProgress / range) * 100), 100);
        } else {
          progress = 100;
        }
        break;
      }
    }
    
    // 如果点数超过最高等级
    if (!currentLevel && points > levels[levels.length - 1].minPoints) {
      currentLevel = levels[levels.length - 1];
      nextLevel = null;
      progress = 100;
    }
    
    this.setData({
      currentLevel,
      nextLevel,
      progress
    });
  },

  // 查看等级详情
  viewLevelDetail(e) {
    const index = e.currentTarget.dataset.index;
    const level = this.data.levels[index];
    
    wx.showModal({
      title: level.name,
      content: `积分范围：${level.minPoints} - ${level.maxPoints || '∞'}\n会员折扣：${(level.discount * 10).toFixed(1)}折\n\n特权：\n${level.benefits.join('\n')}`,
      showCancel: false,
      confirmText: '知道了'
    });
  },

  // 查看特权详情
  viewPrivilegeDetail(e) {
    const index = e.currentTarget.dataset.index;
    const privilege = this.data.privileges[index];
    
    wx.showModal({
      title: privilege.name,
      content: privilege.desc,
      showCancel: false,
      confirmText: '知道了'
    });
  },

  // 如何升级
  howToUpgrade() {
    wx.showModal({
      title: '如何升级会员等级',
      content: '1. 消费累积积分\n2. 完成会员任务\n3. 参与会员活动\n4. 邀请好友注册\n\n每消费1元获得1积分，不同等级有积分加速。',
      confirmText: '去消费',
      cancelText: '知道了',
      success: (res) => {
        if (res.confirm) {
          wx.switchTab({
            url: '/pages/index/index'
          });
        }
      }
    });
  },

  // 分享给好友
  shareToFriend() {
    wx.showModal({
      title: '邀请好友',
      content: '邀请好友注册，双方各得20元优惠券，同时您还能获得额外积分加速升级！',
      confirmText: '立即邀请',
      cancelText: '稍后再说',
      success: (res) => {
        if (res.confirm) {
          wx.navigateTo({
            url: '/pages/share/share'
          });
        }
      }
    });
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
});