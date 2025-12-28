<template>
  <div class="review-page">
    <div class="loading" v-if="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <div v-else-if="order" class="content">
      <!-- 商品简要信息 -->
      <div class="order-card card">
        <div class="goods-list">
          <div class="goods-item" v-for="item in order.items" :key="item.id">
            <img :src="item.image" class="goods-image" />
            <div class="goods-info">
              <span class="goods-name">{{ item.name }}</span>
              <span class="goods-specs" v-if="item.customizations">
                {{ item.customizations.sweetness }} / {{ item.customizations.temperature }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- 评分区域 -->
      <div class="rating-card card">
        <div class="rating-item">
          <div class="section-title">商品评价</div>
          <div class="stars">
            <span
              v-for="i in 5"
              :key="i"
              class="star"
              :class="{ active: i <= productScore }"
              @click="productScore = i"
            >
              {{ i <= productScore ? '⭐' : '☆' }}
            </span>
            <span class="score-text">{{ getScoreText(productScore) }}</span>
          </div>
        </div>

        <div class="rating-item" style="margin-top: 20px;">
          <div class="section-title">配送评价</div>
          <div class="stars">
            <span
              v-for="i in 5"
              :key="i"
              class="star"
              :class="{ active: i <= deliveryScore }"
              @click="deliveryScore = i"
            >
              {{ i <= deliveryScore ? '⭐' : '☆' }}
            </span>
            <span class="score-text">{{ getScoreText(deliveryScore) }}</span>
          </div>
        </div>
      </div>

      <!-- 评价内容 -->
      <div class="content-card card">
        <textarea 
          v-model="content" 
          placeholder="分享你的饮用体验，帮助更多小伙伴~" 
          maxlength="200"
        ></textarea>
        <div class="word-count">{{ content.length }}/200</div>
      </div>

      <!-- 图片上传 -->
      <div class="image-card card">
        <div class="section-title">上传图片</div>
        <div class="image-list">
          <div class="image-item" v-for="(img, index) in images" :key="index" @click="previewImage(img)">
            <img :src="img" />
            <div class="delete-btn" @click.stop="removeImage(index)">×</div>
          </div>
          <div class="upload-btn" v-if="images.length < 4" @click="triggerUpload">
            <span class="plus">+</span>
            <span class="text">添加图片</span>
          </div>
        </div>
        <input type="file" ref="fileInput" hidden accept="image/*" @change="onFileChange" />
      </div>

      <!-- 匿名选项 -->
      <div class="anonymous-card card">
        <label class="checkbox-label">
          <input type="checkbox" v-model="isAnonymous" />
          <span>匿名评价</span>
        </label>
      </div>

      <button class="submit-btn" :disabled="submitting" @click="submitReview">
        {{ submitting ? '提交中...' : '发布评价' }}
      </button>

      <!-- 申诉按钮 -->
      <div class="appeal-section" v-if="order.status === 'REVIEWED' || order.status === 'COMPLETED'">
        <button class="appeal-btn" @click="showAppealModal = true">申请退款/申诉</button>
      </div>

      <!-- 申诉弹窗 -->
      <div class="modal-mask" v-if="showAppealModal">
        <div class="modal-container">
          <div class="modal-header">
            <h3>申请退款</h3>
            <span class="close" @click="showAppealModal = false">×</span>
          </div>
          <div class="modal-body">
            <div class="form-item">
              <label>退款原因</label>
              <select v-model="appealReason">
                <option value="商品错漏">商品错漏</option>
                <option value="质量问题">质量问题</option>
                <option value="配送超时">配送超时</option>
                <option value="其他">其他</option>
              </select>
            </div>
            <div class="form-item">
              <label>详细描述</label>
              <textarea v-model="appealDescription" placeholder="请详细描述您的问题..."></textarea>
            </div>
            <div class="form-item">
              <label>退款金额</label>
              <div class="amount">¥{{ order.totalAmount }}</div>
            </div>
          </div>
          <div class="modal-footer">
            <button class="cancel-btn" @click="showAppealModal = false">取消</button>
            <button class="confirm-btn" :disabled="appealing" @click="submitAppeal">
              {{ appealing ? '提交中...' : '提交申请' }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { orderApi, commonApi } from '../../utils/api'

const route = useRoute()
const router = useRouter()

const loading = ref(true)
const submitting = ref(false)
const order = ref(null)
const productScore = ref(5)
const deliveryScore = ref(5)
const content = ref('')
const images = ref([])
const isAnonymous = ref(false)
const fileInput = ref(null)

const showAppealModal = ref(false)
const appealing = ref(false)
const appealReason = ref('商品错漏')
const appealDescription = ref('')

const getScoreText = (score) => {
  const texts = { 1: '极差', 2: '失望', 3: '一般', 4: '满意', 5: '超赞' }
  return texts[score]
}

const loadOrderDetail = async () => {
  loading.value = true
  try {
    const res = await orderApi.getOrderDetail(route.params.id)
    if (res.code === 200) {
      order.value = res.data
    }
  } catch (error) {
    console.error('加载订单详情失败:', error)
  } finally {
    loading.value = false
  }
}

const triggerUpload = () => {
  fileInput.value.click()
}

const onFileChange = async (e) => {
  const file = e.target.files[0]
  if (!file) return
  
  try {
    const res = await commonApi.uploadImage(file, 'review')
    if (res.code === 200) {
      images.value.push(res.data.url)
    }
  } catch (error) {
    console.error('上传图片失败:', error)
    alert('上传失败，请重试')
  }
}

const removeImage = (index) => {
  images.value.splice(index, 1)
}

const submitReview = async () => {
  if (!content.value.trim()) {
    alert('请输入评价内容')
    return
  }

  submitting.value = true
  try {
    const res = await orderApi.rateOrder(order.value.orderNo, {
      productScore: productScore.value,
      deliveryScore: deliveryScore.value,
      score: Math.round((productScore.value + deliveryScore.value) / 2),
      content: content.value,
      images: images.value,
      isAnonymous: isAnonymous.value,
      productId: order.value.orderItems && order.value.orderItems.length > 0 ? order.value.orderItems[0].productId : null
    })
    if (res.code === 200) {
      alert('评价成功！')
      loadOrderDetail()
    } else {
      alert(res.message || '评价失败')
    }
  } catch (error) {
    console.error('提交评价失败:', error)
    alert('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}

const submitAppeal = async () => {
  if (!appealDescription.value.trim()) {
    alert('请填写详细描述')
    return
  }
  appealing.value = true
  try {
    const res = await orderApi.submitAppeal(order.value.orderNo, {
      reason: appealReason.value,
      description: appealDescription.value,
      amount: order.value.totalAmount
    })
    if (res.code === 200) {
      alert('申诉已提交，请等待后台处理')
      showAppealModal.value = false
      loadOrderDetail()
    } else {
      alert(res.message || '提交失败')
    }
  } catch (error) {
    console.error('提交申诉失败:', error)
    alert('提交失败，请重试')
  } finally {
    appealing.value = false
  }
}

onMounted(() => {
  loadOrderDetail()
})
</script>

<style scoped>
/* 奶茶主题 CSS 变量定义 */
.review-page {
  --background-color: #f5f0e1; /* 奶油色背景 */
  --surface-color: #e8dccb;    /* 表面元素色 - 浅卡其 */
  --primary-color: #a0522d;    /* 焦糖色 - 主色调 */
  --primary-dark: #8b4513;     /* 深咖啡色 */
  --primary-light: #d2b48c;    /* 浅驼色 */
  --accent-cream: #fff8dc;     /* 奶油强调色 */
  --accent-pink: #ffc0cb;      /* 淡粉色 */
  --accent-brown: #deb887;     /* 沙棕色 */
  --text-color-dark: #4a3b30;  /* 深棕色文本 */
  --text-color-medium: #7a6a5b; /* 中棕色文本 */
  --text-color-light: #a09080;  /* 浅咖色文本 */
  --border-color: #d4c7b5;      /* 边框色 */
  --star-color: #ffb347;       /* 星星颜色 - 奶茶焦糖色 */
  --star-hover: #ff8c00;       /* 星星悬停色 */

  --border-radius-sm: 8px;
  --border-radius-md: 16px;
  --border-radius-lg: 24px;
  --border-radius-xl: 50%;      /* 用于圆形元素 */

  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;

  --shadow-soft: 0 4px 12px rgba(160, 82, 45, 0.08);
  --shadow-medium: 0 6px 20px rgba(160, 82, 45, 0.12);
  --shadow-hover: 0 8px 24px rgba(160, 82, 45, 0.16);

  --transition-smooth: all 0.25s ease-out;
  --transition-bounce: all 0.3s cubic-bezier(0.68, -0.55, 0.265, 1.55);
}

.review-page {
  min-height: 100vh;
  background: var(--background-color);
  padding: var(--spacing-md);
  padding-bottom: 120px;
  font-family: 'Noto Sans KR', 'Nunito', 'Quicksand', sans-serif;
  color: var(--text-color-dark);
}

/* 加载状态 */
.loading {
  padding-top: 120px;
  text-align: center;
  color: var(--text-color-medium);
}

.loading p {
  margin-top: var(--spacing-md);
  font-size: 15px;
  font-weight: 500;
  font-family: 'Prompt', sans-serif;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 3px solid var(--accent-cream);
  border-top: 3px solid var(--primary-color);
  border-radius: 50%;
  margin: 0 auto var(--spacing-md);
  animation: spin 1s linear infinite;
  position: relative;
}

.loading-spinner::after {
  content: '';
  position: absolute;
  top: -3px;
  left: -3px;
  right: -3px;
  bottom: -3px;
  border-radius: 50%;
  border: 3px solid transparent;
  border-top: 3px solid var(--accent-pink);
  animation: spin 1.2s linear infinite reverse;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 卡片通用样式 */
.card {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  margin-bottom: var(--spacing-lg);
  overflow: hidden;
  box-shadow: var(--shadow-soft);
  border: 1px solid var(--border-color);
  transition: var(--transition-smooth);
  position: relative;
}

.card:hover {
  box-shadow: var(--shadow-medium);
  transform: translateY(-2px);
}

.card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg,
  var(--primary-color) 0%,
  var(--accent-pink) 50%,
  var(--accent-cream) 100%);
  opacity: 0.6;
  border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
}

/* 订单商品卡片 */
.order-card {
  padding: var(--spacing-lg);
}

.goods-item {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  padding: var(--spacing-md);
  margin-bottom: var(--spacing-sm);
  background: rgba(255, 248, 220, 0.3);
  border-radius: var(--border-radius-md);
  border: 1px solid rgba(212, 199, 181, 0.3);
  transition: var(--transition-smooth);
}

.goods-item:hover {
  background: rgba(255, 248, 220, 0.5);
  transform: translateX(4px);
}

.goods-item:last-child {
  margin-bottom: 0;
}

.goods-image {
  width: 60px;
  height: 60px;
  border-radius: var(--border-radius-md);
  object-fit: cover;
  border: 2px solid var(--accent-cream);
  box-shadow: 0 3px 8px rgba(139, 69, 19, 0.1);
}

.goods-info {
  display: flex;
  flex-direction: column;
  justify-content: center;
  flex: 1;
}

.goods-name {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-color-dark);
  margin-bottom: var(--spacing-xs);
  font-family: 'Prompt', sans-serif;
}

.goods-specs {
  font-size: 13px;
  color: var(--text-color-medium);
  background: rgba(222, 184, 135, 0.2);
  padding: 2px var(--spacing-sm);
  border-radius: var(--border-radius-sm);
  display: inline-block;
  max-width: fit-content;
}

/* 评分卡片 */
.rating-card {
  padding: var(--spacing-lg);
}

.section-title {
  font-size: 16px;
  font-weight: 600;
  margin-bottom: var(--spacing-lg);
  color: var(--primary-dark);
  font-family: 'Prompt', sans-serif;
  position: relative;
  padding-left: var(--spacing-sm);
}

.section-title::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: var(--primary-color);
  border-radius: 2px;
}

.stars {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  flex-wrap: wrap;
}

.star {
  font-size: 36px;
  cursor: pointer;
  color: var(--border-color);
  transition: var(--transition-bounce);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  line-height: 1;
  display: inline-block;
}

.star:hover {
  transform: scale(1.2) rotate(10deg);
  color: var(--star-hover);
}

.star.active {
  color: var(--star-color);
  animation: starPop 0.3s ease-out;
}

@keyframes starPop {
  0% { transform: scale(1); }
  50% { transform: scale(1.3); }
  100% { transform: scale(1); }
}

.score-text {
  font-size: 15px;
  font-weight: 500;
  color: var(--primary-color);
  background: var(--accent-cream);
  padding: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-md);
  border: 1px solid var(--accent-brown);
  transition: var(--transition-smooth);
}

/* 评价内容卡片 */
.content-card {
  padding: var(--spacing-lg);
}

textarea {
  width: 100%;
  height: 140px;
  border: none;
  font-size: 15px;
  resize: none;
  box-sizing: border-box;
  background: transparent;
  color: var(--text-color-dark);
  font-family: 'Noto Sans KR', sans-serif;
  line-height: 1.6;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: var(--transition-smooth);
}

textarea:focus {
  outline: none;
  background: var(--accent-cream);
  box-shadow: 0 0 0 3px rgba(160, 82, 45, 0.15);
}

textarea::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

.word-count {
  text-align: right;
  font-size: 13px;
  color: var(--text-color-medium);
  margin-top: var(--spacing-sm);
  padding-right: var(--spacing-xs);
}

/* 图片上传卡片 */
.image-card {
  padding: var(--spacing-lg);
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: var(--spacing-md);
}

.image-item, .upload-btn {
  width: 85px;
  height: 85px;
  border-radius: var(--border-radius-md);
  position: relative;
  overflow: hidden;
  transition: var(--transition-bounce);
  cursor: pointer;
}

.image-item {
  border: 2px solid var(--accent-cream);
  box-shadow: var(--shadow-soft);
}

.image-item:hover {
  transform: scale(1.05) rotate(2deg);
  box-shadow: var(--shadow-hover);
}

.image-item img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: var(--transition-smooth);
}

