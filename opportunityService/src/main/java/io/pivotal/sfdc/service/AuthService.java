package io.pivotal.sfdc.service;

import com.force.api.ApiSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.SFDC_Constant;

/**
 * SFDC Auth Service This service calls authentication service to retrieve
 * oauth2 token.
 * 
 * @author Jignesh Sheth
 *
 */

@Service
public class AuthService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

	@Value("${sfdc.authservice.endpoint}")
	private String authServiceEP;

	@Bean
	@LoadBalanced
	RestTemplate rest() {
		return new RestTemplate();
	}

	@Autowired
	RestTemplate restTemplate;

	/**
	 * This method uses circuit breaker pattern to fallback and retrieves oauth2
	 * token from redis.
	 * 
	 * @return ApiSession
	 */
	public ApiSession getApiSessionFallback() {
		LOGGER.info("Fetching fallback ApiSession with key: {}", SFDC_Constant.ACCESS_TOKEN);
		ApiSession apiSession = null;
		if (redisCommands.exists(SFDC_Constant.ACCESS_TOKEN, SFDC_Constant.INSTANCE_URL) == 0) {
			apiSession = new ApiSession(redisCommands.get(SFDC_Constant.ACCESS_TOKEN),
					redisCommands.get(SFDC_Constant.INSTANCE_URL));
			LOGGER.debug(redisCommands.get(SFDC_Constant.ACCESS_TOKEN));
		} else
			throw new NullPointerException("ApiSession not found!");

		return apiSession;
	}

	/**
	 * Retrieves the oauth2 token from remote authservice.
	 * 
	 * @return ApiSession
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getApiSessionFallback")
	public ApiSession getApiSession() {
		LOGGER.info("Fetching ApiSession from authservice {}", authServiceEP);
		ApiSession apiSession = restTemplate.getForObject(authServiceEP + "/oauth2", ApiSession.class);
		return apiSession;
	}

	public ApiSession invalidateSession() {
		LOGGER.info("Invalidating ApiSession from authservice {}", authServiceEP);
		ApiSession apiSession = restTemplate.getForObject(authServiceEP + "/invalidateSession", ApiSession.class);
		return apiSession;
	}
}
