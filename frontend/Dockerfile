# Stage 1: Build the Angular application
FROM node:18 as build
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build
# Debug: List contents after build
RUN echo "Contents of /app:" && ls -la /app
RUN echo "Contents of /app/dist:" && ls -la /app/dist
RUN echo "Contents of /app/dist/frontend:" && ls -la /app/dist/frontend

# Stage 2: Serve the application with Nginx
FROM nginx:alpine
# Remove default nginx static assets
RUN rm -rf /usr/share/nginx/html/*
# Copy built Angular assets
COPY --from=build /app/dist/frontend/browser /usr/share/nginx/html
# Copy nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf
# Debug: List contents of nginx html directory
RUN echo "Contents of nginx html dir:" && ls -la /usr/share/nginx/html
EXPOSE 80

CMD ["nginx", "-g", "daemon off;"] 