package com.zup.br.taxes_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxCalculationProcessDTO;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.services.tax_calculation.TaxCalculationProcessService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class TaxCalculationProcessControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    TaxCalculationProcessService taxCalculationProcessService;

    TaxCalculationProcessDTO taxCalculationProcessDTO;

    ObjectMapper mapper;

    TaxCalculationProcessResponseDTO taxCalculationProcessResponseDTO;

    TaxType taxType;

    @BeforeEach
    public void setUp() {

        this.mapper = new ObjectMapper();

        this.taxCalculationProcessDTO = new TaxCalculationProcessDTO();
        taxCalculationProcessDTO.setBaseValue(1000.0);

        this.taxType = new TaxType();
        taxType.setId(1L);
        taxType.setName("ICMS");
        taxType.setDescription("Tax on circulation of goods and services");
        taxType.setAliquot(18.0);

    }

    @Test
    public void testWhenTaxTypeNotFound() throws Exception {
        Mockito.when(taxCalculationProcessService.calculateTaxValue(taxType.getId(), Mockito.anyDouble()))
                .thenThrow(new TaxTypeNotFoundException("Tax type not found"));

        String json = mapper.writeValueAsString(taxCalculationProcessDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/calculation/{id}", taxCalculationProcessDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Tax type not found"));
    }

    @Test
    public void testWhenTaxCalculationProcessDontHaveImpediments() throws Exception {
        taxCalculationProcessResponseDTO.setTaxType(taxType.getName());
        taxCalculationProcessResponseDTO.setBasevalue(taxCalculationProcessDTO.getBaseValue());
        taxCaltulationProcessResponseDTO.setAliquot(taxType.getAliquot());
        taxCalculationProcessResponseDTO.setTaxValue(taxCalculationProcessService
                .calculateTaxValue(taxType.getId(), taxCalculationProcessDTO.getBaseValue()));

        String json = mapper.writeValueAsString(taxCalculationProcessDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/calculation/{id}", taxCalculationProcessDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Tax calculation carried out successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.response.taxType").value(taxType.getName()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.basevalue").value(taxCalculationProcessDTO.getBaseValue()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aliquot").value(taxType.getAliquot()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxValue")
                        .value(taxCalculationProcessService.calculateTaxValue
                                (taxType.getId(), taxCalculationProcessDTO.getBaseValue())));
    }
}
