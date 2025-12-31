<template>
  <div class="order-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <img src="../../assets/images/icons/close.png" />
      </div>
      <div class="title-box">
        <h1 class="promo-title">{{ promoInfo.name || '特惠活动' }}</h1>
        <span class="promo-subtitle" v-if="promoInfo.description">{{ promoInfo.description }}</span>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="content-wrapper">
      <!-- 活动商品列表 -->
      <div class="product-section">
        <div class="section-header">
          <span class="header-title">特惠商品</span>
          <span class="header-tip">选择活动商品，享受限时折扣</span>
        </div>

        <!-- 加载中骨架屏 -->
        <div class="skeleton-list" v-if="loading">
          <div class="skeleton-product" v-for="i in 3" :key="i"></div>
        </div>

        <!-- 商品列表 -->
        <div class="product-list" v-else>
          <div 
            class="product-card"
            v-for="item in productList"
            :key="item.id"
            :class="{ selected: selectedProduct?.id === item.id }"
            @click="onProductSelect(item)"
          >
            <div class="product-image-wrapper">
              <img class="product-image" :src="formatImageUrl(item.productImage)" />
              <div class="tag discount">活动价</div>
            </div>

            <div class="product-info">
              <div class="name-row">
                <span class="product-name">{{ item.name }}</span>
              </div>
              <span class="product-desc">{{ item.description }}</span>
              
              <div class="product-footer">
                <div class="price-box">
                  <!-- 直接展示后端计算好的促销价或通过比例计算 -->
                  <span class="price">¥{{ item.promoPrice }}</span>
                  <span class="original-price">¥{{ item.price }}</span>
                </div>
                <div class="select-indicator">
                  <div class="radio-circle"></div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 空状态 -->
        <div class="empty-tip" v-if="!loading && productList.length === 0">
          <img class="empty-icon" src="../../assets/images/icons/cart.png" />
          <span>暂无活动商品</span>
        </div>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="bottom-bar" v-if="selectedProduct">
      <div class="price-info">
        <div class="total-row">
          <span class="label">合计</span>
          <span class="value">¥{{ selectedProduct.promoPrice }}</span>
        </div>
        <div class="discount-row" v-if="savedAmount > 0">
          已省 ¥{{ savedAmount }}
        </div>
      </div>
      
      <div 
        class="custom-submit-btn" 
        :class="{ 'is-loading': submitting }"
        @click="handleOrder"
      >
        <span v-if="!submitting">立即下单</span>
        <span v-else class="loading-spinner"></span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { productApi, orderApi, promotionApi } from '@/utils/api'
import { formatImageUrl } from '@/utils/util'

const router = useRouter()
const route = useRoute()

const loading = ref(false)
const submitting = ref(false)
const productList = ref([])
const selectedProduct = ref(null)
const promoInfo = ref({
  name: '',
  description: ''
})

// 计算省下的金额
const savedAmount = computed(() => {
  if (!selectedProduct.value) return 0
  const diff = selectedProduct.value.price - selectedProduct.value.promoPrice
  return diff > 0 ? diff.toFixed(2) : 0
})

const goBack = () => {
  router.back()
}

const onProductSelect = (product) => {
  selectedProduct.value = product
}

