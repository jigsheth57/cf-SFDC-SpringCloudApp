package io.pivotal.sfdc.source;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface SFDCAccountNotificationSource {

    @Output("accountNotification")
    MessageChannel notifyAccount();

}
