<template>
  <div class="search-page">
    <!-- 搜索栏 -->
    <div class="search-header">
      <div class="search-input-wrapper">
        <input 
          type="text" 
          placeholder="搜索奶茶、咖啡、小食..." 
          v-model="searchKeyword"
          @keyup.enter="onSearch"
          ref="searchInput"
        />
        <span 
          class="close-icon" 
          v-if="searchKeyword" 
          @click="clearKeyword"
        >✕</span>
      </div>
      <div class="search-action" @click="onSearch">
        <span class="search-icon">🔍</span>
      </div>
    </div>

    <!-- 搜索历史 -->
    <div class="search-history" v-if="!showResults && searchHistory.length > 0">
      <div class="history-header">
        <span class="history-title">搜索历史</span>
        <span class="delete-icon" @click="clearHistory">🗑️</span>
      </div>
      <div class="history-tags">
        <span 
          class="history-tag" 
          v-for="(item, index) in searchHistory" 
          :key="index"
          @click="searchByKeyword(item)"
        >
          {{ item }}
        </span>
      </div>
    </div>

    <!-- 热门搜索 -->
    <div class="hot-search" v-if="!showResults">
      <div class="hot-header">
        <span class="hot-title">热门搜索</span>
      </div>
      <div class="hot-tags">
        <span 
          class="hot-tag" 
          :class="{ hot: index < 3 }" 
          v-for="(item, index) in hotKeywords" 
          :key="index"
          @click="searchByKeyword(item.word)"
        >
          {{ index + 1 }}. {{ item.word }}
        </span>
      </div>
    </div>

    <!-- 搜索建议 -->
    <div class="search-suggest" v-if="showSuggest && searchKeyword && !showResults">
      <div class="suggest-list">
        <div 
          class="suggest-item" 
          v-for="(item, index) in searchSuggest" 
          :key="index"
          @click="searchByKeyword(item)"
        >
          <span class="suggest-icon">🔍</span>
          <span class="suggest-text">{{ item }}</span>
        </div>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div class="search-results" v-if="showResults">
      <!-- 结果类型切换 -->
      <div class="search-tabs">
        <div
          class="tab-item"
          :class="{ active: searchType === 'product' }"
          @click="searchType = 'product'"
        >商品 ({{ filteredProducts.length }})</div>
        <div
          class="tab-item"
          :class="{ active: searchType === 'store' }"
          @click="searchType = 'store'"
        >门店 ({{ filteredStores.length }})</div>
      </div>

      <!-- 商品搜索结果 -->
      <template v-if="searchType === 'product'">
        <div class="results-header">
          <div class="sort-options">
            <span
              class="sort-option"
              :class="{ active: sortBy === 'default' }"
              @click="changeSort('default')"
            >综合</span>
            <span
              class="sort-option"
              :class="{ active: sortBy === 'sales' }"
              @click="changeSort('sales')"
            >销量</span>
            <span
              class="sort-option"
              :class="{ active: sortBy === 'price_asc' }"
              @click="changeSort('price_asc')"
            >价格 ↑</span>
            <span
              class="sort-option"
              :class="{ active: sortBy === 'price_desc' }"
              @click="changeSort('price_desc')"
            >价格 ↓</span>
          </div>
        </div>

        <div class="filter-options">
          <div class="filter-tags">
            <span
              class="filter-tag"
              :class="{ active: activeCategory === '' }"
              @click="activeCategory = ''"
            >全部</span>
            <span
              class="filter-tag"
              :class="{ active: activeCategory === category.id }"
              v-for="category in categories"
              :key="category.id"
              @click="activeCategory = category.id"
            >
              {{ category.name }}
            </span>
          </div>
        </div>

        <div class="product-list" v-if="filteredProducts.length > 0">
          <div class="product-grid">
            <ProductCard
              v-for="item in filteredProducts"
              :key="item.id"
              :product="item"
              :isFavorite="favoriteIds.has(item.id)"
              @click="onProductTap(item.id)"
              @favorite-change="onFavoriteChange"
            />
          </div>
        </div>
        <div class="empty-results" v-else>
          <div class="empty-icon">🔍</div>
          <p class="empty-text">没有找到相关商品</p>
        </div>
      </template>

      <!-- 门店搜索结果 -->
      <template v-else>
        <div class="store-list" v-if="filteredStores.length > 0">
          <div
            class="store-item"
            v-for="store in filteredStores"
            :key="store.id"
            @click="onStoreTap(store.id)"
          >
            <div class="store-info">
              <div class="store-name">{{ store.name }}</div>
              <div class="store-address">{{ store.address }}</div>
              <div class="store-status">
                <span class="status-tag" :class="store.status">{{ store.status === 'OPEN' ? '营业中' : '休息中' }}</span>
                <span class="distance" v-if="store.distance">{{ store.distance }}km</span>
              </div>
            </div>
            <div class="store-action">
              <span class="go-btn">去下单</span>
            </div>
          </div>
        </div>
        <div class="empty-results" v-else>
          <div class="empty-icon">🏪</div>
          <p class="empty-text">没有找到相关门店</p>
        </div>
      </template>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import { productApi, searchApi, favoriteApi } from '@/utils/api.js'

