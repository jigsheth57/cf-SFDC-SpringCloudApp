package io.pivotal.sfdc.service;

import java.util.Calendar;
import java.util.Date;

import com.force.api.ApiSession;
import com.force.api.SessionRefreshListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.SFDC_Constant;

public class APISessionRefreshListener implements SessionRefreshListener {

    @Autowired
    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> redisCommands;

    private static final Logger LOGGER = LoggerFactory.getLogger(APISessionRefreshListener.class);

    @Override
    public void sessionRefreshed(ApiSession apiSession) {
        redisCommands = redisConnection.sync();
        LOGGER.debug("Session was refreshed! New access token: " + apiSession.getAccessToken());
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        redisCommands.set(SFDC_Constant.ACCESS_TOKEN, apiSession.getAccessToken(),
                SetArgs.Builder.ex(cal.getTime().getTime()));
        redisCommands.set(SFDC_Constant.INSTANCE_URL, apiSession.getApiEndpoint(),
                SetArgs.Builder.nx().ex(cal.getTime().getTime()));
    }
}