.image-item:hover img {
  transform: scale(1.1);
}

.delete-btn {
  position: absolute;
  top: -6px;
  right: -6px;
  width: 22px;
  height: 22px;
  background: var(--primary-color);
  color: white;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
  transition: var(--transition-smooth);
  border: 2px solid white;
  box-shadow: 0 2px 6px rgba(0, 0, 0, 0.2);
  z-index: 2;
}

.delete-btn:hover {
  background: var(--primary-dark);
  transform: scale(1.1);
}

.upload-btn {
  border: 2px dashed var(--border-color);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: var(--text-color-medium);
  background: rgba(255, 255, 255, 0.3);
  transition: var(--transition-smooth);
}

.upload-btn:hover {
  border-color: var(--primary-color);
  color: var(--primary-color);
  background: rgba(160, 82, 45, 0.05);
  transform: scale(1.05);
}

.upload-btn .plus {
  font-size: 28px;
  font-weight: 300;
  line-height: 1;
  margin-bottom: var(--spacing-xs);
}

.upload-btn .text {
  font-size: 12px;
  font-weight: 500;
}

/* 匿名选项卡片 */
.anonymous-card {
  padding: var(--spacing-lg);
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  font-size: 14px;
  color: var(--text-color-medium);
  cursor: pointer;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-md);
  transition: var(--transition-smooth);
}

