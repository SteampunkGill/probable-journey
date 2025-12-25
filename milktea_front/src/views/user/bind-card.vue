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
.bind-card-page {
  min-height: 100vh;
  background: #F8F8F8;
}

.header {
  height: 44px;
  background: white;
  display: flex;
  align-items: center;
  padding: 0 15px;
  position: sticky;
  top: 0;
  z-index: 100;
}

.header .iconfont {
  font-size: 20px;
  color: #333;
}

.header .title {
  flex: 1;
  text-align: center;
  font-size: 17px;
  font-weight: bold;
  margin-right: 20px;
}

.content {
  padding: 20px 15px;
}

.tab-header {
  display: flex;
  background: #EEE;
  border-radius: 8px;
  padding: 4px;
  margin-bottom: 20px;
}

.tab-item {
  flex: 1;
  text-align: center;
  padding: 8px 0;
  font-size: 14px;
  color: #666;
  border-radius: 6px;
  transition: all 0.3s;
}

.tab-item.active {
  background: white;
  color: #D4A574;
  font-weight: bold;
}

.card-preview {
  margin-bottom: 20px;
}

.card-bg {
  height: 180px;
  background: linear-gradient(135deg, #666, #333);
  border-radius: 15px;
  padding: 20px;
  color: #D4A574;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  box-shadow: 0 10px 20px rgba(0,0,0,0.1);
}

.card-bg.bound {
  background: linear-gradient(135deg, #444, #111);
  color: #FFD700;
}

.card-bg.apply {
  background: linear-gradient(135deg, #D4A574, #B38B5D);
  color: white;
}

.brand {
  font-size: 18px;
  font-weight: bold;
}

.card-type {
  font-size: 12px;
  opacity: 0.8;
  margin-left: 10px;
}

.card-number {
  font-size: 22px;
  font-family: 'Courier New', Courier, monospace;
  letter-spacing: 2px;
  text-align: center;
}

.card-number-placeholder {
  font-size: 18px;
  opacity: 0.5;
  text-align: center;
}

.card-status {
  text-align: right;
  font-size: 12px;
}

.bind-form {
  background: white;
  border-radius: 12px;
  padding: 0 15px;
  margin-bottom: 20px;
}

.form-item {
  display: flex;
  align-items: center;
  height: 55px;
  border-bottom: 1px solid #F5F5F5;
}

.form-item:last-child {
  border-bottom: none;
}

.form-item label {
  width: 70px;
  font-size: 15px;
  color: #333;
}

.form-item input {
  flex: 1;
  border: none;
  outline: none;
  font-size: 15px;
  color: #333;
  background: transparent;
}

.action-btn {
  width: 100%;
  height: 45px;
  background: #D4A574;
  color: white;
  border: none;
  border-radius: 22.5px;
  font-size: 16px;
  font-weight: bold;
  cursor: pointer;
}

.action-btn:disabled {
  background: #CCC;
}

.unbind-btn {
  width: 100%;
  height: 45px;
  background: white;
  color: #FF4D4F;
  border: 1px solid #FF4D4F;
  border-radius: 22.5px;
  font-size: 16px;
  margin-top: 20px;
  cursor: pointer;
}

.tips {
  margin-top: 30px;
  padding: 0 5px;
}

.tips p {
  font-size: 12px;
  color: #999;
  line-height: 1.8;
  margin: 0;
}
</style>