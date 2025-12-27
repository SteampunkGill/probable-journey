<template>
  <div class="address-picker">
    <!-- 选择按钮 -->
    <div class="picker-trigger" @click="showPicker">
      <slot name="trigger">
        <div class="default-trigger" :class="{ 'has-value': value }">
          <span class="trigger-text">
            {{ value ? displayText : placeholder }}
          </span>
          <i class="iconfont icon-right"></i>
        </div>
      </slot>
    </div>

    <!-- 地址选择弹窗 -->
    <div class="address-modal" v-if="showModal">
      <div class="modal-mask" @click="hidePicker"></div>
      
      <div class="modal-content">
        <!-- 头部 -->
        <div class="modal-header">
          <span class="modal-title">选择地址</span>
          <i class="iconfont icon-close" @click="hidePicker"></i>
        </div>

        <!-- 地址列表 -->
        <div class="address-list">
          <div 
            class="address-item" 
            :class="{ selected: selectedId === address.id }" 
            v-for="address in addressList" 
            :key="address.id"
            @click="selectAddress(address.id)"
          >
            <div class="address-header">
              <span class="address-name">{{ address.name }}</span>
              <span class="address-phone">{{ address.phone }}</span>
              <div class="address-tags">
                <span class="tag tag-default" v-if="address.isDefault">默认</span>
              </div>
            </div>
            
            <div class="address-content">
              <span class="address-text">
                {{ address.province }}{{ address.city }}{{ address.district }}{{ address.detail }}
              </span>
            </div>
          </div>

          <!-- 空状态 -->
          <div class="empty-address" v-if="addressList.length === 0">
            <img src="../assets/images/icons/address.png" class="empty-icon" />
            <span class="empty-text">暂无地址</span>
            <span class="empty-hint">请先添加地址</span>
          </div>
        </div>

        <!-- 底部操作 -->
        <div class="modal-footer">
          <button class="btn btn-secondary" @click="goToAddAddress">
            <i class="iconfont icon-add"></i>
            <span>添加新地址</span>
          </button>
          
          <button 
            class="btn btn-primary" 
            @click="confirmSelection"
            :disabled="!selectedId"
          >
            确定
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import * as storage from '../utils/storage'

const props = defineProps({
  // 当前选中的地址ID
  value: {
    type: String,
    default: ''
  },
  // 占位符
  placeholder: {
    type: String,
    default: '请选择地址'
  },
  // 是否必选
  required: {
    type: Boolean,
    default: false
  },
  // 是否显示默认标签
  showDefaultTag: {
    type: Boolean,
    default: true
  },
  // 是否允许添加新地址
  allowAdd: {
    type: Boolean,
    default: true
  }
})

const emit = defineEmits(['update:value', 'change'])
const router = useRouter()

const showModal = ref(false)
const addressList = ref([])
const selectedId = ref('')
const displayText = ref('')
const selectedAddress = ref(null)

const loadAddressList = () => {
  const list = storage.getAddresses() || []
  
  // 如果没有地址，使用默认数据
  if (list.length === 0) {
    list.push({
      id: 'default_1',
      name: '示例用户',
      phone: '13800138000',
      province: '北京市',
      city: '北京市',
      district: '朝阳区',
      detail: '建国路88号',
      isDefault: true
    })
  }
  
  addressList.value = list
}

const updateDisplayText = (addressId) => {
  const address = addressList.value.find(addr => addr.id === addressId)
  
  if (address) {
    displayText.value = `${address.name} ${address.phone} ${address.province}${address.city}${address.district}${address.detail}`
    selectedAddress.value = address
  } else {
    displayText.value = ''
    selectedAddress.value = null
  }
}

const showPicker = () => {
  loadAddressList()
  selectedId.value = props.value
  showModal.value = true
}

const hidePicker = () => {
  showModal.value = false
}

const selectAddress = (id) => {
  selectedId.value = id
}

const confirmSelection = () => {
  if (!selectedId.value) {
    alert('请选择地址')
    return
  }
  
  const address = addressList.value.find(addr => addr.id === selectedId.value)
  updateDisplayText(selectedId.value)
  
  emit('update:value', selectedId.value)
  emit('change', {
    value: selectedId.value,
    address: address
  })
  
  hidePicker()
}

const goToAddAddress = () => {
  hidePicker()
  router.push('/address/edit')
}

watch(() => props.value, (newVal) => {
  updateDisplayText(newVal)
})

onMounted(() => {
  loadAddressList()
  updateDisplayText(props.value)
})
</script>
<style scoped>
/* 饮饮茶 (SipSipTea) - 奶茶主题地址选择器样式 */
.address-picker {
  width: 100%;
}

/* 选择按钮样式 */
.default-trigger {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-sm);
  background: linear-gradient(135deg, var(--accent-cream) 0%, var(--surface-color) 100%);
  border-radius: var(--border-radius-lg);
  border: 2px solid rgba(var(--primary-color-rgb), 0.15);
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.05);
  backdrop-filter: blur(3px);
  letter-spacing: 0.05em;
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.default-trigger:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.1);
  border-color: rgba(var(--primary-color-rgb), 0.25);
}

.default-trigger:active {
  transform: translateY(0) scale(0.98);
}

.trigger-text {
  font-size: 14px;
  color: var(--text-color-medium);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.7;
  transition: all 0.3s ease-out;
}

