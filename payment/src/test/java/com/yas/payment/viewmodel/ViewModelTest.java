package com.yas.payment.viewmodel;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.assertj.core.api.Assertions.assertThat;

class ViewModelTest {

    @Test
    void testInitPaymentRequestVm() {
        InitPaymentRequestVm vm = InitPaymentRequestVm.builder()
                .paymentMethod("paypal")
                .totalPrice(BigDecimal.TEN)
                .checkoutId("check123")
                .build();
        
        assertThat(vm.paymentMethod()).isEqualTo("paypal");
        assertThat(vm.totalPrice()).isEqualTo(BigDecimal.TEN);
        assertThat(vm.checkoutId()).isEqualTo("check123");
    }

    @Test
    void testInitPaymentResponseVm() {
        InitPaymentResponseVm vm = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("pay123")
                .redirectUrl("http://redirect")
                .build();
        
        assertThat(vm.status()).isEqualTo("PENDING");
        assertThat(vm.paymentId()).isEqualTo("pay123");
        assertThat(vm.redirectUrl()).isEqualTo("http://redirect");
    }

    @Test
    void testCapturePaymentRequestVm() {
        CapturePaymentRequestVm vm = CapturePaymentRequestVm.builder()
                .paymentMethod("paypal")
                .checkoutId("check123")
                .build();
        
        assertThat(vm.paymentMethod()).isEqualTo("paypal");
        assertThat(vm.checkoutId()).isEqualTo("check123");
    }

    @Test
    void testCapturePaymentResponseVm() {
        CapturePaymentResponseVm vm = CapturePaymentResponseVm.builder()
                .orderId(1L)
                .checkoutId("check123")
                .amount(BigDecimal.TEN)
                .paymentFee(BigDecimal.ONE)
                .gatewayTransactionId("trans123")
                .paymentMethod("paypal")
                .paymentStatus("COMPLETED")
                .failureMessage("none")
                .build();
        
        assertThat(vm.orderId()).isEqualTo(1L);
        assertThat(vm.checkoutId()).isEqualTo("check123");
        assertThat(vm.amount()).isEqualTo(BigDecimal.TEN);
        assertThat(vm.paymentFee()).isEqualTo(BigDecimal.ONE);
        assertThat(vm.gatewayTransactionId()).isEqualTo("trans123");
        assertThat(vm.paymentMethod()).isEqualTo("paypal");
        assertThat(vm.paymentStatus()).isEqualTo("COMPLETED");
        assertThat(vm.failureMessage()).isEqualTo("none");
    }

    @Test
    void testPaymentOrderStatusVm() {
        PaymentOrderStatusVm vm = PaymentOrderStatusVm.builder()
                .paymentId(1L)
                .orderId(2L)
                .paymentStatus("COMPLETED")
                .build();
        
        assertThat(vm.paymentId()).isEqualTo(1L);
        assertThat(vm.orderId()).isEqualTo(2L);
        assertThat(vm.paymentStatus()).isEqualTo("COMPLETED");
    }

    @Test
    void testErrorVm() {
        ErrorVm vm = new ErrorVm("404", "Not Found", "Detail");
        assertThat(vm.statusCode()).isEqualTo("404");
        assertThat(vm.title()).isEqualTo("Not Found");
        assertThat(vm.detail()).isEqualTo("Detail");
    }

    @Test
    void testCheckoutStatusVm() {
        CheckoutStatusVm vm = new CheckoutStatusVm(1L, "COMPLETED");
        assertThat(vm.checkoutId()).isEqualTo(1L);
        assertThat(vm.checkoutStatus()).isEqualTo("COMPLETED");
    }
}
