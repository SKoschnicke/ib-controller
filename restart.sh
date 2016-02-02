#!/bin/sh

docker stop ib_controller_dev
docker rm ib_controller_dev
./start.sh