const loadPromoData = async () => {
  const promoId = route.params.id || route.query.id
  if (!promoId) return
  
  loading.value = true
  try {
    // 1. 获取活动配置详情
    const promoRes = await promotionApi.getPromotionDetail(promoId)
    if (promoRes.code === 200) {
      promoInfo.value = promoRes.data
    }

    // 2. 获取该活动下的商品列表
    const productRes = await productApi.getProducts({ promotionId: promoId })
    if (productRes.code === 200) {
      productList.value = (productRes.data || []).map(item => ({
        ...item,
        // 如果后端没返回 promoPrice，前端可根据 discountRate 计算，此处假设后端已处理
        promoPrice: item.promoPrice || (item.price * (promoRes.data.discountRate || 1)).toFixed(2)
      }))
    }
    
    // 默认选中第一个
    if (productList.value.length > 0) {
      selectedProduct.value = productList.value[0]
    }
  } catch (error) {
    console.error('加载活动数据失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交活动订单
const handleOrder = async () => {
  if (!selectedProduct.value || submitting.value) return
  
  submitting.value = true
  try {
    const orderData = {
      items: [
        {
          productId: selectedProduct.value.id,
          quantity: 1,
          price: selectedProduct.value.price,
          promoPrice: selectedProduct.value.promoPrice
        }
      ],
      promotionId: route.params.id || route.query.id,
      deliveryType: 'PICKUP', // 活动单通常默认为自提，也可根据实际业务调整
      remark: `活动专享单: ${promoInfo.value.name}`
    }
    
    const res = await orderApi.createOrder(orderData)
    if (res.code === 200) {
      // 下单成功跳转详情
      router.replace(`/order-detail/${res.data.orderNo}`)
    } else {
      alert(res.message || '下单失败')
    }
  } catch (error) {
    console.error('下单过程异常:', error)
    alert('系统繁忙，请稍后重试')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadPromoData()
})
</script>


<style scoped>
.order-page {
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: var(--background-color);
  font-family: 'Noto Sans KR', sans-serif;
}

.nav-header {
  padding: 40px 24px 20px;
  background: var(--surface-color);
  display: flex;
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid var(--border-color);
}

.back-btn {
  width: 40px;
  height: 40px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-sm);
  cursor: pointer;
}

.back-btn img {
  width: 20px;
  height: 20px;
  opacity: 0.6;
}

.title-box {
  flex: 1;
}

.promo-title {
  font-size: 20px;
  margin: 0;
  color: var(--primary-dark);
}

.promo-subtitle {
  font-size: 12px;
  color: var(--text-color-medium);
  opacity: 0.8;
}

.content-wrapper {
  flex: 1;
  overflow-y: auto;
  padding: 20px 24px;
}

.section-header {
  margin-bottom: 20px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.header-title {
  font-size: 18px;
  font-weight: 700;
  color: var(--text-color-dark);
}

.header-tip {
  font-size: 12px;
  color: var(--text-color-light);
}

.product-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.product-card {
  display: flex;
  gap: 16px;
  padding: 16px;
  background: white;
  border-radius: 20px;
  border: 2px solid transparent;
  transition: all 0.3s ease;
  cursor: pointer;
  box-shadow: var(--shadow-sm);
}

.product-card.selected {
  border-color: var(--primary-color);
  background: var(--accent-cream);
  transform: translateY(-2px);
  box-shadow: var(--shadow-md);
}

.product-image-wrapper {
  position: relative;
  width: 90px;
  height: 90px;
  flex-shrink: 0;
}

.product-image {
  width: 100%;
  height: 100%;
  border-radius: 12px;
  object-fit: cover;
}

.tag.discount {
  position: absolute;
  top: -6px;
  left: -6px;
  background: #ff6b6b;
  color: white;
  font-size: 10px;
  padding: 2px 8px;
  border-radius: 8px;
  font-weight: bold;
}

.product-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.product-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-color-dark);
}

.product-desc {
  font-size: 12px;
  color: var(--text-color-medium);
  line-height: 1.4;
  margin: 4px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.price-box {
  display: flex;
  align-items: baseline;
  gap: 6px;
}

.price {
  font-size: 18px;
  font-weight: 700;
  color: #ff6b6b;
}

.original-price {
  font-size: 12px;
  color: var(--text-color-light);
  text-decoration: line-through;
}

.radio-circle {
  width: 20px;
  height: 20px;
  border: 2px solid var(--border-color);
  border-radius: 50%;
  position: relative;
}

.product-card.selected .radio-circle {
  border-color: var(--primary-color);
  background: var(--primary-color);
}

.product-card.selected .radio-circle::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 8px;
  height: 8px;
  background: white;
  border-radius: 50%;
}

.bottom-bar {
  padding: 20px 24px calc(20px + env(safe-area-inset-bottom));
  background: white;
  border-top: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  box-shadow: 0 -4px 20px rgba(0,0,0,0.05);
}

.price-info {
  display: flex;
  flex-direction: column;
}

.total-row {
  display: flex;
  align-items: baseline;
  gap: 8px;
}

.total-row .label {
  font-size: 14px;
  color: var(--text-color-medium);
}

.total-row .value {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-dark);
}

.discount-row {
  font-size: 12px;
  color: #ff6b6b;
  font-weight: 600;
}

/* 核心修复：自定义按钮样式，确保点击区域大且无样式冲突 */
.custom-submit-btn {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  padding: 14px 40px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.275);
  min-width: 140px;
  display: flex;
  align-items: center;
  justify-content: center;
  user-select: none;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.custom-submit-btn:active {
  transform: scale(0.95);
  box-shadow: 0 2px 6px rgba(160, 82, 45, 0.2);
}

.custom-submit-btn.is-loading {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.skeleton-product {
  height: 120px;
  background: #f0f0f0;
  border-radius: 20px;
  margin-bottom: 16px;
  animation: shimmer 1.5s infinite;
}

@keyframes shimmer {
  0% { opacity: 0.5; }
  50% { opacity: 1; }
  100% { opacity: 0.5; }
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255,255,255,0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.empty-tip {
  text-align: center;
  padding: 60px 0;
  color: var(--text-color-light);
}

.empty-icon {
  width: 60px;
  height: 60px;
  opacity: 0.2;
  margin-bottom: 12px;
}
</style>