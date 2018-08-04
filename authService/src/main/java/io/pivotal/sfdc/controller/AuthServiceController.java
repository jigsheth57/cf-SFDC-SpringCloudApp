package io.pivotal.sfdc.controller;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.force.api.ApiConfig;
import com.force.api.ApiSession;
import com.force.api.Auth;

import io.swagger.annotations.ApiOperation;

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

	@Value("${sfdc.uid}")
    private String username;
    
    @Value("${sfdc.pwd}")
    private String password;
    
    @Value("${sfdc.client_id}")
    private String clientId;
    
    @Value("${sfdc.client_key}")
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

        if (redisCommands.exists(ACCESS_TOKEN,INSTANCE_URL) == 0) {
	    	ApiConfig apiconfig = new ApiConfig()
			.setUsername(username)
			.setPassword(password)
			.setClientId(clientId)
			.setClientSecret(clientSecret);
	    	apiSession = Auth.authenticate(apiconfig);
			Calendar cal = Calendar.getInstance(); // creates calendar
			cal.setTime(new Date()); // sets calendar time/date
			cal.add(Calendar.HOUR_OF_DAY, 2); // adds two hour
            redisCommands.set(ACCESS_TOKEN, apiSession.getAccessToken(),SetArgs.Builder.ex(cal.getTime().getTime()));
            redisCommands.set(INSTANCE_URL, apiSession.getApiEndpoint(), SetArgs.Builder.nx().ex(cal.getTime().getTime()));
		} else {
            apiSession = new ApiSession(redisCommands.get(ACCESS_TOKEN),redisCommands.get(INSTANCE_URL));
		}
		return apiSession;
    }
}
