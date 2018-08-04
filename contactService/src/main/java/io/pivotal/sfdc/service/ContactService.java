package io.pivotal.sfdc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.domain.Contact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.StringWriter;

@Service
public class ContactService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContactService.class);

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

	public Contact addContact(Contact contact) throws Exception {
		logger.debug("Storing new Contact to SFDC");
//        api = new ForceApi(authService.getApiSession());
    	String id = api.createSObject("contact", contact);
    	logger.debug("contactId: {}",id);
    	contact.setId(id);
    	store(id,contact);
    	return contact;
	}

	public Contact updateContact(Contact contact) throws Exception {
		logger.debug("Updating Contact {} to SFDC",contact.getId());
		String id = contact.getId();
		contact.setId(null);
//        api = new ForceApi(authService.getApiSession());
        api.updateSObject("contact", id, contact);
        contact.setId(id);
    	store(id,contact);
    	return contact;
	}

	public void deleteContact(String id) throws Exception {
		logger.debug("Deleting Contact {} from SFDC",id);
//        api = new ForceApi(authService.getApiSession());
        api.deleteSObject("contact", id);
        redisCommands.del(id);
    	return;
	}

	@HystrixCommand(fallbackMethod = "getContactFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2500")
		    })
	public Contact getContact(String id) throws Exception {
		logger.debug("Retrieving Contact by id {} from SFDC",id);
//        api = new ForceApi(authService.getApiSession());
        Contact contact = api.getSObject("contact", id).as(Contact.class);
        store(id,contact);
    	return contact;
	}

	public Contact getContactFallback(String id) {
		logger.debug("Fetching fallback getContact by id {} from cache",id);
		Contact contact = null;
		try {
			contact = (Contact)retrieve(id, Contact.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return contact;
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
