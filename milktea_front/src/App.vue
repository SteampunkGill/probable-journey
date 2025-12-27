<template>
  <div class="app-container" :class="{ 'has-tabbar': showTabBar }">
    <router-view v-slot="{ Component }">
      <keep-alive>
        <component :is="Component" />
      </keep-alive>
    </router-view>
    
    <!-- TabBar 模拟 -->
    <div v-if="showTabBar" class="tab-bar">
      <router-link to="/" class="tab-item">
        <img :src="activePath === '/' ? getAssetUrl('home-active.png') : getAssetUrl('home.png')" />
        <span>首页</span>
      </router-link>
      <router-link to="/order" class="tab-item">
        <img :src="activePath === '/order' ? getAssetUrl('order-active.png') : getAssetUrl('order.png')" />
        <span>点单</span>
      </router-link>
      <router-link to="/order-list" class="tab-item">
        <img :src="activePath === '/order-list' ? getAssetUrl('order-active.png') : getAssetUrl('order.png')" />
        <span>订单</span>
      </router-link>
      <router-link to="/user" class="tab-item">
        <img :src="activePath === '/user' ? getAssetUrl('user-active.png') : getAssetUrl('user.png')" />
        <span>我的</span>
      </router-link>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const activePath = computed(() => route.path)

const tabBarPages = ['/', '/order', '/pickup', '/order-list', '/user']
const showTabBar = computed(() => tabBarPages.includes(route.path))

const getAssetUrl = (name) => {
  return new URL(`./assets/images/icons/${name}`, import.meta.url).href
}
</script>

<style>
/* 饮饮茶(SipSipTea) 奶茶主题 - 应用容器 */
:root {
  /* 色彩方案 */
  --background-color: #f5f0e1; /* 奶油色 */
  --surface-color: #e8dccb; /* 浅卡其色 */
  --primary-color: #a0522d; /* 焦糖色 */
  --primary-dark: #8b4513; /* 深咖啡色 */
  --primary-light: #d2b48c; /* 浅驼色 */
  --accent-cream: #fff8dc; /* 玉米色/奶油 */
  --accent-pink: #ffc0cb; /* 淡粉色 */
  --accent-brown: #deb887; /* 沙棕色 */
  --text-color-dark: #4a3b30; /* 深棕色 */
  --text-color-medium: #7a6a5b; /* 中棕色 */
  --text-color-light: #a09080; /* 浅咖色 */
  --border-color: #d4c7b5; /* 柔和浅棕色 */

  /* 圆角大小 */
  --border-radius-sm: 8px;
  --border-radius-md: 16px;
  --border-radius-lg: 24px;
  --border-radius-xl: 40px;

  /* 间距 */
  --spacing-xs: 8px;
  --spacing-sm: 12px;
  --spacing-md: 16px;
  --spacing-lg: 24px;
  --spacing-xl: 32px;

  /* 字体 */
  --font-heading: 'Noto Serif KR', 'Prompt', serif;
  --font-body: 'Noto Sans KR', 'Nunito', 'Quicksand', sans-serif;

  /* 阴影 */
  --shadow-sm: 0 2px 8px rgba(139, 69, 19, 0.08);
  --shadow-md: 0 4px 16px rgba(139, 69, 19, 0.12);
  --shadow-lg: 0 8px 32px rgba(139, 69, 19, 0.16);
  --shadow-xl: 0 12px 48px rgba(139, 69, 19, 0.2);
}

/* 全局样式 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  margin: 0;
  background-color: var(--background-color);
  color: var(--text-color-dark);
  font-family: var(--font-body);
  font-size: 15px;
  line-height: 1.6;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  overflow-x: hidden;
}

/* 应用容器 */
.app-container {
  min-height: 100vh;
  position: relative;
  background: var(--background-color);
  transition: padding-bottom 0.3s ease-out;
}

.app-container.has-tabbar {
  padding-bottom: 80px;
}

/* 背景装饰元素 */
.app-container::before {
  content: '';
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background:
      radial-gradient(circle at 10% 10%, rgba(255, 248, 220, 0.15) 0%, transparent 40%),
      radial-gradient(circle at 90% 90%, rgba(222, 184, 135, 0.15) 0%, transparent 40%);
  pointer-events: none;
  z-index: 0;
}

/* TabBar 样式 */
.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 80px;
  background: linear-gradient(to top, var(--surface-color), rgba(232, 220, 203, 0.95));
  display: flex;
  align-items: center;
  justify-content: space-around;
  border-top: 2px solid var(--border-color);
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
  box-shadow:
      0 -4px 20px rgba(139, 69, 19, 0.15),
      inset 0 1px 0 rgba(255, 255, 255, 0.6);
  z-index: 1000;
  backdrop-filter: blur(10px);
  padding: 0 var(--spacing-md);
}

.tab-bar::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color), var(--accent-brown));
  border-radius: var(--border-radius-xl) var(--border-radius-xl) 0 0;
}

/* TabBar 项目 */
.tab-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: var(--text-color-medium);
  font-size: 12px;
  font-weight: 600;
  padding: var(--spacing-sm);
  border-radius: var(--border-radius-lg);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
  position: relative;
  overflow: hidden;
  min-width: 64px;
  background: transparent;
}

