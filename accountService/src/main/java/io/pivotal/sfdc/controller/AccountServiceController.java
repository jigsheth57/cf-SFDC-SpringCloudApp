package io.pivotal.sfdc.controller;

import com.fasterxml.jackson.annotation.JsonView;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.View;
import io.pivotal.sfdc.service.AccountService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * SFDC Account Service Controller This controller supports account query by
 * contact & opportunity. It also exposes URI to create, retrieve update &
 * delete account object in SFDC.
 * 
 * @author Jignesh Sheth
 *
 */
@RestController
@RefreshScope
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountServiceController {

	@Autowired
	private AccountService accountService;

	@Value("${sfdc.service.unavailable}")
	private String unavailable;

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceController.class);

	/**
	 * Calls a account service with cache key to retrieve result of a sfdc query
	 * ("sfdc.query.accounts")
	 * 
	 * @return List<Account> account list
	 */
	@JsonView(View.ContactByAccountSummary.class)
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public @ResponseBody String getContactsByAccounts() {
		LOGGER.debug("Fetching getContactsByAccounts");
		String result = null;
		try {
			result = accountService.getContactsByAccounts("accounts");
			if (result == null)
				throw new NullPointerException(unavailable);
		} catch (Exception e) {
			throw new NullPointerException(unavailable);
		}
		return result;
	}

	/**
	 * Calls a account service with cache key to retrieve result of a sfdc query
	 * ("sfdc.query.opp_by_accts")
	 * 
	 * @return List<Account> account list
	 */
	@JsonView(View.OpportunityByAccountSummary.class)
	@RequestMapping(value = "/opp_by_accts", method = RequestMethod.GET)
	@ApiOperation(value = "Retrieve all opportunites by accounts", notes = "Calls a account service with cache key to retrieve result of a sfdc query", response = Account.class, responseContainer = "List")
	public @ResponseBody String getOpportunitiesByAccounts() {
		LOGGER.debug("Fetching getOpportunitesByAccounts");
		String result = null;
		try {
			result = accountService.getOpportunitesByAccounts("opp_by_accts");
			if (result == null)
				throw new NullPointerException(unavailable);
		} catch (Exception e) {
			throw new NullPointerException(unavailable);
		}
		return result;
	}

	/**
	 * Calls a account service with account object to create new account in sfdc.
	 * 
	 * @return Account newly created account object
	 */
	@RequestMapping(value = "/account", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create new account", notes = "Calls a account service to create new account", response = Account.class)
	public ResponseEntity<Account> post(
			@ApiParam(value = "Account model", required = true) @RequestBody Account account) {
		account.setId(null);
		try {
			account = accountService.addAccount(account);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not create new account: [%s]", account));
			return new ResponseEntity<Account>(account, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		HttpStatus httpstatus = HttpStatus.CREATED;
		LOGGER.debug("Created new account with id {}: [{}]", account.getId(), account);
		return new ResponseEntity<Account>(account, new HttpHeaders(), httpstatus);
	}

	/**
	 * Calls a account service with account object to update existing account in
	 * sfdc.
	 * 
	 * @return Account updated account object
	 */
	@RequestMapping(value = "/account/{accountId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update account by id", notes = "Calls a account service to update existing account by id", response = Account.class)
	public ResponseEntity<Account> put(@ApiParam(value = "Account ID", required = true) @PathVariable String accountId,
			@ApiParam(value = "Account model", required = true) @RequestBody Account account) {
		HttpStatus httpstatus = HttpStatus.OK;
		account.setId(accountId);
		try {
			account = accountService.updateAccount(account);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not update existing account with id %s: [%s]", account.getId(), account));
			return new ResponseEntity<Account>(account, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.debug("Updated existing account with id {}: [{}]", account.getId(), account);
		return new ResponseEntity<Account>(account, new HttpHeaders(), httpstatus);
	}

	/**
	 * Calls a account service with account id to delete account from sfdc.
	 * 
	 * @return void
	 */
	@RequestMapping(value = "/account/{accountId}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Delete account by id", notes = "Calls a account service to remove account by id")
	public ResponseEntity<?> delete(@ApiParam(value = "Account ID", required = true) @PathVariable String accountId) {
		HttpStatus httpstatus = HttpStatus.OK;
		try {
			accountService.deleteAccount(accountId);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not remove existing account with id %s", accountId));
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			return ResponseEntity.noContent().build();
		}
		LOGGER.debug("Remove account for id {}", accountId);
		ResponseEntity.status(httpstatus);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Calls a account service with account id to retrieve account from sfdc.
	 * 
	 * @return Account account
	 */
	@JsonView(View.AccountDetailSummary.class)
	@RequestMapping(value = "/account/{accountId}", method = RequestMethod.GET)
	@ApiOperation(value = "Retrieve account by id", notes = "Calls a account service to retrieve account by id", response = Account.class)
	public ResponseEntity<Account> get(
			@ApiParam(value = "Account ID", required = true) @PathVariable String accountId) {
		HttpStatus httpstatus = HttpStatus.OK;
		Account account = null;
		try {
			account = accountService.getAccount(accountId);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not retrieve existing account with id %s", accountId));
		}
		if (account == null) {
			account = new Account();
			account.setId(accountId);
			httpstatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Account>(account, new HttpHeaders(), httpstatus);
	}
}
