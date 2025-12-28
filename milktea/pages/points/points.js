const { pointsApi, authApi } = require('../../utils/api')

Page({
  data: {
    userPoints: 0,
    activeCategoryId: 'all',
    showRecords: false,
    loading: false,
    exchangeRecords: [],
    categories: [
      { id: 'all', name: '全部' }
    ],
    productList: []
  },

  onLoad() {
    this.loadData()
  },

  async loadData() {
    this.setData({ loading: true })
    try {
      const [profileRes, categoriesRes] = await Promise.all([
        authApi.getUserProfile(),
        pointsApi.getPointsCategories()
      ])
      
      const userData = profileRes.data || profileRes
      if (userData) {
        this.setData({ userPoints: userData.points || 0 })
      }

      const categoriesData = categoriesRes.data || categoriesRes
      if (Array.isArray(categoriesData)) {
        this.setData({
          categories: [
            { id: 'all', name: '全部' },
            ...categoriesData.map(c => ({
              id: c.type.toLowerCase(),
              name: c.name
            }))
          ]
        })
      }
      
      await this.fetchProducts()
    } catch (error) {
      console.error('加载积分商城数据失败:', error)
    } finally {
      this.setData({ loading: false })
    }
  },

  async fetchProducts() {
    try {
      const category = this.data.activeCategoryId === 'all' ? null : this.data.activeCategoryId.toUpperCase()
      const res = await pointsApi.getPointsProducts(1, 50, category)
      const data = res.data || res
      if (data) {
        this.setData({
          productList: data.list || data || []
        })
      }
    } catch (error) {
      console.error('获取商品列表失败:', error)
    }
  },

  selectCategory(e) {
    const { id } = e.currentTarget.dataset
    this.setData({ activeCategoryId: id }, () => {
      this.fetchProducts()
    })
  },

  async toggleRecords() {
    const showRecords = !this.data.showRecords
    this.setData({ showRecords })
    
    if (showRecords) {
      try {
        const res = await pointsApi.getExchangeRecords()
        const data = res.data || res
        if (Array.isArray(data)) {
          this.setData({ exchangeRecords: data })
        }
      } catch (error) {
        console.error('加载兑换记录失败:', error)
      }
    }
  },

  goToRules() {
    wx.showModal({
      title: '积分规则',
      content: '1. 消费1元获得1积分\n2. 每日签到获得10积分\n3. 积分可用于兑换商品或优惠券\n4. 积分永久有效',
      showCancel: false
    })
  },

  async exchangeProduct(e) {
    const { product } = e.currentTarget.dataset
    const points = product.points || product.pointCost || 0
    
    if (this.data.userPoints < points) {
      wx.showToast({ title: '积分不足', icon: 'none' })
      return
    }
    
    wx.showModal({
      title: '兑换确认',
      content: `确定花费${points}积分兑换${product.name}吗？`,
      success: async (res) => {
        if (res.confirm) {
          try {
            await pointsApi.exchangeProduct(product.id)
            wx.showToast({ title: '兑换成功！', icon: 'success' })
            this.loadData() // 重新加载数据以更新积分和库存
          } catch (error) {
            console.error('兑换失败:', error)
          }
        }
      }
    })
  }
})