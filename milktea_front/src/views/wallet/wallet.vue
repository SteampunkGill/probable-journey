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
import { userApi } from '../../utils/api'

const balance = ref(0)

onMounted(() => {
  loadBalance()
})

import { authApi } from '../../utils/api'

const loadBalance = async () => {
  try {
    const res = await authApi.getUserProfile()
    if (res.code === 200) {
      balance.value = res.data.balance || 0
    }
  } catch (error) {
    console.error('加载余额失败:', error)
  }
}

const recharge = () => {
  // 实际项目中应跳转到充值页面或调用支付接口
  alert('充值功能请前往柜台或使用支付宝/微信充值')
}

const withdraw = () => {
  alert('提现申请已提交，请等待审核')
}

const viewDetail = () => {
  // 实际项目中应跳转到明细列表页
  alert('暂无更多交易明细')
}
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 - 钱包页面 */
.wallet-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  position: relative;
  overflow-x: hidden;
  padding: var(--spacing-lg);
}

/* 背景装饰元素 */
.wallet-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 80%, rgba(255, 248, 220, 0.15) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(222, 184, 135, 0.15) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 余额卡片 */
.balance-card {
  height: 220px;
  position: relative;
  border-radius: var(--border-radius-xl);
  overflow: hidden;
  color: var(--accent-cream);
  margin-bottom: var(--spacing-xl);
  box-shadow:
      0 15px 40px rgba(139, 69, 19, 0.25),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.balance-card:hover {
  transform: translateY(-5px);
  box-shadow:
      0 20px 50px rgba(139, 69, 19, 0.35),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
}

.card-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  z-index: 1;
}

.card-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 30% 30%, rgba(255, 192, 203, 0.15) 0%, transparent 50%),
      radial-gradient(circle at 70% 70%, rgba(255, 248, 220, 0.15) 0%, transparent 50%);
}

.card-content {
  position: relative;
  height: 100%;
  padding: var(--spacing-xl);
  display: flex;
  flex-direction: column;
  z-index: 2;
}

.balance-label {
  font-size: 15px;
  font-weight: 600;
  opacity: 0.9;
  letter-spacing: 0.5px;
  margin-bottom: var(--spacing-sm);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.2);
}

.balance-amount {
  font-size: 56px;
  font-weight: 800;
  font-family: var(--font-heading);
  margin: var(--spacing-sm) 0 var(--spacing-lg);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
  position: relative;
  display: inline-block;
}

.balance-amount::after {
  content: '';
  position: absolute;
  bottom: -8px;
  left: 0;
  width: 100%;
  height: 3px;
  background: linear-gradient(to right, transparent, var(--accent-cream), transparent);
  border-radius: 2px;
}

.card-actions {
  display: flex;
  gap: var(--spacing-md);
  margin-top: auto;
}

.action-btn {
  flex: 1;
  height: 44px;
  background: rgba(255, 255, 255, 0.15);
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: var(--border-radius-xl);
  color: var(--accent-cream);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: var(--spacing-xs);
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  backdrop-filter: blur(5px);
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease-out;
}

.action-btn:hover {
  background: rgba(255, 255, 255, 0.25);
  border-color: rgba(255, 255, 255, 0.5);
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.action-btn:hover::before {
  left: 100%;
}

.action-btn:active {
  transform: translateY(0);
}

.action-btn .icon {
  font-size: 18px;
  transition: transform 0.3s ease-out;
}

.action-btn:hover .icon {
  transform: scale(1.1) rotate(5deg);
}

/* 功能菜单 */
.menu-section {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-xl);
  box-shadow:
      0 8px 32px rgba(139, 69, 19, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 1;
}

.menu-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color), var(--accent-brown));
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

.menu-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-xl);
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  background: var(--surface-color);
  position: relative;
  overflow: hidden;
}

.menu-item:last-child {
  border-bottom: none;
}

.menu-item:hover {
  background: var(--accent-cream);
  padding-left: calc(var(--spacing-xl) + 8px);
}

.menu-item:hover::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
  border-radius: 0 3px 3px 0;
}

.menu-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  position: relative;
  z-index: 1;
}

.menu-icon {
  font-size: 24px;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.8);
  border-radius: var(--border-radius-md);
  transition: all 0.3s ease-out;
}

.menu-item:hover .menu-icon {
  transform: scale(1.1);
  background: white;
}

.menu-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-color-dark);
  transition: all 0.3s ease-out;
}

.menu-item:hover .menu-title {
  color: var(--primary-color);
}

.menu-arrow {
  font-size: 24px;
  color: var(--text-color-light);
  transition: all 0.3s ease-out;
  position: relative;
  z-index: 1;
}

.menu-item:hover .menu-arrow {
  color: var(--primary-color);
  transform: translateX(4px);
}

/* 提示说明 */
.tips-section {
  background: var(--accent-cream);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  box-shadow:
      0 4px 16px rgba(139, 69, 19, 0.08),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 1;
  overflow: hidden;
}

.tips-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
  border-radius: 2px;
}

.tips-title {
  font-size: 16px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  margin-bottom: var(--spacing-md);
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
}

.tips-title::before {
  content: '';
  width: 8px;
  height: 8px;
  background: var(--primary-color);
  border-radius: 50%;
  opacity: 0.6;
}

.tips-text {
  font-size: 14px;
  color: var(--text-color-medium);
  margin-bottom: var(--spacing-sm);
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  position: relative;
  z-index: 1;
  padding-left: var(--spacing-sm);
  transition: all 0.3s ease-out;
}

.tips-text:hover {
  color: var(--primary-color);
  transform: translateX(4px);
}

.tips-text::before {
  content: '•';
  color: var(--primary-light);
  font-size: 18px;
  transition: all 0.3s ease-out;
}

.tips-text:hover::before {
  color: var(--accent-pink);
  transform: scale(1.2);
}

/* 装饰元素 */
.wallet-page::after {
  content: '';
  position: fixed;
  bottom: -100px;
  right: -100px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.08;
  border-radius: 50%;
  z-index: 0;
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-40px, 20px) rotate(120deg);
  }
  66% {
    transform: translate(20px, -40px) rotate(240deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .wallet-page {
    padding: var(--spacing-md);
  }

  .balance-card {
    height: 200px;
    padding: var(--spacing-lg);
  }

  .balance-amount {
    font-size: 48px;
  }

  .card-actions {
    gap: var(--spacing-sm);
  }

  .action-btn {
    height: 40px;
    font-size: 14px;
  }

  .menu-item {
    padding: var(--spacing-md) var(--spacing-lg);
  }

  .menu-icon {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .menu-title {
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .wallet-page {
    padding: var(--spacing-sm);
  }

  .balance-card {
    height: 180px;
    padding: var(--spacing-md);
  }

  .balance-amount {
    font-size: 40px;
  }

  .balance-label {
    font-size: 14px;
  }

  .card-actions {
    flex-direction: column;
    gap: var(--spacing-xs);
  }

  .action-btn {
    height: 36px;
  }

  .menu-item {
    padding: var(--spacing-sm) var(--spacing-md);
  }

  .tips-section {
    padding: var(--spacing-md);
  }

  .tips-text {
    font-size: 13px;
  }
}

/* 自定义滚动条 */
.wallet-page::-webkit-scrollbar {
  width: 8px;
}

.wallet-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.wallet-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.wallet-page::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
}

/* 加载动画 */
.loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid var(--primary-light);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>