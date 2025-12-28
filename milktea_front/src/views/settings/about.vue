<template>
  <div class="about-page">
    <div class="header">
      <i class="iconfont icon-left" @click="router.back()"></i>
      <span class="title">关于我们</span>
    </div>

    <div class="content card">
      <div class="logo-section">
        <img src="@/assets/images/icons/user.png" class="logo" />
        <h1 class="brand-name">饮饮茶 SipSipTea</h1>
        <p class="version">Version 1.0.0</p>
      </div>

      <div class="intro-section">
        <p>饮饮茶（SipSipTea）致力于为您提供最纯正、最美味的奶茶体验。我们精选优质茶叶，搭配新鲜牛奶，每一杯都承载着我们的匠心与热情。</p>
      </div>

      <div class="info-list">
        <div class="info-item">
          <span class="label">官方网站</span>
          <span class="value">www.sipsiptea.com</span>
        </div>
        <div class="info-item">
          <span class="label">客服电话</span>
          <span class="value">400-123-4567</span>
        </div>
        <div class="info-item">
          <span class="label">商务合作</span>
          <span class="value">bd@sipsiptea.com</span>
        </div>
      </div>
    </div>

    <div class="footer-text">
      <p>© 2025 饮饮茶 SipSipTea 版权所有</p>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
</script>

<style scoped>
.about-page {
  min-height: 100vh;
  background: var(--background-color);
  padding: var(--spacing-md);
}

.header {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
  margin-bottom: var(--spacing-lg);
}

.iconfont.icon-left {
  font-size: 24px;
  color: var(--primary-color);
  cursor: pointer;
}

.title {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-dark);
}

.card {
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  box-shadow: 0 8px 32px rgba(139, 69, 19, 0.12);
  border: 1px solid var(--border-color);
}

.logo-section {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: var(--spacing-xl);
}

.logo {
  width: 80px;
  height: 80px;
  border-radius: 20px;
  margin-bottom: var(--spacing-md);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.brand-name {
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-dark);
  margin-bottom: var(--spacing-xs);
}

.version {
  font-size: 14px;
  color: var(--text-color-medium);
}

.intro-section {
  margin-bottom: var(--spacing-xl);
  line-height: 1.6;
  color: var(--text-color-dark);
  text-align: justify;
}

.info-list {
  border-top: 1px solid var(--border-color);
  padding-top: var(--spacing-lg);
}

.info-item {
  display: flex;
  justify-content: space-between;
  padding: var(--spacing-md) 0;
  font-size: 15px;
}

.info-item .label {
  color: var(--text-color-medium);
}

.info-item .value {
  color: var(--primary-color);
  font-weight: 500;
}

.footer-text {
  text-align: center;
  margin-top: var(--spacing-xl);
  font-size: 12px;
  color: var(--text-color-light);
}
</style>