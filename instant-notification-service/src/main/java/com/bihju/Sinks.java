package com.bihju;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Sinks {
    String INPUT = "input";

    @Input(Sinks.INPUT)
    SubscribableChannel input();
}
