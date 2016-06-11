package io.pivotal.sfdc.service;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;

import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.AccountList;
import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.domain.Opportunity;

/**
 * SFDC Account Service
 * This service executes account query by contact & opportunity in SFDC. It also provides service to create, retrieve
 * update & delete account object in SFDC.
 * 
 * @author Jignesh Sheth
 *
 */
@Service
@EnableConfigurationProperties
public class AccountService {

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);
	
	private StringRedisTemplate redisTemplate;
    
	@Resource
    private JedisConnectionFactory redisConnFactory;

	@Autowired
    private AuthService authService;

	@Value("${sfdc.query.loadsql}")
    private String loadSQL;

	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();
	
    @PostConstruct
    public void init() {
		this.redisTemplate = new StringRedisTemplate(redisConnFactory);
    	logger.debug("HostName: "+redisConnFactory.getHostName());
    	logger.debug("Port: "+redisConnFactory.getPort());
    	logger.debug("Password: "+redisConnFactory.getPassword());
    }

    /**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query to retrieve the accounts by contacts from sfdc.
	 * 
	 * @param key URI for storing result in redis
	 * @return List<Account> account list
	 * 
	 * @throws Exception
	 */
	public void preload() throws Exception {
		logger.debug("Preloading data objects from SFDC");
    	api = new ForceApi(authService.getApiSession());
    	List<Account> result = api.query(loadSQL,Account.class).getRecords();
    	store("/accounts",new AccountList(result));
    	store("/opp_by_accts",new AccountList(result));
    	Iterator<Account> accountListIterator = result.iterator();
    	while (accountListIterator.hasNext()) {
    		Account acct = accountListIterator.next();
    		if(acct != null) {
    			List<Contact> contactList = acct.getContacts();
    			if(contactList != null) {
    				Iterator<Contact> contactListInterator = contactList.iterator();
    				while(contactListInterator.hasNext()) {
    					Contact contact = contactListInterator.next();
    					store(contact.getId(),contact);
    				}
    			}
    			List<Opportunity> opportunityList = acct.getOpportunities();
    			if(opportunityList != null) {
    				Iterator<Opportunity> opportunityListInterator = opportunityList.iterator();
    				while(opportunityListInterator.hasNext()) {
    					Opportunity opportunity = opportunityListInterator.next();
    					store(opportunity.getId(),opportunity);
    				}
    			}
    			acct.setContacts(null);
    			acct.setOpportunities(null);
    			store(acct.getId(),acct);
    		}
    			
    		
    	}
        return;
	}

	/**
	 * Stores serialized domain object in redis
	 * 
	 * @param key 
	 * @param obj
	 * @throws Exception
	 */
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
}
