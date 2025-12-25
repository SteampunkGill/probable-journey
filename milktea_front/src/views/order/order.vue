<template>
  <div class="order-page">
    <!-- 顶部搜索栏 -->
    <div class="search-bar">
      <div class="search-input">
        <img class="search-icon" src="../../assets/images/icons/search.png" />
        <input 
          type="text" 
          placeholder="搜索饮品..." 
          v-model="searchKeyword"
          @confirm="onSearch"
        />
        <img 
          class="clear-icon" 
          src="../../assets/images/icons/close.png" 
          v-if="searchKeyword"
          @click="searchKeyword = ''"
        />
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="content-wrapper">
      <!-- 左侧分类栏 -->
      <div class="category-sidebar">
        <div 
          class="category-item"
          :class="{ active: activeCategoryId === item.id }"
          v-for="item in categories"
          :key="item.id"
          @click="selectCategory(item.id)"
        >
          <span class="category-icon">{{ item.icon }}</span>
          <span class="category-name">{{ item.name }}</span>
          <div class="active-indicator" v-if="activeCategoryId === item.id"></div>
        </div>
      </div>

      <!-- 右侧商品列表 -->
      <div class="product-section" @scroll="onScroll">
        <!-- 骨架屏 -->
        <div class="skeleton-list" v-if="loading && productList.length === 0">
          <div class="skeleton-product" v-for="i in 5" :key="i"></div>
        </div>

        <div class="product-list" v-else>
          <div 
            class="product-card"
            v-for="item in filteredProductList"
            :key="item.id"
            @click="onProductTap(item)"
          >
            <!-- 左侧图片 -->
            <div class="product-image-wrapper">
              <img class="product-image" :src="item.image" />
              <div class="tag hot" v-if="item.hot">热销</div>
              <div class="tag discount" v-if="item.originalPrice">折扣</div>
              <!-- 收藏按钮 -->
              <div 
                class="favorite-btn"
                :class="{ active: item.isFavorite }"
                @click.stop="toggleFavorite(item)"
              >
                <img 
                  class="favorite-icon"
                  :src="item.isFavorite ? heartFillIcon : heartIcon"
                />
              </div>
            </div>

            <!-- 右侧信息 -->
            <div class="product-info">
              <span class="product-name">{{ item.name }}</span>
              <span class="product-desc">{{ item.description }}</span>
              
              <div class="product-footer">
                <div class="price-box">
                  <span class="price">¥{{ item.price }}</span>
                  <span class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</span>
                </div>
                <span class="sales">已售{{ item.sales }}</span>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty-tip" v-if="!loading && filteredProductList.length === 0">
          <img class="empty-icon" src="../../assets/images/icons/cart.png" />
          <span>暂无商品</span>
        </div>
      </div>
    </div>

    <!-- 购物车浮动按钮 -->
    <div class="cart-float" :class="{ show: cartCount > 0 }" @click="router.push('/cart')">
      <img class="cart-icon" src="../../assets/images/icons/cart.png" />
      <div class="cart-badge" :class="{ bounce: cartAnimating }">{{ cartCount }}</div>
    </div>

    <!-- 定制弹窗 -->
    <div class="custom-modal" v-if="showCustomModal">
      <div class="modal-mask" @click="showCustomModal = false"></div>
      <div class="modal-content">
        <!-- 商品信息 -->
        <div class="modal-header">
          <img class="modal-image" :src="selectedProduct.image" />
          <div class="modal-info">
            <span class="modal-name">{{ selectedProduct.name }}</span>
            <span class="modal-price">¥{{ totalPrice }}</span>
          </div>
          <div class="close-btn" @click="showCustomModal = false">
            <img class="close-icon" src="../../assets/images/icons/close.png" />
          </div>
        </div>

        <div class="modal-body">
          <!-- 杯型选择 -->
          <div class="option-group">
            <span class="option-title">杯型</span>
            <div class="option-list">
              <div 
                class="option-item"
                :class="{ active: customizations.size === item }"
                v-for="item in sizeOptions"
                :key="item"
                @click="customizations.size = item"
              >
                {{ item }}
              </div>
            </div>
          </div>

          <!-- 温度选择 -->
          <div class="option-group">
            <span class="option-title">温度</span>
            <div class="option-list">
              <div 
                class="option-item"
                :class="{ active: customizations.temperature === item }"
                v-for="item in temperatureOptions"
                :key="item"
                @click="customizations.temperature = item"
              >
                {{ item }}
              </div>
            </div>
          </div>

          <!-- 甜度选择 -->
          <div class="option-group">
            <span class="option-title">甜度</span>
            <div class="option-list">
              <div 
                class="option-item"
                :class="{ active: customizations.sweetness === item }"
                v-for="item in sweetnessOptions"
                :key="item"
                @click="customizations.sweetness = item"
              >
                {{ item }}
              </div>
            </div>
          </div>

          <!-- 加料选择 -->
          <div class="option-group">
            <span class="option-title">加料（可多选）</span>
            <div class="option-list">
              <div 
                class="option-item topping"
                :class="{ active: customizations.toppings.includes(item.id) }"
                v-for="item in toppingOptions"
                :key="item.id"
                @click="toggleTopping(item.id)"
              >
                <span>{{ item.name }}</span>
                <span class="topping-price">+¥{{ item.price }}</span>
              </div>
            </div>
          </div>

          <!-- 数量选择 -->
          <div class="option-group">
            <span class="option-title">数量</span>
            <div class="quantity-control">
              <div 
                class="quantity-btn" 
                :class="{ disabled: quantity <= 1 }" 
                @click="quantity > 1 && quantity--"
              >-</div>
              <span class="quantity-value">{{ quantity }}</span>
              <div class="quantity-btn" @click="quantity++">+</div>
            </div>
          </div>
        </div>

        <!-- 底部操作栏 -->
        <div class="modal-footer">
          <div class="total-price">
            <span class="total-label">合计</span>
            <span class="total-value">￥{{ totalPrice }}</span>
          </div>
          <button class="confirm-btn" @click="confirmAddToCart">
            加入购物车
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../store/cart'
import heartIcon from '../../assets/images/icons/heart.png'
import heartFillIcon from '../../assets/images/icons/heart-fill.png'

