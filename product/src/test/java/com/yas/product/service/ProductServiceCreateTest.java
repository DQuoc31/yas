package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.lenient;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
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
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceCreateTest {

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
    }

    @Test
    void createProduct_validProductPostVm_returnsProductGetDetailVm() {
        // Arrange
        ProductPostVm productPostVm = new ProductPostVm(
            "Product Name", "product-name", 1L, List.of(1L), "Short Description", "Description",
            "Specification", "SKU", "GTIN", 1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, 100.0, 
            true, true, true, true, true, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(), 1L
        );

        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        lenient().when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        lenient().when(productRepository.save(any(Product.class))).thenReturn(product);

        // Act
        ProductGetDetailVm result = productService.createProduct(productPostVm);

        // Assert
        assertEquals(product.getName(), result.name());
        assertEquals(product.getSlug(), result.slug());
    }

    @Test
    void createProduct_lengthLessThanWidth_throwsBadRequestException() {
        // Arrange
        ProductPostVm productPostVm = new ProductPostVm(
            "Product Name", "product-name", 1L, List.of(1L), "Short Description", "Description",
            "Specification", "SKU", "GTIN", 1.0, DimensionUnit.CM, 5.0, 10.0, 1.0, 100.0, 
            true, true, true, true, true, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(), 1L
        );

        // Act & Assert
        Assertions.assertThrows(BadRequestException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_duplicateSlug_throwsDuplicatedException() {
        // Arrange
        ProductPostVm productPostVm = new ProductPostVm(
            "Product Name", "duplicate-slug", 1L, List.of(1L), "Short Description", "Description",
            "Specification", "SKU", "GTIN", 1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, 100.0, 
            true, true, true, true, true, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(), List.of(), List.of(), List.of(), 1L
        );

        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        lenient().when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        lenient().when(productRepository.findBySlugAndIsPublishedTrue("duplicate-slug")).thenReturn(Optional.of(new Product()));

        // Act & Assert
        Assertions.assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_withVariations_returnsProductGetDetailVm() {
        // Arrange
        ProductVariationPostVm variationVm = new ProductVariationPostVm(
            "Variation Name", "variation-slug", "VSKU", "VGTIN", 50.0, 1L, List.of(1L), Map.of(1L, "Value")
        );

        ProductOptionValueDisplay optionValueVm = new ProductOptionValueDisplay(1L, "Display", 1, "Value");

        ProductPostVm productPostVm = new ProductPostVm(
            "Product Name", "product-name", 1L, List.of(1L), "Short Description", "Description",
            "Specification", "SKU", "GTIN", 1.0, DimensionUnit.CM, 10.0, 5.0, 1.0, 100.0, 
            true, true, true, true, true, "Title", "Keyword", "Desc",
            1L, List.of(1L), List.of(variationVm), List.of(), List.of(optionValueVm), List.of(), 1L
        );

        lenient().when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        Category category = new Category();
        category.setId(1L);
        lenient().when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));
        lenient().when(productRepository.save(any(Product.class))).thenReturn(product);
        
        ProductOption productOption = new ProductOption();
        productOption.setId(1L);
        lenient().when(productOptionRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(productOption));
        
        Product variationProduct = new Product();
        variationProduct.setSlug("variation-slug");
        lenient().when(productRepository.saveAll(any())).thenReturn(List.of(variationProduct));

        // Act
        ProductGetDetailVm result = productService.createProduct(productPostVm);

        // Assert
        assertEquals(product.getName(), result.name());
        assertEquals(product.getSlug(), result.slug());
    }
}
