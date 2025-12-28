import { get, post, put, del } from '../../utils/request'

Page({
  data: {
    isEditMode: false,
    addressId: '',
    submitting: false,
    labelOptions: ['家', '公司', '学校'],
    formData: {
      name: '',
      phone: '',
      province: '',
      city: '',
      district: '',
      detail: '',
      isDefault: false,
      tag: '',
      label: ''
    }
  },

  onLoad(options) {
    if (options.id) {
      this.setData({
        addressId: options.id,
        isEditMode: true
      })
      wx.setNavigationBarTitle({ title: '编辑地址' })
      this.loadAddressDetail()
    } else {
      wx.setNavigationBarTitle({ title: '新增地址' })
    }
  },

  async loadAddressDetail() {
    try {
      const res = await get('/address/list')
      const list = (res.data || res) || []
      const address = list.find(addr => addr.id == this.data.addressId)
      if (address) {
        this.setData({
          formData: {
            ...address,
            label: address.tag || address.label || ''
          }
        })
      }
    } catch (error) {
      console.error('加载地址详情失败:', error)
    }
  },

  onInput(e) {
    const field = e.currentTarget.dataset.field
    const value = e.detail.value
    this.setData({
      [`formData.${field}`]: value
    })
  },

  onRegionChange(e) {
    const region = e.detail.value
    this.setData({
      'formData.province': region[0],
      'formData.city': region[1],
      'formData.district': region[2]
    })
  },

  selectLabel(e) {
    const label = e.currentTarget.dataset.label
    this.setData({
      'formData.label': label,
      'formData.tag': label
    })
  },

  onDefaultChange(e) {
    this.setData({
      'formData.isDefault': e.detail.value
    })
  },

  validateForm() {
    const { name, phone, province, detail } = this.data.formData
    if (!name.trim()) {
      wx.showToast({ title: '请输入收货人姓名', icon: 'none' })
      return false
    }
    if (!/^1[3-9]\d{9}$/.test(phone)) {
      wx.showToast({ title: '请输入正确的手机号', icon: 'none' })
      return false
    }
    if (!province) {
      wx.showToast({ title: '请选择所在地区', icon: 'none' })
      return false
    }
    if (!detail.trim()) {
      wx.showToast({ title: '请输入详细地址', icon: 'none' })
      return false
    }
    return true
  },

  async saveAddress() {
    if (this.data.submitting || !this.validateForm()) return

    this.setData({ submitting: true })
    try {
      const data = { ...this.data.formData }
      if (data.label) data.tag = data.label

      if (this.data.isEditMode) {
        await put(`/address/update/${this.data.addressId}`, data)
      } else {
        await post('/address/add', data)
      }

      wx.showToast({
        title: '保存成功',
        icon: 'success',
        success: () => {
          setTimeout(() => {
            wx.navigateBack()
          }, 1500)
        }
      })
    } catch (error) {
      console.error('保存失败:', error)
    } finally {
      this.setData({ submitting: false })
    }
  },

  async deleteAddress() {
    wx.showModal({
      title: '提示',
      content: '确定要删除该地址吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await del(`/address/delete/${this.data.addressId}`)
            wx.showToast({
              title: '删除成功',
              icon: 'success',
              success: () => {
                setTimeout(() => {
                  wx.navigateBack()
                }, 1500)
              }
            })
          } catch (error) {
            console.error('删除失败:', error)
          }
        }
      }
    })
  }
})