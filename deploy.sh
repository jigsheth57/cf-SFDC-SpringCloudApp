#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}

## if [ -n "$1" ]
## then
  mvn clean install package
  cf t
  echo -n "Validate the space & org, you are currently logged in before continuing!"
  read
  appdomain=`cf curl /v2/domains | jsonValue name 1 | sed -e 's/^[[:space:]]*//'`

  cf cs p-config-server standard config-service -c '{"git":{"uri":"https://github.com/jigsheth57/config-repo"}}'
  cf cs p-service-registry standard service-registry
  cf cs p-redis shared-vm data-grid-service
  cf cs p-circuit-breaker-dashboard standard circuit-breaker-dashboard
  echo -n "Make sure service-registry instance is UP before continuing!"
  read
  cf p -f ./manifest-all.yml

  A_GUID=`cf curl /v2/apps?q=name:sfdcapigateway | jsonValue guid 1 | sed -e 's/^[[:space:]]*//'`
  app_host=`cf curl /v2/apps/${A_GUID}/routes  | jsonValue host 1 | sed -e 's/^[[:space:]]*//'`

  csJSONStr={\"tag\":\"sfdcgateway\",\"uri\":\"http://$app_host.$appdomain\"}
  echo \"$csJSONStr\"
  cf cups sfdcgateway -p \"$csJSONStr\"
  cf p -f ./manifest-webapp.yml -d $appdomain
  for i in {1..5}
  do
    echo "$i"
    rsp=`curl $app_host.$appdomain/authservice/oauth2 | jsonValue accessToken 1`
    if [ ! -z "$rsp" -a "$rsp" != "" ]; then
          echo "$rsp"
          break
    fi
  done
  curl $app_host.$appdomain/accountservice/accounts
  curl $app_host.$appdomain/accountservice/opp_by_accts
  curl $app_host.$appdomain/contactservice/contact/003i000000eXDVVAA4
  curl $app_host.$appdomain/opportunityservice/opportunity/006i000000HiNOyAAN
  cf open sfdcbootwebapp
## else
  ## echo "Usage: deploy <app domain name>"
## fi
