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
    
    return {
      userInfo,
      token: localStorage.getItem('token') || '',
      orderType: 'pickup' // 'pickup' or 'delivery'
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
    logout() {
      this.userInfo = null
      this.token = ''
      localStorage.removeItem('userInfo')
      localStorage.removeItem('token')
    }
  }
})