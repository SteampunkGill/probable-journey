<template>
  <div class="user-page">
    <!-- 用户信息区域 -->
    <div class="user-header">
      <div class="user-info" @click="goToUserProfile">
        <img 
          :src="userInfo?.avatarUrl || userInfo?.avatar || defaultAvatar" 
          class="user-avatar"
        />
        <div class="user-details">
          <span class="user-name">{{ userInfo?.nickname || userInfo?.username || '点击登录' }}</span>
          <div class="user-meta" v-if="userInfo">
            <span class="user-level">⭐ {{ userInfo.levelName || '普通会员' }}</span>
            <span class="user-points">积分: {{ userInfo.points || 0 }}</span>
          </div>
        </div>
        <i class="iconfont icon-right"></i>
      </div>
      
      <div class="user-assets" v-if="userInfo">
        <div class="asset-item" @click="router.push('/wallet')">
          <span class="asset-value">¥{{ userInfo.balance !== undefined ? userInfo.balance.toFixed(2) : '0.00' }}</span>
          <span class="asset-label">余额</span>
        </div>
        <div class="asset-item" @click="router.push('/coupon')">
          <span class="asset-value">{{ userInfo.couponCount || 0 }}</span>
          <span class="asset-label">优惠券</span>
        </div>
        <div class="asset-item" @click="router.push('/points')">
          <span class="asset-value">{{ userInfo.points || 0 }}</span>
          <span class="asset-label">积分</span>
        </div>
      </div>
    </div>

    <!-- 我的订单 -->
    <div class="order-section card">
      <div class="section-header">
        <span class="section-title">📦 我的订单</span>
        <div class="all-orders" @click="router.push('/order-list')">
          <span>全部订单</span>
          <i class="iconfont icon-right"></i>
        </div>
      </div>
      <div class="order-status-grid">
        <div class="status-item" @click="router.push('/order-list?status=pending_payment')">
          <div class="status-icon">💳</div>
          <span class="status-name">待付款</span>
        </div>
        <div class="status-item" @click="router.push('/order-list?status=processing')">
          <div class="status-icon">🍵</div>
          <span class="status-name">制作中</span>
        </div>
        <div class="status-item" @click="router.push('/order-list?status=ready')">
          <div class="status-icon">🛍️</div>
          <span class="status-name">待取餐</span>
        </div>
        <div class="status-item" @click="router.push('/order-list?status=completed')">
          <div class="status-icon">✅</div>
          <span class="status-name">已完成</span>
        </div>
      </div>
    </div>

    <!-- 我的资产 -->
    <div class="assets-section card">
      <div class="section-header">
        <span class="section-title">💰 我的资产</span>
      </div>
      <div class="assets-grid">
        <div
          class="asset-card"
          v-for="item in assets"
          :key="item.id"
          @click="goToAsset(item.type)"
        >
          <div class="asset-icon" :style="{ background: item.bgColor }">
            <i class="iconfont" :class="item.icon"></i>
          </div>
          <span class="asset-name">{{ item.name }}</span>
          <span class="asset-value" v-if="item.value">{{ item.value }}</span>
        </div>
      </div>
    </div>

    <!-- 会员特权 -->
    <div class="privilege-section card" v-if="userInfo">
      <div class="section-header">
        <span class="section-title">👑 会员特权</span>
      </div>
      <div class="privilege-list">
        <div class="privilege-item" @click="checkBirthdayPrivilege">
          <div class="privilege-icon">🎂</div>
          <div class="privilege-info">
            <span class="privilege-name">生日特权</span>
            <span class="privilege-desc">生日当月享专属好礼</span>
          </div>
          <button class="receive-btn">去查看</button>
        </div>
        <div class="privilege-item" @click="router.push('/order')">
          <div class="privilege-icon">🏷️</div>
          <div class="privilege-info">
            <span class="privilege-name">会员专享价</span>
            <span class="privilege-desc">部分商品享受会员折扣</span>
          </div>
          <button class="receive-btn">去下单</button>
        </div>
      </div>
    </div>

    <!-- 常用功能 -->
    <div class="functions-section card">
      <div class="section-header">
        <span class="section-title">📋 常用功能</span>
      </div>
      <div class="function-list">
        <div
          class="function-item"
          v-for="item in functions"
          :key="item.id"
          v-show="!item.requiresAdmin || userInfo?.role === 'admin'"
          @click="goToFunction(item.url)"
        >
          <div class="function-left">
            <i class="iconfont" :class="item.icon" :style="{ color: item.color }"></i>
            <span class="function-name">{{ item.name }}</span>
          </div>
          <i class="iconfont icon-right"></i>
        </div>
      </div>
    </div>


    <!-- 客服中心 -->
    <div class="service-section">
      <button class="service-btn" @click="contactService">
        <i class="iconfont icon-service"></i>
        <span>在线客服</span>
      </button>
      <button class="service-btn" @click="makePhoneCall">
        <i class="iconfont icon-phone"></i>
        <span>电话联系</span>
      </button>
    </div>

    <!-- 设置入口 -->
    <div class="settings-btn" @click="router.push('/settings')">
      <i class="iconfont icon-settings"></i>
      <span>设置</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { authApi, memberApi } from '../../utils/api'
