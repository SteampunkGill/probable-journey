const app = getApp();

Page({
  data: {
    orderNo: '',
    types: ['服务态度', '商品质量', '配送问题', '支付问题', '其他'],
    typeIndex: 0,
    content: '',
    images: [],
    phone: '',
    loading: false
  },

  onLoad(options) {
    if (options.orderNo) {
      this.setData({ orderNo: options.orderNo });
    }
    // 预填手机号
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo && userInfo.phone) {
      this.setData({ phone: userInfo.phone });
    }
  },

  onTypeChange(e) {
    this.setData({ typeIndex: e.detail.value });
  },

  onContentInput(e) {
    this.setData({ content: e.detail.value });
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value });
  },

  chooseImage() {
    wx.chooseImage({
      count: 4 - this.data.images.length,
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

  async submitComplaint() {
    const { content, phone, loading, orderNo, typeIndex, types, images } = this.data;

    if (!content) {
      return wx.showToast({ title: '请输入投诉内容', icon: 'none' });
    }
    if (!phone) {
      return wx.showToast({ title: '请输入联系电话', icon: 'none' });
    }

    this.setData({ loading: true });

    try {
      const { afterSalesApi, systemApi } = require('../../utils/api.js');
      
      // 1. 上传图片 (如果有)
      let imageUrls = [];
      if (images.length > 0) {
        for (const path of images) {
          const url = await systemApi.uploadFile(path);
          imageUrls.push(url);
        }
      }

      // 2. 提交投诉
      const res = await afterSalesApi.submitComplaint({
        orderNo,
        type: types[typeIndex],
        content,
        phone,
        images: imageUrls
      });

      if (res.code === 200) {
        wx.showToast({
          title: '提交成功',
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
      console.error('提交投诉失败:', error);
      wx.showToast({
        title: error.message || '提交失败',
        icon: 'none'
      });
    } finally {
      this.setData({ loading: false });
    }
  }
});