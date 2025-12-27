<template>
  <div class="favorite-page">
    <!-- 顶部操作栏 -->
    <div class="top-bar" v-if="favoriteList.length > 0 || loading">
      <span class="count-text">共{{ total }}件收藏</span>
      <div class="actions">
        <span class="action-btn" @click="toggleEditMode">
          {{ isEditMode ? '完成' : '编辑' }}
        </span>
        <span class="action-btn" @click="clearAll" v-if="isEditMode">清空</span>
      </div>
    </div>

    <!-- 收藏列表 -->
    <div class="favorite-list" v-if="favoriteList.length > 0">
      <div 
        class="favorite-item"
        v-for="item in favoriteList"
        :key="item.id"
        @click="onProductTap(item.product.id)"
      >
        <!-- 左侧图片 -->
        <img class="product-image" :src="item.product.image || defaultImage" />
        
        <!-- 中间信息 -->
        <div class="product-info">
          <h3 class="product-name">{{ item.product.name }}</h3>
          <p class="product-desc" v-if="item.product.description">{{ item.product.description }}</p>
          <div class="product-footer">
            <span class="product-price">¥{{ item.product.price }}</span>
            <span class="product-sales" v-if="item.product.sales">已售{{ item.product.sales }}</span>
          </div>
        </div>
        
        <!-- 右侧操作 -->
        <div class="product-actions">
          <div 
            class="remove-icon" 
            v-if="isEditMode"
            @click.stop="removeFavorite(item.product.id)"
          >🗑️</div>
          <div 
            class="add-icon" 
            v-if="!isEditMode"
            @click.stop="addToCart(item.product.id)"
          >➕</div>
        </div>
      </div>

      <!-- 加载更多 -->
      <div class="load-more" v-if="hasMore" @click="loadMore">
        {{ loading ? '加载中...' : '点击加载更多' }}
      </div>
      <div class="no-more" v-else-if="favoriteList.length > 0">
        没有更多了
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="favoriteList.length === 0 && !loading">
      <div class="empty-icon">❤️</div>
      <p class="empty-text">还没有收藏任何商品哦~</p>
      <button class="go-shopping-btn" @click="goToOrder">去逛逛</button>
    </div>

    <!-- 加载状态 -->
    <div class="loading-state" v-if="loading && favoriteList.length === 0">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { favoriteApi } from '@/utils/api'

const router = useRouter()

const favoriteList = ref([])
const loading = ref(false)
const isEditMode = ref(false)
const page = ref(1)
const size = ref(10)
const total = ref(0)
const hasMore = ref(false)
const defaultImage = 'https://images.unsplash.com/photo-1567095761054-7a02e69e5c43?w=400'

onMounted(() => {
  loadFavorites()
})

const loadFavorites = async (isLoadMore = false) => {
  if (loading.value) return
  
  loading.value = true
  try {
    const params = {
      page: page.value,
      size: size.value
    }
    const res = await favoriteApi.getFavorites(params)
    if (res && res.content) {
      if (isLoadMore) {
        favoriteList.value = [...favoriteList.value, ...res.content]
      } else {
        favoriteList.value = res.content
      }
      total.value = res.totalElements
      hasMore.value = !res.last
    }
  } catch (error) {
    console.error('加载收藏失败:', error)
    alert('加载收藏失败，请稍后重试')
  } finally {
    loading.value = false
  }
}

const loadMore = () => {
  if (hasMore.value && !loading.value) {
    page.value++
    loadFavorites(true)
  }
}

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

const removeFavorite = async (productId) => {
  if (confirm('确定要取消收藏吗？')) {
    try {
      await favoriteApi.removeFavorite(productId)
      favoriteList.value = favoriteList.value.filter(item => item.product.id !== productId)
      total.value--
      alert('已取消收藏')
    } catch (error) {
      console.error('取消收藏失败:', error)
      alert('操作失败，请稍后重试')
    }
  }
}

