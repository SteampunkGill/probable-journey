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
        @click="router.push('/order?mode=select')"
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
            <img class="goods-image" :src="formatImageUrl(item.image)" />
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
      <div class="section coupon-section" @click="router.push(`/coupon?mode=select&amount=${subtotal}`)">
        <span class="label">🎫 优惠券</span>
        <div class="value">
          <div v-if="selectedCoupon" class="selected-coupon-info">
            <span class="coupon-name">{{ selectedCoupon.name }}</span>
            <span class="coupon-tag" v-if="isBestCoupon">最优</span>
          </div>
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
import { addressApi, couponApi, pointsApi, orderApi, cartApi, userApi, storeApi } from '../../utils/api'
import { formatImageUrl } from '../../utils/util'

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
const availablePoints = ref(0)
const remark = ref('')
const estimatedDeliveryTime = ref('预计30分钟送达')

const isBestCoupon = computed(() => {
  if (!selectedCoupon.value || availableCoupons.value.length === 0) return false
  const amount = parseFloat(subtotal.value)
  
  const calculateDiscount = (coupon) => {
    let discount = 0
    const type = coupon.type?.toUpperCase()
    if (type === 'REDUCTION' || type === 'DISCOUNT_FIXED' || coupon.type === 'discount') {
      discount = coupon.value
    } else if (type === 'DISCOUNT' || type === 'PERCENTAGE' || coupon.type === 'percentage') {
      const rate = coupon.value < 1 ? coupon.value : coupon.value / 100
      discount = amount * (1 - rate)
    }
    return discount
  }

  const currentDiscount = calculateDiscount(selectedCoupon.value)
  const maxDiscount = Math.max(...availableCoupons.value.map(calculateDiscount))
  
  return currentDiscount >= maxDiscount
})

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

const loadOrderData = async () => {
  loading.value = true
  try {
    const items = localStorage.getItem('checkoutItems')
    if (items) {
      orderItems.value = JSON.parse(items).map(item => {
        // 兼容后端不同的字段名
        const imageUrl = item.image || item.productImage || item.product?.mainImageUrl || item.product?.imageUrl
        const name = item.name || item.productName || item.product?.name
        return {
          ...item,
          name: name,
          image: formatImageUrl(imageUrl)
        }
      })
    }
    const savedRemark = localStorage.getItem('orderRemark')
    if (savedRemark) {
      remark.value = savedRemark
    }
    
    // 并行获取地址、优惠券和用户信息（积分）
    const [addressRes, couponRes, profileRes] = await Promise.all([
      addressApi.getAddressList(),
      couponApi.getMyCoupons(),
      userApi.getUserProfile()
    ])
    
    if (addressRes.code === 200 && addressRes.data.length > 0) {
      selectedAddress.value = addressRes.data.find(a => a.isDefault) || addressRes.data[0]
    }

    // 获取默认门店（用于外卖配送）
    const storeRes = await storeApi.getNearbyStores({ limit: 1 })
    if (storeRes.code === 200 && storeRes.data?.length > 0) {
      selectedStore.value = storeRes.data[0]
    }
    
    if (couponRes.code === 200) {
      const amount = parseFloat(subtotal.value)
      // 过滤出当前订单可用的优惠券
      availableCoupons.value = couponRes.data.filter(c =>
        c.status === 'UNUSED' && amount >= c.minAmount
      )
      
      // 优先从缓存获取用户手动选择的优惠券
      const savedCoupon = localStorage.getItem('selectedCoupon')
      if (savedCoupon) {
        const coupon = JSON.parse(savedCoupon)
        // 检查该优惠券是否在可用列表中
        const isAvailable = availableCoupons.value.find(c => c.id === coupon.id)
        if (isAvailable) {
          selectedCoupon.value = isAvailable
        }
      }

      // 如果没有手动选择或手动选择的不可用，则自动匹配最佳优惠券
      if (!selectedCoupon.value && availableCoupons.value.length > 0) {
        // 计算每张优惠券的实际折扣金额并排序
        const sortedCoupons = [...availableCoupons.value].map(coupon => {
          let discount = 0
          // 兼容后端字段名：REDUCTION/DISCOUNT 或 模拟数据中的 discount/percentage
          const type = coupon.type?.toUpperCase()
          if (type === 'REDUCTION' || type === 'DISCOUNT_FIXED' || coupon.type === 'discount') {
            discount = coupon.value
          } else if (type === 'DISCOUNT' || type === 'PERCENTAGE' || coupon.type === 'percentage') {
            // 如果是折扣率（如0.8表示8折），折扣金额 = 总额 * (1 - 0.8)
            // 如果是百分比（如80表示8折），折扣金额 = 总额 * (1 - 80/100)
            const rate = coupon.value < 1 ? coupon.value : coupon.value / 100
            discount = amount * (1 - rate)
          }
          return { ...coupon, _discount: discount }
        }).sort((a, b) => b._discount - a._discount)

        // 默认选中折扣最大的那张
        selectedCoupon.value = sortedCoupons[0]
        
        // 记录前三张最优优惠券供 UI 展示（可选）
        const topThree = sortedCoupons.slice(0, 3)
        console.log('推荐的前三张优惠券:', topThree)
      }
    }
    
    if (profileRes.code === 200) {
      availablePoints.value = profileRes.data.points || 0
    }
    
    // 获取预计时间
    // 如果有订单号可以调用 orderApi.getEstimatedTime，这里是下单前，可以根据门店获取
    estimatedDeliveryTime.value = '预计30分钟送达'
    
  } catch (error) {
    console.error('加载结算数据失败:', error)
  } finally {
    loading.value = false
  }
}

