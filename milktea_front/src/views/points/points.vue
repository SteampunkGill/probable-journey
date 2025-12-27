<template>
  <div class="points-page">
    <!-- 积分卡片 -->
    <div class="points-card">
      <div class="card-bg"></div>
      <div class="card-content">
        <div class="points-info">
          <span class="points-label">我的积分</span>
          <h1 class="points-value">{{ userPoints }}</h1>
        </div>
        <div class="card-actions">
          <div class="action-btn" @click="toggleRecords">
            <span class="action-icon">📜</span>
            <span>兑换记录</span>
          </div>
          <div class="action-btn" @click="goToRules">
            <span class="action-icon">ℹ️</span>
            <span>积分规则</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 分类标签 -->
    <div class="category-tabs">
      <div 
        class="tab-item"
        :class="{ active: activeCategoryId === item.id }"
        v-for="item in categories"
        :key="item.id"
        @click="activeCategoryId = item.id"
      >
        {{ item.name }}
      </div>
    </div>

    <!-- 商品列表 -->
    <div class="product-list">
      <div 
        class="product-item"
        v-for="item in filteredProducts"
        :key="item.id"
      >
        <img class="product-image" :src="item.image" />
        <div class="product-info">
          <h3 class="product-name">{{ item.name }}</h3>
          <p class="product-desc">{{ item.description }}</p>
          <div class="product-footer">
            <div class="points-price">
              <span class="points-num">{{ item.points }}</span>
              <span class="points-unit">积分</span>
            </div>
            <button 
              class="exchange-btn" 
              @click="exchangeProduct(item)" 
              :disabled="userPoints < item.points || item.stock <= 0"
            >
              {{ item.stock > 0 ? '立即兑换' : '已兑完' }}
            </button>
          </div>
          <div class="stock-info" v-if="item.stock <= 10 && item.stock > 0">
            <span>仅剩{{ item.stock }}件</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-tip" v-if="filteredProducts.length === 0 && !loading">
      <div class="empty-icon">🎁</div>
      <p>暂无可兑换商品</p>
    </div>

    <!-- 兑换记录弹窗 -->
    <div class="records-modal" v-if="showRecords">
      <div class="modal-mask" @click="showRecords = false"></div>
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="modal-title">兑换记录</h3>
          <span class="close-icon" @click="showRecords = false">✕</span>
        </div>
        <div class="modal-body">
          <div class="empty-records">
            <div class="empty-icon">📜</div>
            <p>暂无兑换记录</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, watch } from 'vue'
import { pointsApi, userApi, authApi } from '../../utils/api'

const userPoints = ref(0)
const activeCategoryId = ref('all')
const showRecords = ref(false)
const loading = ref(false)
const exchangeRecords = ref([])

const categories = ref([
  { id: 'all', name: '全部' }
])

const productList = ref([])

const filteredProducts = computed(() => productList.value)

const fetchProducts = async () => {
  loading.value = true
  try {
    // 转换 ID 为后端需要的 type
    const category = activeCategoryId.value === 'all' ? null : activeCategoryId.value.toUpperCase()
    const res = await pointsApi.getPointsProducts(1, 50, category)
    if (res.code === 200) {
      productList.value = res.data.list || res.data || []
    }
  } catch (error) {
    console.error('获取商品列表失败:', error)
  } finally {
    loading.value = false
  }
}

watch(activeCategoryId, () => {
  fetchProducts()
})

const loadData = async () => {
  loading.value = true
  try {
    const [profileRes, categoriesRes] = await Promise.all([
      authApi.getUserProfile(),
      pointsApi.getPointsCategories()
    ])
    
    if (profileRes.code === 200) {
      userPoints.value = profileRes.data.points || 0
    }

    if (categoriesRes.code === 200) {
      // 保留“全部”选项，合并后端分类
      const backendCategories = categoriesRes.data || []
      categories.value = [
        { id: 'all', name: '全部' },
        ...backendCategories.map(c => ({
          id: c.type.toLowerCase(), // 使用 type 作为 ID 匹配前端逻辑
          name: c.name
        }))
      ]
    }
    
    await fetchProducts()
  } catch (error) {
    console.error('加载积分商城数据失败:', error)
  } finally {
    loading.value = false
  }
}

const toggleRecords = async () => {
  showRecords.value = !showRecords.value
  if (showRecords.value) {
    try {
      const res = await pointsApi.getExchangeRecords()
      if (res.code === 200) {
        exchangeRecords.value = res.data || []
      }
    } catch (error) {
      console.error('加载兑换记录失败:', error)
    }
  }
}

const goToRules = async () => {
  try {
    const res = await userApi.getPointsRules()
    if (res.code === 200) {
      alert(`积分规则：\n${res.data.rules}`)
    }
  } catch (error) {
    console.error('获取积分规则失败:', error)
  }
}

