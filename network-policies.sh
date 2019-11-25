#!/bin/bash
cf add-network-policy portal --destination-app gatewayservice --protocol tcp --port 8080
cf add-network-policy gatewayservice --destination-app authservice --protocol tcp --port 8080
cf add-network-policy accountservice --destination-app authservice --protocol tcp --port 8080
cf add-network-policy accountprocessor --destination-app authservice --protocol tcp --port 8080
cf add-network-policy contactservice --destination-app authservice --protocol tcp --port 8080
cf add-network-policy opportunityservice --destination-app authservice --protocol tcp --port 8080
cf add-network-policy gatewayservice --destination-app accountservice --protocol tcp --port 8080
cf add-network-policy gatewayservice --destination-app contactservice --protocol tcp --port 8080
cf add-network-policy gatewayservice --destination-app opportunityservice --protocol tcp --port 8080
cf add-network-policy dataloader --destination-app authservice --protocol tcp --port 8080

cf add-network-policy portal --destination-app gatewayservice --protocol tcp --port 61001
cf add-network-policy gatewayservice --destination-app authservice --protocol tcp --port 61001
cf add-network-policy accountservice --destination-app authservice --protocol tcp --port 61001
cf add-network-policy accountprocessor --destination-app authservice --protocol tcp --port 61001
cf add-network-policy contactservice --destination-app authservice --protocol tcp --port 61001
cf add-network-policy opportunityservice --destination-app authservice --protocol tcp --port 61001
cf add-network-policy gatewayservice --destination-app accountservice --protocol tcp --port 61001
cf add-network-policy gatewayservice --destination-app contactservice --protocol tcp --port 61001
cf add-network-policy gatewayservice --destination-app opportunityservice --protocol tcp --port 61001
cf add-network-policy dataloader --destination-app authservice --protocol tcp --port 61001
