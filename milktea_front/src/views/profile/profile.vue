<template>
  <div class="profile-page">
    <div class="header">
      <i class="iconfont icon-left" @click="router.back()"></i>
      <span class="title">个人资料</span>
    </div>

    <div class="profile-content">
      <div class="avatar-section card">
        <div class="avatar-item" @click="triggerFileInput">
          <span class="label">头像</span>
          <div class="right">
            <div class="avatar-wrapper">
              <img :src="form.avatarUrl || defaultAvatar" class="avatar" />
              <div v-if="uploading" class="upload-mask">
                <div class="loading-spinner"></div>
              </div>
            </div>
            <i class="iconfont icon-right"></i>
          </div>
        </div>
        <input
          type="file"
          ref="fileInput"
          style="display: none"
          accept="image/*"
          @change="handleFileChange"
        />
      </div>

      <div class="form-section card">
        <div class="form-item">
          <span class="label">昵称</span>
          <input v-model="form.nickname" placeholder="请输入昵称" />
        </div>
        <div class="form-item">
          <span class="label">性别</span>
          <div class="gender-options">
            <label class="gender-label">
              <input type="radio" v-model="form.gender" :value="1" /> 男
            </label>
            <label class="gender-label">
              <input type="radio" v-model="form.gender" :value="2" /> 女
            </label>
            <label class="gender-label">
              <input type="radio" v-model="form.gender" :value="0" /> 保密
            </label>
          </div>
        </div>
        <div class="form-item">
          <span class="label">生日</span>
          <input type="date" v-model="form.birthday" />
        </div>
      </div>

      <div class="info-section card">
        <div class="form-item disabled">
          <span class="label">手机号</span>
          <span class="value">{{ userInfo?.phone || '未绑定' }}</span>
        </div>
        <div class="form-item disabled">
          <span class="label">会员等级</span>
          <span class="value">{{ userInfo?.levelName || '普通会员' }}</span>
        </div>
        <div class="form-item disabled">
          <span class="label">注册时间</span>
          <span class="value">{{ formatDate(userInfo?.createdAt || userInfo?.registrationTime) }}</span>
        </div>
      </div>

      <button class="save-btn" :disabled="loading" @click="handleSave">
        {{ loading ? '保存中...' : '保存修改' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../../store/user'
import { authApi, commonApi } from '../../utils/api'
import defaultAvatar from '../../assets/images/icons/user.png'

const router = useRouter()
const userStore = useUserStore()
const userInfo = computed(() => userStore.userInfo)

const loading = ref(false)
const uploading = ref(false)
const fileInput = ref(null)

const form = ref({
  nickname: '',
  avatarUrl: '',
  gender: 0,
  birthday: ''
})

const triggerFileInput = () => {
  if (fileInput.value) {
    fileInput.value.click()
  }
}

const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  if (!file.type.startsWith('image/')) {
    alert('请选择图片文件')
    return
  }

  if (file.size > 2 * 1024 * 1024) {
    alert('图片大小不能超过2MB')
    return
  }

  uploading.value = true
  try {
    const res = await commonApi.uploadImage(file, 'avatar')
    // commonApi.uploadImage 也会经过拦截器返回 res.data
    if (res && res.url) {
      form.value.avatarUrl = res.url
    } else {
      alert('上传失败：未获取到图片地址')
    }
  } catch (error) {
    console.error('上传头像失败:', error)
    alert('上传失败，请稍后再试')
  } finally {
    uploading.value = false
    event.target.value = ''
  }
}

const loadProfile = async () => {
  try {
    const res = await authApi.getUserProfile()
    // 拦截器返回的是 res.data
    if (res) {
      userStore.setUserInfo(res)
      // 优先使用 res 中的数据，如果为空则使用 store 中的数据
      form.value = {
        nickname: res.nickname || res.username || userStore.userInfo?.nickname || userStore.userInfo?.username || '',
        avatarUrl: res.avatarUrl || res.avatar || userStore.userInfo?.avatarUrl || userStore.userInfo?.avatar || '',
        gender: res.gender !== undefined ? res.gender : (userStore.userInfo?.gender || 0),
        birthday: res.birthday || userStore.userInfo?.birthday || ''
      }
    } else if (userStore.userInfo) {
      // 如果接口没返回，使用 store 中的数据
      form.value = {
        nickname: userStore.userInfo.nickname || userStore.userInfo.username || '',
        avatarUrl: userStore.userInfo.avatarUrl || userStore.userInfo.avatar || '',
        gender: userStore.userInfo.gender !== undefined ? userStore.userInfo.gender : 0,
        birthday: userStore.userInfo.birthday || ''
      }
    }
  } catch (error) {
    console.error('获取个人资料失败:', error)
    if (userStore.userInfo) {
      form.value = {
        nickname: userStore.userInfo.nickname || userStore.userInfo.username || '',
        avatarUrl: userStore.userInfo.avatarUrl || userStore.userInfo.avatar || '',
        gender: userStore.userInfo.gender !== undefined ? userStore.userInfo.gender : 0,
        birthday: userStore.userInfo.birthday || ''
      }
    }
  }
}

const handleSave = async () => {
  if (!form.value.nickname || !form.value.nickname.trim()) {
    alert('昵称不能为空')
    return
  }

  loading.value = true
  try {
    // 构造提交数据，确保字段名正确
    const submitData = {
      nickname: form.value.nickname.trim(),
      avatarUrl: form.value.avatarUrl,
      gender: form.value.gender,
      birthday: form.value.birthday
    }
    
    console.log('提交修改数据:', submitData)
    const res = await authApi.updateUserProfile(submitData)
    
    // 拦截器返回的是 res 对象（包含 code, message, data）
    if (res && res.code === 200) {
      alert('保存成功')
      const updatedUser = res.data
      // 确保更新 store 中的用户信息，同时保留 token
      const currentToken = userStore.token || localStorage.getItem('token')
      const newUserInfo = { ...userStore.userInfo, ...updatedUser }
      userStore.setUserInfo(newUserInfo)
      
      // 重新设置 token，防止丢失
      if (currentToken) {
        userStore.setToken(currentToken)
      }
      
      // 延迟返回，确保 store 更新完成
      setTimeout(() => {
        router.back()
      }, 100)
    } else {
      // 如果 res 直接是数据（旧逻辑兼容）
      alert('保存成功')
      const currentToken = userStore.token || localStorage.getItem('token')
      if (res && !res.code) {
        const newUserInfo = { ...userStore.userInfo, ...res }
        userStore.setUserInfo(newUserInfo)
      } else {
        await loadProfile()
      }
      if (currentToken) {
        userStore.setToken(currentToken)
      }
      setTimeout(() => {
        router.back()
      }, 100)
    }
  } catch (error) {
    console.error('更新个人资料失败:', error)
    // 拦截器报错会进入这里
  } finally {
    loading.value = false
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  try {
    const date = new Date(dateStr)
    if (isNaN(date.getTime())) return dateStr
    return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
  } catch (e) {
    return dateStr
  }
}

onMounted(() => {
  loadProfile()
})
</script>
<style scoped>
/* 奶茶主题 CSS 变量定义 */
.profile-page {
  --background-color: #f5f0e1; /* 奶油色背景 */
  --surface-color: #e8dccb;    /* 表面元素色 - 浅卡其 */
  --primary-color: #a0522d;    /* 焦糖色 - 主色调 */
  --primary-dark: #8b4513;     /* 深咖啡色 */
  --primary-light: #d2b48c;    /* 浅驼色 */
  --accent-cream: #fff8dc;     /* 奶油强调色 */
  --accent-pink: #ffc0cb;      /* 淡粉色 */
  --accent-brown: #deb887;     /* 沙棕色 */
  --text-color-dark: #4a3b30;  /* 深棕色文本 */
  --text-color-medium: #7a6a5b; /* 中棕色文本 */
  --text-color-light: #a09080;  /* 浅咖色文本 */
  --border-color: #d4c7b5;      /* 边框色 */

  --border-radius-sm: 8px;
  --border-radius-md: 16px;
  --border-radius-lg: 24px;
  --border-radius-xl: 50%;      /* 用于圆形元素 */

  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;

  --shadow-soft: 0 4px 12px rgba(160, 82, 45, 0.08);
  --shadow-medium: 0 6px 20px rgba(160, 82, 45, 0.12);
  --shadow-hover: 0 8px 24px rgba(160, 82, 45, 0.16);

  --transition-smooth: all 0.25s ease-out;
}

.profile-page {
  min-height: 100vh;
  background: var(--background-color);
  padding: var(--spacing-md);
  font-family: 'Noto Sans KR', 'Nunito', 'Quicksand', sans-serif;
  color: var(--text-color-dark);
}

/* 头部样式 */
.header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-sm) 0;
  position: relative;
}

