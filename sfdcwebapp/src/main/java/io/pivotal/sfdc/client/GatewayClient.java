package io.pivotal.sfdc.client;

import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.Contact;
import io.pivotal.sfdc.domain.Opportunity;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "gatewayservice")
public interface GatewayClient {

    @GetMapping("/authservice/oauth2")
    String getApiSession();

    @RequestMapping(value = "/accountservice/accounts", method = RequestMethod.GET)
    String getContactsByAccounts();

    @RequestMapping(value = "/accountservice/opp_by_accts", method = RequestMethod.GET)
    String getOpportunitiesByAccounts();

    @RequestMapping(value = "/accountservice/account/{accountId}", method = RequestMethod.GET)
    String getAccount(@PathVariable String accountId);

    @RequestMapping(value = "/accountservice/account", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    String createAccount(@RequestBody Account account);

    @RequestMapping(value = "/accountservice/account/{accountId}", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE)
    String updateAccount(@PathVariable String accountId, @RequestBody Account account);

    @RequestMapping(value = "/accountservice/account/{accountId}", method = RequestMethod.DELETE)
    void deleteAccount(@PathVariable String accountId);

    @RequestMapping(value = "/contactservice/contact/{contactId}", method = RequestMethod.GET)
    String getContact(@PathVariable String contactId);

    @RequestMapping(value = "/contactservice/contact", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    String createContact(@RequestBody Contact contact);

    @RequestMapping(value = "/contactservice/contact/{contactId}", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE)
    String updateContact(@PathVariable String contactId, @RequestBody Contact contact);

    @RequestMapping(value = "/contactservice/contact/{contactId}", method = RequestMethod.DELETE)
    void deleteContact(@PathVariable String contactId);

    @RequestMapping(value = "/opportunityservice/opportunity/{opportunityId}", method = RequestMethod.GET)
    String getOpportunity(@PathVariable String opportunityId);

    @RequestMapping(value = "/opportunityservice/opportunity", method = RequestMethod.POST, consumes=MediaType.APPLICATION_JSON_VALUE)
    String createOpportunity(@RequestBody Opportunity opportunity);

    @RequestMapping(value = "/opportunityservice/opportunity/{opportunityId}", method = RequestMethod.PUT, consumes=MediaType.APPLICATION_JSON_VALUE)
    String updateOpportunity(@PathVariable String opportunityId, @RequestBody Opportunity opportunity);

    @RequestMapping(value = "/opportunityservice/opportunity/{opportunityId}", method = RequestMethod.DELETE)
    void deleteOpportunity(@PathVariable String opportunityId);
}
