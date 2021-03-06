#!/bin/bash
set -eu

#Configure Helm
# kubectl apply -f ./k8/helm-configure.yaml

# Initialize Tiller
# helm init --service-account tiller

# sleep 30

# Install Nginx Ingress Controller
# helm install --name ingress -f ./k8/values.yaml stable/nginx-ingress --set rbac.create=true --set image.pullPolicy=Always

# Install Salesforce.com demo cloud native apps
# Note: don't forget to add the wildcard DNS entry to global.domain and point to external LB.
helm install sfdcdemo --namespace sfdcdemo --set global.domain=democluster.pks.pcfdemo.pcfapps.org --set image.pullPolicy=IfNotPresent --set global.registry=jigsheth57/ ./sfdcapps
