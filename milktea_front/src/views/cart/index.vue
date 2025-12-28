<template>
  <div class="cart-page">
    <!-- 购物车头部 -->
    <div class="cart-header">
      <span class="cart-title">购物车</span>
      <div class="cart-actions">
        <span class="edit-btn" @click="isEditing = !isEditing">
          {{ isEditing ? '完成' : '编辑' }}
        </span>
        <span class="clear-btn" @click="clearCart" v-if="!isEditing && cartItems.length > 0">
          清空
        </span>
      </div>
    </div>

    <!-- 购物车为空 -->
    <div class="empty-cart" v-if="cartItems.length === 0">
      <img src="../../assets/images/icons/cart.png" class="empty-icon" />
      <span class="empty-text">购物车还是空的哦~</span>
      <span class="empty-hint">去首页逛逛吧</span>
      <button class="btn btn-primary" @click="router.push('/')">
        去逛逛
      </button>
    </div>

    <!-- 购物车商品列表 -->
    <div class="cart-list" v-else>
      <!-- 全选操作 -->
      <div class="select-all-section" v-if="!isEditing">
        <div class="select-all-checkbox" @click="toggleSelectAll">
          <div class="checkbox" :class="{ checked: isAllSelected }">
            <i class="iconfont icon-check" v-if="isAllSelected"></i>
          </div>
          <span class="select-all-text">全选</span>
        </div>
        <div class="selected-info">
          <span>已选{{ selectedCount }}件</span>
        </div>
      </div>
<!-- 商品列表 -->
<div class="cart-items">
  <div class="cart-item" v-for="(item, index) in cartItems" :key="item.id">
    <!-- 选择框 -->
    <div
      class="item-checkbox"
      @click="toggleSelect(item)"
      v-if="!isEditing"
    >
      <div class="checkbox" :class="{ checked: item.selected }">
        <i class="iconfont icon-check" v-if="item.selected"></i>
      </div>
    </div>

    <!-- 商品信息 -->
    <div class="item-content">
      <img
        :src="item.image || item.product?.mainImageUrl || item.product?.imageUrl"
        class="item-image"
        @click="router.push(`/product/${item.id}`)"
      />
      
      <div class="item-info">
        <div class="item-header">
          <span class="item-name">{{ item.name }}</span>
          <div class="item-price">
            <span class="current-price">¥{{ item.price }}</span>
          </div>
        </div>
        
        <!-- 定制化信息 -->
        <div class="customization-info">
          <span v-if="item.specDescription">
            {{ item.specDescription }}
          </span>
        </div>
        
        <!-- 操作区域 -->
        <div class="item-actions">
          <div class="price-info" v-if="!isEditing">
            <span class="subtotal">小计: ¥{{ (item.price * item.quantity).toFixed(2) }}</span>
          </div>
          
          <div class="action-buttons" v-if="isEditing">
            <button class="btn btn-secondary delete-btn" @click="deleteItem(item)">
              删除
            </button>
          </div>
          
          <div class="quantity-selector" v-if="!isEditing">
            <div class="q-btn" @click="decreaseQuantity(item)">-</div>
            <span class="q-val">{{ item.quantity }}</span>
            <div class="q-btn" @click="increaseQuantity(item)">+</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>


      <!-- 优惠券 -->
      <div class="coupon-section card" v-if="!isEditing">
        <div class="section-header" @click="router.push('/coupon')">
          <span class="section-title">优惠券</span>
          <div class="coupon-info">
            <span class="available-coupons">{{ availableCouponCount }}张可用</span>
            <i class="iconfont icon-right"></i>
          </div>
        </div>
      </div>

      <!-- 备注 -->
      <div class="remark-section card" v-if="!isEditing">
        <div class="section-header">
          <span class="section-title">订单备注</span>
        </div>
        <textarea 
          class="remark-input" 
          placeholder="请输入备注信息（口味、偏好等）"
          v-model="remark"
          maxlength="100"
        ></textarea>
      </div>
    </div>

    <!-- 底部结算栏 -->
    <div class="bottom-settlement" v-if="cartItems.length > 0">
      <div class="settlement-left" v-if="!isEditing">
        <div class="total-amount">
          <span class="total-label">合计：</span>
          <span class="total-price">¥{{ totalAmount }}</span>
        </div>
      </div>
      
      <div class="settlement-right">
        <button 
          class="btn btn-secondary" 
          @click="deleteSelectedItems"
          v-if="isEditing"
        >
          删除选中
        </button>
        <button 
          class="btn btn-primary checkout-btn" 
          @click="goToCheckout"
          v-if="!isEditing"
          :disabled="selectedCount === 0"
        >
          去结算({{ selectedCount }})
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '../../store/cart'
import { couponApi } from '../../utils/api'

