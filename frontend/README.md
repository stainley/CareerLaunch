## Environment Setup and Usage

This project is configured to support multiple environments (`dev`, `prod`, `qa`, `stag`) using Vite’s environment
variable system. Environment-specific configurations are managed through `.env` files, dynamically loaded at runtime to
ensure flexibility across development, testing, and production stages.

### Prerequisites

- **Node.js**: Version 18.x or higher
- **npm**: Version 9.x or higher
- **Git**: For version control and Husky hooks

### Environment Files

The project uses the following `.env` files, located in the root directory, to define environment-specific variables.
Only variables prefixed with `VITE_` are exposed to the client-side code.

- `.env.dev`: Development environment
  ```env
  VITE_CLIENT_ID=job-tracker-client-dev
  VITE_AUTH_SERVER=http://localhost:8081
  VITE_REDIRECT_URI=http://localhost:3000/callback
  VITE_API_URL=http://localhost:8080
  VITE_ENV=development
    ```
- `.env.prod`: Production environment
  ```env
  VITE_CLIENT_ID=job-tracker-client-prod
  VITE_AUTH_SERVER=https://auth.production.com
  VITE_REDIRECT_URI=https://app.production.com/callback
  VITE_API_URL=https://api.production.com
  VITE_ENV=production
  ```

- `.env.qa`: QA environment
  ```env
    VITE_CLIENT_ID=job-tracker-client-qa
    VITE_AUTH_SERVER=https://auth.qa.com
    VITE_REDIRECT_URI=https://app.qa.com/callback
    VITE_API_URL=https://api.qa.com
    VITE_ENV=qa
  ```

---

### Production

- #### Build for Production:
  ```shell
  npm run build:prod
  ```

### QA

- #### Build for QA:
  ```shell
  npm run build:qa
  ```

### Development

- #### Build for Development:
  ```shell
  npm run build:dev
  ```

### Preview Build

- #### Preview Any Build:
  ```shell
  npm run preview
  ```
    - Serves the latest dist folder locally (e.g., after npm run build:prod).

### Additional Notes

- **Testing**: Use `npm run test` to run Vitest unit tests, which respect the current environment.
- **Linting and Formatting**: Pre-commit hooks (`husky` and `lint-staged`) ensure code quality

  ```shell
    npm run lint  # Run ESLint
    npm run format  # Run Prettier
  ```
- **Customization**: Modify `.env.[mode]` files to match your deployment setup (e.g., update `VITE_API_URL` for
  production).
