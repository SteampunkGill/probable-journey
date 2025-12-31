<template>
  <div class="page-container">
    <!-- 顶部搜索栏 -->
    <div class="search-bar" :class="{ shadow: scrollTop > 50 }">
      <div class="search-box" @click="router.push('/search')">
        <img class="search-icon" src="@/assets/images/icons/search.png" />
        <span class="search-placeholder">搜索饮品、门店...</span>
      </div>
      <img class="scan-btn" src="@/assets/images/icons/scan.png" @click="onScan" />
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
      <!-- 通知栏 -->
      <div class="notification-bar" v-if="showNotification && currentNotification">
        <div class="notification-content">
          <img class="notice-icon" src="@/assets/images/icons/info.png" />
          <span class="notice-text">{{ currentNotification.title }}: {{ currentNotification.content }}</span>
        </div>
        <img class="close-icon" src="@/assets/images/icons/history.png" @click="closeNotification" />
      </div>

      <!-- 轮播图 -->
      <div class="banner-container" v-if="banners.length > 0">
        <div class="banner-wrapper" :style="{ transform: `translateX(-${currentBanner * 100}%)` }">
          <div class="banner-item" v-for="item in banners" :key="item.id" @click="onBannerClick(item)">
            <img :src="item.imageUrl" class="banner-image" />
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
      <div class="banner-placeholder" v-else></div>

      <!-- 门店选择组件 -->
      <div class="store-selector-section">
        <div class="current-store" @click="router.push('/address?type=select_store')">
          <div class="store-main">
            <img class="location-icon" src="@/assets/images/icons/address.png" />
            <div class="store-info-content">
              <div class="store-name-wrapper">
                <span class="store-name">{{ userStore.selectedStore?.name || '正在定位...' }}</span>
                <span class="store-distance" v-if="userStore.selectedStore?.distance">距您 {{ userStore.selectedStore.distance.toFixed(2) }}km</span>
              </div>
              <div class="store-address-text">{{ userStore.selectedStore?.address || '请选择门店' }}</div>
            </div>
            <img class="arrow-icon" src="@/assets/images/icons/right.png" />
          </div>
        </div>
        <div class="location-action" @click="reLocation">
          <img class="re-location-icon" :class="{ rotating: isLocating }" src="@/assets/images/icons/history.png" />
          <span>重新定位</span>
        </div>
      </div>

      <!-- 点餐方式选择 -->
      <div class="order-mode-section">
        <div class="mode-title">选择点餐方式</div>
        <div class="mode-buttons">
          <div class="mode-card delivery" @click="selectOrderMode('delivery')">
            <img class="mode-icon" src="@/assets/images/icons/order.png" />
            <div class="mode-info">
              <span class="mode-name">外卖配送</span>
              <span class="mode-desc">30分钟送达</span>
            </div>
          </div>
          
          <div class="mode-card pickup" @click="selectOrderMode('pickup')">
            <img class="mode-icon" src="@/assets/images/icons/pick_up_food.png" />
            <div class="mode-info">
              <span class="mode-name">到店自取</span>
              <span class="mode-desc">预计制作时间</span>
            </div>
          </div>
        </div>
      </div>

      <!-- 促销活动入口 (DEMO ONLY) -->
      <div class="promo-banner-section" v-if="activePromotions && activePromotions.length > 0">
        <div
          class="promo-banner-card"
          v-for="promo in activePromotions"
          :key="promo.id"
          @click="goToPromoOrder(promo)"
        >
          <div class="promo-banner-content">
            <div class="promo-tag">限时活动</div>
            <div class="promo-title">{{ promo.name }}</div>
            <div class="promo-subtitle">指定商品享超值折扣，点击立即抢购</div>
          </div>
          <div class="promo-action">
            <span>去抢购</span>
            <img class="arrow-icon" src="@/assets/images/icons/right.png" />
          </div>
        </div>
      </div>

      <!-- 快捷入口 -->
      <div class="quick-menu">
        <div class="menu-item" @click="router.push('/address?type=select_store')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="@/assets/images/icons/order.png" />
          </div>
          <span class="menu-name">点单</span>
        </div>
        <div class="menu-item" @click="router.push('/coupon')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="@/assets/images/icons/coupon.png" />
            <div class="coupon-badge" v-if="availableCouponCount > 0">{{ availableCouponCount }}</div>
          </div>
          <span class="menu-name">优惠券</span>
        </div>
        <div class="menu-item" @click="router.push('/wallet')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="@/assets/images/icons/gift.png" />
          </div>
          <span class="menu-name">钱包</span>
        </div>
        <div class="menu-item" @click="router.push('/share')">
          <div class="menu-icon-wrapper">
            <img class="menu-icon" src="@/assets/images/icons/gift.png" />
          </div>
          <span class="menu-name">分享有礼</span>
        </div>
      </div>

      <!-- 推荐商品 -->
      <div class="section" v-if="recommendProducts.length > 0">
        <div class="section-header">
          <span class="section-title">为你推荐</span>
          <div class="section-more" @click="router.push('/order')">
            <span>更多</span>
            <img class="arrow-icon" src="@/assets/images/icons/right.png" />
          </div>
        </div>
        
        <div class="recommend-scroll">
          <div class="recommend-list">
            <div
              class="recommend-item"
              v-for="item in recommendProducts"
              :key="item.id"
              @click="() => { localStorage.setItem('current_tea', JSON.stringify(item)); router.push(`/product/${item.id}`) }"
            >
              <img :src="item.image" class="recommend-image" />
              <div class="recommend-info">
                <span class="recommend-name">{{ item.name }}</span>
                <div class="recommend-bottom">
                  <span class="recommend-price">¥{{ item.price }}</span>
                  <img class="add-icon" src="@/assets/images/icons/add.png" @click.stop="quickAdd(item)" />
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
            <img class="arrow-icon" src="@/assets/images/icons/right.png" />
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
                <img class="add-icon" src="@/assets/images/icons/add.png" @click.stop="quickAdd(item)" />
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 附近门店 -->
      <div class="section" v-if="userStore.selectedStore">
        <div class="section-header">
          <span class="section-title">附近门店</span>
          <div class="section-more" @click="router.push('/address?type=select_store')">
            <span>更多</span>
            <img class="arrow-icon" src="@/assets/images/icons/right.png" />
          </div>
        </div>
        
        <div class="store-card" @click="router.push('/address?type=select_store')">
          <div class="store-header">
            <span class="store-name">{{ userStore.selectedStore.name }}</span>
            <div class="store-status" :class="userStore.selectedStore.status === 'OPEN' ? 'open' : 'closed'">
              {{ userStore.selectedStore.status === 'OPEN' ? '营业中' : '休息中' }}
            </div>
          </div>
          <div class="store-info">
            <img class="info-icon" src="@/assets/images/icons/address.png" />
            <span class="store-address">{{ userStore.selectedStore.address }}</span>
          </div>
          <div class="store-info">
            <img class="info-icon" src="@/assets/images/icons/info.png" />
            <span class="store-hours">{{ userStore.selectedStore.businessHours || '09:00-22:00' }}</span>
          </div>
          <div class="store-footer">
            <span class="store-distance" v-if="userStore.selectedStore.distance">距您 {{ userStore.selectedStore.distance.toFixed(2) }}km</span>
            <div class="store-call" @click.stop="makePhoneCall(userStore.selectedStore.phone)">
              <img class="call-icon" src="@/assets/images/icons/phone.png" />
              <span>联系门店</span>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- 购物车浮动按钮 -->
    <div class="cart-float" :class="{ show: cartCount > 0 }" @click="router.push('/cart')">
      <img class="cart-icon" src="@/assets/images/icons/cart.png" />
      <div class="cart-badge" :class="{ bounce: cartAnimating }">{{ cartCount }}</div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { useUserStore } from '@/store/user'
