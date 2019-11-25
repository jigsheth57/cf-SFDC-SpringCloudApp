package io.pivotal.sfdc.sink;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface SFDCAccountNotificationSink {

    String INPUT = "accountSink";

    @Input("accountSink")
    SubscribableChannel updateCachedAccount();
}
