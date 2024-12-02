package com.zup.br.taxes_management.controllers;

import com.zup.br.taxes_management.controllers.dtos.TaxCalculationDTO;
import com.zup.br.taxes_management.controllers.dtos.TaxCalculationResponseDTO;
import com.zup.br.taxes_management.services.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.services.tax_calculation.TaxCalculationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
@RequestMapping("/calculation")
public class TaxCalculationController {

    @Autowired
    private TaxCalculationService taxCalculationService;

    @PostMapping
    public ResponseEntity<?> calculateTax(@RequestBody TaxCalculationDTO taxCalculationDTO) {
        try {
            TaxCalculationResponseDTO response = taxCalculationService.calculateTaxValue(taxCalculationDTO.getId(), taxCalculationDTO.getBaseValue());
            return ResponseEntity.ok(Map.of("taxType", response.getTaxType()
                    , "baseValue", response.getBaseValue(),
                    "aliquot", response.getAliquot(),
                    "taxValue", response.getTaxValue()));
        } catch (TaxTypeNotFoundException taxTypeNotFoundException) {
            return ResponseEntity.status(404).body(Map.of("description", taxTypeNotFoundException.getMessage()));
        }
    }
}