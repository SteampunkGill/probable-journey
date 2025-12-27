<template>
  <div class="gift-card-page">
    <div class="nav-header">
      <div class="back-icon" @click="$router.back()">←</div>
      <div class="title">礼品卡</div>
    </div>

    <div class="tabs-nav">
      <div 
        v-for="(tab, index) in tabs" 
        :key="index" 
        class="tab-item" 
        :class="{ active: activeTab === index }"
        @click="activeTab = index"
      >
        {{ tab }}
      </div>
    </div>

    <div class="tab-content">
      <!-- 我的礼品卡 -->
      <div v-if="activeTab === 0" class="my-cards">
        <div v-if="giftCards.length > 0" class="card-list">
          <div v-for="card in giftCards" :key="card.id" class="card-item" :class="card.status.toLowerCase()">
            <div class="card-main">
              <div class="card-no">NO. {{ card.cardNo }}</div>
              <div class="card-balance">
                <span class="unit">¥</span>
                <span class="val">{{ card.balance }}</span>
              </div>
              <div class="card-face">面额: ¥{{ card.faceValue }}</div>
            </div>
            <div class="card-badge" :class="card.status.toLowerCase()">
              {{ getStatusText(card.status) }}
            </div>
            <div class="card-footer">
              有效期至: {{ formatDate(card.expiryTime) }}
            </div>
          </div>
        </div>
        <div v-else class="empty-box">
          <div class="empty-icon">🎁</div>
          <p>暂无礼品卡</p>
        </div>
      </div>

      <!-- 购买礼品卡 -->
      <div v-if="activeTab === 1" class="buy-cards">
        <div class="section-label">选择面额</div>
        <div class="price-grid">
          <div 
            v-for="price in priceOptions" 
            :key="price" 
            class="price-card" 
            :class="{ active: selectedPrice === price }"
            @click="selectedPrice = price"
          >
            <div class="p-val">¥{{ price }}</div>
          </div>
        </div>
        <button class="primary-btn" @click="handleBuy" :disabled="buying">
          {{ buying ? '处理中...' : '立即购买' }}
        </button>
      </div>

      <!-- 激活礼品卡 -->
      <div v-if="activeTab === 2" class="activate-card">
        <div class="form-group">
          <div class="input-item">
            <label>卡号</label>
            <input v-model="activateForm.cardNo" placeholder="请输入礼品卡卡号" />
          </div>
          <div class="input-item">
            <label>券码</label>
            <input v-model="activateForm.cardCode" placeholder="请输入8位激活券码" />
          </div>
        </div>
        <button class="primary-btn" @click="handleActivate" :disabled="activating">
          {{ activating ? '激活中...' : '立即激活' }}
        </button>
        <div class="usage-tips">
          <h4>温馨提示：</h4>
          <ul>
            <li>请输入正确的卡号和券码进行激活。</li>
            <li>激活后礼品卡将绑定至您的账户，不可转让。</li>
            <li>礼品卡余额可用于支付订单。</li>
          </ul>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { giftCardApi } from '@/utils/api'
import { formatDate } from '@/utils/util'

const tabs = ['我的礼品卡', '购买礼品卡', '激活礼品卡']
const activeTab = ref(0)
const giftCards = ref([])
const priceOptions = [50, 100, 200, 500, 1000]
const selectedPrice = ref(100)
const buying = ref(false)
const activating = ref(false)

const activateForm = ref({
  cardNo: '',
  cardCode: ''
})

const fetchGiftCards = async () => {
  try {
    const res = await giftCardApi.getMyGiftCards()
    // 假设拦截器返回了 res.data
    giftCards.value = res || []
  } catch (error) {
    console.error('获取礼品卡失败', error)
  }
}

const handleBuy = async () => {
  buying.value = true
  try {
    await giftCardApi.buyGiftCard(selectedPrice.value)
    alert('购买成功')
    activeTab.value = 0
    fetchGiftCards()
  } catch (error) {
    alert(error.message || '购买失败')
  } finally {
    buying.value = false
  }
}

const handleActivate = async () => {
  if (!activateForm.value.cardNo || !activateForm.value.cardCode) {
    alert('请填写完整信息')
    return
  }
  activating.value = true
  try {
    await giftCardApi.activateGiftCard(activateForm.value.cardNo, activateForm.value.cardCode)
    alert('激活成功')
    activateForm.value = { cardNo: '', cardCode: '' }
    activeTab.value = 0
    fetchGiftCards()
  } catch (error) {
    alert(error.message || '激活失败')
  } finally {
    activating.value = false
  }
}

