const { memberApi, authApi } = require('../../utils/api')

Page({
  data: {
    activeTab: 'bind',
    loading: false,
    hasCard: false,
    currentCardNumber: '',
    formattedCurrentCardNumber: '',
    cardNumber: '',
    formattedCardNumber: '',
    activeCode: '',
    applyForm: {
      name: '',
      phone: '',
      birthday: ''
    },
    isBindReady: false,
    isApplyReady: false
  },

  onLoad() {
    this.checkCardStatus()
  },

  async checkCardStatus() {
    try {
      const res = await authApi.getUserProfile()
      const user = res.data || res
      if (user && user.memberCardNo) {
        this.setData({
          hasCard: true,
          currentCardNumber: user.memberCardNo,
          formattedCurrentCardNumber: this.formatCardNumber(user.memberCardNo)
        })
      } else {
        this.setData({
          hasCard: false,
          currentCardNumber: '',
          formattedCurrentCardNumber: ''
        })
      }
    } catch (error) {
      console.error('获取卡状态失败', error)
    }
  },

  switchTab(e) {
    const { tab } = e.currentTarget.dataset
    this.setData({ activeTab: tab })
  },

  onCardNumberInput(e) {
    const val = e.detail.value
    this.setData({
      cardNumber: val,
      formattedCardNumber: this.formatCardNumber(val),
      isBindReady: val.replace(/\s/g, '').length >= 10 && this.data.activeCode.length >= 4
    })
  },

  onActiveCodeInput(e) {
    const val = e.detail.value
    this.setData({
      activeCode: val,
      isBindReady: this.data.cardNumber.replace(/\s/g, '').length >= 10 && val.length >= 4
    })
  },

  onNameInput(e) {
    this.setData({
      'applyForm.name': e.detail.value,
      isApplyReady: e.detail.value && /^1[3-9]\d{9}$/.test(this.data.applyForm.phone)
    })
  },

  onPhoneInput(e) {
    const val = e.detail.value
    this.setData({
      'applyForm.phone': val,
      isApplyReady: this.data.applyForm.name && /^1[3-9]\d{9}$/.test(val)
    })
  },

  onBirthdayChange(e) {
    this.setData({
      'applyForm.birthday': e.detail.value
    })
  },

  formatCardNumber(num) {
    if (!num) return ''
    return num.replace(/\s/g, '').replace(/(.{4})/g, '$1 ').trim()
  },

  async handleBind() {
    if (this.data.loading) return
    this.setData({ loading: true })
    try {
      await memberApi.bindCard(this.data.cardNumber)
      wx.showToast({ title: '会员卡绑定成功！', icon: 'success' })
      await this.checkCardStatus()
    } catch (error) {
      console.error('绑定失败:', error)
      wx.showToast({ title: error.message || '绑定失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  async handleUnbind() {
    wx.showModal({
      title: '解绑确认',
      content: '确定要解除会员卡绑定吗？解绑后将无法享受会员权益。',
      success: async (res) => {
        if (res.confirm) {
          this.setData({ loading: true })
          try {
            await memberApi.unbindCard()
            wx.showToast({ title: '已成功解绑', icon: 'success' })
            await this.checkCardStatus()
          } catch (error) {
            console.error('解绑失败:', error)
            wx.showToast({ title: error.message || '解绑失败', icon: 'none' })
          } finally {
            this.setData({ loading: false })
          }
        }
      }
    })
  },

  async handleApply() {
    if (this.data.loading) return
    this.setData({ loading: true })
    try {
      await memberApi.applyCard(this.data.applyForm)
      wx.showToast({ title: '电子会员卡申请成功！', icon: 'success' })
      await this.checkCardStatus()
    } catch (error) {
      console.error('申请失败:', error)
      wx.showToast({ title: error.message || '申请失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  }
})