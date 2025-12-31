<template>
  <div class="order-detail-page">
    <!-- 加载状态 -->
    <div class="loading" v-if="loading">
      <div class="loading-icon"></div>
      <p>加载中...</p>
    </div>

    <div v-else class="content">
      <!-- 订单状态 -->
      <div class="status-section">
        <div class="status-header">
          <h2 class="status-text">{{ getStatusText(order.status) }}</h2>
          <p class="status-hint" v-if="order.deliveryType === 'delivery'">预计{{ order.estimatedTime }}送达</p>
          <p class="status-hint" v-if="order.deliveryType === 'pickup'">请凭取餐码取餐</p>
        </div>

        <!-- 状态流程 -->
        <div class="status-steps">
          <div 
            class="step" 
            :class="{ active: index <= currentStep }" 
            v-for="(item, index) in statusSteps" 
            :key="item.key"
          >
            <div class="step-dot"></div>
            <span class="step-title">{{ item.title }}</span>
            <span class="step-time" v-if="item.time">{{ item.time }}</span>
          </div>
        </div>
      </div>

      <!-- 取餐码（自取订单） -->
      <div class="pickup-section" v-if="order.deliveryType === 'pickup' && order.pickupCode">
        <div class="pickup-card">
          <span class="pickup-label">取餐码</span>
          <h1 class="pickup-code">{{ order.pickupCode }}</h1>
          <button class="copy-btn" @click="copyPickupCode">复制</button>
        </div>
      </div>

      <!-- 门店/地址信息 -->
      <div class="location-section">
        <h3 class="section-title">
          {{ order.deliveryType === 'delivery' ? '收货信息' : '自提门店' }}
        </h3>
        
        <!-- 配送地址 -->
        <div class="address-card" v-if="order.deliveryType === 'delivery' && order.address">
          <div class="address-header">
            <span class="name">{{ order.address.name }}</span>
            <span class="phone">{{ order.address.phone }}</span>
          </div>
          <p class="address-detail">{{ order.address.fullAddress }}</p>
        </div>

        <!-- 自提门店 -->
        <div class="store-card" v-if="order.deliveryType === 'pickup' && order.store">
          <div class="store-header">
            <span class="store-name">{{ order.store.name }}</span>
            <button class="call-btn" @click="callStore">
              <span class="icon">📞</span>
              <span>联系门店</span>
            </button>
          </div>
          <p class="store-address">{{ order.store.address }}</p>
          <p class="store-hours">营业时间：{{ order.store.businessHours }}</p>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="goods-section">
        <h3 class="section-title">商品清单</h3>
        <div class="goods-list">
          <div class="goods-item" v-for="item in order.orderItems" :key="item.id">
            <!-- 简化图片字段，直接使用后端返回的标准字段 -->
            <img class="goods-image" :src="formatImageUrl(item.productImage)" />
            <div class="goods-info">
              <h4 class="goods-name">{{ item.productName }}</h4>
              <div class="goods-specs" v-if="item.customizations">
                <!-- 假设后端已将规格序列化为对象 -->
                <span>{{ item.customizations.sweetness }} / {{ item.customizations.temperature }}</span>
                <span v-if="item.customizations.toppings?.length > 0">
                  + {{ item.customizations.toppings.length }}种加料
                </span>
              </div>
              <div class="goods-bottom">
                <span class="goods-price">¥{{ item.price }}</span>
                <span class="goods-quantity">×{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 订单信息 -->
      <div class="info-section">
        <h3 class="section-title">订单信息</h3>
        <div class="info-list">
          <div class="info-item">
            <span class="label">订单编号</span>
            <div class="value-copy">
              <span class="value">{{ order.orderNo }}</span>
              <button class="copy-icon" @click="copyOrderNo">复制</button>
            </div>
          </div>
          <div class="info-item">
            <span class="label">下单时间</span>
            <span class="value">{{ order.createTime }}</span>
          </div>
          <div class="info-item" v-if="order.payTime">
            <span class="label">支付时间</span>
            <span class="value">{{ order.payTime }}</span>
          </div>
          <div class="info-item" v-if="order.remark">
            <span class="label">订单备注</span>
            <span class="value">{{ order.remark }}</span>
          </div>
        </div>
      </div>

      <!-- 费用明细 -->
      <div class="amount-section">
        <h3 class="section-title">费用明细</h3>
        <div class="amount-list">
          <div class="amount-item">
            <span class="label">商品小计</span>
            <span class="value">¥{{ order.subtotal }}</span>
          </div>
          <div class="amount-item" v-if="order.deliveryFee > 0">
            <span class="label">配送费</span>
            <span class="value">¥{{ order.deliveryFee }}</span>
          </div>
          <div class="amount-item" v-if="order.packagingFee > 0">
            <span class="label">包装费</span>
            <span class="value">¥{{ order.packagingFee }}</span>
          </div>
          <div class="amount-item discount" v-if="order.couponDiscount > 0">
            <span class="label">优惠券优惠</span>
            <span class="value">-¥{{ order.couponDiscount }}</span>
          </div>
          <div class="amount-item total">
            <span class="label">实付款</span>
            <span class="value">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="footer" v-if="!loading">
      <!-- 待支付 -->
      <template v-if="order.status === 'PENDING_PAYMENT'">
        <button class="footer-btn secondary" @click="cancelOrder">取消订单</button>
        <button class="footer-btn primary" @click="payOrder">立即支付</button>
      </template>

      <!-- 已完成可评价 -->
      <template v-else-if="order.status === 'COMPLETED' || order.status === 'FINISHED'">
        <button class="footer-btn secondary" @click="reorder">再来一单</button>
        <button class="footer-btn primary" @click="reviewOrder">去评价</button>
      </template>

      <!-- 通用：制作中/配送中 -->
      <template v-else>
        <button class="footer-btn secondary" @click="contactService">联系客服</button>
        <button class="footer-btn primary" @click="reorder">再来一单</button>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { orderApi } from '@/utils/api'
import { formatImageUrl } from '@/utils/util'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const orderNo = ref(route.params.id)
const order = ref({})
const loading = ref(true)
const statusSteps = ref([])
const currentStep = ref(0)

onMounted(() => {
  loadOrderDetail()
})

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(orderNo.value)
    if (res.code === 200) {
      // 直接使用后端返回的数据对象，不进行繁琐的本地 fallback 解析
      order.value = res.data
      generateStatusSteps(res.data)
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

const generateStatusSteps = (orderData) => {
  // 定义标准流程
  if (orderData.deliveryType === 'delivery') {
    statusSteps.value = [
      { key: 'created', title: '订单已提交', time: orderData.createTime },
      { key: 'paid', title: '支付成功', time: orderData.payTime },
      { key: 'processing', title: '商家制作中', time: '' },
      { key: 'delivering', title: '配送中', time: '' },
      { key: 'completed', title: '订单完成', time: '' }
    ]
  } else {
    statusSteps.value = [
      { key: 'created', title: '订单已提交', time: orderData.createTime },
      { key: 'paid', title: '支付成功', time: orderData.payTime },
      { key: 'processing', title: '商家制作中', time: '' },
      { key: 'ready', title: '已备餐', time: '' },
      { key: 'completed', title: '已取餐', time: '' }
    ]
  }
  
  // 状态与步骤的映射映射
  const stepMap = {
    'PENDING_PAYMENT': 0,
    'PAID': 1,
    'MAKING': 2,
    'READY': 3,
    'DELIVERING': 3,
    'DELIVERED': 3,
    'COMPLETED': 4,
    'FINISHED': 4,
    'REVIEWED': 4
  }
  currentStep.value = stepMap[orderData.status] || 0
}

const getStatusText = (status) => {
  const statusMap = {
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
  return statusMap[status] || status
}

const copyOrderNo = () => {
  navigator.clipboard.writeText(order.value.orderNo)
  alert('已复制订单号')
}

const copyPickupCode = () => {
  navigator.clipboard.writeText(order.value.pickupCode)
  alert('已复制取餐码')
}

const cancelOrder = async () => {
  if (confirm('确定要取消订单吗？')) {
    const res = await orderApi.cancelOrder(orderNo.value)
    if (res.code === 200) {
      alert('订单已取消')
      router.back()
    }
  }
}

const payOrder = () => {
  router.push({ path: '/payment', query: { orderNo: orderNo.value } })
}

const reorder = () => {
  const items = order.value.orderItems || []
  items.forEach(item => {
    cartStore.addItem({
      productId: item.productId,
      name: item.productName,
      image: item.productImage,
      price: item.price,
      quantity: item.quantity,
      customizations: item.customizations
    })
  })
  router.push('/cart')
}

const contactService = () => {
  window.location.href = 'tel:4001234567'
}
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: 'Noto Sans KR', sans-serif;
  padding-bottom: 100px;
}

.loading {
  padding-top: 150px;
  text-align: center;
  color: var(--text-color-medium);
}

.loading-icon {
  width: 60px;
  height: 60px;
  margin: 0 auto 20px;
  border: 4px solid var(--border-color);
  border-top: 4px solid var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.content {
  padding-bottom: 30px;
}

/* 状态区域 */
.status-section {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  padding: 40px 24px 30px;
  border-radius: 0 0 40px 40px;
  box-shadow: 0 4px 25px rgba(160, 82, 45, 0.2);
  position: relative;
  overflow: hidden;
}

.status-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23ffffff' fill-opacity='0.05' fill-rule='evenodd'/%3E%3C/svg%3E");
  opacity: 0.1;
}

.status-header {
  margin-bottom: 30px;
}

.status-text {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
  font-family: 'Prompt', sans-serif;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.status-hint {
  font-size: 15px;
  opacity: 0.9;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.15);
  padding: 6px 16px;
  border-radius: 20px;
  display: inline-block;
  backdrop-filter: blur(10px);
}

/* 状态步骤 */
.status-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
  padding: 0 10px;
}

.status-steps::before {
  content: '';
  position: absolute;
  top: 12px;
  left: 40px;
  right: 40px;
  height: 3px;
  background: rgba(255, 255, 255, 0.3);
  z-index: 1;
  border-radius: 2px;
}

.step {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
  padding: 0 10px;
}

.step-dot {
  width: 24px;
  height: 24px;
  background: rgba(255, 255, 255, 0.4);
  border-radius: 50%;
  margin-bottom: 12px;
  position: relative;
  transition: all 0.3s ease;
  border: 3px solid transparent;
}

.step.active .step-dot {
  background: white;
  box-shadow: 0 0 15px rgba(255, 255, 255, 0.5);
  border-color: var(--primary-light);
  transform: scale(1.2);
}

.step-title {
  font-size: 13px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.8);
  text-align: center;
  line-height: 1.4;
  margin-bottom: 4px;
  font-family: 'Prompt', sans-serif;
}

