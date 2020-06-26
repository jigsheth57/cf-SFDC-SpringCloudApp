package io.pivotal.sfdc.controller;

import com.force.api.ApiConfig;
import com.force.api.ApiSession;
import com.force.api.Auth;

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

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.SFDC_Constant;
import io.pivotal.sfdc.service.APISessionRefreshListener;
import io.swagger.annotations.ApiOperation;

/**
 * REST Controller to authenticate against Salesforce.com to retrieve timebased
 * oauth2 token.
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

    private static final Logger LOGGER = LoggerFactory.getLogger(AuthServiceController.class);

    /**
     * Retrieves SalesForce.com timebase oauth2 token and stores it into Redis with
     * TTL
     * 
     * @return ApiSession
     */
    @RequestMapping(value = "/oauth2", method = RequestMethod.GET)
    @ApiOperation(value = "Retrieve SFDC oauth2 token", notes = "Retrieves SalesForce.com timebase oauth2 token and stores it into Redis with TTL", response = ApiSession.class)
    public @ResponseBody ApiSession oauth2() {
        return getApiSession();
    }

    ApiSession getApiSession() {
        LOGGER.debug("Fetching ApiSession");
        ApiSession apiSession = null;
        redisCommands = redisConnection.sync();

        LOGGER.debug("Cache token will expire in {}ms.", redisCommands.pttl(SFDC_Constant.ACCESS_TOKEN));
        if (redisCommands.exists(SFDC_Constant.ACCESS_TOKEN, SFDC_Constant.INSTANCE_URL) == 0) {
            LOGGER.info("Retrieving new session.");
            ApiConfig apiconfig = new ApiConfig().setUsername(username).setPassword(password).setClientId(clientId)
                    .setClientSecret(clientSecret).setSessionRefreshListener(new APISessionRefreshListener());
            apiSession = Auth.authenticate(apiconfig);
            Auth.revokeToken(apiconfig, apiSession.getAccessToken());
            apiSession = Auth.authenticate(apiconfig);
            redisCommands.set(SFDC_Constant.ACCESS_TOKEN, apiSession.getAccessToken());
            redisCommands.expire(SFDC_Constant.ACCESS_TOKEN, 7200);
            redisCommands.set(SFDC_Constant.INSTANCE_URL, apiSession.getApiEndpoint());
            redisCommands.expire(SFDC_Constant.INSTANCE_URL, 7200);
        } else {
            LOGGER.debug("Retrieving token from cache.");
            apiSession = new ApiSession(redisCommands.get(SFDC_Constant.ACCESS_TOKEN),
                    redisCommands.get(SFDC_Constant.INSTANCE_URL));
        }
        return apiSession;
    }

    /**
     * Retrieves SalesForce.com timebase oauth2 token and stores it into Redis with
     * TTL
     *
     * @return ApiSession
     */
    @RequestMapping(value = "/invalidateSession", method = RequestMethod.GET)
    @ApiOperation(value = "Remove SFDC oauth2 token cache", notes = "Removes SalesForce.com oauth2 token from Redis", response = ApiSession.class)
    public @ResponseBody ApiSession invalidateSession() {
        return removeSession();
    }

    ApiSession removeSession() {
        LOGGER.debug("Remove ApiSession from cache");
        ApiSession apiSession = null;
        redisCommands = redisConnection.sync();
        ApiConfig apiconfig = new ApiConfig().setUsername(username).setPassword(password).setClientId(clientId)
                .setClientSecret(clientSecret).setSessionRefreshListener(new APISessionRefreshListener());
        try {
            Auth.revokeToken(apiconfig, redisCommands.get(SFDC_Constant.ACCESS_TOKEN));
            redisCommands.del(SFDC_Constant.ACCESS_TOKEN);
            redisCommands.del(SFDC_Constant.INSTANCE_URL);
            apiSession = new ApiSession(redisCommands.get(SFDC_Constant.ACCESS_TOKEN),
                    redisCommands.get(SFDC_Constant.INSTANCE_URL));
        } catch (Exception e) {
            LOGGER.warn(e.getMessage());
        }
        return apiSession;
    }

    /**
     * Retrieve new session and invalidates existing session
     */
    public void refreshSession() {
        removeSession();
        getApiSession();
    }
}