import { homeApi, storeApi, bannerApi, productApi, couponApi, promotionApi, addressApi } from '@/utils/api'
import { getDistance, formatImageUrl } from '@/utils/util'

const router = useRouter()
const cartStore = useCartStore()

// 使用工具类计算距离 (单位: km)
const calculateDistance = (lat1, lon1, lat2, lon2) => {
  if (!lat1 || !lon1 || !lat2 || !lon2) return null
  // getDistance 返回的是米，转换为千米
  return getDistance(lat1, lon1, lat2, lon2) / 1000
}
const userStore = useUserStore()

const loading = ref(true)
const isLocating = ref(false)
const scrollTop = ref(0)
const currentBanner = ref(0)
const banners = ref([])
const recommendProducts = ref([])
const hotProducts = ref([])
const availableCouponCount = ref(0)
const activePromotions = ref([])
const notifications = ref([])
const showNotification = ref(false)
const currentNotification = ref(null)
const cartCount = ref(0)
const cartAnimating = ref(false)

let bannerTimer = null

const getLocation = () => {
  return new Promise((resolve) => {
    if (!navigator.geolocation) {
      alert('您的浏览器不支持地理定位功能')
      resolve(null)
      return
    }

    // 检查是否是安全上下文 (HTTPS 或 localhost)
    if (window.location.protocol !== 'https:' && window.location.hostname !== 'localhost' && window.location.hostname !== '127.0.0.1') {
      console.warn('地理定位需要 HTTPS 环境或 localhost')
      // 在非安全环境下，getCurrentPosition 可能不会触发任何回调
    }

    navigator.geolocation.getCurrentPosition(
      (position) => {
        console.log('定位成功:', position.coords)
        resolve({
          latitude: position.coords.latitude,
          longitude: position.coords.longitude
        })
      },
      (error) => {
        console.error('获取定位失败:', error)
        let msg = '定位失败'
        switch(error.code) {
          case error.PERMISSION_DENIED:
            msg = '您已拒绝地理定位权限，请在浏览器设置中开启'
            break
          case error.POSITION_UNAVAILABLE:
            msg = '无法获取当前位置信息'
            break
          case error.TIMEOUT:
            msg = '定位请求超时，请重试'
            break
        }
        alert(msg)
        resolve(null)
      },
      {
        enableHighAccuracy: true,
        timeout: 10000,
        maximumAge: 0
      }
    )
  })
}

