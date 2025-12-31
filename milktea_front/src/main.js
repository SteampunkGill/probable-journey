import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import './style.css'
import { promotionDB } from './utils/db'

// DEMO ONLY: 初始化 IndexedDB 模拟数据
promotionDB.seed().catch(console.error)

const app = createApp(App)
const pinia = createPinia()

app.use(pinia)
app.use(router)

app.mount('#app')
