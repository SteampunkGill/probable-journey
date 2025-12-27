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
  { name: '门店管理', path: '/admin/store', icon: 'icon-store' },
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
  background: var(--background-color);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  color: var(--text-color-dark);
}

/* 侧边栏 */
.sidebar {
  width: 260px;
  background: linear-gradient(135deg, var(--primary-dark), var(--primary-color));
  color: white;
  display: flex;
  flex-direction: column;
  box-shadow: 4px 0 25px rgba(160, 82, 45, 0.15);
  border-right: 1px solid var(--border-color);
  z-index: 100;
}

.logo {
  height: 80px;
  line-height: 80px;
  text-align: center;
  font-size: 1.4em;
  font-weight: 600;
  background: rgba(139, 69, 19, 0.3);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  border-bottom: 1px solid rgba(255, 248, 220, 0.1);
  padding: 0 var(--spacing-lg);
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.logo::before {
  content: '🥤 ';
  font-size: 1.2em;
  margin-right: var(--spacing-sm);
}

/* 菜单导航 */
.menu {
  flex: 1;
  padding: var(--spacing-lg) 0;
  overflow-y: auto;
}

.menu-item {
  display: flex;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-xl);
  color: rgba(255, 248, 220, 0.8);
  text-decoration: none;
  transition: all 0.3s ease-out;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  border-left: 4px solid transparent;
  margin: var(--spacing-xs) var(--spacing-md);
  border-radius: var(--border-radius-md);
  position: relative;
  overflow: hidden;
}

.menu-item:hover {
  color: white;
  background: rgba(255, 248, 220, 0.15);
  transform: translateX(4px);
  border-left-color: var(--accent-cream);
}

.menu-item.router-link-active {
  color: white;
  background: linear-gradient(90deg, rgba(160, 82, 45, 0.3), rgba(139, 69, 19, 0.2));
  border-left-color: var(--accent-brown);
  font-weight: 600;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.menu-item.router-link-active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(to bottom, var(--accent-brown), var(--primary-light));
}

.menu-item i {
  margin-right: var(--spacing-md);
  font-size: 1.2em;
  width: 24px;
  text-align: center;
}

