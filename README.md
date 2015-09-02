# cf-SFDCApp
Spring Cloud App integrated with SalesForce.com

![SFDC Web App](/document/main-app-screen02.png)

#Introduction
This repository holds a collection of micro-services that interacts with SalesForce.com to present a CRM application though a mobile friendly web UI. Also, uses API Gateway to expose APIs from collection of micro-services. The API Gateway can be used to write native mobile app.

#Architecture
The system is composed of 4 microservices plus a Config Server, a Service Discovery Server (Eureka), a Monitoring Dashboard (Hystrix), API Gateway and Web Application. The relationship between the microservices is illustrated below.

![SFDC Web App Architecture](/document/architecture.png)
