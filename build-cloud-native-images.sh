#!/bin/bash
set -eu

pack build jigsheth57/configserver:latest --path ./configServer --publish

pack build jigsheth57/eurekaserver:latest --path ./eurekaServer --publish

pack build jigsheth57/hystrixdashboard:latest --path ./hystrixDashboard --publish

pack build jigsheth57/authservice:latest --path ./authService --publish

pack build jigsheth57/accountservice:latest --path ./accountService --publish

pack build jigsheth57/contactservice:latest --path ./contactService --publish

pack build jigsheth57/opportunityservice:latest --path ./opportunityService --publish

pack build jigsheth57/gatewayservice:latest --path ./gatewayService --publish

pack build jigsheth57/sfdcwebapp:latest --path ./sfdcwebapp --publish

pack build jigsheth57/dataloader:latest --path ./dataloader --publish