const router = useRouter()
const cartStore = useCartStore()

const categories = [
  { id: 1, name: '经典奶茶', icon: '🧋' },
  { id: 2, name: '果茶系列', icon: '🍹' },
  { id: 3, name: '咖啡系列', icon: '☕' },
  { id: 4, name: '芝士奶盖', icon: '🧀' },
  { id: 5, name: '冰沙系列', icon: '🍦' }
]

const activeCategoryId = ref(1)
const productList = ref([])
const searchKeyword = ref('')
const loading = ref(false)
const cartCount = computed(() => cartStore.totalCount)
const cartAnimating = ref(false)

const showCustomModal = ref(false)
const selectedProduct = ref(null)
const quantity = ref(1)
const customizations = ref({
  size: '中杯',
  temperature: '常温',
  sweetness: '正常糖',
  toppings: []
})

const sizeOptions = ['小杯', '中杯', '大杯']
const temperatureOptions = ['热饮', '常温', '冷饮']
const sweetnessOptions = ['无糖', '三分糖', '五分糖', '七分糖', '正常糖']
const toppingOptions = [
  { id: 1, name: '珍珠', price: 3 },
  { id: 2, name: '椰果', price: 3 },
  { id: 3, name: '布丁', price: 4 }
]

const filteredProductList = computed(() => {
  if (!searchKeyword.value) return productList.value
  return productList.value.filter(p => p.name.includes(searchKeyword.value))
})

const toppingsCost = computed(() => {
  return customizations.value.toppings.reduce((total, id) => {
    const topping = toppingOptions.find(t => t.id === id)
    return total + (topping ? topping.price : 0)
  }, 0)
})

const totalPrice = computed(() => {
  if (!selectedProduct.value) return 0
  return ((selectedProduct.value.price + toppingsCost.value) * quantity.value).toFixed(2)
})

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await productApi.getProducts(activeCategoryId.value)
    if (res.code === 200) {
      productList.value = res.data.list || res.data || []
    }
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

const selectCategory = (id) => {
  activeCategoryId.value = id
  loadProducts()
}

const onProductTap = (product) => {
  selectedProduct.value = product
  quantity.value = 1
  customizations.value = {
    size: '中杯',
    temperature: '常温',
    sweetness: '正常糖',
    toppings: []
  }
  showCustomModal.value = true
}

const toggleTopping = (id) => {
  const index = customizations.value.toppings.indexOf(id)
  if (index > -1) {
    customizations.value.toppings.splice(index, 1)
  } else {
    customizations.value.toppings.push(id)
  }
}

const toggleFavorite = (product) => {
  product.isFavorite = !product.isFavorite
}

