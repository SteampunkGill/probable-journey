<template>
  <div class="coupon-page">
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

    <div class="coupon-list" v-if="coupons.length > 0">
      <div
        class="coupon-item"
        :class="{
          'recommend-item': item._recommend,
          'best-item': item._recommendText === '省最多',
          'disabled-item': item._isUsable === false
        }"
        v-for="item in coupons"
        :key="item.id"
      >
        <div class="recommend-tag" v-if="item._recommend">{{ item._recommendText }}</div>
        <div class="coupon-left">
          <div class="value">¥{{ item.value }}</div>
          <div class="condition">满{{ item.minAmount }}元可用</div>
        </div>
        <div class="coupon-right">
          <h3 class="name">{{ item.name }}</h3>
          <p class="desc">{{ item.description }}</p>
          <p class="expire">有效期至{{ item.expireTime }}</p>
          <button
            class="use-btn"
            v-if="activeTab === 'available'"
            @click="useCoupon(item.id)"
          >{{ mode === 'select' ? '确认选择' : '立即使用' }}</button>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">🧋</div>
      <p>暂无优惠券</p>
      <p class="empty-hint">快去领取专属奶茶优惠吧！</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { couponApi } from '../../utils/api'

const router = useRouter()
const route = useRoute()

const mode = ref(route.query.mode || 'list')

const tabs = [
  { key: 'available', name: '可使用' },
  { key: 'used', name: '已使用' },
  { key: 'expired', name: '已过期' }
]
const activeTab = ref('available')
const coupons = ref([])
const loading = ref(false)

// 获取订单金额用于计算折扣
const orderAmount = ref(parseFloat(route.query.amount || 0))

onMounted(() => {
  loadCoupons()
})

const switchTab = (key) => {
  activeTab.value = key
  loadCoupons()
}

const calculateDiscount = (coupon) => {
  if (!orderAmount.value) return 0
  let discount = 0
  const type = coupon.type?.toUpperCase()
  if (type === 'REDUCTION' || type === 'DISCOUNT_FIXED' || coupon.type === 'discount') {
    discount = coupon.value
  } else if (type === 'DISCOUNT' || type === 'PERCENTAGE' || coupon.type === 'percentage') {
    const rate = coupon.value < 1 ? coupon.value : coupon.value / 100
    discount = orderAmount.value * (1 - rate)
  }
  return discount
}

const loadCoupons = async () => {
  loading.value = true
  try {
    const res = await couponApi.getMyCoupons()
    if (res.code === 200) {
      const allCoupons = res.data || []
      const statusMap = {
        'available': 'UNUSED',
        'used': 'USED',
        'expired': 'EXPIRED'
      }
      let filtered = allCoupons.filter(c => c.status === statusMap[activeTab.value])
      
      // 如果是选择模式且有订单金额，进行智能排序和推荐
      if (mode.value === 'select' && activeTab.value === 'available' && orderAmount.value > 0) {
        filtered = filtered.map(c => ({
          ...c,
          _discount: calculateDiscount(c),
          _isUsable: orderAmount.value >= c.minAmount
        }))

        // 排序：可用优先，折扣金额降序
        filtered.sort((a, b) => {
          if (a._isUsable !== b._isUsable) return b._isUsable ? 1 : -1
          return b._discount - a._discount
        })

        // 标记前三名
        const usableCoupons = filtered.filter(c => c._isUsable)
        usableCoupons.slice(0, 3).forEach((c, index) => {
          c._recommend = true
          c._recommendText = index === 0 ? '省最多' : '推荐'
        })
      }
      
      coupons.value = filtered
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
  } finally {
    loading.value = false
  }
}

const useCoupon = (id) => {
  if (mode.value === 'select') {
    const coupon = coupons.value.find(c => c.id === id)
    if (coupon && coupon._isUsable === false) {
      alert('订单金额未满' + coupon.minAmount + '元，不可使用该优惠券')
      return
    }
    localStorage.setItem('selectedCoupon', JSON.stringify(coupon))
    router.back()
  } else {
    router.push('/order')
  }
}
</script>
<style scoped>
.coupon-page {
  min-height: 100vh;
  background-color: var(--background-color);
  padding: var(--spacing-md);
  font-family: 'Nunito', 'Noto Sans KR', -apple-system, BlinkMacSystemFont, sans-serif;
}

.tabs {
  display: flex;
  background-color: var(--surface-color);
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-xs);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-sm);
  position: sticky;
  top: var(--spacing-md);
  z-index: 10;
  backdrop-filter: blur(10px);
  border: 2px solid var(--border-color);
}

.tab {
  flex: 1;
  text-align: center;
  padding: var(--spacing-md) 0;
  font-size: 15px;
  color: var(--text-color-medium);
  position: relative;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
  letter-spacing: 0.02em;
  opacity: 0.8;
  transition: all var(--transition-normal) ease-out;
  border-radius: var(--border-radius-lg);
  margin: 0 var(--spacing-xs);
}

.tab:hover {
  opacity: 1;
  background-color: rgba(255, 248, 220, 0.3);
  transform: translateY(-1px);
}

.tab.active {
  color: var(--primary-color);
  font-weight: 700;
  opacity: 1;
  background-color: var(--accent-cream);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
  transform: translateY(-2px);
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 20%;
  right: 20%;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--accent-pink) 100%);
  border-radius: var(--border-radius-sm);
  box-shadow: 0 2px 6px rgba(160, 82, 45, 0.25);
}

