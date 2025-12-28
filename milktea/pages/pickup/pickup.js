const { orderApi } = require('../../utils/api.js');

Page({
  data: {
    pendingOrders: [],
    historyOrders: [],
    activeOrder: null,
    loading: true
  },

  onShow() {
    this.loadOrders();
  },

  async loadOrders() {
    this.setData({ loading: true });
    try {
      const res = await orderApi.getOrderList();
      const orders = res.data.list || res.data || [];
      
      // 待取餐状态：PAID(已支付), ACCEPTED(已接单), MAKING(制作中), READY(待取餐)
      const pendingStatuses = ['PAID', 'ACCEPTED', 'MAKING', 'READY'];
      const pending = orders.filter(o => pendingStatuses.includes(o.status)).map(o => ({
        ...o,
        statusText: this.getStatusText(o.status)
      }));

      // 已取餐状态：DELIVERED, FINISHED
      const historyStatuses = ['DELIVERED', 'FINISHED'];
      const history = orders.filter(o => historyStatuses.includes(o.status)).map(o => ({
        ...o,
        statusText: this.getStatusText(o.status)
      }));
      
      this.setData({
        pendingOrders: pending,
        historyOrders: history,
        activeOrder: pending.length > 0 ? pending[0] : null
      });
    } catch (error) {
      console.error('加载订单失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  getStatusText(status) {
    const statusMap = {
      'PAID': '已支付',
      'ACCEPTED': '已接单',
      'MAKING': '制作中',
      'READY': '待取餐',
      'DELIVERED': '已取餐',
      'FINISHED': '已完成',
      'CANCELLED': '已取消'
    };
    return statusMap[status] || status;
  },

  selectOrder(e) {
    const order = e.currentTarget.dataset.order;
    this.setData({ activeOrder: order });
  },

  scanToPickup() {
    wx.scanCode({
      success: (res) => {
        wx.showLoading({ title: '验证中...' });
        // 模拟验证逻辑
        setTimeout(() => {
          wx.hideLoading();
          wx.showToast({ title: '取餐成功', icon: 'success' });
          this.loadOrders();
        }, 1500);
      }
    });
  },

  goToOrderDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({ url: `/pages/order/detail?id=${id}` });
  },

  callStore(e) {
    const phone = e.currentTarget.dataset.phone;
    if (phone) {
      wx.makePhoneCall({ phoneNumber: phone });
    }
  },

  async remindOrder(e) {
    const id = e.currentTarget.dataset.id;
    try {
      const res = await orderApi.remindOrder(id);
      wx.showToast({ title: res.data?.message || '已提醒商家', icon: 'success' });
    } catch (error) {
      console.error('催单失败:', error);
      wx.showToast({ title: '催单失败', icon: 'none' });
    }
  },

  goToOrder() {
    wx.switchTab({ url: '/pages/index/index' });
  }
});