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
import io.pivotal.sfdc.domain.Contact;

@Service
public class ContactService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ContactService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

	@Autowired
	AuthService authService;

	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();

	public Contact addContact(Contact contact) throws Exception {
		LOGGER.debug("Storing new Contact to SFDC");
		api = new ForceApi(authService.getApiSession());
		String id = api.createSObject("contact", contact);
		LOGGER.debug("contactId: {}", id);
		contact.setId(id);
		store(id, contact);
		return contact;
	}

	public Contact updateContact(Contact contact) throws Exception {
		LOGGER.debug("Updating Contact {} to SFDC", contact.getId());
		String id = contact.getId();
		contact.setId(null);
		api = new ForceApi(authService.getApiSession());
		api.updateSObject("contact", id, contact);
		contact.setId(id);
		store(id, contact);
		return contact;
	}

	public void deleteContact(String id) throws Exception {
		LOGGER.debug("Deleting Contact {} from SFDC", id);
		api = new ForceApi(authService.getApiSession());
		api.deleteSObject("contact", id);
		this.redisCommands = redisConnection.sync();
		redisCommands.del(id);
		return;
	}

	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getContactFallback")
	public Contact getContact(String id) throws Exception {
		LOGGER.debug("Fetching fallback getContact by id {} from cache", id);
		return (Contact) retrieve(id, Contact.class);
	}

	public Contact getContactFallback(String id) throws Exception {
		LOGGER.debug("Retrieving Contact by id {} from SFDC", id);
		api = new ForceApi(authService.getApiSession());
		Contact contact = api.getSObject("contact", id).as(Contact.class);
		store(id, contact);
		return contact;
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