const submitOrder = async () => {
  if (submitting.value) return
  
  if (deliveryType.value === 'delivery' && !selectedAddress.value) {
    alert('请选择收货地址')
    return
  }
  
  if (deliveryType.value === 'pickup' && !selectedStore.value) {
    alert('请选择自提门店')
    return
  }
  
  submitting.value = true
  try {
    const orderData = {
      items: orderItems.value.map(item => ({
        // 确保获取的是商品ID而不是购物车项ID
        productId: item.productId || item.product?.id || item.id,
        quantity: item.quantity,
        price: item.price,
        specId: item.specId,
        customizations: item.customizations
      })),
      deliveryType: deliveryType.value.toUpperCase(),
      addressId: selectedAddress.value?.id,
      storeId: selectedStore.value?.id,
      couponId: selectedCoupon.value?.id,
      usePoints: usePoints.value ? pointsToUse.value : 0,
      balanceDiscountAmount: 0, // 暂时传0，后续可根据余额抵扣逻辑调整
      remark: remark.value,
      // 确保 deliveryFee 始终有值，即使是 0
      deliveryFee: parseFloat(deliveryFee.value || 0),
      packagingFee: parseFloat(packagingFee.value || 0),
      totalAmount: parseFloat(totalAmount.value || 0)
    }
    
    const res = await orderApi.createOrder(orderData)
    if (res.code === 200) {
      const orderNo = res.data.orderNo
      alert('订单提交成功！')
      // 清除结算缓存
      localStorage.removeItem('checkoutItems')
      localStorage.removeItem('selectedCoupon')
      localStorage.removeItem('orderRemark')
      cartStore.clearCart()
      router.push(`/payment?orderNo=${orderNo}&amount=${totalAmount.value}`)
    } else {
      alert(res.message || '提交订单失败')
    }
  } catch (error) {
    console.error('提交订单失败:', error)
    alert('提交订单失败，请稍后重试')
  } finally {
    submitting.value = false
  }
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
  background: var(--background-color);
  font-family: 'Noto Sans KR', sans-serif;
  padding-bottom: 100px;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  height: 100vh;
  color: var(--text-color-medium);
}

