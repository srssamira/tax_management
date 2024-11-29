package com.zup.br.taxes_management.services.tax_calculation;

public interface TaxCalculationProcessService {

    Double calculateTaxValue(Long id, Double baseValue);

}
