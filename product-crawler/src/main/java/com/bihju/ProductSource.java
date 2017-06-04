package com.bihju;

import com.bihju.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@EnableBinding(Source.class)
public class ProductSource {
    @Autowired
    private MessageChannel output;

    public void sendProductToQueue(Product product) {
        output.send(MessageBuilder.withPayload(product).build());
    }
}
