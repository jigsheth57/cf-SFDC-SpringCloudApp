package io.pivotal.sfdc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.AccountList;
import io.pivotal.sfdc.processor.SFDCAccountNotificationProcessor;
import io.pivotal.sfdc.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.support.MessageBuilder;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

@EnableBinding(SFDCAccountNotificationProcessor.class)
public class SFDCAccountProcessor {
    @Autowired
    SFDCAccountNotificationProcessor processor;

    @Autowired
    private AccountService accountService;

    private static final Logger LOGGER = LoggerFactory.getLogger(SFDCAccountProcessor.class);

    final ObjectMapper mapper = new ObjectMapper();

    @StreamListener(SFDCAccountNotificationProcessor.INPUT)
    public void enrichAccounts(AccountList accountList) throws Exception {
        LOGGER.info("notification received for account updates: {}",accountList);
        List<Account> accounts = accountList.getAccounts();
        List<Account> enrichedAccounts = new ArrayList<Account>();
        for (Account account : accounts) {
            Account enrichAccount = null;
            try {
                enrichAccount = accountService.getAccount(account.getId());
            } catch (Exception e) {
                e.printStackTrace();
                LOGGER.error(String.format("Can not retrieve existing account with id %s",account.getId()));
            }
            if(enrichAccount == null) {
                enrichAccount = account;
            }
            enrichedAccounts.add(enrichAccount);
        }
        accountList.setAccounts(enrichedAccounts);
        LOGGER.debug("forwarding enriched account list to be cached: {}",convert(accountList));
        processor.updateCache().send(MessageBuilder.withPayload(accountList).build());
    }

    /**
     * Prints serialized domain object
     *
     * @param obj
     * @return String
     * @throws Exception
     */
    private String convert(Object obj) throws Exception {
        mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        //writing to console, can write to any output stream such as file
        StringWriter jsonData = new StringWriter();
        mapper.writeValue(jsonData, obj);
        String jsonDataStr = jsonData.toString();
        LOGGER.debug("value: {}",jsonDataStr);
        return jsonDataStr;
    }

}
