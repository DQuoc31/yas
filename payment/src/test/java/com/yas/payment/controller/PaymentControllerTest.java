package com.yas.payment.controller;

import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.service.PaymentService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.CapturePaymentResponseVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentResponseVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentControllerTest {

    private PaymentService paymentService;
    private PaymentController paymentController;

    @BeforeEach
    void setUp() {
        paymentService = mock(PaymentService.class);
        paymentController = new PaymentController(paymentService);
    }

    @Test
    void initPayment_ShouldReturnResponse() {
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(BigDecimal.TEN)
                .build();
        InitPaymentResponseVm response = InitPaymentResponseVm.builder()
                .status("PENDING")
                .paymentId("pay-123")
                .build();

        when(paymentService.initPayment(request)).thenReturn(response);

        InitPaymentResponseVm result = paymentController.initPayment(request);

        assertThat(result).isNotNull();
        assertThat(result.paymentId()).isEqualTo("pay-123");
    }

    @Test
    void capturePayment_ShouldReturnResponse() {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("token-123")
                .build();
        CapturePaymentResponseVm response = CapturePaymentResponseVm.builder()
                .paymentStatus(PaymentStatus.COMPLETED)
                .build();

        when(paymentService.capturePayment(request)).thenReturn(response);

        CapturePaymentResponseVm result = paymentController.capturePayment(request);

        assertThat(result).isNotNull();
        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }

    @Test
    void cancelPayment_ShouldReturnOk() {
        ResponseEntity<String> result = paymentController.cancelPayment();
        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        assertThat(result.getBody()).isEqualTo("Payment cancelled");
    }
}
