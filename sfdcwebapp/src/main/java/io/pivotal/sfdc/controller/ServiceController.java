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

//	@Autowired
//	RestTemplate restTemplate;

	@Autowired
	GatewayClient gatewayClient;

//	@Value("${sfdc.apigateway.endpoint}")
//    private String apigatewayEP;

	@Value("${sfdc.service.unavailable}")
	private String unavailable;

    @Bean
    @LoadBalanced
    RestTemplate rest() {
      return new RestTemplate();
    }

	
   @RequestMapping(value = "/oauth2", method = RequestMethod.GET)
    public @ResponseBody String getoauth2() {
    	return gatewayClient.getApiSession();
//       return restTemplate.getForObject(apigatewayEP+"/authservice/oauth2", String.class);
    }

   @RequestMapping(value = "/accounts", method = RequestMethod.GET)
    public @ResponseBody String getContactsByAccounts() {
    	return gatewayClient.getContactsByAccounts();
//       return restTemplate.getForObject(apigatewayEP+"/accountservice/accounts", String.class);
    }

   @RequestMapping(value = "/opp_by_accts", method = RequestMethod.GET)
    public @ResponseBody String getOpportunitiesByAccounts() {
    	return gatewayClient.getOpportunitiesByAccounts();
//       return restTemplate.getForObject(apigatewayEP+"/accountservice/opp_by_accts", String.class);
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
//			restTemplate.put(apigatewayEP+"/accountservice/account/"+accountId, account);
			break;
		default:
		    result = gatewayClient.createAccount(account);
//			result = restTemplate.postForObject(apigatewayEP+"/accountservice/account/", account, String.class);
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
//			restTemplate.delete(apigatewayEP+"/accountservice/account/"+accountId);
			break;
		default:
		    result = gatewayClient.getAccount(accountId);
//			result = restTemplate.getForObject(apigatewayEP+"/accountservice/account/"+accountId, String.class);
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
//			restTemplate.put(apigatewayEP+"/contactservice/contact/"+contactId, contact);
			break;
		default:
		    result = gatewayClient.createContact(contact);
//			result = restTemplate.postForObject(apigatewayEP+"/contactservice/contact/", contact, String.class);
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
//			restTemplate.delete(apigatewayEP+"/contactservice/contact/"+contactId);
			break;
		default:
		    result = gatewayClient.getContact(contactId);
//			result = restTemplate.getForObject(apigatewayEP+"/contactservice/contact/"+contactId, String.class);
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
//			restTemplate.put(apigatewayEP+"/opportunityservice/opportunity/"+opportunityId, opportunity);
			break;
		default:
		    result = gatewayClient.createOpportunity(opportunity);
//			result = restTemplate.postForObject(apigatewayEP+"/opportunityservice/opportunity/", opportunity, String.class);
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
//			restTemplate.delete(apigatewayEP+"/opportunityservice/opportunity/"+opportunityId);
			break;
		default:
		    result = gatewayClient.getOpportunity(opportunityId);
//			result = restTemplate.getForObject(apigatewayEP+"/opportunityservice/opportunity/"+opportunityId, String.class);
			break;
		}
		return result;
	}
}
