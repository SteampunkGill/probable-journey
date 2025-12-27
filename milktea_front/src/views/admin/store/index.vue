<template>
  <div class="store-management">
    <div class="page-header">
      <h2>门店管理</h2>
      <button class="btn-primary" @click="showCreateDialog">创建门店</button>
    </div>

    <div class="filter-section">
      <input v-model="searchQuery" type="text" placeholder="搜索门店名称/地址..." @input="handleSearch" />
      <select v-model="statusFilter" @change="handleSearch">
        <option value="">全部状态</option>
        <option value="OPEN">营业中</option>
        <option value="CLOSED">已关闭</option>
        <option value="MAINTENANCE">维护中</option>
      </select>
    </div>

    <div class="store-table">
      <table>
        <thead>
          <tr>
            <th>门店名称</th>
            <th>编码</th>
            <th>地址</th>
            <th>电话</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="store in filteredStores" :key="store.id">
            <td>{{ store.name }}</td>
            <td>{{ store.code }}</td>
            <td>{{ store.address }}</td>
            <td>{{ store.phone }}</td>
            <td>
              <span :class="['status-tag', store.status.toLowerCase()]">
                {{ getStatusText(store.status) }}
              </span>
            </td>
            <td>
              <button class="btn-text" @click="editStore(store)">编辑</button>
              <button class="btn-text btn-danger" @click="confirmDelete(store)">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 创建/编辑弹窗 -->
    <div v-if="dialogVisible" class="dialog-overlay">
      <div class="dialog">
        <h3>{{ isEdit ? '编辑门店' : '创建门店' }}</h3>
        <form @submit.prevent="saveStore">
          <div class="form-group">
            <label>门店名称</label>
            <input v-model="form.name" required />
          </div>
          <div class="form-group">
            <label>门店编码</label>
            <input v-model="form.code" required />
          </div>
          <div class="form-group">
            <label>所在地区</label>
            <div class="region-select-trigger" @click="regionPickerVisible = true">
              {{ form.province ? `${form.province} ${form.city} ${form.district}` : '请选择省市区' }}
            </div>
          </div>
          <div class="form-group">
            <label>详细地址</label>
            <input v-model="form.address" required />
          </div>
          <div class="form-group">
            <label>电话</label>
            <input v-model="form.phone" required />
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>经度</label>
              <input v-model.number="form.longitude" type="number" step="0.000001" required />
            </div>
            <div class="form-group">
              <label>纬度</label>
              <input v-model.number="form.latitude" type="number" step="0.000001" required />
            </div>
          </div>
          <div class="form-group">
            <label>状态</label>
            <select v-model="form.status">
              <option value="OPEN">营业中</option>
              <option value="CLOSED">已关闭</option>
              <option value="MAINTENANCE">维护中</option>
            </select>
          </div>
          <div class="dialog-footer">
            <button type="button" @click="dialogVisible = false">取消</button>
            <button type="submit" class="btn-primary">保存</button>
          </div>
        </form>
      </div>
    </div>

    <RegionPicker 
      v-model="regionPickerVisible"
      :province="form.province"
      :city="form.city"
      :district="form.district"
      @confirm="onRegionConfirm"
    />
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../../../utils/request'
import RegionPicker from '../../../components/RegionPicker.vue'

const stores = ref([])
const searchQuery = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const regionPickerVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  name: '',
  code: '',
  province: '',
  city: '',
  district: '',
  address: '',
  phone: '',
  status: 'OPEN',
  latitude: 23.129162,
  longitude: 113.264434
})

const fetchStores = async () => {
  try {
    const res = await request.get('/api/admin/stores')
    if (res.code === 200) {
      stores.value = res.data
    }
  } catch (error) {
    console.error('获取门店列表失败:', error)
  }
}

const handleSearch = () => {
  // 搜索逻辑已通过 computed 实现
}

const onRegionConfirm = (data) => {
  form.value.province = data.province
  form.value.city = data.city
  form.value.district = data.district
}

const filteredStores = computed(() => {
  return stores.value.filter(store => {
    const name = store.name || ''
    const address = store.address || ''
    const matchesSearch = name.includes(searchQuery.value) || address.includes(searchQuery.value)
    const matchesStatus = !statusFilter.value || store.status === statusFilter.value
    return matchesSearch && matchesStatus
  })
})

const getStatusText = (status) => {
  const map = {
    'OPEN': '营业中',
    'CLOSED': '已关闭',
    'MAINTENANCE': '维护中'
  }
  return map[status] || status
}

