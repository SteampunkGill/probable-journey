<template>
  <div class="complaint-page">
    <div class="header">
      <button class="back-btn" @click="router.back()">←</button>
      <h2 class="title">投诉建议</h2>
    </div>

    <div class="tabs">
      <div 
        class="tab-item" 
        :class="{ active: activeTab === 'submit' }"
        @click="activeTab = 'submit'"
      >
        提交反馈
      </div>
      <div 
        class="tab-item" 
        :class="{ active: activeTab === 'history' }"
        @click="activeTab = 'history'"
      >
        我的反馈
      </div>
    </div>

    <div class="content" v-if="activeTab === 'submit'">
      <!-- 投诉类型 -->
      <div class="form-section">
        <h3 class="section-title">反馈类型</h3>
        <div class="type-list">
          <div 
            class="type-item" 
            v-for="t in types" 
            :key="t"
            :class="{ active: selectedType === t }"
            @click="selectedType = t"
          >
            {{ t }}
          </div>
        </div>
      </div>

      <!-- 反馈内容 -->
      <div class="form-section">
        <h3 class="section-title">反馈内容</h3>
        <textarea 
          v-model="content" 
          placeholder="请详细描述您的问题或建议，我们将不断改进..."
          class="content-input"
        ></textarea>
      </div>

      <!-- 联系方式 -->
      <div class="form-section">
        <h3 class="section-title">联系方式</h3>
        <input 
          type="text" 
          v-model="contact" 
          placeholder="请留下您的手机号或邮箱（选填）"
          class="contact-input"
        />
      </div>

      <div class="tip">
        您的反馈对我们非常重要，我们会在 1-3 个工作日内处理并回复。
      </div>

      <div class="submit-wrapper">
        <button 
          class="submit-btn" 
          :disabled="!selectedType || !content || submitting"
          @click="submitComplaint"
        >
          {{ submitting ? '提交中...' : '提交反馈' }}
        </button>
      </div>
    </div>

    <div class="history-content" v-else>
      <div v-if="loading" class="loading-state">加载中...</div>
      <div v-else-if="complaints.length === 0" class="empty-state">
        <img src="../../assets/images/empty-cart.png" alt="暂无记录" class="empty-img" />
        <p>暂无反馈记录</p>
      </div>
      <div v-else class="complaint-list">
        <div v-for="item in complaints" :key="item.id" class="complaint-item">
          <div class="item-header">
            <span class="item-type">{{ item.type }}</span>
            <span class="item-status" :class="item.status.toLowerCase()">{{ formatStatus(item.status) }}</span>
          </div>
          <div class="item-content">{{ item.content }}</div>
          <div class="item-footer">
            <span class="item-time">{{ formatDate(item.createdAt) }}</span>
          </div>
          <div v-if="item.reply" class="item-reply">
            <div class="reply-title">官方回复：</div>
            <div class="reply-content">{{ item.reply }}</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import { useRouter } from 'vue-router'
import { afterSalesApi } from '../../utils/api'

const router = useRouter()
const activeTab = ref('submit')
const selectedType = ref('')
const content = ref('')
const contact = ref('')
const submitting = ref(false)
const loading = ref(false)
const complaints = ref([])

const types = [
  '服务态度',
  '商品质量',
  '配送问题',
  '功能建议',
  '其他投诉'
]

const formatStatus = (status) => {
  const statusMap = {
    'PENDING': '处理中',
    'HANDLED': '已回复',
    'CLOSED': '已关闭'
  }
  return statusMap[status] || status
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')} ${String(date.getHours()).padStart(2, '0')}:${String(date.getMinutes()).padStart(2, '0')}`
}

const fetchHistory = async () => {
  loading.value = true
  try {
    const res = await afterSalesApi.getAfterSalesRecords()
    if (res.code === 200) {
      complaints.value = res.data || []
    }
  } catch (e) {
    console.error('获取反馈历史失败:', e)
  } finally {
    loading.value = false
  }
}

