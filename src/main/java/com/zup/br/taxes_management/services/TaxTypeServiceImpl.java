package com.zup.br.taxes_management.services;

import com.zup.br.taxes_management.models.TaxType;
import com.zup.br.taxes_management.repositories.TaxTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaxTypeServiceImpl implements TaxTypeService {

    @Autowired
    private TaxTypeRepository taxTypeRepository;

    @Override
    public TaxType registerTaxType(TaxType taxType) {
        return taxTypeRepository.save(taxType);
    }

    @Override
    public List<TaxType> displayAllTaxTypes() {
        return taxTypeRepository.findAll();
    }

    @Override
    public TaxType displayTaxTypeById (Long idTaxType) {
        if (taxTypeRepository.findById(idTaxType).isEmpty()) {
            throw new RuntimeException("Tax type not found");
        }

        return taxTypeRepository.findById(idTaxType).get();
    }

    @Override
    public void deleteTaxType (Long idTaxType) {

    }
}
