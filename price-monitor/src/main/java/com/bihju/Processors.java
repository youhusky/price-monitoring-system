package com.bihju;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Processors {
    String INPUT1 = "input1";
    String INPUT2 = "input1";
    String INPUT3 = "input1";
    String OUTPUT = "output";

    @Input(Processors.INPUT1)
    SubscribableChannel input1();

    @Input(Processors.INPUT2)
    SubscribableChannel input2();

    @Input(Processors.INPUT3)
    SubscribableChannel input3();

    @Output(Processors.OUTPUT)
    MessageChannel output();
}
