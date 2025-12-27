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
  gap: 28px;
  padding: 24px;
  background: linear-gradient(135deg, #f5f0e1 0%, #f8f4e6 100%);
  min-height: 100vh;
}

.metrics-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 24px;
}

.metric-card {
  background: linear-gradient(135deg, #fff8dc 0%, #f5f0e1 100%);
  padding: 28px 24px;
  border-radius: 30px;
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 3px solid #e8dccb;
  backdrop-filter: blur(6px);
  transition: all 0.3s ease-out;
  position: relative;
  overflow: hidden;
}

.metric-card:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: 0 12px 35px rgba(160, 82, 45, 0.15);
  border-color: #d2b48c;
}

.metric-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #ffc0cb, #a0522d);
  border-radius: 30px 30px 0 0;
}

.metric-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: #7a6a5b;
  font-size: 16px;
  margin-bottom: 20px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
  letter-spacing: 0.02em;
}

.metric-label {
  color: #4a3b30;
  font-weight: 600;
}

.metric-unit {
  background: rgba(160, 82, 45, 0.1);
  color: #a0522d;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.metric-value {
  font-size: 42px;
  font-weight: 700;
  color: #8b4513;
  margin-bottom: 20px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.05);
}

.metric-footer {
  font-size: 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
}

.compare-label {
  color: #7a6a5b;
  opacity: 0.8;
}

.compare-value {
  padding: 4px 12px;
  border-radius: 20px;
  font-weight: 600;
  font-size: 14px;
  transition: all 0.3s ease-out;
}

.compare-value.up {
  background: linear-gradient(135deg, rgba(44, 150, 120, 0.15) 0%, rgba(44, 150, 120, 0.08) 100%);
  color: #2c9678;
  border: 2px solid rgba(44, 150, 120, 0.3);
}

.compare-value.down {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.15) 0%, rgba(255, 107, 107, 0.08) 100%);
  color: #ff6b6b;
  border: 2px solid rgba(255, 107, 107, 0.3);
}

.card {
  background: linear-gradient(135deg, #fff8dc 0%, #f5f0e1 100%);
  border-radius: 30px;
  padding: 28px;
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 3px solid #e8dccb;
  backdrop-filter: blur(6px);
  transition: all 0.3s ease-out;
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 35px rgba(160, 82, 45, 0.15);
  border-color: #d2b48c;
}

.card-header {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 28px;
  color: #8b4513;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  padding-bottom: 16px;
  border-bottom: 3px dashed #d4c7b5;
  position: relative;
}

.card-header::after {
  content: '☕';
  position: absolute;
  right: 0;
  top: 0;
  font-size: 24px;
  color: #a0522d;
  opacity: 0.6;
}

.charts-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
}

.bar-chart {
  height: 280px;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  padding-top: 50px;
  position: relative;
}

.bar-chart::before {
  content: '';
  position: absolute;
  top: 50px;
  left: 0;
  right: 0;
  bottom: 40px;
  background: linear-gradient(135deg, rgba(222, 184, 135, 0.05) 0%, rgba(210, 180, 140, 0.03) 100%);
  border-radius: 20px;
  border: 2px dashed #d4c7b5;
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 16px;
  position: relative;
  z-index: 1;
}

.bar-wrapper {
  width: 44px;
  height: 200px;
  background: linear-gradient(135deg, rgba(222, 184, 135, 0.1) 0%, rgba(210, 180, 140, 0.05) 100%);
  border-radius: 22px;
  position: relative;
  display: flex;
  align-items: flex-end;
  border: 2px solid #e8dccb;
  overflow: hidden;
}

