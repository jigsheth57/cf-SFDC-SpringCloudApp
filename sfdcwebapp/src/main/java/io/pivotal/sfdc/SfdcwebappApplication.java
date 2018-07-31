package io.pivotal.sfdc;

import io.pivotal.springcloud.ssl.CloudFoundryCertificateTruster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * The angular and bootstrap web application
 * 
 * @author Jignesh Sheth
 *
 */

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
@EnableDiscoveryClient
public class SfdcwebappApplication {

    public static void main(String[] args) {
    	CloudFoundryCertificateTruster.trustCertificates();
        SpringApplication.run(SfdcwebappApplication.class, args);
    }
}
