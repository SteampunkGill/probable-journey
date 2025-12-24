<template>
  <div class="wallet-page">
    <!-- 余额卡片 -->
    <div class="balance-card">
      <div class="card-bg"></div>
      <div class="card-content">
        <span class="balance-label">账户余额（元）</span>
        <h1 class="balance-amount">{{ balance.toFixed(2) }}</h1>
        
        <div class="card-actions">
          <button class="action-btn" @click="recharge">
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
      <div class="menu-item" @click="viewDetail">
        <div class="menu-left">
          <span class="menu-icon">📋</span>
          <span class="menu-title">交易明细</span>
        </div>
        <span class="menu-arrow">›</span>
      </div>
      
      <div class="menu-item" @click="viewDetail">
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
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const balance = ref(86.50)

onMounted(() => {
  // 加载数据
})

const recharge = () => {
  alert('充值功能开发中')
}

const withdraw = () => {
  alert('提现功能开发中')
}

const viewDetail = () => {
  alert('明细功能开发中')
}
</script>

<style scoped>
.wallet-page {
  min-height: 100vh;
  background: #F8F8F8;
  padding: 20px;
}

.balance-card {
  height: 180px;
  position: relative;
  border-radius: 16px;
  overflow: hidden;
  color: white;
  margin-bottom: 20px;
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
}

.balance-label {
  font-size: 14px;
  opacity: 0.9;
}

.balance-amount {
  font-size: 40px;
  font-weight: bold;
  margin: 10px 0 20px;
}

.card-actions {
  display: flex;
  gap: 15px;
}

.action-btn {
  flex: 1;
  height: 36px;
  background: rgba(255,255,255,0.2);
  border: 1px solid rgba(255,255,255,0.4);
  border-radius: 18px;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
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
  border-bottom: 1px solid #F5F5F5;
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
  font-size: 18px;
}

.menu-title {
  font-size: 15px;
  color: #333;
}

.menu-arrow {
  font-size: 20px;
  color: #CCC;
}

.tips-section {
  padding: 10px;
}

.tips-title {
  font-size: 14px;
  font-weight: bold;
  color: #666;
  margin-bottom: 10px;
}

.tips-text {
  font-size: 12px;
  color: #999;
  margin-bottom: 6px;
  display: block;
}
</style>