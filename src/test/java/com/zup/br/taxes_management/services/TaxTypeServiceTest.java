package com.zup.br.taxes_management.services;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
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

    @BeforeEach
    public void setUp() {
        this.taxType = new TaxType();
        taxType.setId(1L);
        taxType.setName("ICMS");
        taxType.setDescription("Tax on circulation of goods and services");
        taxType.setAliquot(18.0);
    }

    @Test
    public void testWhenRegisterTaxTypeHasNoImpediment() {
        TaxType taxTypeRegistered = taxtypeService.registerTaxType(taxType);

        Mockito.verify(taxTypeRepository, Mockito.times(1)).save(taxType);
    }

    // teste p retornar excecao na validacao eu faco no controller

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

    // teste do delete fazer no controller (pq retorna status http)



}