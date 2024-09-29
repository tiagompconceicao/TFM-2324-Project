# Build stage
FROM node:21.6.0 AS build

WORKDIR /app

COPY public/ /react-docker-example/public
COPY src/ /react-docker-example/src
COPY package.json /react-docker-example/

# Copy the rest of the application code
COPY . .

# Install dependencies
RUN npm install

# Build the React app
RUN npm run build

# Expose port 80
EXPOSE 3000

# Start Nginx
CMD ["npm", "start"]