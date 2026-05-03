package com.yas.recommendation.vector.common.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.document.Document;
import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
class DocumentRowMapperTest {

    @Mock
    private ResultSet resultSet;

    private DocumentRowMapper documentRowMapper;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        documentRowMapper = new DocumentRowMapper(objectMapper);
    }

    @Test
    void mapRow_WhenCalled_ReturnsDocument() throws SQLException {
        when(resultSet.getString("id")).thenReturn("DOC-1");
        when(resultSet.getString("content")).thenReturn("Sample Content");
        when(resultSet.getObject("metadata")).thenReturn("{\"key\":\"value\"}");

        Document result = documentRowMapper.mapRow(resultSet, 1);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("DOC-1");
        assertThat(result.getContent()).isEqualTo("Sample Content");
        assertThat(result.getMetadata()).containsEntry("key", "value");
    }
}
