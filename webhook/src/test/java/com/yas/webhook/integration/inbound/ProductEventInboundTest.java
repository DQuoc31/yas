package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import tools.jackson.databind.JsonNode;
import com.yas.webhook.service.ProductEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProductEventInboundTest {

    private ProductEventService productEventService;
    private ProductEventInbound productEventInbound;

    @BeforeEach
    void setUp() {
        productEventService = mock(ProductEventService.class);
        productEventInbound = new ProductEventInbound(productEventService);
    }

    @Test
    void onProductEvent_ShouldCallService() {
        JsonNode payload = mock(JsonNode.class);
        productEventInbound.onProductEvent(payload);
        verify(productEventService).onProductEvent(payload);
    }
}