.step.active .step-title {
  color: white;
  font-weight: 700;
  opacity: 1;
}

.step-time {
  font-size: 11px;
  color: rgba(255, 255, 255, 0.6);
  text-align: center;
}

/* 取餐码区域 */
.pickup-section {
  padding: 0 24px;
  margin-top: -20px;
  position: relative;
  z-index: 2;
}

.pickup-card {
  background: white;
  border-radius: 30px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 8px 30px rgba(160, 82, 45, 0.15);
  border: 2px solid var(--accent-cream);
  position: relative;
  overflow: hidden;
}

.pickup-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--primary-light) 100%);
}

.pickup-label {
  font-size: 15px;
  color: var(--text-color-medium);
  font-weight: 500;
  display: block;
  margin-bottom: 15px;
}

.pickup-code {
  font-size: 56px;
  font-weight: 700;
  color: var(--primary-dark);
  margin: 20px 0;
  font-family: 'Prompt', sans-serif;
  text-shadow: 0 2px 10px rgba(139, 69, 19, 0.1);
  letter-spacing: 4px;
}

.copy-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 25px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-family: 'Prompt', sans-serif;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
}

.copy-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

/* 通用卡片区域 */
.location-section, .goods-section, .info-section, .amount-section {
  background: var(--surface-color);
  margin: 20px 24px;
  padding: 24px;
  border-radius: 25px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.08);
  border: 1px solid var(--border-color);
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 20px;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  padding-left: 12px;
  border-left: 4px solid var(--primary-color);
}

