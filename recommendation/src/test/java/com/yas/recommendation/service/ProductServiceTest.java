package com.yas.recommendation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.RecommendationConfig;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.net.URI;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.RequestHeadersSpec requestHeadersSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RecommendationConfig config;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        when(config.getApiUrl()).thenReturn("http://localhost:8080");
    }

    @Test
    void getProductDetail_WhenCalled_ReturnsProductDetailVm() {
        long productId = 1L;
        ProductDetailVm mockProductDetailVm = new ProductDetailVm(
            1L, "Test Product", "Short", "Desc", "Spec", "SKU", "GTIN", "Slug",
            true, true, false, true, true, 100.0, 1L, java.util.List.of(),
            "Title", "Keyword", "MetaDesc", 1L, "Brand", java.util.List.of(),
            java.util.List.of(), null, java.util.List.of()
        );

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);
        
        // Use generic parameter for toEntity
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
            .thenReturn(org.springframework.http.ResponseEntity.ok(mockProductDetailVm));

        ProductDetailVm result = productService.getProductDetail(productId);

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(mockProductDetailVm);
    }
}