import defaultAvatar from '../../assets/images/icons/user.png'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const assets = ref([
  { id: 1, type: 'balance', name: '余额', icon: 'icon-wallet', value: '0.00', bgColor: '#36CFC9' },
  { id: 2, type: 'coupon', name: '优惠券', icon: 'icon-coupon', value: '0', bgColor: '#FF6B6B' },
  { id: 3, type: 'points', name: '积分', icon: 'icon-points', value: '0', bgColor: '#FFA940' },
  { id: 4, type: 'gift', name: '礼品卡', icon: 'icon-gift', value: '0', bgColor: '#73D13D' }
])

const functions = ref([
  { id: 1, name: '地址管理', icon: 'icon-address', color: '#36CFC9', url: '/address' },
  { id: 2, name: '我的收藏', icon: 'icon-heart', color: '#FF6B6B', url: '/favorite' },
  { id: 3, name: '购买记录', icon: 'icon-history', color: '#FFA940', url: '/order-list' },
  { id: 4, name: '积分商城', icon: 'icon-gift', color: '#FF6B6B', url: '/points/mall' },
  { id: 10, name: '分享有礼', icon: 'icon-share', color: '#FF9800', url: '/share' },
  { id: 9, name: '绑定会员卡', icon: 'icon-wallet', color: '#D4A574', url: '/user/bind-card' },
  { id: 5, name: '客服中心', icon: 'icon-service', color: '#73D13D', url: '' },
  { id: 6, name: '关于我们', icon: 'icon-info', color: '#597EF7', url: '/about' },
  { id: 7, name: '设置', icon: 'icon-settings', color: '#9254DE', url: '/settings' },
  { id: 8, name: '管理后台', icon: 'icon-lock', color: '#FF4D4F', url: '/admin', requiresAdmin: true }
])

const loadUserData = async () => {
  try {
    const res = await authApi.getUserProfile()
    if (res) {
      // 更新 Store
      userStore.setUserInfo(res)
      
      // 更新资产显示
      assets.value[0].value = (res.balance || 0).toFixed(2)
      assets.value[1].value = (res.couponCount || 0).toString()
      assets.value[2].value = (res.points || 0).toString()
    }
  } catch (error) {
    console.error('加载用户信息失败:', error)
  }
}

const goToUserProfile = () => {
  if (!userInfo.value) {
    router.push('/login')
  } else {
    router.push('/profile')
  }
}

const goToAsset = (type) => {
  const routes = {
    balance: '/wallet',
    coupon: '/coupon',
    points: '/points',
    gift: '/wallet/gift-card'
  }
  router.push(routes[type])
}

const goToFunction = (url) => {
  if (url) router.push(url)
}

const contactService = () => {
  alert('正在连接在线客服...')
}

const makePhoneCall = () => {
  window.location.href = 'tel:400-123-4567'
}

const checkBirthdayPrivilege = async () => {
  try {
    const res = await memberApi.getBirthdayPrivilege()
    if (res.code === 200) {
      const data = res.data
      if (data.isBirthdayMonth) {
        if (data.received) {
          alert('您已领取本月生日礼包，祝您生日快乐！')
        } else {
          if (confirm(`检测到本月是您的生日月！\n专属礼包：${data.privilegeName}\n立即领取？`)) {
            const receiveRes = await memberApi.receiveBirthdayPrivilege()
            if (receiveRes.code === 200) {
              alert('领取成功！已发放至您的账户。')
              loadUserData()
            }
          }
        }
      } else {
        alert(`生日特权仅在生日当月开放。\n您的生日是：${userInfo.value.birthday || '未设置'}`)
      }
    }
  } catch (error) {
    console.error('获取生日特权失败:', error)
  }
}

onMounted(() => {
  loadUserData()
})
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 - 用户中心页面 */
.user-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  position: relative;
  overflow-x: hidden;
  padding-bottom: var(--spacing-xl);
}

