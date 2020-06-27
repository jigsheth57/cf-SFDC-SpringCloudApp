package io.pivotal.sfdc.service;

import java.io.StringWriter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.SFDC_Constant;
import io.pivotal.sfdc.domain.Opportunity;

@Service
public class OpportunityService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OpportunityService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

	@Autowired
	AuthService authService;

	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();

	public Opportunity addOpportunity(Opportunity opportunity) throws Exception {
		LOGGER.debug("Storing new Opportunity to SFDC");
		api = new ForceApi(authService.getApiSession());
		String id = api.createSObject("opportunity", opportunity);
		LOGGER.debug("opportunityId: {}", id);
		opportunity.setId(id);
		store(id, opportunity);
		return opportunity;
	}

	public Opportunity updateOpportunity(Opportunity opportunity) throws Exception {
		LOGGER.debug("Updating Opportunity {} to SFDC", opportunity.getId());
		String id = opportunity.getId();
		opportunity.setId(null);
		api = new ForceApi(authService.getApiSession());
		api.updateSObject("opportunity", id, opportunity);
		opportunity.setId(id);
		store(id, opportunity);
		return opportunity;
	}

	public void deleteOpportunity(String id) throws Exception {
		LOGGER.debug("Deleting Opportunity {} from SFDC", id);
		api = new ForceApi(authService.getApiSession());
		api.deleteSObject("opportunity", id);
		this.redisCommands = redisConnection.sync();
		redisCommands.del(id);
		return;
	}

	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getOpportunityFallback")
	public Opportunity getOpportunity(String id) throws Exception {
		LOGGER.debug("Fetching fallback getOpportunity by id {} from cache", id);
		return (Opportunity) retrieve(id, Opportunity.class);
	}

	public Opportunity getOpportunityFallback(String id) throws Exception {
		LOGGER.debug("Retrieving Opportunity by id {} from SFDC", id);
		api = new ForceApi(authService.getApiSession());
		Opportunity opportunity = api.getSObject("opportunity", id).as(Opportunity.class);
		store(id, opportunity);
		return opportunity;
	}

	private void store(String key, Object obj) throws Exception {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		// writing to console, can write to any output stream such as file
		StringWriter jsonData = new StringWriter();
		mapper.writeValue(jsonData, obj);
		String jsonDataStr = jsonData.toString();
		LOGGER.debug("key: {}, value: {}", key, jsonDataStr);
		this.redisCommands = redisConnection.sync();
		redisCommands.set(key, jsonDataStr);
	}

	private Object retrieve(String key, Class classType) throws Exception {
		LOGGER.debug("key: {}", key);
		this.redisCommands = redisConnection.sync();
		String jsonDataStr = redisCommands.get(key);
		LOGGER.debug("value: {}", jsonDataStr);
		Object obj = mapper.readValue(jsonDataStr, classType);

		return obj;
	}
}
