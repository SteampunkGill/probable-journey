<template>
  <div class="region-picker" v-if="modelValue">
    <div class="picker-mask" @click="$emit('update:modelValue', false)"></div>
    <div class="picker-content">
      <div class="picker-header">
        <span class="cancel" @click="$emit('update:modelValue', false)">取消</span>
        <span class="title">选择地区</span>
        <span class="confirm" @click="handleConfirm">确定</span>
      </div>
      <div class="picker-body">
        <div class="column">
          <div 
            v-for="(p, i) in provinces" 
            :key="i" 
            class="item" 
            :class="{ active: selectedProvince === p.name }"
            @click="onProvinceChange(p)"
          >
            {{ p.name }}
          </div>
        </div>
        <div class="column">
          <div 
            v-for="(c, i) in cities" 
            :key="i" 
            class="item" 
            :class="{ active: selectedCity === c.name }"
            @click="onCityChange(c)"
          >
            {{ c.name }}
          </div>
        </div>
        <div class="column">
          <div 
            v-for="(d, i) in districts" 
            :key="i" 
            class="item" 
            :class="{ active: selectedDistrict === d }"
            @click="selectedDistrict = d"
          >
            {{ d }}
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  modelValue: Boolean,
  province: String,
  city: String,
  district: String
})

const emit = defineEmits(['update:modelValue', 'confirm'])

// 简化的地区数据
const regionData = [
  {
    name: '广东省',
    cities: [
      { name: '深圳市', districts: ['南山区', '福田区', '罗湖区', '宝安区', '龙岗区', '龙华区'] },
      { name: '广州市', districts: ['天河区', '越秀区', '海珠区', '荔湾区', '白云区', '番禺区'] },
      { name: '珠海市', districts: ['香洲区', '斗门区', '金湾区'] }
    ]
  },
  {
    name: '北京市',
    cities: [
      { name: '北京市', districts: ['东城区', '西城区', '朝阳区', '丰台区', '石景山区', '海淀区'] }
    ]
  },
  {
    name: '上海市',
    cities: [
      { name: '上海市', districts: ['黄浦区', '徐汇区', '长宁区', '静安区', '普陀区', '虹口区', '杨浦区'] }
    ]
  },
  {
    name: '浙江省',
    cities: [
      { name: '杭州市', districts: ['上城区', '拱墅区', '西湖区', '滨江区', '萧山区', '余杭区'] },
      { name: '宁波市', districts: ['海曙区', '江北区', '北仑区', '镇海区', '鄞州区'] }
    ]
  }
]

const provinces = ref(regionData)
const cities = ref([])
const districts = ref([])

const selectedProvince = ref(props.province || '')
const selectedCity = ref(props.city || '')
const selectedDistrict = ref(props.district || '')

watch(() => props.modelValue, (val) => {
  if (val) {
    selectedProvince.value = props.province || ''
    selectedCity.value = props.city || ''
    selectedDistrict.value = props.district || ''
    
    const p = regionData.find(p => p.name === selectedProvince.value)
    if (p) {
      cities.value = p.cities
      const c = p.cities.find(c => c.name === selectedCity.value)
      if (c) {
        districts.value = c.districts
      }
    } else {
      cities.value = []
      districts.value = []
    }
  }
})

const onProvinceChange = (p) => {
  selectedProvince.value = p.name
  cities.value = p.cities
  selectedCity.value = p.cities[0].name
  districts.value = p.cities[0].districts
  selectedDistrict.value = p.cities[0].districts[0]
}

const onCityChange = (c) => {
  selectedCity.value = c.name
  districts.value = c.districts
  selectedDistrict.value = c.districts[0]
}

const handleConfirm = () => {
  if (!selectedProvince.value || !selectedCity.value || !selectedDistrict.value) {
    alert('请选择完整的省市区')
    return
  }
  emit('confirm', {
    province: selectedProvince.value,
    city: selectedCity.value,
    district: selectedDistrict.value
  })
  emit('update:modelValue', false)
}
</script>
<style scoped>
/* 饮饮茶 (SipSipTea) - 奶茶主题地区选择器样式 */

.region-picker {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000;
}

/* 遮罩层 */
.picker-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(var(--primary-color-rgb), 0.12);
  backdrop-filter: blur(2px);
  animation: fadeIn 0.3s ease-out;
}

/* 内容区域 */
.picker-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(to bottom, var(--accent-cream) 0%, var(--surface-color) 100%);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
  display: flex;
  flex-direction: column;
  height: 50vh;
  box-shadow: 0 -8px 30px rgba(var(--primary-color-rgb), 0.08);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

