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
.address-picker {
  width: 100%;
}

.default-trigger {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  background: #fff;
  border-radius: 8px;
  border: 1px solid #eee;
}

.trigger-text {
  font-size: 14px;
  color: #999;
}

.has-value .trigger-text {
  color: #333;
}

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
  background: rgba(0, 0, 0, 0.5);
}

.modal-content {
  position: relative;
  background: #fff;
  border-radius: 16px 16px 0 0;
  max-height: 80vh;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 16px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.modal-title {
  font-size: 16px;
  font-weight: bold;
}

.address-list {
  flex: 1;
  overflow-y: auto;
  padding: 16px;
}

.address-item {
  padding: 16px;
  border: 1px solid #eee;
  border-radius: 8px;
  margin-bottom: 12px;
}

.address-item.selected {
  border-color: var(--primary-color, #D4A574);
  background: rgba(212, 165, 116, 0.05);
}

.address-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}

.address-name {
  font-size: 15px;
  font-weight: bold;
  margin-right: 8px;
}

.address-phone {
  font-size: 14px;
  color: #666;
  margin-right: 8px;
}

.tag {
  font-size: 10px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #f0f0f0;
  color: #999;
}

.tag-default {
  background: var(--primary-color, #D4A574);
  color: #fff;
}

.address-content {
  font-size: 13px;
  color: #666;
  line-height: 1.4;
}

.empty-address {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 40px 0;
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin-bottom: 16px;
  opacity: 0.5;
}

.empty-text {
  font-size: 15px;
  color: #333;
  margin-bottom: 8px;
}

.empty-hint {
  font-size: 13px;
  color: #999;
}

.modal-footer {
  padding: 16px;
  display: flex;
  gap: 12px;
  border-top: 1px solid #eee;
}

.btn {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 15px;
  border: none;
}

.btn-secondary {
  background: #f5f5f5;
  color: #333;
}

.btn-primary {
  background: var(--primary-color, #D4A574);
  color: #fff;
}

.btn-primary:disabled {
  opacity: 0.5;
}
</style>