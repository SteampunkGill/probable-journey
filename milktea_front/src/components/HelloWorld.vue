<script setup>
import { ref } from 'vue'

defineProps({
  msg: String,
})

const count = ref(0)
</script>

<template>
  <h1>{{ msg }}</h1>

  <div class="card">
    <button type="button" @click="count++">count is {{ count }}</button>
    <p>
      Edit
      <code>components/HelloWorld.vue</code> to test HMR
    </p>
  </div>

  <p>
    Check out
    <a href="https://vuejs.org/guide/quick-start.html#local" target="_blank"
      >create-vue</a
    >, the official Vue + Vite starter
  </p>
  <p>
    Learn more about IDE Support for Vue in the
    <a
      href="https://vuejs.org/guide/scaling-up/tooling.html#ide-support"
      target="_blank"
      >Vue Docs Scaling up Guide</a
    >.
  </p>
  <p class="read-the-docs">Click on the Vite and Vue logos to learn more</p>
</template>

<style scoped>
/* 饮饮茶 (SipSipTea) - 奶茶主题 HelloWorld 组件样式 */

/* 主标题样式 */
h1 {
  font-size: 2.5em;
  font-weight: 600;
  color: var(--primary-color);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.08em;
  margin-bottom: var(--spacing-lg);
  text-align: center;
  position: relative;
  padding-bottom: var(--spacing-sm);
}

h1::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 50%;
  transform: translateX(-50%);
  width: 80px;
  height: 3px;
  background: linear-gradient(90deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  border-radius: var(--border-radius-sm);
}

/* 卡片样式 */
.card {
  background: linear-gradient(135deg, var(--surface-color) 0%, rgba(var(--surface-color-rgb), 0.9) 100%);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-lg);
  margin: var(--spacing-lg) 0;
  border: 2px solid var(--border-color);
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.05);
  backdrop-filter: blur(4px);
  transition: all 0.3s ease-out;
}

.card:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.08);
  border-color: var(--primary-light);
}

/* 按钮样式 */
button {
  background: linear-gradient(135deg, var(--primary-color) 0%, var(--primary-dark) 100%);
  color: var(--accent-cream);
  border: none;
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-sm) var(--spacing-md);
  font-size: 1em;
  font-weight: 600;
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  letter-spacing: 0.05em;
  cursor: pointer;
  transition: all 0.3s ease-out;
  box-shadow: 0 4px 15px rgba(var(--primary-color-rgb), 0.2);
  position: relative;
  overflow: hidden;
}

button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s ease-out;
}

button:hover {
  transform: translateY(-2px) scale(1.02);
  box-shadow: 0 6px 20px rgba(var(--primary-color-rgb), 0.3);
}

button:hover::before {
  left: 100%;
}

button:active {
  transform: translateY(0) scale(0.98);
}

/* 段落样式 */
p {
  font-size: 1em;
  color: var(--text-color-medium);
  font-family: 'Nunito', 'Noto Sans KR', sans-serif;
  line-height: 1.6;
  margin: var(--spacing-sm) 0;
  letter-spacing: 0.03em;
}

/* 代码样式 */
code {
  background: rgba(var(--primary-color-rgb), 0.08);
  color: var(--primary-color);
  padding: 2px 6px;
  border-radius: var(--border-radius-sm);
  font-family: 'Courier New', monospace;
  font-weight: 500;
  border: 1px solid rgba(var(--primary-color-rgb), 0.15);
}

/* 链接样式 */
a {
  color: var(--accent-pink);
  text-decoration: none;
  font-weight: 500;
  position: relative;
  transition: all 0.3s ease-out;
}

a::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 2px;
  background: linear-gradient(90deg, var(--accent-pink) 0%, var(--primary-color) 100%);
  border-radius: var(--border-radius-sm);
  transition: width 0.3s ease-out;
}

a:hover {
  color: var(--primary-color);
}

a:hover::after {
  width: 100%;
}

/* 特殊段落样式 */
.read-the-docs {
  color: var(--accent-pink);
  font-family: 'Prompt', 'Noto Serif KR', serif;
  letter-spacing: 0.05em;
  font-weight: 500;
  text-align: center;
  margin-top: var(--spacing-xl);
  padding: var(--spacing-md);
  background: linear-gradient(135deg, rgba(var(--accent-pink-rgb), 0.05) 0%, transparent 100%);
  border-radius: var(--border-radius-lg);
  border: 1px solid rgba(var(--accent-pink-rgb), 0.15);
  position: relative;
  overflow: hidden;
}

.read-the-docs::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, var(--accent-pink), transparent);
  animation: shimmer 3s ease-in-out infinite;
}

/* 动画 */
@keyframes shimmer {
  0%, 100% {
    opacity: 0.3;
  }
  50% {
    opacity: 1;
  }
}

/* 响应式设计 */
@media (max-width: 768px) {
  h1 {
    font-size: 2em;
  }

  .card {
    padding: var(--spacing-md);
    margin: var(--spacing-md) 0;
  }

  button {
    padding: var(--spacing-xs) var(--spacing-sm);
    font-size: 0.9em;
  }

  p {
    font-size: 0.9em;
  }
}

/* 计数器数字样式 */
button span {
  font-weight: 700;
  color: var(--accent-cream);
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

/* 卡片内容布局 */
.card p {
  margin-top: var(--spacing-md);
  color: var(--text-color-dark);
  opacity: 0.85;
}

/* 链接容器样式 */
.card + p {
  margin-top: var(--spacing-lg);
  text-align: center;
}

/* 奶茶主题装饰元素 */
.card::before {
  content: '🍵';
  position: absolute;
  top: -15px;
  right: -15px;
  font-size: 1.5em;
  opacity: 0.2;
  transform: rotate(15deg);
  transition: all 0.3s ease-out;
}

.card:hover::before {
  opacity: 0.4;
  transform: rotate(25deg) scale(1.1);
}
</style>