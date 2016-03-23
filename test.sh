#!/bin/bash

function jsonValue() {
  KEY=$1
  num=$2
  awk -F"[,:}]" '{for(i=1;i<=NF;i++){if($i~/'$KEY'\042/){print $(i+1)}}}' | tr -d '"' | sed -n ${num}p
}

A_GUID=`cf curl /v2/apps?q=name:sfdcapigateway | jsonValue guid 1 | sed -e 's/^[[:space:]]*//'`
app_host=`cf curl /v2/apps/${A_GUID}/routes  | jsonValue host 1 | sed -e 's/^[[:space:]]*//'`
echo $app_host
