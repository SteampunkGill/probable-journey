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
        :src="item.image"
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
  background: #F5F5F5;
  padding-bottom: 100px;
}

.cart-header {
  background: white;
  padding: 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: sticky;
  top: 0;
  z-index: 10;
}

.cart-title { font-size: 18px; font-weight: bold; }
.cart-actions { display: flex; gap: 15px; font-size: 14px; color: #666; }
.edit-btn, .clear-btn { cursor: pointer; }

.empty-cart {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding-top: 100px;
}

.empty-icon { width: 120px; height: 120px; opacity: 0.5; margin-bottom: 20px; }
.empty-text { font-size: 16px; color: #333; margin-bottom: 10px; }
.empty-hint { font-size: 13px; color: #999; margin-bottom: 30px; }

.select-all-section {
  background: white;
  padding: 12px 15px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.select-all-checkbox { display: flex; align-items: center; gap: 10px; cursor: pointer; }
.checkbox {
  width: 20px;
  height: 20px;
  border: 1px solid #DDD;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}
.checkbox.checked { background: #D4A574; border-color: #D4A574; color: white; }
.select-all-text { font-size: 14px; }
.selected-info { font-size: 12px; color: #999; }

.cart-items { margin-bottom: 15px; }
.cart-item {
  background: white;
  padding: 15px;
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 1px;
}

.item-checkbox { cursor: pointer; }
.item-content { flex: 1; display: flex; gap: 12px; }
.item-image { width: 80px; height: 80px; border-radius: 8px; object-fit: cover; }
.item-info { flex: 1; display: flex; flex-direction: column; justify-content: space-between; }
.item-header { display: flex; justify-content: space-between; }
.item-name { font-size: 15px; font-weight: bold; }
.current-price { font-size: 15px; color: #333; font-weight: bold; }
.customization-info { font-size: 11px; color: #999; margin: 5px 0; }

.item-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.subtotal { font-size: 12px; color: #D4A574; }

.quantity-selector {
  display: flex;
  align-items: center;
  border: 1px solid #EEE;
  border-radius: 4px;
}
.q-btn { width: 24px; height: 24px; display: flex; align-items: center; justify-content: center; cursor: pointer; }
.q-val { width: 30px; text-align: center; font-size: 13px; border-left: 1px solid #EEE; border-right: 1px solid #EEE; }

.card { background: white; margin: 10px; padding: 15px; border-radius: 8px; }
.section-header { display: flex; justify-content: space-between; align-items: center; cursor: pointer; }
.section-title { font-size: 14px; font-weight: bold; }
.coupon-info { display: flex; align-items: center; gap: 5px; color: #FF6B6B; font-size: 12px; }

.remark-input {
  width: 100%;
  height: 60px;
  background: #F8F8F8;
  border: none;
  border-radius: 4px;
  padding: 10px;
  margin-top: 10px;
  font-size: 13px;
  resize: none;
  box-sizing: border-box;
}

.bottom-settlement {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  background: white;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 15px;
  box-shadow: 0 -2px 10px rgba(0,0,0,0.05);
  z-index: 100;
}

.total-price { font-size: 20px; font-weight: bold; color: #D4A574; }
.btn { border: none; border-radius: 25px; padding: 10px 25px; font-weight: bold; cursor: pointer; }
.btn-primary { background: #D4A574; color: white; }
.btn-secondary { background: #FFF0F0; color: #FF6B6B; }
.checkout-btn:disabled { background: #CCC; cursor: not-allowed; }
</style>