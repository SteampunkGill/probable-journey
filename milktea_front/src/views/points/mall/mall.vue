<template>
  <div class="points-mall-page">
    <!-- 头部 -->
    <div class="mall-header">
      <div class="header-top">
        <div class="header-left">
          <i class="iconfont icon-left" @click="goBack"></i>
          <span class="header-title">积分商城</span>
        </div>
        <div class="header-right">
          <i class="iconfont icon-history" @click="viewExchangeRecords"></i>
          <i class="iconfont icon-info" @click="howToGetPoints"></i>
        </div>
      </div>
      
      <!-- 积分信息 -->
      <div class="points-info-card">
        <div class="points-main">
          <div class="points-left">
            <span class="points-label">当前积分</span>
            <div class="points-value">
              <span class="points-number">{{ points }}</span>
              <span class="points-unit">积分</span>
            </div>
          </div>
          <div class="points-right">
            <span class="exchange-label">可兑换商品</span>
          </div>
        </div>
        
        <div class="points-actions">
          <div class="action-item" @click="signIn">
            <span class="action-label">今日签到</span>
            <div class="action-icon">
              <i class="iconfont icon-check"></i>
            </div>
          </div>
          <div class="action-item" @click="viewTasks">
            <span class="action-label">积分任务</span>
            <div class="action-icon">
              <i class="iconfont icon-gift"></i>
            </div>
          </div>
          <div class="action-item" @click="viewExchangeRecords">
            <span class="action-label">兑换记录</span>
            <div class="action-icon">
              <i class="iconfont icon-list"></i>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 搜索框 -->
    <div class="search-section">
      <div class="search-box">
        <i class="iconfont icon-search"></i>
        <input 
          v-model="searchKeyword"
          placeholder="搜索积分商品"
          @input="searchProducts"
        />
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="categories-section">
      <div class="categories-scroll">
        <div 
          v-for="(category, index) in categories"
          :key="category.id"
          class="category-item"
          :class="{ active: category.active }"
          @click="selectCategory(index)"
        >
          {{ category.name }}
        </div>
      </div>
    </div>

    <!-- 排序选项 -->
    <div class="sort-section">
      <div class="sort-options">
        <div 
          v-for="(option, index) in sortOptions"
          :key="option.id"
          class="sort-item"
          :class="{ active: option.active }"
          @click="selectSort(index)"
        >
          {{ option.name }}
        </div>
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="products-section">
      <div v-if="loading" class="loading-state">
        <i class="iconfont icon-loading"></i>
        <p>加载中...</p>
      </div>
      
      <div v-else class="products-grid">
        <div 
          v-for="product in filteredProducts"
          :key="product.id"
          class="product-card"
          @click="viewProductDetail(product.id)"
        >
          <!-- 热门标签 -->
          <div v-if="product.hot" class="hot-tag">
            <i class="iconfont icon-fire"></i>
            <span>热门</span>
          </div>
          
          <!-- 商品图片 -->
          <div class="product-image">
            <img :src="product.image || product.imageUrl || '/images/default-product.jpg'" :alt="product.name" />
          </div>
          
          <!-- 商品信息 -->
          <div class="product-info">
            <h3 class="product-name">{{ product.name }}</h3>
            <p class="product-desc">{{ product.description || product.desc }}</p>
            
            <!-- 积分信息 -->
            <div class="product-points">
              <div class="points-display">
                <span class="current-points">{{ product.points || product.pointCost }}</span>
                <span class="points-unit">积分</span>
                <span v-if="product.originalPoints > (product.points || product.pointCost)" class="original-points">
                  {{ product.originalPoints }}积分
                </span>
              </div>
              
              <!-- 库存信息 -->
              <div class="stock-info">
                <span>库存: {{ product.stock }}件</span>
                <span class="limit-info">{{ product.limitInfo || product.limit }}</span>
              </div>
            </div>
            
            <!-- 兑换按钮 -->
            <button class="exchange-btn" @click.stop="exchangeProduct(product)">
              立即兑换
            </button>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <div v-if="!loading && filteredProducts.length === 0" class="empty-state">
        <i class="iconfont icon-gift"></i>
        <p class="empty-text">暂无商品</p>
        <p class="empty-hint">换个分类试试吧~</p>
      </div>
    </div>

    <!-- 底部提示 -->
    <div class="footer-hint">
      <p>积分兑换规则：兑换成功后不可退款，优惠券类商品请在有效期内使用</p>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { pointsApi, authApi } from '@/utils/api.js'

const router = useRouter()

// 数据
const points = ref(0)
const searchKeyword = ref('')
const loading = ref(false)