const submitComplaint = async () => {
  submitting.value = true
  try {
    const res = await afterSalesApi.submitComplaint({
      type: selectedType.value,
      content: content.value,
      phone: contact.value
    })
    
    if (res.code === 200) {
      alert('感谢您的反馈，我们会尽快处理')
      selectedType.value = ''
      content.value = ''
      contact.value = ''
      activeTab.value = 'history'
      fetchHistory()
    } else {
      alert(res.message || '提交失败')
    }
  } catch (e) {
    console.error(e)
    alert('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

watch(activeTab, (newTab) => {
  if (newTab === 'history') {
    fetchHistory()
  }
})

onMounted(() => {
  // 初始不加载历史，切换标签时加载
})
</script>

<style scoped>
.complaint-page {
  min-height: 100vh;
  background: #f8f8f8;
}

.header {
  background: white;
  padding: 40px 20px 20px;
  display: flex;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
}

.back-btn {
  background: none;
  border: none;
  font-size: 24px;
  margin-right: 15px;
}

.title {
  font-size: 18px;
  font-weight: 600;
}

.tabs {
  display: flex;
  background: white;
  padding: 0 20px;
  border-bottom: 1px solid #eee;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 15px 0;
  font-size: 15px;
  color: #666;
  position: relative;
}

.tab-item.active {
  color: #ff6b00;
  font-weight: 600;
}

.tab-item.active::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 40px;
  height: 3px;
  background: #ff6b00;
  border-radius: 2px;
}

.content {
  padding: 15px;
}

.form-section {
  background: white;
  border-radius: 12px;
  padding: 15px;
  margin-bottom: 15px;
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: 12px;
}

.type-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.type-item {
  padding: 8px 16px;
  background: #f5f5f5;
  border-radius: 20px;
  font-size: 14px;
  color: #666;
  border: 1px solid transparent;
}

.type-item.active {
  background: #fff5f0;
  color: #ff6b00;
  border-color: #ff6b00;
}

.content-input {
  width: 100%;
  height: 150px;
  background: #f9f9f9;
  border: none;
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  resize: none;
}

.contact-input {
  width: 100%;
  height: 44px;
  background: #f9f9f9;
  border: none;
  border-radius: 8px;
  padding: 0 12px;
  font-size: 14px;
}

.tip {
  font-size: 12px;
  color: #999;
  text-align: center;
  padding: 10px 20px;
}

.submit-wrapper {
  margin-top: 30px;
  padding: 0 10px;
}

.submit-btn {
  width: 100%;
  height: 48px;
  background: #ff6b00;
  color: white;
  border: none;
  border-radius: 24px;
  font-size: 16px;
  font-weight: 600;
}

.submit-btn:disabled {
  background: #ccc;
}

.history-content {
  padding: 15px;
}

.complaint-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.complaint-item {
  background: white;
  border-radius: 12px;
  padding: 15px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.item-type {
  font-size: 15px;
  font-weight: 600;
  color: #333;
}

.item-status {
  font-size: 12px;
  padding: 2px 8px;
  border-radius: 4px;
}

.item-status.pending {
  background: #fff7e6;
  color: #faad14;
}

.item-status.handled {
  background: #f6ffed;
  color: #52c41a;
}

.item-status.closed {
  background: #f5f5f5;
  color: #999;
}

.item-content {
  font-size: 14px;
  color: #666;
  line-height: 1.6;
  margin-bottom: 10px;
}

.item-footer {
  font-size: 12px;
  color: #999;
}

.item-reply {
  margin-top: 12px;
  padding-top: 12px;
  border-top: 1px dashed #eee;
}

.reply-title {
  font-size: 13px;
  font-weight: 600;
  color: #ff6b00;
  margin-bottom: 5px;
}

.reply-content {
  font-size: 13px;
  color: #333;
  background: #fff9f5;
  padding: 8px;
  border-radius: 4px;
}

.loading-state, .empty-state {
  text-align: center;
  padding: 50px 0;
  color: #999;
}

.empty-img {
  width: 120px;
  margin-bottom: 15px;
}
</style>