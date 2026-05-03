package com.yas.payment.service;

import static com.yas.payment.util.SecurityContextUtils.setUpSecurityContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.payment.config.ServiceUrlConfig;
import com.yas.payment.model.PaymentProvider;
import com.yas.payment.model.enumeration.PaymentMethod;
import com.yas.payment.viewmodel.paymentprovider.MediaVm;

import java.net.URI;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    public static final String URL_COD = "http://cod";
    public static final String URL_PAYPAL = "http://paypal";

    @InjectMocks
    private MediaService mediaService;
    @Mock
    private RestClient restClient;
    @Mock
    private ServiceUrlConfig serviceUrlConfig;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Test
    public void getMedia_whenProvideValidProviders_shouldProcessSuccess() {
        // Given
        final String MEDIA = "http://api.yas.local/medias";
        when(serviceUrlConfig.media()).thenReturn(MEDIA);
        mockRestClientGetMethod(restClient);
        long codMediaId = -1L;
        long paypalMediaId = -2L;
        when(responseSpec.body(new ParameterizedTypeReference<List<MediaVm>>() {}))
                .thenReturn(List.of(
                        MediaVm.builder().id(codMediaId).url(URL_COD).build(),
                        MediaVm.builder().id(paypalMediaId).url(URL_PAYPAL).build()
                ));

        // When
        var cod = new PaymentProvider();
        cod.setId(PaymentMethod.COD.name());
        cod.setMediaId(codMediaId);

        var paypal = new PaymentProvider();
        paypal.setId(PaymentMethod.PAYPAL.name());
        paypal.setMediaId(paypalMediaId);

        var mediaVmMap = mediaService.getMediaVmMap(List.of(cod, paypal));

        // Then
        assertEquals(2, mediaVmMap.size());
        assertEquals(URL_COD, mediaVmMap.get(codMediaId).getUrl());
        assertEquals(URL_PAYPAL, mediaVmMap.get(paypalMediaId).getUrl());

        verify(restClient, times(1)).get();
    }




    @Test
    public void getMedia_whenProvideEmptyProviders_shouldNotInvokeApi() {
        // When
        var medias = mediaService.getMediaVmMap(List.of());

        // Then
        assertTrue(medias.isEmpty());
        verify(restClient, times(0)).get();
    }


    @Test
    public void getMedia_whenApiError_shouldTriggerFallback() {
        // Given
        final String MEDIA = "http://api.yas.local";
        when(serviceUrlConfig.media()).thenReturn(MEDIA);
        mockRestClientGetMethod(restClient);
        
        when(responseSpec.body(any(ParameterizedTypeReference.class)))
                .thenThrow(new RuntimeException("API error"));

        // When
        var provider = new PaymentProvider();
        provider.setId("test");
        provider.setMediaId(1L);

        // This should trigger the fallback method internally via AOP or direct call for coverage
        // Note: Resilience4j annotations only trigger fallbacks when called as a Spring Bean (proxied)
        // In unit tests, we test the fallback logic directly.
        assertThrows(RuntimeException.class, () -> mediaService.getMediaVmMap(List.of(provider)));
    }


    @Test
    public void testFallback_directly() {
        var provider = new PaymentProvider();
        provider.setMediaId(1L);
        
        // Covering the private/protected fallback method
        java.lang.reflect.Method method;
        try {
            method = MediaService.class.getDeclaredMethod("fallbackGetMediaVmMap", List.class, Throwable.class);
            method.setAccessible(true);
            var result = (java.util.Map<?, ?>) method.invoke(mediaService, List.of(provider), new RuntimeException("Error"));
            assertTrue(result.isEmpty());
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.fail("Reflection failed: " + e.getMessage());
        }
    }
    
    private void mockRestClientGetMethod(RestClient restClient) {
        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = Mockito.mock(RestClient.RequestHeadersUriSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

}