const confirmAddToCart = () => {
  cartStore.addToCart({
    ...selectedProduct.value,
    quantity: quantity.value,
    customizations: { ...customizations.value },
    totalPrice: parseFloat(totalPrice.value)
  })
  showCustomModal.value = false
  cartAnimating.value = true
  setTimeout(() => cartAnimating.value = false, 600)
}

onMounted(() => {
  loadProducts()
})

watch(activeCategoryId, () => {
  loadProducts()
})
</script>

<style scoped>
.order-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #F5F5F5;
}

.search-bar {
  padding: 10px 15px;
  background: white;
}

.search-input {
  height: 36px;
  background: #F5F5F5;
  border-radius: 18px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  gap: 10px;
}

.search-icon { width: 16px; height: 16px; }
.search-input input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 14px;
}
.clear-icon { width: 16px; height: 16px; cursor: pointer; }

.content-wrapper {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.category-sidebar {
  width: 90px;
  background: #F8F8F8;
  overflow-y: auto;
}

.category-item {
  padding: 20px 10px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  position: relative;
  cursor: pointer;
}

.category-item.active {
  background: white;
  color: #D4A574;
  font-weight: bold;
}

.active-indicator {
  position: absolute;
  left: 0;
  top: 25%;
  height: 50%;
  width: 4px;
  background: #D4A574;
  border-radius: 0 2px 2px 0;
}

.category-icon { font-size: 20px; }
.category-name { font-size: 12px; }

.product-section {
  flex: 1;
  background: white;
  overflow-y: auto;
  padding: 10px;
}

.product-card {
  display: flex;
  gap: 12px;
  margin-bottom: 20px;
  cursor: pointer;
}

.product-image-wrapper {
  position: relative;
  width: 90px;
  height: 90px;
}

.product-image {
  width: 100%;
  height: 100%;
  border-radius: 8px;
  object-fit: cover;
}

.tag {
  position: absolute;
  top: 0;
  left: 0;
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 8px 0 8px 0;
  color: white;
}

.tag.hot { background: #FF6B6B; }
.tag.discount { background: #FFA940; }

.favorite-btn {
  position: absolute;
  bottom: 5px;
  right: 5px;
  width: 24px;
  height: 24px;
  background: rgba(255,255,255,0.8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.favorite-icon { width: 14px; height: 14px; }

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name { font-size: 15px; font-weight: bold; }
.product-desc { font-size: 12px; color: #999; line-height: 1.4; }

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price { font-size: 16px; font-weight: bold; color: #D4A574; }
.original-price { font-size: 11px; color: #CCC; text-decoration: line-through; margin-left: 5px; }
.sales { font-size: 11px; color: #999; }

.cart-float {
  position: fixed;
  right: 20px;
  bottom: 80px;
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

.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.modal-mask {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.6);
}

.modal-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-radius: 20px 20px 0 0;
  padding: 20px;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.modal-image { width: 80px; height: 80px; border-radius: 8px; }
.modal-info { flex: 1; display: flex; flex-direction: column; justify-content: center; }
.modal-name { font-size: 18px; font-weight: bold; }
.modal-price { font-size: 20px; color: #D4A574; font-weight: bold; margin-top: 5px; }
.close-btn { width: 24px; height: 24px; cursor: pointer; }
.close-icon { width: 100%; height: 100%; }

.modal-body {
  flex: 1;
  overflow-y: auto;
}

.option-group { margin-bottom: 20px; }
.option-title { font-size: 14px; font-weight: bold; margin-bottom: 10px; display: block; }
.option-list { display: flex; flex-wrap: wrap; gap: 10px; }
.option-item {
  padding: 6px 15px;
  background: #F5F5F5;
  border-radius: 4px;
  font-size: 13px;
  cursor: pointer;
}
.option-item.active { background: #FFF9F0; color: #D4A574; border: 1px solid #D4A574; }
.option-item.topping { display: flex; justify-content: space-between; min-width: 80px; }
.topping-price { font-size: 11px; color: #999; margin-left: 5px; }

.quantity-control { display: flex; align-items: center; gap: 20px; }
.quantity-btn {
  width: 28px;
  height: 28px;
  border: 1px solid #DDD;
  border-radius: 4px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}
.quantity-btn.disabled { color: #CCC; border-color: #EEE; }

.modal-footer {
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid #F5F5F5;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-label { font-size: 12px; color: #999; }
.total-value { font-size: 22px; font-weight: bold; color: #D4A574; margin-left: 5px; }
.confirm-btn {
  background: #D4A574;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 25px;
  font-weight: bold;
  cursor: pointer;
}
</style>