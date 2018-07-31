package io.pivotal.sfdc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.hystrix.dashboard.EnableHystrixDashboard;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * The Spring configuration and entry point for
 * the application.
 * 
 * @author Jignesh Sheth
 *
 */



@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableHystrixDashboard
@Controller
public class HystrixDashboardApplication {
	
	@RequestMapping("/")
	public String home() {
		return "forward:/hystrix";
	}

    public static void main(String[] args) {
		SpringApplication.run(HystrixDashboardApplication.class, args);
    }
}
