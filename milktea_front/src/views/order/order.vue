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
        <!-- 筛选栏 -->
        <div class="filter-bar">
          <div class="sort-options">
            <div
              class="sort-item"
              :class="{ active: filterConfig.sortBy === 'default' }"
              @click="filterConfig.sortBy = 'default'"
            >综合</div>
            <div
              class="sort-item"
              :class="{ active: filterConfig.sortBy === 'sales' }"
              @click="filterConfig.sortBy = 'sales'"
            >销量</div>
            <div
              class="sort-item"
              :class="{ active: filterConfig.sortBy === 'price' }"
              @click="togglePriceSort"
            >
              价格
              <div class="price-arrows">
                <i class="arrow-up" :class="{ active: filterConfig.sortBy === 'price' && filterConfig.sortOrder === 'asc' }"></i>
                <i class="arrow-down" :class="{ active: filterConfig.sortBy === 'price' && filterConfig.sortOrder === 'desc' }"></i>
              </div>
            </div>
          </div>
          <div class="filter-dropdown" @click="showFilterPanel = !showFilterPanel">
            <span :class="{ active: filterConfig.sugarLevel !== 'all' }">
              {{ sugarLevelLabel }}
            </span>
            <i class="iconfont icon-filter"></i>
          </div>
          
          <!-- 糖度筛选面板 -->
          <div class="filter-panel" v-if="showFilterPanel">
            <div class="panel-mask" @click="showFilterPanel = false"></div>
            <div class="panel-content">
              <div class="panel-title">糖度筛选</div>
              <div class="filter-options">
                <div
                  class="filter-opt-item"
                  :class="{ active: filterConfig.sugarLevel === 'all' }"
                  @click="selectSugar('all')"
                >全部</div>
                <div
                  class="filter-opt-item"
                  :class="{ active: filterConfig.sugarLevel === 'no' }"
                  @click="selectSugar('no')"
                >无糖</div>
                <div
                  class="filter-opt-item"
                  :class="{ active: filterConfig.sugarLevel === 'low' }"
                  @click="selectSugar('low')"
                >低糖</div>
                <div
                  class="filter-opt-item"
                  :class="{ active: filterConfig.sugarLevel === 'normal' }"
                  @click="selectSugar('normal')"
                >正常糖</div>
              </div>
              <div class="panel-footer">
                <button class="reset-btn" @click="resetFilter">重置</button>
                <button class="confirm-btn" @click="showFilterPanel = false">确定</button>
              </div>
            </div>
          </div>
        </div>

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
              <div class="name-row">
                <span class="product-name">{{ item.name }}</span>
                <span class="promo-badge" v-if="item.promoTag">{{ item.promoTag }}</span>
              </div>
              <span class="product-desc">{{ item.description }}</span>
              
              <div class="product-footer">
                <div class="price-box">
                  <span class="price">¥{{ item.price }}</span>
                  <span class="original-price" v-if="item.originalPrice">¥{{ item.originalPrice }}</span>
                </div>
                <div class="action-box">
                  <span class="sales">已售{{ item.sales }}</span>
                  <div class="add-cart-btn" @click.stop="onAddCartClick(item)">
                    <span class="plus-icon">+</span>
                  </div>
                </div>
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

    <!-- 底部购物车栏 -->
    <div class="cart-bar" v-if="cartCount > 0">
      <div class="cart-bar-content" @click="router.push('/cart')">
        <div class="cart-icon-wrapper">
          <img class="cart-icon" src="../../assets/images/icons/cart.png" />
          <div class="cart-badge" :class="{ bounce: cartAnimating }">{{ cartCount }}</div>
        </div>
        <div class="cart-info">
          <div class="total-price">¥{{ cartStore.totalPrice.toFixed(2) }}</div>
          <div class="delivery-info">另需配送费约 ¥5</div>
        </div>
        <button class="checkout-btn" @click.stop="router.push('/cart')">去结算</button>
      </div>
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
import { useUserStore } from '../../store/user'
import { productApi, promotionApi } from '@/utils/api'
import { formatImageUrl } from '@/utils/util'
import heartIcon from '../../assets/images/icons/heart.png'
import heartFillIcon from '../../assets/images/icons/heart-fill.png'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const categories = ref([])
const activeCategoryId = ref(null)
const productList = ref([])
const promoFilterIds = ref(null)
const promoName = ref('')
const searchKeyword = ref('')
const loading = ref(false)
const cartCount = computed(() => cartStore.totalCount)
const cartAnimating = ref(false)

