const { orderApi, commonApi } = require('../../utils/api.js');

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
    this.orderId = options.id;
    this.loadOrderDetail();
  },

  async loadOrderDetail() {
    this.setData({ loading: true });
    try {
      const res = await orderApi.getOrderDetail(this.orderId);
      this.setData({ order: res.data });
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

  async chooseImage() {
    wx.chooseImage({
      count: 4 - this.data.images.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        wx.showLoading({ title: '上传中...' });
        try {
          const uploadPromises = res.tempFilePaths.map(path => commonApi.uploadImage(path, 'review'));
          const results = await Promise.all(uploadPromises);
          const newImages = results.map(r => r.data.url);
          this.setData({
            images: [...this.data.images, ...newImages]
          });
        } catch (error) {
          console.error('上传图片失败:', error);
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
    wx.previewImage({
      current: e.currentTarget.dataset.url,
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
      wx.showToast({ title: '评价成功', icon: 'success' });
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (error) {
      console.error('提交评价失败:', error);
      wx.showToast({ title: '提交失败', icon: 'none' });
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
      await orderApi.submitAppeal(this.data.order.orderNo, {
        reason: this.data.appealReasons[this.data.appealReasonIndex],
        description: this.data.appealDescription,
        amount: this.data.order.totalAmount
      });
      wx.showToast({ title: '申诉已提交', icon: 'success' });
      this.setData({ showAppealModal: false });
      this.loadOrderDetail();
    } catch (error) {
      console.error('提交申诉失败:', error);
      wx.showToast({ title: '提交失败', icon: 'none' });
    } finally {
      this.setData({ appealing: false });
    }
  }
});