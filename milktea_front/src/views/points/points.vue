<template>
  <div class="page-container">
    <!-- 顶部导航栏 -->
    <div class="nav-bar">
      <div class="nav-left" @click="router.back()">
        <img class="back-icon" src="@/assets/images/icons/right.png" style="transform: rotate(180deg);" />
      </div>
      <div class="nav-title">积分商城</div>
      <div class="nav-right"></div>
    </div>

    <div class="main-content">
      <!-- 积分卡片 -->
      <div class="points-card-section">
        <div class="points-card">
          <div class="card-content">
            <div class="user-info">
              <span class="points-label">当前可用积分</span>
              <div class="points-value-wrapper">
                <h1 class="points-value">{{ userPoints }}</h1>
                <div class="points-tag">VIP</div>
              </div>
            </div>
            <div class="card-actions">
              <div class="action-item" @click="toggleRecords">
                <img class="action-icon" src="@/assets/images/icons/order.png" />
                <span>兑换记录</span>
              </div>
              <div class="action-item" @click="goToRules">
                <img class="action-icon" src="@/assets/images/icons/info.png" />
                <span>积分规则</span>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 分类标签 -->
      <div class="category-tabs-container">
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
      </div>

      <!-- 商品列表 -->
      <div class="product-section">
        <div class="product-grid" v-if="filteredProducts.length > 0">
          <div 
            class="product-card"
            v-for="item in filteredProducts"
            :key="item.id"
          >
            <div class="product-info">
              <div class="product-header">
                <h3 class="product-name">{{ item.name }}</h3>
                <div class="product-badge" v-if="item.isHot">HOT</div>
              </div>
              <p class="product-desc">{{ item.description }}</p>
              <div class="product-footer">
                <div class="price-info">
                  <span class="points-num">{{ item.points }}</span>
                  <span class="points-unit">积分</span>
                </div>
                <button 
                  class="exchange-btn" 
                  @click="openExchangeModal(item)" 
                  :disabled="userPoints < item.points || item.stock <= 0"
                >
                  {{ item.stock > 0 ? '兑换' : '售罄' }}
                </button>
              </div>
            </div>
          </div>
        </div>
        
        <!-- 空状态 -->
        <div class="empty-state" v-else-if="!loading">
          <img class="empty-img" src="@/assets/images/icons/coupon.png" />
          <p>暂无可兑换商品</p>
        </div>
      </div>
    </div>

    <!-- 兑换记录弹窗 -->
    <div class="modal-overlay" v-if="showRecords" @click="showRecords = false">
      <div class="modal-container" @click.stop>
        <div class="modal-header">
          <span class="modal-title">兑换记录</span>
          <img class="close-btn" src="@/assets/images/icons/history.png" @click="showRecords = false" />
        </div>
        <div class="modal-body">
          <div v-if="exchangeRecords.length > 0" class="records-list">
            <div v-for="record in exchangeRecords" :key="record.id" class="record-card">
              <div class="record-main">
                <div class="record-name">{{ record.productName }}</div>
                <div class="record-points">-{{ record.points }} 积分</div>
              </div>
              <div class="record-footer">
                <span class="record-time">{{ formatDate(record.createTime) }}</span>
                <span class="record-status">兑换成功</span>
              </div>
            </div>
          </div>
          <div v-else class="empty-records">
            <p>暂无兑换记录</p>
          </div>
        </div>
      </div>
    </div>

    <!-- 兑换信息填写弹窗 (精美渲染) -->
    <div class="modal-overlay" v-if="showExchangeModal" @click="showExchangeModal = false">
      <div class="modal-container exchange-modal-fancy" @click.stop>
        <div class="fancy-header">
          <div class="fancy-title-box">
            <h3 class="fancy-title">确认兑换</h3>
            <p class="fancy-subtitle">请核对您的兑换信息</p>
          </div>
          <div class="close-circle" @click="showExchangeModal = false">
            <img class="close-icon-mini" src="@/assets/images/icons/history.png" />
          </div>
        </div>
        
        <div class="modal-body fancy-body">
          <div class="fancy-product-card">
            <div class="fancy-product-info">
              <div class="fancy-name">{{ selectedProduct?.name }}</div>
              <div class="fancy-points-row">
                <span class="fancy-points-val">{{ selectedProduct?.points }}</span>
                <span class="fancy-points-lab">积分</span>
              </div>
            </div>
            <div class="fancy-decoration">🎁</div>
          </div>
          
          <div class="fancy-form">
            <div class="fancy-input-group">
              <div class="input-label">
                <img class="label-icon" src="@/assets/images/icons/address.png" />
                <span>收货人姓名</span>
              </div>
              <input v-model="exchangeForm.name" placeholder="请输入姓名" class="fancy-input" />
            </div>
            
            <div class="fancy-input-group">
              <div class="input-label">
                <img class="label-icon" src="@/assets/images/icons/phone.png" />
                <span>联系电话</span>
              </div>
              <input v-model="exchangeForm.phone" placeholder="请输入手机号" class="fancy-input" />
            </div>
            
            <div class="fancy-input-group">
              <div class="input-label">
                <img class="label-icon" src="@/assets/images/icons/address.png" />
                <span>详细地址</span>
              </div>
              <textarea v-model="exchangeForm.address" placeholder="请输入详细收货地址" class="fancy-textarea"></textarea>
            </div>
          </div>
          
          <div class="fancy-footer">
            <div class="points-summary">
              <span>扣除积分：</span>
              <span class="minus-val">-{{ selectedProduct?.points }}</span>
            </div>
            <button class="fancy-submit-btn" @click="confirmExchange" :disabled="submitting">
              <span v-if="!submitting">确认兑换</span>
              <div v-else class="loading-dots">
                <span>.</span><span>.</span><span>.</span>
              </div>
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { pointsApi, userApi } from '@/utils/api'

