import { get, post, put, del } from '../../utils/request'

Page({
  data: {
    mode: 'list', // list, select
    type: '', // select_store
    selectedId: '',
    selectedStoreId: '',
    activeTab: 'mine',
    addressList: [],
    historyList: [],
    storeList: []
  },

  onLoad(options) {
    this.setData({
      mode: options.mode || 'list',
      type: options.type || '',
      selectedId: options.selectedId || ''
    })

    if (this.data.type === 'select_store') {
      wx.setNavigationBarTitle({ title: '选择门店' })
      this.loadStoreList()
    } else {
      wx.setNavigationBarTitle({ title: '地址管理' })
      this.loadAddressList()
    }
  },

  onShow() {
    if (this.data.type !== 'select_store') {
      this.loadAddressList()
    }
  },

  async loadAddressList() {
    try {
      const res = await get('/address/list')
      const addressList = (res.data || res) || []
      
      // 模拟历史记录（原项目也是调接口，这里保持一致）
      const historyRes = await get('/address/history')
      const historyList = (historyRes.data || historyRes) || []

      this.setData({ addressList, historyList })
    } catch (error) {
      console.error('加载地址列表失败:', error)
    }
  },

  async loadStoreList() {
    try {
      // 微信小程序获取定位
      wx.getLocation({
        type: 'gcj02',
        success: async (res) => {
          this.fetchStores(res.latitude, res.longitude)
        },
        fail: () => {
          this.fetchStores(null, null)
        }
      })
    } catch (error) {
      console.error('加载门店列表失败:', error)
    }
  },

  async fetchStores(lat, lng) {
    try {
      const res = await get('/store/nearby', {
        latitude: lat,
        longitude: lng
      })
      const resData = res.data || res
      let list = Array.isArray(resData) ? resData : (resData.list || [])
      
      // 格式化距离
      list = list.map(item => ({
        ...item,
        distanceStr: item.distance ? item.distance.toFixed(2) : ''
      }))

      this.setData({ storeList: list })
      
      // 获取当前选中的门店
      const selectedStore = wx.getStorageSync('selectedStore')
      if (selectedStore) {
        this.setData({ selectedStoreId: selectedStore.id })
      }
    } catch (error) {
      console.error('获取门店失败:', error)
    }
  },

  switchTab(e) {
    const tab = e.currentTarget.dataset.tab
    this.setData({ activeTab: tab })
  },

  onAddressClick(e) {
    const address = e.currentTarget.dataset.item
    if (this.data.mode === 'select') {
      wx.setStorageSync('selectedAddress', address)
      wx.navigateBack()
    }
  },

  selectStore(e) {
    const store = e.currentTarget.dataset.item
    wx.setStorageSync('selectedStore', store)
    wx.navigateBack()
  },

  addAddress() {
    wx.navigateTo({
      url: '/pages/address/edit'
    })
  },

  editAddress(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({
      url: `/pages/address/edit?id=${id}`
    })
  },

  async deleteAddress(e) {
    const address = e.currentTarget.dataset.item
    wx.showModal({
      title: '提示',
      content: `确定要删除${address.name}的地址吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await del(`/address/delete/${address.id}`)
            this.loadAddressList()
          } catch (error) {
            console.error('删除地址失败:', error)
          }
        }
      }
    })
  },

  async toggleDefault(e) {
    const id = e.currentTarget.dataset.id
    const address = this.data.addressList.find(a => a.id === id)
    if (!address) return

    try {
      await put(`/address/update/${id}`, { ...address, isDefault: true })
      this.loadAddressList()
    } catch (error) {
      console.error('设置默认地址失败:', error)
    }
  }
})