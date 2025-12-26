// pages/points/product-detail/product-detail.js
const { pointsMallApi } = require('../../../utils/api.js');

Page({
  data: {
    product: null,
    loading: true,
    points: 0,
    selectedCount: 1,
    maxCount: 5
  },

  onLoad(options) {
    const id = options.id;
    if (id) {
      this.loadProductDetail(id);
    }
    this.loadUserData();
  },

  // 加载用户数据
  loadUserData() {
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo && userInfo.points) {
      this.setData({
        points: userInfo.points
      });
    }
  },

  // 加载商品详情
  loadProductDetail(id) {
    this.setData({ loading: true });
    
    // 由于后端没有单独的详情接口，我们从列表中找到对应商品
    pointsMallApi.getProducts().then(res => {
      if (res.code === 200 && res.data) {
        const product = res.data.find(item => item.id == id);
        if (product) {
          this.setData({
            product: {
              id: product.id,
              name: product.name,
              desc: product.description || '积分兑换商品',
              points: product.pointCost || product.points,
              originalPoints: product.originalPoints || Math.round((product.pointCost || product.points) * 1.2),
              stock: product.stock || 0,
              limit: product.limitInfo || '每人限兑5件',
              hot: product.hot || false,
              image: product.imageUrl || product.image || '/images/products/default.jpg',
              status: product.status || 'AVAILABLE',
              detail: product.description || '商品详情信息'
            },
            loading: false,
            maxCount: Math.min(product.stock || 5, 5)
          });
        } else {
          this.setData({ loading: false, product: null });
          wx.showToast({ title: '商品不存在', icon: 'none' });
        }
      } else {
        this.setData({ loading: false, product: null });
      }
    }).catch(() => {
      this.setData({ loading: false, product: null });
    });
  },

  // 增加数量
  increaseCount() {
    if (this.data.selectedCount < this.data.maxCount) {
      this.setData({
        selectedCount: this.data.selectedCount + 1
      });
    }
  },

  // 减少数量
  decreaseCount() {
    if (this.data.selectedCount > 1) {
      this.setData({
        selectedCount: this.data.selectedCount - 1
      });
    }
  },

  // 立即兑换
  exchangeNow() {
    const product = this.data.product;
    if (!product) return;
    
    if (product.stock <= 0) {
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
      return;
    }
    
    const totalPoints = product.points * this.data.selectedCount;
    
    if (this.data.points < totalPoints) {
      wx.showToast({
        title: '积分不足',
        icon: 'none'
      });
      return;
    }
    
    wx.showModal({
      title: '确认兑换',
      content: `确定要兑换【${product.name}】×${this.data.selectedCount}吗？\n需要${totalPoints}积分`,
      success: (res) => {
        if (res.confirm) {
          this.doExchange(product.id, this.data.selectedCount);
        }
      }
    });
  },

  // 执行兑换
  doExchange(productId, quantity) {
    wx.showLoading({ title: '兑换中...' });
    
    // 由于后端只支持兑换1件，我们循环调用
    let promises = [];
    for (let i = 0; i < quantity; i++) {
      promises.push(pointsMallApi.exchangeProduct(productId));
    }
    
    Promise.all(promises).then(results => {
      wx.hideLoading();
      
      const success = results.every(res => res.code === 200);
      if (success) {
        wx.showToast({
          title: '兑换成功',
          icon: 'success'
        });
        
        // 更新用户积分
        const product = this.data.product;
        const totalPoints = product.points * quantity;
        const newPoints = this.data.points - totalPoints;
        
        this.setData({ points: newPoints });
        
        // 更新本地存储的用户信息
        const userInfo = wx.getStorageSync('userInfo') || {};
        userInfo.points = newPoints;
        wx.setStorageSync('userInfo', userInfo);
        
        // 返回上一页
        setTimeout(() => {
          wx.navigateBack();
        }, 1500);
      } else {
        wx.showToast({
          title: '部分兑换失败',
          icon: 'none'
        });
      }
    }).catch(err => {
      wx.hideLoading();
      wx.showToast({
        title: '兑换失败',
        icon: 'none'
      });
    });
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  },

  // 分享
  onShareAppMessage() {
    const product = this.data.product;
    return {
      title: product ? `积分商城：${product.name}` : '积分商城',
      path: `/pages/points/mall/mall`
    };
  }
});