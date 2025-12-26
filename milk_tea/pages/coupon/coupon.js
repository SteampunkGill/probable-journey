// pages/coupon/coupon.js
const app = getApp();

Page({
  data: {
    tabs: [
      { key: 'available', name: '可使用' },
      { key: 'used', name: '已使用' },
      { key: 'expired', name: '已过期' }
    ],
    activeTab: 'available',
    
    coupons: [],
    loading: false
  },

  onLoad() {
    this.loadCoupons();
  },

  onShow() {
    this.loadCoupons();
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({ activeTab: tab });
    this.loadCoupons();
  },

  async loadCoupons() {
    this.setData({ loading: true });
    
    try {
      const { couponApi } = require('../../utils/api.js');
      const res = await couponApi.getMyCoupons();
      
      if (res.code === 200) {
        const allCoupons = res.data || [];
        const statusMap = {
          'available': 'UNUSED',
          'used': 'USED',
          'expired': 'EXPIRED'
        };
        
        const filtered = allCoupons.filter(c => c.status === statusMap[this.data.activeTab]);
        
        this.setData({
          coupons: filtered,
          loading: false
        });
      } else {
        throw new Error(res.message);
      }
    } catch (error) {
      console.error('加载优惠券失败:', error);
      this.setData({ loading: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  useCoupon(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order/order?couponId=${id}`
    });
  }
});
