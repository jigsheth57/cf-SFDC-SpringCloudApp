#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}

if [ -n "$1" ]
then
  while true
#  for i in {1..50}
  do
    echo "$i"
    rsp=`curl $1/authservice/oauth2 | jsonValue accessToken 1`
    if [ ! -z "$rsp" -a "$rsp" != "" ]; then
          echo "$rsp"
          break
    fi
    sleep 5
  done
  curl $1/accountservice/accounts
  curl $1/accountservice/opp_by_accts
  curl $1/contactservice/contact/003i000000eXDVVAA4
  curl $1/opportunityservice/opportunity/006i000000HiNOyAAN
else
  echo "Usage: ./testpcf.sh <sfdcapigateway app uri>"
fi