/* 背景装饰元素 */
.user-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 20%, rgba(255, 248, 220, 0.15) 0%, transparent 50%),
      radial-gradient(circle at 80% 80%, rgba(222, 184, 135, 0.15) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 用户头部区域 */
.user-header {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  padding: var(--spacing-xl) var(--spacing-lg) var(--spacing-lg);
  color: var(--accent-cream);
  border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
  position: relative;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(139, 69, 19, 0.2);
}

.user-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 30% 30%, rgba(255, 192, 203, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 70% 70%, rgba(255, 248, 220, 0.1) 0%, transparent 50%);
}

/* 用户信息 */
.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  cursor: pointer;
  position: relative;
  z-index: 1;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-lg);
  transition: all 0.3s ease-out;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
}

.user-info:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.user-avatar {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  border: 3px solid rgba(255, 255, 255, 0.3);
  background: var(--accent-cream);
  object-fit: cover;
  transition: all 0.3s ease-out;
}

.user-info:hover .user-avatar {
  transform: scale(1.05);
  border-color: rgba(255, 255, 255, 0.5);
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 22px;
  font-weight: 700;
  font-family: var(--font-heading);
  display: block;
  margin-bottom: var(--spacing-xs);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.user-meta {
  display: flex;
  gap: var(--spacing-md);
  font-size: 13px;
  opacity: 0.9;
}

.user-level {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 10px;
  border-radius: var(--border-radius-md);
  font-weight: 600;
}

.user-points {
  opacity: 0.8;
}

.iconfont.icon-right {
  color: rgba(255, 255, 255, 0.7);
  font-size: 18px;
  transition: all 0.3s ease-out;
}

.user-info:hover .iconfont.icon-right {
  color: white;
  transform: translateX(4px);
}

/* 用户资产 */
.user-assets {
  display: flex;
  justify-content: space-around;
  text-align: center;
  position: relative;
  z-index: 1;
  gap: var(--spacing-sm);
}

.asset-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  cursor: pointer;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(5px);
  flex: 1;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.asset-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-4px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
}

.asset-value {
  font-size: 20px;
  font-weight: 800;
  font-family: var(--font-heading);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.asset-label {
  font-size: 12px;
  opacity: 0.8;
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 卡片通用样式 */
.card {
  background: var(--surface-color);
  margin: var(--spacing-md) var(--spacing-lg);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow:
      0 8px 32px rgba(139, 69, 19, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease-out;
}

.card:hover {
  transform: translateY(-2px);
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color), var(--accent-brown));
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

/* 区域头部 */
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.section-title {
  font-size: 17px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  position: relative;
  padding-left: var(--spacing-md);
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 20px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
  border-radius: 3px;
}

.all-orders {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 13px;
  color: var(--text-color-medium);
  cursor: pointer;
  transition: all 0.3s ease-out;
  padding: 4px 8px;
  border-radius: var(--border-radius-md);
}

.all-orders:hover {
  color: var(--primary-color);
  background: var(--accent-cream);
}

.all-orders .iconfont.icon-right {
  font-size: 12px;
  color: var(--text-color-light);
}

.all-orders:hover .iconfont.icon-right {
  color: var(--primary-color);
  transform: translateX(2px);
}

/* 订单状态网格 */
.order-status-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
}

.status-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  cursor: pointer;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: var(--accent-cream);
}

.status-item:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 8px 20px rgba(160, 82, 45, 0.15);
  background: white;
}

.status-icon {
  font-size: 28px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
  border-radius: 50%;
  color: white;
  transition: all 0.3s ease-out;
}

.status-item:hover .status-icon {
  transform: rotate(10deg) scale(1.1);
}

.status-name {
  font-size: 13px;
  font-weight: 600;
  color: var(--text-color-dark);
}

/* 资产网格 */
.assets-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: var(--spacing-md);
}

.asset-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: var(--accent-cream);
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.asset-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary-light);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.15);
}

.asset-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color));
}

.asset-icon {
  width: 50px;
  height: 50px;
  border-radius: var(--border-radius-md);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 24px;
  transition: all 0.3s ease-out;
  position: relative;
  z-index: 1;
}

.asset-card:hover .asset-icon {
  transform: scale(1.1) rotate(5deg);
}

.asset-name {
  font-size: 12px;
  font-weight: 600;
  color: var(--text-color-dark);
  position: relative;
  z-index: 1;
}

.asset-value {
  font-size: 14px;
  font-weight: 700;
  color: var(--primary-color);
  position: relative;
  z-index: 1;
}

/* 特权列表 */
.privilege-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.privilege-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  background: linear-gradient(135deg, var(--accent-cream), white);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
}

