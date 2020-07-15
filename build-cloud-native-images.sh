#!/bin/bash
set -eu
mvn clean spring-boot:build-info package
pack set-default-builder cloudfoundry/cnb:cflinuxfs3
pack build jigsheth57/configserver:2.3.1 --path configServer/target/configServer.jar
pack build jigsheth57/authservice:2.3.1 --path authService/target/authService.jar
pack build jigsheth57/accountservice:2.3.1 --path accountService/target/accountService.jar
pack build jigsheth57/contactservice:2.3.1 --path contactService/target/contactService.jar
pack build jigsheth57/opportunityservice:2.3.1 --path opportunityService/target/opportunityService.jar
pack build jigsheth57/sfdcwebapp:2.3.1 --path sfdcwebapp/target/sfdcwebapp.jar

# pack build jigsheth57/gatewayservice --path gatewayService/target/gatewayService.jar --publish
# pack build jigsheth57/accountsource --path accountSource/target/accountSource.jar --publish
# pack build jigsheth57/accountprocessor --path accountProcessor/target/accountProcessor.jar --publish
# pack build jigsheth57/accountsink --path accountSink/target/accountSink.jar --publish

docker push jigsheth57/configserver:2.3.1
docker push jigsheth57/authservice:2.3.1
docker push jigsheth57/accountservice:2.3.1
docker push jigsheth57/contactservice:2.3.1
docker push jigsheth57/opportunityservice:2.3.1
docker push jigsheth57/sfdcwebapp:2.3.1
#docker push jigsheth57/gatewayservice
#docker push jigsheth57/accountsource
#docker push jigsheth57/accountprocessor
#docker push jigsheth57/accountsink