/* 头部区域 */
.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md);
  border-bottom: 1px solid var(--border-color);
  background: rgba(var(--surface-color-rgb), 0.9);
  backdrop-filter: blur(5px);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}

/* 按钮样式 */
.cancel,
.confirm {
  font-size: 16px;
  cursor: pointer;
  transition: all 0.3s ease-out;
  padding: 8px 12px;
  border-radius: var(--border-radius-md);
  font-weight: 500;
}

.cancel {
  color: var(--text-color-medium);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
}

.cancel:hover {
  color: var(--primary-color);
  background: rgba(var(--primary-color-rgb), 0.08);
}

.confirm {
  color: var(--primary-color);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  font-weight: 600;
}

.confirm:hover {
  background: rgba(var(--primary-color-rgb), 0.08);
  transform: translateY(-1px);
}

.confirm:active {
  transform: translateY(0);
}

/* 标题 */
.title {
  font-size: 18px;
  font-weight: 600;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  color: var(--primary-color);
  letter-spacing: 0.08em;
}

/* 主体区域 */
.picker-body {
  flex: 1;
  display: flex;
  overflow: hidden;
  background: rgba(var(--surface-color-rgb), 0.8);
}

/* 列样式 */
.column {
  flex: 1;
  overflow-y: auto;
  border-right: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.85);
  backdrop-filter: blur(4px);
  position: relative;
}

.column:last-child {
  border-right: none;
}

/* 滚动条样式 */
.column::-webkit-scrollbar {
  width: 4px;
}

.column::-webkit-scrollbar-track {
  background: rgba(var(--border-color-rgb), 0.3);
  border-radius: var(--border-radius-sm);
}

.column::-webkit-scrollbar-thumb {
  background: var(--primary-light);
  border-radius: var(--border-radius-sm);
}

.column::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}

/* 项目样式 */
.item {
  padding: var(--spacing-sm) var(--spacing-xs);
  text-align: center;
  font-size: 15px;
  color: var(--text-color-medium);
  cursor: pointer;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.03em;
  transition: all 0.3s ease-out;
  position: relative;
  border-bottom: 1px solid rgba(var(--border-color-rgb), 0.3);
}

.item:hover {
  background: rgba(var(--primary-color-rgb), 0.05);
  color: var(--primary-color);
  transform: translateX(2px);
}

.item.active {
  color: var(--primary-color);
  background: linear-gradient(135deg, rgba(var(--primary-color-rgb), 0.08) 0%, rgba(var(--primary-dark-rgb), 0.04) 100%);
  font-weight: 600;
  font-family: 'Prompt', 'Noto Serif KR', serif;
  position: relative;
}

.item.active::before {
  content: '';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 60%;
  background: linear-gradient(to bottom, var(--primary-color) 0%, var(--primary-dark) 100%);
  border-radius: 0 var(--border-radius-sm) var(--border-radius-sm) 0;
  animation: slideIn 0.3s ease-out;
}

/* 选中指示器 */
.item.active::after {
  content: '✓';
  position: absolute;
  right: 12px;
  top: 50%;
  transform: translateY(-50%);
  color: var(--primary-color);
  font-size: 14px;
  animation: checkPop 0.3s ease-out;
}

/* 列之间的分隔线装饰 */
.column::before {
  content: '';
  position: absolute;
  top: 0;
  right: 0;
  bottom: 0;
  width: 1px;
  background: linear-gradient(to bottom,
  transparent,
  var(--border-color),
  transparent
  );
}

.column:last-child::before {
  display: none;
}

/* 动画 */
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(100%);
  }
  to {
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    transform: translateY(-50%) scaleX(0);
  }
  to {
    transform: translateY(-50%) scaleX(1);
  }
}

@keyframes checkPop {
  0% {
    transform: translateY(-50%) scale(0);
  }
  50% {
    transform: translateY(-50%) scale(1.2);
  }
  100% {
    transform: translateY(-50%) scale(1);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .picker-content {
    height: 60vh;
    border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
  }

  .picker-header {
    padding: var(--spacing-sm);
  }

  .title {
    font-size: 16px;
  }

  .cancel,
  .confirm {
    font-size: 15px;
    padding: 6px 10px;
  }

  .item {
    padding: 12px 8px;
    font-size: 14px;
  }
}

/* 奶茶主题装饰 */
.picker-content::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg,
  var(--primary-color),
  var(--accent-pink),
  var(--primary-color)
  );
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
  opacity: 0.3;
}

/* 列顶部的渐变装饰 */
.column::after {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg,
  transparent,
  rgba(var(--primary-color-rgb), 0.2),
  transparent
  );
}
</style>