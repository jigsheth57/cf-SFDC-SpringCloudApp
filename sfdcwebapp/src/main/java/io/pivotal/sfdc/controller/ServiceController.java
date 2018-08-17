package io.pivotal.sfdc.controller;


import io.pivotal.sfdc.client.GatewayClient;
import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.domain.Opportunity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;

@RestController
public class ServiceController {

	private static final Logger logger = LoggerFactory.getLogger(ServiceController.class);

	@Autowired
	GatewayClient gatewayClient;

	@Value("${sfdc.service.unavailable}")
	private String unavailable;

	@RequestMapping(value = "/health", method = RequestMethod.GET)
	public @ResponseBody String getHealth() {
		return gatewayClient.getHealth();
	}

   @RequestMapping(value = "/oauth2", method = RequestMethod.GET)
    public @ResponseBody String getoauth2() {
    	return gatewayClient.getApiSession();
    }

   @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody String getContactsByAccounts() {
    	return gatewayClient.getContactsByAccounts();
    }

   @RequestMapping(value = "/opp_by_accts", method = RequestMethod.GET)
    public @ResponseBody String getOpportunitiesByAccounts() {
    	return gatewayClient.getOpportunitiesByAccounts();
    }

   @RequestMapping(value = "/account/{id}", method={RequestMethod.POST, RequestMethod.PUT})
	public @ResponseBody String cuAccount(@PathVariable("id") final String accountId, @RequestBody final Account account) {
	    logger.debug("{cuAccount}Creating account: {}",accountId);
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
		case "put":
		    gatewayClient.updateAccount(accountId,account);
			break;
		default:
		    result = gatewayClient.createAccount(account);
			break;
		}
		return result;
	}
	
	@RequestMapping(value = "/account/{id}", method={RequestMethod.GET, RequestMethod.DELETE})
	public @ResponseBody String rdAccount(@PathVariable("id") final String accountId) {
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
		case "delete":
		    gatewayClient.deleteAccount(accountId);
			break;
		default:
		    result = gatewayClient.getAccount(accountId);
			break;
		}
		return result;
	}
	
	@RequestMapping(value = "/contact/{id}", method={RequestMethod.POST, RequestMethod.PUT})
	public @ResponseBody String cuContact(@PathVariable("id") final String contactId, @RequestBody final Contact contact, UriComponentsBuilder builder) {
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
		case "put":
			contact.setName(null);
			gatewayClient.updateContact(contactId,contact);
			break;
		default:
		    result = gatewayClient.createContact(contact);
			break;
		}
		return result;
	}
	
	@RequestMapping(value = "/contact/{id}", method={RequestMethod.GET, RequestMethod.DELETE})
	public @ResponseBody String rdContact(@PathVariable("id") final String contactId, UriComponentsBuilder builder) {
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
		case "delete":
		    gatewayClient.deleteContact(contactId);
			break;
		default:
		    result = gatewayClient.getContact(contactId);
			break;
		}
		return result;
	}
	
	@RequestMapping(value = "/opportunity/{id}", method={RequestMethod.POST, RequestMethod.PUT})
	public @ResponseBody String cuOpportunity(@PathVariable("id") final String opportunityId, @RequestBody final Opportunity opportunity, UriComponentsBuilder builder) {
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
		case "put":
		    gatewayClient.updateOpportunity(opportunityId, opportunity);
			break;
		default:
		    result = gatewayClient.createOpportunity(opportunity);
			break;
		}
		return result;
	}

	@RequestMapping(value = "/opportunity/{id}", method={RequestMethod.GET, RequestMethod.DELETE})
	public @ResponseBody String rdOpportunity(@PathVariable("id") final String opportunityId, UriComponentsBuilder builder) {
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		String result = unavailable;
		switch (method) {
		case "delete":
		    gatewayClient.deleteOpportunity(opportunityId);
			break;
		default:
		    result = gatewayClient.getOpportunity(opportunityId);
			break;
		}
		return result;
	}
}
