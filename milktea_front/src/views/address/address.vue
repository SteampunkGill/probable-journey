<template>
  <div class="address-page">
    <!-- 门店选择模式 -->
    <template v-if="isSelectStore">
      <div class="address-list" v-if="storeList.length > 0">
        <div class="address-item selectable"
             v-for="item in storeList"
             :key="item.id"
             @click="selectStore(item)">
          
          <!-- 门店信息 -->
          <div class="address-info">
            <div class="name-phone">
              <span class="name">{{ item.name }}</span>
              <span class="status-tag" :class="{ open: item.status === 'OPEN' }">
                {{ item.status === 'OPEN' ? '营业中' : '休息中' }}
              </span>
            </div>
            
            <div class="address-detail">
              <span class="detail">{{ item.address }}</span>
            </div>
            
            <div class="store-meta" v-if="item.distance">
              <span class="distance">距离您 {{ item.distance.toFixed(2) }}km</span>
              <span class="hours" v-if="item.businessHours">营业时间: {{ item.businessHours }}</span>
            </div>
          </div>
          
          <!-- 选中标记 -->
          <div class="selected-mark" v-if="userStore.selectedStore?.id === item.id">
            <span class="icon">✓</span>
          </div>
        </div>
      </div>

      <!-- 门店空状态 -->
      <div class="empty-state" v-else>
        <img class="empty-icon" src="../../assets/images/icons/address.png" />
        <span class="empty-text">暂无门店</span>
      </div>
    </template>

    <!-- 地址管理模式 -->
    <template v-else>
      <div class="address-list" v-if="addressList.length > 0">
        <div class="address-item"
             :class="{ selectable: mode === 'select' }"
             v-for="item in addressList"
             :key="item.id"
             @click="selectAddress(item)">
          
          <!-- 默认标签 -->
          <div class="default-badge" v-if="item.isDefault">默认</div>
          
          <!-- 地址信息 -->
          <div class="address-info">
            <div class="name-phone">
              <span class="name">{{ item.name }}</span>
              <span class="phone">{{ item.phone }}</span>
            </div>
            
            <div class="address-detail">
              <span class="tag" v-if="item.tag || item.label">{{ item.tag || item.label }}</span>
              <span class="detail">{{ item.province }} {{ item.city }} {{ item.district }} {{ item.detail }}</span>
            </div>
          </div>
          
          <!-- 操作按钮 -->
          <div class="address-actions" v-if="mode === 'list'">
            <div class="action-left">
              <input type="checkbox"
                     :checked="item.isDefault"
                     @change.stop="toggleDefault(item.id)" />
              <span class="action-label">设为默认</span>
            </div>
            
            <div class="action-right">
              <button class="action-btn edit" @click.stop="editAddress(item.id)">
                <span class="icon">✏️</span>
                <span>编辑</span>
              </button>
              <button class="action-btn delete" @click.stop="deleteAddress(item)">
                <span class="icon">🗑️</span>
                <span>删除</span>
              </button>
            </div>
          </div>
          
          <!-- 选中标记（选择模式） -->
          <div class="selected-mark" v-if="mode === 'select' && selectedId === item.id">
            <span class="icon">✓</span>
          </div>
        </div>
      </div>

      <!-- 地址空状态 -->
      <div class="empty-state" v-else>
        <img class="empty-icon" src="../../assets/images/icons/address.png" />
        <span class="empty-text">暂无收货地址</span>
      </div>

      <!-- 底部添加按钮 -->
      <div class="footer" v-if="mode === 'list'">
        <button class="add-btn" @click="addAddress">
          <span class="icon">➕</span>
          <span>添加新地址</span>
        </button>
      </div>
    </template>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { addressApi, storeApi } from '@/utils/api'
import { useUserStore } from '@/store/user'

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()

const mode = ref(route.query.mode || 'list')
const type = ref(route.query.type || '')
const selectedId = ref(route.query.selectedId || '')
const addressList = ref([])
const storeList = ref([])

const isSelectStore = computed(() => type.value === 'select_store')

const loadAddressList = async () => {
  try {
    const res = await addressApi.getAddressList()
    // 拦截器已返回 res.data
    addressList.value = res || []
  } catch (error) {
    console.error('加载地址列表失败:', error)
  }
}

