
<template>
  <div class="order-list-page">
    <!-- 标签页 -->
    <div class="tabs">
      <div
        class="tab"
        :class="{ active: activeTab === item.key }"
        v-for="item in tabs"
        :key="item.key"
        @click="switchTab(item.key)"
      >
        {{ item.name }}
      </div>
    </div>

    <!-- 筛选与排序栏 -->
    <div class="filter-bar">
      <div class="search-box">
        <span class="search-icon">🔍</span>
        <input
          v-model="searchKeyword"
          placeholder="搜索订单号"
          class="search-input"
        />
      </div>
      <div class="sort-box">
        <select v-model="sortOption" class="sort-select">
          <option value="time_desc">时间降序</option>
          <option value="time_asc">时间升序</option>
          <option value="amount_desc">金额降序</option>
          <option value="amount_asc">金额升序</option>
        </select>
      </div>
    </div>

    <!-- 订单列表 -->
    <div class="orders" v-if="filteredOrders.length > 0">
      <div
        class="order-item"
        v-for="item in filteredOrders"
        :key="item.id"
        @click="goToOrderDetail(item.orderNo)"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="order-no">订单号：{{ item.orderNo }}</span>
          <span class="order-status" :class="item.status">{{ item.statusText }}</span>
        </div>

        <!-- 订单进度条 -->
        <div class="order-progress-container">
          <div class="progress-bar">
            <div class="progress-inner" :style="{ width: getProgressWidth(item.status) }"></div>
          </div>
          <div class="progress-steps">
            <div class="step" :class="{ active: isStepActive(item.status, 1) }">
              <div class="step-dot"></div>
              <span class="step-text">下单</span>
            </div>
            <div class="step" :class="{ active: isStepActive(item.status, 2) }">
              <div class="step-dot"></div>
              <span class="step-text">支付</span>
            </div>
            <div class="step" :class="{ active: isStepActive(item.status, 3) }">
              <div class="step-dot"></div>
              <span class="step-text">制作</span>
            </div>
            <div class="step" :class="{ active: isStepActive(item.status, 4) }">
              <div class="step-dot"></div>
              <span class="step-text">待取</span>
            </div>
            <div class="step" :class="{ active: isStepActive(item.status, 5) }">
              <div class="step-dot"></div>
              <span class="step-text">完成</span>
            </div>
          </div>
        </div>

        <!-- 制作进度可视化 (仅在制作中显示) -->
        <div class="making-status-bar" v-if="item.status?.toUpperCase() === 'MAKING' || item.status?.toUpperCase() === 'PAID'">
          <div class="making-content">
            <div class="making-icon-box">
              <span class="making-icon">🍵</span>
              <div class="making-wave"></div>
            </div>
            <div class="making-info">
              <div class="making-text">正在为您精心制作中...</div>
              <div class="making-desc">预计取餐时间 <span class="highlight">{{ getCountdown(item.estimatedTimestamp) }}</span></div>
            </div>
          </div>
          <div class="making-progress-mini">
            <div class="mini-dot active"></div>
            <div class="mini-dot active"></div>
            <div class="mini-dot animate"></div>
          </div>
        </div>

        <!-- 商品列表 -->
        <div class="goods-list">
          <div
            class="goods-item"
            v-for="goods in (item.items || item.orderItems)"
            :key="goods.id"
            @click.stop="() => {
              const productId = goods.productId || goods.id;
              if (!productId) return;
              router.push(`/product/${productId}`);
            }"
          >
            <!-- DEMO ONLY: 去掉图片加载 -->
            <!-- <img class="goods-image" :src="formatImageUrl(goods.image || goods.productImage || goods.product?.mainImageUrl || goods.product?.imageUrl || goods.mainImageUrl || goods.imageUrl)" /> -->
            <div class="goods-info">
              <span class="goods-name">{{ goods.name || goods.productName }}</span>
              <div class="goods-bottom">
                <span class="goods-price">¥{{ goods.price }}</span>
                <span class="goods-quantity">×{{ goods.quantity }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 订单信息 -->
        <div class="order-info">
          <div class="info-row">
            <span class="label">下单时间</span>
            <span class="value">{{ item.createTime }}</span>
          </div>
          <div class="info-row" v-if="item.pickupCode">
            <span class="label">取餐码</span>
            <span class="pickup-code">{{ item.pickupCode }}</span>
          </div>
          <div class="info-row total">
            <span class="label">实付款</span>
            <span class="amount">¥{{ item.totalAmount }}</span>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="order-actions">
          <!-- 待支付 -->
          <template v-if="item.status?.toLowerCase() === 'pending_payment'">
            <button class="action-btn secondary" @click.stop="cancelOrder(item.orderNo)">取消订单</button>
            <button class="action-btn primary" @click.stop="payOrder(item)">立即付款</button>
          </template>

          <!-- 制作中 -->
          <template v-else-if="item.status?.toLowerCase() === 'processing' || item.status?.toUpperCase() === 'MAKING' || item.status?.toUpperCase() === 'PAID'">
            <button class="action-btn secondary" @click.stop="applyRefund(item.orderNo)">申请退款</button>
            <button class="action-btn secondary" @click.stop="goToComplaint">投诉建议</button>
            <button class="action-btn primary" @click.stop="remindOrder(item.orderNo)">催单</button>
          </template>

          <!-- 待取餐 -->
          <template v-else-if="item.status?.toLowerCase() === 'ready' || item.status?.toUpperCase() === 'READY'">
            <button class="action-btn secondary" @click.stop="applyRefund(item.orderNo)">申请退款</button>
            <button class="action-btn secondary" @click.stop="goToComplaint">投诉建议</button>
            <button class="action-btn primary" @click.stop="confirmOrder(item.orderNo)">立即取餐</button>
          </template>

          <!-- 已完成 -->
          <template v-else-if="item.status?.toLowerCase() === 'completed' || item.status?.toUpperCase() === 'COMPLETED' || item.status?.toUpperCase() === 'FINISHED' || item.status?.toUpperCase() === 'REVIEWED'">
            <button class="action-btn secondary" @click.stop="applyRefund(item.orderNo)">申请退款</button>
            <button class="action-btn secondary" @click.stop="goToComplaint">投诉建议</button>
            <button class="action-btn primary" v-if="item.status?.toUpperCase() !== 'REVIEWED'" @click.stop="reviewOrder(item.orderNo)">立即评价</button>
          </template>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">📋</div>
      <p class="empty-text">暂无订单</p>
      <button class="go-shopping-btn" @click="goToIndex">去逛逛</button>
    </div>

    <!-- 加载状态 -->
    <div class="loading-more" v-if="loading">
      <span>加载中...</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { orderApi } from '@/utils/api'
import { formatImageUrl } from '@/utils/util'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const tabs = [
  { key: 'all', name: '全部' },
  { key: 'pending_payment', name: '待付款' },
  { key: 'processing', name: '制作中' },
  { key: 'ready', name: '待取餐' },
  { key: 'completed', name: '已完成' }
]
const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)
const now = ref(Date.now())
const searchKeyword = ref('')
const sortOption = ref('time_desc')

