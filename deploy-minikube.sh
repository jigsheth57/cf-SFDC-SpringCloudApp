#!/bin/bash
set -eu

minikube addons enable ingress

if [ -z "$(grep authservice.minikube.io /etc/hosts)" ]
then
  echo "adding following host to /etc/hosts file ..."
  echo "authservice.minikube.io accountservice.minikube.io contactservice.minikube.io opportunityservice.minikube.io gatewayservice.minikube.io sfdcwebapp.minikube.io"
  echo "$(minikube ip) authservice.minikube.io accountservice.minikube.io contactservice.minikube.io opportunityservice.minikube.io gatewayservice.minikube.io sfdcwebapp.minikube.io" | sudo tee -a /etc/hosts
fi

kubectl apply -f ./k8/ingress.yaml

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

open http://sfdcwebapp.minikube.io
# kubectl apply -f ./k8/mysql-deployment.yaml
# until [ `kubectl describe pods -l app=mysql | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
# do
#   echo -n "."
#   sleep 5s
# done
# kubectl apply -f ./k8/zipkin-deployment.yaml
# until [ `kubectl describe pods -l app=zipkin | awk '/Ready:/ {print $2}' | grep -c "True"` -ge 1  ]
# do
#   echo -n "."
#   sleep 5s
# done
