package com.yas.recommendation.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class ViewModelTest {

    @Test
    void testCategoryVm() {
        CategoryVm vm = new CategoryVm(1L, "Name", "Desc", "Slug", "Key", "Meta", (short)1, true);
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Name");
    }

    @Test
    void testImageVm() {
        ImageVm vm = new ImageVm(1L, "url");
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.url()).isEqualTo("url");
    }

    @Test
    void testProductAttributeValueVm() {
        ProductAttributeValueVm vm = new ProductAttributeValueVm(1L, "Attr", "Val");
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.value()).isEqualTo("Val");
    }

    @Test
    void testProductDetailVm() {
        ProductDetailVm vm = new ProductDetailVm(
            1L, "Name", "Short", "Desc", "Spec", "SKU", "GTIN", "Slug",
            true, true, false, true, true, 100.0, 1L, Collections.emptyList(),
            "Title", "Key", "Meta", 1L, "Brand", Collections.emptyList(),
            Collections.emptyList(), null, Collections.emptyList()
        );
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Name");
    }

    @Test
    void testProductVariationVm() {
        ProductVariationVm vm = new ProductVariationVm(1L, "Name", "Slug", "SKU", "GTIN", 100.0, Collections.emptyMap());
        assertThat(vm.id()).isEqualTo(1L);
        assertThat(vm.name()).isEqualTo("Name");
    }

    @Test
    void testRelatedProductVm() {
        RelatedProductVm vm = new RelatedProductVm();
        vm.setProductId(1L);
        vm.setName("Name");
        vm.setPrice(BigDecimal.valueOf(100));
        vm.setBrand("Brand");
        vm.setTitle("Title");
        vm.setDescription("Desc");
        vm.setMetaDescription("Meta");
        vm.setSpecification("Spec");
        vm.setSlug("Slug");
        
        assertThat(vm.getProductId()).isEqualTo(1L);
        assertThat(vm.getName()).isEqualTo("Name");
        assertThat(vm.getPrice()).isEqualTo(BigDecimal.valueOf(100));
        assertThat(vm.getBrand()).isEqualTo("Brand");
        assertThat(vm.getTitle()).isEqualTo("Title");
        assertThat(vm.getDescription()).isEqualTo("Desc");
        assertThat(vm.getMetaDescription()).isEqualTo("Meta");
        assertThat(vm.getSpecification()).isEqualTo("Spec");
        assertThat(vm.getSlug()).isEqualTo("Slug");
    }
}
