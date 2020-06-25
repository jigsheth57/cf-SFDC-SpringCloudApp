package io.pivotal.sfdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * The angular and bootstrap web application
 * 
 * @author Jignesh Sheth
 *
 */

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SfdcwebappApplication {

    public static void main(String[] args) {
    	// CloudFoundryCertificateTruster.trustCertificates();
        SpringApplication.run(SfdcwebappApplication.class, args);
    }
}
