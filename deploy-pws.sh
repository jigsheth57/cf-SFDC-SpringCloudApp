#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}

## Last tested on PWS (run.pivotal.io) 7/27/2016. Jig Sheth
#if [ -n "$1" ]
 #then
  mvn clean install package
  cf t
  echo -n "Validate the space & org, you are currently logged in before continuing!"
  read
  appdomain=`cf curl /v2/domains | jsonValue name 1 | sed -e 's/^[[:space:]]*//'`
  echo "Domain: \"$appdomain\""
  cf cs p-config-server standard config-service -c '{"git":{"uri":"https://github.com/jigsheth57/config-repo"}}'
  cf cs p-service-registry standard service-registry
  cf cs p-circuit-breaker-dashboard standard circuit-breaker-dashboard
  cf cs p-rabbitmq standard config-event-bus
  cf cs redis dedicated-vm data-grid-service
  until [ `cf service config-service | grep -c "succeeded"` -eq 1  ]
  do
    echo -n "."
  done
  until [ `cf service service-registry | grep -c "succeeded"` -eq 1  ]
  do
    echo -n "."
  done
  until [ `cf service circuit-breaker-dashboard | grep -c "succeeded"` -eq 1  ]
  do
    echo -n "."
  done
  echo
  echo "Services created. Pushing applications."
#  echo -n "Make sure service-registry, config-service, circuit-breaker-dashboard instances are UP and RUNNING before continuing!"
#  read
  cf p -f ./manifest-all.yml
  if [ "$?" -ne "0" ]; then
    exit $?
  fi

#  A_GUID=`cf app sfdcapigateway --guid`
#  echo \"$A_GUID\"
#  app_host=`cf curl /v2/apps/${A_GUID}/routes  | jsonValue host 1 | sed -e 's/^[[:space:]]*//'`
#  echo \"$app_host\"

  app_fqdn=`cf app sfdcapigateway | awk '/urls: / {print $2}'`
#  csJSONStr={\"tag\":\"sfdcgateway\",\"uri\":\"http://$app_fqdn\"}
#  echo \"$csJSONStr\"
#  cf cups sfdcgateway -p \"$csJSONStr\"
#  if [ "$?" -ne "0" ]; then
#    cf update-user-provided-service sfdcgateway -p \"$csJSONStr\"
#    if [ "$?" -ne "0" ]; then
#      exit $?
#    fi
#  fi
#  cf p -f ./manifest-webapp.yml -d $appdomain
  for i in {1..5}
  do
    echo "$i"
    rsp=`curl https://$app_fqdn/authservice/oauth2 | jsonValue accessToken 1`
    if [ ! -z "$rsp" -a "$rsp" != "" ]; then
          echo "$rsp"
          break
    fi
  done
  curl https://$app_fqdn/accountservice/accounts
  curl https://$app_fqdn/accountservice/opp_by_accts
  curl https://$app_fqdn/contactservice/contact/003i000000eXDVVAA4
  curl https://$app_fqdn/opportunityservice/opportunity/006i000000HiNOyAAN
  # requires cf open plugin installed https://github.com/cloudfoundry-community/cf-plugin-open
  # cf open sfdcbootwebapp
  webapp_fqdn=`cf app sfdcbootwebapp | awk '/urls: / {print $2}'`
  open https://$webapp_fqdn
#else
#  echo "Usage: deploy-pws <github config-repo e.g. https://github.com/jigsheth57/config-repo>"
#  echo "example: ./deploy-pws.sh https://github.com/jigsheth57/config-repo"
#  exit 1
#fi
