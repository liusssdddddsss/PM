import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import vueDevTools from 'vite-plugin-vue-devtools'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    vueDevTools(),
  ],
  base:'/PM/',
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    port: 3000,
    https: false,
    proxy: {
      '/workbench': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/admin': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/iteration': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/team': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/teams': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/dashboard': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/ai': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/projects': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/bug': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/tasks': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/feedback': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      },
      '/product': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
