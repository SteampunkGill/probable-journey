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
            <td><img :src="formatImageUrl(m.avatarUrl || m.avatar)" class="table-img" /></td>
            <td>{{ m.nickname || m.username }}</td>
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
            <h4>消费行为分析 (RFM)</h4>
            <div class="chart-placeholder">
              <div class="chart-item">
                <span>平均订单金额 (Monetary):</span>
                <span>¥{{ behaviorData.avgOrderValue }}</span>
              </div>
              <div class="chart-item">
                <span>下单频率 (Frequency):</span>
                <span>{{ behaviorData.orderFrequency }}</span>
              </div>
              <div class="chart-item">
                <span>最爱分类:</span>
                <span>{{ behaviorData.favoriteCategory }}</span>
              </div>
              <div class="chart-item">
                <span>留存率:</span>
                <span>{{ (retentionData.retentionRate * 100).toFixed(1) }}%</span>
              </div>
            </div>
          </div>
          <div class="analysis-card">
            <h4>会员价值分层 (RFM模型)</h4>
            <div class="segment-list">
              <div v-for="s in segmentation" :key="s.name" class="segment-item">
                <div class="segment-info">
                  <span class="segment-name">{{ s.name }}</span>
                  <span class="segment-count">{{ s.count }}人 ({{ s.percentage.toFixed(1) }}%)</span>
                </div>
                <div class="segment-bar-bg">
                  <div class="segment-bar" :style="{ width: s.percentage + '%' }"></div>
                </div>
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
import { formatImageUrl } from '../../../utils/util'
import defaultAvatar from '../../../assets/images/icons/user.png'

const members = ref([])
const levels = ref([])
const tags = ref([])
const behaviorData = ref({})
const segmentation = ref([])
const retentionData = ref({ retentionRate: 0, churnRate: 0 })
const activeTab = ref('list')
const query = ref({ nickname: '', levelId: '' })

const levelModal = ref({ show: false, member: null, levelId: '' })
const pointsModal = ref({ show: false, member: null, points: 0, remark: '' })

const loadMembers = async () => {
  try {
    const res = await get('/api/admin/members', query.value)
    let list = []
    if (res.code === 200) {
      list = res.data?.content || res.data?.list || res.data || []
    } else {
      list = res.content || res.list || res || []
    }
    
    // 查询过滤逻辑
    if (query.value.nickname) {
      const k = query.value.nickname.toLowerCase()
      list = list.filter(m =>
        (m.nickname && m.nickname.toLowerCase().includes(k)) ||
        (m.phone && m.phone.includes(k)) ||
        (m.username && m.username.toLowerCase().includes(k))
      )
    }

    if (query.value.levelId) {
      list = list.filter(m => m.memberLevelId == query.value.levelId || m.levelId == query.value.levelId)
    }

    members.value = list
  } catch (e) {
    console.error('加载会员列表失败:', e)
  }
}

const loadLevels = async () => {
  try {
    const res = await get('/api/admin/member-levels')
    if (res.code === 200) {
      levels.value = res.data
    }
  } catch (e) {
    console.error('加载等级列表失败:', e)
  }
}

const loadTags = async () => {
  const res = await get('/api/admin/members/tags')
  tags.value = res.data
}

const loadAnalysis = async () => {
  const [behavior, segment, retention] = await Promise.all([
    get('/api/admin/members/analysis/behavior'),
    get('/api/admin/members/segmentation'),
    get('/api/admin/members/analysis/retention')
  ])
  behaviorData.value = behavior.data
  segmentation.value = segment.data
  retentionData.value = retention.data
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
.member-manage {
  display: flex;
  flex-direction: column;
  gap: 28px;
  padding: 24px;
  background: linear-gradient(135deg, #f5f0e1 0%, #f8f4e6 100%);
  min-height: 100vh;
}

.card {
  background: linear-gradient(135deg, #fff8dc 0%, #f5f0e1 100%);
  border-radius: 30px;
  padding: 28px;
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.08);
  border: 3px solid #e8dccb;
  backdrop-filter: blur(6px);
  transition: all 0.3s ease-out;
}

.action-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  border-radius: 30px;
  padding: 20px 28px;
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.9) 0%, rgba(245, 240, 225, 0.8) 100%);
}

