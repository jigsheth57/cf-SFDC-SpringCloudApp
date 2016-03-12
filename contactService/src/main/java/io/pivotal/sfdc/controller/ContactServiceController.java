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

import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.service.ContactService;

@RestController
public class ContactServiceController {

    @Autowired
    private ContactService contactService;
    
	private static final Logger logger = LoggerFactory.getLogger(ContactServiceController.class);

	ForceApi api;
    
	@RequestMapping(value = "/contact/{id}", method={RequestMethod.POST, RequestMethod.PUT})
	public ResponseEntity<Contact> cuContact(@PathVariable("id") final String contactId, @RequestBody final Contact contact, UriComponentsBuilder builder) {
		logger.debug("(C)-R(U)-D operation on Contact");
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		HttpHeaders responseHeaders = new HttpHeaders();
		logger.debug("method: "+method);
		Contact newContact = null;
		try {
			switch (method) {
			case "put":
				contact.setId(contactId);
				newContact = contactService.updateContact(contact);
				break;
			default:
				newContact = contactService.addContact(contact);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseHeaders.setLocation(builder.path("/contact/{id}")
				.buildAndExpand(newContact.getId()).toUri());
		if (newContact != null && newContact.getId() != null) {
			return new ResponseEntity<Contact>(newContact, responseHeaders, (method.equalsIgnoreCase("post") ? HttpStatus.CREATED : HttpStatus.OK));
		} else {
			logger.warn("new Contact not created!");
			return new ResponseEntity<Contact>(newContact, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	@RequestMapping(value = "/contact/{id}", method={RequestMethod.GET, RequestMethod.DELETE})
	public ResponseEntity<Contact> rdContact(@PathVariable("id") final String contactId, UriComponentsBuilder builder) {
		logger.debug("-C(R)-U(D) operation on Contact");
	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
		String method = request.getMethod().toLowerCase();
		HttpHeaders responseHeaders = new HttpHeaders();

		Contact contact = null;
		try {
			switch (method) {
			case "delete":
				contactService.deleteContact(contactId);
				contact = new Contact();
				contact.setId(contactId);
				break;
			default:
				contact = contactService.getContact(contactId);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		responseHeaders.setLocation(builder.path("/contact/{id}")
				.buildAndExpand(contact.getId()).toUri());
		if (contact != null && contact.getId() != null) {
			return new ResponseEntity<Contact>(contact, responseHeaders, HttpStatus.OK);
		} else {
			logger.warn("Problem retrieving/deleting Contact");
			return new ResponseEntity<Contact>(contact, responseHeaders, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}
