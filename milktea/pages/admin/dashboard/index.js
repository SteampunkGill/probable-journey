const { get } = require('../../../utils/request');

Page({
  data: {
    metrics: {},
    realtimeSales: { timeLabels: [], salesData: [], orderData: [] },
    salesTrend: { dates: [], sales: [], orders: [] },
    productRanking: [],
    inventoryAlerts: [],
    orderAlerts: [],
    metricsConfig: {
      orderCount: { label: '今日订单数', unit: '单' },
      salesAmount: { label: '今日销售额', unit: '元' },
      customerUnitPrice: { label: '客单价', unit: '元' },
      newUsers: { label: '新增用户', unit: '人' }
    },
    maxSales: 100,
    maxTrendSales: 100
  },

  onLoad() {
    this.loadData();
    // 每分钟刷新一次实时数据
    this.refreshTimer = setInterval(() => {
      this.loadData();
    }, 60000);
  },

  onUnload() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer);
    }
  },

  async loadData() {
    try {
      const [metricsRes, realtimeRes, trendRes, rankingRes, invAlertRes, orderAlertRes] = await Promise.all([
        get('/admin/dashboard/today-metrics'),
        get('/admin/dashboard/realtime-sales'),
        get('/admin/dashboard/sales-trend'),
        get('/admin/dashboard/product-ranking'),
        get('/admin/dashboard/inventory-alerts'),
        get('/admin/dashboard/order-alerts')
      ]);

      const realtimeSales = realtimeRes.data || { timeLabels: [], salesData: [], orderData: [] };
      const salesTrend = trendRes.data || { dates: [], sales: [], orders: [] };

      this.setData({
        metrics: metricsRes.data || {},
        realtimeSales,
        salesTrend,
        productRanking: rankingRes.data || [],
        inventoryAlerts: invAlertRes.data || [],
        orderAlerts: orderAlertRes.data || [],
        maxSales: Math.max(...(realtimeSales.salesData || []), 100),
        maxTrendSales: Math.max(...(salesTrend.sales || []), 100)
      });
    } catch (error) {
      console.error('加载仪表盘数据失败:', error);
    }
  }
});