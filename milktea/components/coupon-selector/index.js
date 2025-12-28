const { couponApi } = require('../../utils/api.js');

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    value: {
      type: String,
      value: ''
    },
    orderAmount: {
      type: Number,
      value: 0
    },
    placeholder: {
      type: String,
      value: '选择优惠券'
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    showModal: false,
    coupons: [],
    filteredCoupons: [],
    selectedCouponId: '',
    selectedCoupon: null
  },

  lifetimes: {
    attached() {
      this.loadCoupons();
      this.setData({
        selectedCouponId: this.properties.value
      });
    }
  },

  /**
   * 组件的方法列表
   */
  methods: {
    async loadCoupons() {
      try {
        const res = await couponApi.getMyCoupons();
        const coupons = res.data || [];
        this.setData({ coupons }, () => {
          this.filterCoupons();
          this.updateSelectedCoupon(this.properties.value);
        });
      } catch (err) {
        console.error('加载优惠券失败', err);
      }
    },

    filterCoupons() {
      const { coupons, orderAmount } = this.data;
      const filtered = coupons.map(c => ({
        ...c,
        isUsable: this.isCouponUsable(c, orderAmount)
      }));
      this.setData({ filteredCoupons: filtered });
    },

    isCouponUsable(coupon, amount) {
      if (coupon.minAmount > 0 && amount < coupon.minAmount) return false;
      return true;
    },

    showSelector() {
      this.filterCoupons();
      this.setData({ showModal: true });
    },

    hideSelector() {
      this.setData({ showModal: false });
    },

    selectCoupon(e) {
      const coupon = e.currentTarget.dataset.coupon;
      if (!coupon.isUsable) return;

      if (this.data.selectedCouponId === coupon.id) {
        this.setData({
          selectedCouponId: '',
          selectedCoupon: null
        });
      } else {
        this.setData({
          selectedCouponId: coupon.id,
          selectedCoupon: coupon
        });
      }
    },

    confirmSelection() {
      this.triggerEvent('change', {
        value: this.data.selectedCouponId,
        coupon: this.data.selectedCoupon
      });
      this.hideSelector();
    },

    updateSelectedCoupon(id) {
      const coupon = this.data.coupons.find(c => c.id === id);
      this.setData({
        selectedCouponId: id,
        selectedCoupon: coupon
      });
    }
  },

  observers: {
    'value': function(newVal) {
      this.updateSelectedCoupon(newVal);
    },
    'orderAmount': function() {
      this.filterCoupons();
    }
  }
})