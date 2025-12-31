<template>
  <div class="order-page">
    <!-- 顶部导航 -->
    <div class="nav-header">
      <div class="back-btn" @click="goBack">
        <img src="../../assets/images/icons/close.png" />
      </div>
      <div class="title-box">
        <h1 class="promo-title">{{ promoName || '限时特惠活动' }}</h1>
        <span class="promo-subtitle">DEMO ONLY: 模拟下单与打折存根</span>
      </div>
    </div>

    <!-- 主体内容区 -->
    <div class="content-wrapper">
      <!-- 商品列表 -->
      <div class="product-section">
        <div class="section-header">
          <span class="header-title">活动商品</span>
          <span class="header-tip">选择心仪饮品，立享折扣</span>
        </div>

        <div class="skeleton-list" v-if="loading">
          <div class="skeleton-product" v-for="i in 3" :key="i"></div>
        </div>

        <div class="product-list" v-else>
          <div 
            class="product-card"
            v-for="item in productList"
            :key="item.id"
            :class="{ selected: selectedProduct?.id === item.id }"
            @click="onProductSelect(item)"
          >
            <div class="product-image-wrapper">
              <img class="product-image" :src="item.image" />
              <div class="tag discount">活动价</div>
            </div>

            <div class="product-info">
              <div class="name-row">
                <span class="product-name">{{ item.name }}</span>
              </div>
              <span class="product-desc">{{ item.description }}</span>
              
              <div class="product-footer">
                <div class="price-box">
                  <span class="price">¥{{ (item.price * discount).toFixed(2) }}</span>
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
          <span class="value">¥{{ finalPrice }}</span>
        </div>
        <div class="discount-row">
          已省 ¥{{ savedAmount }} ({{ (discount * 10).toFixed(1) }}折)
        </div>
      </div>
      <!-- 核心修复：使用 div 模拟按钮，彻底规避 button 默认行为或样式覆盖导致的点击失效 -->
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
import { useRouter } from 'vue-router'
import { productApi } from '@/utils/api'
import { formatImageUrl } from '@/utils/util'
import { orderDB, promoOrderDB, pointsDB } from '@/utils/db'

const router = useRouter()
const loading = ref(false)
const submitting = ref(false)
const productList = ref([])
const selectedProduct = ref(null)
const promoName = ref('')
const discount = ref(0.8) // 默认 8 折

const finalPrice = computed(() => {
  if (!selectedProduct.value) return '0.00'
  return (selectedProduct.value.price * discount.value).toFixed(2)
})

const savedAmount = computed(() => {
  if (!selectedProduct.value) return '0.00'
  return (selectedProduct.value.price * (1 - discount.value)).toFixed(2)
})

const goBack = () => {
  router.back()
}

const onProductSelect = (product) => {
  selectedProduct.value = product
}

const loadPromoData = async () => {
  loading.value = true
  try {
    promoName.value = sessionStorage.getItem('promo_name') || '限时特惠'
    
    // 模拟从活动规则中获取折扣
    const promoId = sessionStorage.getItem('promo_id')
    if (promoId) {
      discount.value = promoId == 1 ? 0.8 : 0.9
    }

    // 优先从 sessionStorage 获取首页传递过来的真实奶茶数据
    const cachedData = sessionStorage.getItem('promo_products_data')
    if (cachedData) {
      const rawList = JSON.parse(cachedData)
      // 再次去重，确保万无一失
      const uniqueMap = new Map()
      rawList.forEach(p => uniqueMap.set(p.id, p))
      
      productList.value = Array.from(uniqueMap.values()).map(item => ({
        ...item,
        image: formatImageUrl(item.image || item.productImage) || 'https://images.unsplash.com/photo-1544787210-2827443cb39b?w=200'
      }))
      console.log('[DEMO] 已复用首页真实奶茶数据并去重:', productList.value.length)
    } else {
      // 兜底逻辑：如果缓存失效，再请求接口
      const filterIdsStr = sessionStorage.getItem('promo_filter_ids')
      const res = await productApi.getProducts()
      const resData = res.data || res
      let rawList = Array.isArray(resData) ? resData : (resData.list || [])

      if (filterIdsStr) {
        const filterIds = JSON.parse(filterIdsStr)
        rawList = rawList.filter(p => filterIds.includes(p.id))
      }

      productList.value = rawList.map(item => ({
        ...item,
        image: formatImageUrl(item.image || item.productImage) || 'https://images.unsplash.com/photo-1544787210-2827443cb39b?w=200'
      }))
    }
    
    if (productList.value.length > 0) {
      selectedProduct.value = productList.value[0]
    }
  } catch (error) {
    console.error('加载活动商品失败:', error)
  } finally {
    loading.value = false
  }
}

const handleOrder = async () => {
  console.log('[DEMO] 尝试触发下单逻辑...')
  if (!selectedProduct.value || submitting.value) {
    console.log('[DEMO] 下单被拦截: ', !selectedProduct.value ? '未选择商品' : '正在提交中')
    return
  }
  
  submitting.value = true
  
  try {
    // 模拟网络延迟
    await new Promise(resolve => setTimeout(resolve, 800))
    
    const orderNo = 'PROMO' + Date.now()
    const now = new Date().toISOString()
    
    // 1. 构造订单基本信息 (模拟后端存储)
    const orderData = {
      orderNo: orderNo,
      status: 'MAKING',
      statusText: '正在制作中',
      createTime: now,
      totalAmount: parseFloat(finalPrice.value),
      deliveryType: 'pickup',
      storeName: '王大妈的奶茶店（活动专供）',
      pickupCode: 'P' + Math.floor(Math.random() * 900 + 100),
      orderItems: [
        {
          id: selectedProduct.value.id,
          productName: selectedProduct.value.name,
          price: selectedProduct.value.price,
          quantity: 1,
          image: selectedProduct.value.image
        }
      ]
    }
    
    // 2. 存储到通用订单库
    await orderDB.add(orderData)
    
    // 3. 存储打折细节到 IndexedDB (DEMO ONLY: 核心要求)
    await promoOrderDB.saveDetails({
      orderNo: orderNo,
      promoName: promoName.value,
      discount: discount.value,
      savedAmount: parseFloat(savedAmount.value),
      discountDetail: `参与活动【${promoName.value}】，原价 ¥${selectedProduct.value.price}，享受 ${discount.value * 10} 折优惠。`
    })
    
    // 4. 模拟扣除积分 (视觉真实)
    try {
      const currentPoints = await pointsDB.getUserPoints()
      await pointsDB.updateUserPoints(currentPoints - 10)
      await pointsDB.addRecord({
        type: 'CONSUME',
        points: -10,
        title: '活动下单扣除',
        orderNo: orderNo
      })
    } catch (e) {
      console.warn('积分扣除模拟失败，不影响下单流程')
    }

    console.log('[DEMO] 活动下单成功:', orderNo)
    
    // 跳转到订单详情
    router.replace(`/order-detail/${orderNo}`)
    
  } catch (error) {
    console.error('下单失败:', error)
    alert('下单失败，请重试')
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