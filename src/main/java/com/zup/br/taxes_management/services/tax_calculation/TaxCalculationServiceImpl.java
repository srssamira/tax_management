package com.zup.br.taxes_management.services.tax_calculation;

import com.zup.br.taxes_management.controllers.dtos.TaxCalculationResponseDTO;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxCalculationServiceImpl implements TaxCalculationService {

    @Autowired
    TaxTypeRepository taxTypeRepository;


    public TaxCalculationResponseDTO calculateTaxValue(Long id, Double baseValue) {
        if (taxTypeRepository.findById(id).isEmpty())
            throw new TaxTypeNotFoundException("Tax type not found");

        TaxType taxType = taxTypeRepository.findById(id).get();

        Double taxValue = baseValue * taxType.getAliquot() / 100;

        return new TaxCalculationResponseDTO(taxType.getName(), baseValue, taxType.getAliquot(), taxValue);
    }
}
