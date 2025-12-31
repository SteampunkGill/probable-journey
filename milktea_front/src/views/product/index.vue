<template>
  <div class="product-detail-page">
    <!-- 商品图片区域 -->
    <div class="product-images">
      <div class="swiper-container">
        <div class="swiper-wrapper" :style="{ transform: `translateX(-${currentImageIndex * 100}%)` }">
          <div class="swiper-item" v-for="(img, index) in product.images" :key="index">
            <img :src="img" class="product-image" />
          </div>
        </div>
        <div class="image-indicator">
          <span>{{ currentImageIndex + 1 }}/{{ product.images.length }}</span>
        </div>
      </div>
      
      <!-- 收藏按钮 -->
      <div 
        class="favorite-btn" 
        :class="{ active: isFavorite }" 
        @click="toggleFavorite"
      >
        <img 
          class="favorite-icon"
          :src="isFavorite ? heartFillIcon : heartIcon"
        />
      </div>
    </div>

    <!-- 商品基本信息 -->
    <div class="product-basic-info card">
      <div class="product-header">
        <span class="product-name">{{ product.name }}</span>
        <div class="product-tags">
          <span class="tag tag-hot" v-if="product.isHot">🔥 热销</span>
          <span class="tag tag-new" v-if="product.isNew">✨ 新品</span>
          <span class="tag tag-recommend" v-if="product.isRecommend">👍 推荐</span>
        </div>
      </div>
      
      <div class="product-meta">
        <div class="price-section">
          <span class="current-price">¥{{ product.price }}</span>
          <span class="original-price" v-if="product.originalPrice > product.price">
            ¥{{ product.originalPrice }}
          </span>
          <span class="discount" v-if="product.discount">{{ product.discount }}折</span>
        </div>
        
        <div class="rating-section">
          <div class="rating-stars">
            <i 
              class="iconfont" 
              v-for="i in 5" 
              :key="i"
              :class="i <= Math.floor(product.rating) ? 'icon-star-fill' : 'icon-star'"
            ></i>
          </div>
          <span class="rating-text">{{ product.rating }}分</span>
          <span class="sales-text">已售{{ product.sales }}件</span>
        </div>
      </div>
      
      <div class="product-desc">
        <span>{{ product.description }}</span>
      </div>
    </div>

    <!-- 门店信息 -->
    <div class="store-info-section card" v-if="product.store">
      <div class="section-title">📍 供应门店</div>
      <div class="store-card-mini" @click="router.push('/order')">
        <div class="store-main">
          <span class="store-name">{{ product.store.name }}</span>
          <span class="store-distance" v-if="product.store.distance">{{ product.store.distance }}km</span>
        </div>
        <div class="store-address">{{ product.store.address }}</div>
        <div class="store-footer">
          <span class="store-status" :class="{ open: product.store.isOpen }">
            {{ product.store.isOpen ? '营业中' : '休息中' }}
          </span>
          <span class="store-time">{{ product.store.businessHours }}</span>
        </div>
        <span class="arrow">›</span>
      </div>
    </div>

    <!-- 定制化选项 -->
    <div class="customization-section card">
      <div class="section-title">⚙ 定制你的专属饮品</div>
      
      <!-- 甜度选择 -->
      <div class="customization-item">
        <div class="item-header">
          <span class="item-label">甜度</span>
          <span class="item-required">必选</span>
        </div>
        <div class="option-group">
          <div 
            class="option-item" 
            :class="{ selected: customizations.sweetness === option.value }" 
            v-for="option in sweetnessOptions" 
            :key="option.value"
            @click="customizations.sweetness = option.value"
          >
            <span class="option-label">{{ option.label }}</span>
            <span class="option-hint">{{ option.hint }}</span>
          </div>
        </div>
      </div>

      <!-- 温度选择 -->
      <div class="customization-item">
        <div class="item-header">
          <span class="item-label">温度</span>
          <span class="item-required">必选</span>
        </div>
        <div class="option-group">
          <div 
            class="option-item" 
            :class="{ selected: customizations.temperature === option.value }" 
            v-for="option in temperatureOptions" 
            :key="option.value"
            @click="customizations.temperature = option.value"
          >
            <span class="option-label">{{ option.label }}</span>
          </div>
        </div>
      </div>

      <!-- 加料选择 -->
      <div class="customization-item">
        <div class="item-header">
          <span class="item-label">加料</span>
          <span class="item-hint">可选，最多选择{{ maxToppings }}种</span>
        </div>
        <div class="toppings-grid">
          <div 
            class="topping-item" 
            :class="{ selected: customizations.toppings.includes(topping.id) }" 
            v-for="topping in toppingOptions" 
            :key="topping.id"
            @click="toggleTopping(topping)"
          >
            <div class="topping-info">
              <img :src="topping.icon" class="topping-icon" />
              <div class="topping-text">
                <span class="topping-name">{{ topping.name }}</span>
                <span class="topping-price">+¥{{ topping.price }}</span>
              </div>
            </div>
            <div class="topping-quantity" v-if="customizations.toppings.includes(topping.id)">
              <i class="iconfont icon-check"></i>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 加料总价 -->
      <div class="toppings-total" v-if="toppingsCost > 0">
        <span>加料费用：</span>
        <span class="toppings-price">+¥{{ toppingsCost.toFixed(2) }}</span>
      </div>
    </div>

    <!-- 商品详情 -->
    <div class="product-detail-section card">
      <div class="detail-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 0 }" 
          @click="activeTab = 0"
        >
          商品详情
        </div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 1 }" 
          @click="activeTab = 1"
        >
          营养成分
        </div>
        <div
          class="tab-item"
          :class="{ active: activeTab === 2 }"
          @click="activeTab = 2"
        >
          用户评价
        </div>
      </div>
      
      <div class="tab-content" v-if="activeTab === 0">
        <div class="rich-text" v-html="product.detailHtml"></div>
        <div class="ingredients" v-if="product.ingredients?.length">
          <span class="ingredients-title">原料成分：</span>
          <div class="ingredients-list">
            <span v-for="(item, index) in product.ingredients" :key="index">{{ item }}</span>
          </div>
        </div>
      </div>
      
      <div class="tab-content nutrition" v-if="activeTab === 1">
        <div class="nutrition-table">
          <div class="nutrition-row header">
            <span>营养成分</span>
            <span>含量(每杯)</span>
          </div>
          <div class="nutrition-row" v-for="item in product.nutrition" :key="item.name">
            <span class="nutrition-name">{{ item.name }}</span>
            <span class="nutrition-value">{{ item.value }}{{ item.unit || '' }}</span>
          </div>
        </div>
      </div>
      
      <div class="tab-content comments" v-if="activeTab === 2">
        <div class="comment-list">
          <div class="comment-item" v-for="item in comments" :key="item.id">
            <div class="comment-header">
              <img :src="item.userAvatar" class="user-avatar" />
              <div class="user-info">
                <span class="user-name">{{ item.userName }}</span>
                <div class="comment-rating">
                  <i 
                    class="iconfont" 
                    v-for="i in 5" 
                    :key="i"
                    :class="i <= item.rating ? 'icon-star-fill' : 'icon-star'"
                  ></i>
                </div>
              </div>
              <span class="comment-time">{{ item.createTime }}</span>
            </div>
            <div class="comment-content">
              <span>{{ item.content }}</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-action-bar">
      <div class="bottom-left">
        <div class="cart-icon" @click="router.push('/cart')">
          <img class="cart-icon-img" src="../../assets/images/icons/cart.png" />
          <div class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</div>
        </div>
        <div class="price-info">
          <span class="total-label">合计：</span>
          <span class="total-price">¥{{ totalPrice }}</span>
        </div>
      </div>
      
      <div class="bottom-right">
        <button 
          class="btn btn-secondary add-to-cart-btn" 
          @click="addToCart"
          v-if="product.stock > 0"
        >
          加入购物车
        </button>
        <button 
          class="btn btn-primary buy-now-btn" 
          @click="buyNow"
          v-if="product.stock > 0"
        >
          立即购买
        </button>
        <div class="sold-out-btn" v-else>
          <span>已售罄</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '../../store/cart'
