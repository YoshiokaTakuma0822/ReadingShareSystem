FROM node:24-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install --no-cache
COPY . .
RUN npm run build

EXPOSE 80
ENV PORT=80
CMD ["npm","run","start"]
