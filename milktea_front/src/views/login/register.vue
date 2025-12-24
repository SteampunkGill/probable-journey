<template>
  <div class="register-page">
    <!-- 顶部Logo和标题 -->
    <div class="header">
      <img src="../../assets/images/icons/user.png" class="logo" />
      <span class="app-name">奶茶小店</span>
      <span class="subtitle">加入我们，开启美味之旅</span>
    </div>

    <!-- 注册卡片 -->
    <div class="register-card">
      <div class="form-content">
        <!-- 用户名输入框 -->
        <div class="form-group">
          <div class="input-wrapper" :class="{ 'input-focus': isFocusUsername }">
            <i class="iconfont icon-user"></i>
            <input 
              type="text" 
              placeholder="请设置用户名" 
              class="input-field"
              v-model="username"
              @focus="isFocusUsername = true"
              @blur="isFocusUsername = false"
            />
          </div>
        </div>

        <!-- 手机号输入框 -->
        <div class="form-group">
          <div class="input-wrapper" :class="{ 'input-focus': isFocusPhone }">
            <i class="iconfont icon-phone"></i>
            <input 
              type="tel" 
              placeholder="请输入手机号" 
              class="input-field"
              v-model="phone"
              @focus="isFocusPhone = true"
              @blur="isFocusPhone = false"
              maxlength="11"
            />
          </div>
        </div>

        <!-- 密码输入框 -->
        <div class="form-group">
          <div class="input-wrapper" :class="{ 'input-focus': isFocusPassword }">
            <i class="iconfont icon-lock"></i>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="请设置登录密码" 
              class="input-field"
              v-model="password"
              @focus="isFocusPassword = true"
              @blur="isFocusPassword = false"
            />
            <div class="eye-icon" @click="showPassword = !showPassword">
              <i class="iconfont" :class="showPassword ? 'icon-eye-open' : 'icon-eye-close'"></i>
            </div>
          </div>
        </div>

        <!-- 确认密码输入框 -->
        <div class="form-group">
          <div class="input-wrapper" :class="{ 'input-focus': isFocusConfirmPassword }">
            <i class="iconfont icon-lock"></i>
            <input 
              :type="showConfirmPassword ? 'text' : 'password'" 
              placeholder="请再次输入密码" 
              class="input-field"
              v-model="confirmPassword"
              @focus="isFocusConfirmPassword = true"
              @blur="isFocusConfirmPassword = false"
            />
            <div class="eye-icon" @click="showConfirmPassword = !showConfirmPassword">
              <i class="iconfont" :class="showConfirmPassword ? 'icon-eye-open' : 'icon-eye-close'"></i>
            </div>
          </div>
        </div>

        <!-- 注册按钮 -->
        <button 
          class="register-btn" 
          @click="handleRegister"
          :disabled="loading || !agreed"
        >
          <i v-if="loading" class="loading-icon iconfont icon-loading"></i>
          {{ loading ? '注册中...' : '立即注册' }}
        </button>

        <!-- 返回登录 -->
        <div class="bottom-actions">
          <span class="action-text" @click="router.push('/login')">已有账号？去登录</span>
        </div>
      </div>
    </div>

    <!-- 协议区域 -->
    <div class="agreement-section">
      <input type="checkbox" v-model="agreed" />
      <span class="agreement-text">
        我已阅读并同意<span class="agreement-link" @click="router.push('/agreement?type=user')">《用户协议》</span>和<span class="agreement-link" @click="router.push('/agreement?type=privacy')">《隐私政策》</span>
      </span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../../utils/api'

const router = useRouter()

const username = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const agreed = ref(false)
const loading = ref(false)

const isFocusUsername = ref(false)
const isFocusPhone = ref(false)
const isFocusPassword = ref(false)
const isFocusConfirmPassword = ref(false)

const isValidPhone = computed(() => /^1[3-9]\d{9}$/.test(phone.value))

const handleRegister = async () => {
  if (!username.value) return alert('请输入用户名')
  if (!isValidPhone.value) return alert('请输入正确的手机号')
  if (!password.value) return alert('请设置密码')
  if (password.value !== confirmPassword.value) return alert('两次输入的密码不一致')
  if (!agreed.value) return alert('请先同意用户协议和隐私政策')

  loading.value = true
  try {
    await authApi.register({
      username: username.value,
      phone: phone.value,
      password: password.value
    })
    alert('注册成功，请登录')
    router.push('/login')
  } catch (error) {
    alert(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.register-page {
  min-height: 100vh;
  background: #FFF9E6;
  padding: 20px 15px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
}

.header {
  text-align: center;
  margin: 20px 0 30px 0;
}

.logo {
  width: 70px;
  height: 70px;
  border-radius: 50%;
  margin-bottom: 15px;
  border: 2px solid #FFD166;
  background: #FFD166;
}

.app-name {
  display: block;
  font-size: 21px;
  font-weight: bold;
  color: #8B7500;
  margin-bottom: 5px;
}

.subtitle {
  display: block;
  font-size: 15px;
  color: #B38B00;
}

.register-card {
  background: white;
  border-radius: 12px;
  padding: 20px 17px;
  box-shadow: 0 4px 20px rgba(255, 200, 100, 0.15);
  margin-bottom: 15px;
}

.form-group {
  margin-bottom: 17px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  height: 45px;
  background: #F9F9F9;
  border-radius: 22px;
  border: 1px solid #EEE;
  padding: 0 15px;
  transition: all 0.3s;
}

.input-wrapper.input-focus {
  border-color: #FFD166;
  background: #FFFDF0;
  box-shadow: 0 0 5px rgba(255, 209, 102, 0.3);
}

.input-field {
  flex: 1;
  height: 100%;
  font-size: 15px;
  color: #333;
  background: transparent;
  border: none;
  outline: none;
}

.register-btn {
  width: 100%;
  height: 45px;
  background: #FFD166;
  color: #8B7500;
  border-radius: 22px;
  font-size: 17px;
  font-weight: bold;
  border: none;
  margin-top: 10px;
  margin-bottom: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.register-btn:disabled {
  background: #EEE;
  color: #999;
  cursor: not-allowed;
}

.bottom-actions {
  display: flex;
  justify-content: center;
  align-items: center;
}

.action-text {
  color: #D4A017;
  font-size: 13px;
  cursor: pointer;
}

.agreement-section {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 253, 240, 0.8);
  border-radius: 10px;
  padding: 10px 7px;
  border: 1px solid #FFE8A3;
  margin-top: auto;
  margin-bottom: 20px;
}

.agreement-text {
  font-size: 12px;
  color: #8B7500;
  margin-left: 7px;
}

.agreement-link {
  color: #D4A017;
  font-weight: bold;
  text-decoration: underline;
  cursor: pointer;
}

.eye-icon {
  padding: 0 5px;
  cursor: pointer;
  color: #999;
}
</style>