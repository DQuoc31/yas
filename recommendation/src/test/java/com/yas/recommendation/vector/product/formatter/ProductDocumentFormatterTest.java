package com.yas.recommendation.vector.product.formatter;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.recommendation.viewmodel.CategoryVm;
import com.yas.recommendation.viewmodel.ProductAttributeValueVm;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

class ProductDocumentFormatterTest {

    private ProductDocumentFormatter formatter;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        formatter = new ProductDocumentFormatter();
        objectMapper = new ObjectMapper();
    }

    @Test
    void format_WithBasicData_Success() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Test Product");
        entityMap.put("price", 100.0);
        
        String template = "Name: {name}, Price: {price}";
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertThat(result).isEqualTo("Name: Test Product, Price: 100.0");
    }

    @Test
    void format_WithAttributesAndCategories_Success() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("name", "Test Product");
        
        ProductAttributeValueVm attr = new ProductAttributeValueVm(1L, "Color", "Red");
        entityMap.put("attributeValues", List.of(attr));
        
        CategoryVm cat = new CategoryVm(1L, "Electronics", "Desc", "electronics", "Key", "Meta", (short)1, true);
        entityMap.put("categories", List.of(cat));
        
        String template = "Name: {name}, Attrs: {attributeValues}, Cats: {categories}";
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertThat(result).contains("Name: Test Product");
        assertThat(result).contains("Attrs: [Color: Red]");
        assertThat(result).contains("Cats: [Electronics]");
    }

    @Test
    void format_WithHtmlTags_RemovesTags() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("description", "<p>Hello <b>World</b></p>");
        
        String template = "Desc: {description}";
        String result = formatter.format(entityMap, template, objectMapper);
        
        assertThat(result).isEqualTo("Desc: Hello World");
    }
}