const exchangeProduct = async (product) => {
  if (userPoints.value < product.points) {
    alert('积分不足')
    return
  }
  
  if (confirm(`确定花费${product.points}积分兑换${product.name}吗？`)) {
    try {
      const res = await pointsApi.exchangeProduct({ productId: product.id })
      if (res.code === 200) {
        userPoints.value -= product.points
        product.stock -= 1
        alert('兑换成功！')
      } else {
        alert(res.message || '兑换失败')
      }
    } catch (error) {
      console.error('兑换失败:', error)
      alert('兑换失败，请稍后重试')
    }
  }
}

onMounted(() => {
  loadData()
})
</script>
<style scoped>
/* ============================================
“饮饮茶(SipSipTea)” 积分页面样式优化
基于奶茶主题设计指南
============================================ */

/* ========== 页面容器 ========== */
.points-page {
  min-height: 100vh;
  background: var(--background-color);
  position: relative;
  overflow-x: hidden;
  padding-bottom: var(--spacing-xl);
}

/* 奶茶主题背景装饰 */
.points-page::before {
  content: '';
  position: absolute;
  top: -15%;
  right: -8%;
  width: 150px;
  height: 150px;
  background: radial-gradient(circle, var(--accent-cream) 0%, transparent 70%);
  opacity: 0.2;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

.points-page::after {
  content: '';
  position: absolute;
  bottom: -10%;
  left: -4%;
  width: 100px;
  height: 100px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.15;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

/* ========== 积分卡片 ========== */
.points-card {
  height: 200px;
  position: relative;
  margin: var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  overflow: hidden;
  color: white;
  box-shadow: var(--shadow-lg);
  transition: all var(--transition-normal);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.points-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 40px rgba(160, 82, 45, 0.3);
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--gradient-primary);
}

/* 卡片装饰纹理 */
.card-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--texture-paper);
  opacity: 0.1;
  pointer-events: none;
}

.card-content {
  position: relative;
  height: 100%;
  padding: var(--spacing-xl);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  z-index: 1;
}