import { productApi, favoriteApi } from '../../utils/api.js'
import heartIcon from '../../assets/images/icons/heart.png'
import heartFillIcon from '../../assets/images/icons/heart-fill.png'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const product = ref({
  id: '',
  name: '',
  images: [],
  price: 0,
  originalPrice: 0,
  description: '',
  rating: 0,
  sales: 0,
  stock: 0,
  detailHtml: '',
  ingredients: [],
  nutrition: [],
  commentCount: 0,
  isHot: false,
  isNew: false,
  isRecommend: false
})

const currentImageIndex = ref(0)
const isFavorite = ref(false)
const activeTab = ref(0)
const cartCount = computed(() => cartStore.totalCount)
const loading = ref(false)

const customizations = ref({
  sweetness: 'normal',
  temperature: 'normal',
  toppings: [],
  quantity: 1
})

const sweetnessOptions = ref([
  { value: 'no_sugar', label: '无糖', hint: '0%' },
  { value: 'low_sugar', label: '三分糖', hint: '30%' },
  { value: 'half_sugar', label: '五分糖', hint: '50%' },
  { value: 'less_sugar', label: '七分糖', hint: '70%' },
  { value: 'normal', label: '正常糖', hint: '100%' }
])

const temperatureOptions = ref([
  { value: 'hot', label: '热' },
  { value: 'warm', label: '温' },
  { value: 'no_ice', label: '去冰' },
  { value: 'less_ice', label: '少冰' },
  { value: 'normal', label: '正常冰' }
])

