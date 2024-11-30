package com.zup.br.taxes_management.services.tax_calculation;

import com.zup.br.taxes_management.controllers.dtos.TaxCalculationResponseDTO;

public interface TaxCalculationService {

    TaxCalculationResponseDTO calculateTaxValue(Long id, Double baseValue);

}
