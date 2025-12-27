<template>
  <div class="quantity-selector">
    <button 
      class="btn-decrease" 
      :class="{ disabled: disabled || value <= min }" 
      @click="decrease"
      :disabled="disabled || value <= min"
    >
      <i class="iconfont icon-minus"></i>
    </button>
    
    <input 
      type="number" 
      class="input-quantity" 
      v-model.number="internalValue" 
      :disabled="disabled || !editable"
      @input="onInput"
      @blur="onBlur"
    />
    
    <button 
      class="btn-increase" 
      :class="{ disabled: disabled || value >= max }" 
      @click="increase"
      :disabled="disabled || value >= max"
    >
      <i class="iconfont icon-add"></i>
    </button>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'

const props = defineProps({
  // 当前值
  value: {
    type: Number,
    default: 1
  },
  // 最小值
  min: {
    type: Number,
    default: 1
  },
  // 最大值
  max: {
    type: Number,
    default: 99
  },
  // 是否禁用
  disabled: {
    type: Boolean,
    default: false
  },
  // 输入框是否可编辑
  editable: {
    type: Boolean,
    default: true
  },
  // 步长
  step: {
    type: Number,
    default: 1
  }
})

const emit = defineEmits(['update:value', 'change', 'changeValue'])

const internalValue = ref(1)

const validateValue = (val) => {
  let validValue = parseInt(val) || props.min
  if (validValue < props.min) {
    validValue = props.min
  } else if (validValue > props.max) {
    validValue = props.max
  }
  return validValue
}

const decrease = () => {
  if (props.disabled) return
  const newValue = Math.max(props.min, props.value - props.step)
  if (newValue !== props.value) {
    updateValue(newValue)
  }
}

const increase = () => {
  if (props.disabled) return
  const newValue = Math.min(props.max, props.value + props.step)
  if (newValue !== props.value) {
    updateValue(newValue)
  }
}

const onInput = () => {
  if (props.disabled || !props.editable) return
  internalValue.value = validateValue(internalValue.value)
}

const onBlur = () => {
  if (props.disabled || !props.editable) return
  const validatedValue = validateValue(internalValue.value)
  if (validatedValue !== props.value) {
    updateValue(validatedValue)
  } else {
    internalValue.value = validatedValue
  }
}

const updateValue = (newValue) => {
  emit('update:value', newValue)
  emit('change', {
    value: newValue,
    oldValue: props.value
  })
  emit('changeValue', newValue)
}

watch(() => props.value, (newVal) => {
  internalValue.value = newVal
})

onMounted(() => {
  internalValue.value = validateValue(props.value)
})
</script>
<style scoped>
/* 饮饮茶 (SipSipTea) - 奶茶主题数量选择器样式 */

.quantity-selector {
  display: flex;
  align-items: center;
  background: linear-gradient(135deg, rgba(var(--primary-color-rgb), 0.05) 0%, rgba(var(--primary-dark-rgb), 0.03) 100%);
  border-radius: var(--border-radius-lg);
  padding: 4px;
  border: 2px solid var(--border-color);
  backdrop-filter: blur(2px);
  transition: all 0.3s ease-out;
  width: fit-content;
}

.quantity-selector:hover {
  border-color: var(--primary-light);
  box-shadow: 0 4px 12px rgba(var(--primary-color-rgb), 0.08);
}

/* 按钮基础样式 */
.btn-decrease,
.btn-increase {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(var(--surface-color-rgb), 0.9);
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  color: var(--text-color-dark);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  transition: all 0.3s ease-out;
  box-shadow: 0 2px 6px rgba(var(--primary-color-rgb), 0.05);
  position: relative;
  overflow: hidden;
}

.btn-decrease::before,
.btn-increase::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(var(--primary-color-rgb), 0.1), transparent);
  transition: left 0.5s ease-out;
}

.btn-decrease:hover:not(.disabled),
.btn-increase:hover:not(.disabled) {
  transform: scale(1.1);
  background: rgba(var(--surface-color-rgb), 0.95);
  box-shadow: 0 4px 12px rgba(var(--primary-color-rgb), 0.1);
}

.btn-decrease:hover:not(.disabled)::before,
.btn-increase:hover:not(.disabled)::before {
  left: 100%;
}

.btn-decrease:active:not(.disabled),
.btn-increase:active:not(.disabled) {
  transform: scale(0.95);
}

