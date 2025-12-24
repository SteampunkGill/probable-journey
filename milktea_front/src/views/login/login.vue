<template>
  <div class="login-page">
    <!-- 顶部Logo和标题 -->
    <div class="header">
      <img src="../../assets/images/icons/user.png" class="logo" />
      <span class="app-name">奶茶小店</span>
      <span class="subtitle">欢迎回来</span>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <!-- 密码登录表单 -->
      <div>
        <div class="form-content">
          <!-- 用户名输入框 -->
          <div class="form-group">
            <div class="input-wrapper" :class="{ 'input-focus': isFocusUsername }">
              <i class="iconfont icon-user"></i>
              <input 
                type="text" 
                placeholder="请输入用户名/手机号" 
                class="input-field"
                v-model="username"
                @focus="isFocusUsername = true"
                @blur="isFocusUsername = false"
              />
              <div v-if="username" class="clear-btn" @click="username = ''">
                <i class="iconfont icon-close"></i>
              </div>
            </div>
          </div>

          <!-- 密码输入框 -->
          <div class="form-group">
            <div class="input-wrapper" :class="{ 'input-focus': isFocusPassword }">
              <i class="iconfont icon-lock"></i>
              <input 
                :type="showPassword ? 'text' : 'password'" 
                placeholder="请输入密码" 
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

          <!-- 记住我 -->
          <div class="remember-section">
            <label class="remember-label">
              <input type="checkbox" v-model="rememberMe" />
              <span class="remember-text">记住密码</span>
            </label>
          </div>

          <!-- 登录按钮 -->
          <button 
            class="login-btn" 
            @click="handleLogin"
            :disabled="loading || !agreed"
          >
            <i v-if="loading" class="loading-icon iconfont icon-loading"></i>
            {{ loading ? '登录中...' : '立即登录' }}
          </button>
        </div>
      </div>

      <!-- 底部操作 -->
      <div class="bottom-actions">
        <span class="action-text" @click="router.push('/forgot')">忘记密码</span>
        <span class="separator">|</span>
        <span class="action-text" @click="router.push('/register')">注册账号</span>
      </div>
    </div>

    <!-- 协议区域 -->
    <div class="agreement-section">
      <input type="checkbox" v-model="agreed" />
      <span class="agreement-text">
        我已阅读并同意<span class="agreement-link" @click="router.push('/agreement?type=user')">《用户协议》</span>和<span class="agreement-link" @click="router.push('/agreement?type=privacy')">《隐私政策》</span>
      </span>
    </div>

    <!-- 其他登录方式 -->
    <div class="other-login">
      <span class="other-title">其他方式登录</span>
      <div class="other-options">
        <button class="wechat-btn" @click="handleWechatLogin">
          <i class="iconfont icon-wechat"></i>
          <span class="wechat-text">微信登录</span>
        </button>
      </div>
    </div>

    <!-- 游客登录 -->
    <div class="guest-login" @click="goToHomeAsGuest">
      <span class="guest-text">先逛逛，暂不登录</span>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { authApi } from '../../utils/api'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const username = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(true)
const agreed = ref(false)
const loading = ref(false)

const isFocusUsername = ref(false)
const isFocusPassword = ref(false)

const handleLogin = async () => {
  if (!agreed.value) {
    alert('请先同意用户协议和隐私政策')
    return
  }

  loading.value = true
  try {
    // 调用真实API
    const res = await authApi.login(username.value, password.value)
    const user = res.data
    // 存储用户信息
    userStore.setUserInfo(user)
    // 假设token在user.token中
    if (user.token) {
      userStore.setToken(user.token)
    } else {
      // 如果没有token，使用模拟token（临时方案）
      userStore.setToken('token_' + user.id)
    }
    // 记住用户名
    if (rememberMe.value) {
      localStorage.setItem('remembered_username', username.value)
    }
    alert('登录成功')
    router.push('/')
  } catch (error) {
    alert(error.message || '登录失败')
  } finally {
    loading.value = false
  }
}

const handleWechatLogin = () => {
  if (!agreed.value) {
    alert('请先同意用户协议和隐私政策')
    return
  }
  alert('正在跳转微信授权...')
}

const goToHomeAsGuest = () => {
  if (confirm('以游客身份进入将无法使用收藏、订单等功能，确认继续吗？')) {
    router.push('/')
  }
}

onMounted(() => {
  const remembered = localStorage.getItem('remembered_username')
  if (remembered) {
    username.value = remembered
  }
})
</script>

<style scoped>
.login-page {
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

.login-card {
  background: white;
  border-radius: 12px;
  padding: 20px 17px;
  box-shadow: 0 4px 20px rgba(255, 200, 100, 0.15);
  margin-bottom: 15px;
  flex: 1;
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

.remember-section {
  margin-bottom: 17px;
}

.remember-label {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #666;
  cursor: pointer;
}

.remember-text {
  margin-left: 6px;
}

.login-btn {
  width: 100%;
  height: 45px;
  background: #FFD166;
  color: #8B7500;
  border-radius: 22px;
  font-size: 17px;
  font-weight: bold;
  border: none;
  margin-bottom: 15px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
}

.login-btn:disabled {
  background: #EEE;
  color: #999;
  cursor: not-allowed;
}

.bottom-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 10px;
}

.action-text {
  color: #D4A017;
  font-size: 13px;
  padding: 0 12px;
  cursor: pointer;
}

.separator {
  color: #DDD;
}

.agreement-section {
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 253, 240, 0.8);
  border-radius: 10px;
  padding: 10px 7px;
  border: 1px solid #FFE8A3;
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

.other-login {
  text-align: center;
  margin-bottom: 20px;
}

.other-title {
  display: block;
  color: #999;
  font-size: 13px;
  margin-bottom: 15px;
  position: relative;
}

.wechat-btn {
  width: 150px;
  height: 40px;
  background: white;
  border: 1px solid #D4A017;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto;
  cursor: pointer;
}

.wechat-text {
  color: #8B7500;
  font-size: 15px;
  font-weight: bold;
  margin-left: 6px;
}

.guest-login {
  text-align: center;
  padding: 10px;
  margin-top: auto;
  cursor: pointer;
}

.guest-text {
  color: #D4A017;
  font-size: 13px;
  text-decoration: underline;
}
</style>