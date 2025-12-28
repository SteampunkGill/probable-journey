import { get } from '../../../utils/request'

Page({
  data: {
    metrics: {},
    trends: {},
    realtimeSales: { timeLabels: [], salesData: [], orderData: [] },
    salesTrend: { dates: [], sales: [], orders: [] },
    productRanking: [],
    inventoryAlerts: [],
    orderAlerts: [],
    maxSales: 100,
    maxTrendSales: 100,
    metricsConfig: {
      orderCount: { label: '今日订单数', unit: '单' },
      salesAmount: { label: '今日销售额', unit: '元' },
      customerUnitPrice: { label: '客单价', unit: '元' },
      newUsers: { label: '新增用户', unit: '人' }
    }
  },

  onLoad() {
    this.loadData()
    // 每分钟刷新一次
    this.timer = setInterval(() => {
      this.loadData()
    }, 60000)
  },

  onUnload() {
    if (this.timer) clearInterval(this.timer)
  },

  async loadData() {
    try {
      const [metricsRes, realtimeRes, trendRes, rankingRes, invAlertRes, orderAlertRes] = await Promise.all([
        get('/api/admin/dashboard/today-metrics'),
        get('/api/admin/dashboard/realtime-sales'),
        get('/api/admin/dashboard/sales-trend'),
        get('/api/admin/dashboard/product-ranking'),
        get('/api/admin/dashboard/inventory-alerts'),
        get('/api/admin/dashboard/order-alerts')
      ])

      const metrics = metricsRes.data || {}
      const realtimeSales = realtimeRes.data || { timeLabels: [], salesData: [], orderData: [] }
      const salesTrend = trendRes.data || { dates: [], sales: [], orders: [] }
      
      // 处理趋势样式
      const trends = {}
      if (metrics.comparedToYesterday) {
        Object.keys(metrics.comparedToYesterday).forEach(key => {
          const val = metrics.comparedToYesterday[key]
          trends[key] = {
            class: val && val.startsWith('+') ? 'up' : 'down'
          }
        })
      }

      this.setData({
        metrics,
        trends,
        realtimeSales,
        salesTrend,
        productRanking: rankingRes.data || [],
        inventoryAlerts: invAlertRes.data || [],
        orderAlerts: orderAlertRes.data || [],
        maxSales: Math.max(...(realtimeSales.salesData || []), 100),
        maxTrendSales: Math.max(...(salesTrend.sales || []), 100)
      })
    } catch (error) {
      console.error('加载仪表盘数据失败:', error)
    }
  },

  onPullDownRefresh() {
    this.loadData().then(() => {
      wx.stopPullDownRefresh()
    })
  }
})