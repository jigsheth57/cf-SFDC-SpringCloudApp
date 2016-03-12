package io.pivotal.sfdc.controller;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import com.force.api.ForceApi;

import io.pivotal.sfdc.domain.Opportunity;
import io.pivotal.sfdc.service.OpportunityService;

@RestController
public class OpportunityServiceController {

    @Autowired
    private OpportunityService opportunityService;
    
	private static final Logger logger = LoggerFactory.getLogger(OpportunityServiceController.class);

	ForceApi api;
    
	@RequestMapping(value = "/opportunity/{id}", method={RequestMethod.POST, RequestMethod.PUT})
	public ResponseEntity<Opportunity> cuOpportunity(@PathVariable("id") final String opportunityId, @RequestBody final Opportunity opportunity, UriComponentsBuilder builder) {
		logger.debug("(C)-R(U)-D operation on Opportunity");
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		HttpHeaders responseHeaders = new HttpHeaders();
		logger.debug("method: "+method);
		Opportunity newOpportunity = null;
		try {
			switch (method) {
			case "put":
				opportunity.setId(opportunityId);
				newOpportunity = opportunityService.updateOpportunity(opportunity);
				break;
			default:
				newOpportunity = opportunityService.addOpportunity(opportunity);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseHeaders.setLocation(builder.path("/opportunity/{id}")
				.buildAndExpand(newOpportunity.getId()).toUri());
		if (newOpportunity != null && newOpportunity.getId() != null) {
			return new ResponseEntity<Opportunity>(newOpportunity, responseHeaders, (method.equalsIgnoreCase("post") ? HttpStatus.CREATED : HttpStatus.OK));
		} else {
			logger.warn("new Opportunity not created!");
			return new ResponseEntity<Opportunity>(newOpportunity, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/opportunity/{id}", method={RequestMethod.GET, RequestMethod.DELETE})
	public ResponseEntity<Opportunity> rdOpportunity(@PathVariable("id") final String opportunityId, UriComponentsBuilder builder) {
		logger.debug("-C(R)-U(D) operation on Opportunity");
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		HttpHeaders responseHeaders = new HttpHeaders();

		Opportunity opportunity = null;
		try {
			switch (method) {
			case "delete":
				opportunityService.deleteOpportunity(opportunityId);
				opportunity = new Opportunity();
				opportunity.setId(opportunityId);
				break;
			default:
				opportunity = opportunityService.getOpportunity(opportunityId);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseHeaders.setLocation(builder.path("/opportunity/{id}")
				.buildAndExpand(opportunity.getId()).toUri());
		if (opportunity != null && opportunity.getId() != null) {
			return new ResponseEntity<Opportunity>(opportunity, responseHeaders, HttpStatus.OK);
		} else {
			logger.warn("Problem retrieving/deleting Opportunity");
			return new ResponseEntity<Opportunity>(opportunity, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
