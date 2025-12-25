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
      <!-- 搜索结果头部 -->
      <div class="results-header">
        <span class="results-count">
          找到{{ searchResults.length }}个相关商品
        </span>
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

      <!-- 筛选条件 -->
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

      <!-- 商品列表 -->
      <div class="product-list" v-if="filteredResults.length > 0">
        <div class="product-grid">
          <ProductCard 
            v-for="item in filteredResults" 
            :key="item.id"
            :product="item"
            @click="onProductTap(item.id)"
          />
        </div>
      </div>

      <!-- 空结果提示 -->
      <div class="empty-results" v-else>
        <div class="empty-icon">🔍</div>
        <p class="empty-text">没有找到相关商品</p>
        <p class="empty-hint">换个关键词试试吧</p>
        <button class="reset-btn" @click="resetSearch">重新搜索</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import ProductCard from '@/components/ProductCard.vue'
import { searchApi, productApi } from '@/utils/api'

const router = useRouter()
const route = useRoute()

const searchKeyword = ref('')
const showResults = ref(false)
const showSuggest = ref(false)
const searchHistory = ref([])
const hotKeywords = ref([])
const searchSuggest = ref([])
const searchResults = ref([])
const sortBy = ref('default')
const activeCategory = ref('')
const categories = ref([])

onMounted(async () => {
  try {
    const [historyRes, hotRes, categoriesRes] = await Promise.all([
      productApi.getSearchHistory(),
      productApi.getHotKeywords(),
      productApi.getCategories()
    ])
    
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

const filteredResults = computed(() => {
  let results = [...searchResults.value]
  
  if (activeCategory.value) {
    results = results.filter(item => item.category === activeCategory.value)
  }
  
  switch (sortBy.value) {
    case 'sales':
      results.sort((a, b) => b.sales - a.sales)
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

const onSearch = async () => {
  const keyword = searchKeyword.value.trim()
  if (!keyword) return
  
  try {
    const res = await productApi.searchProducts(keyword)
    if (res.code === 200) {
      searchResults.value = res.data.list || res.data || []
      showResults.value = true
      showSuggest.value = false
      
      // 重新加载历史记录（后端已保存）
      const historyRes = await productApi.getSearchHistory()
      if (historyRes.code === 200) {
        searchHistory.value = historyRes.data || []
      }
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

const resetSearch = () => {
  searchKeyword.value = ''
  showResults.value = false
}
</script>

<style scoped>
.search-page {
  min-height: 100vh;
  background: #F8F8F8;
}

.search-header {
  background: white;
  padding: 10px 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.search-input-wrapper {
  flex: 1;
  height: 36px;
  background: #F5F5F5;
  border-radius: 18px;
  display: flex;
  align-items: center;
  padding: 0 15px;
  position: relative;
}

.search-input-wrapper input {
  flex: 1;
  background: transparent;
  border: none;
  font-size: 14px;
  outline: none;
}

.close-icon {
  font-size: 12px;
  color: #999;
  cursor: pointer;
}

.search-action {
  font-size: 20px;
  cursor: pointer;
}

.search-history, .hot-search {
  background: white;
  padding: 20px 15px;
  margin-bottom: 10px;
}

.history-header, .hot-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.history-title, .hot-title {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.delete-icon {
  font-size: 16px;
  color: #999;
  cursor: pointer;
}

.history-tags, .hot-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.history-tag, .hot-tag {
  background: #F5F5F5;
  padding: 6px 15px;
  border-radius: 15px;
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.hot-tag.hot {
  color: #D4A574;
  background: #FDF8F3;
}

.search-suggest {
  position: absolute;
  top: 56px;
  left: 0;
  right: 0;
  bottom: 0;
  background: white;
  z-index: 90;
}

.suggest-item {
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 15px;
  border-bottom: 1px solid #F5F5F5;
  cursor: pointer;
}

.suggest-icon {
  font-size: 14px;
  color: #CCC;
}

.suggest-text {
  font-size: 14px;
  color: #333;
}

.results-header {
  background: white;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #F5F5F5;
}

.results-count {
  font-size: 12px;
  color: #999;
}

.sort-options {
  display: flex;
  gap: 15px;
}

.sort-option {
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.sort-option.active {
  color: #D4A574;
  font-weight: bold;
}

.filter-options {
  background: white;
  padding: 10px 15px;
  overflow-x: auto;
}

.filter-tags {
  display: flex;
  gap: 10px;
  white-space: nowrap;
}

.filter-tag {
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  background: #F5F5F5;
  color: #666;
  cursor: pointer;
}

.filter-tag.active {
  background: #D4A574;
  color: white;
}

.product-list {
  padding: 15px;
}

.product-grid {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 15px;
}

.empty-results {
  padding-top: 100px;
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  opacity: 0.2;
}

.empty-hint {
  font-size: 12px;
  margin-top: 5px;
}

.reset-btn {
  margin-top: 20px;
  background: #D4A574;
  color: white;
  border: none;
  padding: 8px 25px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
}
</style>