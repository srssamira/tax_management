package com.zup.br.taxes_management.services;

import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;
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
        this.taxCalculationProcessDTO = new TaxCalculationProcess();
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

        Double aliquot = taxTypeRepository.findById(taxType.getId()).get().getAliquot();

        Double taxValue = taxCalculationProcessService.calculateTaxValue(aliquot, taxCalculationProcessDTO.getBaseValue());

        assertEquals(180.0, taxValue);
    }

    @Test
    public void testWhenTaxCalculationProcessHasNotFoundTaxTypeId() {
        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.empty());

        TaxTypeNotFoundException taxTypeNotFoundException = assertThrows(TaxTypeNotFoundException.class, () -> taxCalculationProcessService.calculateTaxValue(Mockito.anyDouble(), Mockito.anyDouble()));
        assertEquals("Tax type not found", taxTypeNotFoundException.getMessage());

        Mockito.verify(taxTypeRepository, Mockito.times(1)).findById(taxType.getId());
    }
}
