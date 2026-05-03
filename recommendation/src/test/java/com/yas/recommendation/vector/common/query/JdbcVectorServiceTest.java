package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.recommendation.configuration.EmbeddingSearchConfiguration;
import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class JdbcVectorServiceTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private EmbeddingSearchConfiguration embeddingSearchConfiguration;

    private JdbcVectorService jdbcVectorService;

    @BeforeEach
    void setUp() {
        jdbcVectorService = new JdbcVectorService(jdbcTemplate, objectMapper, embeddingSearchConfiguration);
    }

    @Test
    void similarityProduct_WhenCalled_ExecutesQuery() {
        Long productId = 1L;
        List<Document> expectedDocuments = List.of(new Document("Content"));

        when(jdbcTemplate.query(anyString(), any(PreparedStatementSetter.class), any(DocumentRowMapper.class)))
                .thenReturn(expectedDocuments);

        List<Document> result = jdbcVectorService.similarityProduct(productId, ProductDocument.class);

        assertThat(result).isEqualTo(expectedDocuments);
        verify(jdbcTemplate).query(anyString(), any(PreparedStatementSetter.class), any(DocumentRowMapper.class));
    }
}