const router = useRouter()
const route = useRoute()

const searchKeyword = ref('')
const searchType = ref('product')
const showResults = ref(false)
const showSuggest = ref(false)
const searchHistory = ref([])
const hotKeywords = ref([])
const searchSuggest = ref([])
const searchResults = ref([])
const storeResults = ref([])
const favoriteIds = ref(new Set())
const sortBy = ref('default')
const activeCategory = ref('')
const categories = ref([])

onMounted(async () => {
  try {
    const [historyRes, hotRes, categoriesRes, favoritesRes] = await Promise.all([
      productApi.getSearchHistory(),
      productApi.getHotKeywords(),
      productApi.getCategories(),
      favoriteApi.getFavorites({ page: 1, size: 100 })
    ])

    if (favoritesRes && favoritesRes.data && favoritesRes.data.content) {
      favoritesRes.data.content.forEach(item => {
        favoriteIds.value.add(item.product.id)
      })
    }
    
    if (historyRes.code === 200) {
      searchHistory.value = historyRes.data || []
    }
    
    if (hotRes.code === 200) {
      hotKeywords.value = hotRes.data || []
    }
    
    if (categoriesRes.code === 200) {
      categories.value = categoriesRes.data || []
    }
  } catch (error) {
    console.error('加载搜索初始化数据失败:', error)
  }
  
  if (route.query.keyword) {
    searchKeyword.value = route.query.keyword
    onSearch()
  }
})

watch(searchKeyword, (newVal) => {
  if (newVal) {
    showSuggest.value = true
    searchSuggest.value = [
      `${newVal}奶茶`,
      `${newVal}拿铁`,
      `${newVal}冰淇淋`
    ]
  } else {
    showSuggest.value = false
    showResults.value = false
  }
})

const filteredProducts = computed(() => {
  let results = [...searchResults.value]
  
  if (activeCategory.value) {
    results = results.filter(item => item.categoryId === activeCategory.value || item.category === activeCategory.value)
  }
  
  switch (sortBy.value) {
    case 'sales':
      results.sort((a, b) => (b.sales || 0) - (a.sales || 0))
      break
    case 'price_asc':
      results.sort((a, b) => a.price - b.price)
      break
    case 'price_desc':
      results.sort((a, b) => b.price - a.price)
      break
  }
  
  return results
})

const filteredStores = computed(() => {
  return storeResults.value
})

const onSearch = async () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return
  
  try {
    const [productRes, storeRes] = await Promise.all([
      productApi.searchProducts(keyword),
      searchApi.searchStores(keyword)
    ])

    if (productRes.code === 200) {
      searchResults.value = productRes.data.list || productRes.data || []
    }

    if (storeRes.code === 200) {
      storeResults.value = storeRes.data.list || storeRes.data || []
    }

    showResults.value = true
    showSuggest.value = false
      
      // 重新加载历史记录（后端已保存）
      const historyRes = await productApi.getSearchHistory()
      if (historyRes.code === 200) {
        searchHistory.value = historyRes.data || []
      }
  } catch (error) {
    console.error('搜索失败:', error)
  }
}

const searchByKeyword = (keyword) => {
  searchKeyword.value = keyword
  onSearch()
}

const clearKeyword = () => {
  searchKeyword.value = ''
  showResults.value = false
}

const clearHistory = async () => {
  if (confirm('确定要清除搜索历史吗？')) {
    try {
      const res = await productApi.clearSearchHistory()
      if (res.code === 200) {
        searchHistory.value = []
      }
    } catch (error) {
      console.error('清空历史失败:', error)
    }
  }
}

const changeSort = (sort) => {
  sortBy.value = sort
}

const onProductTap = (id) => {
  router.push(`/product/${id}`)
}

