<template>
  <div class="about-page">
    <div class="nav-header">
      <div class="back-icon" @click="$router.back()">←</div>
      <div class="title">关于我们</div>
    </div>

    <div class="about-content">
      <div class="brand-header">
        <div class="logo-box">
          <img :src="aboutInfo.logoUrl || defaultLogo" alt="Logo" class="logo">
        </div>
        <h1 class="brand-title">{{ aboutInfo.title }}</h1>
        <p class="brand-slogan">用心做好每一杯奶茶</p>
      </div>

      <div class="info-card">
        <div class="rich-text" v-html="aboutInfo.content"></div>
      </div>

      <div class="contact-list">
        <div class="contact-item" @click="handleCall">
          <div class="item-left">
            <span class="icon">📞</span>
            <span class="label">联系电话</span>
          </div>
          <div class="item-right">
            <span class="val">{{ aboutInfo.contactPhone }}</span>
            <span class="arrow">›</span>
          </div>
        </div>
        <div class="contact-item">
          <div class="item-left">
            <span class="icon">📍</span>
            <span class="label">门店地址</span>
          </div>
          <div class="item-right">
            <span class="val address-val">{{ aboutInfo.address }}</span>
          </div>
        </div>
      </div>

      <div class="page-footer">
        <p>© 2025 {{ aboutInfo.title }} 版权所有</p>
        <p>Version 1.0.0</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const defaultLogo = '/vite.svg' // 使用 public 目录下的默认 logo

// 纯前端静态数据
const aboutInfo = ref({
  title: '茶香溢',
  logoUrl: '',
  content: `茶香溢奶茶店成立于2023年，致力于为每一位顾客提供最纯正、最健康的奶茶体验。
  
我们精选来自全球优质产区的原叶茶，搭配每日新鲜配送的优质牛乳，坚持手工现调，拒绝添加奶精和人工色素。

在这里，每一杯奶茶都承载着我们的匠心与诚意。无论是经典的珍珠奶茶，还是清新的水果茶，我们都力求做到极致，让每一口都充满幸福的味道。

感谢您一直以来的支持与陪伴，我们将继续努力，为您带来更多惊喜。`,
  contactPhone: '400-123-4567',
  address: '北京市朝阳区幸福路88号'
})

const handleCall = () => {
  if (aboutInfo.value?.contactPhone) {
    window.location.href = `tel:${aboutInfo.value.contactPhone}`
  }
}
</script>
<template>
  <div class="about-page">
    <div class="nav-header">
      <div class="back-icon" @click="$router.back()">←</div>
      <div class="title">关于我们</div>
    </div>

    <div class="about-content">
      <div class="brand-header">
        <div class="logo-box">
          <img :src="aboutInfo.logoUrl || defaultLogo" alt="Logo" class="logo">
        </div>
        <h1 class="brand-title">{{ aboutInfo.title }}</h1>
        <p class="brand-slogan">用心做好每一杯奶茶</p>
      </div>

      <div class="info-card">
        <div class="rich-text" v-html="aboutInfo.content"></div>
      </div>

      <div class="contact-list">
        <div class="contact-item" @click="handleCall">
          <div class="item-left">
            <span class="icon">📞</span>
            <span class="label">联系电话</span>
          </div>
          <div class="item-right">
            <span class="val">{{ aboutInfo.contactPhone }}</span>
            <span class="arrow">›</span>
          </div>
        </div>
        <div class="contact-item">
          <div class="item-left">
            <span class="icon">📍</span>
            <span class="label">门店地址</span>
          </div>
          <div class="item-right">
            <span class="val address-val">{{ aboutInfo.address }}</span>
          </div>
        </div>
      </div>

      <div class="page-footer">
        <p>© 2025 {{ aboutInfo.title }} 版权所有</p>
        <p>Version 1.0.0</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const defaultLogo = '/vite.svg' // 使用 public 目录下的默认 logo

// 纯前端静态数据
const aboutInfo = ref({
  title: '茶香溢',
  logoUrl: '',
  content: `茶香溢奶茶店成立于2023年，致力于为每一位顾客提供最纯正、最健康的奶茶体验。

我们精选来自全球优质产区的原叶茶，搭配每日新鲜配送的优质牛乳，坚持手工现调，拒绝添加奶精和人工色素。

在这里，每一杯奶茶都承载着我们的匠心与诚意。无论是经典的珍珠奶茶，还是清新的水果茶，我们都力求做到极致，让每一口都充满幸福的味道。

感谢您一直以来的支持与陪伴，我们将继续努力，为您带来更多惊喜。`,
  contactPhone: '400-123-4567',
  address: '北京市朝阳区幸福路88号'
})

