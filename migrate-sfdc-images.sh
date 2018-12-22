#!/bin/bash

set -e

images=(configserver:pack eurekaserver:pack hystrixdashboard:pack authservice:pack accountservice:pack contactservice:pack opportunityservice:pack gatewayservice:pack sfdcwebapp:pack dataloader:pack)

for i in "${images[@]}"
do
  docker push jigsheth57/$i
  docker tag jigsheth57/$i harbor.pks.pcfdemo.pcfapps.org/library/$i
  docker push harbor.pks.pcfdemo.pcfapps.org/library/$i
done

# docker pull redis:alpine
# docker tag redis:alpine harbor.pks.pcfdemo.pcfapps.org/library/redis:alpine
# docker push harbor.pks.pcfdemo.pcfapps.org/library/redis:alpine
#
# docker pull rabbitmq:3-management
# docker tag rabbitmq:3-management harbor.pks.pcfdemo.pcfapps.org/library/rabbitmq:3-management
# docker push harbor.pks.pcfdemo.pcfapps.org/library/rabbitmq:3-management