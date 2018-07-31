#!/bin/bash

cf t
echo -n "Validate the space & org, you are currently logged in before continuing!"
read
echo yes | cf d -r authservice
echo yes | cf d -r accountservice
echo yes | cf d -r contactservice
echo yes | cf d -r opportunityservice
echo yes | cf d -r gatewayservice
echo yes | cf d -r portal
echo yes | cf d -r dataloader

cf ds scheduler-dataloader -f
cf ds cache-service -f
cf ds event-bus -f
cf ds circuit-breaker -f
cf ds service-registry -f
cf ds config-server -f
cf a
cf s
