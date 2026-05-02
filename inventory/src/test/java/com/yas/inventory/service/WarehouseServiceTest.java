package com.yas.inventory.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.inventory.model.Warehouse;
import com.yas.inventory.repository.StockRepository;
import com.yas.inventory.repository.WarehouseRepository;
import com.yas.inventory.viewmodel.warehouse.WarehouseGetVm;
import com.yas.inventory.viewmodel.warehouse.WarehousePostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WarehouseServiceTest {
    private WarehouseRepository warehouseRepository;
    private StockRepository stockRepository;
    private ProductService productService;
    private LocationService locationService;
    private WarehouseService warehouseService;

    @BeforeEach
    void setUp() {
        warehouseRepository = mock(WarehouseRepository.class);
        stockRepository = mock(StockRepository.class);
        productService = mock(ProductService.class);
        locationService = mock(LocationService.class);
        warehouseService = new WarehouseService(warehouseRepository, stockRepository, productService, locationService);
    }

    @Test
    void findAllWarehouses_ShouldReturnList() {
        Warehouse warehouse = new Warehouse();
        warehouse.setId(1L);
        warehouse.setName("Warehouse 1");
        when(warehouseRepository.findAll()).thenReturn(List.of(warehouse));

        List<WarehouseGetVm> result = warehouseService.findAllWarehouses();

        assertEquals(1, result.size());
        assertEquals("Warehouse 1", result.get(0).name());
    }

    @Test
    void findById_WhenNotFound_ShouldThrowException() {
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.findById(1L));
    }

    @Test
    void create_WhenNameDuplicated_ShouldThrowException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("Duplicate").build();
        when(warehouseRepository.existsByName("Duplicate")).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> warehouseService.create(postVm));
    }

    @Test
    void update_WhenNotFound_ShouldThrowException() {
        WarehousePostVm postVm = WarehousePostVm.builder().name("New Name").build();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> warehouseService.update(postVm, 1L));
    }

    @Test
    void delete_WhenWarehouseHasStock_ShouldThrowException() {
        // Warehouse exists logic
        Warehouse warehouse = new Warehouse();
        when(warehouseRepository.findById(1L)).thenReturn(Optional.of(warehouse));
        // Has stock logic
        when(stockRepository.findByWarehouseId(1L)).thenReturn(List.of(new com.yas.inventory.model.Stock()));

        assertThrows(DuplicatedException.class, () -> warehouseService.delete(1L));
    }
}
