const request = require('../../../utils/request');
const util = require('../../../utils/util');

Page({
  data: {
    orders: [],
    filteredOrders: [],
    complaints: [],
    appeals: [],
    selectedNos: [],
    activeTab: 'all',
    query: { orderNo: '', status: '', storeId: '' },
    statusOptions: ['全部状态', '待支付', '待接单', '制作中', '待取餐', '配送中', '已送达', '已完成', '退款中', '已评价', '已取消'],
    frontendStatusIndex: 0,
    tabs: [
      { key: 'all', label: '全部' },
      { key: 'PENDING_PAYMENT', label: '待支付' },
      { key: 'PAID', label: '待接单' },
      { key: 'MAKING', label: '制作中' },
      { key: 'READY', label: '待取餐' },
      { key: 'DELIVERING', label: '配送中' },
      { key: 'COMPLETED', label: '已完成' },
      { key: 'REFUNDING', label: '退款中' },
      { key: 'appeal', label: '申诉退款' },
      { key: 'complaint', label: '投诉建议' }
    ],
    isAllSelected: false,
    refundModal: {
      show: false,
      order: null,
      reply: ''
    }
  },

  onLoad() {
    this.loadOrders();
  },

  onOrderNoInput(e) {
    this.setData({ 'query.orderNo': e.detail.value });
  },

  onFrontendStatusChange(e) {
    this.setData({ frontendStatusIndex: e.detail.value }, () => {
      this.applyFilter();
    });
  },

  switchTab(e) {
    const key = e.currentTarget.dataset.key;
    this.setData({ activeTab: key }, () => {
      if (key === 'complaint') {
        this.loadComplaints();
      } else if (key === 'appeal') {
        this.loadAppeals();
      } else {
        this.loadOrders();
      }
    });
  },

  async loadOrders() {
    const { activeTab, query } = this.data;
    const params = { ...query };
    const statusTabs = ['PENDING_PAYMENT', 'PAID', 'MAKING', 'READY', 'DELIVERING', 'COMPLETED', 'REFUNDING'];
    if (statusTabs.includes(activeTab)) {
      params.status = activeTab;
    }

    try {
      const res = await request({ url: '/api/admin/orders', data: params });
      const orders = (res.data || []).map(o => ({
        ...o,
        selected: false,
        statusName: this.getStatusName(o.status),
        formattedTime: this.formatDate(o.orderTime || o.createdAt)
      }));
      this.setData({ orders }, () => {
        this.applyFilter();
      });
    } catch (error) {
      console.error('加载订单失败:', error);
    }
  },

  applyFilter() {
    const { orders, statusOptions, frontendStatusIndex } = this.data;
    const filterStatus = statusOptions[frontendStatusIndex];
    
    let filteredOrders = orders;
    if (filterStatus !== '全部状态') {
      filteredOrders = orders.filter(o => o.statusName === filterStatus);
    }
    
    this.setData({ filteredOrders });
    this.updateSelectStatus();
  },

  async loadComplaints() {
    try {
      const res = await request({ url: '/api/admin/complaints' });
      this.setData({ complaints: res.data || [] });
    } catch (error) {
      console.error('加载投诉失败:', error);
    }
  },

  async loadAppeals() {
    try {
      const res = await request({ url: '/api/admin/appeals' });
      this.setData({ appeals: res.data || [] });
    } catch (error) {
      console.error('加载申诉失败:', error);
    }
  },

  toggleSelect(e) {
    const no = e.currentTarget.dataset.no;
    const orders = this.data.orders.map(o => {
      if (o.orderNo === no) o.selected = !o.selected;
      return o;
    });
    this.setData({ orders }, () => {
      this.applyFilter();
    });
  },

  toggleAll() {
    const isAllSelected = !this.data.isAllSelected;
    const filteredNos = this.data.filteredOrders.map(o => o.orderNo);
    const orders = this.data.orders.map(o => {
      if (filteredNos.includes(o.orderNo)) o.selected = isAllSelected;
      return o;
    });
    this.setData({ orders, isAllSelected }, () => {
      this.applyFilter();
    });
  },

  updateSelectStatus() {
    const { filteredOrders } = this.data;
    const isAllSelected = filteredOrders.length > 0 && filteredOrders.every(o => o.selected);
    const selectedNos = this.data.orders.filter(o => o.selected).map(o => o.orderNo);
    this.setData({ isAllSelected, selectedNos });
  },

  async handleOrder(e) {
    const { no, action } = e.currentTarget.dataset;
    try {
      await request({ url: `/api/admin/orders/${no}/${action}`, method: 'POST' });
      this.loadOrders();
      wx.showToast({ title: '操作成功' });
    } catch (error) {
      console.error('处理订单失败:', error);
    }
  },

  async batchAccept() {
    try {
      await request({ url: '/api/admin/orders/batch-accept', method: 'POST', data: this.data.selectedNos });
      this.loadOrders();
      wx.showToast({ title: '批量接单成功' });
    } catch (error) {
      console.error('批量接单失败:', error);
    }
  },

  async printOrder(e) {
    const no = e.currentTarget.dataset.no;
    try {
      await request({ url: `/api/admin/orders/${no}/print`, method: 'POST' });
      wx.showToast({ title: '打印指令已发送' });
    } catch (error) {
      console.error('打印失败:', error);
    }
  },

  exportOrders() {
    wx.showToast({ title: '订单导出中...', icon: 'loading' });
  },

  async testVoice() {
    try {
      const res = await request({ url: '/api/admin/notifications/voice-test', method: 'POST' });
      wx.showModal({ title: '语音测试', content: res.data, showCancel: false });
    } catch (error) {
      console.error('语音测试失败:', error);
    }
  },

  showRefundModal(e) {
    const order = e.currentTarget.dataset.item;
    this.setData({
      'refundModal.show': true,
      'refundModal.order': order,
      'refundModal.reply': ''
    });
  },

  hideRefundModal() {
    this.setData({ 'refundModal.show': false });
  },

  onRefundReplyInput(e) {
    this.setData({ 'refundModal.reply': e.detail.value });
  },

  async submitRefund(e) {
    const approved = e.currentTarget.dataset.approved;
    const { order, reply } = this.data.refundModal;
    try {
      await request({
        url: `/api/admin/refunds/${order.id}/review`,
        method: 'POST',
        data: {
          status: approved ? 'APPROVED' : 'REJECTED',
          reply: reply
        }
      });
      this.hideRefundModal();
      this.loadOrders();
      wx.showToast({ title: '审核完成' });
    } catch (error) {
      console.error('审核退款失败:', error);
    }
  },

  async handleComplaint(e) {
    const c = e.currentTarget.dataset.item;
    wx.showModal({
      title: '处理投诉',
      editable: true,
      placeholderText: '请输入处理意见',
      success: async (res) => {
        if (res.confirm && res.content) {
          try {
            await request({
              url: `/api/admin/complaints/${c.id}/handle`,
              method: 'POST',
              data: { status: 'RESOLVED', reply: res.content }
            });
            this.loadComplaints();
            wx.showToast({ title: '处理成功' });
          } catch (error) {
            console.error('处理投诉失败:', error);
          }
        }
      }
    });
  },

  async handleAppeal(e) {
    const a = e.currentTarget.dataset.item;
    wx.showModal({
      title: '确认退款',
      content: `确定要为订单 ${a.orderNo} 退款 ¥${a.amount} 吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await request({ url: `/api/admin/appeals/${a.id}/refund`, method: 'POST' });
            wx.showToast({ title: '退款成功' });
            this.loadAppeals();
          } catch (error) {
            console.error('退款失败:', error);
          }
        }
      }
    });
  },

  getStatusName(status) {
    const map = {
      'PENDING_PAYMENT': '待支付',
      'PAID': '待接单',
      'MAKING': '制作中',
      'READY': '待取餐',
      'DELIVERING': '配送中',
      'DELIVERED': '已送达',
      'COMPLETED': '已完成',
      'REFUNDING': '退款中',
      'REFUNDED': '已退款',
      'REVIEWED': '已评价',
      'APPEALING': '申诉中',
      'CANCELLED': '已取消',
      'FINISHED': '已结束'
    };
    return map[status] || status;
  },

  formatDate(dateStr) {
    if (!dateStr) return '-';
    const date = new Date(dateStr);
    return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`;
  }
});