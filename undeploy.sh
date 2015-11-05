#!/bin/bash

cf t
echo yes | cf d -r authService
echo yes | cf d -r accountService
echo yes | cf d -r contactService
echo yes | cf d -r opportunityService
echo yes | cf d -r sfdcapigateway
#cf d -r sfdcnodewebapp
echo yes | cf d -r sfdcbootwebapp
echo yes | cf d -r configserver
cf ds sfdcgateway -f
cf ds data-grid-service -f
cf ds service-registry -f
cf ds circuit-breaker-dashboard -f
cf ds circuit-breaker-dashboard -f
cf a
cf s
