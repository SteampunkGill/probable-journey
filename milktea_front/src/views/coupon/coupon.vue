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
const activeTab = ref('center')
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
  background: linear-gradient(135deg, #f8f4e6 0%, #f0f0f0 100%);
}

.tabs {
  display: flex;
  background: rgba(255, 255, 255, 0.9);
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(5px);
  border-bottom: 1px solid rgba(46, 92, 138, 0.1);
}

.tab {
  flex: 1;
  text-align: center;
  padding: 20px 0;
  font-size: 15px;
  color: #2e5c8a;
  position: relative;
  cursor: pointer;
  font-family: "STKaiti", "KaiTi", "楷体", cursive;
  letter-spacing: 0.05em;
  opacity: 0.7;
  transition: all 0.3s ease;
}

.tab.active {
  color: #1687a7;
  font-weight: bold;
  opacity: 1;
}

.tab.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 25%;
  right: 25%;
  height: 2px;
  background: linear-gradient(90deg, #1687a7 0%, #2e5c8a 100%);
  border-radius: 2px;
}

.coupon-list {
  padding: 20px;
}

.coupon-item {
  background: rgba(255, 255, 255, 0.9);
  border-radius: 2px;
  display: flex;
  margin-bottom: 20px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(22, 135, 167, 0.05);
  border: 1px solid rgba(46, 92, 138, 0.08);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease;
}

.coupon-left {
  width: 120px;
  background: linear-gradient(135deg, #1687a7 0%, #2e5c8a 100%);
  color: #f8f4e6;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 20px 0;
}

.value {
  font-size: 28px;
  font-weight: bold;
  font-family: "STKaiti", "KaiTi", "楷体", cursive;
  letter-spacing: 0.05em;
}

.condition {
  font-size: 12px;
  opacity: 0.9;
  margin-top: 6px;
  font-family: "Microsoft YaHei", "SimSun", "宋体", serif;
  letter-spacing: 0.03em;
}

.coupon-right {
  flex: 1;
  padding: 20px;
  position: relative;
}

.name {
  font-size: 16px;
  font-weight: bold;
  color: #3a3a3a;
  margin-bottom: 8px;
  font-family: "STKaiti", "KaiTi", "楷体", cursive;
  letter-spacing: 0.05em;
}

.desc {
  font-size: 13px;
  color: #2c9678;
  margin-bottom: 12px;
  font-family: "Microsoft YaHei", "SimSun", "宋体", serif;
  letter-spacing: 0.03em;
  opacity: 0.8;
}

.expire {
  font-size: 12px;
  color: #d6b85a;
  font-family: "Microsoft YaHei", "SimSun", "宋体", serif;
  letter-spacing: 0.03em;
  opacity: 0.7;
}

.use-btn {
  position: absolute;
  right: 20px;
  bottom: 20px;
  background: linear-gradient(135deg, #1687a7 0%, #2e5c8a 100%);
  color: #f8f4e6;
  border: none;
  padding: 6px 20px;
  border-radius: 2px;
  font-size: 13px;
  cursor: pointer;
  font-family: "STKaiti", "KaiTi", "楷体", cursive;
  letter-spacing: 0.03em;
  box-shadow: 0 2px 8px rgba(22, 135, 167, 0.2);
  transition: all 0.3s ease;
}

.empty-state {
  padding-top: 120px;
  text-align: center;
  color: #2c9678;
  font-family: "STKaiti", "KaiTi", "楷体", cursive;
  letter-spacing: 0.05em;
  opacity: 0.6;
}

.empty-icon {
  font-size: 72px;
  margin-bottom: 24px;
  opacity: 0.3;
  filter: grayscale(0.8);
}
</style>