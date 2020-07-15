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
import io.pivotal.sfdc.domain.Contact;

@Service
public class ContactService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

	@Value("${sfdc.contactservice.endpoint}")
	private String contactServiceEP;

	@Autowired
	RestTemplate restTemplate;

	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Creates new contact in SFDC based on the Contact object
	 * 
	 * @param contact
	 * @return Contact updated contact object with newly created contact id in SFDC
	 */
	public String createContact(Contact contact) {
		LOGGER.debug("Creating createContact using {}", contactServiceEP);
		Contact _contact = restTemplate.postForObject(contactServiceEP + "/contact", contact, Contact.class);
		String data;
		try {
			data = mapper.writeValueAsString(_contact);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Problem creating contact!");
		}
		return data;
	}

	/**
	 * Updates contact in SFDC based on the Contact object
	 * 
	 * @param id      contact id
	 * @param contact contact object
	 */
	public void updateContact(String id, Contact contact) {
		LOGGER.debug("Updating updateContact {} using {}", id, contactServiceEP);
		restTemplate.put(contactServiceEP + "/contact/" + id, contact);
		return;
	}

	/**
	 * Deletes contact in SFDC based on the ContactId
	 * 
	 * @param id contact id
	 */
	public void deleteContact(String id) {
		LOGGER.debug("Deleting deleteContact {} using {}", id, contactServiceEP);
		restTemplate.delete(contactServiceEP + "/contact/" + id);
		return;
	}

	/**
	 * Retrieve contact from SFDC based on the ContactId
	 * 
	 * @param id contact id
	 * @return String
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getContactFallback")
	public String getContact(String id) {
		LOGGER.debug("Fetching getContact by id {} using {}", id, contactServiceEP);
		Contact contact = restTemplate.getForObject(contactServiceEP + "/contact/" + id, Contact.class);
		String data;
		try {
			data = mapper.writeValueAsString(contact);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Problem retrieving contact!");
		}
		return data;
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the contact
	 * object based on the id from redis.
	 * 
	 * @param id contact id
	 * @return String
	 */
	public String getContactFallback(String id) {
		LOGGER.debug("Fetching getContactFallback by id {} from cache", id);
		String data;
		try {
			data = retrieve(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Contact {" + id + "} not found!");
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
