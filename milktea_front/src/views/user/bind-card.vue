<template>
  <div class="bind-card-page">
    <div class="header">
      <i class="iconfont icon-left" @click="router.back()"></i>
      <span class="title">会员卡管理</span>
    </div>

    <div class="content">
      <!-- 已绑定卡片展示 -->
      <div class="card-preview" v-if="hasCard">
        <div class="card-bg bound">
          <div class="card-info">
            <span class="brand">奶茶小店</span>
            <span class="card-type">尊享会员卡</span>
          </div>
          <div class="card-number">
            {{ formatCardNumber(currentCardNumber) }}
          </div>
          <div class="card-status">已激活</div>
        </div>
        <button class="unbind-btn" @click="handleUnbind" :disabled="loading">
          {{ loading ? '处理中...' : '解除绑定' }}
        </button>
      </div>

      <!-- 未绑定状态：选择操作 -->
      <div v-else>
        <div class="tab-header">
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'bind' }"
            @click="activeTab = 'bind'"
          >绑定已有卡</div>
          <div 
            class="tab-item" 
            :class="{ active: activeTab === 'apply' }"
            @click="activeTab = 'apply'"
          >申请电子卡</div>
        </div>

        <!-- 绑定已有卡表单 -->
        <div class="form-container" v-if="activeTab === 'bind'">
          <div class="card-preview">
            <div class="card-bg">
              <div class="card-info">
                <span class="brand">奶茶小店</span>
                <span class="card-type">实体卡绑定</span>
              </div>
              <div class="card-number" v-if="cardNumber">
                {{ formatCardNumber(cardNumber) }}
              </div>
              <div class="card-number-placeholder" v-else>
                **** **** **** ****
              </div>
            </div>
          </div>

          <div class="bind-form">
            <div class="form-item">
              <label>卡号</label>
              <input
                type="text"
                v-model="cardNumber"
                placeholder="请输入会员卡号"
                maxlength="20"
              />
            </div>
            <div class="form-item">
              <label>激活码</label>
              <input 
                type="password" 
                v-model="activeCode" 
                placeholder="请输入卡片背面的激活码" 
                maxlength="6"
              />
            </div>
          </div>

          <button 
            class="action-btn" 
            :disabled="!isBindReady || loading"
            @click="handleBind"
          >
            {{ loading ? '正在绑定...' : '立即绑定' }}
          </button>
        </div>

        <!-- 申请电子卡表单 -->
        <div class="form-container" v-else>
          <div class="card-preview">
            <div class="card-bg apply">
              <div class="card-info">
                <span class="brand">奶茶小店</span>
                <span class="card-type">电子会员卡</span>
              </div>
              <div class="card-number-placeholder">
                申请后自动生成卡号
              </div>
            </div>
          </div>

          <div class="bind-form">
            <div class="form-item">
              <label>姓名</label>
              <input 
                type="text" 
                v-model="applyForm.name" 
                placeholder="请输入真实姓名" 
              />
            </div>
            <div class="form-item">
              <label>手机号</label>
              <input 
                type="tel" 
                v-model="applyForm.phone" 
                placeholder="请输入手机号" 
                maxlength="11"
              />
            </div>
            <div class="form-item">
              <label>生日</label>
              <input 
                type="date" 
                v-model="applyForm.birthday" 
              />
            </div>
          </div>

          <button 
            class="action-btn" 
            :disabled="!isApplyReady || loading"
            @click="handleApply"
          >
            {{ loading ? '正在申请...' : '立即申请' }}
          </button>
        </div>
      </div>

      <div class="tips">
        <p>温馨提示：</p>
        <p>1. 电子会员卡申请成功后立即生效。</p>
        <p>2. 绑定实体卡可同步线下消费积分。</p>
        <p>3. 如有疑问请联系在线客服。</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { memberApi, authApi } from '../../utils/api'

const router = useRouter()
const activeTab = ref('bind') // 'bind' or 'apply'
const loading = ref(false)
const hasCard = ref(false)
const currentCardNumber = ref('')

// 绑定表单
const cardNumber = ref('')
const activeCode = ref('')