const categories = ref([
  { id: 'all', name: '全部', active: true }
])

const sortOptions = ref([
  { id: 1, name: '积分从低到高', active: true },
  { id: 2, name: '积分从高到低', active: false },
  { id: 3, name: '最新上架', active: false },
  { id: 4, name: '热门推荐', active: false }
])

const products = ref([])

// 计算属性
const filteredProducts = computed(() => {
  let filtered = [...products.value]
  
  // 搜索过滤
  if (searchKeyword.value) {
    filtered = filtered.filter(product =>
      product.name.includes(searchKeyword.value) ||
      (product.description || product.desc || '').includes(searchKeyword.value)
    )
  }
  
  // 排序
  const activeSort = sortOptions.value.find(s => s.active)
  if (activeSort) {
    switch (activeSort.name) {
      case '积分从低到高':
        filtered.sort((a, b) => (a.points || a.pointCost || 0) - (b.points || b.pointCost || 0))
        break
      case '积分从高到低':
        filtered.sort((a, b) => (b.points || b.pointCost || 0) - (a.points || a.pointCost || 0))
        break
      case '最新上架':
        filtered.sort((a, b) => b.id - a.id)
        break
      case '热门推荐':
        filtered.sort((a, b) => (b.hot ? 1 : 0) - (a.hot ? 1 : 0))
        break
    }
  }
  
  return filtered
})

// 方法
const goBack = () => {
  router.back()
}

const viewExchangeRecords = () => {
  router.push('/points/mall/exchange-records')
}

const howToGetPoints = () => {
  alert('如何获取积分：\n1. 每日签到\n2. 消费获得\n3. 完成任务\n4. 邀请好友')
}

const signIn = async () => {
  try {
    const res = await pointsApi.signIn()
    // request.js 拦截器已经处理了 code 校验并返回了 res.data
    // 如果能走到这里，说明请求成功且 code 为 200
    if (res !== undefined) {
      points.value = res
      // 更新本地存储的用户信息中的积分
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      userInfo.points = res
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      alert('签到成功！获得10积分')
    }
  } catch (error) {
    console.error('签到失败:', error)
    alert('签到失败，请稍后重试')
  }
}

const viewTasks = () => {
  alert('查看积分任务')
}

const searchProducts = () => {
  // 搜索逻辑已在计算属性中处理
}

const selectCategory = (index) => {
  categories.value.forEach((category, i) => {
    category.active = i === index
  })
  loadProducts()
}

const selectSort = (index) => {
  sortOptions.value.forEach((option, i) => {
    option.active = i === index
  })
}

const viewProductDetail = (productId) => {
  alert(`查看商品详情：${productId}`)
}

const exchangeProduct = async (product) => {
  const productPoints = product.points || product.pointCost || 0
  if (points.value < productPoints) {
    alert(`积分不足！需要${productPoints}积分，当前只有${points.value}积分`)
    return
  }
  
  if (confirm(`确定要兑换【${product.name}】吗？\n需要${productPoints}积分`)) {
    try {
      loading.value = true
      await pointsApi.exchangeProduct(product.id)
      // 如果没有抛出异常，说明兑换成功
      points.value -= productPoints
      // 更新本地存储的用户信息中的积分
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      userInfo.points = points.value
      localStorage.setItem('userInfo', JSON.stringify(userInfo))
      alert('兑换成功！请到兑换记录中查看')
      loadProducts()
    } catch (error) {
      console.error('兑换失败:', error)
      // 错误信息已由 request.js 拦截器弹出
    } finally {
      loading.value = false
    }
  }
}

const loadProducts = async () => {
  try {
    loading.value = true
    const activeCategory = categories.value.find(c => c.active)
    const category = activeCategory && activeCategory.id !== 'all' ? activeCategory.id.toUpperCase() : null
    const data = await pointsApi.getPointsProducts(1, 50, category)
    if (data) {
      products.value = data.list || data || []
    }
  } catch (error) {
    console.error('加载积分商品失败:', error)
  } finally {
    loading.value = false
  }
}

