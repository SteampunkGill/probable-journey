<template>
  <div class="order-manage">
    <div class="action-bar card">
      <div class="search-form">
        <input v-model="query.orderNo" placeholder="订单号" class="admin-input" />
        <select v-model="query.status" class="admin-select">
          <option value="">全部状态</option>
          <option value="PAID">待接单</option>
          <option value="MAKING">制作中</option>
          <option value="READY">待取餐</option>
          <option value="DELIVERED">已完成</option>
          <option value="REFUNDING">退款中</option>
        </select>
        <button class="btn-primary" @click="loadOrders">查询</button>
        <button class="btn-success" @click="exportOrders">导出订单</button>
      </div>
      <div class="batch-actions">
        <button class="btn-warning" :disabled="!selectedNos.length" @click="batchAccept">批量接单</button>
        <button class="btn-info" @click="testVoice">语音提醒测试</button>
      </div>
    </div>

    <div class="order-tabs card">
      <div class="tab-item" :class="{ active: activeTab === 'pending' }" @click="activeTab = 'pending'">待处理订单</div>
      <div class="tab-item" :class="{ active: activeTab === 'all' }" @click="activeTab = 'all'">全部订单</div>
      <div class="tab-item" :class="{ active: activeTab === 'refund' }" @click="activeTab = 'refund'">售后/退款</div>
      <div class="tab-item" :class="{ active: activeTab === 'complaint' }" @click="activeTab = 'complaint'">投诉建议</div>
    </div>

    <div class="table-container card">
      <!-- 待处理/全部订单表格 -->
      <table v-if="activeTab !== 'complaint'" class="admin-table">
        <thead>
          <tr>
            <th><input type="checkbox" @change="toggleAll" :checked="isAllSelected" /></th>
            <th>订单号</th>
            <th>下单时间</th>
            <th>金额</th>
            <th>类型</th>
            <th>状态</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="o in orders" :key="o.orderNo">
            <td><input type="checkbox" v-model="selectedNos" :value="o.orderNo" /></td>
            <td>{{ o.orderNo }}</td>
            <td>{{ formatDate(o.orderTime || o.createdAt) }}</td>
            <td>¥{{ o.payAmount }}</td>
            <td>{{ o.deliveryType === 'PICKUP' ? '自提' : '外送' }}</td>
            <td>
              <span :class="'status-' + (o.status ? o.status.toLowerCase() : '')">{{ getStatusName(o.status) }}</span>
            </td>
            <td class="ops">
              <button v-if="o.status === 'PAID'" @click="handleOrder(o, 'accept')">接单</button>
              <button v-if="o.status === 'MAKING'" @click="handleOrder(o, 'ready')">制作完成</button>
              <button @click="printOrder(o)">打印小票</button>
              <button v-if="o.status === 'REFUNDING'" @click="showRefundModal(o)">审核退款</button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 投诉建议表格 -->
      <table v-else class="admin-table">
        <thead>
          <tr>
            <th>用户ID</th>
            <th>类型</th>
            <th>内容</th>
            <th>状态</th>
            <th>时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="c in complaints" :key="c.id">
            <td>{{ c.userId }}</td>
            <td>{{ c.type }}</td>
            <td class="text-ellipsis">{{ c.content }}</td>
            <td>{{ c.status }}</td>
            <td>{{ formatDate(c.createdAt) }}</td>
            <td class="ops">
              <button @click="handleComplaint(c)">处理</button>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <!-- 退款审核弹窗 -->
    <div v-if="refundModal.show" class="modal-mask">
      <div class="modal-container">
        <h3>退款审核 - {{ refundModal.order?.orderNo }}</h3>
        <div class="modal-body">
          <p>退款金额：<span class="text-danger">¥{{ refundModal.order?.payAmount }}</span></p>
          <p>退款原因：{{ refundModal.order?.refundReason || '无' }}</p>
          <div class="form-item">
            <label>审核意见：</label>
            <textarea v-model="refundModal.reply" class="admin-textarea"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="refundModal.show = false">取消</button>
          <button class="btn-danger" @click="submitRefund(false)">拒绝</button>
          <button class="btn-success" @click="submitRefund(true)">通过</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, watch } from 'vue'
import request from '../../../utils/request'

const orders = ref([])
const complaints = ref([])
const selectedNos = ref([])
const activeTab = ref('pending')
const query = ref({ orderNo: '', status: '', storeId: '' })

const refundModal = ref({
  show: false,
  order: null,
  reply: ''
})

const isAllSelected = computed(() => orders.value.length > 0 && selectedNos.value.length === orders.value.length)