.search-form {
  display: flex;
  gap: 20px;
  align-items: center;
  flex: 1;
}

.admin-input, .admin-select {
  padding: 14px 20px;
  border: 3px solid #e8dccb;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.9);
  color: #4a3b30;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-size: 15px;
  font-weight: 500;
  transition: all 0.3s ease-out;
  outline: none;
  min-width: 180px;
}

.admin-input:focus, .admin-select:focus {
  border-color: #a0522d;
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.2);
  transform: translateY(-2px);
}

.admin-input::placeholder {
  color: #a09080;
  opacity: 0.7;
}

.btn-primary {
  background: linear-gradient(135deg, #a0522d 0%, #8b4513 100%);
  color: #fff8dc;
  border: none;
  padding: 14px 28px;
  border-radius: 25px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.02em;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
  transition: all 0.3s ease-out;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-primary::before {
  content: '🔍';
  font-size: 18px;
}

.btn-primary:hover {
  background: linear-gradient(135deg, #8b4513 0%, #a0522d 100%);
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 10px 25px rgba(160, 82, 45, 0.4);
}

.btn-primary:active {
  transform: translateY(0) scale(0.98);
}

.member-tabs {
  display: flex;
  gap: 32px;
  padding: 0 28px;
  height: 70px;
  align-items: center;
  border-bottom: 3px dashed #d4c7b5;
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.9) 0%, rgba(245, 240, 225, 0.8) 100%);
  border-radius: 30px 30px 0 0;
}

.tab-item {
  cursor: pointer;
  color: #7a6a5b;
  position: relative;
  height: 100%;
  display: flex;
  align-items: center;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.02em;
  padding: 0 16px;
  transition: all 0.3s ease-out;
  border-radius: 20px;
}

.tab-item:hover {
  color: #a0522d;
  background: rgba(255, 192, 203, 0.1);
  transform: translateY(-2px);
}

.tab-item.active {
  color: #8b4513;
  font-weight: 700;
  opacity: 1;
  background: linear-gradient(135deg, rgba(255, 192, 203, 0.2) 0%, rgba(210, 180, 140, 0.1) 100%);
}

.tab-item.active::after {
  content: '👑';
  position: absolute;
  bottom: -8px;
  left: 50%;
  transform: translateX(-50%);
  font-size: 18px;
}

.table-container {
  min-height: 600px;
  border-radius: 30px;
  overflow: hidden;
}

.admin-table {
  width: 100%;
  border-collapse: separate;
  border-spacing: 0;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  border-radius: 25px;
  overflow: hidden;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.1);
}

.admin-table th {
  background: linear-gradient(135deg, #a0522d 0%, #8b4513 100%);
  color: #fff8dc;
  font-weight: 700;
  font-size: 16px;
  padding: 20px;
  text-align: left;
  letter-spacing: 0.02em;
  border-bottom: 3px solid #d2b48c;
}

.admin-table td {
  padding: 20px;
  color: #4a3b30;
  font-size: 15px;
  font-weight: 500;
  background: rgba(255, 255, 255, 0.7);
  border-bottom: 2px dashed #e8dccb;
  transition: all 0.3s ease-out;
}

.admin-table tr:hover td {
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.9) 0%, rgba(245, 240, 225, 0.7) 100%);
  transform: translateX(8px);
}

.admin-table tr:last-child td {
  border-bottom: none;
}

.table-img {
  width: 56px;
  height: 56px;
  border-radius: 50%;
  object-fit: cover;
  border: 3px solid #d2b48c;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.2);
  transition: all 0.3s ease-out;
}