// 筛选相关状态
const showFilterPanel = ref(false)
const filterConfig = ref({
  sortBy: 'default', // default, sales, price
  sortOrder: 'desc', // asc, desc
  sugarLevel: 'all' // all, no, low, normal
})

const sugarLevelLabel = computed(() => {
  const map = {
    'all': '糖度',
    'no': '无糖',
    'low': '低糖',
    'normal': '正常糖'
  }
  return map[filterConfig.value.sugarLevel]
})

const togglePriceSort = () => {
  if (filterConfig.value.sortBy !== 'price') {
    filterConfig.value.sortBy = 'price'
    filterConfig.value.sortOrder = 'asc'
  } else {
    filterConfig.value.sortOrder = filterConfig.value.sortOrder === 'asc' ? 'desc' : 'asc'
  }
}

const selectSugar = (level) => {
  filterConfig.value.sugarLevel = level
}

const resetFilter = () => {
  filterConfig.value.sugarLevel = 'all'
  filterConfig.value.sortBy = 'default'
}

const showCustomModal = ref(false)
const selectedProduct = ref(null)
const quantity = ref(1)
const customizations = ref({
  size: '中杯',
  temperature: '常温',
  sweetness: '正常糖',
  toppings: []
})

const sizeOptions = ref(['小杯', '中杯', '大杯'])
const temperatureOptions = ref(['热饮', '常温', '冷饮'])
const sweetnessOptions = ref(['无糖', '三分糖', '五分糖', '七分糖', '正常糖'])
const toppingOptions = ref([
  { id: 1, name: '珍珠', price: 3 },
  { id: 2, name: '椰果', price: 3 },
  { id: 3, name: '布丁', price: 4 }
])

const filteredProductList = computed(() => {
  let list = [...productList.value]

  // 1. 搜索关键词过滤
  if (searchKeyword.value) {
    list = list.filter(p =>
      (p.name && p.name.includes(searchKeyword.value)) ||
      (p.description && p.description.includes(searchKeyword.value))
    )
  }

  // 2. 糖度筛选 (纯前端模拟逻辑)
  if (filterConfig.value.sugarLevel !== 'all') {
    list = list.filter(p => p.sugarLevel === filterConfig.value.sugarLevel)
  }

  // 3. DEMO ONLY: 促销活动筛选逻辑 (在分类加载后执行)
  const filterIdsStr = sessionStorage.getItem('promo_filter_ids')
  if (filterIdsStr) {
    const filterIds = JSON.parse(filterIdsStr)
    // 仅保留活动指定的商品
    const activeProducts = list.filter(p => filterIds.includes(p.id))
    if (activeProducts.length > 0) {
      // 模拟“装模作样”筛选：随机选 2-3 个
      const count = Math.min(activeProducts.length, Math.floor(Math.random() * 2) + 2)
      list = activeProducts.sort(() => 0.5 - Math.random()).slice(0, count)
      console.log(`[DEMO] 促销筛选生效: 展示 ${count} 款活动商品`)
    }
    // 注意：这里不立即清除，因为 computed 会多次触发，我们在 onUnmounted 或跳转时清除更稳妥
    // 或者在 loadProducts 结束时清除
  }

  // 4. 排序逻辑
  list.sort((a, b) => {
    if (filterConfig.value.sortBy === 'sales') {
      return b.sales - a.sales
    } else if (filterConfig.value.sortBy === 'price') {
      return filterConfig.value.sortOrder === 'asc'
        ? a.price - b.price
        : b.price - a.price
    }
    return 0 // 默认排序（综合）
  })

  return list
})

