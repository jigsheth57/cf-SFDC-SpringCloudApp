#!/bin/bash
set -eu
mvn clean spring-boot:build-info package
pack set-default-builder cloudfoundry/cnb:cflinuxfs3
pack build jigsheth57/configserver --path configServer/target/configServer.jar --publish
pack build jigsheth57/eurekaserver --path eurekaServer/target/eurekaServer.jar --publish
pack build jigsheth57/hystrixdashboard --path hystrixDashboard/target/hystrixDashboard.jar --publish
pack build jigsheth57/authservice --path authService/target/authService.jar --publish
pack build jigsheth57/accountservice --path accountService/target/accountService.jar --publish
pack build jigsheth57/contactservice --path contactService/target/contactService.jar --publish
pack build jigsheth57/opportunityservice --path opportunityService/target/opportunityService.jar --publish
pack build jigsheth57/gatewayservice --path gatewayService/target/gatewayService.jar --publish
pack build jigsheth57/sfdcwebapp --path sfdcwebapp/target/sfdcwebapp.jar --publish
pack build jigsheth57/dataloader --path dataloader/target/dataloader.jar --publish
pack build jigsheth57/accountsource --path accountSource/target/accountSource.jar --publish
pack build jigsheth57/accountprocessor --path accountProcessor/target/accountProcessor.jar --publish
pack build jigsheth57/accountsink --path accountSink/target/accountSink.jar --publish

#docker push jigsheth57/configserver
#docker push jigsheth57/eurekaserver
#docker push jigsheth57/hystrixdashboard
#docker push jigsheth57/authservice
#docker push jigsheth57/accountservice
#docker push jigsheth57/contactservice
#docker push jigsheth57/opportunityservice
#docker push jigsheth57/gatewayservice
#docker push jigsheth57/sfdcwebapp
#docker push jigsheth57/dataloader
#docker push jigsheth57/accountsource
#docker push jigsheth57/accountprocessor
#docker push jigsheth57/accountsink
