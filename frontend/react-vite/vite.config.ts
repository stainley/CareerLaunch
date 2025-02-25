import {defineConfig} from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig(({mode}) => {
    return {
        plugins: [react()],
        envDir: '.',
        envPrefix: 'VITE_',
        define: {
            'process.env': {},
        },
        server: {
            port: 5173,
        }
    };
});