// 纯前端筛选与排序逻辑
const filteredOrders = computed(() => {
  let result = [...orders.value]

  // 1. 标签分类筛选
  if (activeTab.value !== 'all') {
    const statusFilterMap = {
      'pending_payment': ['PENDING_PAYMENT'],
      'processing': ['PAID', 'MAKING', 'ACCEPTED'],
      'ready': ['READY', 'DELIVERING'],
      'completed': ['DELIVERED', 'COMPLETED', 'FINISHED', 'REVIEWED']
    }
    const targetStatuses = statusFilterMap[activeTab.value] || []
    result = result.filter(order => {
      const s = order.status ? order.status.toUpperCase() : ''
      return targetStatuses.includes(s)
    })
  }

  // 2. 搜索筛选 (按订单号)
  if (searchKeyword.value) {
    const keyword = searchKeyword.value.toLowerCase()
    result = result.filter(order =>
      order.orderNo && order.orderNo.toLowerCase().includes(keyword)
    )
  }

  // 3. 排序逻辑
  result.sort((a, b) => {
    switch (sortOption.value) {
      case 'time_desc':
        return new Date(b.createTime) - new Date(a.createTime)
      case 'time_asc':
        return new Date(a.createTime) - new Date(b.createTime)
      case 'amount_desc':
        return b.totalAmount - a.totalAmount
      case 'amount_asc':
        return a.totalAmount - b.totalAmount
      default:
        return 0
    }
  })

  return result
})

