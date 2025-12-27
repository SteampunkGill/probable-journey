<template>
  <div class="settings-page">
    <div class="header">
      <i class="iconfont icon-left" @click="router.back()"></i>
      <span class="title">设置</span>
    </div>

    <div class="settings-list card">
      <div class="setting-item" @click="router.push('/profile')">
        <span class="label">个人资料</span>
        <i class="iconfont icon-right"></i>
      </div>
      <div class="setting-item" @click="router.push('/address')">
        <span class="label">收货地址</span>
        <i class="iconfont icon-right"></i>
      </div>
      <div class="setting-item" @click="router.push('/settings/security')">
        <span class="label">账号安全</span>
        <i class="iconfont icon-right"></i>
      </div>
      <div class="setting-item" @click="showNotificationSettings = true">
        <span class="label">消息推送设置</span>
        <i class="iconfont icon-right"></i>
      </div>
    </div>

    <!-- 消息推送设置弹窗 -->
    <div class="modal" v-if="showNotificationSettings">
      <div class="modal-content card">
        <div class="modal-header">
          <span class="modal-title">消息推送设置</span>
          <i class="iconfont icon-close" @click="showNotificationSettings = false"></i>
        </div>
        <div class="setting-list">
          <div class="setting-item">
            <span class="label">开启消息推送</span>
            <input type="checkbox" v-model="notifSettings.enabled" @change="updateSettings" />
          </div>
          <div class="setting-item" v-if="notifSettings.enabled">
            <span class="label">订单状态更新</span>
            <input type="checkbox" v-model="notifSettings.orderUpdate" @change="updateSettings" />
          </div>
          <div class="setting-item" v-if="notifSettings.enabled">
            <span class="label">营销活动推送</span>
            <input type="checkbox" v-model="notifSettings.promotion" @change="updateSettings" />
          </div>
        </div>
      </div>
    </div>

    <div class="settings-list card">
      <div class="setting-item">
        <span class="label">清除缓存</span>
        <span class="value">12.5MB</span>
      </div>
      <div class="setting-item">
        <span class="label">当前版本</span>
        <span class="value">v1.0.0</span>
      </div>
    </div>

    <button class="logout-btn" @click="handleLogout">退出登录</button>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { systemApi } from '../../utils/api'

const router = useRouter()
const userStore = useUserStore()

const showNotificationSettings = ref(false)
const notifSettings = ref({
  enabled: true,
  orderUpdate: true,
  promotion: false
})

const loadSettings = async () => {
  try {
    const res = await systemApi.getNotificationSettings()
    if (res.code === 200) {
      notifSettings.value = res.data
    }
  } catch (error) {
    console.error('加载设置失败:', error)
  }
}

const updateSettings = async () => {
  try {
    await systemApi.updateNotificationSettings(notifSettings.value)
  } catch (error) {
    console.error('更新设置失败:', error)
  }
}

onMounted(() => {
  loadSettings()
})

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 - 设置页面 */
.settings-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  padding: var(--spacing-md);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰元素 */
.settings-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 10% 10%, rgba(255, 248, 220, 0.15) 0%, transparent 40%),
      radial-gradient(circle at 90% 90%, rgba(222, 184, 135, 0.15) 0%, transparent 40%);
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

/* 设置列表卡片 */
.settings-list.card {
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

.settings-list.card:hover {
  transform: translateY(-2px);
}

.settings-list.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color), var(--accent-brown));
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

