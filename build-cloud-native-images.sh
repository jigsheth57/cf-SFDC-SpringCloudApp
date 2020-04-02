#!/bin/bash
set -eu
mvn clean package
pack set-default-builder cloudfoundry/cnb:cflinuxfs3
pack build jigsheth57/configserver --path configServer/target/configServer.jar
pack build jigsheth57/eurekaserver --path eurekaServer/target/eurekaServer.jar
pack build jigsheth57/hystrixdashboard --path hystrixDashboard/target/hystrixDashboard.jar
pack build jigsheth57/authservice --path authService/target/authService.jar
pack build jigsheth57/accountservice --path accountService/target/accountService.jar
pack build jigsheth57/contactservice --path contactService/target/contactService.jar
pack build jigsheth57/opportunityservice --path opportunityService/target/opportunityService.jar
pack build jigsheth57/gatewayservice --path gatewayService/target/gatewayService.jar
pack build jigsheth57/sfdcwebapp --path sfdcwebapp/target/sfdcwebapp.jar
pack build jigsheth57/dataloader --path dataloader/target/dataloader.jar
pack build jigsheth57/accountsource --path accountSource/target/accountSource.jar
pack build jigsheth57/accountprocessor --path accountProcessor/target/accountProcessor.jar
pack build jigsheth57/accountsink --path accountSink/target/accountSink.jar

docker push jigsheth57/configserver
docker push jigsheth57/eurekaserver
docker push jigsheth57/hystrixdashboard
docker push jigsheth57/authservice
docker push jigsheth57/accountservice
docker push jigsheth57/contactservice
docker push jigsheth57/opportunityservice
docker push jigsheth57/gatewayservice
docker push jigsheth57/sfdcwebapp
docker push jigsheth57/dataloader
docker push jigsheth57/accountsource
docker push jigsheth57/accountprocessor
docker push jigsheth57/accountsink
