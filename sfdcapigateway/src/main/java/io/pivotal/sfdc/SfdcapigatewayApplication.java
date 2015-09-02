package io.pivotal.sfdc;

import io.pivotal.sfdc.zuul.filters.post.FallBackFilter;
import io.pivotal.springcloud.ssl.CloudFoundryCertificateTruster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.circuitbreaker.EnableCircuitBreaker;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableCircuitBreaker
@EnableZuulProxy
public class SfdcapigatewayApplication {

    public static void main(String[] args) {
    	CloudFoundryCertificateTruster.trustCertificates();
        SpringApplication.run(SfdcapigatewayApplication.class, args);
    }
    
    @Bean
    public FallBackFilter fallBackFilter() {
        return new FallBackFilter();
    }
}