.tab-item:hover {
  background: var(--accent-cream);
  transform: translateY(-4px);
  color: var(--primary-color);
}

.tab-item.router-link-active {
  color: var(--primary-color);
  background: var(--accent-cream);
  transform: translateY(-4px);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.15);
}

.tab-item.router-link-active::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color));
  border-radius: 2px;
}

/* TabBar 图标 */
.tab-item img {
  width: 28px;
  height: 28px;
  margin-bottom: var(--spacing-xs);
  transition: all 0.3s ease-out;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.tab-item:hover img {
  transform: scale(1.1) rotate(5deg);
}

.tab-item.router-link-active img {
  transform: scale(1.1);
  filter: drop-shadow(0 2px 4px rgba(160, 82, 45, 0.2));
}

/* TabBar 文字 */
.tab-item span {
  transition: all 0.3s ease-out;
  letter-spacing: 0.3px;
}

.tab-item:hover span {
  font-weight: 700;
}

.tab-item.router-link-active span {
  font-weight: 700;
  text-shadow: 0 1px 2px rgba(255, 255, 255, 0.5);
}

/* 页面过渡动画 */
.router-view {
  position: relative;
  z-index: 1;
}

.page-enter-active,
.page-leave-active {
  transition: all 0.3s ease-out;
}

.page-enter-from {
  opacity: 0;
  transform: translateY(20px);
}

.page-leave-to {
  opacity: 0;
  transform: translateY(-20px);
}

/* 装饰元素 */
.app-container::after {
  content: '';
  position: fixed;
  bottom: -150px;
  right: -150px;
  width: 400px;
  height: 400px;
  background: radial-gradient(circle, var(--accent-pink) 0%, transparent 70%);
  opacity: 0.08;
  border-radius: 50%;
  z-index: 0;
  animation: float 25s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) rotate(0deg);
  }
  33% {
    transform: translate(-50px, 30px) rotate(120deg);
  }
  66% {
    transform: translate(30px, -50px) rotate(240deg);
  }
}

/* 自定义滚动条 */
::-webkit-scrollbar {
  width: 10px;
  height: 10px;
}

::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: var(--border-radius-md);
}

::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: var(--border-radius-md);
  border: 2px solid var(--surface-color);
}

::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
}

/* 响应式设计 */
@media (max-width: 768px) {
  .app-container.has-tabbar {
    padding-bottom: 70px;
  }

  .tab-bar {
    height: 70px;
    border-radius: var(--border-radius-lg) var(--border-radius-lg) 0 0;
  }

  .tab-item {
    min-width: 56px;
    padding: var(--spacing-xs);
  }

  .tab-item img {
    width: 24px;
    height: 24px;
  }

  .tab-item span {
    font-size: 11px;
  }
}

@media (max-width: 480px) {
  .app-container.has-tabbar {
    padding-bottom: 60px;
  }

  .tab-bar {
    height: 60px;
    padding: 0 var(--spacing-sm);
  }

  .tab-item {
    min-width: 48px;
  }

  .tab-item img {
    width: 22px;
    height: 22px;
    margin-bottom: 2px;
  }

  .tab-item span {
    font-size: 10px;
  }
}

/* 安全区域适配 */
@supports (padding-bottom: env(safe-area-inset-bottom)) {
  .app-container.has-tabbar {
    padding-bottom: calc(80px + env(safe-area-inset-bottom));
  }

  .tab-bar {
    padding-bottom: env(safe-area-inset-bottom);
  }
}

/* 加载动画 */
.loading-spinner {
  display: inline-block;
  width: 24px;
  height: 24px;
  border: 3px solid var(--primary-light);
  border-top-color: var(--primary-color);
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

/* 全局按钮样式 */
button {
  font-family: var(--font-body);
  border: none;
  outline: none;
  cursor: pointer;
  transition: all 0.3s ease-out;
}

/* 全局输入框样式 */
input, textarea, select {
  font-family: var(--font-body);
  border: 1px solid var(--border-color);
  border-radius: var(--border-radius-md);
  padding: var(--spacing-sm);
  background: white;
  transition: all 0.3s ease-out;
}

input:focus, textarea:focus, select:focus {
  border-color: var(--primary-color);
  box-shadow: 0 0 0 3px rgba(160, 82, 45, 0.1);
  outline: none;
}

/* 全局卡片样式 */
.card {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  border: 1px solid var(--border-color);
  overflow: hidden;
}

/* 全局标题样式 */
h1, h2, h3, h4, h5, h6 {
  font-family: var(--font-heading);
  color: var(--primary-dark);
  font-weight: 700;
  line-height: 1.3;
}

h1 {
  font-size: 28px;
}

h2 {
  font-size: 24px;
}

h3 {
  font-size: 20px;
}

h4 {
  font-size: 18px;
}

/* 全局链接样式 */
a {
  color: var(--primary-color);
  text-decoration: none;
  transition: all 0.3s ease-out;
}

a:hover {
  color: var(--primary-dark);
  text-decoration: underline;
}
</style>