.coupon-list {
  padding: var(--spacing-sm);
}

.coupon-item {
  background-color: var(--surface-color);
  border-radius: var(--border-radius-lg);
  display: flex;
  margin-bottom: var(--spacing-lg);
  overflow: hidden;
  box-shadow: var(--shadow-md);
  border: 2px solid var(--border-color);
  backdrop-filter: blur(8px);
  transition: all var(--transition-normal) ease-out;
  position: relative;
}

.coupon-item:hover {
  transform: translateY(-6px) scale(1.02);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-light);
}

.recommend-item {
  border-color: var(--accent-pink);
  background-color: rgba(255, 192, 203, 0.05);
}

.best-item {
  border-color: #ff4d4f;
  box-shadow: 0 0 15px rgba(255, 77, 79, 0.2);
}

.disabled-item {
  opacity: 0.6;
  filter: grayscale(0.5);
}

.recommend-tag {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #ff7875 0%, #ff4d4f 100%);
  color: white;
  font-size: 12px;
  padding: 4px 12px;
  border-radius: 0 0 0 12px;
  z-index: 2;
  font-weight: bold;
  box-shadow: -2px 2px 5px rgba(0,0,0,0.1);
}

.best-item .recommend-tag {
  background: linear-gradient(135deg, #f5222d 0%, #cf1322 100%);
}

.coupon-left {
  width: 140px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: var(--accent-cream);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-lg) 0;
  position: relative;
  overflow: hidden;
}

.coupon-left::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255,255,255,0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  opacity: 0.3;
  animation: bubble-float 20s linear infinite;
}

@keyframes bubble-float {
  0% { transform: translateY(0) rotate(0deg); }
  100% { transform: translateY(-20px) rotate(360deg); }
}

.value {
  font-size: 36px;
  font-weight: 800;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  text-shadow: 0 2px 4px rgba(0,0,0,0.2);
  position: relative;
  z-index: 1;
}

.condition {
  font-size: 13px;
  opacity: 0.95;
  margin-top: var(--spacing-xs);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.03em;
  font-weight: 500;
  background-color: rgba(255, 255, 255, 0.2);
  padding: 6px 14px;
  border-radius: var(--border-radius-sm);
  position: relative;
  z-index: 1;
}

.coupon-right {
  flex: 1;
  padding: var(--spacing-lg);
  position: relative;
  background-color: rgba(255, 255, 255, 0.7);
}

.name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-sm);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.name::before {
  content: '🍵';
  font-size: 16px;
}

.desc {
  font-size: 14px;
  color: var(--text-color-medium);
  margin-bottom: var(--spacing-md);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  opacity: 0.9;
  line-height: 1.5;
  padding-left: var(--spacing-md);
  border-left: 3px solid var(--accent-pink);
  background-color: rgba(255, 192, 203, 0.1);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: 0 var(--border-radius-md) var(--border-radius-md) 0;
}

.expire {
  font-size: 13px;
  color: var(--primary-dark);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  opacity: 0.8;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  margin-top: var(--spacing-md);
}

.expire::before {
  content: '⏰';
  font-size: 14px;
}

.use-btn {
  position: absolute;
  right: var(--spacing-lg);
  bottom: var(--spacing-lg);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: var(--accent-cream);
  border: none;
  padding: var(--spacing-sm) var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  font-size: 14px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 700;
  letter-spacing: 0.03em;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.3);
  transition: all var(--transition-normal) ease-out;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.use-btn::before {
  content: '🎁';
  font-size: 16px;
}

.use-btn:hover {
  transform: translateY(-3px) scale(1.08);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.4);
  background: linear-gradient(135deg, var(--primary-dark) 0%, var(--primary-color) 100%);
}

.use-btn:active {
  transform: translateY(0) scale(0.98);
  box-shadow: 0 2px 6px rgba(160, 82, 45, 0.3);
}

.empty-state {
  padding-top: 80px;
  text-align: center;
  color: var(--text-color-medium);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  opacity: 0.7;
  padding: var(--spacing-xl);
  margin-top: var(--spacing-xl);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: var(--spacing-lg);
  opacity: 0.4;
  filter: grayscale(0.7);
  animation: float 3s ease-in-out infinite;
}

.empty-hint {
  font-size: 14px;
  color: var(--text-color-light);
  margin-top: var(--spacing-sm);
  opacity: 0.6;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-12px); }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .coupon-page {
    padding: var(--spacing-sm);
  }
  
  .tabs {
    border-radius: var(--border-radius-lg);
    padding: 4px;
  }
  
  .tab {
    padding: var(--spacing-sm) 0;
    font-size: 14px;
    margin: 0 2px;
  }
  
  .coupon-item {
    flex-direction: column;
  }
  
  .coupon-left {
    width: 100%;
    padding: var(--spacing-md) 0;
    flex-direction: row;
    justify-content: space-around;
  }
  
  .value {
    font-size: 32px;
  }
  
  .condition {
    font-size: 12px;
  }
  
  .use-btn {
    position: relative;
    right: auto;
    bottom: auto;
    margin-top: var(--spacing-md);
    width: 100%;
    justify-content: center;
  }
}
</style>