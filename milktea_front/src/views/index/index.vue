<template>
  <div class="page-container">
    <!-- 顶部搜索栏 -->
    <div class="search-bar" :class="{ shadow: scrollTop > 50 }">
      <div class="search-box" @click="router.push('/search')">
        <img class="search-icon" src="../../assets/images/icons/search.png" />
        <span class="search-placeholder">搜索饮品、门店...</span>
      </div>
      <img class="scan-btn" src="../../assets/images/icons/scan.png" @click="onScan" />
    </div>

    <!-- 骨架屏加载 -->
    <div class="skeleton-container" v-if="loading">
      <div class="skeleton-banner"></div>
      <div class="skeleton-mode">
        <div class="skeleton-mode-item"></div>
        <div class="skeleton-mode-item"></div>
      </div>
      <div class="skeleton-menu">
        <div class="skeleton-menu-item" v-for="i in 4" :key="i"></div>
      </div>
    </div>

    <div class="main-content" v-else @scroll="onScroll">
      <!-- 轮播图 -->
      <div class="banner-container">
        <div class="banner-wrapper" :style="{ transform: `translateX(-${currentBanner * 100}%)` }">
          <div class="banner-item" v-for="item in banners" :key="item.id">
            <img :src="item.image" class="banner-image" />
            <div class="banner-overlay">
              <span class="banner-title">{{ item.title }}</span>
            </div>
          </div>
        </div>
        <div class="banner-dots">
          <div 
            v-for="(item, index) in banners" 
            :key="item.id" 
            class="dot" 
            :class="{ active: currentBanner === index }"
          ></div>
        </div>
      </div>

      <!-- 点餐方式选择 -->
      <div class="order-mode-section">
        <div class="mode-title">选择点餐方式</div>
        <div class="mode-buttons">
          <div class="mode-card delivery" @click="selectOrderMode('delivery')">
            <img class="mode-icon" src="../../assets/images/icons/order.png" />
            <div class="mode-info">
              <span class="mode-name">外卖配送</span>
              <span class="mode-desc">30分钟送达</span>
            </div>
          </div>
          
          <div class="mode-card pickup" @click="selectOrderMode('pickup')">
            <img class="mode-icon" src="../../assets/images/icons/pick_up_food.png" />
            <div class="mode-info">
              <span class="mode-name">到店自取</span>
              <span class="mode-desc">预计制作时间</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-menu">
        <div class="menu-item" @click="router.push('/order')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="../../assets/images/icons/order.png" />
          </div>
          <span class="menu-name">点单</span>
        </div>
        <div class="menu-item" @click="router.push('/pickup')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="../../assets/images/icons/pick_up_food.png" />
          </div>
          <span class="menu-name">取餐</span>
        </div>
        <div class="menu-item" @click="router.push('/coupon')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="../../assets/images/icons/coupon.png" />
            <div class="coupon-badge" v-if="availableCouponCount > 0">{{ availableCouponCount }}</div>
          </div>
          <span class="menu-name">优惠券</span>
        </div>
        <div class="menu-item" @click="router.push('/wallet')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="../../assets/images/icons/gift.png" />
          </div>
          <span class="menu-name">钱包</span>
        </div>
      </div>

      <!-- 推荐商品 -->
      <div class="section" v-if="recommendProducts.length > 0">
        <div class="section-header">
          <span class="section-title">为你推荐</span>
          <div class="section-more" @click="router.push('/order')">
            <span>更多</span>
            <img class="arrow-icon" src="../../assets/images/icons/right.png" />
          </div>
        </div>
        
        <div class="recommend-scroll">
          <div class="recommend-list">
            <div 
              class="recommend-item" 
              v-for="item in recommendProducts" 
              :key="item.id"
              @click="router.push(`/product/${item.id}`)"
            >
              <img :src="item.image" class="recommend-image" />
              <div class="recommend-info">
                <span class="recommend-name">{{ item.name }}</span>
                <div class="recommend-bottom">
                  <span class="recommend-price">¥{{ item.price }}</span>
                  <img class="add-icon" src="../../assets/images/icons/add.png" @click.stop="quickAdd(item)" />
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 今日热门 -->
      <div class="section" v-if="hotProducts.length > 0">
        <div class="section-header">
          <span class="section-title">今日热门</span>
          <div class="section-more" @click="router.push('/order')">
            <span>更多</span>
            <img class="arrow-icon" src="../../assets/images/icons/right.png" />
          </div>
        </div>
        
        <div class="hot-list">
          <div 
            class="hot-item" 
            v-for="item in hotProducts" 
            :key="item.id"
            @click="router.push(`/product/${item.id}`)"
          >
            <img :src="item.image" class="hot-image" />
            <div class="hot-info">
              <span class="hot-name">{{ item.name }}</span>
              <span class="hot-desc">{{ item.description }}</span>
              <div class="hot-bottom">
                <div class="hot-meta">
                  <span class="hot-price">¥{{ item.price }}</span>
                  <span class="hot-sales">已售{{ item.sales }}</span>
                </div>
                <img class="add-icon" src="../../assets/images/icons/add.png" @click.stop="quickAdd(item)" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 附近门店 -->
      <div class="section" v-if="nearbyStore">
        <div class="section-header">
          <span class="section-title">附近门店</span>
          <div class="section-more" @click="router.push('/order')">
            <span>更多</span>
            <img class="arrow-icon" src="../../assets/images/icons/right.png" />
          </div>
        </div>
        
        <div class="store-card">
          <div class="store-header">
            <span class="store-name">{{ nearbyStore.name }}</span>
            <div class="store-status open">营业中</div>
          </div>
          <div class="store-info">
            <img class="info-icon" src="../../assets/images/icons/address.png" />
            <span class="store-address">{{ nearbyStore.address }}</span>
          </div>
          <div class="store-info">
            <img class="info-icon" src="../../assets/images/icons/info.png" />
            <span class="store-hours">{{ nearbyStore.businessHours }}</span>
          </div>
          <div class="store-footer">
            <span class="store-distance">距您 {{ nearbyStore.distance }}</span>
            <div class="store-call" @click="makePhoneCall(nearbyStore.phone)">
              <img class="call-icon" src="../../assets/images/icons/phone.png" />
              <span>联系门店</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 购物车浮动按钮 -->
    <div class="cart-float" :class="{ show: cartCount > 0 }" @click="router.push('/cart')">
      <img class="cart-icon" src="../../assets/images/icons/cart.png" />
      <div class="cart-badge" :class="{ bounce: cartAnimating }">{{ cartCount }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../store/cart'
import { homeApi, storeApi, bannerApi, productApi } from '../../utils/api'

const router = useRouter()
const cartStore = useCartStore()

const loading = ref(true)
const scrollTop = ref(0)
const currentBanner = ref(0)
const banners = ref([])
const recommendProducts = ref([])
const hotProducts = ref([])
const nearbyStore = ref(null)
const availableCouponCount = ref(0)
const cartCount = ref(0)
const cartAnimating = ref(false)

let bannerTimer = null

const loadData = async () => {
  loading.value = true
  try {
    // 并行获取首页数据、轮播图、推荐商品和附近门店
    const [homeRes, bannersRes, recommendRes, nearbyRes] = await Promise.all([
      homeApi.getHomeData(),
      bannerApi.getBanners(),
      homeApi.getRecommendations(),
      storeApi.getNearbyStores({ latitude: null, longitude: null })
    ])
    
    // 首页数据
    const homeData = homeRes.data || {}
    
    // 轮播图
    banners.value = bannersRes.data || []
    
    // 推荐商品
    recommendProducts.value = recommendRes.data || []
    
    // 热门商品
    hotProducts.value = homeData.hotProducts || []
    
    // 附近门店
    if (nearbyRes.data && nearbyRes.data.length > 0) {
      const store = nearbyRes.data[0]
      nearbyStore.value = {
        name: store.name,
        address: store.address,
        distance: store.distance || '500m',
        businessHours: store.businessHours || '9:00-22:00',
        phone: store.phone || '13800138000'
      }
    } else {
      nearbyStore.value = null
    }
    
    // 获取可用优惠券数量
    // 这里可以调用优惠券API获取数量，暂时设为0
    availableCouponCount.value = 0
    
  } catch (error) {
    console.error('加载首页数据失败:', error)
    // 可选的错误处理，例如显示错误消息
  } finally {
    loading.value = false
    startBannerTimer()
  }
}

const startBannerTimer = () => {
  if (banners.value.length > 1) {
    bannerTimer = setInterval(() => {
      currentBanner.value = (currentBanner.value + 1) % banners.value.length
    }, 5000)
  }
}

const onScroll = (e) => {
  scrollTop.value = e.target.scrollTop
}

const selectOrderMode = (mode) => {
  localStorage.setItem('orderMode', mode)
  router.push('/order')
}

const quickAdd = (product) => {
  cartStore.addToCart({
    ...product,
    quantity: 1
  })
  cartCount.value = cartStore.totalCount
  cartAnimating.value = true
  setTimeout(() => cartAnimating.value = false, 600)
}

const onScan = () => {
  alert('扫码功能仅在移动端可用')
}

const makePhoneCall = (phone) => {
  window.location.href = `tel:${phone}`
}

onMounted(() => {
  loadData()
  cartCount.value = cartStore.totalCount
})

onUnmounted(() => {
  if (bannerTimer) clearInterval(bannerTimer)
})
</script>

<style scoped>
.page-container {
  height: 100vh;
  background: #F5F5F5;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.search-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  gap: 15px;
  background: transparent;
  transition: all 0.3s;
}

.search-bar.shadow {
  background: white;
  box-shadow: 0 2px 10px rgba(0,0,0,0.05);
}

.search-box {
  flex: 1;
  height: 36px;
  background: rgba(255,255,255,0.9);
  border-radius: 18px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  gap: 10px;
  cursor: pointer;
}

.search-bar.shadow .search-box {
  background: #F5F5F5;
}

.search-icon { width: 16px; height: 16px; }
.search-placeholder { font-size: 13px; color: #999; }
.scan-btn { width: 24px; height: 24px; cursor: pointer; }

.main-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 30px;
}

.banner-container {
  position: relative;
  height: 250px;
  overflow: hidden;
}

.banner-wrapper {
  display: flex;
  height: 100%;
  transition: transform 0.5s ease-in-out;
}

.banner-item {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
  position: relative;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.banner-overlay {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 40px 20px 20px;
  background: linear-gradient(transparent, rgba(0,0,0,0.5));
  color: white;
}

.banner-title { font-size: 18px; font-weight: bold; }

.banner-dots {
  position: absolute;
  bottom: 15px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 6px;
}

.dot {
  width: 6px;
  height: 6px;
  border-radius: 3px;
  background: rgba(255,255,255,0.5);
  transition: all 0.3s;
}

.dot.active {
  width: 15px;
  background: #D4A574;
}

.order-mode-section {
  margin: -20px 15px 15px;
  position: relative;
  z-index: 10;
  background: white;
  padding: 20px;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0,0,0,0.05);
}

.mode-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 15px;
}

