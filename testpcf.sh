#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}

app_fqdn=`cf app sfdcapigateway | awk '/urls: / {print $2}'`

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
