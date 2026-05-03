package com.yas.payment.paypal.controller;

import com.yas.payment.paypal.service.PaypalService;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCapturePaymentResponse;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentRequest;
import com.yas.payment.paypal.viewmodel.PaypalCreatePaymentResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PaypalController {
    private final PaypalService paypalService;

    @PostMapping("/create-payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<PaypalCreatePaymentResponse> createPayment(
        @Valid @RequestBody PaypalCreatePaymentRequest paypalCreatePaymentRequest
    ) {
        return ResponseEntity.ok(paypalService.createPayment(paypalCreatePaymentRequest));
    }

    @PostMapping("/capture-payment")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok"),
        @ApiResponse(responseCode = "400", description = "Bad request")
    })
    public ResponseEntity<PaypalCapturePaymentResponse> capturePayment(
        @Valid @RequestBody PaypalCapturePaymentRequest paypalCapturePaymentRequest
    ) {
        return ResponseEntity.ok(paypalService.capturePayment(paypalCapturePaymentRequest));
    }
}
