const { giftCardApi } = require('../../utils/api')
const util = require('../../utils/util')

Page({
  data: {
    tabs: ['我的礼品卡', '购买礼品卡', '激活礼品卡'],
    activeTab: 0,
    giftCards: [],
    priceOptions: [50, 100, 200, 500, 1000],
    selectedPrice: 100,
    buying: false,
    activating: false,
    activateForm: {
      cardNo: '',
      cardCode: ''
    }
  },

  onLoad() {
    this.fetchGiftCards()
  },

  async fetchGiftCards() {
    try {
      const res = await giftCardApi.getMyGiftCards()
      const data = res.data || res
      if (Array.isArray(data)) {
        const giftCards = data.map(card => ({
          ...card,
          statusText: this.getStatusText(card.status),
          statusClass: card.status.toLowerCase(),
          expiryTimeText: util.formatDate(new Date(card.expiryTime))
        }))
        this.setData({ giftCards })
      }
    } catch (error) {
      console.error('获取礼品卡失败', error)
    }
  },

  switchTab(e) {
    const { index } = e.currentTarget.dataset
    this.setData({ activeTab: index })
  },

  selectPrice(e) {
    const { price } = e.currentTarget.dataset
    this.setData({ selectedPrice: price })
  },

  async handleBuy() {
    this.setData({ buying: true })
    try {
      await giftCardApi.buyGiftCard(this.data.selectedPrice)
      wx.showToast({ title: '购买成功', icon: 'success' })
      this.setData({ activeTab: 0 })
      this.fetchGiftCards()
    } catch (error) {
      console.error('购买失败', error)
      wx.showToast({ title: '购买失败', icon: 'none' })
    } finally {
      this.setData({ buying: false })
    }
  },

  onCardNoInput(e) {
    this.setData({ 'activateForm.cardNo': e.detail.value })
  },

  onCardCodeInput(e) {
    this.setData({ 'activateForm.cardCode': e.detail.value })
  },

  async handleActivate() {
    const { cardNo, cardCode } = this.data.activateForm
    if (!cardNo || !cardCode) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' })
      return
    }
    this.setData({ activating: true })
    try {
      await giftCardApi.activateGiftCard(cardNo, cardCode)
      wx.showToast({ title: '激活成功', icon: 'success' })
      this.setData({
        activateForm: { cardNo: '', cardCode: '' },
        activeTab: 0
      })
      this.fetchGiftCards()
    } catch (error) {
      console.error('激活失败', error)
      wx.showToast({ title: '激活失败', icon: 'none' })
    } finally {
      this.setData({ activating: false })
    }
  },

  getStatusText(status) {
    const map = {
      'ACTIVE': '已激活',
      'UNUSED': '待激活',
      'USED': '已用完',
      'EXPIRED': '已过期'
    }
    return map[status] || status
  }
})