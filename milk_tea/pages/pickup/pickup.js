// pages/pickup/pickup.js
const app = getApp();
const util = require('../../utils/util.js');

Page({
  data: {
    // 待取餐订单
    pendingOrders: [],
    
    // 历史订单
    historyOrders: [],
    
    // 当前激活的订单（用于显示取餐码）
    activeOrder: null,
    
    // 扫码结果
    scanResult: null,
    
    // 加载状态
    loading: true,
    refreshing: false
  },

  onLoad() {
    this.loadOrders();
  },

  onShow() {
    // 每次显示时刷新数据
    this.refreshOrders();
  },

  onPullDownRefresh() {
    this.refreshOrders();
  },

  // 加载订单
  async loadOrders() {
    try {
      this.setData({ loading: true });
      
      const { orderApi } = require('../../utils/api.js');
      const res = await orderApi.getOrderList();
      
      if (res.code === 200) {
        const orders = res.data;
        
        // 分离待取餐和历史订单
        const pendingOrders = orders.filter(o =>
          ['PAID', 'MAKING', 'READY'].includes(o.status)
        );
        const historyOrders = orders.filter(o =>
          ['DELIVERED', 'COMPLETED', 'FINISHED'].includes(o.status)
        );
        
        // 设置默认激活第一个待取餐订单
        const activeOrder = pendingOrders.length > 0 ? pendingOrders[0] : null;
        
        this.setData({
          pendingOrders,
          historyOrders,
          activeOrder,
          loading: false,
          refreshing: false
        });
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

  // 刷新订单
  refreshOrders() {
    this.setData({ refreshing: true });
    this.loadOrders();
  },

  // 选择订单
  selectOrder(e) {
    const orderId = e.currentTarget.dataset.id;
    const order = this.data.pendingOrders.find(o => o.id === orderId);
    if (order) {
      this.setData({ activeOrder: order });
    }
  },

  // 扫码取餐
  scanToPickup() {
    wx.scanCode({
      onlyFromCamera: true,
      scanType: ['qrCode'],
      success: (res) => {
        console.log('扫码结果:', res);
        this.handleScanResult(res.result);
      },
      fail: (err) => {
        console.error('扫码失败:', err);
        wx.showToast({
          title: '扫码失败',
          icon: 'none'
        });
      }
    });
  },

  // 处理扫码结果
  handleScanResult(result) {
    try {
      // 解析二维码内容
      const data = JSON.parse(result);
      
      if (data.type === 'pickup' && data.storeId) {
        // 显示取餐码输入弹窗
        this.showPickupCodeInput(data.storeId);
      } else {
        wx.showToast({
          title: '无效的二维码',
          icon: 'none'
        });
      }
    } catch (error) {
      wx.showToast({
        title: '二维码格式错误',
        icon: 'none'
      });
    }
  },

  // 显示取餐码输入弹窗
  showPickupCodeInput(storeId) {
    wx.showModal({
      title: '请输入取餐码',
      editable: true,
      placeholderText: '例如：A123',
      success: (res) => {
        if (res.confirm && res.content) {
          this.verifyPickupCode(res.content.toUpperCase(), storeId);
        }
      }
    });
  },

  // 验证取餐码
  async verifyPickupCode(code, storeId) {
    wx.showLoading({ title: '验证中...' });
    
    try {
      // 模拟验证取餐码
      await new Promise(resolve => setTimeout(resolve, 1000));
      
      // 查找对应订单
      const order = this.data.pendingOrders.find(o => 
        o.pickupCode === code && o.store.id === storeId
      );
      
      wx.hideLoading();
      
      if (order) {
        // 确认取餐
        this.confirmPickup(order);
      } else {
        wx.showToast({
          title: '取餐码不正确',
          icon: 'none'
        });
      }
      
    } catch (error) {
      wx.hideLoading();
      wx.showToast({
        title: '验证失败',
        icon: 'none'
      });
    }
  },

  // 确认取餐
  confirmPickup(order) {
    wx.showModal({
      title: '确认取餐',
      content: `订单号：${order.orderNo}\n共${order.itemCount}件商品\n确认已取餐？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            // 模拟确认取餐
            await new Promise(resolve => setTimeout(resolve, 500));
            
            wx.showToast({
              title: '取餐成功',
              icon: 'success',
              duration: 1500
            });
            
            // 刷新订单列表
            setTimeout(() => {
              this.refreshOrders();
            }, 1500);
            
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

  // 查看订单详情
  goToOrderDetail(e) {
    const orderId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/order-detail/order-detail?id=${orderId}`
    });
  },

  // 拨打电话
  callStore(e) {
    const phone = e.currentTarget.dataset.phone;
    wx.makePhoneCall({
      phoneNumber: phone
    });
  },

  // 查看门店
  goToStore(e) {
    const storeId = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/store/detail?id=${storeId}`
    });
  },

  // 催单
  remindOrder(e) {
    e.stopPropagation();
    const orderId = e.currentTarget.dataset.id;
    
    wx.showLoading({ title: '催单中...' });
    
    setTimeout(() => {
      wx.hideLoading();
      wx.showToast({
        title: '已提醒商家尽快制作',
        icon: 'success'
      });
    }, 1000);
  },
  
  // 去点单
  goToOrder() {
    wx.switchTab({
      url: '/pages/order/order'
    });
  }
});
