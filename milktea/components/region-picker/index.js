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
];

Component({
  /**
   * 组件的属性列表
   */
  properties: {
    show: {
      type: Boolean,
      value: false
    },
    province: {
      type: String,
      value: ''
    },
    city: {
      type: String,
      value: ''
    },
    district: {
      type: String,
      value: ''
    }
  },

  /**
   * 组件的初始数据
   */
  data: {
    provinces: regionData,
    cities: [],
    districts: [],
    selectedProvince: '',
    selectedCity: '',
    selectedDistrict: ''
  },

  /**
   * 组件的方法列表
   */
  methods: {
    initData() {
      const provinceName = this.properties.province || regionData[0].name;
      const p = regionData.find(item => item.name === provinceName) || regionData[0];
      
      const cityName = this.properties.city || p.cities[0].name;
      const c = p.cities.find(item => item.name === cityName) || p.cities[0];
      
      const districtName = this.properties.district || c.districts[0];

      this.setData({
        cities: p.cities,
        districts: c.districts,
        selectedProvince: p.name,
        selectedCity: c.name,
        selectedDistrict: districtName
      });
    },

    onProvinceChange(e) {
      const p = e.currentTarget.dataset.province;
      this.setData({
        selectedProvince: p.name,
        cities: p.cities,
        selectedCity: p.cities[0].name,
        districts: p.cities[0].districts,
        selectedDistrict: p.cities[0].districts[0]
      });
    },

    onCityChange(e) {
      const c = e.currentTarget.dataset.city;
      this.setData({
        selectedCity: c.name,
        districts: c.districts,
        selectedDistrict: c.districts[0]
      });
    },

    onDistrictChange(e) {
      this.setData({
        selectedDistrict: e.currentTarget.dataset.district
      });
    },

    onCancel() {
      this.triggerEvent('cancel');
    },

    onConfirm() {
      this.triggerEvent('confirm', {
        province: this.data.selectedProvince,
        city: this.data.selectedCity,
        district: this.data.selectedDistrict
      });
    }
  },

  observers: {
    'show': function(show) {
      if (show) {
        this.initData();
      }
    }
  }
})