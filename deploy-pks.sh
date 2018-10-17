#!/bin/bash
set -eu

helm init --service-account tiller

helm install --name ingress -f ./k8/values.yaml stable/nginx-ingress --set rbac.create=true

kubectl apply -f ./k8/ingress-pks.yaml

kubectl apply -f ./k8/rabbitmq-deployment.yaml
until [ `kubectl describe pods -l app=rabbitmq | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/redis-deployment.yaml
until [ `kubectl describe pods -l app=redis | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/configserver-deployment.yaml
until [ `kubectl describe pods -l app=configserver | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/discovery-deployment.yaml
until [ `kubectl describe pods -l app=discovery | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/authservice-deployment.yaml
until [ `kubectl describe pods -l app=authservice | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/accountservice-deployment.yaml
until [ `kubectl describe pods -l app=accountservice | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/contactservice-deployment.yaml
until [ `kubectl describe pods -l app=contactservice | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/opportunityservice-deployment.yaml
until [ `kubectl describe pods -l app=opportunityservice | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/gatewayservice-deployment.yaml
until [ `kubectl describe pods -l app=gatewayservice | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/sfdcwebapp-deployment.yaml
until [ `kubectl describe pods -l app=sfdcwebapp | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/turbine-deployment.yaml
until [ `kubectl describe pods -l app=turbine | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done
kubectl apply -f ./k8/hystrixdashboard-deployment.yaml
until [ `kubectl describe pods -l app=hystrixdashboard | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
do
  echo -n "."
  sleep 5s
done

open http://sfdcwebapp.pks.pcfdemo.pcfapps.org
