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
/* 饮饮茶 (SipSipTea) - 奶茶主题优惠券选择器样式 */
.coupon-selector {
  width: 100%;
}

/* 选择按钮样式 */
.default-trigger {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-sm);
  background: linear-gradient(135deg, var(--accent-cream) 0%, var(--surface-color) 100%);
  border-radius: var(--border-radius-lg);
  border: 2px solid rgba(var(--primary-color-rgb), 0.15);
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.05);
  backdrop-filter: blur(3px);
  letter-spacing: 0.05em;
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.default-trigger:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.1);
  border-color: rgba(var(--primary-color-rgb), 0.25);
}

.default-trigger:active {
  transform: translateY(0) scale(0.98);
}

.trigger-content {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.trigger-text {
  font-size: 14px;
  color: var(--text-color-medium);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.7;
  transition: all 0.3s ease-out;
}

.has-value .trigger-text {
  color: var(--primary-color);
  font-weight: 600;
  opacity: 1;
}

.trigger-desc {
  font-size: 12px;
  color: var(--accent-pink);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.03em;
  font-weight: 500;
}

/* 弹窗样式 */
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
  background: rgba(var(--primary-color-rgb), 0.12);
  backdrop-filter: blur(2px);
  animation: fadeIn 0.3s ease-out;
}

.modal-content {
  position: relative;
  background: linear-gradient(to bottom, var(--accent-cream) 0%, var(--surface-color) 100%);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 -8px 30px rgba(var(--primary-color-rgb), 0.08);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 弹窗头部 */
.modal-header {
  padding: var(--spacing-lg) var(--spacing-md);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
  background: rgba(var(--surface-color-rgb), 0.9);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  color: var(--primary-color);
  letter-spacing: 0.08em;
  line-height: 1.6;
}

.icon-close {
  font-size: 20px;
  color: var(--text-color-medium);
  cursor: pointer;
  transition: all 0.3s ease-out;
  padding: 4px;
  border-radius: 50%;
}

.icon-close:hover {
  color: var(--primary-color);
  background: rgba(var(--primary-color-rgb), 0.1);
  transform: rotate(90deg);
}

/* 筛选标签 */
.filter-tabs {
  background: rgba(var(--surface-color-rgb), 0.85);
  padding: var(--spacing-sm) 0;
  border-bottom: 1px solid var(--border-color);
  backdrop-filter: blur(4px);
}

.tab-scroll {
  overflow-x: auto;
  white-space: nowrap;
  padding: 0 var(--spacing-md);
  scrollbar-width: none;
}

.tab-scroll::-webkit-scrollbar {
  display: none;
}

.tab-list {
  display: flex;
  gap: var(--spacing-md);
}

.tab-item {
  font-size: 14px;
  color: var(--text-color-medium);
  padding: 6px 0;
  position: relative;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.7;
  letter-spacing: 0.03em;
  cursor: pointer;
  transition: all 0.3s ease-out;
  white-space: nowrap;
}

.tab-item:hover {
  color: var(--primary-color);
  opacity: 0.9;
}

.tab-item.active {
  color: var(--primary-color);
  font-weight: 600;
  opacity: 1;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -6px;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  border-radius: var(--border-radius-sm);
  animation: tabSlide 0.3s ease-out;
}

/* 优惠券列表 */
.coupon-list {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
}

.coupon-item {
  display: flex;
  background: rgba(255, 255, 255, 0.9);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-sm);
  overflow: hidden;
  position: relative;
  border: 2px solid var(--border-color);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.coupon-item:hover:not(.disabled) {
  transform: translateY(-2px);
  border-color: var(--primary-light);
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.08);
}

.coupon-item.selected {
  border-color: var(--primary-color);
  box-shadow: 0 4px 20px rgba(var(--primary-color-rgb), 0.15);
  transform: translateY(-2px) scale(1.01);
}

.coupon-item.disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 左侧优惠信息 */
.coupon-left {
  width: 100px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: var(--accent-cream);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: var(--spacing-md);
  position: relative;
  overflow: hidden;
}

.coupon-left::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 30% 30%, rgba(255, 255, 255, 0.1) 0%, transparent 70%);
}

.coupon-item.disabled .coupon-left {
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--border-color) 100%);
}

