package com.yas.recommendation.kafka.consumer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.yas.commonlibrary.kafka.cdc.message.ProductCdcMessage;
import com.yas.commonlibrary.kafka.cdc.message.ProductMsgKey;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.MessageHeaders;
import java.util.Collections;

@ExtendWith(MockitoExtension.class)
class ProductSyncDataConsumerTest {

    @Mock
    private ProductSyncService productSyncService;

    private ProductSyncDataConsumer productSyncDataConsumer;

    @BeforeEach
    void setUp() {
        productSyncDataConsumer = new ProductSyncDataConsumer(productSyncService);
    }

    @Test
    void processMessage_WhenCalled_CallsProductSyncService() {
        ProductMsgKey key = new ProductMsgKey(1L);
        ProductCdcMessage message = new ProductCdcMessage();
        MessageHeaders headers = new MessageHeaders(Collections.emptyMap());

        productSyncDataConsumer.processMessage(key, message, headers);

        verify(productSyncService).sync(eq(key), eq(message));
    }
}
