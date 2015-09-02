package io.pivotal.sfdc.domain;

import java.util.List;

/**
 * SFDC Account Object List representation
 * @author Jignesh Sheth
 *
 */
public class AccountList {

	private List<Account> accounts;

	/**
	 * @return the accounts
	 */
	public List<Account> getAccounts() {
		return accounts;
	}

	/**
	 * @param accounts the accounts to set
	 */
	public void setAccounts(List<Account> accounts) {
		this.accounts = accounts;
	}
	
	public AccountList() {
		super();
	}
	
	public AccountList(List<Account> accounts) {
		this.accounts = accounts;
	}
}
