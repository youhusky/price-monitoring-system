package com.bihju.service.impl;

import com.bihju.repository.ProductRepository;
import org.junit.Before;
import org.junit.Test;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.mockito.Mockito.mock;

public class ProductServiceImplTest {
    private ProductServiceImpl productServiceImpl;
    private ProductRepository productRepository;

    @Before
    public void setupMock() {
        productRepository = mock(ProductRepository.class);
        productServiceImpl = new ProductServiceImpl(productRepository);
    }

    @Test
    public void testGetDiscountPercent() {
        double discountPercent = productServiceImpl.getDiscountPercent(31.83, 31.81);
        assertThat(discountPercent).isEqualTo(0.06);

    }
}
