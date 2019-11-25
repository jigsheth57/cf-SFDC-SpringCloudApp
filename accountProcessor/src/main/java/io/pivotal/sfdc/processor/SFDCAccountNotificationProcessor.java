package io.pivotal.sfdc.processor;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface SFDCAccountNotificationProcessor {

    String INPUT = "accountProcessor";

    @Input("accountProcessor")
    SubscribableChannel processAccount();

    @Output("accountUpdater")
    MessageChannel updateCache();
}
