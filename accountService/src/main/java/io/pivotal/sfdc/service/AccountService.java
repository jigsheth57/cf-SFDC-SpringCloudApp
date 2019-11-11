package io.pivotal.sfdc.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
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
import org.springframework.cloud.context.config.annotation.RefreshScope;
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
@RefreshScope
@Service
public class AccountService {

	private static final Logger logger = LoggerFactory
			.getLogger(AccountService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands;

    @Autowired
	AuthService authService;

	@Value("${sfdc.query.accounts}")
    private String accountsSQL;
	@Value("${sfdc.query.opp_by_accts}")
    private String opp_by_acctsSQL;
	@Value("${sfdc.query.loadsql}")
    private String preloadSQL;

//    @Autowired
    ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();
	
//    @Bean
//    @RefreshScope
//    ForceApi api() {
//        this.api = new ForceApi(authService.getApiSession());
//        this.redisCommands = redisConnection.sync();
//        return this.api;
//    }
    /**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query to retrieve the accounts by contacts from sfdc.
	 * 
	 * @param key URI for storing result in redis
	 * @return String account list
	 * 
	 * @throws Exception
	 */
	@HystrixCommand(fallbackMethod = "getContactsByAccountsFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2500")
		    })
	public String getContactsByAccounts(String key) throws Exception {
		logger.debug("Fetching getContactsByAccounts from SFDC");
    	api = new ForceApi(authService.getApiSession());
    	List<Account> result = api.query(accountsSQL,Account.class).getRecords();
        return store(key,new AccountList(result));
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the accounts by contacts query result from redis.
	 * 
	 * @param key URI for retrieving result from redis
	 * 
	 * @return String account list
	 */
	public String getContactsByAccountsFallback(String key) {
		logger.debug("Fetching getContactsByAccountsFallback from Cache with key {}",key);
		String result = null;
		try {
			result = retrieve(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return result;
	}
	
	/**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query to retrieve the accounts by opportunities from sfdc.
	 * 
	 * @param key URI for storing result in redis
	 * @return String account list
	 * 
	 * @throws Exception
	 */
	@HystrixCommand(fallbackMethod = "getOpportunitesByAccountsFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2500")
		    })
	public String getOpportunitesByAccounts(String key) throws Exception {
		logger.debug("Fetching getOpportunitesByAccounts from SFDC");
    	api = new ForceApi(authService.getApiSession());
    	List<Account> result = api.query(opp_by_acctsSQL,Account.class).getRecords();
        return store(key,new AccountList(result));
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the accounts by opportunities query result from redis.
	 * 
	 * @param key URI for retrieving result from redis
	 * 
	 * @return String account list
	 */
	public String getOpportunitesByAccountsFallback(String key) {
		logger.debug("Fetching fallback getOpportunitesByAccountsFallback from Cache with key {}",key);
		String result = null;
		try {
			result = retrieve(key);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
        return result;
	}
	
	/**
	 * Creates new account in SFDC based on the Account object
	 * 
	 * @param account
	 * @return Account updated account object with newly created account id in SFDC
	 * @throws Exception
	 */
	public Account addAccount(Account account) throws Exception {
		logger.debug("Storing new Account to SFDC");
        api = new ForceApi(authService.getApiSession());
    	String id = api.createSObject("account", account);
    	logger.debug("accountId: {}",id);
    	account.setId(id);
    	store(id,account);
    	return account;
	}

	/**
	 * Updates account in SFDC based on the Account object
	 * 
	 * @param account
	 * @return Account updated account object with updated attributes in SFDC
	 * @throws Exception
	 */
	public Account updateAccount(Account account) throws Exception {
		logger.debug("Updating Account {} to SFDC",account.getId());
		String id = account.getId();
		account.setId(null);
        api = new ForceApi(authService.getApiSession());
        api.updateSObject("account", id, account);
        account.setId(id);
    	store(id,account);
    	return account;
	}

	/**
	 * Deletes account in SFDC based on the AccountId
	 * 
	 * @param id account id
	 * @throws Exception
	 */
	public void deleteAccount(String id) throws Exception {
		logger.debug("Deleting Account {} from SFDC",id);
        api = new ForceApi(authService.getApiSession());
        api.deleteSObject("account", id);
		this.redisCommands = redisConnection.sync();
        redisCommands.del(id);
//        this.redisTemplate.delete(id);
    	return;
	}
	
	/**
	 * Retrieves account from SFDC based on the AccountId
	 * 
	 * @param id account id
	 * @return Account 
	 * @throws Exception
	 */
	@HystrixCommand(fallbackMethod = "getAccountFallback",
		    commandProperties = {
		      @HystrixProperty(name="execution.isolation.strategy", value="THREAD"),
		      @HystrixProperty(name="execution.isolation.thread.timeoutInMilliseconds", value="2500")
		    })
	public Account getAccount(String id) throws Exception {
		logger.debug("Retrieving Account by id {} from SFDC",id);
        api = new ForceApi(authService.getApiSession());
        Account account = api.getSObject("account", id).as(Account.class);
        store(id,account);
    	return account;
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the account object based on the id from redis.
	 * @param id account id
	 * @return Account
	 */
	public Account getAccountFallback(String id) {
		logger.debug("Fetching getAccount by id {} from cache",id);
		Account account = null;
		try {
			String result = retrieve(id);
            account = mapper.readValue(result, Account.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	return account;
	}

    /**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query to retrieve all of the accounts, contacts & opportunities from sfdc.
	 * 
	 * @return void
	 * 
	 * @throws Exception
	 */
	public void preload() throws Exception {
		logger.debug("Preloading data objects from SFDC");
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
		this.redisCommands = redisConnection.sync();
        redisCommands.set(key, jsonDataStr);

        return jsonDataStr;
	}

	/**
	 * Retrieves serialized domain object from redis.
	 * 
	 * @param key
	 * @return
	 * @throws Exception
	 */
	private String retrieve(String key) throws Exception {
		logger.debug("key: {}",key);
		this.redisCommands = redisConnection.sync();
		String jsonDataStr = redisCommands.get(key);
        logger.debug("value: {}",jsonDataStr);

		return jsonDataStr;
	}
}
