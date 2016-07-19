#!/bin/bash
#set -e
## Last tested on PCFDev 0.15 (api.run.pez.pivotal.io) 5/13/2016. Jig Sheth
#mvn clean install package
cf login -u admin -p admin -a api.local.pcfdev.io --skip-ssl-validation
cf cs p-redis shared-vm data-grid-service
#cf cs p-rabbitmq standard p-rabbitmq
cf p -f ./manifest-registry.yml
registry_fqdn=`cf app eurekaserver | awk '/urls: / {print $2}'`
csJSONStr={\"tag\":\"p-service-registry\",\"uri\":\"http://$registry_fqdn\"}
echo \"$csJSONStr\"
cf cups p-service-registry -p \"$csJSONStr\"
cf p -f ./manifest-config.yml
config_fqdn=`cf app configserver | awk '/urls: / {print $2}'`
csJSONStr={\"tag\":\"p-config-server\",\"uri\":\"http://$config_fqdn\"}
echo \"$csJSONStr\"
cf cups p-config-server -p \"$csJSONStr\"
cf p -f ./manifest-auth.yml
cf p -f ./manifest-account.yml
cf p -f ./manifest-opportunity.yml
cf p -f ./manifest-contact.yml