const onProductTap = (id) => {
  if (isEditMode.value) return
  router.push(`/product/${id}`)
}

const addToCart = (id) => {
  router.push(`/product/${id}`)
}

const clearAll = async () => {
  if (confirm('确定要清空所有收藏吗？')) {
    try {
      await favoriteApi.clearFavorites()
      favoriteList.value = []
      total.value = 0
      isEditMode.value = false
      alert('已清空')
    } catch (error) {
      console.error('清空收藏失败:', error)
      alert('操作失败，请稍后重试')
    }
  }
}

const goToOrder = () => {
  router.push('/')
}
</script>
<style scoped>
.favorite-page {
  min-height: 100vh;
  background: var(--background-color, #f5f0e1);
  padding-bottom: 40px;
}

/* 顶部操作栏 */
.top-bar {
  background: var(--surface-color, #e8dccb);
  padding: var(--spacing-lg) var(--spacing-xl);
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.08);
  border-bottom: 2px solid var(--border-color, #d4c7b5);
  border-radius: 0 0 var(--border-radius-xl) var(--border-radius-xl);
  backdrop-filter: blur(8px);
}

.count-text {
  font-size: 15px;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  letter-spacing: 0.03em;
}

.actions {
  display: flex;
  gap: var(--spacing-lg);
}

.action-btn {
  font-size: 15px;
  color: var(--primary-color, #a0522d);
  cursor: pointer;
  padding: 8px 16px;
  border-radius: var(--border-radius-lg);
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
  transition: all 0.25s ease-out;
  background: rgba(255, 255, 255, 0.9);
  border: 2px solid transparent;
}

.action-btn:hover {
  background: var(--primary-light, #d2b48c);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
}

.action-btn:active {
  transform: translateY(0);
}

/* 收藏列表 */
.favorite-list {
  padding: var(--spacing-lg);
}

.favorite-item {
  background: white;
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-lg);
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color, #d4c7b5);
  cursor: pointer;
  transition: all 0.25s ease-out;
  position: relative;
  overflow: hidden;
}

.favorite-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 12px 30px rgba(160, 82, 45, 0.15);
  border-color: var(--primary-light, #d2b48c);
}

.favorite-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color, #a0522d), var(--accent-pink, #ffc0cb));
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
  opacity: 0;
  transition: opacity 0.3s ease;
}

.favorite-item:hover::before {
  opacity: 1;
}

.product-image {
  width: 100px;
  height: 100px;
  border-radius: var(--border-radius-lg);
  object-fit: cover;
  border: 3px solid var(--accent-cream, #fff8dc);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
  transition: all 0.3s ease;
}

.favorite-item:hover .product-image {
  transform: scale(1.05);
  border-color: var(--primary-light, #d2b48c);
  box-shadow: 0 6px 18px rgba(160, 82, 45, 0.2);
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  min-height: 100px;
}

.product-name {
  font-size: 17px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  line-height: 1.4;
  margin-bottom: var(--spacing-sm);
  letter-spacing: 0.03em;
}

.product-desc {
  font-size: 14px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
  line-height: 1.5;
  margin-bottom: var(--spacing-md);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: auto;
}

.product-price {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color, #a0522d);
  font-family: 'Prompt', serif;
  letter-spacing: 0.03em;
}

.product-sales {
  font-size: 13px;
  color: var(--accent-brown, #deb887);
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  padding: 4px 12px;
  background: rgba(222, 184, 135, 0.1);
  border-radius: var(--border-radius-md);
  border: 1px solid rgba(222, 184, 135, 0.2);
}

.product-actions {
  display: flex;
  align-items: center;
  padding-left: var(--spacing-md);
}

.remove-icon {
  font-size: 22px;
  color: #ff6b6b;
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  background: rgba(255, 107, 107, 0.1);
  border: 2px solid rgba(255, 107, 107, 0.2);
  transition: all 0.25s ease-out;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.remove-icon:hover {
  background: rgba(255, 107, 107, 0.2);
  border-color: #ff6b6b;
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.add-icon {
  font-size: 22px;
  color: var(--primary-color, #a0522d);
  cursor: pointer;
  padding: 8px;
  border-radius: 50%;
  background: rgba(160, 82, 45, 0.1);
  border: 2px solid rgba(160, 82, 45, 0.2);
  transition: all 0.25s ease-out;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-icon:hover {
  background: rgba(160, 82, 45, 0.2);
  border-color: var(--primary-color, #a0522d);
  transform: scale(1.1);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

/* 加载更多 */
.load-more, .no-more {
  text-align: center;
  padding: var(--spacing-xl);
  color: var(--text-color-medium, #7a6a5b);
  font-size: 15px;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
}

.load-more {
  cursor: pointer;
  background: var(--surface-color, #e8dccb);
  border-radius: var(--border-radius-xl);
  margin: var(--spacing-lg);
  border: 2px solid var(--border-color, #d4c7b5);
  transition: all 0.25s ease-out;
}

.load-more:hover {
  background: var(--primary-light, #d2b48c);
  color: white;
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.15);
  border-color: var(--primary-color, #a0522d);
}

.no-more {
  opacity: 0.7;
  font-style: italic;
}

/* 空状态 */
.empty-state {
  padding-top: 120px;
  text-align: center;
  color: var(--text-color-medium, #7a6a5b);
}

.empty-icon {
  font-size: 80px;
  margin-bottom: var(--spacing-lg);
  opacity: 0.3;
  filter: drop-shadow(0 4px 8px rgba(160, 82, 45, 0.1));
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.empty-text {
  font-size: 18px;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', serif;
  margin-bottom: var(--spacing-xl);
  letter-spacing: 0.03em;
}

.go-shopping-btn {
  margin-top: var(--spacing-lg);
  background: linear-gradient(135deg, var(--primary-color, #a0522d), var(--primary-dark, #8b4513));
  color: white;
  border: none;
  padding: 14px 40px;
  border-radius: var(--border-radius-xl);
  font-size: 16px;
  cursor: pointer;
  font-family: 'Prompt', serif;
  font-weight: 600;
  letter-spacing: 0.05em;
  transition: all 0.25s ease-out;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.go-shopping-btn:hover {
  transform: translateY(-3px) scale(1.03);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.4);
}

.go-shopping-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 加载状态 */
.loading-state {
  padding-top: 120px;
  text-align: center;
  color: var(--text-color-medium, #7a6a5b);
}

.loading-spinner {
  width: 50px;
  height: 50px;
  border: 4px solid var(--border-color, #d4c7b5);
  border-top: 4px solid var(--primary-color, #a0522d);
  border-radius: 50%;
  margin: 0 auto var(--spacing-lg);
  animation: spin 1s linear infinite;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

.loading-state p {
  font-size: 15px;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  letter-spacing: 0.03em;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .favorite-page {
    padding-bottom: 20px;
  }

  .top-bar {
    padding: var(--spacing-md);
    border-radius: 0 0 var(--border-radius-lg) var(--border-radius-lg);
  }

  .favorite-list {
    padding: var(--spacing-md);
  }

  .favorite-item {
    padding: var(--spacing-md);
    gap: var(--spacing-md);
    margin-bottom: var(--spacing-md);
    flex-direction: column;
  }

  .product-image {
    width: 100%;
    height: 180px;
    border-radius: var(--border-radius-lg);
  }

  .product-info {
    min-height: auto;
  }

  .product-actions {
    padding-left: 0;
    justify-content: flex-end;
    margin-top: var(--spacing-md);
  }

  .empty-state {
    padding-top: 80px;
  }

  .empty-icon {
    font-size: 60px;
  }

  .empty-text {
    font-size: 16px;
  }

  .go-shopping-btn {
    padding: 12px 32px;
    font-size: 15px;
  }
}
</style>