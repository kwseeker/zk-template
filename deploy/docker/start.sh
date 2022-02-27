#!/bin/bash

# https://hub.docker.com/_/zookeeper?tab=description
# docker pull zookeeper:3.5

docker-compose up -d

#zkCli.sh -server 127.0.0.1:2184