package com.bihju;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.SubscribableChannel;

public interface Processors {
    String INPUT1 = "input1";
    String INPUT2 = "input2";
    String INPUT3 = "input3";
    String OUTPUT1 = "output1";
    String OUTPUT2 = "output2";
    String OUTPUT3 = "output3";

    @Input(Processors.INPUT1)
    SubscribableChannel input1();

    @Input(Processors.INPUT2)
    SubscribableChannel input2();

    @Input(Processors.INPUT3)
    SubscribableChannel input3();

    @Output(Processors.OUTPUT1)
    MessageChannel output1();

    @Output(Processors.OUTPUT2)
    MessageChannel output2();

    @Output(Processors.OUTPUT3)
    MessageChannel output3();
}