.table-img:hover {
  transform: scale(1.1);
  border-color: #ffc0cb;
  box-shadow: 0 6px 20px rgba(255, 192, 203, 0.4);
}

.ops {
  display: flex;
  gap: 12px;
}

.ops button {
  background: linear-gradient(135deg, rgba(160, 82, 45, 0.1) 0%, rgba(139, 69, 19, 0.05) 100%);
  color: #8b4513;
  border: 2px solid rgba(160, 82, 45, 0.3);
  padding: 8px 20px;
  border-radius: 20px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 14px;
  letter-spacing: 0.02em;
  transition: all 0.3s ease-out;
}

.ops button:hover {
  background: linear-gradient(135deg, #ffc0cb 0%, #a0522d 100%);
  color: #fff8dc;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 15px rgba(160, 82, 45, 0.3);
  border-color: #a0522d;
}

.analysis-container {
  padding: 20px;
}

.analysis-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 28px;
}

.analysis-card {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.9) 0%, rgba(248, 244, 230, 0.8) 100%);
  border-radius: 25px;
  padding: 28px;
  border: 3px solid #e8dccb;
  transition: all 0.3s ease-out;
}

.analysis-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 10px 30px rgba(160, 82, 45, 0.15);
  border-color: #d2b48c;
}

.analysis-card h4 {
  font-size: 18px;
  font-weight: 700;
  color: #8b4513;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 3px dashed #d4c7b5;
}

.chart-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding: 16px;
  background: rgba(255, 248, 220, 0.5);
  border-radius: 20px;
  border: 2px solid #e8dccb;
  transition: all 0.3s ease-out;
}

.chart-item:hover {
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.8) 0%, rgba(245, 240, 225, 0.6) 100%);
  transform: translateX(8px);
  border-color: #d2b48c;
}

.chart-item span:first-child {
  color: #7a6a5b;
  font-weight: 600;
}

.chart-item span:last-child {
  color: #8b4513;
  font-weight: 700;
  background: rgba(139, 69, 19, 0.1);
  padding: 6px 16px;
  border-radius: 20px;
  border: 2px solid rgba(139, 69, 19, 0.3);
}

