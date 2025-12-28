Page({
  data: {
    menuItems: [
      { name: '仪表盘', path: '/pages/admin/dashboard/index', icon: 'icon-dashboard' },
      { name: '门店管理', path: '/pages/admin/store/index', icon: 'icon-store' },
      { name: '商品管理', path: '/pages/admin/product/index', icon: 'icon-product' },
      { name: '库存管理', path: '/pages/admin/inventory/index', icon: 'icon-inventory' },
      { name: '订单管理', path: '/pages/admin/order/index', icon: 'icon-order' },
      { name: '会员管理', path: '/pages/admin/member/index', icon: 'icon-user' },
      { name: '营销管理', path: '/pages/admin/marketing/index', icon: 'icon-marketing' },
      { name: '系统管理', path: '/pages/admin/system/index', icon: 'icon-settings' }
    ]
  },

  navigateTo(e) {
    const path = e.currentTarget.dataset.path;
    wx.navigateTo({ url: path });
  },

  logout() {
    wx.reLaunch({ url: '/pages/login/login' });
  }
});