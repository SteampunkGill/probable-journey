import { get, post, del } from '../../utils/request'

Page({
  data: {
    tabs: [
      { key: 'all', name: '全部' },
      { key: 'pending_payment', name: '待付款' },
      { key: 'processing', name: '制作中' },
      { key: 'ready', name: '待取餐' },
      { key: 'completed', name: '已完成' }
    ],
    activeTab: 'all',
    orders: [],
    filteredOrders: [],
    loading: false,
    searchKeyword: '',
    sortOptions: [
      { key: 'time_desc', name: '时间降序' },
      { key: 'time_asc', name: '时间升序' },
      { key: 'amount_desc', name: '金额降序' },
      { key: 'amount_asc', name: '金额升序' }
    ],
    sortIndex: 0
  },

  onLoad(options) {
    if (options.status) {
      this.setData({ activeTab: options.status })
    }
  },

  onShow() {
    this.loadOrders()
  },

  switchTab(e) {
    const key = e.currentTarget.dataset.key
    this.setData({ activeTab: key }, () => {
      this.filterAndSortOrders()
    })
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value }, () => {
      this.filterAndSortOrders()
    })
  },

  onSortChange(e) {
    this.setData({ sortIndex: e.detail.value }, () => {
      this.filterAndSortOrders()
    })
  },

  async loadOrders() {
    this.setData({ loading: true })
    try {
      const res = await get('/api/orders')
      const orderData = res.data || []
      
      const orders = orderData.map(order => {
        const status = order.status?.toUpperCase()
        return {
          ...order,
          status: status,
          statusText: this.getStatusText(status),
          progressWidth: this.getProgressWidth(status),
          step: this.getStatusStep(status),
          items: (order.orderItems || []).map(item => ({
            ...item,
            name: item.productName,
            image: item.productImage
          }))
        }
      })
      
      this.setData({ orders }, () => {
        this.filterAndSortOrders()
      })
    } catch (error) {
      console.error('获取订单列表失败', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  getStatusText(status) {
    const map = {
      'PENDING_PAYMENT': '待支付',
      'PAID': '待接单',
      'MAKING': '制作中',
      'READY': '待取餐',
      'DELIVERING': '配送中',
      'DELIVERED': '已送达',
      'COMPLETED': '已完成',
      'FINISHED': '已完成',
      'CANCELLED': '已取消',
      'REVIEWED': '已评价'
    }
    return map[status] || status
  },

  getProgressWidth(status) {
    const map = {
      'PENDING_PAYMENT': '20%',
      'PAID': '40%',
      'MAKING': '60%',
      'READY': '80%',
      'COMPLETED': '100%',
      'FINISHED': '100%',
      'REVIEWED': '100%'
    }
    return map[status] || '0%'
  },

  getStatusStep(status) {
    const map = {
      'PENDING_PAYMENT': 1,
      'PAID': 2,
      'MAKING': 3,
      'READY': 4,
      'COMPLETED': 5,
      'FINISHED': 5,
      'REVIEWED': 5
    }
    return map[status] || 0
  },

  filterAndSortOrders() {
    let result = [...this.data.orders]
    const { activeTab, searchKeyword, sortOptions, sortIndex } = this.data

    // 1. 标签筛选
    if (activeTab !== 'all') {
      const statusFilterMap = {
        'pending_payment': ['PENDING_PAYMENT'],
        'processing': ['PAID', 'MAKING'],
        'ready': ['READY'],
        'completed': ['COMPLETED', 'FINISHED', 'REVIEWED']
      }
      const targetStatuses = statusFilterMap[activeTab] || []
      result = result.filter(order => targetStatuses.includes(order.status))
    }

    // 2. 搜索筛选
    if (searchKeyword) {
      result = result.filter(order => order.orderNo.includes(searchKeyword))
    }

    // 3. 排序
    const sortKey = sortOptions[sortIndex].key
    result.sort((a, b) => {
      switch (sortKey) {
        case 'time_desc': return new Date(b.createTime) - new Date(a.createTime)
        case 'time_asc': return new Date(a.createTime) - new Date(b.createTime)
        case 'amount_desc': return b.totalAmount - a.totalAmount
        case 'amount_asc': return a.totalAmount - b.totalAmount
        default: return 0
      }
    })

    this.setData({ filteredOrders: result })
  },

  goToOrderDetail(e) {
    const no = e.currentTarget.dataset.no
    wx.navigateTo({ url: `/pages/order/detail?orderNo=${no}` })
  },

  async cancelOrder(e) {
    const no = e.currentTarget.dataset.no
    wx.showModal({
      title: '提示',
      content: '确定要取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await post(`/api/orders/${no}/cancel`)
            this.loadOrders()
          } catch (error) {
            console.error('取消失败', error)
          }
        }
      }
    })
  },

  payOrder(e) {
    const item = e.currentTarget.dataset.item
    wx.navigateTo({
      url: `/pages/payment/payment?orderNo=${item.orderNo}&amount=${item.totalAmount}`
    })
  },

  async remindOrder(e) {
    wx.showToast({ title: '已提醒商家尽快制作' })
  },

  async confirmOrder(e) {
    const no = e.currentTarget.dataset.no
    wx.showModal({
      title: '提示',
      content: '确认已取餐吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await post(`/api/orders/${no}/confirm`)
            this.loadOrders()
          } catch (error) {
            console.error('确认失败', error)
          }
        }
      }
    })
  },

  reorder(e) {
    const item = e.currentTarget.dataset.item
    // 简化逻辑：跳转到点单页
    wx.switchTab({ url: '/pages/order/order' })
  },

  reviewOrder(e) {
    const no = e.currentTarget.dataset.no
    wx.navigateTo({ url: `/pages/review/review?orderNo=${no}` })
  },

  contactService() {
    wx.makePhoneCall({ phoneNumber: '400-123-4567' })
  },

  goToIndex() {
    wx.switchTab({ url: '/pages/index/index' })
  }
})