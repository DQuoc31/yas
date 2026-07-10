package com.yas.payment.service.provider.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.CapturePaymentRequestVm;
import com.yas.payment.viewmodel.InitPaymentRequestVm;
import java.math.BigDecimal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PaypalHandlerTest {

    private PaymentProviderService paymentProviderService;
    private PaypalService paypalService;
    private PaypalHandler paypalHandler;

    @BeforeEach
    void setUp() {
        paymentProviderService = mock(PaymentProviderService.class);
        paypalService = mock(PaypalService.class);
        paypalHandler = new PaypalHandler(paymentProviderService, paypalService);
    }

    @Test
    void getProviderId_ShouldReturnPaypal() {
        assertThat(paypalHandler.getProviderId()).isEqualTo(PaymentMethod.PAYPAL.name());
    }

    @Test
    void initPayment_ShouldReturnInitiatedPayment() {
        InitPaymentRequestVm requestVm = InitPaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .totalPrice(BigDecimal.TEN)
                .checkoutId("check-123")
                .build();

        PaypalCreatePaymentResponse response = PaypalCreatePaymentResponse.builder()
                .status("CREATED")
                .paymentId("pay-123")
                .redirectUrl("http://paypal.com")
                .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL")).thenReturn("{}");
        when(paypalService.createPayment(any(PaypalCreatePaymentRequest.class))).thenReturn(response);

        InitiatedPayment result = paypalHandler.initPayment(requestVm);

        assertThat(result.getStatus()).isEqualTo("CREATED");
        assertThat(result.getPaymentId()).isEqualTo("pay-123");
        assertThat(result.getRedirectUrl()).isEqualTo("http://paypal.com");
    }

    @Test
    void capturePayment_ShouldReturnCapturedPayment() {
        CapturePaymentRequestVm requestVm = CapturePaymentRequestVm.builder()
                .token("token-123")
                .paymentMethod("PAYPAL")
                .build();

        PaypalCapturePaymentResponse response = PaypalCapturePaymentResponse.builder()
                .checkoutId("check-123")
                .amount(BigDecimal.TEN)
                .paymentFee(BigDecimal.ONE)
                .gatewayTransactionId("trans-123")
                .paymentMethod("PAYPAL")
                .paymentStatus("COMPLETED")
                .failureMessage(null)
                .build();

        when(paymentProviderService.getAdditionalSettingsByPaymentProviderId("PAYPAL")).thenReturn("{}");
        when(paypalService.capturePayment(any(PaypalCapturePaymentRequest.class))).thenReturn(response);

        CapturedPayment result = paypalHandler.capturePayment(requestVm);

        assertThat(result.getCheckoutId()).isEqualTo("check-123");
        assertThat(result.getAmount()).isEqualTo(BigDecimal.TEN);
        assertThat(result.getPaymentStatus()).isEqualTo(PaymentStatus.COMPLETED);
    }
}
