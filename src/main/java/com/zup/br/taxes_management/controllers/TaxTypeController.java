package com.zup.br.taxes_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxTypeRegisterDTO;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.services.tax_type.TaxTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/types")
public class TaxTypeController {

    @Autowired
    private TaxTypeService taxTypeService;

    ObjectMapper mapper = new ObjectMapper();


    @GetMapping
    public ResponseEntity<?> getAllTaxTypes() {
        return ResponseEntity.status(200).body(taxTypeService.displayAllTaxTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaxTypeById(@PathVariable Long id) {
        try {
            TaxType taxType = taxTypeService.displayTaxTypeById(id);
            return ResponseEntity.status(200).body(taxType);
        } catch (TaxTypeNotFoundException taxTypeNotFoundException) {
            return ResponseEntity.status(404).body(Map.of("description", taxTypeNotFoundException.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createTaxType(@RequestBody @Valid TaxTypeRegisterDTO taxTypeRegisterDTO) {
        TaxType taxType = taxTypeService.registerTaxType(mapper.convertValue(taxTypeRegisterDTO, TaxType.class));
        return ResponseEntity.status(201).body(taxType);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTaxTypeById(@PathVariable Long id) {
        try {
            taxTypeService.deleteTaxTypeById(id);
            return ResponseEntity.status(204).body(Map.of("description", "Tax type deleted successfully"));
        } catch (TaxTypeNotFoundException taxTypeNotFoundException) {
            return ResponseEntity.status(404).body(Map.of("description", taxTypeNotFoundException.getMessage()));
        }
    }

}
