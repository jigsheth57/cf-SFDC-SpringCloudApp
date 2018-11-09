#!/bin/bash
set -eu

kubectl apply -f ./k8/helm-configure.yaml

helm init --service-account tiller

sleep 30

helm install --name ingress -f ./k8/values.yaml stable/nginx-ingress --set rbac.create=true

helm install ./sfdcapps
