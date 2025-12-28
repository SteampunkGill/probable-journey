const { pointsApi, authApi } = require('../../utils/api')

Page({
  data: {
    points: 0,
    searchKeyword: '',
    loading: false,
    categories: [
      { id: 'all', name: '全部', active: true }
    ],
    sortOptions: [
      { id: 1, name: '积分从低到高', active: true },
      { id: 2, name: '积分从高到低', active: false },
      { id: 3, name: '最新上架', active: false },
      { id: 4, name: '热门推荐', active: false }
    ],
    products: [],
    filteredProducts: []
  },

  onLoad() {
    this.loadInitialData()
  },

  async loadInitialData() {
    this.setData({ loading: true })
    try {
      // 获取积分
      const userProfile = await authApi.getUserProfile()
      const userData = userProfile.data || userProfile
      if (userData) {
        this.setData({ points: userData.points || 0 })
      }

      // 获取分类
      const categoriesRes = await pointsApi.getPointsCategories()
      const categoriesData = categoriesRes.data || categoriesRes
      if (Array.isArray(categoriesData)) {
        const newCategories = [
          { id: 'all', name: '全部', active: true },
          ...categoriesData.map(c => ({
            id: c.type.toLowerCase(),
            name: c.name,
            active: false
          }))
        ]
        this.setData({ categories: newCategories })
      }
      
      await this.loadProducts()
    } catch (error) {
      console.error('加载初始数据失败:', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  async loadProducts() {
    try {
      const activeCategory = this.data.categories.find(c => c.active)
      const category = activeCategory && activeCategory.id !== 'all' ? activeCategory.id.toUpperCase() : null
      const res = await pointsApi.getPointsProducts(1, 50, category)
      const data = res.data || res
      if (data) {
        const products = data.list || data || []
        this.setData({ products }, () => {
          this.filterAndSortProducts()
        })
      }
    } catch (error) {
      console.error('加载积分商品失败:', error)
    }
  },

  onSearchInput(e) {
    this.setData({ searchKeyword: e.detail.value }, () => {
      this.filterAndSortProducts()
    })
  },

  selectCategory(e) {
    const { index } = e.currentTarget.dataset
    const categories = this.data.categories.map((c, i) => ({
      ...c,
      active: i === index
    }))
    this.setData({ categories }, () => {
      this.loadProducts()
    })
  },

  selectSort(e) {
    const { index } = e.currentTarget.dataset
    const sortOptions = this.data.sortOptions.map((s, i) => ({
      ...s,
      active: i === index
    }))
    this.setData({ sortOptions }, () => {
      this.filterAndSortProducts()
    })
  },

  filterAndSortProducts() {
    let filtered = [...this.data.products]
    const { searchKeyword, sortOptions } = this.data
    
    // 搜索过滤
    if (searchKeyword) {
      filtered = filtered.filter(product =>
        product.name.includes(searchKeyword) ||
        (product.description || product.desc || '').includes(searchKeyword)
      )
    }
    
    // 排序
    const activeSort = sortOptions.find(s => s.active)
    if (activeSort) {
      switch (activeSort.name) {
        case '积分从低到高':
          filtered.sort((a, b) => (a.points || a.pointCost || 0) - (b.points || b.pointCost || 0))
          break
        case '积分从高到低':
          filtered.sort((a, b) => (b.points || b.pointCost || 0) - (a.points || a.pointCost || 0))
          break
        case '最新上架':
          filtered.sort((a, b) => b.id - a.id)
          break
        case '热门推荐':
          filtered.sort((a, b) => (b.hot ? 1 : 0) - (a.hot ? 1 : 0))
          break
      }
    }
    
    this.setData({ filteredProducts: filtered })
  },

  async signIn() {
    try {
      const res = await pointsApi.signIn()
      const newPoints = res.data || res
      if (typeof newPoints === 'number') {
        this.setData({ points: newPoints })
        wx.showToast({ title: '签到成功！+10积分', icon: 'success' })
      }
    } catch (error) {
      console.error('签到失败:', error)
      wx.showToast({ title: '签到失败', icon: 'none' })
    }
  },

  viewTasks() {
    wx.showToast({ title: '功能开发中', icon: 'none' })
  },

  viewExchangeRecords() {
    wx.navigateTo({ url: '/pages/points/exchange-records' })
  },

  howToGetPoints() {
    wx.showModal({
      title: '如何获取积分',
      content: '1. 每日签到\n2. 消费获得\n3. 完成任务\n4. 邀请好友',
      showCancel: false
    })
  },

  viewProductDetail(e) {
    const { id } = e.currentTarget.dataset
    wx.showToast({ title: '查看详情: ' + id, icon: 'none' })
  },

  async exchangeProduct(e) {
    const { product } = e.currentTarget.dataset
    const productPoints = product.points || product.pointCost || 0
    
    if (this.data.points < productPoints) {
      wx.showToast({ title: '积分不足', icon: 'none' })
      return
    }
    
    wx.showModal({
      title: '兑换确认',
      content: `确定要兑换【${product.name}】吗？\n需要${productPoints}积分`,
      success: async (res) => {
        if (res.confirm) {
          try {
            this.setData({ loading: true })
            await pointsApi.exchangeProduct(product.id)
            wx.showToast({ title: '兑换成功！', icon: 'success' })
            this.loadInitialData()
          } catch (error) {
            console.error('兑换失败:', error)
          } finally {
            this.setData({ loading: false })
          }
        }
      }
    })
  }
})