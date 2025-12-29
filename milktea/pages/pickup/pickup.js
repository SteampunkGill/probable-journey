const { orderApi } = require('../../utils/api');

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
      if (res.code === 200) {
        const orders = res.data.list || res.data || [];
        
        // 状态映射
        const statusMap = {
          'PAID': '已支付',
          'ACCEPTED': '已接单',
          'MAKING': '制作中',
          'READY': '待取餐',
          'DELIVERED': '已送达',
          'FINISHED': '已完成',
          'CANCELLED': '已取消'
        };

        const formattedOrders = orders.map(o => ({
          ...o,
          statusText: statusMap[o.status] || o.status,
          itemCount: (o.orderItems || []).reduce((sum, item) => sum + item.quantity, 0),
          items: (o.orderItems || []).map(item => ({
            ...item,
            name: item.productName,
            image: item.productImage,
            customizations: item.customizations ? JSON.parse(item.customizations).sweetness + ' / ' + JSON.parse(item.customizations).temperature : ''
          })),
          store: o.store || { name: '默认门店', address: '门店地址', phone: '123456789' }
        }));

        // 待取餐状态
        const pendingStatuses = ['PAID', 'ACCEPTED', 'MAKING', 'READY'];
        const pendingOrders = formattedOrders.filter(o => pendingStatuses.includes(o.status));
        
        // 历史取餐记录
        const historyStatuses = ['DELIVERED', 'FINISHED'];
        const historyOrders = formattedOrders.filter(o => historyStatuses.includes(o.status));
        
        this.setData({
          pendingOrders,
          historyOrders,
          activeOrder: pendingOrders.length > 0 ? pendingOrders[0] : null
        });
      }
    } catch (error) {
      console.error('加载订单失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  selectOrder(e) {
    const index = e.currentTarget.dataset.index;
    this.setData({
      activeOrder: this.data.pendingOrders[index]
    });
  },

  scanToPickup() {
    wx.scanCode({
      success: (res) => {
        wx.showModal({
          title: '扫码结果',
          content: '正在验证取餐码: ' + res.result,
          showCancel: false
        });
      }
    });
  },

  goToOrderDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order/detail?orderNo=${id}`
    });
  },

  callStore(e) {
    const phone = e.currentTarget.dataset.phone;
    wx.makePhoneCall({
      phoneNumber: phone
    });
  },

  async remindOrder(e) {
    const id = e.currentTarget.dataset.id;
    try {
      const res = await orderApi.remindOrder(id);
      if (res.code === 200) {
        wx.showToast({ title: '已提醒商家尽快制作' });
      } else {
        wx.showToast({ title: res.message || '催单失败', icon: 'none' });
      }
    } catch (error) {
      console.error('催单失败:', error);
    }
  },

  goToOrder() {
    wx.switchTab({
      url: '/pages/index/index'
    });
  }
});