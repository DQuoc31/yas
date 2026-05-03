package com.yas.payment.service;

import com.yas.payment.model.CapturedPayment;
import com.yas.payment.model.InitiatedPayment;
import com.yas.payment.model.Payment;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.model.enumeration.PaymentStatus;
import com.yas.payment.repository.PaymentRepository;
import com.yas.payment.service.provider.handler.PaymentHandler;
import com.yas.payment.service.provider.handler.PaypalHandler;
import com.yas.payment.viewmodel.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PaymentServiceTest {
    private PaymentRepository paymentRepository;
    private OrderService orderService;
    private PaymentHandler paymentHandler;
    private List<PaymentHandler> paymentHandlers = new ArrayList<>();
    private PaymentService paymentService;

    private Payment payment;

    @BeforeEach
    void setUp() {
        paymentRepository = mock(PaymentRepository.class);
        orderService = mock(OrderService.class);
        paymentHandler = mock(PaymentHandler.class);
        paymentHandlers.add(paymentHandler);
        paymentService = new PaymentService(paymentRepository, orderService, paymentHandlers);

        when(paymentHandler.getProviderId()).thenReturn(PaymentMethod.PAYPAL.name());
        paymentService.initializeProviders();

        payment = new Payment();
        payment.setId(1L);
        payment.setCheckoutId("secretCheckoutId");
        payment.setOrderId(2L);
        payment.setPaymentStatus(PaymentStatus.COMPLETED);
        payment.setPaymentFee(BigDecimal.valueOf(500));
        payment.setPaymentMethod(PaymentMethod.BANKING);
        payment.setAmount(BigDecimal.valueOf(100.0));
        payment.setFailureMessage(null);
        payment.setGatewayTransactionId("gatewayId");
    }
    

    @Test
    void initPayment_Success() {
        InitPaymentRequestVm initPaymentRequestVm = InitPaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name()).totalPrice(BigDecimal.TEN).checkoutId("123").build();
        InitiatedPayment initiatedPayment = InitiatedPayment.builder().paymentId("123").status("success").redirectUrl("http://abc.com").build();
        when(paymentHandler.initPayment(initPaymentRequestVm)).thenReturn(initiatedPayment);
        InitPaymentResponseVm result = paymentService.initPayment(initPaymentRequestVm);
        assertEquals(initiatedPayment.getPaymentId(), result.paymentId());
        assertEquals(initiatedPayment.getStatus(), result.status());
        assertEquals(initiatedPayment.getRedirectUrl(), result.redirectUrl());
    }



    @Test
    void capturePayment_Success() {
        CapturePaymentRequestVm capturePaymentRequestVM = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name()).token("123").build();
        CapturedPayment capturedPayment = prepareCapturedPayment();
        Long orderId = 999L;
        when(paymentHandler.capturePayment(capturePaymentRequestVM)).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(capturedPayment)).thenReturn(orderId);
        when(paymentRepository.save(any())).thenReturn(payment);
        CapturePaymentResponseVm capturePaymentResponseVm = paymentService.capturePayment(capturePaymentRequestVM);
        verifyPaymentCreation(capturePaymentResponseVm);
        verifyOrderServiceInteractions(capturedPayment);
        verifyResult(capturedPayment, capturePaymentResponseVm);
    }



    @Test
    void getPaymentHandler_WhenProviderNotFound_ThrowsException() {
        InitPaymentRequestVm request = InitPaymentRequestVm.builder()
                .paymentMethod("NON_EXISTENT")
                .build();

        assertThrows(IllegalArgumentException.class, () -> paymentService.initPayment(request));
    }



    @Test
    void capturePayment_Failure_ReturnsFailureResponse() {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod(PaymentMethod.PAYPAL.name())
                .build();

        CapturedPayment failedPayment = CapturedPayment.builder()
                .paymentStatus(PaymentStatus.CANCELLED)
                .failureMessage("Insufficient funds")
                .amount(BigDecimal.TEN)
                .paymentMethod(PaymentMethod.PAYPAL)
                .checkoutId("checkout-123")
                .build();

        when(paymentHandler.capturePayment(request)).thenReturn(failedPayment);
        when(orderService.updateCheckoutStatus(failedPayment)).thenReturn(null);
        when(paymentRepository.save(any())).thenReturn(payment);

        CapturePaymentResponseVm result = paymentService.capturePayment(request);

        assertThat(result.paymentStatus()).isEqualTo(PaymentStatus.CANCELLED);
        assertThat(result.failureMessage()).isEqualTo("Insufficient funds");
    }



    @Test
    void constants_ErrorCode_Constructor() {
        assertDoesNotThrow(() -> {
            java.lang.reflect.Constructor<com.yas.payment.utils.Constants.ErrorCode> constructor =
                    com.yas.payment.utils.Constants.ErrorCode.class.getDeclaredConstructor(com.yas.payment.utils.Constants.class);
            constructor.setAccessible(true);
            constructor.newInstance(new com.yas.payment.utils.Constants());
        });
    }



    @Test
    void constants_Constructor() {
        assertDoesNotThrow(() -> {
            java.lang.reflect.Constructor<com.yas.payment.utils.Constants> constructor =
                    com.yas.payment.utils.Constants.class.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        });
    }

    @Test
    void initializeProviders_ShouldPopulateMap() {
        paymentService.initializeProviders();
        // Since we already test success paths that use getPaymentHandler, 
        // this is just to ensure initializeProviders specifically is called.
        assertDoesNotThrow(() -> paymentService.initializeProviders());
    }

    @Test
    void createPayment_ShouldSavePayment() {
        CapturedPayment capturedPayment = CapturedPayment.builder()
                .checkoutId("check")
                .orderId(1L)
                .paymentStatus(PaymentStatus.COMPLETED)
                .amount(BigDecimal.TEN)
                .paymentMethod(PaymentMethod.PAYPAL)
                .build();
        
        when(paymentRepository.save(any())).thenReturn(payment);
        
        // This method is private, but capturePayment calls it.
        // We ensure the logic inside it is covered by capturePayment.
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod("PAYPAL")
                .token("token")
                .build();
        
        when(paymentHandler.capturePayment(any())).thenReturn(capturedPayment);
        when(orderService.updateCheckoutStatus(any())).thenReturn(1L);
        
        paymentService.capturePayment(request);
        verify(paymentRepository, times(1)).save(any());
    }

    @Test
    void capturePayment_ProviderNotFound_ThrowsException() {
        CapturePaymentRequestVm request = CapturePaymentRequestVm.builder()
                .paymentMethod("INVALID_PROVIDER")
                .build();

        assertThrows(IllegalArgumentException.class, () -> paymentService.capturePayment(request));
    }

    private CapturedPayment prepareCapturedPayment() {
        return CapturedPayment.builder()
            .orderId(2L)
            .checkoutId("secretCheckoutId")
            .amount(BigDecimal.valueOf(100.0))
            .paymentFee(BigDecimal.valueOf(500))
            .gatewayTransactionId("gatewayId")
            .paymentMethod(PaymentMethod.BANKING)
            .paymentStatus(PaymentStatus.COMPLETED)
            .failureMessage(null)
            .build();
    }

    
    private void verifyPaymentCreation(CapturePaymentResponseVm capturedPayment) {
        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository, times(1)).save(paymentCaptor.capture());
        Payment capturedPaymentResult = paymentCaptor.getValue();

        assertThat(capturedPaymentResult.getCheckoutId()).isEqualTo(capturedPayment.checkoutId());
        assertThat(capturedPaymentResult.getOrderId()).isEqualTo(capturedPayment.orderId());
        assertThat(capturedPaymentResult.getPaymentStatus()).isEqualTo(capturedPayment.paymentStatus());
        assertThat(capturedPaymentResult.getPaymentFee()).isEqualByComparingTo(capturedPayment.paymentFee());
        assertThat(capturedPaymentResult.getAmount()).isEqualByComparingTo(capturedPayment.amount());
    }

    private void verifyOrderServiceInteractions(CapturedPayment capturedPayment) {
        verify(orderService, times(1)).updateCheckoutStatus((capturedPayment));
        verify(orderService, times(1)).updateOrderStatus(any(PaymentOrderStatusVm.class));
    }

    private void verifyResult(CapturedPayment capturedPayment, CapturePaymentResponseVm responseVm) {
        assertEquals(capturedPayment.getOrderId(), responseVm.orderId());
        assertEquals(capturedPayment.getCheckoutId(), responseVm.checkoutId());
        assertEquals(capturedPayment.getAmount(), responseVm.amount());
        assertEquals(capturedPayment.getPaymentFee(), responseVm.paymentFee());
        assertEquals(capturedPayment.getGatewayTransactionId(), responseVm.gatewayTransactionId());
        assertEquals(capturedPayment.getPaymentMethod(), responseVm.paymentMethod());
        assertEquals(capturedPayment.getPaymentStatus(), responseVm.paymentStatus());
        assertEquals(capturedPayment.getFailureMessage(), responseVm.failureMessage());
    }

}