const toppingsCost = computed(() => {
  const toppings = customizations.value.toppings || []
  return toppings.reduce((total, id) => {
    const topping = toppingOptions.value.find(t => t.id === id)
    return total + (topping ? topping.price : 0)
  }, 0)
})

const totalPrice = computed(() => {
  if (!selectedProduct.value) return 0
  return ((selectedProduct.value.price + toppingsCost.value) * quantity.value).toFixed(2)
})

const loadCategories = async () => {
  try {
    const res = await productApi.getCategories()
    const resData = res.data || res
    const list = Array.isArray(resData) ? resData : (resData.list || [])
    
    if (list.length > 0) {
      const formattedCategories = list.map(c => ({
        id: c.id,
        name: c.name,
        icon: c.iconUrl && !c.iconUrl.includes('/') ? c.iconUrl : '🧋'
      }))
      
      // 添加“全部”分类
      categories.value = [
        { id: 'all', name: '全部', icon: '🌟' },
        ...formattedCategories
      ]
      
      if (!activeCategoryId.value) {
        activeCategoryId.value = 'all'
      }
    }
  } catch (error) {
    console.error('加载分类失败:', error)
  }
}

const loadProducts = async () => {
  if (!activeCategoryId.value) return
  loading.value = true
  try {
    // 如果是“全部”分类，传 null 或不传 ID
    const categoryId = activeCategoryId.value === 'all' ? null : activeCategoryId.value
    const res = await productApi.getProducts(categoryId)
    const resData = res.data || res
    let rawList = Array.isArray(resData) ? resData : (resData.list || [])
    
    // DEMO ONLY: 注入 IndexedDB 中的模拟商品
    try {
      const { productDB } = await import('@/utils/db')
      let dbProducts = await productDB.getAll()
      if (dbProducts && dbProducts.length > 0) {
        // 仅在“全部”分类或匹配分类时注入，避免在所有分类下都显示
        if (activeCategoryId.value !== 'all') {
          dbProducts = dbProducts.filter(p => p.categoryId === activeCategoryId.value)
        }
        if (dbProducts.length > 0) {
          rawList = [...dbProducts, ...rawList]
          console.log('[DEMO] 已从 IndexedDB 注入模拟商品:', dbProducts.length)
        }
      }
    } catch (e) {
      console.warn('注入模拟商品失败:', e)
    }


    productList.value = rawList.map(item => {
      // 统一使用 formatImageUrl 处理图片路径，并兼容多种可能的字段名
      const imageUrl = item.image || item.productImage || item.product?.mainImageUrl || item.product?.imageUrl || item.mainImageUrl || item.imageUrl
      return {
        ...item,
        image: formatImageUrl(imageUrl) || 'https://images.unsplash.com/photo-1544787210-2827443cb39b?w=200',
        hot: item.isHot || false,
        sales: item.sales || 0,
        sugarLevel: 'normal' // 默认糖度
      }
    })
  } catch (error) {
    console.error('加载商品失败:', error)
  } finally {
    loading.value = false
  }
}

const selectCategory = (id) => {
  activeCategoryId.value = id
  // 切换分类时清除促销筛选，确保分类功能正常
  sessionStorage.removeItem('promo_filter_ids')
  loadProducts()
}

const onProductTap = (product) => {
  // 将奶茶完整对象存入 localStorage
  localStorage.setItem('current_tea', JSON.stringify(product))
  // 跳转到详情页
  router.push(`/product/${product.id}`)
}

const onAddCartClick = (product) => {
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
  if (!customizations.value.toppings) {
    customizations.value.toppings = []
  }
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
    storeId: userStore.selectedStore?.id,
    quantity: quantity.value,
    customizations: { ...customizations.value },
    totalPrice: parseFloat(totalPrice.value)
  })
  showCustomModal.value = false
  cartAnimating.value = true
  setTimeout(() => cartAnimating.value = false, 600)
}

