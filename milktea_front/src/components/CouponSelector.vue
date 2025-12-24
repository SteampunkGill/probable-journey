<template>
  <div class="coupon-selector">
    <!-- 选择按钮 -->
    <div class="selector-trigger" @click="showSelector">
      <slot name="trigger">
        <div class="default-trigger" :class="{ 'has-value': selectedCoupon }">
          <div class="trigger-content">
            <span class="trigger-text">
              {{ selectedCoupon ? selectedCoupon.name : placeholder }}
            </span>
            <span class="trigger-desc" v-if="selectedCoupon">
              优惠{{ formatValue(selectedCoupon) }}元
            </span>
          </div>
          <i class="iconfont icon-right"></i>
        </div>
      </slot>
    </div>

    <!-- 优惠券选择弹窗 -->
    <div class="coupon-modal" v-if="showModal">
      <div class="modal-mask" @click="hideSelector"></div>
      
      <div class="modal-content">
        <!-- 头部 -->
        <div class="modal-header">
          <span class="modal-title">选择优惠券</span>
          <i class="iconfont icon-close" @click="hideSelector"></i>
        </div>

        <!-- 筛选标签 -->
        <div class="filter-tabs" v-if="couponTypes.length > 1">
          <div class="tab-scroll">
            <div class="tab-list">
              <span 
                class="tab-item" 
                :class="{ active: activeTab === '' }" 
                @click="changeTab('')"
              >
                全部({{ totalCount }})
              </span>
              <span 
                class="tab-item" 
                :class="{ active: activeTab === tab.type }" 
                v-for="tab in couponTypes" 
                :key="tab.type"
                @click="changeTab(tab.type)"
              >
                {{ tab.name }}({{ tab.count }})
              </span>
            </div>
          </div>
        </div>

        <!-- 优惠券列表 -->
        <div class="coupon-list">
          <div 
            class="coupon-item" 
            :class="{ 
              selected: isSelected(coupon.id), 
              disabled: !isUsable(coupon) 
            }" 
            v-for="coupon in filteredCoupons" 
            :key="coupon.id"
            @click="selectCoupon(coupon)"
          >
            <!-- 左侧优惠信息 -->
            <div class="coupon-left">
              <div class="coupon-value">
                <span class="value-symbol" v-if="coupon.value >= 1">¥</span>
                <span class="value-number">{{ formatValue(coupon) }}</span>
                <span class="value-unit" v-if="coupon.value < 1">折</span>
              </div>
              <div class="coupon-conditions">
                <span class="condition-text" v-if="coupon.minAmount > 0">
                  满{{ coupon.minAmount }}元可用
                </span>
                <span class="condition-text" v-else>
                  无门槛
                </span>
              </div>
            </div>

            <!-- 右侧详细信息 -->
            <div class="coupon-right">
              <div class="coupon-info">
                <span class="coupon-name">{{ coupon.name }}</span>
                <span class="coupon-desc">{{ coupon.description }}</span>
                <div class="coupon-validity">
                  <span class="validity-text">
                    {{ formatDateStr(coupon.expireAt) }}
                  </span>
                </div>
              </div>
              
              <!-- 选择状态 -->
              <div class="coupon-status">
                <div class="status-indicator" v-if="isSelected(coupon.id)">
                  <i class="iconfont icon-check"></i>
                </div>
                <div class="status-text" v-else>
                  <span v-if="!isUsable(coupon)">不可用</span>
                  <span v-else>点击使用</span>
                </div>
              </div>
            </div>
          </div>

          <!-- 空状态 -->
          <div class="empty-coupons" v-if="filteredCoupons.length === 0">
            <img src="../assets/images/icons/coupon.png" class="empty-icon" />
            <span class="empty-text">暂无可用优惠券</span>
            <span class="empty-hint">去领取更多优惠券吧</span>
          </div>
        </div>

        <!-- 底部操作 -->
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="goToCouponCenter">
            <i class="iconfont icon-coupon"></i>
            <span>去领券</span>
          </button>
          
          <button 
            class="btn btn-primary" 
            @click="confirmSelection"
            :disabled="!selectedCouponId"
          >
            确定
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch, computed } from 'vue'
import { useRouter } from 'vue-router'
import { formatDate } from '../utils/util'