/* 禁用状态 */
.btn-decrease.disabled,
.btn-increase.disabled {
  color: var(--primary-light);
  cursor: not-allowed;
  background: rgba(var(--surface-color-rgb), 0.6);
  box-shadow: none;
  transform: none !important;
}

.btn-decrease.disabled::before,
.btn-increase.disabled::before {
  display: none;
}

/* 输入框样式 */
.input-quantity {
  width: 56px;
  height: 36px;
  text-align: center;
  font-size: 16px;
  border: none;
  background: transparent;
  color: var(--primary-color);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  font-weight: 600;
  letter-spacing: 0.05em;
  -moz-appearance: textfield;
  margin: 0 8px;
  transition: all 0.3s ease-out;
  border-radius: var(--border-radius-sm);
}

.input-quantity:focus {
  outline: none;
  background: rgba(var(--primary-color-rgb), 0.05);
  box-shadow: 0 0 0 3px rgba(var(--primary-color-rgb), 0.1);
}

.input-quantity:disabled {
  color: var(--text-color-light);
  background: transparent;
  cursor: not-allowed;
}

/* 隐藏数字输入框的上下箭头 */
.input-quantity::-webkit-outer-spin-button,
.input-quantity::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

/* 图标样式 */
.iconfont {
  font-size: 18px;
  transition: all 0.3s ease-out;
}

.btn-decrease:hover:not(.disabled) .iconfont,
.btn-increase:hover:not(.disabled) .iconfont {
  transform: scale(1.2);
}

/* 奶茶主题装饰效果 */
.quantity-selector::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 100%;
  height: 100%;
  background: radial-gradient(circle at center, rgba(var(--accent-pink-rgb), 0.03) 0%, transparent 70%);
  pointer-events: none;
  border-radius: inherit;
  z-index: -1;
}

/* 按钮特殊效果 */
.btn-decrease {
  background: linear-gradient(135deg, rgba(var(--surface-color-rgb), 0.9) 0%, rgba(var(--accent-cream-rgb), 0.8) 100%);
}

.btn-increase {
  background: linear-gradient(135deg, rgba(var(--surface-color-rgb), 0.9) 0%, rgba(var(--accent-cream-rgb), 0.8) 100%);
}

.btn-decrease:hover:not(.disabled) {
  background: linear-gradient(135deg, rgba(var(--surface-color-rgb), 0.95) 0%, rgba(var(--accent-cream-rgb), 0.9) 100%);
}

.btn-increase:hover:not(.disabled) {
  background: linear-gradient(135deg, rgba(var(--surface-color-rgb), 0.95) 0%, rgba(var(--accent-cream-rgb), 0.9) 100%);
}

/* 动画效果 */
@keyframes pulse {
  0% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
  100% {
    transform: scale(1);
  }
}

.btn-decrease:active:not(.disabled),
.btn-increase:active:not(.disabled) {
  animation: pulse 0.2s ease-out;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .quantity-selector {
    border-radius: var(--border-radius-md);
  }

  .btn-decrease,
  .btn-increase {
    width: 32px;
    height: 32px;
    border-radius: var(--border-radius-sm);
  }

  .input-quantity {
    width: 48px;
    height: 32px;
    font-size: 15px;
    margin: 0 6px;
  }

  .iconfont {
    font-size: 16px;
  }
}

/* 数量变化时的视觉反馈 */
.input-quantity {
  transition: transform 0.2s ease-out, color 0.3s ease-out;
}

.input-quantity:focus {
  transform: scale(1.02);
}

/* 按钮的奶茶主题颜色 */
.btn-decrease:not(.disabled) .iconfont {
  color: var(--primary-color);
}

.btn-increase:not(.disabled) .iconfont {
  color: var(--primary-color);
}

.btn-decrease.disabled .iconfont,
.btn-increase.disabled .iconfont {
  color: var(--border-color);
}

/* 输入框的珍珠效果 */
.input-quantity {
  position: relative;
}

.input-quantity::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  background: radial-gradient(circle, rgba(var(--accent-brown-rgb), 0.1) 0%, transparent 70%);
  border-radius: 50%;
  opacity: 0;
  transition: opacity 0.3s ease-out;
}

.input-quantity:focus::before {
  opacity: 1;
}
</style>