const toppingOptions = ref([])

const maxToppings = 5

const comments = ref([
  {
    id: 1,
    userName: '茶饮爱好者',
    userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=1',
    rating: 5,
    createTime: '2023-10-24',
    content: '味道非常正宗，甜度刚刚好，珍珠很Q弹！'
  },
  {
    id: 2,
    userName: '小红薯用户',
    userAvatar: 'https://api.dicebear.com/7.x/avataaars/svg?seed=2',
    rating: 4,
    createTime: '2023-10-23',
    content: '包装很好看，配送也很快，下次还会再点。'
  }
])

const toppingsCost = computed(() => {
  return customizations.value.toppings.reduce((total, toppingId) => {
    const topping = toppingOptions.value.find(t => t.id === toppingId || t.name === toppingId || t.id === Number(toppingId))
    return total + (topping ? topping.price : 0)
  }, 0)
})

const totalPrice = computed(() => {
  return ((product.value.price + toppingsCost.value) * customizations.value.quantity).toFixed(2)
})

const toggleTopping = (topping) => {
  const index = customizations.value.toppings.indexOf(topping.id)
  if (index > -1) {
    customizations.value.toppings.splice(index, 1)
  } else if (customizations.value.toppings.length < maxToppings) {
    customizations.value.toppings.push(topping.id)
  }
}