onMounted(async () => {
  await loadCategories()
  if (activeCategoryId.value) {
    loadProducts()
  }
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
  background: var(--background-color);
  font-family: 'Noto Sans KR', sans-serif;
}

.search-bar {
  padding: 20px 24px;
  background: var(--surface-color);
  border-bottom: 1px solid var(--border-color);
  box-shadow: 0 2px 10px rgba(139, 69, 19, 0.05);
}

.search-input {
  height: 52px;
  background: white;
  border-radius: 26px;
  display: flex;
  align-items: center;
  padding: 0 20px;
  gap: 12px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.1);
}

.search-input:focus-within {
  border-color: var(--primary-light);
  box-shadow: 0 4px 20px rgba(160, 82, 45, 0.15);
  transform: translateY(-2px);
}

.search-icon {
  width: 20px;
  height: 20px;
  opacity: 0.6;
}

.search-input input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 16px;
  color: var(--text-color-dark);
  font-family: 'Noto Sans KR', sans-serif;
  outline: none;
}

.search-input input::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

.clear-icon {
  width: 18px;
  height: 18px;
  cursor: pointer;
  opacity: 0.5;
  transition: opacity 0.3s ease;
}

.clear-icon:hover {
  opacity: 0.8;
}

.content-wrapper {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.category-sidebar {
  width: 100px;
  background: var(--surface-color);
  overflow-y: auto;
  border-right: 1px solid var(--border-color);
  padding: 20px 0;
}

.category-item {
  padding: 24px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease;
  margin: 0 8px;
  border-radius: 20px;
}

.category-item:hover {
  background: rgba(160, 82, 45, 0.05);
  transform: translateX(3px);
}

.category-item.active {
  background: white;
  color: var(--primary-dark);
  font-weight: 700;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
  border: 2px solid var(--primary-light);
}

.active-indicator {
  position: absolute;
  left: -8px;
  top: 50%;
  transform: translateY(-50%);
  height: 40px;
  width: 6px;
  background: linear-gradient(180deg, var(--primary-color) 0%, var(--primary-light) 100%);
  border-radius: 0 3px 3px 0;
  box-shadow: 0 2px 8px rgba(160, 82, 45, 0.3);
}

.category-icon {
  font-size: 28px;
  margin-bottom: 4px;
}

.category-name {
  font-size: 13px;
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
  text-align: center;
  line-height: 1.3;
}

.product-section {
  flex: 1;
  background: white;
  overflow-y: auto;
  padding: 0 24px;
  display: flex;
  flex-direction: column;
}

.filter-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 0;
  background: white;
  border-bottom: 2px dashed var(--border-color);
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(10px);
  background: rgba(255, 255, 255, 0.95);
}

.sort-options {
  display: flex;
  gap: 24px;
}

.sort-item {
  font-size: 14px;
  color: var(--text-color-medium);
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  font-weight: 500;
  padding: 6px 12px;
  border-radius: 15px;
  transition: all 0.3s ease;
}

.sort-item:hover {
  background: rgba(160, 82, 45, 0.05);
  color: var(--primary-color);
}

.sort-item.active {
  color: var(--primary-dark);
  font-weight: 700;
  background: linear-gradient(135deg, var(--accent-cream) 0%, rgba(255, 248, 220, 0.3) 100%);
  box-shadow: 0 2px 8px rgba(210, 180, 140, 0.2);
}

.price-arrows {
  display: flex;
  flex-direction: column;
  gap: 2px;
  margin-left: 4px;
}

.arrow-up, .arrow-down {
  width: 0;
  height: 0;
  border-left: 4px solid transparent;
  border-right: 4px solid transparent;
  transition: all 0.3s ease;
}

.arrow-up {
  border-bottom: 5px solid var(--border-color);
}

.arrow-up.active {
  border-bottom-color: var(--primary-color);
}

.arrow-down {
  border-top: 5px solid var(--border-color);
}

