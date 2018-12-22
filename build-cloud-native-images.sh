#!/bin/bash
set -eu

pack build jigsheth57/configserver:pack --path ./configServer

pack build jigsheth57/eurekaserver:pack --path ./eurekaServer

pack build jigsheth57/hystrixdashboard:pack --path ./hystrixDashboard

pack build jigsheth57/authservice:pack --path ./authService

pack build jigsheth57/accountservice:pack --path ./accountService

pack build jigsheth57/contactservice:pack --path ./contactService

pack build jigsheth57/opportunityservice:pack --path ./opportunityService

pack build jigsheth57/gatewayservice:pack --path ./gatewayService

pack build jigsheth57/sfdcwebapp:pack --path ./sfdcwebapp

pack build jigsheth57/dataloader:pack --path ./dataloader 
