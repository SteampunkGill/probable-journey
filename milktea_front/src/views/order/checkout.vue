<template>
  <div class="checkout-page">
    <!-- 加载状态 -->
    <div class="loading" v-if="loading">
      <div class="loading-spinner"></div>
      <span>加载中...</span>
    </div>

    <div v-else class="content">
      <!-- 配送方式 -->
      <div class="section delivery-type">
        <div class="type-tabs">
          <div 
            class="tab" 
            :class="{ active: deliveryType === 'delivery' }" 
            @click="deliveryType = 'delivery'"
          >
            <span class="icon">🚚</span>
            <span>外卖配送</span>
          </div>
          <div 
            class="tab" 
            :class="{ active: deliveryType === 'pickup' }" 
            @click="deliveryType = 'pickup'"
          >
            <span class="icon">🏪</span>
            <span>到店自取</span>
          </div>
        </div>
      </div>

      <!-- 收货地址 -->
      <div 
        class="section address-section" 
        v-if="deliveryType === 'delivery'" 
        @click="router.push('/address?mode=select')"
      >
        <div class="address-card" v-if="selectedAddress">
          <div class="address-header">
            <span class="name">{{ selectedAddress.name }}</span>
            <span class="phone">{{ selectedAddress.phone }}</span>
          </div>
          <div class="address-detail">
            <span class="icon">📍</span>
            <span class="text">{{ selectedAddress.province }} {{ selectedAddress.city }} {{ selectedAddress.district }} {{ selectedAddress.detail }}</span>
          </div>
        </div>
        <div class="address-empty" v-else>
          <span class="icon">➕</span>
          <span>请选择收货地址</span>
        </div>
        <span class="arrow">›</span>
      </div>

      <!-- 自提门店 -->
      <div 
        class="section store-section" 
        v-if="deliveryType === 'pickup'" 
        @click="router.push('/store/list?mode=select')"
      >
        <div class="store-card" v-if="selectedStore">
          <div class="store-name">{{ selectedStore.name }}</div>
          <div class="store-address">{{ selectedStore.address }}</div>
          <div class="store-time">营业时间：{{ selectedStore.businessHours }}</div>
        </div>
        <div class="store-empty" v-else>
          <span class="icon">🏪</span>
          <span>请选择自提门店</span>
        </div>
        <span class="arrow">›</span>
      </div>

      <!-- 预计送达时间 -->
      <div class="section time-section" v-if="deliveryType === 'delivery'">
        <span class="label">⏰ 预计送达</span>
        <span class="time">{{ estimatedDeliveryTime }}</span>
      </div>

      <!-- 商品列表 -->
      <div class="section goods-section">
        <div class="section-title">订单商品</div>
        <div class="goods-list">
          <div class="goods-item" v-for="item in orderItems" :key="item.id">
            <img class="goods-image" :src="item.image" />
            <div class="goods-info">
              <div class="goods-name">{{ item.name }}</div>
              <div class="goods-specs" v-if="item.customizations">
                <span>{{ item.customizations.sweetness }} / {{ item.customizations.temperature }}</span>
                <span v-if="item.customizations.toppings?.length > 0"> + {{ item.customizations.toppings.length }}种加料</span>
              </div>
              <div class="goods-price">
                <span class="price">¥{{ item.price }}</span>
                <span class="quantity">×{{ item.quantity }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 优惠券 -->
      <div class="section coupon-section" @click="router.push('/coupon')">
        <span class="label">🎫 优惠券</span>
        <div class="value">
          <span v-if="selectedCoupon">{{ selectedCoupon.name }}</span>
          <span v-else class="placeholder">请选择优惠券</span>
          <span class="count" v-if="availableCoupons.length > 0">{{ availableCoupons.length }}张可用</span>
        </div>
        <span class="arrow">›</span>
      </div>

      <!-- 积分抵扣 -->
      <div class="section points-section">
        <div class="points-header">
          <span class="label">💎 积分抵扣</span>
          <input type="checkbox" v-model="usePoints" />
        </div>
        <div class="points-input" v-if="usePoints">
          <span>使用</span>
          <input type="number" v-model.number="pointsToUse" :max="availablePoints" />
          <span>积分（可用{{ availablePoints }}）</span>
        </div>
      </div>

      <!-- 备注 -->
      <div class="section remark-section">
        <div class="remark-label">💬 订单备注</div>
        <textarea 
          class="remark-input" 
          placeholder="口味、偏好等要求" 
          v-model="remark"
          maxlength="100"
        ></textarea>
      </div>

      <!-- 费用明细 -->
      <div class="section amount-section">
        <div class="section-title">费用明细</div>
        <div class="amount-list">
          <div class="amount-item">
            <span class="label">商品小计</span>
            <span class="value">¥{{ subtotal }}</span>
          </div>
          <div class="amount-item" v-if="deliveryType === 'delivery'">
            <span class="label">配送费</span>
            <span class="value">¥{{ deliveryFee }}</span>
          </div>
          <div class="amount-item">
            <span class="label">包装费</span>
            <span class="value">¥{{ packagingFee }}</span>
          </div>
          <div class="amount-item discount" v-if="couponDiscount > 0">
            <span class="label">优惠券优惠</span>
            <span class="value">-¥{{ couponDiscount }}</span>
          </div>
          <div class="amount-item discount" v-if="pointsDiscount > 0">
            <span class="label">积分抵扣</span>
            <span class="value">-¥{{ pointsDiscount }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部提交栏 -->
    <div class="footer">
      <div class="total-amount">
        <span class="label">实付款：</span>
        <span class="amount">¥{{ totalAmount }}</span>
      </div>
      <button 
        class="submit-btn" 
        :class="{ disabled: submitting }" 
        @click="submitOrder" 
        :disabled="submitting"
      >
        {{ submitting ? '提交中...' : '提交订单' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../store/cart'
import { addressApi, couponApi, pointsApi, orderApi, cartApi } from '../../utils/api'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const submitting = ref(false)
const deliveryType = ref('delivery')
const orderItems = ref([])
const selectedAddress = ref(null)
const selectedStore = ref(null)
const selectedCoupon = ref(null)
const availableCoupons = ref([])
const usePoints = ref(false)
const pointsToUse = ref(0)
const availablePoints = ref(1250)
const remark = ref('')
const estimatedDeliveryTime = ref('18:30')

const subtotal = computed(() => {
  return orderItems.value.reduce((total, item) => total + item.price * item.quantity, 0).toFixed(2)
})

const deliveryFee = computed(() => {
  if (deliveryType.value === 'pickup') return '0.00'
  return (parseFloat(subtotal.value) >= 30 ? 0 : 5).toFixed(2)
})

const packagingFee = computed(() => {
  return (orderItems.value.length * 0.5).toFixed(2)
})

const couponDiscount = computed(() => {
  if (!selectedCoupon.value) return '0.00'
  return selectedCoupon.value.value.toFixed(2)
})

const pointsDiscount = computed(() => {
  if (!usePoints.value) return '0.00'
  return (pointsToUse.value / 100).toFixed(2)
})

const totalAmount = computed(() => {
  const total = parseFloat(subtotal.value) + 
                parseFloat(deliveryFee.value) + 
                parseFloat(packagingFee.value) - 
                parseFloat(couponDiscount.value) - 
                parseFloat(pointsDiscount.value)
  return Math.max(0, total).toFixed(2)
})

const loadOrderData = () => {
  const items = localStorage.getItem('checkoutItems')
  if (items) {
    orderItems.value = JSON.parse(items)
  }
  const savedRemark = localStorage.getItem('orderRemark')
  if (savedRemark) {
    remark.value = savedRemark
  }
  
  // 模拟地址
  selectedAddress.value = {
    name: '张三',
    phone: '13800138000',
    province: '广东省',
    city: '深圳市',
    district: '南山区',
    detail: '腾讯大厦'
  }
  
  loading.value = false
}

const submitOrder = async () => {
  if (submitting.value) return
  
  if (deliveryType.value === 'delivery' && !selectedAddress.value) {
    alert('请选择收货地址')
    return
  }
  
  submitting.value = true
  // 模拟提交
  await new Promise(resolve => setTimeout(resolve, 1500))
  
  const orderId = 'ORD' + Date.now()
  alert('订单提交成功！')
  router.push(`/payment?orderId=${orderId}&amount=${totalAmount.value}`)
  submitting.value = false
}

onMounted(() => {
  loadOrderData()
})

watch(usePoints, (val) => {
  if (val) {
    pointsToUse.value = Math.min(availablePoints.value, Math.floor(parseFloat(subtotal.value) * 100))
  } else {
    pointsToUse.value = 0
  }
})
</script>

<style scoped>
.checkout-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 80px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
}

.section {
  background: white;
  margin: 10px;
  padding: 15px;
  border-radius: 8px;
}

.delivery-type {
  padding: 0;
  overflow: hidden;
}

.type-tabs {
  display: flex;
}

.tab {
  flex: 1;
  padding: 15px;
  text-align: center;
  font-size: 14px;
  background: #F8F8F8;
  cursor: pointer;
}

.tab.active {
  background: white;
  color: #D4A574;
  font-weight: bold;
}

.address-section, .store-section {
  display: flex;
  align-items: center;
  gap: 10px;
  cursor: pointer;
}

.address-card, .store-card {
  flex: 1;
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
  color: #333;
  display: flex;
  gap: 5px;
}

.section-title {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #F5F5F5;
}

.goods-item {
  display: flex;
  gap: 12px;
  margin-bottom: 15px;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: 4px;
  object-fit: cover;
}

.goods-info {
  flex: 1;
}

.goods-name {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 4px;
}

.goods-specs {
  font-size: 11px;
  color: #999;
  margin-bottom: 8px;
}

.goods-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price {
  font-size: 14px;
  color: #333;
}

.quantity {
  font-size: 12px;
  color: #999;
}

.coupon-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
}

.label {
  font-size: 14px;
}

.value {
  display: flex;
  align-items: center;
  gap: 8px;
}

.placeholder {
  color: #999;
  font-size: 13px;
}

.count {
  background: #FF6B6B;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 10px;
}

.points-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points-input {
  margin-top: 10px;
  padding-top: 10px;
  border-top: 1px solid #F5F5F5;
  font-size: 13px;
  color: #666;
  display: flex;
  align-items: center;
  gap: 5px;
}

.points-input input {
  width: 60px;
  border: 1px solid #DDD;
  border-radius: 4px;
  padding: 2px 5px;
}

.remark-input {
  width: 100%;
  height: 60px;
  background: #F8F8F8;
  border: none;
  border-radius: 4px;
  padding: 10px;
  margin-top: 10px;
  font-size: 13px;
  resize: none;
  box-sizing: border-box;
}

.amount-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 13px;
  color: #666;
}

.amount-item.discount .value {
  color: #FF6B6B;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 20px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
}

.total-amount .label {
  font-size: 14px;
  color: #333;
}

.total-amount .amount {
  font-size: 20px;
  font-weight: bold;
  color: #D4A574;
}

.submit-btn {
  background: #D4A574;
  color: white;
  border: none;
  padding: 12px 30px;
  border-radius: 25px;
  font-weight: bold;
  cursor: pointer;
}

.submit-btn.disabled {
  background: #CCC;
  cursor: not-allowed;
}
</style>