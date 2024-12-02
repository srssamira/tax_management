package com.zup.br.taxes_management.services.tax_calculation;

import com.zup.br.taxes_management.controllers.dtos.TaxCalculationResponseDTO;
import com.zup.br.taxes_management.services.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxCalculationServiceImpl implements TaxCalculationService {

    @Autowired
    TaxTypeRepository taxTypeRepository;



    @Override
    public TaxCalculationResponseDTO calculateTaxValue(Long id, Double baseValue) {
        TaxType taxType = findTaxType(id);
        Double taxValue = baseValue * taxType.getAliquot() / 100;
        return new TaxCalculationResponseDTO(taxType.getName(), baseValue, taxType.getAliquot(), taxValue);
    }


    @Override
    public TaxType findTaxType(Long id) {
        return taxTypeRepository.findById(id)
                .orElseThrow(() -> new TaxTypeNotFoundException("Tax type not found"));
    }


}
