const { orderApi, commonApi } = require('../../utils/api');

Page({
  data: {
    loading: true,
    submitting: false,
    order: null,
    productScore: 5,
    deliveryScore: 5,
    content: '',
    images: [],
    isAnonymous: false,
    showAppealModal: false,
    appealing: false,
    appealReasons: ['商品错漏', '质量问题', '配送超时', '其他'],
    appealReasonIndex: 0,
    appealDescription: '',
    productScoreText: '超赞',
    deliveryScoreText: '超赞'
  },

  onLoad(options) {
    const { orderNo } = options;
    if (orderNo) {
      this.loadOrderDetail(orderNo);
    }
  },

  async loadOrderDetail(orderNo) {
    this.setData({ loading: true });
    try {
      const res = await orderApi.getOrderDetail(orderNo);
      if (res.code === 200) {
        const order = res.data;
        // 格式化商品数据
        order.items = (order.orderItems || []).map(item => ({
          ...item,
          name: item.productName,
          image: item.productImage
        }));
        this.setData({ order });
      }
    } catch (error) {
      console.error('加载订单详情失败:', error);
    } finally {
      this.setData({ loading: false });
    }
  },

  getScoreText(score) {
    const texts = { 1: '极差', 2: '失望', 3: '一般', 4: '满意', 5: '超赞' };
    return texts[score];
  },

  setProductScore(e) {
    const score = e.currentTarget.dataset.score;
    this.setData({
      productScore: score,
      productScoreText: this.getScoreText(score)
    });
  },

  setDeliveryScore(e) {
    const score = e.currentTarget.dataset.score;
    this.setData({
      deliveryScore: score,
      deliveryScoreText: this.getScoreText(score)
    });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  chooseImage() {
    wx.chooseImage({
      count: 4 - this.data.images.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePaths = res.tempFilePaths;
        wx.showLoading({ title: '上传中...' });
        try {
          for (const path of tempFilePaths) {
            const uploadRes = await commonApi.uploadImage(path);
            if (uploadRes.code === 200) {
              this.setData({
                images: [...this.data.images, uploadRes.data.url]
              });
            }
          }
        } catch (error) {
          wx.showToast({ title: '上传失败', icon: 'none' });
        } finally {
          wx.hideLoading();
        }
      }
    });
  },

  removeImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = [...this.data.images];
    images.splice(index, 1);
    this.setData({ images });
  },

  previewImage(e) {
    const url = e.currentTarget.dataset.url;
    wx.previewImage({
      current: url,
      urls: this.data.images
    });
  },

  toggleAnonymous() {
    this.setData({ isAnonymous: !this.data.isAnonymous });
  },

  async submitReview() {
    if (!this.data.content.trim()) {
      wx.showToast({ title: '请输入评价内容', icon: 'none' });
      return;
    }

    this.setData({ submitting: true });
    try {
      const res = await orderApi.rateOrder(this.data.order.orderNo, {
        productScore: this.data.productScore,
        deliveryScore: this.data.deliveryScore,
        score: Math.round((this.data.productScore + this.data.deliveryScore) / 2),
        content: this.data.content,
        images: this.data.images,
        isAnonymous: this.data.isAnonymous,
        productId: this.data.order.items && this.data.order.items.length > 0 ? this.data.order.items[0].productId : null
      });
      if (res.code === 200) {
        wx.showToast({ title: '评价成功！' });
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({ title: res.message || '评价失败', icon: 'none' });
      }
    } catch (error) {
      wx.showToast({ title: '提交失败，请重试', icon: 'none' });
    } finally {
      this.setData({ submitting: false });
    }
  },

  showAppeal() {
    this.setData({ showAppealModal: true });
  },

  hideAppeal() {
    this.setData({ showAppealModal: false });
  },

  onAppealReasonChange(e) {
    this.setData({ appealReasonIndex: e.detail.value });
  },

  onAppealDescriptionInput(e) {
    this.setData({ appealDescription: e.detail.value });
  },

  async submitAppeal() {
    if (!this.data.appealDescription.trim()) {
      wx.showToast({ title: '请填写详细描述', icon: 'none' });
      return;
    }
    this.setData({ appealing: true });
    try {
      const res = await orderApi.applyRefund(this.data.order.orderNo, {
        reason: this.data.appealReasons[this.data.appealReasonIndex],
        description: this.data.appealDescription,
        amount: this.data.order.totalAmount
      });
      if (res.code === 200) {
        wx.showModal({
          title: '提示',
          content: '申诉已提交，请等待后台处理',
          showCancel: false,
          success: () => {
            this.hideAppeal();
            this.loadOrderDetail(this.data.order.orderNo);
          }
        });
      } else {
        wx.showToast({ title: res.message || '提交失败', icon: 'none' });
      }
    } catch (error) {
      wx.showToast({ title: '提交失败，请重试', icon: 'none' });
    } finally {
      this.setData({ appealing: false });
    }
  }
});