.segment-list {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.segment-item {
  background: rgba(255, 248, 220, 0.5);
  border-radius: 20px;
  padding: 20px;
  border: 2px solid #e8dccb;
  transition: all 0.3s ease-out;
}

.segment-item:hover {
  background: linear-gradient(135deg, rgba(255, 248, 220, 0.8) 0%, rgba(245, 240, 225, 0.6) 100%);
  transform: translateX(8px);
  border-color: #d2b48c;
}

.segment-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.segment-name {
  color: #8b4513;
  font-weight: 700;
  font-size: 16px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
}

.segment-count {
  color: #7a6a5b;
  font-weight: 600;
  font-size: 14px;
  background: rgba(255, 255, 255, 0.8);
  padding: 4px 12px;
  border-radius: 20px;
  border: 2px solid #e8dccb;
}

.segment-bar-bg {
  height: 12px;
  background: linear-gradient(135deg, rgba(222, 184, 135, 0.1) 0%, rgba(210, 180, 140, 0.05) 100%);
  border-radius: 6px;
  overflow: hidden;
  border: 2px solid #e8dccb;
}

.segment-bar {
  height: 100%;
  background: linear-gradient(90deg, #ffc0cb 0%, #a0522d 100%);
  border-radius: 6px;
  transition: width 0.6s ease-out;
}

.tags-container {
  padding: 20px;
}

.tag-actions {
  margin-bottom: 32px;
}

.btn-success {
  background: linear-gradient(135deg, #2c9678 0%, #1e7d5e 100%);
  color: #fff8dc;
  border: none;
  padding: 14px 28px;
  border-radius: 25px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.02em;
  box-shadow: 0 6px 20px rgba(44, 150, 120, 0.3);
  transition: all 0.3s ease-out;
  display: flex;
  align-items: center;
  gap: 8px;
}

.btn-success::before {
  content: '🏷️';
  font-size: 18px;
}

.btn-success:hover {
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 10px 25px rgba(44, 150, 120, 0.4);
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.tag-item {
  background: linear-gradient(135deg, rgba(255, 192, 203, 0.2) 0%, rgba(210, 180, 140, 0.1) 100%);
  padding: 16px 24px;
  border-radius: 25px;
  display: flex;
  align-items: center;
  gap: 20px;
  border: 3px solid #e8dccb;
  transition: all 0.3s ease-out;
  cursor: pointer;
  min-width: 200px;
}

.tag-item:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow: 0 10px 25px rgba(160, 82, 45, 0.2);
  border-color: #d2b48c;
  background: linear-gradient(135deg, rgba(255, 192, 203, 0.3) 0%, rgba(210, 180, 140, 0.2) 100%);
}

.tag-item span {
  color: #8b4513;
  font-weight: 700;
  font-size: 16px;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  flex: 1;
}

.tag-ops {
  display: flex;
  gap: 12px;
}

.tag-ops button {
  background: linear-gradient(135deg, rgba(160, 82, 45, 0.1) 0%, rgba(139, 69, 19, 0.05) 100%);
  color: #8b4513;
  border: 2px solid rgba(160, 82, 45, 0.3);
  padding: 6px 16px;
  border-radius: 20px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 13px;
  transition: all 0.3s ease-out;
}

.tag-ops button:hover {
  background: linear-gradient(135deg, #ffc0cb 0%, #a0522d 100%);
  color: #fff8dc;
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.3);
  border-color: #a0522d;
}

.text-danger {
  color: #ff6b6b !important;
  font-weight: 700;
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(139, 69, 19, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  backdrop-filter: blur(8px);
}

.modal-container {
  background: linear-gradient(135deg, #fff8dc 0%, #f5f0e1 100%);
  padding: 32px;
  border-radius: 30px;
  width: 500px;
  max-width: 90vw;
  box-shadow: 0 15px 40px rgba(160, 82, 45, 0.25);
  border: 3px solid #d2b48c;
  animation: modalAppear 0.4s ease-out;
}

@keyframes modalAppear {
  from {
    opacity: 0;
    transform: translateY(30px) scale(0.95);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.modal-container h3 {
  font-size: 24px;
  font-weight: 700;
  color: #8b4513;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  margin-bottom: 28px;
  padding-bottom: 16px;
  border-bottom: 3px dashed #d4c7b5;
  text-align: center;
}

.form-item {
  margin-bottom: 24px;
}

.form-item label {
  display: block;
  margin-bottom: 12px;
  color: #7a6a5b;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 15px;
  letter-spacing: 0.02em;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 20px;
  margin-top: 32px;
  padding-top: 24px;
  border-top: 3px dashed #d4c7b5;
}

.modal-footer button {
  padding: 12px 28px;
  border-radius: 25px;
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  font-weight: 600;
  font-size: 16px;
  letter-spacing: 0.02em;
  transition: all 0.3s ease-out;
  border: 3px solid;
}

.modal-footer button:first-child {
  background: rgba(255, 255, 255, 0.9);
  color: #7a6a5b;
  border-color: #d4c7b5;
}

.modal-footer button:first-child:hover {
  background: rgba(255, 192, 203, 0.2);
  color: #a0522d;
  border-color: #ffc0cb;
  transform: translateY(-2px);
}

.modal-footer .btn-primary {
  background: linear-gradient(135deg, #a0522d 0%, #8b4513 100%);
  color: #fff8dc;
  border-color: #d2b48c !important;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.modal-footer .btn-primary:hover {
  background: linear-gradient(135deg, #8b4513 0%, #a0522d 100%);
  transform: translateY(-3px) scale(1.05);
  box-shadow: 0 10px 25px rgba(160, 82, 45, 0.4);
}

.modal-footer .btn-primary:active {
  transform: translateY(0) scale(0.98);
}
</style>