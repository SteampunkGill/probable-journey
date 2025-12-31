<template>
  <div class="wallet-page">
    <!-- 余额卡片 -->
    <div class="balance-card">
      <div class="card-bg"></div>
      <div class="card-content">
        <span class="balance-label">账户余额（元）</span>
        <h1 class="balance-amount">{{ balance.toFixed(2) }}</h1>
        
        <div class="card-actions">
          <button class="action-btn" @click="showRechargeModal = true">
            <span class="icon">💰</span>
            <span>充值</span>
          </button>
          <button class="action-btn" @click="withdraw">
            <span class="icon">💸</span>
            <span>提现</span>
          </button>
        </div>
      </div>
    </div>

    <!-- 功能菜单 -->
    <div class="menu-section">
      <div class="menu-item" @click="showTransactions = true">
        <div class="menu-left">
          <span class="menu-icon">📋</span>
          <span class="menu-title">交易明细</span>
        </div>
        <span class="menu-arrow">›</span>
      </div>
      
      <div class="menu-item" @click="alert('暂无充值优惠')">
        <div class="menu-left">
          <span class="menu-icon">🎁</span>
          <span class="menu-title">充值优惠</span>
        </div>
        <span class="menu-arrow">›</span>
      </div>
    </div>

    <!-- 说明 -->
    <div class="tips-section">
      <h3 class="tips-title">💡 温馨提示</h3>
      <p class="tips-text">• 余额可用于支付订单</p>
      <p class="tips-text">• 提现将在1-3个工作日内到账</p>
      <p class="tips-text">• 充值赠送积分，多充多送</p>
    </div>

    <!-- 充值弹窗 -->
    <div v-if="showRechargeModal" class="modal-mask" @click="showRechargeModal = false">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>选择充值金额</h3>
          <span class="close-btn" @click="showRechargeModal = false">×</span>
        </div>
        <div class="recharge-options">
          <div 
            v-for="amount in rechargeAmounts" 
            :key="amount"
            class="recharge-item"
            :class="{ active: selectedAmount === amount }"
            @click="selectedAmount = amount"
          >
            <span class="amount">¥{{ amount }}</span>
          </div>
        </div>
        <div class="custom-amount">
          <input 
            type="number" 
            v-model="customAmount" 
            placeholder="输入其他金额"
            @focus="selectedAmount = null"
          />
        </div>
        <button 
          class="confirm-btn" 
          :disabled="submitting || (!selectedAmount && !customAmount)"
          @click="handleRecharge"
        >
          {{ submitting ? '处理中...' : '立即充值' }}
        </button>
      </div>
    </div>

    <!-- 交易明细弹窗 -->
    <div v-if="showTransactions" class="modal-mask" @click="showTransactions = false">
      <div class="modal-content transactions-modal" @click.stop>
        <div class="modal-header">
          <h3>交易明细</h3>
          <span class="close-btn" @click="showTransactions = false">×</span>
        </div>
        <div class="transactions-list" v-if="transactions.length > 0">
          <div v-for="item in transactions" :key="item.id" class="transaction-item">
            <div class="item-left">
              <span class="item-title">{{ formatType(item.type) }}</span>
              <span class="item-time">{{ formatDate(item.createdAt) }}</span>
            </div>
            <div class="item-right" :class="{ plus: item.amount > 0 }">
              {{ item.amount > 0 ? '+' : '' }}{{ item.amount.toFixed(2) }}
            </div>
          </div>
        </div>
        <div v-else class="empty-transactions">
          暂无交易记录
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { walletApi } from '../../utils/api'

const balance = ref(0)
const showRechargeModal = ref(false)
const showTransactions = ref(false)
const rechargeAmounts = [10, 20, 50, 100, 200, 500]
const selectedAmount = ref(50)
const customAmount = ref('')
const submitting = ref(false)
const transactions = ref([])

const loadBalance = async () => {
  try {
    const res = await walletApi.getBalance()
    if (res.code === 200) {
      balance.value = res.data.balance || 0
    }
  } catch (error) {
    console.error('加载余额失败:', error)
  }
}

const loadTransactions = async () => {
  try {
    const res = await walletApi.getTransactions()
    if (res.code === 200) {
      transactions.value = res.data || []
    }
  } catch (error) {
    console.error('加载交易记录失败:', error)
  }
}

