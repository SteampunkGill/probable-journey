<template>
  <div class="member-manage">
    <div class="action-bar card">
      <div class="search-form">
        <input v-model="query.nickname" placeholder="昵称/手机号" class="admin-input" />
        <select v-model="query.levelId" class="admin-select">
          <option value="">全部等级</option>
          <option v-for="l in levels" :key="l.id" :value="l.id">{{ l.name }}</option>
        </select>
        <button class="btn-primary" @click="loadMembers">查询</button>
      </div>
    </div>

    <div class="member-tabs card">
      <div class="tab-item" :class="{ active: activeTab === 'list' }" @click="activeTab = 'list'">会员列表</div>
      <div class="tab-item" :class="{ active: activeTab === 'analysis' }" @click="activeTab = 'analysis'">会员分析</div>
      <div class="tab-item" :class="{ active: activeTab === 'tags' }" @click="activeTab = 'tags'">标签管理</div>
    </div>

    <div class="table-container card">
      <!-- 会员列表 -->
      <table v-if="activeTab === 'list'" class="admin-table">
        <thead>
          <tr>
            <th>头像</th>
            <th>昵称</th>
            <th>手机号</th>
            <th>等级</th>
            <th>积分</th>
            <th>余额</th>
            <th>注册时间</th>
            <th>操作</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="m in members" :key="m.id">
            <td><img :src="m.avatarUrl" class="table-img" /></td>
            <td>{{ m.nickname }}</td>
            <td>{{ m.phone }}</td>
            <td>{{ m.levelName }}</td>
            <td>{{ m.points }}</td>
            <td>¥{{ m.balance }}</td>
            <td>{{ formatDate(m.createdAt) }}</td>
            <td class="ops">
              <button @click="showLevelModal(m)">调整等级</button>
              <button @click="showPointsModal(m)">积分调整</button>
            </td>
          </tr>
        </tbody>
      </table>

      <!-- 会员分析 -->
      <div v-else-if="activeTab === 'analysis'" class="analysis-container">
        <div class="analysis-row">
          <div class="analysis-card">
            <h4>消费行为分析</h4>
            <div class="chart-placeholder">
              <div v-for="(val, key) in behaviorData" :key="key" class="chart-item">
                <span>{{ key }}:</span>
                <span>{{ val }}</span>
              </div>
            </div>
          </div>
          <div class="analysis-card">
            <h4>会员价值分层</h4>
            <div class="segment-list">
              <div v-for="s in segmentation" :key="s.name" class="segment-item">
                <span class="segment-name">{{ s.name }}</span>
                <span class="segment-count">{{ s.count }}人</span>
                <div class="segment-bar" :style="{ width: s.percentage + '%' }"></div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 标签管理 -->
      <div v-else class="tags-container">
        <div class="tag-actions">
          <button class="btn-success" @click="showTagModal()">新增标签</button>
        </div>
        <div class="tag-list">
          <div v-for="t in tags" :key="t.id" class="tag-item">
            <span>{{ t.name }}</span>
            <div class="tag-ops">
              <button @click="showTagModal(t)">编辑</button>
              <button class="text-danger" @click="deleteTag(t)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 等级调整弹窗 -->
    <div v-if="levelModal.show" class="modal-mask">
      <div class="modal-container">
        <h3>调整等级 - {{ levelModal.member.nickname }}</h3>
        <div class="form-item">
          <label>选择等级：</label>
          <select v-model="levelModal.levelId" class="admin-select">
            <option v-for="l in levels" :key="l.id" :value="l.id">{{ l.name }}</option>
          </select>
        </div>
        <div class="modal-footer">
          <button @click="levelModal.show = false">取消</button>
          <button class="btn-primary" @click="submitLevel">确定</button>
        </div>
      </div>
    </div>

    <!-- 积分调整弹窗 -->
    <div v-if="pointsModal.show" class="modal-mask">
      <div class="modal-container">
        <h3>积分调整 - {{ pointsModal.member.nickname }}</h3>
        <div class="form-item">
          <label>调整分值：</label>
          <input v-model.number="pointsModal.points" type="number" class="admin-input" placeholder="正数为加，负数为减" />
        </div>
        <div class="form-item">
          <label>备注：</label>
          <input v-model="pointsModal.remark" class="admin-input" />
        </div>
        <div class="modal-footer">
          <button @click="pointsModal.show = false">取消</button>
          <button class="btn-primary" @click="submitPoints">确定</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { get, post, put, del } from '../../../utils/request'

const members = ref([])
const levels = ref([])
const tags = ref([])
const behaviorData = ref({})
const segmentation = ref([])
const activeTab = ref('list')
const query = ref({ nickname: '', levelId: '' })

