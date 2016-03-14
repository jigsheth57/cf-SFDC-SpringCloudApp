# Spring Cloud Example Project

This is an example project that demonstrates an end-to-end cloud-native platform using Spring Cloud for building a practical microservices architecture. This example integrated with SalesForce.com.

Demonstrated concepts:

* Integration testing using Docker
* Data Grid Caching
* Microservice architecture
* Service discovery
* API gateway
* Circuit Breaker Pattern

## Docker

Each service is built and deployed using Docker. End-to-end integration testing can be done on a developer's machine using Docker compose.
(See ![running it locally](/running-it-locally.adoc)

## SalesForce.com Integration App

The mobile friendly responsive web application allows user to manage SFDC domain model such as Account, Contact & Opportunity. The application support ability to create, retrieve, update & delete Account, Contact & Opportunity. Also supports association of Account to Contact and Account to Opportunity.

![SFDC Web App](/document/main-app-screen02.png)
![SFDC Web App](/document/main-app-screen01.png)

#Introduction
This repository holds a collection of micro-services that interacts with SalesForce.com to present a CRM application though a mobile friendly web UI. Also, uses API Gateway to expose APIs from collection of micro-services. The API Gateway can be used to write native mobile app.

### Architecture
The system is composed of four microservices (Auth, Account, Contact & Opportunity). Also, it leverages Market Place services such as Data Grid (Redis) and Spring Cloud Services (Config Server, Service Discovery Server (Eureka), Circuit Breaker & Monitoring Dashboard (Hystrix), API Gateway (Zuul)). In addition, there are multiple Web Applications written to expose the backend microservices to show the power of polygot language (![Spring Boot](/sfdcwebapp) & ![NodeJS](/sfdc-web-app)) support by PCF.

The relationship between the microservices is illustrated below.

![SFDC Web App Architecture](/document/architecture.png)
