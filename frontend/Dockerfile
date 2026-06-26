FROM node:18 AS build

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

RUN npm run build

FROM node:18-slim

WORKDIR /app

COPY --from=build /app/build ./build
COPY --from=build /app/server.js ./server.js

EXPOSE 3000

CMD ["node", "server.js"]
