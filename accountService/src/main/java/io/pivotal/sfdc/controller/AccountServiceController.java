package io.pivotal.sfdc.controller;

import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.service.AccountService;
import io.pivotal.sfdc.service.AuthService;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.force.api.ForceApi;

/**
 * SFDC Account Service Controller
 * This controller supports account query by contact & opportunity. It also exposes URI to create, retrieve
 * update & delete account object in SFDC.
 * 
 * @author Jignesh Sheth
 *
 */
@RestController
@RefreshScope
public class AccountServiceController {

    @Autowired
	private StringRedisTemplate redisTemplate;

    @Autowired
    private AuthService authService;
    @Autowired
    private AccountService accountService;
    
	@Value("${sfdc.service.unavailable}")
    private String unavailable;

    
	private static final Logger logger = LoggerFactory.getLogger(AccountServiceController.class);

	ForceApi api;
    
	/**
	 * Calls a account service with cache key to retrieve result of a sfdc query ("sfdc.query.accounts")
	 * 
	 * @return List<Account> account list
	 */
    @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody List<Account> getContactsByAccounts() {
		logger.debug("Fetching getContactsByAccounts");
    	List<Account> result = null;
		try {
			result = accountService.getContactsByAccounts("/accounts");
			if (result == null) throw new NullPointerException(unavailable);
		} catch (Exception e) {
			throw new NullPointerException(unavailable);
		}
     	return result;
    }

	/**
	 * Calls a account service with cache key to retrieve result of a sfdc query ("sfdc.query.opp_by_accts")
	 * 
	 * @return List<Account> account list
	 */
    @RequestMapping(value = "/opp_by_accts", method = RequestMethod.GET)
    public @ResponseBody List<Account> getOpportunitiesByAccounts() {
		logger.debug("Fetching getOpportunitesByAccounts");
    	List<Account> result = null;
		try {
			result = accountService.getOpportunitesByAccounts("/opp_by_accts");
			if (result == null) throw new NullPointerException(unavailable);
		} catch (Exception e) {
			throw new NullPointerException(unavailable);
		}
     	return result;
    }
    
	/**
	 * Calls a account service with account object to either create new account or update existing account based on account id in sfdc.
	 * Note: for new account, the id is represented as "new"
	 * 
	 * @return Account account newly or updated account object
	 */
	@RequestMapping(value = "/account/{id}", method={RequestMethod.POST, RequestMethod.PUT})
	public ResponseEntity<Account> cuAccount(@PathVariable("id") final String accountId, @RequestBody final Account account, UriComponentsBuilder builder) {
		logger.debug("(C)-R(U)-D operation on Account");
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		HttpHeaders responseHeaders = new HttpHeaders();
		logger.debug("method: "+method);
		Account newAccount = null;
		try {
			switch (method) {
			case "put":
				account.setId(accountId);
				newAccount = accountService.updateAccount(account);
				break;
			default:
				newAccount = accountService.addAccount(account);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseHeaders.setLocation(builder.path("/account/{id}")
				.buildAndExpand(newAccount.getId()).toUri());
		if (newAccount != null && newAccount.getId() != null) {
			return new ResponseEntity<Account>(newAccount, responseHeaders, (method.equalsIgnoreCase("post") ? HttpStatus.CREATED : HttpStatus.OK));
		} else {
			logger.warn("new Account not created!");
			return new ResponseEntity<Account>(newAccount, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * Calls a account service with account id to either retrieve or delete account from sfdc.
	 * 
	 * @return Account account 
	 */
	@RequestMapping(value = "/account/{id}", method={RequestMethod.GET, RequestMethod.DELETE})
	public ResponseEntity<Account> rdAccount(@PathVariable("id") final String accountId, UriComponentsBuilder builder) {
		logger.debug("-C(R)-U(D) operation on Account");
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		HttpHeaders responseHeaders = new HttpHeaders();

		Account account = null;
		try {
			switch (method) {
			case "delete":
				accountService.deleteAccount(accountId);
				account = new Account();
				account.setId(accountId);
				break;
			default:
				account = accountService.getAccount(accountId);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseHeaders.setLocation(builder.path("/account/{id}")
				.buildAndExpand(account.getId()).toUri());
		if (account != null && account.getId() != null) {
			return new ResponseEntity<Account>(account, responseHeaders, HttpStatus.OK);
		} else {
			logger.warn("Problem retrieving/deleting Account");
			return new ResponseEntity<Account>(account, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}