.arrow-down.active {
  border-top-color: var(--primary-color);
}

.filter-dropdown {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
  color: var(--text-color-medium);
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  background: var(--accent-cream);
  border: 2px solid var(--primary-light);
  transition: all 0.3s ease;
}

.filter-dropdown:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.3);
}

.filter-dropdown span.active {
  color: var(--primary-dark);
  font-weight: 700;
}

.filter-panel {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
}

.panel-mask {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.4);
  backdrop-filter: blur(4px);
}

.panel-content {
  position: absolute;
  top: 0;
  right: 0;
  width: 80%;
  height: 100%;
  background: var(--surface-color);
  padding: 30px;
  display: flex;
  flex-direction: column;
  border-radius: 30px 0 0 30px;
  box-shadow: -4px 0 20px rgba(139, 69, 19, 0.1);
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from { transform: translateX(100%); }
  to { transform: translateX(0); }
}

.panel-title {
  font-size: 20px;
  font-weight: 700;
  margin-bottom: 30px;
  padding-top: 40px;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  border-left: 4px solid var(--primary-color);
  padding-left: 16px;
}

.filter-options {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 12px;
  flex: 1;
}

.filter-opt-item {
  padding: 16px 12px;
  background: white;
  border-radius: 16px;
  text-align: center;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  box-shadow: 0 2px 8px rgba(210, 180, 140, 0.1);
}

.filter-opt-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.15);
}

.filter-opt-item.active {
  background: linear-gradient(135deg, var(--accent-cream) 0%, white 100%);
  color: var(--primary-dark);
  border: 2px solid var(--primary-light);
  font-weight: 700;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.2);
}

.panel-footer {
  display: flex;
  gap: 16px;
  padding: 20px 0;
  border-top: 2px dashed var(--border-color);
  margin-top: 20px;
}

.reset-btn, .confirm-btn {
  flex: 1;
  padding: 14px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  border: none;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-family: 'Prompt', sans-serif;
}

.reset-btn {
  background: white;
  color: var(--text-color-dark);
  border: 2px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
}

.reset-btn:hover {
  transform: translateY(-3px);
  border-color: var(--primary-light);
  color: var(--primary-dark);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.15);
}

.confirm-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
}

.confirm-btn:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

.skeleton-list {
  padding: 20px 0;
}

.skeleton-product {
  height: 120px;
  background: linear-gradient(90deg, #f0f0f0 25%, #e0e0e0 50%, #f0f0f0 75%);
  border-radius: 20px;
  margin-bottom: 20px;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { background-position: -200px 0; }
  100% { background-position: 200px 0; }
}

.product-list {
  padding: 20px 0;
}

.product-card {
  display: flex;
  gap: 20px;
  margin-bottom: 24px;
  cursor: pointer;
  padding: 20px;
  border-radius: 25px;
  background: var(--surface-color);
  border: 2px solid transparent;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.1);
}

.product-card:hover {
  transform: translateY(-5px);
  border-color: var(--primary-light);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.15);
}

.product-image-wrapper {
  position: relative;
  width: 120px;
  height: 120px;
  flex-shrink: 0;
}

.product-image {
  width: 100%;
  height: 100%;
  border-radius: 20px;
  object-fit: cover;
  border: 3px solid var(--accent-cream);
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.2);
}

.tag {
  position: absolute;
  top: 8px;
  left: 8px;
  font-size: 11px;
  padding: 4px 10px;
  border-radius: 12px;
  color: white;
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
  box-shadow: 0 2px 6px rgba(0,0,0,0.1);
}