const handleCall = () => {
  if (aboutInfo.value?.contactPhone) {
    window.location.href = `tel:${aboutInfo.value.contactPhone}`
  }
}
</script>

<style scoped>
/* 饮饮茶(SipSipTea) 奶茶主题 UI 设计 - CSS变量定义 */
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
}

/* 奶茶主题样式 */
.about-page {
  min-height: 100vh;
  background-color: var(--background-color);
  font-family: var(--font-body);
  color: var(--text-color-dark);
  padding-bottom: var(--spacing-xl);
}

/* 导航头部 */
.nav-header {
  display: flex;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  background: var(--surface-color);
  border-bottom: 2px solid var(--border-color);
  position: sticky;
  top: 0;
  z-index: 100;
  backdrop-filter: blur(10px);
}

.back-icon {
  font-size: 24px;
  margin-right: var(--spacing-md);
  cursor: pointer;
  color: var(--primary-color);
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: var(--accent-cream);
  transition: all 0.3s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.back-icon:hover {
  transform: translateY(-2px) scale(1.05);
  background: var(--primary-light);
  box-shadow: 0 4px 12px rgba(160, 82, 45, 0.2);
}

.back-icon:active {
  transform: translateY(0) scale(0.98);
}

.title {
  font-size: 20px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  letter-spacing: 0.5px;
}

/* 品牌头部 */
.brand-header {
  padding: var(--spacing-xl) var(--spacing-lg);
  text-align: center;
  background: linear-gradient(135deg, var(--accent-cream) 0%, var(--background-color) 100%);
  margin: var(--spacing-lg);
  border-radius: var(--border-radius-xl);
  box-shadow: 0 8px 32px rgba(160, 82, 45, 0.08);
  position: relative;
  overflow: hidden;
}

.brand-header::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -50%;
  width: 200%;
  height: 200%;
  background: radial-gradient(circle, rgba(255, 248, 220, 0.1) 1px, transparent 1px);
  background-size: 20px 20px;
  animation: float 20s linear infinite;
}

@keyframes float {
  0% { transform: translate(0, 0) rotate(0deg); }
  100% { transform: translate(-20px, -20px) rotate(360deg); }
}

.logo-box {
  width: 100px;
  height: 100px;
  margin: 0 auto var(--spacing-md);
  background: linear-gradient(135deg, var(--surface-color), var(--accent-cream));
  border-radius: var(--border-radius-xl);
  box-shadow:
      0 8px 20px rgba(160, 82, 45, 0.15),
      inset 0 2px 4px rgba(255, 255, 255, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 3px solid var(--primary-light);
  position: relative;
  z-index: 1;
  transition: all 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

.logo-box:hover {
  transform: translateY(-4px) scale(1.05);
  box-shadow:
      0 12px 30px rgba(160, 82, 45, 0.25),
      inset 0 2px 4px rgba(255, 255, 255, 0.8);
}

.logo {
  width: 70px;
  height: 70px;
  object-fit: contain;
  filter: drop-shadow(0 2px 4px rgba(0, 0, 0, 0.1));
}

.brand-title {
  font-size: 28px;
  font-weight: 700;
  font-family: var(--font-heading);
  color: var(--primary-dark);
  margin-bottom: var(--spacing-sm);
  text-shadow: 1px 1px 2px rgba(255, 255, 255, 0.8);
  position: relative;
  display: inline-block;
}

.brand-title::after {
  content: '';
  position: absolute;
  bottom: -4px;
  left: 50%;
  transform: translateX(-50%);
  width: 60px;
  height: 3px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color));
  border-radius: 2px;
}

.brand-slogan {
  font-size: 14px;
  color: var(--text-color-medium);
  font-weight: 500;
  letter-spacing: 1px;
  padding: var(--spacing-xs) var(--spacing-md);
  background: rgba(255, 255, 255, 0.6);
  border-radius: var(--border-radius-lg);
  display: inline-block;
  backdrop-filter: blur(5px);
}

/* 信息卡片 */
.info-card {
  padding: var(--spacing-lg);
  line-height: 1.8;
  color: var(--text-color-medium);
  font-size: 15px;
  background: var(--surface-color);
  margin: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  box-shadow: 0 4px 16px rgba(139, 69, 19, 0.1);
  border: 1px solid var(--border-color);
  position: relative;
  overflow: hidden;
}