const toggleFavorite = async () => {
  if (!product.value.id) return
  try {
    if (isFavorite.value) {
      const res = await favoriteApi.removeFavorite(product.value.id)
      if (res.code === 200 || res.status === 'success') {
        isFavorite.value = false
        alert('已取消收藏')
      }
    } else {
      const res = await favoriteApi.addFavorite(product.value.id)
      if (res.code === 200 || res.status === 'success') {
        isFavorite.value = true
        alert('已收藏')
      }
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
  }
}

const checkFavoriteStatus = async (id) => {
  try {
    const res = await favoriteApi.checkFavorite(id)
    // 拦截器返回 res.data 或 res
    isFavorite.value = res.data === true || res === true
  } catch (error) {
    console.error('检查收藏状态失败:', error)
  }
}

const addToCart = async () => {
  try {
    await cartStore.addToCart({
      ...product.value,
      customizations: { ...customizations.value },
      toppingsCost: toppingsCost.value,
      quantity: customizations.value.quantity
    })
    alert('已加入购物车')
  } catch (error) {
    console.error('加入购物车失败', error)
  }
}

const buyNow = async () => {
  try {
    const orderItem = {
      productId: product.value.id,
      quantity: customizations.value.quantity,
      sweetness: customizations.value.sweetness,
      temperature: customizations.value.temperature,
      toppings: customizations.value.toppings
    }
    // 立即购买通常也是先加入购物车或直接创建订单，这里根据业务逻辑跳转
    // 如果后端有立即购买接口，则调用；否则存入临时状态
    localStorage.setItem('checkoutItems', JSON.stringify([{
      ...product.value,
      customizations: { ...customizations.value },
      toppingsCost: toppingsCost.value,
      quantity: customizations.value.quantity
    }]))
    router.push('/order/checkout')
  } catch (error) {
    console.error('立即购买失败', error)
  }
}

const fetchProductDetail = async (id) => {
  loading.value = true
  
  try {
    const [detailRes, customRes] = await Promise.all([
      productApi.getProductDetail(id),
      productApi.getProductCustomizations(id)
    ])
    
    const res = detailRes.data || detailRes

    product.value = {
      ...res,
      id: res.id,
      name: res.name,
      images: res.images || [res.imageUrl].filter(Boolean),
      price: res.price || product.value.price,
      originalPrice: res.originalPrice || res.price,
      description: res.description,
      rating: res.rating || 5.0,
      sales: res.sales || 0,
      stock: res.stock || 0,
      detailHtml: res.detailHtml || res.description,
      ingredients: res.ingredients || [],
      nutrition: res.nutrition || [],
      commentCount: res.commentCount || 0,
      isHot: res.isHot || false,
      isNew: res.isNew || false,
      isRecommend: res.isRecommend || false,
      store: res.store || {
        id: 1,
        name: '饮饮茶 (总店)',
        address: '广州市天河区珠江新城兴盛路10号',
        distance: '0.8',
        isOpen: true,
        businessHours: '09:00-22:00'
      }
    }

    if (customRes && customRes.data) {
      const { specs, options } = customRes.data
      // 处理规格 (糖度、温度)
      if (specs && specs.length > 0) {
        const sweetness = specs.filter(s => s.type === 'SWEETNESS')
        if (sweetness.length > 0) {
          sweetnessOptions.value = sweetness.map(s => ({ value: s.name, label: s.name }))
          customizations.value.sweetness = sweetness.find(s => s.isDefault)?.name || sweetness[0].name
        }
        
        const temperature = specs.filter(s => s.type === 'TEMPERATURE')
        if (temperature.length > 0) {
          temperatureOptions.value = temperature.map(s => ({ value: s.name, label: s.name }))
          customizations.value.temperature = temperature.find(s => s.isDefault)?.name || temperature[0].name
        }
      }
      // 处理加料
      if (options && options.length > 0) {
        toppingOptions.value = options.filter(o => o.type === 'TOPPING').map(o => ({
          id: o.id,
          name: o.name,
          price: o.price,
          icon: o.iconUrl || 'https://images.unsplash.com/photo-1567095761054-7a02e69e5c43?w=50'
        }))
      }
    }
  } catch (error) {
    console.error('获取商品详情失败', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  const id = route.params.id || route.query.id
  if (id) {
    product.value.id = id
    fetchProductDetail(id)
    checkFavoriteStatus(id)
  }
})
</script>
<style scoped>
/* ============================================
“饮饮茶(SipSipTea)” 商品详情页样式优化
基于奶茶主题设计指南
============================================ */

/* ========== 页面容器 ========== */
.product-detail-page {
  min-height: 100vh;
  background: var(--background-color);
  padding-bottom: 90px;
  position: relative;
  overflow-x: hidden;
}

/* 奶茶主题背景装饰 */
.product-detail-page::before {
  content: '';
  position: absolute;
  top: -12%;
  right: -6%;
  width: 140px;
  height: 140px;
  background: radial-gradient(circle, var(--accent-cream) 0%, transparent 70%);
  opacity: 0.15;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

.product-detail-page::after {
  content: '';
  position: absolute;
  bottom: 8%;
  left: -4%;
  width: 90px;
  height: 90px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.1;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

/* ========== 商品图片区域 ========== */
.product-images {
  position: relative;
  height: 420px;
  background: white;
  border-bottom-left-radius: var(--border-radius-xl);
  border-bottom-right-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
}

.swiper-container {
  height: 100%;
  overflow: hidden;
  position: relative;
  background: var(--surface-color);
}

.swiper-wrapper {
  display: flex;
  height: 100%;
  transition: transform 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.swiper-item {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
  position: relative;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.6s ease-out;
}

.swiper-item:hover .product-image {
  transform: scale(1.05);
}

/* 图片指示器 */
.image-indicator {
  position: absolute;
  bottom: var(--spacing-lg);
  right: var(--spacing-lg);
  background: rgba(0, 0, 0, 0.4);
  color: white;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-lg);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 2;
  animation: fadeIn 0.5s ease-out;
}

/* 收藏按钮 */
.favorite-btn {
  position: absolute;
  top: var(--spacing-lg);
  right: var(--spacing-lg);
  width: 48px;
  height: 48px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: var(--border-radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-lg);
  cursor: pointer;
  transition: all var(--transition-bounce);
  z-index: 2;
  border: 2px solid rgba(160, 82, 45, 0.1);
}

.favorite-btn:hover {
  transform: scale(1.15) rotate(5deg);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.2);
  border-color: rgba(160, 82, 45, 0.2);
}

.favorite-btn.active {
  background: linear-gradient(135deg, #FF6B6B, var(--primary-color));
  border-color: transparent;
  animation: heartBeat 0.6s ease-out;
}

@keyframes heartBeat {
  0%, 100% { transform: scale(1); }
  25% { transform: scale(1.2); }
  50% { transform: scale(0.95); }
  75% { transform: scale(1.1); }
}

.favorite-icon {
  width: 24px;
  height: 24px;
  transition: all var(--transition-normal);
}

.favorite-btn.active .favorite-icon {
  filter: brightness(0) invert(1);
  animation: pulse 1.5s infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.8; }
}

/* ========== 卡片基础样式 ========== */
.card {
  background: white;
  margin: var(--spacing-md);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-sm);
  border: 1px solid var(--border-color);
  transition: all var(--transition-normal);
  position: relative;
  z-index: 1;
  animation: fadeInUp 0.6s ease-out var(--delay, 0.2s) both;
}

.card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-3px);
}