const props = defineProps({
  // 当前选中的优惠券ID
  value: {
    type: String,
    default: ''
  },
  // 订单金额
  orderAmount: {
    type: Number,
    default: 0
  },
  // 占位符
  placeholder: {
    type: String,
    default: '选择优惠券'
  },
  // 是否显示不可用的优惠券
  showUnusable: {
    type: Boolean,
    default: true
  },
  // 优惠券类型筛选
  couponType: {
    type: String,
    default: ''
  }
})

const emit = defineEmits(['update:value', 'change'])
const router = useRouter()

const showModal = ref(false)
const coupons = ref([])
const filteredCoupons = ref([])
const selectedCouponId = ref('')
const selectedCoupon = ref(null)
const activeTab = ref('')
const couponTypes = ref([])
const totalCount = ref(0)

const loadCoupons = () => {
  // 模拟优惠券数据
  const data = [
    {
      id: 'coupon_001',
      name: '满30减5',
      type: 'discount',
      value: 5,
      minAmount: 30,
      description: '全场通用',
      expireAt: '2024-12-31',
      usable: true
    },
    {
      id: 'coupon_002',
      name: '满50减10',
      type: 'discount',
      value: 10,
      minAmount: 50,
      description: '仅限茶饮类',
      expireAt: '2024-12-31',
      usable: true
    },
    {
      id: 'coupon_003',
      name: '饮品8折券',
      type: 'percentage',
      value: 0.2,
      minAmount: 0,
      maxDiscount: 15,
      description: '最高优惠15元',
      expireAt: '2024-12-31',
      usable: true
    },
    {
      id: 'coupon_004',
      name: '免配送费券',
      type: 'free_delivery',
      value: 0,
      minAmount: 20,
      description: '免配送费',
      expireAt: '2024-06-30',
      usable: true
    },
    {
      id: 'coupon_005',
      name: '新客专享',
      type: 'new_user',
      value: 8,
      minAmount: 15,
      description: '新用户专享优惠',
      expireAt: '2024-03-31',
      usable: false
    }
  ]
  
  coupons.value = data
  const stats = calculateTypeStats(data)
  couponTypes.value = stats.types
  totalCount.value = stats.total
  filterCoupons()
}

const calculateTypeStats = (data) => {
  const typeMap = {}
  data.forEach(coupon => {
    const type = coupon.type || 'other'
    if (!typeMap[type]) {
      typeMap[type] = {
        type: type,
        name: getTypeName(type),
        count: 0
      }
    }
    typeMap[type].count++
  })
  return {
    types: Object.values(typeMap),
    total: data.length
  }
}

const getTypeName = (type) => {
  const nameMap = {
    'discount': '满减券',
    'percentage': '折扣券',
    'free_delivery': '免运费券',
    'new_user': '新人券',
    'other': '其他'
  }
  return nameMap[type] || type
}

const isCouponUsable = (coupon, amount) => {
  if (coupon.expireAt) {
    const expireDate = new Date(coupon.expireAt)
    if (expireDate < new Date()) return false
  }
  if (coupon.usable === false) return false
  if (coupon.minAmount > 0 && amount < coupon.minAmount) return false
  return true
}

const filterCoupons = () => {
  let filtered = [...coupons.value]
  if (activeTab.value) {
    filtered = filtered.filter(c => c.type === activeTab.value)
  }
  
  filtered = filtered.map(c => ({
    ...c,
    isUsable: isCouponUsable(c, props.orderAmount)
  }))
  
  if (!props.showUnusable) {
    filtered = filtered.filter(c => c.isUsable)
  }
  
  filteredCoupons.value = filtered
}

const formatValue = (coupon) => {
  if (!coupon) return '0'
  if (coupon.value < 1) {
    return (coupon.value * 10).toFixed(1)
  }
  return coupon.value.toFixed(0)
}

const formatDateStr = (date) => {
  if (!date) return '长期有效'
  return formatDate(date, 'YYYY-MM-DD')
}

const showSelector = () => {
  filterCoupons()
  showModal.value = true
}

const hideSelector = () => {
  showModal.value = false
}

const changeTab = (tab) => {
  activeTab.value = tab
  filterCoupons()
}