const handleRecharge = async () => {
  const amount = selectedAmount.value || parseFloat(customAmount.value)
  if (!amount || amount <= 0) return

  submitting.value = true
  try {
    const res = await walletApi.recharge(amount)
    if (res.code === 200) {
      alert('充值成功！')
      showRechargeModal.value = false
      loadBalance()
      loadTransactions()
    } else {
      alert(res.message || '充值失败')
    }
  } catch (error) {
    console.error('充值失败:', error)
    alert('充值失败，请重试')
  } finally {
    submitting.value = false
  }
}

const withdraw = () => {
  alert('提现功能暂未开放，请联系客服')
}

const formatType = (type) => {
  const map = {
    'RECHARGE': '在线充值',
    'CONSUME': '订单消费',
    'WITHDRAW': '余额提现',
    'REFUND': '订单退款'
  }
  return map[type] || type
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}-${date.getDate()} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

watch(showTransactions, (val) => {
  if (val) loadTransactions()
})

onMounted(() => {
  loadBalance()
})
</script>

<style scoped>
.wallet-page {
  min-height: 100vh;
  background: #f8f8f8;
  padding: 20px;
}

.balance-card {
  height: 180px;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  color: white;
  margin-bottom: 20px;
  box-shadow: 0 10px 20px rgba(255, 107, 0, 0.2);
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #ff9d00, #ff6b00);
  z-index: 1;
}

.card-content {
  position: relative;
  height: 100%;
  padding: 25px;
  display: flex;
  flex-direction: column;
  z-index: 2;
}

.balance-label {
  font-size: 14px;
  opacity: 0.9;
}

.balance-amount {
  font-size: 40px;
  font-weight: bold;
  margin: 10px 0;
}

.card-actions {
  display: flex;
  gap: 15px;
  margin-top: auto;
}

.action-btn {
  flex: 1;
  height: 36px;
  background: rgba(255, 255, 255, 0.2);
  border: 1px solid rgba(255, 255, 255, 0.4);
  border-radius: 18px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 14px;
  cursor: pointer;
}

.menu-section {
  background: white;
  border-radius: 12px;
  overflow: hidden;
  margin-bottom: 20px;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #f5f5f5;
  cursor: pointer;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.menu-icon {
  font-size: 20px;
}

.menu-title {
  font-size: 15px;
  color: #333;
}

.menu-arrow {
  color: #ccc;
  font-size: 20px;
}

.tips-section {
  background: #fff9f5;
  border-radius: 12px;
  padding: 15px;
}

.tips-title {
  font-size: 15px;
  color: #ff6b00;
  margin-bottom: 10px;
}

.tips-text {
  font-size: 13px;
  color: #999;
  margin-bottom: 5px;
}

/* 弹窗样式 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: flex-end;
  z-index: 100;
}

.modal-content {
  width: 100%;
  background: white;
  border-radius: 20px 20px 0 0;
  padding: 20px;
  animation: slideUp 0.3s ease-out;
}

@keyframes slideUp {
  from { transform: translateY(100%); }
  to { transform: translateY(0); }
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.close-btn {
  font-size: 28px;
  color: #999;
  cursor: pointer;
}

.recharge-options {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.recharge-item {
  height: 60px;
  border: 1px solid #eee;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
}

.recharge-item.active {
  border-color: #ff6b00;
  background: #fff9f5;
  color: #ff6b00;
}

.custom-amount input {
  width: 100%;
  height: 44px;
  border: 1px solid #eee;
  border-radius: 8px;
  padding: 0 15px;
  margin-bottom: 20px;
}

.confirm-btn {
  width: 100%;
  height: 48px;
  background: #ff6b00;
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: bold;
}

.confirm-btn:disabled {
  background: #ccc;
}

.transactions-modal {
  max-height: 70vh;
  display: flex;
  flex-direction: column;
}

.transactions-list {
  flex: 1;
  overflow-y: auto;
}

.transaction-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #f5f5f5;
}

.item-left {
  display: flex;
  flex-direction: column;
  gap: 5px;
}

.item-title {
  font-size: 15px;
  color: #333;
}

.item-time {
  font-size: 12px;
  color: #999;
}

.item-right {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.item-right.plus {
  color: #ff6b00;
}

.empty-transactions {
  text-align: center;
  padding: 50px 0;
  color: #999;
}
</style>