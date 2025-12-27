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
/* 饮饮茶 (SipSipTea) - 奶茶主题商品卡片样式 */

.product-card {
  background: linear-gradient(135deg, var(--surface-color) 0%, rgba(var(--surface-color-rgb), 0.9) 100%);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.05);
  display: flex;
  flex-direction: column;
  cursor: pointer;
  border: 2px solid var(--border-color);
  backdrop-filter: blur(3px);
  transition: all 0.3s ease-out;
  position: relative;
}

.product-card:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 8px 25px rgba(var(--primary-color-rgb), 0.1);
  border-color: var(--primary-light);
}

.product-card:active {
  transform: translateY(-2px) scale(1.01);
}

/* 商品图片容器 */
.product-image-container {
  position: relative;
  width: 100%;
  padding-top: 100%;
  overflow: hidden;
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

.product-image {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  object-fit: cover;
  filter: saturate(0.9) contrast(1.05);
  transition: transform 0.5s ease-out;
}

.product-card:hover .product-image {
  transform: scale(1.05);
}

/* 商品标签 */
.product-tags {
  position: absolute;
  top: var(--spacing-sm);
  left: var(--spacing-sm);
  display: flex;
  flex-direction: column;
  gap: 6px;
  z-index: 2;
}

.tag {
  font-size: 11px;
  padding: 4px 10px;
  border-radius: var(--border-radius-sm);
  color: var(--accent-cream);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.03em;
  font-weight: 600;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  backdrop-filter: blur(2px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  animation: tagFloat 3s ease-in-out infinite;
}

.tag:nth-child(2) {
  animation-delay: 0.5s;
}

.tag:nth-child(3) {
  animation-delay: 1s;
}

.tag-hot {
  background: linear-gradient(135deg, #ff6b6b 0%, #d63031 100%);
}

.tag-new {
  background: linear-gradient(135deg, var(--accent-pink) 0%, #ff6b9d 100%);
}

.tag-discount {
  background: linear-gradient(135deg, var(--accent-brown) 0%, #c19a3b 100%);
}

/* 收藏按钮 */
.favorite-btn {
  position: absolute;
  top: var(--spacing-sm);
  right: var(--spacing-sm);
  width: 36px;
  height: 36px;
  background: rgba(var(--surface-color-rgb), 0.85);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  backdrop-filter: blur(4px);
  border: 2px solid rgba(var(--primary-color-rgb), 0.15);
  transition: all 0.3s ease-out;
  z-index: 2;
  cursor: pointer;
}

.favorite-btn:hover {
  transform: scale(1.1);
  border-color: rgba(var(--primary-color-rgb), 0.3);
  background: rgba(var(--surface-color-rgb), 0.95);
}

.favorite-btn.active {
  background: rgba(255, 107, 107, 0.15);
  border-color: rgba(255, 107, 107, 0.3);
}

.favorite-btn.active:hover {
  background: rgba(255, 107, 107, 0.25);
}

.favorite-icon {
  width: 20px;
  height: 20px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
  transition: all 0.3s ease-out;
}

.favorite-btn.active .favorite-icon {
  filter: drop-shadow(0 2px 4px rgba(255, 107, 107, 0.3));
}

/* 售罄遮罩 */
.sold-out-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(58, 58, 58, 0.6) 0%, rgba(var(--primary-color-rgb), 0.4) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--accent-cream);
  font-size: 16px;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  font-weight: 600;
  backdrop-filter: blur(2px);
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
  z-index: 1;
}

.sold-out-mask span {
  background: rgba(0, 0, 0, 0.3);
  padding: 8px 16px;
  border-radius: var(--border-radius-sm);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

/* 商品信息 */
.product-info {
  padding: var(--spacing-md);
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  flex: 1;
}

.product-name {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-dark);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-desc {
  font-size: 13px;
  color: var(--text-color-medium);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.8;
  letter-spacing: 0.03em;
  line-height: 1.6;
}

/* 价格和操作区域 */
.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  margin-top: var(--spacing-sm);
  padding-top: var(--spacing-xs);
  border-top: 1px solid var(--border-color);
}

.price-section {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.current-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  text-shadow: 0 2px 4px rgba(var(--primary-color-rgb), 0.1);
}

.original-price {
  font-size: 13px;
  color: var(--text-color-light);
  text-decoration: line-through;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.7;
  letter-spacing: 0.03em;
}

/* 操作区域 */
.action-section {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.product-rating {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 13px;
  color: var(--accent-brown);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  letter-spacing: 0.03em;
}

.star-icon {
  width: 14px;
  height: 14px;
  filter: drop-shadow(0 1px 2px rgba(var(--accent-brown-rgb), 0.3));
}

/* 加入购物车按钮 */
.add-to-cart-btn {
  width: 36px;
  height: 36px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(var(--primary-color-rgb), 0.2);
  transition: all 0.3s ease-out;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.add-to-cart-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease-out;
}

.add-to-cart-btn:hover {
  transform: scale(1.1);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.3);
}

.add-to-cart-btn:hover::before {
  left: 100%;
}

.add-to-cart-btn:active {
  transform: scale(0.95);
}

.add-icon {
  width: 20px;
  height: 20px;
  filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
}

/* 动画 */
@keyframes tagFloat {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-3px);
  }
}

/* 奶茶主题装饰 */
.product-card::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(circle at 20% 80%, rgba(var(--accent-pink-rgb), 0.03) 0%, transparent 50%);
  pointer-events: none;
  border-radius: var(--border-radius-lg);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .product-card {
    border-radius: var(--border-radius-md);
  }

  .product-image-container {
    border-radius: var(--border-radius-md) var(--border-radius-md) 0 0;
  }

  .product-info {
    padding: var(--spacing-sm);
  }

  .product-name {
    font-size: 15px;
  }

  .current-price {
    font-size: 18px;
  }

  .add-to-cart-btn {
    width: 32px;
    height: 32px;
  }

  .favorite-btn {
    width: 32px;
    height: 32px;
  }
}

/* 加载动画 */
.product-image {
  background: linear-gradient(90deg, var(--surface-color) 25%, var(--border-color) 50%, var(--surface-color) 75%);
  background-size: 200% 100%;
  animation: loading 1.5s ease-in-out infinite;
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}
</style>