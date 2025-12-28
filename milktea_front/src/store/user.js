import { defineStore } from 'pinia'

export const useUserStore = defineStore('user', {
  state: () => {
    let userInfo = null
    try {
      const stored = localStorage.getItem('userInfo')
      if (stored && stored !== 'undefined' && stored !== 'null') {
        userInfo = JSON.parse(stored)
      }
    } catch (e) {
      console.error('Pinia state init error:', e)
    }
    
    const token = localStorage.getItem('token')
    let selectedStore = null
    try {
      const storedStore = localStorage.getItem('selectedStore')
      if (storedStore && storedStore !== 'undefined' && storedStore !== 'null') {
        selectedStore = JSON.parse(storedStore)
      }
    } catch (e) {
      console.error('Pinia store selectedStore init error:', e)
    }

    return {
      userInfo,
      token: (token && token !== 'undefined' && token !== 'null') ? token : '',
      orderType: localStorage.getItem('orderMode') || 'pickup',
      selectedStore
    }
  },
  actions: {
    setUserInfo(info) {
      this.userInfo = info
      localStorage.setItem('userInfo', JSON.stringify(info))
    },
    setToken(token) {
      this.token = token
      localStorage.setItem('token', token)
    },
    setOrderType(type) {
      this.orderType = type
    },
    setSelectedStore(store) {
      this.selectedStore = store
      localStorage.setItem('selectedStore', JSON.stringify(store))
    },
    logout() {
      this.userInfo = null
      this.token = ''
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    }
  }
})