const { addressApi } = require('../../utils/api.js');

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    value: {
      type: String,
      value: ''
    },
    placeholder: {
      type: String,
      value: '请选择地址'
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    showModal: false,
    addressList: [],
    selectedId: '',
    displayText: ''
  },

  lifetimes: {
    attached() {
      this.loadAddressList();
      this.setData({
        selectedId: this.properties.value
      });
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    async loadAddressList() {
      try {
        const res = await addressApi.getAddressList();
        const list = res.data || [];
        this.setData({ addressList: list }, () => {
          this.updateDisplayText(this.properties.value);
        });
      } catch (err) {
        console.error('加载地址列表失败', err);
      }
    },

    updateDisplayText(addressId) {
      const address = this.data.addressList.find(addr => addr.id === addressId);
      if (address) {
        const text = `${address.name} ${address.phone} ${address.province}${address.city}${address.district}${address.detail}`;
        this.setData({ displayText: text });
      } else {
        this.setData({ displayText: '' });
      }
    },

    showPicker() {
      this.loadAddressList();
      this.setData({ 
        showModal: true,
        selectedId: this.properties.value
      });
    },

    hidePicker() {
      this.setData({ showModal: false });
    },

    selectAddress(e) {
      this.setData({
        selectedId: e.currentTarget.dataset.id
      });
    },

    confirmSelection() {
      const address = this.data.addressList.find(addr => addr.id === this.data.selectedId);
      this.updateDisplayText(this.data.selectedId);
      this.triggerEvent('change', {
        value: this.data.selectedId,
        address: address
      });
      this.hidePicker();
    },

    goToAddAddress() {
      this.hidePicker();
      wx.navigateTo({
        url: '/pages/address/edit'
      });
    }
  },

  observers: {
    'value': function(newVal) {
      this.updateDisplayText(newVal);
      this.setData({ selectedId: newVal });
    }
  }
})