<template>
  <div class="payment-page">
    <div class="header">
      <div class="amount-section">
        <span class="label">订单金额</span>
        <h1 class="amount">¥{{ order?.totalAmount || '0.00' }}</h1>
      </div>
      
      <div class="countdown">
        <span>剩余支付时间：</span>
        <span class="time">{{ formatCountdown(countdown) }}</span>
      </div>
    </div>

    <div class="payment-methods">
      <h3 class="section-title">选择支付方式</h3>
      
      <div 
        class="method-item" 
        :class="{ active: paymentMethod === 'wechat' }" 
        @click="paymentMethod = 'wechat'"
      >
        <div class="method-info">
          <span class="method-icon">💬</span>
          <span class="method-name">微信支付</span>
        </div>
        <div class="radio" :class="{ checked: paymentMethod === 'wechat' }"></div>
      </div>
      
      <div 
        class="method-item" 
        :class="{ active: paymentMethod === 'balance' }" 
        @click="paymentMethod = 'balance'"
      >
        <div class="method-info">
          <span class="method-icon">💰</span>
          <div class="method-detail">
            <span class="method-name">余额支付</span>
            <span class="balance">余额：¥86.50</span>
          </div>
        </div>
        <div class="radio" :class="{ checked: paymentMethod === 'balance' }"></div>
      </div>
    </div>

    <div class="order-info" v-if="order">
      <h3 class="section-title">订单信息</h3>
      <div class="info-item">
        <span class="label">订单编号</span>
        <span class="value">{{ order.orderNo }}</span>
      </div>
      <div class="info-item">
        <span class="label">下单时间</span>
        <span class="value">{{ order.createTime }}</span>
      </div>
    </div>

    <div class="footer">
      <button class="cancel-btn" @click="cancelOrder">取消订单</button>
      <button 
        class="pay-btn" 
        :class="{ disabled: paying }" 
        @click="submitPayment" 
        :disabled="paying"
      >
        {{ paying ? '支付中...' : '确认支付' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const order = ref(null)
const paymentMethod = ref('wechat')
const paying = ref(false)
const countdown = ref(15 * 60)
let timer = null

onMounted(() => {
  loadOrderDetail()
  startCountdown()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const loadOrderDetail = () => {
  // 模拟数据
  order.value = {
    id: route.query.orderId,
    orderNo: 'MT' + Date.now(),
    totalAmount: 68.50,
    createTime: new Date().toLocaleString()
  }
}

const startCountdown = () => {
  timer = setInterval(() => {
    if (countdown.value > 0) {
      countdown.value--
    } else {
      clearInterval(timer)
      handleTimeout()
    }
  }, 1000)
}

const handleTimeout = () => {
  alert('订单支付已超时，请重新下单')
  router.push('/order-list')
}

const formatCountdown = (seconds) => {
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}:${secs.toString().padStart(2, '0')}`
}

const submitPayment = async () => {
  if (paying.value) return
  paying.value = true
  
  // 模拟支付过程
  setTimeout(() => {
    paying.value = false
    alert('支付成功！')
    router.push({
      path: '/order-detail/' + order.value.id,
      query: { status: 'success' }
    })
  }, 1500)
}

const cancelOrder = () => {
  if (confirm('确定要取消该订单吗？')) {
    alert('订单已取消')
    router.push('/order-list')
  }
}
</script>

<style scoped>
.payment-page {
  min-height: 100vh;
  background: #F8F8F8;
  padding-bottom: 80px;
}

.header {
  background: white;
  padding: 40px 20px;
  text-align: center;
  margin-bottom: 12px;
}

.amount-section .label {
  font-size: 14px;
  color: #999;
  display: block;
  margin-bottom: 10px;
}

.amount-section .amount {
  font-size: 36px;
  font-weight: bold;
  color: #333;
}

.countdown {
  margin-top: 20px;
  font-size: 13px;
  color: #666;
}

.countdown .time {
  color: #FF4D4F;
  font-weight: bold;
}

.payment-methods, .order-info {
  background: white;
  margin: 12px;
  padding: 20px;
  border-radius: 12px;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 20px;
  color: #333;
}

.method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #F5F5F5;
  cursor: pointer;
}

.method-item:last-child {
  border-bottom: none;
}

.method-info {
  display: flex;
  align-items: center;
  gap: 12px;
}

.method-icon {
  font-size: 24px;
}

.method-name {
  font-size: 15px;
  color: #333;
}

.method-detail {
  display: flex;
  flex-direction: column;
}

.balance {
  font-size: 12px;
  color: #999;
  margin-top: 2px;
}

.radio {
  width: 20px;
  height: 20px;
  border: 2px solid #DDD;
  border-radius: 50%;
  position: relative;
}

.radio.checked {
  border-color: #D4A574;
  background: #D4A574;
}

.radio.checked::after {
  content: '';
  position: absolute;
  top: 4px;
  left: 4px;
  width: 8px;
  height: 4px;
  border-left: 2px solid white;
  border-bottom: 2px solid white;
  transform: rotate(-45deg);
}

.info-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;
  font-size: 13px;
}

.info-item .label {
  color: #999;
}

.info-item .value {
  color: #333;
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
  padding: 0 15px;
  gap: 12px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}

.cancel-btn {
  flex: 1;
  height: 44px;
  background: #F5F5F5;
  border: none;
  border-radius: 22px;
  font-size: 15px;
  color: #666;
  cursor: pointer;
}

.pay-btn {
  flex: 2;
  height: 44px;
  background: #D4A574;
  color: white;
  border: none;
  border-radius: 22px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.pay-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>