const updateLocationAndNearbyStores = async () => {
  isLocating.value = true
  try {
    const location = await getLocation()
    
    // 如果定位失败，尝试获取用户默认地址作为参考
    let refProvince = '', refCity = '', refDistrict = ''
    if (!location) {
      try {
        const addrRes = await addressApi.getAddressList()
        const defaultAddr = addrRes.find(a => a.isDefault) || addrRes[0]
        if (defaultAddr) {
          refProvince = defaultAddr.province
          refCity = defaultAddr.city
          refDistrict = defaultAddr.district
          console.log('定位失败，使用默认地址参考:', refCity)
        }
      } catch (e) {
        console.warn('获取参考地址失败')
      }
    }

    const params = {
      latitude: location?.latitude || null,
      longitude: location?.longitude || null,
      province: refProvince,
      city: refCity,
      district: refDistrict
    }
    
    const nearbyRes = await storeApi.getNearbyStores(params)
    
    // 统一处理后端返回的数据结构
    const resData = nearbyRes.data || nearbyRes
    if (resData && (Array.isArray(resData) || Array.isArray(resData.list))) {
      let stores = Array.isArray(resData) ? resData : resData.list
      
      // 如果有定位，计算精确距离并排序
      if (location) {
        stores = stores.map(store => {
          const distance = calculateDistance(
            location.latitude,
            location.longitude,
            store.latitude,
            store.longitude
          )
          return { ...store, distance }
        }).sort((a, b) => a.distance - b.distance)
      } else if (refCity) {
        // 如果没有定位但有参考城市，优先匹配同城门店
        stores = stores.sort((a, b) => {
          if (a.city === refCity && b.city !== refCity) return -1
          if (a.city !== refCity && b.city === refCity) return 1
          return 0
        })
      }
      
      const store = stores[0]
      userStore.setSelectedStore(store)
    }
  } catch (e) {
    console.error('更新定位和门店失败:', e)
  } finally {
    isLocating.value = false
  }
}