/* 地址/门店卡片 */
.address-card, .store-card {
  background: white;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.1);
  border: 2px solid transparent;
  transition: all 0.3s ease;
}

.address-card:hover, .store-card:hover {
  border-color: var(--primary-light);
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.15);
}

.address-header, .store-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.name, .store-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-color-dark);
  font-family: 'Prompt', sans-serif;
}

.phone {
  font-size: 15px;
  color: var(--primary-color);
  font-weight: 500;
}

.address-detail, .store-address, .store-hours {
  font-size: 14px;
  color: var(--text-color-medium);
  line-height: 1.6;
  margin-bottom: 8px;
}

.call-btn {
  background: linear-gradient(135deg, var(--accent-cream) 0%, white 100%);
  color: var(--primary-dark);
  border: 2px solid var(--primary-light);
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Prompt', sans-serif;
}

.call-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(210, 180, 140, 0.3);
  background: white;
}

.call-btn .icon {
  font-size: 16px;
}

/* 商品列表 */
.goods-list {
  margin-top: 10px;
}

.goods-item {
  display: flex;
  gap: 20px;
  padding: 20px;
  background: white;
  border-radius: 20px;
  margin-bottom: 16px;
  transition: all 0.3s ease;
  border: 2px solid transparent;
}

.goods-item:hover {
  border-color: var(--primary-light);
  transform: translateX(5px);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.1);
}

