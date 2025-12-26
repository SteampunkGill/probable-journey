// pages/order-list/order-list.js
const app = getApp();
const util = require('../../utils/util.js');

Page({
  data: {
    // 标签页
    tabs: [
      { key: 'all', name: '全部' },
      { key: 'pending', name: '待支付' },
      { key: 'processing', name: '制作中' },
      { key: 'completed', name: '已完成' }
    ],
    activeTab: 'all',
    
    // 订单列表
    orders: [],
    
    // 分页
    page: 1,
    pageSize: 10,
    hasMore: true,
    
    // 加载状态
    loading: false,
    refreshing: false
  },

  onLoad(options) {
    if (options.status) {
      this.setData({ activeTab: options.status });
    }
    this.loadOrders();
  },

  onShow() {
    // 每次显示时刷新列表
    this.refreshOrders();
  },

  onPullDownRefresh() {
    this.refreshOrders();
  },

  onReachBottom() {
    this.loadMore();
  },

  // 切换标签
  switchTab(e) {
    const tab = e.currentTarget.dataset.tab;
    this.setData({
      activeTab: tab,
      orders: [],
      page: 1,
      hasMore: true
    });
    this.loadOrders();
  },

  // 加载订单
  async loadOrders() {
    if (this.data.loading) return;
    
    this.setData({ loading: true });
    
    try {
      const { activeTab, page, pageSize } = this.data;
      const { orderApi } = require('../../utils/api.js');
      
      let status = null;
      if (activeTab === 'pending') status = 'PENDING_PAYMENT';
      else if (activeTab === 'processing') status = 'MAKING';
      else if (activeTab === 'completed') status = 'COMPLETED';
      
      const res = await orderApi.getOrderList({ status, page, pageSize });
      
      if (res.code === 200) {
        const orders = res.data.map(order => ({
          ...order,
          statusText: this.getStatusText(order.status),
          items: order.orderItems || []
        }));
        
        this.setData({
          orders: page === 1 ? orders : [...this.data.orders, ...orders],
          hasMore: orders.length >= pageSize,
          loading: false,
          refreshing: false
        });
      } else {
        throw new Error(res.message);
      }
      
      wx.stopPullDownRefresh();
      
    } catch (error) {
      console.error('加载订单失败:', error);
      this.setData({ loading: false, refreshing: false });
      wx.showToast({
        title: '加载失败',
        icon: 'none'
      });
    }
  },

  getStatusText(status) {
    const map = {
      'PENDING_PAYMENT': '待支付',
      'PAID': '已支付',
      'MAKING': '制作中',
      'READY': '待取餐',
      'DELIVERED': '配送中',
      'COMPLETED': '已完成',
      'CANCELLED': '已取消',
      'REFUNDING': '退款中'
    };
    return map[status] || status;
  },

  // 刷新订单
  refreshOrders() {
    this.setData({
      page: 1,
      orders: [],
      hasMore: true,
      refreshing: true
    });
    this.loadOrders();
  },

  // 加载更多
  loadMore() {
    if (!this.data.hasMore || this.data.loading) return;
    
    this.setData({
      page: this.data.page + 1
    });
    this.loadOrders();
  },


  // 跳转到订单详情
  goToOrderDetail(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${orderId}`
    });
  },

  // 继续支付
  payOrder(e) {
    e.stopPropagation();
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/payment/payment?orderId=${orderId}`
    });
  },

  // 取消订单
  cancelOrder(e) {
    e.stopPropagation();
    const orderNo = e.currentTarget.dataset.no;
    
    wx.showModal({
      title: '取消订单',
      content: '确定要取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const { orderApi } = require('../../utils/api.js');
            const response = await orderApi.cancelOrder(orderNo, '用户取消');
            
            if (response.code === 200) {
              wx.showToast({
                title: '订单已取消',
                icon: 'success'
              });
              // 刷新列表
              this.refreshOrders();
            } else {
              wx.showToast({
                title: response.message || '取消失败',
                icon: 'none'
              });
            }
            
          } catch (error) {
            wx.showToast({
              title: '取消失败',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 催单
  async remindOrder(e) {
    e.stopPropagation();
    const orderNo = e.currentTarget.dataset.no;
    
    try {
      wx.showLoading({ title: '催单中...' });
      const { orderApi } = require('../../utils/api.js');
      const res = await orderApi.remindOrder(orderNo);
      
      wx.hideLoading();
      if (res.code === 200) {
        wx.showToast({
          title: res.data.message || '已提醒商家尽快制作',
          icon: 'success'
        });
        this.refreshOrders();
      } else {
        wx.showToast({
          title: res.message || '催单失败',
          icon: 'none'
        });
      }
    } catch (error) {
      wx.hideLoading();
      wx.showToast({
        title: '催单失败',
        icon: 'none'
      });
    }
  },

  // 确认收货
  confirmOrder(e) {
    e.stopPropagation();
    const orderNo = e.currentTarget.dataset.no;
    
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            const { orderApi } = require('../../utils/api.js');
            const response = await orderApi.confirmOrder(orderNo);
            
            if (response.code === 200) {
              wx.showToast({
                title: '已确认收货',
                icon: 'success'
              });
              // 刷新列表
              this.refreshOrders();
            } else {
              wx.showToast({
                title: response.message || '操作失败',
                icon: 'none'
              });
            }
            
          } catch (error) {
            wx.showToast({
              title: '操作失败',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 去评价
  reviewOrder(e) {
    e.stopPropagation();
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/review/review?orderId=${orderId}`
    });
  },

  // 再来一单
  reorder(e) {
    e.stopPropagation();
    const orderId = e.currentTarget.dataset.id;
    const order = this.data.orders.find(o => o.id === orderId);
    
    if (!order) return;
    
    // 将订单商品添加到购物车
    const storage = require('../../utils/storage.js');
    let cart = storage.getCart();
    
    order.items.forEach(item => {
      const existingIndex = cart.findIndex(c => c.id === item.id);
      if (existingIndex >= 0) {
        cart[existingIndex].quantity += item.quantity;
      } else {
        cart.push({
          id: item.id,
          name: item.name,
          image: item.image,
          price: item.price,
          quantity: item.quantity
        });
      }
    });
    
    storage.saveCart(cart);
    app.updateCartCount();
    
    wx.showToast({
      title: '已添加到购物车',
      icon: 'success',
      duration: 1500
    });
    
    setTimeout(() => {
      wx.switchTab({
        url: '/pages/cart/cart'
      });
    }, 1500);
  },

  // 联系客服
  contactService(e) {
    e.stopPropagation();
    wx.makePhoneCall({
      phoneNumber: '400-123-4567'
    });
  }
});
