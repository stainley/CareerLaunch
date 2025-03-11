import { defineConfig, loadEnv } from 'vite';
import react from '@vitejs/plugin-react';
import path from 'path';
import https from 'https';

export default defineConfig(({ mode }) => {
  console.log(`Running in mode: ${mode}`);
  const env = loadEnv(mode, process.cwd(), '');

  const apiTarget = env.VITE_API_URL || 'https://api-gateway:8080';
  const isDev = mode === 'development';

  console.log(`Proxy target: ${apiTarget}`);

  // Create an agent that ignores self-signed certificates
  const agent = new https.Agent({
    rejectUnauthorized: false,
  });

  return {
    plugins: [react()],
    envDir: '.',
    envPrefix: 'VITE_',
    define: {
      'process.env': {},
    },
    server: {
      port: 5173,
      proxy: {
        '/auth': {
          target: apiTarget,
          changeOrigin: true,
          secure: !isDev,
          agent: agent,
          configure: (proxy) => {
            if (isDev) {
              proxy.on('proxyReq', (_proxyReq, req) => {
                console.log(`Proxying ${req.url} to ${apiTarget}`);
              });
            }
          },
        },
        '/users': {
          target: apiTarget,
          changeOrigin: true,
          secure: !isDev,
          agent: agent,
          configure: (proxy) => {
            if (isDev) {
              proxy.on('proxyReq', (_proxyReq, req) => {
                console.log(`Proxying ${req.url} to ${apiTarget}`);
              });
            }
          },
        },
      },
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
      sourcemap: true,
    },
  };
});