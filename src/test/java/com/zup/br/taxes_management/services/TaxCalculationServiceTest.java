package com.zup.br.taxes_management.services;

import static org.junit.jupiter.api.Assertions.assertEquals;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.zup.br.taxes_management.controllers.dtos.TaxCalculationDTO;
import com.zup.br.taxes_management.controllers.dtos.TaxCalculationResponseDTO;
import com.zup.br.taxes_management.services.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import com.zup.br.taxes_management.services.tax_calculation.TaxCalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Optional;

@SpringBootTest
public class TaxCalculationServiceTest {

    @Autowired
    private TaxCalculationService taxCalculationService;

    @MockitoBean
    private TaxTypeRepository taxTypeRepository;


    private TaxCalculationDTO taxCalculation;
    private TaxType taxType;


    @BeforeEach
    public void setUp() {

        this.taxCalculation = new TaxCalculationDTO();
        this.taxType = new TaxType();

        taxCalculation.setBaseValue(1230.0);

        taxType.setId(2L);
        taxType.setName("INSS");
        taxType.setDescription("Social security manager");
        taxType.setAliquot(7.5);

    }



    @Test
    public void testWhenTaxTypeNotFoundById() {

        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.empty());

        TaxTypeNotFoundException taxTypeNotFoundException = assertThrows(TaxTypeNotFoundException.class,
                () -> taxCalculationService.calculateTaxValue(taxType.getId(), taxCalculation.getBaseValue()));

        assertEquals("Tax type not found", taxTypeNotFoundException.getMessage());
        Mockito.verify(taxTypeRepository, Mockito.times(1)).findById(taxType.getId());

    }


    @Test
    public void testWhenTaxTypeHasFound() {

        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.of(taxType));

        TaxCalculationResponseDTO taxCalculationResponseDTO =
                taxCalculationService.calculateTaxValue(taxType.getId(), taxCalculation.getBaseValue());

        assertEquals("INSS", taxCalculationResponseDTO.getTaxType());
        assertEquals(1230.0, taxCalculationResponseDTO.getBaseValue());
        assertEquals(7.5, taxCalculationResponseDTO.getAliquot());
        assertEquals(92.25, taxCalculationResponseDTO.getTaxValue());

    }

}
