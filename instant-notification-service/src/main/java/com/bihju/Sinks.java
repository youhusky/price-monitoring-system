package com.bihju;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface Sinks {
    String INPUT1 = "input1";
    String INPUT2 = "input2";
    String INPUT3 = "input3";

    @Input(Sinks.INPUT1)
    SubscribableChannel input1();

    @Input(Sinks.INPUT2)
    SubscribableChannel input2();

    @Input(Sinks.INPUT3)
    SubscribableChannel input3();
}
