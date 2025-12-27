// pages/order-detail/order-detail.js
const app = getApp();
const util = require('../../utils/util.js');

Page({
  data: {
    orderId: '',
    order: null,
    loading: true,
    
    // 订单状态流程
    statusSteps: [],
    currentStep: 0,
    progressVisual: null,
    estimatedTime: null
  },

  onLoad(options) {
    const orderId = options.id;
    if (orderId) {
      this.setData({ orderId });
      this.loadOrderDetail();
    }
  },

  onShow() {
    // 每次显示时刷新订单状态
    if (this.data.orderId) {
      this.loadOrderDetail();
    }
  },

  onPullDownRefresh() {
    this.loadOrderDetail();
  },

  // 加载订单详情
  async loadOrderDetail() {
    try {
      this.setData({ loading: true });
      
      const { orderApi } = require('../../utils/api.js');
      const res = await orderApi.getOrderDetail(this.data.orderId);
      
      if (res.code === 200) {
        const order = res.data;
        // 适配后端字段
        order.statusText = this.getStatusText(order.status);
        order.items = order.orderItems || [];
        
        // 生成状态流程
        const statusSteps = this.generateStatusSteps(order);
        const currentStep = this.getCurrentStep(order.status);
        
        this.setData({
          order,
          statusSteps,
          currentStep,
          loading: false
        });
      } else {
        throw new Error(res.message);
      }
      
      wx.stopPullDownRefresh();
      
    } catch (error) {
      console.error('加载订单详情失败:', error);
      this.setData({ loading: false });
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


  // 生成状态步骤
  generateStatusSteps(order) {
    if (order.deliveryType === 'delivery') {
      return [
        { key: 'created', title: '订单已提交', time: order.createTime },
        { key: 'paid', title: '支付成功', time: order.payTime },
        { key: 'processing', title: '商家制作中', time: '' },
        { key: 'delivering', title: '配送中', time: '' },
        { key: 'completed', title: '订单完成', time: '' }
      ];
    } else {
      return [
        { key: 'created', title: '订单已提交', time: order.createTime },
        { key: 'paid', title: '支付成功', time: order.payTime },
        { key: 'processing', title: '商家制作中', time: '' },
        { key: 'ready', title: '已备餐', time: '' },
        { key: 'completed', title: '已取餐', time: '' }
      ];
    }
  },

  // 获取当前步骤
  getCurrentStep(status) {
    const stepMap = {
      'PENDING_PAYMENT': 0,
      'PAID': 1,
      'MAKING': 2,
      'READY': 3,
      'DELIVERED': 3,
      'COMPLETED': 4
    };
    return stepMap[status] || 0;
  },

  // 复制订单号
  copyOrderNo() {
    wx.setClipboardData({
      data: this.data.order.orderNo,
      success: () => {
        wx.showToast({
          title: '已复制',
          icon: 'success'
        });
      }
    });
  },

  // 复制取餐码
  copyPickupCode() {
    wx.setClipboardData({
      data: this.data.order.pickupCode,
      success: () => {
        wx.showToast({
          title: '已复制',
          icon: 'success'
        });
      }
    });
  },

  // 拨打电话
  callStore() {
    wx.makePhoneCall({
      phoneNumber: this.data.order.store.phone
    });
  },

  // 查看门店
  goToStore() {
    wx.navigateTo({
      url: `/pages/store/detail?id=${this.data.order.store.id}`
    });
  },

  // 继续支付
  payOrder() {
    wx.navigateTo({
      url: `/pages/payment/payment?orderId=${this.data.orderId}`
    });
  },

  // 取消订单
  cancelOrder() {
    wx.showActionSheet({
      itemList: ['不想要了', '信息填写错误', '重复下单', '其他原因'],
      success: async (res) => {
        const reasons = ['不想要了', '信息填写错误', '重复下单', '其他原因'];
        const reason = reasons[res.tapIndex];
        
        try {
          wx.showLoading({ title: '取消中...' });
          
          const { orderApi } = require('../../utils/api.js');
          const response = await orderApi.cancelOrder(this.data.order.orderNo, reason);
          
          wx.hideLoading();
          if (response.code === 200) {
            wx.showToast({
              title: '订单已取消',
              icon: 'success',
              duration: 1500
            });
            
            setTimeout(() => {
              wx.navigateBack();
            }, 1500);
          } else {
            wx.showToast({
              title: response.message || '取消失败',
              icon: 'none'
            });
          }
          
        } catch (error) {
          wx.hideLoading();
          wx.showToast({
            title: '取消失败',
            icon: 'none'
          });
        }
      }
    });
  },

  // 催单
  async remindOrder() {
    try {
      wx.showLoading({ title: '催单中...' });
      const { orderApi } = require('../../utils/api.js');
      const res = await orderApi.remindOrder(this.data.order.orderNo);
      
      wx.hideLoading();
      if (res.code === 200) {
        wx.showToast({
          title: res.data.message || '已提醒商家尽快制作',
          icon: 'success'
        });
        this.loadOrderDetail();
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

  // 申请退款
  applyRefund() {
    wx.showModal({
      title: '申请退款',
      content: '确定要申请退款吗？',
      editable: true,
      placeholderText: '请输入退款原因',
      success: async (res) => {
        if (res.confirm) {
          const reason = res.content || '用户申请退款';
          try {
            wx.showLoading({ title: '提交中...' });
            const { orderApi } = require('../../utils/api.js');
            const response = await orderApi.applyRefund(this.data.order.orderNo, reason);
            
            wx.hideLoading();
            if (response.code === 200) {
              wx.showToast({
                title: '退款申请已提交',
                icon: 'success'
              });
              this.loadOrderDetail();
            } else {
              wx.showToast({
                title: response.message || '提交失败',
                icon: 'none'
              });
            }
          } catch (error) {
            wx.hideLoading();
            wx.showToast({
              title: '提交失败',
              icon: 'none'
            });
          }
        }
      }
    });
  },

  // 投诉建议
  goToComplaint() {
    wx.navigateTo({
      url: `/pages/complaint/complaint?orderNo=${this.data.order.orderNo}`
    });
  },

  // 确认收货
  confirmOrder() {
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            wx.showLoading({ title: '确认中...' });
            
            const { orderApi } = require('../../utils/api.js');
            const response = await orderApi.confirmOrder(this.data.order.orderNo);
            
            wx.hideLoading();
            if (response.code === 200) {
              wx.showToast({
                title: '已确认收货',
                icon: 'success'
              });
              // 刷新订单
              this.loadOrderDetail();
            } else {
              wx.showToast({
                title: response.message || '操作失败',
                icon: 'none'
              });
            }
            
          } catch (error) {
            wx.hideLoading();
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
  reviewOrder() {
    wx.navigateTo({
      url: `/pages/review/review?orderId=${this.data.orderId}`
    });
  },

  // 再来一单
  reorder() {
    const storage = require('../../utils/storage.js');
    let cart = storage.getCart();
    
    this.data.order.items.forEach(item => {
      const cartItem = {
        id: item.productId,
        name: item.name,
        image: item.image,
        price: item.price,
        quantity: item.quantity,
        customizations: item.customizations
      };
      
      const existingIndex = cart.findIndex(c => c.id === item.productId);
      if (existingIndex >= 0) {
        cart[existingIndex].quantity += item.quantity;
      } else {
        cart.push(cartItem);
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
  contactService() {
    wx.makePhoneCall({
      phoneNumber: '400-123-4567'
    });
  }
});
