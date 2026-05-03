package com.yas.webhook.integration.inbound;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import tools.jackson.databind.JsonNode;
import com.yas.webhook.service.OrderEventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class OrderEventInboundTest {

    private OrderEventService orderEventService;
    private OrderEventInbound orderEventInbound;

    @BeforeEach
    void setUp() {
        orderEventService = mock(OrderEventService.class);
        orderEventInbound = new OrderEventInbound(orderEventService);
    }

    @Test
    void onOrderEvent_ShouldCallService() {
        JsonNode payload = mock(JsonNode.class);
        orderEventInbound.onOrderEvent(payload);
        verify(orderEventService).onOrderEvent(payload);
    }
}