const getStatusText = (status) => {
  const map = {
    'ACTIVE': '已激活',
    'UNUSED': '待激活',
    'USED': '已用完',
    'EXPIRED': '已过期'
  }
  return map[status] || status
}

onMounted(() => {
  fetchGiftCards()
})
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 - 礼品卡页面 */
.gift-card-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰元素 */
.gift-card-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 10% 30%, rgba(255, 192, 203, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 90% 70%, rgba(222, 184, 135, 0.1) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 导航头部 */
.nav-header {
  display: flex;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--surface-color);
  border-bottom: 2px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.back-icon {
  font-size: 24px;
  margin-right: var(--spacing-md);
  cursor: pointer;
  color: var(--primary-color);
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--accent-cream);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
}

.back-icon:hover {
  transform: translateY(-2px) scale(1.05);
  background: var(--primary-light);
  box-shadow: 0 6px 16px rgba(160, 82, 45, 0.2);
}

.back-icon:active {
  transform: translateY(0) scale(0.98);
}

.title {
  font-size: 22px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  letter-spacing: 0.5px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

/* 标签页导航 */
.tabs-nav {
  display: flex;
  background: var(--surface-color);
  border-bottom: 2px solid var(--border-color);
  margin: 0 var(--spacing-lg);
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
  overflow: hidden;
  position: relative;
  z-index: 1;
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: var(--spacing-md) 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-color-medium);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  background: var(--accent-cream);
}

.tab-item:hover:not(.active) {
  background: rgba(255, 255, 255, 0.8);
  color: var(--primary-color);
}

.tab-item.active {
  background: white;
  color: var(--primary-color);
  font-weight: 700;
  box-shadow: 0 -2px 8px rgba(160, 82, 45, 0.1);
}

.tab-item.active::before {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  right: 20%;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color));
  border-radius: 2px;
}

/* 标签页内容 */
.tab-content {
  padding: var(--spacing-lg);
  position: relative;
  z-index: 1;
  animation: fadeIn 0.4s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 我的礼品卡 */
.card-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-lg);
}

.card-item {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  color: var(--accent-cream);
  position: relative;
  overflow: hidden;
  box-shadow:
      0 10px 30px rgba(139, 69, 19, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.card-item:hover {
  transform: translateY(-4px);
  box-shadow:
      0 15px 40px rgba(139, 69, 19, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.card-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255, 192, 203, 0.1) 0%, transparent 50%);
}

/* 礼品卡状态样式 */
.card-item.used {
  background: linear-gradient(135deg, var(--text-color-light), #ccc);
}

.card-item.expired {
  background: linear-gradient(135deg, #999, #777);
}

.card-item.unused {
  background: linear-gradient(135deg, var(--accent-pink), var(--primary-light));
}

/* 卡片内容 */
.card-main {
  position: relative;
  z-index: 1;
}

.card-no {
  font-size: 12px;
  font-weight: 600;
  opacity: 0.9;
  margin-bottom: var(--spacing-md);
  letter-spacing: 1px;
}

.card-balance {
  display: flex;
  align-items: baseline;
  margin-bottom: var(--spacing-sm);
}

.card-balance .unit {
  font-size: 20px;
  font-weight: 700;
  margin-right: 4px;
  opacity: 0.9;
}

.card-balance .val {
  font-size: 36px;
  font-weight: 800;
  font-family: var(--font-heading);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.card-face {
  font-size: 13px;
  font-weight: 600;
  opacity: 0.8;
  margin-top: var(--spacing-xs);
}

/* 卡片徽章 */
.card-badge {
  position: absolute;
  top: var(--spacing-md);
  right: var(--spacing-md);
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.25);
  border-radius: var(--border-radius-md);
  font-size: 11px;
  font-weight: 700;
  letter-spacing: 0.5px;
  backdrop-filter: blur(4px);
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.card-badge.active {
  background: rgba(54, 207, 201, 0.3);
  color: #36CFC9;
}

.card-badge.used {
  background: rgba(153, 153, 153, 0.3);
  color: #666;
}

.card-badge.expired {
  background: rgba(153, 153, 153, 0.3);
  color: #666;
}

.card-badge.unused {
  background: rgba(255, 107, 107, 0.3);
  color: #ff6b6b;
}

/* 卡片页脚 */
.card-footer {
  margin-top: var(--spacing-md);
  font-size: 11px;
  text-align: right;
  opacity: 0.7;
  position: relative;
  z-index: 1;
  font-weight: 500;
}

/* 空状态 */
.empty-box {
  text-align: center;
  padding: 80px var(--spacing-lg);
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  border: 2px dashed var(--border-color);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 0.9;
  }
  50% {
    opacity: 1;
  }
}

.empty-icon {
  font-size: 60px;
  margin-bottom: var(--spacing-md);
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

.empty-box p {
  font-size: 15px;
  color: var(--text-color-medium);
  font-weight: 600;
}

/* 购买礼品卡 */
.section-label {
  font-size: 17px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  margin-bottom: var(--spacing-lg);
  position: relative;
  padding-left: var(--spacing-md);
}

.section-label::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 20px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
  border-radius: 3px;
}

.price-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-xl);
}

.price-card {
  background: var(--surface-color);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg) 0;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

.price-card:hover {
  transform: translateY(-4px);
  border-color: var(--primary-light);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.15);
}

