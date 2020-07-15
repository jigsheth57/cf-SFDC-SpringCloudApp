package io.pivotal.sfdc.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.domain.Opportunity;
import io.pivotal.sfdc.service.AccountService;
import io.pivotal.sfdc.service.ContactService;
import io.pivotal.sfdc.service.OpportunityService;

@RestController
public class ServiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

	@Autowired
	private AccountService accountService;

	@Autowired
	private ContactService contactService;

	@Autowired
	private OpportunityService opportunityService;

	@Value("${sfdc.service.unavailable}")
	private String unavailable;

	// @RequestMapping(value = "/health", method = RequestMethod.GET)
	// public @ResponseBody String getHealth() {
	// return gatewayClient.getHealth();
	// }

	// @RequestMapping(value = "/oauth2", method = RequestMethod.GET)
	// public @ResponseBody String getoauth2() {
	// return gatewayClient.getApiSession();
	// }

	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	public @ResponseBody String getContactsByAccounts() {
		return accountService.getContactsByAccounts();
	}

	@RequestMapping(value = "/opp_by_accts", method = RequestMethod.GET)
	public @ResponseBody String getOpportunitiesByAccounts() {
		return accountService.getOpportunitesByAccounts();
	}

	@RequestMapping(value = "/account/{id}", method = { RequestMethod.POST, RequestMethod.PUT })
	public @ResponseBody String cuAccount(@PathVariable("id") final String accountId,
			@RequestBody final Account account) {
		LOGGER.debug("{cuAccount}Creating account: {}", accountId);
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
			case "put":
				accountService.updateAccount(accountId, account);
				result = "";
				break;
			default:
				result = accountService.createAccount(account);
				break;
		}
		return result;
	}

	@RequestMapping(value = "/account/{id}", method = { RequestMethod.GET, RequestMethod.DELETE })
	public @ResponseBody String rdAccount(@PathVariable("id") final String accountId) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
			case "delete":
				accountService.deleteAccount(accountId);
				result = "";
				break;
			default:
				result = accountService.getAccount(accountId);
				break;
		}
		return result;
	}

	@RequestMapping(value = "/contact/{id}", method = { RequestMethod.POST, RequestMethod.PUT })
	public @ResponseBody String cuContact(@PathVariable("id") final String contactId,
			@RequestBody final Contact contact, UriComponentsBuilder builder) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
			case "put":
				contact.setName(null);
				contactService.updateContact(contactId, contact);
				result = "";
				break;
			default:
				result = contactService.createContact(contact);
				break;
		}
		return result;
	}

	@RequestMapping(value = "/contact/{id}", method = { RequestMethod.GET, RequestMethod.DELETE })
	public @ResponseBody String rdContact(@PathVariable("id") final String contactId, UriComponentsBuilder builder) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
			case "delete":
				contactService.deleteContact(contactId);
				result = "";
				break;
			default:
				result = contactService.getContact(contactId);
				break;
		}
		return result;
	}

	@RequestMapping(value = "/opportunity/{id}", method = { RequestMethod.POST, RequestMethod.PUT })
	public @ResponseBody String cuOpportunity(@PathVariable("id") final String opportunityId,
			@RequestBody final Opportunity opportunity, UriComponentsBuilder builder) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
			case "put":
				opportunityService.updateOpportunity(opportunityId, opportunity);
				result = "";
				break;
			default:
				result = opportunityService.createOpportunity(opportunity);
				break;
		}
		return result;
	}

	@RequestMapping(value = "/opportunity/{id}", method = { RequestMethod.GET, RequestMethod.DELETE })
	public @ResponseBody String rdOpportunity(@PathVariable("id") final String opportunityId,
			UriComponentsBuilder builder) {
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
			case "delete":
				opportunityService.deleteOpportunity(opportunityId);
				result = "";
				break;
			default:
				result = opportunityService.getOpportunity(opportunityId);
				break;
		}
		return result;
	}
}
