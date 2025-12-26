// pages/points/mall/mall.js
const { pointsMallApi } = require('../../../utils/api.js');
const { get } = require('../../../utils/request.js');

Page({
  data: {
    points: 0,
    loading: true,
    categories: [
      { id: 0, name: '全部', active: true },
      { id: 1, name: '热门商品', active: false },
      { id: 2, name: '优惠券', active: false },
      { id: 3, name: '实物礼品', active: false },
      { id: 4, name: '饮品券', active: false }
    ],
    sortOptions: [
      { id: 0, name: '默认排序', active: true },
      { id: 1, name: '积分从低到高', active: false },
      { id: 2, name: '积分从高到低', active: false },
      { id: 3, name: '热门优先', active: false },
      { id: 4, name: '库存充足', active: false }
    ],
    products: [],
    filteredProducts: [],
    searchKeyword: '',
    selectedCategory: 0,
    selectedSort: 0
  },

  onLoad() {
    this.loadUserData();
    this.loadProducts();
    this.loadCategories();
  },

  onShow() {
    this.loadUserData();
  },

  // 下拉刷新
  onPullDownRefresh() {
    this.loadUserData();
    this.loadProducts();
    setTimeout(() => {
      wx.stopPullDownRefresh();
    }, 1000);
  },

  // 加载用户数据
  loadUserData() {
    // 从本地存储获取用户积分
    const userInfo = wx.getStorageSync('userInfo');
    if (userInfo && userInfo.points) {
      this.setData({
        points: userInfo.points
      });
    } else {
      // 如果没有用户信息，尝试获取
      get('/user/profile').then(res => {
        if (res.code === 200 && res.data) {
          this.setData({
            points: res.data.points || 0
          });
          wx.setStorageSync('userInfo', res.data);
        }
      }).catch(() => {
        this.setData({
          points: 0
        });
      });
    }
  },

  // 加载分类
  loadCategories() {
    pointsMallApi.getCategories().then(res => {
      if (res.code === 200 && res.data) {
        const categories = [{ id: 0, name: '全部', active: true }];
        res.data.forEach((cat, index) => {
          categories.push({
            id: cat.id || index + 1,
            name: cat.name,
            active: false
          });
        });
        this.setData({ categories });
      }
    }).catch(() => {
      // 如果API失败，使用默认分类
    });
  },

  // 加载商品数据
  loadProducts() {
    this.setData({ loading: true });
    
    const category = this.data.selectedCategory > 0 ? this.data.selectedCategory : null;
    
    pointsMallApi.getProducts(category).then(res => {
      if (res.code === 200 && res.data) {
        const products = res.data.map(item => ({
          id: item.id,
          name: item.name,
          desc: item.description || item.limitInfo || '积分兑换商品',
          points: item.pointCost || item.points,
          originalPoints: item.originalPoints || Math.round((item.pointCost || item.points) * 1.2),
          stock: item.stock || 0,
          limit: item.limitInfo || '每人限兑5件',
          hot: item.hot || false,
          image: item.imageUrl || item.image || '/images/products/default.jpg',
          status: item.status || 'AVAILABLE'
        }));
        
        this.setData({
          products: products,
          filteredProducts: products,
          loading: false
        });
      } else {
        this.loadMockProducts();
      }
    }).catch(() => {
      this.loadMockProducts();
    });
  },

  // 加载模拟数据（备用）
  loadMockProducts() {
    const mockProducts = [
      {
        id: 1,
        name: '5元优惠券',
        desc: '全场通用，无门槛使用',
        points: 100,
        originalPoints: 150,
        stock: 50,
        limit: '每人限兑3张',
        hot: true,
        image: '/images/products/coupon1.jpg'
      },
      {
        id: 2,
        name: '奶茶买一送一券',
        desc: '经典奶茶系列买一送一',
        points: 200,
        originalPoints: 300,
        stock: 30,
        limit: '每人限兑1张',
        hot: true,
        image: '/images/products/coupon2.jpg'
      },
      {
        id: 3,
        name: '定制保温杯',
        desc: '品牌联名款保温杯',
        points: 500,
        originalPoints: 800,
        stock: 15,
        limit: '每人限兑1个',
        hot: false,
        image: '/images/products/mug.jpg'
      },
      {
        id: 4,
        name: '免配送费券',
        desc: '免去一次配送费用',
        points: 80,
        originalPoints: 100,
        stock: 100,
        limit: '每人限兑5张',
        hot: true,
        image: '/images/products/delivery.jpg'
      },
      {
        id: 5,
        name: '第二杯半价券',
        desc: '指定饮品第二杯半价',
        points: 150,
        originalPoints: 200,
        stock: 40,
        limit: '每人限兑2张',
        hot: false,
        image: '/images/products/halfprice.jpg'
      },
      {
        id: 6,
        name: '品牌帆布袋',
        desc: '环保帆布袋，时尚实用',
        points: 300,
        originalPoints: 450,
        stock: 20,
        limit: '每人限兑1个',
        hot: false,
        image: '/images/products/bag.jpg'
      }
    ];
    
    this.setData({
      products: mockProducts,
      filteredProducts: mockProducts,
      loading: false
    });
  },

  // 搜索商品
  searchProducts(e) {
    const keyword = e.detail.value;
    this.setData({ searchKeyword: keyword });
    this.filterProducts();
  },

  // 选择分类
  selectCategory(e) {
    const index = e.currentTarget.dataset.index;
    const categories = this.data.categories.map((item, i) => ({
      ...item,
      active: i === index
    }));
    
    this.setData({
      categories,
      selectedCategory: index
    });
    
    // 重新加载商品
    this.loadProducts();
  },

  // 选择排序
  selectSort(e) {
    const index = e.currentTarget.dataset.index;
    const sortOptions = this.data.sortOptions.map((item, i) => ({
      ...item,
      active: i === index
    }));
    
    this.setData({
      sortOptions,
      selectedSort: index
    });
    
    this.filterProducts();
  },

  // 筛选商品
  filterProducts() {
    let filtered = [...this.data.products];
    
    // 按搜索关键词筛选
    if (this.data.searchKeyword) {
      const keyword = this.data.searchKeyword.toLowerCase();
      filtered = filtered.filter(item => 
        item.name.toLowerCase().includes(keyword) || 
        item.desc.toLowerCase().includes(keyword)
      );
    }
    
    // 排序
    switch (this.data.selectedSort) {
      case 1: // 积分从低到高
        filtered.sort((a, b) => a.points - b.points);
        break;
      case 2: // 积分从高到低
        filtered.sort((a, b) => b.points - a.points);
        break;
      case 3: // 热门优先
        filtered.sort((a, b) => (b.hot ? 1 : 0) - (a.hot ? 1 : 0));
        break;
      case 4: // 库存充足
        filtered.sort((a, b) => b.stock - a.stock);
        break;
      default: // 默认排序
        filtered.sort((a, b) => a.id - b.id);
    }
    
    this.setData({ filteredProducts: filtered });
  },

  // 查看商品详情
  viewProductDetail(e) {
    const id = e.currentTarget.dataset.id;
    wx.navigateTo({
      url: `/pages/points/product-detail/product-detail?id=${id}`
    });
  },

  // 兑换商品
  exchangeProduct(e) {
    const id = e.currentTarget.dataset.id;
    const product = this.data.products.find(p => p.id === id);
    
    if (!product) return;
    
    if (product.stock <= 0) {
      wx.showToast({
        title: '库存不足',
        icon: 'none'
      });
      return;
    }
    
    if (this.data.points < product.points) {
      wx.showToast({
        title: '积分不足',
        icon: 'none'
      });
      return;
    }
    
    wx.showModal({
      title: '确认兑换',
      content: `确定要兑换【${product.name}】吗？\n需要${product.points}积分`,
      success: (res) => {
        if (res.confirm) {
          this.doExchangeProduct(id);
        }
      }
    });
  },

  // 执行兑换
  doExchangeProduct(productId) {
    wx.showLoading({ title: '兑换中...' });
    
    pointsMallApi.exchangeProduct(productId).then(res => {
      wx.hideLoading();
      
      if (res.code === 200) {
        wx.showToast({
          title: '兑换成功',
          icon: 'success'
        });
        
        // 更新用户积分
        const product = this.data.products.find(p => p.id === productId);
        if (product) {
          const newPoints = this.data.points - product.points;
          this.setData({ points: newPoints });
          
          // 更新本地存储的用户信息
          const userInfo = wx.getStorageSync('userInfo') || {};
          userInfo.points = newPoints;
          wx.setStorageSync('userInfo', userInfo);
          
          // 更新商品库存
          const products = this.data.products.map(p => {
            if (p.id === productId) {
              return { ...p, stock: p.stock - 1 };
            }
            return p;
          });
          
          this.setData({
            products,
            filteredProducts: products
          });
        }
      } else {
        wx.showToast({
          title: res.message || '兑换失败',
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

  // 查看兑换记录
  viewExchangeRecords() {
    wx.navigateTo({
      url: '/pages/points/records/records'
    });
  },

  // 如何获取积分
  howToGetPoints() {
    wx.showModal({
      title: '如何获取积分',
      content: '1. 每日签到可获得积分\n2. 消费1元获得1积分\n3. 完成积分任务\n4. 邀请好友注册\n5. 评价订单',
      showCancel: false
    });
  },

  // 返回上一页
  goBack() {
    wx.navigateBack();
  }
});