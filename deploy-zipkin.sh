#!/bin/bash

cf t
echo -n "Validate the space & org, you are currently logged in before continuing!"
read
cf cs p-rabbitmq standard event-bus
cf cs p.mysql db-small zipkin-db
echo "Checking status of the Service Instances!"
until [ `cf service zipkin-db | grep -c "succeeded"` -eq 1  ]
do
  echo -n "."
  sleep 5s
done

cf p -f manifest-zipkin.yml

cat mysql.sql | cf mysql zipkin-db

cf restart zipkin-bbq
