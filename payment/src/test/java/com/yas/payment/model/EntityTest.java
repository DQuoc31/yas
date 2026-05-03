package com.yas.payment.model;

import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
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

    @Test
    void testInitiatedPayment_GetterSetter() {
        InitiatedPayment payment = InitiatedPayment.builder()
                .status("status")
                .paymentId("pay")
                .redirectUrl("url")
                .build();
        payment.setStatus("new");
        assertThat(payment.getStatus()).isEqualTo("new");
        assertThat(payment.getPaymentId()).isEqualTo("pay");
        assertThat(payment.getRedirectUrl()).isEqualTo("url");
    }

    @Test
    void testCapturedPayment_GetterSetter() {
        CapturedPayment payment = CapturedPayment.builder()
                .orderId(1L)
                .checkoutId("check")
                .amount(java.math.BigDecimal.TEN)
                .paymentFee(java.math.BigDecimal.ONE)
                .gatewayTransactionId("trans")
                .paymentMethod(PaymentMethod.PAYPAL)
                .paymentStatus(PaymentStatus.COMPLETED)
                .failureMessage("none")
                .build();
        payment.setOrderId(2L);
        assertThat(payment.getOrderId()).isEqualTo(2L);
        assertThat(payment.getCheckoutId()).isEqualTo("check");
        assertThat(payment.getAmount()).isEqualTo(java.math.BigDecimal.TEN);
        assertThat(payment.getPaymentFee()).isEqualTo(java.math.BigDecimal.ONE);
        assertThat(payment.getGatewayTransactionId()).isEqualTo("trans");
        assertThat(payment.getPaymentMethod()).isEqualTo(PaymentMethod.PAYPAL);
        assertThat(payment.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
        assertThat(payment.getFailureMessage()).isEqualTo("none");
    }

    @Test
    void testPaymentProvider_GetterSetter() {
        PaymentProvider provider = new PaymentProvider();
        provider.setAdditionalSettings("settings");
        provider.setConfigureUrl("url");
        provider.setLandingViewComponentName("comp");
        
        assertThat(provider.getAdditionalSettings()).isEqualTo("settings");
        assertThat(provider.getConfigureUrl()).isEqualTo("url");
        assertThat(provider.getLandingViewComponentName()).isEqualTo("comp");
    }
}
