package io.pivotal.sfdc;

import io.pivotal.sfdc.domain.AccountList;
import io.pivotal.sfdc.source.SFDCAccountNotificationSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(SFDCAccountNotificationSource.class)
public class SFDCAccountNotification {
    @Autowired
    SFDCAccountNotificationSource SFDCAccountNotificationSource;

    private static final Logger LOGGER = LoggerFactory.getLogger(SFDCAccountNotification.class);

    public void forwardAccountNotifications(AccountList accountList) {
        LOGGER.info("Sending notifications: {}",accountList);
        SFDCAccountNotificationSource.notifyAccount().send(MessageBuilder.withPayload(accountList).build());
    }
}