onMounted(() => {
  if (route.query.status) {
    activeTab.value = route.query.status.toLowerCase()
  }
  
  const token = localStorage.getItem('token')
  if (token && token !== 'undefined' && token !== 'null') {
    loadOrders()
  } else {
    loading.value = false
  }

  // 启动倒计时定时器
  const timer = setInterval(() => {
    now.value = Date.now()
  }, 1000)

  return () => clearInterval(timer)
})

const getCountdown = (timestamp) => {
  if (!timestamp) return '00:00'
  const diff = Math.max(0, Math.floor((timestamp - now.value) / 1000))
  const m = Math.floor(diff / 60).toString().padStart(2, '0')
  const s = (diff % 60).toString().padStart(2, '0')
  return `${m}:${s}`
}

const switchTab = (key) => {
  activeTab.value = key
  loadOrders()
}

// 状态文本映射
const getStatusText = (status) => {
  if (!status) return '未知状态'
  const s = status.toUpperCase()
  const map = {
    'PENDING_PAYMENT': '待支付',
    'PAID': '待接单',
    'MAKING': '制作中',
    'READY': '待取餐',
    'DELIVERING': '配送中',
    'DELIVERED': '已送达',
    'COMPLETED': '已完成',
    'FINISHED': '已完成',
    'REFUNDING': '退款中',
    'REFUNDED': '已退款',
    'CANCELLED': '已取消',
    'REVIEWED': '已评价'
  }
  return map[s] || status
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      status: activeTab.value === 'all' ? '' : activeTab.value.toUpperCase()
    }
    
    const res = await orderApi.getOrderList(params)
    
    if (res.code === 200 && Array.isArray(res.data)) {
      orders.value = res.data.map(order => {
        const createTime = order.createTime || order.orderTime || order.createdAt
        let estimatedTimestamp = Date.now() + 20 * 60 * 1000
        if (createTime) {
          const date = new Date(createTime.replace(/-/g, '/'))
          if (!isNaN(date.getTime())) {
            date.setMinutes(date.getMinutes() + 20)
            estimatedTimestamp = date.getTime()
          }
        }

        const rawItems = order.orderItems || order.items || []
        const items = rawItems.map(item => ({
          ...item,
          name: item.productName || item.name,
          image: item.productImage || item.image
        }))

        return {
          ...order,
          statusText: getStatusText(order.status),
          estimatedTimestamp: estimatedTimestamp,
          items: items
        }
      })
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
    orders.value = []
  } finally {
    loading.value = false
  }
}

const goToOrderDetail = (orderNo) => {
  router.push(`/order-detail/${orderNo}`)
}

const payOrder = (order) => {
  router.push({
    path: '/payment',
    query: {
      orderNo: order.orderNo,
      amount: order.totalAmount
    }
  })
}

const cancelOrder = async (orderNo) => {
  if (!confirm('确定要取消该订单吗？')) return
  try {
    await orderApi.cancelOrder(orderNo)
    alert('订单已取消')
    loadOrders()
  } catch (error) {
    console.error('取消订单失败', error)
    alert('取消订单失败，请重试')
  }
}

const remindOrder = async (orderNo) => {
  try {
    await orderApi.remindOrder(orderNo)
    alert('已提醒商家尽快制作')
  } catch (error) {
    console.error('催单失败', error)
    alert('催单失败，请重试')
  }
}

const reorder = (order) => {
  order.items.forEach(item => {
    cartStore.addItem({
      ...item,
      productId: item.id,
      subtotal: item.price * item.quantity
    })
  })
  alert('已添加到购物车')
  router.push('/cart')
}

const reviewOrder = (orderNo) => {
  router.push(`/review/${orderNo}`)
}

