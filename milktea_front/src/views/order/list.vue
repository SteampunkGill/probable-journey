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

import { orderApi } from '@/utils/api'

const loadOrders = async () => {
  loading.value = true
  try {
    const statusMap = {
      'all': '',
      'pending': 'PAID', // 根据文档映射
      'processing': 'MAKING',
      'completed': 'FINISHED'
    }
    
    const params = {
      status: statusMap[activeTab.value]
    }
    
    const res = await orderApi.getOrderList(params)
    if (res.code === 200) {
      orders.value = res.data.list || res.data || []
    }
  } catch (error) {
    console.error('加载订单列表失败:', error)
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
  if (confirm('确定要取消该订单吗？')) {
    try {
      const res = await orderApi.cancelOrder(id)
      if (res.code === 200) {
        alert('订单已取消')
        loadOrders()
      } else {
        alert(res.message || '取消失败')
      }
    } catch (error) {
      console.error('取消订单失败:', error)
    }
  }
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

.orders {
  padding: 0 24px 100px;
}

.order-item {
  background: var(--surface-color);
  border-radius: 25px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.08);
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.order-item:hover {
  transform: translateY(-5px);
  border-color: var(--primary-light);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.15);
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

.label {
  font-size: 15px;
  color: var(--text-color-medium);
  font-weight: 500;
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

.amount {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  text-shadow: 0 2px 4px rgba(139, 69, 19, 0.1);
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

  .orders {
    padding: 0 20px 100px;
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