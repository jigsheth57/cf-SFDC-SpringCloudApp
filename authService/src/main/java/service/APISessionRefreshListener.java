package service;

import com.force.api.ApiSession;
import com.force.api.SessionRefreshListener;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

public class APISessionRefreshListener implements SessionRefreshListener {

    @Autowired
    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> redisCommands;

    private static String ACCESS_TOKEN = "access_token";

    private static String INSTANCE_URL = "instance_url";

    private static final Logger logger = LoggerFactory.getLogger(APISessionRefreshListener.class);
    @Override
    public void sessionRefreshed(ApiSession apiSession) {
        redisCommands = redisConnection.sync();
        logger.info("Session was refreshed! New access token: "+apiSession.getAccessToken());
        Calendar cal = Calendar.getInstance(); // creates calendar
        cal.setTime(new Date()); // sets calendar time/date
        cal.add(Calendar.HOUR_OF_DAY, 1); // adds one hour
        redisCommands.set(ACCESS_TOKEN, apiSession.getAccessToken(), SetArgs.Builder.ex(cal.getTime().getTime()));
        redisCommands.set(INSTANCE_URL, apiSession.getApiEndpoint(), SetArgs.Builder.nx().ex(cal.getTime().getTime()));

    }
}
