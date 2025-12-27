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
    const res = await authApi.login(username.value, password.value)
    
    if (!res) {
      throw new Error('登录失败，服务器未响应')
    }

    // 后端返回结构为 { code: 200, message: "success", data: { token: "...", user: {...} } }
    const data = res.data || res
    const userInfo = data.user || data
    const token = data.token
    const isAdmin = data.isAdmin || userInfo.status === 'ADMIN'

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
      router.replace('/admin')
    } else {
      const redirect = route.query.redirect || '/'
      router.replace(redirect)
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
/* ============================================
   “饮饮茶(SipSipTea)” 登录页面样式优化
   基于奶茶主题设计指南
   ============================================ */

/* ========== 页面容器 ========== */
.login-page {
  min-height: 100vh;
  background: var(--background-color);
  padding: var(--spacing-xl) var(--spacing-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow-x: hidden;
  transition: background-color 0.5s ease;
}

/* 奶茶主题背景装饰 */
.login-page::before {
  content: '';
  position: absolute;
  top: -40%;
  right: -15%;
  width: 280px;
  height: 280px;
  background: radial-gradient(circle, var(--accent-cream) 0%, transparent 70%);
  opacity: 0.25;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

.login-page::after {
  content: '';
  position: absolute;
  bottom: -25%;
  left: -8%;
  width: 220px;
  height: 220px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.15;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

/* 管理员模式背景 */
.login-page.admin {
  background: linear-gradient(135deg, #2a2118 0%, #3a2e22 100%);
}

.login-page.admin::before {
  background: radial-gradient(circle, rgba(255, 248, 220, 0.1) 0%, transparent 70%);
}

.login-page.admin::after {
  background: radial-gradient(circle, rgba(255, 192, 203, 0.1) 0%, transparent 70%);
}

/* ========== 顶部Logo和标题 ========== */
.header {
  text-align: center;
  margin-bottom: var(--spacing-xl);
  position: relative;
  z-index: 1;
}

.logo-wrapper {
  width: 100px;
  height: 100px;
  margin: 0 auto var(--spacing-md);
  background: linear-gradient(135deg, var(--accent-cream) 0%, var(--surface-color) 100%);
  border-radius: var(--border-radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: var(--shadow-lg);
  border: 3px solid white;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
}

.login-page.admin .logo-wrapper {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.1) 0%, rgba(160, 82, 45, 0.2) 100%);
  border-color: rgba(255, 255, 255, 0.1);
}

.logo-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(45deg, transparent 30%, rgba(255,255,255,0.3) 50%, transparent 70%);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { transform: translateX(-100%); }
  100% { transform: translateX(100%); }
}

.logo-wrapper:hover {
  transform: scale(1.05) rotate(5deg);
  box-shadow: 0 12px 40px rgba(160, 144, 128, 0.25);
}

.login-page.admin .logo-wrapper:hover {
  box-shadow: 0 12px 40px rgba(160, 82, 45, 0.3);
}

.logo {
  width: 60px;
  height: 60px;
  filter: drop-shadow(0 2px 4px rgba(0,0,0,0.1));
}

.app-name {
  display: block;
  font-family: 'Noto Serif KR', 'Prompt', serif;
  font-size: var(--font-size-3xl);
  font-weight: 700;
  color: var(--primary-dark);
  margin-bottom: var(--spacing-sm);
  text-shadow: 2px 2px 4px rgba(0,0,0,0.05);
  letter-spacing: 1px;
}

.login-page.admin .app-name {
  color: var(--accent-cream);
  text-shadow: 0 2px 8px rgba(0,0,0,0.3);
}

.subtitle {
  display: block;
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  font-weight: 500;
  position: relative;
  padding: 0 var(--spacing-md);
}

.subtitle::before,
.subtitle::after {
  content: '☕';
  position: absolute;
  top: 50%;
  transform: translateY(-50%);
  color: var(--primary-light);
  font-size: var(--font-size-sm);
}

.login-page.admin .subtitle::before,
.login-page.admin .subtitle::after {
  content: '🔐';
  color: var(--accent-cream);
}

.subtitle::before {
  left: 0;
}

.subtitle::after {
  right: 0;
}

/* ========== 角色切换 Tab ========== */
.role-tabs {
  width: 100%;
  max-width: 420px;
  margin: 0 auto var(--spacing-xl);
  background: rgba(255, 255, 255, 0.5);
  border-radius: var(--border-radius-xl);
  display: flex;
  position: relative;
  height: 56px;
  padding: 4px;
  box-sizing: border-box;
  border: 2px solid var(--border-color);
  overflow: hidden;
}

.login-page.admin .role-tabs {
  background: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.1);
}

.tab-item {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  z-index: 1;
  cursor: pointer;
  transition: all var(--transition-normal);
  border-radius: var(--border-radius-lg);
  font-weight: 500;
  gap: var(--spacing-xs);
}

.tab-item i {
  font-size: var(--font-size-lg);
  transition: all var(--transition-normal);
}

.tab-item:hover {
  color: var(--primary-color);
}

.tab-item:hover i {
  transform: scale(1.1);
}

.tab-item.active {
  color: white;
  font-weight: 600;
}

.tab-slider {
  position: absolute;
  width: calc(50% - 4px);
  height: calc(100% - 8px);
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  border-radius: var(--border-radius-lg);
  transition: all 0.4s cubic-bezier(0.68, -0.55, 0.265, 1.55);
  box-shadow: var(--shadow-md);
  z-index: 0;
}

.login-page.admin .tab-slider {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
}

/* ========== 登录卡片 ========== */
.login-card {
  background: var(--surface-color);
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-xl);
  box-shadow: var(--shadow-lg);
  width: 100%;
  max-width: 420px;
  margin: 0 auto;
  position: relative;
  z-index: 1;
  border: 1px solid var(--border-color);
  backdrop-filter: blur(10px);
  background-image:
      radial-gradient(circle at 20% 80%, rgba(255,255,255,0.1) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255,255,255,0.1) 0%, transparent 50%);
}

