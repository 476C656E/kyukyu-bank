FROM node:20-alpine
WORKDIR /app

COPY package*.json ./
RUN npm install

EXPOSE 9091

CMD ["npm", "run", "dev"]