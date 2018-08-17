package io.pivotal.sfdc.actuator;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.domain.SFDCStatus;
import io.pivotal.sfdc.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URL;

@Component
public class SFDCHealthIndicator extends AbstractHealthIndicator {

    @Value("${sfdc.service.unavailable}")
    private String unavailable;

    @Resource(name="redisConnection")
    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> redisCommands;

    private static final Logger logger = LoggerFactory.getLogger(SFDCHealthIndicator.class);

    private static String INSTANCE_URL = "instance_url";

    private static String STATUS_URL = "https://api.status.salesforce.com/v1/instances/";

    RestTemplate restTemplate;

    private String statusEP = null;

    @Autowired
    AuthService authService;

    @Autowired
    public SFDCHealthIndicator() {
        restTemplate = new RestTemplate();
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        redisCommands = redisConnection.sync();
        String cachedata = unavailable;
        String host = null;
        try {
            cachedata = redisCommands.get(INSTANCE_URL);
            if(cachedata == null || cachedata.isEmpty()) {
                cachedata = unavailable;
                authService.getApiSession();
            } else {
                URL url = new URL(cachedata);
                host = url.getHost();
                host = host.substring(0,host.indexOf("."));
                statusEP = STATUS_URL+host.toUpperCase()+"/status";
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            authService.getApiSession();
        }
        if (statusEP != null) {
            logger.debug("statusEP: {}",statusEP);
            try {
                SFDCStatus sfdcStatus = restTemplate.getForObject(statusEP,SFDCStatus.class);
                if (sfdcStatus.getActive() && sfdcStatus.getStatus().equalsIgnoreCase("ok")) {
                    builder.up()
                            .withDetail("host",host.toLowerCase())
                            .withDetail("isActive",sfdcStatus.getActive())
                            .withDetail("status",sfdcStatus.getStatus());

                } else {
                    builder.down()
                            .withDetail("host",host.toLowerCase());
                }
            } catch (RestClientException e) {
                logger.error(e.getMessage());
                builder.down()
                        .withDetail("host",host.toLowerCase());
            }
        } else {
            builder.down()
                    .withDetail("host",host.toLowerCase());
        }

    }
}
