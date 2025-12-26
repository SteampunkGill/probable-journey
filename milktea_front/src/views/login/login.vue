<template>
  <div class="login-page" :class="loginType">
    <!-- 顶部Logo和标题 -->
    <div class="header">
      <div class="logo-wrapper">
        <img :src="loginType === 'user' ? userIcon : adminIcon" class="logo" />
      </div>
      <span class="app-name">奶茶小店</span>
      <span class="subtitle">{{ loginType === 'user' ? '前台用户登录' : '后台管理系统' }}</span>
    </div>

    <!-- 角色切换 Tab -->
    <div class="role-tabs">
      <div 
        class="tab-item" 
        :class="{ active: loginType === 'user' }"
        @click="switchRole('user')"
      >
        <i class="iconfont icon-user"></i>
        前台用户
      </div>
      <div 
        class="tab-item" 
        :class="{ active: loginType === 'admin' }"
        @click="switchRole('admin')"
      >
        <i class="iconfont icon-lock"></i>
        后台管理
      </div>
      <div class="tab-slider" :style="{ left: loginType === 'user' ? '0' : '50%' }"></div>
    </div>

    <!-- 登录卡片 -->
    <div class="login-card">
      <div class="form-content">
        <!-- 用户名输入框 -->
        <div class="form-group">
          <div class="input-wrapper" :class="{ 'input-focus': isFocusUsername }">
            <i class="iconfont icon-user"></i>
            <input 
              type="text" 
              :placeholder="loginType === 'user' ? '请输入用户名/手机号' : '请输入管理员账号'" 
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
              @keyup.enter="handleLogin"
            />
            <div class="eye-icon" @click="showPassword = !showPassword">
              <i class="iconfont" :class="showPassword ? 'icon-eye-open' : 'icon-eye-close'"></i>
            </div>
          </div>
        </div>

        <!-- 记住我 & 忘记密码 -->
        <div class="form-options">
          <label class="remember-label">
            <input type="checkbox" v-model="rememberMe" />
            <span class="remember-text">记住密码</span>
          </label>
          <span v-if="loginType === 'user'" class="forgot-text" @click="router.push('/forgot')">忘记密码？</span>
        </div>

        <!-- 登录按钮 -->
        <button 
          class="login-btn" 
          @click="handleLogin"
          :disabled="loading || (loginType === 'user' && !agreed)"
        >
          <i v-if="loading" class="loading-icon iconfont icon-loading"></i>
          {{ loading ? '登录中...' : '立即登录' }}
        </button>
      </div>

      <!-- 底部操作 (仅前台用户显示) -->
      <div v-if="loginType === 'user'" class="bottom-actions">
        <span class="no-account">还没有账号？</span>
        <span class="action-text register-link" @click="router.push('/register')">立即注册</span>
      </div>
    </div>

    <!-- 协议区域 (仅前台用户显示) -->
    <div v-if="loginType === 'user'" class="agreement-section">
      <input type="checkbox" v-model="agreed" />
      <span class="agreement-text">
        我已阅读并同意<span class="agreement-link" @click="router.push('/agreement?type=user')">《用户协议》</span>和<span class="agreement-link" @click="router.push('/agreement?type=privacy')">《隐私政策》</span>
      </span>
    </div>

    <!-- 其他登录方式 (仅前台用户显示) -->
    <div v-if="loginType === 'user'" class="other-login">
      <div class="divider">
        <span>其他方式登录</span>
      </div>
      <div class="other-options">
        <div class="other-item" @click="handleWechatLogin">
          <div class="icon-circle wechat">
            <i class="iconfont icon-wechat"></i>
          </div>
          <span>微信登录</span>
        </div>
      </div>
    </div>

    <!-- 游客登录 -->
    <div v-if="loginType === 'user'" class="guest-login" @click="goToHomeAsGuest">
      <span class="guest-text">先逛逛，暂不登录</span>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useUserStore } from '../../store/user'
import { authApi } from '../../utils/api'
import userIcon from '../../assets/images/icons/user.png'
// 假设有一个管理员图标，如果没有则复用
import adminIcon from '../../assets/images/icons/user.png'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const loginType = ref('user') // 'user' or 'admin'
const username = ref('')
const password = ref('')
const showPassword = ref(false)
const rememberMe = ref(true)
const agreed = ref(false)
const loading = ref(false)

const isFocusUsername = ref(false)
const isFocusPassword = ref(false)

const switchRole = (role) => {
  if (loading.value) return
  loginType.value = role
  // 切换角色时清除当前输入，或者根据需要保留
  // username.value = ''
  // password.value = ''
}

