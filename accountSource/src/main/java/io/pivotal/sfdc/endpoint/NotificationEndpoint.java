package io.pivotal.sfdc.endpoint;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.sforce.soap._2005._09.outbound.AccountNotification;
import com.sforce.soap._2005._09.outbound.Notifications;
import com.sforce.soap._2005._09.outbound.NotificationsResponse;
import com.sforce.soap._2005._09.outbound.ObjectFactory;
import io.pivotal.sfdc.SFDCAccountNotification;
import io.pivotal.sfdc.domain.Account;
import io.pivotal.sfdc.domain.AccountList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Endpoint
public class NotificationEndpoint {
    private static final Logger LOGGER = LoggerFactory.getLogger(NotificationEndpoint.class);

    @Autowired
    SFDCAccountNotification acctNotify;

    final ObjectMapper mapper = new ObjectMapper();

    @PayloadRoot(
            namespace = "http://soap.sforce.com/2005/09/outbound",
            localPart = "notifications")
    @ResponsePayload
    public NotificationsResponse notifications(@RequestPayload Notifications request) throws Exception {
        LOGGER.info("Notification received.");
        List<AccountNotification> notificationsList = request.getNotification();
        List<Account> accountList = new ArrayList<Account>();
        Iterator<AccountNotification> notificationIterator = notificationsList.iterator();
        while (notificationIterator.hasNext()) {
            AccountNotification accountNotification = notificationIterator.next();
            String acctId = accountNotification.getSObject().getId();
            Account account = new Account();
            account.setId(acctId);
            accountList.add(account);
            LOGGER.info("Account info: accountID={}",acctId);
        }


        ObjectFactory factory = new ObjectFactory();
        NotificationsResponse response = factory.createNotificationsResponse();
        response.setAck(true);

        LOGGER.info("Sending notification.ack='{}'",response.isAck());
        AccountList acctList = new AccountList();
        acctList.setAccounts(accountList);
        LOGGER.debug("forwarding account notifications to be process: {}",convert(acctList));
        acctNotify.forwardAccountNotifications(acctList);

        return response;
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
        return jsonData.toString();
    }

}
