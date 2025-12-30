<template>
  <div class="product-detail-page">
    <!-- 商品图片区域 -->
    <div class="product-images">
      <div class="swiper-container">
        <img 
          v-for="(img, index) in product.images" 
          :key="index"
          :src="img" 
          class="product-image" 
          v-show="currentImageIndex === index"
        />
        <div class="image-indicator">
          {{ currentImageIndex + 1 }}/{{ product.images?.length || 0 }}
        </div>
      </div>
      
      <!-- 收藏按钮 -->
      <div 
        class="favorite-btn" 
        :class="{ active: isFavorite }" 
        @click="toggleFavorite"
      >
        <span class="favorite-icon">{{ isFavorite ? '❤️' : '🤍' }}</span>
      </div>
    </div>

    <!-- 商品基本信息 -->
    <div class="product-basic-info card">
      <div class="product-header">
        <h1 class="product-name">{{ product.name }}</h1>
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
        </div>
        
        <div class="rating-section">
          <div class="rating-stars">
            <span v-for="i in 5" :key="i" class="star">⭐</span>
          </div>
          <span class="rating-text">{{ product.rating }}分</span>
          <span class="sales-text">已售{{ product.sales }}件</span>
        </div>
      </div>
      
      <p class="product-desc">{{ product.description }}</p>
    </div>

    <!-- 定制化选项 -->
    <div class="customization-section card">
      <h3 class="section-title">⚙ 定制你的专属饮品</h3>
      
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
              <span class="topping-name">{{ topping.name }}</span>
              <span class="topping-price">+¥{{ topping.price }}</span>
            </div>
            <div class="topping-check" v-if="customizations.toppings.includes(topping.id)">✓</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商品详情标签页 -->
    <div class="product-detail-section card">
      <div class="detail-tabs">
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 0 }" 
          @click="activeTab = 0"
        >商品详情</div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 1 }" 
          @click="activeTab = 1"
        >营养成分</div>
        <div 
          class="tab-item" 
          :class="{ active: activeTab === 2 }" 
          @click="activeTab = 2"
        >用户评价</div>
      </div>
      
      <div class="tab-content" v-if="activeTab === 0">
        <div class="rich-text" v-html="product.detailHtml"></div>
      </div>
      
      <div class="tab-content nutrition" v-if="activeTab === 1">
        <div class="nutrition-table">
          <div class="nutrition-row" v-for="item in product.nutrition" :key="item.name">
            <span>{{ item.name }}</span>
            <span>{{ item.value }}{{ item.unit }}</span>
          </div>
        </div>
      </div>
      
      <div class="tab-content comments" v-if="activeTab === 2">
        <div class="comment-list">
          <div class="comment-item" v-for="comment in comments" :key="comment.id">
            <div class="comment-header">
              <img :src="comment.userAvatar" class="user-avatar" />
              <div class="user-info">
                <span class="user-name">{{ comment.userName }}</span>
                <div class="comment-rating">⭐ {{ comment.rating }}</div>
              </div>
              <span class="comment-time">{{ comment.createTime }}</span>
            </div>
            <p class="comment-content">{{ comment.content }}</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 底部操作栏 -->
    <div class="bottom-action-bar">
      <div class="bottom-left">
        <div class="cart-icon" @click="goToCart">
          <span class="icon">🛒</span>
          <div class="cart-badge" v-if="cartCount > 0">{{ cartCount }}</div>
        </div>
        <div class="price-info">
          <span class="total-label">合计：</span>
          <span class="total-price">¥{{ totalPrice }}</span>
        </div>
      </div>
      
      <div class="bottom-right">
        <button class="add-to-cart-btn" @click="addToCart">加入购物车</button>
        <button class="buy-now-btn" @click="buyNow">立即购买</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useCartStore } from '@/store/cart'
import { productApi, favoriteApi } from '@/utils/api'

const route = useRoute()
const router = useRouter()
const cartStore = useCartStore()

