<template>
  <div class="pickup-page">
    <!-- 加载状态 -->
    <div class="loading" v-if="loading">
      <div class="loading-icon"></div>
      <span>加载中...</span>
    </div>

    <div v-else>
      <!-- 待取餐订单 -->
      <div class="pending-section" v-if="pendingOrders.length > 0">
        <!-- 取餐码大卡片 -->
        <div class="pickup-card" v-if="activeOrder">
          <div class="card-header">
            <div class="status-badge" :class="activeOrder.status">{{ activeOrder.statusText }}</div>
            <span class="order-no">{{ activeOrder.orderNo }}</span>
          </div>

          <div class="pickup-code-section">
            <span class="label">取餐码</span>
            <span class="pickup-code">{{ activeOrder.pickupCode }}</span>
            <span class="hint">请出示此码给店员</span>
          </div>

          <div class="order-info">
            <div class="info-item">
              <span class="icon">🏪</span>
              <div class="info-detail">
                <span class="store-name">{{ activeOrder.store.name }}</span>
                <span class="store-address">{{ activeOrder.store.address }}</span>
              </div>
            </div>
            
            <div class="info-item" v-if="activeOrder.status === 'ready'">
              <span class="icon">⏰</span>
              <div class="info-detail">
                <span class="time-label">已备餐</span>
                <span class="time-value">请尽快前往取餐</span>
              </div>
            </div>

            <div class="info-item" v-if="activeOrder.status === 'processing'">
              <span class="icon">⏰</span>
              <div class="info-detail">
                <span class="time-label">预计完成</span>
                <span class="time-value">{{ activeOrder.estimatedTime }}</span>
              </div>
            </div>
          </div>

          <div class="order-items">
            <div class="items-header">
              <span>商品清单</span>
              <span class="item-count">共{{ activeOrder.itemCount }}件</span>
            </div>
            <div class="item" v-for="item in activeOrder.items" :key="item.id">
              <img class="item-image" :src="item.image" />
              <div class="item-info">
                <span class="item-name">{{ item.name }}</span>
                <span class="item-specs" v-if="item.customizations">{{ item.customizations }}</span>
              </div>
              <span class="item-quantity">×{{ item.quantity }}</span>
            </div>
          </div>

          <div class="card-actions">
            <button class="action-btn" @click="callStore(activeOrder.store.phone)">
              <span class="icon">📞</span>
              <span>联系门店</span>
            </button>
            <button class="action-btn" v-if="activeOrder.status === 'processing'" @click="remindOrder(activeOrder.id)">
              <span class="icon">⏰</span>
              <span>催单</span>
            </button>
            <button class="action-btn primary" @click="goToOrderDetail(activeOrder.id)">
              <span>查看详情</span>
            </button>
          </div>
        </div>

        <!-- 其他待取餐订单 -->
        <div class="other-orders" v-if="pendingOrders.length > 1">
          <div class="section-title">其他待取餐订单</div>
          <div class="order-list">
            <div class="order-item" 
                 :class="{ active: activeOrder?.id === item.id }"
                 v-for="item in pendingOrders" 
                 :key="item.id"
                 @click="selectOrder(item)">
              <div class="order-header">
                <span class="pickup-code-small">{{ item.pickupCode }}</span>
                <span class="status">{{ item.statusText }}</span>
              </div>
              <span class="order-no">{{ item.orderNo }}</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 扫码取餐 -->
      <div class="scan-section">
        <button class="scan-btn" @click="scanToPickup">
          <span class="icon">📷</span>
          <span class="text">扫码取餐</span>
        </button>
        <span class="scan-hint">扫描门店二维码快速取餐</span>
      </div>

      <!-- 历史订单 -->
      <div class="history-section" v-if="historyOrders.length > 0">
        <div class="section-title">历史取餐记录</div>
        <div class="history-list">
          <div class="history-item" v-for="item in historyOrders" :key="item.id" @click="goToOrderDetail(item.id)">
            <div class="history-left">
              <span class="pickup-code-text">{{ item.pickupCode }}</span>
              <span class="order-no-text">{{ item.orderNo }}</span>
              <span class="picked-time">{{ item.pickedTime }}</span>
            </div>
            <div class="history-right">
              <span class="amount">¥{{ item.totalAmount }}</span>
              <span class="arrow">›</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div class="empty-state" v-if="pendingOrders.length === 0 && historyOrders.length === 0">
        <img class="empty-icon" src="../../assets/images/icons/pick_up_food.png" />
        <span class="empty-text">暂无取餐订单</span>
        <button class="go-order-btn" @click="goToOrder">去点单</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { orderApi } from '../../utils/api'

