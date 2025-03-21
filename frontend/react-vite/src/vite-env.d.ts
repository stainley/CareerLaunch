/// <reference types="vite/client" />
/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_CLIENT_ID: string;
  readonly VITE_AUTH_SERVER: string;
  readonly VITE_REDIRECT_URI: string;
  readonly VITE_API_URL: string;
  readonly VITE_ENV: 'DEV' | 'QA' | 'STAG' | 'PROD';
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
