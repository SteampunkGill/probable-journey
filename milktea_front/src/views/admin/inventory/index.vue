<template>
  <div class="inventory-manage">
    <div class="page-header card">
      <h2>库存与成本管理</h2>
    </div>

    <div class="table-container card">
      <table class="admin-table">
        <thead>
          <tr>
            <th>原料名称</th>
            <th>单位</th>
            <th>当前库存</th>
            <th>预警阈值</th>
            <th>单位成本</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="item in inventory" :key="item.id">
            <td>{{ item.name }}</td>
            <td>{{ item.unit }}</td>
            <td :class="{ 'text-danger': item.isLowStock }">{{ item.stock }}</td>
            <td>{{ item.alertThreshold }}</td>
            <td>¥{{ item.costPerUnit || 0 }}</td>
            <td>
              <span v-if="item.isLowStock" class="badge-warn">库存不足</span>
              <span v-else class="badge-success">正常</span>
            </td>
            <td class="ops">
              <button @click="showUpdateModal(item, 'stock')">更新库存</button>
              <button @click="showUpdateModal(item, 'cost')">调整成本</button>
              <button @click="showUpdateModal(item, 'alert')">预警设置</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 更新弹窗 -->
    <div v-if="modal.show" class="modal-mask">
      <div class="modal-container">
        <h3>{{ modalTitle }} - {{ modal.item.name }}</h3>
        <div class="form-item">
          <label>{{ modalLabel }}：</label>
          <input v-model.number="modal.value" type="number" step="0.01" class="admin-input" />
        </div>
        <div v-if="modal.type === 'cost'" class="cost-tip">
          <i class="iconfont icon-info"></i>
          温馨提示：调整原料成本后，系统将自动重新计算所有关联奶茶产品的成本价。
        </div>
        <div class="modal-footer">
          <button @click="modal.show = false">取消</button>
          <button class="btn-primary" @click="handleUpdate">确认更新</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { get, put } from '../../../utils/request'

const inventory = ref([])
const modal = ref({
  show: false,
  type: '', // stock, cost, alert
  item: null,
  value: 0
})

const modalTitle = computed(() => {
  const maps = { stock: '更新库存', cost: '调整成本', alert: '预警设置' }
  return maps[modal.value.type] || ''
})

const modalLabel = computed(() => {
  const maps = { stock: '当前库存', cost: '单位成本', alert: '预警阈值' }
  return maps[modal.value.type] || ''
})

const loadInventory = async () => {
  const res = await get('/api/admin/inventory')
  inventory.value = res.data || []
}

const showUpdateModal = (item, type) => {
  modal.value.item = item
  modal.value.type = type
  if (type === 'stock') modal.value.value = item.stock
  else if (type === 'cost') modal.value.value = item.costPerUnit
  else if (type === 'alert') modal.value.value = item.alertThreshold
  modal.value.show = true
}

const handleUpdate = async () => {
  const { id } = modal.value.item
  const { type, value } = modal.value
  
  try {
    let res
    if (type === 'stock') {
      res = await put(`/api/admin/inventory/${id}/stock`, { stock: value })
    } else if (type === 'cost') {
      res = await put(`/api/admin/inventory/${id}/cost`, { costPerUnit: value })
    } else if (type === 'alert') {
      res = await put(`/api/admin/inventory/${id}/alert`, { alertThreshold: value })
    }
    
    if (res.code === 200) {
      alert('更新成功')
      modal.value.show = false
      loadInventory()
    }
  } catch (error) {
    console.error('更新失败:', error)
    alert('更新失败')
  }
}

onMounted(() => {
  loadInventory()
})
</script>

<style scoped>
.inventory-manage {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  background: var(--background-color);
  min-height: 100vh;
  padding: var(--spacing-xl);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  color: var(--text-color-dark);
}

.card {
  background: var(--surface-color);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 1px solid var(--border-color);
  backdrop-filter: blur(4px);
}

.page-header h2 {
  margin: 0;
  color: var(--primary-dark);
  font-family: 'Prompt', serif;
}

.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.admin-table th, .admin-table td {
  padding: var(--spacing-md);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.admin-table th {
  color: var(--primary-dark);
  font-weight: 600;
  background: rgba(255, 248, 220, 0.3);
}

.badge-warn {
  background: #fce8e6;
  color: #d93025;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85em;
  font-weight: 600;
}

.badge-success {
  background: #e6f4ea;
  color: #1e8e3e;
  padding: 4px 12px;
  border-radius: 20px;
  font-size: 0.85em;
  font-weight: 600;
}

.text-danger { color: #d93025; font-weight: 600; }

.ops { display: flex; gap: 10px; }
.ops button {
  padding: 6px 12px;
  border: none;
  border-radius: 8px;
  background: rgba(160, 82, 45, 0.1);
  color: var(--primary-color);
  cursor: pointer;
  font-weight: 600;
  transition: all 0.2s;
}

.ops button:hover {
  background: var(--primary-color);
  color: white;
}

.modal-mask {
  position: fixed;
  top: 0; left: 0; width: 100%; height: 100%;
  background: rgba(0,0,0,0.5);
  display: flex; align-items: center; justify-content: center;
  z-index: 1000;
}

.modal-container {
  background: white;
  padding: 30px;
  border-radius: 20px;
  width: 400px;
}

.form-item { margin-bottom: 20px; }
.form-item label { display: block; margin-bottom: 8px; font-weight: 600; }
.admin-input {
  width: 100%;
  padding: 12px;
  border: 2px solid var(--border-color);
  border-radius: 10px;
}

.cost-tip {
  background: #fff7e6;
  border: 1px solid #ffd591;
  padding: 12px;
  border-radius: 8px;
  color: #873800;
  font-size: 0.9em;
  margin-bottom: 20px;
  line-height: 1.5;
}

.modal-footer {
  display: flex; justify-content: flex-end; gap: 12px;
}

.modal-footer button {
  padding: 10px 20px;
  border-radius: 10px;
  border: none;
  cursor: pointer;
}

.btn-primary {
  background: var(--primary-color);
  color: white;
  font-weight: 600;
}

:root {
  --background-color: #f5f0e1;
  --surface-color: #ffffff;
  --primary-color: #a0522d;
  --primary-dark: #8b4513;
  --border-color: #e0e0e0;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
  --border-radius-lg: 20px;
}
</style>