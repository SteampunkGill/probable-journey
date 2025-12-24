<template>
  <div class="favorite-page">
    <!-- 顶部操作栏 -->
    <div class="top-bar" v-if="favoriteList.length > 0">
      <span class="count-text">共{{ favoriteList.length }}件收藏</span>
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
        @click="onProductTap(item.id)"
      >
        <!-- 左侧图片 -->
        <img class="product-image" :src="item.image" />
        
        <!-- 中间信息 -->
        <div class="product-info">
          <h3 class="product-name">{{ item.name }}</h3>
          <p class="product-desc" v-if="item.description">{{ item.description }}</p>
          <div class="product-footer">
            <span class="product-price">¥{{ item.price }}</span>
            <span class="product-sales" v-if="item.sales">已售{{ item.sales }}</span>
          </div>
        </div>
        
        <!-- 右侧操作 -->
        <div class="product-actions">
          <div 
            class="remove-icon" 
            v-if="isEditMode"
            @click.stop="removeFavorite(item.id)"
          >🗑️</div>
          <div 
            class="add-icon" 
            v-if="!isEditMode"
            @click.stop="addToCart(item.id)"
          >➕</div>
        </div>
      </div>
    </div>

    <!-- 空状态 -->
    <div class="empty-state" v-if="favoriteList.length === 0 && !loading">
      <div class="empty-icon">❤️</div>
      <p class="empty-text">还没有收藏任何商品哦~</p>
      <button class="go-shopping-btn" @click="goToOrder">去逛逛</button>
    </div>

    <!-- 加载状态 -->
    <div class="loading-state" v-if="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const favoriteList = ref([])
const loading = ref(false)
const isEditMode = ref(false)

onMounted(() => {
  loadFavorites()
})

const loadFavorites = () => {
  loading.value = true
  // 模拟从 localStorage 获取
  const favorites = JSON.parse(localStorage.getItem('favorites') || '[]')
  
  // 如果为空，添加一些模拟数据
  if (favorites.length === 0) {
    favoriteList.value = [
      {
        id: 'p001',
        name: '经典珍珠奶茶',
        description: 'Q弹珍珠，浓郁奶香',
        price: 18.00,
        sales: 1200,
        image: 'https://images.unsplash.com/photo-1567095761054-7a02e69e5c43?w=400'
      },
      {
        id: 'p002',
        name: '芝士奶盖红茶',
        description: '咸甜奶盖，清爽红茶',
        price: 22.00,
        sales: 850,
        image: 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=400'
      }
    ]
  } else {
    favoriteList.value = favorites
  }
  
  loading.value = false
}

const toggleEditMode = () => {
  isEditMode.value = !isEditMode.value
}

const removeFavorite = (id) => {
  if (confirm('确定要取消收藏吗？')) {
    favoriteList.value = favoriteList.value.filter(item => item.id !== id)
    localStorage.setItem('favorites', JSON.stringify(favoriteList.value))
    alert('已取消收藏')
  }
}

const onProductTap = (id) => {
  if (isEditMode.value) return
  router.push(`/product/${id}`)
}

const addToCart = (id) => {
  router.push(`/product/${id}`)
}

const clearAll = () => {
  if (confirm('确定要清空所有收藏吗？')) {
    favoriteList.value = []
    localStorage.setItem('favorites', '[]')
    isEditMode.value = false
    alert('已清空')
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