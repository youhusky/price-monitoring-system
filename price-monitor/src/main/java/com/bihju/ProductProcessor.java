package com.bihju;

import com.bihju.domain.Product;
import com.bihju.service.ProductService;
import lombok.extern.log4j.Log4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;

@MessageEndpoint
@EnableBinding(Processor.class)
@Log4j
public class ProductProcessor {
    @Autowired
    private ProductService productService;
    @Autowired
    private MessageChannel output;

    private StringRedisTemplate template;
    private ValueOperations<String, String> ops;

    @Autowired
    public ProductProcessor(StringRedisTemplate template) {
        this.template = template;
        ops = this.template.opsForValue();
    }

    @ServiceActivator(inputChannel = Sink.INPUT)
    public void checkProduct(Product product) throws Exception {
        log.info("Product received, productId = " + product.getProductId());
        if (!isProductExist(product)) {
            cacheProduct(product);
            productService.createProduct(product);
        } else {
            double oldPrice = getOldPrice(product);
            double newPrice = product.getPrice();

            if (oldPrice != newPrice) {
                updateCache(product);
                productService.updateProduct(product);
            }
            if (oldPrice > newPrice) {
                output.send(MessageBuilder.withPayload(product).build());
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

    private void cacheProduct(Product product) {
        String productId = product.getProductId();
        ops.set(productId, String.valueOf(product.getPrice()));
        log.debug("product is stored in cache, productId = " + productId
                + ", cached price = " + ops.get(productId));
    }

    private double getOldPrice(Product product) {
        String productId = product.getProductId();
        return Double.parseDouble(ops.get(productId));
    }
}
