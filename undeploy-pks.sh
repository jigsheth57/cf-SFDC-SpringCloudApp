#!/bin/bash

kubectl delete -f ./k8/ingress-pks.yaml
kubectl delete -f ./k8/rabbitmq-deployment.yaml
kubectl delete -f ./k8/redis-deployment.yaml
kubectl delete -f ./k8/configserver-deployment.yaml
kubectl delete -f ./k8/discovery-deployment.yaml
kubectl delete -f ./k8/authservice-deployment.yaml
kubectl delete -f ./k8/accountservice-deployment.yaml
kubectl delete -f ./k8/contactservice-deployment.yaml
kubectl delete -f ./k8/opportunityservice-deployment.yaml
kubectl delete -f ./k8/gatewayservice-deployment.yaml
kubectl delete -f ./k8/sfdcwebapp-deployment.yaml
kubectl delete -f ./k8/turbine-deployment.yaml
kubectl delete -f ./k8/hystrixdashboard-deployment.yaml

helm del --purge ingress