.login-page.admin .login-card {
  background: rgba(58, 46, 34, 0.8);
  border-color: rgba(255, 255, 255, 0.1);
}

/* 卡片装饰元素 */
.login-card::before {
  content: '';
  position: absolute;
  top: -10px;
  left: -10px;
  right: -10px;
  bottom: -10px;
  background: linear-gradient(45deg, var(--accent-cream), var(--accent-pink), var(--primary-light));
  border-radius: calc(var(--border-radius-xl) + 10px);
  z-index: -1;
  opacity: 0.1;
  filter: blur(20px);
}

.login-page.admin .login-card::before {
  background: linear-gradient(45deg, #1890ff, #096dd9, #52c41a);
}

/* ========== 表单组 ========== */
.form-group {
  margin-bottom: var(--spacing-lg);
}

/* ========== 输入框容器 ========== */
.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  height: 56px;
  background: white;
  border-radius: var(--border-radius-lg);
  border: 2px solid var(--border-color);
  padding: 0 var(--spacing-md);
  transition: all var(--transition-normal);
  overflow: hidden;
}

.login-page.admin .input-wrapper {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
}

.input-wrapper::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-pink));
  transform: scaleX(0);
  transition: transform var(--transition-normal);
  transform-origin: left;
}

.login-page.admin .input-wrapper::before {
  background: linear-gradient(90deg, #1890ff, #52c41a);
}

.input-wrapper.input-focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.15);
  transform: translateY(-2px);
}

.login-page.admin .input-wrapper.input-focus {
  border-color: #1890ff;
  box-shadow: 0 0 0 4px rgba(24, 144, 255, 0.15);
}

.input-wrapper.input-focus::before {
  transform: scaleX(1);
}

.input-wrapper i {
  color: var(--text-color-light);
  margin-right: var(--spacing-sm);
  font-size: var(--font-size-lg);
  transition: color var(--transition-normal);
}

