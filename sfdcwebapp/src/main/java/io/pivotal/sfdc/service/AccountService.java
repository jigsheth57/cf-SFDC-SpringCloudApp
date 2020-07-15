package io.pivotal.sfdc.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.pivotal.sfdc.SFDC_Constant;
import io.pivotal.sfdc.domain.Account;

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

	@Value("${sfdc.accountservice.endpoint}")
	private String accountServiceEP;

	@Autowired
	RestTemplate restTemplate;

	final ObjectMapper mapper = new ObjectMapper();

	/**
	 * Calls AccountService to retrieve the accounts by contacts from sfdc.
	 * 
	 * @return String account list
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getContactsByAccountsFallback")
	public String getContactsByAccounts() {
		LOGGER.debug("Fetching getContactsByAccounts from {}", accountServiceEP);
		return restTemplate.exchange(accountServiceEP + "/accounts", HttpMethod.GET, null, String.class).getBody();
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the
	 * accounts by contacts query result from redis.
	 * 
	 * @param key URI for retrieving result from redis
	 * 
	 * @return String account list
	 */
	public String getContactsByAccountsFallback() {
		LOGGER.debug("Fetching getContactsByAccountsFallback from cache using key {}",
				SFDC_Constant.ACCOUNT_CONTACT_LIST);
		String data;
		try {
			data = retrieve(SFDC_Constant.ACCOUNT_CONTACT_LIST);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Accounts by contacts not found!");
		}
		return data;
	}

	/**
	 * Calls AccountService to retrieve the accounts by opportunities from sfdc.
	 * 
	 * @return String account list
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getOpportunitesByAccountsFallback")
	public String getOpportunitesByAccounts() {
		LOGGER.debug("Fetching getOpportunitesByAccounts from {}", accountServiceEP);
		return restTemplate.exchange(accountServiceEP + "/opp_by_accts", HttpMethod.GET, null, String.class).getBody();
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the
	 * accounts by opportunities query result from redis.
	 * 
	 * @return String account list
	 */
	public String getOpportunitesByAccountsFallback() {
		LOGGER.debug("Fetching getOpportunitesByAccountsFallback from cache using key {}",
				SFDC_Constant.ACCOUNT_OPPORTUNITY_LIST);
		String data;
		try {
			data = retrieve(SFDC_Constant.ACCOUNT_OPPORTUNITY_LIST);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Accounts by opportunities not found!");
		}
		return data;
	}

	/**
	 * Creates new account in SFDC based on the Account object
	 * 
	 * @param account
	 * @return Account updated account object with newly created account id in SFDC
	 */
	public String createAccount(Account account) {
		LOGGER.debug("Creating createAccount using {}", accountServiceEP);
		Account _account = restTemplate.postForObject(accountServiceEP + "/account", account, Account.class);
		String data;
		try {
			data = mapper.writeValueAsString(_account);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Problem creating account!");
		}
		return data;
	}

	/**
	 * Updates account in SFDC based on the Account object
	 * 
	 * @param id
	 * @param account
	 */
	public void updateAccount(String id, Account account) {
		LOGGER.debug("Updating updateAccount {} using {}", id, accountServiceEP);
		restTemplate.put(accountServiceEP + "/account/" + id, account);
		return;
	}

	/**
	 * Deletes account in SFDC based on the AccountId
	 * 
	 * @param id account id
	 */
	public void deleteAccount(String id) {
		LOGGER.debug("Deleting deleteAccount {} using {}", id, accountServiceEP);
		restTemplate.delete(accountServiceEP + "/account/" + id);
		return;
	}

	/**
	 * Retrieves account from SFDC based on the AccountId
	 * 
	 * @param id account id
	 * @return String
	 */
	@CircuitBreaker(name = SFDC_Constant.CIRCUIT_BREAKER_DEFAULTS, fallbackMethod = "getAccountFallback")
	public String getAccount(String id) {
		LOGGER.debug("Fetching getAccount by id {} using {}", id, accountServiceEP);

		// Account account = restTemplate.getForObject(accountServiceEP + "/account/" +
		// id, Account.class);
		// String data;
		// try {
		// data = mapper.writeValueAsString(account);
		// } catch (Exception e) {
		// e.printStackTrace();
		// throw new NullPointerException("Problem retrieving account!");
		// }
		return restTemplate.exchange(accountServiceEP + "/account/" + id, HttpMethod.GET, null, String.class).getBody();
	}

	/**
	 * This method uses circuit breaker pattern to fallback and retrieve the account
	 * object based on the id from redis.
	 * 
	 * @param id account id
	 * @return String
	 */
	public String getAccountFallback(String id) {
		LOGGER.debug("Fetching getAccountFallback by id {} from cache", id);
		String data;
		try {
			data = retrieve(id);
		} catch (Exception e) {
			e.printStackTrace();
			throw new NullPointerException("Account {" + id + "} not found!");
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
		// this.redisCommands = redisConnection.sync();
		String jsonDataStr = redisCommands.get(key);
		LOGGER.debug("value: {}", jsonDataStr);

		return jsonDataStr;
	}
}
