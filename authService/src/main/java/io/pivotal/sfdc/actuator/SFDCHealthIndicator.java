package io.pivotal.sfdc.actuator;

import java.net.URL;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.SFDC_Constant;
import io.pivotal.sfdc.domain.SFDCStatus;

@Component
public class SFDCHealthIndicator extends AbstractHealthIndicator {

    @Resource(name = "redisConnection")
    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> redisCommands;

    private static final Logger LOGGER = LoggerFactory.getLogger(SFDCHealthIndicator.class);

    RestTemplate restTemplate;

    private String statusEP = null;

    @Autowired
    public SFDCHealthIndicator() {
        restTemplate = new RestTemplate();
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) throws Exception {
        redisCommands = redisConnection.sync();
        String cachedata = null;
        String host = null;
        try {
            cachedata = redisCommands.get(SFDC_Constant.INSTANCE_URL);
            if (cachedata != null || !cachedata.isEmpty()) {
                URL url = new URL(cachedata);
                host = url.getHost();
                host = host.substring(0, host.indexOf("."));
                statusEP = SFDC_Constant.STATUS_URL + host.toUpperCase() + "/status";
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
        }
        if (statusEP != null) {
            LOGGER.debug("statusEP: {}", statusEP);
            try {
                SFDCStatus sfdcStatus = restTemplate.getForObject(statusEP, SFDCStatus.class);
                if (sfdcStatus.getActive() && sfdcStatus.getStatus().equalsIgnoreCase("ok")) {
                    builder.up().withDetail("host", host.toLowerCase()).withDetail("isActive", sfdcStatus.getActive())
                            .withDetail("status", sfdcStatus.getStatus());

                } else {
                    builder.down().withDetail("host", host.toLowerCase());
                }
            } catch (RestClientException e) {
                LOGGER.error(e.getMessage());
                builder.down().withDetail("host", host.toLowerCase());
            }
        } else {
            builder.down().withDetail("host", host.toLowerCase());
        }

    }
}
