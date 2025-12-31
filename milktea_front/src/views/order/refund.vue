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
import { refundDB } from '../../utils/db'

const router = useRouter()
const route = useRoute()
const orderId = route.query.orderId
const selectedReason = ref('')
const description = ref('')
const submitting = ref(false)

const reasons = [
  '点错了/不想要了',
  '商品售罄',
  '配送时间太长',
  '商家服务态度差',
  '商品质量问题',
  '其他原因'
]

const submitRefund = async () => {
  submitting.value = true
  // DEMO ONLY: 模拟退款逻辑
  try {
    await refundDB.add({
      orderId: orderId,
      reason: selectedReason.value,
      description: description.value,
      status: 'PENDING'
    })
    
    setTimeout(() => {
      alert('退款申请已提交，请耐心等待')
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