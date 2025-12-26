// components/address-picker/address-picker.js
const storage = require('../../utils/storage.js');

Component({
  properties: {
    // 当前选中的地址ID
    value: {
      type: String,
      value: '',
      observer: function(newVal) {
        this.updateDisplayText(newVal);
      }
    },
    
    // 占位符
    placeholder: {
      type: String,
      value: '请选择地址'
    },
    
    // 是否必选
    required: {
      type: Boolean,
      value: false
    },
    
    // 是否显示默认标签
    showDefaultTag: {
      type: Boolean,
      value: true
    },
    
    // 是否允许添加新地址
    allowAdd: {
      type: Boolean,
      value: true
    }
  },

  data: {
    // 是否显示弹窗
    showModal: false,
    
    // 地址列表
    addressList: [],
    
    // 当前选中的地址ID
    selectedId: '',
    
    // 显示文本
    displayText: '',
    
    // 完整地址对象
    selectedAddress: null
  },

  lifetimes: {
    attached() {
      this.loadAddressList();
      this.updateDisplayText(this.properties.value);
    }
  },

  methods: {
    // 加载地址列表
    loadAddressList() {
      const addressList = storage.getAddresses() || [];
      
      // 如果没有地址，使用默认数据
      if (addressList.length === 0) {
        addressList.push({
          id: 'default_1',
          name: '示例用户',
          phone: '13800138000',
          province: '北京市',
          city: '北京市',
          district: '朝阳区',
          detail: '建国路88号',
          isDefault: true
        });
      }
      
      this.setData({ addressList });
    },

    // 更新显示文本
    updateDisplayText(addressId) {
      const { addressList } = this.data;
      const address = addressList.find(addr => addr.id === addressId);
      
      if (address) {
        const displayText = `${address.name} ${address.phone} ${address.province}${address.city}${address.district}${address.detail}`;
        this.setData({
          displayText,
          selectedAddress: address
        });
      } else {
        this.setData({
          displayText: '',
          selectedAddress: null
        });
      }
    },

    // 显示选择器
    showPicker() {
      this.loadAddressList();
      
      // 设置当前选中
      this.setData({
        showModal: true,
        selectedId: this.properties.value
      });
    },

    // 隐藏选择器
    hidePicker() {
      this.setData({
        showModal: false
      });
    },

    // 选择地址
    selectAddress(e) {
      const addressId = e.currentTarget.dataset.id;
      this.setData({
        selectedId: addressId
      });
    },

    // 确认选择
    confirmSelection() {
      const { selectedId, addressList } = this.data;
      
      if (!selectedId) {
        wx.showToast({
          title: '请选择地址',
          icon: 'none'
        });
        return;
      }
      
      const selectedAddress = addressList.find(addr => addr.id === selectedId);
      
      // 更新显示文本
      this.updateDisplayText(selectedId);
      
      // 触发change事件
      this.triggerEvent('change', {
        value: selectedId,
        address: selectedAddress
      });
      
      // 隐藏弹窗
      this.hidePicker();
    },

    // 跳转到添加地址页
    goToAddAddress() {
      this.hidePicker();
      
      wx.navigateTo({
        url: '/pages/address/edit'
      });
    },

    // 获取选中的地址
    getSelectedAddress() {
      return this.data.selectedAddress;
    },

    // 设置地址
    setAddress(addressId) {
      this.updateDisplayText(addressId);
      this.setData({
        selectedId: addressId
      });
    },

    // 清除选择
    clear() {
      this.setData({
        selectedId: '',
        displayText: '',
        selectedAddress: null
      });
      
      this.triggerEvent('change', {
        value: '',
        address: null
      });
    }
  }
});