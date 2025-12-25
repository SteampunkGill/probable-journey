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
      <div class="coupon-item" v-for="item in coupons" :key="item.id">
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
          >立即使用</button>
        </div>
      </div>
    </div>

    <div class="empty-state" v-else>
      <div class="empty-icon">🎫</div>
      <p>暂无优惠券</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { couponApi } from '../../utils/api'

const router = useRouter()

const tabs = [
  { key: 'available', name: '可使用' },
  { key: 'used', name: '已使用' },
  { key: 'expired', name: '已过期' }
]
const activeTab = ref('available')
const coupons = ref([])
const loading = ref(false)

onMounted(() => {
  loadCoupons()
})

const switchTab = (key) => {
  activeTab.value = key
  loadCoupons()
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
      coupons.value = allCoupons.filter(c => c.status === statusMap[activeTab.value])
    }
  } catch (error) {
    console.error('加载优惠券失败:', error)
  } finally {
    loading.value = false
  }
}

const useCoupon = (id) => {
  router.push('/order')
}
</script>

<style scoped>
.coupon-page {
  min-height: 100vh;
  background: #F8F8F8;
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
  left: 20%;
  right: 20%;
  height: 3px;
  background: #D4A574;
  border-radius: 3px;
}

.coupon-list {
  padding: 15px;
}

.coupon-item {
  background: white;
  border-radius: 12px;
  display: flex;
  margin-bottom: 15px;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.coupon-left {
  width: 100px;
  background: #D4A574;
  color: white;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 15px 0;
}

.value {
  font-size: 24px;
  font-weight: bold;
}

.condition {
  font-size: 11px;
  opacity: 0.9;
  margin-top: 4px;
}

.coupon-right {
  flex: 1;
  padding: 15px;
  position: relative;
}

.name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
  margin-bottom: 5px;
}

.desc {
  font-size: 12px;
  color: #999;
  margin-bottom: 10px;
}

.expire {
  font-size: 11px;
  color: #CCC;
}

.use-btn {
  position: absolute;
  right: 15px;
  bottom: 15px;
  background: #D4A574;
  color: white;
  border: none;
  padding: 5px 15px;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
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
</style>