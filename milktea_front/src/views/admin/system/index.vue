<template>
  <div class="system-manage">
    <div class="system-tabs card">
      <div class="tab-item" :class="{ active: activeTab === 'staff' }" @click="activeTab = 'staff'">员工管理</div>
      <div class="tab-item" :class="{ active: activeTab === 'store' }" @click="activeTab = 'store'">门店设置</div>
      <div class="tab-item" :class="{ active: activeTab === 'config' }" @click="activeTab = 'config'">系统设置</div>
      <div class="tab-item" :class="{ active: activeTab === 'log' }" @click="activeTab = 'log'">操作日志</div>
    </div>

    <div class="content-container card">
      <!-- 员工管理 -->
      <div v-if="activeTab === 'staff'" class="staff-section">
        <div class="section-header">
          <button class="btn-success" @click="showStaffModal()">新增员工</button>
        </div>
        <table class="admin-table">
          <thead>
            <tr>
              <th>用户名</th>
              <th>姓名</th>
              <th>角色</th>
              <th>所属门店</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="s in staffList" :key="s.id">
              <td>{{ s.username }}</td>
              <td>{{ s.realName }}</td>
              <td>{{ s.roleName }}</td>
              <td>{{ s.storeName || '总店' }}</td>
              <td>{{ s.status === 1 ? '启用' : '禁用' }}</td>
              <td class="ops">
                <button @click="showStaffModal(s)">编辑</button>
                <button @click="resetPwd(s)">重置密码</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 门店设置 -->
      <div v-else-if="activeTab === 'store'" class="store-section">
        <div class="section-header">
          <button class="btn-success" @click="showStoreModal()">新增门店</button>
        </div>
        <div class="store-grid">
          <div v-for="s in stores" :key="s.id" class="store-card">
            <div class="store-info">
              <h4>{{ s.name }}</h4>
              <p>{{ s.address }}</p>
              <p>营业时间：{{ s.businessHours }}</p>
              <p>状态：<span :class="s.businessStatus === 1 ? 'text-success' : 'text-danger'">{{ s.businessStatus === 1 ? '营业中' : '休息中' }}</span></p>
            </div>
            <div class="store-ops">
              <button @click="showStoreModal(s)">编辑</button>
              <button @click="toggleStoreStatus(s)">{{ s.businessStatus === 1 ? '打烊' : '开业' }}</button>
            </div>
          </div>
        </div>
      </div>

      <!-- 系统设置 -->
      <div v-else-if="activeTab === 'config'" class="config-section">
        <div class="config-group">
          <h4>基础参数配置</h4>
          <div v-for="c in configs" :key="c.configKey" class="config-item">
            <label>{{ c.description }}：</label>
            <input v-model="c.configValue" class="admin-input" />
            <button class="btn-primary" @click="saveConfig(c)">保存</button>
          </div>
        </div>
        <div class="config-group">
          <h4>数据备份与恢复</h4>
          <div class="backup-actions">
            <button class="btn-warning" @click="createBackup">立即备份</button>
          </div>
          <table class="admin-table">
            <thead>
              <tr>
                <th>备份文件</th>
                <th>大小</th>
                <th>时间</th>
                <th>状态</th>
                <th>操作</th>
              </tr>
            </thead>
            <tbody>
              <tr v-for="b in backups" :key="b.id">
                <td>{{ b.fileName }}</td>
                <td>{{ (b.fileSize / 1024 / 1024).toFixed(2) }} MB</td>
                <td>{{ formatDate(b.createdAt) }}</td>
                <td>{{ b.status }}</td>
                <td class="ops">
                  <button @click="downloadBackup(b)">下载</button>
                  <button @click="restoreBackup(b)">恢复</button>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 操作日志 -->
      <div v-else class="log-section">
        <table class="admin-table">
          <thead>
            <tr>
              <th>操作人</th>
              <th>模块</th>
              <th>动作</th>
              <th>IP</th>
              <th>时间</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="l in logs" :key="l.id">
              <td>{{ l.username }}</td>
              <td>{{ l.module }}</td>
              <td>{{ l.action }}</td>
              <td>{{ l.ip }}</td>
              <td>{{ formatDate(l.createdAt) }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>

    <!-- 员工编辑弹窗 -->
    <div v-if="staffModal.show" class="modal-mask">
      <div class="modal-container">
        <h3>{{ staffModal.isEdit ? '编辑员工' : '新增员工' }}</h3>
        <div class="form-item">
          <label>用户名：</label>
          <input v-model="staffModal.form.username" class="admin-input" :disabled="staffModal.isEdit" />
        </div>
        <div class="form-item">
          <label>姓名：</label>
          <input v-model="staffModal.form.realName" class="admin-input" />
        </div>
        <div class="form-item">
          <label>角色：</label>
          <select v-model="staffModal.form.roleId" class="admin-select">
            <option value="1">管理员</option>
            <option value="2">店长</option>
            <option value="3">店员</option>
          </select>
        </div>
        <div class="form-item">
          <label>所属门店：</label>
          <select v-model="staffModal.form.storeId" class="admin-select">
            <option :value="null">总店</option>
            <option v-for="s in stores" :key="s.id" :value="s.id">{{ s.name }}</option>
          </select>
        </div>
        <div class="modal-footer">
          <button @click="staffModal.show = false">取消</button>
          <button class="btn-primary" @click="saveStaff">保存</button>
        </div>
      </div>
    </div>
  </div>
    <!-- 门店编辑弹窗 -->
    <div v-if="storeModal.show" class="modal-mask">
      <div class="modal-container">
        <h3>{{ storeModal.isEdit ? '编辑门店' : '新增门店' }}</h3>
        <div class="form-item">
          <label>门店名称：</label>
          <input v-model="storeModal.form.name" class="admin-input" />
        </div>
        <div class="form-item">
          <label>门店地址：</label>
          <input v-model="storeModal.form.address" class="admin-input" />
        </div>
        <div class="form-item">
          <label>营业时间：</label>
          <input v-model="storeModal.form.businessHours" class="admin-input" placeholder="例如：09:00-22:00" />
        </div>
        <div class="modal-footer">
          <button @click="storeModal.show = false">取消</button>
          <button class="btn-primary" @click="saveStore">保存</button>
        </div>
      </div>
    </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { get, post, put } from '../../../utils/request'

const activeTab = ref('staff')
const staffList = ref([])
const stores = ref([])
const configs = ref([])
const backups = ref([])
const logs = ref([])

const staffModal = ref({ show: false, isEdit: false, form: {} })
const storeModal = ref({ show: false, isEdit: false, form: {} })

const showStoreModal = (s = null) => {
  if (s) {
    storeModal.value.isEdit = true
    storeModal.value.form = { ...s }
  } else {
    storeModal.value.isEdit = false
    storeModal.value.form = { name: '', address: '', businessHours: '', businessStatus: 1 }
  }
  storeModal.value.show = true
}

const saveStore = async () => {
  try {
    if (storeModal.value.isEdit) {
      await put(`/api/admin/stores/${storeModal.value.form.id}`, storeModal.value.form)
    } else {
      await post('/api/admin/stores', storeModal.value.form)
    }
    storeModal.value.show = false
    loadStores()
    alert('保存成功')
  } catch (error) {
    console.error('保存门店失败:', error)
    alert('保存失败')
  }
}


const showStaffModal = (s = null) => {
  if (s) {
    staffModal.value.isEdit = true
    staffModal.value.form = { ...s }
  } else {
    staffModal.value.isEdit = false
    staffModal.value.form = { username: '', realName: '', roleId: '', storeId: '' }
  }
  staffModal.value.show = true
}

const saveStaff = async () => {
  if (staffModal.value.isEdit) {
    await put(`/api/admin/staff/${staffModal.value.form.id}`, staffModal.value.form)
  } else {
    await post('/api/admin/staff', staffModal.value.form)
  }
  staffModal.value.show = false
  loadStaff()
}

const resetPwd = async (s) => {
  if (confirm(`确定要重置员工 ${s.username} 的密码吗？`)) {
    await post(`/api/admin/staff/${s.id}/reset-password`)
    alert('密码已重置为默认密码')
  }
}

const loadStaff = async () => {
  const res = await get('/api/admin/staff')
  staffList.value = res.data
}

const loadStores = async () => {
  const res = await get('/api/admin/stores')
  stores.value = res.data
}

const loadConfigs = async () => {
  const res = await get('/api/admin/settings')
  configs.value = res.data
}

const loadBackups = async () => {
  const res = await get('/api/admin/backups')
  backups.value = res.data
}

const loadLogs = async () => {
  const res = await get('/api/admin/logs')
  logs.value = res.data
}

watch(activeTab, (val) => {
  if (val === 'staff') loadStaff()
  if (val === 'store') loadStores()
  if (val === 'config') { loadConfigs(); loadBackups(); }
  if (val === 'log') loadLogs()
})

const saveConfig = async (c) => {
  await put('/api/admin/settings', c)
  alert('保存成功')
}

const createBackup = async () => {
  await post('/api/admin/backups')
  alert('备份任务已启动')
  loadBackups()
}

const downloadBackup = (b) => {
  const token = localStorage.getItem('token')
  const url = `/api/admin/backups/${b.id}/download${token ? '?token=' + token : ''}`
  window.open(url, '_blank')
}

const restoreBackup = async (b) => {
  if (confirm(`确定要恢复备份 ${b.fileName} 吗？当前数据将被覆盖！`)) {
    await post(`/api/admin/backups/${b.id}/restore`)
    alert('恢复成功')
  }
}

const toggleStoreStatus = async (s) => {
  await put(`/api/admin/stores/${s.id}/business-status`, { status: s.businessStatus === 1 ? 'CLOSED' : 'OPEN' })
  loadStores()
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}-${date.getDate()} ${date.getHours()}:${date.getMinutes()}`
}

onMounted(() => {
  loadStaff()
  loadStores()
})
</script>

<style scoped>
.system-manage {
  display: flex;
  flex-direction: column;
  gap: var(--spacing-xl);
  background: var(--background-color);
  min-height: 100vh;
  padding: var(--spacing-xl);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  color: var(--text-color-dark);
}

/* 卡片样式 */
.card {
  background: var(--surface-color);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 1px solid var(--border-color);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease-out;
}

.card:hover {
  box-shadow: 0 12px 35px rgba(160, 82, 45, 0.12);
  transform: translateY(-2px);
}

/* 系统标签页 */
.system-tabs {
  display: flex;
  gap: var(--spacing-lg);
  padding: 0 var(--spacing-lg);
  height: 60px;
  align-items: center;
  border-bottom: 2px solid var(--border-color);
  background: linear-gradient(135deg, var(--surface-color) 0%, rgba(232, 220, 203, 0.9) 100%);
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

.tab-item {
  cursor: pointer;
  color: var(--text-color-medium);
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  font-size: 1.1em;
  font-weight: 500;
  padding: 0 var(--spacing-sm);
  transition: all 0.3s ease-out;
  border-radius: var(--border-radius-md);
}

.tab-item:hover {
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.05);
}

.tab-item.active {
  color: var(--primary-color);
  font-weight: 600;
  background: rgba(160, 82, 45, 0.1);
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 50%;
  transform: translateX(-50%);
  width: 80%;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-brown));
  border-radius: 3px 3px 0 0;
}

/* 内容容器 */
.content-container {
  overflow: hidden;
  border-radius: 0 var(--border-radius-lg) var(--border-radius-lg) var(--border-radius-lg);
}

/* 员工管理 */
.staff-section,
.store-section,
.config-section,
.log-section {
  animation: fadeIn 0.4s ease-out;
}

.section-header {
  margin-bottom: var(--spacing-lg);
  display: flex;
  justify-content: flex-end;
}

/* 表格样式 */
.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-family: 'Nunito', sans-serif;
}

.admin-table th,
.admin-table td {
  padding: var(--spacing-md);
  text-align: left;
  border-bottom: 1px solid var(--border-color);
  transition: all 0.25s ease-out;
}

.admin-table th {
  font-family: 'Prompt', 'Noto Serif KR', serif;
  color: var(--primary-dark);
  font-weight: 600;
  background: rgba(255, 248, 220, 0.3);
  border-bottom: 2px solid var(--accent-brown);
  position: sticky;
  top: 0;
  z-index: 10;
}

.admin-table tbody tr {
  transition: all 0.25s ease-out;
}

.admin-table tbody tr:hover {
  background: rgba(255, 248, 220, 0.2);
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.05);
}

/* 门店网格 */
.store-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(320px, 1fr));
  gap: var(--spacing-lg);
}

.store-card {
  background: rgba(255, 255, 255, 0.7);
  border: 2px solid var(--border-color);
  padding: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  transition: all 0.3s ease-out;
  position: relative;
  overflow: hidden;
}

.store-card:hover {
  border-color: var(--primary-color);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.1);
}

.store-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, var(--primary-color), var(--accent-brown));
  opacity: 0;
  transition: opacity 0.3s ease-out;
}

.store-card:hover::before {
  opacity: 1;
}

.store-info h4 {
  font-family: 'Prompt', serif;
  font-size: 1.2em;
  font-weight: 600;
  color: var(--primary-dark);
  margin: 0 0 var(--spacing-sm) 0;
}

.store-info p {
  color: var(--text-color-medium);
  margin: 6px 0;
  font-size: 0.95em;
}

.store-ops {
  margin-top: var(--spacing-md);
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-sm);
}

/* 系统配置 */
.config-group {
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: rgba(255, 255, 255, 0.5);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color);
}

.config-group h4 {
  font-family: 'Prompt', serif;
  font-size: 1.2em;
  font-weight: 600;
  color: var(--primary-dark);
  margin: 0 0 var(--spacing-lg) 0;
  padding-left: var(--spacing-md);
  border-left: 4px solid var(--accent-brown);
}

.config-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-md);
  padding: var(--spacing-sm);
  background: rgba(255, 255, 255, 0.3);
  border-radius: var(--border-radius-md);
  transition: all 0.25s ease-out;
}

.config-item:hover {
  background: rgba(255, 248, 220, 0.3);
  transform: translateX(4px);
}

.config-item label {
  width: 180px;
  font-weight: 500;
  color: var(--text-color-dark);
  font-family: 'Nunito', sans-serif;
}

.backup-actions {
  margin-bottom: var(--spacing-lg);
  padding: var(--spacing-md);
  background: rgba(255, 248, 220, 0.3);
  border-radius: var(--border-radius-lg);
  border: 1px dashed var(--accent-brown);
}

/* 操作按钮 */
.ops {
  display: flex;
  gap: var(--spacing-sm);
  flex-wrap: wrap;
}

.ops button {
  padding: 6px 16px;
  border: none;
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  font-size: 0.9em;
  transition: all 0.25s ease-out;
  background: rgba(160, 82, 45, 0.1);
  color: var(--primary-color);
  position: relative;
  overflow: hidden;
}

.ops button:hover {
  background: var(--primary-color);
  color: white;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.ops button:active {
  transform: translateY(0) scale(0.98);
}

/* 表单元素 */
.admin-input,
.admin-select {
  padding: 12px 20px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-lg);
  background: rgba(255, 255, 255, 0.8);
  font-family: 'Nunito', sans-serif;
  color: var(--text-color-dark);
  transition: all 0.25s ease-out;
  font-size: 1em;
  flex: 1;
  max-width: 400px;
}

.admin-input:focus,
.admin-select:focus {
  outline: none;
  border-color: var(--primary-color);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.2);
  transform: translateY(-1px);
}

/* 模态框 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(74, 59, 48, 0.25);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(4px);
  animation: fadeIn 0.3s ease-out;
}

.modal-container {
  background: var(--surface-color);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  width: 400px;
  max-width: 90vw;
  box-shadow: 0 20px 60px rgba(160, 82, 45, 0.15);
  border: 2px solid var(--accent-cream);
  animation: slideUp 0.4s cubic-bezier(0.175, 0.885, 0.32, 1.275);
}

.modal-container h3 {
  font-family: 'Prompt', serif;
  font-size: 1.4em;
  font-weight: 600;
  color: var(--primary-dark);
  margin: 0 0 var(--spacing-lg) 0;
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--border-color);
}

.form-item {
  margin-bottom: var(--spacing-lg);
}

.form-item label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
  font-family: 'Prompt', serif;
  color: var(--primary-dark);
  font-size: 1em;
}

/* 模态框底部 */
.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  margin-top: var(--spacing-lg);
  padding-top: var(--spacing-lg);
  border-top: 1px solid var(--border-color);
}

.modal-footer button {
  padding: 10px 24px;
  border-radius: var(--border-radius-lg);
  border: none;
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  transition: all 0.25s ease-out;
}

.modal-footer button:first-child {
  background: var(--border-color);
  color: var(--text-color-medium);
}

.modal-footer button:first-child:hover {
  background: var(--text-color-medium);
  color: white;
  transform: translateY(-1px);
}

/* 按钮样式 */
.btn-primary,
.btn-success,
.btn-warning {
  padding: 12px 28px;
  border: none;
  border-radius: var(--border-radius-xl);
  cursor: pointer;
  font-family: 'Prompt', serif;
  font-weight: 600;
  font-size: 1em;
  transition: all 0.25s ease-out;
  position: relative;
  overflow: hidden;
  letter-spacing: 0.5px;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.btn-success {
  background: linear-gradient(135deg, #2c9678, #5f9e5f);
  color: white;
  box-shadow: 0 6px 20px rgba(44, 150, 120, 0.3);
}

.btn-warning {
  background: linear-gradient(135deg, var(--accent-brown), #b8860b);
  color: white;
  box-shadow: 0 6px 20px rgba(222, 184, 135, 0.3);
}

/* 按钮悬停效果 */
.btn-primary:hover,
.btn-success:hover,
.btn-warning:hover {
  transform: translateY(-3px) scale(1.03);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.4);
}

.btn-primary:active,
.btn-success:active,
.btn-warning:active {
  transform: translateY(0) scale(0.98);
}

/* 文本颜色 */
.text-success {
  color: #2c9678;
  font-weight: 600;
}

.text-danger {
  color: #c13c3c;
  font-weight: 600;
}

/* 动画 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .system-manage {
    padding: var(--spacing-md);
    gap: var(--spacing-lg);
  }

  .system-tabs {
    flex-wrap: wrap;
    height: auto;
    padding: var(--spacing-md);
    gap: var(--spacing-sm);
  }

  .tab-item {
    flex: 1;
    min-width: 120px;
    justify-content: center;
    padding: var(--spacing-sm);
  }

  .store-grid {
    grid-template-columns: 1fr;
  }

  .config-item {
    flex-direction: column;
    align-items: stretch;
  }

  .config-item label {
    width: 100%;
  }

  .admin-input,
  .admin-select {
    max-width: 100%;
  }

  .modal-container {
    padding: var(--spacing-md);
    width: 95vw;
  }

  .modal-footer {
    flex-direction: column;
  }

  .modal-footer button {
    width: 100%;
  }
}

/* 定义CSS变量 */
:root {
  /* 色彩方案 */
  --background-color: #f5f0e1;
  --surface-color: #e8dccb;
  --primary-color: #a0522d;
  --primary-dark: #8b4513;
  --primary-light: #d2b48c;
  --accent-cream: #fff8dc;
  --accent-pink: #ffc0cb;
  --accent-brown: #deb887;
  --text-color-dark: #4a3b30;
  --text-color-medium: #7a6a5b;
  --text-color-light: #a09080;
  --border-color: #d4c7b5;

  /* 圆角大小 */
  --border-radius-sm: 8px;
  --border-radius-md: 12px;
  --border-radius-lg: 20px;
  --border-radius-xl: 30px;

  /* 间距 */
  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
}
</style>