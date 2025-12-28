import { get, post, put, del } from '../../../utils/request'

Page({
  data: {
    stores: [],
    filteredStores: [],
    searchQuery: '',
    statusIndex: 0,
    statusOptions: [
      { label: '全部状态', value: '' },
      { label: '营业中', value: 'OPEN' },
      { label: '已关闭', value: 'CLOSED' },
      { label: '维护中', value: 'MAINTENANCE' }
    ],
    dialogVisible: false,
    isEdit: false,
    form: {
      id: null,
      name: '',
      code: '',
      province: '',
      city: '',
      district: '',
      address: '',
      phone: '',
      status: 'OPEN',
      latitude: 23.129162,
      longitude: 113.264434
    },
    formStatusIndex: 0,
    formStatusOptions: [
      { label: '营业中', value: 'OPEN' },
      { label: '已关闭', value: 'CLOSED' },
      { label: '维护中', value: 'MAINTENANCE' }
    ]
  },

  onLoad() {
    this.fetchStores()
  },

  async fetchStores() {
    try {
      const res = await get('/api/admin/stores')
      const stores = (res.data || res || []).map(store => this.formatStore(store))
      this.setData({ stores }, () => {
        this.filterStores()
      })
    } catch (error) {
      console.error('获取门店列表失败:', error)
    }
  },

  formatStore(store) {
    const statusMap = {
      'OPEN': { text: '营业中', class: 'open' },
      'CLOSED': { text: '已关闭', class: 'closed' },
      'MAINTENANCE': { text: '维护中', class: 'maintenance' }
    }
    const statusInfo = statusMap[store.status] || { text: store.status, class: '' }
    return {
      ...store,
      statusText: statusInfo.text,
      statusClass: statusInfo.class
    }
  },

  onSearchInput(e) {
    this.setData({ searchQuery: e.detail.value }, () => {
      this.filterStores()
    })
  },

  onStatusFilterChange(e) {
    this.setData({ statusIndex: e.detail.value }, () => {
      this.filterStores()
    })
  },

  filterStores() {
    const { stores, searchQuery, statusIndex, statusOptions } = this.data
    const statusFilter = statusOptions[statusIndex].value
    
    const filteredStores = stores.filter(store => {
      const name = store.name || ''
      const address = store.address || ''
      const matchesSearch = name.includes(searchQuery) || address.includes(searchQuery)
      const matchesStatus = !statusFilter || store.status === statusFilter
      return matchesSearch && matchesStatus
    })
    
    this.setData({ filteredStores })
  },

  showCreateDialog() {
    this.setData({
      isEdit: false,
      dialogVisible: true,
      form: {
        id: null,
        name: '',
        code: '',
        province: '',
        city: '',
        district: '',
        address: '',
        phone: '',
        status: 'OPEN',
        longitude: 113.264434,
        latitude: 23.129162
      },
      formStatusIndex: 0
    })
  },

  editStore(e) {
    const store = e.currentTarget.dataset.item
    const statusIndex = this.data.formStatusOptions.findIndex(opt => opt.value === store.status)
    this.setData({
      isEdit: true,
      dialogVisible: true,
      form: { ...store },
      formStatusIndex: statusIndex > -1 ? statusIndex : 0
    })
  },

  hideDialog() {
    this.setData({ dialogVisible: false })
  },

  onFormInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    this.setData({
      [`form.${field}`]: value
    })
  },

  onRegionChange(e) {
    const region = e.detail.value
    this.setData({
      'form.province': region[0],
      'form.city': region[1],
      'form.district': region[2]
    })
  },

  onFormStatusChange(e) {
    const index = e.detail.value
    this.setData({
      formStatusIndex: index,
      'form.status': this.data.formStatusOptions[index].value
    })
  },

  async saveStore() {
    const { form, isEdit } = this.data
    if (!form.name || !form.code) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' })
      return
    }

    try {
      let res
      if (isEdit) {
        res = await put(`/api/admin/stores/${form.id}`, form)
      } else {
        res = await post('/api/admin/stores', form)
      }
      
      wx.showToast({ title: '保存成功', icon: 'success' })
      this.setData({ dialogVisible: false })
      this.fetchStores()
    } catch (error) {
      console.error('保存门店失败:', error)
    }
  },

  async confirmDelete(e) {
    const store = e.currentTarget.dataset.item
    wx.showModal({
      title: '提示',
      content: `确定要删除门店 "${store.name}" 吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await del(`/api/admin/stores/${store.id}`)
            wx.showToast({ title: '删除成功', icon: 'success' })
            this.fetchStores()
          } catch (error) {
            console.error('删除门店失败:', error)
          }
        }
      }
    })
  }
})