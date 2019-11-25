#!/bin/bash

cf t
echo -n "Validate the space & org, you are currently logged in before continuing!"
read
cf cs p.rabbitmq single-node-3.7 event-bus -c '{"tls": false}'
cf cs p.mysql db-small zipkin-db
echo "Checking status of the Service Instances!"
until [ `cf service event-bus | grep -c "succeeded"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
until [ `cf service zipkin-db | grep -c "succeeded"` -eq 1  ]
do
  echo -n "."
  sleep 5s
done
echo
echo "Required service instances created. Pushing all required applications."

#echo "create database if not exists zipkin" | cf mysql zipkin-db
cat zipkin/mysql.sql | cf mysql zipkin-db

cf p -f manifest-zipkin.yml
