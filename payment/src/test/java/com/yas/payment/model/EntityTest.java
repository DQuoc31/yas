package com.yas.payment.model;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class EntityTest {

    @Test
    void testPaymentEntity() {
        Payment payment = Payment.builder()
                .id(1L)
                .orderId(123L)
                .amount(java.math.BigDecimal.TEN)
                .build();
        
        assertThat(payment.getId()).isEqualTo(1L);
        assertThat(payment.getOrderId()).isEqualTo(123L);
        assertThat(payment.getAmount()).isEqualTo(java.math.BigDecimal.TEN);
    }

    @Test
    void testPaymentProviderEntity() {
        PaymentProvider provider = new PaymentProvider();
        provider.setId("paypal");
        provider.setName("PayPal");
        provider.setEnabled(true);
        
        assertThat(provider.getId()).isEqualTo("paypal");
        assertThat(provider.getName()).isEqualTo("PayPal");
        assertThat(provider.isEnabled()).isTrue();
    }
}
