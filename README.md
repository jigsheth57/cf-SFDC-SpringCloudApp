# Spring Cloud Example Project

This is an example project that demonstrates an end-to-end cloud-native platform using Spring Cloud for building a practical microservices architecture. This example integrated with SalesForce.com.

Demonstrated concepts:

* Integration testing using Docker
* Microservice architecture
* Configuration Server
* Service Registry
* API gateway
* Circuit Breaker Pattern

## Docker

Each service is built and deployed using Docker. End-to-end integration testing can be done on a developer's machine using Docker compose.
(See [running it locally](/running locally.adoc))

## SalesForce.com Integration App

The mobile friendly responsive web application allows user to manage SFDC domain model such as Account, Contact & Opportunity. The application support ability to create, retrieve, update & delete Account, Contact & Opportunity. Also supports association of Account to Contact and Account to Opportunity. If you like to setup your own developer SalesForce account, [click here for the instruction](/Configuring SalesForce Account.pdf).

![SFDC Web App](/document/main-app-screen02.png)
![SFDC Web App](/document/main-app-screen01.png)

### Architecture

This example project demonstrates how to build a new application using microservices, as opposed to a monolith-first strategy. Since each microservice in the project is a module of a single parent project, developers have the advantage of being able to run and develop with each microservice running on their local machine. Adding a new microservice is easy, as the discovery microservice will automatically discover new services running on the cluster.

In this example, the system is composed of four microservices (Auth, Account, Contact & Opportunity). Also, it leverages Market Place services such as Data Grid (Redis) and Spring Cloud Services (Config Server, Service Registry (Eureka), Circuit Breaker Dashboard (Hystrix), API Gateway (Zuul)). In addition, there are multiple Web Applications written to expose the backend microservices to show the power of polyglot language ([Spring Boot + AngularJS](/sfdcwebapp) & [NodeJS + AngularJS](/sfdc-web-app)) support by PCF.

The relationship between the microservices is illustrated below.

![SFDC Web App Architecture](/document/architecture.png)

### Service Registry

This project contains a [service registry](http://projects.spring.io/spring-cloud/spring-cloud.html#_spring_cloud_netflix), Netflix Eureka. The service allows microservices and API Gateway to discover each other location and health of the service. By leveraging Ribbon+Eureka, it can provide client side load balancing to microservice instances.

![Service Registry Dashboard](/document/service-discovery.png)

### Circuit Breaker Dashboard

This example project leverages [Circuit Breaker Dashboard](http://projects.spring.io/spring-cloud/spring-cloud.html#_circuit_breaker_hystrix_dashboard) to displays the health of each microservice in an efficient manner.

![Circuit Breaker Dashboard](/document/hystrix-monitor-01.png)
Figure 1. Circuit Breaker Dashboard

Below shows example of when exception is occured and circuit breaker is trigger and opened.

![Circuit Breaker Dashboard](/document/hystrix-monitor-02.png)
Figure 2. Circuit Breaker Dashboard during exception


### API gateway

Each microservice will coordinate with Eureka to retrieve API routes for the entire cluster. Using this strategy each microservice in a cluster can be load balanced and exposed through one API gateway. Each service will automatically discover and route API requests to the service that owns the route. This proxying technique is equally helpful when developing user interfaces, as the full API of the platform is available through its own host as a proxy. This enables de-coupling of UI and Backend Services and allows you to build various UI such as NodeJS, SpringBoot, .NET, iOS, etc.
