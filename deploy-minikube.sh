#!/bin/bash
set -eu

function removehost() {
  HOSTNAME=$1
  if [ -n "$(grep "$HOSTNAME" /etc/hosts)" ]
  then
      echo "Removing \"$HOSTNAME\" from your $ETC_HOSTS";
      sudo sed -i".bak" "/$HOSTNAME/d" $ETC_HOSTS
  else
      echo "\"$HOSTNAME\" was not found in your $ETC_HOSTS";
  fi
}

function remove_add_host() {
  HOSTNAME=$1
  HOSTS_LINE="$IP\t$HOSTNAME"
  removehost "$HOSTNAME"
  echo "Adding \"$HOSTNAME\" to your $ETC_HOSTS";
  sudo -- sh -c -e "echo '$HOSTS_LINE' >> /etc/hosts";
  if [ -n "$(grep "$HOSTNAME" /etc/hosts)" ]
      then
          echo "\"$HOSTNAME\" was added succesfully!";
      else
          echo "Failed to Add \"$HOSTNAME\", Try again!";
  fi
}

# PATH TO YOUR HOSTS FILE
ETC_HOSTS=/etc/hosts

# DEFAULT IP FOR HOSTNAME
IP=$(minikube ip)

HOSTNAME="authservice.minikube.io accountservice.minikube.io contactservice.minikube.io opportunityservice.minikube.io gatewayservice.minikube.io portal.minikube.io hystrixdashboard.minikube.io discovery.minikube.io configserver.minikube.io rabbitmq.minikube.io"
remove_add_host "$HOSTNAME"

# Enable Nginx Ingress Controller
minikube addons enable ingress

kubectl config use-context minikube

#Configure Helm
kubectl apply -f ./k8/helm-configure.yaml

# Initialize Tiller
helm init --service-account tiller

sleep 30

# Install Salesforce.com demo cloud native apps
helm install --set global.domain=minikube.io ./sfdcapps-1.0.0.tgz