const loadData = async () => {
  loading.value = true
  try {
    const [homeRes, bannersRes, recommendRes] = await Promise.all([
      homeApi.getHomeData(),
      bannerApi.getBanners(),
      homeApi.getRecommendations()
    ])
    
    const homeData = homeRes.data || homeRes || {}
    const bannersData = bannersRes.data || bannersRes || []
    const recommendData = recommendRes.data || recommendRes || []
    
    banners.value = (Array.isArray(bannersData) ? bannersData : bannersData.list || []).map(b => {
      const imageUrl = b.imageUrl || b.image
      return { ...b, imageUrl: formatImageUrl(imageUrl) }
    })
    
    recommendProducts.value = (Array.isArray(recommendData) ? recommendData : recommendData.list || []).map(p => {
      const imageUrl = p.image || p.productImage || p.product?.mainImageUrl || p.product?.imageUrl || p.mainImageUrl || p.imageUrl
      return {
        ...p,
        image: formatImageUrl(imageUrl) || 'https://images.unsplash.com/photo-1544787210-2827443cb39b?w=200'
      }
    })
    
    const hotList = homeData.hotProducts || []
    hotProducts.value = hotList.map(p => {
      const imageUrl = p.image || p.productImage || p.product?.mainImageUrl || p.product?.imageUrl || p.mainImageUrl || p.imageUrl
      return {
        ...p,
        image: formatImageUrl(imageUrl) || 'https://images.unsplash.com/photo-1544787210-2827443cb39b?w=200'
      }
    })

    // DEMO ONLY: 将数据存入本地供搜索页面使用
    localStorage.setItem('demo_all_products', JSON.stringify([...recommendProducts.value, ...hotProducts.value]))
    
    // 获取促销活动 (DEMO ONLY)
    try {
      const promoRes = await promotionApi.getPromotionList()
      const resData = promoRes.data || promoRes
      const list = Array.isArray(resData) ? resData : (resData.list || [])
      activePromotions.value = list.filter(p => p.isActive && p.type === 'PROMOTION_DISCOUNT')
      console.log('首页加载促销活动成功:', activePromotions.value)
    } catch (e) {
      console.warn('获取促销活动失败:', e)
    }

    // 异步获取定位，不阻塞页面显示
    updateLocationAndNearbyStores()
    
    // 获取可用优惠券数量 (仅在已登录时获取)
    const token = localStorage.getItem('token')
    if (token && token !== 'undefined' && token !== 'null') {
      try {
        const couponRes = await couponApi.getMyCoupons()
        if (couponRes && (couponRes.code === 200 || couponRes.status === 'success')) {
          const resData = couponRes.data || couponRes
          const coupons = resData.list || resData || []
          if (Array.isArray(coupons)) {
            availableCouponCount.value = coupons.filter(c => c.status === 'UNUSED').length
          }
        }
      } catch (e) {
        console.warn('获取优惠券数量失败，用户可能未登录或Token失效:', e.message)
        // 不触发登出逻辑，静默失败
      }
    }
    
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
  userStore.setOrderType(mode)
  // 确保跳转到点餐页面
  router.push('/order')
}

const quickAdd = (product) => {
  cartStore.addToCart({
    ...product,
    storeId: userStore.selectedStore?.id,
    quantity: 1
  })
  cartCount.value = cartStore.totalCount
  cartAnimating.value = true
  setTimeout(() => cartAnimating.value = false, 600)
}

const onScan = () => {
  alert('扫码功能仅在移动端可用')
}

const reLocation = async () => {
  isLocating.value = true
  await loadData()
  isLocating.value = false
}

const makePhoneCall = (phone) => {
  window.location.href = `tel:${phone}`
}
const goToPromoOrder = (promo) => {
  // DEMO ONLY: 处理促销跳转筛选
  let productIds = []
  if (promo.rulesJson) {
    try {
      const rules = JSON.parse(promo.rulesJson)
      productIds = rules.productIds || []
    } catch (e) {
      console.error('解析规则失败:', e)
    }
  } else if (promo.productIds) {
    productIds = promo.productIds
  }

  if (productIds && productIds.length > 0) {
    sessionStorage.setItem('promo_filter_ids', JSON.stringify(productIds))
    sessionStorage.setItem('promo_name', promo.name)
  }
  
  router.push('/order')
}


const closeNotification = () => {
  if (currentNotification.value) {
    const closedNotices = JSON.parse(localStorage.getItem('closedNotices') || '[]')
    closedNotices.push(currentNotification.value.id)
    localStorage.setItem('closedNotices', JSON.stringify(closedNotices))
  }
  showNotification.value = false
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
  background: var(--background-color, #f5f0e1);
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

/* 顶部搜索栏 */
.search-bar {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  z-index: 100;
  padding: var(--spacing-lg) var(--spacing-xl);
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  background: transparent;
  transition: all 0.3s ease-out;
  backdrop-filter: blur(8px);
}

.search-bar.shadow {
  background: rgba(255, 255, 255, 0.95);
  box-shadow: 0 4px 20px rgba(160, 82, 45, 0.08);
  border-bottom: 2px solid var(--border-color, #d4c7b5);
}

.search-box {
  flex: 1;
  height: 48px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: var(--border-radius-xl);
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-lg);
  gap: var(--spacing-md);
  cursor: pointer;
  border: 2px solid transparent;
  transition: all 0.25s ease-out;
  box-shadow: 0 2px 8px rgba(160, 82, 45, 0.05);
}

.search-bar.shadow .search-box {
  background: var(--accent-cream, #fff8dc);
}

.search-box:hover {
  border-color: var(--primary-light, #d2b48c);
  transform: translateY(-1px);
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.1);
}

.search-icon {
  width: 20px;
  height: 20px;
  filter: drop-shadow(0 2px 4px rgba(160, 82, 45, 0.1));
}

.search-placeholder {
  font-size: 15px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
  letter-spacing: 0.03em;
}

.scan-btn {
  width: 32px;
  height: 32px;
  cursor: pointer;
  transition: all 0.25s ease-out;
  filter: drop-shadow(0 2px 6px rgba(160, 82, 45, 0.1));
}

.scan-btn:hover {
  transform: scale(1.1) rotate(5deg);
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 40px;
  padding-top: 80px;
}

/* 通知栏样式 */
.notification-bar {
  margin: 0 var(--spacing-lg) var(--spacing-md);
  background: linear-gradient(135deg, #fff8dc 0%, #fff 100%);
  padding: 12px 16px;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: space-between;
  border: 1px solid var(--primary-light);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.05);
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from { transform: translateY(-20px); opacity: 0; }
  to { transform: translateY(0); opacity: 1; }
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 10px;
  flex: 1;
  min-width: 0;
}

.notice-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.notice-text {
  font-size: 13px;
  color: var(--primary-dark);
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.close-icon {
  width: 16px;
  height: 16px;
  cursor: pointer;
  opacity: 0.5;
  transition: opacity 0.2s;
  margin-left: 10px;
}

.close-icon:hover {
  opacity: 1;
}

/* 轮播图 */
.banner-container {
  position: relative;
  height: 280px;
  overflow: hidden;
  margin: var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.12);
  border: 3px solid var(--accent-cream, #fff8dc);
}

.banner-wrapper {
  display: flex;
  height: 100%;
  transition: transform 0.5s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.banner-item {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
  position: relative;
  cursor: pointer;
}

.banner-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.banner-item:hover .banner-image {
  transform: scale(1.03);
}

.banner-dots {
  position: absolute;
  bottom: 20px;
  left: 0;
  right: 0;
  display: flex;
  justify-content: center;
  gap: 8px;
}

.dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.5);
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.dot.active {
  width: 24px;
  background: var(--primary-color, #a0522d);
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(160, 82, 45, 0.3);
}

.dot:hover {
  background: rgba(255, 255, 255, 0.8);
}

/* 门店选择组件 */
.store-selector-section {
  margin: var(--spacing-lg);
  background: var(--surface-color, #e8dccb);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: var(--spacing-lg);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color, #d4c7b5);
  backdrop-filter: blur(4px);
  transition: all 0.25s ease-out;
}

.store-selector-section:hover {
  transform: translateY(-2px);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.12);
  border-color: var(--primary-light, #d2b48c);
}

.current-store {
  flex: 1;
  cursor: pointer;
  min-width: 0;
}

.store-main {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.location-icon {
  width: 28px;
  height: 28px;
  flex-shrink: 0;
  filter: drop-shadow(0 2px 4px rgba(160, 82, 45, 0.1));
}

.store-info-content {
  flex: 1;
  min-width: 0;
}

.store-name-wrapper {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
  margin-bottom: 4px;
}

.store-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.03em;
}

.store-distance {
  font-size: 13px;
  color: var(--accent-brown, #deb887);
  white-space: nowrap;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
}

.store-address-text {
  font-size: 13px;
  color: var(--text-color-medium, #7a6a5b);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  font-family: 'Nunito', sans-serif;
}

.arrow-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
  transition: transform 0.3s ease;
}

.current-store:hover .arrow-icon {
  transform: translateX(4px);
}

.location-action {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding-left: var(--spacing-lg);
  border-left: 2px solid var(--border-color, #d4c7b5);
  cursor: pointer;
  flex-shrink: 0;
  transition: all 0.25s ease-out;
}

.location-action:hover {
  transform: translateY(-2px);
}

.re-location-icon {
  width: 24px;
  height: 24px;
  transition: transform 0.3s ease;
}

.location-action:hover .re-location-icon {
  transform: rotate(15deg);
}

.location-action span {
  font-size: 11px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 点餐方式选择 */
.order-mode-section {
  margin: 0 var(--spacing-lg) var(--spacing-lg);
  background: white;
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color, #d4c7b5);
  backdrop-filter: blur(4px);
}

.mode-title {
  font-size: 17px;
  font-weight: 700;
  margin-bottom: var(--spacing-lg);
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.03em;
}

.mode-buttons {
  display: flex;
  gap: var(--spacing-lg);
}

.mode-card {
  flex: 1;
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  gap: var(--spacing-md);
  cursor: pointer;
  transition: all 0.25s ease-out;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.mode-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.15);
}

.mode-card.delivery {
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.8), rgba(255, 240, 225, 0.9));
  border-color: var(--accent-cream, #fff8dc);
}

.mode-card.pickup {
  background: linear-gradient(135deg, rgba(222, 184, 135, 0.1), rgba(210, 180, 140, 0.2));
  border-color: var(--accent-brown, #deb887);
}

.mode-icon {
  width: 48px;
  height: 48px;
  transition: transform 0.3s ease;
}

.mode-card:hover .mode-icon {
  transform: scale(1.1);
}

.mode-info {
  display: flex;
  flex-direction: column;
}

.mode-name {
  font-size: 15px;
  font-weight: 700;
  display: block;
  margin-bottom: 4px;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', serif;
  letter-spacing: 0.03em;
}

.mode-desc {
  font-size: 12px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
}

/* 快捷入口 */
.quick-menu {
  display: flex;
  justify-content: space-around;
  padding: var(--spacing-lg);
  background: white;
  margin: 0 var(--spacing-lg) var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color, #d4c7b5);
}

.menu-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  transition: all 0.25s ease-out;
  position: relative;
}

.menu-item:hover {
  transform: translateY(-4px);
}

.menu-icon-wrapper {
  width: 56px;
  height: 56px;
  background: linear-gradient(135deg, var(--accent-cream, #fff8dc), rgba(255, 248, 220, 0.8));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  position: relative;
  border: 3px solid var(--border-color, #d4c7b5);
  transition: all 0.3s ease;
}

.menu-item:hover .menu-icon-wrapper {
  background: linear-gradient(135deg, var(--primary-light, #d2b48c), var(--accent-cream, #fff8dc));
  border-color: var(--primary-color, #a0522d);
  transform: scale(1.1);
}

.menu-icon {
  width: 28px;
  height: 28px;
  transition: transform 0.3s ease;
}

.menu-item:hover .menu-icon {
  transform: scale(1.1);
}

.menu-name {
  font-size: 13px;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  letter-spacing: 0.03em;
}

.coupon-badge {
  position: absolute;
  top: -4px;
  right: -4px;
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 12px;
  border: 3px solid white;
  font-weight: 700;
  min-width: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

/* 通用区块样式 */
.section {
  margin: 0 var(--spacing-lg) var(--spacing-xl);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.section-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.03em;
}

.section-more {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  color: var(--text-color-medium, #7a6a5b);
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  transition: all 0.25s ease-out;
}

.section-more:hover {
  color: var(--primary-color, #a0522d);
  transform: translateX(4px);
}

.section-more:hover .arrow-icon {
  transform: translateX(4px);
}

/* 推荐商品 */
.recommend-scroll {
  overflow-x: auto;
  padding-bottom: 10px;
  margin: 0 -15px;
  padding: 0 15px;
}

.recommend-list {
  display: flex;
  gap: var(--spacing-lg);
}

.recommend-item {
  width: 160px;
  flex-shrink: 0;
  background: white;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  cursor: pointer;
  transition: all 0.25s ease-out;
  border: 2px solid var(--border-color, #d4c7b5);
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.08);
}

.recommend-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.15);
  border-color: var(--primary-light, #d2b48c);
}

.recommend-image {
  width: 160px;
  height: 160px;
  object-fit: cover;
  transition: transform 0.5s ease;
}

.recommend-item:hover .recommend-image {
  transform: scale(1.05);
}

.recommend-info {
  padding: var(--spacing-md);
}

.recommend-name {
  font-size: 14px;
  font-weight: 700;
  display: block;
  margin-bottom: var(--spacing-sm);
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', serif;
  letter-spacing: 0.03em;
  line-height: 1.4;
}

.recommend-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.recommend-price {
  font-size: 16px;
  color: var(--primary-color, #a0522d);
  font-weight: 700;
  font-family: 'Prompt', serif;
  letter-spacing: 0.03em;
}

.add-icon {
  width: 24px;
  height: 24px;
  cursor: pointer;
  transition: all 0.25s ease-out;
  filter: drop-shadow(0 2px 4px rgba(160, 82, 45, 0.1));
}

.add-icon:hover {
  transform: scale(1.2) rotate(15deg);
}

/* 今日热门 */
.hot-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.hot-item {
  display: flex;
  background: white;
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  gap: var(--spacing-lg);
  cursor: pointer;
  transition: all 0.25s ease-out;
  border: 2px solid var(--border-color, #d4c7b5);
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.08);
}

.hot-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.15);
  border-color: var(--primary-light, #d2b48c);
}

.hot-image {
  width: 100px;
  height: 100px;
  border-radius: var(--border-radius-md);
  object-fit: cover;
  border: 3px solid var(--accent-cream, #fff8dc);
  transition: transform 0.5s ease;
}

.hot-item:hover .hot-image {
  transform: scale(1.05);
}

.hot-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.hot-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', serif;
  letter-spacing: 0.03em;
  margin-bottom: 4px;
}

.hot-desc {
  font-size: 12px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
  line-height: 1.4;
  margin-bottom: var(--spacing-md);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.hot-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.hot-meta {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.hot-price {
  font-size: 16px;
  color: var(--primary-color, #a0522d);
  font-weight: 700;
  font-family: 'Prompt', serif;
  letter-spacing: 0.03em;
}

.hot-sales {
  font-size: 12px;
  color: var(--accent-brown, #deb887);
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
}

/* 附近门店 */
.store-card {
  background: white;
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color, #d4c7b5);
  cursor: pointer;
  transition: all 0.25s ease-out;
}

.store-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.15);
  border-color: var(--primary-light, #d2b48c);
}

.store-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.store-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', serif;
  letter-spacing: 0.03em;
}

.store-status {
  font-size: 12px;
  padding: 4px 10px;
  border-radius: var(--border-radius-md);
  font-weight: 600;
  font-family: 'Nunito', sans-serif;
  letter-spacing: 0.03em;
}

.store-status.open {
  background: linear-gradient(135deg, rgba(44, 150, 120, 0.15), rgba(44, 150, 120, 0.25));
  color: #2c9678;
  border: 1px solid rgba(44, 150, 120, 0.3);
}

.store-status.closed {
  background: linear-gradient(135deg, rgba(193, 60, 60, 0.15), rgba(193, 60, 60, 0.25));
  color: #c13c3c;
  border: 1px solid rgba(193, 60, 60, 0.3);
}

.store-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  margin-bottom: var(--spacing-sm);
  font-size: 13px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
}

.info-icon {
  width: 16px;
  height: 16px;
  opacity: 0.7;
}

.store-footer {
  margin-top: var(--spacing-md);
  padding-top: var(--spacing-md);
  border-top: 2px solid var(--border-color, #d4c7b5);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.store-distance {
  font-size: 13px;
  color: var(--accent-brown, #deb887);
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
}

.store-call {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--primary-color, #a0522d);
  font-size: 13px;
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  padding: 6px 12px;
  border-radius: var(--border-radius-md);
  background: rgba(160, 82, 45, 0.1);
  border: 2px solid transparent;
  transition: all 0.25s ease-out;
}

.store-call:hover {
  background: rgba(160, 82, 45, 0.2);
  border-color: var(--primary-color, #a0522d);
  transform: translateY(-2px);
}

.call-icon {
  width: 16px;
  height: 16px;
}

/* 购物车浮动按钮 */
.cart-float {
  position: fixed;
  right: var(--spacing-xl);
  bottom: calc(80px + var(--spacing-xl));
  width: 64px;
  height: 64px;
  background: linear-gradient(135deg, var(--primary-color, #a0522d), var(--primary-dark, #8b4513));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
  transform: scale(0);
  transition: transform 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  cursor: pointer;
  z-index: 99;
  border: 3px solid var(--accent-cream, #fff8dc);
}

.cart-float.show {
  transform: scale(1);
}

.cart-float:hover {
  transform: scale(1.1);
  box-shadow: 0 12px 35px rgba(160, 82, 45, 0.5);
}

.cart-icon {
  width: 28px;
  height: 28px;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: linear-gradient(135deg, #ff6b6b, #ff8e8e);
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 12px;
  min-width: 22px;
  text-align: center;
  border: 3px solid white;
  font-weight: 700;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.bounce {
  animation: bounce 0.6s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

/* 骨架屏样式 */
.skeleton-container {
  padding: var(--spacing-lg);
}

.skeleton-banner {
  height: 280px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  border-radius: var(--border-radius-xl);
  margin-bottom: var(--spacing-lg);
  animation: shimmer 1.5s infinite;
}

.skeleton-mode {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.skeleton-mode-item {
  flex: 1;
  height: 100px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  border-radius: var(--border-radius-lg);
  animation: shimmer 1.5s infinite;
}

.skeleton-menu {
  display: flex;
  justify-content: space-around;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.skeleton-menu-item {
  width: 56px;
  height: 56px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  border-radius: 50%;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: -200px 0; }
  100% { background-position: 200px 0; }
}

.promo-banner-section {
  margin: 0 var(--spacing-lg) var(--spacing-lg);
}

.promo-banner-card {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5253 100%);
  border-radius: var(--border-radius-lg);
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  color: white;
  cursor: pointer;
  box-shadow: 0 4px 15px rgba(238, 82, 83, 0.3);
  transition: transform 0.2s;
  margin-bottom: 12px;
}

.promo-banner-card:hover {
  transform: translateY(-2px);
}

.promo-tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 10px;
  width: fit-content;
  margin-bottom: 4px;
}

.promo-title {
  font-size: 16px;
  font-weight: bold;
}

.promo-subtitle {
  font-size: 12px;
  opacity: 0.9;
}

.promo-action {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .search-bar {
    padding: var(--spacing-md);
  }

  .main-content {
    padding-top: 70px;
  }

  .banner-container {
    height: 200px;
    margin: var(--spacing-md);
  }

  .store-selector-section,
  .order-mode-section,
  .quick-menu,
  .section {
    margin: 0 var(--spacing-md) var(--spacing-lg);
  }

  .mode-buttons {
    flex-direction: column;
  }

  .recommend-item {
    width: 140px;
  }

  .recommend-image {
    width: 140px;
    height: 140px;
  }

  .hot-item {
    flex-direction: column;
  }

  .hot-image {
    width: 100%;
    height: 120px;
  }

  .cart-float {
    width: 56px;
    height: 56px;
    right: var(--spacing-md);
    bottom: var(--spacing-md);
  }
}
</style>