.checkbox-label:hover {
  background: rgba(255, 248, 220, 0.3);
}

.checkbox-label input[type="checkbox"] {
  appearance: none;
  width: 20px;
  height: 20px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  outline: none;
  cursor: pointer;
  position: relative;
  transition: var(--transition-smooth);
}

.checkbox-label input[type="checkbox"]:checked {
  background-color: var(--primary-color);
  border-color: var(--primary-color);
}

.checkbox-label input[type="checkbox"]:checked::after {
  content: '✓';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  color: white;
  font-size: 12px;
  font-weight: bold;
}

.checkbox-label input[type="checkbox"]:hover {
  border-color: var(--primary-color);
}

/* 提交按钮 */
.submit-btn {
  position: fixed;
  bottom: var(--spacing-lg);
  left: var(--spacing-lg);
  right: var(--spacing-lg);
  height: 56px;
  background: linear-gradient(135deg,
  var(--primary-color) 0%,
  var(--primary-dark) 100%);
  color: white;
  border: none;
  border-radius: var(--border-radius-xl);
  font-size: 18px;
  font-weight: 600;
  font-family: 'Prompt', sans-serif;
  cursor: pointer;
  box-shadow: var(--shadow-medium);
  transition: var(--transition-bounce);
  letter-spacing: 1px;
  z-index: 10;
  overflow: hidden;
}

