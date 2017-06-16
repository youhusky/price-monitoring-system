package com.bihju;

import com.bihju.domain.Product;
import com.bihju.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@MessageEndpoint
@EnableBinding(Processors.class)
@Log4j
public class ProductProcessor {
    public final static int PRIORITY_HIGH = 1;
    public final static int PRIORITY_MEDIUM = 2;
    public final static int PRIORITY_LOW = 3;

    @Autowired
    private ProductService productService;
    @Autowired
    private MessageChannel output1;
    @Autowired
    private MessageChannel output2;
    @Autowired
    private MessageChannel output3;

    private StringRedisTemplate template;
    private ValueOperations<String, String> ops;

    @Autowired
    public ProductProcessor(StringRedisTemplate template) {
        this.template = template;
        ops = this.template.opsForValue();
    }

    @ServiceActivator(inputChannel = Processors.INPUT1)
    public void checkProductHigh(Product product) throws Exception {
        log.info("Product received on channel 1, productId = " + product.getProductId());

        processProduct(product);
    }

    @ServiceActivator(inputChannel = Processors.INPUT2)
    public void checkProductMedium(Product product) throws Exception {
        log.info("Product received on channel 2, productId = " + product.getProductId());

        processProduct(product);
    }

    @ServiceActivator(inputChannel = Processors.INPUT3)
    public void checkProductLow(Product product) throws Exception {
        log.info("Product received on channel 3, productId = " + product.getProductId());

        processProduct(product);
    }

    private void processProduct(Product product) {
        if (!isProductExist(product)) {
            cacheProductPrice(product);
            productService.createProduct(product);
        } else {
            double cachedPrice = getCachedPrice(product);

            if (cachedPrice != product.getPrice()) {
                product.setOldPrice(cachedPrice);
                if (cachedPrice > product.getPrice()) {
                    output1.send(MessageBuilder.withPayload(product).build());
                }

                updateCache(product);
                productService.updateProduct(product);
            }
        }
    }

    private boolean isProductExist(Product product) {
        return this.template.hasKey(product.getProductId());
    }

    private void updateCache(Product product) {
        String productId = product.getProductId();
        ops.set(productId, String.valueOf(product.getPrice()));
        log.debug("product price is updated in cache, productId = " + productId
                + ", new price = " + ops.get(productId));
    }

    private void cacheProductPrice(Product product) {
        String productId = product.getProductId();
        ops.set(productId, String.valueOf(product.getPrice()));
        log.debug("product is stored in cache, productId = " + productId
                + ", cached price = " + ops.get(productId));
    }

    private double getCachedPrice(Product product) {
        return Double.parseDouble(ops.get(product.getProductId()));
    }
}
