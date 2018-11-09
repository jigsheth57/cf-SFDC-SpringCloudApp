#!/bin/bash
set -eu

minikube addons enable ingress

if [ -z "$(grep authservice.minikube.io /etc/hosts)" ]
then
  echo "adding following host to /etc/hosts file ..."
  echo "authservice.minikube.io accountservice.minikube.io contactservice.minikube.io opportunityservice.minikube.io gatewayservice.minikube.io portal.minikube.io hystrixdashboard.minikube.io discovery.minikube.io configserver.minikube.io"
  echo "$(minikube ip) authservice.minikube.io accountservice.minikube.io contactservice.minikube.io opportunityservice.minikube.io gatewayservice.minikube.io portal.minikube.io hystrixdashboard.minikube.io discovery.minikube.io configserver.minikube.io" | sudo tee -a /etc/hosts
fi

kubectl config use-context minikube

kubectl apply -f ./k8/helm-configure.yaml

helm init --service-account tiller

sleep 30

helm install --set global.domain=minikube.io ./sfdcapps