const loadOrders = async () => {
  let url = '/api/admin/orders'
  if (activeTab.value === 'pending') url = '/api/admin/orders/pending'
  
  const params = { ...query.value }
  if (activeTab.value === 'refund') params.status = 'REFUNDING'
  
  try {
    const res = await request.get(url, { params })
    orders.value = res.data || []
  } catch (error) {
    console.error('加载订单失败:', error)
  }
}

const loadComplaints = async () => {
  try {
    const res = await request.get('/api/admin/complaints')
    complaints.value = res.data || []
  } catch (error) {
    console.error('加载投诉失败:', error)
  }
}

watch(activeTab, (val) => {
  if (val === 'complaint') {
    loadComplaints()
  } else {
    loadOrders()
  }
})

const toggleAll = (e) => {
  selectedNos.value = e.target.checked ? orders.value.map(o => o.orderNo) : []
}

const handleOrder = async (o, action) => {
  try {
    await request.post(`/api/admin/orders/${o.orderNo}/${action}`)
    loadOrders()
  } catch (error) {
    console.error('处理订单失败:', error)
  }
}

const batchAccept = async () => {
  try {
    await request.post('/api/admin/orders/batch-accept', selectedNos.value)
    loadOrders()
  } catch (error) {
    console.error('批量接单失败:', error)
  }
}

const printOrder = async (o) => {
  try {
    await request.post(`/api/admin/orders/${o.orderNo}/print`)
    alert('打印指令已发送')
  } catch (error) {
    console.error('打印失败:', error)
  }
}

const exportOrders = async () => {
  alert('订单导出中...')
}

const testVoice = async () => {
  try {
    const res = await request.post('/api/admin/notifications/voice-test')
    alert(res.data)
  } catch (error) {
    console.error('语音测试失败:', error)
  }
}

const showRefundModal = (o) => {
  refundModal.value.order = o
  refundModal.value.show = true
}

const submitRefund = async (approved) => {
  try {
    await request.post(`/api/admin/refunds/${refundModal.value.order.id}/review`, {
      status: approved ? 'APPROVED' : 'REJECTED',
      reply: refundModal.value.reply
    })
    refundModal.value.show = false
    loadOrders()
  } catch (error) {
    console.error('审核退款失败:', error)
  }
}

const handleComplaint = async (c) => {
  const reply = prompt('请输入处理意见：')
  if (reply) {
    try {
      await request.post(`/api/admin/complaints/${c.id}/handle`, { status: 'RESOLVED', reply })
      loadComplaints()
    } catch (error) {
      console.error('处理投诉失败:', error)
    }
  }
}

const getStatusName = (status) => {
  const map = {
    'PAID': '待接单',
    'MAKING': '制作中',
    'READY': '待取餐',
    'DELIVERED': '已完成',
    'REFUNDING': '退款中',
    'REFUNDED': '已退款'
  }
  return map[status] || status
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-manage {
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
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.08);
  border: 1px solid var(--border-color);
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.search-form {
  display: flex;
  gap: var(--spacing-md);
  align-items: center;
  flex-wrap: wrap;
}

.batch-actions {
  display: flex;
  gap: var(--spacing-md);
}

.order-tabs {
  display: flex;
  gap: var(--spacing-lg);
  padding: 0 var(--spacing-lg);
  height: 60px;
  align-items: center;
  border-bottom: 2px solid var(--border-color);
}

.tab-item {
  cursor: pointer;
  color: var(--text-color-medium);
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  font-size: 1.1em;
  font-weight: 500;
  padding: 0 var(--spacing-sm);
}

.tab-item.active {
  color: var(--primary-color);
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 100%;
  height: 3px;
  background: var(--primary-color);
}

.table-container {
  overflow: hidden;
}

.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
}

.admin-table th,
.admin-table td {
  padding: var(--spacing-md);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
}

.admin-table th {
  color: var(--primary-dark);
  font-weight: 600;
  background: rgba(255, 248, 220, 0.3);
}

.ops {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.ops button {
  padding: 6px 12px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  background: #f0f0f0;
}

.ops button:hover {
  background: #e0e0e0;
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
  border-radius: 12px;
  width: 500px;
}

.admin-input, .admin-select {
  padding: 8px 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.btn-primary { background: var(--primary-color); color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-success { background: #2c9678; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-warning { background: #f39c12; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-danger { background: #e74c3c; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }
.btn-info { background: #3498db; color: white; border: none; padding: 10px 20px; border-radius: 4px; cursor: pointer; }

:root {
  --background-color: #f5f0e1;
  --surface-color: #ffffff;
  --primary-color: #a0522d;
  --primary-dark: #8b4513;
  --border-color: #e0e0e0;
  --spacing-lg: 16px;
  --spacing-xl: 24px;
  --border-radius-lg: 12px;
}
</style>