.input-wrapper.input-focus i {
  color: var(--primary-color);
}

.login-page.admin .input-wrapper.input-focus i {
  color: #1890ff;
}

/* ========== 输入框 ========== */
.input-field {
  flex: 1;
  height: 100%;
  font-size: var(--font-size-base);
  color: var(--text-color-dark);
  background: transparent;
  border: none;
  outline: none;
  font-family: inherit;
}

.login-page.admin .input-field {
  color: var(--accent-cream);
}

.input-field::placeholder {
  color: var(--text-color-light);
  font-weight: 300;
}

.login-page.admin .input-field::placeholder {
  color: rgba(255, 255, 255, 0.4);
}

/* ========== 清除按钮 ========== */
.clear-btn {
  padding: var(--spacing-xs);
  cursor: pointer;
  color: var(--text-color-light);
  transition: all var(--transition-normal);
  border-radius: var(--border-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 32px;
  height: 32px;
  margin-left: var(--spacing-xs);
}

.clear-btn:hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.1);
  transform: scale(1.1);
}

.clear-btn:active {
  transform: scale(0.95);
}

/* ========== 眼睛图标 ========== */
.eye-icon {
  padding: var(--spacing-xs);
  cursor: pointer;
  color: var(--text-color-light);
  transition: all var(--transition-normal);
  border-radius: var(--border-radius-sm);
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
}

.eye-icon:hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.1);
  transform: scale(1.1);
}

.eye-icon:active {
  transform: scale(0.95);
}

.login-page.admin .eye-icon:hover {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.1);
}

/* ========== 表单选项 ========== */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding: 0 var(--spacing-xs);
}

.remember-label {
  display: flex;
  align-items: center;
  font-size: var(--font-size-sm);
  color: var(--text-color-medium);
  cursor: pointer;
  transition: all var(--transition-normal);
}

.login-page.admin .remember-label {
  color: rgba(255, 255, 255, 0.7);
}

.remember-label:hover {
  color: var(--primary-color);
}

.login-page.admin .remember-label:hover {
  color: #1890ff;
}

.remember-label input[type="checkbox"] {
  appearance: none;
  width: 18px;
  height: 18px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  margin-right: var(--spacing-sm);
  cursor: pointer;
  position: relative;
  transition: all var(--transition-normal);
}

.login-page.admin .remember-label input[type="checkbox"] {
  border-color: rgba(255, 255, 255, 0.3);
}

.remember-label input[type="checkbox"]:checked {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.login-page.admin .remember-label input[type="checkbox"]:checked {
  background: #1890ff;
  border-color: #1890ff;
}

.remember-label input[type="checkbox"]:checked::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: var(--font-size-xs);
  font-weight: bold;
}

.remember-label input[type="checkbox"]:hover {
  border-color: var(--primary-color);
  transform: scale(1.1);
}

.login-page.admin .remember-label input[type="checkbox"]:hover {
  border-color: #1890ff;
}

.remember-text {
  margin-left: var(--spacing-xs);
}

.forgot-text {
  font-size: var(--font-size-sm);
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 500;
  position: relative;
  padding: var(--spacing-xs) 0;
  transition: all var(--transition-normal);
}

.login-page.admin .forgot-text {
  color: #1890ff;
}

.forgot-text::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  width: 100%;
  height: 1px;
  background: var(--primary-color);
  transform: scaleX(0);
  transition: transform var(--transition-normal);
}

.forgot-text:hover {
  color: var(--primary-dark);
}

.forgot-text:hover::after {
  transform: scaleX(1);
}

.login-page.admin .forgot-text:hover {
  color: #40a9ff;
}

/* ========== 登录按钮 ========== */
.login-btn {
  width: 100%;
  height: 56px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border-radius: var(--border-radius-xl);
  font-size: var(--font-size-lg);
  font-weight: 600;
  border: none;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all var(--transition-normal);
  position: relative;
  overflow: hidden;
  box-shadow: var(--shadow-md);
  letter-spacing: 1px;
}