.info-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(to right, var(--accent-pink), var(--primary-color));
}

.rich-text {
  white-space: pre-wrap;
  font-family: var(--font-body);
  line-height: 1.8;
}

.rich-text::first-letter {
  font-size: 24px;
  font-weight: bold;
  color: var(--primary-color);
  float: left;
  margin-right: 8px;
  margin-top: 4px;
}

/* 联系列表 */
.contact-list {
  margin: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  overflow: hidden;
  box-shadow: 0 4px 16px rgba(139, 69, 19, 0.1);
  background: var(--surface-color);
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-md) var(--spacing-lg);
  border-bottom: 1px solid var(--border-color);
  cursor: pointer;
  transition: all 0.3s ease-out;
  position: relative;
  overflow: hidden;
}

.contact-item:last-child {
  border-bottom: none;
}

.contact-item:hover {
  background: var(--accent-cream);
  transform: translateX(4px);
}

.contact-item:hover::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 4px;
  background: linear-gradient(to bottom, var(--accent-pink), var(--primary-color));
}

.item-left {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.item-left .icon {
  font-size: 20px;
  width: 40px;
  height: 40px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--accent-cream);
  border-radius: var(--border-radius-md);
  color: var(--primary-color);
  transition: all 0.3s ease-out;
}

.contact-item:hover .item-left .icon {
  transform: scale(1.1) rotate(5deg);
  background: var(--primary-light);
}

.item-left .label {
  font-size: 15px;
  color: var(--text-color-dark);
  font-weight: 600;
}

.item-right {
  display: flex;
  align-items: center;
  gap: var(--spacing-sm);
  color: var(--text-color-medium);
  font-size: 14px;
}

.item-right .val {
  font-weight: 500;
  color: var(--primary-color);
}

.address-val {
  max-width: 180px;
  text-align: right;
  font-size: 13px;
  line-height: 1.4;
}

.arrow {
  font-size: 20px;
  color: var(--primary-light);
  transition: transform 0.3s ease-out;
}

.contact-item:hover .arrow {
  transform: translateX(4px);
}

/* 页面页脚 */
.page-footer {
  margin-top: var(--spacing-xl);
  padding: var(--spacing-lg);
  text-align: center;
  font-size: 12px;
  color: var(--text-color-light);
  background: var(--surface-color);
  border-radius: var(--border-radius-lg);
  margin: var(--spacing-lg);
  box-shadow: 0 2px 8px rgba(139, 69, 19, 0.05);
}

.page-footer p {
  margin-bottom: var(--spacing-xs);
  font-family: var(--font-body);
}

.page-footer p:first-child {
  color: var(--text-color-medium);
  font-weight: 500;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .brand-header {
    margin: var(--spacing-md);
    padding: var(--spacing-lg);
  }

  .info-card,
  .contact-list,
  .page-footer {
    margin: var(--spacing-md);
  }

  .brand-title {
    font-size: 24px;
  }

  .logo-box {
    width: 80px;
    height: 80px;
  }

  .logo {
    width: 60px;
    height: 60px;
  }
}

@media (max-width: 480px) {
  :root {
    --border-radius-lg: 20px;
    --border-radius-xl: 30px;
  }

  .nav-header {
    padding: var(--spacing-sm) var(--spacing-md);
  }

  .brand-header {
    margin: var(--spacing-sm);
    padding: var(--spacing-md);
  }

  .info-card,
  .contact-list,
  .page-footer {
    margin: var(--spacing-sm);
  }

  .contact-item {
    padding: var(--spacing-sm) var(--spacing-md);
  }

  .item-left .icon {
    width: 36px;
    height: 36px;
    font-size: 18px;
  }
}

/* 加载动画 */
.loading-box {
  text-align: center;
  padding: 100px 0;
  color: var(--text-color-light);
  font-size: 16px;
}

/* 自定义滚动条 */
.about-page::-webkit-scrollbar {
  width: 8px;
}

.about-page::-webkit-scrollbar-track {
  background: var(--surface-color);
  border-radius: 4px;
}

.about-page::-webkit-scrollbar-thumb {
  background: linear-gradient(to bottom, var(--primary-light), var(--primary-color));
  border-radius: 4px;
}

.about-page::-webkit-scrollbar-thumb:hover {
  background: linear-gradient(to bottom, var(--primary-color), var(--primary-dark));
}
</style>