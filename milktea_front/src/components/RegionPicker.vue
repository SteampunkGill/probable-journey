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
.region-picker {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  z-index: 2000;
}

.picker-mask {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
}

.picker-content {
  position: absolute;
  left: 0;
  right: 0;
  bottom: 0;
  background: white;
  border-radius: 16px 16px 0 0;
  display: flex;
  flex-direction: column;
  height: 40vh;
}

.picker-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
}

.cancel { color: #999; cursor: pointer; }
.confirm { color: #D4A574; font-weight: bold; cursor: pointer; }
.title { font-weight: bold; }

.picker-body {
  flex: 1;
  display: flex;
  overflow: hidden;
}

.column {
  flex: 1;
  overflow-y: auto;
  border-right: 1px solid #f5f5f5;
}

.column:last-child { border-right: none; }

.item {
  padding: 12px 10px;
  text-align: center;
  font-size: 14px;
  color: #333;
  cursor: pointer;
}

.item.active {
  color: #D4A574;
  background: #FFF9E6;
  font-weight: bold;
}
</style>