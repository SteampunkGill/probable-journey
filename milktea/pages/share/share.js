const { shareApi } = require('../../utils/api')

Page({
  data: {
    stats: {
      inviteCount: 0,
      totalReward: 0,
      inviteCode: ''
    }
  },

  onLoad() {
    this.loadStats()
  },

  async loadStats() {
    try {
      // 默认模拟数据
      let stats = {
        inviteCount: 5,
        totalReward: 25,
        inviteCode: 'TEA' + Math.floor(Math.random() * 10000)
      }
      
      const res = await shareApi.getShareStats()
      const resData = res.data || res
      if (resData && typeof resData === 'object') {
        stats = { ...stats, ...resData }
      }
      
      this.setData({ stats })
    } catch (error) {
      console.error('加载分享统计失败:', error)
    }
  },

  onShareAppMessage() {
    return {
      title: '邀请好友 共享美味',
      path: `/pages/index/index?inviteCode=${this.data.stats.inviteCode}`,
      imageUrl: '/assets/images/banners/default.jpg'
    }
  },

  handleShare(e) {
    const { type } = e.currentTarget.dataset
    if (type === 'poster') {
      wx.showToast({ title: '海报生成中...', icon: 'loading' })
      setTimeout(() => {
        wx.showToast({ title: '功能开发中', icon: 'none' })
      }, 1500)
    }
  },

  copyLink() {
    const link = `https://milktea.example.com/register?inviteCode=${this.data.stats.inviteCode || 'USER123'}`
    wx.setClipboardData({
      data: link,
      success: () => {
        wx.showToast({ title: '链接已复制', icon: 'success' })
      }
    })
  }
})