/* 门店信息 */
.store-info-section {
  --delay: 0.35s;
}

.store-card-mini {
  background: var(--surface-color);
  padding: 16px;
  border-radius: 16px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
}

.store-card-mini:hover {
  background: var(--accent-cream);
  transform: translateX(5px);
}

.store-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.store-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-color-dark);
}

.store-distance {
  font-size: 13px;
  color: var(--primary-color);
  font-weight: 600;
}

.store-address {
  font-size: 13px;
  color: var(--text-color-medium);
  margin-bottom: 8px;
  padding-right: 20px;
}

.store-footer {
  display: flex;
  align-items: center;
  gap: 12px;
}

.store-status {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
  background: #ccc;
  color: white;
}

.store-status.open {
  background: #52C41A;
}

.store-time {
  font-size: 12px;
  color: var(--text-color-light);
}

.store-card-mini .arrow {
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 20px;
  color: var(--text-color-light);
}

/* 商品基本信息卡片 */
.product-basic-info {
  --delay: 0.3s;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: var(--spacing-md);
}

.product-name {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  line-height: var(--line-height-tight);
  flex: 1;
}

.product-tags {
  display: flex;
  gap: var(--spacing-xs);
  flex-wrap: wrap;
}

.tag {
  font-size: var(--font-size-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.3px;
  transition: all var(--transition-normal);
}

.tag:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.tag-hot {
  background: linear-gradient(135deg, #FFF1F0, #FFE8E6);
  color: #FF4D4F;
  border: 1px solid rgba(255, 77, 79, 0.2);
}

.tag-new {
  background: linear-gradient(135deg, #F6FFED, #F0FFE6);
  color: #52C41A;
  border: 1px solid rgba(82, 196, 26, 0.2);
}

.tag-recommend {
  background: linear-gradient(135deg, var(--accent-cream), #FFF8E6);
  color: var(--primary-color);
  border: 1px solid rgba(160, 82, 45, 0.2);
}

/* 价格区域 */
.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-sm);
}

.current-price {
  font-family: 'Prompt', sans-serif;
  font-size: 2.2rem;
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
  text-shadow: 0 2px 4px rgba(160, 82, 45, 0.1);
}

.original-price {
  font-size: var(--font-size-sm);
  color: var(--text-color-light);
  text-decoration: line-through;
  font-weight: var(--font-weight-normal);
}

.discount {
  font-size: var(--font-size-xs);
  color: white;
  background: linear-gradient(135deg, #FF6B6B, var(--primary-color));
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-weight: var(--font-weight-bold);
  box-shadow: 0 2px 6px rgba(255, 107, 107, 0.3);
}

/* 评分区域 */
.rating-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: var(--font-size-sm);
  color: var(--text-color-medium);
}

.rating-stars {
  display: flex;
  gap: 2px;
}

.rating-stars .iconfont {
  font-size: var(--font-size-base);
  color: #FFD700;
  transition: transform var(--transition-normal);
}

.rating-stars:hover .iconfont {
  transform: scale(1.1);
}

.rating-text {
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
}

.sales-text {
  background: var(--surface-color);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  color: var(--text-color-medium);
  transition: all var(--transition-normal);
}

.sales-text:hover {
  background: var(--primary-light);
  color: var(--primary-dark);
}

/* 商品描述 */
.product-desc {
  margin-top: var(--spacing-md);
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  line-height: var(--line-height-relaxed);
  padding: var(--spacing-md);
  background: var(--surface-color);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--primary-light);
}

/* ========== 定制化选项 ========== */
.customization-section {
  --delay: 0.4s;
}

.section-title {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  padding-bottom: var(--spacing-sm);
  border-bottom: 2px solid var(--border-color);
}

.section-title::before {
  content: '⚙';
  font-size: var(--font-size-base);
  color: var(--primary-color);
}

.customization-item {
  margin-bottom: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
}

.customization-item:last-child {
  margin-bottom: 0;
  padding-bottom: 0;
  border-bottom: none;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
}

.item-label {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.item-label::before {
  content: '•';
  color: var(--primary-color);
  font-size: var(--font-size-lg);
}

.item-required {
  font-size: var(--font-size-xs);
  color: #FF4D4F;
  background: rgba(255, 77, 79, 0.1);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-weight: var(--font-weight-medium);
}

.item-hint {
  font-size: var(--font-size-xs);
  color: var(--text-color-light);
  font-weight: var(--font-weight-normal);
}

/* 选项组 */
.option-group {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.option-item {
  padding: var(--spacing-sm) var(--spacing-lg);
  background: var(--surface-color);
  border-radius: var(--border-radius-md);
  text-align: center;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all var(--transition-normal);
  flex: 1;
  min-width: 80px;
  position: relative;
  overflow: hidden;
}

.option-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(160, 82, 45, 0.1), transparent);
  transition: left 0.5s ease;
}

.option-item:hover {
  border-color: var(--primary-light);
  transform: translateY(-3px);
  box-shadow: var(--shadow-sm);
}

.option-item:hover::before {
  left: 100%;
}

.option-item.selected {
  background: linear-gradient(135deg, var(--primary-light), var(--accent-cream));
  border-color: var(--primary-color);
  color: var(--primary-dark);
  font-weight: var(--font-weight-semibold);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
}

.option-label {
  display: block;
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.option-hint {
  display: block;
  font-size: var(--font-size-xs);
  color: var(--text-color-light);
  margin-top: var(--spacing-xs);
}

/* 加料网格 */
.toppings-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(150px, 1fr));
  gap: var(--spacing-sm);
}

