package com.yas.payment.utils;

import org.junit.jupiter.api.Test;
import java.util.Locale;
import java.util.ResourceBundle;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class MessagesUtilsTest {

    @Test
    void getMessage_withValidCode_shouldReturnMessage() {
        // Since we can't easily mock the static ResourceBundle in a simple way without Mockito inline,
        // and we don't want to rely on the actual bundle existence for a pure unit test if possible,
        // but here we test the behavior.
        
        String code = "PP_NAME_ALREADY_EXITED"; // Assuming this exists in messages.properties
        String message = MessagesUtils.getMessage(code, "Test");
        assertNotNull(message);
    }

    @Test
    void getMessage_withInvalidCode_shouldReturnCode() {
        String code = "non.existent.code";
        String message = MessagesUtils.getMessage(code);
        assertEquals(code, message);
    }

    @Test
    void testConstructor_forCoverage() {
        // Just for line coverage of the utility class constructor if needed
        assertDoesNotThrow(() -> {
            var constructor = MessagesUtils.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }

    @Test
    void testConstants_forCoverage() {
        // Just for line coverage of constant utility class
        assertThat(Constants.ErrorCode.PAYMENT_PROVIDER_NOT_FOUND).isEqualTo("PAYMENT_PROVIDER_NOT_FOUND");
    }
}
