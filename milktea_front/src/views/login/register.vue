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
.register-page {
  min-height: 100vh;
  background: #FFF9E6;
  padding: 40px 20px;
  display: flex;
  flex-direction: column;
  box-sizing: border-box;
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

.register-card {
  background: white;
  border-radius: 16px;
  padding: 30px 25px;
  box-shadow: 0 8px 30px rgba(0,0,0,0.05);
  width: 100%;
  max-width: 400px;
  margin: 0 auto;
  box-sizing: border-box;
}

.type-switch {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 25px;
  font-size: 15px;
  color: #999;
}

.type-switch span:not(.divider) {
  cursor: pointer;
  padding: 5px 10px;
  transition: all 0.3s;
}

.type-switch span.active {
  color: #D4A017;
  font-weight: bold;
  border-bottom: 2px solid #D4A017;
}

.type-switch .divider {
  margin: 0 15px;
  color: #EEE;
}

.form-group {
  margin-bottom: 18px;
}

.input-label {
  font-size: 14px;
  color: #333;
  margin-bottom: 8px;
  padding-left: 4px;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
  height: 48px;
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

.input-wrapper.error {
  border-color: #ff4d4f;
  background: #fff2f0;
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

.error-msg {
  font-size: 12px;
  color: #ff4d4f;
  margin-top: 4px;
  padding-left: 4px;
}

.register-btn {
  width: 100%;
  height: 50px;
  background: #FFD166;
  color: #8B7500;
  border-radius: 12px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  margin-top: 10px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s;
}

.register-btn.admin-btn {
  background: #1890ff;
  color: white;
}

.register-btn:hover {
  opacity: 0.9;
  transform: translateY(-1px);
}

.register-btn:disabled {
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

.login-link {
  color: #D4A017;
  font-weight: bold;
  margin-left: 5px;
  cursor: pointer;
}

.agreement-section {
  display: flex;
  align-items: flex-start;
  justify-content: center;
  margin: 25px auto;
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

.eye-icon {
  padding: 0 5px;
  cursor: pointer;
  color: #999;
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
  .register-page {
    padding: 20px 15px;
  }
  .register-card {
    padding: 25px 20px;
  }
}
</style>