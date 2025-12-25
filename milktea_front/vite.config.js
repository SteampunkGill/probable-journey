import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import path from 'path'

// https://vite.dev/config/
export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': path.resolve(__dirname, './src')
    }
  },
  server: {
    proxy: {
      // 代理所有 /api 开头的请求
      '/api': {
        target: 'http://localhost:8081',
        changeOrigin: true,
        secure: false,
        // 不再剥离 /api 前缀，因为后端控制器路径包含 /api
        configure: (proxy, options) => {
          proxy.on('proxyReq', (proxyReq, req, res) => {
            proxyReq.setHeader('Origin', 'http://localhost:8081');
          });
        }
      }
    }
  }
})
