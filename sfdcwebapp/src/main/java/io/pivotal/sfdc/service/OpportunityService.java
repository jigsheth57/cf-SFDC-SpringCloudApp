package io.pivotal.sfdc.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

	@Value("${sfdc.opportunityservice.endpoint}")
	private String opportunityServiceEP;

	@Autowired
	RestTemplate restTemplate;

	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Creates new opportunity in SFDC based on the Opportunity object
	 * 
	 * @param opportunity
	 * @return Opportunity updated opportunity object with newly created opportunity
	 *         id in SFDC
	 */
	public String createOpportunity(Opportunity opportunity) {
		LOGGER.debug("Creating createOpportunity using {}", opportunityServiceEP);
		Opportunity _opportunity = restTemplate.postForObject(opportunityServiceEP + "/opportunity", opportunity,
				Opportunity.class);
		String data;
		try {
			data = mapper.writeValueAsString(_opportunity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Problem creating opportunity!");
		}
		return data;
	}

	/**
	 * Updates opportunity in SFDC based on the Opportunity object
	 * 
	 * @param id          opportunity id
	 * @param opportunity opportunity object
	 */
	public void updateOpportunity(String id, Opportunity opportunity) {
		LOGGER.debug("Updating updateOpportunity {} using {}", id, opportunityServiceEP);
		restTemplate.put(opportunityServiceEP + "/opportunity/" + id, opportunity);
		return;
	}

	/**
	 * Deletes opportunity in SFDC based on the OpportunityId
	 * 
	 * @param id opportunity id
	 */
	public void deleteOpportunity(String id) {
		LOGGER.debug("Deleting deleteOpportunity {} using {}", id, opportunityServiceEP);
		restTemplate.delete(opportunityServiceEP + "/opportunity/" + id);
		return;
	}

	/**
	 * Retrieve opportunity from SFDC based on the OpportunityId
	 * 
	 * @param id opportunity id
	 * @return String
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getOpportunityFallback")
	public String getOpportunity(String id) {
		LOGGER.debug("Fetching getOpportunity by id {} using {}", id, opportunityServiceEP);
		Opportunity opportunity = restTemplate.getForObject(opportunityServiceEP + "/opportunity/" + id,
				Opportunity.class);
		String data;
		try {
			data = mapper.writeValueAsString(opportunity);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Problem retrieving opportunity!");
		}
		return data;
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the
	 * opportunity object based on the id from redis.
	 * 
	 * @param id opportunity id
	 * @return String
	 */
	public String getOpportunityFallback(String id) {
		LOGGER.debug("Fetching getOpportunityFallback by id {} from cache", id);
		String data;
		try {
			data = retrieve(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Opportunity {" + id + "} not found!");
		}
		return data;
	}

	/**
	 * Retrieves serialized domain object from redis.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private String retrieve(String key) throws Exception {
		LOGGER.debug("key: {}", key);
		this.redisCommands = redisConnection.sync();
		String jsonDataStr = redisCommands.get(key);
		LOGGER.debug("value: {}", jsonDataStr);

		return jsonDataStr;
	}
}
