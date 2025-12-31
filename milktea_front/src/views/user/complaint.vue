<template>
  <div class="complaint-page">
    <div class="header">
      <button class="back-btn" @click="router.back()">←</button>
      <h2 class="title">投诉建议</h2>
    </div>

    <div class="content">
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
    </div>

    <div class="footer">
      <button 
        class="submit-btn" 
        :disabled="!selectedType || !content || submitting"
        @click="submitComplaint"
      >
        {{ submitting ? '提交中...' : '提交反馈' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { complaintDB } from '../../utils/db'

const router = useRouter()
const selectedType = ref('')
const content = ref('')
const contact = ref('')
const submitting = ref(false)

const types = [
  '服务态度',
  '商品质量',
  '配送问题',
  '功能建议',
  '其他投诉'
]

const submitComplaint = async () => {
  submitting.value = true
  // DEMO ONLY: 模拟提交逻辑
  try {
    await complaintDB.add({
      type: selectedType.value,
      content: content.value,
      contact: contact.value,
      status: 'PENDING'
    })
    
    setTimeout(() => {
      alert('感谢您的反馈，我们会尽快处理')
      router.back()
    }, 500)
  } catch (e) {
    console.error(e)
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.complaint-page {
  min-height: 100vh;
  background: #f8f8f8;
  padding-bottom: 100px;
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

.footer {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  padding: 20px;
  background: white;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
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
</style>