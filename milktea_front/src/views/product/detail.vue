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
import { productApi } from '@/utils/api'

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

const sweetnessOptions = [
  { value: 'no_sugar', label: '无糖', hint: '0%' },
  { value: 'low_sugar', label: '三分糖', hint: '30%' },
  { value: 'half_sugar', label: '五分糖', hint: '50%' },
  { value: 'less_sugar', label: '七分糖', hint: '70%' },
  { value: 'normal', label: '正常糖', hint: '100%' }
]

const temperatureOptions = [
  { value: 'hot', label: '热' },
  { value: 'warm', label: '温' },
  { value: 'no_ice', label: '去冰' },
  { value: 'less_ice', label: '少冰' },
  { value: 'normal', label: '正常冰' }
]

const toppingOptions = ref([])

const maxToppings = 5
const comments = ref([])
const cartCount = computed(() => cartStore.totalCount)

const toppingsCost = computed(() => {
  return customizations.value.toppings.reduce((sum, id) => {
    const topping = toppingOptions.find(t => t.id === id)
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
    const [detailRes, reviewsRes] = await Promise.all([
      productApi.getProductDetail(productId.value),
      productApi.getProductReviews(productId.value)
    ])
    
    if (detailRes.code === 200) {
      product.value = detailRes.data
    }
    
    if (reviewsRes.code === 200) {
      comments.value = reviewsRes.data.list || []
    }
  } catch (error) {
    console.error('加载商品详情失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  alert(isFavorite.value ? '已收藏' : '已取消收藏')
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
.product-detail-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 80px;
}

.product-images {
  position: relative;
  background: white;
}

.swiper-container {
  height: 375px;
  position: relative;
}

.product-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.image-indicator {
  position: absolute;
  bottom: 15px;
  right: 15px;
  background: rgba(0,0,0,0.3);
  color: white;
  padding: 2px 10px;
  border-radius: 10px;
  font-size: 12px;
}

.favorite-btn {
  position: absolute;
  top: 15px;
  right: 15px;
  width: 36px;
  height: 36px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  cursor: pointer;
}

.favorite-btn.active .favorite-icon {
  color: #FF4D4F;
}

.card {
  background: white;
  margin: 12px;
  padding: 15px;
  border-radius: 12px;
}

.product-name {
  font-size: 20px;
  font-weight: bold;
  margin-bottom: 10px;
}

.product-tags {
  display: flex;
  gap: 8px;
  margin-bottom: 15px;
}

.tag {
  font-size: 11px;
  padding: 2px 8px;
  border-radius: 4px;
}

.tag-hot { background: #FFF1F0; color: #FF4D4F; }
.tag-new { background: #F6FFED; color: #52C41A; }
.tag-recommend { background: #E6F7FF; color: #1890FF; }

.product-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.current-price {
  font-size: 24px;
  font-weight: bold;
  color: #D4A574;
}

.original-price {
  font-size: 14px;
  color: #999;
  text-decoration: line-through;
  margin-left: 8px;
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 12px;
  color: #999;
}

.product-desc {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.section-title {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 15px;
}

.customization-item {
  margin-bottom: 20px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.item-label {
  font-size: 14px;
  font-weight: bold;
}

.item-required {
  font-size: 11px;
  color: #FF4D4F;
  background: #FFF1F0;
  padding: 1px 5px;
  border-radius: 2px;
}

.option-group {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.option-item {
  padding: 8px 15px;
  background: #F5F5F5;
  border-radius: 6px;
  text-align: center;
  cursor: pointer;
  border: 1px solid transparent;
}

.option-item.selected {
  background: #FDF8F3;
  border-color: #D4A574;
  color: #D4A574;
}

.option-label {
  display: block;
  font-size: 13px;
}

.option-hint {
  display: block;
  font-size: 10px;
  opacity: 0.6;
}

.toppings-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 10px;
}

.topping-item {
  padding: 10px;
  background: #F5F5F5;
  border-radius: 8px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  border: 1px solid transparent;
}

.topping-item.selected {
  background: #FDF8F3;
  border-color: #D4A574;
  color: #D4A574;
}

.topping-name {
  font-size: 13px;
  display: block;
}

.topping-price {
  font-size: 11px;
  opacity: 0.6;
}

.detail-tabs {
  display: flex;
  border-bottom: 1px solid #F5F5F5;
  margin-bottom: 15px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 10px 0;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.tab-item.active {
  color: #D4A574;
  font-weight: bold;
  border-bottom: 2px solid #D4A574;
}

.tab-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
}

.nutrition-row {
  display: flex;
  justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid #F9F9F9;
}

.comment-item {
  padding: 15px 0;
  border-bottom: 1px solid #F9F9F9;
}

.comment-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
}

.user-info {
  flex: 1;
}

.user-name {
  font-size: 13px;
  font-weight: bold;
}

.comment-rating {
  font-size: 11px;
  color: #FFD700;
}

.comment-time {
  font-size: 11px;
  color: #CCC;
}

.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 70px;
  background: white;
  display: flex;
  align-items: center;
  padding: 0 15px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 100;
}

.bottom-left {
  display: flex;
  align-items: center;
  gap: 15px;
  flex: 1;
}

.cart-icon {
  position: relative;
  font-size: 24px;
  cursor: pointer;
}

.cart-badge {
  position: absolute;
  top: -5px;
  right: -8px;
  background: #FF4D4F;
  color: white;
  font-size: 10px;
  padding: 2px 5px;
  border-radius: 10px;
}

.total-price {
  font-size: 20px;
  font-weight: bold;
  color: #D4A574;
}

.bottom-right {
  display: flex;
  gap: 10px;
}

.add-to-cart-btn, .buy-now-btn {
  padding: 10px 20px;
  border-radius: 22px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  border: none;
}

.add-to-cart-btn {
  background: #FDF8F3;
  color: #D4A574;
  border: 1px solid #D4A574;
}

.buy-now-btn {
  background: #D4A574;
  color: white;
}
</style>