.price-card.active {
  background: linear-gradient(135deg, var(--accent-cream), white);
  border-color: var(--primary-color);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.2);
  color: var(--primary-color);
}

.price-card.active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color));
}

.p-val {
  font-size: 22px;
  font-weight: 800;
  font-family: var(--font-heading);
  position: relative;
  z-index: 1;
}

/* 激活礼品卡 */
.form-group {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  padding: 0 var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  box-shadow:
      0 4px 16px rgba(139, 69, 19, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.input-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid var(--border-color);
  transition: all 0.3s ease-out;
}

.input-item:hover {
  background: var(--accent-cream);
  padding-left: 8px;
}

.input-item:last-child {
  border-bottom: none;
}

.input-item label {
  width: 70px;
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color-dark);
  position: relative;
  padding-left: var(--spacing-sm);
}

.input-item label::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: var(--primary-color);
  border-radius: 2px;
  opacity: 0.6;
  transition: all 0.3s ease-out;
}

.input-item:hover label::before {
  opacity: 1;
  background: var(--accent-pink);
  transform: translateY(-50%) scale(1.1);
}

.input-item input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
  color: var(--text-color-dark);
  background: transparent;
  font-family: var(--font-body);
  padding: var(--spacing-sm) 0;
}

.input-item input::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

.input-item input:focus {
  color: var(--primary-color);
}

/* 使用提示 */
.usage-tips {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--accent-cream);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
  position: relative;
  overflow: hidden;
}

.usage-tips::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
}

.usage-tips h4 {
  font-size: 15px;
  font-weight: 700;
  color: var(--primary-dark);
  margin-bottom: var(--spacing-sm);
  position: relative;
  z-index: 1;
}

.usage-tips ul {
  padding-left: var(--spacing-md);
  font-size: 13px;
  line-height: 1.8;
  color: var(--text-color-medium);
  position: relative;
  z-index: 1;
}

.usage-tips li {
  margin-bottom: var(--spacing-xs);
}

/* 主要按钮 */
.primary-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  border: none;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-xl);
  font-size: 16px;
  font-weight: 700;
  font-family: var(--font-body);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.3);
  margin-bottom: var(--spacing-lg);
}

.primary-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease-out;
}

.primary-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(160, 82, 45, 0.4);
}

.primary-btn:hover::before {
  left: 100%;
}

.primary-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.primary-btn:disabled {
  background: var(--border-color);
  color: var(--text-color-light);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.primary-btn:disabled::before {
  display: none;
}

/* 装饰元素 */
.gift-card-page::after {
  content: '';
  position: fixed;
  bottom: -150px;
  right: -150px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, var(--accent-brown) 0%, transparent 70%);
  opacity: 0.08;
  border-radius: 50%;
  z-index: 0;
  animation: float 25s ease-in-out infinite;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .gift-card-page {
    padding: var(--spacing-sm);
  }

  .nav-header {
    padding: var(--spacing-sm) var(--spacing-md);
  }

  .tabs-nav {
    margin: 0 var(--spacing-md);
  }

  .tab-content {
    padding: var(--spacing-md);
  }

  .price-grid {
    grid-template-columns: repeat(2, 1fr);
  }

  .card-balance .val {
    font-size: 32px;
  }
}

@media (max-width: 480px) {
  .gift-card-page {
    padding: var(--spacing-xs);
  }

  .nav-header {
    padding: var(--spacing-xs) var(--spacing-sm);
  }

  .tabs-nav {
    margin: 0 var(--spacing-sm);
  }

  .back-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .title {
    font-size: 18px;
  }

  .tab-item {
    padding: var(--spacing-sm) 0;
    font-size: 14px;
  }

  .price-grid {
    grid-template-columns: 1fr;
  }

  .card-item {
    padding: var(--spacing-md);
  }

  .card-balance .val {
    font-size: 28px;
  }
}

/* 自定义滚动条 */
.gift-card-page::-webkit-scrollbar {
  width: 8px;
}

.gift-card-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.gift-card-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.gift-card-page::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
}
</style>