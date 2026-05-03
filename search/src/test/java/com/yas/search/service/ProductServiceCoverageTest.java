package com.yas.search.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.yas.search.constant.enums.SortType;
import com.yas.search.model.Product;
import com.yas.search.model.ProductCriteriaDto;
import com.yas.search.viewmodel.ProductListGetVm;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.TotalHitsRelation;
import org.springframework.data.elasticsearch.core.AggregationsContainer;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchAggregation;
import co.elastic.clients.elasticsearch._types.aggregations.Aggregate;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsAggregate;
import co.elastic.clients.elasticsearch._types.aggregations.Buckets;
import co.elastic.clients.elasticsearch._types.aggregations.StringTermsBucket;
import co.elastic.clients.elasticsearch._types.FieldValue;

class ProductServiceCoverageTest {

    private ElasticsearchOperations elasticsearchOperations;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        elasticsearchOperations = mock(ElasticsearchOperations.class);
        productService = new ProductService(elasticsearchOperations);
    }

    @SuppressWarnings("unchecked")
    private SearchHits<Product> mockSearchHits() {
        SearchHits<Product> searchHitsResult = mock(SearchHits.class);
        when(searchHitsResult.getSearchHits()).thenReturn(new ArrayList<>());
        when(searchHitsResult.getTotalHits()).thenReturn(0L);
        when(searchHitsResult.stream()).thenReturn(new ArrayList<SearchHit<Product>>().stream());
        return searchHitsResult;
    }

    @Test
    void findProductAdvance_withFilters_shouldCoverExtractedLogic() {
        // Given
        ProductCriteriaDto criteria = new ProductCriteriaDto(
            "test", 0, 10, "brand1,brand2", "cat1",
            "attr1", 10.0, 100.0, SortType.DEFAULT
        );

        SearchHits<Product> searchHitsResult = mockSearchHits();
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHitsResult);

        // When
        ProductListGetVm result = productService.findProductAdvance(criteria);

        // Then
        assertNotNull(result);
        assertEquals(0, result.products().size());
    }

    @Test
    void findProductAdvance_withBlankFilters_shouldCoverEarlyReturns() {
        // Given
        ProductCriteriaDto criteria = new ProductCriteriaDto(
            "test", 0, 10, "", null,
            null, null, null, SortType.DEFAULT
        );

        SearchHits<Product> searchHitsResult = mockSearchHits();
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHitsResult);

        // When
        ProductListGetVm result = productService.findProductAdvance(criteria);

        // Then
        assertNotNull(result);
    }

    @Test
    void findProductAdvance_withPriceDesc_shouldCoverSortLogic() {
        // Given
        ProductCriteriaDto criteria = new ProductCriteriaDto(
            "test", 0, 10, null, null,
            null, null, null, SortType.PRICE_DESC
        );

        SearchHits<Product> searchHitsResult = mockSearchHits();
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHitsResult);

        // When
        productService.findProductAdvance(criteria);

        // Then
    }

    @Test
    void findProductAdvance_withPriceAsc_shouldCoverSortLogic() {
        // Given
        ProductCriteriaDto criteria = new ProductCriteriaDto(
            "test", 0, 10, null, null,
            null, null, null, SortType.PRICE_ASC
        );

        SearchHits<Product> searchHitsResult = mockSearchHits();
        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHitsResult);

        // When
        productService.findProductAdvance(criteria);

        // Then
    }

    @Test
    @SuppressWarnings("unchecked")
    void findProductAdvance_withAggregations_shouldCoverAggregationLogic() {
        // Given
        ProductCriteriaDto criteria = new ProductCriteriaDto(
            "test", 0, 10, null, null,
            null, null, null, SortType.DEFAULT
        );

        SearchHits<Product> searchHitsResult = mockSearchHits();

        // Mock Aggregations
        AggregationsContainer aggregationsContainer = mock(AggregationsContainer.class);
        ElasticsearchAggregation elsAgg = mock(ElasticsearchAggregation.class);
        org.springframework.data.elasticsearch.client.elc.Aggregation aggregation = mock(org.springframework.data.elasticsearch.client.elc.Aggregation.class);
        Aggregate aggregate = mock(Aggregate.class);
        StringTermsAggregate stringTermsAggregate = mock(StringTermsAggregate.class);
        Buckets<StringTermsBucket> buckets = mock(Buckets.class);
        StringTermsBucket bucket = mock(StringTermsBucket.class);

        when(searchHitsResult.hasAggregations()).thenReturn(true);
        when(searchHitsResult.getAggregations()).thenReturn(aggregationsContainer);
        when(aggregationsContainer.aggregations()).thenReturn(List.of(elsAgg));
        when(elsAgg.aggregation()).thenReturn(aggregation);
        
        when(aggregation.getAggregate()).thenReturn(aggregate);
        when(aggregation.getName()).thenReturn("testAgg");

        when(aggregate._get()).thenReturn(stringTermsAggregate);
        when(stringTermsAggregate.buckets()).thenReturn(buckets);
        when(buckets._get()).thenReturn(List.of(bucket));
        
        FieldValue fieldValue = mock(FieldValue.class);
        when(fieldValue._get()).thenReturn("testKey");
        when(bucket.key()).thenReturn(fieldValue);
        
        when(bucket.docCount()).thenReturn(10L);

        when(elasticsearchOperations.search(any(NativeQuery.class), eq(Product.class))).thenReturn(searchHitsResult);

        // When
        ProductListGetVm result = productService.findProductAdvance(criteria);

        // Then
        assertNotNull(result);
        assertEquals(1, result.aggregations().size());
        assertEquals(10L, result.aggregations().get("testAgg").get("testKey"));
    }
}