// 申请表单
const applyForm = ref({
  name: '',
  phone: '',
  birthday: ''
})

const isBindReady = computed(() => {
  // 去掉空格后判断长度，卡号通常为 10-19 位，激活码 4-8 位
  const pureCardNumber = cardNumber.value.replace(/\s/g, '')
  return pureCardNumber.length >= 10 && activeCode.value.length >= 4
})

const isApplyReady = computed(() => {
  return applyForm.value.name && /^1[3-9]\d{9}$/.test(applyForm.value.phone)
})

const formatCardNumber = (num) => {
  if (!num) return ''
  return num.replace(/\s/g, '').replace(/(.{4})/g, '$1 ').trim()
}

const checkCardStatus = async () => {
  try {
    const user = await authApi.getUserProfile()
    if (user && user.memberCardNo) {
      hasCard.value = true
      currentCardNumber.value = user.memberCardNo
    } else {
      hasCard.value = false
      currentCardNumber.value = ''
    }
  } catch (error) {
    console.error('获取卡状态失败', error)
  }
}

const handleBind = async () => {
  if (loading.value) return
  loading.value = true
  try {
    await memberApi.bindCard(cardNumber.value)
    alert('会员卡绑定成功！')
    await checkCardStatus()
  } catch (error) {
    console.error('绑定失败:', error)
    alert(error.message || '绑定失败')
  } finally {
    loading.value = false
  }
}

const handleUnbind = async () => {
  if (!confirm('确定要解除会员卡绑定吗？解绑后将无法享受会员权益。')) return
  loading.value = true
  try {
    await memberApi.unbindCard()
    alert('已成功解绑')
    await checkCardStatus()
  } catch (error) {
    console.error('解绑失败:', error)
    alert(error.message || '解绑失败')
  } finally {
    loading.value = false
  }
}

const handleApply = async () => {
  if (loading.value) return
  loading.value = true
  try {
    await memberApi.applyCard(applyForm.value)
    alert('电子会员卡申请成功！')
    await checkCardStatus()
  } catch (error) {
    console.error('申请失败:', error)
    alert(error.message || '申请失败')
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  checkCardStatus()
})
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 - 会员卡管理页面 */
.bind-card-page {
  min-height: 100vh;
  background: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  position: relative;
  overflow-x: hidden;
}

/* 背景装饰元素 */
.bind-card-page::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 10% 90%, rgba(255, 248, 220, 0.15) 0%, transparent 40%),
      radial-gradient(circle at 90% 10%, rgba(222, 184, 135, 0.15) 0%, transparent 40%);
  pointer-events: none;
  z-index: 0;
}

/* 头部 */
.header {
  height: 60px;
  background: var(--surface-color);
  display: flex;
  align-items: center;
  padding: 0 var(--spacing-lg);
  position: sticky;
  top: 0;
  z-index: 100;
  border-bottom: 2px solid var(--border-color);
  backdrop-filter: blur(10px);
}

.header .iconfont.icon-left {
  font-size: 24px;
  color: var(--primary-color);
  cursor: pointer;
  width: 44px;
  height: 44px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-cream);
  border-radius: var(--border-radius-xl);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
}

.header .iconfont.icon-left:hover {
  transform: translateY(-2px) scale(1.05);
  background: var(--primary-light);
  box-shadow: 0 6px 16px rgba(160, 82, 45, 0.2);
}

.header .iconfont.icon-left:active {
  transform: translateY(0) scale(0.98);
}

.header .title {
  flex: 1;
  text-align: center;
  font-size: 20px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  letter-spacing: 0.5px;
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
  margin-right: 44px;
}

/* 内容区域 */
.content {
  padding: var(--spacing-lg);
  position: relative;
  z-index: 1;
}

/* 标签页头部 */
.tab-header {
  display: flex;
  background: var(--accent-cream);
  border-radius: var(--border-radius-lg);
  padding: 4px;
  margin-bottom: var(--spacing-xl);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
  border: 1px solid var(--border-color);
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: var(--spacing-md) 0;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-color-medium);
  border-radius: var(--border-radius-md);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.tab-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, var(--primary-color), var(--accent-pink));
  opacity: 0;
  transition: opacity 0.3s ease-out;
}