.tag.hot {
  background: linear-gradient(135deg, #ff6b6b 0%, #ff5252 100%);
}

.tag.discount {
  background: linear-gradient(135deg, #ffa940 0%, #ff8c00 100%);
}

.favorite-btn {
  position: absolute;
  bottom: 8px;
  right: 8px;
  width: 32px;
  height: 32px;
  background: rgba(255,255,255,0.9);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid var(--border-color);
}

.favorite-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.favorite-btn.active {
  background: rgba(255, 192, 203, 0.2);
  border-color: var(--accent-pink);
}

.favorite-icon {
  width: 18px;
  height: 18px;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.name-row {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 8px;
}

.product-name {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-color-dark);
  font-family: 'Prompt', sans-serif;
  line-height: 1.3;
}

.promo-badge {
  font-size: 10px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff5252 100%);
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
  font-weight: 600;
  white-space: nowrap;
}

.product-desc {
  font-size: 13px;
  color: var(--text-color-medium);
  line-height: 1.5;
  margin-bottom: 12px;
  flex: 1;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.action-box {
  display: flex;
  align-items: center;
  gap: 12px;
}

.add-cart-btn {
  width: 32px;
  height: 32px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 4px 10px rgba(160, 82, 45, 0.2);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.add-cart-btn:hover {
  transform: scale(1.15) rotate(90deg);
  box-shadow: 0 6px 15px rgba(160, 82, 45, 0.3);
}

.plus-icon {
  color: white;
  font-size: 20px;
  font-weight: bold;
  line-height: 1;
  margin-top: -2px;
}

.price-box {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
}

.original-price {
  font-size: 13px;
  color: var(--text-color-light);
  text-decoration: line-through;
}

.sales {
  font-size: 12px;
  color: var(--text-color-medium);
  font-weight: 500;
  background: var(--accent-cream);
  padding: 4px 12px;
  border-radius: 12px;
}

.empty-tip {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  color: var(--text-color-medium);
}

.empty-icon {
  width: 80px;
  height: 80px;
  opacity: 0.3;
  margin-bottom: 20px;
}

.empty-tip span {
  font-size: 16px;
  font-weight: 500;
}

.cart-bar {
  position: fixed;
  bottom: 80px;
  left: 0;
  right: 0;
  background: var(--surface-color);
  padding: 16px 24px;
  border-top: 1px solid var(--border-color);
  box-shadow: 0 -4px 20px rgba(139, 69, 19, 0.1);
  z-index: 100;
  border-radius: 30px 30px 0 0;
}

.cart-bar-content {
  display: flex;
  align-items: center;
  gap: 20px;
  background: white;
  padding: 16px 24px;
  border-radius: 25px;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.1);
  border: 2px solid var(--accent-cream);
  cursor: pointer;
  transition: all 0.3s ease;
}

.cart-bar-content:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.15);
}

.cart-icon-wrapper {
  position: relative;
}

.cart-icon {
  width: 32px;
  height: 32px;
}

.cart-badge {
  position: absolute;
  top: -8px;
  right: -8px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff5252 100%);
  color: white;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  min-width: 20px;
  text-align: center;
  font-weight: 700;
  border: 2px solid white;
  font-family: 'Prompt', sans-serif;
}

.cart-badge.bounce {
  animation: bounce 0.6s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

@keyframes bounce {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.3); }
}

.cart-info {
  flex: 1;
}

.total-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  margin-bottom: 4px;
}

.delivery-info {
  font-size: 12px;
  color: var(--text-color-medium);
}

.checkout-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  padding: 12px 32px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-family: 'Prompt', sans-serif;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
}

.checkout-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

.custom-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000;
}

.modal-mask {
  position: absolute;
  width: 100%;
  height: 100%;
  background: rgba(0,0,0,0.6);
  backdrop-filter: blur(4px);
}

.modal-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: var(--surface-color);
  border-radius: 40px 40px 0 0;
  padding: 30px;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 -8px 30px rgba(139, 69, 19, 0.15);
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.modal-header {
  display: flex;
  gap: 20px;
  margin-bottom: 30px;
  padding-bottom: 20px;
  border-bottom: 2px dashed var(--border-color);
}

.modal-image {
  width: 100px;
  height: 100px;
  border-radius: 20px;
  object-fit: cover;
  border: 3px solid var(--accent-cream);
  box-shadow: 0 4px 12px rgba(210, 180, 140, 0.3);
}

