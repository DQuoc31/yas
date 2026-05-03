package com.yas.recommendation.vector.product.service;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.recommendation.vector.product.store.ProductVectorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductVectorSyncServiceTest {

    @Mock
    private ProductVectorRepository productVectorRepository;

    @Mock
    private Product product;

    private ProductVectorSyncService productVectorSyncService;

    @BeforeEach
    void setUp() {
        productVectorSyncService = new ProductVectorSyncService(productVectorRepository);
    }

    @Test
    void createProductVector_WhenPublished_CallsAdd() {
        when(product.getId()).thenReturn(1L);
        when(product.isPublished()).thenReturn(true);

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository).add(1L);
    }

    @Test
    void createProductVector_WhenNotPublished_DoesNotCallAdd() {
        when(product.isPublished()).thenReturn(false);

        productVectorSyncService.createProductVector(product);

        verify(productVectorRepository, never()).add(anyLong());
    }

    @Test
    void updateProductVector_WhenPublished_CallsUpdate() {
        when(product.getId()).thenReturn(1L);
        when(product.isPublished()).thenReturn(true);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).update(1L);
    }

    @Test
    void updateProductVector_WhenNotPublished_CallsDelete() {
        when(product.getId()).thenReturn(1L);
        when(product.isPublished()).thenReturn(false);

        productVectorSyncService.updateProductVector(product);

        verify(productVectorRepository).delete(1L);
    }

    @Test
    void deleteProductVector_WhenCalled_CallsDelete() {
        Long productId = 1L;

        productVectorSyncService.deleteProductVector(productId);

        verify(productVectorRepository).delete(productId);
    }
}