const router = useRouter()
const cartStore = useCartStore()

const isEditing = ref(false)
const remark = ref('')
const availableCouponCount = ref(0)

const cartItems = computed(() => cartStore.items)

const isAllSelected = computed({
  get: () => cartItems.value.length > 0 && cartItems.value.every(item => item.selected),
  set: (val) => {
    cartStore.toggleSelectAll(val)
  }
})

const selectedCount = computed(() => {
  return cartItems.value.filter(item => item.selected).reduce((total, item) => total + item.quantity, 0)
})

const totalAmount = computed(() => {
  return cartItems.value
    .filter(item => item.selected)
    .reduce((total, item) => total + item.price * item.quantity, 0)
    .toFixed(2)
})

const toggleSelectAll = () => {
  isAllSelected.value = !isAllSelected.value
}

const deleteItem = (item) => {
  if (confirm('确定删除该商品吗？')) {
    cartStore.removeFromCart(item.cartItemId || item.id)
  }
}

const deleteSelectedItems = () => {
  if (confirm('确定删除选中的商品吗？')) {
    cartStore.removeSelected()
  }
}

const clearCart = async () => {
  if (confirm('确定要清空购物车吗？')) {
    await cartStore.clearCart()
  }
}

const goToCheckout = () => {
  const selectedItems = cartItems.value.filter(item => item.selected)
  localStorage.setItem('checkoutItems', JSON.stringify(selectedItems))
  localStorage.setItem('orderRemark', remark.value)
  router.push('/order/checkout')
}

const toggleSelect = (item) => {
  cartStore.toggleSelect(item.cartItemId || item.id)
}

const decreaseQuantity = (item) => {
  if (item.quantity > 1) {
    cartStore.updateQuantity(item.cartItemId || item.id, item.quantity - 1)
  }
}

const increaseQuantity = (item) => {
  cartStore.updateQuantity(item.cartItemId || item.id, item.quantity + 1)
}

const loadAvailableCouponCount = async () => {
  try {
    const res = await couponApi.getMyCoupons()
    if (res.data) {
      // 统计可用优惠券数量
      const availableCoupons = res.data.filter(coupon => 
        coupon.status === 'AVAILABLE' || coupon.status === 'UNUSED'
      )
      availableCouponCount.value = availableCoupons.length
    }
  } catch (error) {
    console.error('获取优惠券数量失败:', error)
  }
}

