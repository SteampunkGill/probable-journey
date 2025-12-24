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
          用户评价({{ product.commentCount || 0 }})
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
import { productApi } from '../../utils/api.js'
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

// 加料选项暂时使用模拟数据，后续可从后端获取
const toppingOptions = ref([
  { id: 'pearl', name: '珍珠', price: 2.00, icon: 'https://images.unsplash.com/photo-1567095761054-7a02e69e5c43?w=50' },
  { id: 'coconut', name: '椰果', price: 2.00, icon: 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=50' }
])

const maxToppings = 5

const comments = ref([])

const toppingsCost = computed(() => {
  return customizations.value.toppings.reduce((total, toppingId) => {
    const topping = toppingOptions.value.find(t => t.id === toppingId)
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

const toggleFavorite = () => {
  isFavorite.value = !isFavorite.value
  alert(isFavorite.value ? '已收藏' : '已取消收藏')
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

const buyNow = () => {
  const orderItem = {
    ...product.value,
    customizations: { ...customizations.value },
    toppingsCost: toppingsCost.value,
    quantity: customizations.value.quantity
  }
  localStorage.setItem('checkoutItems', JSON.stringify([orderItem]))
  router.push('/order/checkout')
}

const fetchProductDetail = async (id) => {
  loading.value = true
  try {
    const res = await productApi.getProductDetail(id)
    // 假设后端返回的数据结构与前端匹配
    product.value = {
      id: res.id,
      name: res.name,
      images: res.images || [res.image].filter(Boolean),
      price: res.price,
      originalPrice: res.originalPrice || res.price,
      description: res.description,
      rating: res.rating || 0,
      sales: res.sales || 0,
      stock: res.stock || 0,
      detailHtml: res.detailHtml || '',
      ingredients: res.ingredients || [],
      nutrition: res.nutrition || [],
      commentCount: res.commentCount || 0,
      isHot: res.isHot || false,
      isNew: res.isNew || false,
      isRecommend: res.isRecommend || false
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
  }
})
</script>

<style scoped>
.product-detail-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 80px;
}

.product-images {
  position: relative;
  height: 375px;
  background: white;
}

.swiper-container {
  height: 100%;
  overflow: hidden;
  position: relative;
}

.swiper-wrapper {
  display: flex;
  height: 100%;
  transition: transform 0.3s ease-out;
}

.swiper-item {
  flex-shrink: 0;
  width: 100%;
  height: 100%;
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
  background: rgba(0,0,0,0.4);
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

.favorite-icon { width: 20px; height: 20px; }

.card {
  background: white;
  margin: 10px;
  padding: 15px;
  border-radius: 12px;
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 10px;
}

.product-name {
  font-size: 20px;
  font-weight: bold;
  color: #333;
}

.product-tags {
  display: flex;
  gap: 5px;
}

.tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
}

.tag-hot { background: #FFF0F0; color: #FF6B6B; }
.tag-new { background: #F0F9FF; color: #0095FF; }

.price-section {
  display: flex;
  align-items: baseline;
  gap: 8px;
  margin-bottom: 10px;
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
}

.rating-section {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 12px;
  color: #666;
}

.rating-stars { color: #FFB800; }

.product-desc {
  margin-top: 15px;
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

.item-label { font-size: 14px; font-weight: bold; }
.item-required { font-size: 11px; color: #FF6B6B; }

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
  min-width: 60px;
}

.option-item.selected {
  background: #FFF9F0;
  color: #D4A574;
  border: 1px solid #D4A574;
}

.option-label { font-size: 13px; display: block; }
.option-hint { font-size: 10px; opacity: 0.6; }

.toppings-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 10px;
}

.topping-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px;
  background: #F5F5F5;
  border-radius: 8px;
  cursor: pointer;
}

.topping-item.selected {
  background: #FFF9F0;
  border: 1px solid #D4A574;
}

.topping-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.topping-icon { width: 30px; height: 30px; border-radius: 4px; }
.topping-name { font-size: 13px; display: block; }
.topping-price { font-size: 11px; color: #999; }

.toppings-total {
  text-align: right;
  font-size: 13px;
  color: #666;
  padding-top: 10px;
  border-top: 1px solid #F5F5F5;
}

.detail-tabs {
  display: flex;
  border-bottom: 1px solid #F5F5F5;
  margin-bottom: 15px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 12px 0;
  font-size: 14px;
  color: #666;
  position: relative;
  cursor: pointer;
}

.tab-item.active {
  color: #D4A574;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 25%;
  width: 50%;
  height: 2px;
  background: #D4A574;
}

.tab-content {
  font-size: 14px;
  color: #666;
  line-height: 1.8;
}

.bottom-action-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  padding: 10px 15px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 100;
}

.bottom-left {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-right: 15px;
}

.cart-icon {
  position: relative;
  cursor: pointer;
}

.cart-icon-img { width: 28px; height: 28px; }

.cart-badge {
  position: absolute;
  top: -5px;
  right: -5px;
  background: #FF6B6B;
  color: white;
  font-size: 10px;
  padding: 0 4px;
  border-radius: 8px;
  min-width: 16px;
  text-align: center;
}

.price-info {
  display: flex;
  flex-direction: column;
}

.total-label { font-size: 11px; color: #999; }
.total-price { font-size: 18px; font-weight: bold; color: #D4A574; }

.bottom-right {
  flex: 1;
  display: flex;
  gap: 10px;
}

.btn {
  flex: 1;
  border: none;
  border-radius: 25px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
}

.btn-secondary { background: #FFF9F0; color: #D4A574; border: 1px solid #D4A574; }
.btn-primary { background: #D4A574; color: white; }

.sold-out-btn {
  flex: 1;
  background: #EEE;
  color: #999;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 25px;
  font-size: 14px;
}
</style>