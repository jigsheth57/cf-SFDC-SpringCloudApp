package io.pivotal.sfdc.service;

import com.force.api.ForceApi;
import io.pivotal.sfdc.domain.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

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

	private static final Logger logger = LoggerFactory.getLogger(AccountService.class);

    @Autowired
	AuthService authService;

    ForceApi api;

	/**
	 * Retrieves account from SFDC based on the AccountId
	 * 
	 * @param id account id
	 * @return Account 
	 * @throws Exception
	 */
	public Account getAccount(String id) throws Exception {
		logger.debug("Retrieving Account by id {} from SFDC",id);
        api = new ForceApi(authService.getApiSession());
        Account account = api.getSObject("account", id).as(Account.class);
    	return account;
	}
}
