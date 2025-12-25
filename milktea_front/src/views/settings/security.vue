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
  // 实际项目中可能跳转到修改密码页面
  alert('修改密码功能开发中')
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
.security-page {
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

.security-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #F5F5F5;
  cursor: pointer;
}

.security-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 15px;
  color: #333;
}

.value {
  font-size: 12px;
  margin-left: auto;
  margin-right: 5px;
}

.danger {
  color: #FF4D4F;
}

.iconfont {
  color: #999;
}
</style>