const handleLogin = async () => {
  // 表单验证
  if (!username.value.trim()) {
    alert('请输入用户名')
    return
  }
  if (!password.value.trim()) {
    alert('请输入密码')
    return
  }
  if (loginType.value === 'user' && !agreed.value) {
    alert('请先同意用户协议和隐私政策')
    return
  }

  loading.value = true
  try {
    // 调用登录API
    const response = await authApi.login(username.value, password.value)
    
    if (!response) {
      throw new Error('登录失败，服务器未响应')
    }

    const userInfo = response.user || response
    const token = response.token
    const isAdmin = response.isAdmin || userInfo.status === 'ADMIN'

    if (!token) {
      throw new Error('登录返回数据异常：缺失Token')
    }

    // 角色校验
    if (loginType.value === 'admin' && !isAdmin) {
      throw new Error('该账号没有管理员权限')
    }

    // 统一角色标识，确保路由守卫能正确识别
    if (isAdmin) {
      userInfo.role = 'admin'
    }

    // 存储信息
    userStore.setUserInfo(userInfo)
    userStore.setToken(token)

    // 记住用户名
    if (rememberMe.value) {
      localStorage.setItem('remembered_username', username.value)
      localStorage.setItem('remembered_role', loginType.value)
    } else {
      localStorage.removeItem('remembered_username')
      localStorage.removeItem('remembered_role')
    }
    
    // 根据角色重定向
    if (loginType.value === 'admin') {
      router.push('/admin')
    } else {
      router.push(route.query.redirect || '/')
    }
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
  router.push('/')
}

onMounted(() => {
  const remembered = localStorage.getItem('remembered_username')
  const rememberedRole = localStorage.getItem('remembered_role')
  if (remembered) {
    username.value = remembered
  }
  if (rememberedRole) {
    loginType.value = rememberedRole
  }
})
</script>

<style scoped>
.login-page {
  min-height: 100vh;
  background: #FFF9E6;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
  transition: background-color 0.5s ease;
}

/* 管理员模式背景色切换 */
.login-page.admin {
  background: #F0F2F5;
}

.header {
  text-align: center;
  margin-bottom: 30px;
}

.logo-wrapper {
  width: 80px;
  height: 80px;
  margin: 0 auto 15px;
  background: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.logo {
  width: 50px;
  height: 50px;
}

.app-name {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.subtitle {
  display: block;
  font-size: 14px;
  color: #666;
}

/* 角色切换 Tab */
.role-tabs {
  width: 100%;
  max-width: 400px;
  margin: 0 auto 25px;
  background: rgba(0,0,0,0.05);
  border-radius: 25px;
  display: flex;
  position: relative;
  height: 45px;
  padding: 4px;
  box-sizing: border-box;
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  color: #666;
  z-index: 1;
  cursor: pointer;
  transition: color 0.3s;
}

.tab-item.active {
  color: #8B7500;
  font-weight: bold;
}

.admin .tab-item.active {
  color: #1890ff;
}

.tab-slider {
  position: absolute;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: white;
  border-radius: 21px;
  transition: all 0.3s cubic-bezier(0.645, 0.045, 0.355, 1);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}

.login-card {
  background: white;
  border-radius: 16px;
  padding: 30px 25px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.05);
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
  box-sizing: border-box;
}

.form-group {
  margin-bottom: 20px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  height: 50px;
  background: #F5F7FA;
  border-radius: 12px;
  border: 1px solid transparent;
  padding: 0 15px;
  transition: all 0.3s;
}

.input-wrapper i {
  color: #999;
  margin-right: 10px;
  font-size: 18px;
}

.input-wrapper.input-focus {
  border-color: #FFD166;
  background: white;
  box-shadow: 0 0 0 3px rgba(255, 209, 102, 0.1);
}

.admin .input-wrapper.input-focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 3px rgba(24, 144, 255, 0.1);
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

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
}

.remember-label {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.remember-text {
  margin-left: 8px;
}

.forgot-text {
  font-size: 14px;
  color: #D4A017;
  cursor: pointer;
}

.login-btn {
  width: 100%;
  height: 50px;
  background: #FFD166;
  color: #8B7500;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.admin .login-btn {
  background: #1890ff;
  color: white;
}

.login-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.login-btn:disabled {
  background: #E0E0E0;
  color: #999;
  cursor: not-allowed;
  transform: none;
}

.bottom-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: 20px;
  font-size: 14px;
}

.no-account {
  color: #999;
}

.register-link {
  color: #D4A017;
  font-weight: bold;
  margin-left: 5px;
  cursor: pointer;
}

.agreement-section {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  margin: 20px auto;
  max-width: 400px;
}

.agreement-text {
  font-size: 12px;
  color: #999;
  margin-left: 8px;
  line-height: 1.5;
}

.agreement-link {
  color: #D4A017;
  cursor: pointer;
}

.other-login {
  margin-top: auto;
  padding-bottom: 20px;
  width: 100%;
  max-width: 400px;
  margin-left: auto;
  margin-right: auto;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
}

.divider::before, .divider::after {
  content: "";
  flex: 1;
  height: 1px;
  background: #EEE;
}

.divider span {
  padding: 0 15px;
  font-size: 12px;
  color: #999;
}

.other-options {
  display: flex;
  justify-content: center;
}

.other-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
}

.icon-circle {
  width: 45px;
  height: 45px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 8px;
  border: 1px solid #EEE;
  transition: all 0.3s;
}

.icon-circle.wechat {
  color: #07C160;
}

.icon-circle:hover {
  background: #F5F7FA;
}

.other-item span {
  font-size: 12px;
  color: #666;
}

.guest-login {
  text-align: center;
  margin-top: 10px;
}

.guest-text {
  color: #999;
  font-size: 13px;
  text-decoration: underline;
  cursor: pointer;
}

.loading-icon {
  margin-right: 8px;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 响应式调整 */
@media (max-width: 480px) {
  .login-page {
    padding: 20px 15px;
  }
  .login-card {
    padding: 25px 20px;
  }
}
</style>