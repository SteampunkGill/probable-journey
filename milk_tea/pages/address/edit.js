// pages/address/edit.js
const storage = require('../../utils/storage.js');
const util = require('../../utils/util.js');
const { addressApi } = require('../../utils/api.js');

Page({
  data: {
    // 地址ID（编辑模式下）
    addressId: '',
    
    // 表单数据
    formData: {
      name: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: '',
      isDefault: false,
      tag: ''
    },
    
    // 标签选项
    labelOptions: ['家', '公司', '学校'],
    
    // 是否为编辑模式
    isEditMode: false,
    
    // 提交状态
    submitting: false
  },

  onLoad(options) {
    const addressId = options.id;
    
    if (addressId) {
      // 编辑模式
      this.setData({ 
        addressId,
        isEditMode: true
      });
      wx.setNavigationBarTitle({ title: '编辑地址' });
      this.loadAddressDetail(addressId);
    } else {
      // 新增模式
      wx.setNavigationBarTitle({ title: '添加地址' });
    }
  },

  // 加载地址详情
  async loadAddressDetail(addressId) {
    try {
      const res = await addressApi.getAddressList();
      if (res.code === 200) {
        const address = res.data.find(addr => addr.id == addressId);
        if (address) {
          this.setData({
            formData: {
              name: address.name || '',
              phone: address.phone || '',
              province: address.province || '',
              city: address.city || '',
              district: address.district || '',
              detail: address.detail || '',
              isDefault: address.isDefault || false,
              tag: address.tag || ''
            }
          });
        }
      }
    } catch (error) {
      console.error('加载地址详情失败:', error);
    }
  },

  // 输入姓名
  onNameInput(e) {
    this.setData({
      'formData.name': e.detail.value
    });
  },

  // 输入电话
  onPhoneInput(e) {
    this.setData({
      'formData.phone': e.detail.value
    });
  },

  // 智能定位获取地址
  onLocationClick() {
    wx.chooseLocation({
      success: (res) => {
        console.log('选择位置成功:', res);
        // 解析地址
        // 微信返回的地址通常包含省市区，但格式不固定
        // 这里简单处理，实际开发中可能需要逆地理编码
        this.setData({
          'formData.detail': res.name || res.address,
          'formData.latitude': res.latitude,
          'formData.longitude': res.longitude
        });
        
        // 尝试从地址字符串中提取省市区
        this.parseAddress(res.address);
      },
      fail: (err) => {
        console.error('选择位置失败:', err);
        if (err.errMsg.indexOf('auth deny') >= 0) {
          wx.showModal({
            title: '授权提示',
            content: '需要您的位置权限才能使用定位功能',
            confirmText: '去授权',
            success: (res) => {
              if (res.confirm) {
                wx.openSetting();
              }
            }
          });
        }
      }
    });
  },

  // 简单解析地址字符串
  parseAddress(addressStr) {
    // 这是一个非常简单的解析逻辑，仅供演示
    const regions = ['省', '市', '区', '县'];
    let province = '', city = '', district = '';
    
    const pIdx = addressStr.indexOf('省');
    if (pIdx > 0) {
      province = addressStr.substring(0, pIdx + 1);
      addressStr = addressStr.substring(pIdx + 1);
    }
    
    const cIdx = addressStr.indexOf('市');
    if (cIdx > 0) {
      city = addressStr.substring(0, cIdx + 1);
      addressStr = addressStr.substring(cIdx + 1);
    }
    
    const dIdx = addressStr.indexOf('区') || addressStr.indexOf('县');
    if (dIdx > 0) {
      district = addressStr.substring(0, dIdx + 1);
    }

    this.setData({
      'formData.province': province || '广东省', // 兜底
      'formData.city': city || '深圳市',
      'formData.district': district || '南山区'
    });
  },

  // 选择地区
  onRegionChange(e) {
    const value = e.detail.value;
    this.setData({
      'formData.province': value[0],
      'formData.city': value[1],
      'formData.district': value[2]
    });
  },

  // 输入详细地址
  onDetailInput(e) {
    this.setData({
      'formData.detail': e.detail.value
    });
  },

  // 选择标签
  selectLabel(e) {
    const tag = e.currentTarget.dataset.label;
    this.setData({
      'formData.tag': tag
    });
  },

  // 切换默认地址
  toggleDefault(e) {
    this.setData({
      'formData.isDefault': e.detail.value
    });
  },

  // 验证表单
  validateForm() {
    const { name, phone, province, city, district, detail } = this.data.formData;
    
    if (!name || !name.trim()) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' });
      return false;
    }
    
    if (!phone || !util.validatePhone(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' });
      return false;
    }
    
    if (!province || !city || !district) {
      wx.showToast({ title: '请选择所在地区', icon: 'none' });
      return false;
    }
    
    if (!detail || !detail.trim()) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' });
      return false;
    }
    
    return true;
  },

  // 保存地址
  async saveAddress() {
    if (this.data.submitting) return;
    if (!this.validateForm()) return;
    
    this.setData({ submitting: true });
    wx.showLoading({ title: '保存中...' });
    
    try {
      const { formData, addressId, isEditMode } = this.data;
      let res;
      
      if (isEditMode) {
        res = await addressApi.updateAddress(addressId, formData);
      } else {
        res = await addressApi.addAddress(formData);
      }
      
      if (res.code === 200) {
        wx.showToast({ title: '保存成功', icon: 'success' });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({ title: res.message || '保存失败', icon: 'none' });
      }
    } catch (error) {
      console.error('保存地址失败:', error);
      wx.showToast({ title: '网络错误', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
      wx.hideLoading();
    }
  },

  // 删除地址
  deleteAddress() {
    wx.showModal({
      title: '删除地址',
      content: '确定要删除该地址吗？',
      confirmColor: '#FF6B6B',
      success: (res) => {
        if (res.confirm) {
          this.confirmDelete();
        }
      }
    });
  },

  // 确认删除
  async confirmDelete() {
    try {
      const res = await addressApi.deleteAddress(this.data.addressId);
      if (res.code === 200) {
        wx.showToast({ title: '删除成功', icon: 'success' });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      }
    } catch (error) {
      console.error('删除地址失败:', error);
      wx.showToast({ title: '删除失败', icon: 'none' });
    }
  }
});
