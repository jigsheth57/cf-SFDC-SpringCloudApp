package io.pivotal.sfdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.function.FunctionConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

/**
 * The react web application
 * 
 * @author Jignesh Sheth
 *
 */

@SpringBootApplication(exclude = FunctionConfiguration.class)
public class SfdcwebappApplication {

    @Bean
    RestTemplate rest() {
        return new RestTemplate();
    }

    public static void main(String[] args) {
        SpringApplication.run(SfdcwebappApplication.class, args);
    }
}
