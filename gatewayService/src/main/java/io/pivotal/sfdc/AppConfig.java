package io.pivotal.sfdc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@Profile("local")
public class AppConfig {

	@Value("${spring.redis.host:localhost}")
	String redis_host;
	@Value("${spring.redis.port:6379}")
	int redis_port;

	@Bean
	public JedisConnectionFactory jedisConnectionFactory() {
		JedisConnectionFactory redisConnFactory = new JedisConnectionFactory();
    	redisConnFactory.setHostName(redis_host);
    	redisConnFactory.setPort(redis_port);
    	
    	return redisConnFactory;
	}
}
