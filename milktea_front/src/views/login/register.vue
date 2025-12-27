<template>
  <div class="register-page">
    <!-- 顶部Logo和标题 -->
    <div class="header">
      <div class="logo-wrapper">
        <img src="../../assets/images/icons/user.png" class="logo" />
      </div>
      <span class="app-name">奶茶小店</span>
      <span class="subtitle">加入我们，开启美味之旅</span>
    </div>

    <!-- 注册卡片 -->
    <div class="register-card">
      <div class="form-content">
        <!-- 注册类型切换 -->
        <div class="type-switch">
          <span 
            :class="{ active: registerType === 'user' }" 
            @click="registerType = 'user'"
          >普通用户注册</span>
          <span class="divider">|</span>
          <span 
            :class="{ active: registerType === 'admin' }" 
            @click="registerType = 'admin'"
          >管理员注册</span>
        </div>

        <!-- 用户名输入框 -->
        <div class="form-group">
          <div class="input-label">用户名</div>
          <div class="input-wrapper" :class="{ 'input-focus': isFocusUsername, 'error': errors.username }">
            <i class="iconfont icon-user"></i>
            <input 
              type="text" 
              placeholder="请设置用户名" 
              class="input-field"
              v-model="username"
              @focus="isFocusUsername = true"
              @blur="validateUsername"
            />
          </div>
          <div class="error-msg" v-if="errors.username">{{ errors.username }}</div>
        </div>

        <!-- 手机号输入框 -->
        <div class="form-group">
          <div class="input-label">手机号</div>
          <div class="input-wrapper" :class="{ 'input-focus': isFocusPhone, 'error': errors.phone }">
            <i class="iconfont icon-phone"></i>
            <input 
              type="tel" 
              placeholder="请输入手机号" 
              class="input-field"
              v-model="phone"
              @focus="isFocusPhone = true"
              @blur="validatePhone"
              maxlength="11"
            />
          </div>
          <div class="error-msg" v-if="errors.phone">{{ errors.phone }}</div>
        </div>

        <!-- 密码输入框 -->
        <div class="form-group">
          <div class="input-label">登录密码</div>
          <div class="input-wrapper" :class="{ 'input-focus': isFocusPassword, 'error': errors.password }">
            <i class="iconfont icon-lock"></i>
            <input 
              :type="showPassword ? 'text' : 'password'" 
              placeholder="请设置6-20位登录密码" 
              class="input-field"
              v-model="password"
              @focus="isFocusPassword = true"
              @blur="validatePassword"
            />
            <div class="eye-icon" @click="showPassword = !showPassword">
              <i class="iconfont" :class="showPassword ? 'icon-eye-open' : 'icon-eye-close'"></i>
            </div>
          </div>
          <div class="error-msg" v-if="errors.password">{{ errors.password }}</div>
        </div>

        <!-- 确认密码输入框 -->
        <div class="form-group">
          <div class="input-label">确认密码</div>
          <div class="input-wrapper" :class="{ 'input-focus': isFocusConfirmPassword, 'error': errors.confirmPassword }">
            <i class="iconfont icon-lock"></i>
            <input 
              :type="showConfirmPassword ? 'text' : 'password'" 
              placeholder="请再次输入密码" 
              class="input-field"
              v-model="confirmPassword"
              @focus="isFocusConfirmPassword = true"
              @blur="validateConfirmPassword"
            />
            <div class="eye-icon" @click="showConfirmPassword = !showConfirmPassword">
              <i class="iconfont" :class="showConfirmPassword ? 'icon-eye-open' : 'icon-eye-close'"></i>
            </div>
          </div>
          <div class="error-msg" v-if="errors.confirmPassword">{{ errors.confirmPassword }}</div>
        </div>

        <!-- 管理员密令输入框 (仅管理员注册显示) -->
        <div class="form-group" v-if="registerType === 'admin'">
          <div class="input-label">管理员注册密令</div>
          <div class="input-wrapper" :class="{ 'input-focus': isFocusSecret, 'error': errors.secret }">
            <i class="iconfont icon-lock"></i>
            <input 
              type="password" 
              placeholder="请输入管理员注册密令" 
              class="input-field"
              v-model="secret"
              @focus="isFocusSecret = true"
              @blur="isFocusSecret = false"
            />
          </div>
          <div class="error-msg" v-if="errors.secret">{{ errors.secret }}</div>
        </div>

        <!-- 注册按钮 -->
        <button 
          class="register-btn" 
          :class="{ 'admin-btn': registerType === 'admin' }"
          @click="handleRegister"
          :disabled="loading || !agreed"
        >
          <i v-if="loading" class="loading-icon iconfont icon-loading"></i>
          {{ loading ? '注册中...' : (registerType === 'admin' ? '注册管理员' : '立即注册') }}
        </button>

        <!-- 返回登录 -->
        <div class="bottom-actions">
          <span class="no-account">已有账号？</span>
          <span class="action-text login-link" @click="router.push('/login')">去登录</span>
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
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authApi } from '../../utils/api'

const router = useRouter()

