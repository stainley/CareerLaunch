import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
// https://vite.dev/config/
export default defineConfig(({ mode }) => {
  console.log(`Running in mode: ${mode}`);

  const isProduction = mode === 'prod' || mode === 'stag';
  return {
    plugins: [react()],
    envDir: '.', // Directory for .env files
    envPrefix: 'VITE_',
    define: {
      'process.env': {},
    },
    server: {
      port: 5173, // Dev server port
    },
    resolve: {
      alias: {
        '@config': path.resolve(__dirname, 'src/config'),
        '@components': path.resolve(__dirname, 'src/components'),
        '@hooks': path.resolve(__dirname, 'src/hooks'),
        '@models': path.resolve(__dirname, 'src/models'),
        '@utils': path.resolve(__dirname, 'src/utils'),
        '@assets': path.resolve(__dirname, 'src/assets'),
      },
    },
    build: {
      sourcemap: !isProduction, // Enable sourcemaps only in non-prod modes
    },
  };
});

