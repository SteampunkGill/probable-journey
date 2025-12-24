<template>
  <div class="user-page">
    <!-- 用户信息区域 -->
    <div class="user-header">
      <div class="user-info" @click="goToUserProfile">
        <img 
          :src="userInfo?.avatar || defaultAvatar" 
          class="user-avatar"
        />
        <div class="user-details">
          <span class="user-name">{{ userInfo?.nickname || '点击登录' }}</span>
          <div class="user-meta" v-if="userInfo">
            <span class="user-level">⭐ {{ userInfo.levelName || '普通会员' }}</span>
            <span class="user-points">积分: {{ userInfo.points || 0 }}</span>
          </div>
        </div>
        <i class="iconfont icon-right"></i>
      </div>
      
      <div class="user-assets" v-if="userInfo">
        <div class="asset-item" @click="router.push('/wallet')">
          <span class="asset-value">¥{{ userInfo.balance?.toFixed(2) || '0.00' }}</span>
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
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { userApi, couponApi } from '../../utils/api'
import defaultAvatar from '../../assets/images/icons/user.png'

const router = useRouter()
const userStore = useUserStore()

const userInfo = ref(null)

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
  { id: 5, name: '客服中心', icon: 'icon-service', color: '#73D13D', url: '' },
  { id: 6, name: '关于我们', icon: 'icon-info', color: '#597EF7', url: '/about' },
  { id: 7, name: '设置', icon: 'icon-settings', color: '#9254DE', url: '/settings' }
])

const loadUserData = async () => {
  // 模拟加载数据
  userInfo.value = {
    nickname: '奶茶爱好者',
    avatar: 'https://images.unsplash.com/photo-1494790108755-2616c113a1c4',
    levelName: '黄金会员',
    points: 1250,
    balance: 86.50,
    couponCount: 3
  }
  
  // 更新资产显示
  assets.value[0].value = userInfo.value.balance.toFixed(2)
  assets.value[1].value = userInfo.value.couponCount.toString()
  assets.value[2].value = userInfo.value.points.toString()
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
    gift: '/gift'
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

onMounted(() => {
  loadUserData()
})
</script>

<style scoped>
.user-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 30px;
}

.user-header {
  background: linear-gradient(135deg, #D4A574, #B38B5D);
  padding: 40px 20px 30px;
  color: white;
  border-radius: 0 0 20px 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 30px;
  cursor: pointer;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  border: 2px solid rgba(255,255,255,0.5);
  background: white;
}

.user-details {
  flex: 1;
}

.user-name {
  font-size: 20px;
  font-weight: bold;
  display: block;
  margin-bottom: 5px;
}

.user-meta {
  display: flex;
  gap: 15px;
  font-size: 12px;
  opacity: 0.9;
}

.user-assets {
  display: flex;
  justify-content: space-around;
  text-align: center;
}

.asset-item {
  display: flex;
  flex-direction: column;
  gap: 5px;
  cursor: pointer;
}

.asset-value {
  font-size: 18px;
  font-weight: bold;
}

.asset-label {
  font-size: 12px;
  opacity: 0.8;
}

.card {
  background: white;
  margin: 15px;
  padding: 15px;
  border-radius: 12px;
}

.section-header {
  margin-bottom: 15px;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.assets-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
}

.asset-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.asset-icon {
  width: 40px;
  height: 40px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
}

.asset-name {
  font-size: 12px;
  color: #666;
}

.function-list {
  display: flex;
  flex-direction: column;
}

.function-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #F5F5F5;
  cursor: pointer;
}

.function-item:last-child {
  border-bottom: none;
}

.function-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.function-name {
  font-size: 14px;
  color: #333;
}


.service-section {
  display: flex;
  justify-content: center;
  gap: 20px;
  margin: 30px 0;
}

.service-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 25px;
  border: 1px solid #DDD;
  background: white;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.settings-btn {
  margin: 0 15px;
  padding: 15px;
  background: white;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #666;
  font-size: 14px;
  cursor: pointer;
}
</style>