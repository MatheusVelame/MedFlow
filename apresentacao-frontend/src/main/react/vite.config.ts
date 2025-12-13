import { defineConfig } from "vite";
import react from "@vitejs/plugin-react-swc";
import path from "path";
import { componentTagger } from "lovable-tagger";

// https://vitejs.dev/config/
export default defineConfig(({ mode }) => ({
  server: {
    host: "::",
    port: 8090,
    // --- BLOCO DE PROXY ADICIONADO ---
    proxy: {
      // Proxy específico para exames: reescreve '/api/exames' -> '/exames' no backend
      '/api/exames': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/api/, ''),
      },

      // Redireciona todas as requisições que começam com /api para o servidor Spring Boot
	  '/api': {
	      target: 'http://localhost:8080',
	      changeOrigin: true,
	      secure: false,
	    },
	  
	  	 '/backend': {
        target: 'http://localhost:8080', // Porta onde seu Spring Boot está rodando
        changeOrigin: true, // Necessário para evitar problemas de CORS
        secure: false,      // Usar 'false' se estiver rodando o backend em HTTP (padrão)
      },

      // --- NOVO: proxy para /exames ---
      // O backend expõe o controller em '/exames' (sem /api), então o Vite estava servindo a SPA
      // quando você acessava http://localhost:8090/exames. Encaminhamos essas requisições ao backend.
      '/exames': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        // Não reescrevemos o caminho: backend já espera '/exames'
      },
    },
    // --- FIM DO BLOCO ADICIONADO ---
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