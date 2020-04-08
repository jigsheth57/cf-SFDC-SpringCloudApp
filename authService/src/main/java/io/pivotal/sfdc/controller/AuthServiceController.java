package io.pivotal.sfdc.controller;

import com.force.api.ApiConfig;
import com.force.api.ApiSession;
import com.force.api.Auth;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import service.APISessionRefreshListener;

/**
 * REST Controller to authenticate against Salesforce.com to retrieve timebased oauth2 token.
 * 
 * @author Jignesh Sheth
 *
 */

@RestController
@RefreshScope
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthServiceController {

    @Autowired
    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> redisCommands;

	@Value("${USERNAME}")
    private String username;
    
    @Value("${PASSWORD}")
    private String password;
    
    @Value("${CLIENT_ID}")
    private String clientId;
    
    @Value("${CLIENT_SECRET}")
    private String clientSecret;

    private static String ACCESS_TOKEN = "access_token";

    private static String INSTANCE_URL = "instance_url";

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceController.class);

    /**
     * Retrieves SalesForce.com timebase oauth2 token and stores it into Redis with TTL
     * 
     * @return ApiSession
     */
    @RequestMapping(value = "/oauth2", method = RequestMethod.GET)
	@ApiOperation(value = "Retrieve SFDC oauth2 token", notes = "Retrieves SalesForce.com timebase oauth2 token and stores it into Redis with TTL", response = ApiSession.class)
    public @ResponseBody ApiSession oauth2() {
        return getApiSession();
    }
    
    public ApiSession getApiSession() {
		logger.debug("Fetching ApiSession");
        ApiSession apiSession = null;
        redisCommands = redisConnection.sync();

        logger.debug("Cache token will expire in {}ms.",redisCommands.pttl(ACCESS_TOKEN));
        if (redisCommands.exists(ACCESS_TOKEN,INSTANCE_URL) == 0) {
            logger.info("Retrieving new session.");
	    	ApiConfig apiconfig = new ApiConfig()
			.setUsername(username)
			.setPassword(password)
			.setClientId(clientId)
			.setClientSecret(clientSecret)
            .setSessionRefreshListener(new APISessionRefreshListener());
	    	apiSession = Auth.authenticate(apiconfig);
            Auth.revokeToken(apiconfig,apiSession.getAccessToken());
            apiSession = Auth.authenticate(apiconfig);
            redisCommands.set(ACCESS_TOKEN, apiSession.getAccessToken());
            redisCommands.expire(ACCESS_TOKEN,7200);
            redisCommands.set(INSTANCE_URL, apiSession.getApiEndpoint());
            redisCommands.expire(INSTANCE_URL,7200);
		} else {
            logger.debug("Retrieving token from cache.");
            apiSession = new ApiSession(redisCommands.get(ACCESS_TOKEN),redisCommands.get(INSTANCE_URL));
		}
		return apiSession;
    }

    /**
     * Retrieves SalesForce.com timebase oauth2 token and stores it into Redis with TTL
     *
     * @return ApiSession
     */
    @RequestMapping(value = "/invalidateSession", method = RequestMethod.GET)
    @ApiOperation(value = "Remove SFDC oauth2 token cache", notes = "Removes SalesForce.com oauth2 token from Redis", response = ApiSession.class)
    public @ResponseBody ApiSession invalidateSession() {
        return removeSession();
    }

    public ApiSession removeSession() {
        logger.debug("Remove ApiSession from cache");
        ApiSession apiSession = null;
        redisCommands = redisConnection.sync();
        ApiConfig apiconfig = new ApiConfig()
                .setUsername(username)
                .setPassword(password)
                .setClientId(clientId)
                .setClientSecret(clientSecret)
                .setSessionRefreshListener(new APISessionRefreshListener());
        try {
            Auth.revokeToken(apiconfig,redisCommands.get(ACCESS_TOKEN));
            redisCommands.del(ACCESS_TOKEN);
            redisCommands.del(INSTANCE_URL);
            apiSession = new ApiSession(redisCommands.get(ACCESS_TOKEN),redisCommands.get(INSTANCE_URL));
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
        return apiSession;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }
}