.coupon-value {
  display: flex;
  align-items: baseline;
  gap: 2px;
}

.value-symbol {
  font-size: 14px;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  font-weight: 500;
}

.value-number {
  font-size: 26px;
  font-weight: 700;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  line-height: 1;
}

.value-unit {
  font-size: 12px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
}

.coupon-conditions {
  font-size: 10px;
  margin-top: 6px;
  text-align: center;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.03em;
  opacity: 0.9;
  font-weight: 500;
}

/* 右侧详细信息 */
.coupon-right {
  flex: 1;
  padding: var(--spacing-md);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-sm);
}

.coupon-info {
  display: flex;
  flex-direction: column;
  gap: 6px;
  flex: 1;
}

.coupon-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-dark);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
}

.coupon-desc {
  font-size: 13px;
  color: var(--accent-pink);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.8;
  letter-spacing: 0.03em;
  font-weight: 500;
}

.coupon-validity {
  font-size: 12px;
  color: var(--accent-brown);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.03em;
  font-weight: 500;
}

/* 选择状态 */
.coupon-status {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
}

.status-indicator {
  color: var(--primary-color);
  font-size: 20px;
  animation: checkPop 0.3s ease-out;
}

.status-text {
  font-size: 12px;
  color: var(--text-color-medium);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.6;
  letter-spacing: 0.03em;
  font-weight: 500;
}

/* 空状态 */
.empty-coupons {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xl) 0;
  text-align: center;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: var(--spacing-lg);
  opacity: 0.4;
  filter: grayscale(0.8);
  transition: all 0.5s ease-out;
}

.empty-coupons:hover .empty-icon {
  opacity: 0.6;
  filter: grayscale(0.5);
  transform: scale(1.05);
}

.empty-text {
  font-size: 16px;
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-xs);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  font-weight: 500;
}

.empty-hint {
  font-size: 14px;
  color: var(--accent-pink);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.7;
  letter-spacing: 0.03em;
  font-weight: 500;
}

/* 底部操作按钮 */
.modal-footer {
  padding: var(--spacing-lg) var(--spacing-md);
  display: flex;
  gap: var(--spacing-sm);
  border-top: 1px solid var(--border-color);
  background: rgba(var(--surface-color-rgb), 0.9);
}

.btn {
  flex: 1;
  height: 48px;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  border: none;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.05em;
  font-weight: 600;
  transition: all 0.3s ease-out;
  cursor: pointer;
  gap: 8px;
}

.btn-secondary {
  background: rgba(var(--primary-color-rgb), 0.08);
  color: var(--primary-color);
  border: 2px solid rgba(var(--primary-color-rgb), 0.2);
}

.btn-secondary:hover {
  background: rgba(var(--primary-color-rgb), 0.15);
  border-color: rgba(var(--primary-color-rgb), 0.3);
  transform: translateY(-2px);
}

.btn-secondary:active {
  transform: translateY(0) scale(0.98);
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: var(--accent-cream);
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.2);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.3);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.btn-primary:disabled {
  opacity: 0.5;
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--border-color) 100%);
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

@keyframes tabSlide {
  from {
    transform: scaleX(0);
  }
  to {
    transform: scaleX(1);
  }
}

@keyframes checkPop {
  0% {
    transform: scale(0);
  }
  50% {
    transform: scale(1.2);
  }
  100% {
    transform: scale(1);
  }
}

/* 滚动条样式 */
.coupon-list::-webkit-scrollbar {
  width: 6px;
}

.coupon-list::-webkit-scrollbar-track {
  background: rgba(var(--border-color-rgb), 0.3);
  border-radius: var(--border-radius-sm);
}

.coupon-list::-webkit-scrollbar-thumb {
  background: var(--primary-light);
  border-radius: var(--border-radius-sm);
}

.coupon-list::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}

/* 优惠券装饰效果 */
.coupon-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 100px;
  bottom: 0;
  width: 1px;
  background: repeating-linear-gradient(
      to bottom,
      transparent,
      transparent 6px,
      var(--border-color) 6px,
      var(--border-color) 12px
  );
}

.coupon-item.selected::before {
  background: repeating-linear-gradient(
      to bottom,
      transparent,
      transparent 6px,
      var(--primary-color) 6px,
      var(--primary-color) 12px
  );
}
</style>