.goods-image {
  width: 100px;
  height: 100px;
  border-radius: 16px;
  object-fit: cover;
  border: 3px solid var(--accent-cream);
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.2);
}

.goods-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.goods-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-color-dark);
  font-family: 'Prompt', sans-serif;
  margin-bottom: 8px;
  line-height: 1.4;
}

.goods-specs {
  font-size: 13px;
  color: var(--text-color-medium);
  margin-bottom: 15px;
  line-height: 1.5;
}

.goods-specs span {
  background: var(--accent-cream);
  padding: 4px 12px;
  border-radius: 12px;
  margin-right: 8px;
  display: inline-block;
  margin-bottom: 4px;
}

.goods-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.goods-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
}

.goods-quantity {
  font-size: 15px;
  color: var(--text-color-medium);
  font-weight: 500;
  background: var(--accent-cream);
  padding: 6px 16px;
  border-radius: 15px;
}

/* 订单信息 */
.info-list {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.1);
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 20px;
  border-bottom: 2px dashed var(--border-color);
  transition: all 0.3s ease;
}

.info-item:hover {
  background: rgba(245, 240, 225, 0.3);
}

.info-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 15px;
  color: var(--text-color-medium);
  font-weight: 500;
}

.value {
  font-size: 15px;
  color: var(--text-color-dark);
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
}

.value-copy {
  display: flex;
  align-items: center;
  gap: 15px;
}

.copy-icon {
  background: var(--accent-cream);
  border: 2px solid var(--primary-light);
  padding: 6px 16px;
  border-radius: 15px;
  font-size: 13px;
  color: var(--primary-dark);
  cursor: pointer;
  font-weight: 600;
  transition: all 0.3s ease;
  font-family: 'Prompt', sans-serif;
}

.copy-icon:hover {
  background: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.3);
}

/* 评价信息 */
.review-section {
  background: var(--surface-color);
  margin: 20px 24px;
  padding: 24px;
  border-radius: 25px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.08);
  border: 1px solid var(--border-color);
}

.review-card {
  background: white;
  padding: 20px;
  border-radius: 20px;
}

.review-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 15px;
}

