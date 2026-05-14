FROM node:20-alpine AS build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .

RUN mkdir -p src/environments && \
    echo "export const environment = { production: true, apiUrl: '/api' };" > src/environments/environment.ts && \
    echo "export const environment = { production: true, apiUrl: '/api' };" > src/environments/environment.production.ts

RUN npm run build

FROM nginx:alpine
COPY --from=build /app/dist/purchasing-frontend/browser /usr/share/nginx/html
COPY nginx.conf /etc/nginx/conf.d/default.conf
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]