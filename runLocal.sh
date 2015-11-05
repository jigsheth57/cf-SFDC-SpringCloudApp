#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}

mvn clean package -DskipTests
pushd configServer
nohup mvn spring-boot:run -DENCRYPT_KEY=pivotal -Dspring.profiles.active=local &
popd
pushd eurekaServer
nohup mvn spring-boot:run -Dspring.profiles.active=local &
popd
sleep 30
pushd authService
nohup mvn spring-boot:run -DCONFIG_SERVER_URL=http://localhost:8888 -Dspring.profiles.active=local &
popd
pushd accountService
nohup mvn spring-boot:run -DCONFIG_SERVER_URL=http://localhost:8888 -Dspring.profiles.active=local &
popd
pushd contactService
nohup mvn spring-boot:run -DCONFIG_SERVER_URL=http://localhost:8888 -Dspring.profiles.active=local &
popd
pushd opportunityService
nohup mvn spring-boot:run -DCONFIG_SERVER_URL=http://localhost:8888 -Dspring.profiles.active=local &
popd
pushd sfdcapigateway
nohup mvn spring-boot:run -DCONFIG_SERVER_URL=http://localhost:8888 -Dspring.profiles.active=local &
popd
pushd sfdcwebapp
nohup mvn spring-boot:run -DCONFIG_SERVER_URL=http://localhost:8888 -Dspring.profiles.active=local &
popd
#pushd hystrixDashboard
#nohup mvn spring-boot:run -DCONFIG_SERVER_URL=http://localhost:8888 -Dspring.profiles.active=local &
#popd
sleep 100
for i in {1..5}
do
  echo "$i"
  rsp=`curl localhost:9011/authservice/oauth2 | jsonValue accessToken 1`
  if [ ! -z "$rsp" -a "$rsp" != "" ]; then
        echo "$rsp"
        break
  fi
done
curl http://localhost:9011/accountservice/accounts
curl http://localhost:9011/accountservice/opp_by_accts
curl http://localhost:9011/contactservice/contact/003i000000eXDVVAA4
curl http://localhost:9011/opportunityservice/opportunity/006i000000HiNOyAAN

open http://localhost:9090
