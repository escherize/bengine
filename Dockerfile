FROM mhart/alpine-node:latest

MAINTAINER Your Name <you@example.com>

# Create app directory
RUN mkdir -p /bengine
WORKDIR /bengine

# Install app dependencies
COPY package.json /bengine
RUN npm install pm2 -g
RUN npm install

# Bundle app source
COPY target/release/bengine.js /bengine/bengine.js
COPY public /bengine/public

ENV HOST 0.0.0.0

EXPOSE 3000
CMD [ "pm2-docker", "/bengine/bengine.js" ]
