const app = getApp();

Page({
  data: {
    orderId: '',
    order: null,
    score: 5,
    content: '',
    images: [],
    loading: false
  },

  onLoad(options) {
    if (options.orderId) {
      this.setData({ orderId: options.orderId });
      this.loadOrderDetail();
    }
  },

  async loadOrderDetail() {
    try {
      const { orderApi } = require('../../utils/api.js');
      const res = await orderApi.getOrderDetail(this.data.orderId);
      if (res.code === 200) {
        this.setData({ order: res.data });
      }
    } catch (error) {
      console.error('加载订单失败:', error);
    }
  },

  onScoreChange(e) {
    this.setData({ score: e.currentTarget.dataset.score });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  chooseImage() {
    wx.chooseImage({
      count: 6 - this.data.images.length,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: (res) => {
        this.setData({
          images: [...this.data.images, ...res.tempFilePaths]
        });
      }
    });
  },

  deleteImage(e) {
    const index = e.currentTarget.dataset.index;
    const images = this.data.images;
    images.splice(index, 1);
    this.setData({ images });
  },

  previewImage(e) {
    wx.previewImage({
      current: e.currentTarget.dataset.url,
      urls: this.data.images
    });
  },

  async submitReview() {
    const { score, content, images, order, loading } = this.data;

    if (!content) {
      return wx.showToast({ title: '请输入评价内容', icon: 'none' });
    }

    this.setData({ loading: true });

    try {
      const { orderApi, systemApi } = require('../../utils/api.js');
      
      // 1. 上传图片
      let imageUrls = [];
      if (images.length > 0) {
        for (const path of images) {
          const url = await systemApi.uploadFile(path);
          imageUrls.push(url);
        }
      }

      // 2. 提交评价
      const res = await orderApi.rateOrder(order.orderNo, {
        score,
        content,
        images: imageUrls
      });

      if (res.code === 200) {
        wx.showToast({
          title: '评价成功',
          icon: 'success',
          duration: 2000
        });
        setTimeout(() => {
          wx.navigateBack();
        }, 2000);
      } else {
        throw new Error(res.message);
      }
    } catch (error) {
      console.error('提交评价失败:', error);
      wx.showToast({
        title: error.message || '提交失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  }
});