const productId = ref(route.params.id)
const product = ref({})
const currentImageIndex = ref(0)
const isFavorite = ref(false)
const activeTab = ref(0)
const loading = ref(true)

const customizations = ref({
  sweetness: 'normal',
  temperature: 'normal',
  toppings: [],
  quantity: 1
})

const sweetnessOptions = ref([])
const temperatureOptions = ref([])
const toppingOptions = ref([])

const maxToppings = 5
const comments = ref([])
const cartCount = computed(() => cartStore.totalCount)

const toppingsCost = computed(() => {
  return customizations.value.toppings.reduce((sum, id) => {
    const topping = toppingOptions.value.find(t => t.id === id)
    return sum + (topping ? topping.price : 0)
  }, 0)
})

const totalPrice = computed(() => {
  if (!product.value.price) return '0.00'
  return ((product.value.price + toppingsCost.value) * customizations.value.quantity).toFixed(2)
})

onMounted(() => {
  loadProductDetail()
})

const loadProductDetail = async () => {
  loading.value = true
  try {
    // 获取商品详情
    const detailRes = await productApi.getProductDetail(productId.value)
    if (detailRes.code === 200) {
      const res = detailRes.data
      product.value = {
        ...res,
        images: res.images || [res.imageUrl].filter(Boolean)
      }
    }

    // 检查收藏状态
    try {
      const favRes = await favoriteApi.checkFavorite(productId.value)
      if (favRes.code === 200) {
        isFavorite.value = favRes.data
      }
    } catch (e) {
      console.warn('获取收藏状态失败:', e)
    }
    
    // 获取规格和加料
    try {
      const customRes = await productApi.getProductCustomizations(productId.value)
      if (customRes.code === 200) {
        const { specs, options } = customRes.data
        
        // 映射甜度
        const sweetnessSpec = specs.find(s => s.type === 'SWEETNESS')
        if (sweetnessSpec && sweetnessSpec.items) {
          sweetnessOptions.value = sweetnessSpec.items.map(item => ({
            value: item.name,
            label: item.name,
            hint: item.description
          }))
          if (sweetnessOptions.value.length > 0) {
            customizations.value.sweetness = sweetnessOptions.value[0].value
          }
        }

        // 映射温度
        const tempSpec = specs.find(s => s.type === 'TEMPERATURE')
        if (tempSpec && tempSpec.items) {
          temperatureOptions.value = tempSpec.items.map(item => ({
            value: item.name,
            label: item.name
          }))
          if (temperatureOptions.value.length > 0) {
            customizations.value.temperature = temperatureOptions.value[0].value
          }
        }

        // 映射加料
        toppingOptions.value = options.filter(o => o.type === 'TOPPING').map(o => ({
          id: o.id,
          name: o.name,
          price: o.price
        }))
      }
    } catch (e) {
      console.warn('加载规格失败:', e)
    }

    // 尝试获取评价 (如果接口存在)
    try {
      if (productApi.getProductReviews) {
        const reviewsRes = await productApi.getProductReviews(productId.value)
        if (reviewsRes && reviewsRes.code === 200) {
          // 后端返回的是 List<OrderReview>，直接赋值
          comments.value = Array.isArray(reviewsRes.data) ? reviewsRes.data : (reviewsRes.data.list || [])
        }
      }
    } catch (e) {
      console.warn('加载商品评价失败:', e)
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleFavorite = async () => {
  try {
    if (isFavorite.value) {
      const res = await favoriteApi.removeFavorite(productId.value)
      if (res.code === 200 || res.status === 'success') {
        isFavorite.value = false
        alert('已取消收藏')
      }
    } else {
      const res = await favoriteApi.addFavorite(productId.value)
      if (res.code === 200 || res.status === 'success') {
        isFavorite.value = true
        alert('已收藏')
      }
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    alert('操作失败，请稍后重试')
  }
}

const toggleTopping = (topping) => {
  const index = customizations.value.toppings.indexOf(topping.id)
  if (index > -1) {
    customizations.value.toppings.splice(index, 1)
  } else {
    if (customizations.value.toppings.length >= maxToppings) {
      alert(`最多只能选择${maxToppings}种加料`)
      return
    }
    customizations.value.toppings.push(topping.id)
  }
}

const addToCart = () => {
  cartStore.addItem({
    id: product.value.id,
    name: product.value.name,
    image: product.value.images[0],
    price: product.value.price,
    quantity: customizations.value.quantity,
    customizations: { ...customizations.value }
  })
  alert('已加入购物车')
}

const buyNow = () => {
  const buyNowItem = {
    id: product.value.id,
    name: product.value.name,
    image: product.value.images[0],
    price: product.value.price,
    quantity: customizations.value.quantity,
    customizations: { ...customizations.value }
  }
  localStorage.setItem('buyNowItem', JSON.stringify(buyNowItem))
  router.push({ path: '/checkout', query: { type: 'buyNow' } })
}

const goToCart = () => {
  router.push('/cart')
}
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
  padding-bottom: 80px;
  position: relative;
  overflow-x: hidden;
}

/* 奶茶主题背景装饰 */
.product-detail-page::before {
  content: '';
  position: absolute;
  top: -10%;
  right: -5%;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, var(--accent-cream) 0%, transparent 70%);
  opacity: 0.15;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

.product-detail-page::after {
  content: '';
  position: absolute;
  bottom: 5%;
  left: -3%;
  width: 80px;
  height: 80px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.1;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

/* ========== 商品图片区域 ========== */
.product-images {
  position: relative;
  background: white;
  border-bottom-left-radius: var(--border-radius-xl);
  border-bottom-right-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-md);
  overflow: hidden;
}

.swiper-container {
  height: 400px;
  position: relative;
  background: var(--surface-color);
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.swiper-container:hover .product-image {
  transform: scale(1.02);
}

.image-indicator {
  position: absolute;
  bottom: var(--spacing-md);
  right: var(--spacing-md);
  background: rgba(0, 0, 0, 0.3);
  color: white;
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-lg);
  font-size: var(--font-size-xs);
  font-weight: var(--font-weight-medium);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.1);
  z-index: 2;
}

/* 收藏按钮 */
.favorite-btn {
  position: absolute;
  top: var(--spacing-md);
  right: var(--spacing-md);
  width: 44px;
  height: 44px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: var(--border-radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-md);
  cursor: pointer;
  transition: all var(--transition-normal);
  z-index: 2;
  border: 1px solid rgba(160, 82, 45, 0.1);
}

.favorite-btn:hover {
  transform: scale(1.1);
  box-shadow: var(--shadow-lg);
  background: white;
}

.favorite-btn.active {
  background: linear-gradient(135deg, #FF6B6B, var(--primary-color));
  border-color: transparent;
}

.favorite-btn.active .favorite-icon {
  color: white;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.favorite-icon {
  font-size: var(--font-size-lg);
  transition: all var(--transition-normal);
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
}

.card:hover {
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}

/* ========== 商品基本信息 ========== */
.product-header {
  margin-bottom: var(--spacing-md);
}

.product-name {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-sm);
  line-height: var(--line-height-tight);
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
  transform: translateY(-1px);
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
  background: linear-gradient(135deg, #E6F7FF, #E0F7FF);
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
  font-size: 2rem;
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

.star {
  font-size: var(--font-size-base);
  color: #FFD700;
  transition: transform var(--transition-normal);
}

.rating-stars:hover .star {
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
}

/* 商品描述 */
.product-desc {
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  line-height: var(--line-height-relaxed);
  margin: 0;
}

/* ========== 定制化选项 ========== */
.customization-section {
  animation: fadeInUp 0.6s ease-out 0.2s both;
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
  transform: translateY(-2px);
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
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
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
  transform: translateY(-2px);
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
  flex-direction: column;
  gap: var(--spacing-xs);
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

.topping-check {
  font-size: var(--font-size-base);
  color: var(--primary-color);
  font-weight: var(--font-weight-bold);
  animation: checkIn 0.3s ease-out;
}

@keyframes checkIn {
  from {
    opacity: 0;
    transform: scale(0);
  }
  to {
    opacity: 1;
    transform: scale(1);
  }
}

/* ========== 商品详情标签页 ========== */
.product-detail-section {
  animation: fadeInUp 0.6s ease-out 0.4s both;
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
  transform: translateY(-1px);
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

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
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

.nutrition-row span:first-child {
  font-weight: var(--font-weight-medium);
  color: var(--text-color-dark);
}

.nutrition-row span:last-child {
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
  width: 40px;
  height: 40px;
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
  font-size: var(--font-size-xs);
  color: #FFD700;
  font-weight: var(--font-weight-medium);
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
  height: 80px;
  background: white;
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-lg);
  box-shadow: var(--shadow-lg);
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
  font-size: 28px;
  cursor: pointer;
  transition: all var(--transition-normal);
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-circle);
  background: var(--surface-color);
}

.cart-icon:hover {
  background: var(--primary-light);
  transform: scale(1.1);
}

.cart-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: linear-gradient(135deg, #FF6B6B, var(--primary-color));
  color: white;
  font-size: var(--font-size-xs);
  padding: 2px 6px;
  border-radius: var(--border-radius-circle);
  font-weight: var(--font-weight-bold);
  min-width: 18px;
  text-align: center;
  box-shadow: 0 2px 6px rgba(255, 107, 107, 0.3);
  animation: bounce 0.5s ease-out;
}

@keyframes bounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.2);
  }
}

.price-info {
  display: flex;
  align-items: baseline;
  gap: var(--spacing-xs);
}

.total-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-medium);
  font-weight: var(--font-weight-medium);
}