const router = useRouter()
const userPoints = ref(0)
const activeCategoryId = ref('all')
const showRecords = ref(false)
const showExchangeModal = ref(false)
const loading = ref(false)
const submitting = ref(false)
const exchangeRecords = ref([])
const selectedProduct = ref(null)
const exchangeForm = ref({
  name: '',
  phone: '',
  address: ''
})

const categories = ref([
  { id: 'all', name: '全部' },
  { id: 'DRINK', name: '饮品券' },
  { id: 'PERIPHERAL', name: '周边礼品' },
  { id: 'VIRTUAL', name: '虚拟权益' }
])

const productList = ref([])

const filteredProducts = computed(() => {
  if (activeCategoryId.value === 'all') return productList.value
  return productList.value.filter(p => p.category === activeCategoryId.value)
})

const loadData = async () => {
  loading.value = true
  try {
    // 获取用户积分
    const userRes = await userApi.getUserInfo()
    if (userRes.code === 200) {
      userPoints.value = userRes.data.points || 0
    }

    // 获取积分商品列表
    const productRes = await pointsApi.getPointsProducts()
    if (productRes.code === 200) {
      productList.value = productRes.data
    }
  } catch (error) {
    console.error('加载积分数据失败:', error)
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
        exchangeRecords.value = res.data
      }
    } catch (error) {
      console.error('加载兑换记录失败:', error)
    }
  }
}

const goToRules = () => {
  alert('积分规则：\n1. 每消费1元获得1积分\n2. 积分可用于兑换商品或优惠券\n3. 兑换成功后积分将实时扣除\n4. 虚拟商品即时到账，实物商品请填写正确地址')
}

const openExchangeModal = (product) => {
  selectedProduct.value = product
  showExchangeModal.value = true
}

const confirmExchange = async () => {
  if (!exchangeForm.value.name || !exchangeForm.value.phone || !exchangeForm.value.address) {
    alert('请填写完整的收货信息')
    return
  }

  submitting.value = true
  try {
    const res = await pointsApi.exchangeProduct({
      productId: selectedProduct.value.id,
      receiverName: exchangeForm.value.name,
      receiverPhone: exchangeForm.value.phone,
      receiverAddress: exchangeForm.value.address
    })

    if (res.code === 200) {
      alert('兑换成功！')
      showExchangeModal.value = false
      exchangeForm.value = { name: '', phone: '', address: '' }
      loadData() // 刷新积分和列表
    } else {
      alert(res.message || '兑换失败')
    }
  } catch (error) {
    console.error('兑换失败:', error)
    alert('兑换失败，请稍后重试')
  } finally {
    submitting.value = false
  }
}

