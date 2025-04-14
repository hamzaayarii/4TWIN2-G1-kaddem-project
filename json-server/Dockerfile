FROM node:16-alpine

WORKDIR /app

# Copy db.json
#COPY ./db.json /app/db.json
# Create db.json with your user data
RUN echo '{"users":[{"id":"e19c","email":"test@gmail.com","password":"password","role":"ROLE_ADMIN"}]}' > /app/db.json


# Install json-server
RUN npm install -g json-server

EXPOSE 3000

CMD ["json-server", "--watch", "/app/db.json", "--host", "0.0.0.0", "--port", "3000"]
