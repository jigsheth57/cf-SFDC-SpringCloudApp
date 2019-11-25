#!/bin/bash
set -eu

#  mvn clean spring-boot:build-info package
cf t
echo -n "Validate the space & org, you are currently logged in before continuing!"
read
cf cs p.config-server standard config-server -c '{"git":{"privateKey":"-----BEGIN RSA PRIVATE KEY-----\n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\n-----END RSA PRIVATE KEY-----\n","uri":"git@github.com:jigsheth57/io.pivotal.sfdc.config-repo.git","label":"master","cloneOnStart":true}}'
cf cs p.service-registry standard service-registry
cf cs p-circuit-breaker-dashboard standard circuit-breaker
cf cs p.rabbitmq single-node-3.7 event-bus -c '{"tls": false}'
cf cs p-redis shared-vm cache-service
# cf cs scheduler-for-pcf standard scheduler-dataloader
echo "Checking status of the Spring Cloud Service Instances!"
until [ `cf service config-server | grep -c "succeeded"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
until [ `cf service service-registry | grep -c "succeeded"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
until [ `cf service circuit-breaker | grep -c "succeeded"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
until [ `cf service event-bus | grep -c "succeeded"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
echo
echo "Required service instances created. Pushing all required applications."

cf p -f ./manifest-authservice.yml --no-start
cf p -f ./manifest-accountservice.yml --no-start
cf p -f ./manifest-contactservice.yml --no-start
cf p -f ./manifest-opportunityservice.yml --no-start
cf p -f ./manifest-gatewayservice.yml --no-start
cf p -f ./manifest-portal.yml --no-start
cf p -f ./manifest-accountsource.yml --no-start
cf p -f ./manifest-accountprocessor.yml --no-start
cf p -f ./manifest-accountsink.yml --no-start
# cf p dataloader -f ./manifest-dataloader.yml
# cf sp dataloader

( exec "./network-policies.sh" )

cf st authservice
cf st accountservice
cf st contactservice
cf st opportunityservice
cf st gatewayservice
cf st portal
cf st accountsource
cf st accountprocessor
cf st accountsink

# if [[ `cf jobs | grep -c "preload-cache"` -gt 0 ]]; then
#   cf delete-job preload-cache -f
# fi
# DATALOADER_START_CMD=$(cf curl /v2/apps/`cf app dataloader --guid`/summary | jq -r '.detected_start_command')
# CF_TASK_JOB_DATALOADER_CMD="cf create-job dataloader preload-cache '$DATALOADER_START_CMD'"
# eval $CF_TASK_JOB_DATALOADER_CMD
# cf run-job preload-cache
# cf schedule-job preload-cache "0 12 ? * * "

webapp_fqdn=`cf app portal | awk '/routes: / {print $2}'`
open https://$webapp_fqdn
