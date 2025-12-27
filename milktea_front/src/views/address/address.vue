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
      <!-- Tab 切换 -->
      <div class="address-tabs">
        <div class="tab-item" :class="{ active: activeTab === 'mine' }" @click="activeTab = 'mine'">我的地址</div>
        <div class="tab-item" :class="{ active: activeTab === 'history' }" @click="activeTab = 'history'">历史记录</div>
      </div>

      <!-- 我的地址列表 -->
      <div v-if="activeTab === 'mine'">
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
      </div>

      <!-- 历史记录列表 -->
      <div v-if="activeTab === 'history'">
        <div class="address-list" v-if="historyList.length > 0">
          <div class="address-item history"
               v-for="item in historyList"
               :key="'hist-'+item.id"
               @click="selectAddress(item)">
            <div class="address-info">
              <div class="name-phone">
                <span class="name">{{ item.name }}</span>
                <span class="phone">{{ item.phone }}</span>
              </div>
              <div class="address-detail">
                <span class="detail">{{ item.province }} {{ item.city }} {{ item.district }} {{ item.detail }}</span>
              </div>
              <div class="history-meta" v-if="item.usedCount">
                <span class="used-count">使用次数: {{ item.usedCount }}</span>
              </div>
            </div>
          </div>
        </div>
        <div class="empty-state" v-else>
          <img class="empty-icon" src="../../assets/images/icons/address.png" />
          <span class="empty-text">暂无历史地址</span>
        </div>
      </div>

      <!-- 底部添加按钮 -->
      <div class="footer">
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
const activeTab = ref('mine')
const addressList = ref([])
const historyList = ref([])
const storeList = ref([])

const isSelectStore = computed(() => type.value === 'select_store')

const loadAddressList = async () => {
  try {
    const res = await addressApi.getAddressList()
    // 统一处理 ApiResponse 结构，获取 data 字段
    addressList.value = (res.data || res) || []
    
    // 加载历史地址
    const historyRes = await addressApi.getAddressHistory()
    historyList.value = (historyRes.data || historyRes) || []
  } catch (error) {
    console.error('加载地址列表失败:', error)
  }
}

