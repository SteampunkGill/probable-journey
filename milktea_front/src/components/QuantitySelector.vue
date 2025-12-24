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
.quantity-selector {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 4px;
  padding: 2px;
}

.btn-decrease,
.btn-increase {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  color: #333;
}

.btn-decrease.disabled,
.btn-increase.disabled {
  color: #ccc;
  cursor: not-allowed;
}

.input-quantity {
  width: 40px;
  height: 28px;
  text-align: center;
  font-size: 14px;
  border: none;
  background: transparent;
  color: #333;
  -moz-appearance: textfield;
}

.input-quantity::-webkit-outer-spin-button,
.input-quantity::-webkit-inner-spin-button {
  -webkit-appearance: none;
  margin: 0;
}

.iconfont {
  font-size: 14px;
}
</style>