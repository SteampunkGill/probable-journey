const { get, put } = require('../../../utils/request');

Page({
  data: {
    inventory: [],
    modal: {
      show: false,
      type: '', // stock, cost, alert
      item: null,
      value: 0
    },
    modalTitle: '',
    modalLabel: ''
  },

  onLoad() {
    this.loadInventory();
  },

  async loadInventory() {
    try {
      const res = await get('/admin/inventory');
      this.setData({
        inventory: res.data || []
      });
    } catch (error) {
      console.error('加载库存失败:', error);
    }
  },

  showUpdateModal(e) {
    const { item, type } = e.currentTarget.dataset;
    let value = 0;
    let modalTitle = '';
    let modalLabel = '';

    if (type === 'stock') {
      value = item.stock;
      modalTitle = '更新库存';
      modalLabel = '当前库存';
    } else if (type === 'cost') {
      value = item.costPerUnit;
      modalTitle = '调整成本';
      modalLabel = '单位成本';
    } else if (type === 'alert') {
      value = item.alertThreshold;
      modalTitle = '预警设置';
      modalLabel = '预警阈值';
    }

    this.setData({
      modal: {
        show: true,
        type,
        item,
        value
      },
      modalTitle,
      modalLabel
    });
  },

  hideModal() {
    this.setData({
      'modal.show': false
    });
  },

  onModalValueInput(e) {
    this.setData({
      'modal.value': parseFloat(e.detail.value) || 0
    });
  },

  async handleUpdate() {
    const { item, type, value } = this.data.modal;
    const id = item.id;
    
    wx.showLoading({ title: '更新中...' });
    try {
      let res;
      if (type === 'stock') {
        res = await put(`/admin/inventory/${id}/stock`, { stock: value });
      } else if (type === 'cost') {
        res = await put(`/admin/inventory/${id}/cost`, { costPerUnit: value });
      } else if (type === 'alert') {
        res = await put(`/admin/inventory/${id}/alert`, { alertThreshold: value });
      }
      
      if (res.code === 200) {
        wx.showToast({ title: '更新成功' });
        this.hideModal();
        this.loadInventory();
      } else {
        wx.showToast({ title: res.message || '更新失败', icon: 'none' });
      }
    } catch (error) {
      console.error('更新失败:', error);
      wx.showToast({ title: '更新失败', icon: 'none' });
    } finally {
      wx.hideLoading();
    }
  }
});