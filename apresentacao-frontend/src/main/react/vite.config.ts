import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import path from "path";
import { componentTagger } from "lovable-tagger";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
  server: {
    host: "::",
    port: 8090,
    // Proxy: todas as chamadas que começam por /api serão encaminhadas ao backend Spring Boot
    // Mantemos um único mapeamento para evitar reescritas contraditórias que causavam "No static resource exames.".
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        configure: (proxy, options) => {
          // proxy is an http-proxy instance
          proxy.on('proxyReq', (proxyReq, req, res) => {
            const url = req.url;
            const method = req.method;
            // eslint-disable-next-line no-console
            console.log(`[vite-proxy] ${method} ${url} -> ${options.target}`);
          });
          proxy.on('proxyRes', (proxyRes, req, res) => {
            // eslint-disable-next-line no-console
            console.log(`[vite-proxy] response for ${req.method} ${req.url} -> ${proxyRes.statusCode}`);
          });
        }
      },
    },
  },
  plugins: [
    react(),
    mode === 'development' &&
    componentTagger(),
  ].filter(Boolean),
  resolve: {
    alias: {
      // Já está configurado, mas mantemos
      "@": path.resolve(__dirname, "./src"),
    },
  },
}));