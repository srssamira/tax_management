package com.zup.br.taxes_management.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxTypeRegisterDTO;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import com.zup.br.taxes_management.services.tax_type.TaxTypeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

@SpringBootTest
public class TaxTypeServiceTest {

    @Autowired
    private TaxTypeService taxtypeService;

    @MockitoBean
    private TaxTypeRepository taxTypeRepository;

    private TaxType taxType;
    private TaxType taxType2 = new TaxType();
    private TaxTypeRegisterDTO taxTypeRegisterDTO;
    ObjectMapper mapper;


    @BeforeEach
    public void setUp() {

        this.mapper = new ObjectMapper();

        this.taxType = new TaxType();

        taxType.setId(1L);
        taxType.setName("ICMS");
        taxType.setDescription("Tax on circulation of goods and services");
        taxType.setAliquot(18.0);

        taxType2.setName("IOF");
        taxType2.setDescription("Tax on financial transactions");
        taxType2.setAliquot(0.38);

        this.taxTypeRegisterDTO = new TaxTypeRegisterDTO();
        taxTypeRegisterDTO.setName("IOF");
        taxTypeRegisterDTO.setDescription("Tax on financial transactions");
        taxTypeRegisterDTO.setAliquot(0.38);
    }



    @Test
    public void testWhenRegisterTaxTypeHasNoImpediment() {

        TaxType taxTypeRegistered = taxtypeService.registerTaxType(taxTypeRegisterDTO);
        Mockito.verify(taxTypeRepository, Mockito.times(1)).save(taxType2);

    }



    @Test
    public void testWhenDisplayListAllTaxTypes() {
        TaxType taxType2 = new TaxType();
        taxType2.setId(2L);
        taxType2.setName("ISS");
        taxType2.setDescription("Service tax");
        taxType2.setAliquot(5.0);

        Mockito.when(taxTypeRepository.findAll()).thenReturn(List.of(taxType, taxType2));

        List<TaxType> taxTypeList = taxTypeRepository.findAll();

        assertEquals(2, taxTypeList.size());
        assertEquals("ISS", taxType2.getName());
        assertEquals("ICMS", taxType.getName());

        Mockito.verify(taxTypeRepository, Mockito.times(1)).findAll();
    }



    @Test
    public void testWhenDisplayTaxTypeById() {
        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.of(taxType));
        Mockito.when(taxTypeRepository.save(taxType)).thenReturn(taxType);

        TaxType taxTypeTest = taxTypeRepository.findById(taxType.getId()).get();

        Mockito.verify(taxTypeRepository, Mockito.times(1)).findById(taxType.getId());
        assertEquals("ICMS", taxType.getName());
    }


    @Test
    public void testWhenDisplayTaxTypeByIdDoesNotExist() {
        Mockito.when(taxTypeRepository.findById(taxType.getId())).thenReturn(Optional.empty());

        RuntimeException expectedException = assertThrows(RuntimeException.class, () -> taxtypeService.displayTaxTypeById(taxType.getId()));

        assertEquals("Tax type not found", expectedException.getMessage());
        Mockito.verify(taxTypeRepository, Mockito.times(1)).findById(taxType.getId());
    }


}
