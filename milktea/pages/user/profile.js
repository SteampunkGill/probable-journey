const { authApi, commonApi } = require('../../utils/api')
const util = require('../../utils/util')

Page({
  data: {
    userInfo: null,
    registrationTime: '-',
    loading: false,
    uploading: false,
    form: {
      nickname: '',
      avatarUrl: '',
      gender: 0,
      birthday: ''
    }
  },

  onLoad() {
    this.loadProfile()
  },

  async loadProfile() {
    try {
      const res = await authApi.getUserProfile()
      const userData = res.data || res
      if (userData) {
        this.setData({
          userInfo: userData,
          registrationTime: this.formatDate(userData.createdAt || userData.registrationTime),
          form: {
            nickname: userData.nickname || userData.username || '',
            avatarUrl: userData.avatarUrl || userData.avatar || '',
            gender: userData.gender !== undefined ? userData.gender : 0,
            birthday: userData.birthday || ''
          }
        })
      }
    } catch (error) {
      console.error('获取个人资料失败:', error)
    }
  },

  formatDate(dateStr) {
    if (!dateStr) return '-'
    try {
      const date = new Date(dateStr)
      if (isNaN(date.getTime())) return dateStr
      return `${date.getFullYear()}-${String(date.getMonth() + 1).padStart(2, '0')}-${String(date.getDate()).padStart(2, '0')}`
    } catch (e) {
      return dateStr
    }
  },

  handleAvatarClick() {
    wx.chooseImage({
      count: 1,
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePath = res.tempFilePaths[0]
        this.uploadAvatar(tempFilePath)
      }
    })
  },

  async uploadAvatar(filePath) {
    this.setData({ uploading: true })
    try {
      const res = await commonApi.uploadImage(filePath, 'avatar')
      // 兼容不同返回格式
      const url = res.url || (res.data && res.data.url) || res
      if (typeof url === 'string') {
        this.setData({
          'form.avatarUrl': url
        })
      }
    } catch (error) {
      console.error('上传头像失败:', error)
      wx.showToast({ title: '上传失败', icon: 'none' })
    } finally {
      this.setData({ uploading: false })
    }
  },

  onNicknameInput(e) {
    this.setData({ 'form.nickname': e.detail.value })
  },

  onGenderChange(e) {
    this.setData({ 'form.gender': parseInt(e.detail.value) })
  },

  onBirthdayChange(e) {
    this.setData({ 'form.birthday': e.detail.value })
  },

  async handleSave() {
    if (!this.data.form.nickname.trim()) {
      wx.showToast({ title: '昵称不能为空', icon: 'none' })
      return
    }

    this.setData({ loading: true })
    try {
      const res = await authApi.updateUserProfile(this.data.form)
      if (res.code === 200 || res.status === 'success') {
        wx.showToast({ title: '保存成功', icon: 'success' })
        setTimeout(() => wx.navigateBack(), 1500)
      } else {
        wx.showToast({ title: res.message || '保存失败', icon: 'none' })
      }
    } catch (error) {
      console.error('更新个人资料失败:', error)
      wx.showToast({ title: '系统繁忙', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  }
})