.login-page.admin .login-btn {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
}

.login-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}

.login-btn:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: var(--shadow-lg);
}

.login-btn:hover::before {
  left: 100%;
}

.login-btn:active {
  transform: translateY(0) scale(0.98);
}

.login-btn:disabled {
  background: var(--border-color);
  color: var(--text-color-light);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.login-btn:disabled:hover::before {
  left: -100%;
}

/* ========== 底部操作 ========== */
.bottom-actions {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-top: var(--spacing-xl);
  font-size: var(--font-size-base);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.login-page.admin .bottom-actions {
  border-top-color: rgba(255, 255, 255, 0.1);
}

.no-account {
  color: var(--text-color-medium);
}

.login-page.admin .no-account {
  color: rgba(255, 255, 255, 0.6);
}

.register-link {
  color: var(--primary-color);
  font-weight: 600;
  margin-left: var(--spacing-sm);
  cursor: pointer;
  position: relative;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.login-page.admin .register-link {
  color: #1890ff;
}

.register-link::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 2px;
  background: var(--primary-color);
  transform: scaleX(0);
  transition: transform var(--transition-normal);
  transform-origin: right;
}

.register-link:hover {
  background: rgba(160, 82, 45, 0.1);
  transform: translateY(-1px);
}

.register-link:hover::after {
  transform: scaleX(1);
  transform-origin: left;
}

.login-page.admin .register-link:hover {
  background: rgba(24, 144, 255, 0.1);
}

/* ========== 协议区域 ========== */
.agreement-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: var(--spacing-xl) auto;
  max-width: 420px;
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.8);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color);
}

.login-page.admin .agreement-section {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
}

.agreement-section input[type="checkbox"] {
  appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  margin-right: var(--spacing-sm);
  cursor: pointer;
  position: relative;
  transition: all var(--transition-normal);
}

.login-page.admin .agreement-section input[type="checkbox"] {
  border-color: rgba(255, 255, 255, 0.3);
}

.agreement-section input[type="checkbox"]:checked {
  background: var(--primary-color);
  border-color: var(--primary-color);
}

.login-page.admin .agreement-section input[type="checkbox"]:checked {
  background: #1890ff;
  border-color: #1890ff;
}

.agreement-section input[type="checkbox"]:checked::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: var(--font-size-sm);
  font-weight: bold;
}

.agreement-section input[type="checkbox"]:hover {
  border-color: var(--primary-color);
  transform: scale(1.1);
}

.login-page.admin .agreement-section input[type="checkbox"]:hover {
  border-color: #1890ff;
}

.agreement-text {
  font-size: var(--font-size-sm);
  color: var(--text-color-medium);
  line-height: 1.5;
}

.login-page.admin .agreement-text {
  color: rgba(255, 255, 255, 0.7);
}

.agreement-link {
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 500;
  position: relative;
  padding: 0 2px;
  transition: all var(--transition-normal);
}

.login-page.admin .agreement-link {
  color: #1890ff;
}

.agreement-link::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 0;
  width: 100%;
  height: 1px;
  background: var(--primary-color);
  transform: scaleX(0);
  transition: transform var(--transition-normal);
}

.agreement-link:hover {
  color: var(--primary-dark);
}

.agreement-link:hover::after {
  transform: scaleX(1);
}

.login-page.admin .agreement-link:hover {
  color: #40a9ff;
}

/* ========== 其他登录方式 ========== */
.other-login {
  margin-top: var(--spacing-xl);
  padding-bottom: var(--spacing-lg);
  width: 100%;
  max-width: 420px;
  margin-left: auto;
  margin-right: auto;
  position: relative;
  z-index: 1;
}

