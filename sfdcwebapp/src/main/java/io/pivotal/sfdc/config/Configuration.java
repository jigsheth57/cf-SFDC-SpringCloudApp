package io.pivotal.sfdc.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;

public class Configuration {

    @org.springframework.context.annotation.Configuration
    @Profile("local")
    static class LocalConfiguration {
        @Value("${spring.redis.host:redis}")
        String redis_host;
        @Value("${spring.redis.port:6379}")
        int redis_port;

        private static final Logger LOGGER = LoggerFactory.getLogger(LocalConfiguration.class);

        @Bean
        @Primary
        public RedisClient redisClient() {
            LOGGER.debug("spring.redis.host: {}", redis_host);
            LOGGER.debug("spring.redis.port: {}", redis_port);
            return RedisClient.create(RedisURI.builder().withHost(redis_host).withPort(redis_port).build());
        }

        @Bean
        @Primary
        public StatefulRedisConnection<String, String> redisConnection() {
            return redisClient().connect();
        }
    }
}
