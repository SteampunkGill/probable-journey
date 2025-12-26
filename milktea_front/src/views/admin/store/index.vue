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
            <label>地址</label>
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
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import request from '../../../utils/request'

const stores = ref([])
const searchQuery = ref('')
const statusFilter = ref('')
const dialogVisible = ref(false)
const isEdit = ref(false)
const form = ref({
  id: null,
  name: '',
  code: '',
  address: '',
  phone: '',
  status: 'OPEN',
  latitude: 0,
  longitude: 0
})

const fetchStores = async () => {
  try {
    const res = await request.get('/api/admin/stores')
    if (res.code === 200) {
      stores.ref = res.data
      // 修正：Vue 3 setup 中 ref 的赋值应该是 .value
      stores.value = res.data
    }
  } catch (error) {
    console.error('获取门店列表失败:', error)
  }
}

const filteredStores = computed(() => {
  return stores.value.filter(store => {
    const matchesSearch = store.name.includes(searchQuery.value) || store.address.includes(searchQuery.value)
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
    address: '',
    phone: '',
    status: 'OPEN',
    latitude: 113.264434, // 默认经度
    latitude: 23.129162, // 默认纬度
    longitude: 113.264434,
    latitude: 23.129162
  }
  // 修正经纬度赋值
  form.value.longitude = 113.264434
  form.value.latitude = 23.129162
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
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.filter-section {
  display: flex;
  gap: 15px;
  margin-bottom: 20px;
}

.filter-section input {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  width: 250px;
}

.filter-section select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.store-table {
  background: white;
  border-radius: 8px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

table {
  width: 100%;
  border-collapse: collapse;
}

th, td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #eee;
}

th {
  background: #fafafa;
  font-weight: 600;
}

.status-tag {
  padding: 4px 8px;
  border-radius: 4px;
  font-size: 12px;
}

.status-tag.open { background: #e6f7ff; color: #1890ff; }
.status-tag.closed { background: #fff1f0; color: #f5222d; }
.status-tag.maintenance { background: #fff7e6; color: #fa8c16; }

.btn-primary {
  background: #1890ff;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 4px;
  cursor: pointer;
}

.btn-text {
  background: none;
  border: none;
  color: #1890ff;
  cursor: pointer;
  margin-right: 10px;
}

.btn-danger {
  color: #f5222d;
}

.dialog-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000;
}

.dialog {
  background: white;
  padding: 24px;
  border-radius: 8px;
  width: 500px;
}

.form-group {
  margin-bottom: 15px;
}

.form-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: 500;
}

.form-group input, .form-group select {
  width: 100%;
  padding: 8px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.form-row {
  display: flex;
  gap: 15px;
}

.form-row .form-group {
  flex: 1;
}

.dialog-footer {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  margin-top: 20px;
}
</style>