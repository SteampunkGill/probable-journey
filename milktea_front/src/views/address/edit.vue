WWWWWWWWWWWWWWWWWWWWWWWWWWWWWW<template>
  <div class="edit-page">
    <div class="form">
      <!-- 收货人信息 -->
      <div class="form-section">
        <div class="form-item">
          <span class="label required">收货人</span>
          <input class="input" placeholder="请输入姓名" v-model="formData.name" maxlength="20" />
        </div>
        
        <div class="form-item">
          <span class="label required">手机号</span>
          <input class="input" type="tel" placeholder="请输入手机号" v-model="formData.phone" maxlength="11" />
        </div>
      </div>

      <!-- 地区选择 -->
      <div class="form-section">
        <div class="form-item clickable" @click="chooseRegion">
          <span class="label required">所在地区</span>
          <div class="region-display">
            <span v-if="formData.province" class="region-text">{{ formData.province }} {{ formData.city }} {{ formData.district }}</span>
            <span v-else class="placeholder">请选择省市区</span>
            <span class="arrow">›</span>
          </div>
        </div>
      </div>

      <!-- 详细地址 -->
      <div class="form-section">
        <div class="form-item textarea-item">
          <span class="label required">详细地址</span>
          <textarea 
            class="textarea" 
            placeholder="请输入街道、门牌号等详细信息" 
            v-model="formData.detail" 
            maxlength="100"
          ></textarea>
        </div>
      </div>

      <!-- 地址标签 -->
      <div class="form-section">
        <span class="section-title">地址标签</span>
        <div class="label-options">
          <div class="label-item" 
                :class="{ active: formData.label === item }"
                v-for="item in labelOptions" 
                :key="item"
                @click="formData.label = item">
            {{ item }}
          </div>
        </div>
      </div>

      <!-- 默认地址 -->
      <div class="form-section">
        <div class="form-item">
          <span class="label">设为默认地址</span>
          <input type="checkbox" v-model="formData.isDefault" />
        </div>
      </div>
    </div>

    <!-- 底部按钮 -->
    <div class="footer">
      <button class="delete-btn" v-if="isEditMode" @click="deleteAddress">删除地址</button>
      <button class="save-btn" :class="{ disabled: submitting }" @click="saveAddress" :disabled="submitting">
        {{ submitting ? '保存中...' : '保存' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { addressApi } from '@/utils/api'

const router = useRouter()
const route = useRoute()

const addressId = ref(route.query.id || '')
const isEditMode = ref(!!addressId.value)
const submitting = ref(false)
const labelOptions = ['家', '公司', '学校']

const formData = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false,
  label: ''
})

const loadAddressDetail = () => {
  const savedAddresses = localStorage.getItem('addresses')
  if (savedAddresses) {
    const addresses = JSON.parse(savedAddresses)
    const address = addresses.find(addr => addr.id === addressId.value)
    if (address) {
      formData.value = { ...address }
    }
  }
}

const chooseRegion = () => {
  // 模拟地区选择
  const region = prompt('请输入省市区 (例如: 广东省 深圳市 南山区)', '广东省 深圳市 南山区')
  if (region) {
    const parts = region.split(' ')
    if (parts.length >= 3) {
      formData.value.province = parts[0]
      formData.value.city = parts[1]
      formData.value.district = parts[2]
    }
  }
}

const validateForm = () => {
  const { name, phone, province, city, district, detail } = formData.value
  if (!name.trim()) {
    alert('请输入收货人姓名')
    return false
  }
  if (!/^1[3-9]\d{9}$/.test(phone)) {
    alert('请输入正确的手机号')
    return false
  }
  if (!province || !city || !district) {
    alert('请选择所在地区')
    return false
  }
  if (!detail.trim()) {
    alert('请输入详细地址')
    return false
  }
  return true
}

const saveAddress = async () => {
  if (submitting.value || !validateForm()) return
  
  submitting.value = true
  try {
    let addresses = JSON.parse(localStorage.getItem('addresses') || '[]')
    
    if (isEditMode.value) {
      const index = addresses.findIndex(addr => addr.id === addressId.value)
      if (index >= 0) {
        addresses[index] = { ...formData.value, id: addressId.value }
      }
    } else {
      const newAddress = {
        ...formData.value,
        id: 'addr_' + Date.now()
      }
      addresses.push(newAddress)
    }
    
    if (formData.value.isDefault) {
      addresses = addresses.map(addr => ({
        ...addr,
        isDefault: addr.id === (isEditMode.value ? addressId.value : addresses[addresses.length-1].id)
      }))
    }
    
    localStorage.setItem('addresses', JSON.stringify(addresses))
    alert('保存成功')
    router.back()
  } catch (error) {
    console.error('保存失败:', error)
  } finally {
    submitting.value = false
  }
}

const deleteAddress = () => {
  if (confirm('确定要删除该地址吗？')) {
    let addresses = JSON.parse(localStorage.getItem('addresses') || '[]')
    addresses = addresses.filter(addr => addr.id !== addressId.value)
    if (formData.value.isDefault && addresses.length > 0) {
      addresses[0].isDefault = true
    }
    localStorage.setItem('addresses', JSON.stringify(addresses))
    alert('删除成功')
    router.back()
  }
}

onMounted(() => {
  if (isEditMode.value) {
    loadAddressDetail()
  }
})
</script>

<style scoped>
.edit-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 80px;
}

.form {
  padding: 10px;
}

.form-section {
  background: white;
  border-radius: 8px;
  padding: 0 15px;
  margin-bottom: 10px;
}

.section-title {
  display: block;
  padding: 15px 0 10px;
  font-size: 14px;
  color: #666;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid #F0F0F0;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item.clickable {
  cursor: pointer;
}

.form-item.textarea-item {
  align-items: flex-start;
}

.label {
  flex-shrink: 0;
  width: 80px;
  font-size: 14px;
  color: #333;
}

.label.required::before {
  content: '*';
  color: #FF6B6B;
  margin-right: 2px;
}

.input {
  flex: 1;
  font-size: 14px;
  color: #333;
  border: none;
  outline: none;
}

.textarea {
  flex: 1;
  width: 100%;
  min-height: 60px;
  font-size: 14px;
  color: #333;
  border: none;
  outline: none;
  resize: none;
}

.region-display {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.region-text {
  font-size: 14px;
  color: #333;
}

.placeholder {
  font-size: 14px;
  color: #CCC;
}

.arrow {
  font-size: 24px;
  color: #CCC;
  margin-left: 10px;
}

.label-options {
  display: flex;
  gap: 10px;
  padding-bottom: 15px;
}

.label-item {
  flex: 1;
  text-align: center;
  padding: 8px 0;
  border: 1px solid #E0E0E0;
  border-radius: 6px;
  font-size: 14px;
  color: #666;
  cursor: pointer;
}

.label-item.active {
  border-color: #D4A574;
  background: #FFF9E6;
  color: #D4A574;
  font-weight: bold;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 10px 15px;
  display: flex;
  gap: 10px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.delete-btn {
  flex: 1;
  padding: 12px;
  background: white;
  color: #FF6B6B;
  border: 1px solid #FF6B6B;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.save-btn {
  flex: 2;
  padding: 12px;
  background: linear-gradient(135deg, #D4A574, #B08968);
  color: white;
  border-radius: 25px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  cursor: pointer;
}

.save-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style>