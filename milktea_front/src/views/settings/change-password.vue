<template>
  <div class="change-password-page">
    <div class="header">
      <i class="iconfont icon-left" @click="router.back()"></i>
      <span class="title">修改密码</span>
    </div>

    <div class="form-card card">
      <div class="form-item">
        <span class="label">旧密码</span>
        <input 
          type="password" 
          v-model="form.oldPassword" 
          placeholder="请输入旧密码"
        />
      </div>
      <div class="form-item">
        <span class="label">新密码</span>
        <input 
          type="password" 
          v-model="form.newPassword" 
          placeholder="请输入新密码"
        />
      </div>
      <div class="form-item">
        <span class="label">确认新密码</span>
        <input 
          type="password" 
          v-model="form.confirmPassword" 
          placeholder="请再次输入新密码"
        />
      </div>
    </div>

    <button 
      class="submit-btn" 
      :disabled="loading" 
      @click="handleSubmit"
    >
      {{ loading ? '提交中...' : '确认修改' }}
    </button>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../../utils/api'

const router = useRouter()
const loading = ref(false)
const form = ref({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const handleSubmit = async () => {
  if (!form.value.oldPassword) return alert('请输入旧密码')
  if (!form.value.newPassword) return alert('请输入新密码')
  if (form.value.newPassword.length < 6) return alert('新密码长度不能少于6位')
  if (form.value.newPassword !== form.value.confirmPassword) return alert('两次输入的新密码不一致')

  loading.value = true
  try {
    const res = await authApi.changePassword(
      form.value.oldPassword,
      form.value.newPassword
    )
    if (res && (res.code === 200 || res.status === 'success')) {
      alert('密码修改成功，请重新登录')
      // 退出登录
      localStorage.removeItem('token')
      localStorage.removeItem('userInfo')
      router.push('/login')
    } else {
      alert(res.message || '修改失败')
    }
  } catch (error) {
    console.error('修改密码失败:', error)
    alert(error.message || '修改失败，请检查旧密码是否正确')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.change-password-page {
  min-height: 100vh;
  background: var(--background-color);
  padding: var(--spacing-md);
}

.header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.iconfont.icon-left {
  font-size: 24px;
  color: var(--primary-color);
  cursor: pointer;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-dark);
}

.card {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-md);
  box-shadow: 0 8px 32px rgba(139, 69, 19, 0.12);
  border: 1px solid var(--border-color);
}

.form-item {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xs);
  padding: var(--spacing-md) 0;
  border-bottom: 1px solid var(--border-color);
}

.form-item:last-child {
  border-bottom: none;
}

.label {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 600;
}

input {
  border: none;
  background: transparent;
  font-size: 16px;
  color: var(--text-color-dark);
  outline: none;
  padding: var(--spacing-xs) 0;
}

.submit-btn {
  width: 100%;
  height: 50px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: 600;
  margin-top: var(--spacing-xl);
  cursor: pointer;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.submit-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>