package io.pivotal.sfdc.service;

import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.force.api.ForceApi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.SFDC_Constant;
import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.AccountList;
import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.domain.Opportunity;

/**
 * SFDC Account Service This service executes account query by contact &
 * opportunity in SFDC. It also provides service to create, retrieve update &
 * delete account object in SFDC.
 * 
 * @author Jignesh Sheth
 *
 */
@RefreshScope
@Service
public class AccountService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountService.class);

	@Autowired
	private StatefulRedisConnection<String, String> redisConnection;

	private RedisCommands<String, String> redisCommands = redisConnection.sync();

	@Autowired
	AuthService authService;

	@Value("${sfdc.query.accounts}")
	private String accountsSQL;
	@Value("${sfdc.query.opp_by_accts}")
	private String opp_by_acctsSQL;
	@Value("${sfdc.query.loadsql}")
	private String preloadSQL;

	ForceApi api;
	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query
	 * to retrieve the accounts by contacts from sfdc.
	 * 
	 * @param key URI for storing result in redis
	 * @return String account list
	 * 
	 * @throws Exception
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getContactsByAccountsFallback")
	public String getContactsByAccounts(String key) throws Exception {
		LOGGER.debug("Fetching getContactsByAccountsFallback from Cache with key {}", key);
		return retrieve(key);
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the
	 * accounts by contacts query result from redis.
	 * 
	 * @param key URI for retrieving result from redis
	 * 
	 * @return String account list
	 */
	public String getContactsByAccountsFallback(String key) throws Exception {
		LOGGER.debug("Fetching getContactsByAccounts from SFDC");
		api = new ForceApi(authService.getApiSession());
		List<Account> result = api.query(accountsSQL, Account.class).getRecords();
		return store(key, new AccountList(result));
	}

	/**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query
	 * to retrieve the accounts by opportunities from sfdc.
	 * 
	 * @param key URI for storing result in redis
	 * @return String account list
	 * 
	 * @throws Exception
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getOpportunitesByAccountsFallback")
	public String getOpportunitesByAccounts(String key) throws Exception {
		LOGGER.debug("Fetching fallback getOpportunitesByAccountsFallback from Cache with key {}", key);
		return retrieve(key);
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the
	 * accounts by opportunities query result from redis.
	 * 
	 * @param key URI for retrieving result from redis
	 * 
	 * @return String account list
	 */
	public String getOpportunitesByAccountsFallback(String key) throws Exception {
		LOGGER.debug("Fetching getOpportunitesByAccounts from SFDC");
		api = new ForceApi(authService.getApiSession());
		List<Account> result = api.query(opp_by_acctsSQL, Account.class).getRecords();
		return store(key, new AccountList(result));
	}

	/**
	 * Creates new account in SFDC based on the Account object
	 * 
	 * @param account
	 * @return Account updated account object with newly created account id in SFDC
	 * @throws Exception
	 */
	public Account addAccount(Account account) throws Exception {
		LOGGER.debug("Storing new Account to SFDC");
		api = new ForceApi(authService.getApiSession());
		String id = api.createSObject("account", account);
		LOGGER.debug("accountId: {}", id);
		account.setId(id);
		store(id, account);
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
		LOGGER.debug("Updating Account {} to SFDC", account.getId());
		String id = account.getId();
		account.setId(null);
		api = new ForceApi(authService.getApiSession());
		api.updateSObject("account", id, account);
		account.setId(id);
		store(id, account);
		return account;
	}

	/**
	 * Deletes account in SFDC based on the AccountId
	 * 
	 * @param id account id
	 * @throws Exception
	 */
	public void deleteAccount(String id) throws Exception {
		LOGGER.debug("Deleting Account {} from SFDC", id);
		api = new ForceApi(authService.getApiSession());
		api.deleteSObject("account", id);
		this.redisCommands = redisConnection.sync();
		redisCommands.del(id);
		// this.redisTemplate.delete(id);
		return;
	}

	/**
	 * Retrieves account from SFDC based on the AccountId
	 * 
	 * @param id account id
	 * @return Account
	 * @throws Exception
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getAccountFallback")
	public Account getAccount(String id) throws Exception {
		LOGGER.debug("Fetching getAccount by id {} from cache", id);
		return mapper.readValue(retrieve(id), Account.class);
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the account
	 * object based on the id from redis.
	 * 
	 * @param id account id
	 * @return Account
	 */
	public Account getAccountFallback(String id) throws Exception {
		LOGGER.debug("Retrieving Account by id {} from SFDC", id);
		api = new ForceApi(authService.getApiSession());
		Account account = api.getSObject("account", id).as(Account.class);
		store(id, account);
		return account;
	}

	/**
	 * Calls AuthService to retrieve oauth2 token from SFDC and then executes query
	 * to retrieve all of the accounts, contacts & opportunities from sfdc.
	 * 
	 * @return void
	 * 
	 * @throws Exception
	 */
	public void preload() throws Exception {
		LOGGER.info("Preloading data objects from SFDC");
		try {
			this.api = new ForceApi(authService.getApiSession());
		} catch (Exception e) {
			Thread.sleep(30000);
			this.api = new ForceApi(authService.getApiSession());
		}
		List<Account> result = api.query(preloadSQL, Account.class).getRecords();
		store("accounts", new AccountList(result));
		store("opp_by_accts", new AccountList(result));
		Iterator<Account> accountListIterator = result.iterator();
		while (accountListIterator.hasNext()) {
			Account acct = accountListIterator.next();
			if (acct != null) {
				List<Contact> contactList = acct.getContacts();
				if (contactList != null) {
					Iterator<Contact> contactListInterator = contactList.iterator();
					while (contactListInterator.hasNext()) {
						Contact contact = contactListInterator.next();
						store(contact.getId(), contact);
					}
				}
				List<Opportunity> opportunityList = acct.getOpportunities();
				if (opportunityList != null) {
					Iterator<Opportunity> opportunityListInterator = opportunityList.iterator();
					while (opportunityListInterator.hasNext()) {
						Opportunity opportunity = opportunityListInterator.next();
						store(opportunity.getId(), opportunity);
					}
				}
				acct.setContacts(null);
				acct.setOpportunities(null);
				store(acct.getId(), acct);
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
		// writing to console, can write to any output stream such as file
		StringWriter jsonData = new StringWriter();
		mapper.writeValue(jsonData, obj);
		String jsonDataStr = jsonData.toString();
		LOGGER.debug("key: {}, value: {}", key, jsonDataStr);
		// this.redisCommands = redisConnection.sync();
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
		LOGGER.debug("key: {}", key);
		// this.redisCommands = redisConnection.sync();
		String jsonDataStr = redisCommands.get(key);
		LOGGER.debug("value: {}", jsonDataStr);

		return jsonDataStr;
	}
}
