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
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    https: false,
    proxy: {
      '/workbench': {
        target: 'http://localhost:9091',
        changeOrigin: true,
        secure: false
      },
      '/admin': {
        target: 'http://localhost:9091',
        changeOrigin: true,
        secure: false
      },
      '/iteration': {
        target: 'http://localhost:9091',
        changeOrigin: true,
        secure: false
      },
      '/team': {
        target: 'http://localhost:9091',
        changeOrigin: true,
        secure: false
      }
    }
  }
})
