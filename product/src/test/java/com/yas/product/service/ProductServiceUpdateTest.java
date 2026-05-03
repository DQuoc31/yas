package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductVariationPutVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceUpdateTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private MediaService mediaService;
    @Mock
    private BrandRepository brandRepository;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private ProductCategoryRepository productCategoryRepository;
    @Mock
    private ProductImageRepository productImageRepository;
    @Mock
    private ProductOptionRepository productOptionRepository;
    @Mock
    private ProductOptionValueRepository productOptionValueRepository;
    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;
    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Product Name");
        product.setSlug("product-name");
        product.setSku("SKU");
        product.setGtin("GTIN");
    }

    @Test
    void updateProduct_existingProductId_success() {
        // Arrange
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Name", "updated-slug", 150.0, true, true, true, true, true,
            1L, List.of(1L), "Short Description", "Description", "Specification", "SKU", "GTIN",
            1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(), 1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        when(productRepository.findBySlugAndIsPublishedTrue("updated-slug")).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue("SKU")).thenReturn(Optional.empty());

        // Act & Assert
        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void updateProduct_lengthLessThanWidth_throwsBadRequestException() {
        // Arrange
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Name", "updated-slug", 150.0, true, true, true, true, true,
            1L, List.of(1L), "Short Description", "Description", "Specification", "SKU", "GTIN",
            1.0, DimensionUnit.CM, 5.0, 10.0, 1.0, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(), 1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void updateProduct_duplicateSlug_throwsDuplicatedException() {
        // Arrange
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Name", "duplicate-slug", 150.0, true, true, true, true, true,
            1L, List.of(1L), "Short Description", "Description", "Specification", "SKU", "GTIN",
            1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(), 1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        Product existingProduct = new Product();
        existingProduct.setId(2L); // Different ID to trigger DuplicatedException
        when(productRepository.findBySlugAndIsPublishedTrue("duplicate-slug")).thenReturn(Optional.of(existingProduct));

        // Act & Assert
        Assertions.assertThrows(DuplicatedException.class, () -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void updateProduct_nonExistingProductId_throwsNotFoundException() {
        // Arrange
        ProductPutVm productPutVm = org.mockito.Mockito.mock(ProductPutVm.class);
        when(productRepository.findById(1L)).thenReturn(Optional.ofNullable(null));

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void subtractStockQuantity_validItems_success() {
        // Arrange
        ProductQuantityPutVm item = new ProductQuantityPutVm(1L, 10L);
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(20L);
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

        // Act
        productService.subtractStockQuantity(List.of(item));

        // Assert
        assertEquals(10L, product.getStockQuantity());
    }

    @Test
    void restoreStockQuantity_validItems_success() {
        // Arrange
        ProductQuantityPutVm item = new ProductQuantityPutVm(1L, 10L);
        product.setStockTrackingEnabled(true);
        product.setStockQuantity(20L);
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

        // Act
        productService.restoreStockQuantity(List.of(item));

        // Assert
        assertEquals(30L, product.getStockQuantity());
    }

    @Test
    void updateProductQuantity_validItems_success() {
        // Arrange
        ProductQuantityPostVm item = new ProductQuantityPostVm(1L, 50L);
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

        // Act
        productService.updateProductQuantity(List.of(item));

        // Assert
        assertEquals(50L, product.getStockQuantity());
    }

    @Test
    void updateProduct_withVariations_success() {
        // Arrange
        ProductVariationPutVm variationVm = new ProductVariationPutVm(
            2L, "Var Name", "var-slug", "VSKU", "VGTIN", 50.0, 1L, List.of(1L), java.util.Map.of(1L, "Value")
        );
        
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Name", "updated-slug", 150.0, true, true, true, true, true,
            1L, List.of(1L), "Short Description", "Description", "Specification", "SKU", "GTIN",
            1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(variationVm), List.of(), List.of(), List.of(), 1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        lenient().when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        
        Product variationProduct = new Product();
        variationProduct.setId(2L);
        product.setProducts(List.of(variationProduct));

        // Act & Assert
        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void updateProduct_withRelations_success() {
        // Arrange
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Name", "updated-slug", 150.0, true, true, true, true, true,
            1L, List.of(1L), "Short Description", "Description", "Specification", "SKU", "GTIN",
            1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(2L), 1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        lenient().when(productRepository.findById(2L)).thenReturn(Optional.of(new Product()));
        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        lenient().when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

        // Act & Assert
        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void deleteProduct_validId_success() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        productService.deleteProduct(1L);

        // Assert
        Assertions.assertFalse(product.isPublished());
    }

    @Test
    void deleteProduct_withParent_success() {
        // Arrange
        Product parent = new Product();
        product.setParent(parent);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(product)).thenReturn(List.of());

        // Act
        productService.deleteProduct(1L);

        // Assert
        Assertions.assertFalse(product.isPublished());
    }

    @Test
    void updateProduct_withNonExistingVariant_success() {
        // Arrange
        ProductVariationPutVm variationVm = new ProductVariationPutVm(
            2L, "Var Name", "var-slug", "VSKU", "VGTIN", 50.0, 1L, List.of(1L), java.util.Map.of(1L, "Value")
        );
        
        ProductPutVm productPutVm = new ProductPutVm(
            "Updated Name", "updated-slug", 150.0, true, true, true, true, true,
            1L, List.of(1L), "Short Description", "Description", "Specification", "SKU", "GTIN",
            1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(variationVm), List.of(), List.of(), List.of(), 1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        product.setProducts(List.of()); // Empty list, so variant 2L won't be found
        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        lenient().when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

        // Act & Assert
        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));
    }

    @Test
    void updateProduct_slugUnchanged_success() {
        // Arrange
        ProductPutVm productPutVm = new ProductPutVm(
            "Name", "product-name", 150.0, true, true, true, true, true,
            1L, List.of(1L), "Short Description", "Description", "Specification", "SKU", "GTIN",
            1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(), 1L
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        // Slug is "product-name", same as product.getSlug()
        when(productRepository.findBySlugAndIsPublishedTrue("product-name")).thenReturn(Optional.of(product));
        when(productRepository.findBySkuAndIsPublishedTrue("SKU")).thenReturn(Optional.of(product));
        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        lenient().when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

        // Act & Assert
        assertDoesNotThrow(() -> productService.updateProduct(1L, productPutVm));
    }
}
