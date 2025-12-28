const { giftCardApi } = require('../../utils/api.js');
const { formatDate } = require('../../utils/util.js');

Page({
  data: {
    tabs: ['我的礼品卡', '购买礼品卡', '激活礼品卡'],
    activeTab: 0,
    giftCards: [],
    priceOptions: [50, 100, 200, 500, 1000],
    selectedPrice: 100,
    buying: false,
    activating: false,
    cardNo: '',
    cardCode: ''
  },

  onLoad() {
    this.fetchGiftCards();
  },

  async fetchGiftCards() {
    try {
      const res = await giftCardApi.getMyGiftCards();
      const list = (res.data || res || []).map(card => ({
        ...card,
        statusText: this.getStatusText(card.status),
        expiryTime: formatDate(new Date(card.expiryTime))
      }));
      this.setData({ giftCards: list });
    } catch (error) {
      console.error('获取礼品卡失败', error);
    }
  },

  getStatusText(status) {
    const map = {
      'ACTIVE': '已激活',
      'UNUSED': '待激活',
      'USED': '已用完',
      'EXPIRED': '已过期'
    };
    return map[status] || status;
  },

  switchTab(e) {
    this.setData({ activeTab: e.currentTarget.dataset.index });
  },

  selectPrice(e) {
    this.setData({ selectedPrice: e.currentTarget.dataset.price });
  },

  onCardNoInput(e) {
    this.setData({ cardNo: e.detail.value });
  },

  onCardCodeInput(e) {
    this.setData({ cardCode: e.detail.value });
  },

  async handleBuy() {
    this.setData({ buying: true });
    try {
      await giftCardApi.buyGiftCard(this.data.selectedPrice);
      wx.showToast({ title: '购买成功' });
      this.setData({ activeTab: 0 });
      this.fetchGiftCards();
    } catch (error) {
      wx.showToast({ title: error.message || '购买失败', icon: 'none' });
    } finally {
      this.setData({ buying: false });
    }
  },

  async handleActivate() {
    if (!this.data.cardNo || !this.data.cardCode) {
      wx.showToast({ title: '请填写完整信息', icon: 'none' });
      return;
    }
    this.setData({ activating: true });
    try {
      await giftCardApi.activateGiftCard(this.data.cardNo, this.data.cardCode);
      wx.showToast({ title: '激活成功' });
      this.setData({ 
        cardNo: '', 
        cardCode: '',
        activeTab: 0 
      });
      this.fetchGiftCards();
    } catch (error) {
      wx.showToast({ title: error.message || '激活失败', icon: 'none' });
    } finally {
      this.setData({ activating: false });
    }
  }
});