const showCreateDialog = () => {
  isEdit.value = false
  form.value = {
    id: null,
    name: '',
    code: '',
    province: '',
    city: '',
    district: '',
    address: '',
    phone: '',
    status: 'OPEN',
    longitude: 113.264434,
    latitude: 23.129162
  }
  dialogVisible.value = true
}

const editStore = (store) => {
  isEdit.value = true
  form.value = { ...store }
  dialogVisible.value = true
}

const saveStore = async () => {
  try {
    let res
    if (isEdit.value) {
      res = await request.put(`/api/admin/stores/${form.value.id}`, form.value)
    } else {
      res = await request.post('/api/admin/stores', form.value)
    }
    
    if (res.code === 200) {
      alert('保存成功')
      dialogVisible.value = false
      fetchStores()
    } else {
      alert(res.message || '保存失败')
    }
  } catch (error) {
    console.error('保存门店失败:', error)
    alert('保存失败')
  }
}

const confirmDelete = async (store) => {
  if (confirm(`确定要删除门店 "${store.name}" 吗？`)) {
    try {
      const res = await request.delete(`/api/admin/stores/${store.id}`)
      if (res.code === 200) {
        alert('删除成功')
        fetchStores()
      }
    } catch (error) {
      console.error('删除门店失败:', error)
    }
  }
}

onMounted(() => {
  fetchStores()
})
</script>

<style scoped>
.store-management {
  padding: var(--spacing-xl);
  background: var(--background-color);
  min-height: 100vh;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  color: var(--text-color-dark);
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--surface-color);
  border-radius: var(--border-radius-xl);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color);
}

.page-header h2 {
  font-size: 1.8em;
  font-weight: 600;
  color: var(--primary-dark);
  margin: 0;
}

.filter-section {
  display: flex;
  gap: var(--spacing-lg);
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--surface-color);
  border-radius: var(--border-radius-xl);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color);
}

.filter-section input {
  padding: 14px 24px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-xl);
  background: rgba(255, 255, 255, 0.9);
  flex: 1;
}

.filter-section select {
  padding: 14px 24px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-xl);
  background: rgba(255, 255, 255, 0.9);
  min-width: 180px;
}

.store-table {
  background: var(--surface-color);
  border-radius: var(--border-radius-xl);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 2px solid var(--border-color);
  overflow: hidden;
  padding: var(--spacing-lg);
}

table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

th, td {
  padding: var(--spacing-lg);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

th {
  background: rgba(255, 248, 220, 0.8);
  color: var(--primary-dark);
  font-weight: 600;
}

.status-tag {
  padding: 8px 20px;
  border-radius: var(--border-radius-xl);
  font-size: 0.9em;
  font-weight: 600;
}

.status-tag.open { background: #e6f4ea; color: #1e8e3e; }
.status-tag.closed { background: #fce8e6; color: #d93025; }
.status-tag.maintenance { background: #fef7e0; color: #f9ab00; }

.btn-primary {
  padding: 12px 28px;
  background: var(--primary-color);
  color: white;
  border: none;
  border-radius: var(--border-radius-xl);
  cursor: pointer;
  font-weight: 600;
}

.btn-text {
  background: none;
  border: none;
  color: var(--primary-color);
  cursor: pointer;
  margin-right: 10px;
  font-weight: 600;
}

.btn-danger { color: #d93025; }

.dialog-overlay {
  position: fixed;
  top: 0; left: 0; right: 0; bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 30px;
  border-radius: 20px;
  width: 500px;
  max-width: 90vw;
}

.form-group { margin-bottom: 20px; }
.form-group label { display: block; margin-bottom: 8px; font-weight: 600; }
.form-group input, .form-group select {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
}

.region-select-trigger {
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 8px;
  cursor: pointer;
  background: #f9f9f9;
}

.form-row { display: flex; gap: 20px; }
.form-row .form-group { flex: 1; }

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 30px;
}

.dialog-footer button {
  padding: 10px 20px;
  border-radius: 8px;
  border: none;
  cursor: pointer;
}

:root {
  --background-color: #f5f0e1;
  --surface-color: #ffffff;
  --primary-color: #a0522d;
  --primary-dark: #8b4513;
  --border-color: #e0e0e0;
  --spacing-lg: 16px;
  --spacing-xl: 24px;
  --border-radius-xl: 12px;
}
</style>