.topping-item {
  padding: var(--spacing-md);
  background: var(--surface-color);
  border-radius: var(--border-radius-md);
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  border: 2px solid transparent;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.topping-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(160, 82, 45, 0.1), transparent);
  transition: left 0.5s ease;
}

.topping-item:hover {
  border-color: var(--primary-light);
  transform: translateY(-3px);
  box-shadow: var(--shadow-sm);
}

.topping-item:hover::before {
  left: 100%;
}

.topping-item.selected {
  background: linear-gradient(135deg, var(--primary-light), var(--accent-cream));
  border-color: var(--primary-color);
  color: var(--primary-dark);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
}

.topping-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.topping-icon {
  width: 36px;
  height: 36px;
  border-radius: var(--border-radius-sm);
  object-fit: cover;
  border: 2px solid var(--border-color);
  transition: all var(--transition-normal);
}

.topping-item.selected .topping-icon {
  border-color: var(--primary-color);
  transform: scale(1.05);
}

.topping-text {
  display: flex;
  flex-direction: column;
  gap: 2px;
}

.topping-name {
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-medium);
}

.topping-price {
  font-size: var(--font-size-xs);
  color: var(--primary-color);
  font-weight: var(--font-weight-semibold);
}

.topping-quantity {
  font-size: var(--font-size-base);
  color: var(--primary-color);
  font-weight: var(--font-weight-bold);
  animation: checkIn 0.3s ease-out;
}

@keyframes checkIn {
  from {
    opacity: 0;
    transform: scale(0) rotate(-180deg);
  }
  to {
    opacity: 1;
    transform: scale(1) rotate(0);
  }
}