/* 设置项 */
.setting-item {
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

.setting-item:last-child {
  border-bottom: none;
}

.setting-item:hover {
  background: var(--accent-cream);
  padding-left: calc(var(--spacing-lg) + 8px);
}

.setting-item:hover::before {
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

.setting-item:hover .label::before {
  transform: translateY(-50%) scale(1.2);
  opacity: 1;
  background: var(--accent-pink);
}

/* 值样式 */
.value {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  background: rgba(255, 255, 255, 0.5);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease-out;
}

.setting-item:hover .value {
  background: var(--accent-cream);
  color: var(--primary-color);
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

.setting-item:hover .iconfont.icon-right {
  color: var(--primary-color);
  transform: translateX(4px) scale(1.1);
  background: var(--accent-cream);
}

/* 复选框样式 */
.setting-item input[type="checkbox"] {
  appearance: none;
  width: 52px;
  height: 28px;
  background: var(--border-color);
  border-radius: var(--border-radius-xl);
  position: relative;
  cursor: pointer;
  transition: all 0.3s ease-out;
  outline: none;
}

.setting-item input[type="checkbox"]:checked {
  background: var(--primary-color);
}

.setting-item input[type="checkbox"]::before {
  content: '';
  position: absolute;
  top: 2px;
  left: 2px;
  width: 24px;
  height: 24px;
  background: white;
  border-radius: 50%;
  transition: all 0.3s ease-out;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.setting-item input[type="checkbox"]:checked::before {
  transform: translateX(24px);
  background: var(--accent-cream);
}

.setting-item input[type="checkbox"]:hover {
  transform: scale(1.05);
}

/* 退出登录按钮 */
.logout-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, var(--surface-color), var(--accent-cream));
  color: #ff6b6b;
  border: 2px solid rgba(255, 107, 107, 0.3);
  border-radius: var(--border-radius-xl);
  font-size: 16px;
  font-weight: 600;
  font-family: var(--font-body);
  margin-top: var(--spacing-xl);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  z-index: 1;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15);
}

.logout-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.4), transparent);
  transition: left 0.5s ease-out;
}

.logout-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.5);
  color: #ff5252;
}

.logout-btn:hover::before {
  left: 100%;
}

.logout-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

/* 模态框 */
.modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(139, 69, 19, 0.4);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: var(--spacing-md);
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    backdrop-filter: blur(0);
  }
  to {
    opacity: 1;
    backdrop-filter: blur(8px);
  }
}

.modal-content.card {
  width: 90%;
  max-width: 400px;
  padding: var(--spacing-lg);
  background: var(--surface-color);
  border-radius: var(--border-radius-xl);
  box-shadow:
      0 20px 60px rgba(139, 69, 19, 0.3),
      inset 0 1px 0 rgba(255, 255, 255, 0.8);
  border: 1px solid var(--border-color);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-content.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 6px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color), var(--accent-brown));
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
  padding-bottom: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
}

.modal-title {
  font-size: 18px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
}

.iconfont.icon-close {
  font-size: 20px;
  color: var(--text-color-light);
  cursor: pointer;
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: rgba(255, 255, 255, 0.6);
  transition: all 0.3s ease-out;
}

.iconfont.icon-close:hover {
  color: var(--primary-color);
  background: var(--accent-cream);
  transform: rotate(90deg) scale(1.1);
}

/* 设置列表（模态框内） */
.setting-list {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-md);
}

.setting-list .setting-item {
  padding: var(--spacing-md);
  border-radius: var(--border-radius-md);
  border: 1px solid transparent;
  transition: all 0.3s ease-out;
}

.setting-list .setting-item:hover {
  border-color: var(--primary-light);
  background: var(--accent-cream);
  transform: translateX(4px);
}

/* 装饰元素 */
.settings-page::after {
  content: '';
  position: fixed;
  bottom: -100px;
  left: -100px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--accent-brown) 0%, transparent 70%);
  opacity: 0.08;
  border-radius: 50%;
  z-index: 0;
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(40px, -20px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 40px) scale(0.9);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-page {
    padding: var(--spacing-sm);
  }

  .header {
    margin-bottom: var(--spacing-md);
  }

  .title {
    font-size: 20px;
  }

  .setting-item {
    padding: var(--spacing-md);
  }

  .label {
    font-size: 15px;
  }

  .logout-btn {
    height: 48px;
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .settings-page {
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

  .setting-item {
    padding: var(--spacing-sm) var(--spacing-md);
  }

  .modal-content.card {
    padding: var(--spacing-md);
  }

  .modal-title {
    font-size: 16px;
  }
}

/* 自定义滚动条 */
.settings-page::-webkit-scrollbar {
  width: 8px;
}

.settings-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.settings-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.settings-page::-webkit-scrollbar-thumb:hover {
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
</style>