.loading-spinner {
  width: 60px;
  height: 60px;
  border: 4px solid var(--border-color);
  border-top: 4px solid var(--primary-color);
  border-radius: 50%;
  margin-bottom: 20px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.content {
  padding-bottom: 30px;
}

.section {
  background: var(--surface-color);
  margin: 20px 24px;
  padding: 24px;
  border-radius: 25px;
  box-shadow: 0 4px 15px rgba(139, 69, 19, 0.08);
  border: 1px solid var(--border-color);
  transition: all 0.3s ease;
}

.section:hover {
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.12);
}

/* 配送方式 */
.delivery-type {
  padding: 0;
  overflow: hidden;
  border: 2px solid var(--accent-cream);
}

.type-tabs {
  display: flex;
  height: 100%;
}

.tab {
  flex: 1;
  padding: 24px 20px;
  text-align: center;
  font-size: 16px;
  background: rgba(255, 255, 255, 0.5);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  font-weight: 500;
  font-family: 'Prompt', sans-serif;
}

.tab:hover {
  background: white;
  transform: translateY(-3px);
}

.tab.active {
  background: white;
  color: var(--primary-dark);
  font-weight: 700;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.1);
  position: relative;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--primary-light) 100%);
}

.tab .icon {
  font-size: 28px;
  margin-bottom: 4px;
}

/* 地址/门店选择 */
.address-section, .store-section {
  display: flex;
  align-items: center;
  gap: 20px;
  cursor: pointer;
  position: relative;
  padding-right: 60px;
  min-height: 120px;
}

.address-section:hover, .store-section:hover {
  border-color: var(--primary-light);
  transform: translateY(-3px);
}

.address-card, .store-card {
  flex: 1;
}

.address-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.name {
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

.address-detail {
  font-size: 14px;
  color: var(--text-color-medium);
  line-height: 1.6;
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.address-detail .icon {
  font-size: 16px;
  color: var(--primary-light);
  margin-top: 2px;
}

.store-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-color-dark);
  font-family: 'Prompt', sans-serif;
  margin-bottom: 8px;
}

.store-address {
  font-size: 14px;
  color: var(--text-color-medium);
  line-height: 1.6;
  margin-bottom: 4px;
}

.store-time {
  font-size: 13px;
  color: var(--text-color-light);
}

.address-empty, .store-empty {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  color: var(--text-color-medium);
  padding: 20px 0;
}

.address-empty .icon, .store-empty .icon {
  font-size: 32px;
  opacity: 0.5;
}

.arrow {
  position: absolute;
  right: 30px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 24px;
  color: var(--text-color-light);
  font-weight: bold;
  opacity: 0.7;
}

/* 预计送达时间 */
.time-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  background: linear-gradient(135deg, var(--accent-cream) 0%, rgba(255, 248, 220, 0.3) 100%);
  border: 2px solid var(--primary-light);
}

.time-section .label {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
}

.time-section .time {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color);
  font-family: 'Prompt', sans-serif;
  background: white;
  padding: 8px 16px;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.2);
}

/* 商品列表 */
.section-title {
  font-size: 18px;
  font-weight: 700;
  margin-bottom: 20px;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  padding-left: 12px;
  border-left: 4px solid var(--primary-color);
}

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
  font-size: 16px;
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

.goods-price {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.goods-price .price {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
}

.goods-price .quantity {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
  background: var(--accent-cream);
  padding: 6px 16px;
  border-radius: 15px;
}

/* 优惠券 */
.coupon-section {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 20px 24px;
  position: relative;
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.5) 0%, rgba(255, 255, 255, 0.3) 100%);
}

.coupon-section:hover {
  background: linear-gradient(135deg, var(--accent-cream) 0%, white 100%);
  border-color: var(--primary-light);
}

.coupon-section .label {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  display: flex;
  align-items: center;
  gap: 8px;
}

.coupon-section .value {
  display: flex;
  align-items: center;
  gap: 12px;
}

.selected-coupon-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.coupon-tag {
  background: #ff4d4f;
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  line-height: 1;
  font-weight: bold;
}

.coupon-section .placeholder {
  color: var(--text-color-light);
  font-size: 14px;
  font-weight: 500;
}