.menu-item span {
  flex: 1;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

/* 主容器 */
.main-container {
  flex: 1;
  display: flex;
  flex-direction: column;
  overflow: hidden;
  background: var(--background-color);
}

/* 顶部导航栏 */
.admin-header {
  height: 80px;
  background: var(--surface-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 var(--spacing-xl);
  box-shadow: 0 4px 20px rgba(160, 82, 45, 0.08);
  backdrop-filter: blur(4px);
  border-bottom: 1px solid var(--border-color);
  z-index: 50;
}

.breadcrumb {
  font-family: 'Prompt', 'Noto Serif KR', serif;
  font-size: 1.3em;
  font-weight: 600;
  color: var(--primary-dark);
  position: relative;
  padding-left: var(--spacing-lg);
}

.breadcrumb::before {
  content: '🍵';
  position: absolute;
  left: 0;
  top: 50%;
  transform: translateY(-50%);
  font-size: 1.1em;
}

/* 用户信息区域 */
.user-info {
  display: flex;
  align-items: center;
  gap: var(--spacing-lg);
  font-family: 'Nunito', sans-serif;
}

.user-info span {
  color: var(--text-color-dark);
  font-weight: 500;
  padding: var(--spacing-sm) var(--spacing-md);
  background: rgba(255, 255, 255, 0.5);
  border-radius: var(--border-radius-lg);
  border: 1px solid var(--border-color);
}

.user-info button {
  padding: var(--spacing-sm) var(--spacing-lg);
  border: none;
  border-radius: var(--border-radius-lg);
  cursor: pointer;
  font-family: 'Nunito', sans-serif;
  font-weight: 500;
  transition: all 0.25s ease-out;
  background: linear-gradient(135deg, var(--primary-color), var(--primary-dark));
  color: white;
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.user-info button:hover {
  transform: translateY(-2px) scale(1.05);
  box-shadow: 0 6px 20px rgba(160, 82, 45, 0.3);
}

.user-info button:active {
  transform: translateY(0) scale(0.98);
}

/* 主要内容区域 */
.admin-content {
  flex: 1;
  padding: var(--spacing-xl);
  overflow-y: auto;
  background: linear-gradient(135deg, rgba(245, 240, 225, 0.3), rgba(232, 220, 203, 0.2));
  position: relative;
}

/* 滚动条样式 */
.admin-content::-webkit-scrollbar {
  width: 8px;
}

.admin-content::-webkit-scrollbar-track {
  background: rgba(212, 199, 181, 0.2);
  border-radius: var(--border-radius-sm);
}

.admin-content::-webkit-scrollbar-thumb {
  background: var(--accent-brown);
  border-radius: var(--border-radius-sm);
  transition: all 0.3s ease-out;
}

.admin-content::-webkit-scrollbar-thumb:hover {
  background: var(--primary-color);
}

.menu::-webkit-scrollbar {
  width: 6px;
}

.menu::-webkit-scrollbar-track {
  background: rgba(139, 69, 19, 0.2);
}

.menu::-webkit-scrollbar-thumb {
  background: var(--accent-cream);
  border-radius: var(--border-radius-sm);
}

/* 动画效果 */
.menu-item {
  animation: slideIn 0.4s ease-out;
  animation-fill-mode: both;
}

.menu-item:nth-child(1) { animation-delay: 0.1s; }
.menu-item:nth-child(2) { animation-delay: 0.2s; }
.menu-item:nth-child(3) { animation-delay: 0.3s; }
.menu-item:nth-child(4) { animation-delay: 0.4s; }
.menu-item:nth-child(5) { animation-delay: 0.5s; }
.menu-item:nth-child(6) { animation-delay: 0.6s; }
.menu-item:nth-child(7) { animation-delay: 0.7s; }

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  .admin-layout {
    flex-direction: column;
  }

  .sidebar {
    width: 100%;
    height: 60px;
    flex-direction: row;
    box-shadow: 0 4px 20px rgba(160, 82, 45, 0.15);
  }

  .logo {
    height: 60px;
    line-height: 60px;
    width: 180px;
    padding: 0 var(--spacing-md);
    font-size: 1.1em;
  }

  .menu {
    flex: 1;
    padding: 0;
    display: flex;
    overflow-x: auto;
    overflow-y: hidden;
  }

  .menu-item {
    flex-direction: column;
    padding: var(--spacing-sm) var(--spacing-md);
    margin: var(--spacing-xs);
    border-left: none;
    border-bottom: 3px solid transparent;
    min-width: 80px;
  }

  .menu-item:hover {
    transform: translateY(-2px);
  }

  .menu-item.router-link-active {
    border-left: none;
    border-bottom-color: var(--accent-brown);
  }

  .menu-item.router-link-active::before {
    display: none;
  }

  .menu-item i {
    margin-right: 0;
    margin-bottom: var(--spacing-xs);
    font-size: 1.1em;
  }

  .menu-item span {
    font-size: 0.85em;
    text-align: center;
  }

  .admin-header {
    height: 60px;
    padding: 0 var(--spacing-md);
  }

  .breadcrumb {
    font-size: 1.1em;
    padding-left: var(--spacing-md);
  }

  .user-info {
    gap: var(--spacing-sm);
  }

  .user-info span {
    font-size: 0.9em;
    padding: var(--spacing-xs) var(--spacing-sm);
  }

  .user-info button {
    padding: var(--spacing-xs) var(--spacing-md);
    font-size: 0.9em;
  }

  .admin-content {
    padding: var(--spacing-md);
  }
}

/* 定义CSS变量 */
:root {
  /* 色彩方案 */
  --background-color: #f5f0e1;
  --surface-color: #e8dccb;
  --primary-color: #a0522d;
  --primary-dark: #8b4513;
  --primary-light: #d2b48c;
  --accent-cream: #fff8dc;
  --accent-pink: #ffc0cb;
  --accent-brown: #deb887;
  --text-color-dark: #4a3b30;
  --text-color-medium: #7a6a5b;
  --text-color-light: #a09080;
  --border-color: #d4c7b5;

  /* 圆角大小 */
  --border-radius-sm: 8px;
  --border-radius-md: 12px;
  --border-radius-lg: 20px;
  --border-radius-xl: 30px;

  /* 间距 */
  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;
}
</style>