const registerType = ref('user') // 'user' or 'admin'
const username = ref('')
const phone = ref('')
const password = ref('')
const confirmPassword = ref('')
const secret = ref('')
const showPassword = ref(false)
const showConfirmPassword = ref(false)
const agreed = ref(false)
const loading = ref(false)

const isFocusUsername = ref(false)
const isFocusPhone = ref(false)
const isFocusPassword = ref(false)
const isFocusConfirmPassword = ref(false)
const isFocusSecret = ref(false)

const ADMIN_SECRET = '13603994106'

const errors = reactive({
  username: '',
  phone: '',
  password: '',
  confirmPassword: '',
  secret: ''
})

const validateUsername = () => {
  isFocusUsername.value = false
  if (!username.value.trim()) {
    errors.username = '用户名不能为空'
  } else if (username.value.length < 2) {
    errors.username = '用户名至少2个字符'
  } else {
    errors.username = ''
  }
}

const validatePhone = () => {
  isFocusPhone.value = false
  const phoneReg = /^1[3-9]\d{9}$/
  if (!phone.value.trim()) {
    errors.phone = '手机号不能为空'
  } else if (!phoneReg.test(phone.value)) {
    errors.phone = '请输入正确的11位手机号'
  } else {
    errors.phone = ''
  }
}

const validatePassword = () => {
  isFocusPassword.value = false
  if (!password.value) {
    errors.password = '密码不能为空'
  } else if (password.value.length < 6) {
    errors.password = '密码长度不能少于6位'
  } else {
    errors.password = ''
  }
}

const validateConfirmPassword = () => {
  isFocusConfirmPassword.value = false
  if (!confirmPassword.value) {
    errors.confirmPassword = '请再次输入密码'
  } else if (confirmPassword.value !== password.value) {
    errors.confirmPassword = '两次输入的密码不一致'
  } else {
    errors.confirmPassword = ''
  }
}

const handleRegister = async () => {
  // 全量验证
  validateUsername()
  validatePhone()
  validatePassword()
  validateConfirmPassword()

  if (registerType.value === 'admin') {
    if (!secret.value) {
      errors.secret = '请输入管理员注册密令'
    } else if (secret.value !== ADMIN_SECRET) {
      errors.secret = '密令错误，无法注册管理员'
    } else {
      errors.secret = ''
    }
  }

  if (errors.username || errors.phone || errors.password || errors.confirmPassword || (registerType.value === 'admin' && errors.secret)) {
    return
  }

  if (!agreed.value) {
    alert('请先同意用户协议和隐私政策')
    return
  }

  loading.value = true
  try {
    // 构造注册数据，确保符合后端接口要求
    const registerData = {
      username: username.value.trim(),
      phone: phone.value.trim(),
      password: password.value,
      role: registerType.value === 'admin' ? 'admin' : 'user',
      adminCode: registerType.value === 'admin' ? secret.value.trim() : ''
    }
    
    console.log('发送注册数据:', registerData)
    await authApi.register(registerData)
    alert(registerType.value === 'admin' ? '管理员账号注册成功，请登录' : '注册成功，请登录')
    router.push('/login')
  } catch (error) {
    alert(error.message || '注册失败')
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
/* ============================================
   “饮饮茶(SipSipTea)” 注册页面样式优化
   基于奶茶主题设计指南
   ============================================ */

/* ========== 页面容器 ========== */
.register-page {
  min-height: 100vh;
  background: var(--background-color);
  padding: var(--spacing-xl) var(--spacing-md);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  position: relative;
  overflow-x: hidden;
}

/* 添加奶茶主题背景装饰 */
.register-page::before {
  content: '';
  position: absolute;
  top: -50%;
  right: -20%;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--accent-cream) 0%, transparent 70%);
  opacity: 0.3;
  border-radius: var(--border-radius-circle);
  z-index: 0;
}

.register-page::after {
  content: '';
  position: absolute;
  bottom: -30%;
  left: -10%;
  width: 250px;
  height: 250px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.2;
  border-radius: var(--border-radius-circle);
  z-index: 0;
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

.subtitle::before {
  left: 0;
}

.subtitle::after {
  right: 0;
}

/* ========== 注册卡片 ========== */
.register-card {
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

/* 卡片装饰元素 */
.register-card::before {
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

/* ========== 注册类型切换 ========== */
.type-switch {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  font-size: var(--font-size-base);
  color: var(--text-color-medium);
  background: rgba(255, 255, 255, 0.5);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xs);
  position: relative;
}

.type-switch::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 1px;
  height: 60%;
  background: var(--border-color);
}

.type-switch span:not(.divider) {
  cursor: pointer;
  padding: var(--spacing-sm) var(--spacing-lg);
  transition: all var(--transition-normal);
  flex: 1;
  text-align: center;
  border-radius: var(--border-radius-md);
  font-weight: 500;
  position: relative;
  z-index: 1;
}

.type-switch span.active {
  color: white;
  font-weight: 600;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  box-shadow: var(--shadow-sm);
  transform: translateY(-1px);
}

.type-switch span:not(.active):hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.1);
}

