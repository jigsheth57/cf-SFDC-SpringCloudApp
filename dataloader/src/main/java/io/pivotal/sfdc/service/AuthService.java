package io.pivotal.sfdc.service;

import com.force.api.ApiSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    RestTemplate restTemplate;

	@Value("${sfdc.authservice.endpoint}")
    private String authserviceEP;

	private static String ACCESS_TOKEN = "access_token";

    private static String INSTANCE_URL = "instance_url";

    @Bean
    RestTemplate rest() {
      return new RestTemplate();
    }

	/**
	 * Retrieves the oauth2 token from remote authservice.
	 * @return ApiSession
	 */
	public ApiSession getApiSession() {
		logger.debug("Fetching ApiSession from authservice");
		ApiSession apiSession = restTemplate.getForObject(authserviceEP+"/oauth2", ApiSession.class);
		return apiSession;
	}
}