const loadInitialData = async () => {
  try {
    loading.value = true
    // 获取积分
    try {
      const userProfile = await authApi.getUserProfile()
      if (userProfile) {
        points.value = userProfile.points || 0
        // 同步更新本地存储
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
        userInfo.points = points.value
        localStorage.setItem('userInfo', JSON.stringify(userInfo))
      }
    } catch (e) {
      console.error('获取用户信息失败，使用本地缓存:', e)
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
      points.value = userInfo.points || 0
    }

    const data = await pointsApi.getPointsCategories()
    if (data) {
      const backendCategories = data || []
      categories.value = [
        { id: 'all', name: '全部', active: true },
        ...backendCategories.map(c => ({
          id: c.type.toLowerCase(),
          name: c.name,
          active: false
        }))
      ]
    }
    
    await loadProducts()
  } catch (error) {
    console.error('加载初始数据失败:', error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadInitialData()
})
</script>

<style scoped>

  /* ============================================
  “饮饮茶(SipSipTea)” 积分商城页面样式优化
  基于奶茶主题设计指南
  ============================================ */

  /* ========== 页面容器 ========== */
.points-mall-page {
  min-height: 100vh;
  background: var(--background-color);
  position: relative;
  overflow-x: hidden;
}

/* 奶茶主题背景装饰 */
.points-mall-page::before {
  content: '';
  position: absolute;
  top: -20%;
  right: -10%;
  width: 180px;
  height: 180px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.1;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

.points-mall-page::after {
  content: '';
  position: absolute;
  bottom: -15%;
  left: -5%;
  width: 120px;
  height: 120px;
  background: radial-gradient(circle, var(--accent-cream) 0%, transparent 70%);
  opacity: 0.15;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

/* ========== 头部样式 ========== */
.mall-header {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  padding: var(--spacing-xl) var(--spacing-lg) var(--spacing-lg);
  color: white;
  position: relative;
  z-index: 1;
  border-bottom-left-radius: var(--border-radius-xl);
  border-bottom-right-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-md);
}

/* 头部装饰 */
.mall-header::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--texture-paper);
  opacity: 0.05;
  border-radius: inherit;
  pointer-events: none;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  position: relative;
  z-index: 2;
}

.header-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.icon-left {
  font-size: var(--font-size-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--border-radius-circle);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.icon-left:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateX(-2px);
}

.header-title {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  letter-spacing: 0.5px;
}

.header-right {
  display: flex;
  gap: var(--spacing-md);
}

.icon-history, .icon-info {
  font-size: var(--font-size-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.15);
  border-radius: var(--border-radius-circle);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.icon-history:hover, .icon-info:hover {
  background: rgba(255, 255, 255, 0.25);
  transform: translateY(-2px);
}

/* ========== 积分信息卡片 ========== */
.points-info-card {
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.15);
  position: relative;
  z-index: 2;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-normal);
}

.points-info-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.points-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.points-left {
  flex: 1;
}

.points-label {
  font-size: var(--font-size-sm);
  opacity: 0.9;
  display: block;
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.5px;
}

.points-value {
  display: flex;
  align-items: baseline;
}

.points-number {
  font-family: 'Prompt', sans-serif;
  font-size: 3rem;
  font-weight: var(--font-weight-bold);
  margin-right: var(--spacing-xs);
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  background: linear-gradient(135deg, #fff, var(--accent-cream));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.points-unit {
  font-size: var(--font-size-base);
  opacity: 0.9;
  font-weight: var(--font-weight-medium);
}

.points-right {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-sm) var(--spacing-lg);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all var(--transition-normal);
  cursor: pointer;
}

.points-right:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 16px rgba(0, 0, 0, 0.15);
}

.exchange-label {
  color: var(--primary-color);
  font-weight: var(--font-weight-semibold);
  font-size: var(--font-size-sm);
  white-space: nowrap;
}

/* ========== 积分操作按钮 ========== */
.points-actions {
  display: flex;
  justify-content: space-between;
  gap: var(--spacing-sm);
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-xs);
  cursor: pointer;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: all var(--transition-normal);
  flex: 1;
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(255, 255, 255, 0.1);
  position: relative;
  overflow: hidden;
}

.action-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.action-item:hover {
  background: rgba(255, 255, 255, 0.2);
  transform: translateY(-3px);
}

.action-item:hover::before {
  left: 100%;
}

.action-label {
  font-size: var(--font-size-xs);
  opacity: 0.9;
  font-weight: var(--font-weight-medium);
}

.action-icon {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: var(--border-radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
}

.action-item:hover .action-icon {
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

.action-icon .iconfont {
  color: var(--primary-color);
  font-size: var(--font-size-lg);
}

/* ========== 搜索框 ========== */
.search-section {
  background: var(--surface-color);
  margin: calc(var(--spacing-lg) * -1) var(--spacing-lg) var(--spacing-lg);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  position: relative;
  z-index: 3;
  border: 1px solid var(--border-color);
}

.search-box {
  display: flex;
  align-items: center;
  background: white;
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-sm) var(--spacing-lg);
  transition: all var(--transition-normal);
  border: 2px solid transparent;
}

.search-box:focus-within {
  border-color: var(--primary-color);
  box-shadow: var(--input-focus-shadow);
}

.search-box .iconfont {
  color: var(--primary-color);
  margin-right: var(--spacing-sm);
  font-size: var(--font-size-base);
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: var(--font-size-base);
  color: var(--text-color-dark);
  outline: none;
  font-family: var(--font-family-body);
}

.search-box input::placeholder {
  color: var(--text-color-light);
  font-weight: var(--font-weight-normal);
}

/* ========== 分类标签 ========== */
.categories-section {
  background: var(--surface-color);
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--border-color);
  position: relative;
  z-index: 1;
}

