<template>
  <div class="security-page">
    <div class="header">
      <i class="iconfont icon-left" @click="router.back()"></i>
      <span class="title">账号安全</span>
    </div>

    <div class="security-list card">
      <div class="security-item" @click="handleChangePassword">
        <span class="label">修改密码</span>
        <i class="iconfont icon-right"></i>
      </div>
      <div class="security-item" @click="handleDeactivate">
        <span class="label">注销账号</span>
        <span class="value danger">注销后无法恢复</span>
        <i class="iconfont icon-right"></i>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { authApi } from '../../utils/api'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

const handleChangePassword = () => {
  router.push('/settings/change-password')
}

const handleDeactivate = async () => {
  if (confirm('确定要注销账号吗？注销后所有数据将被清空且无法恢复！')) {
    try {
      await authApi.deactivate()
      alert('账号已注销')
      userStore.logout()
      router.push('/login')
    } catch (error) {
      console.error('注销失败:', error)
      alert(error.message || '注销失败，请稍后重试')
    }
  }
}
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 - 账号安全页面 */
.security-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  padding: var(--spacing-md);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰元素 */
.security-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 80%, rgba(255, 192, 203, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(222, 184, 135, 0.1) 0%, transparent 50%);
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

/* 安全列表卡片 */
.security-list.card {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-lg);
  overflow: hidden;
  box-shadow:
      0 8px 32px rgba(139, 69, 19, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
  position: relative;
  z-index: 1;
  transition: transform 0.3s ease-out;
}

.security-list.card:hover {
  transform: translateY(-2px);
}

.security-list.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color), var(--accent-brown));
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

/* 安全项 */
.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s ease-out;
  position: relative;
  overflow: hidden;
  background: var(--surface-color);
}

.security-item:last-child {
  border-bottom: none;
}

.security-item:hover {
  background: var(--accent-cream);
  padding-left: calc(var(--spacing-lg) + 8px);
}

.security-item:hover::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 6px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
  border-radius: 0 3px 3px 0;
}

/* 标签样式 */
.label {
  font-size: 16px;
  color: var(--text-color-dark);
  font-weight: 600;
  font-family: var(--font-body);
  letter-spacing: 0.3px;
  position: relative;
  padding-left: var(--spacing-md);
}

.label::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 8px;
  height: 8px;
  background: var(--primary-color);
  border-radius: 50%;
  opacity: 0.6;
  transition: all 0.3s ease-out;
}

.security-item:hover .label::before {
  transform: translateY(-50%) scale(1.2);
  opacity: 1;
  background: var(--accent-pink);
}

/* 值样式 */
.value {
  font-size: 13px;
  margin-left: auto;
  margin-right: var(--spacing-sm);
  font-weight: 500;
  transition: all 0.3s ease-out;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(4px);
}

.danger {
  color: #ff6b6b;
  background: rgba(255, 107, 107, 0.1);
  border: 1px solid rgba(255, 107, 107, 0.2);
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0% {
    box-shadow: 0 0 0 0 rgba(255, 107, 107, 0.4);
  }
  70% {
    box-shadow: 0 0 0 6px rgba(255, 107, 107, 0);
  }
  100% {
    box-shadow: 0 0 0 0 rgba(255, 107, 107, 0);
  }
}

/* 图标样式 */
.iconfont.icon-right {
  color: var(--primary-light);
  font-size: 20px;
  transition: all 0.3s ease-out;
  width: 32px;
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
}

.security-item:hover .iconfont.icon-right {
  color: var(--primary-color);
  transform: translateX(4px) scale(1.1);
  background: var(--accent-cream);
}

/* 装饰元素 */
.security-page::after {
  content: '';
  position: fixed;
  bottom: -50px;
  right: -50px;
  width: 200px;
  height: 200px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.1;
  border-radius: 50%;
  z-index: 0;
  animation: float 15s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-30px, -20px) rotate(120deg);
  }
  66% {
    transform: translate(20px, -40px) rotate(240deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .security-page {
    padding: var(--spacing-sm);
  }

  .header {
    margin-bottom: var(--spacing-md);
  }

  .title {
    font-size: 20px;
  }

  .security-item {
    padding: var(--spacing-md);
  }

  .label {
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .security-page {
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

  .security-item {
    padding: var(--spacing-sm) var(--spacing-md);
  }

  .value {
    font-size: 12px;
    padding: 4px 8px;
  }
}

/* 自定义滚动条 */
.security-page::-webkit-scrollbar {
  width: 8px;
}

.security-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.security-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.security-page::-webkit-scrollbar-thumb:hover {
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

/* 确认对话框样式增强 */
:global(.confirm-dialog) {
  background: var(--surface-color) !important;
  border-radius: var(--border-radius-lg) !important;
  border: 1px solid var(--border-color) !important;
  box-shadow: 0 8px 32px rgba(139, 69, 19, 0.2) !important;
}

:global(.confirm-dialog button) {
  border-radius: var(--border-radius-md) !important;
  background: var(--primary-color) !important;
  color: white !important;
  border: none !important;
  transition: all 0.3s ease-out !important;
}

:global(.confirm-dialog button:hover) {
  background: var(--primary-dark) !important;
  transform: translateY(-2px) !important;
}
</style>