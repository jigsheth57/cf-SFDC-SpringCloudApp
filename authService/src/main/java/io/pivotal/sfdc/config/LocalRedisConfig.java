package io.pivotal.sfdc.config;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.api.StatefulRedisConnection;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("local")
public class LocalRedisConfig {

	@Value("${spring.redis.host:redis}")
	String redis_host;
	@Value("${spring.redis.port:6379}")
	int redis_port;


	@Bean
	public RedisClient redisClient() {
	    return RedisClient.create(RedisURI.builder().withHost(redis_host).withPort(redis_port).build());
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
