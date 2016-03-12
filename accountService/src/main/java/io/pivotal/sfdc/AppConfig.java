package io.pivotal.sfdc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;

@Configuration
@ConditionalOnClass(RedisAutoConfiguration.class)
@AutoConfigureAfter
public class AppConfig {

	@Value("${spring.redis.host}")
	String redis_host;
	@Value("${spring.redis.port}")
	int redis_port;
	@Value("${spring.redis.password}")
	String redis_password;

	@Bean
	@ConditionalOnMissingBean(JedisConnectionFactory.class)
	@ConditionalOnProperty(prefix = "spring.redis", name = "create-jedis-connection-factory", matchIfMissing = true)
	public JedisConnectionFactory getJedisConnectionFactory() {
		JedisConnectionFactory redisConnFactory = new JedisConnectionFactory();
    	redisConnFactory.setHostName(redis_host);
    	redisConnFactory.setPort(redis_port);
    	if(!redis_password.equalsIgnoreCase("guest")) {
    		redisConnFactory.setPassword(redis_password);
    	}
    	return redisConnFactory;
	}

	@Bean
	@ConditionalOnMissingBean(RedisAutoConfiguration.class)
	@ConditionalOnProperty(prefix = "spring.redis", name = "create-jedis-connection-factory", matchIfMissing = true)
	public RedisAutoConfiguration getRedisAutoConfiguration() {
		RedisAutoConfiguration redisAutoConfig = new RedisAutoConfiguration();
    	RedisProperties redisProp = redisAutoConfig.redisProperties();
    	redisProp.setHost(redis_host);
    	redisProp.setPort(redis_port);
    	if(!redis_password.equalsIgnoreCase("guest")) {
    		redisProp.setPassword(redis_password);
    	}
    	return redisAutoConfig;
	}
}
