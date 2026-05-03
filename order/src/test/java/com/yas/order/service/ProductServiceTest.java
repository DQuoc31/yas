package com.yas.order.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.order.config.ServiceUrlConfig;
import com.yas.order.viewmodel.product.ProductVariationVm;
import com.yas.order.viewmodel.order.OrderVm;
import com.yas.order.viewmodel.order.OrderItemVm;
import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

class ProductServiceTest {

    private RestClient restClient;
    private ServiceUrlConfig serviceUrlConfig;
    private ProductService productService;
    private RestClient.ResponseSpec responseSpec;

    private static final String PRODUCT_URL = "http://api.yas.local/product";

    @BeforeEach
    void setUp() {
        restClient = mock(RestClient.class);
        serviceUrlConfig = mock(ServiceUrlConfig.class);
        productService = new ProductService(restClient, serviceUrlConfig);
        responseSpec = mock(RestClient.ResponseSpec.class);
        
        when(serviceUrlConfig.product()).thenReturn(PRODUCT_URL);
        
        // Setup Security Context
        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("mock-token");
        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    void getProductVariations_Success() {
        Long productId = 1L;
        final URI url = UriComponentsBuilder
                .fromUriString(PRODUCT_URL)
                .path("/backoffice/product-variations/{productId}")
                .buildAndExpand(productId)
                .toUri();

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.RequestHeadersSpec requestHeadersSpec = mock(RestClient.RequestHeadersSpec.class);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.headers(any())).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        @SuppressWarnings("unchecked")
        ResponseEntity<List<ProductVariationVm>> responseEntity = mock(ResponseEntity.class);
        List<ProductVariationVm> variations = List.of(new ProductVariationVm(1L, "V1", "SKU1"));
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class))).thenReturn(responseEntity);
        when(responseEntity.getBody()).thenReturn(variations);

        List<ProductVariationVm> result = productService.getProductVariations(productId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("V1");
    }

    @Test
    void subtractProductStockQuantity_Success() {
        OrderVm orderVm = OrderVm.builder()
                .orderItemVms(Set.of(
                        OrderItemVm.builder()
                                .productId(1L)
                                .quantity(2)
                                .productPrice(BigDecimal.TEN)
                                .discountAmount(BigDecimal.ZERO)
                                .taxAmount(BigDecimal.ONE)
                                .build()
                ))
                .build();

        final URI url = UriComponentsBuilder
                .fromUriString(PRODUCT_URL)
                .path("/backoffice/products/subtract-quantity")
                .buildAndExpand()
                .toUri();

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.RequestBodySpec requestBodySpec = mock(RestClient.RequestBodySpec.class);
        when(restClient.put()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(url)).thenReturn(requestBodySpec);
        when(requestBodySpec.headers(any())).thenReturn(requestBodySpec);
        when(requestBodySpec.body(any(Object.class))).thenReturn(requestBodySpec);
        when(requestBodySpec.retrieve()).thenReturn(responseSpec);

        productService.subtractProductStockQuantity(orderVm);

        Mockito.verify(requestBodySpec).retrieve();
    }
}
