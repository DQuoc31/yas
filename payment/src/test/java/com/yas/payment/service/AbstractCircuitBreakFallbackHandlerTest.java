package com.yas.payment.service;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AbstractCircuitBreakFallbackHandlerTest {

    private final AbstractCircuitBreakFallbackHandler handler = new AbstractCircuitBreakFallbackHandler() {};

    @Test
    void handleBodilessFallback_ShouldThrowException() {
        RuntimeException ex = new RuntimeException("test");
        assertThrows(RuntimeException.class, () -> handler.handleBodilessFallback(ex));
    }
    

    @Test
    void handleTypedFallback_ShouldThrowException() {
        RuntimeException ex = new RuntimeException("test");
        assertThrows(RuntimeException.class, () -> handler.handleTypedFallback(ex));
    }
}