const loadStoreList = async () => {
  try {
    // 先尝试不带定位加载门店列表，保证页面有内容
    const initialRes = await storeApi.getNearbyStores({
      latitude: null,
      longitude: null
    })
    storeList.value = initialRes || []

    // 异步获取定位并更新列表，不阻塞页面显示
    if (navigator.geolocation) {
      new Promise((resolve) => {
        navigator.geolocation.getCurrentPosition(
          (pos) => resolve({ latitude: pos.coords.latitude, longitude: pos.coords.longitude }),
          () => resolve(null),
          { timeout: 5000 }
        )
      }).then(async (location) => {
        if (location) {
          const res = await storeApi.getNearbyStores({
            latitude: location.latitude,
            longitude: location.longitude
          })
          storeList.value = res || []
        }
      })
    }
  } catch (error) {
    console.error('加载门店列表失败:', error)
  }
}

const selectAddress = (address) => {
  if (mode.value === 'select') {
    localStorage.setItem('selectedAddress', JSON.stringify(address))
    router.back()
  }
}

const selectStore = (store) => {
  userStore.setSelectedStore(store)
  router.back()
}

const addAddress = () => {
  router.push('/address/edit')
}

const editAddress = (id) => {
  router.push(`/address/edit?id=${id}`)
}

const deleteAddress = async (address) => {
  if (confirm(`确定要删除${address.name}的地址吗？`)) {
    try {
      await addressApi.deleteAddress(address.id)
      // 拦截器在非 200 时会抛出异常，能执行到这里说明成功
      loadAddressList()
    } catch (error) {
      console.error('删除地址失败:', error)
      alert(error.message || '删除失败')
    }
  }
}

const toggleDefault = async (id) => {
  try {
    const address = addressList.value.find(a => a.id === id)
    if (!address) return
    
    await addressApi.updateAddress(id, { ...address, isDefault: true })
    // 拦截器在非 200 时会抛出异常，能执行到这里说明成功
    loadAddressList()
  } catch (error) {
    console.error('设置默认地址失败:', error)
  }
}

onMounted(() => {
  if (isSelectStore.value) {
    loadStoreList()
  } else {
    loadAddressList()
  }
})
</script>

<style scoped>
.address-page {
  min-height: 100vh;
  background: #F5F5F5;
  padding-bottom: 80px;
}

.address-list {
  padding: 10px;
}

.address-item {
  position: relative;
  background: white;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 10px;
  box-shadow: 0 1px 5px rgba(0, 0, 0, 0.05);
}

.address-item.selectable {
  padding-right: 50px;
  cursor: pointer;
}

.default-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, #D4A574, #B08968);
  color: white;
  padding: 4px 12px;
  border-radius: 0 8px 0 10px;
  font-size: 11px;
  font-weight: bold;
}

.status-tag {
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 4px;
  background: #F5F5F5;
  color: #999;
}

.status-tag.open {
  background: #E6F7ED;
  color: #27AE60;
}

.store-meta {
  margin-top: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.distance, .hours {
  font-size: 12px;
  color: #999;
}

.address-info {
  margin-bottom: 12px;
}

.name-phone {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.name {
  font-size: 16px;
  font-weight: bold;
  color: #333;
}

.phone {
  font-size: 14px;
  color: #666;
}

.address-detail {
  display: flex;
  align-items: flex-start;
}

.tag {
  flex-shrink: 0;
  padding: 2px 6px;
  background: #FFF9E6;
  color: #D4A574;
  border-radius: 4px;
  font-size: 11px;
  margin-right: 6px;
  border: 1px solid #D4A574;
}

.detail {
  flex: 1;
  font-size: 13px;
  color: #666;
  line-height: 1.6;
}

.address-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #F0F0F0;
}

.action-left {
  display: flex;
  align-items: center;
  gap: 6px;
}

.action-label {
  font-size: 13px;
  color: #666;
}

.action-right {
  display: flex;
  gap: 10px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  padding: 6px 12px;
  border-radius: 25px;
  font-size: 12px;
  border: none;
  cursor: pointer;
}

.action-btn.edit {
  background: #FFF9E6;
  color: #D4A574;
}

.action-btn.delete {
  background: #FFF5F5;
  color: #FF6B6B;
}

.selected-mark {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  width: 30px;
  height: 30px;
  background: #D4A574;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.selected-mark .icon {
  color: white;
  font-size: 20px;
  font-weight: bold;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 100px 0;
}

.empty-icon {
  width: 100px;
  height: 100px;
  margin-bottom: 20px;
  opacity: 0.3;
}

.empty-text {
  font-size: 14px;
  color: #999;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  padding: 10px 15px;
  box-shadow: 0 -2px 10px rgba(0, 0, 0, 0.05);
}

.add-btn {
  width: 100%;
  background: linear-gradient(135deg, #D4A574, #B08968);
  color: white;
  border-radius: 25px;
  padding: 12px;
  font-size: 16px;
  font-weight: bold;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
}
</style>