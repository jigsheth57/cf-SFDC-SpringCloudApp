package io.pivotal.sfdc.controller;

import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.net.URI;
import java.util.LinkedHashSet;

/**
 * SFDC Gateway Hystrix Controller
 *
 * @author Jignesh Sheth
 *
 */
@RestController
@RefreshScope
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class HystrixFallbackServiceController {

    @Value("${sfdc.service.unavailable}")
    private String unavailable;

    @Resource(name="redisConnection")
    private StatefulRedisConnection<String, String> redisConnection;

    private RedisCommands<String, String> redisCommands;

    private static final Logger logger = LoggerFactory.getLogger(HystrixFallbackServiceController.class);

    @RequestMapping(value = "/servicefallback", method = RequestMethod.GET)
    public @ResponseBody Mono<String> getServiceFallback(ServerWebExchange exchange) {
        logger.debug("Fetching getServiceFallback(ServerWebExchange)");
        logger.debug(exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR).toString());
        redisCommands = redisConnection.sync();
        LinkedHashSet lhs = (LinkedHashSet)exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_ORIGINAL_REQUEST_URL_ATTR);
        URI requestUrl = (URI)lhs.iterator().next();
        String path = requestUrl.getPath();
        logger.debug("path: {}",path);
        String id = path.substring(path.lastIndexOf('/')+1);
        logger.debug("key: {}",id);
        String cachedata = unavailable;
        try {
            cachedata = redisCommands.get(id);
            if(cachedata == null || cachedata.isEmpty())
                cachedata = unavailable;
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return Mono.just(cachedata);
    }
}