.tab-item.active {
  background: white;
  color: var(--primary-color);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
  transform: translateY(-1px);
}

.tab-item.active::before {
  opacity: 0.1;
}

.tab-item:hover:not(.active) {
  background: rgba(255, 255, 255, 0.6);
  transform: translateY(-1px);
}

/* 卡片预览 */
.card-preview {
  margin-bottom: var(--spacing-xl);
  animation: fadeInUp 0.5s ease-out;
}

@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.card-bg {
  height: 200px;
  background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
  border-radius: var(--border-radius-xl);
  padding: var(--spacing-lg);
  color: var(--accent-cream);
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow:
      0 15px 35px rgba(139, 69, 19, 0.2),
      inset 0 1px 0 rgba(255, 255, 255, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.2);
  position: relative;
  overflow: hidden;
  transition: transform 0.3s ease-out;
}

.card-bg:hover {
  transform: translateY(-4px);
}

.card-bg::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 20% 80%, rgba(255, 255, 255, 0.1) 0%, transparent 50%),
      radial-gradient(circle at 80% 20%, rgba(255, 192, 203, 0.1) 0%, transparent 50%);
}

.card-bg.bound {
  background: linear-gradient(135deg, var(--primary-color), var(--accent-brown));
}

.card-bg.apply {
  background: linear-gradient(135deg, var(--accent-pink), var(--primary-light));
}

.card-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  z-index: 1;
}

.brand {
  font-size: 22px;
  font-weight: 800;
  font-family: var(--font-heading);
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
}

.card-type {
  font-size: 13px;
  font-weight: 600;
  opacity: 0.9;
  padding: 4px 12px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-md);
  backdrop-filter: blur(4px);
}

.card-number {
  font-size: 28px;
  font-family: 'Courier New', Courier, monospace;
  letter-spacing: 3px;
  text-align: center;
  position: relative;
  z-index: 1;
  text-shadow: 1px 1px 2px rgba(0, 0, 0, 0.3);
  font-weight: 700;
}

.card-number-placeholder {
  font-size: 18px;
  opacity: 0.7;
  text-align: center;
  position: relative;
  z-index: 1;
  font-weight: 500;
}

.card-status {
  text-align: right;
  font-size: 13px;
  font-weight: 600;
  position: relative;
  z-index: 1;
  padding: 4px 8px;
  background: rgba(255, 255, 255, 0.2);
  border-radius: var(--border-radius-md);
  display: inline-block;
  margin-left: auto;
  backdrop-filter: blur(4px);
}

/* 表单容器 */
.form-container {
  animation: fadeIn 0.5s ease-out;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.bind-form {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  padding: 0 var(--spacing-md);
  margin-bottom: var(--spacing-xl);
  box-shadow:
      0 8px 32px rgba(139, 69, 19, 0.12),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

.form-item {
  display: flex;
  align-items: center;
  height: 60px;
  border-bottom: 1px solid var(--border-color);
  transition: all 0.3s ease-out;
  position: relative;
}

.form-item:hover {
  background: var(--accent-cream);
  padding-left: 8px;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item label {
  width: 80px;
  font-size: 15px;
  font-weight: 600;
  color: var(--text-color-dark);
  position: relative;
  padding-left: var(--spacing-sm);
}

.form-item label::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 4px;
  height: 16px;
  background: var(--primary-color);
  border-radius: 2px;
  opacity: 0.6;
  transition: all 0.3s ease-out;
}

.form-item:hover label::before {
  opacity: 1;
  background: var(--accent-pink);
  transform: translateY(-50%) scale(1.1);
}

.form-item input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  color: var(--text-color-dark);
  background: transparent;
  font-family: var(--font-body);
  padding: var(--spacing-sm) 0;
}

.form-item input::placeholder {
  color: var(--text-color-light);
  opacity: 0.7;
}

.form-item input:focus {
  color: var(--primary-color);
}

/* 按钮样式 */
.action-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  border: none;
  border-radius: var(--border-radius-xl);
  font-size: 16px;
  font-weight: 700;
  font-family: var(--font-body);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 8px 24px rgba(160, 82, 45, 0.3);
  margin-bottom: var(--spacing-lg);
}