const formatDate = (dateStr) => {
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  height: 100vh;
  background: var(--background-color, #f5f0e1);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 顶部导航栏 */
.nav-bar {
  height: 50px;
  background: white;
  display: flex;
  align-items: center;
  padding: 0 15px;
  border-bottom: 1px solid var(--border-color, #d4c7b5);
  flex-shrink: 0;
}

.back-icon {
  width: 20px;
  height: 20px;
  cursor: pointer;
}

.nav-title {
  flex: 1;
  text-align: center;
  font-size: 17px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Prompt', serif;
}

.nav-right {
  width: 20px;
}

.main-content {
  flex: 1;
  overflow-y: auto;
  padding-bottom: 30px;
}

/* 积分卡片 */
.points-card-section {
  padding: 15px;
}

.points-card {
  background: linear-gradient(135deg, var(--primary-color, #a0522d), var(--primary-dark, #8b4513));
  border-radius: 16px;
  padding: 20px;
  color: white;
  box-shadow: 0 8px 20px rgba(160, 82, 45, 0.2);
  border: 2px solid var(--accent-cream, #fff8dc);
}

.points-label {
  font-size: 13px;
  opacity: 0.8;
}

.points-value-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-top: 5px;
}

.points-value {
  font-size: 36px;
  font-weight: 700;
  font-family: 'Prompt', serif;
}

.points-tag {
  background: rgba(255, 255, 255, 0.2);
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 10px;
  border: 1px solid rgba(255, 255, 255, 0.3);
}

.card-actions {
  display: flex;
  gap: 20px;
  margin-top: 20px;
  padding-top: 15px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.action-item {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  cursor: pointer;
}

.action-icon {
  width: 16px;
  height: 16px;
  filter: brightness(0) invert(1);
}

/* 分类标签 */
.category-tabs-container {
  background: white;
  position: sticky;
  top: 0;
  z-index: 10;
  border-bottom: 1px solid var(--border-color, #d4c7b5);
}

.category-tabs {
  display: flex;
  padding: 0 15px;
  gap: 25px;
  overflow-x: auto;
  scrollbar-width: none;
}

.category-tabs::-webkit-scrollbar {
  display: none;
}

.tab-item {
  height: 45px;
  display: flex;
  align-items: center;
  font-size: 14px;
  color: var(--text-color-medium, #7a6a5b);
  white-space: nowrap;
  position: relative;
  cursor: pointer;
}

.tab-item.active {
  color: var(--primary-color, #a0522d);
  font-weight: 700;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: var(--primary-color, #a0522d);
  border-radius: 3px 3px 0 0;
}

/* 商品列表 */
.product-section {
  padding: 15px;
}

.product-grid {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.product-card {
  background: white;
  border-radius: 12px;
  padding: 15px;
  border: 1px solid var(--border-color, #d4c7b5);
  box-shadow: 0 2px 8px rgba(160, 82, 45, 0.05);
}

.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 6px;
}

.product-name {
  font-size: 15px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
}

.product-badge {
  background: #ff6b6b;
  color: white;
  font-size: 10px;
  font-weight: 700;
  padding: 2px 6px;
  border-radius: 4px;
}

.product-desc {
  font-size: 12px;
  color: var(--text-color-medium, #7a6a5b);
  margin-bottom: 12px;
  line-height: 1.4;
}

.product-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.points-num {
  font-size: 18px;
  font-weight: 700;
  color: var(--primary-color, #a0522d);
}

.points-unit {
  font-size: 11px;
  color: var(--text-color-medium, #7a6a5b);
  margin-left: 2px;
}

.exchange-btn {
  background: var(--primary-color, #a0522d);
  color: white;
  border: none;
  padding: 6px 16px;
  border-radius: 15px;
  font-size: 13px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.2s;
}

.exchange-btn:active {
  transform: scale(0.95);
}

.exchange-btn:disabled {
  background: #d4c7b5;
  cursor: not-allowed;
}

/* 弹窗通用样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  z-index: 100;
  display: flex;
  align-items: flex-end;
  justify-content: center;
  backdrop-filter: blur(4px);
}

.modal-container {
  width: 100%;
  background: white;
  border-radius: 20px 20px 0 0;
  max-height: 85vh;
  display: flex;
  flex-direction: column;
  animation: slideUp 0.3s cubic-bezier(0.175, 0.885, 0.32, 1.1);
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

/* 精美兑换框样式 */
.exchange-modal-fancy {
  background: #fff;
  border-radius: 24px 24px 0 0;
  overflow: hidden;
}

.fancy-header {
  padding: 25px 20px 15px;
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  background: linear-gradient(to bottom, #fff8f0, #fff);
}

.fancy-title {
  font-size: 20px;
  font-weight: 800;
  color: var(--text-color-dark, #4a3b30);
  margin-bottom: 4px;
}

.fancy-subtitle {
  font-size: 12px;
  color: var(--text-color-medium, #7a6a5b);
}

.close-circle {
  width: 32px;
  height: 32px;
  background: #f0f0f0;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.close-icon-mini {
  width: 16px;
  height: 16px;
  opacity: 0.6;
}

.fancy-body {
  padding: 0 20px 30px;
}

.fancy-product-card {
  background: linear-gradient(135deg, #fff, #fdf8f3);
  border: 2px solid var(--accent-cream, #fff8dc);
  border-radius: 16px;
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.05);
}

.fancy-name {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
  margin-bottom: 4px;
}

.fancy-points-val {
  font-size: 22px;
  font-weight: 800;
  color: var(--primary-color, #a0522d);
  font-family: 'Prompt', serif;
}

.fancy-points-lab {
  font-size: 12px;
  color: var(--primary-color, #a0522d);
  margin-left: 4px;
  font-weight: 600;
}

.fancy-decoration {
  font-size: 32px;
  filter: drop-shadow(0 4px 8px rgba(0,0,0,0.1));
}

.fancy-form {
  display: flex;
  flex-direction: column;
  gap: 18px;
}

.fancy-input-group {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.input-label {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 13px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
}

.label-icon {
  width: 14px;
  height: 14px;
  opacity: 0.7;
}

.fancy-input, .fancy-textarea {
  width: 100%;
  background: #f9f9f9;
  border: 1.5px solid #eee;
  padding: 12px 15px;
  border-radius: 12px;
  font-size: 14px;
  transition: all 0.2s;
  box-sizing: border-box;
}

.fancy-input:focus, .fancy-textarea:focus {
  background: #fff;
  border-color: var(--primary-light, #d2b48c);
  box-shadow: 0 0 0 3px rgba(160, 82, 45, 0.05);
  outline: none;
}

.fancy-textarea {
  height: 90px;
  resize: none;
}

.fancy-footer {
  margin-top: 30px;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.points-summary {
  display: flex;
  justify-content: center;
  align-items: center;
  font-size: 14px;
  color: var(--text-color-medium, #7a6a5b);
}

.minus-val {
  color: #ff6b6b;
  font-weight: 800;
  font-size: 16px;
  margin-left: 4px;
}

.fancy-submit-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--primary-color, #a0522d), var(--primary-dark, #8b4513));
  color: white;
  border: none;
  padding: 16px;
  border-radius: 16px;
  font-size: 16px;
  font-weight: 700;
  cursor: pointer;
  box-shadow: 0 8px 20px rgba(160, 82, 45, 0.25);
  transition: all 0.2s;
}

.fancy-submit-btn:active {
  transform: translateY(2px);
  box-shadow: 0 4px 10px rgba(160, 82, 45, 0.2);
}

.fancy-submit-btn:disabled {
  background: #d4c7b5;
  box-shadow: none;
  cursor: not-allowed;
}

.loading-dots span {
  animation: blink 1.4s infinite both;
}

.loading-dots span:nth-child(2) { animation-delay: 0.2s; }
.loading-dots span:nth-child(3) { animation-delay: 0.4s; }

@keyframes blink {
  0% { opacity: 0.2; }
  20% { opacity: 1; }
  100% { opacity: 0.2; }
}

/* 记录列表样式 */
.modal-header {
  padding: 15px 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color, #d4c7b5);
}

.modal-title {
  font-size: 16px;
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
}

.close-btn {
  width: 20px;
  height: 20px;
  cursor: pointer;
  opacity: 0.5;
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;
}

.record-card {
  background: var(--background-color, #f5f0e1);
  padding: 15px;
  border-radius: 12px;
  margin-bottom: 12px;
  border: 1px solid var(--border-color, #d4c7b5);
}

.record-main {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.record-name {
  font-weight: 700;
  color: var(--text-color-dark, #4a3b30);
}

.record-points {
  color: var(--primary-color, #a0522d);
  font-weight: 700;
}

.record-footer {
  display: flex;
  justify-content: space-between;
  font-size: 12px;
  color: var(--text-color-medium, #7a6a5b);
}

.record-status {
  color: #2c9678;
}

.empty-state, .empty-records {
  text-align: center;
  padding: 40px 0;
  color: var(--text-color-medium, #7a6a5b);
}

.empty-img {
  width: 80px;
  height: 80px;
  opacity: 0.3;
  margin-bottom: 15px;
}

@media (min-width: 768px) {
  .modal-container {
    width: 450px;
    border-radius: 24px;
    margin-bottom: 5vh;
  }
}
</style>