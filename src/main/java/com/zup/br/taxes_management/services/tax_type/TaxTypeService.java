package com.zup.br.taxes_management.services.tax_type;

import com.zup.br.taxes_management.controllers.dtos.TaxTypeRegisterDTO;
import com.zup.br.taxes_management.models.TaxType;

import java.util.List;

public interface TaxTypeService {

        List<TaxType> displayAllTaxTypes();

    TaxType displayTaxTypeById(Long idTaxType);

    boolean deleteTaxTypeById (Long idTaxType);

    TaxType registerTaxType(TaxTypeRegisterDTO taxTypeRegisterDTO);
}