.total-price {
  font-family: 'Prompt', sans-serif;
  font-size: 1.8rem;
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
  text-shadow: 0 2px 4px rgba(160, 82, 45, 0.1);
}

.bottom-right {
  display: flex;
  gap: var(--spacing-md);
}

.add-to-cart-btn, .buy-now-btn {
  padding: var(--spacing-md) var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  border: none;
  transition: all var(--transition-normal);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.add-to-cart-btn::before, .buy-now-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.add-to-cart-btn {
  background: white;
  color: var(--primary-color);
  border: 2px solid var(--primary-color);
}

.add-to-cart-btn:hover {
  background: var(--surface-color);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
}

.add-to-cart-btn:hover::before {
  left: 100%;
}

.buy-now-btn {
  background: var(--gradient-primary);
  color: white;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.buy-now-btn:hover {
  opacity: 0.95;
  transform: translateY(-2px);
  box-shadow: 0 6px 16px rgba(160, 82, 45, 0.3);
}

.buy-now-btn:hover::before {
  left: 100%;
}

/* ========== 响应式设计 ========== */
@media (max-width: 480px) {
  .product-detail-page {
    padding-bottom: 70px;
  }

  .swiper-container {
    height: 320px;
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
    height: 70px;
    padding: 0 var(--spacing-md);
  }

  .add-to-cart-btn, .buy-now-btn {
    padding: var(--spacing-sm) var(--spacing-lg);
    font-size: var(--font-size-sm);
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  .swiper-container {
    height: 360px;
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

  .swiper-container {
    height: 450px;
    border-radius: var(--border-radius-lg);
  }

  .product-images {
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

/* 加载动画 */
.loading {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  z-index: 10;
}

.loading .iconfont {
  font-size: var(--loading-size-lg);
  color: var(--primary-color);
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
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
}

.rich-text :deep(ul) {
  padding-left: var(--spacing-lg);
  margin-bottom: var(--spacing-md);
}

.rich-text :deep(li) {
  margin-bottom: var(--spacing-xs);
  color: var(--text-color-medium);
}
</style>