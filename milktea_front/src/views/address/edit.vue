<template>
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

    <!-- 地区选择器 -->
    <RegionPicker
      v-model="regionPickerVisible"
      :province="formData.province"
      :city="formData.city"
      :district="formData.district"
      @confirm="onRegionConfirm"
    />

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
import RegionPicker from '@/components/RegionPicker.vue'

const router = useRouter()
const route = useRoute()

const addressId = ref(route.query.id || '')
const isEditMode = ref(!!addressId.value)
const submitting = ref(false)
const regionPickerVisible = ref(false)
const labelOptions = ['家', '公司', '学校']

const formData = ref({
  name: '',
  phone: '',
  province: '',
  city: '',
  district: '',
  detail: '',
  isDefault: false,
  tag: '',
  label: ''
})

const loadAddressDetail = async () => {
  try {
    const res = await addressApi.getAddressList()
    const list = (res.data || res) || []
    if (list && Array.isArray(list)) {
      const address = list.find(addr => addr.id == addressId.value)
      if (address) {
        formData.value = {
          ...address,
          label: address.tag || address.label || ''
        }
      }
    }
  } catch (error) {
    console.error('加载地址详情失败:', error)
  }
}

const chooseRegion = () => {
  regionPickerVisible.value = true
}

const onRegionConfirm = (data) => {
  formData.value.province = data.province
  formData.value.city = data.city
  formData.value.district = data.district
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
    if (formData.value.label) {
      formData.value.tag = formData.value.label
    }

    if (isEditMode.value) {
      await addressApi.updateAddress(addressId.value, formData.value)
    } else {
      await addressApi.addAddress(formData.value)
    }
    
    alert('保存成功')
    // 触发一个自定义事件或使用全局状态通知列表刷新
    window.dispatchEvent(new CustomEvent('address-updated'))
    // 兼容某些情况下直接返回不刷新的问题，可以尝试在 localStorage 标记一下
    localStorage.setItem('needAddressRefresh', 'true')
    setTimeout(() => {
      router.back()
    }, 100)
  } catch (error) {
    console.error('保存失败:', error)
    const errorMsg = error.response?.data?.message || error.message || '保存失败，请稍后重试'
    alert(errorMsg)
  } finally {
    submitting.value = false
  }
}

