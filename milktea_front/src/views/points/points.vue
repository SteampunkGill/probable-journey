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
import { ref, computed, onMounted } from 'vue'
import { pointsApi, userApi } from '../../utils/api'

const userPoints = ref(0)
const activeCategoryId = ref('all')
const showRecords = ref(false)
const loading = ref(false)

const categories = [
  { id: 'all', name: '全部' },
  { id: 'drink', name: '饮品' },
  { id: 'coupon', name: '优惠券' },
  { id: 'gift', name: '周边' }
]

const productList = ref([
  {
    id: 'p001',
    name: '珍珠奶茶兑换券',
    image: 'https://images.unsplash.com/photo-1567095761054-7a02e69e5c43?w=400',
    points: 200,
    stock: 50,
    category: 'drink',
    description: '可兑换任意口味珍珠奶茶一杯'
  },
  {
    id: 'p002',
    name: '满30减5优惠券',
    image: 'https://images.unsplash.com/photo-1563729784474-d77dbb933a9e?w=400',
    points: 100,
    stock: 100,
    category: 'coupon',
    description: '全场通用，满30元减5元'
  },
  {
    id: 'p003',
    name: '品牌帆布袋',
    image: 'https://images.unsplash.com/photo-1514432324607-a09d9b4aefdd?w=400',
    points: 500,
    stock: 20,
    category: 'gift',
    description: '环保帆布袋，时尚又实用'
  },
  {
    id: 'p004',
    name: '芝士奶盖兑换券',
    image: 'https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=400',
    points: 250,
    stock: 30,
    category: 'drink',
    description: '可兑换芝士奶盖系列饮品'
  },
  {
    id: 'p005',
    name: '8折优惠券',
    image: 'https://images.unsplash.com/photo-1578775887804-699de7086ff9?w=400',
    points: 150,
    stock: 80,
    category: 'coupon',
    description: '全场8折，最高优惠15元'
  },
  {
    id: 'p006',
    name: '品牌保温杯',
    image: 'https://images.unsplash.com/photo-1558857563-b322d0427955?w=400',
    points: 800,
    stock: 15,
    category: 'gift',
    description: '304不锈钢保温杯'
  }
])

const filteredProducts = computed(() => {
  if (activeCategoryId.value === 'all') return productList.value
  return productList.value.filter(p => p.category === activeCategoryId.value)
})

const toggleRecords = () => {
  showRecords.value = !showRecords.value
}

const goToRules = () => {
  alert('积分规则：\n1. 每消费1元获得1积分\n2. 每日签到可获得10积分\n3. 完成订单评价可获得20积分\n4. 积分可用于兑换商品')
}

const exchangeProduct = (product) => {
  if (userPoints.value < product.points) {
    alert('积分不足')
    return
  }
  
  if (confirm(`确定花费${product.points}积分兑换${product.name}吗？`)) {
    userPoints.value -= product.points
    product.stock -= 1
    alert('兑换成功！')
  }
}
</script>

<style scoped>
.points-page {
  min-height: 100vh;
  background: #F8F8F8;
  padding-bottom: 20px;
}

.points-card {
  height: 180px;
  position: relative;
  margin: 15px;
  border-radius: 16px;
  overflow: hidden;
  color: white;
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #D4A574 0%, #B88A58 100%);
}

.card-content {
  position: relative;
  height: 100%;
  padding: 25px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.points-label {
  font-size: 14px;
  opacity: 0.9;
}

.points-value {
  font-size: 40px;
  font-weight: bold;
  margin-top: 5px;
}

.card-actions {
  display: flex;
  gap: 20px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  background: rgba(255,255,255,0.2);
  padding: 4px 12px;
  border-radius: 15px;
  cursor: pointer;
}

.category-tabs {
  display: flex;
  background: white;
  padding: 10px 15px;
  gap: 20px;
  position: sticky;
  top: 0;
  z-index: 10;
}

.tab-item {
  font-size: 14px;
  color: #666;
  padding: 5px 0;
  position: relative;
  cursor: pointer;
}

.tab-item.active {
  color: #D4A574;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: #D4A574;
  border-radius: 3px;
}

.product-list {
  padding: 15px;
}

.product-item {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 15px;
  display: flex;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
}

.product-image {
  width: 120px;
  height: 120px;
  object-fit: cover;
}

.product-info {
  flex: 1;
  padding: 12px;
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
  margin-top: 10px;
}

.points-price {
  display: flex;
  align-items: baseline;
  color: #D4A574;
}

.points-num {
  font-size: 20px;
  font-weight: bold;
}

.points-unit {
  font-size: 11px;
  margin-left: 2px;
}

.exchange-btn {
  background: #D4A574;
  color: white;
  border: none;
  padding: 5px 15px;
  border-radius: 15px;
  font-size: 12px;
  cursor: pointer;
}

.exchange-btn:disabled {
  background: #CCC;
  cursor: not-allowed;
}

.stock-info {
  font-size: 10px;
  color: #FF4D4F;
  margin-top: 4px;
}

.empty-tip {
  padding-top: 100px;
  text-align: center;
  color: #999;
}

.empty-icon {
  font-size: 60px;
  margin-bottom: 20px;
  opacity: 0.2;
}

.records-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 100;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
}

.modal-content {
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  border-radius: 20px 20px 0 0;
  height: 70vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #F5F5F5;
}

.modal-title {
  font-size: 16px;
  font-weight: bold;
}

.close-icon {
  font-size: 20px;
  color: #999;
  cursor: pointer;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.empty-records {
  padding-top: 100px;
  text-align: center;
  color: #999;
}
</style>