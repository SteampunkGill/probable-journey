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

    <!-- 订单列表 -->
    <div class="orders" v-if="orders.length > 0">
      <div 
        class="order-item" 
        v-for="item in orders" 
        :key="item.id" 
        @click="goToOrderDetail(item.id)"
      >
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="order-no">订单号：{{ item.orderNo }}</span>
          <span class="order-status" :class="item.status">{{ item.statusText }}</span>
        </div>

        <!-- 商品列表 -->
        <div class="goods-list">
          <div class="goods-item" v-for="goods in item.items" :key="goods.id">
            <img class="goods-image" :src="goods.image" />
            <div class="goods-info">
              <span class="goods-name">{{ goods.name }}</span>
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
          <template v-if="item.status === 'pending_payment'">
            <button class="action-btn secondary" @click.stop="cancelOrder(item.id)">取消订单</button>
            <button class="action-btn primary" @click.stop="payOrder(item.id)">去支付</button>
          </template>

          <!-- 制作中 -->
          <template v-if="item.status === 'processing'">
            <button class="action-btn secondary" @click.stop="contactService">联系客服</button>
            <button class="action-btn primary" @click.stop="remindOrder(item.id)">催单</button>
          </template>

          <!-- 已完成 -->
          <template v-if="item.status === 'completed'">
            <button class="action-btn secondary" @click.stop="reorder(item)">再来一单</button>
            <button class="action-btn primary" v-if="item.canReview" @click.stop="reviewOrder(item.id)">去评价</button>
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
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { orderApi } from '@/utils/api'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const tabs = [
  { key: 'all', name: '全部' },
  { key: 'pending', name: '待支付' },
  { key: 'processing', name: '制作中' },
  { key: 'completed', name: '已完成' }
]
const activeTab = ref('all')
const orders = ref([])
const loading = ref(false)

onMounted(() => {
  if (route.query.status) {
    activeTab.value = route.query.status
  }
  loadOrders()
})

const switchTab = (key) => {
  activeTab.value = key
  loadOrders()
}

// 状态文本映射
const getStatusText = (status) => {
  const map = {
    'PAID': '待使用',
    'ACCEPTED': '待制作',
    'MAKING': '制作中',
    'READY': '待取餐',
    'DELIVERED': '已送达',
    'FINISHED': '已完成',
    'CANCELLED': '已取消',
    'REFUNDED': '已退款'
  }
  return map[status] || status
}

const loadOrders = async () => {
  loading.value = true
  try {
    const params = {}
    if (activeTab.value !== 'all') {
      // 映射前端状态到后端状态
      const statusMap = {
        'pending': 'PAID', // 待使用/待取餐
        'processing': 'MAKING', // 制作中
        'completed': 'FINISHED' // 已完成
      }
      params.status = statusMap[activeTab.value]
    }
    const res = await orderApi.getOrderList(params)
    let orderData = res.data || res || []
    
    // 纯前端筛选逻辑
    if (activeTab.value !== 'all') {
      const statusMap = {
        'pending': ['PAID', 'ACCEPTED', 'READY'],
        'processing': ['MAKING'],
        'completed': ['FINISHED', 'DELIVERED']
      }
      const targetStatuses = statusMap[activeTab.value] || []
      orderData = orderData.filter(order => targetStatuses.includes(order.status))
    }

    // 转换数据格式以匹配前端
    orders.value = orderData.map(order => ({
      id: order.id,
      orderNo: order.orderNo,
      status: order.status,
      statusText: getStatusText(order.status),
      createTime: order.createTime || order.orderTime || order.createdAt,
      totalAmount: order.totalAmount,
      items: order.items || [],
      pickupCode: order.pickupCode,
      canReview: order.canReview || false
    }))
  } catch (error) {
    console.error('获取订单列表失败', error)
    orders.value = []
  } finally {
    loading.value = false
  }
}

const goToOrderDetail = (id) => {
  router.push(`/order-detail/${id}`)
}

const payOrder = (id) => {
  router.push({ path: '/payment', query: { orderId: id } })
}

const cancelOrder = async (id) => {
  if (!confirm('确定要取消该订单吗？')) return
  try {
    await orderApi.cancelOrder(id)
    alert('订单已取消')
    loadOrders()
  } catch (error) {
    console.error('取消订单失败', error)
    alert('取消订单失败，请重试')
  }
}

const remindOrder = async (id) => {
  try {
    await orderApi.remindOrder(id)
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

const reviewOrder = (id) => {
  router.push(`/review/${id}`)
}

const contactService = () => {
  alert('联系客服：400-123-4567')
}

const goToIndex = () => {
  router.push('/')
}
</script>

<style scoped>
.order-list-page {
  min-height: 100vh;
  background: #F5F5F5;
}

.tabs {
  display: flex;
  background: white;
  position: sticky;
  top: 0;
  z-index: 10;
}

.tab {
  flex: 1;
  text-align: center;
  padding: 15px 0;
  font-size: 14px;
  color: #666;
  position: relative;
  cursor: pointer;
}

.tab.active {
  color: #D4A574;
  font-weight: bold;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 3px;
  background: #D4A574;
  border-radius: 3px;
}

.orders {
  padding: 10px;
}

.order-item {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 10px;
  cursor: pointer;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 12px;
  border-bottom: 1px solid #F5F5F5;
  margin-bottom: 12px;
}

.order-no {
  font-size: 13px;
  color: #999;
}

.order-status {
  font-size: 13px;
  font-weight: bold;
}

.order-status.pending_payment { color: #FF4D4F; }
.order-status.processing { color: #D4A574; }
.order-status.completed { color: #999; }

.goods-list {
  margin-bottom: 15px;
}

.goods-item {
  display: flex;
  gap: 12px;
  margin-bottom: 10px;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 14px;
  color: #333;
}

.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.goods-price {
  font-size: 14px;
  font-weight: bold;
}

.goods-quantity {
  font-size: 12px;
  color: #999;
}

.order-info {
  padding: 12px 0;
  border-top: 1px solid #F5F5F5;
  border-bottom: 1px solid #F5F5F5;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.info-row.total {
  margin-top: 12px;
  font-size: 14px;
}

.info-row.total .amount {
  font-size: 18px;
  font-weight: bold;
  color: #333;
}

.pickup-code {
  color: #D4A574;
  font-weight: bold;
  font-size: 16px;
}

.order-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 15px;
}

.action-btn {
  padding: 6px 15px;
  border-radius: 15px;
  font-size: 13px;
  cursor: pointer;
}

.action-btn.primary {
  background: #D4A574;
  color: white;
  border: none;
}

.action-btn.secondary {
  background: white;
  color: #666;
  border: 1px solid #DDD;
}

.empty-state {
  padding-top: 100px;
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  opacity: 0.2;
}

.go-shopping-btn {
  margin-top: 20px;
  background: #D4A574;
  color: white;
  border: none;
  padding: 10px 40px;
  border-radius: 25px;
  font-size: 15px;
  cursor: pointer;
}

.loading-more {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>