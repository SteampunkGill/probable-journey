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
      <div class="order-item" v-for="item in orders" :key="item.id" @click="goToOrderDetail(item.id)">
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
          <template v-else-if="item.status === 'processing'">
            <button class="action-btn secondary" @click.stop="contactService">联系客服</button>
            <button class="action-btn primary" @click.stop="remindOrder(item.id)">催单</button>
          </template>

          <!-- 已完成 -->
          <template v-else-if="item.status === 'completed'">
            <button class="action-btn secondary" @click.stop="reorder(item)">再来一单</button>
            <button class="action-btn primary" v-if="item.canReview" @click.stop="reviewOrder(item.id)">去评价</button>
          </template>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-else-if="!loading">
      <div class="empty-icon">📦</div>
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

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const tabs = [
  { key: 'all', name: '全部' },
  { key: 'pending', name: '待支付' },
  { key: 'processing', name: '制作中' },
  { key: 'completed', name: '已完成' }
]
const activeTab = ref(route.query.status || 'all')
const orders = ref([])
const loading = ref(false)

onMounted(() => {
  loadOrders()
})

const switchTab = (key) => {
  activeTab.value = key
  loadOrders()
}

const loadOrders = async () => {
  loading.value = true
  // 模拟数据
  setTimeout(() => {
    const allOrders = [
      {
        id: 'order_001',
        orderNo: 'MT20231201001',
        status: 'pending_payment',
        statusText: '待支付',
        createTime: '2023-12-01 14:30',
        totalAmount: 68.50,
        items: [
          { id: 'p001', name: '经典珍珠奶茶', image: 'https://images.unsplash.com/photo-1567095761054-7a02e69e5c43?w=400', price: 18.00, quantity: 2 }
        ]
      },
      {
        id: 'order_002',
        orderNo: 'MT20231130089',
        status: 'processing',
        statusText: '制作中',
        createTime: '2023-11-30 16:20',
        totalAmount: 42.00,
        pickupCode: 'A123',
        items: [
          { id: 'p003', name: '满杯水果茶', image: 'https://images.unsplash.com/photo-1570598912132-0ba1dc952b7d?w=400', price: 20.00, quantity: 2 }
        ]
      }
    ]
    
    if (activeTab.value === 'all') {
      orders.value = allOrders
    } else {
      const statusMap = {
        'pending': 'pending_payment',
        'processing': 'processing',
        'completed': 'completed'
      }
      orders.value = allOrders.filter(o => o.status === statusMap[activeTab.value])
    }
    loading.value = false
  }, 500)
}

const goToOrderDetail = (id) => {
  router.push(`/order-detail/${id}`)
}

const payOrder = (id) => {
  router.push({ path: '/payment', query: { orderId: id } })
}

const cancelOrder = (id) => {
  if (confirm('确定要取消该订单吗？')) {
    alert('订单已取消')
    loadOrders()
  }
}

const remindOrder = (id) => {
  alert('已提醒商家尽快制作')
}

const reviewOrder = (id) => {
  alert('评价功能开发中')
}

const reorder = (order) => {
  order.items.forEach(item => {
    cartStore.addItem({
      id: item.id,
      name: item.name,
      image: item.image,
      price: item.price,
      quantity: item.quantity
    })
  })
  alert('已添加到购物车')
  router.push('/cart')
}

const contactService = () => {
  alert('联系电话：400-123-4567')
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
  padding: 15px;
}

.order-item {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.order-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 13px;
}

.order-no {
  color: #999;
}

.order-status {
  font-weight: bold;
}

.order-status.pending_payment { color: #FF4D4F; }
.order-status.processing { color: #D4A574; }
.order-status.completed { color: #999; }

.goods-item {
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
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
  font-weight: bold;
}

.goods-bottom {
  display: flex;
  justify-content: space-between;
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
  border-top: 1px solid #F5F5F5;
  padding-top: 12px;
  margin-top: 12px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
  font-size: 13px;
}

.info-row.total {
  margin-top: 10px;
  font-weight: bold;
}

.label {
  color: #999;
}

.pickup-code {
  color: #D4A574;
  font-weight: bold;
}

.amount {
  font-size: 16px;
  color: #D4A574;
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
  font-size: 12px;
  cursor: pointer;
}

.action-btn.primary {
  background: #D4A574;
  color: white;
  border: none;
}

.action-btn.secondary {
  background: white;
  border: 1px solid #DDD;
  color: #666;
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
  padding: 10px 30px;
  border-radius: 20px;
  cursor: pointer;
}

.loading-more {
  text-align: center;
  padding: 20px;
  color: #999;
}
</style>