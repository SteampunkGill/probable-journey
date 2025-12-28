Page({
  data: {
    menuItems: [
      { name: '仪表盘', path: '/pages/admin/dashboard/index', emoji: '📊' },
      { name: '门店管理', path: '/pages/admin/store/index', emoji: '🏪' },
      { name: '商品管理', path: '/pages/admin/product/index', emoji: '🥤' },
      { name: '库存管理', path: '/pages/admin/inventory/index', emoji: '📦' },
      { name: '订单管理', path: '/pages/admin/order/index', emoji: '📋' },
      { name: '会员管理', path: '/pages/admin/member/index', emoji: '👥' },
      { name: '营销管理', path: '/pages/admin/marketing/index', emoji: '📢' },
      { name: '系统管理', path: '/pages/admin/system/index', emoji: '⚙️' }
    ]
  },

  navigateTo(e) {
    const path = e.currentTarget.dataset.path
    wx.navigateTo({ url: path })
  },

  logout() {
    wx.showModal({
      title: '提示',
      content: '确定要退出管理后台吗？',
      success: (res) => {
        if (res.confirm) {
          wx.reLaunch({ url: '/pages/index/index' })
        }
      }
    })
  }
})