.submit-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
  transparent,
  rgba(255, 255, 255, 0.2),
  transparent);
  transition: 0.6s;
}

.submit-btn:hover {
  transform: translateY(-4px) scale(1.02);
  box-shadow: var(--shadow-hover);
}

.submit-btn:hover::before {
  left: 100%;
}

.submit-btn:active {
  transform: translateY(0) scale(0.98);
}

.submit-btn:disabled {
  background: linear-gradient(135deg,
  var(--text-color-light) 0%,
  var(--border-color) 100%);
  cursor: not-allowed;
  transform: none;
  box-shadow: var(--shadow-soft);
}

.submit-btn:disabled:hover {
  transform: none;
  box-shadow: var(--shadow-soft);
}

.submit-btn:disabled::before {
  display: none;
}

/* 申诉部分 */
.appeal-section {
  margin-top: 20px;
  text-align: center;
}

.appeal-btn {
  background: transparent;
  color: var(--text-color-medium);
  border: 1px solid var(--border-color);
  padding: 8px 20px;
  border-radius: 20px;
  font-size: 14px;
  cursor: pointer;
  transition: var(--transition-smooth);
}

.appeal-btn:hover {
  color: var(--primary-color);
  border-color: var(--primary-color);
  background: rgba(160, 82, 45, 0.05);
}