const onFavoriteChange = async ({ id, isFavorite }) => {
  try {
    if (isFavorite) {
      await favoriteApi.addFavorite(id)
      favoriteIds.value.add(id)
    } else {
      await favoriteApi.removeFavorite(id)
      favoriteIds.value.delete(id)
    }
  } catch (error) {
    console.error('操作收藏失败:', error)
    alert('操作失败，请稍后重试')
  }
}

const onStoreTap = (id) => {
  router.push(`/order?storeId=${id}`)
}

const resetSearch = () => {
  searchKeyword.value = ''
  showResults.value = false
}
</script>
<style scoped>
/* 奶茶主题 CSS 变量定义 */
.search-page {
  --background-color: #f5f0e1; /* 奶油色背景 */
  --surface-color: #e8dccb;    /* 表面元素色 - 浅卡其 */
  --primary-color: #a0522d;    /* 焦糖色 - 主色调 */
  --primary-dark: #8b4513;     /* 深咖啡色 */
  --primary-light: #d2b48c;    /* 浅驼色 */
  --accent-cream: #fff8dc;     /* 奶油强调色 */
  --accent-pink: #ffc0cb;      /* 淡粉色 */
  --accent-brown: #deb887;     /* 沙棕色 */
  --text-color-dark: #4a3b30;  /* 深棕色文本 */
  --text-color-medium: #7a6a5b; /* 中棕色文本 */
  --text-color-light: #a09080;  /* 浅咖色文本 */
  --border-color: #d4c7b5;      /* 边框色 */
  --hot-color: #ff6b6b;        /* 热门标签颜色 */

  --border-radius-sm: 8px;
  --border-radius-md: 16px;
  --border-radius-lg: 24px;
  --border-radius-xl: 50%;      /* 用于圆形元素 */

  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;

  --shadow-soft: 0 4px 12px rgba(160, 82, 45, 0.08);
  --shadow-medium: 0 6px 20px rgba(160, 82, 45, 0.12);
  --shadow-hover: 0 8px 24px rgba(160, 82, 45, 0.16);

  --transition-smooth: all 0.25s ease-out;
  --transition-bounce: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.search-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: 'Noto Sans KR', 'Nunito', 'Quicksand', sans-serif;
  color: var(--text-color-dark);
}

/* 搜索头部 */
.search-header {
  background: linear-gradient(135deg,
  var(--primary-color) 0%,
  var(--primary-dark) 100%);
  padding: var(--spacing-md) var(--spacing-lg);
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  position: sticky;
  top: 0;
  z-index: 100;
  box-shadow: var(--shadow-medium);
}

.search-input-wrapper {
  flex: 1;
  height: 44px;
  background: var(--accent-cream);
  border-radius: var(--border-radius-xl);
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-md);
  position: relative;
  box-shadow: 0 2px 8px rgba(139, 69, 19, 0.15);
  transition: var(--transition-smooth);
}

.search-input-wrapper:focus-within {
  box-shadow: 0 4px 16px rgba(139, 69, 19, 0.25);
  transform: translateY(-1px);
}

.search-input-wrapper input {
  flex: 1;
  background: transparent;
  border: none;
  font-size: 15px;
  outline: none;
  color: var(--text-color-dark);
  font-family: 'Noto Sans KR', sans-serif;
  padding-right: var(--spacing-sm);
}

.search-input-wrapper input::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

.close-icon {
  font-size: 14px;
  color: var(--text-color-medium);
  cursor: pointer;
  width: 20px;
  height: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.8);
  transition: var(--transition-smooth);
}

.close-icon:hover {
  background: white;
  color: var(--primary-color);
  transform: scale(1.1);
}

.search-action {
  width: 44px;
  height: 44px;
  background: var(--accent-cream);
  border-radius: var(--border-radius-xl);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  box-shadow: 0 2px 8px rgba(139, 69, 19, 0.15);
  transition: var(--transition-bounce);
}

.search-action:hover {
  background: white;
  transform: scale(1.05) rotate(5deg);
  box-shadow: 0 4px 16px rgba(139, 69, 19, 0.25);
}

.search-icon {
  font-size: 18px;
  color: var(--primary-color);
}

/* 搜索历史和热门搜索 */
.search-history, .hot-search {
  background: var(--surface-color);
  padding: var(--spacing-lg);
  margin: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
}

.search-history::before, .hot-search::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg,
  var(--primary-color) 0%,
  var(--accent-pink) 50%,
  var(--accent-cream) 100%);
  opacity: 0.6;
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

.history-header, .hot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.history-title, .hot-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  position: relative;
  padding-left: var(--spacing-sm);
}

.history-title::before, .hot-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: var(--primary-color);
  border-radius: 2px;
}

