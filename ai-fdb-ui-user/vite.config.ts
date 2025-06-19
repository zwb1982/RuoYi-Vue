import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [vue()],
  server: {
    port: 5173, // Default Vite port, can be changed
    proxy: {
      // Proxy /api requests to backend
      '/api': {
        target: 'http://localhost:8080', // Assuming backend runs on 8080
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '') // Remove /api prefix if backend doesn't expect it
      }
    }
  }
})