.modal-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.modal-name {
  font-size: 20px;
  font-weight: 700;
  color: var(--text-color-dark);
  font-family: 'Prompt', sans-serif;
  margin-bottom: 8px;
}

.modal-price {
  font-size: 24px;
  color: var(--primary-dark);
  font-weight: 700;
  font-family: 'Prompt', sans-serif;
}

.close-btn {
  width: 32px;
  height: 32px;
  cursor: pointer;
  background: var(--accent-cream);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 2px solid var(--border-color);
  transition: all 0.3s ease;
}

.close-btn:hover {
  transform: rotate(90deg);
  background: white;
  border-color: var(--primary-light);
}

.close-icon {
  width: 16px;
  height: 16px;
  opacity: 0.6;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding-right: 10px;
}

.option-group {
  margin-bottom: 24px;
}

.option-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  margin-bottom: 16px;
  display: block;
}

.option-list {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
}

.option-item {
  padding: 10px 20px;
  background: white;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
  border: 2px solid transparent;
  box-shadow: 0 2px 8px rgba(210, 180, 140, 0.1);
}

.option-item:hover {
  transform: translateY(-3px);
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.15);
}

.option-item.active {
  background: linear-gradient(135deg, var(--accent-cream) 0%, white 100%);
  color: var(--primary-dark);
  border: 2px solid var(--primary-light);
  font-weight: 700;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.2);
}

.option-item.topping {
  display: flex;
  justify-content: space-between;
  align-items: center;
  min-width: 120px;
  padding: 12px 16px;
}

.topping-price {
  font-size: 12px;
  color: var(--primary-color);
  font-weight: 600;
  margin-left: 8px;
}

.quantity-control {
  display: flex;
  align-items: center;
  gap: 20px;
}

.quantity-btn {
  width: 36px;
  height: 36px;
  border: 2px solid var(--border-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 18px;
  font-weight: 600;
  color: var(--text-color-dark);
  background: white;
  transition: all 0.3s ease;
}

.quantity-btn:hover:not(.disabled) {
  border-color: var(--primary-color);
  color: var(--primary-dark);
  transform: scale(1.1);
}

.quantity-btn.disabled {
  color: var(--text-color-light);
  border-color: var(--border-color);
  cursor: not-allowed;
  opacity: 0.5;
}

.quantity-value {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-dark);
  min-width: 40px;
  text-align: center;
  font-family: 'Prompt', sans-serif;
}

.modal-footer {
  margin-top: 24px;
  padding-top: 20px;
  border-top: 2px dashed var(--border-color);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.total-price {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-label {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
}

.total-value {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  text-shadow: 0 2px 4px rgba(139, 69, 19, 0.1);
}

.confirm-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border: none;
  padding: 14px 40px;
  border-radius: 25px;
  font-size: 18px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  font-family: 'Prompt', sans-serif;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.3);
}

.confirm-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

/* 响应式调整 */
@media (max-width: 768px) {
  .cart-bar {
    bottom: 70px;
  }
}

@media (max-width: 480px) {
  .cart-bar {
    bottom: 60px;
  }
}


@media (max-width: 375px) {
  .search-bar {
    padding: 16px 20px;
  }

  .search-input {
    height: 48px;
    padding: 0 16px;
  }

  .category-sidebar {
    width: 85px;
  }

  .category-item {
    padding: 20px 10px;
  }

  .product-section {
    padding: 0 20px;
  }

  .filter-bar {
    padding: 16px 0;
  }

  .sort-options {
    gap: 16px;
  }

  .product-card {
    padding: 16px;
    gap: 16px;
  }

  .product-image-wrapper {
    width: 100px;
    height: 100px;
  }

  .modal-content {
    padding: 24px;
  }

  .modal-image {
    width: 80px;
    height: 80px;
  }

  .option-item {
    padding: 8px 16px;
  }

  .option-item.topping {
    min-width: 100px;
  }
}
</style>