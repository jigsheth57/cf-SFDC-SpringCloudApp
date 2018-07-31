package io.pivotal.sfdc;

import io.pivotal.springcloud.ssl.CloudFoundryCertificateTruster;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@SpringBootApplication
@EnableAutoConfiguration
@EnableDiscoveryClient
@EnableHystrix
public class GatewayApplication {


    @Bean
    SecurityWebFilterChain springWebFilterChain(ServerHttpSecurity http) throws Exception {
        return http.httpBasic().and()
                .csrf().disable()
                .authorizeExchange()
                .anyExchange().permitAll()
                .and()
                .build();
    }

    public static void main(String[] args) {
    	CloudFoundryCertificateTruster.trustCertificates();
        SpringApplication.run(GatewayApplication.class, args);
    }
}
