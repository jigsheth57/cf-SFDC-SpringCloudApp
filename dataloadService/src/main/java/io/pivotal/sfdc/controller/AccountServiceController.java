package io.pivotal.sfdc.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.force.api.ForceApi;

/**
 * SFDC Account Service Controller
 * This controller supports account query by contact & opportunity. It also exposes URI to create, retrieve
 * update & delete account object in SFDC.
 * 
 * @author Jignesh Sheth
 *
 */
@RestController
public class AccountServiceController {

	@Value("${sfdc.service.unavailable}")
    private String unavailable;

    
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	ForceApi api;

    @RequestMapping(value = "/datasync",  method={RequestMethod.POST, RequestMethod.GET})
    public @ResponseBody String service(RequestEntity<byte[]> incoming) {
        try {
			logger.info("Incoming Request Body: {}", incoming);
			byte[] rbody = incoming.getBody();
			if(rbody != null)
				logger.info("Incoming Request Body: {}", rbody.toString());
			logger.debug(unavailable);
		} catch (Exception e) {
			e.printStackTrace();
		}
        return unavailable;
    }

}