.header::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 0;
  width: 100%;
  height: 2px;
  background: linear-gradient(90deg,
  var(--primary-color) 0%,
  var(--accent-pink) 50%,
  var(--accent-cream) 100%);
  border-radius: 1px;
}

.icon-left {
  font-size: 20px;
  color: var(--primary-color);
  cursor: pointer;
  padding: var(--spacing-xs);
  border-radius: var(--border-radius-md);
  transition: var(--transition-smooth);
}

.icon-left:hover {
  background-color: var(--accent-cream);
  transform: translateX(-2px);
}

.title {
  font-size: 20px;
  font-weight: 600;
  color: var(--primary-dark);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.5px;
}

/* 卡片通用样式 */
.card {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-lg);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
  transition: var(--transition-smooth);
  position: relative;
}

.card:hover {
  box-shadow: var(--shadow-medium);
  transform: translateY(-2px);
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg,
  var(--primary-color) 0%,
  var(--accent-brown) 100%);
  opacity: 0.6;
}

/* 头像区域 */
.avatar-section {
  padding: var(--spacing-md);
}

.avatar-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  cursor: pointer;
  border-radius: var(--border-radius-md);
  transition: var(--transition-smooth);
}

.avatar-item:hover {
  background-color: rgba(255, 248, 220, 0.3);
}