const router = useRouter()

const pendingOrders = ref([])
const historyOrders = ref([])
const activeOrder = ref(null)
const loading = ref(true)

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderList()
    if (res.code === 200) {
      const orders = res.data.list || res.data || []
      // 待取餐状态：PAID(已支付), ACCEPTED(已接单), MAKING(制作中), READY(待取餐)
      const pendingStatuses = ['PAID', 'ACCEPTED', 'MAKING', 'READY']
      pendingOrders.value = orders.filter(o => pendingStatuses.includes(o.status))
      // 已取餐状态：DELIVERED, FINISHED
      const historyStatuses = ['DELIVERED', 'FINISHED']
      historyOrders.value = orders.filter(o => historyStatuses.includes(o.status))
      
      if (pendingOrders.value.length > 0) {
        activeOrder.value = pendingOrders.value[0]
      }
    }
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

const selectOrder = (order) => {
  activeOrder.value = order
}

const scanToPickup = () => {
  alert('Web端暂不支持原生扫码，请手动输入取餐码')
  const code = prompt('请输入取餐码 (例如: A123)')
  if (code) {
    alert(`正在验证取餐码: ${code}`)
  }
}

const goToOrderDetail = (id) => {
  router.push(`/order-detail/${id}`)
}

const callStore = (phone) => {
  window.location.href = `tel:${phone}`
}

const remindOrder = async (id) => {
  try {
    const res = await orderApi.remindOrder(id)
    if (res.code === 200) {
      alert(res.data?.message || '已提醒商家尽快制作')
    } else {
      alert(res.message || '催单失败')
    }
  } catch (error) {
    console.error('催单失败:', error)
  }
}

const goToOrder = () => {
  router.push('/')
}

onMounted(() => {
  const token = localStorage.getItem('token')
  if (token && token !== 'undefined' && token !== 'null') {
    loadOrders()
  } else {
    loading.value = false
  }
})
</script>
<style scoped>
/* ============================================
“饮饮茶(SipSipTea)” 奶茶主题 CSS 变量
基于设计指南定义的核心视觉变量
============================================ */

