package com.zup.br.taxes_management.services.tax_calculation;

import com.zup.br.taxes_management.infra.IllegalBaseValueException;
import com.zup.br.taxes_management.infra.TaxTypeNotFoundException;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TaxCalculationProcessServiceImpl implements TaxCalculationProcessService {

    @Autowired
    private TaxTypeRepository taxTypeRepository;

    @Override
    public Double calculateTaxValue(Long id, Double baseValue) {
        if (taxTypeRepository.findById(id).isEmpty())
            throw new TaxTypeNotFoundException("Tax type not found");


        if (baseValue < 0 || baseValue == 0)
            throw new IllegalBaseValueException("Base value must be greater than zero");

        Double aliquot = taxTypeRepository.findById(id).get().getAliquot();

        return baseValue * aliquot / 100;
    }
}
