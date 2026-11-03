import react from '@vitejs/plugin-react'
import { defineConfig } from 'vite'

export default defineConfig({
  plugins: [react()],
  server: {
    // Vite reenvía cada petición al primer prefijo que encaje, en este orden.
    // Por eso /api/tarifas va antes que /api: si no, iría a consumption-service.
    proxy: {
      '/api/tarifas': 'http://localhost:8083',
      '/api': 'http://localhost:8081',
    },
  },
})