const applyRefund = (orderNo) => {
  router.push({
    path: '/order/refund',
    query: { orderId: orderNo }
  })
}

const goToComplaint = () => {
  router.push('/user/complaint')
}

const contactService = () => {
  alert('联系客服：400-123-4567')
}

const goToIndex = () => {
  router.push('/')
}

const confirmOrder = async (orderNo) => {
  if (!confirm('确认已取餐/收到商品吗？')) return
  try {
    await orderApi.confirmOrder(orderNo)
    alert('操作成功')
    loadOrders()
  } catch (error) {
    console.error('确认订单失败', error)
    alert('操作失败，请重试')
  }
}

const getProgressWidth = (status) => {
  if (!status) return '0%'
  const s = status.toUpperCase()
  const statusMap = {
    'PENDING_PAYMENT': '20%',
    'PAID': '40%',
    'MAKING': '60%',
    'READY': '80%',
    'DELIVERING': '85%',
    'DELIVERED': '90%',
    'COMPLETED': '100%',
    'FINISHED': '100%',
    'REVIEWED': '100%'
  }
  return statusMap[s] || '0%'
}

const isStepActive = (status, step) => {
  if (!status) return false
  const s = status.toUpperCase()
  const statusLevel = {
    'PENDING_PAYMENT': 1,
    'PAID': 2,
    'MAKING': 3,
    'READY': 4,
    'DELIVERING': 4,
    'DELIVERED': 4,
    'COMPLETED': 5,
    'FINISHED': 5,
    'REVIEWED': 5
  }
  const currentLevel = statusLevel[s] || 0
  return currentLevel >= step
}
</script>
<style scoped>
.order-list-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: 'Noto Sans KR', sans-serif;
}

.tabs {
  display: flex;
  background: var(--surface-color);
  position: sticky;
  top: 0;
  z-index: 10;
  padding: 0 20px;
  border-radius: 0 0 25px 25px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.08);
  border-bottom: 1px solid var(--border-color);
}

.tab {
  flex: 1;
  text-align: center;
  padding: 20px 0;
  font-size: 15px;
  color: var(--text-color-medium);
  position: relative;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  font-family: 'Prompt', sans-serif;
}

.tab:hover {
  color: var(--primary-color);
  transform: translateY(-2px);
}

.tab.active {
  color: var(--primary-dark);
  font-weight: 700;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--primary-light) 100%);
  border-radius: 4px;
  box-shadow: 0 2px 8px rgba(160, 82, 45, 0.3);
}

.filter-bar {
  display: flex;
  padding: 20px;
  gap: 16px;
  background: transparent;
  align-items: center;
}

.search-box {
  flex: 1;
  position: relative;
  display: flex;
  align-items: center;
  background: white;
  border-radius: 25px;
  padding: 0 20px;
  height: 48px;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.15);
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.search-box:focus-within {
  border-color: var(--primary-light);
  box-shadow: 0 4px 20px rgba(160, 82, 45, 0.2);
  transform: translateY(-2px);
}

.search-icon {
  font-size: 18px;
  margin-right: 12px;
  color: var(--primary-light);
}

.search-input {
  flex: 1;
  height: 100%;
  border: none;
  background: transparent;
  font-size: 15px;
  outline: none;
  color: var(--text-color-dark);
  font-family: 'Noto Sans KR', sans-serif;
}

.search-input::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

.sort-box {
  width: 120px;
}

.sort-select {
  width: 100%;
  height: 48px;
  border: 2px solid var(--border-color);
  border-radius: 24px;
  padding: 0 20px;
  font-size: 14px;
  color: var(--text-color-dark);
  outline: none;
  background: white;
  font-family: 'Noto Sans KR', sans-serif;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  appearance: none;
  background-image: url("data:image/svg+xml;charset=UTF-8,%3csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 24 24' fill='none' stroke='%23a0522d' stroke-width='2' stroke-linecap='round' stroke-linejoin='round'%3e%3cpolyline points='6 9 12 15 18 9'%3e%3c/polyline%3e%3c/svg%3e");
  background-repeat: no-repeat;
  background-position: right 15px center;
  background-size: 16px;
}

