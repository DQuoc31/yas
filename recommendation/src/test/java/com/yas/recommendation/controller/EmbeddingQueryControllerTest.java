package com.yas.recommendation.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.recommendation.vector.common.query.VectorQuery;
import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class EmbeddingQueryControllerTest {

    @Mock
    private VectorQuery<ProductDocument, RelatedProductVm> relatedProductSearch;

    private EmbeddingQueryController embeddingQueryController;

    @BeforeEach
    void setUp() {
        embeddingQueryController = new EmbeddingQueryController(relatedProductSearch);
    }

    @Test
    void searchProduct_WhenCalled_ReturnsRelatedProducts() {
        Long productId = 1L;
        RelatedProductVm vm1 = new RelatedProductVm();
        vm1.setProductId(1L);
        vm1.setName("Product 1");

        RelatedProductVm vm2 = new RelatedProductVm();
        vm2.setProductId(2L);
        vm2.setName("Product 2");

        List<RelatedProductVm> expectedResult = List.of(vm1, vm2);

        when(relatedProductSearch.similaritySearch(productId)).thenReturn(expectedResult);

        List<RelatedProductVm> result = embeddingQueryController.searchProduct(productId);

        assertThat(result).hasSize(2).isEqualTo(expectedResult);
        verify(relatedProductSearch).similaritySearch(productId);
    }
}