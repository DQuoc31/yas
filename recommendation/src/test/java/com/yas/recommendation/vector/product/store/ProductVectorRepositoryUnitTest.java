package com.yas.recommendation.vector.product.store;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.recommendation.vector.product.document.ProductDocument;
import org.springframework.ai.vectorstore.SearchRequest;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.service.ProductService;
import com.yas.recommendation.viewmodel.ProductDetailVm;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class ProductVectorRepositoryUnitTest {

    @Mock
    private VectorStore vectorStore;

    @Mock
    private ProductService productService;

    @Mock
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    private ProductVectorRepository productVectorRepository;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        productVectorRepository = new ProductVectorRepository(vectorStore, productService);
        ReflectionTestUtils.setField(productVectorRepository, "objectMapper", objectMapper);
        ReflectionTestUtils.setField(productVectorRepository, "embeddingSearchConfiguration", embeddingSearchConfiguration);
    }

    @Test
    void add_WhenCalled_CallsVectorStoreAdd() {
        Long productId = 1L;
        ProductDetailVm productDetailVm = new ProductDetailVm(
            productId, "Product", "Short", "Desc", "Spec", "SKU", "GTIN", "Slug",
            true, true, false, true, true, 100.0, 1L, Collections.emptyList(),
            "Title", "Key", "Meta", 1L, "Brand", Collections.emptyList(),
            Collections.emptyList(), null, Collections.emptyList()
        );

        when(productService.getProductDetail(productId)).thenReturn(productDetailVm);

        productVectorRepository.add(productId);

        ArgumentCaptor<List<Document>> captor = ArgumentCaptor.forClass(List.class);
        verify(vectorStore).add(captor.capture());
        assertThat(captor.getValue()).hasSize(1);
        assertThat(captor.getValue().get(0).getContent()).contains("Product");
    }

    @Test
    void delete_WhenCalled_CallsVectorStoreDelete() {
        Long productId = 1L;
        productVectorRepository.delete(productId);
        verify(vectorStore).delete(anyList());
    }

    @Test
    void update_WhenCalled_CallsDeleteAndAdd() {
        Long productId = 1L;
        ProductDetailVm productDetailVm = new ProductDetailVm(
            productId, "Product", "Short", "Desc", "Spec", "SKU", "GTIN", "Slug",
            true, true, false, true, true, 100.0, 1L, Collections.emptyList(),
            "Title", "Key", "Meta", 1L, "Brand", Collections.emptyList(),
            Collections.emptyList(), null, Collections.emptyList()
        );
        when(productService.getProductDetail(productId)).thenReturn(productDetailVm);

        productVectorRepository.update(productId);

        verify(vectorStore).delete(anyList());
        verify(vectorStore).add(anyList());
    }

    @Test
    void search_WhenCalled_ReturnsMappedDocuments() {
        Long productId = 1L;
        ProductDetailVm productDetailVm = new ProductDetailVm(
            productId, "Product", "Short", "Desc", "Spec", "SKU", "GTIN", "Slug",
            true, true, false, true, true, 100.0, 1L, Collections.emptyList(),
            "Title", "Key", "Meta", 1L, "Brand", Collections.emptyList(),
            Collections.emptyList(), null, Collections.emptyList()
        );
        when(productService.getProductDetail(productId)).thenReturn(productDetailVm);
        when(embeddingSearchConfiguration.topK()).thenReturn(5);
        when(embeddingSearchConfiguration.similarityThreshold()).thenReturn(0.7);

        Document doc = new Document("Result", Collections.emptyMap());
        when(vectorStore.similaritySearch(any(SearchRequest.class)))
            .thenReturn(List.of(doc));

        List<ProductDocument> result = productVectorRepository.search(productId);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Result");
    }

    @Test
    void getEntity_WhenCalled_CallsProductService() {
        Long productId = 1L;
        productVectorRepository.getEntity(productId);
        verify(productService).getProductDetail(productId);
    }

    @Test
    void getIdGenerator_WhenCalled_ReturnsGenerator() {
        Long productId = 1L;
        var generator = productVectorRepository.getIdGenerator(productId);
        assertThat(generator).isNotNull();
    }
}
