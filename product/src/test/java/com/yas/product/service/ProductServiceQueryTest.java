package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductCategory;
import com.yas.product.model.ProductImage;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductDetailVm;
import com.yas.product.viewmodel.product.ProductExportingDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductGetCheckoutListVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductListVm;
import com.yas.product.viewmodel.product.ProductThumbnailVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.product.ProductVariationGetVm;
import com.yas.product.viewmodel.product.ProductSlugGetVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductThumbnailGetVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class ProductServiceQueryTest {

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
    void getProductById_existingProductId_returnsProductDetailVm() {
        // Arrange
        Brand brand = new Brand();
        brand.setId(1L);
        product.setBrand(brand);

        Category category = new Category();
        category.setId(1L);
        ProductCategory productCategory = new ProductCategory();
        productCategory.setCategory(category);
        product.setProductCategories(List.of(productCategory));

        ProductImage productImage = new ProductImage();
        productImage.setImageId(1L);
        product.setProductImages(List.of(productImage));
        product.setThumbnailMediaId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        NoFileMediaVm mediaVm = new NoFileMediaVm(1L, "caption", "fileName", "mediaType", "url");
        when(mediaService.getMedia(1L)).thenReturn(mediaVm);

        // Act
        ProductDetailVm result = productService.getProductById(1L);

        // Assert
        assertEquals(product.getId(), result.id());
        assertEquals(product.getName(), result.name());
    }

    @Test
    void getProductById_nonExistingProductId_throwsNotFoundException() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> productService.getProductById(1L));
    }

    @Test
    void getProductDetail_validSlug_returnsProductDetailGetVm() {
        // Arrange
        Brand brand = new Brand();
        brand.setName("Brand Name");
        product.setBrand(brand);
        product.setThumbnailMediaId(1L);
        product.setProductCategories(List.of());

        ProductAttributeValue attributeValue = new ProductAttributeValue();
        ProductAttribute attribute = new ProductAttribute();
        attribute.setName("Attr");
        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setName("Group");
        attribute.setProductAttributeGroup(group);
        attributeValue.setProductAttribute(attribute);
        attributeValue.setValue("Val");
        product.setAttributeValues(List.of(attributeValue));

        when(productRepository.findBySlugAndIsPublishedTrue("product-name")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        ProductDetailGetVm result = productService.getProductDetail("product-name");

        // Assert
        assertEquals(product.getId(), result.id());
        assertEquals(1, result.productAttributeGroups().size());
        assertEquals("Group", result.productAttributeGroups().get(0).name());
    }

    @Test
    void getProductsWithFilter_validPageNoAndPageSize_returnsProductListGetVm() {
        // Arrange
        List<Product> products = List.of(product);
        Page<Product> productPage = new PageImpl<>(products, PageRequest.of(0, 10), 1);
        when(productRepository.getProductsWithFilter(any(String.class), any(String.class), any(Pageable.class)))
            .thenReturn(productPage);

        // Act
        ProductListGetVm result = productService.getProductsWithFilter(0, 10, "Product", "Brand");

        // Assert
        assertEquals(1, result.productContent().size());
        assertEquals(0, result.pageNo());
    }

    @Test
    void getLatestProducts_validCount_returnsProductListVmList() {
        // Arrange
        when(productRepository.getLatestProducts(any(Pageable.class))).thenReturn(List.of(product));

        // Act
        List<ProductListVm> result = productService.getLatestProducts(1);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getProductsByBrand_validBrandSlug_returnsProductThumbnailVmList() {
        // Arrange
        Brand brand = new Brand();
        brand.setSlug("brand-slug");
        when(brandRepository.findBySlug("brand-slug")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(product));
        when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        List<ProductThumbnailVm> result = productService.getProductsByBrand("brand-slug");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getProductsFromCategory_validCategorySlug_returnsProductListGetFromCategoryVm() {
        // Arrange
        Category category = new Category();
        category.setSlug("cat-slug");
        ProductCategory productCategory = new ProductCategory();
        productCategory.setProduct(product);
        Page<ProductCategory> page = new PageImpl<>(List.of(productCategory), PageRequest.of(0, 10), 1);

        when(categoryRepository.findBySlug("cat-slug")).thenReturn(Optional.of(category));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), any(Category.class))).thenReturn(page);
        when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 10, "cat-slug");

        // Assert
        assertEquals(1, result.productContent().size());
    }

    @Test
    void getListFeaturedProducts_validPage_returnsProductFeatureGetVm() {
        // Arrange
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 10);

        // Assert
        assertEquals(1, result.productList().size());
    }

    @Test
    void getProductByIds_validIds_returnsProductListVmList() {
        // Arrange
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));

        // Act
        List<ProductListVm> result = productService.getProductByIds(List.of(1L));

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getProductByCategoryIds_validIds_returnsProductListVmList() {
        // Arrange
        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(product));

        // Act
        List<ProductListVm> result = productService.getProductByCategoryIds(List.of(1L));

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getProductByBrandIds_validIds_returnsProductListVmList() {
        // Arrange
        when(productRepository.findByBrandIdsIn(List.of(1L))).thenReturn(List.of(product));

        // Act
        List<ProductListVm> result = productService.getProductByBrandIds(List.of(1L));

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getRelatedProductsBackoffice_validId_returnsProductListVmList() {
        // Arrange
        Product relatedProduct = new Product();
        relatedProduct.setId(2L);
        relatedProduct.setName("Related");
        com.yas.product.model.ProductRelated pr = new com.yas.product.model.ProductRelated();
        pr.setRelatedProduct(relatedProduct);
        product.setRelatedProducts(List.of(pr));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        List<ProductListVm> result = productService.getRelatedProductsBackoffice(1L);

        // Assert
        assertEquals(1, result.size());
        assertEquals("Related", result.get(0).name());
    }

    @Test
    void getRelatedProductsStorefront_validId_returnsProductsGetVm() {
        // Arrange
        Product relatedProduct = new Product();
        relatedProduct.setId(2L);
        relatedProduct.setName("Related");
        relatedProduct.setPublished(true);
        com.yas.product.model.ProductRelated pr = new com.yas.product.model.ProductRelated();
        pr.setRelatedProduct(relatedProduct);
        Page<com.yas.product.model.ProductRelated> page = new PageImpl<>(List.of(pr));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRelatedRepository.findAllByProduct(any(), any())).thenReturn(page);
        when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        var result = productService.getRelatedProductsStorefront(1L, 0, 10);

        // Assert
        assertEquals(1, result.productContent().size());
    }

    @Test
    void getProductCheckoutList_validIds_returnsProductGetCheckoutListVm() {
        // Arrange
        Brand brand = new Brand();
        brand.setId(1L);
        product.setBrand(brand);
        Page<Product> page = new PageImpl<>(List.of(product));
        when(productRepository.findAllPublishedProductsByIds(any(), any())).thenReturn(page);
        when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        ProductGetCheckoutListVm result = productService.getProductCheckoutList(0, 10, List.of(1L));

        // Assert
        assertEquals(1, result.productCheckoutListVms().size());
    }

    @Test
    void getProductsForWarehouse_validFilter_returnsProductInfoVmList() {
        // Arrange
        when(productRepository.findProductForWarehouse(any(), any(), any(), any()))
            .thenReturn(List.of(product));

        // Act
        List<ProductInfoVm> result = productService.getProductsForWarehouse("Name", "SKU", List.of(1L), FilterExistInWhSelection.ALL);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void exportProducts_validFilter_returnsProductExportingDetailVmList() {
        // Arrange
        Brand brand = new Brand();
        brand.setId(1L);
        product.setBrand(brand);
        product.setProductCategories(List.of());
        product.setAttributeValues(List.of());
        when(productRepository.getExportingProducts(any(), any())).thenReturn(List.of(product));

        // Act
        List<ProductExportingDetailVm> result = productService.exportProducts("Name", "Brand");

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getProductsByMultiQuery_validFilter_returnsProductsGetVm() {
        // Arrange
        Page<Product> productPage = new PageImpl<>(List.of(product));
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(any(), any(), any(), any(), any()))
            .thenReturn(productPage);
        when(mediaService.getMedia(any())).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        var result = productService.getProductsByMultiQuery(0, 10, "Name", "Cat", 0.0, 100.0);

        // Assert
        assertEquals(1, result.productContent().size());
    }

    @Test
    void getProductVariationsByParentId_validId_returnsProductVariationGetVmList() {
        // Arrange
        product.setHasOptions(true);
        Product variation = new Product();
        variation.setId(2L);
        variation.setPublished(true);
        variation.setProductImages(List.of());
        product.setProducts(List.of(variation));
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of());

        // Act
        List<ProductVariationGetVm> result = productService.getProductVariationsByParentId(1L);

        // Assert
        assertEquals(1, result.size());
    }

    @Test
    void getProductSlug_withParent_returnsParentSlug() {
        // Arrange
        Product parent = new Product();
        parent.setSlug("parent-slug");
        product.setParent(parent);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductSlugGetVm result = productService.getProductSlug(1L);

        // Assert
        assertEquals("parent-slug", result.slug());
        assertEquals(1L, result.productVariantId());
    }

    @Test
    void getProductEsDetailById_validId_returnsProductEsDetailVm() {
        // Arrange
        product.setProductCategories(List.of());
        product.setAttributeValues(List.of());
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Act
        ProductEsDetailVm result = productService.getProductEsDetailById(1L);

        // Assert
        assertEquals(product.getId(), result.id());
    }

    @Test
    void getFeaturedProductsById_validIds_returnsProductThumbnailGetVmList() {
        // Arrange
        product.setThumbnailMediaId(1L);
        when(productRepository.findAllByIdIn(List.of(1L))).thenReturn(List.of(product));
        when(mediaService.getMedia(1L)).thenReturn(new NoFileMediaVm(1L, "", "", "", "url"));

        // Act
        List<ProductThumbnailGetVm> result = productService.getFeaturedProductsById(List.of(1L));

        // Assert
        assertEquals(1, result.size());
    }
}
