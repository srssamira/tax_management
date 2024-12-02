package com.zup.br.taxes_management.services.tax_calculation;

import com.zup.br.taxes_management.controllers.dtos.TaxCalculationResponseDTO;
import com.zup.br.taxes_management.models.TaxType;

public interface TaxCalculationService {

    TaxCalculationResponseDTO calculateTaxValue(Long id, Double baseValue);

    TaxType findTaxType(Long id);

}
