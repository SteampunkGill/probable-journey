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
.gift-card-page {
  min-height: 100vh;
  background-color: #f8f9fa;
}

.nav-header {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.back-icon {
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.tabs-nav {
  display: flex;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 15px 0;
  font-size: 14px;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #D4A574;
  font-weight: bold;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 20%;
  right: 20%;
  height: 3px;
  background: #D4A574;
  border-radius: 3px;
}

.tab-content {
  padding: 15px;
}

.card-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.card-item {
  background: linear-gradient(135deg, #D4A574, #B08968);
  border-radius: 12px;
  padding: 20px;
  color: #fff;
  position: relative;
  box-shadow: 0 4px 10px rgba(212, 165, 116, 0.3);
}

.card-item.used, .card-item.expired {
  background: #ccc;
  box-shadow: none;
}

.card-no {
  font-size: 12px;
  opacity: 0.8;
  margin-bottom: 10px;
}

.card-balance .unit {
  font-size: 18px;
  margin-right: 4px;
}

.card-balance .val {
  font-size: 30px;
  font-weight: bold;
}

.card-face {
  font-size: 13px;
  margin-top: 5px;
  opacity: 0.9;
}

.card-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 4px 8px;
  background: rgba(255,255,255,0.2);
  border-radius: 4px;
  font-size: 11px;
}

.card-footer {
  margin-top: 15px;
  font-size: 11px;
  text-align: right;
  opacity: 0.7;
}

.empty-box {
  text-align: center;
  padding: 100px 0;
  color: #999;
}

.empty-icon {
  font-size: 50px;
  margin-bottom: 10px;
}

.section-label {
  font-size: 15px;
  font-weight: bold;
  margin-bottom: 15px;
  color: #333;
}

.price-grid {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
  margin-bottom: 30px;
}

.price-card {
  background: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px 0;
  text-align: center;
  cursor: pointer;
}

.price-card.active {
  border-color: #D4A574;
  background: #FFF9E6;
  color: #D4A574;
}

.p-val {
  font-size: 18px;
  font-weight: bold;
}

.primary-btn {
  width: 100%;
  background: #D4A574;
  color: #fff;
  border: none;
  padding: 15px;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.primary-btn:disabled {
  background: #ccc;
}

.form-group {
  background: #fff;
  border-radius: 8px;
  padding: 0 15px;
  margin-bottom: 20px;
}

.input-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #eee;
}

.input-item:last-child {
  border-bottom: none;
}

.input-item label {
  width: 60px;
  font-size: 14px;
  color: #333;
}

.input-item input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 14px;
}

.usage-tips {
  margin-top: 30px;
  color: #999;
}

.usage-tips h4 {
  font-size: 14px;
  margin-bottom: 10px;
}

.usage-tips ul {
  padding-left: 20px;
  font-size: 12px;
  line-height: 1.8;
}
</style>