const selectCoupon = (coupon) => {
  if (!isCouponUsable(coupon, props.orderAmount)) {
    alert('此优惠券不可用')
    return
  }
  
  if (selectedCouponId.value === coupon.id) {
    selectedCouponId.value = ''
    selectedCoupon.value = null
  } else {
    selectedCouponId.value = coupon.id
    selectedCoupon.value = coupon
  }
}

const isSelected = (id) => selectedCouponId.value === id
const isUsable = (coupon) => isCouponUsable(coupon, props.orderAmount)

const confirmSelection = () => {
  emit('update:value', selectedCouponId.value)
  emit('change', {
    value: selectedCouponId.value,
    coupon: selectedCoupon.value
  })
  hideSelector()
}

const goToCouponCenter = () => {
  hideSelector()
  router.push('/coupon')
}

const updateSelectedCoupon = (id) => {
  const coupon = coupons.value.find(c => c.id === id)
  selectedCouponId.value = id
  selectedCoupon.value = coupon
}

watch(() => props.value, (newVal) => {
  updateSelectedCoupon(newVal)
})

watch(() => props.orderAmount, () => {
  filterCoupons()
})

watch(() => props.couponType, (newVal) => {
  activeTab.value = newVal
  filterCoupons()
})

onMounted(() => {
  loadCoupons()
  updateSelectedCoupon(props.value)
})
</script>

<style scoped>
.coupon-selector {
  width: 100%;
}

.default-trigger {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #eee;
}

.trigger-content {
  display: flex;
  flex-direction: column;
}

.trigger-text {
  font-size: 14px;
  color: #999;
}

.has-value .trigger-text {
  color: #333;
  font-weight: bold;
}

.trigger-desc {
  font-size: 12px;
  color: var(--primary-color, #D4A574);
  margin-top: 4px;
}

.coupon-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background: #f8f8f8;
  border-radius: 16px 16px 0 0;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  background: #fff;
  border-bottom: 1px solid #eee;
  border-radius: 16px 16px 0 0;
}

.modal-title {
  font-size: 16px;
  font-weight: bold;
}

.filter-tabs {
  background: #fff;
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.tab-scroll {
  overflow-x: auto;
  white-space: nowrap;
  padding: 0 16px;
}

.tab-list {
  display: flex;
  gap: 20px;
}

.tab-item {
  font-size: 14px;
  color: #666;
  padding: 4px 0;
  position: relative;
}

.tab-item.active {
  color: var(--primary-color, #D4A574);
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  right: 0;
  height: 2px;
  background: var(--primary-color, #D4A574);
}

.coupon-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.coupon-item {
  display: flex;
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  overflow: hidden;
  position: relative;
  border: 1px solid transparent;
}

.coupon-item.selected {
  border-color: var(--primary-color, #D4A574);
}

.coupon-item.disabled {
  opacity: 0.6;
}

.coupon-left {
  width: 100px;
  background: var(--primary-color, #D4A574);
  color: #fff;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 12px;
}

.coupon-item.disabled .coupon-left {
  background: #ccc;
}

.coupon-value {
  display: flex;
  align-items: baseline;
}

.value-symbol {
  font-size: 14px;
  margin-right: 2px;
}

.value-number {
  font-size: 24px;
  font-weight: bold;
}

.value-unit {
  font-size: 12px;
  margin-left: 2px;
}

.coupon-conditions {
  font-size: 10px;
  margin-top: 4px;
  text-align: center;
}

.coupon-right {
  flex: 1;
  padding: 12px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.coupon-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.coupon-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.coupon-desc {
  font-size: 12px;
  color: #999;
}

.coupon-validity {
  font-size: 11px;
  color: #bbb;
  margin-top: 4px;
}

.status-indicator {
  color: var(--primary-color, #D4A574);
}

.status-text {
  font-size: 12px;
  color: #999;
}

.empty-coupons {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 13px;
  color: #999;
}

.modal-footer {
  padding: 16px;
  display: flex;
  gap: 12px;
  background: #fff;
  border-top: 1px solid #eee;
}

.btn {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  border: none;
}

.btn-secondary {
  background: #f5f5f5;
  color: #333;
}

.btn-primary {
  background: var(--primary-color, #D4A574);
  color: #fff;
}

.btn-primary:disabled {
  opacity: 0.5;
}
</style>