.bar {
  width: 100%;
  background: linear-gradient(to top, #a0522d 0%, #d2b48c 100%);
  border-radius: 22px;
  transition: all 0.4s ease-out;
  position: relative;
  cursor: pointer;
}

.bar:hover {
  transform: scale(1.05);
  background: linear-gradient(to top, #8b4513 0%, #deb887 100%);
}

.bar:hover .bar-tooltip {
  display: block;
  animation: bubbleUp 0.3s ease-out;
}

@keyframes bubbleUp {
  from {
    opacity: 0;
    transform: translateX(-50%) translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateX(-50%) translateY(0);
  }
}

.bar-tooltip {
  display: none;
  position: absolute;
  top: -42px;
  left: 50%;
  transform: translateX(-50%);
  background: linear-gradient(135deg, #8b4513, #a0522d);
  color: #fff8dc;
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  white-space: nowrap;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  box-shadow: 0 6px 20px rgba(139, 69, 19, 0.3);
  border: 2px solid #d2b48c;
  z-index: 10;
}

.bar-tooltip::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 50%;
  transform: translateX(-50%);
  border-left: 6px solid transparent;
  border-right: 6px solid transparent;
  border-top: 6px solid #8b4513;
}

.bar-label {
  font-size: 14px;
  color: #7a6a5b;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
  letter-spacing: 0.02em;
  background: rgba(255, 248, 220, 0.8);
  padding: 6px 12px;
  border-radius: 20px;
  border: 2px solid #e8dccb;
}

.line-chart-placeholder {
  height: 280px;
  position: relative;
  border-bottom: 3px dashed #d4c7b5;
  border-left: 3px dashed #d4c7b5;
  background: linear-gradient(135deg, rgba(245, 240, 225, 0.1) 0%, rgba(248, 244, 230, 0.05) 100%);
  border-radius: 0 0 0 20px;
  padding: 20px;
}

.line-point {
  position: absolute;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: all 0.3s ease-out;
}

.line-point:hover {
  transform: scale(1.2);
}

.point {
  width: 14px;
  height: 14px;
  background: linear-gradient(135deg, #ffc0cb, #a0522d);
  border-radius: 50%;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.4);
  border: 3px solid #fff8dc;
  cursor: pointer;
  transition: all 0.3s ease-out;
}

.point:hover {
  transform: scale(1.3);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.6);
}

.point-label {
  font-size: 12px;
  color: #8b4513;
  margin-top: 8px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  background: rgba(255, 248, 220, 0.9);
  padding: 4px 8px;
  border-radius: 15px;
  border: 2px solid #e8dccb;
  transform: rotate(-45deg);
  white-space: nowrap;
}

.bottom-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
}

.ranking-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.ranking-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.7);
  border: 2px solid #e8dccb;
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.ranking-item:hover {
  transform: translateX(8px) scale(1.02);
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.9) 0%, rgba(245, 240, 225, 0.7) 100%);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.1);
  border-color: #d2b48c;
}

.rank-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.rank-index {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, rgba(222, 184, 135, 0.2) 0%, rgba(210, 180, 140, 0.1) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  color: #7a6a5b;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 700;
  border: 3px solid #e8dccb;
  transition: all 0.3s ease-out;
}

.ranking-item:hover .rank-index {
  transform: scale(1.1);
  border-color: #a0522d;
}

.rank-index.top3 {
  background: linear-gradient(135deg, #ffc0cb 0%, #a0522d 100%);
  color: #fff8dc;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.4);
  border: 3px solid #fff8dc;
}

.rank-name {
  font-size: 16px;
  color: #4a3b30;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.rank-data {
  display: flex;
  gap: 24px;
  font-size: 16px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
}

.rank-count {
  color: #2c9678;
  background: rgba(44, 150, 120, 0.1);
  padding: 6px 16px;
  border-radius: 20px;
  border: 2px solid rgba(44, 150, 120, 0.3);
}

.rank-amount {
  color: #8b4513;
  background: rgba(139, 69, 19, 0.1);
  padding: 6px 16px;
  border-radius: 20px;
  border: 2px solid rgba(139, 69, 19, 0.3);
}

.alert-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.alert-item {
  display: flex;
  align-items: flex-start;
  gap: 20px;
  padding: 20px;
  border-radius: 25px;
  border: 3px solid;
  backdrop-filter: blur(4px);
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.alert-item:hover {
  transform: translateX(8px) scale(1.02);
  box-shadow: 0 8px 25px rgba(0, 0, 0, 0.1);
}

.alert-item.warning {
  background: linear-gradient(135deg, rgba(255, 192, 203, 0.2) 0%, rgba(255, 248, 220, 0.1) 100%);
  border-color: rgba(255, 192, 203, 0.5);
}

.alert-item.danger {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.2) 0%, rgba(255, 248, 220, 0.1) 100%);
  border-color: rgba(255, 107, 107, 0.5);
}

.alert-item i {
  font-size: 28px;
  color: #a0522d;
  background: rgba(255, 255, 255, 0.9);
  padding: 12px;
  border-radius: 50%;
  border: 3px solid #e8dccb;
}

.alert-item.warning i {
  color: #ffc0cb;
}

.alert-item.danger i {
  color: #ff6b6b;
}

.alert-content {
  flex: 1;
}

.alert-title {
  font-weight: 700;
  font-size: 16px;
  margin-bottom: 8px;
  color: #8b4513;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
}

.alert-item.warning .alert-title {
  color: #a0522d;
}

.alert-item.danger .alert-title {
  color: #ff6b6b;
}

.alert-desc {
  font-size: 14px;
  color: #7a6a5b;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  line-height: 1.5;
  opacity: 0.9;
}

.empty-alert {
  text-align: center;
  color: #d2b48c;
  padding: 80px 0;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-size: 16px;
  font-weight: 500;
  letter-spacing: 0.02em;
  opacity: 0.7;
}

.empty-alert::before {
  content: '🍵';
  font-size: 48px;
  display: block;
  margin-bottom: 16px;
  opacity: 0.5;
}
</style>