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
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest
public class TaxCalculationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxCalculationService taxCalculationService;

    TaxType taxType = new TaxType();

    TaxCalculationDTO taxCalculationDTO;

    ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    public void setUp() {
        this.taxType = new TaxType();
        this.taxCalculationDTO = new TaxCalculationDTO();

        taxType.setId(3L);
        taxType.setName("IOF");
        taxType.setDescription("Tax on financial transactions");
        taxType.setAliquot(0.38);

        taxCalculationDTO.setBaseValue(3000.0);
    }

    @Test
    public void testWhenTaxTypeIdNotFound() throws Exception {

        Mockito.when(taxCalculationService.calculateTaxValue(Mockito.anyLong(), Mockito.anyDouble()))
                .thenThrow(new TaxTypeNotFoundException("Tax type not found"));

        String json = mapper.writeValueAsString(taxCalculationDTO);

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
        Mockito.when(taxCalculationService.calculateTaxValue(taxType.getId(), taxCalculationDTO.getBaseValue()))
                .thenReturn(new TaxCalculationResponseDTO
                        (taxType.getName(), taxCalculationDTO.getBaseValue(),taxType.getAliquot(),
                                taxCalculationDTO.getBaseValue() * taxType.getAliquot() / 100));

        String json = mapper.writeValueAsString(taxCalculationDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/calculation")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description")
                        .value("Tax calculation carried out successfully"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxType", CoreMatchers.is(taxType.getName())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.baseValue", CoreMatchers.is(taxCalculationDTO.getBaseValue())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aliquot", CoreMatchers.is(taxType.getAliquot())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.taxValue", CoreMatchers.is
                        (taxCalculationDTO.getBaseValue() * taxType.getAliquot() / 100)));
    }

}
