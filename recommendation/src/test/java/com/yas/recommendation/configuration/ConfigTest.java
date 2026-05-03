package com.yas.recommendation.configuration;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class ConfigTest {

    @Test
    void testEmbeddingSearchConfiguration() {
        EmbeddingSearchConfiguration config = new EmbeddingSearchConfiguration(0.5, 10);
        assertThat(config.similarityThreshold()).isEqualTo(0.5);
        assertThat(config.topK()).isEqualTo(10);
    }
}