const loadStoreList = async () => {
  try {
    // 尝试获取用户默认地址作为参考
    let refProvince = '', refCity = '', refDistrict = ''
    try {
      const addrRes = await addressApi.getAddressList()
      const defaultAddr = addrRes.find(a => a.isDefault) || addrRes[0]
      if (defaultAddr) {
        refProvince = defaultAddr.province
        refCity = defaultAddr.city
        refDistrict = defaultAddr.district
      }
    } catch (e) {
      console.warn('获取参考地址失败')
    }

    // 先尝试不带定位加载门店列表，保证页面有内容
    const initialRes = await storeApi.getNearbyStores({
      latitude: null,
      longitude: null,
      province: refProvince,
      city: refCity,
      district: refDistrict
    })
    
    // 统一处理后端返回的数据结构
    const resData = initialRes.data || initialRes
    storeList.value = Array.isArray(resData) ? resData : (resData.list || [])

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
            longitude: location.longitude,
            province: refProvince,
            city: refCity,
            district: refDistrict
          })
          const resData2 = res.data || res
          storeList.value = Array.isArray(resData2) ? resData2 : (resData2.list || [])
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
</script><style scoped>
.address-page {
  min-height: 100vh;
  background: var(--background-color, #f5f0e1);
  padding-bottom: 100px;
}

.address-tabs {
  display: flex;
  background: var(--surface-color, #e8dccb);
  padding: 12px 20px;
  margin-bottom: 16px;
  border-radius: 0 0 25px 25px;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.08);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 14px 0;
  font-size: 16px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  position: relative;
  cursor: pointer;
  border-radius: 20px;
  margin: 0 6px;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.tab-item.active {
  color: var(--primary-color, #a0522d);
  font-weight: 600;
  background: rgba(255, 248, 220, 0.4);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 6px;
  left: 50%;
  transform: translateX(-50%);
  width: 30px;
  height: 4px;
  background: var(--accent-cream, #fff8dc);
  border-radius: 2px;
}

.history-meta {
  margin-top: 10px;
  font-size: 13px;
  color: var(--primary-color, #a0522d);
  opacity: 0.8;
  font-family: 'Quicksand', sans-serif;
}

.address-list {
  padding: 20px;
}

.address-item {
  position: relative;
  background: var(--surface-color, #e8dccb);
  border-radius: 25px;
  padding: 24px;
  margin-bottom: 20px;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.1);
  border: 2px solid var(--border-color, #d4c7b5);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  overflow: hidden;
}

.address-item:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 10px 25px rgba(160, 82, 45, 0.15);
}

.address-item.selectable {
  padding-right: 70px;
  cursor: pointer;
}

.default-badge {
  position: absolute;
  top: 0;
  right: 0;
  background: linear-gradient(135deg, var(--primary-color, #a0522d), var(--primary-dark, #8b4513));
  color: var(--accent-cream, #fff8dc);
  padding: 8px 20px;
  border-radius: 0 25px 0 30px;
  font-size: 13px;
  font-weight: 600;
  font-family: 'Nunito', sans-serif;
  letter-spacing: 0.5px;
  box-shadow: 0 4px 10px rgba(139, 69, 19, 0.2);
}

.status-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 20px;
  background: rgba(222, 184, 135, 0.15);
  color: var(--accent-brown, #deb887);
  font-family: 'Quicksand', sans-serif;
  font-weight: 500;
  margin-left: 10px;
}

.status-tag.open {
  background: rgba(255, 192, 203, 0.2);
  color: var(--accent-pink, #ffc0cb);
}

.store-meta {
  margin-top: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.distance, .hours {
  font-size: 14px;
  color: var(--primary-color, #a0522d);
  font-family: 'Quicksand', sans-serif;
  opacity: 0.9;
  display: flex;
  align-items: center;
  gap: 6px;
}

.distance::before {
  content: '📍';
  font-size: 12px;
}

.hours::before {
  content: '🕐';
  font-size: 12px;
}

.address-info {
  margin-bottom: 20px;
}

.name-phone {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 16px;
}

.name {
  font-size: 20px;
  font-weight: 600;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Nunito', sans-serif;
  letter-spacing: 0.3px;
}

.phone {
  font-size: 16px;
  color: var(--primary-color, #a0522d);
  font-family: 'Quicksand', sans-serif;
  opacity: 0.9;
  font-weight: 500;
}

.address-detail {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.tag {
  flex-shrink: 0;
  padding: 6px 14px;
  background: rgba(255, 192, 203, 0.15);
  color: var(--accent-pink, #ffc0cb);
  border-radius: 20px;
  font-size: 13px;
  font-weight: 500;
  border: 1px solid rgba(255, 192, 203, 0.3);
  font-family: 'Quicksand', sans-serif;
}

.detail {
  flex: 1;
  font-size: 15px;
  color: var(--text-color-dark, #4a3b30);
  line-height: 1.7;
  font-family: 'Quicksand', sans-serif;
  opacity: 0.9;
}

.address-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 20px;
  border-top: 2px dashed var(--border-color, #d4c7b5);
}

.action-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.action-label {
  font-size: 15px;
  color: var(--primary-color, #a0522d);
  font-family: 'Quicksand', sans-serif;
  font-weight: 500;
}

.action-right {
  display: flex;
  gap: 16px;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 10px 20px;
  border-radius: 20px;
  font-size: 14px;
  border: none;
  cursor: pointer;
  font-family: 'Quicksand', sans-serif;
  font-weight: 500;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.action-btn:hover {
  transform: translateY(-2px) scale(1.05);
}

.action-btn.edit {
  background: rgba(255, 192, 203, 0.2);
  color: var(--accent-pink, #ffc0cb);
  border: 2px solid rgba(255, 192, 203, 0.4);
}

.action-btn.edit:hover {
  background: rgba(255, 192, 203, 0.3);
  box-shadow: 0 4px 12px rgba(255, 192, 203, 0.2);
}

.action-btn.delete {
  background: rgba(255, 107, 107, 0.1);
  color: #ff6b6b;
  border: 2px solid rgba(255, 107, 107, 0.2);
}

.action-btn.delete:hover {
  background: rgba(255, 107, 107, 0.15);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.15);
}

.selected-mark {
  position: absolute;
  right: 24px;
  top: 50%;
  transform: translateY(-50%);
  width: 40px;
  height: 40px;
  background: linear-gradient(135deg, var(--primary-color, #a0522d), var(--primary-dark, #8b4513));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 6px 15px rgba(160, 82, 45, 0.3);
  animation: bounceIn 0.5s cubic-bezier(0.4, 0, 0.2, 1);
}

@keyframes bounceIn {
  0% { transform: translateY(-50%) scale(0); }
  70% { transform: translateY(-50%) scale(1.1); }
  100% { transform: translateY(-50%) scale(1); }
}

.selected-mark .icon {
  color: var(--accent-cream, #fff8dc);
  font-size: 24px;
  font-weight: bold;
}

.empty-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 120px 0;
}

.empty-icon {
  width: 140px;
  height: 140px;
  margin-bottom: 28px;
  opacity: 0.5;
  filter: drop-shadow(0 8px 16px rgba(160, 82, 45, 0.1));
}

.empty-text {
  font-size: 16px;
  color: var(--primary-color, #a0522d);
  font-family: 'Nunito', sans-serif;
  opacity: 0.7;
  font-weight: 500;
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  color: var(--primary-color, #a0522d);
  padding: 16px 24px;
  font-family: 'Nunito', sans-serif;
  letter-spacing: 0.5px;
}

.address-item.history {
  background: rgba(255, 255, 255, 0.7);
  padding: 20px 24px;
  margin-bottom: 16px;
}

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: rgba(245, 240, 225, 0.95);
  padding: 20px 24px;
  box-shadow: 0 -6px 20px rgba(160, 82, 45, 0.1);
  backdrop-filter: blur(10px);
  border-radius: 30px 30px 0 0;
}

.add-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--primary-color, #a0522d), var(--primary-dark, #8b4513));
  color: var(--accent-cream, #fff8dc);
  border-radius: 30px;
  padding: 20px;
  font-size: 18px;
  font-weight: 600;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 12px;
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  letter-spacing: 0.5px;
  box-shadow: 0 8px 20px rgba(160, 82, 45, 0.25);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.add-btn:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: 0 12px 25px rgba(160, 82, 45, 0.35);
}

.add-btn:active {
  transform: translateY(0) scale(0.98);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .address-tabs {
    padding: 10px 16px;
    margin-bottom: 12px;
  }

  .tab-item {
    padding: 12px 0;
    font-size: 15px;
    margin: 0 4px;
  }

  .address-list {
    padding: 16px;
  }

  .address-item {
    padding: 20px;
    border-radius: 22px;
    margin-bottom: 16px;
  }

  .name {
    font-size: 18px;
  }

  .phone {
    font-size: 15px;
  }

  .action-right {
    flex-direction: column;
    gap: 12px;
  }

  .action-btn {
    padding: 8px 16px;
    font-size: 13px;
  }

  .footer {
    padding: 16px 20px;
  }

  .add-btn {
    padding: 18px;
    font-size: 16px;
  }
}
</style>