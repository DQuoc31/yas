package com.yas.recommendation.vector.common.document;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.yas.recommendation.vector.product.document.ProductDocument;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;

class DocumentLogicTest {

    @Test
    void productDocument_ToDocument_Success() {
        ProductDocument productDocument = new ProductDocument();
        productDocument.setContent("Test Content");
        productDocument.setMetadata(Map.of("key", "value"));
        
        DefaultIdGenerator idGenerator = new DefaultIdGenerator("PREFIX", 1L);
        Document document = productDocument.toDocument(idGenerator);
        
        assertThat(document.getContent()).isEqualTo("Test Content");
        assertThat(document.getMetadata()).containsEntry("key", "value");
        assertThat(document.getId()).isNotNull();
    }

    @Test
    void productDocument_ToDocument_MissingContent_ThrowsException() {
        ProductDocument productDocument = new ProductDocument();
        productDocument.setMetadata(Map.of("key", "value"));
        
        DefaultIdGenerator idGenerator = new DefaultIdGenerator("PREFIX", 1L);
        assertThrows(IllegalArgumentException.class, () -> productDocument.toDocument(idGenerator));
    }

    @Test
    void productDocument_ToDocument_MissingMetadata_ThrowsException() {
        ProductDocument productDocument = new ProductDocument();
        productDocument.setContent("Test Content");
        
        DefaultIdGenerator idGenerator = new DefaultIdGenerator("PREFIX", 1L);
        assertThrows(IllegalArgumentException.class, () -> productDocument.toDocument(idGenerator));
    }

    @Test
    void defaultIdGenerator_GenerateId_IsDeterministic() {
        DefaultIdGenerator gen1 = new DefaultIdGenerator("PROD", 123L);
        DefaultIdGenerator gen2 = new DefaultIdGenerator("PROD", 123L);
        
        assertThat(gen1.generateId()).isEqualTo(gen2.generateId());
    }

    @Test
    void defaultIdGenerator_GenerateId_DifferentId_DifferentResult() {
        DefaultIdGenerator gen1 = new DefaultIdGenerator("PROD", 123L);
        DefaultIdGenerator gen2 = new DefaultIdGenerator("PROD", 124L);
        
        assertThat(gen1.generateId()).isNotEqualTo(gen2.generateId());
    }
}
