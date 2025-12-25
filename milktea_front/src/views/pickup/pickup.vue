<template>
  <div class="pickup-page">
    <!-- 加载状态 -->
    <div class="loading" v-if="loading">
      <div class="loading-icon"></div>
      <span>加载中...</span>
    </div>

    <div v-else>
      <!-- 待取餐订单 -->
      <div class="pending-section" v-if="pendingOrders.length > 0">
        <!-- 取餐码大卡片 -->
        <div class="pickup-card" v-if="activeOrder">
          <div class="card-header">
            <div class="status-badge" :class="activeOrder.status">{{ activeOrder.statusText }}</div>
            <span class="order-no">{{ activeOrder.orderNo }}</span>
          </div>

          <div class="pickup-code-section">
            <span class="label">取餐码</span>
            <span class="pickup-code">{{ activeOrder.pickupCode }}</span>
            <span class="hint">请出示此码给店员</span>
          </div>

          <div class="order-info">
            <div class="info-item">
              <span class="icon">🏪</span>
              <div class="info-detail">
                <span class="store-name">{{ activeOrder.store.name }}</span>
                <span class="store-address">{{ activeOrder.store.address }}</span>
              </div>
            </div>
            
            <div class="info-item" v-if="activeOrder.status === 'ready'">
              <span class="icon">⏰</span>
              <div class="info-detail">
                <span class="time-label">已备餐</span>
                <span class="time-value">请尽快前往取餐</span>
              </div>
            </div>

            <div class="info-item" v-if="activeOrder.status === 'processing'">
              <span class="icon">⏰</span>
              <div class="info-detail">
                <span class="time-label">预计完成</span>
                <span class="time-value">{{ activeOrder.estimatedTime }}</span>
              </div>
            </div>
          </div>

          <div class="order-items">
            <div class="items-header">
              <span>商品清单</span>
              <span class="item-count">共{{ activeOrder.itemCount }}件</span>
            </div>
            <div class="item" v-for="item in activeOrder.items" :key="item.id">
              <img class="item-image" :src="item.image" />
              <div class="item-info">
                <span class="item-name">{{ item.name }}</span>
                <span class="item-specs" v-if="item.customizations">{{ item.customizations }}</span>
              </div>
              <span class="item-quantity">×{{ item.quantity }}</span>
            </div>
          </div>

          <div class="card-actions">
            <button class="action-btn" @click="callStore(activeOrder.store.phone)">
              <span class="icon">📞</span>
              <span>联系门店</span>
            </button>
            <button class="action-btn" v-if="activeOrder.status === 'processing'" @click="remindOrder(activeOrder.id)">
              <span class="icon">⏰</span>
              <span>催单</span>
            </button>
            <button class="action-btn primary" @click="goToOrderDetail(activeOrder.id)">
              <span>查看详情</span>
            </button>
          </div>
        </div>

        <!-- 其他待取餐订单 -->
        <div class="other-orders" v-if="pendingOrders.length > 1">
          <div class="section-title">其他待取餐订单</div>
          <div class="order-list">
            <div class="order-item" 
                 :class="{ active: activeOrder?.id === item.id }"
                 v-for="item in pendingOrders" 
                 :key="item.id"
                 @click="selectOrder(item)">
              <div class="order-header">
                <span class="pickup-code-small">{{ item.pickupCode }}</span>
                <span class="status">{{ item.statusText }}</span>
              </div>
              <span class="order-no">{{ item.orderNo }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 扫码取餐 -->
      <div class="scan-section">
        <button class="scan-btn" @click="scanToPickup">
          <span class="icon">📷</span>
          <span class="text">扫码取餐</span>
        </button>
        <span class="scan-hint">扫描门店二维码快速取餐</span>
      </div>

      <!-- 历史订单 -->
      <div class="history-section" v-if="historyOrders.length > 0">
        <div class="section-title">历史取餐记录</div>
        <div class="history-list">
          <div class="history-item" v-for="item in historyOrders" :key="item.id" @click="goToOrderDetail(item.id)">
            <div class="history-left">
              <span class="pickup-code-text">{{ item.pickupCode }}</span>
              <span class="order-no-text">{{ item.orderNo }}</span>
              <span class="picked-time">{{ item.pickedTime }}</span>
            </div>
            <div class="history-right">
              <span class="amount">¥{{ item.totalAmount }}</span>
              <span class="arrow">›</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="pendingOrders.length === 0 && historyOrders.length === 0">
        <img class="empty-icon" src="../../assets/images/icons/pick_up_food.png" />
        <span class="empty-text">暂无取餐订单</span>
        <button class="go-order-btn" @click="goToOrder">去点单</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../../utils/api'

const router = useRouter()

const pendingOrders = ref([])
const historyOrders = ref([])
const activeOrder = ref(null)
const loading = ref(true)

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderList()
    if (res.code === 200) {
      const orders = res.data.list || res.data || []
      // 待取餐状态：PAID(已支付), ACCEPTED(已接单), MAKING(制作中), READY(待取餐)
      const pendingStatuses = ['PAID', 'ACCEPTED', 'MAKING', 'READY']
      pendingOrders.value = orders.filter(o => pendingStatuses.includes(o.status))
      // 已取餐状态：DELIVERED, FINISHED
      const historyStatuses = ['DELIVERED', 'FINISHED']
      historyOrders.value = orders.filter(o => historyStatuses.includes(o.status))
      
      if (pendingOrders.value.length > 0) {
        activeOrder.value = pendingOrders.value[0]
      }
    }
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const selectOrder = (order) => {
  activeOrder.value = order
}

const scanToPickup = () => {
  alert('Web端暂不支持原生扫码，请手动输入取餐码')
  const code = prompt('请输入取餐码 (例如: A123)')
  if (code) {
    alert(`正在验证取餐码: ${code}`)
  }
}

const goToOrderDetail = (id) => {
  router.push(`/order-detail/${id}`)
}

const callStore = (phone) => {
  window.location.href = `tel:${phone}`
}

const remindOrder = async (id) => {
  try {
    const res = await orderApi.remindOrder(id)
    if (res.code === 200) {
      alert(res.data?.message || '已提醒商家尽快制作')
    } else {
      alert(res.message || '催单失败')
    }
  } catch (error) {
    console.error('催单失败:', error)
  }
}

const goToOrder = () => {
  router.push('/')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.pickup-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 20px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 100px 0;
  color: #999;
}

.loading-icon {
  width: 30px;
  height: 30px;
  border: 2px solid #E0E0E0;
  border-top-color: #D4A574;
  border-radius: 50%;
  animation: rotate 1s linear infinite;
  margin-bottom: 10px;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

.pickup-card {
  margin: 10px;
  background: linear-gradient(135deg, #D4A574, #B08968);
  border-radius: 12px;
  padding: 20px;
  color: white;
  box-shadow: 0 4px 15px rgba(212, 165, 116, 0.3);
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.status-badge {
  padding: 4px 10px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 25px;
  font-size: 12px;
  font-weight: bold;
}

.status-badge.ready {
  background: #52C41A;
}

.status-badge.processing {
  background: #FFA940;
}

.order-no {
  font-size: 12px;
  opacity: 0.9;
}

.pickup-code-section {
  text-align: center;
  padding: 30px 0;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 10px;
  margin-bottom: 20px;
}

.pickup-code-section .label {
  display: block;
  font-size: 14px;
  margin-bottom: 10px;
  opacity: 0.9;
}

.pickup-code {
  display: block;
  font-size: 60px;
  font-weight: bold;
  letter-spacing: 10px;
  text-shadow: 0 2px 5px rgba(0, 0, 0, 0.2);
  margin-bottom: 10px;
}

.pickup-code-section .hint {
  display: block;
  font-size: 12px;
  opacity: 0.8;
}

.order-info {
  margin-bottom: 15px;
}

.info-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 12px;
}

.info-item .icon {
  font-size: 18px;
  margin-right: 8px;
}

.info-detail {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.store-name {
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 3px;
}

.store-address,
.time-label {
  font-size: 13px;
  opacity: 0.9;
}

.time-value {
  font-size: 14px;
  font-weight: bold;
}

.order-items {
  background: rgba(255, 255, 255, 0.15);
  border-radius: 8px;
  padding: 12px;
  margin-bottom: 15px;
}

.items-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding-bottom: 8px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 14px;
}

.item-count {
  opacity: 0.8;
}

.item {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.item:last-child {
  margin-bottom: 0;
}

.item-image {
  width: 40px;
  height: 40px;
  border-radius: 6px;
  margin-right: 8px;
  object-fit: cover;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.item-name {
  font-size: 14px;
}

.item-specs {
  font-size: 12px;
  opacity: 0.8;
}

.item-quantity {
  font-size: 14px;
  margin-left: 8px;
}

.card-actions {
  display: flex;
  gap: 8px;
}

.action-btn {
  flex: 1;
  padding: 10px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 25px;
  color: white;
  font-size: 13px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 4px;
  cursor: pointer;
}

.action-btn.primary {
  background: white;
  color: #D4A574;
  border-color: white;
  font-weight: bold;
}

.other-orders {
  margin: 10px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #333;
  margin-bottom: 10px;
}

.order-list {
  display: flex;
  gap: 8px;
  overflow-x: auto;
  padding-bottom: 5px;
}

.order-item {
  flex-shrink: 0;
  width: 100px;
  padding: 12px;
  background: white;
  border-radius: 8px;
  border: 1px solid #E0E0E0;
  cursor: pointer;
}

.order-item.active {
  border-color: #D4A574;
  background: #FFF9E6;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.pickup-code-small {
  font-size: 18px;
  font-weight: bold;
  color: #D4A574;
}

.status {
  font-size: 11px;
  color: #666;
}

.order-item .order-no {
  font-size: 11px;
  color: #999;
  display: block;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.scan-section {
  margin: 20px 10px;
  text-align: center;
}

.scan-btn {
  width: 100%;
  background: white;
  border: 1px dashed #D4A574;
  border-radius: 10px;
  padding: 30px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  color: #D4A574;
  cursor: pointer;
}

.scan-btn .icon {
  font-size: 44px;
}

.scan-btn .text {
  font-size: 16px;
  font-weight: bold;
}

.scan-hint {
  display: block;
  margin-top: 10px;
  font-size: 12px;
  color: #999;
}

.history-section {
  margin: 10px;
}

.history-list {
  background: white;
  border-radius: 8px;
  overflow: hidden;
}

.history-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #F0F0F0;
  cursor: pointer;
}

.history-item:last-child {
  border-bottom: none;
}

.history-left {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.pickup-code-text {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.order-no-text {
  font-size: 12px;
  color: #999;
}

.picked-time {
  font-size: 11px;
  color: #BBB;
}

.history-right {
  display: flex;
  align-items: center;
  gap: 8px;
}

.amount {
  font-size: 16px;
  color: #333;
  font-weight: bold;
}

.arrow {
  font-size: 24px;
  color: #CCC;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100px 0;
}

.empty-icon {
  width: 100px;
  height: 100px;
  margin-bottom: 20px;
  opacity: 0.3;
}

.empty-text {
  font-size: 14px;
  color: #999;
  margin-bottom: 20px;
}

.go-order-btn {
  padding: 10px 30px;
  background: linear-gradient(135deg, #D4A574, #B08968);
  color: white;
  border-radius: 25px;
  font-size: 14px;
  border: none;
  cursor: pointer;
}
</style>