Component({
  properties: {
    title: {
      type: String,
      value: ''
    },
    showBack: {
      type: Boolean,
      value: true
    },
    background: {
      type: String,
      value: '#f5f0e1'
    },
    titleColor: {
      type: String,
      value: '#4a3b30'
    },
    placeholder: {
      type: Boolean,
      value: true
    }
  },
  data: {
    statusBarHeight: 20,
    navContentHeight: 44,
    navHeight: 64
  },
  lifetimes: {
    attached() {
      const systemInfo = wx.getSystemInfoSync();
      const menuButtonInfo = wx.getMenuButtonBoundingClientRect();
      
      const statusBarHeight = systemInfo.statusBarHeight;
      const navContentHeight = (menuButtonInfo.top - statusBarHeight) * 2 + menuButtonInfo.height;
      const navHeight = statusBarHeight + navContentHeight;
      
      this.setData({
        statusBarHeight,
        navContentHeight,
        navHeight
      });
    }
  },
  methods: {
    onBack() {
      const pages = getCurrentPages();
      if (pages.length > 1) {
        wx.navigateBack();
      } else {
        wx.switchTab({ url: '/pages/index/index' });
      }
    }
  }
});