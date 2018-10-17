#!/bin/bash
set -eu

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=sfdcapp -Dversion=1.0.0 -Dfile=../SFDCApp/pom.xml -Dpackaging=pom -DpomFile=../SFDCApp/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=configServer -Dversion=1.0.0 -Dfile=../SFDCApp/configServer/target/configServer.jar -Dpackaging=jar -DpomFile=../SFDCApp/configServer/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=eurekaServer -Dversion=1.0.0 -Dfile=../SFDCApp/eurekaServer/target/eurekaServer.jar -Dpackaging=jar -DpomFile=../SFDCApp/eurekaServer/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=hystrixDashboard -Dversion=1.0.0 -Dfile=../SFDCApp/hystrixDashboard/target/hystrixDashboard.jar -Dpackaging=jar -DpomFile=../SFDCApp/hystrixDashboard/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=domainObjs -Dversion=1.0.0 -Dfile=../SFDCApp/domainObjs/target/domainObjs.jar -Dpackaging=jar -DpomFile=../SFDCApp/domainObjs/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=authService -Dversion=1.0.0 -Dfile=../SFDCApp/authService/target/authService.jar -Dpackaging=jar -DpomFile=../SFDCApp/authService/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=accountService -Dversion=1.0.0 -Dfile=../SFDCApp/accountService/target/accountService.jar -Dpackaging=jar -DpomFile=../SFDCApp/accountService/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=contactService -Dversion=1.0.0 -Dfile=../SFDCApp/contactService/target/contactService.jar -Dpackaging=jar -DpomFile=../SFDCApp/contactService/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=opportunityService -Dversion=1.0.0 -Dfile=../SFDCApp/opportunityService/target/opportunityService.jar -Dpackaging=jar -DpomFile=../SFDCApp/opportunityService/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=gatewayService -Dversion=1.0.0 -Dfile=../SFDCApp/gatewayService/target/gatewayService.jar -Dpackaging=jar -DpomFile=../SFDCApp/gatewayService/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=sfdcwebapp -Dversion=1.0.0 -Dfile=../SFDCApp/sfdcwebapp/target/sfdcwebapp.jar -Dpackaging=jar -DpomFile=../SFDCApp/sfdcwebapp/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=dataloader -Dversion=1.0.0 -Dfile=../SFDCApp/dataloader/target/dataloader.jar -Dpackaging=jar -DpomFile=../SFDCApp/dataloader/pom.xml -DlocalRepositoryPath=.  -DcreateChecksum=true
