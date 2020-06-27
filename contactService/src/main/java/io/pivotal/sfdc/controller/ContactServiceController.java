package io.pivotal.sfdc.controller;

import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.service.ContactService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RefreshScope
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactServiceController {

	@Autowired
	private ContactService contactService;

	private static final Logger LOGGER = LoggerFactory.getLogger(ContactServiceController.class);

	/**
	 * Calls a contact service with contact object to create new contact in sfdc.
	 * 
	 * @return Contact newly created contact object
	 */
	@RequestMapping(value = "/contact", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Create new contact", notes = "Calls a contact service to create new contact", response = Contact.class)
	public ResponseEntity<Contact> post(
			@ApiParam(value = "Contact model", required = true) @RequestBody Contact contact) {
		contact.setId(null);
		try {
			contact = contactService.addContact(contact);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not create new contact: [%s]", contact));
			return new ResponseEntity<Contact>(contact, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		HttpStatus httpstatus = HttpStatus.CREATED;
		LOGGER.debug("Created new contact with id {}: [{}]", contact.getId(), contact);
		return new ResponseEntity<Contact>(contact, new HttpHeaders(), httpstatus);
	}

	/**
	 * Calls a contact service with contact object to update existing contact in
	 * sfdc.
	 * 
	 * @return Contact updated contact object
	 */
	@RequestMapping(value = "/contact/{contactId}", method = RequestMethod.PUT, consumes = MediaType.APPLICATION_JSON_VALUE)
	@ApiOperation(value = "Update contact by id", notes = "Calls a contact service to update existing contact by id", response = Contact.class)
	public ResponseEntity<Contact> put(@ApiParam(value = "Contact ID", required = true) @PathVariable String contactId,
			@ApiParam(value = "Contact model", required = true) @RequestBody Contact contact) {
		HttpStatus httpstatus = HttpStatus.OK;
		contact.setId(contactId);
		try {
			contact = contactService.updateContact(contact);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not update existing contact with id %s: [%s]", contact.getId(), contact));
			return new ResponseEntity<Contact>(contact, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		LOGGER.debug("Updated existing contact with id {}: [{}]", contact.getId(), contact);
		return new ResponseEntity<Contact>(contact, new HttpHeaders(), httpstatus);
	}

	/**
	 * Calls a contact service with contact id to delete contact from sfdc.
	 * 
	 * @return void
	 */
	@RequestMapping(value = "/contact/{contactId}", method = RequestMethod.DELETE)
	@ApiOperation(value = "Delete contact by id", notes = "Calls a contact service to remove contact by id")
	public ResponseEntity<?> delete(@ApiParam(value = "Contact ID", required = true) @PathVariable String contactId) {
		HttpStatus httpstatus = HttpStatus.OK;
		try {
			contactService.deleteContact(contactId);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not remove existing contact with id %s", contactId));
			ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR);
			return ResponseEntity.noContent().build();
		}
		LOGGER.debug("Remove contact for id {}", contactId);
		ResponseEntity.status(httpstatus);
		return ResponseEntity.noContent().build();
	}

	/**
	 * Calls a contact service with contact id to retrieve contact from sfdc.
	 * 
	 * @return Contact contact
	 */
	@RequestMapping(value = "/contact/{contactId}", method = RequestMethod.GET)
	@ApiOperation(value = "Retrieve contact by id", notes = "Calls a contact service to retrieve contact by id", response = Contact.class)
	public ResponseEntity<Contact> get(
			@ApiParam(value = "Contact ID", required = true) @PathVariable String contactId) {
		HttpStatus httpstatus = HttpStatus.OK;
		Contact contact = null;
		try {
			contact = contactService.getContact(contactId);
		} catch (Exception e) {
			e.printStackTrace();
			LOGGER.error(String.format("Can not retrieve existing contact with id %s", contactId));
		}
		if (contact == null) {
			contact = new Contact();
			contact.setId(contactId);
			httpstatus = HttpStatus.INTERNAL_SERVER_ERROR;
		}
		return new ResponseEntity<Contact>(contact, new HttpHeaders(), httpstatus);
	}
}
