package io.pivotal.sfdc.service;

import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;

import io.pivotal.sfdc.domain.Contact;

@Service
public class ContactService {

	private static final Logger logger = LoggerFactory
			.getLogger(ContactService.class);
	
	private StringRedisTemplate redisTemplate;
    
	@Resource
    private JedisConnectionFactory redisConnFactory;

    @Autowired
    private AuthService authService;

	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();
	
    @PostConstruct
    public void init() {
		this.redisTemplate = new StringRedisTemplate(redisConnFactory);
    	logger.debug("HostName: "+redisConnFactory.getHostName());
    	logger.debug("Port: "+redisConnFactory.getPort());
    	logger.debug("Password: "+redisConnFactory.getPassword());
    }

    public Contact addContact(Contact contact) throws Exception {
		logger.debug("Storing new Contact to SFDC");
        api = new ForceApi(authService.getApiSession());
    	String id = api.createSObject("contact", contact);
    	logger.debug("contactId: "+id);
    	contact.setId(id);
    	store(id,contact);
    	return contact;
	}

	public Contact updateContact(Contact contact) throws Exception {
		logger.debug("Updating Contact("+contact.getId()+") to SFDC");
		String id = contact.getId();
		contact.setId(null);
        api = new ForceApi(authService.getApiSession());
        api.updateSObject("contact", id, contact);
        contact.setId(id);
    	store(id,contact);
    	return contact;
	}

	public void deleteContact(String id) throws Exception {
		logger.debug("Deleting Contact("+id+") from SFDC");
        api = new ForceApi(authService.getApiSession());
        api.deleteSObject("contact", id);
        this.redisTemplate.delete(id);
    	return;
	}

	@HystrixCommand(fallbackMethod = "getContactFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2500")
		    })
	public Contact getContact(String id) throws Exception {
		logger.debug("Retrieving Contact by id "+id+" from SFDC");
        api = new ForceApi(authService.getApiSession());
        Contact contact = api.getSObject("contact", id).as(Contact.class);
        store(id,contact);
    	return contact;
	}

	public Contact getContactFallback(String id) {
		logger.debug("Fetching fallback getContact by id "+id+" from cache");
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
