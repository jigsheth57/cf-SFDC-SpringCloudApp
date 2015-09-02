#!/bin/bash

cf t
cf d -r authService
cf d -r accountService
cf d -r contactService
cf d -r opportunityService
cf d -r sfdcapigateway
#cf d -r sfdcnodewebapp
cf d -r sfdcbootwebapp
cf d -r configserver
cf ds sfdcgateway -f
cf ds data-grid-service -f
cf ds service-registry -f
cf ds circuit-breaker-dashboard -f
cf ds circuit-breaker-dashboard -f
cf a
cf s