.delete-icon {
  font-size: 18px;
  color: var(--text-color-medium);
  cursor: pointer;
  padding: var(--spacing-xs);
  border-radius: var(--border-radius-sm);
  transition: var(--transition-smooth);
}

.delete-icon:hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.1);
  transform: rotate(15deg) scale(1.1);
}

/* 标签样式 */
.history-tags, .hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-sm);
}

.history-tag, .hot-tag {
  background: var(--accent-cream);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-xl);
  font-size: 13px;
  color: var(--text-color-medium);
  cursor: pointer;
  border: 1px solid var(--border-color);
  transition: var(--transition-bounce);
  position: relative;
  overflow: hidden;
}

.history-tag:hover, .hot-tag:hover {
  background: white;
  color: var(--primary-color);
  transform: translateY(-2px) scale(1.05);
  box-shadow: var(--shadow-hover);
  border-color: var(--primary-light);
}

.history-tag::before, .hot-tag::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
  transparent,
  rgba(255, 255, 255, 0.4),
  transparent);
  transition: 0.5s;
}

.history-tag:hover::before, .hot-tag:hover::before {
  left: 100%;
}

/* 热门标签特殊样式 */
.hot-tag {
  position: relative;
}

.hot-tag.hot {
  color: var(--hot-color);
  background: rgba(255, 107, 107, 0.1);
  border-color: rgba(255, 107, 107, 0.3);
  font-weight: 500;
}

.hot-tag.hot::after {
  content: '🔥';
  position: absolute;
  top: -6px;
  right: -6px;
  font-size: 10px;
  opacity: 0.8;
}

.hot-tag:nth-child(1).hot::after {
  content: '🥇';
  font-size: 12px;
}

.hot-tag:nth-child(2).hot::after {
  content: '🥈';
  font-size: 12px;
}

.hot-tag:nth-child(3).hot::after {
  content: '🥉';
  font-size: 12px;
}

/* 搜索建议 */
.search-suggest {
  position: absolute;
  top: 72px;
  left: var(--spacing-md);
  right: var(--spacing-md);
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-medium);
  border: 1px solid var(--border-color);
  z-index: 90;
  overflow: hidden;
  animation: slideDown 0.3s ease-out;
}

@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.suggest-list {
  max-height: 300px;
  overflow-y: auto;
}

.suggest-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: var(--transition-smooth);
}

.suggest-item:hover {
  background: var(--accent-cream);
  padding-left: var(--spacing-xl);
}

.suggest-item:last-child {
  border-bottom: none;
}

.suggest-icon {
  font-size: 14px;
  color: var(--text-color-light);
  transition: var(--transition-smooth);
}

.suggest-item:hover .suggest-icon {
  color: var(--primary-color);
  transform: scale(1.2);
}

.suggest-text {
  font-size: 14px;
  color: var(--text-color-dark);
  flex: 1;
}

/* 搜索结果区域 */
.search-results {
  padding: var(--spacing-md);
}

/* 搜索标签页 */
.search-tabs {
  display: flex;
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xs);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: 14px;
  font-weight: 500;
  color: var(--text-color-medium);
  cursor: pointer;
  border-radius: var(--border-radius-md);
  transition: var(--transition-smooth);
  position: relative;
  overflow: hidden;
}

.tab-item:hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.05);
}

.tab-item.active {
  color: white;
  background: linear-gradient(135deg,
  var(--primary-color) 0%,
  var(--primary-dark) 100%);
  box-shadow: var(--shadow-soft);
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 20px;
  height: 3px;
  background: var(--accent-cream);
  border-radius: 2px;
}

/* 排序选项 */
.results-header {
  background: var(--surface-color);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-md);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
}

.sort-options {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
}

.sort-option {
  font-size: 13px;
  color: var(--text-color-medium);
  cursor: pointer;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  transition: var(--transition-smooth);
  position: relative;
}

.sort-option:hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.1);
}

.sort-option.active {
  color: var(--primary-color);
  font-weight: 600;
  background: var(--accent-cream);
  padding-right: var(--spacing-md);
}

.sort-option.active::after {
  content: '✓';
  position: absolute;
  right: var(--spacing-xs);
  font-size: 10px;
}

/* 分类筛选 */
.filter-options {
  background: var(--surface-color);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-lg);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
  overflow-x: auto;
  white-space: nowrap;
}

.filter-tags {
  display: flex;
  gap: var(--spacing-sm);
  padding-bottom: var(--spacing-xs);
}

