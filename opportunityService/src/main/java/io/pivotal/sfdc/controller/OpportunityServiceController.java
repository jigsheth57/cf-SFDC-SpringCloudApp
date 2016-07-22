package io.pivotal.sfdc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.force.api.ForceApi;

import io.pivotal.sfdc.domain.Opportunity;
import io.pivotal.sfdc.service.OpportunityService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

@RestController
@RefreshScope
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class OpportunityServiceController {

    @Autowired
    private OpportunityService opportunityService;
    
	private static final Logger logger = LoggerFactory.getLogger(OpportunityServiceController.class);

	ForceApi api;
    
	/**
	 * Calls a opportunity service with opportunity object to create new opportunity in sfdc.
	 * 
	 * @return Opportunity newly created opportunity object
	 */
	@RequestMapping(value = "/opportunity", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create new opportunity",notes = "Calls a opportunity service to create new opportunity", response = Opportunity.class)
	public ResponseEntity<Opportunity> post(@ApiParam(value = "Opportunity model", required = true) @RequestBody Opportunity opportunity) {
		opportunity.setId(null);
		try {
			opportunity = opportunityService.addOpportunity(opportunity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("Can not create new opportunity: [%s]", opportunity));
			return new ResponseEntity<Opportunity>(opportunity, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		HttpStatus httpstatus = HttpStatus.CREATED;
		logger.debug(String.format("Created new opportunity with id %s: [%s]",opportunity.getId(), opportunity));
		return new ResponseEntity<Opportunity>(opportunity, new HttpHeaders(), httpstatus);
	}

	/**
	 * Calls a opportunity service with opportunity object to update existing opportunity in sfdc.
	 * 
	 * @return Opportunity updated opportunity object
	 */
	@RequestMapping(value = "/opportunity/{opportunityId}", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update opportunity by id",notes = "Calls a opportunity service to update existing opportunity by id", response = Opportunity.class)
	public ResponseEntity<Opportunity> put(@ApiParam(value = "Opportunity ID", required = true) @PathVariable String opportunityId, @ApiParam(value = "Opportunity model", required = true) @RequestBody Opportunity opportunity) {
		HttpStatus httpstatus = HttpStatus.OK;
		opportunity.setId(opportunityId);
		try {
			opportunity = opportunityService.updateOpportunity(opportunity);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("Can not update existing opportunity with id %s: [%s]",opportunity.getId(), opportunity));
			return new ResponseEntity<Opportunity>(opportunity, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		logger.debug(String.format("Updated existing opportunity with id %s: [%s]",opportunity.getId(), opportunity));
		return new ResponseEntity<Opportunity>(opportunity, new HttpHeaders(), httpstatus);
	}

	/**
	 * Calls a opportunity service with opportunity id to delete opportunity from sfdc.
	 * 
	 * @return void 
	 */
	@RequestMapping(value = "/opportunity/{opportunityId}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Delete opportunity by id",notes = "Calls a opportunity service to remove opportunity by id")
	public ResponseEntity<?> delete(@ApiParam(value = "Opportunity ID", required = true) @PathVariable String opportunityId) {
		HttpStatus httpstatus = HttpStatus.OK;
		try {
			opportunityService.deleteOpportunity(opportunityId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("Can not remove existing opportunity with id %s",opportunityId));
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			return ResponseEntity.noContent().build();
		}
		logger.debug(String.format("Remove opportunity for id %s",opportunityId));
		ResponseEntity.status(httpstatus);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Calls a opportunity service with opportunity id to retrieve opportunity from sfdc.
	 * 
	 * @return Opportunity opportunity 
	 */
	@RequestMapping(value = "/opportunity/{opportunityId}", method = RequestMethod.GET)
	@ApiOperation(value = "Retrieve opportunity by id", notes = "Calls a opportunity service to retrieve opportunity by id", response = Opportunity.class)
	public ResponseEntity<Opportunity> get(@ApiParam(value = "Opportunity ID", required = true) @PathVariable String opportunityId) {
		HttpStatus httpstatus = HttpStatus.OK;
		Opportunity opportunity = null;
		try {
			opportunity = opportunityService.getOpportunity(opportunityId);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(String.format("Can not retrieve existing opportunity with id %s",opportunityId));
		}
		if(opportunity == null) {
			opportunity = new Opportunity();
			opportunity.setId(opportunityId);
			httpstatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Opportunity>(opportunity, new HttpHeaders(), httpstatus);
	}
}
