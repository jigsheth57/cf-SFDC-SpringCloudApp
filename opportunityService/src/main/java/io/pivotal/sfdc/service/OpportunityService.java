package io.pivotal.sfdc.service;

import io.pivotal.sfdc.domain.Opportunity;

import java.io.StringWriter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

@Service
public class OpportunityService {

	private static final Logger logger = LoggerFactory
			.getLogger(OpportunityService.class);
	
    @Autowired
	private StringRedisTemplate redisTemplate;

    @Autowired
    private AuthService authService;

	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();
	
	public Opportunity addOpportunity(Opportunity opportunity) throws Exception {
		logger.debug("Storing new Opportunity to SFDC");
        api = new ForceApi(authService.getApiSession());
    	String id = api.createSObject("opportunity", opportunity);
    	logger.debug("opportunityId: "+id);
    	opportunity.setId(id);
    	store(id,opportunity);
    	return opportunity;
	}

	public Opportunity updateOpportunity(Opportunity opportunity) throws Exception {
		logger.debug("Updating Opportunity("+opportunity.getId()+") to SFDC");
		String id = opportunity.getId();
		opportunity.setId(null);
        api = new ForceApi(authService.getApiSession());
        api.updateSObject("opportunity", id, opportunity);
        opportunity.setId(id);
    	store(id,opportunity);
    	return opportunity;
	}

	public void deleteOpportunity(String id) throws Exception {
		logger.debug("Deleting Opportunity("+id+") from SFDC");
        api = new ForceApi(authService.getApiSession());
        api.deleteSObject("opportunity", id);
        this.redisTemplate.delete(id);
    	return;
	}

	@HystrixCommand(fallbackMethod = "getOpportunityFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2500")
		    })
	public Opportunity getOpportunity(String id) throws Exception {
		logger.debug("Retrieving Opportunity by id "+id+" from SFDC");
        api = new ForceApi(authService.getApiSession());
        Opportunity opportunity = api.getSObject("opportunity", id).as(Opportunity.class);
        store(id,opportunity);
    	return opportunity;
	}

	public Opportunity getOpportunityFallback(String id) {
		logger.debug("Fetching fallback getOpportunity by id "+id+" from cache");
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
		logger.debug("key: "+key);
        logger.debug("value: "+jsonDataStr);
        this.redisTemplate.opsForValue().set(key, jsonDataStr);
	}

	private Object retrieve(String key, Class classType) throws Exception {
		logger.debug("key: "+key);
		String jsonDataStr = this.redisTemplate.opsForValue().get(key);
        logger.debug("value: "+jsonDataStr);
		Object obj = mapper.readValue(jsonDataStr, classType);
		
		return obj;
	}
}
