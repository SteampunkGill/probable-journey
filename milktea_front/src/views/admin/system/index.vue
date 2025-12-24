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
})
</script>

<style scoped>
.system-manage { display: flex; flex-direction: column; gap: 20px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }

.system-tabs { display: flex; gap: 32px; padding: 0 20px; height: 50px; align-items: center; }
.tab-item { cursor: pointer; color: #595959; position: relative; height: 100%; display: flex; align-items: center; }
.tab-item.active { color: #1890ff; font-weight: bold; }
.tab-item.active::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 2px; background: #1890ff; }

.section-header { margin-bottom: 20px; }
.admin-table { width: 100%; border-collapse: collapse; }
.admin-table th, .admin-table td { padding: 12px; text-align: left; border-bottom: 1px solid #f0f0f0; }

.store-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(300px, 1fr)); gap: 20px; }
.store-card { border: 1px solid #f0f0f0; padding: 16px; border-radius: 8px; }
.store-ops { margin-top: 16px; display: flex; justify-content: flex-end; gap: 12px; }

.config-group { margin-bottom: 32px; }
.config-group h4 { margin-bottom: 16px; border-left: 4px solid #1890ff; padding-left: 12px; }
.config-item { display: flex; align-items: center; gap: 12px; margin-bottom: 12px; }
.config-item label { width: 150px; }

.ops button { margin-right: 8px; color: #1890ff; background: none; border: none; cursor: pointer; }
.ops button:hover { text-decoration: underline; }

.admin-input { padding: 6px 12px; border: 1px solid #d9d9d9; border-radius: 4px; flex: 1; max-width: 400px; }
.btn-primary { background: #1890ff; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-success { background: #52c41a; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-warning { background: #faad14; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.text-success { color: #52c41a; }
.text-danger { color: #f5222d; }
</style>