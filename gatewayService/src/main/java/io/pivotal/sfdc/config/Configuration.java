package io.pivotal.sfdc.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;

public class Configuration {

    @org.springframework.context.annotation.Configuration
    @Profile("cloud")
    static class CloudConfiguration {
        @Value("${vcap.services.cache-service.credentials.host:spring.redis.host}")
        String redis_host;
        @Value("${vcap.services.cache-service.credentials.port:spring.redis.port}")
        String redis_port;
        @Value("${vcap.services.cache-service.credentials.password:spring.redis.password}")
        String redis_password;

        private static final Logger logger = LoggerFactory.getLogger(CloudConfiguration.class);

        @Bean
        @Primary
        public RedisClient redisClient() {
            logger.debug("spring.redis.host: {}",redis_host);
            logger.debug("spring.redis.port: {}",redis_port);
            logger.debug("spring.redis.password: {}",redis_password);
            return RedisClient.create(RedisURI.builder().withHost(redis_host).withPort(Integer.parseInt(redis_port)).withPassword(redis_password).build());
        }

        @Bean
        @Primary
        public StatefulRedisConnection<String, String> redisConnection() {
            return redisClient().connect();
        }

        @Bean
        @Primary
        public org.springframework.data.redis.connection.ReactiveRedisConnectionFactory reactiveRedisConnectionFactory() {
            RedisStandaloneConfiguration rsc = new RedisStandaloneConfiguration(redis_host,Integer.parseInt(redis_port));
            rsc.setPassword(RedisPassword.of(redis_password));
            LettuceConnectionFactory lcf = new LettuceConnectionFactory(rsc);

            return lcf;
        }
    }

    @org.springframework.context.annotation.Configuration
    @Profile("local")
    static class LocalConfiguration {
        @Value("${spring.redis.host:redis}")
        String redis_host;
        @Value("${spring.redis.port:6379}")
        int redis_port;

        private static final Logger logger = LoggerFactory.getLogger(LocalConfiguration.class);

        @Bean
        @Primary
        public RedisClient redisClient() {
            logger.debug("spring.redis.host: {}",redis_host);
            logger.debug("spring.redis.port: {}",redis_port);
            return RedisClient.create(RedisURI.builder().withHost(redis_host).withPort(redis_port).build());
        }

        @Bean
        @Primary
        public StatefulRedisConnection<String, String> redisConnection() {
            return redisClient().connect();
        }
    }
}