.type-switch .divider {
  display: none;
}

/* ========== 表单组 ========== */
.form-group {
  margin-bottom: var(--spacing-lg);
}

.input-label {
  font-size: var(--font-size-sm);
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-xs);
  padding-left: var(--spacing-sm);
  font-weight: 500;
  display: flex;
  align-items: center;
}

.input-label::before {
  content: '✨';
  margin-right: var(--spacing-xs);
  font-size: var(--font-size-xs);
  color: var(--primary-light);
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

.input-wrapper.input-focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.15);
  transform: translateY(-2px);
}

.input-wrapper.input-focus::before {
  transform: scaleX(1);
}

.input-wrapper.error {
  border-color: #ff6b6b;
  background: #fff5f5;
  animation: shake 0.5s ease-in-out;
}

@keyframes shake {
  0%, 100% { transform: translateX(0); }
  25% { transform: translateX(-5px); }
  75% { transform: translateX(5px); }
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

.input-field::placeholder {
  color: var(--text-color-light);
  font-weight: 300;
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

/* ========== 错误信息 ========== */
.error-msg {
  font-size: var(--font-size-xs);
  color: #ff6b6b;
  margin-top: var(--spacing-xs);
  padding-left: var(--spacing-sm);
  display: flex;
  align-items: center;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(-5px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.error-msg::before {
  content: '⚠️';
  margin-right: var(--spacing-xs);
  font-size: var(--font-size-xs);
}

/* ========== 注册按钮 ========== */
.register-btn {
  width: 100%;
  height: 56px;
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: white;
  border-radius: var(--border-radius-xl);
  font-size: var(--font-size-lg);
  font-weight: 600;
  border: none;
  margin-top: var(--spacing-lg);
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

.register-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s ease;
}

.register-btn:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: var(--shadow-lg);
}

.register-btn:hover::before {
  left: 100%;
}

.register-btn:active {
  transform: translateY(0) scale(0.98);
}

.register-btn.admin-btn {
  background: linear-gradient(135deg, #1890ff 0%, #096dd9 100%);
}

.register-btn.admin-btn::after {
  content: '👑';
  margin-left: var(--spacing-sm);
  font-size: var(--font-size-base);
}

.register-btn:disabled {
  background: var(--border-color);
  color: var(--text-color-light);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.register-btn:disabled:hover::before {
  left: -100%;
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

.no-account {
  color: var(--text-color-medium);
}

.login-link {
  color: var(--primary-color);
  font-weight: 600;
  margin-left: var(--spacing-sm);
  cursor: pointer;
  position: relative;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  transition: all var(--transition-normal);
}

.login-link::after {
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

.login-link:hover {
  background: rgba(160, 82, 45, 0.1);
  transform: translateY(-1px);
}

.login-link:hover::after {
  transform: scaleX(1);
  transform-origin: left;
}

/* ========== 协议区域 ========== */
.agreement-section {
  display: flex;
  align-items: center;
  justify-content: center;
  margin: var(--spacing-xl) auto;
  max-width: 400px;
  position: relative;
  z-index: 1;
  background: rgba(255, 255, 255, 0.8);
  padding: var(--spacing-md);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color);
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

.agreement-section input[type="checkbox"]:checked {
  background: var(--primary-color);
  border-color: var(--primary-color);
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

.agreement-text {
  font-size: var(--font-size-sm);
  color: var(--text-color-medium);
  line-height: 1.5;
}

.agreement-link {
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 500;
  position: relative;
  padding: 0 2px;
  transition: all var(--transition-normal);
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

/* ========== 响应式设计 ========== */
@media (max-width: 480px) {
  .register-page {
    padding: var(--spacing-lg) var(--spacing-sm);
  }

  .register-card {
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

  .type-switch {
    font-size: var(--font-size-sm);
  }

  .input-wrapper {
    height: 52px;
  }

  .register-btn {
    height: 52px;
    font-size: var(--font-size-base);
  }
}

/* 平板设备 */
@media (min-width: 481px) and (max-width: 768px) {
  .register-page {
    padding: var(--spacing-xl) var(--spacing-lg);
  }

  .register-card {
    max-width: 450px;
  }
}

/* 桌面设备 */
@media (min-width: 769px) {
  .register-page {
    padding: var(--spacing-xxl) var(--spacing-xl);
  }

  .register-card {
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
}

/* 深色模式适配 */
@media (prefers-color-scheme: dark) {
  :root {
    --background-color: #2a2118;
    --surface-color: #3a2e22;
    --text-color-dark: #f5f0e1;
    --text-color-medium: #d4c7b5;
    --text-color-light: #a09080;
    --border-color: #5a4a38;
    --shadow-color: rgba(0, 0, 0, 0.3);
  }

  .register-card {
    background: var(--surface-color);
    border-color: var(--border-color);
  }

  .input-wrapper {
    background: rgba(255, 255, 255, 0.05);
    border-color: var(--border-color);
  }

  .agreement-section {
    background: rgba(255, 255, 255, 0.05);
  }
}
</style>