.categories-scroll {
  display: flex;
  gap: var(--spacing-sm);
  overflow-x: auto;
  white-space: nowrap;
  padding-bottom: var(--spacing-xs);
  scrollbar-width: thin;
  scrollbar-color: var(--primary-light) var(--border-color);
}

.categories-scroll::-webkit-scrollbar {
  height: 4px;
}

.categories-scroll::-webkit-scrollbar-track {
  background: var(--border-color);
  border-radius: var(--border-radius-sm);
}

.categories-scroll::-webkit-scrollbar-thumb {
  background: var(--primary-light);
  border-radius: var(--border-radius-sm);
}

.category-item {
  padding: var(--spacing-xs) var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  font-size: var(--font-size-sm);
  cursor: pointer;
  flex-shrink: 0;
  background: white;
  color: var(--text-color-medium);
  border: 2px solid var(--border-color);
  transition: all var(--transition-normal);
  font-weight: var(--font-weight-medium);
  position: relative;
  overflow: hidden;
}

.category-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(160, 82, 45, 0.1), transparent);
  opacity: 0;
  transition: opacity var(--transition-normal);
}

.category-item:hover {
  border-color: var(--primary-color);
  transform: translateY(-2px);
  box-shadow: var(--shadow-sm);
}

.category-item:hover::before {
  opacity: 1;
}

.category-item.active {
  background: var(--gradient-primary);
  color: white;
  font-weight: var(--font-weight-semibold);
  box-shadow: var(--shadow-md);
  border-color: var(--primary-color);
}

.category-item.active:hover {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

/* ========== 排序选项 ========== */
.sort-section {
  background: white;
  padding: var(--spacing-md) var(--spacing-lg);
  border-top: 1px solid var(--border-color);
  position: relative;
  z-index: 1;
}

.sort-options {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.sort-item {
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-lg);
  font-size: var(--font-size-xs);
  cursor: pointer;
  background: var(--surface-color);
  color: var(--text-color-medium);
  border: 1px solid var(--border-color);
  transition: all var(--transition-normal);
  font-weight: var(--font-weight-medium);
}

.sort-item:hover {
  border-color: var(--primary-color);
  transform: translateY(-1px);
  box-shadow: var(--shadow-sm);
}

.sort-item.active {
  background: var(--primary-color);
  color: white;
  font-weight: var(--font-weight-semibold);
  border-color: var(--primary-color);
  box-shadow: var(--shadow-sm);
}

/* ========== 商品列表 ========== */
.products-section {
  padding: var(--spacing-lg);
  position: relative;
  z-index: 1;
}

.loading-state {
  text-align: center;
  padding: var(--spacing-xxl) var(--spacing-lg);
}

.loading-state .iconfont {
  font-size: var(--loading-size-lg);
  color: var(--primary-color);
  animation: spin 1s linear infinite;
  margin-bottom: var(--spacing-lg);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  color: var(--text-color-medium);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
}

/* ========== 商品网格 ========== */
.products-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(160px, 1fr));
  gap: var(--spacing-md);
}

.product-card {
  background: white;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  position: relative;
  transition: all var(--transition-normal);
  cursor: pointer;
  border: 1px solid var(--border-color);
}

.product-card:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-color);
}

/* 热门标签 */
.hot-tag {
  position: absolute;
  top: var(--spacing-sm);
  left: var(--spacing-sm);
  background: linear-gradient(135deg, #FF6B6B, var(--primary-color));
  color: white;
  font-size: var(--font-size-xs);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  z-index: 2;
  font-weight: var(--font-weight-semibold);
  box-shadow: 0 2px 8px rgba(255, 107, 107, 0.3);
}

.hot-tag .iconfont {
  font-size: var(--font-size-xs);
}

/* 商品图片 */
.product-image {
  width: 100%;
  height: 160px;
  overflow: hidden;
  position: relative;
}

.product-image::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(160, 82, 45, 0.1), transparent);
  opacity: 0;
  transition: opacity var(--transition-normal);
  z-index: 1;
}