/* 加料总价 */
.toppings-total {
  text-align: right;
  font-size: var(--font-size-base);
  color: var(--text-color-dark);
  padding-top: var(--spacing-md);
  border-top: 1px solid var(--border-color);
  margin-top: var(--spacing-md);
  font-weight: var(--font-weight-medium);
}

.toppings-price {
  color: var(--primary-color);
  font-weight: var(--font-weight-bold);
  font-size: var(--font-size-lg);
  margin-left: var(--spacing-xs);
}

/* ========== 商品详情标签页 ========== */
.product-detail-section {
  --delay: 0.5s;
}

.detail-tabs {
  display: flex;
  border-bottom: 1px solid var(--border-color);
  margin-bottom: var(--spacing-lg);
  position: relative;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: var(--spacing-md) 0;
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  cursor: pointer;
  transition: all var(--transition-normal);
  font-weight: var(--font-weight-medium);
  position: relative;
}

.tab-item:hover {
  color: var(--primary-color);
  transform: translateY(-2px);
}

.tab-item.active {
  color: var(--primary-color);
  font-weight: var(--font-weight-semibold);
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: var(--border-radius-sm);
  animation: slideIn 0.3s ease-out;
}

.tab-content {
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  line-height: var(--line-height-relaxed);
  animation: fadeIn 0.5s ease-out;
}

/* 原料成分 */
.ingredients {
  margin-top: var(--spacing-lg);
  padding: var(--spacing-md);
  background: var(--surface-color);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--accent-brown);
}

.ingredients-title {
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  display: block;
  margin-bottom: var(--spacing-sm);
}

.ingredients-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.ingredients-list span {
  background: white;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-xs);
  color: var(--text-color-medium);
  border: 1px solid var(--border-color);
  transition: all var(--transition-normal);
}

.ingredients-list span:hover {
  background: var(--primary-light);
  color: var(--primary-dark);
  border-color: var(--primary-color);
  transform: translateY(-1px);
}

/* 营养成分表 */
.nutrition-table {
  background: var(--surface-color);
  border-radius: var(--border-radius-md);
  overflow: hidden;
  border: 1px solid var(--border-color);
}

.nutrition-row {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  transition: all var(--transition-normal);
}

.nutrition-row:hover {
  background: rgba(160, 82, 45, 0.05);
}

.nutrition-row:last-child {
  border-bottom: none;
}

.nutrition-row.header {
  background: var(--primary-light);
  color: var(--primary-dark);
  font-weight: var(--font-weight-semibold);
  border-bottom: 2px solid var(--primary-color);
}

.nutrition-name {
  font-weight: var(--font-weight-medium);
  color: var(--text-color-dark);
}

.nutrition-value {
  color: var(--primary-color);
  font-weight: var(--font-weight-semibold);
}

/* 用户评价 */
.comment-item {
  padding: var(--spacing-lg) 0;
  border-bottom: 1px solid var(--border-color);
  transition: all var(--transition-normal);
}

.comment-item:hover {
  background: rgba(160, 82, 45, 0.03);
  padding-left: var(--spacing-sm);
  padding-right: var(--spacing-sm);
  border-radius: var(--border-radius-md);
}

.comment-header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
}

.user-avatar {
  width: 44px;
  height: 44px;
  border-radius: var(--border-radius-circle);
  border: 2px solid var(--border-color);
  transition: all var(--transition-normal);
}

.comment-item:hover .user-avatar {
  border-color: var(--primary-light);
  transform: scale(1.05);
}

.user-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.user-name {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
}

.comment-rating {
  display: flex;
  gap: 2px;
}

.comment-rating .iconfont {
  font-size: var(--font-size-sm);
  color: #FFD700;
}

.comment-time {
  font-size: var(--font-size-xs);
  color: var(--text-color-light);
  font-weight: var(--font-weight-normal);
}

.comment-content {
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  line-height: var(--line-height-relaxed);
  margin: 0;
}

/* ========== 底部操作栏 ========== */
.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 90px;
  background: white;
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-lg);
  box-shadow: var(--shadow-xl);
  z-index: 100;
  border-top: 1px solid var(--border-color);
}

.bottom-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  flex: 1;
}

.cart-icon {
  position: relative;
  cursor: pointer;
  transition: all var(--transition-normal);
  width: 48px;
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-circle);
  background: var(--surface-color);
  border: 2px solid transparent;
}

