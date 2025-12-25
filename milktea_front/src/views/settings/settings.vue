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
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'

const router = useRouter()
const userStore = useUserStore()

const handleLogout = () => {
  if (confirm('确定要退出登录吗？')) {
    userStore.logout()
    router.push('/login')
  }
}
</script>

<style scoped>
.settings-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding: 15px;
}

.header {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 20px;
  padding: 10px 0;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.card {
  background: white;
  border-radius: 12px;
  margin-bottom: 15px;
  overflow: hidden;
}

.setting-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #F5F5F5;
  cursor: pointer;
}

.setting-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 15px;
  color: #333;
}

.value {
  font-size: 14px;
  color: #999;
}

.logout-btn {
  width: 100%;
  height: 45px;
  background: white;
  color: #FF4D4F;
  border: none;
  border-radius: 12px;
  font-size: 16px;
  margin-top: 20px;
  cursor: pointer;
}
</style>