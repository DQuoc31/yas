package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.yas.recommendation.vector.product.document.ProductDocument;
import com.yas.recommendation.viewmodel.RelatedProductVm;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.test.util.ReflectionTestUtils;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class VectorQueryUnitTest {

    @Mock
    private JdbcVectorService jdbcVectorService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private TestVectorQuery vectorQuery;

    private static class TestVectorQuery extends VectorQuery<ProductDocument, RelatedProductVm> {
        protected TestVectorQuery() {
            super(ProductDocument.class, RelatedProductVm.class);
        }
    }

    @BeforeEach
    void setUp() {
        vectorQuery = new TestVectorQuery();
        ReflectionTestUtils.setField(vectorQuery, "jdbcVectorService", jdbcVectorService);
        ReflectionTestUtils.setField(vectorQuery, "objectMapper", objectMapper);
    }

    @Test
    void similaritySearch_WhenCalled_ReturnsConvertedResults() {
        Long id = 1L;
        Document doc = new Document("Content", Map.of("id", 2L, "name", "Product 2"));
        when(jdbcVectorService.similarityProduct(id, ProductDocument.class)).thenReturn(List.of(doc));

        List<RelatedProductVm> result = vectorQuery.similaritySearch(id);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getProductId()).isEqualTo(2L);
        assertThat(result.get(0).getName()).isEqualTo("Product 2");
    }

    @Test
    void toResult_WhenMetadataIsNull_FiltersOut() {
        Document doc = org.mockito.Mockito.mock(Document.class);
        when(doc.getMetadata()).thenReturn(null);
        List<RelatedProductVm> result = vectorQuery.toResult(List.of(doc));
        assertThat(result).isEmpty();
    }
}