.cart-icon:hover {
  background: var(--primary-light);
  border-color: var(--primary-color);
  transform: scale(1.1);
}

.cart-icon-img {
  width: 28px;
  height: 28px;
  transition: transform var(--transition-normal);
}

.cart-icon:hover .cart-icon-img {
  transform: scale(1.1);
}

.cart-badge {
  position: absolute;
  top: -6px;
  right: -6px;
  background: linear-gradient(135deg, #FF6B6B, var(--primary-color));
  color: white;
  font-size: var(--font-size-xs);
  padding: 2px 6px;
  border-radius: var(--border-radius-circle);
  font-weight: var(--font-weight-bold);
  min-width: 20px;
  text-align: center;
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
  animation: bounce 0.5s ease-out;
}

.price-info {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
}

.total-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-medium);
  font-weight: var(--font-weight-medium);
}

.total-price {
  font-family: 'Prompt', sans-serif;
  font-size: 2rem;
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
  text-shadow: 0 2px 4px rgba(160, 82, 45, 0.1);
}

.bottom-right {
  flex: 1;
  display: flex;
  gap: var(--spacing-md);
}

.btn {
  flex: 1;
  border: none;
  border-radius: var(--border-radius-xl);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-bounce);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
  padding: var(--spacing-md) 0;
}

.btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.2);
}

.btn:hover::before {
  left: 100%;
}

.btn:active {
  transform: translateY(-1px);
}

.btn-secondary {
  background: white;
  color: var(--primary-color);
  border: 2px solid var(--primary-color);
}

.btn-secondary:hover {
  background: var(--surface-color);
}

.btn-primary {
  background: var(--gradient-primary);
  color: white;
  box-shadow: 0 4px 16px rgba(160, 82, 45, 0.2);
}

.btn-primary:hover {
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.3);
}

.sold-out-btn {
  flex: 1;
  background: var(--border-color);
  color: var(--text-color-light);
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-xl);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  padding: var(--spacing-md) 0;
  cursor: not-allowed;
}

/* ========== 响应式设计 ========== */
@media (max-width: 480px) {
  .product-detail-page {
    padding-bottom: 80px;
  }

  .product-images {
    height: 350px;
  }

  .product-name {
    font-size: var(--font-size-lg);
  }

  .current-price {
    font-size: 1.8rem;
  }

  .toppings-grid {
    grid-template-columns: 1fr;
  }

  .option-item {
    min-width: 70px;
  }

  .bottom-action-bar {
    height: 80px;
    padding: 0 var(--spacing-md);
  }

  .btn {
    font-size: var(--font-size-sm);
    padding: var(--spacing-sm) 0;
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  .product-images {
    height: 380px;
  }

  .toppings-grid {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (min-width: 769px) {
  .product-detail-page {
    max-width: 800px;
    margin: 0 auto;
  }

  .product-images {
    height: 450px;
    border-radius: var(--border-radius-lg);
    margin: var(--spacing-lg) var(--spacing-lg) 0;
  }

  .card {
    margin: var(--spacing-lg);
  }

  .toppings-grid {
    grid-template-columns: repeat(3, 1fr);
  }
}

/* 动画定义 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideIn {
  from {
    transform: scaleX(0);
  }
  to {
    transform: scaleX(1);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

/* 富文本内容样式 */
.rich-text {
  font-size: var(--font-size-base);
  line-height: var(--line-height-relaxed);
}

.rich-text :deep(img) {
  max-width: 100%;
  height: auto;
  border-radius: var(--border-radius-md);
  margin: var(--spacing-md) 0;
  box-shadow: var(--shadow-sm);
}

.rich-text :deep(p) {
  margin-bottom: var(--spacing-md);
  color: var(--text-color-medium);
}

.rich-text :deep(h2) {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  margin: var(--spacing-lg) 0 var(--spacing-md);
  padding-bottom: var(--spacing-xs);
  border-bottom: 2px solid var(--border-color);
}

.rich-text :deep(ul) {
  padding-left: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
}

.rich-text :deep(li) {
  margin-bottom: var(--spacing-xs);
  color: var(--text-color-medium);
  position: relative;
}

.rich-text :deep(li)::before {
  content: '•';
  color: var(--primary-color);
  position: absolute;
  left: -15px;
  font-size: var(--font-size-lg);
}
</style>