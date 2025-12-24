<template>
  <div class="admin-layout">
    <div class="sidebar">
      <div class="logo">奶茶店管理系统</div>
      <nav class="menu">
        <router-link v-for="item in menuItems" :key="item.path" :to="item.path" class="menu-item">
          <i class="iconfont" :class="item.icon"></i>
          <span>{{ item.name }}</span>
        </router-link>
      </nav>
    </div>
    <div class="main-container">
      <header class="admin-header">
        <div class="breadcrumb">{{ currentTitle }}</div>
        <div class="user-info">
          <span>管理员</span>
          <button @click="logout">退出</button>
        </div>
      </header>
      <main class="admin-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

const menuItems = [
  { name: '仪表盘', path: '/admin/dashboard', icon: 'icon-dashboard' },
  { name: '商品管理', path: '/admin/product', icon: 'icon-product' },
  { name: '订单管理', path: '/admin/order', icon: 'icon-order' },
  { name: '会员管理', path: '/admin/member', icon: 'icon-user' },
  { name: '营销管理', path: '/admin/marketing', icon: 'icon-marketing' },
  { name: '系统管理', path: '/admin/system', icon: 'icon-settings' }
]

const currentTitle = computed(() => route.meta.title || '管理后台')

const logout = () => {
  router.push('/login')
}
</script>

<style scoped>
.admin-layout {
  display: flex;
  height: 100vh;
  background: #f0f2f5;
}

.sidebar {
  width: 240px;
  background: #001529;
  color: white;
  display: flex;
  flex-direction: column;
}

.logo {
  height: 64px;
  line-height: 64px;
  text-align: center;
  font-size: 18px;
  font-weight: bold;
  background: #002140;
}

.menu {
  flex: 1;
  padding: 16px 0;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: 12px 24px;
  color: rgba(255, 255, 255, 0.65);
  text-decoration: none;
  transition: all 0.3s;
}

.menu-item:hover, .menu-item.router-link-active {
  color: white;
  background: #1890ff;
}

.menu-item i {
  margin-right: 12px;
}

.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

.admin-header {
  height: 64px;
  background: white;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 24px;
  box-shadow: 0 1px 4px rgba(0,21,41,.08);
}

.admin-content {
  flex: 1;
  padding: 24px;
  overflow-y: auto;
}
</style>