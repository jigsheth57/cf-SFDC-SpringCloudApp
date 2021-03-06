package io.pivotal.sfdc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.AccountList;
import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.domain.Opportunity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

/**
 * SFDC Account Service
 * This service executes account query by contact & opportunity in SFDC. It also provides service to create, retrieve
 * update & delete account object in SFDC.
 * 
 * @author Jignesh Sheth
 *
 */
@Service
public class AccountService {

	private static final Logger logger = LoggerFactory
			.getLogger(AccountService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

    @Autowired
	AuthService authService;

	@Value("${sfdc.query.loadsql}")
    private String preloadSQL;

	@Autowired
	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();

	@Bean
	ForceApi api() {
		this.api = new ForceApi(authService.getApiSession());
		this.redisCommands = redisConnection.sync();
		return this.api;
	}

    /**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query to retrieve all of the accounts, contacts & opportunities from sfdc.
	 * 
	 * @return void
	 * 
	 * @throws Exception
	 */
	public void preload() throws Exception {
		logger.debug("Start preloading data objects from SFDC");
    	try {
			this.api = new ForceApi(authService.getApiSession());
		} catch (Exception e) {
			Thread.sleep(30000);
			this.api = new ForceApi(authService.getApiSession());
		}
    	List<Account> result = api.query(preloadSQL,Account.class).getRecords();
    	store("accounts",new AccountList(result));
    	store("opp_by_accts",new AccountList(result));
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
		logger.debug("Done preloading data objects from SFDC");
		return;
	}
	
	/**
	 * Stores serialized domain object in redis
	 * 
	 * @param key 
	 * @param obj
     * @return String
	 * @throws Exception
	 */
	private String store(String key, Object obj) throws Exception {
		mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        //writing to console, can write to any output stream such as file
        StringWriter jsonData = new StringWriter();
        mapper.writeValue(jsonData, obj);
        String jsonDataStr = jsonData.toString();
		logger.debug("key: {}, value: {}",key,jsonDataStr);
        redisCommands.set(key,jsonDataStr);

        return jsonDataStr;
	}
}
