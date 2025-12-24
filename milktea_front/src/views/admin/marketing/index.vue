<template>
  <div class="marketing-manage">
    <div class="marketing-tabs card">
      <div class="tab-item" :class="{ active: activeTab === 'activity' }" @click="activeTab = 'activity'">活动管理</div>
      <div class="tab-item" :class="{ active: activeTab === 'coupon' }" @click="activeTab = 'coupon'">优惠券管理</div>
      <div class="tab-item" :class="{ active: activeTab === 'banner' }" @click="activeTab = 'banner'">广告/轮播图</div>
    </div>

    <div class="content-container card">
      <!-- 活动管理 -->
      <div v-if="activeTab === 'activity'" class="activity-section">
        <div class="section-header">
          <button class="btn-success" @click="showActivityModal()">创建活动</button>
        </div>
        <table class="admin-table">
          <thead>
            <tr>
              <th>活动名称</th>
              <th>类型</th>
              <th>时间范围</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="a in activities" :key="a.id">
              <td>{{ a.name }}</td>
              <td>{{ a.type }}</td>
              <td>{{ formatDate(a.startTime) }} ~ {{ formatDate(a.endTime) }}</td>
              <td>
                <span :class="a.isActive ? 'status-on' : 'status-off'">{{ a.isActive ? '进行中' : '已停止' }}</span>
              </td>
              <td class="ops">
                <button @click="toggleActivity(a)">{{ a.isActive ? '停止' : '启动' }}</button>
                <button @click="showActivityModal(a)">编辑</button>
                <button @click="viewEffect(a)">效果分析</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 优惠券管理 -->
      <div v-else-if="activeTab === 'coupon'" class="coupon-section">
        <div class="section-header">
          <button class="btn-success" @click="showCouponModal()">新增模板</button>
        </div>
        <table class="admin-table">
          <thead>
            <tr>
              <th>模板名称</th>
              <th>类型</th>
              <th>面值/折扣</th>
              <th>门槛</th>
              <th>已领/总量</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="t in couponTemplates" :key="t.id">
              <td>{{ t.name }}</td>
              <td>{{ t.type === 'REDUCTION' ? '满减' : '折扣' }}</td>
              <td>{{ t.type === 'REDUCTION' ? '¥' + t.value : t.value + '折' }}</td>
              <td>¥{{ t.minAmount }}</td>
              <td>{{ t.receivedCount }}/{{ t.totalCount }}</td>
              <td class="ops">
                <button @click="distributeCoupon(t)">精准发放</button>
                <button @click="viewDistribution(t)">发放记录</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 广告管理 -->
      <div v-else class="banner-section">
        <div class="section-header">
          <button class="btn-success" @click="showBannerModal()">新增轮播图</button>
        </div>
        <div class="banner-grid">
          <div v-for="b in banners" :key="b.id" class="banner-card">
            <img :src="b.imageUrl" class="banner-img" />
            <div class="banner-info">
              <p>位置：{{ b.position }}</p>
              <p>排序：{{ b.sort }}</p>
            </div>
            <div class="banner-ops">
              <button @click="showBannerModal(b)">编辑</button>
              <button class="text-danger" @click="deleteBanner(b)">删除</button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 活动编辑弹窗 -->
    <div v-if="activityModal.show" class="modal-mask">
      <div class="modal-container">
        <h3>{{ activityModal.isEdit ? '编辑活动' : '创建活动' }}</h3>
        <div class="form-item">
          <label>活动名称：</label>
          <input v-model="activityModal.form.name" class="admin-input" />
        </div>
        <div class="form-item">
          <label>活动类型：</label>
          <select v-model="activityModal.form.type" class="admin-select">
            <option value="FULL_REDUCE">满减活动</option>
            <option value="SECOND_HALF">第二杯半价</option>
            <option value="FLASH_SALE">限时秒杀</option>
          </select>
        </div>
        <div class="modal-footer">
          <button @click="activityModal.show = false">取消</button>
          <button class="btn-primary" @click="saveActivity">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { get, post, put, del } from '../../../utils/request'

const activeTab = ref('activity')
const activities = ref([])
const couponTemplates = ref([])
const banners = ref([])

const activityModal = ref({ show: false, isEdit: false, form: {} })

const loadActivities = async () => {
  const res = await get('/api/admin/activities')
  activities.value = res.data
}

const loadCoupons = async () => {
  const res = await get('/api/admin/coupon-templates')
  couponTemplates.value = res.data
}

const loadBanners = async () => {
  const res = await get('/api/admin/banners')
  banners.value = res.data
}

watch(activeTab, (val) => {
  if (val === 'activity') loadActivities()
  if (val === 'coupon') loadCoupons()
  if (val === 'banner') loadBanners()
})

const toggleActivity = async (a) => {
  await put(`/api/admin/activities/${a.id}/status`, { isActive: !a.isActive })
  loadActivities()
}

const viewEffect = (a) => {
  alert(`活动 ${a.name} 效果分析：\n参与人数：120\n带动销售额：¥2,400`)
}

const distributeCoupon = async (t) => {
  const userId = prompt('请输入目标用户ID（留空则按等级发放）：')
  await post('/api/admin/coupons/distribute', { templateId: t.id, userId })
  alert('发放成功')
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}-${date.getDate()}`
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.marketing-manage { display: flex; flex-direction: column; gap: 20px; }
.card { background: white; padding: 20px; border-radius: 8px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); }

.marketing-tabs { display: flex; gap: 32px; padding: 0 20px; height: 50px; align-items: center; }
.tab-item { cursor: pointer; color: #595959; position: relative; height: 100%; display: flex; align-items: center; }
.tab-item.active { color: #1890ff; font-weight: bold; }
.tab-item.active::after { content: ''; position: absolute; bottom: 0; left: 0; width: 100%; height: 2px; background: #1890ff; }

.section-header { margin-bottom: 20px; }
.admin-table { width: 100%; border-collapse: collapse; }
.admin-table th, .admin-table td { padding: 12px; text-align: left; border-bottom: 1px solid #f0f0f0; }

.status-on { color: #52c41a; }
.status-off { color: #f5222d; }

.ops button { margin-right: 8px; color: #1890ff; background: none; border: none; cursor: pointer; }
.ops button:hover { text-decoration: underline; }

.banner-grid { display: grid; grid-template-columns: repeat(auto-fill, minmax(240px, 1fr)); gap: 20px; }
.banner-card { border: 1px solid #f0f0f0; border-radius: 8px; overflow: hidden; }
.banner-img { width: 100%; height: 120px; object-fit: cover; }
.banner-info { padding: 12px; font-size: 14px; }
.banner-ops { padding: 12px; border-top: 1px solid #f0f0f0; display: flex; justify-content: flex-end; gap: 12px; }

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