:root {
  /* ========== 色彩方案 ========== */
  /* 主背景色 */
  --background-color: #f5f0e1; /* 柔和的奶油色 */
  --surface-color: #e8dccb; /* 表面元素色 - 浅卡其色 */

  /* 主色调 */
  --primary-color: #a0522d; /* 焦糖色 */
  --primary-dark: #8b4513; /* 较深咖啡色 */
  --primary-light: #d2b48c; /* 较浅驼色 */

  /* 强调色 */
  --accent-cream: #fff8dc; /* 玉米色/奶油 */
  --accent-pink: #ffc0cb; /* 淡粉色 */
  --accent-brown: #deb887; /* 沙棕色 */

  /* 文本颜色 */
  --text-color-dark: #4a3b30; /* 深棕色 */
  --text-color-medium: #7a6a5b; /* 中棕色 */
  --text-color-light: #a09080; /* 浅灰咖色 */

  /* 边框/分隔线颜色 */
  --border-color: #d4c7b5; /* 柔和的浅棕色 */

  /* ========== 形状与圆角 ========== */
  --border-radius-sm: 8px; /* 小圆角 */
  --border-radius-md: 12px; /* 中圆角 */
  --border-radius-lg: 20px; /* 大圆角 */
  --border-radius-xl: 30px; /* 超大圆角 */
  --border-radius-circle: 50%; /* 圆形 */

  /* ========== 字体排版 ========== */
  /* 字体家族 */
  --font-family-heading: 'Noto Serif KR', 'Prompt', serif;
  --font-family-body: 'Noto Sans KR', 'Nunito', 'Quicksand', sans-serif;

  /* 字号 */
  --font-size-xs: 12px;
  --font-size-sm: 14px;
  --font-size-base: 16px;
  --font-size-lg: 18px;
  --font-size-xl: 20px;
  --font-size-xxl: 24px;
  --font-size-xxxl: 32px;

  /* 字重 */
  --font-weight-normal: 400;
  --font-weight-medium: 500;
  --font-weight-semibold: 600;
  --font-weight-bold: 700;

  /* 行高 */
  --line-height-tight: 1.4;
  --line-height-normal: 1.5;
  --line-height-relaxed: 1.7;

  /* ========== 间距系统 ========== */
  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
  --spacing-xxl: 48px;
  --spacing-xxxl: 64px;

  /* ========== 阴影效果 ========== */
  --shadow-sm: 0 2px 8px rgba(160, 82, 45, 0.1);
  --shadow-md: 0 4px 16px rgba(160, 82, 45, 0.15);
  --shadow-lg: 0 8px 32px rgba(160, 82, 45, 0.2);
  --shadow-xl: 0 16px 48px rgba(160, 82, 45, 0.25);

  /* ========== 过渡动画 ========== */
  --transition-fast: 0.15s ease-out;
  --transition-normal: 0.25s ease-out;
  --transition-slow: 0.4s ease-out;
  --transition-bounce: 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);

  /* ========== 按钮样式 ========== */
  --button-padding-sm: var(--spacing-sm) var(--spacing-md);
  --button-padding-md: var(--spacing-md) var(--spacing-lg);
  --button-padding-lg: var(--spacing-lg) var(--spacing-xl);

  /* ========== 卡片样式 ========== */
  --card-padding-sm: var(--spacing-md);
  --card-padding-md: var(--spacing-lg);
  --card-padding-lg: var(--spacing-xl);

  /* ========== 输入框样式 ========== */
  --input-padding: var(--spacing-sm) var(--spacing-md);
  --input-border-width: 2px;
  --input-focus-shadow: 0 0 0 4px rgba(160, 82, 45, 0.3);

  /* ========== 导航栏样式 ========== */
  --navbar-height: 64px;
  --navbar-padding: var(--spacing-md);

  /* ========== 商品卡片样式 ========== */
  --product-card-width: 160px;
  --product-card-height: 220px;
  --product-image-height: 120px;

  /* ========== 加载动画 ========== */
  --loading-size-sm: 24px;
  --loading-size-md: 40px;
  --loading-size-lg: 60px;

  /* ========== 弹窗样式 ========== */
  --modal-backdrop: rgba(74, 59, 48, 0.6);
  --modal-padding: var(--spacing-xl);
  --modal-max-width: 500px;

  /* ========== 分隔线样式 ========== */
  --divider-thickness: 1px;
  --divider-pattern: url("data:image/svg+xml,%3Csvg width='20' height='4' viewBox='0 0 20 4' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Ccircle cx='2' cy='2' r='2' fill='%23d4c7b5'/%3E%3Ccircle cx='10' cy='2' r='2' fill='%23d4c7b5'/%3E%3Ccircle cx='18' cy='2' r='2' fill='%23d4c7b5'/%3E%3C/svg%3E");

  /* ========== 渐变背景 ========== */
  --gradient-primary: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  --gradient-cream: linear-gradient(135deg, var(--accent-cream) 0%, #fffaf0 100%);
  --gradient-pink: linear-gradient(135deg, var(--accent-pink) 0%, #ffebee 100%);
  --gradient-brown: linear-gradient(135deg, var(--accent-brown) 0%, #f5e6d3 100%);

  /* ========== 纹理背景 ========== */
  --texture-paper: url("data:image/svg+xml,%3Csvg width='100' height='100' viewBox='0 0 100 100' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M11 18c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm48 25c3.866 0 7-3.134 7-7s-3.134-7-7-7-7 3.134-7 7 3.134 7 7 7zm-43-7c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm63 31c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM34 90c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zm56-76c1.657 0 3-1.343 3-3s-1.343-3-3-3-3 1.343-3 3 1.343 3 3 3zM12 86c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm28-65c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm23-11c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-6 60c2.21 0 4-1.79 4-4s-1.79-4-4-4-4 1.79-4 4 1.79 4 4 4zm29 22c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zM32 63c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm57-13c2.76 0 5-2.24 5-5s-2.24-5-5-5-5 2.24-5 5 2.24 5 5 5zm-9-21c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM60 91c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM35 41c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2zM12 60c1.105 0 2-.895 2-2s-.895-2-2-2-2 .895-2 2 .895 2 2 2z' fill='%23e8dccb' fill-opacity='0.1' fill-rule='evenodd'/%3E%3C/svg%3E");

  /* ========== 奶茶主题图标 ========== */
  --icon-bubble: url("data:image/svg+xml,%3Csvg width='24' height='24' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Ccircle cx='12' cy='12' r='10' fill='%23a0522d'/%3E%3Ccircle cx='8' cy='8' r='3' fill='white' opacity='0.6'/%3E%3Ccircle cx='16' cy='9' r='2' fill='white' opacity='0.4'/%3E%3C/svg%3E");
  --icon-cup: url("data:image/svg+xml,%3Csvg width='24' height='24' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M6 3H18V18C18 19.6569 16.6569 21 15 21H9C7.34315 21 6 19.6569 6 18V3Z' fill='%23a0522d'/%3E%3Cpath d='M4 8H20V10H4V8Z' fill='%23d2b48c'/%3E%3Cpath d='M8 13H16V15H8V13Z' fill='%23fff8dc'/%3E%3C/svg%3E");
  --icon-straw: url("data:image/svg+xml,%3Csvg width='24' height='24' viewBox='0 0 24 24' fill='none' xmlns='http://www.w3.org/2000/svg'%3E%3Cpath d='M12 3L15 6L9 12L12 15L18 9L21 12V3H12Z' fill='%23ffc0cb'/%3E%3Cpath d='M12 15L15 18L9 21H6L12 15Z' fill='%23deb887'/%3E%3C/svg%3E");
}

/* ========== 全局基础样式 ========== */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: var(--font-family-body);
  font-size: var(--font-size-base);
  line-height: var(--line-height-normal);
  color: var(--text-color-dark);
  background-color: var(--background-color);
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
}

/* ========== 标题样式 ========== */
h1, h2, h3, h4, h5, h6 {
  font-family: var(--font-family-heading);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-md);
}

