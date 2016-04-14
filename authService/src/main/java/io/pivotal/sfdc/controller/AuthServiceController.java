package io.pivotal.sfdc.controller;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.force.api.ApiConfig;
import com.force.api.ApiSession;
import com.force.api.Auth;

/**
 * REST Controller to authenticate against Salesforce.com to retrieve timebased oauth2 token.
 * 
 * @author Jignesh Sheth
 *
 */

@RestController
@RefreshScope
public class AuthServiceController {

	private StringRedisTemplate redisTemplate;
    
	@Resource
    private JedisConnectionFactory redisConnFactory;
    
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

    @PostConstruct
    public void init() {
		this.redisTemplate = new StringRedisTemplate(redisConnFactory);
    	logger.debug("HostName: "+redisConnFactory.getHostName());
    	logger.debug("Port: "+redisConnFactory.getPort());
    	logger.debug("Password: "+redisConnFactory.getPassword());
    }

    /**
     * Retrieves SalesForce.com timebase oauth2 token and stores it into Redis with TTL
     * 
     * @return ApiSession
     */
    @RequestMapping(value = "/oauth2", method = RequestMethod.GET)
    public @ResponseBody ApiSession oauth2() {
		logger.debug("Fetching ApiSession");

    	Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 2); // adds two hour
        ApiSession apiSession = null;
    	ValueOperations<String, String> ops = this.redisTemplate.opsForValue();
		if (!this.redisTemplate.hasKey(ACCESS_TOKEN)) {
	    	ApiConfig apiconfig = new ApiConfig()
			.setUsername(username)
			.setPassword(password)
			.setClientId(clientId)
			.setClientSecret(clientSecret);
	    	apiSession = Auth.authenticate(apiconfig);
			ops.set(ACCESS_TOKEN, apiSession.getAccessToken());
			this.redisTemplate.expireAt(ACCESS_TOKEN, cal.getTime());
			ops.setIfAbsent(INSTANCE_URL, apiSession.getApiEndpoint());
			this.redisTemplate.expireAt(INSTANCE_URL, cal.getTime());
		} else {
			apiSession = new ApiSession(ops.get(ACCESS_TOKEN),ops.get(INSTANCE_URL));
		}
        return apiSession;
    }
	
    @RequestMapping(value = "/credentials", method = RequestMethod.GET)
    public @ResponseBody String getCredentials() {
    	String jsonStr = "{\"username\": "+username+",\"password\":"+password+",\"clientId\":"+clientId+",\"clientSecret\":"+clientSecret+"}";
    	return jsonStr;
    }

}
