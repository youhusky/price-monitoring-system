package com.bihju;

import org.springframework.cloud.stream.annotation.Output;
import org.springframework.messaging.MessageChannel;

public interface Sources {
    String OUTPUT1 = "output1";
    String OUTPUT2 = "output2";
    String OUTPUT3 = "output3";

    @Output(Sources.OUTPUT1)
    MessageChannel output1();

    @Output(Sources.OUTPUT2)
    MessageChannel output2();

    @Output(Sources.OUTPUT3)
    MessageChannel output3();
}
