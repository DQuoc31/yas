package com.yas.payment.controller;

import com.yas.payment.service.PaymentProviderService;
import com.yas.payment.viewmodel.paymentprovider.CreatePaymentVm;
import com.yas.payment.viewmodel.paymentprovider.PaymentProviderVm;
import com.yas.payment.viewmodel.paymentprovider.UpdatePaymentVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PaymentProviderControllerTest {

    private PaymentProviderService paymentProviderService;
    private PaymentProviderController paymentProviderController;

    @BeforeEach
    void setUp() {
        paymentProviderService = mock(PaymentProviderService.class);
        paymentProviderController = new PaymentProviderController(paymentProviderService);
    }

    @Test
    void create_ShouldReturnCreatedStatus() {
        CreatePaymentVm createVm = CreatePaymentVm.builder().name("Test Provider").build();
        PaymentProviderVm responseVm = new PaymentProviderVm("id", "Test Provider", "url", 1, 1L, "icon");

        when(paymentProviderService.create(any(CreatePaymentVm.class))).thenReturn(responseVm);

        ResponseEntity<PaymentProviderVm> response = paymentProviderController.create(createVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isEqualTo(responseVm);
    }

    @Test
    void update_ShouldReturnOkStatus() {
        UpdatePaymentVm updateVm = UpdatePaymentVm.builder().name("Updated Provider").build();
        PaymentProviderVm responseVm = new PaymentProviderVm("id", "Updated Provider", "url", 2, 1L, "icon");

        when(paymentProviderService.update(any(UpdatePaymentVm.class))).thenReturn(responseVm);

        ResponseEntity<PaymentProviderVm> response = paymentProviderController.update(updateVm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(responseVm);
    }

    @Test
    void getAll_ShouldReturnListOfProviders() {
        Pageable pageable = Pageable.ofSize(10);
        List<PaymentProviderVm> providers = List.of(new PaymentProviderVm("id", "Provider", "url", 1, 1L, "icon"));

        when(paymentProviderService.getEnabledPaymentProviders(pageable)).thenReturn(providers);

        ResponseEntity<List<PaymentProviderVm>> response = paymentProviderController.getAll(pageable);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(providers);
    }
}
