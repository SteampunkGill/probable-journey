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
      form.value = {
        nickname: res.nickname || res.username || '',
        avatarUrl: res.avatarUrl || res.avatar || '',
        gender: res.gender !== undefined ? res.gender : 0,
        birthday: res.birthday || ''
      }
    }
  } catch (error) {
    console.error('获取个人资料失败:', error)
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
    
    // 拦截器返回的是 res.data，如果成功，res 应该是更新后的用户对象
    // 只要 res 存在，或者请求没有抛出异常，通常就代表后端处理成功了
    if (res) {
      alert('保存成功')
      userStore.setUserInfo(res)
      router.back()
    } else {
      // 某些情况下后端可能只返回 200 OK 而没有 body
      alert('保存成功')
      loadProfile() // 重新加载一次数据
      router.back()
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
.profile-page {
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

.avatar-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  cursor: pointer;
}

.avatar-wrapper {
  position: relative;
  width: 50px;
  height: 50px;
}

.avatar {
  width: 100%;
  height: 100%;
  border-radius: 50%;
  object-fit: cover;
}

.upload-mask {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.4);
  border-radius: 50%;
  display: flex;
  justify-content: center;
  align-items: center;
}

.loading-spinner {
  width: 20px;
  height: 20px;
  border: 2px solid #FFF;
  border-top-color: transparent;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

.right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.form-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #F5F5F5;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item.disabled {
  background: #FAFAFA;
}

.label {
  font-size: 15px;
  color: #333;
  width: 80px;
}

.value {
  font-size: 15px;
  color: #666;
}

input {
  flex: 1;
  border: none;
  text-align: right;
  font-size: 15px;
  outline: none;
  background: transparent;
}

.gender-options {
  display: flex;
  gap: 15px;
}

.gender-label {
  display: flex;
  align-items: center;
  gap: 5px;
  font-size: 14px;
  cursor: pointer;
}

.save-btn {
  width: 100%;
  height: 45px;
  background: #D4A574;
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  margin-top: 30px;
  cursor: pointer;
}

.save-btn:disabled {
  background: #E0C3A5;
  cursor: not-allowed;
}
</style>