package com.bihju;

import com.bihju.domain.Product;
import com.bihju.service.UserService;
import com.bihju.util.MailUtil;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import java.util.List;

@MessageEndpoint
@EnableBinding(Sinks.class)
@Log4j
public class ReducedProductSink {
    private final static String MAIL_SUBJECT = "Your Instant Deal Alert";
    private final static String MAIL_TEMPLATE = "<html><body>Hello,<br><br>" +
            "The following is your instant deal alert:<br><br>" +
            // TODO find out why this is not working
//            "Product: <a href='$DETAIL_URL'>$PRODUCT_TITLE</a><br>" +
            "Product: $PRODUCT_TITLE<br>" +
            "DetailUrl: $DETAIL_URL<br>" +
            "Price: $$NEW_PRICE<br>" +
            "Original price: $$OLD_PRICE<br>" +
            "Discount percent: $DISCOUNT_PERCENT%<br>" +
            "CategoryId: $CATEGORY_ID</body></html>";

    private UserService userService;
    private MailUtil mailUtil;
    @Value("${spring.mail.username}")
    private String MAIL_USER;

    @Autowired
    public ReducedProductSink(UserService userService, MailUtil mailUtil) {
        this.userService = userService;
        this.mailUtil = mailUtil;
    }

    @ServiceActivator(inputChannel = Sinks.INPUT1)
    public void processProductsHigh(Product product) throws Exception {
        log.info("Product received from high priority queue, productId = " + product.getProductId() + ", categoryId = " + product.getCategoryId());
        sendNotificationToSubscribers(product);
    }

    @ServiceActivator(inputChannel = Sinks.INPUT2)
    public void processProductsMedium(Product product) throws Exception {
        log.info("Product received from medium priority queue, productId = " + product.getProductId() + ", categoryId = " + product.getCategoryId());
        sendNotificationToSubscribers(product);
    }

    @ServiceActivator(inputChannel = Sinks.INPUT3)
    public void processProductsLow(Product product) throws Exception {
        log.info("Product received from low priority queue, productId = " + product.getProductId() + ", categoryId = " + product.getCategoryId());
        sendNotificationToSubscribers(product);
    }

    private void sendNotificationToSubscribers(final Product product) {
        Thread thread = new Thread() {
            public void run() {
                long categoryId = product.getCategoryId();
                List<String> emails = userService.findUsersByCategoryId(categoryId);
                if (!emails.isEmpty()) {
                    sendNotification(emails.toArray(new String[0]), product);
                }
            }
        };

        thread.start();
    }

    private void sendNotification(String[] emails, Product product) {
        String body = MAIL_TEMPLATE
                .replace("$PRODUCT_TITLE", product.getTitle())
                .replace("$DETAIL_URL", product.getDetailUrl())
                .replace("$CATEGORY_ID", String.valueOf(product.getCategoryId()))
                .replace("$DISCOUNT_PERCENT", String.valueOf(product.getDiscountPercent()))
                .replace("$NEW_PRICE", String.valueOf(product.getPrice()))
                .replace("$OLD_PRICE", String.valueOf(product.getOldPrice()));
        mailUtil.send(emails, MAIL_USER, MAIL_USER
                , MAIL_SUBJECT, body);
    }
}
