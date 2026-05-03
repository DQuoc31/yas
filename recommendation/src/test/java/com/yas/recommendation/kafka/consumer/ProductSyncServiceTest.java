package com.yas.recommendation.kafka.consumer;

import static com.yas.commonlibrary.kafka.cdc.message.Operation.CREATE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.DELETE;
import static com.yas.commonlibrary.kafka.cdc.message.Operation.UPDATE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.kafka.cdc.message.Product;
import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import com.yas.recommendation.vector.product.service.ProductVectorSyncService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductSyncServiceTest {

    @Mock
    private ProductVectorSyncService productVectorSyncService;

    @Mock
    private ProductMsgKey key;

    @Mock
    private ProductCdcMessage productCdcMessage;

    @Mock
    private Product product;

    private ProductSyncService productSyncService;

    @BeforeEach
    void setUp() {
        productSyncService = new ProductSyncService(productVectorSyncService);
    }

    @Test
    void sync_WhenHardDelete_CallsDeleteProductVector() {
        when(key.getId()).thenReturn(1L);

        productSyncService.sync(key, null);

        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_WhenDeleteOperation_CallsDeleteProductVector() {
        when(key.getId()).thenReturn(1L);
        when(productCdcMessage.getOp()).thenReturn(DELETE);

        productSyncService.sync(key, productCdcMessage);

        verify(productVectorSyncService).deleteProductVector(1L);
    }

    @Test
    void sync_WhenCreateOperation_CallsCreateProductVector() {
        when(productCdcMessage.getOp()).thenReturn(CREATE);
        when(productCdcMessage.getAfter()).thenReturn(product);

        productSyncService.sync(key, productCdcMessage);

        verify(productVectorSyncService).createProductVector(product);
    }

    @Test
    void sync_WhenUpdateOperation_CallsUpdateProductVector() {
        when(productCdcMessage.getOp()).thenReturn(UPDATE);
        when(productCdcMessage.getAfter()).thenReturn(product);

        productSyncService.sync(key, productCdcMessage);

        verify(productVectorSyncService).updateProductVector(product);
    }
}
