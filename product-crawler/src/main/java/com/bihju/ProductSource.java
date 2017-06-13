package com.bihju;

import com.bihju.domain.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

//@EnableBinding(Source.class)
@EnableBinding(Sources.class)
public class ProductSource {
    @Autowired
    private MessageChannel output1;
    @Autowired
    private MessageChannel output2;
    @Autowired
    private MessageChannel output3;

    public void sendProductToQueue(Product product, int priority) {
        switch (priority) {
            case ProductCrawlerTask.PRIORITY_HIGH:
                output1.send(MessageBuilder.withPayload(product).build());
                break;

            case ProductCrawlerTask.PRIORITY_MEDIUM:
                output2.send(MessageBuilder.withPayload(product).build());
                break;

            case ProductCrawlerTask.PRIORITY_LOW:
                output3.send(MessageBuilder.withPayload(product).build());
                break;
        }
    }
}
