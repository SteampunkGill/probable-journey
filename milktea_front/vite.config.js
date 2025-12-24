import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    proxy: {
      // 代理 /api/v1/app 路径的请求到后端
      '/api/v1/app': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      },
      // 代理 /api/common 路径的请求到后端
      '/api/common': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      },
      // 代理 /api 路径的请求到后端（通用配置）
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      }
    }
  }
})
