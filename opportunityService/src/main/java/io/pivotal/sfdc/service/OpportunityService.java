package io.pivotal.sfdc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.domain.Opportunity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class OpportunityService {

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

    @Autowired
    AuthService authService;

    @Autowired
	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();

	@Bean
	@RefreshScope
	ForceApi api() {
		this.api = new ForceApi(authService.getApiSession());
		this.redisCommands = redisConnection.sync();
		return this.api;
	}

    public Opportunity addOpportunity(Opportunity opportunity) throws Exception {
		logger.debug("Storing new Opportunity to SFDC");
//        api = new ForceApi(authService.getApiSession());
    	String id = api.createSObject("opportunity", opportunity);
    	logger.debug("opportunityId: {}",id);
    	opportunity.setId(id);
    	store(id,opportunity);
    	return opportunity;
	}

	public Opportunity updateOpportunity(Opportunity opportunity) throws Exception {
		logger.debug("Updating Opportunity {} to SFDC",opportunity.getId());
		String id = opportunity.getId();
		opportunity.setId(null);
//        api = new ForceApi(authService.getApiSession());
        api.updateSObject("opportunity", id, opportunity);
        opportunity.setId(id);
    	store(id,opportunity);
    	return opportunity;
	}

	public void deleteOpportunity(String id) throws Exception {
		logger.debug("Deleting Opportunity {} from SFDC",id);
//        api = new ForceApi(authService.getApiSession());
        api.deleteSObject("opportunity", id);
        redisCommands.del(id);
    	return;
	}

	@HystrixCommand(fallbackMethod = "getOpportunityFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2500")
		    })
	public Opportunity getOpportunity(String id) throws Exception {
		logger.debug("Retrieving Opportunity by id {} from SFDC",id);
//        api = new ForceApi(authService.getApiSession());
        Opportunity opportunity = api.getSObject("opportunity", id).as(Opportunity.class);
        store(id,opportunity);
    	return opportunity;
	}

	public Opportunity getOpportunityFallback(String id) {
		logger.debug("Fetching fallback getOpportunity by id {} from cache",id);
		Opportunity opportunity = null;
		try {
			opportunity = (Opportunity)retrieve(id, Opportunity.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return opportunity;
	}

	private void store(String key, Object obj) throws Exception {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        //writing to console, can write to any output stream such as file
        StringWriter jsonData = new StringWriter();
        mapper.writeValue(jsonData, obj);
        String jsonDataStr = jsonData.toString();
		logger.debug("key: {}, value: {}",key,jsonDataStr);
		redisCommands.set(key,jsonDataStr);
	}

	private Object retrieve(String key, Class classType) throws Exception {
		logger.debug("key: {}",key);
		String jsonDataStr = redisCommands.get(key);
        logger.debug("value: {}",jsonDataStr);
		Object obj = mapper.readValue(jsonDataStr, classType);
		
		return obj;
	}
}