h1 {
  font-size: var(--font-size-xxxl);
  line-height: var(--line-height-tight);
}

h2 {
  font-size: var(--font-size-xxl);
}

h3 {
  font-size: var(--font-size-xl);
}

h4 {
  font-size: var(--font-size-lg);
}

/* ========== 段落和文本样式 ========== */
p {
  margin-bottom: var(--spacing-md);
  color: var(--text-color-medium);
}

a {
  color: var(--primary-color);
  text-decoration: none;
  transition: color var(--transition-fast);
}

a:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}

/* ========== 按钮基础样式 ========== */
button {
  font-family: var(--font-family-body);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  border: none;
  cursor: pointer;
  transition: all var(--transition-normal);
  border-radius: var(--border-radius-lg);
  outline: none;
}

button:focus {
  box-shadow: var(--input-focus-shadow);
}

/* ========== 输入框基础样式 ========== */
input, textarea, select {
  font-family: var(--font-family-body);
  font-size: var(--font-size-base);
  padding: var(--input-padding);
  border: var(--input-border-width) solid var(--border-color);
  border-radius: var(--border-radius-lg);
  background-color: var(--surface-color);
  color: var(--text-color-dark);
  transition: all var(--transition-normal);
}

input:focus, textarea:focus, select:focus {
  border-color: var(--primary-color);
  box-shadow: var(--input-focus-shadow);
  outline: none;
}

/* ========== 卡片基础样式 ========== */
.card {
  background-color: var(--surface-color);
  border-radius: var(--border-radius-lg);
  padding: var(--card-padding-md);
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
}

.card:hover {
  transform: translateY(-4px);
  box-shadow: var(--shadow-md);
}

/* ========== 分隔线样式 ========== */
hr {
  border: none;
  height: var(--divider-thickness);
  background-color: var(--border-color);
  margin: var(--spacing-lg) 0;
}

.divider-pattern {
  border: none;
  height: 4px;
  background-image: var(--divider-pattern);
  background-repeat: repeat-x;
  background-position: center;
}

/* ========== 加载动画样式 ========== */
.loading-bubble {
  width: var(--loading-size-md);
  height: var(--loading-size-md);
  background: var(--gradient-primary);
  border-radius: var(--border-radius-circle);
  animation: bubble 1.5s ease-in-out infinite;
}

@keyframes bubble {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.2);
    opacity: 0.7;
  }
}

/* ========== 响应式断点 ========== */
@media (max-width: 480px) {
  :root {
    --border-radius-lg: 16px;
    --border-radius-xl: 24px;
    --spacing-lg: 20px;
    --spacing-xl: 28px;
  }
}

@media (min-width: 769px) {
  :root {
    --border-radius-xl: 40px;
    --spacing-xl: 40px;
    --spacing-xxl: 56px;
  }
}

/* ========== 暗色模式支持 ========== */
@media (prefers-color-scheme: dark) {
  :root {
    --background-color: #2a2118;
    --surface-color: #3a2e23;
    --primary-color: #d2b48c;
    --primary-dark: #e8dccb;
    --primary-light: #a0522d;
    --text-color-dark: #f5f0e1;
    --text-color-medium: #d4c7b5;
    --text-color-light: #a09080;
    --border-color: #4a3b30;
    --shadow-sm: 0 2px 8px rgba(0, 0, 0, 0.3);
    --shadow-md: 0 4px 16px rgba(0, 0, 0, 0.4);
    --shadow-lg: 0 8px 32px rgba(0, 0, 0, 0.5);
  }
}
</style>