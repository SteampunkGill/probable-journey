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
        :class="{ active: paymentMethod === 'alipay' }"
        @click="paymentMethod = 'alipay'"
      >
        <div class="method-info">
          <span class="method-icon">💳</span>
          <span class="method-name">支付宝支付</span>
        </div>
        <div class="radio" :class="{ checked: paymentMethod === 'alipay' }"></div>
      </div>

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
            <span class="balance">余额：¥{{ userBalance.toFixed(2) }}</span>
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
import { orderApi, authApi, paymentApi } from '@/utils/api'

const route = useRoute()
const router = useRouter()

const orderNo = ref(route.query.orderNo)
const order = ref(null)
const paymentMethod = ref('alipay') // 接口文档提到支付宝沙箱
const paying = ref(false)
const countdown = ref(15 * 60)
const userBalance = ref(0)
let timer = null

onMounted(() => {
  loadOrderDetail()
  loadUserBalance()
  startCountdown()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})

const loadOrderDetail = async () => {
  try {
    const res = await orderApi.getOrderDetail(orderNo.value)
    if (res.code === 200) {
      order.value = res.data
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  }
}

const loadUserBalance = async () => {
  try {
    const res = await authApi.getUserProfile()
    if (res.code === 200) {
      userBalance.value = res.data.balance || 0
    }
  } catch (error) {
    console.error('加载余额失败:', error)
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
  
  try {
    // 调用后端确认支付接口，同步订单状态
    const res = await paymentApi.confirmPayment(orderNo.value, paymentMethod.value.toUpperCase())
    if (res.code === 200) {
      alert('支付成功！')
      router.push({
        path: '/order-detail/' + order.value.orderNo,
        query: { status: 'success' }
      })
    } else {
      alert(res.message || '支付失败')
    }
  } catch (error) {
    console.error('支付请求失败:', error)
    alert('支付系统繁忙，请稍后重试')
  } finally {
    paying.value = false
  }
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
  background: var(--background-color);
  padding: 0 0 100px 0;
  font-family: 'Noto Sans KR', sans-serif;
}

.header {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  padding: 40px 24px 30px;
  border-radius: 0 0 40px 40px;
  color: white;
  text-align: center;
  box-shadow: 0 4px 20px rgba(160, 82, 45, 0.15);
  margin-bottom: 24px;
}

.amount-section {
  margin-bottom: 20px;
}

.amount-section .label {
  font-size: 14px;
  color: var(--accent-cream);
  display: block;
  margin-bottom: 12px;
  opacity: 0.9;
  font-weight: 500;
}

.amount-section .amount {
  font-size: 48px;
  font-weight: 700;
  color: white;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  letter-spacing: 1px;
  font-family: 'Prompt', sans-serif;
}

.countdown {
  background: rgba(255, 255, 255, 0.15);
  padding: 12px 20px;
  border-radius: 25px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  font-size: 14px;
  font-weight: 500;
}

.countdown .time {
  color: var(--accent-cream);
  font-weight: 700;
  font-size: 16px;
  font-family: 'Prompt', sans-serif;
}

.payment-methods, .order-info {
  background: var(--surface-color);
  margin: 0 20px 20px;
  padding: 24px;
  border-radius: 25px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.08);
  border: 1px solid var(--border-color);
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 24px;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  padding-left: 8px;
  border-left: 4px solid var(--primary-color);
  padding-left: 12px;
}

.method-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-radius: 20px;
  margin-bottom: 12px;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: rgba(255, 255, 255, 0.5);
  border: 2px solid transparent;
}

.method-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.12);
  background: white;
}

.method-item.active {
  border-color: var(--primary-color);
  background: white;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.15);
}

.method-info {
  display: flex;
  align-items: center;
  gap: 16px;
}

.method-icon {
  font-size: 32px;
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, var(--accent-cream) 0%, #fff 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.3);
}

.method-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-dark);
  font-family: 'Prompt', sans-serif;
}

.method-detail {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.balance {
  font-size: 13px;
  color: var(--text-color-medium);
  font-weight: 500;
}

.radio {
  width: 24px;
  height: 24px;
  border: 2px solid var(--border-color);
  border-radius: 50%;
  position: relative;
  transition: all 0.3s ease;
  background: white;
}

.radio.checked {
  border-color: var(--primary-color);
  background: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.2);
}

.radio.checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%) rotate(-45deg);
  width: 8px;
  height: 4px;
  border-left: 2px solid white;
  border-bottom: 2px solid white;
}

.info-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 0;
  border-bottom: 1px dashed var(--border-color);
  font-size: 14px;
}

.info-item:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-item .label {
  color: var(--text-color-medium);
  font-weight: 500;
}

.info-item .value {
  color: var(--text-color-dark);
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
}

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
}

.cancel-btn {
  flex: 1;
  height: 52px;
  background: linear-gradient(135deg, #f5f0e1 0%, #e8dccb 100%);
  border: 2px solid var(--border-color);
  border-radius: 26px;
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-medium);
  cursor: pointer;
  transition: all 0.3s ease;
  font-family: 'Prompt', sans-serif;
}

.cancel-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.15);
  border-color: var(--primary-light);
  color: var(--primary-dark);
}

.pay-btn {
  flex: 2;
  height: 52px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: 26px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-family: 'Prompt', sans-serif;
  letter-spacing: 1px;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
}

.pay-btn:hover:not(.disabled) {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

.pay-btn:active:not(.disabled) {
  transform: translateY(0) scale(0.98);
}

.pay-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.15) !important;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .header {
    padding: 30px 20px 25px;
  }

  .amount-section .amount {
    font-size: 42px;
  }

  .payment-methods, .order-info {
    margin: 0 16px 16px;
    padding: 20px;
  }

  .footer {
    height: 80px;
    padding: 0 20px;
  }

  .pay-btn, .cancel-btn {
    height: 48px;
    font-size: 15px;
  }
}
</style>