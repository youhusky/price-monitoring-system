package com.bihju;

import com.bihju.domain.Product;
import com.bihju.service.UserService;
import com.bihju.util.MailUtil;
import lombok.extern.log4j.Log4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.util.List;

@MessageEndpoint
@EnableBinding(Sink.class)
@Log4j
public class ReducedProductSink {
    private final static String MAIL_SUBJECT = "Price reduction";
    private final static String MAIL_TEMPLATE = "The price of product $PRODUCT_TITLE has been reduced to $NEW_PRICE from $OLD_PRICE";
    private UserService userService;
    private MailUtil mailUtil;

    @Autowired
    public ReducedProductSink(UserService userService, MailUtil mailUtil) {
        this.userService = userService;
        this.mailUtil = mailUtil;
    }

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void processProducts(Product product) throws Exception {
        log.info("Product received, productId = " + product.getProductId() + ", categoryId = " + product.getCategoryId());
        long categoryId = product.getCategoryId();
        List<String> emails = userService.findUsersByCategoryId(categoryId);
        if (!emails.isEmpty()) {
            sendNotification(emails, product);
        }
    }

    private void sendNotification(List<String> emails, Product product) {
        String body = MAIL_TEMPLATE.replace("$PRODUCT_TITLE", product.getTitle())
                .replace("$NEW_PRICE", String.valueOf(product.getPrice()))
                .replace("$OLD_PRICE", String.valueOf(product.getOldPrice()));
        mailUtil.send(StringUtils.join(emails, ","), "no-reply", "no-reply"
                , MAIL_SUBJECT, body);
    }
}
