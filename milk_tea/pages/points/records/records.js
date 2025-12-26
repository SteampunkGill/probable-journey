// pages/points/records/records.js
const { pointsMallApi } = require('../../../utils/api.js');

Page({
  data: {
    loading: true,
    records: [],
    tabs: [
      { id: 'all', name: '全部', active: true },
      { id: 'completed', name: '已完成', active: false },
      { id: 'pending', name: '待发货', active: false },
      { id: 'cancelled', name: '已取消', active: false }
    ],
    activeTab: 'all',
    empty: false
  },

  onLoad() {
    this.loadExchangeRecords();
  },

  onShow() {
    this.loadExchangeRecords();
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadExchangeRecords();
    setTimeout(() => {
      wx.stopPullDownRefresh();
    }, 1000);
  },

  // 加载兑换记录
  loadExchangeRecords() {
    this.setData({ loading: true });
    
    pointsMallApi.getExchangeRecords().then(res => {
      if (res.code === 200 && res.data) {
        const records = res.data.map(item => ({
          id: item.id,
          orderNo: item.orderNo || `POINT${item.id.toString().padStart(8, '0')}`,
          productName: item.productName || '积分兑换商品',
          pointCost: item.pointCost || 0,
          exchangeTime: item.exchangeTime || new Date().toISOString(),
          status: item.status || 'COMPLETED',
          productImage: item.productImage || '/images/products/default.jpg'
        }));
        
        this.setData({
          records: records,
          loading: false,
          empty: records.length === 0
        });
        
        this.filterRecords();
      } else {
        this.setData({ records: [], loading: false, empty: true });
      }
    }).catch(() => {
      this.setData({ records: [], loading: false, empty: true });
    });
  },

  // 切换标签
  switchTab(e) {
    const tabId = e.currentTarget.dataset.id;
    const tabs = this.data.tabs.map(tab => ({
      ...tab,
      active: tab.id === tabId
    }));
    
    this.setData({
      tabs,
      activeTab: tabId
    });
    
    this.filterRecords();
  },

  // 筛选记录
  filterRecords() {
    const { records, activeTab } = this.data;
    
    let filtered = [...records];
    
    if (activeTab !== 'all') {
      filtered = records.filter(record => {
        if (activeTab === 'completed') {
          return record.status === 'COMPLETED';
        } else if (activeTab === 'pending') {
          return record.status === 'PENDING';
        } else if (activeTab === 'cancelled') {
          return record.status === 'CANCELLED';
        }
        return true;
      });
    }
    
    this.setData({
      filteredRecords: filtered,
      empty: filtered.length === 0
    });
  },

  // 查看记录详情
  viewRecordDetail(e) {
    const id = e.currentTarget.dataset.id;
    const record = this.data.records.find(r => r.id === id);
    
    if (record) {
      wx.showModal({
        title: '兑换记录详情',
        content: `订单号：${record.orderNo}\n商品：${record.productName}\n积分：${record.pointCost}\n时间：${record.exchangeTime}\n状态：${this.getStatusText(record.status)}`,
        showCancel: false
      });
    }
  },

  // 获取状态文本
  getStatusText(status) {
    const statusMap = {
      'COMPLETED': '已完成',
      'PENDING': '待发货',
      'CANCELLED': '已取消',
      'PROCESSING': '处理中'
    };
    return statusMap[status] || status;
  },

  // 取消兑换
  cancelExchange(e) {
    const id = e.currentTarget.dataset.id;
    const record = this.data.records.find(r => r.id === id);
    
    if (!record || record.status !== 'PENDING') {
      return;
    }
    
    wx.showModal({
      title: '确认取消',
      content: `确定要取消兑换【${record.productName}】吗？`,
      success: (res) => {
        if (res.confirm) {
          this.doCancelExchange(id);
        }
      }
    });
  },

  // 执行取消
  doCancelExchange(id) {
    wx.showLoading({ title: '处理中...' });
    
    // 模拟API调用
    setTimeout(() => {
      wx.hideLoading();
      
      // 更新记录状态
      const records = this.data.records.map(record => {
        if (record.id === id) {
          return { ...record, status: 'CANCELLED' };
        }
        return record;
      });
      
      this.setData({ records });
      this.filterRecords();
      
      wx.showToast({
        title: '取消成功',
        icon: 'success'
      });
    }, 1000);
  },

  // 确认收货
  confirmReceipt(e) {
    const id = e.currentTarget.dataset.id;
    const record = this.data.records.find(r => r.id === id);
    
    if (!record || record.status !== 'PENDING') {
      return;
    }
    
    wx.showModal({
      title: '确认收货',
      content: `请确认已收到【${record.productName}】`,
      success: (res) => {
        if (res.confirm) {
          this.doConfirmReceipt(id);
        }
      }
    });
  },

  // 执行确认收货
  doConfirmReceipt(id) {
    wx.showLoading({ title: '处理中...' });
    
    // 模拟API调用
    setTimeout(() => {
      wx.hideLoading();
      
      // 更新记录状态
      const records = this.data.records.map(record => {
        if (record.id === id) {
          return { ...record, status: 'COMPLETED' };
        }
        return record;
      });
      
      this.setData({ records });
      this.filterRecords();
      
      wx.showToast({
        title: '确认成功',
        icon: 'success'
      });
    }, 1000);
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
});