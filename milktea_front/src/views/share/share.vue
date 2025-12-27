<template>
  <div class="share-page">
    <div class="header">
      <i class="iconfont icon-left" @click="router.back()"></i>
      <span class="title">分享有礼</span>
    </div>

    <div class="share-banner card">
      <img src="@/assets/images/banners/default.jpg" class="banner-img" />
      <div class="banner-content">
        <h2>邀请好友 共享美味</h2>
        <p>好友注册即得优惠券，您也将获得奖励</p>
      </div>
    </div>

    <div class="reward-section card">
      <div class="section-title">我的奖励</div>
      <div class="reward-stats">
        <div class="stat-item">
          <span class="value">{{ stats.inviteCount || 0 }}</span>
          <span class="label">成功邀请</span>
        </div>
        <div class="stat-item">
          <span class="value">¥{{ stats.totalReward || 0 }}</span>
          <span class="label">累计奖励</span>
        </div>
      </div>
    </div>

    <div class="share-actions card">
      <div class="section-title">立即分享</div>
      <div class="action-buttons">
        <div class="action-btn" @click="handleShare('wechat')">
          <div class="icon-wrapper wechat">
            <i class="iconfont icon-wechat"></i>
          </div>
          <span>微信好友</span>
        </div>
        <div class="action-btn" @click="handleShare('poster')">
          <div class="icon-wrapper poster">
            <i class="iconfont icon-pic"></i>
          </div>
          <span>生成海报</span>
        </div>
        <div class="action-btn" @click="copyLink">
          <div class="icon-wrapper link">
            <i class="iconfont icon-link"></i>
          </div>
          <span>复制链接</span>
        </div>
      </div>
    </div>

    <div class="rules-section card">
      <div class="section-title">活动规则</div>
      <ul class="rules-list">
        <li>1. 分享您的专属链接或海报给好友。</li>
        <li>2. 好友通过您的链接完成注册，即可获得新人大礼包。</li>
        <li>3. 好友完成首单消费后，您将获得一张5元无门槛优惠券。</li>
        <li>4. 奖励将在好友订单完成后自动发放至您的账户。</li>
      </ul>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { shareApi } from '@/utils/api'

const router = useRouter()
const stats = ref({
  inviteCount: 0,
  totalReward: 0
})

const loadStats = async () => {
  try {
    const res = await shareApi.getShareStats()
    if (res.code === 200) {
      stats.value = res.data
    }
  } catch (error) {
    console.error('加载分享统计失败:', error)
  }
}

const handleShare = (type) => {
  alert(`分享功能开发中，类型: ${type}`)
}

const copyLink = () => {
  const link = `${window.location.origin}/register?inviteCode=${stats.value.inviteCode || 'USER123'}`
  navigator.clipboard.writeText(link).then(() => {
    alert('链接已复制到剪贴板')
  })
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 - 分享有礼页面 */
.share-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  padding: var(--spacing-md);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰元素 */
.share-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 30%, rgba(255, 192, 203, 0.15) 0%, transparent 50%),
      radial-gradient(circle at 80% 70%, rgba(222, 184, 135, 0.15) 0%, transparent 50%);
  pointer-events: none;
  z-index: 0;
}

/* 头部 */
.header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-sm) 0;
  position: relative;
  z-index: 1;
}

.iconfont.icon-left {
  font-size: 24px;
  color: var(--primary-color);
  cursor: pointer;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-cream);
  border-radius: var(--border-radius-xl);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
}

.iconfont.icon-left:hover {
  transform: translateY(-2px) scale(1.05);
  background: var(--primary-light);
  box-shadow: 0 6px 16px rgba(160, 82, 45, 0.2);
}

.iconfont.icon-left:active {
  transform: translateY(0) scale(0.98);
}

.title {
  font-size: 22px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  letter-spacing: 0.5px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
  position: relative;
  padding-left: var(--spacing-sm);
}

.title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 20px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
  border-radius: 2px;
}

/* 卡片通用样式 */
.card {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-lg);
  overflow: hidden;
  box-shadow:
      0 8px 32px rgba(139, 69, 19, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease-out;
}

.card:hover {
  transform: translateY(-2px);
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color), var(--accent-brown));
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

/* 分享横幅 */
.share-banner {
  padding: 0;
  position: relative;
  height: 200px;
  overflow: hidden;
  border-radius: var(--border-radius-xl);
}

.banner-img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s ease-out;
}

.share-banner:hover .banner-img {
  transform: scale(1.05);
}

.banner-content {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(160, 82, 45, 0.7), rgba(255, 192, 203, 0.5));
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  color: white;
  text-align: center;
  padding: var(--spacing-lg);
  backdrop-filter: blur(2px);
}

.banner-content h2 {
  font-size: 28px;
  font-weight: 700;
  font-family: var(--font-heading);
  margin-bottom: var(--spacing-sm);
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
  letter-spacing: 1px;
}

.banner-content p {
  font-size: 15px;
  font-weight: 500;
  opacity: 0.9;
  max-width: 300px;
  line-height: 1.6;
}