.points-info {
  animation: fadeInUp 0.6s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.points-label {
  font-size: var(--font-size-sm);
  opacity: 0.9;
  font-weight: var(--font-weight-medium);
  letter-spacing: 0.5px;
  display: block;
  margin-bottom: var(--spacing-xs);
}

.points-value {
  font-family: 'Prompt', sans-serif;
  font-size: 3.5rem;
  font-weight: var(--font-weight-bold);
  margin-top: var(--spacing-xs);
  text-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
  background: linear-gradient(135deg, #fff, var(--accent-cream));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.card-actions {
  display: flex;
  gap: var(--spacing-md);
  animation: fadeInUp 0.6s ease-out 0.2s both;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: var(--font-size-sm);
  background: rgba(255, 255, 255, 0.2);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  transition: all var(--transition-normal);
  font-weight: var(--font-weight-medium);
  backdrop-filter: blur(5px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.3);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.action-btn:hover::before {
  left: 100%;
}

.action-icon {
  font-size: var(--font-size-base);
  transition: transform var(--transition-normal);
}

.action-btn:hover .action-icon {
  transform: scale(1.1);
}

/* ========== 分类标签 ========== */
.category-tabs {
  display: flex;
  background: var(--surface-color);
  padding: var(--spacing-md) var(--spacing-lg);
  gap: var(--spacing-lg);
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid var(--border-color);
  overflow-x: auto;
  scrollbar-width: none;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.tab-item {
  font-size: var(--font-size-sm);
  color: var(--text-color-medium);
  padding: var(--spacing-xs) 0;
  position: relative;
  cursor: pointer;
  font-weight: var(--font-weight-medium);
  white-space: nowrap;
  transition: all var(--transition-normal);
  flex-shrink: 0;
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
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--gradient-primary);
  border-radius: var(--border-radius-sm);
  animation: slideIn 0.3s ease-out;
}

@keyframes slideIn {
  from {
    transform: scaleX(0);
  }
  to {
    transform: scaleX(1);
  }
}

/* ========== 商品列表 ========== */
.product-list {
  padding: var(--spacing-lg);
}

.product-item {
  background: white;
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-md);
  display: flex;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-normal);
  border: 1px solid var(--border-color);
  position: relative;
}

.product-item:hover {
  transform: translateY(-6px);
  box-shadow: var(--shadow-lg);
  border-color: var(--primary-color);
}

/* 奶茶主题装饰 */
.product-item::before {
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
  z-index: 1;
}

.product-item:hover::before {
  opacity: 0.3;
}

.product-image {
  width: 140px;
  height: 140px;
  object-fit: cover;
  transition: transform var(--transition-normal);
  position: relative;
}

.product-item:hover .product-image {
  transform: scale(1.05);
}

.product-image::after {
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

.product-item:hover .product-image::after {
  opacity: 1;
}

.product-info {
  flex: 1;
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  position: relative;
  z-index: 2;
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
  margin-top: var(--spacing-xs);
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: var(--line-height-normal);
  min-height: 2.8em;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: var(--spacing-md);
}

.points-price {
  display: flex;
  align-items: baseline;
  color: var(--primary-color);
}

.points-num {
  font-family: 'Prompt', sans-serif;
  font-size: var(--font-size-xl);
  font-weight: var(--font-weight-bold);
  text-shadow: 0 2px 4px rgba(160, 82, 45, 0.1);
}

.points-unit {
  font-size: var(--font-size-sm);
  margin-left: var(--spacing-xs);
  font-weight: var(--font-weight-medium);
}

.exchange-btn {
  background: var(--gradient-primary);
  color: white;
  border: none;
  padding: var(--spacing-xs) var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  font-size: var(--font-size-sm);
  cursor: pointer;
  transition: all var(--transition-normal);
  font-weight: var(--font-weight-semibold);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
  min-width: 100px;
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

.exchange-btn:disabled {
  background: var(--border-color);
  color: var(--text-color-light);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.exchange-btn:disabled:hover::before {
  left: -100%;
}

.stock-info {
  font-size: var(--font-size-xs);
  color: #FF6B6B;
  margin-top: var(--spacing-xs);
  font-weight: var(--font-weight-medium);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.7;
  }
}

/* ========== 空状态 ========== */
.empty-tip {
  padding-top: var(--spacing-xxl);
  text-align: center;
  color: var(--text-color-light);
  position: relative;
  z-index: 1;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: var(--spacing-lg);
  opacity: 0.2;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

.empty-tip p {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-color-medium);
}

/* ========== 兑换记录弹窗 ========== */
.records-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
  display: flex;
  align-items: flex-end;
  justify-content: center;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(3px);
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.modal-content {
  position: relative;
  background: var(--surface-color);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
  width: 100%;
  max-height: 70vh;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  border: 1px solid var(--border-color);
  border-bottom: none;
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

.modal-header {
  padding: var(--spacing-lg);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
  background: white;
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}

.modal-title {
  font-family: var(--font-family-heading);
  font-size: var(--font-size-lg);
  font-weight: var(--font-weight-semibold);
  color: var(--text-color-dark);
}

.close-icon {
  font-size: var(--font-size-lg);
  color: var(--text-color-light);
  cursor: pointer;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: var(--border-radius-circle);
  transition: all var(--transition-normal);
  background: rgba(0, 0, 0, 0.05);
}

.close-icon:hover {
  background: rgba(0, 0, 0, 0.1);
  color: var(--text-color-dark);
  transform: rotate(90deg);
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-lg);
}

.empty-records {
  padding-top: var(--spacing-xxl);
  text-align: center;
  color: var(--text-color-light);
}

.empty-records .empty-icon {
  font-size: 3rem;
  margin-bottom: var(--spacing-md);
}

.empty-records p {
  font-size: var(--font-size-base);
  font-weight: var(--font-weight-medium);
  color: var(--text-color-medium);
}

/* ========== 响应式设计 ========== */
@media (max-width: 480px) {
  .points-card {
    height: 180px;
    margin: var(--spacing-md);
  }

  .points-value {
    font-size: 3rem;
  }

  .product-item {
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 160px;
  }

  .card-actions {
    flex-wrap: wrap;
    gap: var(--spacing-sm);
  }

  .action-btn {
    flex: 1;
    min-width: 100px;
    justify-content: center;
  }
}

@media (min-width: 481px) and (max-width: 768px) {
  .product-list {
    display: grid;
    grid-template-columns: repeat(2, 1fr);
    gap: var(--spacing-md);
  }

  .product-item {
    flex-direction: column;
    margin-bottom: 0;
  }

  .product-image {
    width: 100%;
    height: 140px;
  }
}

@media (min-width: 769px) {
  .product-list {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: var(--spacing-lg);
  }

  .product-item {
    flex-direction: column;
    margin-bottom: 0;
  }

  .product-image {
    width: 100%;
    height: 160px;
  }

  .points-card {
    max-width: 600px;
    margin: var(--spacing-lg) auto;
  }

  .category-tabs {
    max-width: 600px;
    margin: 0 auto;
    border-radius: var(--border-radius-lg);
    margin-top: var(--spacing-md);
  }
}

/* 滚动条样式优化 */
.modal-body::-webkit-scrollbar {
  width: 6px;
}

.modal-body::-webkit-scrollbar-track {
  background: var(--border-color);
  border-radius: var(--border-radius-sm);
}

.modal-body::-webkit-scrollbar-thumb {
  background: var(--primary-light);
  border-radius: var(--border-radius-sm);
}

.modal-body::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}

/* 加载状态 */
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
</style>