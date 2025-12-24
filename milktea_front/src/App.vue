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
      <router-link to="/pickup" class="tab-item">
        <img :src="activePath === '/pickup' ? getAssetUrl('pick_up_food_active.png') : getAssetUrl('pick_up_food.png')" />
        <span>取餐</span>
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

const tabBarPages = ['/', '/order', '/pickup', '/user']
const showTabBar = computed(() => tabBarPages.includes(route.path))

const getAssetUrl = (name) => {
  return new URL(`./assets/images/icons/${name}`, import.meta.url).href
}
</script>

<style>
:root {
  --primary-color: #D4A574;
  --bg-color: #FFF9E6;
  --text-color: #8B7355;
}

body {
  margin: 0;
  background-color: var(--bg-color);
  color: var(--text-color);
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, Cantarell, 'Open Sans', 'Helvetica Neue', sans-serif;
}

.app-container {
  min-height: 100vh;
  box-sizing: border-box;
}

.app-container.has-tabbar {
  padding-bottom: 60px;
}

.tab-bar {
  position: fixed;
  bottom: 0;
  left: 0;
  right: 0;
  height: 60px;
  background-color: var(--bg-color);
  display: flex;
  border-top: 1px solid #eee;
  z-index: 100;
}

.tab-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-decoration: none;
  color: var(--text-color);
  font-size: 12px;
}

.tab-item img {
  width: 24px;
  height: 24px;
  margin-bottom: 4px;
}

.tab-item.router-link-active {
  color: var(--primary-color);
}
</style>