/* 区域标题 */
.section-title {
  font-size: 18px;
  font-weight: 700;
  font-family: var(--font-heading);
  margin-bottom: var(--spacing-lg);
  color: var(--primary-dark);
  position: relative;
  padding-left: var(--spacing-md);
}

.section-title::before {
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

/* 奖励统计 */
.reward-stats {
  display: flex;
  justify-content: space-around;
  gap: var(--spacing-md);
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-md);
  background: var(--accent-cream);
  border-radius: var(--border-radius-lg);
  flex: 1;
  transition: all 0.3s ease-out;
  border: 2px solid transparent;
  position: relative;
  overflow: hidden;
}

.stat-item:hover {
  transform: translateY(-4px);
  border-color: var(--primary-light);
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.15);
}

.stat-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color));
}

.stat-item .value {
  font-size: 32px;
  font-weight: 800;
  color: var(--primary-color);
  margin-bottom: var(--spacing-xs);
  font-family: var(--font-heading);
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
}

.stat-item .label {
  font-size: 13px;
  color: var(--text-color-medium);
  font-weight: 500;
  letter-spacing: 0.5px;
}

/* 分享操作按钮 */
.action-buttons {
  display: flex;
  justify-content: space-around;
  gap: var(--spacing-md);
}

.action-btn {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: var(--spacing-sm);
  cursor: pointer;
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  flex: 1;
  background: var(--accent-cream);
  position: relative;
  overflow: hidden;
}

.action-btn:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
}

.action-btn:hover::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), transparent);
}

.icon-wrapper {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 28px;
  transition: all 0.3s ease-out;
  position: relative;
  z-index: 1;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.2);
}

.action-btn:hover .icon-wrapper {
  transform: scale(1.1) rotate(5deg);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.icon-wrapper.wechat {
  background: linear-gradient(135deg, #07C160, #05A854);
}

.icon-wrapper.poster {
  background: linear-gradient(135deg, #FF9800, #F57C00);
}

.icon-wrapper.link {
  background: linear-gradient(135deg, #2196F3, #1976D2);
}

.action-btn span {
  font-size: 14px;
  font-weight: 600;
  color: var(--text-color-dark);
  transition: all 0.3s ease-out;
  position: relative;
  z-index: 1;
}

.action-btn:hover span {
  color: var(--primary-color);
}

/* 规则列表 */
.rules-list {
  padding-left: 0;
  list-style: none;
  display: flex;
  flex-direction: column;
  gap: var(--spacing-sm);
}

.rules-list li {
  font-size: 14px;
  color: var(--text-color-medium);
  line-height: 1.7;
  padding: var(--spacing-sm) var(--spacing-md);
  background: var(--accent-cream);
  border-radius: var(--border-radius-md);
  border-left: 4px solid var(--primary-light);
  transition: all 0.3s ease-out;
  position: relative;
  padding-left: calc(var(--spacing-md) + 8px);
}

.rules-list li::before {
  content: '';
  position: absolute;
  left: 8px;
  top: 50%;
  transform: translateY(-50%);
  width: 6px;
  height: 6px;
  background: var(--primary-color);
  border-radius: 50%;
}

.rules-list li:hover {
  transform: translateX(4px);
  background: var(--surface-color);
  border-left-color: var(--accent-pink);
}

/* 装饰元素 */
.share-page::after {
  content: '';
  position: fixed;
  bottom: -150px;
  right: -150px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.08;
  border-radius: 50%;
  z-index: 0;
  animation: float 25s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-50px, -30px) rotate(120deg);
  }
  66% {
    transform: translate(30px, -60px) rotate(240deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .share-page {
    padding: var(--spacing-sm);
  }

  .header {
    margin-bottom: var(--spacing-md);
  }

  .title {
    font-size: 20px;
  }

  .card {
    padding: var(--spacing-md);
  }

  .share-banner {
    height: 160px;
  }

  .banner-content h2 {
    font-size: 24px;
  }

  .stat-item .value {
    font-size: 28px;
  }

  .icon-wrapper {
    width: 50px;
    height: 50px;
    font-size: 24px;
  }

  .action-btn span {
    font-size: 13px;
  }
}

@media (max-width: 480px) {
  .share-page {
    padding: var(--spacing-xs);
  }

  .header {
    gap: var(--spacing-sm);
  }

  .iconfont.icon-left {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .title {
    font-size: 18px;
  }

  .share-banner {
    height: 140px;
  }

  .banner-content h2 {
    font-size: 20px;
  }

  .banner-content p {
    font-size: 13px;
  }

  .reward-stats {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .action-buttons {
    flex-direction: column;
    gap: var(--spacing-sm);
  }

  .rules-list li {
    font-size: 13px;
  }
}

/* 自定义滚动条 */
.share-page::-webkit-scrollbar {
  width: 8px;
}

.share-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.share-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.share-page::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
}

/* 加载状态 */
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

/* 复制链接成功提示 */
:global(.copy-success) {
  background: var(--primary-color) !important;
  color: white !important;
  border-radius: var(--border-radius-md) !important;
  padding: var(--spacing-sm) var(--spacing-md) !important;
  box-shadow: 0 4px 16px rgba(160, 82, 45, 0.3) !important;
}
</style>