const levelModal = ref({ show: false, member: null, levelId: '' })
const pointsModal = ref({ show: false, member: null, points: 0, remark: '' })

const loadMembers = async () => {
  const res = await get('/api/admin/members', query.value)
  members.value = res.data.content
}

const loadLevels = async () => {
  const res = await get('/api/admin/settings') // 假设在系统设置里
  // 过滤出等级配置
  levels.value = [
    { id: 1, name: '普通会员' },
    { id: 2, name: '黄金会员' },
    { id: 3, name: '钻石会员' }
  ]
}

const loadTags = async () => {
  const res = await get('/api/admin/members/tags')
  tags.value = res.data
}

const loadAnalysis = async () => {
  const [behavior, segment] = await Promise.all([
    get('/api/admin/members/analysis/behavior'),
    get('/api/admin/members/segmentation')
  ])
  behaviorData.value = behavior.data
  segmentation.value = segment.data
}

watch(activeTab, (val) => {
  if (val === 'list') loadMembers()
  if (val === 'tags') loadTags()
  if (val === 'analysis') loadAnalysis()
})

const showLevelModal = (m) => {
  levelModal.value.member = m
  levelModal.value.levelId = m.memberLevelId
  levelModal.value.show = true
}

const submitLevel = async () => {
  await put(`/api/admin/members/${levelModal.value.member.id}/level`, { levelId: levelModal.value.levelId })
  levelModal.value.show = false
  loadMembers()
}

const showPointsModal = (m) => {
  pointsModal.value.member = m
  pointsModal.value.points = 0
  pointsModal.value.remark = ''
  pointsModal.value.show = true
}

const submitPoints = async () => {
  await post(`/api/admin/members/${pointsModal.value.member.id}/points`, {
    points: pointsModal.value.points,
    remark: pointsModal.value.remark
  })
  pointsModal.value.show = false
  loadMembers()
}

const deleteTag = async (t) => {
  if (confirm('确定删除该标签吗？')) {
    await del(`/api/admin/members/tags/${t.id}`)
    loadTags()
  }
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${date.getMonth() + 1}-${date.getDate()}`
}

onMounted(() => {
  loadMembers()
  loadLevels()
})
</script>

<style scoped>
.member-manage { display: flex; flex-direction: column; gap: 20px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }
.action-bar { display: flex; justify-content: space-between; align-items: center; }
.search-form { display: flex; gap: 12px; }

.member-tabs { display: flex; gap: 32px; padding: 0 20px; height: 50px; align-items: center; }
.tab-item { cursor: pointer; color: #595959; position: relative; height: 100%; display: flex; align-items: center; }
.tab-item.active { color: #1890ff; font-weight: bold; }
.tab-item.active::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 2px; background: #1890ff; }

.admin-table { width: 100%; border-collapse: collapse; }
.admin-table th, .admin-table td { padding: 12px; text-align: left; border-bottom: 1px solid #f0f0f0; }
.table-img { width: 40px; height: 40px; border-radius: 50%; object-fit: cover; }

.ops button { margin-right: 8px; color: #1890ff; background: none; border: none; cursor: pointer; }
.ops button:hover { text-decoration: underline; }

.analysis-row { display: grid; grid-template-columns: 1fr 1fr; gap: 24px; }
.analysis-card { border: 1px solid #f0f0f0; padding: 16px; border-radius: 8px; }
.chart-item { display: flex; justify-content: space-between; margin-bottom: 12px; }

.segment-item { margin-bottom: 16px; }
.segment-bar { height: 8px; background: #1890ff; border-radius: 4px; margin-top: 4px; }

.tag-list { display: flex; flex-wrap: wrap; gap: 12px; margin-top: 20px; }
.tag-item { background: #f5f5f5; padding: 8px 16px; border-radius: 4px; display: flex; align-items: center; gap: 12px; }
.tag-ops button { font-size: 12px; color: #1890ff; background: none; border: none; cursor: pointer; }

.modal-mask { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0,0,0,0.5); display: flex; align-items: center; justify-content: center; z-index: 1000; }
.modal-container { background: white; padding: 24px; border-radius: 8px; width: 400px; }
.form-item { margin-bottom: 16px; }
.form-item label { display: block; margin-bottom: 8px; }
.modal-footer { display: flex; justify-content: flex-end; gap: 12px; margin-top: 24px; }

.admin-input, .admin-select { width: 100%; padding: 8px; border: 1px solid #d9d9d9; border-radius: 4px; }
.btn-primary { background: #1890ff; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.btn-success { background: #52c41a; color: white; border: none; padding: 6px 16px; border-radius: 4px; cursor: pointer; }
.text-danger { color: #f5222d !important; }
</style>