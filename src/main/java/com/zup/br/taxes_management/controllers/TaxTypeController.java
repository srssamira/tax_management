package com.zup.br.taxes_management.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zup.br.taxes_management.controllers.dtos.TaxTypeRegisterDTO;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.services.TaxTypeService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/taxes/types")
public class TaxTypeController {

    @Autowired
    private TaxTypeService taxTypeService;

    @GetMapping
    public ResponseEntity<?> getAllTaxTypes() {
        return ResponseEntity.status(200).body(taxTypeService.displayAllTaxTypes());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTaxTypeById(@PathVariable Long id) {
        try {
            TaxType taxType = taxTypeService.displayTaxTypeById(id);
            return ResponseEntity.status(200).body(taxType);
        }
        catch (TaxTypeNotFoundException taxTypeNotFoundException) {
            return ResponseEntity.status(404).body(Map.of("description", taxTypeNotFoundException.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createTaxType(@RequestBody @Valid TaxTypeRegisterDTO taxTypeRegisterDTO) {
            ObjectMapper objectMapper = new ObjectMapper();
            TaxType taxType = taxTypeService.registerTaxType(objectMapper.convertValue(taxTypeRegisterDTO, TaxType.class));
            return ResponseEntity.status(201).body(taxType);
    }


}