.sort-select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.15);
}

.orders {
  padding: 0 20px 100px;
}

.order-item {
  background: var(--surface-color);
  border-radius: 25px;
  padding: 24px;
  margin-bottom: 20px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 2px solid transparent;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.08);
  position: relative;
  overflow: hidden;
}

.order-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.15);
  border-color: var(--primary-light);
}

.order-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--accent-cream) 100%);
  border-radius: 25px 25px 0 0;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  margin-bottom: 16px;
  border-bottom: 2px dashed var(--border-color);
}

/* 订单进度条样式 */
.order-progress-container {
  padding: 20px 10px;
  margin-bottom: 15px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 15px;
}

/* 制作进度可视化样式 */
.making-status-bar {
  background: linear-gradient(135deg, #fff9f0 0%, #fff2e0 100%);
  border-radius: 16px;
  padding: 16px;
  margin-bottom: 20px;
  border: 1px solid #ffe0b2;
  display: flex;
  justify-content: space-between;
  align-items: center;
  animation: fadeIn 0.5s ease;
}

.making-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.making-icon-box {
  position: relative;
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 20px;
  box-shadow: 0 4px 8px rgba(255, 167, 38, 0.2);
}

.making-wave {
  position: absolute;
  width: 100%;
  height: 100%;
  border-radius: 50%;
  border: 2px solid #ffa726;
  animation: wave 2s infinite;
}

.making-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.making-text {
  font-size: 14px;
  font-weight: 600;
  color: #e65100;
}

.making-desc {
  font-size: 12px;
  color: #fb8c00;
}

.making-desc .highlight {
  font-weight: 700;
  color: #ef6c00;
  font-size: 14px;
  margin-left: 4px;
}

.making-progress-mini {
  display: flex;
  gap: 6px;
}

.mini-dot {
  width: 6px;
  height: 6px;
  background: #ffe0b2;
  border-radius: 50%;
}

.mini-dot.active {
  background: #ffa726;
}

.mini-dot.animate {
  background: #ffa726;
  animation: pulse 1s infinite;
}

@keyframes wave {
  0% { transform: scale(1); opacity: 0.8; }
  100% { transform: scale(1.5); opacity: 0; }
}

@keyframes pulse {
  0% { transform: scale(1); opacity: 1; }
  50% { transform: scale(1.5); opacity: 0.5; }
  100% { transform: scale(1); opacity: 1; }
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.progress-bar {
  height: 6px;
  background: #eee;
  border-radius: 3px;
  position: relative;
  margin-bottom: 15px;
  overflow: hidden;
}

.progress-inner {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: linear-gradient(90deg, var(--primary-light), var(--primary-color));
  transition: width 0.5s ease;
  border-radius: 3px;
}

.progress-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.step {
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  z-index: 1;
}

.step-dot {
  width: 10px;
  height: 10px;
  background: #ddd;
  border-radius: 50%;
  margin-bottom: 8px;
  transition: all 0.3s ease;
}

.step.active .step-dot {
  background: var(--primary-color);
  transform: scale(1.3);
  box-shadow: 0 0 8px rgba(160, 82, 45, 0.4);
}

.step-text {
  font-size: 12px;
  color: #999;
  transition: all 0.3s ease;
}

.step.active .step-text {
  color: var(--primary-dark);
  font-weight: bold;
}

.order-no {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
  font-family: 'Prompt', sans-serif;
}

.order-status {
  font-size: 14px;
  font-weight: 700;
  padding: 6px 16px;
  border-radius: 20px;
  font-family: 'Prompt', sans-serif;
}

.order-status.pending_payment {
  background: linear-gradient(135deg, #ffc0cb 0%, #ffb6c1 100%);
  color: #c71585;
  box-shadow: 0 2px 8px rgba(255, 182, 193, 0.3);
}

.order-status.processing {
  background: linear-gradient(135deg, var(--accent-cream) 0%, #fff8e1 100%);
  color: var(--primary-dark);
  box-shadow: 0 2px 8px rgba(210, 180, 140, 0.3);
}

.order-status.completed {
  background: linear-gradient(135deg, #e8f5e8 0%, #d4edda 100%);
  color: #28a745;
  box-shadow: 0 2px 8px rgba(40, 167, 69, 0.2);
}

.goods-list {
  margin-bottom: 20px;
}

.goods-item {
  display: flex;
  gap: 16px;
  margin-bottom: 16px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.5);
  border-radius: 20px;
  transition: all 0.3s ease;
}

.goods-item:hover {
  background: white;
  transform: translateX(4px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
}

.goods-image {
  width: 80px;
  height: 80px;
  border-radius: 16px;
  object-fit: cover;
  border: 2px solid var(--accent-cream);
  box-shadow: 0 4px 8px rgba(210, 180, 140, 0.2);
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 16px;
  color: var(--text-color-dark);
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
  line-height: 1.4;
}


.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.goods-price {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
}

.goods-quantity {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
  background: var(--accent-cream);
  padding: 4px 12px;
  border-radius: 12px;
}

.order-info {
  padding: 20px 0;
  border-top: 2px dashed var(--border-color);
  border-bottom: 2px dashed var(--border-color);
  margin: 20px 0;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 14px;
  align-items: center;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row.total {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid var(--border-color);
}

.info-row.total .amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  text-shadow: 0 2px 4px rgba(139, 69, 19, 0.1);
}

.pickup-code {
  color: var(--primary-color);
  font-weight: 700;
  font-size: 20px;
  font-family: 'Prompt', sans-serif;
  background: linear-gradient(135deg, var(--accent-cream) 0%, white 100%);
  padding: 8px 16px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.3);
  border: 2px solid var(--primary-light);
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  margin-top: 20px;
}

.action-btn {
  padding: 10px 24px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 2px solid transparent;
  min-width: 100px;
  text-align: center;
}

.action-btn.primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
}

.action-btn.primary:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

.action-btn.primary:active {
  transform: translateY(0) scale(0.98);
}

.action-btn.secondary {
  background: white;
  color: var(--text-color-dark);
  border: 2px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
}

.action-btn.secondary:hover {
  transform: translateY(-3px);
  border-color: var(--primary-light);
  color: var(--primary-dark);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.15);
}

.empty-state {
  padding-top: 120px;
  text-align: center;
  color: var(--text-color-medium);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: 24px;
  opacity: 0.3;
  filter: drop-shadow(0 4px 8px rgba(139, 69, 19, 0.1));
}

.empty-text {
  font-size: 16px;
  color: var(--text-color-medium);
  margin-bottom: 32px;
  font-weight: 500;
}

.go-shopping-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  padding: 14px 48px;
  border-radius: 25px;
  font-size: 16px;
  cursor: pointer;
  font-weight: 700;
  font-family: 'Prompt', sans-serif;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 20px rgba(160, 82, 45, 0.3);
}

.go-shopping-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 30px rgba(160, 82, 45, 0.4);
}

.loading-more {
  text-align: center;
  padding: 30px;
  color: var(--text-color-medium);
  font-size: 14px;
  font-weight: 500;
}

.loading-more::after {
  content: '...';
  animation: dots 1.5s steps(4, end) infinite;
}

@keyframes dots {
  0%, 20% { content: ''; }
  40% { content: '.'; }
  60% { content: '..'; }
  80%, 100% { content: '...'; }
}

/* 响应式调整 */
@media (max-width: 375px) {
  .tabs {
    padding: 0 16px;
  }

  .tab {
    padding: 18px 0;
    font-size: 14px;
  }

  .filter-bar {
    padding: 16px;
    gap: 12px;
  }

  .search-box {
    height: 44px;
    padding: 0 16px;
  }

  .sort-select {
    height: 44px;
    padding: 0 16px;
  }

  .orders {
    padding: 0 16px 100px;
  }

  .order-item {
    padding: 20px;
    border-radius: 20px;
  }

  .goods-image {
    width: 70px;
    height: 70px;
  }

  .action-btn {
    padding: 8px 20px;
    min-width: 90px;
    font-size: 13px;
  }
}
</style>