/* 弹窗样式 */
.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 100;
  backdrop-filter: blur(4px);
}

.modal-container {
  width: 90%;
  max-width: 400px;
  background: white;
  border-radius: 24px;
  overflow: hidden;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  animation: modalPop 0.3s ease-out;
}

@keyframes modalPop {
  from { transform: scale(0.9); opacity: 0; }
  to { transform: scale(1); opacity: 1; }
}

.modal-header {
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  border-bottom: 1px solid #eee;
}

.modal-header h3 {
  margin: 0;
  font-size: 18px;
  color: var(--text-color-dark);
}

.close {
  font-size: 24px;
  color: #999;
  cursor: pointer;
}

.modal-body {
  padding: 20px;
}

.form-item {
  margin-bottom: 16px;
}

.form-item label {
  display: block;
  font-size: 14px;
  color: var(--text-color-medium);
  margin-bottom: 8px;
}

.form-item select, .form-item textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid var(--border-color);
  border-radius: 12px;
  font-size: 14px;
  outline: none;
}

.form-item textarea {
  height: 100px;
  resize: none;
}

.form-item .amount {
  font-size: 20px;
  font-weight: bold;
  color: var(--primary-color);
}

.modal-footer {
  padding: 16px 20px;
  display: flex;
  gap: 12px;
  border-top: 1px solid #eee;
}

.modal-footer button {
  flex: 1;
  height: 44px;
  border-radius: 22px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  border: none;
}

.cancel-btn {
  background: #f5f5f5;
  color: #666;
}

.confirm-btn {
  background: var(--primary-color);
  color: white;
}

.confirm-btn:disabled {
  opacity: 0.6;
}

/* 响应式调整 */
@media (max-width: 480px) {
  .review-page {
    padding: var(--spacing-sm);
    padding-bottom: 110px;
  }

  .card {
    padding: var(--spacing-md);
  }

  .goods-item {
    padding: var(--spacing-sm);
    gap: var(--spacing-sm);
  }

  .goods-image {
    width: 50px;
    height: 50px;
  }

  .star {
    font-size: 32px;
  }

  .image-item, .upload-btn {
    width: 75px;
    height: 75px;
  }

  .submit-btn {
    height: 52px;
    font-size: 17px;
    left: var(--spacing-sm);
    right: var(--spacing-sm);
  }
}

/* 图片预览动画 */
@keyframes imagePreview {
  from { opacity: 0; transform: scale(0.8); }
  to { opacity: 1; transform: scale(1); }
}

.image-item {
  animation: imagePreview 0.3s ease-out;
}

/* 奶茶主题装饰元素 */
.order-card::after {
  content: '🥤';
  position: absolute;
  top: -10px;
  right: -10px;
  font-size: 24px;
  opacity: 0.1;
  transform: rotate(15deg);
}

.rating-card::after {
  content: '⭐';
  position: absolute;
  bottom: -15px;
  right: 15px;
  font-size: 20px;
  opacity: 0.1;
  transform: rotate(-10deg);
}
</style>