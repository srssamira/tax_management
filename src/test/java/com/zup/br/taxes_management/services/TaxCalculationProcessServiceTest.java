package com.zup.br.taxes_management.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxCalculationProcessDTO;
import com.zup.br.taxes_management.infra.IllegalBaseValueException;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.zup.br.taxes_management.services.tax_calculation.TaxCalculationProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

@SpringBootTest
public class TaxCalculationProcessServiceTest {

    @Autowired
    private TaxCalculationProcessService taxCalculationProcessService;

    @MockitoBean
    private TaxTypeRepository taxTypeRepository;

    TaxCalculationProcessDTO taxCalculationProcessDTO;

    TaxType taxType;

    @BeforeEach
    public void setUp() {

        this.taxCalculationProcessDTO = new TaxCalculationProcessDTO();
        taxCalculationProcessDTO.setBaseValue(1000.0);

        this.taxType = new TaxType();
        taxType.setId(1L);
        taxType.setName("ICMS");
        taxType.setDescription("Tax on circulation of goods and services");
        taxType.setAliquot(18.0);
    }

    @Test
    public void testWhenTaxCalculationProcessHasFoundTaxTypeId() {
        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.of(taxType));

        Long id = taxTypeRepository.findById(taxType.getId()).get().getId();

        Double taxValue = taxCalculationProcessService.calculateTaxValue(id, taxCalculationProcessDTO.getBaseValue());

        assertEquals(180.0, taxValue);
    }

    @Test
    public void testWhenTaxCalculationProcessHasNotFoundTaxTypeId() {
        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.empty());

        TaxTypeNotFoundException taxTypeNotFoundException = assertThrows(TaxTypeNotFoundException.class, () -> taxCalculationProcessService.calculateTaxValue(taxType.getId(), Mockito.anyDouble()));
        assertEquals("Tax type not found", taxTypeNotFoundException.getMessage());

        Mockito.verify(taxTypeRepository, Mockito.times(1)).findById(taxType.getId());
    }

    @Test
    public void testWhenBaseValueIsEqualZero() {
        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.of(taxType));

        IllegalBaseValueException illegalBaseValueException =
                assertThrows(IllegalBaseValueException.class, ()
                        -> taxCalculationProcessService.calculateTaxValue(taxType.getId(), 0.0));

        assertEquals("Base value must be greater than zero", illegalBaseValueException.getMessage());

        Mockito.verify(taxTypeRepository, Mockito.times(1)).findById(taxType.getId());
    }

    @Test
    public void testWhenBaseValueIsLessThanZero() {
        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.of(taxType));

        IllegalBaseValueException illegalBaseValueException =
                assertThrows(IllegalBaseValueException.class, ()
                        -> taxCalculationProcessService.calculateTaxValue(taxType.getId(), -Mockito.anyDouble()));

        assertEquals("Base value must be greater than zero", illegalBaseValueException.getMessage());
        Mockito.verify(taxTypeRepository, Mockito.times(1)).findById(taxType.getId());
    }
}
