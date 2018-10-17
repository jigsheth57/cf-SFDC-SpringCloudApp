mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=configServer -Dversion=1.0.0 -Dfile=../SFDCApp/configServer/target/configServer.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=eurekaServer -Dversion=1.0.0 -Dfile=../SFDCApp/eurekaServer/target/eurekaServer.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=hystrixDashboard -Dversion=1.0.0 -Dfile=../SFDCApp/hystrixDashboard/target/hystrixDashboard.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=domainObjs -Dversion=1.0.0 -Dfile=../SFDCApp/domainObjs/target/domainObjs.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=authService -Dversion=1.0.0 -Dfile=../SFDCApp/authService/target/authService.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=accountService -Dversion=1.0.0 -Dfile=../SFDCApp/accountService/target/accountService.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=contactService -Dversion=1.0.0 -Dfile=../SFDCApp/contactService/target/contactService.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=opportunityService -Dversion=1.0.0 -Dfile=../SFDCApp/opportunityService/target/opportunityService.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=gatewayService -Dversion=1.0.0 -Dfile=../SFDCApp/gatewayService/target/gatewayService.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=sfdcwebapp -Dversion=1.0.0 -Dfile=../SFDCApp/sfdcwebapp/target/sfdcwebapp.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true

mvn install:install-file -DgroupId=io.pivotal.sfdc -DartifactId=dataloader -Dversion=1.0.0 -Dfile=../SFDCApp/dataloader/target/dataloader.jar -Dpackaging=jar -DgeneratePom=true -DlocalRepositoryPath=.  -DcreateChecksum=true
