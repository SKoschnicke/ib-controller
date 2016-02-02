#!/bin/sh

echo "starting IBController development conatiner..."
docker run -d -v $PWD:/usr/src/ib_controller -w /usr/src/ib_controller -p 5902:5902 --name ib_controller_dev ib_controller_image
docker exec -it ib_controller_dev /bin/bash
