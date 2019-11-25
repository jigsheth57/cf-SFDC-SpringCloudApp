package io.pivotal.sfdc.service;

import com.force.api.ApiSession;
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

	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

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
	 * Retrieves the oauth2 token from remote authservice.
	 * @return ApiSession
	 */
	public ApiSession getApiSession() {
		logger.info("Fetching ApiSession from authservice");
		return authClient.getApiSession();
	}
}