onMounted(() => {
  cartStore.fetchCart()
  loadAvailableCouponCount()
})
</script>
<style scoped>
.cart-page {
  min-height: 100vh;
  background: var(--background-color, #f5f0e1);
  padding-bottom: 120px;
}

.cart-header {
  background: rgba(255, 255, 255, 0.95);
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
  backdrop-filter: blur(10px);
  border-bottom: 1px solid var(--border-color, #d4c7b5);
  border-radius: 0 0 20px 20px;
}

.cart-title {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color, #a0522d);
  font-family: 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
}

.cart-actions {
  display: flex;
  gap: 20px;
  font-size: 16px;
  color: var(--primary-color, #a0522d);
  font-family: 'Nunito', sans-serif;
}

.edit-btn, .clear-btn {
  cursor: pointer;
  padding: 8px 16px;
  border-radius: 20px;
  background: var(--surface-color, #e8dccb);
  transition: all 0.3s ease;
}

.edit-btn:hover, .clear-btn:hover {
  background: var(--primary-light, #d2b48c);
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
}

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 120px;
}

.empty-icon {
  width: 160px;
  height: 160px;
  opacity: 0.6;
  margin-bottom: 24px;
  filter: drop-shadow(0 4px 8px rgba(160, 82, 45, 0.1));
}

.empty-text {
  font-size: 20px;
  color: var(--text-color-dark, #4a3b30);
  margin-bottom: 12px;
  font-family: 'Noto Serif KR', serif;
  font-weight: 500;
}

.empty-hint {
  font-size: 15px;
  color: var(--text-color-medium, #7a6a5b);
  margin-bottom: 36px;
  font-family: 'Nunito', sans-serif;
}

.select-all-section {
  background: var(--surface-color, #e8dccb);
  padding: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 16px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.08);
}

.select-all-checkbox {
  display: flex;
  align-items: center;
  gap: 12px;
  cursor: pointer;
}

.checkbox {
  width: 28px;
  height: 28px;
  border: 2px solid var(--primary-color, #a0522d);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  background: white;
}

.checkbox.checked {
  background: var(--primary-color, #a0522d);
  border-color: var(--primary-color, #a0522d);
  color: white;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.3);
}

.select-all-text {
  font-size: 16px;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
}

.selected-info {
  font-size: 14px;
  color: var(--accent-brown, #deb887);
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
}

.cart-items {
  margin: 0 16px 20px;
}

.cart-item {
  background: white;
  padding: 20px;
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 12px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.08);
  border: 1px solid var(--border-color, #d4c7b5);
  transition: all 0.3s ease;
}

.cart-item:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.15);
}

.item-checkbox {
  cursor: pointer;
}

.item-content {
  flex: 1;
  display: flex;
  gap: 20px;
  align-items: center;
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 16px;
  object-fit: cover;
  border: 2px solid var(--accent-cream, #fff8dc);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.1);
  cursor: pointer;
  transition: all 0.3s ease;
}

.item-image:hover {
  transform: scale(1.05);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.2);
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.item-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.item-name {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Noto Serif KR', serif;
  line-height: 1.4;
}

.current-price {
  font-size: 18px;
  color: var(--primary-color, #a0522d);
  font-weight: 700;
  font-family: 'Noto Serif KR', serif;
}

.customization-info {
  font-size: 14px;
  color: var(--accent-brown, #deb887);
  font-family: 'Nunito', sans-serif;
  padding: 8px 12px;
  background: var(--accent-cream, #fff8dc);
  border-radius: 12px;
  display: inline-block;
}

.item-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.subtotal {
  font-size: 16px;
  color: var(--primary-dark, #8b4513);
  font-weight: 700;
  font-family: 'Noto Serif KR', serif;
}

.quantity-selector {
  display: flex;
  align-items: center;
  border: 2px solid var(--primary-light, #d2b48c);
  border-radius: 25px;
  background: white;
  overflow: hidden;
  box-shadow: 0 2px 8px rgba(160, 82, 45, 0.1);
}

.q-btn {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  color: var(--primary-color, #a0522d);
  font-size: 18px;
  font-weight: 600;
  background: var(--accent-cream, #fff8dc);
  transition: all 0.3s ease;
}

.q-btn:hover {
  background: var(--primary-light, #d2b48c);
  color: white;
}

.q-val {
  width: 40px;
  text-align: center;
  font-size: 16px;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
}

.card {
  background: white;
  margin: 16px;
  padding: 20px;
  border-radius: 20px;
  box-shadow: 0 4px 15px rgba(160, 82, 45, 0.08);
  border: 1px solid var(--border-color, #d4c7b5);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  cursor: pointer;
  padding: 8px 0;
}

.section-title {
  font-size: 17px;
  font-weight: 600;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Noto Serif KR', serif;
}

.coupon-info {
  display: flex;
  align-items: center;
  gap: 8px;
  color: var(--accent-pink, #ffc0cb);
  font-size: 14px;
  font-family: 'Nunito', sans-serif;
  font-weight: 600;
}

.remark-input {
  width: 100%;
  height: 100px;
  background: var(--accent-cream, #fff8dc);
  border: 2px solid var(--primary-light, #d2b48c);
  border-radius: 16px;
  padding: 16px;
  margin-top: 12px;
  font-size: 15px;
  resize: none;
  box-sizing: border-box;
  color: var(--text-color-dark, #4a3b30);
  font-family: 'Nunito', sans-serif;
  line-height: 1.6;
  transition: all 0.3s ease;
}

.remark-input:focus {
  outline: none;
  border-color: var(--primary-color, #a0522d);
  box-shadow: 0 0 0 4px rgba(160, 82, 45, 0.2);
}

.remark-input::placeholder {
  color: var(--text-color-light, #a09080);
}

.bottom-settlement {
  position: fixed;
  bottom: 60px; /* 向上移动，避开底部导航栏 */
  left: 16px;
  right: 16px;
  background: rgba(255, 255, 255, 0.95);
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  box-shadow: 0 8px 32px rgba(160, 82, 45, 0.15);
  z-index: 100;
  backdrop-filter: blur(10px);
  border: 1px solid var(--border-color, #d4c7b5);
  border-radius: 24px; /* 改成悬浮圆角卡片样式 */
}

.settlement-left {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.total-label {
  font-size: 15px;
  color: var(--text-color-medium, #7a6a5b);
  font-family: 'Nunito', sans-serif;
}

.total-price {
  font-size: 28px;
  font-weight: 700;
  color: var(--primary-color, #a0522d);
  font-family: 'Noto Serif KR', serif;
}

.btn {
  border: none;
  border-radius: 25px;
  padding: 14px 32px;
  font-weight: 600;
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-size: 16px;
  letter-spacing: 0.03em;
  transition: all 0.3s ease;
}

.btn-primary {
  background: linear-gradient(135deg, var(--primary-color, #a0522d) 0%, var(--primary-dark, #8b4513) 100%);
  color: white;
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.btn-primary:hover:not(:disabled) {
  transform: translateY(-3px) scale(1.02);
  box-shadow: 0 8px 25px rgba(160, 82, 45, 0.4);
}

.btn-primary:active:not(:disabled) {
  transform: translateY(0) scale(0.98);
}

.btn-secondary {
  background: linear-gradient(135deg, var(--accent-pink, #ffc0cb) 0%, rgba(255, 192, 203, 0.8) 100%);
  color: var(--primary-dark, #8b4513);
  border: 2px solid var(--accent-pink, #ffc0cb);
}

.btn-secondary:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 15px rgba(255, 192, 203, 0.3);
}

.checkout-btn:disabled {
  background: linear-gradient(135deg, #e8dccb 0%, #d4c7b5 100%);
  color: var(--text-color-light, #a09080);
  cursor: not-allowed;
  box-shadow: none;
}

.checkout-btn:disabled:hover {
  transform: none;
  box-shadow: none;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.delete-btn {
  padding: 10px 24px;
  font-size: 14px;
  background: linear-gradient(135deg, #ff6b6b 0%, #ff8e8e 100%);
  color: white;
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.delete-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(255, 107, 107, 0.3);
}

.iconfont {
  font-size: 14px;
}

.icon-right {
  color: var(--primary-color, #a0522d);
}

.icon-check {
  font-size: 16px;
  font-weight: bold;
}
</style>