.mode-buttons {
  display: flex;
  gap: 15px;
}

.mode-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: 15px;
  border-radius: 8px;
  gap: 12px;
  cursor: pointer;
}

.mode-card.delivery { background: #FFF9F0; }
.mode-card.pickup { background: #F0F7FF; }

.mode-icon { width: 40px; height: 40px; }
.mode-name { font-size: 14px; font-weight: bold; display: block; }
.mode-desc { font-size: 11px; color: #999; }

.quick-menu {
  display: flex;
  justify-content: space-around;
  padding: 15px;
  background: white;
  margin: 0 15px 15px;
  border-radius: 12px;
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.menu-icon-wrapper {
  width: 45px;
  height: 45px;
  background: #FDF8F3;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
}

.menu-icon { width: 24px; height: 24px; }
.menu-name { font-size: 12px; color: #333; }

.coupon-badge {
  position: absolute;
  top: -2px;
  right: -2px;
  background: #FF6B6B;
  color: white;
  font-size: 10px;
  padding: 0 4px;
  border-radius: 8px;
  border: 2px solid white;
}

.section {
  margin: 0 15px 20px;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.section-title { font-size: 16px; font-weight: bold; }
.section-more { display: flex; align-items: center; gap: 4px; font-size: 12px; color: #999; cursor: pointer; }
.arrow-icon { width: 12px; height: 12px; }

.recommend-scroll {
  overflow-x: auto;
  padding-bottom: 5px;
}

.recommend-list {
  display: flex;
  gap: 12px;
}

.recommend-item {
  width: 140px;
  flex-shrink: 0;
  background: white;
  border-radius: 8px;
  overflow: hidden;
  cursor: pointer;
}

.recommend-image { width: 140px; height: 140px; object-fit: cover; }
.recommend-info { padding: 10px; }
.recommend-name { font-size: 13px; font-weight: bold; display: block; margin-bottom: 8px; }
.recommend-bottom { display: flex; justify-content: space-between; align-items: center; }
.recommend-price { font-size: 14px; color: #D4A574; font-weight: bold; }
.add-icon { width: 20px; height: 20px; cursor: pointer; }

.hot-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.hot-item {
  display: flex;
  background: white;
  padding: 12px;
  border-radius: 8px;
  gap: 12px;
  cursor: pointer;
}

.hot-image { width: 80px; height: 80px; border-radius: 4px; object-fit: cover; }
.hot-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.hot-name { font-size: 14px; font-weight: bold; }
.hot-desc { font-size: 11px; color: #999; }
.hot-bottom { display: flex; justify-content: space-between; align-items: center; }
.hot-price { font-size: 15px; color: #D4A574; font-weight: bold; }
.hot-sales { font-size: 11px; color: #999; margin-left: 10px; }

.store-card {
  background: white;
  padding: 15px;
  border-radius: 12px;
}

.store-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 12px; }
.store-name { font-size: 15px; font-weight: bold; }
.store-status { font-size: 11px; padding: 2px 6px; border-radius: 4px; }
.store-status.open { background: #E6F7ED; color: #27AE60; }

.store-info { display: flex; align-items: center; gap: 8px; margin-bottom: 8px; font-size: 12px; color: #666; }
.info-icon { width: 14px; height: 14px; }

.store-footer {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px solid #F5F5F5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.store-distance { font-size: 12px; color: #999; }
.store-call { display: flex; align-items: center; gap: 6px; color: #D4A574; font-size: 13px; cursor: pointer; }
.call-icon { width: 16px; height: 16px; }

.cart-float {
  position: fixed;
  right: 20px;
  bottom: 30px;
  width: 50px;
  height: 50px;
  background: #D4A574;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(212,165,116,0.4);
  transform: scale(0);
  transition: transform 0.3s;
  cursor: pointer;
}

.cart-float.show { transform: scale(1); }
.cart-icon { width: 24px; height: 24px; }
.cart-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: #FF6B6B;
  color: white;
  font-size: 10px;
  padding: 0 4px;
  border-radius: 9px;
  min-width: 18px;
  text-align: center;
  border: 2px solid white;
}

.bounce { animation: bounce 0.6s; }
@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}
</style>