.avatar-item .label {
  font-size: 16px;
  font-weight: 500;
  color: var(--text-color-dark);
  font-family: 'Prompt', sans-serif;
}

.right {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
}

.avatar-wrapper {
  position: relative;
  width: 60px;
  height: 60px;
  border-radius: var(--border-radius-xl);
  border: 3px solid var(--accent-cream);
  box-shadow: 0 4px 8px rgba(139, 69, 19, 0.15);
  overflow: hidden;
  transition: var(--transition-smooth);
}

.avatar-wrapper:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 12px rgba(139, 69, 19, 0.25);
}

.avatar {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: var(--border-radius-xl);
}

.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(160, 82, 45, 0.7);
  border-radius: var(--border-radius-xl);
  display: flex;
  justify-content: center;
  align-items: center;
  backdrop-filter: blur(2px);
}

.loading-spinner {
  width: 24px;
  height: 24px;
  border: 3px solid var(--accent-cream);
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.icon-right {
  font-size: 18px;
  color: var(--text-color-medium);
  transition: var(--transition-smooth);
}

.avatar-item:hover .icon-right {
  color: var(--primary-color);
  transform: translateX(2px);
}

/* 表单区域 */
.form-section, .info-section {
  padding: 0;
}

.form-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg) var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  transition: var(--transition-smooth);
  min-height: 60px;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item:hover {
  background-color: rgba(255, 248, 220, 0.2);
}

.form-item.disabled {
  background-color: rgba(232, 220, 203, 0.5);
  cursor: not-allowed;
}

.form-item.disabled:hover {
  background-color: rgba(232, 220, 203, 0.5);
}

