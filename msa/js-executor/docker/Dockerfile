

FROM thingsboard/node:16.20.2-bookworm-slim

ENV NODE_ENV production
ENV DOCKER_MODE true

COPY start-js-executor.sh /tmp/

WORKDIR ${pkg.installFolder}

COPY ["src/package.json", "src/yarn.lock", "./"]

COPY package/linux/conf ./conf
COPY package/linux/conf ./config
COPY src/api ./api
COPY src/queue ./queue
COPY src/config ./config
COPY src/server.js ./

RUN chmod a+x /tmp/*.sh \
    && mv /tmp/start-js-executor.sh /usr/bin \
    && chown -R node:node ${pkg.installFolder} \
    && yarn install --production && yarn cache clean --all

USER node

CMD ["start-js-executor.sh"]
