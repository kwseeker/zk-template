#!/bin/bash

# https://hub.docker.com/_/zookeeper?tab=description
# docker pull zookeeper:3.5

docker run \
        -p 2181:2181 \
        --name zk-single \
        -d zookeeper:3.5
# more options:
#       --restart always \
#       -v $(pwd)/zoo.cfg:/conf/zoo.cfg \
#       -e JVMFLAGS="-Dzookeeper.serverCnxnFactory=org.apache.zookeeper.server.NettyServerCnxnFactory" \
#       -e "ZOO_INIT_LIMIT=10" \
