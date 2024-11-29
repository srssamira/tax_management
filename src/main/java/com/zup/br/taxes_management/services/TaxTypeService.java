package com.zup.br.taxes_management.services;

import com.zup.br.taxes_management.models.TaxType;

import java.util.List;

public interface TaxTypeService {

    List<TaxType> displayAllTaxTypes();

    TaxType displayTaxTypeById(Long idTaxType);

    boolean deleteTaxTypeById (Long idTaxType);

    TaxType registerTaxType(TaxType taxType);
}
