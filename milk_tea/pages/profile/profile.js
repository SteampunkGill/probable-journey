const app = getApp();
const storage = require('../../utils/storage.js');
const { authApi, systemApi } = require('../../utils/api.js');

Page({
  data: {
    userInfo: null
  },

  onLoad() {
    this.loadUserInfo();
  },

  loadUserInfo() {
    const userInfo = storage.getUserInfo();
    if (userInfo) {
      this.setData({
        userInfo
      });
    }
  },

  onNicknameInput(e) {
    // 兼容微信 nickname 类型的 input
    // 微信 nickname 类型的 input 在 blur 时也会触发，且 e.detail.value 可能是最新的
    const nickname = e.detail.value;
    if (nickname !== undefined) {
      this.setData({
        'userInfo.nickname': nickname
      });
    }
  },

  onBirthdayChange(e) {
    this.setData({
      'userInfo.birthday': e.detail.value
    });
  },

  async onChooseAvatar(e) {
    // 微信小程序选择头像
    wx.chooseMedia({
      count: 1,
      mediaType: ['image'],
      sizeType: ['compressed'],
      sourceType: ['album', 'camera'],
      success: async (res) => {
        const tempFilePath = res.tempFiles[0].tempFilePath;
        wx.showLoading({ title: '上传中...' });
        try {
          // 调用上传接口
          const uploadRes = await systemApi.uploadImage(tempFilePath);
          this.setData({
            'userInfo.avatarUrl': uploadRes.url
          });
          wx.showToast({ title: '上传成功', icon: 'success' });
        } catch (error) {
          console.error('上传头像失败:', error);
          wx.showToast({ title: '上传失败', icon: 'none' });
        } finally {
          wx.hideLoading();
        }
      }
    });
  },

  async onSave() {
    const { userInfo } = this.data;
    wx.showLoading({ title: '保存中...' });
    
    try {
      // 构造发送给后端的 DTO，确保字段名匹配
      const updateData = {
        nickname: userInfo.nickname,
        avatarUrl: userInfo.avatarUrl,
        birthday: userInfo.birthday,
        gender: userInfo.gender
      };
      
      const updatedUser = await authApi.updateUserProfile(updateData);
      
      wx.showToast({
        title: '保存成功',
        icon: 'success'
      });
      
      // 合并更新后的数据，保留可能存在的其他字段（如手机号、等级等）
      // 注意：后端返回的字段名可能是 avatarUrl，而前端之前可能用的是 avatar
      const newUserInfo = {
        ...userInfo,
        ...updatedUser,
        avatarUrl: updatedUser.avatarUrl || updatedUser.avatar || userInfo.avatarUrl
      };
      
      // 使用后端返回的最新数据更新本地缓存
      storage.saveUserInfo(newUserInfo);
      app.globalData.userInfo = newUserInfo;
      
      this.setData({
        userInfo: newUserInfo
      });
      
      setTimeout(() => {
        wx.navigateBack();
      }, 1500);
    } catch (error) {
      console.error('保存用户信息失败:', error);
      wx.showToast({
        title: error.message || '保存失败',
        icon: 'none'
      });
    } finally {
      wx.hideLoading();
    }
  }
});