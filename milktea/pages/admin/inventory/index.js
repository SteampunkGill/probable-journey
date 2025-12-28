import { get, put } from '../../../utils/request'

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
    this.loadInventory()
  },

  async loadInventory() {
    try {
      const res = await get('/api/admin/inventory')
      this.setData({ inventory: res.data || [] })
    } catch (error) {
      console.error('加载库存失败:', error)
    }
  },

  showUpdateModal(e) {
    const { item, type } = e.currentTarget.dataset
    const titles = { stock: '更新库存', cost: '调整成本', alert: '预警设置' }
    const labels = { stock: '当前库存', cost: '单位成本', alert: '预警阈值' }
    
    let value = 0
    if (type === 'stock') value = item.stock
    else if (type === 'cost') value = item.costPerUnit
    else if (type === 'alert') value = item.alertThreshold

    this.setData({
      modal: {
        show: true,
        type,
        item,
        value
      },
      modalTitle: titles[type],
      modalLabel: labels[type]
    })
  },

  hideModal() {
    this.setData({ 'modal.show': false })
  },

  onModalInput(e) {
    this.setData({
      'modal.value': parseFloat(e.detail.value) || 0
    })
  },

  async handleUpdate() {
    const { item, type, value } = this.data.modal
    const id = item.id
    
    wx.showLoading({ title: '更新中' })
    try {
      let res
      if (type === 'stock') {
        res = await put(`/api/admin/inventory/${id}/stock`, { stock: value })
      } else if (type === 'cost') {
        res = await put(`/api/admin/inventory/${id}/cost`, { costPerUnit: value })
      } else if (type === 'alert') {
        res = await put(`/api/admin/inventory/${id}/alert`, { alertThreshold: value })
      }
      
      wx.showToast({ title: '更新成功' })
      this.setData({ 'modal.show': false })
      this.loadInventory()
    } catch (error) {
      console.error('更新失败:', error)
    } finally {
      wx.hideLoading()
    }
  },

  onPullDownRefresh() {
    this.loadInventory().then(() => {
      wx.stopPullDownRefresh()
    })
  }
})