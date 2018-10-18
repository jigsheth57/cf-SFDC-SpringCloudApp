#!/bin/bash
set -eu

pack build jigsheth57/configserver:latest --path ./configServer

pack build jigsheth57/eurekaserver:latest --path ./eurekaServer

pack build jigsheth57/hystrixdashboard:latest --path ./hystrixDashboard

pack build jigsheth57/authservice:latest --path ./authService

pack build jigsheth57/accountservice:latest --path ./accountService

pack build jigsheth57/contactservice:latest --path ./contactService

pack build jigsheth57/opportunityservice:latest --path ./opportunityService

pack build jigsheth57/gatewayservice:latest --path ./gatewayService

pack build jigsheth57/sfdcwebapp:latest --path ./sfdcwebapp

pack build jigsheth57/dataloader:latest --path ./dataloader 
