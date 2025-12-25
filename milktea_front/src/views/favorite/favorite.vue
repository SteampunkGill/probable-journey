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
  background: #F8F8F8;
  padding-bottom: 20px;
}

.top-bar {
  background: white;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.count-text {
  font-size: 14px;
  color: #666;
}

.actions {
  display: flex;
  gap: 15px;
}

.action-btn {
  font-size: 14px;
  color: #D4A574;
  cursor: pointer;
}

.favorite-list {
  padding: 15px;
}

.favorite-item {
  background: white;
  border-radius: 12px;
  padding: 12px;
  display: flex;
  gap: 12px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.02);
  cursor: pointer;
}

.product-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  object-fit: cover;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 15px;
  font-weight: bold;
  color: #333;
}

.product-desc {
  font-size: 12px;
  color: #999;
  margin-top: 4px;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.product-price {
  font-size: 16px;
  font-weight: bold;
  color: #D4A574;
}

.product-sales {
  font-size: 11px;
  color: #CCC;
}

.product-actions {
  display: flex;
  align-items: center;
  padding-left: 10px;
}

.remove-icon {
  font-size: 20px;
  color: #FF4D4F;
}

.add-icon {
  font-size: 20px;
  color: #D4A574;
}

.load-more, .no-more {
  text-align: center;
  padding: 20px;
  color: #999;
  font-size: 14px;
}

.empty-state {
  padding-top: 100px;
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  opacity: 0.2;
}

.go-shopping-btn {
  margin-top: 20px;
  background: #D4A574;
  color: white;
  border: none;
  padding: 10px 30px;
  border-radius: 22px;
  font-size: 15px;
  cursor: pointer;
}

.loading-state {
  padding-top: 100px;
  text-align: center;
  color: #999;
}

.loading-spinner {
  width: 30px;
  height: 30px;
  border: 3px solid #EEE;
  border-top: 3px solid #D4A574;
  border-radius: 50%;
  margin: 0 auto 15px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style>