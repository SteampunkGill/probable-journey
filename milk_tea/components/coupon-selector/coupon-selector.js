// components/coupon-selector/coupon-selector.js
const util = require('../../utils/util.js');

Component({
  properties: {
    // 当前选中的优惠券ID
    value: {
      type: String,
      value: '',
      observer: function(newVal) {
        this.updateSelectedCoupon(newVal);
      }
    },
    
    // 订单金额（用于判断优惠券是否可用）
    orderAmount: {
      type: Number,
      value: 0
    },
    
    // 占位符
    placeholder: {
      type: String,
      value: '选择优惠券'
    },
    
    // 是否显示不可用的优惠券
    showUnusable: {
      type: Boolean,
      value: true
    },
    
    // 优惠券类型筛选
    couponType: {
      type: String,
      value: '' // 空表示所有类型
    }
  },

  data: {
    // 是否显示选择器
    showModal: false,
    
    // 优惠券列表
    coupons: [],
    
    // 筛选后的优惠券
    filteredCoupons: [],
    
    // 当前选中的优惠券ID
    selectedCouponId: '',
    
    // 选中的优惠券对象
    selectedCoupon: null,
    
    // 当前激活的标签
    activeTab: '',
    
    // 优惠券类型统计
    couponTypes: [],
    totalCount: 0
  },

  lifetimes: {
    attached() {
      this.loadCoupons();
      this.updateSelectedCoupon(this.properties.value);
    }
  },

  observers: {
    'orderAmount': function(orderAmount) {
      // 订单金额变化时，重新筛选优惠券
      this.filterCoupons();
    },
    
    'couponType': function(couponType) {
      // 优惠券类型变化时，更新激活标签
      this.setData({
        activeTab: couponType
      }, () => {
        this.filterCoupons();
      });
    }
  },

  methods: {
    // 加载优惠券数据
    loadCoupons() {
      // 模拟优惠券数据
      const coupons = [
        {
          id: 'coupon_001',
          name: '满30减5',
          type: 'discount',
          value: 5,
          minAmount: 30,
          description: '全场通用',
          expireAt: '2024-12-31',
          usable: true
        },
        {
          id: 'coupon_002',
          name: '满50减10',
          type: 'discount',
          value: 10,
          minAmount: 50,
          description: '仅限茶饮类',
          expireAt: '2024-12-31',
          usable: true
        },
        {
          id: 'coupon_003',
          name: '饮品8折券',
          type: 'percentage',
          value: 0.2,
          minAmount: 0,
          maxDiscount: 15,
          description: '最高优惠15元',
          expireAt: '2024-12-31',
          usable: true
        },
        {
          id: 'coupon_004',
          name: '免配送费券',
          type: 'free_delivery',
          value: 0,
          minAmount: 20,
          description: '免配送费',
          expireAt: '2024-06-30',
          usable: true
        },
        {
          id: 'coupon_005',
          name: '新客专享',
          type: 'new_user',
          value: 8,
          minAmount: 15,
          description: '新用户专享优惠',
          expireAt: '2024-03-31',
          usable: false
        }
      ];
      
      // 统计优惠券类型
      const typeStats = this.calculateTypeStats(coupons);
      
      this.setData({
        coupons,
        couponTypes: typeStats.types,
        totalCount: typeStats.total
      }, () => {
        this.filterCoupons();
      });
    },

    // 统计优惠券类型
    calculateTypeStats(coupons) {
      const typeMap = {};
      
      coupons.forEach(coupon => {
        const type = coupon.type || 'other';
        if (!typeMap[type]) {
          typeMap[type] = {
            type: type,
            name: this.getTypeName(type),
            count: 0
          };
        }
        typeMap[type].count++;
      });
      
      const types = Object.values(typeMap);
      const total = coupons.length;
      
      return { types, total };
    },

    // 获取类型名称
    getTypeName(type) {
      const nameMap = {
        'discount': '满减券',
        'percentage': '折扣券',
        'free_delivery': '免运费券',
        'new_user': '新人券',
        'other': '其他'
      };
      
      return nameMap[type] || type;
    },

    // 筛选优惠券
    filterCoupons() {
      const { coupons, activeTab, showUnusable } = this.data;
      const { orderAmount } = this.properties;
      
      let filtered = [...coupons];
      
      // 按类型筛选
      if (activeTab) {
        filtered = filtered.filter(coupon => coupon.type === activeTab);
      }
      
      // 判断是否可用
      filtered = filtered.map(coupon => {
        const usable = this.isCouponUsable(coupon, orderAmount);
        return {
          ...coupon,
          usable
        };
      });
      
      // 是否显示不可用的
      if (!showUnusable) {
        filtered = filtered.filter(coupon => coupon.usable);
      }
      
      this.setData({ filteredCoupons: filtered });
    },

    // 判断优惠券是否可用
    isCouponUsable(coupon, orderAmount) {
      // 检查是否过期
      if (coupon.expireAt) {
        const expireDate = new Date(coupon.expireAt);
        const now = new Date();
        if (expireDate < now) {
          return false;
        }
      }
      
      // 检查是否可用
      if (coupon.usable === false) {
        return false;
      }
      
      // 检查最低金额要求
      if (coupon.minAmount > 0 && orderAmount < coupon.minAmount) {
        return false;
      }
      
      return true;
    },

    // 更新选中的优惠券
    updateSelectedCoupon(couponId) {
      const { coupons } = this.data;
      const coupon = coupons.find(c => c.id === couponId);
      
      this.setData({
        selectedCouponId: couponId,
        selectedCoupon: coupon
      });
    },

    // 格式化优惠券值
    formatValue(coupon) {
      if (!coupon) return '0';
      
      if (coupon.value < 1) {
        // 折扣券
        return (coupon.value * 10).toFixed(1);
      } else {
        // 满减券
        return coupon.value.toFixed(0);
      }
    },

    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '长期有效';
      return util.formatDate(dateString, 'YYYY-MM-DD');
    },

    // 显示选择器
    showSelector() {
      this.filterCoupons();
      this.setData({
        showModal: true
      });
    },

    // 隐藏选择器
    hideSelector() {
      this.setData({
        showModal: false
      });
    },

    // 切换标签
    changeTab(e) {
      const tab = e.currentTarget.dataset.tab;
      this.setData({
        activeTab: tab
      }, () => {
        this.filterCoupons();
      });
    },

    // 选择优惠券
    selectCoupon(e) {
      const coupon = e.currentTarget.dataset.coupon;
      
      // 检查是否可用
      if (!this.isCouponUsable(coupon, this.properties.orderAmount)) {
        wx.showToast({
          title: '此优惠券不可用',
          icon: 'none'
        });
        return;
      }
      
      // 如果已经选中，则取消选择
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

    // 判断是否选中
    isSelected(couponId) {
      return this.data.selectedCouponId === couponId;
    },

    // 判断是否可用
    isUsable(coupon) {
      return this.isCouponUsable(coupon, this.properties.orderAmount);
    },

    // 确认选择
    confirmSelection() {
      const { selectedCouponId, selectedCoupon } = this.data;
      
      if (!selectedCouponId) {
        this.triggerEvent('change', {
          value: '',
          coupon: null
        });
      } else {
        this.triggerEvent('change', {
          value: selectedCouponId,
          coupon: selectedCoupon
        });
      }
      
      this.hideSelector();
    },

    // 跳转到领券中心
    goToCouponCenter() {
      this.hideSelector();
      
      wx.navigateTo({
        url: '/pages/coupon/center'
      });
    },

    // 设置选中的优惠券
    setCoupon(couponId) {
      this.updateSelectedCoupon(couponId);
    },

    // 清除选择
    clear() {
      this.setData({
        selectedCouponId: '',
        selectedCoupon: null
      });
      
      this.triggerEvent('change', {
        value: '',
        coupon: null
      });
    },

    // 获取选中的优惠券
    getSelectedCoupon() {
      return this.data.selectedCoupon;
    }
  }
});