.form-item .label {
  font-size: 15px;
  font-weight: 500;
  color: var(--text-color-dark);
  width: 90px;
  font-family: 'Prompt', sans-serif;
}

.form-item .value {
  font-size: 15px;
  color: var(--text-color-medium);
  font-weight: 400;
}

/* 输入框样式 */
input[type="text"],
input[type="date"] {
  flex: 1;
  border: none;
  text-align: right;
  font-size: 15px;
  outline: none;
  background: transparent;
  color: var(--text-color-dark);
  font-family: 'Noto Sans KR', sans-serif;
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  transition: var(--transition-smooth);
  max-width: 200px;
}

input[type="text"]:focus,
input[type="date"]:focus {
  background-color: var(--accent-cream);
  box-shadow: 0 0 0 3px rgba(160, 82, 45, 0.2);
}

input[type="text"]::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

/* 性别选择器 */
.gender-options {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;
}

.gender-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-xs);
  font-size: 14px;
  cursor: pointer;
  color: var(--text-color-medium);
  padding: var(--spacing-xs) var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: var(--transition-smooth);
  position: relative;
}

.gender-label:hover {
  background-color: var(--accent-cream);
  color: var(--primary-color);
}

.gender-label input[type="radio"] {
  appearance: none;
  width: 18px;
  height: 18px;
  border: 2px solid var(--border-color);
  border-radius: 50%;
  outline: none;
  cursor: pointer;
  transition: var(--transition-smooth);
  position: relative;
}

.gender-label input[type="radio"]:checked {
  border-color: var(--primary-color);
  background-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(160, 82, 45, 0.2);
}

.gender-label input[type="radio"]:checked::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 8px;
  height: 8px;
  background-color: white;
  border-radius: 50%;
}

/* 保存按钮 */
.save-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg,
  var(--primary-color) 0%,
  var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: var(--border-radius-xl);
  font-size: 17px;
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
  margin-top: var(--spacing-xl);
  cursor: pointer;
  box-shadow: var(--shadow-medium);
  transition: var(--transition-smooth);
  letter-spacing: 0.5px;
  position: relative;
  overflow: hidden;
}

.save-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
  transparent,
  rgba(255, 255, 255, 0.2),
  transparent);
  transition: 0.5s;
}

.save-btn:hover {
  transform: translateY(-3px) scale(1.02);
  box-shadow: var(--shadow-hover);
}

.save-btn:hover::before {
  left: 100%;
}

.save-btn:active {
  transform: translateY(0) scale(0.98);
}

.save-btn:disabled {
  background: linear-gradient(135deg,
  var(--text-color-light) 0%,
  var(--border-color) 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: var(--shadow-soft);
}

.save-btn:disabled:hover {
  transform: none;
  box-shadow: var(--shadow-soft);
}

/* 日期选择器自定义样式 */
input[type="date"] {
  cursor: pointer;
  min-width: 140px;
}

input[type="date"]::-webkit-calendar-picker-indicator {
  cursor: pointer;
  opacity: 0.6;
  filter: sepia(1) saturate(5) hue-rotate(20deg);
  transition: var(--transition-smooth);
}

input[type="date"]::-webkit-calendar-picker-indicator:hover {
  opacity: 1;
  transform: scale(1.1);
}

/* 响应式调整 */
@media (max-width: 480px) {
  .profile-page {
    padding: var(--spacing-sm);
  }

  .title {
    font-size: 18px;
  }

  .avatar-wrapper {
    width: 50px;
    height: 50px;
  }

  .gender-options {
    gap: var(--spacing-md);
  }

  .form-item {
    padding: var(--spacing-md);
    min-height: 56px;
  }

  .save-btn {
    height: 48px;
    font-size: 16px;
  }
}

/* 加载动画增强 */
@keyframes bubble {
  0%, 100% { transform: translateY(0) scale(1); }
  50% { transform: translateY(-10px) scale(1.1); }
}

.upload-mask .loading-spinner {
  animation: spin 0.8s linear infinite, bubble 1.5s ease-in-out infinite;
}
</style>