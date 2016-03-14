#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}
# 192.168.99.101 IS DOCKER-MACHINE IP
for i in {1..5}
do
  echo "$i"
  rsp=`curl 192.168.99.101:9011/authservice/oauth2 | jsonValue accessToken 1`
  if [ ! -z "$rsp" -a "$rsp" != "" ]; then
        echo "$rsp"
        break
  fi
done
curl http://192.168.99.101:9011/accountservice/accounts
curl http://192.168.99.101:9011/accountservice/opp_by_accts
curl http://192.168.99.101:9011/contactservice/contact/003i000000eXDVVAA4
curl http://192.168.99.101:9011/opportunityservice/opportunity/006i000000HiNOyAAN