.review-scores {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.score-item {
  display: flex;
  align-items: center;
  gap: 10px;
}

.score-label {
  font-size: 13px;
  color: var(--text-color-medium);
}

.stars {
  display: flex;
  gap: 2px;
}

.star {
  font-size: 12px;
  color: #ddd;
}

.star.active {
  color: #ffb347;
}

.review-time {
  font-size: 12px;
  color: var(--text-color-light);
}

.review-content {
  font-size: 14px;
  color: var(--text-color-dark);
  line-height: 1.6;
  margin-bottom: 15px;
}

.review-images {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.review-images img {
  width: 70px;
  height: 70px;
  border-radius: 10px;
  object-fit: cover;
  border: 1px solid var(--border-color);
}

/* 费用明细 */
.amount-list {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.1);
}

.amount-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.amount-item:hover {
  background: rgba(245, 240, 225, 0.3);
}

.amount-item.discount .value {
  color: #ff6b6b;
  font-weight: 700;
}

.amount-item.total {
  background: linear-gradient(135deg, var(--accent-cream) 0%, rgba(255, 248, 220, 0.5) 100%);
  border-top: 3px solid var(--primary-light);
  border-bottom: none;
  margin-top: 10px;
  font-size: 18px;
}

.amount-item.total .label {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-dark);
}

.amount-item.total .value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  text-shadow: 0 2px 4px rgba(139, 69, 19, 0.1);
}

/* 底部操作栏 */
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 90px;
  background: var(--surface-color);
  display: flex;
  align-items: center;
  padding: 0 24px;
  gap: 16px;
  box-shadow: 0 -4px 20px rgba(139, 69, 19, 0.1);
  border-top: 1px solid var(--border-color);
  border-radius: 30px 30px 0 0;
  z-index: 10;
}

.footer-btn {
  flex: 1;
  height: 52px;
  border-radius: 26px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-family: 'Prompt', sans-serif;
  border: 2px solid transparent;
}

.footer-btn.primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
}

.footer-btn.primary:hover:not(:disabled) {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

.footer-btn.primary:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.footer-btn.secondary {
  background: white;
  color: var(--text-color-dark);
  border: 2px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
}

.footer-btn.secondary:hover {
  transform: translateY(-3px);
  border-color: var(--primary-light);
  color: var(--primary-dark);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.15);
}

.footer-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* 弹窗样式 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(5px);
}

.modal-content {
  background: white;
  width: 90%;
  max-width: 400px;
  border-radius: 24px;
  overflow: hidden;
  animation: modal-in 0.3s ease-out;
}

@keyframes modal-in {
  from { transform: translateY(20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.modal-header {
  padding: 20px 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: var(--primary-dark);
}

.close-btn {
  background: none;
  border: none;
  font-size: 24px;
  color: var(--text-color-medium);
  cursor: pointer;
}

.modal-body {
  padding: 24px;
}

.form-item {
  margin-bottom: 20px;
}

.form-item label {
  display: block;
  margin-bottom: 8px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color-dark);
}

.form-item select, .form-item textarea, .form-item input {
  width: 100%;
  padding: 12px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 14px;
  outline: none;
}

.form-item textarea {
  height: 100px;
  resize: none;
}

.hint {
  font-size: 12px;
  color: var(--text-color-medium);
  margin-top: 4px;
  display: block;
}

.modal-footer {
  padding: 16px 24px;
  display: flex;
  gap: 12px;
  border-top: 1px solid var(--border-color);
}

.modal-btn {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  border: none;
}

.modal-btn.primary {
  background: var(--primary-color);
  color: white;
}

.modal-btn.secondary {
  background: var(--background-color);
  color: var(--text-color-dark);
}

/* 响应式调整 */
@media (max-width: 375px) {
  .status-section {
    padding: 30px 20px 25px;
  }

  .status-text {
    font-size: 28px;
  }

  .pickup-code {
    font-size: 48px;
  }

  .location-section, .goods-section, .info-section, .amount-section {
    margin: 16px 20px;
    padding: 20px;
  }

  .goods-image {
    width: 80px;
    height: 80px;
  }

  .footer {
    height: 80px;
    padding: 0 20px;
  }

  .footer-btn {
    height: 48px;
    font-size: 15px;
  }
}
</style>