.filter-tag {
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-xl);
  font-size: 12px;
  background: var(--accent-cream);
  color: var(--text-color-medium);
  cursor: pointer;
  border: 1px solid var(--border-color);
  transition: var(--transition-bounce);
  flex-shrink: 0;
}

.filter-tag:hover {
  background: white;
  color: var(--primary-color);
  transform: translateY(-2px);
  border-color: var(--primary-light);
}

.filter-tag.active {
  background: linear-gradient(135deg,
  var(--primary-color) 0%,
  var(--primary-dark) 100%);
  color: white;
  border-color: var(--primary-color);
  box-shadow: var(--shadow-soft);
  transform: translateY(-2px);
}

/* 商品列表 */
.product-list {
  padding: var(--spacing-sm);
}

.product-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: var(--spacing-md);
}

/* 门店列表 */
.store-list {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
}

.store-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: var(--transition-smooth);
  background: rgba(255, 255, 255, 0.5);
}

.store-item:hover {
  background: var(--accent-cream);
  transform: translateX(4px);
}

.store-item:last-child {
  border-bottom: none;
}

.store-info {
  flex: 1;
}

.store-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-xs);
  font-family: 'Prompt', sans-serif;
}

.store-address {
  font-size: 13px;
  color: var(--text-color-medium);
  margin-bottom: var(--spacing-sm);
}

.store-status {
  display: flex;
  gap: var(--spacing-sm);
  align-items: center;
}

.status-tag {
  font-size: 11px;
  padding: 2px var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  font-weight: 500;
}

.status-tag.OPEN {
  background: rgba(76, 175, 80, 0.1);
  color: #4CAF50;
  border: 1px solid rgba(76, 175, 80, 0.3);
}

.status-tag.CLOSED {
  background: rgba(244, 67, 54, 0.1);
  color: #F44336;
  border: 1px solid rgba(244, 67, 54, 0.3);
}

.distance {
  font-size: 12px;
  color: var(--text-color-light);
}

.store-action {
  margin-left: var(--spacing-md);
}

.go-btn {
  background: linear-gradient(135deg,
  var(--primary-color) 0%,
  var(--primary-dark) 100%);
  color: white;
  padding: var(--spacing-xs) var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  font-size: 12px;
  font-weight: 500;
  cursor: pointer;
  transition: var(--transition-bounce);
  display: inline-block;
  box-shadow: var(--shadow-soft);
}

.go-btn:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-hover);
}

/* 空状态 */
.empty-results {
  padding: var(--spacing-xl) var(--spacing-md);
  text-align: center;
  color: var(--text-color-medium);
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  margin-top: var(--spacing-lg);
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
}

.empty-icon {
  font-size: 64px;
  margin-bottom: var(--spacing-lg);
  opacity: 0.3;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-10px); }
}

.empty-text {
  font-size: 15px;
  font-weight: 500;
  margin-bottom: var(--spacing-md);
  font-family: 'Prompt', sans-serif;
}

.empty-hint {
  font-size: 13px;
  color: var(--text-color-light);
  margin-top: var(--spacing-sm);
}

.reset-btn {
  margin-top: var(--spacing-lg);
  background: linear-gradient(135deg,
  var(--primary-color) 0%,
  var(--primary-dark) 100%);
  color: white;
  border: none;
  padding: var(--spacing-sm) var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  font-size: 14px;
  font-weight: 600;
  cursor: pointer;
  transition: var(--transition-bounce);
  box-shadow: var(--shadow-medium);
  font-family: 'Prompt', sans-serif;
}

.reset-btn:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: var(--shadow-hover);
}

/* 响应式调整 */
@media (max-width: 480px) {
  .search-header {
    padding: var(--spacing-sm);
  }

  .search-input-wrapper {
    height: 40px;
  }

  .search-action {
    width: 40px;
    height: 40px;
  }

  .search-history, .hot-search {
    margin: var(--spacing-sm);
    padding: var(--spacing-md);
  }

  .product-grid {
    grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
    gap: var(--spacing-sm);
  }

  .store-item {
    padding: var(--spacing-md);
  }

  .empty-icon {
    font-size: 56px;
  }
}

/* 奶茶主题装饰 */
.search-history::after {
  content: '📝';
  position: absolute;
  bottom: -10px;
  right: -10px;
  font-size: 24px;
  opacity: 0.1;
  transform: rotate(-15deg);
}

.hot-search::after {
  content: '🔥';
  position: absolute;
  bottom: -10px;
  right: -10px;
  font-size: 24px;
  opacity: 0.1;
  transform: rotate(15deg);
}
</style>