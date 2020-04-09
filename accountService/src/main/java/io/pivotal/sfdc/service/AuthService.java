package io.pivotal.sfdc.service;

import com.force.api.ApiSession;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.client.AuthClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * SFDC Auth Service
 * This service calls authentication service to retrieve oauth2 token.
 * 
 * @author Jignesh Sheth
 *
 */

@Service
public class AuthService {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

    @Autowired
	AuthClient authClient;

	private static String ACCESS_TOKEN = "access_token";

    private static String INSTANCE_URL = "instance_url";

    @Bean
    @LoadBalanced
    RestTemplate rest() {
      return new RestTemplate();
    }

    /**
     * This method uses circuit breaker pattern to fallback and retrieves oauth2 token from redis.
     * 
     * @return ApiSession
     */
	public ApiSession getApiSessionFallback() {
		logger.info("Fetching fallback ApiSession with key: {}",ACCESS_TOKEN);
        ApiSession apiSession = null;
        if (redisCommands.exists(ACCESS_TOKEN,INSTANCE_URL) == 0) {
			apiSession = new ApiSession(redisCommands.get(ACCESS_TOKEN),redisCommands.get(INSTANCE_URL));
			logger.debug(redisCommands.get(ACCESS_TOKEN));
		} else
			throw new NullPointerException("ApiSession not found!");
    	
        return apiSession;
	}
	
	/**
	 * Retrieves the oauth2 token from remote authservice.
	 * @return ApiSession
	 */
	@HystrixCommand(fallbackMethod = "getApiSessionFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="1000")
		    })
	public ApiSession getApiSession() {
		logger.info("Fetching ApiSession from authservice");
		return authClient.getApiSession();
	}

	public ApiSession invalidateSession() {
		logger.info("Invalidating ApiSession from authservice");
		return authClient.invalidateSession();
	}
}
