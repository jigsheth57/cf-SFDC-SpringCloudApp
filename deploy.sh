#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}

if [ -n "$1" ]
then
  mvn clean install package
  cf t
  echo -n "Validate the space & org, you are currently logged in before continuing!"
  read
  cf cs p-config-server standard config-service
  cf cs p-service-registry standard service-registry
  cf cs p-redis shared-vm data-grid-service
  cf cs p-circuit-breaker-dashboard standard circuit-breaker-dashboard
  csJSONStr={\"tag\":\"sfdcgateway\",\"uri\":\"http://sfdcapigateway.$1\"}
  echo $csJSONStr
  cf cups sfdcgateway -p ${csJSONStr}
  echo -n "Add the git repo for config files in config-service before continuing!"
  echo -n "Make sure service-registry instance is UP before continuing!"
  read
  cf p -f ./manifest-all.yml

  for i in {1..5}
  do
    echo "$i"
    rsp=`curl sfdcapigateway.$1/authservice/oauth2 | jsonValue accessToken 1`
    if [ ! -z "$rsp" -a "$rsp" != "" ]; then
          echo "$rsp"
          break
    fi
  done
  curl sfdcapigateway.$1/accountservice/accounts
  curl sfdcapigateway.$1/accountservice/opp_by_accts
  curl sfdcapigateway.$1/contactservice/contact/003i000000eXDVVAA4
  curl sfdcapigateway.$1/opportunityservice/opportunity/006i000000HiNOyAAN
  cf open sfdcbootwebapp
else
  echo "Usage: deploy <app domain name>"
fi
