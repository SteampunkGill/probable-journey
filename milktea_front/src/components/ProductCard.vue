<template>
  <div class="product-card" @click="onTap">
    <!-- 商品图片 -->
    <div class="product-image-container">
      <img 
        :src="product.image || product.mainImage" 
        class="product-image"
        loading="lazy"
      />
      
      <!-- 商品标签 -->
      <div class="product-tags">
        <span class="tag tag-hot" v-if="product.isHot">🔥 热销</span>
        <span class="tag tag-new" v-if="product.isNew">✨ 新品</span>
        <span class="tag tag-discount" v-if="product.discount">{{ product.discount }}</span>
      </div>
      
      <!-- 收藏按钮 -->
      <div 
        class="favorite-btn" 
        :class="{ active: isFavorite }"
        @click.stop="onFavoriteTap"
      >
        <img 
          class="favorite-icon" 
          :src="isFavorite ? getIconUrl('heart-fill.png') : getIconUrl('heart.png')"
        />
      </div>
      
      <!-- 售罄遮罩 -->
      <div class="sold-out-mask" v-if="product.stock <= 0">
        <span>已售罄</span>
      </div>
    </div>
    
    <!-- 商品信息 -->
    <div class="product-info">
      <span class="product-name">{{ product.name }}</span>
      
      <div class="product-meta">
        <span class="product-desc" v-if="product.description">{{ product.description }}</span>
      </div>
      
      <!-- 价格和操作 -->
      <div class="product-footer">
        <div class="price-section">
          <span class="current-price">¥{{ product.price }}</span>
          <span class="original-price" v-if="product.originalPrice > product.price">
            ¥{{ product.originalPrice }}
          </span>
        </div>
        
        <div class="action-section">
          <!-- 评分 -->
          <div class="product-rating" v-if="product.rating">
            <img class="star-icon" src="../assets/images/icons/star.png" />
            <span>{{ product.rating }}</span>
          </div>
          
          <!-- 购物车按钮 -->
          <div 
            class="add-to-cart-btn" 
            @click.stop="onAddToCart"
            v-if="product.stock > 0"
          >
            <img class="add-icon" src="../assets/images/icons/add.png" />
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const props = defineProps({
  product: {
    type: Object,
    required: true
  }
})

const emit = defineEmits(['click', 'add-to-cart', 'favorite-change'])

const isFavorite = ref(false)

const onTap = () => {
  emit('click', props.product.id)
}

const onFavoriteTap = () => {
  isFavorite.value = !isFavorite.value
  emit('favorite-change', { id: props.product.id, isFavorite: isFavorite.value })
}

const onAddToCart = () => {
  emit('add-to-cart', props.product)
}

const getIconUrl = (name) => {
  return new URL(`../assets/images/icons/${name}`, import.meta.url).href
}
</script>

<style scoped>
.product-card {
  background: var(--bg-primary);
  border-radius: var(--radius-lg);
  overflow: hidden;
  box-shadow: var(--shadow-sm);
  display: flex;
  flex-direction: column;
  cursor: pointer;
}

.product-image-container {
  position: relative;
  width: 100%;
  padding-top: 100%; /* 1:1 Aspect Ratio */
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-tags {
  position: absolute;
  top: 8px;
  left: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  color: white;
}

.tag-hot { background: #ff4d4f; }
.tag-new { background: #52c41a; }
.tag-discount { background: #faad14; }

.favorite-btn {
  position: absolute;
  top: 8px;
  right: 8px;
  width: 28px;
  height: 28px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.favorite-icon {
  width: 16px;
  height: 16px;
}

.sold-out-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 14px;
}

.product-info {
  padding: 12px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.product-name {
  font-size: 15px;
  font-weight: bold;
  color: var(--text-primary);
}

.product-desc {
  font-size: 12px;
  color: var(--text-tertiary);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: 4px;
}

.current-price {
  font-size: 18px;
  font-weight: bold;
  color: var(--primary-color);
}

.original-price {
  font-size: 12px;
  color: var(--text-tertiary);
  text-decoration: line-through;
  margin-left: 4px;
}

.action-section {
  display: flex;
  align-items: center;
  gap: 8px;
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 2px;
  font-size: 12px;
  color: #faad14;
}

.star-icon {
  width: 12px;
  height: 12px;
}

.add-to-cart-btn {
  width: 28px;
  height: 28px;
  background: var(--primary-color);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.add-icon {
  width: 16px;
  height: 16px;
}
</style>