.has-value .trigger-text {
  color: var(--primary-color);
  opacity: 1;
  font-weight: 500;
}

/* 弹窗样式 */
.address-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 1000;
  display: flex;
  flex-direction: column;
  justify-content: flex-end;
}

.modal-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(var(--primary-color-rgb), 0.12);
  backdrop-filter: blur(2px);
  animation: fadeIn 0.3s ease-out;
}

.modal-content {
  position: relative;
  background: linear-gradient(to bottom, var(--accent-cream) 0%, var(--surface-color) 100%);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
  box-shadow: 0 -8px 30px rgba(var(--primary-color-rgb), 0.08);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 弹窗头部 */
.modal-header {
  padding: var(--spacing-lg) var(--spacing-md);
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid var(--border-color);
  background: rgba(var(--surface-color-rgb), 0.9);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}

.modal-title {
  font-size: 18px;
  font-weight: 600;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  color: var(--primary-color);
  letter-spacing: 0.08em;
  line-height: 1.6;
}

.icon-close {
  font-size: 20px;
  color: var(--text-color-medium);
  cursor: pointer;
  transition: all 0.3s ease-out;
  padding: 4px;
  border-radius: 50%;
}

.icon-close:hover {
  color: var(--primary-color);
  background: rgba(var(--primary-color-rgb), 0.1);
  transform: rotate(90deg);
}

/* 地址列表 */
.address-list {
  flex: 1;
  overflow-y: auto;
  padding: var(--spacing-md);
}

.address-item {
  padding: var(--spacing-md);
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-sm);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease-out;
  cursor: pointer;
}

.address-item:hover {
  transform: translateY(-2px);
  border-color: var(--primary-light);
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.08);
}

.address-item.selected {
  border-color: var(--primary-color);
  background: linear-gradient(135deg, rgba(var(--primary-color-rgb), 0.08) 0%, rgba(var(--primary-dark-rgb), 0.04) 100%);
  box-shadow: 0 4px 20px rgba(var(--primary-color-rgb), 0.1);
  transform: translateY(-2px) scale(1.01);
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: var(--spacing-xs);
  flex-wrap: wrap;
  gap: var(--spacing-xs);
}

.address-name {
  font-size: 16px;
  font-weight: 600;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  color: var(--text-color-dark);
  letter-spacing: 0.05em;
}

.address-phone {
  font-size: 14px;
  color: var(--accent-pink);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 500;
}

.tag {
  font-size: 11px;
  padding: 3px 8px;
  border-radius: var(--border-radius-sm);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.05em;
  font-weight: 500;
}

.tag-default {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: var(--accent-cream);
  box-shadow: 0 2px 8px rgba(var(--primary-color-rgb), 0.2);
}

.address-content {
  font-size: 14px;
  color: var(--text-color-medium);
  line-height: 1.6;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.85;
  letter-spacing: 0.03em;
}

/* 空状态 */
.empty-address {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: var(--spacing-xl) 0;
  text-align: center;
}

.empty-icon {
  width: 80px;
  height: 80px;
  margin-bottom: var(--spacing-lg);
  opacity: 0.4;
  filter: grayscale(0.8);
  transition: all 0.5s ease-out;
}

.empty-address:hover .empty-icon {
  opacity: 0.6;
  filter: grayscale(0.5);
  transform: scale(1.05);
}

.empty-text {
  font-size: 16px;
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-xs);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  font-weight: 500;
}

.empty-hint {
  font-size: 14px;
  color: var(--accent-pink);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  opacity: 0.7;
  letter-spacing: 0.03em;
}

/* 底部操作按钮 */
.modal-footer {
  padding: var(--spacing-lg) var(--spacing-md);
  display: flex;
  gap: var(--spacing-sm);
  border-top: 1px solid var(--border-color);
  background: rgba(var(--surface-color-rgb), 0.9);
}

.btn {
  flex: 1;
  height: 48px;
  border-radius: var(--border-radius-lg);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  border: none;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.05em;
  font-weight: 600;
  transition: all 0.3s ease-out;
  cursor: pointer;
  gap: 8px;
}

.btn-secondary {
  background: rgba(var(--primary-color-rgb), 0.08);
  color: var(--primary-color);
  border: 2px solid rgba(var(--primary-color-rgb), 0.2);
}

.btn-secondary:hover {
  background: rgba(var(--primary-color-rgb), 0.15);
  border-color: rgba(var(--primary-color-rgb), 0.3);
  transform: translateY(-2px);
}

.btn-secondary:active {
  transform: translateY(0) scale(0.98);
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: var(--accent-cream);
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.2);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.3);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.btn-primary:disabled {
  opacity: 0.5;
  background: linear-gradient(135deg, var(--primary-light) 0%, var(--border-color) 100%);
  cursor: not-allowed;
  transform: none !important;
  box-shadow: none !important;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

/* 滚动条样式 */
.address-list::-webkit-scrollbar {
  width: 6px;
}

.address-list::-webkit-scrollbar-track {
  background: rgba(var(--border-color-rgb), 0.3);
  border-radius: var(--border-radius-sm);
}

.address-list::-webkit-scrollbar-thumb {
  background: var(--primary-light);
  border-radius: var(--border-radius-sm);
}

.address-list::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}
</style>