const deleteAddress = async () => {
  if (confirm('确定要删除该地址吗？')) {
    try {
      await addressApi.deleteAddress(addressId.value)
      alert('删除成功')
      router.back()
    } catch (error) {
      console.error('删除失败:', error)
      alert(error.message || '删除失败')
    }
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
  background: linear-gradient(135deg, #f5f0e1 0%, #f8f4e6 100%);
  padding-bottom: 100px;
}

.form {
  padding: 24px 16px;
}

.form-section {
  background: #fff8dc;
  border-radius: 24px;
  padding: 0 20px;
  margin-bottom: 20px;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid #e8dccb;
  transition: all 0.3s ease-out;
}

.form-section:hover {
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.12);
  transform: translateY(-2px);
}

.section-title {
  display: block;
  padding: 20px 0 16px;
  font-size: 17px;
  color: #a0522d;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  letter-spacing: 0.02em;
}

.form-item {
  display: flex;
  align-items: center;
  padding: 22px 0;
  border-bottom: 2px dashed #d4c7b5;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item.clickable {
  cursor: pointer;
  transition: all 0.25s ease-out;
}

.form-item.clickable:hover {
  background: rgba(255, 248, 220, 0.5);
  border-radius: 16px;
  padding-left: 12px;
  padding-right: 12px;
  margin: 0 -12px;
}

.form-item.textarea-item {
  align-items: flex-start;
}

.label {
  flex-shrink: 0;
  width: 100px;
  font-size: 16px;
  color: #4a3b30;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
  letter-spacing: 0.02em;
}

.label.required::before {
  content: '✦';
  color: #ff6b6b;
  margin-right: 6px;
  font-size: 14px;
}

.input {
  flex: 1;
  font-size: 16px;
  color: #4a3b30;
  border: none;
  outline: none;
  background: transparent;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  padding: 8px 0;
  letter-spacing: 0.02em;
}

.input::placeholder {
  color: #a09080;
  opacity: 0.7;
}

.textarea {
  flex: 1;
  width: 100%;
  min-height: 100px;
  font-size: 16px;
  color: #4a3b30;
  border: none;
  outline: none;
  resize: none;
  background: transparent;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  padding: 12px 0;
  letter-spacing: 0.02em;
  line-height: 1.6;
}

.textarea::placeholder {
  color: #a09080;
  opacity: 0.7;
}

.region-display {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.region-text {
  font-size: 16px;
  color: #4a3b30;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
  letter-spacing: 0.02em;
}

.placeholder {
  font-size: 16px;
  color: #a09080;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.02em;
  opacity: 0.7;
}

.arrow {
  font-size: 28px;
  color: #d2b48c;
  margin-left: 12px;
  opacity: 0.8;
  transform: scale(1.2);
  transition: transform 0.25s ease-out;
}

.form-item.clickable:hover .arrow {
  transform: scale(1.4) translateX(4px);
  color: #a0522d;
}

.label-options {
  display: flex;
  gap: 16px;
  padding-bottom: 24px;
}

.label-item {
  flex: 1;
  text-align: center;
  padding: 14px 0;
  border: 2px solid #deb887;
  border-radius: 20px;
  font-size: 15px;
  color: #7a6a5b;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
  letter-spacing: 0.02em;
  background: rgba(255, 255, 255, 0.7);
  transition: all 0.3s ease-out;
}

.label-item:hover {
  transform: translateY(-3px) scale(1.03);
  box-shadow: 0 6px 15px rgba(222, 184, 135, 0.3);
}

.label-item.active {
  border-color: #a0522d;
  background: linear-gradient(135deg, #ffc0cb, #fff8dc);
  color: #a0522d;
  font-weight: 600;
  box-shadow: 0 8px 20px rgba(160, 82, 45, 0.2);
  transform: translateY(-3px) scale(1.03);
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(245, 240, 225, 0.95);
  padding: 20px 24px;
  display: flex;
  gap: 16px;
  box-shadow: 0 -8px 30px rgba(160, 82, 45, 0.1);
  backdrop-filter: blur(10px);
  border-top: 2px solid #e8dccb;
}

.delete-btn {
  flex: 1;
  padding: 18px;
  background: rgba(255, 255, 255, 0.9);
  color: #ff6b6b;
  border: 2px solid rgba(255, 107, 107, 0.3);
  border-radius: 25px;
  font-size: 17px;
  font-weight: 600;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.03em;
  transition: all 0.3s ease-out;
}

.delete-btn:hover {
  background: rgba(255, 107, 107, 0.1);
  border-color: #ff6b6b;
  transform: translateY(-2px);
  box-shadow: 0 6px 15px rgba(255, 107, 107, 0.2);
}

.save-btn {
  flex: 2;
  padding: 18px;
  background: linear-gradient(135deg, #a0522d, #8b4513);
  color: #fff8dc;
  border-radius: 25px;
  font-size: 18px;
  font-weight: 600;
  border: none;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.04em;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
  transition: all 0.3s ease-out;
}

.save-btn:hover:not(.disabled) {
  background: linear-gradient(135deg, #8b4513, #a0522d);
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 10px 25px rgba(160, 82, 45, 0.4);
}

.save-btn:active:not(.disabled) {
  transform: translateY(0) scale(0.98);
}

.save-btn.disabled {
  opacity: 0.6;
  cursor: not-allowed;
  background: linear-gradient(135deg, #d2b48c, #deb887);
  transform: none;
  box-shadow: none;
}

/* 输入框聚焦效果 */
.input:focus,
.textarea:focus {
  background: rgba(255, 248, 220, 0.5);
  border-radius: 12px;
  padding: 8px 12px;
  margin: -8px -12px;
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.15);
}

.textarea:focus {
  margin: -12px -12px;
}
</style>