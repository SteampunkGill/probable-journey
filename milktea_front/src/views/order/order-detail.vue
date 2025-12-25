<template>
  <div class="order-detail-page">
    <!-- 加载状态 -->
    <div class="loading" v-if="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="order" class="content">
      <!-- 订单状态 -->
      <div class="status-section">
        <div class="status-header">
          <h2 class="status-text">{{ order.statusText }}</h2>
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
          <button class="copy-btn" @click="copyText(order.pickupCode)">复制</button>
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
            <button class="call-btn" @click="callPhone(order.store.phone)">
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
          <div class="goods-item" v-for="item in order.items" :key="item.id">
            <img class="goods-image" :src="item.image" />
            <div class="goods-info">
              <h4 class="goods-name">{{ item.name }}</h4>
              <div class="goods-specs" v-if="item.customizations">
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
              <span class="copy-icon" @click="copyText(order.orderNo)">复制</span>
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
          <div class="info-item">
            <span class="label">支付方式</span>
            <span class="value">{{ order.paymentMethodText }}</span>
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
          <div class="amount-item discount" v-if="order.pointsDiscount > 0">
            <span class="label">积分抵扣</span>
            <span class="value">-¥{{ order.pointsDiscount }}</span>
          </div>
          <div class="amount-item total">
            <span class="label">实付款</span>
            <span class="value">¥{{ order.totalAmount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="footer" v-if="!loading && order">
      <!-- 待支付 -->
      <template v-if="order.status === 'pending_payment'">
        <button class="footer-btn secondary" @click="cancelOrder">取消订单</button>
        <button class="footer-btn primary" @click="payOrder">立即支付</button>
      </template>

      <!-- 制作中 -->
      <template v-else-if="order.status === 'processing'">
        <button class="footer-btn secondary" @click="contactService">联系客服</button>
        <button class="footer-btn primary" @click="remindOrder">催单</button>
      </template>

      <!-- 已完成 -->
      <template v-else-if="order.status === 'completed'">
        <button class="footer-btn secondary" @click="reorder">再来一单</button>
        <button class="footer-btn primary" @click="reviewOrder">去评价</button>
      </template>

      <!-- 其他状态 -->
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

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const order = ref(null)
const statusSteps = ref([])
const currentStep = ref(0)

import { orderApi } from '../../utils/api'

onMounted(() => {
  loadOrderDetail()
})

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(route.params.id)
    if (res.code === 200) {
      order.value = res.data
      generateStatusSteps(res.data)
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

const generateStatusSteps = (order) => {
  if (order.deliveryType === 'delivery') {
    statusSteps.value = [
      { key: 'created', title: '订单已提交', time: '14:30' },
      { key: 'paid', title: '支付成功', time: '14:31' },
      { key: 'processing', title: '商家制作中', time: '' },
      { key: 'delivering', title: '配送中', time: '' },
      { key: 'completed', title: '订单完成', time: '' }
    ]
  } else {
    statusSteps.value = [
      { key: 'created', title: '订单已提交', time: '14:30' },
      { key: 'paid', title: '支付成功', time: '14:31' },
      { key: 'processing', title: '商家制作中', time: '' },
      { key: 'ready', title: '已备餐', time: '' },
      { key: 'completed', title: '已取餐', time: '' }
    ]
  }
  currentStep.value = 2 // 制作中
}

const copyText = (text) => {
  navigator.clipboard.writeText(text)
  alert('已复制到剪贴板')
}

const callPhone = (phone) => {
  window.location.href = `tel:${phone}`
}

const cancelOrder = () => {
  if (confirm('确定要取消订单吗？')) {
    alert('订单已取消')
    router.back()
  }
}

const payOrder = () => {
  router.push({ path: '/payment', query: { orderId: order.value.id } })
}

const remindOrder = () => {
  alert('已提醒商家尽快制作')
}

const confirmOrder = () => {
  if (confirm('确认已收到商品吗？')) {
    alert('已确认收货')
    loadOrderDetail()
  }
}

const reviewOrder = () => {
  alert('评价功能开发中')
}

const reorder = () => {
  alert('已添加到购物车')
  router.push('/cart')
}

const contactService = () => {
  window.location.href = 'tel:400-123-4567'
}
</script>

<style scoped>
.order-detail-page {
  min-height: 100vh;
  background: #F8F8F8;
  padding-bottom: 80px;
}

.loading {
  padding-top: 100px;
  text-align: center;
  color: #999;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #EEE;
  border-top: 3px solid #D4A574;
  border-radius: 50%;
  margin: 0 auto 15px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.status-section {
  background: #D4A574;
  color: white;
  padding: 30px 20px;
}

.status-header {
  margin-bottom: 25px;
}

.status-text {
  font-size: 24px;
  font-weight: bold;
  margin-bottom: 5px;
}

.status-hint {
  font-size: 13px;
  opacity: 0.9;
}

.status-steps {
  display: flex;
  justify-content: space-between;
  position: relative;
}

.status-steps::before {
  content: '';
  position: absolute;
  top: 5px;
  left: 10px;
  right: 10px;
  height: 2px;
  background: rgba(255,255,255,0.3);
  z-index: 1;
}

.step {
  position: relative;
  z-index: 2;
  display: flex;
  flex-direction: column;
  align-items: center;
  flex: 1;
}

.step-dot {
  width: 12px;
  height: 12px;
  background: rgba(255,255,255,0.5);
  border-radius: 50%;
  margin-bottom: 8px;
  border: 2px solid #D4A574;
}

.step.active .step-dot {
  background: white;
  box-shadow: 0 0 0 4px rgba(255,255,255,0.2);
}

.step-title {
  font-size: 11px;
  opacity: 0.8;
}

.step.active .step-title {
  opacity: 1;
  font-weight: bold;
}

.step-time {
  font-size: 10px;
  opacity: 0.6;
  margin-top: 2px;
}

.pickup-section {
  margin: -20px 15px 15px;
  position: relative;
  z-index: 10;
}

.pickup-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.05);
}

.pickup-label {
  font-size: 14px;
  color: #999;
}

.pickup-code {
  font-size: 40px;
  font-weight: bold;
  color: #333;
  margin: 10px 0;
  letter-spacing: 2px;
}

.copy-btn {
  background: #F5F5F5;
  border: none;
  padding: 4px 15px;
  border-radius: 12px;
  font-size: 12px;
  color: #666;
  cursor: pointer;
}

.location-section, .goods-section, .info-section, .amount-section {
  background: white;
  margin: 15px;
  padding: 15px;
  border-radius: 12px;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
  border-left: 3px solid #D4A574;
  padding-left: 10px;
}

.address-header {
  margin-bottom: 5px;
}

.name {
  font-size: 16px;
  font-weight: bold;
  margin-right: 10px;
}

.phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  font-size: 13px;
  color: #999;
}

.store-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.store-name {
  font-size: 16px;
  font-weight: bold;
}

.call-btn {
  background: #FDF8F3;
  border: 1px solid #D4A574;
  color: #D4A574;
  padding: 4px 10px;
  border-radius: 15px;
  font-size: 12px;
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
}

.store-address, .store-hours {
  font-size: 13px;
  color: #999;
  display: block;
  margin-top: 4px;
}

.goods-item {
  display: flex;
  gap: 12px;
  margin-bottom: 15px;
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

.goods-specs {
  font-size: 11px;
  color: #999;
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

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 13px;
}

.label {
  color: #999;
}

.value {
  color: #333;
}

.value-copy {
  display: flex;
  align-items: center;
  gap: 10px;
}

.copy-icon {
  color: #D4A574;
  font-size: 12px;
  cursor: pointer;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 13px;
  color: #666;
}

.amount-item.discount .value {
  color: #FF4D4F;
}

.amount-item.total {
  margin-top: 15px;
  padding-top: 15px;
  border-top: 1px solid #F5F5F5;
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.amount-item.total .value {
  color: #D4A574;
  font-size: 20px;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: flex-end;
  padding: 0 15px;
  gap: 12px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}

.footer-btn {
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
}

.footer-btn.primary {
  background: #D4A574;
  color: white;
  border: none;
}

.footer-btn.secondary {
  background: white;
  color: #666;
  border: 1px solid #DDD;
}
</style>