.privilege-item:hover {
  transform: translateX(8px);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.15);
}

.privilege-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
}

.privilege-icon {
  font-size: 32px;
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, var(--primary-light), var(--primary-color));
  border-radius: 50%;
  color: white;
  transition: all 0.3s ease-out;
  position: relative;
  z-index: 1;
}

.privilege-item:hover .privilege-icon {
  transform: scale(1.1) rotate(10deg);
}

.privilege-info {
  flex: 1;
  position: relative;
  z-index: 1;
}

.privilege-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--primary-dark);
  display: block;
  margin-bottom: 2px;
}

.privilege-desc {
  font-size: 12px;
  color: var(--text-color-medium);
}

.receive-btn {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: var(--border-radius-md);
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s ease-out;
  position: relative;
  z-index: 1;
}

.receive-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.3);
}

/* 功能列表 */
.function-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.function-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: var(--accent-cream);
  position: relative;
  overflow: hidden;
}

.function-item:hover {
  transform: translateX(8px);
  background: white;
  box-shadow: 0 4px 16px rgba(160, 82, 45, 0.1);
}

.function-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
  opacity: 0;
  transition: opacity 0.3s ease-out;
}

.function-item:hover::before {
  opacity: 1;
}

.function-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  position: relative;
  z-index: 1;
}

.function-left .iconfont {
  font-size: 20px;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--border-radius-md);
  transition: all 0.3s ease-out;
}

.function-item:hover .function-left .iconfont {
  transform: scale(1.1);
  background: white;
}

.function-name {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color-dark);
  transition: all 0.3s ease-out;
}

.function-item:hover .function-name {
  color: var(--primary-color);
}

.function-item .iconfont.icon-right {
  color: var(--text-color-light);
  font-size: 16px;
  transition: all 0.3s ease-out;
}

.function-item:hover .iconfont.icon-right {
  color: var(--primary-color);
  transform: translateX(4px);
}

/* 客服中心 */
.service-section {
  display: flex;
  justify-content: center;
  gap: var(--spacing-lg);
  margin: var(--spacing-xl) var(--spacing-lg);
  position: relative;
  z-index: 1;
}

.service-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  border: 2px solid var(--border-color);
  background: white;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color-dark);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  flex: 1;
  max-width: 160px;
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
}

.service-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.15);
  border-color: var(--primary-light);
  color: var(--primary-color);
}

.service-btn .iconfont {
  font-size: 18px;
  transition: all 0.3s ease-out;
}

.service-btn:hover .iconfont {
  transform: scale(1.1);
}

/* 设置入口 */
.settings-btn {
  margin: var(--spacing-md) var(--spacing-lg);
  padding: var(--spacing-md);
  background: white;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-sm);
  color: var(--text-color-dark);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  z-index: 1;
  box-shadow:
      0 4px 12px rgba(139, 69, 19, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
}

.settings-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.15);
  color: var(--primary-color);
  background: var(--accent-cream);
}

.settings-btn .iconfont {
  font-size: 18px;
  transition: all 0.3s ease-out;
}

.settings-btn:hover .iconfont {
  transform: rotate(30deg);
}

/* 装饰元素 */
.user-page::after {
  content: '';
  position: fixed;
  bottom: -100px;
  left: -100px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--accent-brown) 0%, transparent 70%);
  opacity: 0.08;
  border-radius: 50%;
  z-index: 0;
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(40px, -20px) rotate(120deg);
  }
  66% {
    transform: translate(-20px, 40px) rotate(240deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .user-header {
    padding: var(--spacing-lg) var(--spacing-md);
  }

  .card {
    margin: var(--spacing-md);
    padding: var(--spacing-md);
  }

  .assets-grid,
  .order-status-grid {
    grid-template-columns: repeat(4, 1fr);
    gap: var(--spacing-sm);
  }

  .asset-card,
  .status-item {
    padding: var(--spacing-sm);
  }

  .service-section {
    margin: var(--spacing-lg) var(--spacing-md);
  }
}

@media (max-width: 480px) {
  .user-header {
    padding: var(--spacing-md) var(--spacing-sm);
  }

  .user-avatar {
    width: 60px;
    height: 60px;
  }

  .user-name {
    font-size: 18px;
  }

  .assets-grid,
  .order-status-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .asset-item {
    padding: var(--spacing-sm);
  }

  .service-section {
    flex-direction: column;
    align-items: center;
  }

  .service-btn {
    max-width: 100%;
    width: 100%;
  }
}

/* 自定义滚动条 */
.user-page::-webkit-scrollbar {
  width: 8px;
}

.user-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.user-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.user-page::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
}
</style>