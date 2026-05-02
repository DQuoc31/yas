package com.yas.tax.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.tax.model.TaxClass;
import com.yas.tax.repository.TaxClassRepository;
import com.yas.tax.viewmodel.taxclass.TaxClassListGetVm;
import com.yas.tax.viewmodel.taxclass.TaxClassPostVm;
import com.yas.tax.viewmodel.taxclass.TaxClassVm;
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
import org.springframework.data.domain.Sort;

@ExtendWith(MockitoExtension.class)
class TaxClassServiceTest {

    @Mock
    private TaxClassRepository taxClassRepository;

    @InjectMocks
    private TaxClassService taxClassService;

    private TaxClass taxClass;
    private TaxClassPostVm taxClassPostVm;

    @BeforeEach
    void setUp() {
        taxClass = new TaxClass();
        taxClass.setId(1L);
        taxClass.setName("Standard Tax");

        taxClassPostVm = new TaxClassPostVm("Standard Tax");
    }

    @Test
    void findAllTaxClasses_ReturnsListOfTaxClassVm() {
        when(taxClassRepository.findAll(Sort.by(Sort.Direction.ASC, "name")))
            .thenReturn(List.of(taxClass));

        List<TaxClassVm> result = taxClassService.findAllTaxClasses();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).id()).isEqualTo(1L);
        assertThat(result.get(0).name()).isEqualTo("Standard Tax");
    }

    @Test
    void findById_WhenIdIsValid_ReturnsTaxClassVm() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));

        TaxClassVm result = taxClassService.findById(1L);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Standard Tax");
    }

    @Test
    void findById_WhenIdIsInvalid_ThrowsNotFoundException() {
        when(taxClassRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.findById(2L));
    }

    @Test
    void create_WhenNameIsUnique_SavesAndReturnsTaxClass() {
        when(taxClassRepository.existsByName(taxClassPostVm.name())).thenReturn(false);
        when(taxClassRepository.save(any(TaxClass.class))).thenReturn(taxClass);

        TaxClass result = taxClassService.create(taxClassPostVm);

        assertThat(result.getName()).isEqualTo("Standard Tax");
        verify(taxClassRepository).save(any(TaxClass.class));
    }

    @Test
    void create_WhenNameIsDuplicated_ThrowsDuplicatedException() {
        when(taxClassRepository.existsByName(taxClassPostVm.name())).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.create(taxClassPostVm));
    }

    @Test
    void update_WhenValid_UpdatesTaxClass() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass(taxClassPostVm.name(), 1L)).thenReturn(false);

        taxClassService.update(taxClassPostVm, 1L);

        verify(taxClassRepository).save(taxClass);
        assertThat(taxClass.getName()).isEqualTo("Standard Tax");
    }

    @Test
    void update_WhenIdIsInvalid_ThrowsNotFoundException() {
        when(taxClassRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> taxClassService.update(taxClassPostVm, 2L));
    }

    @Test
    void update_WhenNameIsDuplicated_ThrowsDuplicatedException() {
        when(taxClassRepository.findById(1L)).thenReturn(Optional.of(taxClass));
        when(taxClassRepository.existsByNameNotUpdatingTaxClass(taxClassPostVm.name(), 1L)).thenReturn(true);

        assertThrows(DuplicatedException.class, () -> taxClassService.update(taxClassPostVm, 1L));
    }

    @Test
    void delete_WhenIdIsValid_DeletesTaxClass() {
        when(taxClassRepository.existsById(1L)).thenReturn(true);

        taxClassService.delete(1L);

        verify(taxClassRepository).deleteById(1L);
    }

    @Test
    void delete_WhenIdIsInvalid_ThrowsNotFoundException() {
        when(taxClassRepository.existsById(2L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> taxClassService.delete(2L));
    }

    @Test
    void getPageableTaxClasses_ReturnsTaxClassListGetVm() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<TaxClass> taxClassPage = new PageImpl<>(List.of(taxClass), pageable, 1);
        
        when(taxClassRepository.findAll(pageable)).thenReturn(taxClassPage);

        TaxClassListGetVm result = taxClassService.getPageableTaxClasses(0, 10);

        assertThat(result.taxClassContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        assertThat(result.totalPages()).isEqualTo(1);
    }
}
