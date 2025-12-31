<template>
  <div class="refund-page">
    <div class="header">
      <button class="back-btn" @click="router.back()">←</button>
      <h2 class="title">申请退款</h2>
    </div>

    <div class="content">
      <!-- 订单信息 -->
      <div class="order-info card">
        <div class="section-title">订单信息</div>
        <div class="info-item">
          <span>订单编号：</span>
          <span>{{ orderId }}</span>
        </div>
      </div>

      <!-- 退款原因 -->
      <div class="refund-reason card">
        <div class="section-title">退款原因</div>
        <div class="reason-list">
          <div 
            class="reason-item" 
            v-for="r in reasons" 
            :key="r"
            :class="{ active: selectedReason === r }"
            @click="selectedReason = r"
          >
            {{ r }}
          </div>
        </div>
      </div>

      <!-- 详细说明 -->
      <div class="refund-desc card">
        <div class="section-title">详细说明</div>
        <textarea 
          v-model="description" 
          placeholder="请补充退款详细说明（选填）"
          class="desc-input"
        ></textarea>
      </div>

      <div class="tip">
        提交申请后，商家将在 24 小时内处理。退款将原路返回。
      </div>
    </div>

    <div class="footer">
      <button 
        class="submit-btn" 
        :disabled="!selectedReason || submitting"
        @click="submitRefund"
      >
        {{ submitting ? '提交中...' : '提交申请' }}
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { orderApi } from '../../utils/api' // 确保此处的 api 内部已切换为真实后台接口

const router = useRouter()
const route = useRoute()

// 状态变量
const orderId = route.query.orderId
const selectedReason = ref('')
const description = ref('')
const submitting = ref(false)

// 退款原因选项（通常也可从后端字典接口获取）
const reasons = [
  '点错了/不想要了',
  '商品售罄',
  '配送时间太长',
  '商家服务态度差',
  '商品质量问题',
  '其他原因'
]

/**
 * 提交退款申请
 * 直接对接后端 API，不再处理任何 IndexedDB 本地存储逻辑
 */
const submitRefund = async () => {
  if (!orderId) return
  
  submitting.value = true
  try {
    // 拼接原因和描述
    const reasonText = selectedReason.value + (description.value ? ': ' + description.value : '')
    
    // 发起网络请求
    const res = await orderApi.applyRefund({
      orderId,
      reason: reasonText
    })
    
    if (res.code === 200 || res.success) {
      alert('退款申请已提交')
      router.back()
    } else {
      alert(res.message || '申请失败，请稍后重试')
    }
  } catch (error) {
    console.error('退款提交异常:', error)
    alert('网络异常，请检查网络连接')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
/* 样式部分保持不变，仅用于UI展示 */
.refund-page {
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
.card {
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
.info-item {
  font-size: 14px;
  color: #666;
  display: flex;
  justify-content: space-between;
}
.reason-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}
.reason-item {
  padding: 12px;
  background: #f9f9f9;
  border-radius: 8px;
  font-size: 14px;
  color: #333;
  border: 1px solid transparent;
  cursor: pointer;
}
.reason-item.active {
  background: #fff5f0;
  color: #ff6b00;
  border-color: #ff6b00;
}
.desc-input {
  width: 100%;
  height: 100px;
  background: #f9f9f9;
  border: none;
  border-radius: 8px;
  padding: 12px;
  font-size: 14px;
  resize: none;
  box-sizing: border-box;
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
  cursor: pointer;
}
.submit-btn:disabled {
  background: #ccc;
  cursor: not-allowed;
}
</style>
