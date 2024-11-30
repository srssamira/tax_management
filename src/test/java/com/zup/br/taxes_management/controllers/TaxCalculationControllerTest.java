package com.zup.br.taxes_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxCalculationDTO;
import com.zup.br.taxes_management.controllers.dtos.TaxCalculationResponseDTO;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.services.tax_calculation.TaxCalculationService;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;

@WebMvcTest(TaxCalculationController.class)
public class TaxCalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxCalculationService taxCalculationService;

    TaxType taxType;

    TaxCalculationDTO taxCalculationDTO;

    ObjectMapper mapper;

    @BeforeEach
    public void setUp() {
        this.taxType = new TaxType();
        this.taxCalculationDTO = new TaxCalculationDTO();
        this.mapper = new ObjectMapper();

        taxType.setId(3L);
        taxType.setName("IOF");
        taxType.setDescription("Tax on financial transactions");
        taxType.setAliquot(0.38);

        taxCalculationDTO.setBaseValue(3000.0);
    }

    @Test
    public void testWhenTaxTypeIdNotFound() throws Exception {
        Mockito.when(taxCalculationService.findTaxType(Mockito.anyLong()))
                .thenThrow(new TaxTypeNotFoundException("Tax type not found"));

        Mockito.when(taxCalculationService
                        .calculateTaxValue(taxCalculationDTO.getId(), taxCalculationDTO.getBaseValue()))
                .thenThrow(new TaxTypeNotFoundException("Tax type not found"));

        String json = mapper.writeValueAsString(taxCalculationDTO);
        System.out.println(json);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/calculation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description").value("Tax type not found"));
    }

    @Test
    public void testWhenTaxCalculationIsFoundAndReturnTaxCalculationResponseDTO() throws Exception {

        TaxCalculationResponseDTO expectedResponse = new TaxCalculationResponseDTO(taxType.getName(), taxCalculationDTO.getBaseValue(), taxType.getAliquot(), 11.4);

        Mockito.when(taxCalculationService.findTaxType(taxType.getId())).thenReturn(taxType);

        taxCalculationDTO.setId(taxType.getId());

        Mockito.when(taxCalculationService.calculateTaxValue(taxCalculationDTO.getId(), 3000.0))
                .thenReturn(expectedResponse);

        String json = mapper.writeValueAsString(taxCalculationDTO);
        System.out.println(json);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/calculation")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxType").value("IOF"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.baseValue").value(3000.0))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aliquot").value(0.38))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxValue").value(11.4));
    }

}
