package com.zup.br.taxes_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxTypeRegisterDTO;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.services.tax_type.TaxTypeService;
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

import java.util.List;

@WebMvcTest(TaxTypeController.class)
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

        ObjectMapper objectMapper = new ObjectMapper();

        Mockito.when(taxTypeService.registerTaxType(Mockito.any(TaxType.class))).thenReturn(objectMapper.convertValue(taxTypeRegisterDTO, TaxType.class));

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .post("/types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("IPI")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is("Service tax")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aliquot", CoreMatchers.is(12.0)));
    }

    @Test
    public void testWhenRegisterTaxTypeHasInvalidData() throws Exception {
        TaxTypeRegisterDTO taxTypeRegisterDTO = new TaxTypeRegisterDTO();
        taxTypeRegisterDTO.setName(" ");
        taxTypeRegisterDTO.setDescription("Service tax");
        taxTypeRegisterDTO.setAliquot(12.0);

        String json = mapper.writeValueAsString(taxTypeRegisterDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.field == 'name')].description").value("name can't be blank"));

        taxTypeRegisterDTO.setName("IPI");
        taxTypeRegisterDTO.setDescription("");
        taxTypeRegisterDTO.setAliquot(12.0);

        json = mapper.writeValueAsString(taxTypeRegisterDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.field == 'description')].description").value("description can't be blank"));
                                                            // procurando no meu map a chave description e verificando se ela retorna a mensagem de erro esperada

        taxTypeRegisterDTO.setName("IPI");
        taxTypeRegisterDTO.setDescription("Service tax");
        taxTypeRegisterDTO.setAliquot(null);

        json = mapper.writeValueAsString(taxTypeRegisterDTO);

        mockMvc.perform(
                MockMvcRequestBuilders
                        .post("/types")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(MockMvcResultMatchers.jsonPath("$[?(@.field == 'aliquot')].description").value("aliquot can't be null"));
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
                                .get("/types")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))

                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].name", CoreMatchers.is("ICMS")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].description", CoreMatchers.is("Tax on circulation of goods and services")))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].aliquot", CoreMatchers.is(18.0)))

                .andExpect(MockMvcResultMatchers.jsonPath("$[1].id", CoreMatchers.is(2)))
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
                                .get("/types/{id}", taxType.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", CoreMatchers.is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", CoreMatchers.is("ICMS")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is("Tax on circulation of goods and services")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.aliquot", CoreMatchers.is(18.0)));

    }

    @Test
    public void testWhenSpecificTaxTypeHasNotFoundById() throws Exception {

        Mockito.when(taxTypeService.displayTaxTypeById(Mockito.anyLong()))
                .thenThrow(new TaxTypeNotFoundException("Tax type not found"));

        String json = mapper.writeValueAsString(taxType);

        mockMvc.perform(
                        MockMvcRequestBuilders
                                .get("/types/{id}", taxType.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(json))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is("Tax type not found")));

    }

    @Test
    public void testWhenDeleteTaxTypeReturnsNoContent() throws Exception {
        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/types/{id}", taxType.getId()))
                .andExpect(MockMvcResultMatchers.status().isNoContent())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is("Tax type deleted successfully")));
    }

    @Test
    public void testWhenDeleteTaxTypeReturnsNotFound() throws Exception {

        Mockito.when(taxTypeService.deleteTaxTypeById(taxType.getId()))
                .thenThrow(new TaxTypeNotFoundException("Tax type not found"));

        mockMvc.perform(
                MockMvcRequestBuilders
                        .delete("/types/{id}", taxType.getId()))
                .andExpect(MockMvcResultMatchers.status().isNotFound())
                .andExpect(MockMvcResultMatchers.jsonPath("$.description", CoreMatchers.is("Tax type not found")));
    }
}
