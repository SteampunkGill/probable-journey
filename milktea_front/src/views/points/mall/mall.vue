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
.points-mall-page {
  min-height: 100vh;
  background: #FFF9E6;
}

/* 头部样式 */
.mall-header {
  background: linear-gradient(135deg, #FFD166, #FFA940);
  padding: 40px 20px 30px;
  color: white;
}

.header-top {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 15px;
}

.icon-left {
  font-size: 20px;
  cursor: pointer;
}

.header-title {
  font-size: 18px;
  font-weight: bold;
}

.header-right {
  display: flex;
  gap: 20px;
}

.icon-history, .icon-info {
  font-size: 20px;
  cursor: pointer;
}

/* 积分信息卡片 */
.points-info-card {
  background: rgba(255, 255, 255, 0.2);
  border-radius: 15px;
  padding: 20px;
  backdrop-filter: blur(10px);
}

.points-main {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.points-left {
  flex: 1;
}

.points-label {
  font-size: 14px;
  opacity: 0.9;
  display: block;
  margin-bottom: 8px;
}

.points-value {
  display: flex;
  align-items: baseline;
}

.points-number {
  font-size: 36px;
  font-weight: bold;
  margin-right: 8px;
}

.points-unit {
  font-size: 16px;
  opacity: 0.9;
}

.points-right {
  background: white;
  border-radius: 25px;
  padding: 8px 20px;
}

.exchange-label {
  color: #FFA940;
  font-weight: bold;
  font-size: 14px;
}

.points-actions {
  display: flex;
  justify-content: space-between;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
}

.action-label {
  font-size: 12px;
  opacity: 0.9;
}

.action-icon {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.action-icon .iconfont {
  color: #FFA940;
  font-size: 20px;
}

/* 搜索框 */
.search-section {
  background: white;
  margin: -15px 15px 15px;
  padding: 15px;
  border-radius: 15px;
  box-shadow: 0 4px 20px rgba(255, 168, 64, 0.1);
}

.search-box {
  display: flex;
  align-items: center;
  background: #FFF9E6;
  border-radius: 25px;
  padding: 12px 20px;
}

.search-box .iconfont {
  color: #FFA940;
  margin-right: 10px;
  font-size: 18px;
}

.search-box input {
  flex: 1;
  border: none;
  background: transparent;
  font-size: 14px;
  color: #8B7355;
  outline: none;
}

.search-box input::placeholder {
  color: #D4A574;
}

/* 分类标签 */
.categories-section {
  background: white;
  padding: 15px;
  border-top: 1px solid #FFE8A3;
}

.categories-scroll {
  display: flex;
  gap: 15px;
  overflow-x: auto;
  white-space: nowrap;
  padding-bottom: 5px;
}

.category-item {
  padding: 8px 20px;
  border-radius: 25px;
  font-size: 14px;
  cursor: pointer;
  flex-shrink: 0;
  background: #FFF9E6;
  color: #8B7355;
  border: 1px solid #FFE8A3;
}

.category-item.active {
  background: linear-gradient(135deg, #FFD166, #FFA940);
  color: white;
  font-weight: bold;
  box-shadow: 0 4px 12px rgba(255, 168, 64, 0.3);
}

/* 排序选项 */
.sort-section {
  background: white;
  padding: 15px;
  border-top: 1px solid #FFE8A3;
}

.sort-options {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.sort-item {
  padding: 6px 15px;
  border-radius: 20px;
  font-size: 12px;
  cursor: pointer;
  background: #FFFDF0;
  color: #B38B00;
  border: 1px solid #FFE8A3;
}

.sort-item.active {
  background: #FFE8A3;
  color: #8B7355;
  font-weight: bold;
  border: 1px solid #FFD166;
}

/* 商品列表 */
.products-section {
  padding: 15px;
}

.loading-state {
  text-align: center;
  padding: 60px 20px;
}

.loading-state .iconfont {
  font-size: 40px;
  color: #FFA940;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.products-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.product-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(255, 168, 64, 0.1);
  position: relative;
  transition: all 0.3s ease;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 25px rgba(255, 168, 64, 0.2);
}

.hot-tag {
  position: absolute;
  top: 10px;
  left: 10px;
  background: linear-gradient(135deg, #FF6B6B, #FFA940);
  color: white;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 15px;
  display: flex;
  align-items: center;
  gap: 4px;
  z-index: 2;
}

.product-image {
  width: 100%;
  height: 150px;
  overflow: hidden;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-info {
  padding: 15px;
}

.product-name {
  font-size: 16px;
  font-weight: bold;
  color: #8B7355;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-desc {
  font-size: 12px;
  color: #B38B00;
  margin-bottom: 15px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.4;
}

.product-points {
  margin-bottom: 15px;
}

.points-display {
  display: flex;
  align-items: baseline;
  margin-bottom: 8px;
}

.current-points {
  font-size: 24px;
  font-weight: bold;
  color: #FFA940;
  margin-right: 6px;
}

.points-unit {
  font-size: 14px;
  color: #FFA940;
  margin-right: 15px;
}

.original-points {
  font-size: 12px;
  color: #D4A574;
  text-decoration: line-through;
}

.stock-info {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: #8B7355;
}

.limit-info {
  color: #FF6B6B;
}

.exchange-btn {
  width: 100%;
  background: linear-gradient(135deg, #FFD166, #FFA940);
  color: white;
  border: none;
  padding: 10px;
  border-radius: 10px;
  font-size: 14px;
  font-weight: bold;
  cursor: pointer;
  transition: all 0.3s ease;
}

.exchange-btn:hover {
  opacity: 0.9;
  transform: scale(0.98);
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.empty-state .iconfont {
  font-size: 60px;
  color: #FFE8A3;
  margin-bottom: 20px;
}

.empty-text {
  font-size: 18px;
  color: #D4A574;
  margin-bottom: 10px;
}

.empty-hint {
  font-size: 14px;
  color: #B38B00;
}

/* 底部提示 */
.footer-hint {
  padding: 30px 20px;
  text-align: center;
  color: #B38B00;
  font-size: 12px;
}

/* 响应式调整 */
@media (max-width: 375px) {
  .products-grid {
    grid-template-columns: 1fr;
  }
  
  .product-image {
    height: 180px;
  }
}

/* 滚动条样式 */
.categories-scroll::-webkit-scrollbar {
  height: 4px;
}

.categories-scroll::-webkit-scrollbar-track {
  background: #FFE8A3;
  border-radius: 2px;
}

.categories-scroll::-webkit-scrollbar-thumb {
  background: #FFA940;
  border-radius: 2px;
}
</style>