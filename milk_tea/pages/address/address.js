// pages/address/address.js
const app = getApp();
const storage = require('../../utils/storage.js');
const { addressApi } = require('../../utils/api.js');

Page({
  data: {
    // 模式:list-列表模式, select-选择模式
    mode: 'list',
    
    // 地址列表
    addressList: [],
    
    // 历史地址列表
    historyList: [],
    
    // 当前选中的地址ID(选择模式下使用)
    selectedId: '',
    
    // 加载状态
    loading: false
  },

  onLoad(options) {
    const mode = options.mode || 'list';
    const selectedId = options.selectedId || '';
    
    this.setData({ mode, selectedId });
    
    // 设置导航栏标题
    wx.setNavigationBarTitle({
      title: mode === 'select' ? '选择地址' : '地址管理'
    });
    
    this.loadAddressList();
  },

  onShow() {
    this.loadAddressList();
  },

  onPullDownRefresh() {
    this.loadAddressList();
    wx.stopPullDownRefresh();
  },

  // 加载地址列表
  async loadAddressList() {
    this.setData({ loading: true });
    try {
      const res = await addressApi.getAddressList();
      if (res.code === 200) {
        this.setData({ addressList: res.data });
      }
    } catch (error) {
      console.error('加载地址列表失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  // 添加地址
  addAddress() {
    wx.navigateTo({
      url: '/pages/address/edit'
    });
  },

  // 编辑地址
  editAddress(e) {
    e.stopPropagation();
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/address/edit?id=${id}`
    });
  },

  // 选择地址(选择模式)
  selectAddress(e) {
    const id = e.currentTarget.dataset.id;
    const address = this.data.addressList.find(addr => addr.id === id);
    
    if (this.data.mode === 'select' && address) {
      // 通过事件通道返回选中的地址
      const eventChannel = this.getOpenerEventChannel();
      if (eventChannel && eventChannel.emit) {
        eventChannel.emit('selectAddress', address);
      }
      
      wx.navigateBack();
    }
  },

  // 删除地址
  deleteAddress(e) {
    e.stopPropagation();
    const id = e.currentTarget.dataset.id;
    const addressList = this.data.addressList;
    const address = addressList.find(addr => addr.id === id);
    
    if (!address) return;
    
    wx.showModal({
      title: '确认删除',
      content: `确定要删除${address.name}的地址吗？`,
      confirmColor: '#FF6B6B',
      success: (res) => {
        if (res.confirm) {
          this.handleDeleteAddress(id);
        }
      }
    });
  },

  // 处理删除地址
  async handleDeleteAddress(id) {
    try {
      const res = await addressApi.deleteAddress(id);
      if (res.code === 200) {
        wx.showToast({ title: '删除成功', icon: 'success' });
        this.loadAddressList();
      }
    } catch (error) {
      console.error('删除地址失败:', error);
      wx.showToast({ title: '删除失败', icon: 'none' });
    }
  },

  // 切换默认地址
  async toggleDefault(e) {
    e.stopPropagation();
    const id = e.currentTarget.dataset.id;
    const address = this.data.addressList.find(addr => addr.id === id);
    if (!address || address.isDefault) return;

    try {
      const res = await addressApi.updateAddress(id, { ...address, isDefault: true });
      if (res.code === 200) {
        wx.showToast({ title: '已设为默认地址', icon: 'success' });
        this.loadAddressList();
      }
    } catch (error) {
      console.error('设置默认地址失败:', error);
    }
  }
});