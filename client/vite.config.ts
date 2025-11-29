import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    host: true, // This makes Vite listen on 0.0.0.0
    port: 9091,
    watch: {
      usePolling: true // This is often needed for HMR in Docker environments
    },
    proxy: {
      '/api': {
        target: 'http://host.docker.internal:8080',
        changeOrigin: true,
      }
    }
  }
})
