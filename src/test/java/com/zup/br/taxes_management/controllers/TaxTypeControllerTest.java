package com.zup.br.taxes_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.services.TaxTypeService;
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

import java.util.List;

@WebMvcTest
public class TaxTypeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TaxTypeService taxTypeService;

    private ObjectMapper mapper;
    private TaxType taxType;

    @BeforeEach
    public void setUp() {
        mapper = new ObjectMapper();

        this.taxType = new TaxType();
        taxType.setId(1L);
        taxType.setName("ICMS");
        taxType.setDescription("Tax on circulation of goods and services");
        taxType.setAliquot(18.0);
    }

    @Test
    public void testWhenRegisterTaxTypeDoesntHaveImpediments() throws Exception {
        TaxTypeRegisterDTO taxTypeRegisterDTO = new TaxTypeRegisterDTO();
        taxTypeRegisterDTO.setName("IPI");
        taxTypeRegisterDTO.setDescription("Service tax");
        taxTypeRegisterDTO.setAliquot(12.0);

        String json = mapper.writeValueAsString(taxTypeRegisterDTO);

        Mockito.when(taxTypeService.registerTaxType(Mockito.any(TaxType.class))).thenReturn(taxType);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/api/taxes/types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(taxType.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("IPI")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is("Service tax")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aliquot", CoreMatchers.is(12.0)));
    }

    @Test
    public void testWhenReturnAllTaxTypes() throws Exception {
        TaxType taxType2 = new TaxType();
        taxType2.setId(2L);
        taxType2.setName("IPI");
        taxType2.setDescription("Service tax");
        taxType2.setAliquot(12.0);

        Mockito.when(taxTypeService.displayAllTaxTypes()).thenReturn(List.of(taxType, taxType2));

        List<TaxType> taxTypeList = taxTypeService.displayAllTaxTypes();

        String json = mapper.writeValueAsString(taxTypeList);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/taxes/types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.is(taxType.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.is("ICMS")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", CoreMatchers.is("Tax on circulation of goods and services")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].aliquot", CoreMatchers.is(18.0)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", CoreMatchers.is(taxType2.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].name", CoreMatchers.is("IPI")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].description", CoreMatchers.is("Service tax")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[1].aliquot", CoreMatchers.is(12.0)));

    }

    @Test
    public void testWhenSpecificTaxTypeHasFoundById() throws Exception {

        String json = mapper.writeValueAsString(taxType);

        Mockito.when(taxTypeService.displayTaxTypeById(Mockito.anyLong())).thenReturn(taxType);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/taxes/types/{id}", taxType.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(taxType.getId())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("ICMS")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is("Tax on circulation of goods and services")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aliquot", CoreMatchers.is(12.0)));

    }

    @Test
    public void testWhenSpecificTaxTypeHasNotFoundById() throws Exception {

        Mockito.when(taxTypeService.displayTaxTypeById(Mockito.anyLong())).thenThrow(new RuntimeException("Tax type not found"));

        String json = mapper.writeValueAsString(taxType);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/api/taxes/types/{id}", taxType.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message", CoreMatchers.is("Tax type not found")));

    }

    @Test
    public void testWhenDeleteTaxTypeReturnsNoContent() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/taxes/types/{id}", taxType.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.content().string("Tax type deleted successfully"));
    }

    @Test
    public void testWhenDeleteTaxTypeReturnsNotFound() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/api/taxes/types/{id}", taxType.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.content().string("Tax type not found"));
    }
}