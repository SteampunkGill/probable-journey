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
.about-page {
  min-height: 100vh;
  background-color: #fff;
}

.nav-header {
  display: flex;
  align-items: center;
  padding: 15px;
  background: #fff;
  border-bottom: 1px solid #eee;
}

.back-icon {
  font-size: 20px;
  margin-right: 15px;
  cursor: pointer;
}

.title {
  font-size: 18px;
  font-weight: bold;
}

.brand-header {
  padding: 40px 20px;
  text-align: center;
  background: linear-gradient(to bottom, #FFF9E6, #fff);
}

.logo-box {
  width: 80px;
  height: 80px;
  margin: 0 auto 15px;
  background: #fff;
  border-radius: 18px;
  box-shadow: 0 4px 10px rgba(0,0,0,0.05);
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
}

.logo {
  width: 60px;
  height: 60px;
}

.brand-title {
  font-size: 22px;
  font-weight: bold;
  color: #333;
  margin-bottom: 8px;
}

.brand-slogan {
  font-size: 13px;
  color: #999;
}

.info-card {
  padding: 20px;
  line-height: 1.8;
  color: #666;
  font-size: 14px;
}

.rich-text {
  white-space: pre-wrap;
}

.contact-list {
  margin-top: 20px;
  border-top: 10px solid #f8f9fa;
}

.contact-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
}

.item-left {
  display: flex;
  align-items: center;
  gap: 10px;
}

.item-left .icon {
  font-size: 18px;
}

.item-left .label {
  font-size: 14px;
  color: #333;
}

.item-right {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #999;
  font-size: 14px;
}

.address-val {
  max-width: 200px;
  text-align: right;
  font-size: 13px;
}

.page-footer {
  margin-top: 50px;
  padding-bottom: 30px;
  text-align: center;
  font-size: 12px;
  color: #ccc;
}

.page-footer p {
  margin-bottom: 5px;
}

.loading-box {
  text-align: center;
  padding: 100px 0;
  color: #999;
}
</style>