.product-card:hover .product-image::before {
  opacity: 1;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform var(--transition-normal);
}

.product-card:hover .product-image img {
  transform: scale(1.05);
}

/* 商品信息 */
.product-info {
  padding: var(--spacing-md);
}

.product-name {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-xs);
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: var(--font-size-xs);
  color: var(--text-color-medium);
  margin-bottom: var(--spacing-md);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: var(--line-height-normal);
  min-height: 2.8em;
}

/* 积分信息 */
.product-points {
  margin-bottom: var(--spacing-md);
}

.points-display {
  display: flex;
  align-items: baseline;
  margin-bottom: var(--spacing-xs);
}

.current-points {
  font-family: 'Prompt', sans-serif;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  color: var(--primary-color);
  margin-right: var(--spacing-xs);
  text-shadow: 0 2px 4px rgba(160, 82, 45, 0.1);
}

.points-unit {
  font-size: var(--font-size-sm);
  color: var(--primary-color);
  margin-right: var(--spacing-md);
  font-weight: var(--font-weight-medium);
}

.original-points {
  font-size: var(--font-size-xs);
  color: var(--text-color-light);
  text-decoration: line-through;
  font-weight: var(--font-weight-normal);
}

.stock-info {
  display: flex;
  justify-content: space-between;
  font-size: var(--font-size-xs);
  color: var(--text-color-medium);
}

.limit-info {
  color: #FF6B6B;
  font-weight: var(--font-weight-medium);
}

/* 兑换按钮 */
.exchange-btn {
  width: 100%;
  background: var(--gradient-primary);
  color: white;
  border: none;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  font-weight: var(--font-weight-semibold);
  cursor: pointer;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.exchange-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.exchange-btn:hover {
  opacity: 0.95;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.3);
}

.exchange-btn:hover::before {
  left: 100%;
}

.exchange-btn:active {
  transform: translateY(0);
}

/* ========== 空状态 ========== */
.empty-state {
  text-align: center;
  padding: var(--spacing-xxl) var(--spacing-lg);
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-color);
  margin: var(--spacing-lg) 0;
}

.empty-state .iconfont {
  font-size: 4rem;
  color: var(--border-color);
  margin-bottom: var(--spacing-lg);
  opacity: 0.5;
}

.empty-text {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-lg);
  color: var(--text-color-medium);
  margin-bottom: var(--spacing-xs);
  font-weight: var(--font-weight-semibold);
}

.empty-hint {
  font-size: var(--font-size-sm);
  color: var(--text-color-light);
  font-weight: var(--font-weight-normal);
}

/* ========== 底部提示 ========== */
.footer-hint {
  padding: var(--spacing-lg);
  text-align: center;
  color: var(--text-color-light);
  font-size: var(--font-size-xs);
  background: var(--surface-color);
  border-top: 1px solid var(--border-color);
  margin-top: var(--spacing-lg);
  position: relative;
  z-index: 1;
}

.footer-hint p {
  margin: 0;
  line-height: var(--line-height-normal);
  max-width: 600px;
  margin: 0 auto;
}

/* ========== 响应式设计 ========== */
@media (max-width: 480px) {
  .products-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .product-image {
    height: 140px;
  }

  .points-number {
    font-size: 2.5rem;
  }
}

@media (max-width: 375px) {
  .products-grid {
    grid-template-columns: 1fr;
  }

  .product-image {
    height: 180px;
  }

  .points-actions {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .action-item {
    flex-direction: row;
    justify-content: flex-start;
    padding: var(--spacing-sm) var(--spacing-md);
  }

  .action-icon {
    margin-right: var(--spacing-sm);
  }
}

@media (min-width: 769px) {
  .products-grid {
    grid-template-columns: repeat(3, 1fr);
    gap: var(--spacing-lg);
  }

  .product-image {
    height: 180px;
  }

  .points-info-card {
    max-width: 600px;
    margin: 0 auto;
  }

  .search-section {
    max-width: 600px;
    margin: calc(var(--spacing-lg) * -1) auto var(--spacing-lg);
  }
}

/* 动画效果增强 */
@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.product-card:hover .hot-tag {
  animation: float 0.6s ease-in-out;
}

/* 奶茶主题装饰元素 */
.product-card::after {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  width: 40px;
  height: 40px;
  background: radial-gradient(circle, var(--accent-cream) 0%, transparent 70%);
  opacity: 0;
  transition: opacity var(--transition-normal);
  border-radius: var(--border-radius-circle);
}

.product-card:hover::after {
  opacity: 0.3;
}
</style>
