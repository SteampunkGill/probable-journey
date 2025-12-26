<template>
  <div class="dashboard-container">
    <!-- 今日关键指标 -->
    <div class="metrics-grid">
      <div class="metric-card" v-for="(val, key) in metricsConfig" :key="key">
        <div class="metric-header">
          <span class="metric-label">{{ val.label }}</span>
          <span class="metric-unit">{{ val.unit }}</span>
        </div>
        <div class="metric-value">{{ metrics[key] || 0 }}</div>
        <div class="metric-footer">
          <span class="compare-label">较昨日</span>
          <span class="compare-value" :class="getTrendClass(metrics.comparedToYesterday?.[key])">
            {{ metrics.comparedToYesterday?.[key] || '0%' }}
          </span>
        </div>
      </div>
    </div>

    <div class="charts-row">
      <!-- 实时销售趋势 -->
      <div class="chart-card card">
        <div class="card-header">实时销售趋势 (最近8小时)</div>
        <div class="bar-chart">
          <div v-for="(val, index) in realtimeSales.salesData" :key="index" class="bar-item">
            <div class="bar-wrapper">
              <div class="bar" :style="{ height: (val / maxSales * 100 || 0) + '%' }">
                <div class="bar-tooltip">¥{{ val }}</div>
              </div>
            </div>
            <div class="bar-label">{{ realtimeSales.timeLabels[index] }}</div>
          </div>
        </div>
      </div>

      <!-- 销售趋势分析 -->
      <div class="chart-card card">
        <div class="card-header">近7日销售趋势</div>
        <div class="line-chart-placeholder">
          <div v-for="(val, index) in salesTrend.sales" :key="index" class="line-point" :style="{ left: (index * 15) + '%', bottom: (val / maxTrendSales * 100 || 0) + '%' }">
            <div class="point"></div>
            <div class="point-label">{{ salesTrend.dates[index] }}</div>
          </div>
        </div>
      </div>
    </div>

    <div class="bottom-row">
      <!-- 商品销售排行 -->
      <div class="ranking-card card">
        <div class="card-header">商品销售排行 (本月)</div>
        <div class="ranking-list">
          <div v-for="(item, index) in productRanking" :key="item.productId" class="ranking-item">
            <div class="rank-info">
              <span class="rank-index" :class="{ top3: index < 3 }">{{ index + 1 }}</span>
              <span class="rank-name">{{ item.productName }}</span>
            </div>
            <div class="rank-data">
              <span class="rank-count">{{ item.salesCount }} 杯</span>
              <span class="rank-amount">¥{{ item.salesAmount }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 预警系统 -->
      <div class="alert-card card">
        <div class="card-header">系统预警</div>
        <div class="alert-list">
          <div v-if="inventoryAlerts.length === 0 && orderAlerts.length === 0" class="empty-alert">
            暂无预警信息
          </div>
          <div v-for="alert in inventoryAlerts" :key="alert.productId" class="alert-item warning">
            <i class="iconfont icon-inventory"></i>
            <div class="alert-content">
              <div class="alert-title">库存预警</div>
              <div class="alert-desc">{{ alert.productName }}({{ alert.specName }}) 当前库存 {{ alert.currentStock }}，低于阈值 {{ alert.threshold }}</div>
            </div>
          </div>
          <div v-for="alert in orderAlerts" :key="alert.orderId" class="alert-item danger">
            <i class="iconfont icon-order"></i>
            <div class="alert-content">
              <div class="alert-title">异常订单</div>
              <div class="alert-desc">订单 {{ alert.orderNo }} 制作超时，请尽快处理</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { get } from '../../../utils/request'

const metrics = ref({})
const realtimeSales = ref({ timeLabels: [], salesData: [], orderData: [] })
const salesTrend = ref({ dates: [], sales: [], orders: [] })
const productRanking = ref([])
const inventoryAlerts = ref([])
const orderAlerts = ref([])

const metricsConfig = {
  orderCount: { label: '今日订单数', unit: '单' },
  salesAmount: { label: '今日销售额', unit: '元' },
  customerUnitPrice: { label: '客单价', unit: '元' },
  newUsers: { label: '新增用户', unit: '人' }
}

const maxSales = computed(() => Math.max(...realtimeSales.value.salesData, 100))
const maxTrendSales = computed(() => Math.max(...salesTrend.value.sales, 100))

const getTrendClass = (trend) => {
  if (!trend) return ''
  return trend.startsWith('+') ? 'up' : 'down'
}

const loadData = async () => {
  try {
    const [metricsRes, realtimeRes, trendRes, rankingRes, invAlertRes, orderAlertRes] = await Promise.all([
      get('/api/admin/dashboard/today-metrics'),
      get('/api/admin/dashboard/realtime-sales'),
      get('/api/admin/dashboard/sales-trend'),
      get('/api/admin/dashboard/product-ranking'),
      get('/api/admin/dashboard/inventory-alerts'),
      get('/api/admin/dashboard/order-alerts')
    ])

    metrics.value = metricsRes.data || {}
    realtimeSales.value = realtimeRes.data || { timeLabels: [], salesData: [], orderData: [] }
    salesTrend.value = trendRes.data || { dates: [], sales: [], orders: [] }
    productRanking.value = rankingRes.data || []
    inventoryAlerts.value = invAlertRes.data || []
    orderAlerts.value = orderAlertRes.data || []
  } catch (error) {
    console.error('加载仪表盘数据失败:', error)
  }
}

onMounted(() => {
  loadData()
  // 每分钟刷新一次实时数据
  setInterval(loadData, 60000)
})
</script>

<style scoped>
.dashboard-container {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.metric-card {
  background: white;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.metric-header {
  display: flex;
  justify-content: space-between;
  color: #8c8c8c;
  font-size: 14px;
  margin-bottom: 12px;
}

.metric-value {
  font-size: 30px;
  font-weight: bold;
  color: #262626;
  margin-bottom: 12px;
}

.metric-footer {
  font-size: 14px;
  display: flex;
  gap: 8px;
}

.compare-label { color: #8c8c8c; }
.compare-value.up { color: #52c41a; }
.compare-value.down { color: #f5222d; }

.card {
  background: white;
  border-radius: 8px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.card-header {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #262626;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.bar-chart {
  height: 200px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding-top: 30px;
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
}

.bar-wrapper {
  width: 30px;
  height: 150px;
  background: #f5f5f5;
  border-radius: 4px;
  position: relative;
  display: flex;
  align-items: flex-end;
}

.bar {
  width: 100%;
  background: #1890ff;
  border-radius: 4px;
  transition: height 0.3s;
  position: relative;
}

.bar:hover .bar-tooltip {
  display: block;
}

.bar-tooltip {
  display: none;
  position: absolute;
  top: -25px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0,0,0,0.7);
  color: white;
  padding: 2px 6px;
  border-radius: 4px;
  font-size: 12px;
  white-space: nowrap;
}

.bar-label {
  font-size: 12px;
  color: #8c8c8c;
}

.line-chart-placeholder {
  height: 200px;
  position: relative;
  border-bottom: 1px solid #f0f0f0;
  border-left: 1px solid #f0f0f0;
}

.line-point {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.point {
  width: 8px;
  height: 8px;
  background: #1890ff;
  border-radius: 50%;
}

.point-label {
  font-size: 10px;
  color: #8c8c8c;
  margin-top: 4px;
  transform: rotate(-45deg);
}

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 24px;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.ranking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 0;
  border-bottom: 1px solid #f5f5f5;
}

.rank-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.rank-index {
  width: 20px;
  height: 20px;
  background: #f5f5f5;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
}

.rank-index.top3 {
  background: #1890ff;
  color: white;
}

.rank-name { font-size: 14px; }
.rank-data { display: flex; gap: 16px; font-size: 14px; }
.rank-count { color: #8c8c8c; }
.rank-amount { font-weight: bold; color: #f5222d; }

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.alert-item {
  display: flex;
  gap: 12px;
  padding: 12px;
  border-radius: 4px;
}

.alert-item.warning { background: #fff7e6; border: 1px solid #ffe7ba; }
.alert-item.danger { background: #fff1f0; border: 1px solid #ffa39e; }

.alert-title { font-weight: bold; font-size: 14px; margin-bottom: 4px; }
.alert-desc { font-size: 12px; color: #595959; }
.empty-alert { text-align: center; color: #bfbfbf; padding: 40px 0; }
</style>