.action-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
  transition: left 0.5s ease-out;
}

.action-btn:hover {
  transform: translateY(-3px);
  box-shadow: 0 12px 32px rgba(160, 82, 45, 0.4);
}

.action-btn:hover::before {
  left: 100%;
}

.action-btn:active {
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.action-btn:disabled {
  background: var(--border-color);
  color: var(--text-color-light);
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

.action-btn:disabled::before {
  display: none;
}

/* 解绑按钮 */
.unbind-btn {
  width: 100%;
  height: 52px;
  background: linear-gradient(135deg, var(--accent-cream), white);
  color: #ff6b6b;
  border: 2px solid rgba(255, 107, 107, 0.4);
  border-radius: var(--border-radius-xl);
  font-size: 16px;
  font-weight: 700;
  font-family: var(--font-body);
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(255, 107, 107, 0.15);
  margin-top: var(--spacing-lg);
}

.unbind-btn::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 107, 107, 0.1), transparent);
  transition: left 0.5s ease-out;
}

.unbind-btn:hover:not(:disabled) {
  transform: translateY(-3px);
  box-shadow: 0 8px 24px rgba(255, 107, 107, 0.25);
  border-color: rgba(255, 107, 107, 0.6);
  color: #ff5252;
}

.unbind-btn:hover:not(:disabled)::before {
  left: 100%;
}

.unbind-btn:active:not(:disabled) {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.2);
}

.unbind-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
  box-shadow: none;
}

/* 提示信息 */
.tips {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: var(--accent-cream);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color);
  box-shadow: 0 4px 12px rgba(139, 69, 19, 0.08);
  position: relative;
  overflow: hidden;
}

.tips::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
}

.tips p {
  font-size: 13px;
  color: var(--text-color-medium);
  line-height: 1.8;
  margin: 0 0 var(--spacing-xs) 0;
  padding-left: var(--spacing-sm);
  position: relative;
  z-index: 1;
}

.tips p:first-child {
  font-weight: 600;
  color: var(--primary-dark);
  margin-bottom: var(--spacing-sm);
}

/* 装饰元素 */
.bind-card-page::after {
  content: '';
  position: fixed;
  top: -100px;
  right: -100px;
  width: 300px;
  height: 300px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.08;
  border-radius: 50%;
  z-index: 0;
  animation: float 20s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-40px, 40px) rotate(120deg);
  }
  66% {
    transform: translate(40px, -20px) rotate(240deg);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .content {
    padding: var(--spacing-md);
  }

  .header {
    height: 56px;
    padding: 0 var(--spacing-md);
  }

  .header .title {
    font-size: 18px;
  }

  .card-bg {
    height: 180px;
    padding: var(--spacing-md);
  }

  .brand {
    font-size: 20px;
  }

  .card-number {
    font-size: 24px;
    letter-spacing: 2px;
  }

  .action-btn,
  .unbind-btn {
    height: 48px;
    font-size: 15px;
  }
}

@media (max-width: 480px) {
  .content {
    padding: var(--spacing-sm);
  }

  .header {
    height: 52px;
    padding: 0 var(--spacing-sm);
  }

  .header .iconfont.icon-left {
    width: 40px;
    height: 40px;
    font-size: 20px;
  }

  .header .title {
    font-size: 17px;
  }

  .tab-item {
    padding: var(--spacing-sm) 0;
    font-size: 14px;
  }

  .card-bg {
    height: 160px;
  }

  .brand {
    font-size: 18px;
  }

  .card-number {
    font-size: 20px;
    letter-spacing: 1px;
  }

  .form-item {
    height: 56px;
  }

  .form-item label {
    width: 70px;
    font-size: 14px;
  }

  .form-item input {
    font-size: 14px;
  }
}

/* 自定义滚动条 */
.bind-card-page::-webkit-scrollbar {
  width: 8px;
}

.bind-card-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.bind-card-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.bind-card-page::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
}

/* 加载状态 */
.loading {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 3px solid var(--primary-light);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}
</style>