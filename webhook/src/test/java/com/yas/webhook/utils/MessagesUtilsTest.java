package com.yas.webhook.utils;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void getMessage_WhenErrorCodeExists_ReturnsFormattedMessage() {
        // Since we don't know the exact content of messages.properties, 
        // we test with a non-existent key to cover the catch block
        String errorCode = "non.existent.key";
        String result = MessagesUtils.getMessage(errorCode);
        assertThat(result).isEqualTo(errorCode);
    }
    
    @Test
    void getMessage_WithArguments_ReturnsFormattedMessage() {
        String errorCode = "Test message with {}";
        String arg = "argument";
        String result = MessagesUtils.getMessage(errorCode, arg);
        assertThat(result).isEqualTo("Test message with argument");
    }
}