.divider {
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.divider::before, .divider::after {
  content: "";
  flex: 1;
  height: 1px;
  background: var(--border-color);
  transition: all var(--transition-normal);
}

.login-page.admin .divider::before,
.login-page.admin .divider::after {
  background: rgba(255, 255, 255, 0.1);
}

.divider span {
  padding: 0 var(--spacing-lg);
  font-size: var(--font-size-xs);
  color: var(--text-color-light);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 1px;
}

.login-page.admin .divider span {
  color: rgba(255, 255, 255, 0.4);
}

.other-options {
  display: flex;
  justify-content: center;
  gap: var(--spacing-xl);
}

.other-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  cursor: pointer;
  transition: all var(--transition-normal);
}

.other-item:hover {
  transform: translateY(-3px);
}

.icon-circle {
  width: 56px;
  height: 56px;
  border-radius: var(--border-radius-circle);
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: var(--spacing-sm);
  border: 2px solid var(--border-color);
  transition: all var(--transition-normal);
  background: white;
}

.login-page.admin .icon-circle {
  background: rgba(255, 255, 255, 0.05);
  border-color: rgba(255, 255, 255, 0.1);
}

.icon-circle.wechat {
  color: #07C160;
}

.icon-circle:hover {
  background: var(--accent-cream);
  border-color: var(--primary-light);
  transform: scale(1.1);
  box-shadow: var(--shadow-md);
}

.login-page.admin .icon-circle:hover {
  background: rgba(24, 144, 255, 0.1);
  border-color: #1890ff;
}

.other-item span {
  font-size: var(--font-size-xs);
  color: var(--text-color-medium);
  font-weight: 500;
}

.login-page.admin .other-item span {
  color: rgba(255, 255, 255, 0.6);
}

/* ========== 游客登录 ========== */
.guest-login {
  text-align: center;
  margin-top: var(--spacing-lg);
  position: relative;
  z-index: 1;
}

.guest-text {
  color: var(--text-color-light);
  font-size: var(--font-size-sm);
  text-decoration: underline;
  cursor: pointer;
  transition: all var(--transition-normal);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-md);
  display: inline-block;
}

.guest-text:hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.1);
  transform: translateY(-1px);
}

.login-page.admin .guest-text {
  color: rgba(255, 255, 255, 0.5);
}

.login-page.admin .guest-text:hover {
  color: #1890ff;
  background: rgba(24, 144, 255, 0.1);
}

/* ========== 加载图标 ========== */
.loading-icon {
  margin-right: var(--spacing-sm);
  animation: spin 1s linear infinite;
  font-size: var(--font-size-lg);
}

@keyframes spin {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* ========== 响应式设计 ========== */
@media (max-width: 480px) {
  .login-page {
    padding: var(--spacing-lg) var(--spacing-sm);
  }

  .login-card {
    padding: var(--spacing-lg);
    border-radius: var(--border-radius-lg);
  }

  .logo-wrapper {
    width: 80px;
    height: 80px;
  }

  .logo {
    width: 50px;
    height: 50px;
  }

  .app-name {
    font-size: var(--font-size-2xl);
  }

  .subtitle {
    font-size: var(--font-size-sm);
  }

  .role-tabs {
    height: 48px;
    max-width: 320px;
  }

  .tab-item {
    font-size: var(--font-size-sm);
  }

  .input-wrapper {
    height: 52px;
  }

  .login-btn {
    height: 52px;
    font-size: var(--font-size-base);
  }

  .other-options {
    gap: var(--spacing-lg);
  }

  .icon-circle {
    width: 48px;
    height: 48px;
  }
}

/* 平板设备 */
@media (min-width: 481px) and (max-width: 768px) {
  .login-page {
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .login-card {
    max-width: 450px;
  }
}

/* 桌面设备 */
@media (min-width: 769px) {
  .login-page {
    padding: var(--spacing-xxl) var(--spacing-xl);
  }

  .login-card {
    max-width: 480px;
    padding: var(--spacing-xxl);
  }

  .logo-wrapper {
    width: 120px;
    height: 120px;
  }

  .logo {
    width: 70px;
    height: 70px;
  }

  .app-name {
    font-size: var(--font-size-4xl);
  }

  .role-tabs {
    max-width: 480px;
    height: 60px;
  }
}
</style>