.coupon-section .count {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff5252 100%);
  color: white;
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 12px;
  font-weight: 700;
  font-family: 'Prompt', sans-serif;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

/* 积分抵扣 */
.points-section {
  padding: 20px 24px;
}

.points-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.points-header .label {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  display: flex;
  align-items: center;
  gap: 8px;
}

.points-header input[type="checkbox"] {
  width: 24px;
  height: 24px;
  border-radius: 6px;
  border: 2px solid var(--primary-light);
  appearance: none;
  cursor: pointer;
  position: relative;
  transition: all 0.3s ease;
}

.points-header input[type="checkbox"]:checked {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.points-header input[type="checkbox"]:checked::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 14px;
  font-weight: bold;
}

.points-input {
  margin-top: 15px;
  padding: 20px;
  background: white;
  border-radius: 20px;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.1);
  border: 2px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
}

.points-input input[type="number"] {
  width: 80px;
  padding: 8px 12px;
  border: 2px solid var(--primary-light);
  border-radius: 12px;
  font-size: 14px;
  font-weight: 600;
  color: var(--primary-dark);
  text-align: center;
  font-family: 'Prompt', sans-serif;
  transition: all 0.3s ease;
}

.points-input input[type="number"]:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.15);
}

/* 备注 */
.remark-section {
  padding: 20px 24px;
}

.remark-label {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  margin-bottom: 15px;
  display: flex;
  align-items: center;
  gap: 8px;
}

.remark-input {
  width: 100%;
  height: 100px;
  background: white;
  border: 2px solid var(--border-color);
  border-radius: 20px;
  padding: 16px;
  font-size: 14px;
  color: var(--text-color-dark);
  font-family: 'Noto Sans KR', sans-serif;
  resize: none;
  box-sizing: border-box;
  transition: all 0.3s ease;
  font-weight: 500;
}

.remark-input:focus {
  outline: none;
  border-color: var(--primary-light);
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.1);
}

.remark-input::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

/* 费用明细 */
.amount-list {
  background: white;
  border-radius: 20px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.1);
  border: 2px solid var(--accent-cream);
}

.amount-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 18px 24px;
  border-bottom: 2px dashed var(--border-color);
  transition: all 0.3s ease;
}

.amount-item:hover {
  background: rgba(245, 240, 225, 0.3);
}

.amount-item:last-child {
  border-bottom: none;
}

.amount-item .label {
  font-size: 15px;
  color: var(--text-color-medium);
  font-weight: 500;
}

.amount-item .value {
  font-size: 15px;
  color: var(--text-color-dark);
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
}

.amount-item.discount .value {
  color: #ff6b6b;
  font-weight: 700;
}

/* 底部提交栏 */
.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 90px;
  background: var(--surface-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 -4px 20px rgba(139, 69, 19, 0.1);
  border-top: 1px solid var(--border-color);
  border-radius: 30px 30px 0 0;
  z-index: 10;
}

.total-amount {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.total-amount .label {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
}

.total-amount .amount {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  text-shadow: 0 2px 4px rgba(139, 69, 19, 0.1);
}

.submit-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  padding: 16px 48px;
  border-radius: 25px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-family: 'Prompt', sans-serif;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
  min-width: 180px;
}

.submit-btn:hover:not(.disabled) {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

.submit-btn:active:not(.disabled) {
  transform: translateY(0) scale(0.98);
}

.submit-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none !important;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .section {
    margin: 16px 20px;
    padding: 20px;
    border-radius: 20px;
  }

  .tab {
    padding: 20px 16px;
    font-size: 14px;
  }

  .address-section, .store-section {
    padding-right: 50px;
    min-height: 100px;
  }

  .goods-image {
    width: 80px;
    height: 80px;
  }

  .footer {
    height: 80px;
    padding: 0 20px;
  }

  .total-amount .amount {
    font-size: 24px;
  }

  .submit-btn {
    padding: 14px 36px;
    font-size: 16px;
    min-width: 150px;
  }
}
</style>