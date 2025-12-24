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
              <span :class="'status-' + o.status.toLowerCase()">{{ getStatusName(o.status) }}</span>
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
        <h3>退款审核 - {{ refundModal.order.orderNo }}</h3>
        <div class="modal-body">
          <p>退款金额：<span class="text-danger">¥{{ refundModal.order.payAmount }}</span></p>
          <p>退款原因：{{ refundModal.order.refundReason || '无' }}</p>
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
import { get, post, put } from '../../../utils/request'

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
  if (activeTab.value === 'refund') query.value.status = 'REFUNDING'
  
  const res = await get(url, query.value)
  orders.value = res.data
}

const loadComplaints = async () => {
  const res = await get('/api/admin/complaints')
  complaints.value = res.data
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
  await post(`/api/admin/orders/${o.orderNo}/${action}`)
  loadOrders()
}

const batchAccept = async () => {
  await post('/api/admin/orders/batch-accept', selectedNos.value)
  loadOrders()
}

const printOrder = async (o) => {
  await post(`/api/admin/orders/${o.orderNo}/print`)
  alert('打印指令已发送')
}

const exportOrders = async () => {
  // 模拟导出
  alert('订单导出中...')
}

const testVoice = async () => {
  const res = await post('/api/admin/notifications/voice-test')
  alert(res.data)
}

const showRefundModal = (o) => {
  refundModal.value.order = o
  refundModal.value.show = true
}

const submitRefund = async (approved) => {
  await post(`/api/admin/refunds/${refundModal.value.order.id}/review`, {
    status: approved ? 'APPROVED' : 'REJECTED',
    reply: refundModal.value.reply
  })
  refundModal.value.show = false
  loadOrders()
}

const handleComplaint = async (c) => {
  const reply = prompt('请输入处理意见：')
  if (reply) {
    await post(`/api/admin/complaints/${c.id}/handle`, { status: 'RESOLVED', reply })
    loadComplaints()
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
.order-manage { display: flex; flex-direction: column; gap: 20px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.action-bar { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; gap: 12px; }
.batch-actions { display: flex; gap: 12px; }

.order-tabs { display: flex; gap: 32px; padding: 0 20px; height: 50px; align-items: center; }
.tab-item { cursor: pointer; color: #595959; position: relative; height: 100%; display: flex; align-items: center; }
.tab-item.active { color: #1890ff; font-weight: bold; }
.tab-item.active::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 2px; background: #1890ff; }

.admin-table { width: 100%; border-collapse: collapse; }
.admin-table th, .admin-table td { padding: 12px; text-align: left; border-bottom: 1px solid #f0f0f0; }

.status-paid { color: #faad14; }
.status-making { color: #1890ff; }
.status-ready { color: #52c41a; }
.status-refunding { color: #f5222d; }

.ops button { margin-right: 8px; color: #1890ff; background: none; border: none; cursor: pointer; }
.ops button:hover { text-decoration: underline; }

.modal-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-container { background: white; padding: 24px; border-radius: 8px; width: 500px; }
.modal-body { margin: 20px 0; }
.admin-textarea { width: 100%; height: 80px; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; margin-top: 8px; }
.modal-footer { display: flex; justify-content: flex-end; gap: 12px; }

.admin-input, .admin-select { padding: 6px 12px; border: 1px solid #d9d9d9; border-radius: 4px; }
.btn-primary { background: #1890ff; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-success { background: #52c41a; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-warning { background: #faad14; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-danger { background: #f5222d; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-info { background: #13c2c2; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.text-ellipsis { max-width: 300px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>