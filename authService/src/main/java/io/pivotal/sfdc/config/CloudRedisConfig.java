package io.pivotal.sfdc.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("cloud")
public class CloudRedisConfig {

	@Value("${vcap.services.cache-service.credentials.host:spring.redis.host}")
	String redis_host;
	@Value("${vcap.services.cache-service.credentials.port:spring.redis.port}")
	String redis_port;
	@Value("${vcap.services.cache-service.credentials.password:spring.redis.password}")
	String redis_password;


	@Bean
	public RedisClient redisClient() {
	    return RedisClient.create(RedisURI.builder().withHost(redis_host).withPort(Integer.parseInt(redis_port)).withPassword(redis_password).build());
    }

    @Bean
    public StatefulRedisConnection<String, String> redisConnection() {
	    return redisClient().connect();
    }
//	@Bean
//	public JedisConnectionFactory jedisConnectionFactory() {
//		JedisConnectionFactory redisConnFactory = new JedisConnectionFactory();
//    	redisConnFactory.setHostName(redis_host);
//    	redisConnFactory.setPort(redis_port);
//
//    	return redisConnFactory;
//	}

}
