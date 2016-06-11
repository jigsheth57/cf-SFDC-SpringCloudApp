package io.pivotal.sfdc.service;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import com.force.api.ApiConfig;
import com.force.api.ApiSession;
import com.force.api.Auth;

/**
 * SFDC Auth Service
 * This service calls authentication service to retrieve oauth2 token.
 * 
 * @author Jignesh Sheth
 *
 */

@Service
@EnableConfigurationProperties
public class AuthService {

	private static final Logger logger = LoggerFactory
			.getLogger(AuthService.class);
	
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
    
    @PostConstruct
    public void init() {
		this.redisTemplate = new StringRedisTemplate(redisConnFactory);
    	logger.debug("HostName: "+redisConnFactory.getHostName());
    	logger.debug("Port: "+redisConnFactory.getPort());
    	logger.debug("Password: "+redisConnFactory.getPassword());
    }

    /**
     * Retrieves SalesForce.com oauth2 token.
     * 
     * @return ApiSession
     */
	public ApiSession getApiSession() {
		logger.debug("Fetching ApiSession from authservice");
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
	
}
